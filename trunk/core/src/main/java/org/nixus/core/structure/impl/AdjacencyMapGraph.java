package org.nixus.core.structure.impl;

import java.util.LinkedList;

import org.nixus.core.structure.AbstractGraph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.nodes.Node;
import org.nixus.core.structure.nodes.impl.AdjacencyMapNode;

public class AdjacencyMapGraph extends AbstractGraph {
	
	private static final long serialVersionUID = -4310812027462596404L;


	public AdjacencyMapGraph() {
		this.nodes = new LinkedList<Node>();
		this.nodeCount = 0;
	}
	

	@Override
	public Node addNode(Measurable<? extends Object, ? extends Object> content, String tag) {
		Node node = new AdjacencyMapNode(this, content);
		super.commonNodeAdd(node, tag);
		return node;
	}
	
}
