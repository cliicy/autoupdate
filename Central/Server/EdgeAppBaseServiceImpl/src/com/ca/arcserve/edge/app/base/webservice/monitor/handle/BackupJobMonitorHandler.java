package com.ca.arcserve.edge.app.base.webservice.monitor.handle;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.monitor.DataStoreInfoCache;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitorReader;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;


public class BackupJobMonitorHandler extends JobMonitorHandler{

	private static final Logger logger = Logger.getLogger(BackupJobMonitorHandler.class);
	
	@Override
	public void doPreHandles(JobMonitorReader reader, JobDetail jobDetail) {
		// do nothing in this method
	}

	@Override
	public void doPostHandles(JobMonitorReader reader, JobDetail jobDetail,
			List<FlashJobMonitor> monitors) {
		if(monitors==null||monitors.isEmpty())
			return;
		// set Job's rpsName and DataStoreName 
		logger.debug("BackupJobMonitorHandler doPostHandles start jobDetail:"+jobDetail.getSource()+" serverId:"+jobDetail.getServerId());
		for (FlashJobMonitor job : monitors) {
			if(job.getJobType()!=JobType.JOBTYPE_BACKUP
					&&job.getJobType()!=JobType.JOBTYPE_VM_BACKUP){
				continue;
			}
			if(StringUtil.isEmptyOrNull(job.getRpsServerName())){
				logger.debug("BackupJobMonitorHandler doPostHandles this BackupJob has no RPSServerName");
				continue;
			}
			logger.debug("BackupJobMonitorHandler doPostHandles this BackupJob 's RPSServerName:"+job.getRpsServerName());
			if(StringUtil.isEmptyOrNull(job.getDataStoreUUID())){
				logger.debug("BackupJobMonitorHandler doPostHandles DataStoreUUID IsNull");
			} else {
				logger.debug("BackupJobMonitorHandler doPostHandles DataStoreUUID:"+job.getDataStoreUUID());				
				EdgeRpsDataStore store = DataStoreInfoCache.getInstance().getDataStoreInfo(job.getDataStoreUUID());
				if(store==null||store.getDatastore_name()==null){
					logger.error("BackupJobMonitorHandler doPostHandles Error to getStoreInfo DataStoreUUID:"+job.getDataStoreUUID()+(store==null?"store==null":store.getDatastore_name()));	
					job.setRpsDataStoreName("");
					return;
				}	
				logger.debug("BackupJobMonitorHandler doPostHandles DataStoreUUID:"+job.getDataStoreUUID()+" Name:"+store.getDatastore_name());				
				job.setRpsDataStoreName(store.getDatastore_name());
			}
		}		
		logger.debug("BackupJobMonitorHandler doPostHandles end ");		
	}	
}
