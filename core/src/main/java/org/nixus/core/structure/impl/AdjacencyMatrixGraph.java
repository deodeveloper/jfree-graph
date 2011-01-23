package org.nixus.core.structure.impl;

import java.util.ArrayList;

import org.nixus.core.structure.Arc;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.nodes.Node;
import org.nixus.core.structure.nodes.impl.AdjacencyMatrixNode;

public class AdjacencyMatrixGraph extends AbstractGraph {

	private static final long serialVersionUID = -7669246707726460518L;

	/**
	 * Graph nodes
	 */
	private Arc [][] adjacencyMatrix;
	
	private int currentMatrixSize;

	private final float growthFactor;
	
	
	/**
	 * Creates a graph with the initial size of the underling adjacency matrix being 64 
	 * and the growth factor 1.5 
	 * */
	protected AdjacencyMatrixGraph(){
		this(64, 1.5F);
	}
	
	/**
	 * @param initialSize specifies the initial size of the underling adjacency matrix
	 * @param growthFactor a number(must be grater than 1) that specifies by how much 
	 * 	will grow the underling adjacency matrix if space isn't enough to hold a new node.   
	 * */
	protected AdjacencyMatrixGraph(final int initialSize, final float growthFactor) {
		this.nodes = new ArrayList<Node>();
		this.nodeCount = 0;
		this.adjacencyMatrix = new Arc[initialSize][initialSize];
		this.currentMatrixSize = initialSize;
		this.growthFactor = growthFactor;
	}
	

	@Override
	public Node addNode(Measurable<? extends Object> content, String tag) {
		Node node = new AdjacencyMatrixNode(this, content, nodeCount);
		if(nodeCount >= currentMatrixSize){
			expandAdjacencyMatrix();
		}
		super.commonNodeAdd(node, tag);
		return node;
	}

	private void expandAdjacencyMatrix() {
		int newMSize = Math.max((int)(currentMatrixSize * growthFactor), currentMatrixSize + 1);
		
		Arc[][] newAdjacencyMatrix = new Arc[newMSize][newMSize];
		
		for(int i = 0; i < currentMatrixSize; i++){
			for(int j = 0; j < currentMatrixSize; j++){
				newAdjacencyMatrix[i][j] = adjacencyMatrix[i][j];
			}
		}
		this.adjacencyMatrix = newAdjacencyMatrix;
	}

	public Arc[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}
}
