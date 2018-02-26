package com.ca.arcflash.webservice.edge.datasync;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.foredge.exception.D2DSyncErrorCode;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;

public class EdgeDataSynchronization {
	private static final Logger logger = Logger.getLogger(EdgeDataSynchronization.class);
	NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
	
	private static List<IDataSynchronization> syncers = new ArrayList<IDataSynchronization>();
	
	private volatile static boolean SyncThreadStarted = false;
	private volatile static boolean IsStop  = false;
	private volatile static boolean IsSyncData  = false;
	
	private static Thread theSyncThread=null;
	
	static {
		syncers.add(new BackupInfoSyncer());
		syncers.add(new ActiveLogSyncer());
		syncers.add(new VSPhereSyncer());
		syncers.add(new ArchiveSyncer());
		syncers.add(new VCMSyncer());	
	}
	
	private static synchronized boolean getSyncThreadLock(){
		if(SyncThreadStarted)	
			return false;
		
		SyncThreadStarted = true;
		return true;
	}
	
	private static synchronized void releaseSyncThreadLock(){
		SyncThreadStarted = false;
	}
	
	public static void stopSyncThread() {
   		long timeout = 30*60*1000; // 30 minutes
   		try {
	   		if(theSyncThread == null || !theSyncThread.isAlive()) {
	   			logger.info("There is no active Edge sync thread to stop.");
	   		}
	   		else {
	   			logger.info("Stop Edge sync thread " + theSyncThread.getId());
		   		SetStopFlag();
		   		theSyncThread.join(timeout);
		   		while(theSyncThread.isAlive()) {
		   			logger.info("Cannot stop thread " + theSyncThread.getId() + ". interrupt it now...");
		   			theSyncThread.interrupt();
		   			if(!theSyncThread.isAlive())
		   				break;
		   			Thread.sleep(10000);
		   		}
	   		}
   		}catch(Throwable t) {
   			logger.error("Stopping sync thread got exception: " + t.toString());
   		}
	}
	
	public int doFullSynchronization() {
		// [shuzh02 - remove d2d sync] : remove d2d data syncrhonization since
		// CPM don't use this function any more
		/*synchronized(FullSyncThread.theFullSynchronizedObj) {
			if(FullSyncThread.theFullSyncThread != null) {
				logger.error("There is another full sync thread running.");
				return D2DSyncErrorCode.D2D_SYNC_ANOTHER_FULL_SYNC_THREAD_RUNNING;
			}
			else {
				logger.info("Start full sync thread...");
				try {
					FullSyncThread.theFullSyncThread = new Thread(new FullSyncThread());
				    FullSyncThread.theFullSyncThread.start();
					logger.info("Full sync thread("+FullSyncThread.theFullSyncThread.getId()+") started");
				}catch(Throwable t) {
					logger.error("Failed to start Full sync thread due to exception:");
					logger.error(t.toString());
					return D2DSyncErrorCode.D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE;
				}
			}
		}*/
		logger.info("full syncrhonization have already been removed");
		return D2DSyncErrorCode.D2D_SYNC_SUCCEED;
	}
	
	private static class FullSyncThread implements Runnable{
		public static Object theFullSynchronizedObj = new Object();
		public static Thread theFullSyncThread = null;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
	   		logger.info("Start D2D full synchronization to Edge...");
	   		
	   		boolean syncResult=true;
	   		
