package com.ca.arcserve.edge.app.base.webservice.srm;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.IEdgeTaskItem;


public class SrmProbing implements IEdgeTaskItem {
	private static Logger _log = Logger.getLogger(SrmProbing.class);
	private String mHost;
	private int mHostID;
	private String mProtocol;
	private int mPort;
	private int mCommand;
	private int mRetryTimes;
	private int mRetryInterval;
	private long mJobID;
	private static Logger logger = Logger.getLogger( SrmProbing.class );
	
	public void setHost(String host) {
		mHost = host;
	}

	public void setHostID(int hostID) {
		mHostID = hostID;
	}
	
	public void setProtocol(String protocol) {
		mProtocol = protocol;
	}
	
	public void setPort(int port) {
		mPort = port;
	}
	
	public void setCommand(int command) {
		mCommand |= command;
	}
	
	public void setRetryTimes(int times) {
		mRetryTimes = times;
	}
	
	public void setRetryInterval(int interval) {
		mRetryInterval = interval;
	}
	
	public long getJobID() {
		return mJobID;
	}
	
	public void setJobID(long id) {
		mJobID = id;
	}
	

	@Override
	public void run() {
		Thread.currentThread().setName(Long.toString(mJobID));
		if (!SrmServiceImpl.IsManagedByDifferentEdge(mHostID, mProtocol, mHost,
				mPort)) {
			
			do {
				if (SrmServiceImpl.InvokeGetSrmInfo(mHostID, mProtocol, mHost, mPort,
						mCommand)) {
					_log.debug(mHost + " Probe succeed.");
					break;
				} else {
					_log.debug(mHost + " Probe failed.");
					try {
						Thread.sleep(mRetryInterval * 1000);
					} catch (InterruptedException e) {
						logger.error("interrpution error happens when wait for srm probe retry thread!", e );
						break;
					}
				}
			} while (mRetryTimes-- > 0);
			
		}
	}
	
	
}
