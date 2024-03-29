package org.nixus.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.nixus.core.strategies.ShortestPathStrategy;
import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.auxiliary.NodeVisitor;
import org.nixus.core.structure.exceptions.NegativeWeightCycleFoundException;
import org.nixus.core.structure.exceptions.NotADirectedAcyclicGraphException;
import org.nixus.core.structure.nodes.Node;
import org.nixus.core.structure.nodes.NodePath;

public abstract class AbstractGraphTest extends TestCase {
	protected abstract Graph buildGraph();
	
	public void testGraphNodeCount(){
		Graph aGraph = buildGraph();
		
		assertTrue(aGraph.isEmpty());
		
		aGraph.addNode(new MockContent());
		aGraph.addNode(new MockContent());
		aGraph.addNode(new MockContent());
		
		assertEquals(aGraph.size(), 3);
		
		aGraph.addNode(new MockContent());
		
		assertEquals(aGraph.size(), 4);
	}
	
	public void testNonConnected(){
		Graph aGraph = buildGraph();
		
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		
		assertFalse(node1.isDirectionallyConnectedTo(node2));
		assertFalse(node1.isDirectionallyConnectedTo(node3));
		assertFalse(node2.isDirectionallyConnectedTo(node3));
		assertFalse(node1.isConnectedTo(node2));
		assertFalse(node1.isConnectedTo(node3));
		assertFalse(node2.isConnectedTo(node3));

	}
	
	public void testDirectedConnection(){
		Graph aGraph = buildGraph();
		
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());

		node1.addArcTo(node2, new MockContent());
		
		assertFalse(node2.isDirectionallyConnectedTo(node1));
		assertTrue(node1.isDirectionallyConnectedTo(node2));
		
		node3.addArcTo(node2, new MockContent());
		node2.addArcTo(node3, new MockContent());
		
