package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;

public class NodeInfoList implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private List<NodeGroup> groupList;
	private List<NodeGroup> esxGroupList;
	private List<NodeInfo> nodeList;
	
	public List<NodeGroup> getGroupList()
	{
		return groupList;
	}
	
	public void setGroupList( List<NodeGroup> groupList )
	{
		this.groupList = groupList;
	}
	
	public List<NodeGroup> getEsxGroupList()
	{
		return esxGroupList;
	}
	
	public void setEsxGroupList( List<NodeGroup> esxGroupList )
	{
		this.esxGroupList = esxGroupList;
	}
	
	public List<NodeInfo> getNodeList()
	{
		return nodeList;
	}
	
	public void setNodeList( List<NodeInfo> nodeList )
	{
		this.nodeList = nodeList;
	}
}
