package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.assurerecovery.AssureRecoveryJobScript;
import com.ca.arcflash.assurerecovery.AssureRecoveryVerification;
import com.ca.arcflash.assurerecovery.BackupDestination;
import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.webservice.service.InstantVMService;

public class AssureRecoveryJob implements Job {

	private static final Logger logger = Logger.getLogger(AssureRecoveryJob.class);
	public static final String JobScript = "JobScript"; 

	private List<String> getUnverifiedSessions(AssureRecoveryJobScript arJobScript) {
		List<String> sessions = new ArrayList<String>();
		// BackupDestination backupDestination = arJobScript.getBackupDestination();
		// TODO
		return sessions;
	}
	
	private InstantVMConfig generateIvmScript(AssureRecoveryJobScript arJobScript, String session) {
		InstantVMConfig config = arJobScript.getInstantVMconfig();
		// Update unverified session information to job script.
		return config;
	}
	
	private boolean verifyArResult(AssureRecoveryJobScript arJobScript, String instantVmUUID) {
		// AssureRecoveryVerification verification = arJobScript.getVerification();
		// TODO
		return false;
	}
	
	private void saveARStatus(AssureRecoveryJobScript arJobScript, String session, String instantVmUUID, boolean passed) {
		// TODO	
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// Starting IVM AR here.
		// TODO AR policy, if running AR against multiple sessions simultaneously.
		logger.info("Starting AR job.");
		JobDataMap detail = context.getJobDetail().getJobDataMap();
		AssureRecoveryJobScript arJobScript = (AssureRecoveryJobScript)detail.get(JobScript);
		List<String> sessions = getUnverifiedSessions(arJobScript);
		for (String session : sessions) {
			InstantVMConfig config = generateIvmScript(arJobScript, session);
			String instantVmUUID = null;
			try {
				instantVmUUID = InstantVMService.getInstance().startInstantVM(config);
				boolean passed = verifyArResult(arJobScript, instantVmUUID);
				saveARStatus(arJobScript, session, instantVmUUID, passed);
				if (passed) {
					logger.info(String.format("Session %s passed AR test.", session));
				} else {
					logger.error(String.format("AR test failed on session %s.", session));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				try {
					InstantVMService.getInstance().stopInstantVM(instantVmUUID, true);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		logger.info("Finish AR job.");
	}

}
