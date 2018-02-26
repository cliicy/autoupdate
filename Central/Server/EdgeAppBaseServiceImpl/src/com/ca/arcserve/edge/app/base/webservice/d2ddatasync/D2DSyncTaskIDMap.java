package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeTaskIdDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;

public class D2DSyncTaskIDMap {
	private static final Logger logger = Logger.getLogger(D2DSyncTaskIDMap.class);
	private static Map<Integer, Long> sync_HostID_TaskID_List = new HashMap<Integer, Long>();
	
	public synchronized static long getNextTaskId(Integer d2dHostId) {
		long nextTaskId = getNextTaskId();
		sync_HostID_TaskID_List.put(d2dHostId, nextTaskId);
		return nextTaskId;
	}
	
	public synchronized static long getCurrentTaskId(Integer d2dHostId) {
		Long nextTaskId = sync_HostID_TaskID_List.get(d2dHostId);
		if(nextTaskId == null)
			return 0;
		else
			return nextTaskId;
	}
	
	private static long getNextTaskId() {
		try {
			long[] taskId = new long[1];
			IEdgeTaskIdDao taskIdDao = DaoFactory.getDao(IEdgeTaskIdDao.class);
			taskIdDao.as_edge_get_next_taskid(taskId);
			return taskId[0];
		} catch(Throwable t) {
			logger.error(t.toString());
			return 0;
		}
	}
}
