package com.ca.arcflash.webservice.scheduler;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class CalMissedJob implements IComputeMissedJob {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CalMissedJob.class);

	protected JobExecutionContext missedJobContext = null;
	protected JobExecutionContext runningJobContext = null;
	private final static CalMissedJob cal = new CalMissedJob();

	public static IComputeMissedJob getInstance() {
		return cal;
	}

	@Override
	public JobExecutionContext compute(JobExecutionContext incoming) {

		Date incomeDate = incoming.getScheduledFireTime();
		//NOTE: runningJobContext may be null IF restart the webservice and the backend job is running.
		Date runnigDate = runningJobContext == null ? new Date(0) : runningJobContext.getScheduledFireTime();
		boolean isAfter = incomeDate.after(runnigDate);

		logger.info("isAfter:" + isAfter + ", in date:" + incomeDate + ", run date:" + runnigDate);

		if (missedJobContext == null) {
			if (isAfter) {
				missedJobContext = incoming;
			}

		} else {
			//r1: if the incoming job and current running job scheduled at the same point of time,
			//    keep the existing missed one since it occurred at least no later than the incoming one.
			if (!isAfter) {
				logger.info("isAfter:" + isAfter + ", in date:" + incomeDate + ", run date:" + runnigDate);
				return missedJobContext;
			}
			//r2: if the job scheduled has higher priority.
			int incomingPriority = incoming.getTrigger().getPriority();
			int existingPriority = missedJobContext.getTrigger().getPriority();
			if (incomingPriority >= existingPriority) {
				logger.info("High priority one found:" + incomingPriority + ", replace exist missed priority:" + existingPriority);
				missedJobContext = incoming;
			}
		}

		return missedJobContext;
	}

	@Override
	public void initJobContext(JobExecutionContext missedJobContext, JobExecutionContext runningJobContext) {
		this.missedJobContext = missedJobContext;
		this.runningJobContext = runningJobContext;

	}
	
	// for debug log use
	protected void logJobContextForHBBU(String caption, JobExecutionContext jobContext)
	{		
		logger.info("=== " + caption + " begin ===");
		
		try
		{
			if (jobContext != null)
			{
				logger.info("scheduledFireTime: " + jobContext.getScheduledFireTime());
				logger.info("fireTime: " + jobContext.getFireTime());
				logger.info("previousFireTime: " + jobContext.getPreviousFireTime());
				logger.info("nextFireTime: " + jobContext.getNextFireTime());
				
				if (jobContext.getJobDetail() != null)
				{
					//logger.info("JobFullName: " + jobContext.getJobDetail().getFullName());
					
					Object jobID = jobContext.getJobDetail().getJobDataMap().get(VSphereService.JOB_ID);
					if(jobID != null)
					{
						logger.info("jobID: " + (Long)jobID);
					}
					
					Integer jobType = jobContext.getJobDetail().getJobDataMap().getInt("jobType");
					logger.info("JobType: " + jobType);
					
					String jobName = jobContext.getJobDetail().getJobDataMap().getString("jobName");
					logger.info("JobDetailName: " + jobName);
					
					VirtualMachine vm = (VirtualMachine)jobContext.getJobDetail().getJobDataMap().get("vm");
					if (vm != null)
					{
						logger.info("VMName: " + vm.getVmName());
						logger.info("VMInstanceUUID: " + vm.getVmInstanceUUID());					
					}			
				}
				
				if (jobContext.getTrigger() != null)
				{
					//logger.info("TriggerFullName: " + jobContext.getTrigger().getFullName());
					logger.info("TriggerPriority: " + jobContext.getTrigger().getPriority());
					logger.info("previousFireTime: " + jobContext.getTrigger().getPreviousFireTime());
					logger.info("nextFireTime: " + jobContext.getTrigger().getNextFireTime());
				}
			}
		}
		catch(Exception e)
		{
			logger.error("error when logging job context", e);
		}
		
		logger.info("=== " + caption + " end ===");	
	}

	protected int getJobPriority(JobExecutionContext jobContext)
	{
		int priority = -1;
		
		try
		{
			do
			{
				if (jobContext == null)
				{
					break;
				}
				
				// get from trigger priority
				if (jobContext.getTrigger() != null && jobContext.getTrigger().getPriority() > 5)
				{
					priority = jobContext.getTrigger().getPriority();
					logger.info("Got priority from trigger = " + priority);
					break;
				}
				
				if (jobContext.getJobDetail() == null)
				{
					break;
				}
					
				// get the priority from periodRetentionFlag
				if (jobContext.getJobDetail().getJobDataMap().containsKey("periodRetentionFlag"))
				{
					int periodRetentionFlag = jobContext.getJobDetail().getJobDataMap().getInt("periodRetentionFlag");

					if ((periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Daily) == PeriodRetentionValue.QJDTO_B_Backup_Daily)
					{
						priority = ScheduleUtils.DAY_PRIORITY;
					}

					if ((periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Weekly) == PeriodRetentionValue.QJDTO_B_Backup_Weekly)
					{
						priority = ScheduleUtils.WEEK_PRIORITY;
					}

					if ((periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Monthly) == PeriodRetentionValue.QJDTO_B_Backup_Monthly)
					{
						priority = ScheduleUtils.MONTH_PRIORITY;
					}
					
					logger.info("periodRetentionFlag = " + periodRetentionFlag + " priority = " + priority);
				}
				
				if (priority > 5)
				{
					break;
				}

				// get the priority from jobName
				String jobName = jobContext.getJobDetail().getJobDataMap().getString("jobName");
				if (jobName != null)
				{
					if (jobName.contains(Constants.daily))
					{
						priority = ScheduleUtils.DAY_PRIORITY;
					}

					if (jobName.contains(Constants.weekly))
					{
						priority = ScheduleUtils.WEEK_PRIORITY;
					}

					if (jobName.contains(Constants.monthly))
					{
						priority = ScheduleUtils.MONTH_PRIORITY;
					}
					
					logger.info("jobName = " + jobName + " priority = " + priority);
				}
			}
			while(false);
		}
		catch(Exception e)
		{
			logger.error("error when getting job priority", e);
		}
		
		return priority;		
	}

	// liuwe05 2015-05-07 fix 219434: RW : Schedule for daily backup jobs  was not running while custom scheduled backup job  is running
	// daily job is not saved as missed job when there is a custom job running
	// This function is for HBBU. Previously both HBBU and Agent use compute()
	@Override	
	public JobExecutionContext computeForHBBU(JobExecutionContext incomingJobContext)  
	{		
		// compare the time of incoming job and running job
		Date incomingDate = incomingJobContext.getScheduledFireTime();		
		// NOTE: runningJobContext may be null IF restart the webservice and the backend job is running.
		Date runningDate = runningJobContext == null ? new Date(0) : runningJobContext.getScheduledFireTime();
		
		int compare = incomingDate.compareTo(runningDate); // -1: incomingDate<runningDate; 0: incomingDate=runningDate; 1: incomingDate>runningDate
		logger.info("compare = " + compare + ", incomingDate = " + incomingDate + ", runningDate = " + runningDate);

		if (missedJobContext == null)
		{
			logger.info("missedJobContext is null.");
			if (compare > 0)
			{
				missedJobContext = incomingJobContext;
				logger.info("incoming job is newer. incomingJobContext saved.");
			}
			else if (compare == 0)
			{
				int runningPriority = getJobPriority(runningJobContext);
				int incomingPriority = getJobPriority(incomingJobContext);				
				logger.info(String.format("runningPriority = %d, incomingPriority = %d", runningPriority, incomingPriority));
				
				// if time are equal, lower priority job doesn't need makeup
				if (incomingPriority > runningPriority)
				{
					missedJobContext = incomingJobContext;
					logger.info(String.format("same scheduledFireTime, higher priority. runningPriority = %d, incomingPriority = %d,  incomingJobContext saved.", runningPriority, incomingPriority));
				}
			}
		}
		else
		{
			if (compare < 0)
			{
				logger.info("incoming job is older than the running job, don't overwrite the existing makeup");
			}
			else
			{
				int incomingPriority = getJobPriority(incomingJobContext);
				int existingPriority = getJobPriority(missedJobContext);	
				logger.info(String.format("incomingPriority = %d, existingPriority = %d", incomingPriority, existingPriority));
				
				if (incomingPriority >= existingPriority)
				{
					logger.info("Incomming job has higher priority. overwrite the existing makeup job");
					missedJobContext = incomingJobContext;
				}
			}
		}

		return missedJobContext;
	}
}
