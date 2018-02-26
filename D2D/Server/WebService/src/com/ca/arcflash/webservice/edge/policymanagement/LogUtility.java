package com.ca.arcflash.webservice.edge.policymanagement;

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
	
	public Logger getLogger()
	{
		return logger;
	}

	public void setLogger( Logger logger )
	{
		this.logger = logger;
	}

	//////////////////////////////////////////////////////////////////////////
	
	public void writeLog( int logType, String format, Object... args )
	{
		try
		{
			if (this.logger == null)
				return;
			
			String message = String.format( format, args );
			
			switch (logType)
			{
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
				
			case LogTypes.Debug:
				logger.debug( message );
				break;
			}
		}
		catch (Exception e)
		{
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	public void writeLog( int logType,
		Throwable throwable, String format, Object... args )
	{
		try
		{
			if (this.logger == null)
				return;
			
			String message = String.format( format, args );
			
			switch (logType)
			{
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
				
			case LogTypes.Debug:
				logger.debug( message, throwable );
				break;
			}
		}
		catch (Exception e)
		{
		}
	}

}
