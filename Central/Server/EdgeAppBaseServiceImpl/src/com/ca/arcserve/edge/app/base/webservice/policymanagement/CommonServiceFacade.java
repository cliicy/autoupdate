package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.common.EdgeCommonServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;

public class CommonServiceFacade
{
	private static CommonServiceFacade instance = null;
	private EdgeCommonServiceImpl commonServiceImpl = null;
	
	//////////////////////////////////////////////////////////////////////////
	
	private CommonServiceFacade()
	{
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static synchronized CommonServiceFacade getInstance()
	{
		if (instance == null)
			instance = new CommonServiceFacade();
		
		return instance;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private synchronized EdgeCommonServiceImpl getCommonServiceImpl()
	{
		if (this.commonServiceImpl == null)
			this.commonServiceImpl = new EdgeCommonServiceImpl();
		
		return this.commonServiceImpl;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public EdgeVersionInfo getVersionInformation() throws EdgeServiceFault
	{
		return this.getCommonServiceImpl().getVersionInformation();
	}
}
