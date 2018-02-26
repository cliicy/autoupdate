package com.ca.arcflash.webservice.service.rps;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.catalog.CatalogJobPara;
import com.ca.arcflash.webservice.data.job.rps.ArchiveJobArg;
import com.ca.arcflash.webservice.data.job.rps.BackupJobArg;
import com.ca.arcflash.webservice.data.job.rps.BaseJobArg;
import com.ca.arcflash.webservice.data.job.rps.CatalogJobArg;
import com.ca.arcflash.webservice.data.job.rps.CopyJobArg;
import com.ca.arcflash.webservice.data.job.rps.RestoreJobArg;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.ServiceException;

/**
 * The class is called by D2D to ask RPS server to see whether a backup or other jobs can run.
 * @author zhawe03
 *
 */
public class JobService {
	private static final Logger logger = Logger.getLogger(JobService.class);
	
	private static JobService INSTANCE = new JobService();
	
	private JobService() {}
	
	public static JobService getInstance() {
		return INSTANCE;
	}
	
	public long submitBackup(BackupJobArg arg, RpsHost host){
		try {
			if(host == null || arg == null) {
				logger.error("Invalid parameter");
				return -1;
			}

			return RPSServiceProxyManager.getServiceByHost(host).RPSSubmitBackup(arg);
		}catch(ServiceException se) {
			logger.error("Failed to get backup configuration", se);
		}catch(WebServiceException e){
			logger.error("Failed get RPS webservice client and submit backup job", e );
		}
		return 0;		
	}
	
	public long submitFSOnDemandCatalog(CatalogJobPara para, RpsHost host) {
		try {
			if (host == null || para == null) {
				logger.error("Invalid parameter");
				return -1;
			}

			return RPSServiceProxyManager.getServiceByHost(host).submitFSOndemandCatalogJob(para);
		} catch (ServiceException se) {
			logger.error("Failed submit fs on demand catalog", se);
		} catch (WebServiceException e) {
			logger.error(
					"Failed get RPS webservice client and submit fs on demand catalog job", e);
		}
		return 0;
	}

	public long submitCatalog(CatalogJobArg arg, RpsHost host) {
		
		try {
			if(host == null || arg == null) {
				logger.error("Invalid parameter");
				return -1;
			}
			
			return RPSServiceProxyManager.getServiceByHost(host).RPSSubmitCatalog(arg);
		} catch(ServiceException se) {
			logger.error("Failed to get backup configuration", se);
		} catch(WebServiceException e) {
			logger.error(
					"Failed get RPS webservice Client and submit catalog job",
					e);
		}
		
		return 0;
	}

	public long submitArchive(ArchiveJobArg arg, RpsHost host) {
		try {
			if(host == null || arg == null) {
				logger.error("Invalid parameter");
				return -1;
			}
			
			return RPSServiceProxyManager.getServiceByHost(host).RPSSubmitArchiveJob(arg);
		} catch(ServiceException se) {
			logger.error("Failed to get backup configuration", se);
		} catch(WebServiceException e) {
			logger.error(
					"Failed get RPS webservice Client and submit catalog job",
					e);
		}
		
		return 0;
		
	}
	
	public long submitRestoreJob(RestoreJobArg arg, RpsHost host) {
		try {
			if (host == null || arg == null) {
				logger.error("Invalid parameter");
				return -1;
			}

			return RPSServiceProxyManager.getServiceByHost(host).RPSSubmitRestoreJob(arg);
		} catch(ServiceException se) {
			logger.error("Failed to get backup configuration", se);
		} catch(WebServiceException e) {
			logger.error(
					"Failed get RPS webservice Client and submit catalog job",
					e);
		}
		
		return 0;
	}
	
	public long submitCopyJob(CopyJobArg arg, RpsHost host) {
		try {
			if (host == null || arg == null) {
				logger.error("Invalid parameter");
				return -1;
			}

			return RPSServiceProxyManager.getServiceByHost(host).RPSSubmitCopyJob(arg);
		} catch (ServiceException se) {
			logger.error("Failed to get backup configuration", se);
		} catch (WebServiceException e) {
			logger.error(
					"Failed get RPS webservice Client and submit catalog job",
					e);
		}

		return 0;
	}
}
