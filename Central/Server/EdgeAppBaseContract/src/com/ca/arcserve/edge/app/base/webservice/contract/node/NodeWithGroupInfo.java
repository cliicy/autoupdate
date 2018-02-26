package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.List;

public class NodeWithGroupInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Node nodeInfo;
	private List<Integer> groupIds;
	
	public Node getNodeInfo()
	{
		return nodeInfo;
	}
	
	public void setNodeInfo( Node nodeInfo )
	{
		this.nodeInfo = nodeInfo;
	}
	
	public List<Integer> getGroupIds()
	{
		return groupIds;
	}
	
	public void setGroupIds( List<Integer> groupIds )
	{
		this.groupIds = groupIds;
	}
}
