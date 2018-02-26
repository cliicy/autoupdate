package com.ca.arcflash.webservice.service.internal;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;

public class VMThreadPoolExecutor
{
	protected ThreadPoolExecutor hypervVmPool; // thread pool for Hyper-V
	protected ThreadPoolExecutor vmwareVmPool; // thread pool for VMware
	protected static Map<String, Integer> mapVmUuidToVmType = new HashMap<String, Integer>(); // to cache VM UUID and it's VM type	
	
	
	private static final Logger logger = Logger.getLogger(VMThreadPoolExecutor.class);
	
	public VMThreadPoolExecutor(int maxVMwareJob, int maxHyperVJob)
	{		
		vmwareVmPool = new ThreadPoolExecutor(maxVMwareJob, maxVMwareJob, 0L, TimeUnit.MILLISECONDS, createPriorityQueue(maxVMwareJob));
		hypervVmPool = new ThreadPoolExecutor(maxHyperVJob, maxHyperVJob, 0L, TimeUnit.MILLISECONDS, createPriorityQueue(maxHyperVJob));		
		
		logger.info("maxVMwareJob: " + maxVMwareJob + " maxHyperVJob: " + maxHyperVJob);
	}
	
	protected PriorityBlockingQueue<Runnable> createPriorityQueue(int maxJob) {
		
		PriorityBlockingQueue<Runnable> priorityQueue = new PriorityBlockingQueue<Runnable>(maxJob, new Comparator<Runnable>() {
			@Override
			public int compare(Runnable o1, Runnable o2) {
				
				if (o1 instanceof VSphereBaseBackupJobTask)
				{
					VSphereBaseBackupJobTask job1 = (VSphereBaseBackupJobTask)o1;
					VSphereBaseBackupJobTask job2 = (VSphereBaseBackupJobTask)o2;
					int p1 = job1.getJobPriority();
					int p2 = job2.getJobPriority();				
					if(p1 == p2) {
						Date t1 = job1.getSubmitTime();
						Date t2 = job2.getSubmitTime();
						int ret = t1.compareTo(t2);
						if(ret == 0) {
							return 0;
						} else {
							return ret > 0 ? 1 : -1;
						}
					} else {
						return p1 < p2 ? 1 : -1;
					}
				}
				else if (o1 instanceof VSphereBaseRestoreJobTask)
				{
					VSphereBaseRestoreJobTask job1 = (VSphereBaseRestoreJobTask)o1;
					VSphereBaseRestoreJobTask job2 = (VSphereBaseRestoreJobTask)o2;
					int p1 = job1.getJobPriority();
					int p2 = job2.getJobPriority();
					if(p1 == p2) {
						Date t1 = job1.getSubmitTime();
						Date t2 = job2.getSubmitTime();
						int ret = t1.compareTo(t2);
						if(ret == 0) {
							return 0;
						} else {
							return ret > 0 ? 1 : -1;
						}
					} else {
						return p1 < p2 ? 1 : -1;
					}
				}
				return 1;			
			}
		});
		
		return priorityQueue;
	}
	
	public static int getVmType(String vmUuid)
	{
		int result = 0;
		
		do
		{
			try
			{
				if (mapVmUuidToVmType.containsKey(vmUuid))
				{
					result = mapVmUuidToVmType.get(vmUuid);
					break;
				}
				
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(vmUuid);
				VMBackupConfiguration vmBackupConfig = VSphereService.getInstance().getVMBackupConfiguration(vm);
				result = vmBackupConfig.getBackupVM().getVmType();
				
				mapVmUuidToVmType.put(vmUuid, result);
				
			}
			catch (ServiceException e)
			{
				logger.error("Failed to get VMBackupConfiguration");
			}			
			
		}while(false);	
		
		return result;		
	}
	
	
	public  BlockingQueue<Runnable> getQueue(String vmUuid)
	{
		BlockingQueue<Runnable> result = null;	
		
		int vmType = getVmType(vmUuid);
		
		if (vmType == BackupVM.Type.VMware.ordinal() || vmType == BackupVM.Type.VMware_VApp.ordinal())
		{
			result = this.vmwareVmPool.getQueue();
		}
		else if (vmType == BackupVM.Type.HyperV.ordinal() || vmType == BackupVM.Type.HyperV_Cluster.ordinal())
		{
			result = this.hypervVmPool.getQueue();
		}		
		
		return result;		
	}
	
