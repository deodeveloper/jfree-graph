package org.nixus.core.structure.nodes.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.nixus.core.structure.AbstractGraph;
import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.impl.AdjacencyMatrixGraph;
import org.nixus.core.structure.impl.ArcImpl;
import org.nixus.core.structure.nodes.AbstractNode;
import org.nixus.core.structure.nodes.Node;


/**
 * Base node implementation for {@link AdjacencyMatrixGraph}'s.
 * */
public class AdjacencyMatrixNode extends AbstractNode {

	protected int index;
	
	/**
	 * {@inheritDoc}
	 * @param index index of this node within the adjacency matrix
	 */
	public AdjacencyMatrixNode(Graph owner, Measurable<? extends Object, ? extends Object> content, int index) {
		super(owner, content);
		this.index = index;
	}

	@Override
	public Arc addArcTo(Node targetNode, Measurable<? extends Object, ? extends Object> arcContent) {
		this.validateNodeIsInTheSameGraph(targetNode);
		Arc arc = new ArcImpl(this, targetNode, arcContent);
		((AdjacencyMatrixGraph)owner).getAdjacencyMatrix()[this.index][((AdjacencyMatrixNode)targetNode).index] = arc;
		((AbstractGraph)this.getOwner()).addArc(arc);
		return arc;
	}
	
	@Override
	public Arc addArc(Node aNode, Measurable<? extends Object, ? extends Object> arcContent) {
		this.validateNodeIsInTheSameGraph(aNode);
		Arc arc = new ArcImpl(this, aNode, arcContent);
		((AdjacencyMatrixGraph)owner).getAdjacencyMatrix()[this.index][((AdjacencyMatrixNode)aNode).index] = arc;
		aNode.addArcTo(this, arcContent);
		return arc;
	}
	private Arc getArcTo(Node aNode) {
		return ((AdjacencyMatrixGraph)owner).getAdjacencyMatrix()[this.index][((AdjacencyMatrixNode)aNode).index];
	}

	@Override
	public boolean isConnectedTo(Node aNode) {
		Arc arc = this.getArcTo(aNode);
		Arc opositeArc = this.getArcTo(aNode);
		return arc != null && opositeArc != null;
	}
	
	@Override
	public boolean isDirectionallyConnectedTo(Node aNode) {
		Arc arc = this.getArcTo(aNode);
		return arc != null;
	}

	@Override
	public Collection<Arc> getArcsIn() {
		List<Arc> arcsOut = new LinkedList<Arc>();
		Arc[][] adjacencyMatrix = ((AdjacencyMatrixGraph)owner).getAdjacencyMatrix();
		for(int i = 0; i < owner.size(); i++){
			Arc cArc = adjacencyMatrix[i][this.index];
			if(cArc != null){
				arcsOut.add(cArc);
			}
		}
		return arcsOut;
	}

	@Override
	public Collection<Arc> getArcsOut() {
		List<Arc> arcsOut = new LinkedList<Arc>();
		Arc[][] adjacencyMatrix = ((AdjacencyMatrixGraph)owner).getAdjacencyMatrix();
		for(int i = 0; i < owner.size(); i++){
			Arc cArc = adjacencyMatrix[this.index][i];
			if(cArc != null){
				arcsOut.add(cArc);
			}
		}
		return arcsOut;
	}

}
