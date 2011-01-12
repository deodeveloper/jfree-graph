package org.nixus.core.structure.nodes;

import java.util.List;

public class NodePath {
	private List<Node> path;
	private long pathTotalDistance;
	
	public NodePath(List<Node> path, long pathTotalDistance) {
		super();
		this.path = path;
		this.pathTotalDistance = pathTotalDistance;
	}

	public List<Node> getPath() {
		return path;
	}

	public long getPathTotalDistance() {
		return pathTotalDistance;
	}
	
	/**
	 * Convenience method to know whether a path was 
	 * found or not to destination 
	 * */
	public boolean pathFound(){
		return pathTotalDistance != Integer.MAX_VALUE;
	}
}
