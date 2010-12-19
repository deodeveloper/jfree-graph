package org.nixus.core.structure.auxiliary;

import org.nixus.core.structure.nodes.Node;

/**
 * Object to be implemented to transform a node content, for example during a traversal 
 * */
public interface NodeTransformer<T> {

	/**
	 * Used to transform the content of a node.
	 * */
	public void transform(Node node);

}
