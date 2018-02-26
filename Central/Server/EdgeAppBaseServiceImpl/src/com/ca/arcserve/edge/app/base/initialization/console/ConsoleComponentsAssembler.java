package com.ca.arcserve.edge.app.base.initialization.console;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.initialization.IAppInitializer;
import com.ca.arcserve.edge.app.base.initialization.common.CrossProjectInitializer;

public class ConsoleComponentsAssembler extends CrossProjectInitializer
{
	private static Logger logger = Logger.getLogger( ConsoleComponentsAssembler.class );
	private static IAppInitializer realInitializer = null;
	
	public ConsoleComponentsAssembler()
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
