package org.nixus.core.strategies;


public enum ShortestPathStrategy {
	/**
	 * This strategy will find the shortest path using a modified binary heap in the implementation. 
	 * The complexity to be expected is O(E.log(V)) where E is the number of edges/arcs and V is the number 
	 * of vertex/nodes of the graph. 
	 * NOTE: Negative arcs will result in unexpected behavior, use BELLMAN_FORD 
	 * strategy instead. 
	 * */
	BINARY_DIJKSTRA,
	/**
	 * This strategy will find the shortest path with a complexity expected of O(E.V) where 
	 * E is the number of edges/arcs and V is the number of vertex/nodes of the graph.
	 * This algorithm can not find shortest paths when negative weight cycles are 
	 * present(finding the shortest path is absurd in this cases), so a NegativeWeightCycleFoundException
	 * is thrown if it is the case. 
	 * */
	BELLMAN_FORD
}
