package com.ca.arcserve.edge.app.base.webservice.gateway;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeGatewayDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class GatewayContactTimeUpdater
{
	private static Logger logger = Logger.getLogger( GatewayContactTimeUpdater.class );
	private static GatewayContactTimeUpdater instance = new GatewayContactTimeUpdater();
	
	private class UpdateRequest
	{
		private GatewayId gatewayId;
		private Date contactTime;
		
		public UpdateRequest( GatewayId gatewayId, Date contactTime )
		{
			this.gatewayId = gatewayId;
			this.contactTime = contactTime;
		}
		
		public GatewayId getGatewayId()
		{
			return gatewayId;
		}
		
		public Date getContactTime()
		{
			return contactTime;
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "UpdateRequest { " );
			sb.append( "gatewayId = " + gatewayId );
			sb.append( ", contactTime = " + contactTime );
			sb.append( " }" );
			return sb.toString();
		}
	}
	
	private BlockingQueue<UpdateRequest> requestQueue = new LinkedBlockingQueue<>();
	private Thread reqProcThread = null;
	
	private GatewayContactTimeUpdater()
	{
	}
	
	public static GatewayContactTimeUpdater getInstance()
	{
		return instance;
	}
	
	public void start()
	{
		String logPrefix = this.getClass().getSimpleName() + ".start(): ";
		logger.info( logPrefix + "Start the gateway contact time updater." );
		
		if (reqProcThread != null)
		{
			logger.info( logPrefix + "The processor thread is already running, return directly." );
			return;
		}
		
		reqProcThread = new Thread( new UpdateRunnable( this.requestQueue ),
			"GatewayContactTimeUpdater.RequestProcessorThread" );
		reqProcThread.setDaemon( true );
		reqProcThread.start();
		
		logger.info( logPrefix + "The gateway contact time updater was started." );
	}
	
	public void stop()
	{
		String logPrefix = this.getClass().getSimpleName() + ".stop(): ";
		logger.info( logPrefix + "Stop the gateway contact time updater." );
		
		// The thread is a deamon thread, so don't need to stop the
		// thread explicitly. But make sure to invoke this method when
		// the application is about to be destroyed in order to guarantee
		// we have a chance to do something if needed.
	}
	
	public void updateGateway( GatewayId gatewayId )
	{
		UpdateRequest request = new UpdateRequest( gatewayId, new Date() );

		try
		{
			this.requestQueue.put( request );
		}
		catch (InterruptedException e)
		{
			String logPrefix = this.getClass().getSimpleName() + ".updateGateway(): ";
			logger.error( logPrefix + "Error putting request to request queue. Request: " + request, e );
		}
	}
	
	private class UpdateRunnable implements Runnable
	{
		private IEdgeGatewayDao gatewayDao = DaoFactory.getDao( IEdgeGatewayDao.class );
		private BlockingQueue<UpdateRequest> requestQueue = null;
		
		public UpdateRunnable( BlockingQueue<UpdateRequest> requestQueue )
		{
			this.requestQueue = requestQueue;
		}

		@Override
		public void run()
		{
			String logPrefix = this.getClass().getSimpleName() + ".run(): ";
			
			UpdateRequest request = null;
			
			for (;;)
			{
				try
				{
					request = requestQueue.take();
				}
				catch (InterruptedException e)
				{
					break;
				}
				
				try
				{
					this.gatewayDao.updateGatewayHostLastContactTime(
						request.getGatewayId().getRecordId(), request.getContactTime() );
				}
				catch (Exception e)
				{
					logger.error( logPrefix + "Error processing request. Request: " + request, e );
				}
			}
		}
	}
}
