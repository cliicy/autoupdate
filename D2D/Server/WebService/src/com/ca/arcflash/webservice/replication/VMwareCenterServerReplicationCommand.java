package com.ca.arcflash.webservice.replication;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.VMWareVirtualCenterReplicaRoot;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVMDetails;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.webservice.service.HAService;

public class VMwareCenterServerReplicationCommand extends VMWareBaseReplicationCommand {

	
	private static final long serialVersionUID = -8763884767238395488L;
	private static final Logger log = Logger.getLogger(VMwareESXReplicationCommand.class);
	@Override
	VMWareInfo getVMWareInfo(ReplicationDestination dest) {
		
		if(dest == null){
			log.error("Replication Destination is null!!!");
			return null;
		}
		
		List<DiskDestination> diskDestinations =  dest.getDiskDestinations();
		Collections.sort(diskDestinations, new Comparator<DiskDestination>() {
			@Override
			public int compare(DiskDestination d1, DiskDestination d2) {
				return d1.getDisk().getDiskNumber() - d2.getDisk().getDiskNumber();
			}
		});
		
		VMWareInfo vmInfo = new VMWareInfo();
		
		vmInfo.guestID = getGuestOSID();
		vmInfo.vmVersion = "";
		
		VMwareVirtualCenterStorage vCenter = (VMwareVirtualCenterStorage)dest;
		vmInfo.protocol = vCenter.getProtocol();
		vmInfo.port = vCenter.getPort();
		vmInfo.hostname = vCenter.getVirtualCenterHostName();
		vmInfo.username = vCenter.getVirtualCenterUserName();
		vmInfo.password = vCenter.getVirtualCenterPassword();
		vmInfo.esxHost = vCenter.getEsxName();
		vmInfo.dcName = vCenter.getDcName();
		vmInfo.vmName = vCenter.getVirtualMachineDisplayName();
	    vmInfo.memoryMB = vCenter.getMemorySizeInMB();
		vmInfo.cpuCount = vCenter.getVirtualMachineProcessorNumber();
		vmInfo.resourcePool = vCenter.getResourcePool()==""?null:vCenter.getResourcePool();
		vmInfo.resourcePoolmoref = vCenter.getResourcePoolRef()==""?null:vCenter.getResourcePoolRef();

		String vmDatastore = vCenter.getvmDatastore();
		if(StringUtil.isEmptyOrNull(vmDatastore)) {
			vmInfo.storage = diskDestinations.get(0).getStorage().getName();
		}
		else {
			vmInfo.storage =  vmDatastore; 
		}
		
		vmInfo.isProxyEnabled = vCenter.isProxyEnabled();
		
		return vmInfo;
		
	}
	
	@Override
	boolean saveVMUUIDToRepository(CAVMDetails vmDetails, String afguid,
									ReplicationJobScript jobScript,String latestSessGuid) throws Exception{
		
		
		VMwareVirtualCenterStorage vCenter = (VMwareVirtualCenterStorage)jobScript.getReplicationDestination().get(0);
		vCenter.setVirtualMachineDisplayName(vmDetails.getVmName());
		HAService.getInstance().updateRepJobScript(jobScript);
		
		ProductionServerRoot prodRoot = null;
		String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		try{
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afguid);
		}catch (Exception e) {}
		
		try{

			if(prodRoot == null){
				prodRoot = new ProductionServerRoot();
			}
			
			prodRoot.setProductionServerAFUID(afguid);
			prodRoot.setProductionServerHostname(getHostNameByUUId(afguid));
			
			VMWareVirtualCenterReplicaRoot vCenterRoot = new VMWareVirtualCenterReplicaRoot();
			vCenterRoot.setVmuuid(vmDetails.getUuid());
			
			vCenterRoot.setVmname(vmDetails.getVmName());
			
			long replicateTime = jobScript.getReplicateTime();
			vCenterRoot.setReplicaTime(replicateTime);
			List<DiskDestination> diskDestinations = jobScript.getReplicationDestination().get(0).getDiskDestinations();
			vCenterRoot.getDiskDestinations().clear();
			for (DiskDestination diskDestination : diskDestinations) {
				vCenterRoot.getDiskDestinations().add(diskDestination);
			}
			if(latestSessGuid != null)
				vCenterRoot.setLatestReplicatedSession(latestSessGuid);
			prodRoot.setReplicaRoot(vCenterRoot);
			if(replicateTime > 0)
				prodRoot.clearMostRecentReplic();
			RepositoryUtil.getInstance(xml).saveProductionServerRoot(prodRoot);
			
			return true;
			
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		
	}