		assertTrue(node3.isDirectionallyConnectedTo(node2));
		assertTrue(node2.isDirectionallyConnectedTo(node3));
	}
	
	public void testUndirectedConnection(){
		Graph aGraph = buildGraph();
		
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());

		node1.addArc(node2, new MockContent());
		
		assertTrue(node2.isConnectedTo(node1));
		assertFalse(node3.isConnectedTo(node2));
		
		node3.addArc(node2, new MockContent());
		
		assertTrue(node3.isConnectedTo(node2));
	}
	
	public void testArcsIn(){
		Graph aGraph = buildGraph();
		
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());

		Arc arc12 = node1.addArcTo(node2, new MockContent());
		Arc arc21 = node2.addArcTo(node1, new MockContent());
		Arc arc23 = node2.addArcTo(node3, new MockContent());
		Arc arc32 = node3.addArcTo(node2, new MockContent());
		
		Collection<Arc> arcsIn = node1.getArcsIn();
		
		assertFalse(arcsIn.contains(arc12));
		assertTrue(arcsIn.contains(arc21));
		assertFalse(arcsIn.contains(arc23));
		assertFalse(arcsIn.contains(arc32));
		
		arcsIn = node2.getArcsIn();
		
		assertTrue(arcsIn.contains(arc12));
		assertFalse(arcsIn.contains(arc21));
		assertFalse(arcsIn.contains(arc23));
		assertTrue(arcsIn.contains(arc32));
	}
	
	public void testArcsOut(){
		Graph aGraph = buildGraph();
		
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());

		Arc arc12 = node1.addArcTo(node2, new MockContent());
		Arc arc21 = node2.addArcTo(node1, new MockContent());
		Arc arc23 = node2.addArcTo(node3, new MockContent());
		Arc arc32 = node3.addArcTo(node2, new MockContent());
		
		Collection<Arc> arcsOut = node1.getArcsOut();
		
		assertTrue(arcsOut.contains(arc12));
		assertFalse(arcsOut.contains(arc21));
		assertFalse(arcsOut.contains(arc23));
		assertFalse(arcsOut.contains(arc32));
		
		arcsOut = node2.getArcsOut();
		
		assertFalse(arcsOut.contains(arc12));
		assertTrue(arcsOut.contains(arc21));
		assertTrue(arcsOut.contains(arc23));
		assertFalse(arcsOut.contains(arc32));
	}
	
	public void testGetNodes(){
		Graph aGraph = buildGraph();
		
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		
		Collection<Node> nodes = aGraph.getNodes();
		
		assertEquals(2, nodes.size());
		assertTrue(nodes.contains(node1));
		assertTrue(nodes.contains(node2));
		
		Node node3 = aGraph.addNode(new MockContent());
		
		assertEquals(3, nodes.size());
		assertTrue(nodes.contains(node3));
	}
	
	public void testGetArcs(){
		Graph aGraph = buildGraph();
		
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		
		Arc arc12 = node1.addArcTo(node2, new MockContent());
		
		Collection<Arc> arcs = aGraph.getArcs();
		
		assertEquals(1, arcs.size());
		Arc arc = arcs.iterator().next();
		assertEquals(arc12, arc);
		assertEquals(node1, arc.getSourceNode());
		assertEquals(node2, arc.getTargetNode());
		
		node2.addArcTo(node1, new MockContent());
		node2.addArcTo(node3, new MockContent());
		node3.addArcTo(node2, new MockContent());
		
		assertEquals(4, arcs.size());
		
	}
	
	public void testSimpleDepthFirstTraversal(){
		Graph aGraph = buildGraph();
		
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		
		node0.addArcTo(node1, new MockContent());
		node0.addArcTo(node0, new MockContent());
		node1.addArcTo(node3, new MockContent());
		node3.addArcTo(node2, new MockContent());
		node2.addArcTo(node0, new MockContent());
		node2.addArcTo(node4, new MockContent());
		node1.addArcTo(node5, new MockContent());
		
		List<Node> dftResult = aGraph.depthFirstTraversal(node0);
		
		assertEquals(6,dftResult.size());
		assertEquals(node0,dftResult.get(0));
		assertEquals(node1,dftResult.get(1));
		assertEquals(node5,dftResult.get(2));
		assertEquals(node3,dftResult.get(3));
		assertEquals(node2,dftResult.get(4));
		assertEquals(node4,dftResult.get(5));
	}
	
	public void testDepthFirstTraversalWithTransformation(){
		Graph aGraph = buildGraph();
		
		Node node0 = aGraph.addNode(new TraversalCountContent());
		Node node1 = aGraph.addNode(new TraversalCountContent());
		Node node2 = aGraph.addNode(new TraversalCountContent());
		Node node3 = aGraph.addNode(new TraversalCountContent());
		Node node4 = aGraph.addNode(new TraversalCountContent());
		Node node5 = aGraph.addNode(new TraversalCountContent());
		
		node0.addArcTo(node1, new MockContent());
		node0.addArcTo(node0, new MockContent());
		node1.addArcTo(node3, new MockContent());
		node3.addArcTo(node2, new MockContent());
		node2.addArcTo(node0, new MockContent());
		node2.addArcTo(node4, new MockContent());
		node1.addArcTo(node5, new MockContent());
		
		List<Node> dftResult = aGraph.depthFirstTraversal(node0, new NodeVisitor() {
			@Override
			public void visit(Node node) {
				TraversalCountContent content = (TraversalCountContent) node.getContent();
				
				content.traversalCount = TraversalCountContent.totalTraversalCount;
				TraversalCountContent.totalTraversalCount++;
			}
		});
		
		assertEquals(6,dftResult.size());
		assertEquals(0, ((TraversalCountContent)node0.getContent()).traversalCount);
		assertEquals(1, ((TraversalCountContent)node1.getContent()).traversalCount);
		assertEquals(2, ((TraversalCountContent)node5.getContent()).traversalCount);
		assertEquals(3, ((TraversalCountContent)node3.getContent()).traversalCount);
		assertEquals(4, ((TraversalCountContent)node2.getContent()).traversalCount);
		assertEquals(5, ((TraversalCountContent)node4.getContent()).traversalCount);
		assertEquals(node0,dftResult.get(0));
		assertEquals(node1,dftResult.get(1));
		assertEquals(node5,dftResult.get(2));
		assertEquals(node3,dftResult.get(3));
		assertEquals(node2,dftResult.get(4));
		assertEquals(node4,dftResult.get(5));
		
		//Clear for other tests
		TraversalCountContent.totalTraversalCount = 0;
	}
	
	public void testSimpleBreathFirstTraversal(){
		Graph aGraph = buildGraph();
		
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		
		node0.addArcTo(node1, new MockContent());
		node0.addArcTo(node0, new MockContent());
		node0.addArcTo(node2, new MockContent());
		node3.addArcTo(node6, new MockContent());
		node1.addArcTo(node3, new MockContent());
		node3.addArcTo(node2, new MockContent());
		node2.addArcTo(node0, new MockContent());
		node2.addArcTo(node4, new MockContent());
		node1.addArcTo(node5, new MockContent());
		
		List<Node> dftResult = aGraph.breathFirstTraversal(node0);
		
		assertEquals(7,dftResult.size());
		assertEquals(node0,dftResult.get(0));
		assertEquals(node1,dftResult.get(1));
		assertEquals(node2,dftResult.get(2));
		assertEquals(node3,dftResult.get(3));
		assertEquals(node5,dftResult.get(4));
		assertEquals(node4,dftResult.get(5));
		assertEquals(node6,dftResult.get(6));
	}
	
	public void testBreathFirstTraversalWithTransformation(){
		Graph aGraph = buildGraph();
		
		Node node0 = aGraph.addNode(new TraversalCountContent());
		Node node1 = aGraph.addNode(new TraversalCountContent());
		Node node2 = aGraph.addNode(new TraversalCountContent());
		Node node3 = aGraph.addNode(new TraversalCountContent());
		Node node4 = aGraph.addNode(new TraversalCountContent());
		Node node5 = aGraph.addNode(new TraversalCountContent());
		Node node6 = aGraph.addNode(new TraversalCountContent());
		
		node0.addArcTo(node1, new MockContent());
		node0.addArcTo(node0, new MockContent());
		node0.addArcTo(node2, new MockContent());
		node3.addArcTo(node6, new MockContent());
		node1.addArcTo(node3, new MockContent());
		node3.addArcTo(node2, new MockContent());
		node2.addArcTo(node0, new MockContent());
		node2.addArcTo(node4, new MockContent());
		node1.addArcTo(node5, new MockContent());
		
		List<Node> dftResult = aGraph.breathFirstTraversal(node0, new NodeVisitor() {
			@Override
			public void visit(Node node) {
				TraversalCountContent content = (TraversalCountContent) node.getContent();
				
				content.traversalCount = TraversalCountContent.totalTraversalCount;
				TraversalCountContent.totalTraversalCount++;
			}
		});
		
		assertEquals(7,dftResult.size());
		assertEquals(0, ((TraversalCountContent)node0.getContent()).traversalCount);
		assertEquals(1, ((TraversalCountContent)node1.getContent()).traversalCount);
		assertEquals(2, ((TraversalCountContent)node2.getContent()).traversalCount);
		assertEquals(3, ((TraversalCountContent)node3.getContent()).traversalCount);
		assertEquals(5, ((TraversalCountContent)node4.getContent()).traversalCount);
		assertEquals(4, ((TraversalCountContent)node5.getContent()).traversalCount);
		assertEquals(6, ((TraversalCountContent)node6.getContent()).traversalCount);
		
		assertEquals(node1,dftResult.get(1));
		assertEquals(node2,dftResult.get(2));
		assertEquals(node3,dftResult.get(3));
		assertEquals(node5,dftResult.get(4));
		assertEquals(node4,dftResult.get(5));
		assertEquals(node6,dftResult.get(6));
		
		//Clear for other tests
		TraversalCountContent.totalTraversalCount = 0;
	}
	
	public void testBinaryDijkstra(){
		Graph aGraph = buildGraph();
		
		Node nodePre = aGraph.addNode(new MockContent());
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		
		nodePre.addArc(node0, new MockContent(10));
		node0.addArc(node1, new MockContent(20));
		node0.addArc(node2, new MockContent(10));
		node3.addArc(node6, new MockContent(7));
		node1.addArc(node3, new MockContent(3));
		node3.addArc(node2, new MockContent(15));
		node2.addArc(node4, new MockContent(17));
		node1.addArc(node5, new MockContent(9));
		node5.addArc(node6, new MockContent(12));
		
		
		NodePath shortestPath = node0.findShortestPathTo(node6, ShortestPathStrategy.BINARY_DIJKSTRA);
		
		assertNotNull(shortestPath);
		assertTrue(shortestPath.pathFound());
		assertEquals(30, shortestPath.getPathTotalDistance());
		List<Node> path = shortestPath.getPath();
		assertEquals(4,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node1, path.get(1));
		assertEquals(node3, path.get(2));
		assertEquals(node6, path.get(3));
		
	}
	
	public void testBinaryDijkstraUnreachableDestination(){
		Graph aGraph = buildGraph();
		
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		
		node0.addArc(node1, new MockContent(20));
		node0.addArc(node2, new MockContent(10));
		node1.addArc(node3, new MockContent(3));
		node3.addArc(node2, new MockContent(15));
		node2.addArc(node4, new MockContent(17));
		node1.addArc(node5, new MockContent(9));
		
		
		NodePath shortestPath = node0.findShortestPathTo(node6, ShortestPathStrategy.BINARY_DIJKSTRA);
		
		assertNotNull(shortestPath);
		assertFalse(shortestPath.pathFound());
		assertEquals(Integer.MAX_VALUE, shortestPath.getPathTotalDistance());
		List<Node> path = shortestPath.getPath();
		assertNotNull(path);
		assertEquals(0,path.size());
		
	}
	
	public void testBellmanFordPositiveArcs(){
		Graph aGraph = buildGraph();
		
		Node nodePre = aGraph.addNode(new MockContent());
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		
		nodePre.addArc(node0, new MockContent(20));
		node0.addArc(node1, new MockContent(20));
		node0.addArc(node2, new MockContent(10));
		node3.addArc(node6, new MockContent(7));
		node1.addArc(node3, new MockContent(3));
		node3.addArc(node2, new MockContent(15));
		node2.addArc(node4, new MockContent(17));
		node1.addArc(node5, new MockContent(9));
		node5.addArc(node6, new MockContent(12));
		
		
		NodePath shortestPath = node0.findShortestPathTo(node6, ShortestPathStrategy.BELLMAN_FORD);
		
		assertNotNull(shortestPath);
		assertTrue(shortestPath.pathFound());
		assertEquals(30, shortestPath.getPathTotalDistance());
		List<Node> path = shortestPath.getPath();
		assertEquals(4,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node1, path.get(1));
		assertEquals(node3, path.get(2));
		assertEquals(node6, path.get(3));
	}
	
	public void testBellmanFordNegativeArcs(){
		Graph aGraph = buildGraph();
		
		Node nodePre = aGraph.addNode(new MockContent());
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		
		nodePre.addArc(node0, new MockContent(20));
		node0.addArc(node1, new MockContent(20));
		node0.addArc(node2, new MockContent(10));
		node3.addArc(node6, new MockContent(7));
		node1.addArc(node3, new MockContent(3));
		node3.addArc(node2, new MockContent(15));
		node2.addArc(node4, new MockContent(17));
		node1.addArcTo(node5, new MockContent(-9));
		node5.addArc(node6, new MockContent(10));
		
		
		NodePath shortestPath = node0.findShortestPathTo(node6, ShortestPathStrategy.BELLMAN_FORD);
		
		assertNotNull(shortestPath);
		assertTrue(shortestPath.pathFound());
		assertEquals(21, shortestPath.getPathTotalDistance());
		List<Node> path = shortestPath.getPath();
		assertEquals(4,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node1, path.get(1));
		assertEquals(node5, path.get(2));
		assertEquals(node6, path.get(3));
		
	}
	
	
	public void testBellmanFordNegativeWeightedCycle(){
		Graph aGraph = buildGraph();
		
		
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		
		node0.addArc(node1, new MockContent(20));
		node0.addArc(node2, new MockContent(10));
		node3.addArc(node6, new MockContent(7));
		node1.addArc(node3, new MockContent(3));
		node3.addArc(node2, new MockContent(15));
		node2.addArc(node4, new MockContent(17));
		node1.addArcTo(node5, new MockContent(-9));
		node5.addArcTo(node1, new MockContent(8));
		node5.addArc(node6, new MockContent(10));
		
		try {
			node0.findShortestPathTo(node6, ShortestPathStrategy.BELLMAN_FORD);
			fail();
		} catch (NegativeWeightCycleFoundException e) {
			
		}
	}
	
	public void testTopologicalOrder(){
		Graph aGraph = buildGraph();
		
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		Node node7 = aGraph.addNode(new MockContent());
		
		node0.addArcTo(node1, new MockContent(20));
		node0.addArcTo(node2, new MockContent(-10));
		node3.addArcTo(node2, new MockContent(15));
		node3.addArcTo(node6, new MockContent(7));
		node2.addArcTo(node4, new MockContent(3));
		node6.addArcTo(node4, new MockContent(3));
		node1.addArcTo(node5, new MockContent(-9));
		node5.addArcTo(node7, new MockContent(8));
		
		List<Node> topologicalOrderedNodeList = aGraph.getNodesInTopologicalOrder();
		
		assertNotNull(topologicalOrderedNodeList);
		assertEquals(8, topologicalOrderedNodeList.size());
		assertEquals(node0, topologicalOrderedNodeList.get(0));
		assertEquals(node3, topologicalOrderedNodeList.get(1));
		assertEquals(node1, topologicalOrderedNodeList.get(2));
		assertEquals(node2, topologicalOrderedNodeList.get(3));
		assertEquals(node6, topologicalOrderedNodeList.get(4));
		assertEquals(node5, topologicalOrderedNodeList.get(5));
		assertEquals(node4, topologicalOrderedNodeList.get(6));
		assertEquals(node7, topologicalOrderedNodeList.get(7));
	}

	public void testTopologicalOrderForNoDAG(){
		Graph aGraph = buildGraph();
		
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		Node node7 = aGraph.addNode(new MockContent());
		
		node0.addArcTo(node1, new MockContent(20));
		node0.addArcTo(node2, new MockContent(-10));
		node3.addArcTo(node2, new MockContent(15));
		node3.addArcTo(node6, new MockContent(7));
		node2.addArcTo(node4, new MockContent(3));
		node6.addArcTo(node4, new MockContent(3));
		node1.addArcTo(node5, new MockContent(-9));
		node5.addArcTo(node7, new MockContent(8));
		node5.addArcTo(node0, new MockContent(8));
		try {
			aGraph.getNodesInTopologicalOrder();
			fail();
		} catch (NotADirectedAcyclicGraphException e) {
			//Expected
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testDAGShortestPath(){
		Graph aGraph = buildGraph();
		
		Node nodePre = aGraph.addNode(new MockContent());
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		Node node7 = aGraph.addNode(new MockContent());
		
		nodePre.addArcTo(node3, new MockContent(20));
		node0.addArcTo(node1, new MockContent(20));
		node0.addArcTo(node2, new MockContent(-10));
		node3.addArcTo(node2, new MockContent(15));
		node3.addArcTo(node6, new MockContent(7));
		node2.addArcTo(node4, new MockContent(3));
		node6.addArcTo(node4, new MockContent(3));
		node1.addArcTo(node5, new MockContent(-9));
		node5.addArcTo(node7, new MockContent(8));
		
		NodePath nodePath =  node3.findShortestPathTo(node4, ShortestPathStrategy.DAG);
		
		assertNotNull(nodePath);
		assertEquals(10, nodePath.getPathTotalDistance());
		assertTrue(nodePath.pathFound());
		List<Node> path = nodePath.getPath();
		assertEquals(3, path.size());
		assertEquals(node3, path.get(0));
		assertEquals(node6, path.get(1));
		assertEquals(node4, path.get(2));
	}

	public void testDAGShortestPathNotFound(){
		Graph aGraph = buildGraph();
		
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		Node node7 = aGraph.addNode(new MockContent());
		
		node0.addArcTo(node1, new MockContent(20));
		node0.addArcTo(node2, new MockContent(-10));
		node3.addArcTo(node2, new MockContent(15));
		node3.addArcTo(node6, new MockContent(7));
		node2.addArcTo(node4, new MockContent(3));
		node6.addArcTo(node4, new MockContent(3));
		node1.addArcTo(node5, new MockContent(-9));
		node5.addArcTo(node7, new MockContent(8));
		
		NodePath nodePath =  node3.findShortestPathTo(node7, ShortestPathStrategy.DAG);
		
		assertNotNull(nodePath);
		assertFalse(nodePath.pathFound());
	}
	
	public void testBinaryDijkstraAllPaths(){
		Graph aGraph = buildGraph();
		
		Node nodePre = aGraph.addNode(new MockContent());
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		
		nodePre.addArc(node0, new MockContent(10));
		node0.addArc(node1, new MockContent(20));
		node0.addArc(node2, new MockContent(10));
		node3.addArc(node6, new MockContent(7));
		node1.addArc(node3, new MockContent(3));
		node3.addArc(node2, new MockContent(15));
		node2.addArc(node4, new MockContent(17));
		node1.addArc(node5, new MockContent(9));
		node5.addArc(node6, new MockContent(12));
		
		
		Map<Node,NodePath> shortestPaths = node0.findShortestPathToEveryOtherNode(ShortestPathStrategy.BINARY_DIJKSTRA);
		
		assertNotNull(shortestPaths);
		assertEquals(8, shortestPaths.size());
		assertNotNull(shortestPaths.get(node0));
		assertNotNull(shortestPaths.get(node1));
		assertNotNull(shortestPaths.get(node2));
		assertNotNull(shortestPaths.get(node3));
		assertNotNull(shortestPaths.get(node4));
		assertNotNull(shortestPaths.get(node5));
		NodePath shortestPathNode6 = shortestPaths.get(node6);
		assertNotNull(shortestPathNode6);
		assertTrue(shortestPathNode6.pathFound());
		assertEquals(30, shortestPathNode6.getPathTotalDistance());
		List<Node> path = shortestPathNode6.getPath();
		assertEquals(4,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node1, path.get(1));
		assertEquals(node3, path.get(2));
		assertEquals(node6, path.get(3));
		NodePath shortestPathNode1 = shortestPaths.get(node1);
		assertNotNull(shortestPathNode1);
		assertTrue(shortestPathNode1.pathFound());
		assertEquals(20, shortestPathNode1.getPathTotalDistance());
		path = shortestPathNode1.getPath();
		assertEquals(2,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node1, path.get(1));
		NodePath shortestPathNode2 = shortestPaths.get(node2);
		assertNotNull(shortestPathNode2);
		assertTrue(shortestPathNode2.pathFound());
		assertEquals(10, shortestPathNode2.getPathTotalDistance());
		path = shortestPathNode2.getPath();
		assertEquals(2,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node2, path.get(1));
	}
	
	public void testBellmanFordNegativeArcsAllPaths(){
		Graph aGraph = buildGraph();
		
		Node nodePre = aGraph.addNode(new MockContent());
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		
		nodePre.addArc(node0, new MockContent(10));
		node0.addArc(node1, new MockContent(20));
		node0.addArcTo(node2, new MockContent(-1));
		node3.addArc(node6, new MockContent(7));
		node1.addArc(node3, new MockContent(3));
		node3.addArcTo(node2, new MockContent(15));
		node2.addArc(node4, new MockContent(17));
		node1.addArc(node5, new MockContent(9));
		node5.addArc(node6, new MockContent(12));
		
		
		Map<Node,NodePath> shortestPaths = node0.findShortestPathToEveryOtherNode(ShortestPathStrategy.BELLMAN_FORD);
		
		assertNotNull(shortestPaths);
		assertEquals(8, shortestPaths.size());
		assertNotNull(shortestPaths.get(node0));
		assertNotNull(shortestPaths.get(node1));
		assertNotNull(shortestPaths.get(node2));
		assertNotNull(shortestPaths.get(node3));
		assertNotNull(shortestPaths.get(node4));
		assertNotNull(shortestPaths.get(node5));
		NodePath shortestPathNode6 = shortestPaths.get(node6);
		assertNotNull(shortestPathNode6);
		assertTrue(shortestPathNode6.pathFound());
		assertEquals(30, shortestPathNode6.getPathTotalDistance());
		List<Node> path = shortestPathNode6.getPath();
		assertEquals(4,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node1, path.get(1));
		assertEquals(node3, path.get(2));
		assertEquals(node6, path.get(3));
		NodePath shortestPathNode1 = shortestPaths.get(node1);
		assertNotNull(shortestPathNode1);
		assertTrue(shortestPathNode1.pathFound());
		assertEquals(20, shortestPathNode1.getPathTotalDistance());
		path = shortestPathNode1.getPath();
		assertEquals(2,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node1, path.get(1));
		NodePath shortestPathNode2 = shortestPaths.get(node2);
		assertNotNull(shortestPathNode2);
		assertTrue(shortestPathNode2.pathFound());
		assertEquals(-1, shortestPathNode2.getPathTotalDistance());
		path = shortestPathNode2.getPath();
		assertEquals(2,path.size());
		assertEquals(node0, path.get(0));
		assertEquals(node2, path.get(1));
	}
	
	public void testDAGAllShortestPaths(){
		Graph aGraph = buildGraph();
		
		Node nodePre = aGraph.addNode(new MockContent());
		Node node0 = aGraph.addNode(new MockContent());
		Node node1 = aGraph.addNode(new MockContent());
		Node node2 = aGraph.addNode(new MockContent());
		Node node3 = aGraph.addNode(new MockContent());
		Node node4 = aGraph.addNode(new MockContent());
		Node node5 = aGraph.addNode(new MockContent());
		Node node6 = aGraph.addNode(new MockContent());
		Node node7 = aGraph.addNode(new MockContent());
		
		nodePre.addArcTo(node3, new MockContent(20));
		node0.addArcTo(node1, new MockContent(20));
		node0.addArcTo(node2, new MockContent(-10));
		node3.addArcTo(node2, new MockContent(15));
		node3.addArcTo(node6, new MockContent(7));
		node2.addArcTo(node4, new MockContent(3));
		node6.addArcTo(node4, new MockContent(3));
		node1.addArcTo(node5, new MockContent(-9));
		node5.addArcTo(node7, new MockContent(8));
		
		Map<Node,NodePath> nodePaths =  node3.findShortestPathToEveryOtherNode(ShortestPathStrategy.DAG);
		
		assertNotNull(nodePaths);
		assertEquals(9, nodePaths.size());
		
		NodePath nodePath4 = nodePaths.get(node4);
		assertEquals(10, nodePath4 .getPathTotalDistance());
		assertTrue(nodePath4.pathFound());
		List<Node> path = nodePath4.getPath();
		assertEquals(3, path.size());
		assertEquals(node3, path.get(0));
		assertEquals(node6, path.get(1));
		assertEquals(node4, path.get(2));
		NodePath nodePath0 = nodePaths.get(node0);
		assertNotNull(nodePath0);
		assertFalse(nodePath0.pathFound());
	}
	
	private class MockContent implements Measurable<MockContent>{
		
		int distance;
		
		public MockContent(int i) {
			this.distance = i;
		}

		public MockContent() {
			this(0);
		}
		
		@Override
		public int measure() {
			return distance;
		}
	}
	
	private static class TraversalCountContent implements Measurable<TraversalCountContent>{
		private int traversalCount = 0;
		private static int totalTraversalCount = 0;

		@Override
		public int measure() {
			return 0;
		}
	}
}

