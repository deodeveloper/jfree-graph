package org.nixus.core;

import org.nixus.core.structure.Graph;
import org.nixus.core.structure.impl.GraphFactory;


public class TestAdjacencyMatrixGraph extends AbstractGraphTest {
	
	@Override
	protected Graph buildGraph() {
		Graph aGraph = GraphFactory.instantiateAdjacencyMatrixGraph();
		return aGraph;
	}
	
}
