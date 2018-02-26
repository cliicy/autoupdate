package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareInfrastructureEntityInfo;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.service.common.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;

public class VSPhereJobLauncherForGroupOrSingleVM
{
	private static enum InstanceType {
		INSTANCE_GROUP, // A VM group
		INSTANCE_VM,    // A stand alone VM
		INSTANCE_UNKNOWN,
	}
	
	private static final Logger logger = Logger.getLogger(VSPhereJobLauncherForGroupOrSingleVM.class);
	
	private String jobName, cfgFolderPath;
	private Scheduler scheduler;
	private JobDetail jobDetail;
	Trigger trigger;
	
	private VSphereBackupConfigurationXMLDAO backupConfigurationDAO = new VSphereBackupConfigurationXMLDAO();
	
	public VSPhereJobLauncherForGroupOrSingleVM(String jobName, Scheduler scheduler, JobDetail jobDetail, Trigger trigger)
	{
		this.jobName = jobName;
		this.scheduler = scheduler;
		this.jobDetail = jobDetail;
		this.trigger = trigger;
		cfgFolderPath = ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath();
	}
	
	public void launchJobForGroupOrSingleVM(VirtualMachine vm)
					throws SchedulerException, ServiceException
	{
		String nodePath = vm.getNodePath();
		
		// must delete
		//nodePath = "shuli02-vc60\\Physical ESXi\\155.35.80.55\\zhahe04-vApp";
		nodePath = "zhahe05-w2k8r2";
		// must delete

		String instUUID = vm.getVmInstanceUUID();

		// must delete
		//instUUID = "zhahe04-vApp";
		instUUID = "zhahe05-w2k8r2";
		// must delete

		if (nodePath == null && instUUID == null)
			throw new SchedulerException("Both nodePath and VM UUID are NULL!");

		InstanceType instType = InstanceType.INSTANCE_GROUP;

		if (nodePath == null)
			instType = InstanceType.INSTANCE_VM;
		else
		{
			String vmGroupName = nodePath.substring(nodePath.lastIndexOf("\\") + 1, nodePath.length());
			// vmUUID is not group name, which means it is UUID of VM, 
			// in this case user is going to back up a VM under group
			if (instUUID == null || vmGroupName.compareToIgnoreCase(instUUID) != 0 )
				instType = InstanceType.INSTANCE_VM;
			else
				instType = InstanceType.INSTANCE_GROUP;
		}

		if (instType == InstanceType.INSTANCE_VM)
		{
			scheduleJob(jobName, vm);
			return ;
		}

		if (ifVMListChanged(vm))
		{
			// notify Console here
		}

		VMBackupConfiguration configuration;
		BackupVM backupVM;

		String vmGroupName = nodePath.substring(nodePath.lastIndexOf("\\") + 1, nodePath.length());;

		String groupCfgFolderPath = cfgFolderPath + "\\" + vmGroupName;

		File configFolder = new File(groupCfgFolderPath);
		for (File file : configFolder.listFiles())
		{
			String filename = file.getName();
			String instUUIDOfVM = filename.substring(0, filename.indexOf(".xml"));
			// this is config file of VM group, we skip it.
			if (instUUIDOfVM.compareToIgnoreCase(vmGroupName) == 0)
				continue;

			VirtualMachine vmOfGroup = new VirtualMachine();
			vmOfGroup.setVmInstanceUUID(instUUIDOfVM);

			configuration = VSphereService.getInstance().getVMBackupConfiguration(vmOfGroup);

			backupVM = configuration.getBackupVM();

			vmOfGroup.setVmName(backupVM.getVmName());
			vmOfGroup.setVmInstanceUUID(backupVM.getInstanceUUID());
			vmOfGroup.setVmUUID(backupVM.getUuid());

			scheduleJob(String.format("%s(%s/%s)", jobName, backupVM.getEsxServerName(), backupVM.getVmName()), vmOfGroup);
		}
	}
	
