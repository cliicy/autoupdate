package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import org.apache.log4j.Logger;

public class LogUtility
{
	//////////////////////////////////////////////////////////////////////////
	
	public class LogTypes
	{
		public static final int Error	= 1;
		public static final int Warning	= 2;
		public static final int Info	= 3;
		public static final int Trace	= 4;
		public static final int Debug	= 5;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private Logger logger;
	
	//////////////////////////////////////////////////////////////////////////
	
	public LogUtility( Logger logger )
	{
		this.logger = logger;
	}

	//////////////////////////////////////////////////////////////////////////
	
	public void writeLog( int logType, String format, Object... args )
	{
		String message = String.format( format, args );
		
		switch (logType)
		{
		case LogTypes.Debug:
			logger.debug( message );
			break;
		
		case LogTypes.Trace:
			logger.trace( message );
			break;

		case LogTypes.Info:
			logger.info( message );
			break;

		case LogTypes.Warning:
			logger.warn( message );
			break;

		case LogTypes.Error:
			logger.error( message );
			break;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	public void writeLog( int logType,
		Throwable throwable, String format, Object... args )
	{
		if(format==null)
			format="";
		String message = String.format( format, args );
		
		switch (logType)
		{
		case LogTypes.Debug:
			logger.debug( message, throwable );
			break;
			
		case LogTypes.Trace:
			logger.trace( message, throwable );
			break;

		case LogTypes.Info:
			logger.info( message, throwable );
			break;

		case LogTypes.Warning:
			logger.warn( message, throwable );
			break;

		case LogTypes.Error:
			logger.error( message, throwable );
			break;
		}
	}
}
