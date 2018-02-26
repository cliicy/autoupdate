package com.ca.arcflash.webservice.scheduler;

import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSPhereCatalogService;


public class VSphereOndemandCatalogJob extends VSphereCatalogJob {	
	
	private static final Logger logger = Logger.getLogger(VSphereOndemandCatalogJob.class);
	private static final Set<String> runningJobs = new HashSet<String>();
	static final Observer[] observers = new Observer[] {VSPhereCatalogService.getInstance()};
	
	public VSphereOndemandCatalogJob(){
		jobType = JobType.JOBTYPE_CATALOG_GRT;
	}
	
	@Override
	public boolean getJobLock(String vmInstanceUUID){
		synchronized(VSphereOndemandCatalogJob.class)
		{
			if(vmInstanceUUID == null){
				logger.error("Null UUID ");
				return false;
			}
			if(runningJobs.contains(vmInstanceUUID)){	
				return false;
			}else{
				runningJobs.add(vmInstanceUUID);
				return true;
			}
		}		
	}
	
	public static synchronized boolean isJobRunning(String vmInstanceUUID){
		if(vmInstanceUUID == null){
			logger.error("Null UUID ");
			return false;
		}
		return runningJobs.contains(vmInstanceUUID);
	}
	
	@Override
	public void releaseJobLock(String vmInstanceUUID) {
		synchronized(VSphereOndemandCatalogJob.class)
		{
			if(vmInstanceUUID == null){
				logger.error("Null UUID ");
				return;
			}
			runningJobs.remove(vmInstanceUUID);
		}
		
	}
	
	@Override
	public boolean isJobRunningFromJobMonitor(String instanceUUID, long jobType) {
		
		return false;
		
	}
	
	@Override
	//on demand catalog should run immediately, not limited by datastore pool.
	protected boolean checkRPS4Job(Boolean runNow, boolean toRPS,
			RpsHost rpsHost, String dataStoreUUID,
			JobExecutionContext jobContext, long jobType,
			NativeFacade nativeFacade) throws ServiceException {
		return false;
	}

}