	private boolean ifVMListChanged(VirtualMachine groupNode) throws ServiceException
	{
		VMBackupConfiguration configuration;
		
//		String nodePath = groupNode.getNodePath();

		// must delete
		String nodePath = "zhahe05-w2k8r2";
		// must delete
		
		String groupName = nodePath.substring(nodePath.lastIndexOf("\\") + 1, nodePath.length());;
		
		configuration = VSphereService.getInstance().getVMBackupConfiguration(groupNode);
		BackupVM backupVM = configuration.getBackupVM();

		// must delete
		//nodePath = "shuli02-vc60\\Physical ESXi\\155.35.80.55\\zhahe04-vApp";
		// must delete
		
		int vmType = backupVM.getVmType() ;
		if (vmType == BackupVM.Type.VMware.ordinal())
		{
			return checkIfVMListChangedOnESXGroup(configuration, nodePath);
		}
		else
		{
			return checkIfVMListChangedOnHyperV(configuration);
		}
	}
	
	private void scheduleJob(String newJobName, VirtualMachine vm) throws SchedulerException
	{	
		JobDetailImpl newJobDetail = (JobDetailImpl)this.jobDetail.clone();
		
		JobDataMap dataMap = newJobDetail.getJobDataMap();
		String newName = dataMap.get("jobName") + vm.getVmInstanceUUID();
		
		dataMap.put("jobName", newName);
		newJobDetail.getJobDataMap().put("vm", vm);
		
		TriggerBuilder trib = trigger.getTriggerBuilder();

		Trigger newTrigger = trib.build();
//		newTrigger.setName(newName);
		
		scheduler.scheduleJob(newJobDetail, newTrigger);		
	}
	

