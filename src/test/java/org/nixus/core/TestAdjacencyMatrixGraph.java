package org.nixus.core;

import org.nixus.core.structure.Graph;
import org.nixus.core.structure.impl.AdjacencyMatrixGraph;


public class TestAdjacencyMatrixGraph extends AbstractGraphTest {
	
	@Override
	protected Graph buildGraph() {
		Graph aGraph = new AdjacencyMatrixGraph();
		return aGraph;
	}
	
}
