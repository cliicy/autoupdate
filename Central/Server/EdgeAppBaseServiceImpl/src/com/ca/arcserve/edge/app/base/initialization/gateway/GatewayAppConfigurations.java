package com.ca.arcserve.edge.app.base.initialization.gateway;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.common.udpapplication.GatewayApplication;
import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication;
import com.ca.arcserve.edge.app.base.initialization.IAppConfigurations;
import com.ca.arcserve.edge.app.base.initialization.IAppInitializer;

public class GatewayAppConfigurations implements IAppConfigurations
{
	private List<IAppInitializer> initList = null;

	@Override
	public String getLogsConfigFileName()
	{
		return "log4j-ARCAPP-Gateway.properties";
	}

	@Override
	public List<IAppInitializer> getInitializationList()
	{
		if (initList == null)
		{
			initList = new ArrayList<>();
			initList.add( new GatewayComponentsAssembler() );
			initList.add( new GatewayEnvironmentInitializer() );
			initList.add( new GatewayMessageServiceInitializer() );
		}
		
		return initList;
	}

	@Override
	public UDPApplication createApplicationObject()
	{
		return new GatewayApplication();
	}

}
