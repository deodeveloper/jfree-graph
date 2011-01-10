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
}
