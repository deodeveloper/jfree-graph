package org.nixus.core.structure.exceptions;

import org.nixus.core.structure.nodes.Node;

/**
 * Thrown when an operation with a node was done with a graph that doesn't contain it. 
 * Access to the node is provided thru the getNode method.
 * */
public class NodeNotInThisGraphException extends RuntimeException{

	private static final long serialVersionUID = 2797502788824090582L;
	
	private final Node node;
	
	public NodeNotInThisGraphException(String msg, Node fromNode) {
		super(msg);
		this.node = fromNode;
	}

	/**
	 * Node who caused the trouble.
	 * */
	public Node getNode() {
		return node;
	}
}
