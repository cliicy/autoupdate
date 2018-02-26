package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;

public class NodeFilterGroup extends NodeGroup {

	private static final long serialVersionUID = -1327818498935196124L;
	
	private EdgeNodeFilter nodeFilter;
	
	public NodeFilterGroup() {
		this(new EdgeNodeFilter());
	}
	
	public NodeFilterGroup(EdgeNodeFilter nodeFilter) {
		setType(NodeGroup.NodeFilterGroupType);
		setNodeFilter(nodeFilter);
	}

	public EdgeNodeFilter getNodeFilter() {
		return nodeFilter;
	}

	public void setNodeFilter(EdgeNodeFilter nodeFilter) {
		this.nodeFilter = nodeFilter;
	}

}
