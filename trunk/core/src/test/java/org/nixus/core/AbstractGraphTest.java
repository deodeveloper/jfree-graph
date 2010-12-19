package org.nixus.core;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.nixus.core.structure.Arc;
import org.nixus.core.structure.Graph;
import org.nixus.core.structure.auxiliary.Measurable;
import org.nixus.core.structure.auxiliary.NodeTransformer;
import org.nixus.core.structure.nodes.Node;

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
		
		List<Node> dftResult = aGraph.depthFirstTraversal(node0, new NodeTransformer<Node>() {
			@Override
			public void transform(Node node) {
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
		
		List<Node> dftResult = aGraph.breathFirstTraversal(node0, new NodeTransformer<Node>() {
			@Override
			public void transform(Node node) {
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

	private class MockContent implements Measurable<MockContent, Integer>{
		@Override
		public Integer measure() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private static class TraversalCountContent implements Measurable<TraversalCountContent, Integer>{
		private int traversalCount = 0;
		private static int totalTraversalCount = 0;

		@Override
		public Integer measure() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
