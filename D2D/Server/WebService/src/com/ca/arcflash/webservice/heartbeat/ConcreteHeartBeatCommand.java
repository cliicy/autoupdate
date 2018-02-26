package com.ca.arcflash.webservice.heartbeat;

import java.util.HashSet;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.webservice.MonitorWebClientManager;
import com.ca.arcflash.job.AFJob;
import com.ca.arcflash.job.HAJobStatus.Status;
import com.ca.arcflash.jobqueue.JobQueueFactory;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatCommand;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class ConcreteHeartBeatCommand extends HeartBeatCommand {

	private static final long serialVersionUID = 8181676104506321756L;
	private static final Logger log = Logger.getLogger(ConcreteHeartBeatCommand.class);
	private static final String COLDSTANDBY_HEARTJOB_HEARTBEAT_FAIL = "COLDSTANDBY_HEARTJOB_HEARTBEAT_FAIL";
	private long printActiveLogCount = 0;
	private long lastPrintLogTime = 0;
	
	private static final Set<String> heartbeatJobLocks = new HashSet<String>();
	
	public void runHeartBeat(HeartBeatJobScript jobScript) {
		String key = MonitorWebClientManager.getKey4Client(jobScript)+ jobScript.getAFGuid();
		try {
			synchronized (heartbeatJobLocks) {
				if (heartbeatJobLocks.contains(key)) {
					log.debug(String.format("Duplicated heartbeat procedure for %s exits.", key));
					return;
				}
				heartbeatJobLocks.add(key);
			}
		} catch (Exception e) {
			log.error("Fail to check heartbeat mutex lock.", e);
			return;
		}
		WebServiceClientProxy client = null;
		try {
			log.debug("Execute heart beat.");
			double perc = -1.0;
			int toRep = 0;
			if (!jobScript.getBackupToRPS()) {
				RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(jobScript.getAFGuid());
				long shrmemid = 0;
				long repTotalSize = 0;
				String[] toRepSessions = null;
				long repTransedSize = 0;
				synchronized (jobMonitor) {
					shrmemid = jobMonitor.getId();
					repTotalSize = jobMonitor.getRepTotalSize();
					repTransedSize = jobMonitor.getRepTransedSize();
					toRepSessions = jobMonitor.getToRepSessions();
				}

				if (shrmemid > 0 && repTotalSize > 0) {
					perc = (repTransedSize * 0.1) / repTotalSize;
				}
				toRep = toRepSessions == null ? 0 : toRepSessions.length;
			}
			client = MonitorWebClientManager.getMonitorWebClientProxy4HeartBeat(jobScript);
			client.getServiceV2().heartBeat(jobScript.getAFGuid(), perc, toRep, CommonUtil.isFailoverMode());
			changeJobStatus(jobScript, Status.Active);
			printActiveLogCount = 0;

		} catch (WebServiceException e) {
			log.debug(e.getMessage());
			try {
				printActiveLog(jobScript);
				changeJobStatus(jobScript, Status.Failed);
				revalidate(client, jobScript);
			} catch (Exception e1) {
				log.debug(e1.getMessage());
			}
		} catch (Exception e2) {
			log.debug(e2.getMessage());
		}finally {
			synchronized (heartbeatJobLocks) {
				heartbeatJobLocks.remove(key);
			}
		}
	}
	
	class HeartbeatRunnable implements Runnable {
		 ConcreteHeartBeatCommand command;
		 HeartBeatJobScript jobScript;
		 
		 public HeartbeatRunnable(ConcreteHeartBeatCommand cmd, HeartBeatJobScript script) {
			 command = cmd;
			 jobScript = script;
		 }
		 @Override
		 public void run() {
			command.runHeartBeat(jobScript); 
		 }
	 }

	@Override
	public void executeHeartBeat(HeartBeatJobScript jobScript) {
		Thread thread = new Thread(new HeartbeatRunnable(this, jobScript));
		thread.start();
	}

	private void changeJobStatus(HeartBeatJobScript jobScript, Status status) {
		synchronized(JobQueueFactory.getDefaultJobQueue()) {
			AFJob job = HAService.getInstance().getHeartBeatJob(
					jobScript.getAFGuid());
			if(job.getJobStatus().getStatus() != status
					&& job.getJobStatus().getStatus() != Status.Canceled) {
				job.getJobStatus().setStatus(status);
				HAService.getInstance().reStoreJob(job);
			}
		}
	}

	public NativeFacade getNativeFacade() {
		return BackupService.getInstance().getNativeFacade();
	}
	
	private void revalidate(WebServiceClientProxy client, HeartBeatJobScript jobScript){
		//Session Timeout, so revalidate
		String monitorUUID = MonitorWebClientManager.getMonitorUUID(jobScript);
		log.info("MonitorHostName: " + jobScript.getHeartBeatMonitorHostName());
		if(!StringUtil.isEmptyOrNull(monitorUUID)){
			try {
				client.getServiceV2().validateUserByUUID(monitorUUID);
			} catch (Exception e2) {
				String domainUsername = jobScript.getHeartBeatMonitorUserName();
				String username = HACommon.getUserFromUsername(domainUsername);
				String domain   = HACommon.getDomainFromUsername(domainUsername);
				String password = jobScript.getHeartBeatMonitorPassword();
				client.getServiceV2().validateUser(username, password, domain);
			}
			
		}
		//revalidate end
	}
	
	private void printActiveLog(HeartBeatJobScript jobScript){
		if(printActiveLogCount<10){
			String msg = WebServiceMessages.getResource(
					COLDSTANDBY_HEARTJOB_HEARTBEAT_FAIL, jobScript.getHeartBeatMonitorHostName());
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "", "", "" }, jobScript.getAFGuid());
			
			printActiveLogCount++;
			
			lastPrintLogTime = System.currentTimeMillis();
		}
		else{
			long fiveMins = 5*60*1000;
			if((System.currentTimeMillis()-lastPrintLogTime)>=fiveMins){
				String msg = WebServiceMessages.getResource(
						COLDSTANDBY_HEARTJOB_HEARTBEAT_FAIL, jobScript.getHeartBeatMonitorHostName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "", "", "" }, jobScript.getAFGuid());
				
				lastPrintLogTime = System.currentTimeMillis();
			}
		}
		
	}

}
