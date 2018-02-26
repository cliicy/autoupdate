package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(value = {CommonNodeFilter.class, BitmapFilter.class})
public abstract class NodeFilter implements Serializable {
	
	private static final long serialVersionUID = -565365636148125477L;

	public static enum NodeFilterType {
		Common, JobStatus, PlanProtectionType, NodeStatus, RemoteDeployStatus, NotNullField, LastBackupStatus,GateWay
	}
	
	private NodeFilterType type;
	
	public NodeFilter() {
		this(NodeFilterType.Common);
	}
	
	public NodeFilter(NodeFilterType type) {
		this.type = type;
	}

	public NodeFilterType getType() {
		return type;
	}

	public void setType(NodeFilterType type) {
		this.type = type;
	}
	
	public abstract boolean isEnabled();

}
