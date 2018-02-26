package com.ca.arcserve.edge.app.base.webservice.srm;


import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSyncDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.NodeDeleteJob;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;


public class NodeDeleteServiceImpl {

	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static Logger logger = Logger.getLogger(NodeDeleteServiceImpl.class);
	public static boolean IsNodeDeleteDone() {
		EdgeTask nodeDeleteTask = EdgeTaskFactory.getInstance().getTask(
	              EdgeTaskFactory.EDGE_TASK_NodeDelete);
	    int waitingItemCount = nodeDeleteTask.getWaitingQueueSize();
	    int executeItemCount = nodeDeleteTask.getExecuteQueueSize();

	    return (waitingItemCount == 0 && executeItemCount == 0);
	}

	/*
	 * Probe several nodes which is user selected at the same time
	 */
	public static void deleteNodes(List<Integer> nodesIDList) {
		NodeDeleteJob job = new NodeDeleteJob();
	    job.setDeletedNodesIDList(nodesIDList);

		ScheduleData data = new ScheduleData();
	    data.setScheduleName("Node Data Deletion UI Job");

	    job.run(data,(Object)null);
	}

	public static boolean deleteNodebyID ( int hostID ) {
		try {
			IEdgeSyncDao iDao = DaoFactory.getDao(IEdgeSyncDao.class);
            iDao.as_edge_sync_delete_branch(hostID);
			hostMgrDao.as_edge_purge_d2d(hostID);
			return true;
		} catch(Throwable t) {
			logger.error(t.getMessage(), t);
			return false;
		}
	}
}
