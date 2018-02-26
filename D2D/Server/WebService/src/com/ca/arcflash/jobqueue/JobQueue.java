package com.ca.arcflash.jobqueue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.job.AFJob;
import com.ca.arcflash.job.JobFactory;
import com.ca.arcflash.job.alert.AlertJob;
import com.ca.arcflash.job.failover.FailoverJob;
import com.ca.arcflash.job.heartbeat.HeartBeatJob;
import com.ca.arcflash.job.replication.ReplicationJob;
import com.ca.arcflash.jobqueue.encrypt.Base64;
import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.alert.EmailModel;
import com.ca.arcflash.jobscript.base.JobScript;
import com.ca.arcflash.jobscript.base.JobType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.VSphereProxyServer;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.SharedFolder;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
/**
 * This class is not synchronized, the accessor needs to do it.
 * synchronized(JobQueueFactory.getDefaultJobQueue()){
 * ...
 * } 
 * @author gonro07
 *
 */
public class JobQueue {
	
	private Map<String, AFJob> jobMap = new HashMap<String, AFJob>();
	
	private String location;

	private static Logger log = Logger.getLogger(JobQueue.class);
	private static final String JOB_FILENAME_EXTENSION = "-job.xml";
	private static final String HEARTBEAT_JOB_FILE_EXT = "-heartbeat" + JOB_FILENAME_EXTENSION;
	private static final String FAILOVER_JOB_FILE_EXT = "-failover" + JOB_FILENAME_EXTENSION;
	private static final String REPLICATION_JOB_FILE_EXT = "-replication" + JOB_FILENAME_EXTENSION;
	private static final String ALTER_JOB_FILE_EXT = "-alter" + JOB_FILENAME_EXTENSION;

	/**
	 * Load the jobs from job repository and start the scheduler, but jobs are not scheduled actually
	*/
	public JobQueue(String location) {
		this.location = location;

		load();

	}
/**
 * It has not scheduled the jobs in job repository
 */
	private void load() {
		try {
			File queueLocation = new File(getLocation());
			
			if(!queueLocation.exists()){
				queueLocation.mkdirs();
			}
			
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(JOB_FILENAME_EXTENSION)) {
						return true;
					}
					return false;
				}

			};

			for (File file : queueLocation.listFiles(filter)) {
				JAXBContext ctx = JAXBContext.newInstance(AFJob.class);
				AFJob job = (AFJob) ctx.createUnmarshaller().unmarshal(file);
				enOrDecrypt(job, false);
				if(job != null){
					String name = getCorrectJobName(job);
					jobMap.put(name, job);
				}
			}

		} catch (Exception e) {
			log.debug("Failed to load jobs.", e);
		}
	}

	private void store(AFJob job) {
		FileOutputStream fos = null;
		try {
			
			File queueLocation = new File(getLocation());
			if(!queueLocation.exists()){
				queueLocation.mkdirs();
			}
			job = cloneAFJob(job);
			enOrDecrypt(job, true);
			String name = getCorrectJobName(job);
			fos = new FileOutputStream(new File(queueLocation, name));
			
			JAXB.marshal(job, fos);
			
			fos.close();

		} catch (Exception e) {
			log.debug("Failed to store job.", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.debug(e.getMessage());
				}
			}
		}
	}
	
/**
 * delete the jobID.job file from job repository
 * @param job
 */
	private void delete(AFJob job) {
		try {
			File queueLocation = new File(getLocation());
			if(!queueLocation.exists()){
				queueLocation.mkdirs();
			}
			String name = getCorrectJobName(job);
			File file = new File(queueLocation, name);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			log.debug("Failed to store job.", e);
		}
	}

	public String getLocation() {
		return location;
	}
	/**
	 * it will generate the default jobID, it is a random UUID
	 * @param jobScript
	 * @return
	 */
	public String add(JobScript jobScript) {
		AFJob job = JobFactory.create(jobScript);
		job.setJobID(jobScript.getAFGuid());
		String name = getCorrectJobName(job);
		jobMap.put(name, job);
		store(job);
		return job.getJobID();
	}
	/**
	 * after chagne the state of a job, invoke this method to store the Job.
	 * @param job
	 * @return false for failure
	 */
	public boolean reStoreJob(AFJob job){
		AFJob job1 = jobMap.get(getCorrectJobName(job));
		if(job1 == null || job1 != job) 
			return false;
		store(job1);
		return true;
	}
	
	//store the job directly
	public boolean storeJob(AFJob job){
		if(job == null)
			return false;
		store(job);
		return true;
	}