	@Override
	void sendVMInfoToMonitor(ReplicationJobScript replicationJobScript,
							 String vmuuid,String vmname) throws Exception{
		String afGuid = replicationJobScript.getAFGuid();
		try {

			HeartBeatJobScript heartBeatJobScript = HAService.getInstance()
					.getHeartBeatJobScript(replicationJobScript.getAFGuid());
			if (heartBeatJobScript == null) {
				log.debug("HeartBeatJobScript is not configured!!!! Can not send repository.xml to monitor!!!!");
				return;
			}
			
			log.info("Send the VM repository info to the monitor.");
			ReplicationDestination dest =replicationJobScript.getReplicationDestination().get(0);
			VMwareVirtualCenterStorage vCenter = (VMwareVirtualCenterStorage)dest;
			vCenter.setVmUUID(vmuuid);
			vCenter.setVmName(vmname);
			HAService.getInstance().reportProductionServerRoot(replicationJobScript,"");
			setSendVMInfoToMonitorFlag(afGuid, true);

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	@Override
	CAVMDetails loadUUIDFromRepository(ReplicationJobScript jobScript, CAVirtualInfrastructureManager vmManager, boolean downloadFromDatastore) throws Exception{
		try {
			ReplicaRoot repRoot = loadProductionServerRoot(jobScript, vmManager, downloadFromDatastore);
			if (repRoot == null) {
				log.warn("Replica root of production server root from repository is empty.");
				return null;
			}
			VMWareVirtualCenterReplicaRoot vmwareRoot = (VMWareVirtualCenterReplicaRoot) repRoot;
			if (vmwareRoot != null) {
				CAVMDetails vmDetails = new CAVMDetails();
				vmDetails.setUuid(vmwareRoot.getVmuuid());
				vmDetails.setVmName(vmwareRoot.getVmname());
				return vmDetails;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	@Override
	protected ReplicaRoot loadProductionServerRootFromRepository(ReplicationJobScript jobScript) throws Exception {
		String afguid = jobScript.getAFGuid();
		try {
			String xml = CommonUtil.D2DInstallPath
					+ "Configuration\\repository.xml";
			ProductionServerRoot prodRoot = null;
			try{
				prodRoot = RepositoryUtil.getInstance(xml)
						.getProductionServerRoot(afguid);
			}catch (Exception e) {
				log.warn("Failed to get the production server root from repository.");
			}
			if (prodRoot == null) {
				log.warn("Production server root from repository is empty.");
				return null;
			}
			if (prodRoot.getReplicaRoot() == null) {
				log.warn("Replica root of production server root from repository is empty.");
				return null;
			}
			if (prodRoot.getReplicaRoot() instanceof VMWareVirtualCenterReplicaRoot) {
				// ...
				return prodRoot.getReplicaRoot();
			} else {
				log.warn("Replica root of production server root from repository is not VMWareVirtualCenterReplicaRoot.");
				return null;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	@Override
	VM_Info getVirtualMachine(CAVirtualInfrastructureManager vmwareOBJ,VMWareInfo vminfo) throws Exception{
		
		try {
			
			VM_Info result = HAService.getInstance().getVMFromVC(vmwareOBJ, vminfo.vmName);
			
			return result;
			
		} catch (Exception e) {
			
			log.error(e.getMessage(),e);
			
			throw e;
		
		}
		
	}

	@Override
	void updateReplicationAndFailoverScript(String afGuid,String vmname,String vminstanceuuid, String resPoolID, String resPoolName){
		
		ReplicationJobScript repJobScript = HAService.getInstance().getReplicationJobScript(afGuid);
		VMwareVirtualCenterStorage dest = (VMwareVirtualCenterStorage)repJobScript.getReplicationDestination().get(0);

		dest.setVirtualMachineDisplayName(vmname);
		dest.setVmName(vmname);
			
		dest.setVmUUID(vminstanceuuid);
		dest.setResourcePool(resPoolName);
		dest.setResourcePoolRef(resPoolID);
		
		HAService.getInstance().updateRepJobScript(repJobScript);
		
		FailoverJobScript failoverScript = HAService.getInstance().getFailoverJobScript(afGuid);
		VMwareVirtualCenter esx = (VMwareVirtualCenter)failoverScript.getFailoverMechanism().get(0);
		
		esx.setVirtualMachineDisplayName(vmname);
		esx.setVmname(vmname);
		esx.setUuid(vminstanceuuid);
		
		HAService.getInstance().setFailoverJobScript(failoverScript);
		
	}
	
	
}
