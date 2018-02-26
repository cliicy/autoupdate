package com.ca.arcflash.webservice.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;

public class JobMonitorService {
	private static final Logger logger = Logger.getLogger(JobMonitorService.class);
	
	private static JobMonitorService jms = new JobMonitorService();

	private JobMonitorService(){}
	
	public static JobMonitorService getInstance() {
		return jms;
	}	
	private final Map<String, Map<String, Map<Long, JobMonitor>>> vmJobMonitorMap = new Hashtable<String, Map<String, Map<Long, JobMonitor>>>();
	private final Hashtable<String, Map<Long, JobMonitor>> jobTypeMapToJobIdJobMonitorMap = new Hashtable<String, Map<Long, JobMonitor>>();

	public JobMonitor getJobMonitor(String jobType, Long jobId) {
		if (jobTypeMapToJobIdJobMonitorMap.get(jobType) == null) {
			return null;
		}

		JobMonitor jm = jobTypeMapToJobIdJobMonitorMap.get(jobType).get(jobId);
		if (isValidJobMonitor(jm))
			return jm;
		else
			return null;
	}

	public void cleanJobMonitor() {
		jobTypeMapToJobIdJobMonitorMap.clear();
	}

	public Hashtable<String, Map<Long, JobMonitor>> getJobMonitorMap() {
		return jobTypeMapToJobIdJobMonitorMap;
	}

	public synchronized JobMonitor[] getAllJobMonitors() {
		Map<String, Map<Long, JobMonitor>> jobMonitorMap = getJobMonitorMap();
		if (jobMonitorMap == null) {
			return null;
		}

		List<JobMonitor> jobMonitorList = new ArrayList<JobMonitor>();
		for (Map<Long, JobMonitor> jobMonitors : jobMonitorMap.values()) {
			for (JobMonitor jm : jobMonitors.values()) {
				if (isValidJobMonitor(jm))
					jobMonitorList.add(jm);
			}
		}
		return jobMonitorList.toArray(new JobMonitor[0]);
	}

	private JobMonitor getJobMonitorInternal(String jobType, Long jobId, boolean addForNotExist) {
		Map<Long, JobMonitor> jobMonitorMap = jobTypeMapToJobIdJobMonitorMap.get(jobType);
		JobMonitor jJM = null;
		if (jobMonitorMap == null && addForNotExist) {
			jobMonitorMap = new Hashtable<Long, JobMonitor>();
			jJM = new JobMonitor(jobId);
			jobMonitorMap.put(jobId, jJM);
			jobTypeMapToJobIdJobMonitorMap.put(jobType, jobMonitorMap);
		} else {
			jJM = jobMonitorMap.get(jobId);
			if (jJM == null) {
				jJM = new JobMonitor(jobId);
				jobMonitorMap.put(jobId, jJM);
				jobTypeMapToJobIdJobMonitorMap.put(jobType, jobMonitorMap);
			}
		}

		return jJM;
	}

	public synchronized JobMonitor getJobMonitorInternal(String jobType, Long jobId, long productType, String vmInstanceUUID, boolean addForNotExist, long startTime) {
		JobMonitor jobMonitor = getJobMonitorInternal(jobType, jobId, addForNotExist);
		jobMonitor.setBackupStartTime(startTime);
		addJobMonitorToVMJobMonitorIfNeed(jobType, jobId, productType, vmInstanceUUID, jobMonitor);
		return jobMonitor;
	}

	public synchronized void removeJobMonitor(String jobType, Long jobId) {
		// since only job id is unique, job type may not correct at the
		// beginning of the jobtype		
		Iterator<Map.Entry<String, Map<Long, JobMonitor>>> iterJobMonitorMap = jobTypeMapToJobIdJobMonitorMap.entrySet().iterator();
		while (iterJobMonitorMap.hasNext()) {
			Map.Entry<String, Map<Long, JobMonitor>> jobMonitorMap = iterJobMonitorMap.next();
			jobMonitorMap.getValue().remove(jobId);
			if (jobMonitorMap.getValue().isEmpty())
				iterJobMonitorMap.remove();
		}

	}

