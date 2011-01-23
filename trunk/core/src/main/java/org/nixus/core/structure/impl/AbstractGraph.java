package org.nixus.core.structure.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.auxiliary.NodeVisitor;
import org.nixus.core.structure.exceptions.NotADirectedAcyclicGraphException;
import org.nixus.core.structure.nodes.HiddenNodeAbstraction;
import org.nixus.core.structure.nodes.Node;
import org.nixus.core.structure.nodes.impl.AbstractNode;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = 7724173851692313275L;

	private static final String NODE_DEFAULT_TAG_START = "Node ";

	protected int nodeCount;

	/**
	 * Graph nodes
	 */
	protected List<Node> nodes;
	
	protected List<Arc> arcs;

	public AbstractGraph(){
		this.arcs = new ArrayList<Arc>();
	}
	
	public AbstractGraph(String tag){
		this.arcs = new ArrayList<Arc>();
	}
	
	@Override
	public boolean isEmpty() {
		return this.nodeCount == 0;
	}

	@Override
	public int size() {
		return this.nodeCount;
	}
	
	@Override
	public List<Node> getNodes() {
		return this.nodes;
	}
	
	@Override
	public List<Arc> getArcs(){
		return this.arcs;
	}
	
	@Override
	public Node addNode(Measurable<? extends Object> content) {
		String tag = NODE_DEFAULT_TAG_START + nodeCount;
		Node node = addNode(content, tag );
		return node;
	}
	
	@Override
	public List<Node> depthFirstTraversal(Node start, NodeVisitor transformer) {
		List<Node> returnList = depthFirstTraverseOnly(start);
		for (Node node : returnList) {
			((AbstractNode)node).setVisited(false);
			transformer.visit(node);
		}
		return returnList;
	}
	
	@Override
	public List<Node> depthFirstTraversal(Node start) {
		List<Node> returnList = depthFirstTraverseOnly(start);
		for (Node node : returnList) {
			((AbstractNode)node).setVisited(false);
		}
		return returnList;
	}

	public void addArc(Arc arc) {
		this.arcs.add(arc);
	}
	
	@Override
	public List<Node> breathFirstTraversal(Node start) {
		List<Node> returnList = this.breathFirstTraverseOnly(start);
		
		for (Node node : returnList) {
			((AbstractNode)node).setVisited(false);
		}
		return returnList;
	}
	
	@Override
	public List<Node> breathFirstTraversal(Node start,
			NodeVisitor transformer) {
		List<Node> returnList = this.breathFirstTraverseOnly(start);
		
		for (Node node : returnList) {
			((AbstractNode)node).setVisited(false);
			transformer.visit(node);
		}
		return returnList;
	}
	
	/**
	 * Doesn't clean the visited flags 
	 * */
	private List<Node> breathFirstTraverseOnly(Node start) {
		Queue<Node> nodeQueue = new LinkedList<Node>();
		List<Node> returnList = new LinkedList<Node>();
		
		nodeQueue.add(start);
		while(!nodeQueue.isEmpty()){
			Node parentNode = nodeQueue.poll();
			returnList.add(parentNode);
			((AbstractNode)parentNode).setVisited(true);
			List<Node> neighbors = parentNode.getReachableNeighbors();
			
			for (Node cNode : neighbors) {
				if(!((AbstractNode)cNode).wasVisited()){
					nodeQueue.add(cNode);
				}
			}
		}
		
		return returnList;
	}
	
	/**
	 * Doesn't clean the visited flags 
	 * */
	private List<Node> depthFirstTraverseOnly(Node start){
		Stack<Node> nodeStack = new Stack<Node>();
		List<Node> returnList = new LinkedList<Node>();
		nodeStack.push(start);
		
		while(!nodeStack.isEmpty()){
			Node cNode = nodeStack.pop();
			((AbstractNode)cNode).setVisited(true);
			returnList.add(cNode);
			List<Node> neighbors = cNode.getReachableNeighbors();
			for (Node node : neighbors) {
				if(!((AbstractNode)node).wasVisited()){
					nodeStack.push(node);	
				};
			}
		}
		
		return returnList;
	}
	
	protected void commonNodeAdd(Node node, String tag) {
		this.nodes.add(node);
		((HiddenNodeAbstraction)node).setInsertionOrder(nodeCount);
		this.nodeCount++;
		node.setTag(tag);
	}
	
	@Override
	public List<Node> getNodesInTopologicalOrder() throws NotADirectedAcyclicGraphException{
		List<Node> topologicalSortedNodeList = new ArrayList<Node>(this.nodeCount);
		
		Queue<Node> startNodes = initializeTopologicalOrder();
		
		int numEdges = this.getArcs().size();
		
		//process algorithm
		while(!startNodes.isEmpty()){
			Node from = startNodes.poll();
			topologicalSortedNodeList.add(from);
			for (Node to : from.getReachableNeighbors()) {
				AbstractNode toNode = (AbstractNode)to;
				toNode.decCurrentIncomingArcs();
				numEdges--;
				if(toNode.hasNoMoreIncomingArcs()){
					startNodes.add(toNode);
				}
			}
		}
		//Is this really a DAG?
		if(numEdges > 0){
			throw new NotADirectedAcyclicGraphException("his Graph is not a Directed Acyclic Graph.");
		}
		
		return topologicalSortedNodeList;
	}
	
	/**
	 * Initializes the graph for the algorithm and 
	 * returns the start nodes of the algorithm (nodes 
	 * with no incoming arcs/edges)
	 * */
	private Queue<Node> initializeTopologicalOrder() {
		Queue<Node> startNodes = new LinkedList<Node>();
		for (Node node : this.getNodes()) {
			((AbstractNode)node).resetCurrentIncomingArcs();
			if(node.getArcsIn().size() == 0){
				startNodes.add(node);
			}
		}
		return startNodes;
	}

	@Override
	public boolean contains(Object o) {
		return nodes.contains(o);
	}


	@Override
	public Iterator<Node> iterator() {
		return this.nodes.iterator();
	}


	@Override
	public Object[] toArray() {
		return this.nodes.toArray();
	}


	@Override
	public <T> T[] toArray(T[] a) {
		return this.nodes.toArray(a);
	}


	@Override
	public boolean add(Node e) {
		this.addNode(e.getContent());
		return true;
	}

	@Override
	/**
	 * Not supported
	 * */
	public boolean remove(Object o) {
		//TODO: implement
		throw new RuntimeException("Not yet supported");
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		return this.nodes.containsAll(c);
	}


	@Override
	public boolean addAll(Collection<? extends Node> c) {
		for (Node node : c) {
			this.add(node);
		}
		return true;
	}


	@Override
	/**
	 * Not supported
	 * */
	public boolean removeAll(Collection<?> c) {
		for (Object node : c) {
			this.remove(node);
		}
		return true;
	}


	@Override
	/**
	 * Not supported
	 * */
	public boolean retainAll(Collection<?> c) {
		throw new RuntimeException("Not supported operation");
	}


	@Override
	/**
	 * Not supported
	 * */
	public void clear() {
		for (Object node : this.nodes) {
			this.remove(node);
		}
	}
	
}