	private boolean checkIfVMListChangedOnESXGroup(VMBackupConfiguration configuration, String nodePath)
	{
		CAVirtualInfrastructureManager vmwareManager = null;
		try
		{
			BackupVM backupVM = configuration.getBackupVM();
			vmwareManager = 
		        CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
					backupVM.getEsxServerName(), backupVM.getEsxUsername(),
					backupVM.getEsxPassword(), backupVM.getProtocol(),
					true, backupVM.getPort());

			VMwareInfrastructureEntityInfo vmWareEntity = vmwareManager.getVmwareTreeRootEntity(null, true);
			if (vmWareEntity != null)
			{
				String [] nodesOnPath = nodePath.split("\\\\");
				return checkIfESXVMListChanged(vmWareEntity, nodesOnPath, 0);
			}
		} 
		catch (Exception e)
		{
			logger.error("Error on getVmwareTreeRootEntity.", e);
		}
		finally
		{
			if (vmwareManager != null)
			{
				try			
				{
					vmwareManager.close();
				}
			    catch (Exception e)
				{
					logger.error("CAVirtualInfrastructureManager.close() failed", e);
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	private boolean checkIfESXVMListChanged(VMwareInfrastructureEntityInfo nodeEntityInfo, 
					                        String[] nodesOnPath, int nodeIdx)
					throws Exception
	{
		if (nodesOnPath.length - 1 == nodeIdx)
		{
			if (nodesOnPath[nodeIdx].equalsIgnoreCase(nodeEntityInfo.getName()))
			    return checkIfESXVMListChanged(nodeEntityInfo, nodesOnPath[nodeIdx]);
			else
				return false;
		}

		if (!nodesOnPath[nodeIdx].equalsIgnoreCase(nodeEntityInfo.getName()))
		    return false;

		for(VMwareInfrastructureEntityInfo child : nodeEntityInfo.getChildren())
		{
			if (nodesOnPath[nodeIdx + 1].equalsIgnoreCase(child.getName()))
				return checkIfESXVMListChanged(child, nodesOnPath, nodeIdx + 1);
		}

		return false;
	}
	
	private boolean checkIfESXVMListChanged(VMwareInfrastructureEntityInfo nodeEntityInfo, String nodeName)
					throws Exception
	{
		if (!nodeName.equalsIgnoreCase(nodeEntityInfo.getName()))
				return false;

		String groupCfgFolderPath = cfgFolderPath + "\\" + nodeName;
		
		boolean bRet = false;

		List<String> vmUUIDList = new ArrayList<String>();
		//Here we check if new VMs were added into group
		for(VMwareInfrastructureEntityInfo child : nodeEntityInfo.getChildren())
		{
			if (child.getType() == null || !child.getType().equalsIgnoreCase("VirtualMachine") ||
				child.getVmInfo() == null) 
				continue;

			VM_Info detailInfo = child.getVmInfo();
			String vmInstUUID = detailInfo.getVMvmInstanceUUID();
			String vmCfgFilePath = groupCfgFolderPath + "\\" + vmInstUUID + ".xml";
			
			vmUUIDList.add(vmInstUUID);

			File cfgFile = new File(vmCfgFilePath);

			if (cfgFile.exists())
				continue;

			VirtualMachine newVMOfGroup = new VirtualMachine();
			newVMOfGroup.setVmInstanceUUID(vmInstUUID);
			newVMOfGroup.setVmName(detailInfo.getVMName());
			newVMOfGroup.setVmUUID(detailInfo.getVMUUID());
			newVMOfGroup.setVmVMX(detailInfo.getVMVMX());

			launchBackupForNewVMAndSaveCfgFile(nodeName, newVMOfGroup);
			bRet = true;
		}

		File cfgFile = new File(groupCfgFolderPath);

		// Here we check if some VMs were deleted from server
		for (File file : cfgFile.listFiles())
		{
			String filename = file.getName();
			String instUUIDOfVM = filename.substring(0, filename.indexOf(".xml"));
			
			// we have some vms' config, but we don't find these vm on server, 
			// these vm must have been deleted
			if (!vmUUIDList.contains(instUUIDOfVM))
			{
			}
		}

		return bRet;
	}
	
	private void launchBackupForNewVMAndSaveCfgFile(String nodeName, VirtualMachine newVM) throws Exception
	{
		VMBackupConfiguration configuration = getConfigurationForNewVM(nodeName, newVM);

		VSphereBackupConfiguration backupConfig = backupConfigurationDAO.VMConfigToVSphereConfig(configuration);

		backupConfigurationDAO.saveVM(ServiceContext.getInstance()
						.getVsphereBackupConfigurationFolderPath() + "\\" + nodeName,
						backupConfig, configuration.getBackupVM());

		// this is a new VM, we backup it automatically
		scheduleJob(String.format("%s(%s)", jobName, newVM.getVmName()), newVM);		
	}

	private boolean checkIfVMListChangedOnHyperV(VMBackupConfiguration configuration)
	{
		long handle = 0;
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		try
		{
			BackupVM backupVM = configuration.getBackupVM();

			String serverName = backupVM.getEsxServerName();
			String user = backupVM.getEsxUsername();
			String pwd = backupVM.getEsxPassword();
			handle = facade.OpenHypervHandle(serverName, user, pwd);
			Map<String, String> vmMap = facade.GetHyperVVmList(handle);
			if (vmMap == null || vmMap.size() == 0)
				return false;

			String hpvCfgFolderPath = cfgFolderPath + "\\" + serverName;
			
			File configFolder = new File(hpvCfgFolderPath);

			File [] files = configFolder.listFiles();
			
			// Here we check if new VMs were added into group
			for (Entry<String, String> entry : vmMap.entrySet())
			{
				String vmGUID = entry.getKey();
				String vmName = entry.getValue();
				
				if (isItANewHyperVVM(files, vmGUID))
				{
					VirtualMachine newVMOfGroup = new VirtualMachine();
					newVMOfGroup.setVmInstanceUUID(vmGUID);
					newVMOfGroup.setVmName(vmName);

					launchBackupForNewVMAndSaveCfgFile(serverName, newVMOfGroup);
				}

				logger.info(String.format("HyperV server %s: %s, %s",
							backupVM.getEsxServerName(), vmGUID, vmName));
			}
			
			// Here we check if some VMs were deleted from server
			for (File file : files)
			{
				String filename = file.getName();
				String instUUIDOfVM = filename.substring(0, filename.indexOf(".xml"));
				
				if (!vmMap.containsKey(instUUIDOfVM))
				{
					
				}
			}
		}
		catch (Exception e)
		{
			String msg = e.getMessage();
			logger.error(e.getMessage());
			throw AxisFault.fromAxisFault(e.getMessage(),
							FlashServiceErrorCode.Common_ErrorOccursInService);
		} 
        finally
		{
			try
			{
				logger.debug("GetHyperVVmList() - end");
				facade.CloseHypervHandle(handle);
			} catch (ServiceException e)
			{
				logger.error("Failed to close hyperv manager handle."
								+ e.getMessage());
			}
		}
		
		return false;
	}
	
	private boolean isItANewHyperVVM(File [] cfgFiles, String newUUID)
	{
		for (File file : cfgFiles)
		{
			String filename = file.getName();
			String instUUIDOfVM = filename.substring(0, filename.indexOf(".xml"));
			if (instUUIDOfVM.compareToIgnoreCase(newUUID) == 0)
				return false;
		}
		
		return true;
	}

	public static VMBackupConfiguration getConfigurationForNewVM(String nodePath, VirtualMachine newVM)
	{
		String groupName = nodePath.substring(nodePath.lastIndexOf("\\") + 1, nodePath.length());

		String groupCfgFolder = ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath() + "\\" + groupName;		

		VMBackupConfiguration configuration = null;

		File cfgFile = new File(groupCfgFolder);
		if (!cfgFile.exists())
			return null;

		for (File file : cfgFile.listFiles())
		{
			String filename = file.getName();
			String vmInstUUID = filename.substring(0, filename.indexOf(".xml"));
			
			VirtualMachine vm = new VirtualMachine();

			vm.setVmInstanceUUID(vmInstUUID);

			try
			{
				configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);

				BackupVM backupVM = configuration.getBackupVM();
				backupVM.setVmName(newVM.getVmName());
				backupVM.setInstanceUUID(newVM.getVmInstanceUUID());
				backupVM.setUuid(newVM.getVmUUID());
				backupVM.setVmVMX(newVM.getVmVMX());

				String serverName = backupVM.getEsxServerName();

				String backupDestOfNewVM = configuration.getDestination()
								+ "\\" + newVM.getVmName() + "@" + serverName;
				backupVM.setDestination(backupDestOfNewVM);
			} catch (ServiceException e)
			{
				logger.error("getConfigurationForNewVM Exception" + e.getMessage());
				e.printStackTrace();
			}
			break;
		}
		
		return configuration;
	}
	
	public static Map<String, List<String>> getUUIDOfAllVM(String groupName)
	{
		Map<String, List<String>> uuidOfAllVM = new HashMap<String, List<String>>();
		String cfgFolder = ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath();
		if (groupName != null)
			cfgFolder += "\\" + groupName;
		
		
		List<String> uuidOfVMOfGroup = new LinkedList<String>();
		File cfgFiles = new File(cfgFolder);
		for (File file : cfgFiles.listFiles())
		{
			if (file.isDirectory())
			{
				uuidOfAllVM.putAll(getUUIDOfAllVM(file.getName()));
				continue;
			}

			String filename = file.getName();
			String vmInstUUID = filename.substring(0, filename.indexOf(".xml"));
			uuidOfVMOfGroup.add(vmInstUUID);
		}
		
		uuidOfAllVM.put(groupName, uuidOfVMOfGroup);

		return uuidOfAllVM;
	}	
}