	// to get all the queues for read
	public ArrayList<Runnable> getQueuesForReadOnly()
	{
		ArrayList<Runnable> result = new ArrayList<Runnable>();
		try
		{
			result.addAll(vmwareVmPool.getQueue());
			result.addAll(hypervVmPool.getQueue());
		}
		catch (Exception e)
		{
			logger.error("Failed to get queues for read", e);
		}

		return result;
	}
	
	public  ThreadPoolExecutor getVmPool(String vmUuid)
	{
		ThreadPoolExecutor result = null;	
		
		int vmType = getVmType(vmUuid);
		
		if (vmType == BackupVM.Type.VMware.ordinal() || vmType == BackupVM.Type.VMware_VApp.ordinal())
		{
			result = this.vmwareVmPool;
		}
		else if (vmType == BackupVM.Type.HyperV.ordinal() || vmType == BackupVM.Type.HyperV_Cluster.ordinal())
		{
			result = this.hypervVmPool;
		}		
		
		return result;		
	}
	
	public void execute(Runnable task)
	{
		
		if (task instanceof VSphereBackupJobTask)
		{
			String vmUuid = ((VSphereBackupJobTask) task).getVMInstanceUUID();
			ThreadPoolExecutor vmPool = getVmPool(vmUuid);
			vmPool.execute(task);
			
			logger.info(String.format("VSphereBackupJobTask vmUuid = %s, vmType = %d", vmUuid, getVmType(vmUuid)));
		}
		else if (task instanceof VSphereRestoreJobTask)
		{
			String vmUuid = ((VSphereRestoreJobTask) task).getVMInstanceUUID();
			ThreadPoolExecutor vmPool = getVmPool(vmUuid);
			vmPool.execute(task);
			
			logger.info(String.format("VSphereRestoreJobTask vmUuid = %s, vmType = %d", vmUuid, getVmType(vmUuid)));
		}
		else if (task instanceof VSphereRestartBackupJobTask)
		{
			String vmUuid = ((VSphereRestartBackupJobTask) task).getVMInstanceUUID();
			ThreadPoolExecutor vmPool = getVmPool(vmUuid);
			vmPool.execute(task);
			
			logger.info(String.format("VSphereRestartBackupJobTask vmUuid = %s, vmType = %d", vmUuid, getVmType(vmUuid)));
		}
		else if (task instanceof VSphereRestartRestoreJobTask)
		{
			String vmUuid = ((VSphereRestartRestoreJobTask) task).getVMInstanceUUID();
			ThreadPoolExecutor vmPool = getVmPool(vmUuid);
			vmPool.execute(task);
			
			logger.info(String.format("VSphereRestartRestoreJobTask vmUuid = %s, vmType = %d", vmUuid, getVmType(vmUuid)));
		}
		else
		{
			logger.error("Unexpected task to execute: " + task.getClass().getCanonicalName());
		}
	}
	
	
	 public List<Runnable> shutdownNow()
	 {
		ArrayList<Runnable> result = new ArrayList<Runnable>();
		 
		if (this.vmwareVmPool != null)
		{
			result.addAll(vmwareVmPool.shutdownNow());
		}
		
		if (this.hypervVmPool != null)
		{
			result.addAll(hypervVmPool.shutdownNow());
		}		
		
		return result;
		 
	 }
	 
	 public void setMaxPoolSize(int maxVMwareJob, int maxHyperVJob)
	 {
		 int curPoolSize = vmwareVmPool.getCorePoolSize();
		 if (maxVMwareJob > 0 && curPoolSize != maxVMwareJob)
		 {
			 vmwareVmPool.setCorePoolSize(maxVMwareJob);
			 vmwareVmPool.setMaximumPoolSize(maxVMwareJob);
		 }
		 
		 curPoolSize = hypervVmPool.getCorePoolSize();
		 if (maxHyperVJob > 0 && curPoolSize != maxHyperVJob)
		 {
			 hypervVmPool.setCorePoolSize(maxHyperVJob);
			 hypervVmPool.setMaximumPoolSize(maxHyperVJob);	
		 }

	 }
	

}
