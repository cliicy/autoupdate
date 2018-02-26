package com.ca.arcserve.edge.app.base.webservice.jobhistory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.appdaos.EdgeJobHistory;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;


public class QueryJobHistorysScheduler extends TimerTask
{
	private Timer timer;
	private ConcurrentMap<Long, FlashJobMonitor> map;
	private static final int DEFAULT_RESYNCINTERVAL	= 10 * 1000; // 10 seconds
	private static Logger logger = Logger.getLogger( QueryJobHistorysScheduler.class );
	private IEdgeJobHistoryDao jobHistoryDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);
	
	public QueryJobHistorysScheduler(ConcurrentMap<Long, FlashJobMonitor> HISTORY_MONITOR_MAP)
	{
		this.map = HISTORY_MONITOR_MAP;
		logger.debug("QueryJobHistorysTaskScheduler init DEFAULT_RESYNCINTERVAL="+DEFAULT_RESYNCINTERVAL);
		this.timer = new Timer( "Query JobHistory_Monitor Timer" );
		this.timer.schedule( this, 1000, DEFAULT_RESYNCINTERVAL );
	}	
	
	public void cancleTimer(){
		logger.debug("QueryJobHistorysTaskScheduler cancleTimer");
		if(timer!=null)
			timer.cancel();
	}

	@Override
	public void run() {
		logger.debug("QueryJobHistorysTaskScheduler run enter");
		// Get all jobMonitor form DB
		List<EdgeJobHistory> lstJobHistory = new ArrayList<EdgeJobHistory>();
		try {
			jobHistoryDao.as_edge_d2dJobHistory_monitor_getJobMonitor(-1, -1, "",lstJobHistory);
		} catch (Exception e) {
			logger.error("QueryJobHistorysTaskScheduler run catch Exception "+e.getMessage());
			return;
		}
		if (lstJobHistory == null || lstJobHistory.isEmpty()){
			logger.debug("QueryJobHistorysTaskScheduler run lstJobHistory==null map.clear");
			map.clear();
		}else{
			logger.debug("QueryJobHistorysTaskScheduler run lstJobHistory.size="+lstJobHistory.size());
			for (Iterator<Long> iterator = map.keySet().iterator(); iterator.hasNext();) {
				Long key = (Long) iterator.next();
				boolean findFlg = false;
				for (EdgeJobHistory edgeJobHistory : lstJobHistory) {
					if(edgeJobHistory.getId()==key){
						findFlg = true;
						break;
					}
				}
				if(!findFlg){
					logger.debug("QueryJobHistorysTaskScheduler run find Key="+key+" ");
					map.remove(key);
				}
			}
		}
		logger.debug("QueryJobHistorysTaskScheduler run end");
	}	
}
