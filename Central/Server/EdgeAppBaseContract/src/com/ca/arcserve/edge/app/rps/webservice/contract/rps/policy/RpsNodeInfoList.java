package com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy;

import java.io.Serializable;
import java.util.List;

import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsGroup;

public class RpsNodeInfoList implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private List<RpsGroup> groupList;
	private List<RpsGroup> esxGroupList;
	private List<RpsNodeInfo> nodeList;
	
	public List<RpsGroup> getGroupList()
	{
		return groupList;
	}
	
	public void setGroupList( List<RpsGroup> groupList )
	{
		this.groupList = groupList;
	}
	
	public List<RpsGroup> getEsxGroupList()
	{
		return esxGroupList;
	}
	
	public void setEsxGroupList( List<RpsGroup> esxGroupList )
	{
		this.esxGroupList = esxGroupList;
	}
	
	public List<RpsNodeInfo> getNodeList()
	{
		return nodeList;
	}
	
	public void setNodeList( List<RpsNodeInfo> nodeList )
	{
		this.nodeList = nodeList;
	}
}