	public synchronized JobMonitor getJobMonitorInternal(String jobType, boolean addForNotExist) {
		Map<Long, JobMonitor> jobMonitorMap = jobTypeMapToJobIdJobMonitorMap.get(jobType);
		if (jobMonitorMap != null && !addForNotExist && !jobMonitorMap.values().isEmpty()) {
			return jobMonitorMap.values().toArray(new JobMonitor[0])[0];
		}
		return null;
	}

	private void addJobMonitorToVMJobMonitorIfNeed(String jobType, Long jobId, long productType, String vmInstanceUUID, JobMonitor jobMonitor) {
		if (productType == VSphereJobContext.JOB_LAUNCHER_VSPHERE && jobMonitor.getJobType() == Constants.AF_JOBTYPE_ARCHIVE_RESTORE) {
			addVMJobMonitor(vmInstanceUUID, jobType, jobMonitor);
		}
	}

	public boolean isValidJobMonitor(JobMonitor jm) {
		if (jm == null) {
			logger.debug("jJM is not valid");
			return false;
		}
		// For catalog, catalog don't know job type before it read the job
		// script
		if (jm.getJobType() == 0
				&& (jm.getJobPhase() == Constants.CATPROC_PHASE_VALIDATE_CATALOG_SCRIPT || jm.getJobPhase() == Constants.CATPROC_PHASE_PARSE_CATALOG_SCRIPT)) {
			logger.debug("jJM is not valid");
			return false;
		}
		return true;
	}

	public synchronized JobMonitor getVMJobMonitorByJobTypeAndJobId(String vmIndentification, String jobType, Long jobId) {
		Map<String, Map<Long, JobMonitor>> jobMonitorTypeMap = vmJobMonitorMap.get(vmIndentification);
		if (jobMonitorTypeMap == null) {
			return null;
		} else {
			Map<Long, JobMonitor> jobMonitorMap = jobMonitorTypeMap.get(jobType);
			if (jobMonitorMap == null) {
				return null;
			} else {
				JobMonitor jm = jobMonitorMap.get(jobId);
				if (isValidJobMonitor(jm))
					return jm;
				else
					return null;
			}
		}
	}

	public synchronized JobMonitor getVMJobMonitor(String jobType, Long jobId) {
		if (vmJobMonitorMap.size() == 0) {
			return null;
		} else {
			for (Map<String, Map<Long, JobMonitor>> oneVmMap : vmJobMonitorMap.values()) {
				if (oneVmMap.size() == 0 || oneVmMap.get(jobType).size() == 0) {
					continue;
				}
				for (Map<Long, JobMonitor> oneVm : oneVmMap.values()) {
					if (oneVm.containsKey(jobId)) {
						JobMonitor jm = oneVm.get(jobId);
						if (isValidJobMonitor(jm))
							return jm;
						else
							return null;
					}
				}
			}
		}
		return null;
	}

