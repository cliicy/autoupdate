package com.ca.arcserve.edge.app.base.initialization.common;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.initialization.IAppInitializer;

public abstract class BaseAppInitializer implements IAppInitializer
{
	private static Logger logger = Logger.getLogger( BaseAppInitializer.class );

	@Override
	public void initialize()
	{
		String logPrefix = getLogPrefix();
		logger.info( logPrefix + "Enter" );
		
		doInitialization();
		
		logger.info( logPrefix + "Leave" );
	}

	@Override
	public void uninitialize()
	{
		String logPrefix = getLogPrefix();
		logger.info( logPrefix + "Enter" );
		
		doUninitialization();
		
		logger.info( logPrefix + "Leave" );
	}
	
	private String getLogPrefix()
	{
		return this.getClass().getSimpleName() + "." +
			Thread.currentThread().getStackTrace()[2].getMethodName() + "(): ";
	}
	
	protected abstract void doInitialization();
	protected abstract void doUninitialization();

}
