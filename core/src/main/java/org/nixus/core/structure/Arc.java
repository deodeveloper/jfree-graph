package org.nixus.core.structure;

import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.nodes.Node;


/**
 * Represents an arc/edge of a Graph
 * */
public interface Arc {

	/**
	 * Gets the start node of this arc
	 * */
	Node getSourceNode();

	/**
	 * Gets the content of this arc
	 * */
	Measurable<? extends Object, ? extends Object> getArcContent();

	/**
	 * Gets the end node of this arc
	 * */
	Node getTargetNode();

}