	// vsphere job monitor
	public synchronized JobMonitor getVMJobMonitorByJobTypeAndJobIdInternal(VSphereJobContext jobContext, String jobType, Long jobId) {
		Map<String, Map<Long, JobMonitor>> jobMonitorTypeMap = vmJobMonitorMap.get(jobContext.getExecuterInstanceUUID());
		Map<Long, JobMonitor> jobMonitorMap = null;
		JobMonitor jobMonitor = null;
		if (jobMonitorTypeMap == null) {
			jobMonitorTypeMap = new Hashtable<String, Map<Long, JobMonitor>>();
			jobMonitorMap = new Hashtable<Long, JobMonitor>();
			jobMonitor = new JobMonitor(-1);
			jobMonitor.setVmInstanceUUID(jobContext.getExecuterInstanceUUID());
			// 2016-01-25 recovery vm from a standalone agent, no job monitor on the agent UI and console UI
			// the reason is the agent is not the proxy, so it doesn't have VMBackupConfiguration to get planUUID
			// in in BaseVSphereJob.preprocess, it has already get the planUUID from console, and VSphereJobMonitorThread.updateJobMonitor can update it correctly
//			jobMonitor.setPlanUUID(
//				VSphereService.getInstance().getPlanUUIDByVMInstanceUUID(jobMonitor.getVmInstanceUUID()));  
			jobMonitorMap.put(jobId, jobMonitor);
			jobMonitorTypeMap.put(jobType, jobMonitorMap);
			vmJobMonitorMap.put(jobContext.getExecuterInstanceUUID(), jobMonitorTypeMap);
		} else {
			jobMonitorMap = jobMonitorTypeMap.get(jobType);
			if (jobMonitorMap == null) {
				jobMonitorMap = new Hashtable<Long, JobMonitor>();
				jobMonitor = new JobMonitor(-1);
				jobMonitor.setVmInstanceUUID(jobContext.getExecuterInstanceUUID());
//				jobMonitor.setPlanUUID(
//					VSphereService.getInstance().getPlanUUIDByVMInstanceUUID(jobMonitor.getVmInstanceUUID()));
				jobMonitorMap.put(jobId, jobMonitor);
				jobMonitorTypeMap.put(jobType, jobMonitorMap);
			} else {
				jobMonitor = jobMonitorMap.get(jobId);
				if (jobMonitor == null) {
					jobMonitor = new JobMonitor(-1);
					jobMonitor.setVmInstanceUUID(jobContext.getExecuterInstanceUUID());
//					jobMonitor.setPlanUUID(
//						VSphereService.getInstance().getPlanUUIDByVMInstanceUUID(jobMonitor.getVmInstanceUUID()));
					jobMonitorMap.put(jobId, jobMonitor);
				} else {
					jobMonitor.setVmInstanceUUID(jobContext.getExecuterInstanceUUID());
					addVMJobMonitorToOtherIfNeed(jobContext, jobMonitor, jobType, jobId);
					return jobMonitor;
				}
			}
		}
		addVMJobMonitorToOtherIfNeed(jobContext, jobMonitor, jobType, jobId);
		return jobMonitor;
	}

	private synchronized void addVMJobMonitorToOtherIfNeed(VSphereJobContext jobContext, JobMonitor jJM, String jobType, Long jobId) {
		if (jJM.getJobType() == Constants.AF_JOBTYPE_VM_RECOVERY) {			
			if (jobContext.getJobLauncher() == VSphereJobContext.JOB_LAUNCHER_D2D) {
				Map<Long, JobMonitor> jobMonitorMap = jobTypeMapToJobIdJobMonitorMap.get(jobType);
				if (jobMonitorMap == null) {
					jobMonitorMap = new Hashtable<Long, JobMonitor>();
					jobMonitorMap.put(jobId, jJM);
					jobTypeMapToJobIdJobMonitorMap.put(jobType, jobMonitorMap);
				} else {
					jobMonitorMap.put(jobId, jJM);
				}
			} else {				
				if (!jobContext.getExecuterInstanceUUID().equals(jobContext.getLauncherInstanceUUID())) {
					addVMJobMonitor(jobContext.getLauncherInstanceUUID(), jobType, jJM);
				}
			}
		} else if (jJM.getJobType() == Constants.AF_JOBTYPE_RESTORE) {
			if (jobContext.getJobLauncher() == VSphereJobContext.JOB_LAUNCHER_VSPHERE) {
				Map<Long, JobMonitor> jobMonitorMap = jobTypeMapToJobIdJobMonitorMap.get(jobType);
				if (jobMonitorMap == null) {
					jobMonitorMap = new Hashtable<Long, JobMonitor>();
					jobMonitorMap.put(jobId, jJM);
					jobTypeMapToJobIdJobMonitorMap.put(jobType, jobMonitorMap);
				} else {
					jobMonitorMap.put(jobId, jJM);
				}
			}
		}
	}

