package org.nixus.core.structure.nodes;

import java.util.LinkedList;
import java.util.List;

import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.auxiliary.NullNodeContent;
import org.nixus.core.structure.exceptions.NodeNotInThisGraphException;



/**
 * Node interface for method only accessed by the graph implementation
 * */
public abstract class AbstractNode implements Node{
	
	private static final long serialVersionUID = 4367531029777710724L;

	/**
	 * Contend of this graph
	 * */
	protected Measurable<? extends Object, ? extends Object> content;
	
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
	 * Creates a node.
	 * @param owner owner graph
	 * @param content the node will contain
	 */
	protected AbstractNode(Graph owner, Measurable<? extends Object, ? extends Object> content) {
		this.owner = owner;
		this.wasVisited = false;
		
		if(content == null){
			this.content = new NullNodeContent();
		} else {
			this.content = content;
		}
	}

	@Override
	public Measurable<? extends Object, ? extends Object> getContent() {
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

	public boolean wasVisited() {
		return this.wasVisited;
	}
	
	public void setVisited(boolean wasVisited) {
		this.wasVisited = wasVisited;
	}
}
