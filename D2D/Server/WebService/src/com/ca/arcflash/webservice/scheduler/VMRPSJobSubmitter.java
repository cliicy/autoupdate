package com.ca.arcflash.webservice.scheduler;

import java.util.HashMap;
import java.util.Map;









import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.rps.webservice.data.RPSJobInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.data.job.rps.BackupJobArg;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.VSphereBackupJobTask;
import com.ca.arcflash.webservice.service.rps.JobService;
import com.ca.arcflash.webservice.service.rps.RPSServiceProxyManager;

// by default keepRPSJobInProxyRunningQueue is enabled
// set "SOFTWARE\\Arcserve\\Unified Data Protection\\Engine\\keepRPSJobInProxyRunningQueue = 0" to disable it
//
// it takes effect when the destination is RPS server
// job behavior difference:
// enabled:  proxy waiting queue 1st -> proxy running queue 1st -> rps waiting queue -> rps running queue -> continue in proxy running queue 1st
// disabled: proxy waiting queue 1st -> proxy running queue 1st -> rps waiting queue -> rps running queue -> proxy waiting queue 2nd -> proxy running queue 2nd

// Background: (TFS Bug 416690:[211815] HBBU catalog job doesn't start occasionally when HBBU job queue is smaller than data store job queue)
// previously it is not enabled, the job need to enter proxy waiting queue again. 
// RPS consider the job is end after waiting for 3 minutes and still no job monitor data
// then the following catalog/replication jobs won't be scheduled as expected (refer to RPSBaseClientJob.startRunning())

public class VMRPSJobSubmitter
{
	private static final Logger logger = Logger.getLogger(VMRPSJobSubmitter.class);
	private static VMRPSJobSubmitter instace = new VMRPSJobSubmitter();
	
	protected static final long DEFAULT_TIMEOUT = 3 * 60 * 1000;//3 minutes	
	
	// store the backup job arg submitted back from RPS server
	// The key is "{"vminstanceuuid":"502c665a-0186-030f-bf28-696b7d449cb5", "jobId":99}
	protected static final Map<String, BackupJobArg> rpsBackupJobArgMap = new HashMap<String, BackupJobArg>(); 
		
	// true to keep the rps job in proxy running queue
	private static Boolean keepRPSJobInProxyRunningQueue = null;
	
	private VMRPSJobSubmitter()
	{		
	}
	
	public static VMRPSJobSubmitter getInstance()
	{
		return instace;
	}

	
	
	// called by VSphereBackupJob.executeBackupJob
	// submit the backup job to RPS and wait for the result
	public BackupJobArg submitBackupToRpsAndWait(BackupJobArg arg, RpsHost host)
	{
		BackupJobArg result = null;
		
		try
		{
			do
			{
				if (host == null || arg == null)
				{
					logger.error("Invalid parameter");
					break;
				}

				// submit job to RPS
				long ret = JobService.getInstance().submitBackup(arg, host);
				if (ret != 0)
				{
					logger.error("Failed to RPSSubmitBackup ret = " + ret + " " + arg2String(arg));
					break;
				}
				
				logger.info("Submit backup to RPS server succeeded"+ " " + arg2String(arg));
				
				// wait till it run								
				if (!waitJobRunOnRPS(arg, host))
				{
					logger.error("Failed to wait till job running on RPS " + arg2String(arg));
					break;
				}
				
				logger.info("Backup job enter RPS running queue"+ " " + arg2String(arg));
				
				// Job is running now, get the returned BackupJobArg from backupNow()
				result = waitBackupJobArg(arg);			
				
				logger.info("Backup job is submitted back from RPS"+ " " + arg2String(result));
				
			}while(false);
		}
		catch (Exception e)
		{
			logger.error("Failed to get returned arg"+ " " + arg2String(arg), e);
		}
		
		return result;
	}
	
