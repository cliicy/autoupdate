package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DBackupJobStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

/**
 * Confirmed with Youwei, this class is useless. We should use D2DAllJobStatusCache
 * to get job status.
 * 
 * @author panbo01 (2014-08-21)
 */
@Deprecated
public class D2DJobsStatusCache extends AbstractJobStatusCache<D2DBackupJobStatusInfo> {
	
	protected static final Logger logger = Logger.getLogger( D2DJobsStatusCache.class );
	
	private static D2DJobsStatusCache backupJobsStatusCache = new D2DJobsStatusCache();
	
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	public static final int JOB_PHASE = 0;
	public static final int HOST_ID = 0;
	
	private D2DJobsStatusCache() {
	}
	
	public static D2DJobsStatusCache getD2DBackupJobsStatusCache() {
		return backupJobsStatusCache;
	}
	
	@Override
	public void run() {
		updateJobPhase(JOB_PHASE, HOST_ID);
		super.run();
	}
	
	@Override
	protected void onTimeout(TimeoutJobStatus<D2DBackupJobStatusInfo> timeoutJobStatus) {
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
	
	public void put(String nodeId, D2DBackupJobStatusInfo backupJob) {
		add(Integer.valueOf(nodeId), backupJob);
	}
	
	public D2DBackupJobStatusInfo get(String nodeId) {
		return get(Integer.valueOf(nodeId));
	}

	public List<D2DBackupJobStatusInfo> getD2DBackupJobStatusInfoList(List<String> nodeIdList) {
		List<D2DBackupJobStatusInfo> infoList = new ArrayList<D2DBackupJobStatusInfo>();
		for (String nodeId : nodeIdList) {
			D2DBackupJobStatusInfo info = get(nodeId);
			if (info != null) {
				infoList.add(info);
			}
		}
		return infoList;
	}

}
