package com.ca.arcserve.edge.app.base.initialization.console;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.initialization.IAppInitializer;
import com.ca.arcserve.edge.app.base.initialization.common.CrossProjectInitializer;

public class MessageServiceBrokerInitializer extends CrossProjectInitializer
{
	private static Logger logger = Logger.getLogger( MessageServiceBrokerInitializer.class );
	private static IAppInitializer realInitializer = null;
	
	public MessageServiceBrokerInitializer()
	{
		super( logger );
	}
	
	public static void setRealInitializer( IAppInitializer initializer )
	{
		realInitializer = initializer;
	}

	@Override
	protected IAppInitializer getRealInitializer()
	{
		return realInitializer;
	}
	
}
