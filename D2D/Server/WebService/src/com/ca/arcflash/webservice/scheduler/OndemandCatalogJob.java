package com.ca.arcflash.webservice.scheduler;

import java.util.Observer;

import org.quartz.JobExecutionContext;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.common.CatalogQueueType;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.ServiceException;

public class OndemandCatalogJob extends CatalogJob {
	private volatile static boolean isJobRunning = false;
	static final Observer[] observers = new Observer[] {CatalogService.getInstance()};
	
	public OndemandCatalogJob(){
		jobType = JobType.JOBTYPE_CATALOG_GRT;
	}
	
	@Override
	protected boolean getJobLock() {
		if(isJobRunning)
			return false;
		
		isJobRunning = true;
		return true;
	}

	@Override
	protected void releaseJobLock() {
		isJobRunning = false;
	}

	@Override
	protected long launchJob(long id) throws ServiceException {
		return CatalogService.getInstance().launchCatalogJob(id, CatalogQueueType.ONDEMAND_JOB);
	}
	
	public static boolean isJobRunning() {
		return isJobRunning;
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
