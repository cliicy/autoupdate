package com.ca.arcserve.edge.app.base.initialization.common;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.initialization.IAppInitializer;

public abstract class CrossProjectInitializer implements IAppInitializer
{
	private Logger logger;
	
	public CrossProjectInitializer( Logger logger )
	{
		this.logger = logger;
	}
	
	private void outputNoRealInitializerLogs( String methodName, Throwable t )
	{
		String logPrefix = this.getClass().getSimpleName() + "." + methodName + "(): ";
		logger.error( logPrefix + "realInitializer is not set yet.", t );
	}

	@Override
	public void initialize()
	{
		IAppInitializer realInitializer = this.getRealInitializer();
		if (realInitializer == null)
		{
			outputNoRealInitializerLogs( "initialize", new Exception() );
			return;
		}
		
		realInitializer.initialize();
	}

	@Override
	public void uninitialize()
	{
		IAppInitializer realInitializer = this.getRealInitializer();
		if (realInitializer == null)
		{
			outputNoRealInitializerLogs( "uninitialize", new Exception() );
			return;
		}
		
		realInitializer.uninitialize();
	}

	protected abstract IAppInitializer getRealInitializer();
}
