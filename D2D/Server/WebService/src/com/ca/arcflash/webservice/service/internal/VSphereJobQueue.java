package com.ca.arcflash.webservice.service.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.common.FlashSyncher;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupJob;
import com.ca.arcflash.webservice.data.vsphere.VMBackupJobList;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.BaseVSphereJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.VMRPSJobSubmitter;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.JobMonitorService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.VSphereJobQueueEmailAlert;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class VSphereJobQueue implements IVSphereJobQueue {
	
	private static final Logger logger = Logger.getLogger(VSphereJobQueue.class);
	public static final String VMJobQueueDirectory = CommonUtil.D2DHAInstallPath+"Configuration\\VMJobQueue";
	public static final String VMJobQueueFileName = "VMJobQueue.xml";
	protected Queue<VMBackupJob> vmJobQueue_Running;
	protected Hashtable<String, JobMonitor> waitingJobTable = new Hashtable<String,JobMonitor>();
	
	protected HashMap<String, Object> vmJobsWaitingAtRPS = new HashMap<String, Object>(); // vmUuid, jobId. Jobs waiting at RPS side 
	
	protected IEdgeD2DService proxy;
	protected static String d2dServerUUID = CommonService.getInstance().getNodeUUID();
	
	//@Deprecated protected ThreadPoolExecutor vmPool;  
	protected VMThreadPoolExecutor vmPoolEx; // contains two thread pools for VMware and Hyper-V
	
	private static final VSphereJobQueue jobQueue = new VSphereJobQueue();
	//@Deprecated protected static BlockingQueue<Runnable> priorityQueue;
	final Timer timer = new Timer();
	
	public static final int JOBQUEUE_VM_PRIORITY = 0;
	public static final int JOBQUEUE_VAPPCHILDVM_PRIORITY = 1;
	
	protected VSphereJobQueue(){
		initJobQueue();
	}
	
	public static VSphereJobQueue getInstance(){
		return jobQueue;
	}
	
	public void initJobQueue(){
		vmJobQueue_Running = new LinkedList<VMBackupJob>();
		
//		int maxJob = this.getMaxJobCount();
//		priorityQueue = createPriorityQueue(maxJob);		
//		vmPool = new ThreadPoolExecutor(maxJob, maxJob, 0L, TimeUnit.MILLISECONDS, priorityQueue);
		
		vmPoolEx = new VMThreadPoolExecutor(getMaxVMwareJobCount(), getMaxHyperVJobCount());
		
		timer.schedule(new SyncStatusTask(), 1000);
	}
	
//	protected void createPriorityQueue(int maxJob) {
//		priorityQueue = new PriorityBlockingQueue<Runnable>(maxJob, new Comparator<Runnable>() {
//			@Override
//			public int compare(Runnable o1, Runnable o2) {
//				VSphereBaseBackupJobTask job1 = (VSphereBaseBackupJobTask)o1;
//				VSphereBaseBackupJobTask job2 = (VSphereBaseBackupJobTask)o2;
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
	
	public void destroy() {
		try {
			if (timer != null)
				timer.cancel();
			logger.debug("VSphereJobQueue timer has been cancelled");
			
			vmPoolEx.shutdownNow();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
//	@Deprecated
//	protected int getMaxJobCount(){
//		// vSphereMaxJobNum has been initialed to above zero in ContextListener.java
//		int maxJob = ServiceContext.getInstance().getvSphereMaxJobNum();
//		maxJob = maxJob > 0? maxJob:10;
//		return maxJob;
//	}
	
	protected int getMaxVMwareJobCount(){
		// vSphereMaxJobNum has been initialed to above zero in ContextListener.java
		int maxJob = ServiceContext.getInstance().getVmwareMaxJobNum();
		maxJob = maxJob > 0? maxJob:4;
		return maxJob;
	}
	
	protected int getMaxHyperVJobCount(){
		// vSphereMaxJobNum has been initialed to above zero in ContextListener.java
		int maxJob = ServiceContext.getInstance().getHypervMaxJobNum();
		maxJob = maxJob > 0? maxJob:10;
		return maxJob;
		}
	
	public int getMaxJobCount(int vmType)
	{
		int result = 0;
		
		if (vmType == BackupVM.Type.VMware.ordinal() || vmType == BackupVM.Type.VMware_VApp.ordinal())
		{
			result = getMaxVMwareJobCount();
		}
		else if (vmType == BackupVM.Type.HyperV.ordinal() || vmType == BackupVM.Type.HyperV_Cluster.ordinal())
		{
			result = getMaxHyperVJobCount();
		}
				
		return result;
	}
	
	private int getCurrentRunningJobCount(int vmType)
	{
		int runningJobCount = 0;
		
		synchronized (vmJobQueue_Running)
		{
		    for (VMBackupJob vmBackupJob : vmJobQueue_Running)
			{
			   if ((vmType == BackupVM.Type.VMware.ordinal() || 
			       vmType == BackupVM.Type.VMware_VApp.ordinal())
			       &&
				   (vmBackupJob.getVmType() == BackupVM.Type.VMware.ordinal() ||  
					vmBackupJob.getVmType() == BackupVM.Type.VMware_VApp.ordinal()))
			   {
				   runningJobCount++;
			   }
			   else if ((vmType == BackupVM.Type.HyperV.ordinal() || 
					     vmType == BackupVM.Type.HyperV_Cluster.ordinal())
					     && 
					     (vmBackupJob.getVmType() == BackupVM.Type.HyperV.ordinal() || 
					      vmBackupJob.getVmType() == BackupVM.Type.HyperV_Cluster.ordinal()))
			   {
					    runningJobCount++;
			   }				
		    }
		}
		
		logger.info("The current VM running job count:" + runningJobCount + "/" + getMaxJobCount(vmType) + " vmType = " + vmType);
		return runningJobCount;
	}
	
	public void addJobToRunningQueue(VMBackupJob job){
		synchronized (JobQueueAddingLock.addingToQueueLock)
		{
		    synchronized(vmJobQueue_Running)
		    {
			    if(job != null)
			    {
				    String msg = StringUtil.enFormat("Add the VMBackup job in the job queue. JobType[%d] JobName[%s]  VMName[%s] VMInstanceUUID[%s]",
							     job.getJobType(), job.getJobName(), job.getVm().getVmName(), job.getVm().getVmInstanceUUID());
				    logger.info(msg);
				    vmJobQueue_Running.add(job);
			    }
		    }
		    
		    JobQueueAddingLock.addingToQueueLock.notifyAll();
		    logger.info("JobQueueAddingLock.addingToQueueLock.notify()");
		}
	}
	
	//restart the d2d service, and put the running job in the queue
	public int addJobToWaitingQueue(VSphereRestartBackupJobTask targetRunnable){
		vmPoolEx.execute(targetRunnable);
		return 0;
	}
	
	public boolean isJobWaiting(String targetuuid){
		BlockingQueue<Runnable>  waitQueue = vmPoolEx.getQueue(targetuuid);
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereBackupJobTask){
				VSphereBackupJobTask tempTask = (VSphereBackupJobTask)runnable;
				if(tempTask.getVMInstanceUUID().equals(targetuuid)){
					return true;
				}
			}
		}
		return false;
	}
	
	public int addJobToWaitingQueue(VSphereBackupJobTask targetTask){
		vmPoolEx.setMaxPoolSize(getMaxVMwareJobCount(), getMaxHyperVJobCount());
		BlockingQueue<Runnable>  waitQueue = vmPoolEx.getQueue(targetTask.getVMInstanceUUID());
		String info = StringUtil.enFormat("The current VM running job count:[%d], waiting job count[%d]", vmJobQueue_Running.size(),waitQueue.size());
		logger.info(info);
		
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereBackupJobTask){
				VSphereBackupJobTask tempTask = (VSphereBackupJobTask)runnable;
				if(tempTask.getVMInstanceUUID().equals(targetTask.getVMInstanceUUID()) &&  
						!isJobRunning(tempTask.getVMInstanceUUID())){
					if(isNeedChangeJobType(tempTask.getJobType(), targetTask.getJobType())){
						String msg = StringUtil.enFormat("Change backup job type in the job queue for the vm[%s] from[%d] to [%d]", 
								tempTask.getVMInstanceUUID(), tempTask.getJobType(), targetTask.getJobType());
						logger.warn(msg);
						
						//print the active logs
						String activeLogs = WebServiceMessages.getResource("vsphereJobQueueMerge",
								ServiceUtils.backupType2String(targetTask.getJobType()),ServiceUtils.backupType2String(tempTask.getJobType()));
						printActiveLog(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { activeLogs,"", "", "", "" }, tempTask.getVMInstanceUUID());
						
						tempTask.setBackupJob(targetTask.getJobName(), targetTask.getJobType());
						//send the email alert
						VSphereJobQueueEmailAlert.getInstance().sendEmailOnJobQueue(tempTask.getVirtualMachine(), true, activeLogs, targetTask.getJobType());
						return 0;
					}
					else{
						String msg = String.format("The vm[%s] is in the backup job queue.", targetTask.getVMInstanceUUID());
						logger.info(msg);
						
						//print the active logs
						String activeLogs = WebServiceMessages.getResource("vsphereJobQueueSkip", ServiceUtils.backupType2String(tempTask.getJobType()),
								ServiceUtils.backupType2String(targetTask.getJobType()));
						printActiveLog(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { activeLogs,"", "", "", "" }, tempTask.getVMInstanceUUID());
						//send the email alert
						VSphereJobQueueEmailAlert.getInstance().sendEmailOnJobQueue(tempTask.getVirtualMachine(), false, activeLogs, targetTask.getJobType());
						return 0;
					}
				}
			}
		}
		
		// an existing backup job of the same VM is waiting on RPS
		if (isJobWaitingAtRPS(targetTask.getVMInstanceUUID()))
		{
			// do not put the new job into proxy thread pool, submit to RPS		
			if (VMRPSJobSubmitter.getInstance().submitBackupToRps(targetTask))
			{
				// RPS will handle the duplicated waiting jobs and submit only one back
				// then the existing thread of executeBackupJob will continue to run the returned job
				
				return 0;
			}
		}
		
		vmPoolEx.execute(targetTask);
		return 0;
	}
	
	private boolean isNeedChangeJobType(int currentType, int targetType){
		if(currentType == BackupType.Full){
			return false;
		}
		else if(currentType == BackupType.Resync){
			if(targetType ==  BackupType.Full)
				return true;
			else
				return false;
		}
		else if(currentType == BackupType.Incremental){
			if(targetType == BackupType.Full || targetType == BackupType.Resync){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}
	
	public void removeWaitingJobByDatastoreUUID(String datastoreUUID) {
		logger.info("Datastore " + datastoreUUID + " is stopped. " + "Will remove related waiting backup jobs");
		ArrayList<Runnable>  waitQueue = vmPoolEx.getQueuesForReadOnly();
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereBackupJobTask){
				VSphereBackupJobTask tempTask = (VSphereBackupJobTask)runnable;
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
			if(runnable instanceof VSphereBackupJobTask){
				VSphereBackupJobTask tempTask = (VSphereBackupJobTask)runnable;
				if(tempTask.getVMInstanceUUID().equals(targetuuid)){
					JobMonitor jobMonitor = new JobMonitor();
					jobMonitor.setJobPhase(Constants.PHASE_BACKUP_PHASE_WAITING);
					jobMonitor.setJobType(Constants.AF_JOBTYPE_VM_BACKUP);
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
		return true;//false;
	}
	
	public void removeJobFromRunningQueue(VMBackupJob job){
		synchronized(vmJobQueue_Running){
			String msg = StringUtil.enFormat("Remove the VMBackup job in the job queue. JobType[%d] JobName[%s]  VMName[%s] VMInstanceUUID[%s]",
					 job.getJobType(), job.getJobName(), job.getVm().getVmName(), job.getVm().getVmInstanceUUID());
			logger.info(msg);
			vmJobQueue_Running.remove(job);
		}
	}
	
	public int validateRunningCount(int vmType){	
		int result = 0;
		
		int currentRunningJobs = getCurrentRunningJobCount(vmType);
		int maxJobCount = getMaxJobCount(vmType);
		
		logger.info("currentRunningJobs " + currentRunningJobs + " maxJobCount " + maxJobCount);
		
		result = ( currentRunningJobs >= maxJobCount) ? 1 : 0;	
				
		return result;
	}
	
	public void logRunningJobQuueInfo()
	{	
		synchronized (vmJobQueue_Running) 
		{
			for (VMBackupJob vmBackupJob : vmJobQueue_Running) 
			{
				String log = StringUtil.enFormat("Job for %s is running, JobType[%d] JobName[%s]  VMName[%s]",
								vmBackupJob.getVm().getVmInstanceUUID(),
								vmBackupJob.getJobType(), vmBackupJob.getJobName(), vmBackupJob.getVm().getVmName());
				logger.info(log);
			}
		}
		
		JobMonitorService.getInstance().logJobMonitorMap();
		
	}
	
	protected boolean isJobRunning(String vmIndentification){
		synchronized (vmJobQueue_Running) {
			for (VMBackupJob vmBackupJob : vmJobQueue_Running) {
				if(vmBackupJob.getVm().getVmInstanceUUID().equals(vmIndentification))
					return true;
			}
		}
		
		logRunningJobQuueInfo();
		
		return false;
	}
	
	public boolean isJobRunning(String vmIndentification,String jobType){
		if(isJobRunning(vmIndentification))
			return true;
		
		return BaseVSphereJob.isJobRunning(vmIndentification, jobType);
	}
	
	public void saveJobQueueToFile(){
		ArrayList<Runnable>  waitQueue = vmPoolEx.getQueuesForReadOnly();
		VMBackupJobList backupJobList = new VMBackupJobList();
		List<VMBackupJob> jobList = backupJobList.getVMBackupJobList();
		for (Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereBackupJobTask){
				VSphereBackupJobTask tempRunnable = (VSphereBackupJobTask)runnable;
				jobList.add(tempRunnable.getVMBackupJob());
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
				JAXB.marshal(backupJobList, buffer);
				
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
	
	public void readJobQueueFromFile(){
		String fullFileName = VMJobQueueDirectory + "\\" + VMJobQueueFileName;
				
		File file = new File(fullFileName);
		if(!file.exists()){
			return;
		}
		FileInputStream inputStream = null;
		ByteArrayOutputStream stream = null;
		ByteArrayInputStream input = null;
		try {
			//read vmjobqueue.xml to get all the waiting job
			inputStream = new FileInputStream(fullFileName);
			byte[] inBytes = new byte[1024];
			int readNum = -1;
			stream = new ByteArrayOutputStream();
			while((readNum = inputStream.read(inBytes)) > 0) {
				stream.write(inBytes, 0, readNum);
			}
			
			byte[] objBytes = stream.toByteArray();
			input = new ByteArrayInputStream(objBytes);
			
			VMBackupJobList jobList = JAXB.unmarshal(input, VMBackupJobList.class);
			if(jobList == null || jobList.getVMBackupJobList()==null){
				return;
			}else{
				Queue<VMBackupJob> jobQueueTemp = new LinkedList<VMBackupJob>();
				
				for(VMBackupJob job : jobList.getVMBackupJobList()){
					jobQueueTemp.add(job);
				}
				
				runJobQueueAfterRestart(jobQueueTemp);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			ServiceUtils.closeResource(input);
			ServiceUtils.closeResource(stream);
			ServiceUtils.closeResource(inputStream);
		}
	}
	
	protected void runJobQueueAfterRestart(Queue<VMBackupJob> jobQueueTemp){
		for (VMBackupJob job : jobQueueTemp) {
			try{
				VSphereService.getInstance().backupVM(job.getJobType(), job.getJobName(), job.getVm());
			}catch (ServiceException e) {
				logger.error(e);
			}
		}
	}
	
	protected void printActiveLog(long level, long jobID, long resourceID, String[] msg,String vmIndentification){
		NativeFacade nativeFacade = VSphereService.getInstance().getNativeFacade();
		if(jobID != -1){
			nativeFacade.addVMLogActivityWithJobID(level, jobID,resourceID, msg, vmIndentification);
		}else {
			nativeFacade.addVMLogActivity(level,resourceID, msg, vmIndentification);
		}
	}

	protected synchronized IEdgeD2DService getEdgeConnection(){	
		if(proxy==null){
			try{
				D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
				EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(ApplicationType.vShpereManager);
				IEdgeD2DService proxy1 = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeD2DService.class);
				proxy1.validateUserByUUID(edgeRegInfo.getEdgeUUID());
				proxy=proxy1;
			}catch(Exception ex){
				logger.error(ex);
				proxy=null;
				return null;
			}
		}
		return proxy;
	}
	
	public synchronized ArrayList<Runnable> getWaitingJobQueue(){
		return vmPoolEx.getQueuesForReadOnly();
	}
	
	public Hashtable<String, JobMonitor> getWaitingJobTable() {
		return waitingJobTable;
	}
	
	public JobMonitor[] getWaitingJobTable(String vmInstanceUuid){
		
		logger.debug("getWaitingJobMonitor() start...");
		java.util.Hashtable<String ,JobMonitor> waitTable = null;

		waitTable = getWaitingJobTable();			
		if(waitTable.size() == 0){
			logger.debug("No waiting job monitor exists in waitQueue.");
			return null;
		}
		if(StringUtil.isEmptyOrNull(vmInstanceUuid)){
			logger.debug(" return all waiting job. return size is " + waitTable.size());
			return waitTable.values().toArray(new JobMonitor[0]);	
		}
		JobMonitor jobMonitor = waitTable.get(vmInstanceUuid);
		if(jobMonitor == null){
			logger.error(" no such vmInstanceUUID("+vmInstanceUuid+") waiting job");
			return null;
		}
        
		JobMonitor[] montors = new JobMonitor[1];
		montors[0] = jobMonitor;
		
		logger.debug(" getWaitingJobTable() end. JobMonitor:" + jobMonitor.toString());
		
		return montors;
	}
	
	@Override
	public long getWaitingJobID(String vmIdentification) {
		BlockingQueue<Runnable>  waitQueue = vmPoolEx.getQueue(vmIdentification);
		for(Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereBackupJobTask){
				VSphereBackupJobTask tempTask = (VSphereBackupJobTask)runnable;
				return tempTask.getBackupJob().getJobContext().getJobId();
			}
		}
		return 0;
	}

	public void addJobToWaitingAtRPS(String vmUuid, Object obj)
	{
		synchronized (vmJobsWaitingAtRPS)
		{
			if (!vmJobsWaitingAtRPS.containsKey(vmUuid))
			{
				vmJobsWaitingAtRPS.put(vmUuid, obj);
				logger.info("JobWaitingAtRPS:add: vmUuid = " + vmUuid);
			}
		}
	}
	
	public void removeJobFromWaitingAtRPS(String vmUuid)
	{
		synchronized (vmJobsWaitingAtRPS)
		{
			if (vmJobsWaitingAtRPS.containsKey(vmUuid))
			{
				vmJobsWaitingAtRPS.remove(vmUuid);
				logger.info("JobWaitingAtRPS:remove: vmUuid = " + vmUuid);
			}
		}
	}
	
	// return true if this vm's job is waiting at RPS
	public boolean isJobWaitingAtRPS(String vmUuid)
	{
		synchronized (vmJobsWaitingAtRPS)
		{
			return vmJobsWaitingAtRPS.containsKey(vmUuid);
		}
	}
	
	// return true if any of the job waiting at RPS has the same type
	public boolean isJobWaitingAtRPSByType(int vmType)
	{
		synchronized (vmJobsWaitingAtRPS)
		{
			for (String vmUuid : vmJobsWaitingAtRPS.keySet())
			{
				if (VMThreadPoolExecutor.getVmType(vmUuid) == vmType)
				{
					return true;
				}
			}			
		}
		
		return false;
	}
	
	protected class SyncStatusTask extends TimerTask{
		@Override
		public void run() {
			syncWaitingQueueToEdge();
			
			timer.schedule(new SyncStatusTask(), 3000);
		}
		
		

		void syncRPSSWaitingQueueToEdge()
		{
			logger.info("entered syncPRSWaitingQueueToEdge vmJobsWaitingAtRPS size " + vmJobsWaitingAtRPS.size());
			
			IEdgeD2DService proxy1=getEdgeConnection();
			if(proxy1==null)
			{
				logger.info("--- syncPRSWaitingQueueToEdge, proxy1==null");
				return;
			}

			List<JobMonitor> monitors = new ArrayList<JobMonitor>();
			List<String> uuids = new ArrayList<String>();
			
			synchronized (vmJobsWaitingAtRPS)
			{
				for (Entry<String, Object> entry: vmJobsWaitingAtRPS.entrySet())
				{
					String vmuuid = entry.getKey();
					VMBackupConfiguration configuration = (VMBackupConfiguration) entry.getValue();
					
					JobMonitor jobMonitor = new JobMonitor();
					jobMonitor.setJobPhase(Constants.PHASE_BACKUP_PHASE_WAITING);
					jobMonitor.setJobType(Constants.AF_JOBTYPE_VM_BACKUP);
					jobMonitor.setJobStatus(JobStatus.JOBSTATUS_WAITING);
					jobMonitor.setStartTime(new Date().getTime());
					jobMonitor.setVmInstanceUUID(vmuuid);
					jobMonitor.setD2dServerName(configuration.getBackupVM().getVmName());
					jobMonitor.setVmHostName(configuration.getBackupVM().getVmHostName());

					uuids.add(vmuuid);
					monitors.add(jobMonitor);

					logger.info(String.format("syncPRSWaitingQueueToEdge uuid %s, vm name %s, vm host name %s", 
									vmuuid, configuration.getBackupVM().getVmName(), configuration.getBackupVM().getVmHostName()));
				}
			}	
			
			try {
				if (uuids.size() > 0 && monitors.size() > 0)
				    proxy1.syncBackupJobsStatusAll(uuids, monitors);
			}catch (EdgeServiceFault e) {
				proxy=null;
				logger.error(e);
			}catch(Exception e){
				proxy=null;
				logger.error(e);
			}
		}
		
		void syncWaitingQueueToEdge(){
			ArrayList<Runnable>  waitQueue = vmPoolEx.getQueuesForReadOnly();
			if(waitQueue.size()<=0)
				return;
			logger.debug("Waiting Queue:"+waitQueue.size()+", Running Queue:"+vmJobQueue_Running.size());
			IEdgeD2DService proxy1=getEdgeConnection();
			if(proxy1==null){
			
				syncRPSSWaitingQueueToEdge();
				return;
			}
			waitingJobTable.clear();
			List<String> uuids = new ArrayList<String>();
			List<JobMonitor> monitors = new ArrayList<JobMonitor>();
			for (Runnable runnable : waitQueue) {
				if(runnable instanceof VSphereBackupJobTask){
					VSphereBackupJobTask tempTask = (VSphereBackupJobTask)runnable;

					String uuid = tempTask.getVMInstanceUUID();
					JobMonitor jobMonitor = new JobMonitor();
					jobMonitor.setJobPhase(Constants.PHASE_BACKUP_PHASE_WAITING);
					jobMonitor.setJobType(Constants.AF_JOBTYPE_VM_BACKUP);
					jobMonitor.setJobStatus(JobStatus.JOBSTATUS_WAITING);
					//jobMonitor.setJobMethod(((VSphereBackupJobTask) runnable).getJobType());
					jobMonitor.setJobMethod(((VSphereBackupJobTask) runnable).getVMBackupJob().getJobType());
					jobMonitor.setStartTime(new Date().getTime());
					jobMonitor.setVmInstanceUUID(uuid);
					VirtualMachine tempVM = tempTask.getVirtualMachine();
					if (tempVM != null) {
						jobMonitor.setD2dServerName(tempVM.getVmName());
					}
					waitingJobTable.put(uuid, jobMonitor);
					/*if(!waitingJobTable.containsKey(uuid)) {
						waitingJobTable.put(uuid, jobMonitor);
					}*/
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
