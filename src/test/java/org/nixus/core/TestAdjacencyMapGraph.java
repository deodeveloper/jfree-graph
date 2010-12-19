package org.nixus.core;

import org.nixus.core.structure.Graph;
import org.nixus.core.structure.impl.AdjacencyMapGraph;

public class TestAdjacencyMapGraph extends AbstractGraphTest {
	
	@Override
	protected Graph buildGraph() {
		Graph aGraph = new AdjacencyMapGraph();
		return aGraph;
	}
}
