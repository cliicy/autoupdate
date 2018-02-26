package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.RepJobMonitor4Edge;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public class D2DConversionJobsStatusCache extends AbstractJobStatusCache<RepJobMonitor4Edge> {
	
	protected static final Logger logger = Logger.getLogger( D2DConversionJobsStatusCache.class );
	
	private static D2DConversionJobsStatusCache jobsStatusCache = new D2DConversionJobsStatusCache();
	
	IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	public static final int JOB_PHASE = 0;
	public static final int HOST_ID = 0;
	
	private D2DConversionJobsStatusCache() {
	}
	
	public static D2DConversionJobsStatusCache getJobsStatusCache() {
		return jobsStatusCache;
	}
	
	@Override
	public void run() {
		updateJobPhase(JOB_PHASE, HOST_ID);
		super.run();
	}
	
	@Override
	protected void onTimeout(TimeoutJobStatus<RepJobMonitor4Edge> timeoutJobStatus) {
		int nodeId = timeoutJobStatus.getNodeId();
		boolean removed = cache.remove(nodeId, timeoutJobStatus);
		if (removed) {
			updateJobPhase(JOB_PHASE, nodeId);
		}
	}
	
	public void updateJobPhase(int jobPhase, int hostID) {
		
		/*
		 * Per Youwei, this class is useless, and should not be used. But in
		 * real environment, we observed that this was still be called very
		 * frequently, and with the hostID is 0. This caused a very frequent
		 * deadlock in SQL server. Since this code and related store procedure
		 * will no longer be used, according to Eric, it's not worth putting
		 * in more time to investigate the wrong hostID (it should not be 0),
		 * just remove the code here and the store procedure. So any code
		 * this method should be removed in future release.
		 * 
		 * Pang, Bo (panbo01)
		 * 2014-09-03
		 */
	}
	
	public void put(String nodeId, RepJobMonitor4Edge backupJob) {
		add(Integer.valueOf(nodeId), backupJob);
	}
	
	public RepJobMonitor4Edge get(String nodeId) {
		return get(Integer.valueOf(nodeId));
	}
	
	public List<RepJobMonitor4Edge> getD2DConversionJobStatusInfoList(List<String> nodeIdList) {
		List<RepJobMonitor4Edge> infoList = new ArrayList<RepJobMonitor4Edge>();
		for (String nodeId : nodeIdList) {
			RepJobMonitor4Edge info = get(nodeId);
			if (info != null) {
				infoList.add(info);
			}
		}
		return infoList;
	}

}
