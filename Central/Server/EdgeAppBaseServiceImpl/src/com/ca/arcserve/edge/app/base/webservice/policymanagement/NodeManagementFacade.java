package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public class NodeManagementFacade
{
	private static NodeManagementFacade instance = null;
	private NodeServiceImpl nodeServiceImpl = null;
	
	//////////////////////////////////////////////////////////////////////////
	
	private NodeManagementFacade()
	{
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static synchronized NodeManagementFacade getInstance()
	{
		if (instance == null)
			instance = new NodeManagementFacade();
		
		return instance;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private synchronized NodeServiceImpl getNodeServiceImpl()
	{
		if (this.nodeServiceImpl == null)
			this.nodeServiceImpl = new NodeServiceImpl();
		
		return this.nodeServiceImpl;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public Node convertDaoNode2ContractNode( EdgeHost edgeHost )
	{
		return NodeServiceImpl.convertDaoNode2ContractNode( edgeHost );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public List<NodeGroup> getNodeGroups() throws EdgeServiceFault
	{
		return getNodeServiceImpl().getNodeGroups();
	}
}
