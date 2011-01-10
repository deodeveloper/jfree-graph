package org.nixus.core.strategies;

public enum ShortestPathStrategy {
	/**
	 * This strategy will find the shortest path using a modified binary heap in the implementation. 
	 * The complexity to be expected is O(E.log(V)) where E is the number of edges/arcs and V is the number 
	 * of vertex/nodes of the graph.
	 * */
	BINARY_DIJKSTRA
}
