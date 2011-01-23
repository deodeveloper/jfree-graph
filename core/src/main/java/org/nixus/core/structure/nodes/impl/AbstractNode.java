package org.nixus.core.structure.nodes.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nixus.core.strategies.ShortestPathStrategy;
import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.auxiliary.NodeBasedBinaryHeap;
import org.nixus.core.structure.auxiliary.NullNodeContent;
import org.nixus.core.structure.exceptions.NegativeWeightCycleFoundException;
import org.nixus.core.structure.exceptions.NodeNotInThisGraphException;
import org.nixus.core.structure.impl.AbstractGraph;
import org.nixus.core.structure.nodes.HiddenNodeAbstraction;
import org.nixus.core.structure.nodes.Node;
import org.nixus.core.structure.nodes.NodePath;



/**
 * Node interface for method only accessed by the graph implementation
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
	 * Number of incoming arcs/edges this node has
	 * */
	private int numIncomingArcs;

	/**
	 * Used for some algorithms (e.g. topological order)
	 * */
	private int numCurrentIncomingArcs;
	
	
	/**
	 * Creates a node.
	 * @param owner owner graph
	 * @param content the node will contain
	 */
	protected AbstractNode(Graph owner, Measurable<? extends Object> content) {
		this.owner = owner;
		this.wasVisited = false;

		this.numIncomingArcs = 0;
		this.numCurrentIncomingArcs = 0;
		
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
	public Arc addArcTo(Node targetNode, Measurable<? extends Object> arcContent){
		Arc arc = addArcToImplementation(targetNode, arcContent);
		((AbstractGraph)this.getOwner()).addArc(arc);
		((AbstractNode)targetNode).incrementIncomingArcs();
		return arc;
	}
	
	private void incrementIncomingArcs() {
		this.numIncomingArcs++;
		this.numCurrentIncomingArcs++;
	}

	protected abstract Arc addArcToImplementation(Node targetNode,
			Measurable<? extends Object> arcContent);

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
	public Map<Node,NodePath> findShortestPathToEveryOtherNode(ShortestPathStrategy strategy) {
		int numNodes = this.owner.size();
		List<Node> nodes = owner.getNodes();
		initializeGraphForShortestPath(nodes);
		
		if(numNodes > 0){
			switch (strategy) {
				case BINARY_DIJKSTRA:
					allShortestPathsBinaryDijkstra(nodes);
					break;
				case BELLMAN_FORD:
					shortestPathBellmanFordAlgorithm(nodes);
					break;
				case DAG:
					shortestPathDAGAlgorithm(nodes);
					break;
				default:
					break;
			}
		}
		
		
		Map<Node,NodePath> nodePaths = new LinkedHashMap<Node, NodePath>();

		//Build all paths
		for (Node node : nodes) {
			addNodePath(nodePaths, node);
		}
		
		return nodePaths;
	}

	private List<Node> addNodePath(Map<Node, NodePath> nodePaths, Node node) {
		if(node == null){
			return new LinkedList<Node>();
		}
		NodePath nodePathBuild = nodePaths.get(node);
		if(nodePathBuild != null){
			return nodePathBuild.getPath();
		} else {
			AbstractNode cNode = (AbstractNode) node;
			
			List<Node> nodePath = new LinkedList<Node>();
			
			AbstractNode parent = cNode.traversalParent;
			NodePath parentPath = nodePaths.get(parent);
			//was parent node path already build? 
			if(parentPath != null){
				nodePath.addAll(parentPath.getPath());
			} else {
				nodePath.addAll(addNodePath(nodePaths, parent));
			}
			if(nodePath.size()>0 || cNode == this){
				nodePath.add(cNode);
			}
			
			nodePathBuild = buildNodePath(nodePath);
			nodePaths.put(cNode, nodePathBuild);
			return nodePath;
		}
	}

	private NodePath buildNodePath(List<Node> shortestPath) {
		int numHops = shortestPath.size();
		int totalDistance = Integer.MAX_VALUE;
		if(numHops > 0){
			totalDistance = ((AbstractNode)shortestPath.get(numHops-1)).distance;
		} 
		NodePath nodePath = new NodePath(shortestPath, totalDistance);
		return nodePath;
	};
	
	@Override
	public NodePath findShortestPathTo(Node destination,
			ShortestPathStrategy strategy) {
		int numNodes = this.owner.size();
		List<Node> nodes = owner.getNodes();
		initializeGraphForShortestPath(nodes);
		
		if(numNodes > 0){
			switch (strategy) {
				case BINARY_DIJKSTRA:
					singleShortestPathBinaryDijkstra(destination, nodes);
					break;
				case BELLMAN_FORD:
					shortestPathBellmanFordAlgorithm(nodes);
					break;
				case DAG:
					shortestPathDAGAlgorithm(nodes);
					break;
				default:
					break;
			}
		}
		
		List<Node> shortestPath = ((AbstractNode)destination).createTraversalNodePath(this);
		NodePath nodePath = buildNodePath(shortestPath);
		
		return nodePath;
	}

	private void singleShortestPathBinaryDijkstra(Node destination,
			List<Node> nodes) {
		NodeBasedBinaryHeap pq = asNodeBasedBinaryHeap(nodes);
		
		AbstractNode currentNode = null;
		while(!destination.equals(currentNode) && !pq.isEmpty()){
			currentNode = dijkstraInnerLoop(pq);
		}
	}

	private void allShortestPathsBinaryDijkstra(List<Node> nodes) {
		NodeBasedBinaryHeap pq = asNodeBasedBinaryHeap(nodes);
		
		while(!pq.isEmpty()){
			dijkstraInnerLoop(pq);
		}
	}
	
	private NodeBasedBinaryHeap asNodeBasedBinaryHeap(List<Node> nodes) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		NodeBasedBinaryHeap pq = new NodeBasedBinaryHeap((Collection) nodes, new Comparator<AbstractNode>() {
			@Override
			public int compare(AbstractNode o1, AbstractNode o2) {
				return o1.distance - o2.distance;
			}
		});
		return pq;
	}

	/**
	 * Gets the current node in the queue and relaxes its neighbors
	 * */
	private AbstractNode dijkstraInnerLoop(NodeBasedBinaryHeap pq) {
		AbstractNode currentNode;
		currentNode = (AbstractNode) pq.poll();
		for (Arc arc : currentNode.getArcsOut()) {
			AbstractNode neigboringNode = (AbstractNode) arc.getTargetNode();
			int distance = arc.getArcContent().measure();
			boolean relaxationOccurred = relax(currentNode, neigboringNode, distance);
			//update priority queue
			if(relaxationOccurred){
				pq.remove(neigboringNode);
				pq.add(neigboringNode);
			}
		}
		return currentNode;
	}

	private void shortestPathDAGAlgorithm(List<Node> nodes) {
		List<Node> nodesInTopologicalOrder = this.owner.getNodesInTopologicalOrder();
		for (Node node : nodesInTopologicalOrder) {
			for (Arc arc : node.getArcsOut()) {
				relax((AbstractNode)node, (AbstractNode)arc.getTargetNode(), arc.getArcContent().measure());
			}
		}
	}

	private void shortestPathBellmanFordAlgorithm(List<Node> nodes) {
		//Relax all edges #nodes - 1 times
		for(int i = 1; i < nodes.size(); i++){
			for (Arc arc : owner.getArcs()) {
				relax(((AbstractNode)arc.getSourceNode()), ((AbstractNode)arc.getTargetNode()), arc.getArcContent().measure());
			}				
		}
		//check for negative-weight cycles
		for (Arc arc : owner.getArcs()) {
			AbstractNode src = (AbstractNode) arc.getSourceNode();
			AbstractNode dest = (AbstractNode) arc.getTargetNode();
			//casting because of possible carries
			if(dest.distance > (long)src.distance + arc.getArcContent().measure()){
				throw new NegativeWeightCycleFoundException();
			}
		}
	}

	private List<Node> createTraversalNodePath(AbstractNode source) {
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
		//Did we got an answer?
		if(!result[0].equals(source)){
			return new ArrayList<Node>();
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

	/**
	 * @return true if a relaxation of the neighbor occurred
	 * **/
	private boolean relax(AbstractNode minNode, AbstractNode neighbor, int distanceBetween){
		long uDistance = (long)minNode.distance + distanceBetween;
		if(neighbor.distance > uDistance){
			neighbor.distance = (int)uDistance;
			neighbor.traversalParent = minNode;
			neighbor.traversalHops = minNode.traversalHops + 1;
			return true;
		}
		return false;
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

	/**
	 * Used for topological sort algorithm
	 * */
	public void resetCurrentIncomingArcs() {
		this.numCurrentIncomingArcs = this.numIncomingArcs;
	}

	/**
	 * Used for topological sort algorithm
	 * */
	public void decCurrentIncomingArcs() {
		this.numCurrentIncomingArcs--;
	}

	/**
	 * Used for topological sort algorithm
	 * */
	public boolean hasNoMoreIncomingArcs() {
		return this.numCurrentIncomingArcs == 0;
	}
}
