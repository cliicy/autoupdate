package com.ca.arcserve.edge.app.base.webservice.monitor.cache;

import java.util.HashMap;
import java.util.Map;

import com.ca.arcflash.webservice.data.FlashJobMonitor;

public class HistoryMonitorMap
{
	private static HistoryMonitorMap instance = null;
	private Map<Long, FlashJobMonitor> jobsMap;
	
	protected HistoryMonitorMap()
	{
		this.jobsMap = new HashMap<Long, FlashJobMonitor>();
	}	

	public static synchronized HistoryMonitorMap getInstance()
	{
		if (instance == null)
			instance = new HistoryMonitorMap();
		
		return instance;
	}
	
	public synchronized void clear()
	{
		this.jobsMap.clear();
	}	
	
	public synchronized FlashJobMonitor getJobMonitor( long historyId )
	{
		return this.jobsMap.get( historyId );
	}
	
	public synchronized void putJobMonitor( long historyId, FlashJobMonitor job)
	{
		this.jobsMap.put(historyId, job);		
	}

}
