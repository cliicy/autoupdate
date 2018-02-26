package com.ca.arcserve.edge.app.base.webservice.srm;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.IEdgeTaskItem;


public class NodeDeleteProbing implements IEdgeTaskItem {
	private static Logger _log = Logger.getLogger(NodeDeleteProbing.class);
	private String mHost;
	private int hostID;
	private int mRetryTimes;
	private int mRetryInterval;
	
	public void setHost(String host) {
		mHost = host;
	}
	
	public void setHostID(int hostID) {
		this.hostID = hostID;
	}
	
	public int getHostID( ) {
		return hostID;
	}
	
	public void setRetryTimes(int times) {
		mRetryTimes = times;
	}
	
	public void setRetryInterval(int interval) {
		mRetryInterval = interval;
	}

	@Override
	public void run() {
		do {
			if (NodeDeleteServiceImpl.deleteNodebyID(hostID)) {
				_log.debug(mHost + " Deleted succeed.");
				break;
			} else {
				_log.debug(mHost + " Deleted failed.");
				try {
					Thread.sleep(mRetryInterval * 1000);
				} catch (InterruptedException e) {
					_log.error(e.getMessage(), e);
				}
			}
		}while (mRetryTimes-- > 0); 
	}
}