	// wait until the job enter running queue on RPS
	protected boolean waitJobRunOnRPS(BackupJobArg arg, RpsHost host)
	{
		boolean result = false;
		
		long startTime = System.currentTimeMillis();
		
		while(true)
		{
			try
			{
				if (!isJobRunningOnRPS(arg, host)) // not running
				{
					if ((System.currentTimeMillis() - startTime) > DEFAULT_TIMEOUT) // timeout
					{
						if (!isJobWaitingOnRPS(arg, host)) // is not waiting
						{
							logger.info("Job is not running or waiting. Maybe it is canceled or completed" + " " + arg2String(arg));
							break;
						}
					}

					// wait for a while
					try
					{
						Thread.sleep(3000);
					}
					catch (InterruptedException e)
					{
						logger.info("Interrupted" + " " + arg2String(arg), e);
						break;
					}

					continue;
				}

				// job is running
				result = true;
				break;

			}
			catch (Exception e)
			{
				logger.error("Error when waiting for job run on RPS", e);
				break;
			}
		}				
		
		return result;		
	}
	
	// wait until RPS submit the backup job arg back
	protected BackupJobArg waitBackupJobArg(BackupJobArg arg)
	{
		BackupJobArg result = null;
		
		long startTime = System.currentTimeMillis();
		while(true)
		{
			try
			{
				BackupJobArg temp = getReturnedBackupJobArg(arg);
				if (temp == null) // RPS has not submit the job back yet
				{
					if ((System.currentTimeMillis() - startTime) > DEFAULT_TIMEOUT) // timeout
					{ 
						logger.error("After the job is running on RPS, RPS has not submitted the job back in 3 minutes." + " " + arg2String(arg));
						break;
					}

					// wait for a while
					try
					{
						Thread.sleep(3000);
					}
					catch (InterruptedException e)
					{
						logger.info("Interrupted", e);
						break;
					}
					
					continue;				
				}
				
				// job is running
				result = temp;			
				break;		
				
			}
			catch(Exception e)
			{
				logger.error("Error when waiting for backupJobArg from RPS", e);
				break;
			}
						
		}				
		
		return result;
	}
	
