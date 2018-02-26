package com.ca.arcserve.edge.app.base.initialization.console;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.common.udpapplication.ConsoleApplication;
import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication;
import com.ca.arcserve.edge.app.base.initialization.IAppConfigurations;
import com.ca.arcserve.edge.app.base.initialization.IAppInitializer;

public class ConsoleAppConfigurations implements IAppConfigurations
{
	private List<IAppInitializer> initList = null;

	@Override
	public String getLogsConfigFileName()
	{
		return "log4j-ARCAPP.properties";
	}

	@Override
	public List<IAppInitializer> getInitializationList()
	{
		if (initList == null)
		{
			initList = new ArrayList<>();
			initList.add( new ConsoleComponentsAssembler() );
			initList.add( new ConsoleEnvironmentInitializer() );
			initList.add( new ConsoleDAOInitializer() );
			initList.add( new MessageServiceBrokerInitializer() );
			initList.add( new ConsoleMessageServiceInitializer() );
		}
		
		return initList;
	}

	@Override
	public UDPApplication createApplicationObject()
	{
		return new ConsoleApplication();
	}

}
