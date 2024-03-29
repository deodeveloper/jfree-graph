package org.nixus.core.structure;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.auxiliary.NodeVisitor;
import org.nixus.core.structure.exceptions.NotADirectedAcyclicGraphException;
import org.nixus.core.structure.nodes.Node;

/**
 * Public graph interface
 * @author gwachnitz
 */
public interface Graph extends Serializable, Collection<Node>{
	

	/**
	 * Adds a node to the graph with the specified content. Its tag 
	 * will be the order of its creation
	 * @param content the content of the node.
	 * @return the recently created node.
	 */
	public Node addNode(Measurable<? extends Object> content);
	
	/**
	 * Adds a node to the graph with the specified content.
	 * @param content the content of the node.
	 * @param tag a tag for this node(used for toString purposes)
	 * @return the recently created node.
	 */
	public Node addNode(Measurable<? extends Object> content, String tag);
	
	/**
	 * @return true if the graph has no nodes in it, false otherwise
	 * */
	@Override
	public boolean isEmpty();

	/**
	 * @return the number of nodes this graph has
	 */
	@Override
	public int size();
	
	/**
	 * Returns the nodes of this graph. The list is expected to be used as a read only.  
	 * Modifications may turn in unexpected results.
	 * */
	public List<Node> getNodes();
	
	/**
	 * Returns the arcs of this graph. The list is expected to be used as a read only. 
	 * Modifications may turn in unexpected results.
	 * */
	public List<Arc> getArcs();
	
	/**
	 * Makes a depth first traversal on the graph, applies the transformer to the content of the nodes 
	 * and finally returns the List of nodes in the traverse order.
	 * @param start sets the node where the algorithm should begin.
	 * @param transformer object to transform the node content when traversing.
	 * @return the List of nodes in the traverse order with the transformation done. 
	 * */
	public List<Node> depthFirstTraversal(Node start, NodeVisitor transformer);
	
	/**
	 * Makes a depth first traversal on the graph and returns the List of nodes 
	 * in the traverse order.
	 * @param start sets the node where the algorithm should begin.
	 * @return the List of nodes in the traverse order.
	 * */
	public List<Node> depthFirstTraversal(Node start);
	
	/**
	 * Makes a breath first traversal on the graph, applies the transformer to the content of the nodes 
	 * and finally returns the List of nodes in the traverse order.
	 * @param start sets the node where the algorithm should begin.
	 * @param transformer object to transform the node content when traversing. 
	 * @return the List of nodes in the traverse order with the transformation done. 
	 * */
	public List<Node> breathFirstTraversal(Node start, NodeVisitor transformer);
	
	/**
	 * Makes a breath first traversal on the graph and returns the List of nodes 
	 * in the traverse order.
	 * @param start sets the node where the algorithm should begin.
	 * @return the List of nodes in the traverse order.
	 * */
	public List<Node> breathFirstTraversal(Node start);
	
	/**
	 * @return the list of nodes of this graph in topological order.
	 * @throws NotADirectedAcyclicGraphException when the graph is not a directed acyclic graph. 
	 * In this case a topological order is absurd  
	 * */
	public List<Node> getNodesInTopologicalOrder() throws NotADirectedAcyclicGraphException;
	
}
