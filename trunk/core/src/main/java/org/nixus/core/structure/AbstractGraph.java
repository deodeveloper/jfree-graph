package org.nixus.core.structure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.auxiliary.NodeTransformer;
import org.nixus.core.structure.nodes.AbstractNode;
import org.nixus.core.structure.nodes.Node;

public abstract class AbstractGraph implements Graph {

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
	public long size() {
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
	public Node addNode(Measurable<? extends Object, ? extends Object> content) {
		String tag = NODE_DEFAULT_TAG_START + nodeCount;
		return addNode(content, tag );
	}
	
	@Override
	public List<Node> depthFirstTraversal(Node start, NodeTransformer<Node> transformer) {
		List<Node> returnList = depthFirstTraverseOnly(start);
		for (Node node : returnList) {
			((AbstractNode)node).setVisited(false);
			transformer.transform(node);
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
			NodeTransformer<Node> transformer) {
		List<Node> returnList = this.breathFirstTraverseOnly(start);
		
		for (Node node : returnList) {
			((AbstractNode)node).setVisited(false);
			transformer.transform(node);
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
			System.out.println(cNode);
			System.out.println(neighbors);
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
		this.nodeCount++;
		node.setTag(tag);
	}
}
