package org.nixus.core.structure.impl;

import org.nixus.core.structure.Graph;

/**
 * Graph factory
 * */
public class GraphFactory {

	/**
	 * Instantiates a new AdjacencyMapGraph
	 * */
	public static Graph instantiateAdjacencyMapGraph() {
		return new AdjacencyMapGraph();
	}
	
	/**
	 * Creates a graph with the initial size of the underling adjacency matrix being 64 
	 * and the growth factor 1.5 
	 * */
	public static Graph instantiateAdjacencyMatrixGraph() {
		return new AdjacencyMatrixGraph();
	}
	
	/**
	 * @param initialSize specifies the initial size of the underling adjacency matrix
	 * @param growthFactor a number(must be grater than 1) that specifies by how much 
	 * 	will grow the underling adjacency matrix if space isn't enough to hold a new node.   
	 * */
	public static Graph instantiateAdjacencyMatrixGraph(final int initialSize, final int growthFactor) {
		return new AdjacencyMatrixGraph(initialSize, growthFactor);
	}
	 

}
