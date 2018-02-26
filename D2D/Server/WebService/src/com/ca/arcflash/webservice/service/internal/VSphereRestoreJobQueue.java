package com.ca.arcflash.webservice.service.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.common.FlashSyncher;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.vsphere.VMRestoreJob;
import com.ca.arcflash.webservice.data.vsphere.VMRestoreJobList;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class VSphereRestoreJobQueue extends VSphereJobQueue {
	private static final Logger logger = Logger.getLogger(VSphereRestoreJobQueue.class);
	public static final String VMJobQueueFileName = "VMRestoreJobQueue.xml";
	protected Queue<VMRestoreJob> vmJobQueue_Running;
	private static final VSphereRestoreJobQueue jobQueue = new VSphereRestoreJobQueue();
	public static VSphereRestoreJobQueue getInstance(){
		return jobQueue;
	}
	
	public void initJobQueue(){
		vmJobQueue_Running = new LinkedList<VMRestoreJob>();
//		int maxJob = getMaxJobCount();
//		
//		priorityQueue = createPriorityQueue(maxJob);
//		
//		vmPool = new ThreadPoolExecutor(maxJob, maxJob, 0L, TimeUnit.MILLISECONDS, priorityQueue);
		
		vmPoolEx = new VMThreadPoolExecutor(getMaxVMwareJobCount(), getMaxHyperVJobCount());
		
		timer.schedule(new SyncStatusTask(), 1000);
	}
	
//	protected void createPriorityQueue(int maxJob) {
//		priorityQueue = new PriorityBlockingQueue<Runnable>(maxJob, new Comparator<Runnable>() {
//			@Override
//			public int compare(Runnable o1, Runnable o2) {
//				VSphereBaseRestoreJobTask job1 = (VSphereBaseRestoreJobTask)o1;
//				VSphereBaseRestoreJobTask job2 = (VSphereBaseRestoreJobTask)o2;
//				int p1 = job1.getJobPriority();
//				int p2 = job2.getJobPriority();
//				if(p1 == p2) {
//					Date t1 = job1.getSubmitTime();
//					Date t2 = job2.getSubmitTime();
//					int ret = t1.compareTo(t2);
//					if(ret == 0) {
//						return 0;
//					} else {
//						return ret > 0 ? 1 : -1;
//					}
//				} else {
//					return p1 < p2 ? 1 : -1;
//				}
//			}
//		});
//	}
	
	protected boolean isJobRunning(String vmIndentification){
		synchronized (vmJobQueue_Running) {
			for (VMRestoreJob vmRestoreJob : vmJobQueue_Running) {
				if(vmRestoreJob.getVmInstanceUUID().equals(vmIndentification))
					return true;
			}
		}
		return false;
	}
	
	public void addJobToRunningQueue(VMRestoreJob job){
		synchronized(vmJobQueue_Running){
			if(job != null){
				logger.info("Add the VMBackup job in the job queue.");
				vmJobQueue_Running.add(job);
			}
		}
	}

	//restart the d2d service, and put the running job in the queue
	public int addJobToWaitingQueue(VSphereRestartRestoreJobTask targetRunnable){
		vmPoolEx.execute(targetRunnable);
		return 0;
	}
	
	public boolean isJobWaiting(String targetuuid){
		BlockingQueue<Runnable>  waitQueue = vmPoolEx.getQueue(targetuuid);
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereRestoreJobTask){
				VSphereRestoreJobTask tempTask = (VSphereRestoreJobTask)runnable;
				if(tempTask.getVMInstanceUUID().equals(targetuuid)){
					return true;
				}
			}
		}
		return false;
	}
	
	public int addJobToWaitingQueue(VSphereRestoreJobTask targetTask){
		vmPoolEx.setMaxPoolSize(getMaxVMwareJobCount(), getMaxHyperVJobCount());
		BlockingQueue<Runnable> waitQueue = vmPoolEx.getQueue(targetTask.getVMInstanceUUID());
		String info = StringUtil.enFormat("The current VM running job count:[%d], waiting job count[%d]", vmJobQueue_Running.size(),waitQueue.size());
		logger.info(info);
		
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereRestoreJobTask){
				VSphereRestoreJobTask tempTask = (VSphereRestoreJobTask)runnable;
				if(tempTask.getVMInstanceUUID().equals(targetTask.getVMInstanceUUID()) &&  
						!isJobRunning(tempTask.getVMInstanceUUID())){
					return 0;
				}
			}
		}
		vmPoolEx.execute(targetTask);
		return 0;
	}
	
	public void removeWaitingJobByDatastoreUUID(String datastoreUUID) {
		logger.info("Datastore " + datastoreUUID + " is stopped. " + "Will remove related waiting restore jobs");
		ArrayList<Runnable>  waitQueue = vmPoolEx.getQueuesForReadOnly();
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereRestoreJobTask){
				VSphereRestoreJobTask tempTask = (VSphereRestoreJobTask)runnable;
				String tempDatastoreUUID = VSphereService.getInstance().getRpsDataStoreUUID(tempTask.getVMInstanceUUID());
				if(tempDatastoreUUID == null && datastoreUUID.equalsIgnoreCase(tempDatastoreUUID)) {
					removeWaitingJob(tempTask.getVMInstanceUUID());			
				}
			}
		}
	}
	
	public boolean removeWaitingJob(String targetuuid){
		BlockingQueue<Runnable>  waitQueue = vmPoolEx.getQueue(targetuuid);
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereRestoreJobTask){
				VSphereRestoreJobTask tempTask = (VSphereRestoreJobTask)runnable;
				if(tempTask.getVMInstanceUUID().equals(targetuuid)){
					JobMonitor jobMonitor = new JobMonitor();
					jobMonitor.setJobPhase(Constants.PHASE_BACKUP_PHASE_WAITING);
					jobMonitor.setJobType(Constants.AF_JOBTYPE_VM_RECOVERY);
					jobMonitor.setD2dServerName(tempTask.getVirtualMachine().getVmName());
					jobMonitor.setFinished(true);
					String policyUUID = VSphereService.getInstance().getRpsPolicyUUID(targetuuid);
					if(FlashSyncher.getInstance().reportJobMonitor(jobMonitor, targetuuid, 
							policyUUID, d2dServerUUID) != 0) {
						logger.error("Failed to report VM job monitor for " + 
								targetuuid + " JobMonitor is " + jobMonitor);
					}
					
					if(waitQueue.remove(runnable)){
						logger.info("Remove a waiting job, jobtype:"+tempTask.getJobType()+", uuid:"+targetuuid);
						
						if(waitingJobTable.containsKey(targetuuid)) {
							waitingJobTable.remove(targetuuid);
						}
						
						//print the active logs
						String activeLogs = WebServiceMessages.getResource("vsphereJobQueueCancelWaiting", ServiceUtils.backupType2String(tempTask.getJobType()));
						printActiveLog(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { activeLogs,"", "", "", "" }, tempTask.getVMInstanceUUID());
						try{
							IEdgeD2DService proxy=getEdgeConnection();
							if(proxy!=null){
								proxy.syncBackupJobsStatus(targetuuid, jobMonitor);
							}
						} catch (EdgeServiceFault e) {
							proxy=null;
							logger.error(e);
						}
						return true;
					}else{
						//print the active logs
						String activeLogs = WebServiceMessages.getResource("vsphereJobQueueCancelWaiting_Fail_Run", ServiceUtils.backupType2String(tempTask.getJobType()));
						printActiveLog(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { activeLogs,"", "", "", "" }, tempTask.getVMInstanceUUID());
						return false;
					}
				}
			}
		}
		//print the active logs
		String activeLogs = WebServiceMessages.getResource("vsphereJobQueueCancelWaiting_Fail_None");
		printActiveLog(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { activeLogs,"", "", "", "" }, targetuuid);
		return false;
	}

	public void removeJobFromRunningQueue(VMRestoreJob job){
		synchronized(vmJobQueue_Running){
			logger.info("Remove the VMRestore job in the job queue.");
			vmJobQueue_Running.remove(job);
		}
	}
	
	@Override
	public long getWaitingJobID(String vmIdentification) {
		BlockingQueue<Runnable>  waitQueue = vmPoolEx.getQueue(vmIdentification);
		for(Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereRestoreJobTask){
				VSphereRestoreJobTask tempTask = (VSphereRestoreJobTask)runnable;
				return tempTask.getRestoreJob().getJobContext().getJobId();
			}
		}
		return 0;
	}
	
	public void saveJobQueueToFile(){
		ArrayList<Runnable>  waitQueue = vmPoolEx.getQueuesForReadOnly();
		VMRestoreJobList restoreJobList = new VMRestoreJobList();
		List<VMRestoreJob> jobList = restoreJobList.getVMRestoreJobList();
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereRestoreJobTask){
				VSphereRestoreJobTask tempRunnable = (VSphereRestoreJobTask)runnable;
				jobList.add(tempRunnable.getVMRestoreJob());
			}
		}
			
		if(!jobList.isEmpty()){
			try {
				File directory = new File(VMJobQueueDirectory);
				
				if(!directory.exists()) {
					directory.mkdir();
				}
				String fullFileName = VMJobQueueDirectory + "\\" + VMJobQueueFileName;
				File file = new File(fullFileName);
				FileOutputStream outStream = new FileOutputStream(file);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				JAXB.marshal(restoreJobList, buffer);
				
				byte[] jobBytes = buffer.toByteArray();
				
				outStream.write(jobBytes);
				outStream.flush();
				outStream.close();
			}
			catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}else{
			String fullFileName = VMJobQueueDirectory + "\\" + VMJobQueueFileName;
			
			File file = new File(fullFileName);
			
			if(file.exists()){
				file.delete();
			}
		}
		
		logger.info("Exit VSphereJobQueue.saveJobQueueToFile");
	}
	

	protected class SyncStatusTask extends TimerTask{
		@Override
		public void run() {
			syncWaitingQueueToEdge();
			
			timer.schedule(new SyncStatusTask(), 3000);
		}
		
		void syncWaitingQueueToEdge(){
			ArrayList<Runnable>  waitQueue = vmPoolEx.getQueuesForReadOnly();
			if(waitQueue.size()<=0)
				return;
			logger.debug("Waiting Queue:"+waitQueue.size()+", Running Queue:"+vmJobQueue_Running.size());
			IEdgeD2DService proxy1=getEdgeConnection();
			if(proxy1==null){
				return;
			}
			waitingJobTable.clear();
			List<String> uuids = new ArrayList<String>();
			List<JobMonitor> monitors = new ArrayList<JobMonitor>();
			for (Runnable runnable : waitQueue) {
				if(runnable instanceof VSphereRestoreJobTask){
					VSphereRestoreJobTask tempTask = (VSphereRestoreJobTask)runnable;

					String uuid = tempTask.getVMInstanceUUID();
					JobMonitor jobMonitor = new JobMonitor();
					jobMonitor.setJobPhase(Constants.PHASE_BACKUP_PHASE_WAITING);
					jobMonitor.setJobType(Constants.AF_JOBTYPE_VM_RECOVERY);
					jobMonitor.setJobStatus(JobStatus.JOBSTATUS_WAITING);
					jobMonitor.setStartTime(new Date().getTime());
					jobMonitor.setVmInstanceUUID(uuid);
					VirtualMachine tempVM = tempTask.getVirtualMachine();
					if (tempVM != null) {
						jobMonitor.setD2dServerName(tempVM.getVmName());
					}
//					if(!waitingJobTable.containsKey(uuid)) {
						waitingJobTable.put(uuid, jobMonitor);
//					}
					uuids.add(uuid);
					monitors.add(jobMonitor);
				}
			}
			try {
				proxy1.syncBackupJobsStatusAll(uuids, monitors);
			}catch (EdgeServiceFault e) {
				proxy=null;
				logger.error(e);
			}catch(Exception e){
				proxy=null;
				logger.error(e);
			}
		}
	}
}
