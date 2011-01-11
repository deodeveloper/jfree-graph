package org.nixus.core.strategies;

public enum ShortestPathStrategy {
	/**
	 * This strategy will find the shortest path using a modified binary heap in the implementation. 
	 * The complexity to be expected is O(E.log(V)) where E is the number of edges/arcs and V is the number 
	 * of vertex/nodes of the graph. Negative arcs will result in unexpected behavior, use BELLMAN_FORD 
	 * strategy instead. 
	 * */
	BINARY_DIJKSTRA,
	BINARY_BELLMAN_FORD
}