	private void addVMJobMonitor(String vmInstanceUUID, String jobType, JobMonitor jJM) {
		Map<String, Map<Long, JobMonitor>> jobMonitorTypeMap = vmJobMonitorMap.get(vmInstanceUUID);
		Map<Long, JobMonitor> jobMonitorMap = null;
		if (jobMonitorTypeMap == null) {
			jobMonitorTypeMap = new Hashtable<String, Map<Long, JobMonitor>>();
			jobMonitorMap = new Hashtable<Long, JobMonitor>();
			jJM.setVmInstanceUUID(vmInstanceUUID);
			jobMonitorMap.put(jJM.getJobId(), jJM);
			jobMonitorTypeMap.put(jobType, jobMonitorMap);
			vmJobMonitorMap.put(vmInstanceUUID, jobMonitorTypeMap);
		} else {
			jobMonitorMap = jobMonitorTypeMap.get(jobType);
			if (jobMonitorMap == null) {
				jobMonitorMap = new Hashtable<Long, JobMonitor>();
			}
			jJM.setVmInstanceUUID(vmInstanceUUID);
			jobMonitorMap.put(jJM.getJobId(), jJM);
			jobMonitorTypeMap.put(jobType, jobMonitorMap);
		}
	}

	public synchronized Map<String, Map<Long, JobMonitor>> getVMJobMonitorMap(String vmIndentification) {
		return vmJobMonitorMap.get(vmIndentification);
	}

	public synchronized Map<String, Map<String, Map<Long, JobMonitor>>> getVMJobMonitorMap() {
		return vmJobMonitorMap;
	}

	public synchronized Map<Long, JobMonitor> getVMJobMonitorMapByJobType(String vmIndentification, String jobType) {
		if (vmJobMonitorMap.get(vmIndentification) == null)
			return null;
		return vmJobMonitorMap.get(vmIndentification).get(jobType);
	}
	
	public synchronized void logJobMonitorMap()
	{
		for (Entry<String, Map<String, Map<Long, JobMonitor>>> monitorEntry : vmJobMonitorMap.entrySet())
		{			
			StringBuilder sb = new StringBuilder();			

			String vmInstanceUUID = monitorEntry.getKey();
			
			sb.append("VM [" + vmInstanceUUID + "] => {");
			
			Map<String, Map<Long, JobMonitor>> jobMonitorTypeMap = monitorEntry.getValue();
			for (Entry<String, Map<Long, JobMonitor>> jobIDEntry : jobMonitorTypeMap.entrySet())
			{
				String jobType = jobIDEntry.getKey();
				sb.append("JobType[" + jobType + "] => {jobID: ");
				
				Map<Long, JobMonitor> jobIDMap = jobIDEntry.getValue();
				for (Entry<Long, JobMonitor> idEntry : jobIDMap.entrySet())
				{
				    Long jobId = idEntry.getKey();
				    sb.append(jobId);
				    sb.append(",");
				}

				sb.append("}");
			}
			
			sb.append("}");
			
			logger.info(sb.toString());
		}
	}

	public synchronized void removeVMJobMonitor(String vmIndentification, String jobType, Long jobId) {
		if (vmJobMonitorMap.get(vmIndentification) == null)
			return;
	
		Map<String, Map<Long, JobMonitor>> jobMonitors = vmJobMonitorMap.get(vmIndentification);
		if (jobMonitors != null) {
			Iterator<Map<Long, JobMonitor>> jms = jobMonitors.values().iterator();
			while (jms.hasNext()) {
				Map<Long, JobMonitor> jm = jms.next();
				jm.remove(jobId.longValue());
				if (jm.isEmpty())
					jms.remove();
			}
		}
		if (jobMonitors != null && jobMonitors.get(jobType) != null && jobMonitors.get(jobType).isEmpty()) {
			jobMonitors.remove(jobType);
		}
	}

}
