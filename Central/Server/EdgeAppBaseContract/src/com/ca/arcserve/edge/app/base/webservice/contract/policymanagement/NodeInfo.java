package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;

public class NodeInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int nodeId;
	private String nodeName;
	private List<Integer> groupIdList;
	private List<Integer> esxGroupIdList;
	private int policyId;
	private String policyName;
	private int deployStatus;
	private int deployReason;
	private int deployFlags;
	private int tryCount;
	private String proxyName;
	private Node detailedNodeInfo;
	private String warning;
	private String error;
	
	
	public String getWarning()
	{
		return warning;
	}
	
	public void setWarning(String warning)
	{
		this.warning = warning;
	}
	
	public String getError()
	{
		return error;
	}
	
	public void setError(String error)
	{
		this.error = error;
	}
	
	public int getNodeId()
	{
		return nodeId;
	}
	
	public void setNodeId( int nodeId )
	{
		this.nodeId = nodeId;
	}
	
	public String getNodeName()
	{
		return nodeName;
	}
	
	public void setNodeName( String nodeName )
	{
		this.nodeName = nodeName;
	}
	
	public List<Integer> getGroupIdList()
	{
		return groupIdList;
	}

	public void setGroupIdList( List<Integer> groupIdList )
	{
		this.groupIdList = groupIdList;
	}

	public List<Integer> getEsxGroupIdList()
	{
		return esxGroupIdList;
	}

	public void setEsxGroupIdList( List<Integer> esxGroupIdList )
	{
		this.esxGroupIdList = esxGroupIdList;
	}
	
	public int getPolicyId()
	{
		return policyId;
	}
	
	public void setPolicyId( int policyId )
	{
		this.policyId = policyId;
	}
	
	public String getPolicyName()
	{
		return policyName;
	}
	
	public void setPolicyName( String policyName )
	{
		this.policyName = policyName;
	}
	
	public int getDeployStatus()
	{
		return deployStatus;
	}
	
	public void setDeployStatus( int deployStatus )
	{
		this.deployStatus = deployStatus;
	}
	
	public int getDeployReason()
	{
		return deployReason;
	}

	public void setDeployReason( int deployReason )
	{
		this.deployReason = deployReason;
	}

	public int getDeployFlags()
	{
		return deployFlags;
	}

	public void setDeployFlags( int deployFlag )
	{
		this.deployFlags = deployFlag;
	}

	public int getTryCount()
	{
		return tryCount;
	}
	
	public void setTryCount( int tryCount )
	{
		this.tryCount = tryCount;
	}
	
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public String getProxyName()
	{
		return proxyName;
	}

	public void setProxyName( String proxyName )
	{
		this.proxyName = proxyName;
	}

	public Node getDetailedNodeInfo()
	{
		return detailedNodeInfo;
	}

	public void setDetailedNodeInfo( Node nodeDetailedData )
	{
		this.detailedNodeInfo = nodeDetailedData;
	}
}
