package org.nixus.core.structure.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.nixus.core.strategies.ShortestPathStrategy;
import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.auxiliary.NodeBasedBinaryHeap;
import org.nixus.core.structure.auxiliary.NullNodeContent;
import org.nixus.core.structure.exceptions.NodeNotInThisGraphException;



/**
 * Node interface for method only accessed by the graph implementation
 * @param <M>
 * */
public abstract class AbstractNode implements Node, HiddenNodeAbstraction{
	
	private static final long serialVersionUID = 4367531029777710724L;

	/**
	 * Contend of this graph
	 * */
	protected Measurable<? extends Object> content;
	
	/**
	 * Owner graph of this node  
	 */
	protected Graph owner;

	private boolean wasVisited;

	/**
	 * This nodes tag
	 * */
	private String tag;

	/**
	 * Node distance, used for path algorithms
	 * */
	private int distance; 
	
	/**
	 * Node insertion order starting from 0
	 * */
	private int insertionOrder = 0;

	/**
	 * Parent on the last traversal, null if no one was the parent
	 * */
	private AbstractNode traversalParent;

	/**
	 * Number of hops to reach  this node during the last traversal
	 * */
	private int traversalHops;
	
	
	/**
	 * Creates a node.
	 * @param owner owner graph
	 * @param content the node will contain
	 */
	protected AbstractNode(Graph owner, Measurable<? extends Object> content) {
		this.owner = owner;
		this.wasVisited = false;
		
		if(content == null){
			this.content = new NullNodeContent();
		} else {
			this.content = content;
		}
		
		this.distance = 0;
	}

	@Override
	public Measurable<? extends Object> getContent() {
		return this.content;
	}


	@Override
	public Graph getOwner() {
		return this.owner;
	}
	
	protected void validateNodeIsInTheSameGraph(Node aNode) {
		if(this.getOwner() != aNode.getOwner()){
			new NodeNotInThisGraphException("The from node passed is not contained in this graph!", aNode);
		}
	} 

	@Override
	public List<Arc> getArcs() {
		List<Arc> arcs = new LinkedList<Arc>();
		arcs.addAll(this.getArcsOut());
		arcs.addAll(this.getArcsIn());
		return arcs;
	}
	
	@Override
	public List<Node> getReachableNeighbors() {
		List<Node> reachableNeighbors = new LinkedList<Node>();
		for (Arc arc : this.getArcsOut()) {
			Node targetNode = arc.getTargetNode();
			reachableNeighbors.add(targetNode);
		}
		return reachableNeighbors;
	}
	
	@Override
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public String toString() {
		return this.getTag();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public NodePath findShortestPathTo(Node destination,
			ShortestPathStrategy strategy) {
		int numNodes = this.owner.size();
		
		List<Node> nodes = owner.getNodes();
		long totalDistance = 0;
		
		if(numNodes > 0){
			switch (strategy) {
				case BINARY_DIJKSTRA:
					totalDistance = Long.MIN_VALUE;
					initializeGraphForShortestPath(nodes);
					
					NodeBasedBinaryHeap pq = new NodeBasedBinaryHeap((Collection<? extends AbstractNode>) nodes, new Comparator<AbstractNode>() {
						@Override
						public int compare(AbstractNode o1, AbstractNode o2) {
							return o1.distance - o2.distance;
						}
					});
					
					AbstractNode currentNode = null;
					while(!destination.equals(currentNode) && !pq.isEmpty()){
						currentNode = (AbstractNode) pq.poll();
						for (Arc arc : currentNode.getArcsOut()) {
							AbstractNode neigboringNode = (AbstractNode) arc.getTargetNode();
							int distance = arc.getArcContent().measure();
							relax(currentNode, neigboringNode, distance, pq);
						}
					}
					totalDistance = currentNode.distance;
					break;
				default:

					break;
			}
		}
		
		List<Node> shortestPath = ((AbstractNode)destination).createTraversalNodePath();
		
		NodePath nodePath = new NodePath(shortestPath, totalDistance);
		
		return nodePath;
	}

	private List<Node> createTraversalNodePath() {
		int size = this.traversalHops + 1;
		if(size <= 1){
			return new ArrayList<Node>();
		}
		Node[] result = new Node[size];
		AbstractNode cNode = this;
		for(int i = size-1; i >= 0; i--){
			result[i] = cNode;
			cNode = cNode.traversalParent;
		}
		return Arrays.asList(result);
	}

	/**
	 * initializes this node's owner to start a path algorithm 
	 * */
	private void initializeGraphForShortestPath(List<Node> nodes) {
		for (Node node : nodes) {
			AbstractNode nodeImpl = (AbstractNode) node;
			nodeImpl.distance = Integer.MAX_VALUE;
			nodeImpl.traversalParent = null;
			nodeImpl.traversalHops = 0;
		}
		this.distance = 0;
	}

	private void relax(AbstractNode minNode, AbstractNode neighbor, int distanceBetween, NodeBasedBinaryHeap pq){
		int uDistance = minNode.distance + distanceBetween;
		if(neighbor.distance > uDistance){
			neighbor.distance = uDistance;
			neighbor.traversalParent = minNode;
			neighbor.traversalHops = minNode.traversalHops + 1;
			pq.remove(neighbor);
			pq.add(neighbor);
		}
	}

	public boolean wasVisited() {
		return this.wasVisited;
	}
	
	public void setVisited(boolean wasVisited) {
		this.wasVisited = wasVisited;
	}
	
	@Override
	public int compareTo(Node node) {
		AbstractNode nodeImpl = (AbstractNode) node;
		return (int) (this.distance - nodeImpl.distance);
	}
	
	@Override
	public int getInsertionOrder() {
		return insertionOrder;
	}

	@Override
	public void setInsertionOrder(int insertionOrder) {
		this.insertionOrder = insertionOrder;
	}
}