	// check if the job is in RPS running queue
	protected boolean isJobRunningOnRPS(BackupJobArg arg, RpsHost host)
	{
		boolean result = false;
		
		try
		{
			RPSJobInfo[] rpsRunningJobs = RPSServiceProxyManager.getServiceByHost(host).getRPSRunningJobs(null);
			
			for (RPSJobInfo rpsJobInfo : rpsRunningJobs)
			{
				if (rpsJobInfo.getJobId() == arg.getJobId() && rpsJobInfo.getD2duuid().equals(arg.getD2dServerUUID()))
				{
					result = true;
					break;
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to get RPS running jobs", e);
		}
		
		return result;
	}
	
	// check if the job is in RPS waiting queue
	protected boolean isJobWaitingOnRPS(BackupJobArg arg, RpsHost host)
	{
		boolean result = false;
		
		try
		{
			RPSJobInfo[] rpsRunningJobs = RPSServiceProxyManager.getServiceByHost(host).getRPSWaitingJobs(null);
			
			for (RPSJobInfo rpsJobInfo : rpsRunningJobs)
			{
				if (rpsJobInfo.getJobId() == arg.getJobId() && rpsJobInfo.getD2duuid().equals(arg.getD2dServerUUID()))
				{
					result = true;
					break;
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to get RPS waiting jobs", e);
		}
		
		return result;
	}
	
	protected String generateKey(BackupJobArg arg)
	{
		String result = "";
		
		//{"vmInstanceUuid":"502c665a-0186-030f-bf28-696b7d449cb5", "jobId":99}
		if (arg != null && arg.getD2dServerUUID() != null && !arg.getD2dServerUUID().isEmpty())
		{
			result = String.format("{\"vmInstanceUuid\":\"%s\", \"jobId\":ignored}", arg.getD2dServerUUID(), arg.getJobId());
		}		
		
		return result;
	}
	
	public static String arg2String(BackupJobArg arg)
	{
		String result = "";
		
		//{"vmInstanceUuid":"502c665a-0186-030f-bf28-696b7d449cb5", "jobId":99}
		if (arg != null && arg.getD2dServerUUID() != null && !arg.getD2dServerUUID().isEmpty())
		{
			result = String.format("BackupJobArg - vmInstanceUuid:%s, jobId:%d, jobMethod:%d, jobName:%s, jobDetailName:%s, jobDetailGroup:%s, enableCatalog:%b, periodRetentionFlag:%d", 
					arg.getD2dServerUUID(), arg.getJobId(), arg.getJobMethod(), arg.getJobName(), arg.getJobDetailName(), arg.getJobDetailGroup(), arg.isEnableCatalog(), arg.getPeriodRetentionFlag());
		}		
		
		return result;
	}
	
	// called by VSphereService.backupNow(BackupJobArg)
	// save the returned job arg
	public void setReturnedBackupJobArg(BackupJobArg arg)
	{
		String key = generateKey(arg);
		if (key != null & !key.isEmpty())
		{
			synchronized(rpsBackupJobArgMap)
			{
				rpsBackupJobArgMap.put(key, arg);
			}
			
			logger.info(arg2String(arg));
		}		
	}
	
	// get the returned job arg
	protected BackupJobArg getReturnedBackupJobArg(BackupJobArg arg)
	{
		BackupJobArg result = null;
		
		String key = generateKey(arg);
		if (key != null & !key.isEmpty())
		{
			synchronized(rpsBackupJobArgMap)
			{
				if (rpsBackupJobArgMap.containsKey(key))
				{
					result = rpsBackupJobArgMap.get(key);
					rpsBackupJobArgMap.remove(key);
				}
			}
		}	
		
		return result;
	}	
	
	// read this setting from the registry
	public static boolean keepRPSJobInProxyRunningQueue()
	{
		if (keepRPSJobInProxyRunningQueue != null)
		{
			return keepRPSJobInProxyRunningQueue;
		}

		// default value
		keepRPSJobInProxyRunningQueue = true;

		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try
		{
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String value = registry.getValue(handle, "keepRPSJobInProxyRunningQueue"/*RegConstants.REGISTRY_KEY_VMWARE_MAX_JOB_NUM*/);

			if (value != null && !value.isEmpty())
			{
				int temp = Integer.parseInt(value);
				if (temp <= 0)
				{
					keepRPSJobInProxyRunningQueue = false;
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to parse " + "keepRPSJobInProxyRunningQueue", e);
		}
		finally
		{
			try
			{
				if (handle != 0)
				{
					registry.closeKey(handle);
				}
			}
			catch (Exception e)
			{
			}

			
			logger.info("keepRPSJobInProxyRunningQueue " + keepRPSJobInProxyRunningQueue);
		}

		return true;
	}
	
	// submit a backup job to RPS server directly, refer to VSphereBackupJob.executeBackupJob(JobExecutionContext)
	public boolean submitBackupToRps(VSphereBackupJobTask task)
	{
		boolean result = true;

		try
		{
			do
			{
				if (task == null)
				{
					logger.error("Invalid parameter");
					break;
				}
				
				

				JobExecutionContext context = task.getContext();
				JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
				
				logger.info(jobDetail.getFullName());

				VirtualMachine vm = (VirtualMachine) jobDetail.getJobDataMap().get("vm");
				VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
				
				// create a new job id
				long jobId = 0;
				Object jobID = jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
				if (jobID != null)
					jobId = (Long) jobID;
				if (jobId <= 0)
				{
					try
					{
						jobId = CommonService.getInstance().getNativeFacade().getJobID();
						logger.info("Get Job ID:" + jobId);
					}
					catch (Exception e)
					{
						logger.error("Failed to get job id", e);
						throw e;
					}
				}
				
				

				// BackupJobArg
				BackupJobArg arg = VSphereBackupJob.getBackupJobArg(jobDetail, configuration, jobId);
				
				logger.info(VMRPSJobSubmitter.arg2String(arg));

				// submit job to RPS
				long ret = JobService.getInstance().submitBackup(arg, configuration.getBackupRpsDestSetting().getRpsHost());
				if (ret != 0)
				{
					logger.error("Failed to RPSSubmitBackup ret = " + ret);
					break;
				}

				logger.info("Submit backup to RPS server succeeded");
			}
			while (false);
		}
		catch (Exception e)
		{
			logger.error("Failed to submitBackupToRps", e);
			result = false;
		}

		return result;
	}
	
}
