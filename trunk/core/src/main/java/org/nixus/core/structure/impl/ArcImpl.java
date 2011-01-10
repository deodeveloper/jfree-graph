package org.nixus.core.structure.impl;

import org.nixus.core.structure.Arc;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.nodes.Node;

public class ArcImpl implements Arc {

	private static final long serialVersionUID = 7984234560219375034L;

	private Measurable<? extends Object> arcContent;
	
	private Node sourceNode;
	
	private Node targetNode;
	
	private String tag; 
	
	public ArcImpl(Node sourceNode, Node targetNode,
			Measurable<? extends Object> edgeContent) {
		this.arcContent = edgeContent;
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.tag = "[ "+sourceNode+", "+targetNode+"]";
	}

	@Override
	public Measurable<? extends Object> getArcContent() {
		return arcContent;
	}

	@Override
	public Node getSourceNode() {
		return sourceNode;
	}

	@Override
	public Node getTargetNode() {
		return targetNode;
	}

	@Override
	public String toString() {
		return tag;
	}
	
}
