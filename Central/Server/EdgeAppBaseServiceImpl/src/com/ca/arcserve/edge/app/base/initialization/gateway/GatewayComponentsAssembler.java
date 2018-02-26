package com.ca.arcserve.edge.app.base.initialization.gateway;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.initialization.IAppInitializer;
import com.ca.arcserve.edge.app.base.initialization.common.CrossProjectInitializer;

public class GatewayComponentsAssembler extends CrossProjectInitializer
{
	private static Logger logger = Logger.getLogger( GatewayComponentsAssembler.class );
	private static IAppInitializer realInitializer = null;
	
	public GatewayComponentsAssembler()
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