/**
 * the job will unscheduled, and its *.job file will also deleted
 * @param jobID
 */
	private void remove(String jobID) {
		AFJob job = findByJobID(jobID);
		job.unschedule();
		jobMap.remove(jobID.toString());
		delete(job);
	}

	public void remove(Collection<AFJob> jobs) {
		for (AFJob job : jobs) {
			this.remove(getCorrectJobName(job));
		}
	}

	public void remove(AFJob job) {
		this.remove(getCorrectJobName(job));
	}

	public Collection<AFJob> findAll() {
		return jobMap.values();
	}

	private AFJob findByJobID(String jobID) {
		return jobMap.get(jobID);
	}

	public AFJob findFailoverJobByID(String jobID) {
		return jobMap.get(jobID + FAILOVER_JOB_FILE_EXT);
	}
	
	public AFJob findReplicationJobByID(String jobID) {
		return jobMap.get(jobID + REPLICATION_JOB_FILE_EXT);
	}
	
	public AFJob findHeartBeatJobByID(String jobID) {
		return jobMap.get(jobID + HEARTBEAT_JOB_FILE_EXT);
	}
	
	public Collection<AFJob> findByJobType(JobType type) {
		Collection<AFJob> result = new ArrayList<AFJob>();
		for (AFJob job : jobMap.values()) {
			if (job.getJobScript().getJobType() == type) {
				result.add(job);
			}
		}
		return result;
	}

	public synchronized void shutdown() {
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			if (scheduler.isStarted()) {
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void enOrDecrypt(AFJob job, boolean encrypt){
		try {
			if(job instanceof HeartBeatJob) {
				HeartBeatJob heartJob = (HeartBeatJob)job;
				HeartBeatJobScript script = heartJob.getJobScript();
				if(script != null) {
					String psw = script.getHeartBeatMonitorPassword();
					script.setHeartBeatMonitorPassword(enOrDecryptStr(psw, encrypt));
				}
			}
			else if(job instanceof ReplicationJob) {
				ReplicationJob replicJob = (ReplicationJob)job;
				if(replicJob.getJobScript() == null)
					return;
				
				List<ReplicationDestination> destList = replicJob.getJobScript().getReplicationDestination();
				for (int i = 0, count = destList == null ? 0 : destList.size(); i < count; i++) {
					ReplicationDestination dest = destList.get(i);
					if(dest instanceof VMwareESXStorage) {
						VMwareESXStorage esxDest = (VMwareESXStorage)dest;
						String pwd = esxDest.getESXPassword();
						esxDest.setESXPassword(enOrDecryptStr(pwd, encrypt));
						
						pwd = esxDest.getHAMonitorPassword();
						esxDest.setHAMonitorPassword(enOrDecryptStr(pwd, encrypt));
						
						pwd = esxDest.getPassword();
						esxDest.setPassword(enOrDecryptStr(pwd, encrypt));
					}
					else if(dest instanceof VMwareVirtualCenterStorage) {
						VMwareVirtualCenterStorage vcDest = (VMwareVirtualCenterStorage) dest;
						String pwd = vcDest.getHAMonitorPassword();
						vcDest.setHAMonitorPassword(enOrDecryptStr(pwd, encrypt));
						
						pwd = vcDest.getVirtualCenterPassword();
						vcDest.setVirtualCenterPassword(enOrDecryptStr(pwd, encrypt));
					}
					else if(dest instanceof SharedFolder) {
						SharedFolder shareDest = (SharedFolder)dest;
						String pwd = shareDest.getPassword();
						shareDest.setPassword(enOrDecryptStr(pwd, encrypt));
					}
					else if(dest instanceof ARCFlashStorage) {
						ARCFlashStorage flashDest = (ARCFlashStorage)dest;
						String pwd = flashDest.getPassword();
						flashDest.setPassword(enOrDecryptStr(pwd, encrypt));
					}
				}

				String agentUUID = replicJob.getJobScript().getAgentUUID();
				if (!StringUtil.isEmptyOrNull(agentUUID))
					replicJob.getJobScript().setAgentUUID(enOrDecryptStr(agentUUID, encrypt));
			}
			else if(job instanceof FailoverJob) {
				FailoverJob failoverJob = (FailoverJob)job;
				FailoverJobScript jobscript = failoverJob.getJobScript();
				if(jobscript == null)
					return;
				
				VSphereProxyServer proxyServer = jobscript.getVSphereproxyServer();
				if(proxyServer != null) {
					String pwd = proxyServer.getVSphereProxyPassword();
					proxyServer.setVSphereProxyPassword(enOrDecryptStr(pwd, encrypt));

					String vSphereUUID = proxyServer.getVSphereUUID();
					if (!StringUtil.isEmptyOrNull(vSphereUUID))
							proxyServer.setVSphereUUID(enOrDecryptStr(vSphereUUID, encrypt));
				}
				
				List<Virtualization> virtList = jobscript.getFailoverMechanism();
				for (int i = 0, count = virtList == null ? 0 : virtList.size(); i < count; i++) {
					Virtualization virtual = virtList.get(i);
					if(virtual instanceof VMwareESX) {
						String pwd = ((VMwareESX)virtual).getPassword();
						((VMwareESX)virtual).setPassword(enOrDecryptStr(pwd, encrypt));
					}
					else if(virtual instanceof VMwareVirtualCenter) {
						String pwd = ((VMwareVirtualCenter)virtual).getPassword();
						((VMwareVirtualCenter)virtual).setPassword(enOrDecryptStr(pwd, encrypt));
					}
					else if(virtual instanceof HyperV) {
						String pwd = ((HyperV)virtual).getPassword();
						((HyperV)virtual).setPassword(enOrDecryptStr(pwd, encrypt));
					}
				}
				
				String converterUUID = jobscript.getConverterUUID();
				if (!StringUtil.isEmptyOrNull(converterUUID))
					jobscript.setConverterUUID(enOrDecryptStr(converterUUID, encrypt));

				String agentUUID = jobscript.getAgentUUID();
				if (!StringUtil.isEmptyOrNull(agentUUID))
					jobscript.setAgentUUID(enOrDecryptStr(agentUUID, encrypt));
			}
			else if(job instanceof AlertJob) {
				AlertJob aJob = (AlertJob)job;
				AlertJobScript jobScript = aJob.getJobScript();
				EmailModel emailModel = jobScript.getEmailModel();
				if(jobScript != null && emailModel != null) {
					emailModel.setMailPassword(enOrDecryptStr(emailModel.getMailPassword(), encrypt));
					
					emailModel.setProxyPassword(enOrDecryptStr(emailModel.getProxyPassword(), encrypt));
				}
			}
		
		} catch (Exception e) {
			log.error("Fails to encrypt the password", e);
		}
	}
	private AFJob cloneAFJob(AFJob originalJob) throws JAXBException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		JAXB.marshal(originalJob, buffer);
		
		ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
		JAXBContext ctx;
		ctx = JAXBContext.newInstance(AFJob.class);
		AFJob job = (AFJob) ctx.createUnmarshaller().unmarshal(input);
		return job;
		
	}
	private String enOrDecryptStr(String psw, boolean encrypt) {
		if(encrypt)
			return Base64.encode(psw);
		else
			return Base64.decode(psw);
	}
	
	private String getAFJobFileExtention(AFJob afjob){
		String className = afjob.getClass().getName();
		String fileExt = "";
		if(className.equals(FailoverJob.class.getName())){
			fileExt = FAILOVER_JOB_FILE_EXT;
		}else if(className.equals(HeartBeatJob.class.getName())){
			fileExt = HEARTBEAT_JOB_FILE_EXT; 
		}else if(className.equals(ReplicationJob.class.getName())){
			fileExt = REPLICATION_JOB_FILE_EXT;
		}
		else if(className.equals(AlertJob.class.getName())) {
			fileExt = ALTER_JOB_FILE_EXT;
		}
		return fileExt;
	}

	private String getCorrectJobName(AFJob job){
		
		String fileExt = getAFJobFileExtention(job);
		return job.getJobID() + fileExt;
		
	}
	
	public static void main(String[] args) {
		new JobQueue("g:\\news\\temp");
	}
	
}
