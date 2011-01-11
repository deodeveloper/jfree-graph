package org.nixus.core.structure.nodes.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nixus.core.structure.AbstractGraph;
import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.impl.ArcImpl;
import org.nixus.core.structure.nodes.AbstractNode;
import org.nixus.core.structure.nodes.Node;


/**
 * Base node implementation for {@link AdjacencyMapGraph}'s.
 * */
public class AdjacencyMapNode extends AbstractNode {

	private static final long serialVersionUID = 6988184205744230216L;
	
	private Map<Node, Arc> arcsOut;
	private List<Arc> arcsIn;
	
	/**
	 * {@inheritDoc}
	 */
	public AdjacencyMapNode(Graph owner, Measurable<? extends Object> content) {
		super(owner, content);
		this.arcsOut = new LinkedHashMap<Node, Arc>();
		this.arcsIn = new LinkedList<Arc>();
	}

	@Override
	public Arc addArcTo(Node targetNode, Measurable<? extends Object> arcContent) {
		this.validateNodeIsInTheSameGraph(targetNode);
		Arc arc = new ArcImpl(this, targetNode, arcContent);
		this.arcsOut.put(targetNode, arc);
		((AbstractGraph)this.getOwner()).addArc(arc);
		((AdjacencyMapNode)targetNode).addArcIn(arc);
		return arc;
	}
	
	@Override
	public Arc addArc(Node aNode, Measurable<? extends Object> arcContent) {
		this.validateNodeIsInTheSameGraph(aNode);
		Arc arc = new ArcImpl(this, aNode, arcContent);
		this.arcsOut.put(aNode, arc);
		aNode.addArcTo(this, arcContent);
		return arc;
	}

	@Override
	public boolean isConnectedTo(Node aNode) {
		return this.arcsOut.containsKey(aNode) && aNode.isDirectionallyConnectedTo(this);
	}

	@Override
	public boolean isDirectionallyConnectedTo(Node aNode) {
		return this.arcsOut.containsKey(aNode);
	}

	@Override
	public Collection<Arc> getArcsIn() {
		return this.arcsIn;
	}

	@Override
	public Collection<Arc> getArcsOut() {
		return this.arcsOut.values();
	}

	private void addArcIn(Arc arcIn){
		this.arcsIn.add(arcIn);
	}
}
