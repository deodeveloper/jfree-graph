package org.nixus.core.structure.nodes;

import java.util.Collection;
import java.util.List;

import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;

public interface Node {

	/**
	 * @return the node content.
	 * */
	Measurable<? extends Object, ? extends Object> getContent();

	/**
	 * @return the graph that owns this node
	 * */
	Graph getOwner();


	/**
	 * @return true if this node is connected to another node, false else otherwise. Notice that a the 
	 * check is directional
	 */
	boolean isDirectionallyConnectedTo(Node aNode);
	
	/**
	 * @return true if aNode is connected to anotherNode, else otherwise. This method 
	 * checks both directions of the connection.
	 */
	boolean isConnectedTo(Node aNode);
	
	/**
	 * Connects 2 nodes of a graph with and edge between them. This connection is a single direction(targetNode). 
	 * @param toNode Ending node.
	 * @param arcContent content of the edge between the two nodes.
	 */
	Arc addArcTo(Node targetNode, Measurable<? extends Object, ? extends Object> arcContent);

	/**
	 * Connects 2 nodes of a graph with and edge between them. This connection is in both directions. 
	 * @param anotherNode Ending node.
	 * @param arcContent content of the edge between the two nodes.
	 */
	Arc addArc(Node aNode, Measurable<? extends Object, ? extends Object> arcContent);
	
	
	/**
	 * Gets a list of arcs directed at this node.
	 * */
	Collection<Arc> getArcsIn();
	
	/**
	 * Gets a list of arcs directed out of this node.
	 * */
	Collection<Arc> getArcsOut();
	
	/**
	 * Gets the list of arcs this node connects to.
	 * */
	Collection<Arc> getArcs();

	/**
	 * Gets the nodes from which this node can traverse too
	 * */
	List<Node> getReachableNeighbors();

	/**
	 * Sets this node tag.
	 * */
	void setTag(String tag);

	/**
	 * This return the tag of this node. 
	 * */
	String getTag();
}
