package com.ca.arcflash.webservice.edge.datasync.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

abstract class AbstractSyncMonitor {
	
	private static Logger logger = Logger.getLogger(AbstractSyncMonitor.class);
	
	private ExecutorService executor;
	private Future<?> future;
	private AtomicBoolean initialized = new AtomicBoolean();
	private long failInterval = 3000;
	private String lastSyncErrorMessage;
	
	protected void logSyncErrorMessage(String syncErrorMessage) {
		if (syncErrorMessage.equalsIgnoreCase(lastSyncErrorMessage)) {
			logger.debug(getConcrateClassPrefix() + syncErrorMessage);
		} else {
			logger.error(getConcrateClassPrefix() + syncErrorMessage);
		}
		
		lastSyncErrorMessage = syncErrorMessage;
	}
	
	protected void logSyncErrorMessage(String syncErrorMessage, Throwable t) {
		if (syncErrorMessage.equalsIgnoreCase(lastSyncErrorMessage)) {
			logger.debug(getConcrateClassPrefix() + syncErrorMessage, t);
		} else {
			logger.error(getConcrateClassPrefix() + syncErrorMessage, t);
		}
		
		lastSyncErrorMessage = syncErrorMessage;
	}
	
	protected boolean isInitialized() {
		return initialized.get();
	}
	
	protected abstract void resetSyncData();
	protected abstract void doInitSync() throws SOAPFaultException, WebServiceException, Exception;
	protected abstract boolean doSync();
	protected abstract void onSyncFailed();
	protected abstract boolean setupSyncData();
	
	protected String getConcrateClassPrefix() {
		return getClass().getSimpleName() + ": ";
	}
	
	public void start(ExecutorService executor) {
		this.executor = executor;
		resetSyncData();
		future = executor.submit(new Runnable() {
			
			@Override
			public void run() {
				ensureInitSync();
			}
			
		});
		
		logger.info(getConcrateClassPrefix() + "started.");
	}
	
	protected void ensureInitSync() {
		logger.debug(getConcrateClassPrefix() + "init sync started.");
		
		boolean finish = false;
		while (!finish) {
			try {
				doInitSync();
				finish = true;
			} catch(SOAPFaultException e) {
				logSyncErrorMessage("init sync failed and wait for a moment to try again, error message = " + e.getMessage());
			} catch (WebServiceException e) {
				logSyncErrorMessage("init sync failed and wait for a moment to try again, error message = " + e.getMessage());
			} catch (Throwable e) {
				logSyncErrorMessage("init sync failed with unexpected exception, error message = " + e.getMessage(), e);
				finish = true;	// Avoid blocking job monitor data sync when unexpected exception occurred.
			}
			
			if (!finish) {
				try {
					Thread.sleep(failInterval);
				} catch (InterruptedException e) {
					logger.warn(getConcrateClassPrefix() + "sleep interrupted after init sync failed.");
					return;
				}
			}
		}
		
		boolean hasSyncData = setupSyncData();
		initialized.set(true);
		
		if (hasSyncData) {
			logger.info(getConcrateClassPrefix() + "after init sync, begin to sync queued data.");
			startSync();
		}
		
		logger.info(getConcrateClassPrefix() + "init sync finished.");
	}

	public void stop() {
		resetSyncData();
		future.cancel(true);
		logger.info(getConcrateClassPrefix() + "stopped.");
	}

	protected void startSync() {
		logger.debug(getConcrateClassPrefix() + "sync data started.");
		
		if (!isInitialized()) {
			logger.warn(getConcrateClassPrefix() + "init sync not finished, cannot start to sync data.");
			return;
		}
		
		future = executor.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					ensureSync();
				} catch (Throwable e) {
					logger.error("sync failed.", e);
				}
			}
			
		});
	}
	
	private void ensureSync() {
		if (!doSync()) {
			logger.debug(getConcrateClassPrefix() + "sync data failed and wait for a moment to continue.");
			
			try {
				Thread.sleep(failInterval);
			} catch (InterruptedException e) {
				return;
			}
			
			onSyncFailed();
			logger.debug(getConcrateClassPrefix() + "handle the failed sync data.");
		}
		
		if (setupSyncData()) {
			logger.debug(getConcrateClassPrefix() + "has more data to sync.");
			startSync();
		} else {
			logger.debug(getConcrateClassPrefix() + "has no more data to sync.");
		}
	}
	
}