	   		try {
		   		stopSyncThread();
				
		   		int result = BaseDataSyncer.startSync(true);
				if(result != 0) {
					logger.error("start sync failed (" + result + ")");
					//if(result == D2DSyncErrorCode.D2D_SYNC_CONNECT_TO_EDGE_FAILURE)
						////return result;
					//else
					//	return D2DSyncErrorCode.D2D_SYNC_UNKNOWN_ERROR;
				}
				else {
					Iterator<IDataSynchronization> iter = syncers.iterator();
					while(iter.hasNext()) {
						IDataSynchronization syncer = iter.next();
						if(false == syncer.doSync(true))
							syncResult = false;
						else
							syncer.markFullSyncFinished();
					}
					
					BaseDataSyncer.endSync(true, syncResult);
				}
	   		}catch(Throwable t) {
	   			logger.error("Got exception during D2D full synchronization:");
	   			logger.error(t.toString());
				Throwable cause = t.getCause();
				if(t instanceof SOAPFaultException) {// protocol problem due to D2D version doesn't match Edge version
					//TODO
					BaseDataSyncer.writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_EDGE_NOT_MATCH, 
							BaseDataSyncer.getRegisteredEdgeHostName());
					////return D2DSyncErrorCode.D2D_SYNC_COMPATIBILITY_FAILURE;
				}
				else
				if(t instanceof WebServiceException) {// communication problem
					BaseDataSyncer.writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_CONNECT_TO_EDGE_FAILURE, 
							BaseDataSyncer.getRegisteredEdgeHostName());
					////return D2DSyncErrorCode.D2D_SYNC_CONNECT_TO_EDGE_FAILURE;
				}	
				else
				if((t.getCause() != null ) && (t.getCause() instanceof ConnectException)) {// communication problem
					BaseDataSyncer.writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_CONNECT_TO_EDGE_FAILURE, 
							BaseDataSyncer.getRegisteredEdgeHostName());
					////return D2DSyncErrorCode.D2D_SYNC_CONNECT_TO_EDGE_FAILURE;
				}	
				else {
					BaseDataSyncer.writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_UNKNOWN_ERROR, 
							BaseDataSyncer.getRegisteredEdgeHostName());
					////return D2DSyncErrorCode.D2D_SYNC_UNKNOWN_ERROR;
				}
	   		}finally {
	   			try {
	   				startSyncThread();
	   				synchronized(FullSyncThread.theFullSynchronizedObj) {
	   					FullSyncThread.theFullSyncThread = null;
	   				}
	   			}catch (Throwable t) {
	   				logger.error(t.toString());
	   			}
	   		}
	   		
	   		////return (syncResult==true)?0:D2DSyncErrorCode.D2D_SYNC_UNKNOWN_ERROR;
	   	}
	}
	
	private static synchronized boolean IsStopRunning() {
		return IsStop;
	}
	
	private static synchronized void SetStopFlag() {
		IsStop = true;
	}
	
	private static synchronized void unsetStopFlag() {
		IsStop = false;
	}
	
	private static synchronized boolean IsSyncDataSet(){
		return IsSyncData;
	}
	
	private static synchronized void UnsetSyncDataFlag(){
		IsSyncData = false;
	}
	
	public static synchronized void SetSyncDataFlag(){
		logger.debug("D2DSync(Incr): sync data flag is set!");
		IsSyncData = true;
	}
	
	public static void startSyncThread() {
		// [shuzh02 - remove d2d sync] : remove d2d data syncrhonization since
		// CPM don't use this function any more
		logger.info("startSyncThread have already been removed");
		/*logger.info("Start Edge sync thread ...");
		try {
			if(getSyncThreadLock() == false){
				logger.error("Cannot start Edge sync thread due to another thread is running!");
				return;
			}
			theSyncThread = new Thread(new SyncThread());
			theSyncThread.setDaemon(true);
			theSyncThread.start();
			logger.info("Edge sync thread("+theSyncThread.getId()+") started");
		}catch(Throwable t) {
			logger.error("Failed to start Edge sync thread due to exception:");
			logger.error(t.toString());
		}*/
	}
	
	private static class SyncThread implements Runnable{
		public SyncThread(){
			
		}
		
		@Override
		public void run() {
			try {
				doRun();
			}catch(Throwable t) {
				logger.error("D2D sync thread exit due to unexpected exception: ");
				logger.error(t.toString());
			}finally {
				releaseSyncThreadLock();
			}
		}
		
		private void doRun() throws InterruptedException {
			boolean result = true;
			
			D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
			String wsdl = edgeRegInfo.GetEdgeWSDL();
			if(wsdl == null)
			{
				logger.info("The D2D node is not managed by any Edge server. Edge sync thread Exit!");
				return;
			}
			
			logger.info("Edge sync thread is running now!");
			
			while(true){
				if(IsStopRunning()){
					logger.info("Edge sync thread stopped");
					unsetStopFlag();
					break;
				}
				
				try {
					if(IsSyncDataSet()) {
						UnsetSyncDataFlag();
						logger.info("Begin sync D2D data to Edge...");
						
						int stat = BaseDataSyncer.startSync(false);
						if(stat == D2DSyncErrorCode.D2D_SYNC__NOT_MANAGED){
							logger.info("start sync: not managed by Edge already! Stop Sync thread...");
							break;
						}
						else if(stat != D2DSyncErrorCode.D2D_SYNC_SUCCEED) {
							logger.error("Failed to start sync to Edge");
						}
						else {
							result = true;
							Iterator<IDataSynchronization> iter = syncers.iterator();
							while(iter.hasNext()) {
								IDataSynchronization syncer = iter.next();
								boolean cleanFlag = true;
								if(syncer.isFullSyncFinished())
									cleanFlag = false;
								
								if(cleanFlag == true)
									syncer.markFullSyncFinished();
					
								if(false == syncer.doSync(cleanFlag)){
									result = false;
									if(cleanFlag == true)
										syncer.cleanFullSyncFinished();
								}
							}
							BaseDataSyncer.endSync(false, result);
						}
					}
				} catch (Throwable t) {
		   			logger.error("Got exception during D2D synchronization:");
		   			logger.error(t.toString());
					Throwable cause = t.getCause();
					if(t instanceof SOAPFaultException) {// protocol problem due to D2D version doesn't match Edge version
						//TODO
						BaseDataSyncer.writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_EDGE_NOT_MATCH, 
								BaseDataSyncer.getRegisteredEdgeHostName());
					}
					else
					if(t instanceof WebServiceException) {// communication problem
						BaseDataSyncer.writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_CONNECT_TO_EDGE_FAILURE, 
								BaseDataSyncer.getRegisteredEdgeHostName());
					}	
					else
					if((t.getCause() != null ) && (t.getCause() instanceof ConnectException)) {// communication problem
						BaseDataSyncer.writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_CONNECT_TO_EDGE_FAILURE, 
								BaseDataSyncer.getRegisteredEdgeHostName());
					}	
					else {
						BaseDataSyncer.writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_UNKNOWN_ERROR, 
								BaseDataSyncer.getRegisteredEdgeHostName());
					}
				}
				
				Thread.sleep(10000);
			}
		}
	}
}
