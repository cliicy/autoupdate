package com.ca.arcflash.webservice.service.internal;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.vcloudmanager.common.VAppSessionInfo;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudNetwork;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVApp;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVM;
import com.ca.arcflash.webservice.data.catalog.CatalogType;
import com.ca.arcflash.webservice.data.restore.DestTypeContratct;
import com.ca.arcflash.webservice.data.restore.RecoverVMOption;
import com.ca.arcflash.webservice.data.restore.RestoreADOption;
import com.ca.arcflash.webservice.data.restore.RestoreDiskDataStore;
import com.ca.arcflash.webservice.data.restore.RestoreExchangeGRTOption;
import com.ca.arcflash.webservice.data.restore.RestoreExchangeOption;
import com.ca.arcflash.webservice.data.restore.RestoreJob;
import com.ca.arcflash.webservice.data.restore.RestoreJobADItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobExchSubItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobItemEntry;
import com.ca.arcflash.webservice.data.restore.RestoreJobNode;
import com.ca.arcflash.webservice.data.restore.RestoreJobType;
import com.ca.arcflash.webservice.data.restore.RestoreSQLNewDest;
import com.ca.arcflash.webservice.data.vsphere.VMNetworkConfig;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JJobScriptADItem;
import com.ca.arcflash.webservice.jni.model.JJobScriptBackupVC;
import com.ca.arcflash.webservice.jni.model.JJobScriptDiskDataStore;
import com.ca.arcflash.webservice.jni.model.JJobScriptExchSubItem;
import com.ca.arcflash.webservice.jni.model.JJobScriptNode;
import com.ca.arcflash.webservice.jni.model.JJobScriptRecoverVMNode;
import com.ca.arcflash.webservice.jni.model.JJobScriptRestoreOptionAD;
import com.ca.arcflash.webservice.jni.model.JJobScriptRestoreOptionExch;
import com.ca.arcflash.webservice.jni.model.JJobScriptRestoreVolApp;
import com.ca.arcflash.webservice.jni.model.JJobScriptVMNetworkConfigInfo;
import com.ca.arcflash.webservice.jni.model.JJobScriptVolAppItem;
import com.ca.arcflash.webservice.scheduler.Constants;

public class RestoreJobConverter {
	private static int ASTIO_TRAVERSEDIR = 0x00000001;
	private static int FILEENTRY = 0x00000002;

	private static int QJDTO_R_ONCONFLICT_REPLACE = 0x00000001;
	private static int QJDTO_R_REPLACE_ACTIVE = 0x00000002;
	private static int QJDTO_R_ONCONFLICT_RENAME = 0x00000004;
	private static int QJDTO_R_CREATEWHOLEPATH = 0x00400000; // Create whole
	private static int QJDFS_BLI_NT_RESTORE_FILE = 5059;
	private static int QJDFS_BLI_NT_RESTORE_APP = 5141;
	private static int QJDFS_BLI_EXCH_GRT       = 5163;
	private static int QJDFS_BLI_AD_GRT 		= 5168;
	private static int OPTION_HYPERV_VM_R_REGISTER2CLUSTER = 0x00000001;    //register to cluster
	private static int OPTION_GENERATE_NEW_VM_ID = 0x00000002; //generate new instance vm id

	// path for
	// destination

	public JJobScript convert2JobScript(RestoreJob job, String hostname)
			throws UnknownHostException {
		if (job.getJobType() == 0) {
			job.setJobType(RestoreJobType.FileSystem);
		}

		if (job.getJobType() == RestoreJobType.FileSystem)
			return packageFileSystemJob(job, hostname);

		if (job.getJobType() == RestoreJobType.VSS_SQLServer)
			return packageSQLServerJob(job, hostname);

		if (job.getJobType() == RestoreJobType.VSS_Exchange)
			return packageExchangeJob(job, hostname);
		if (job.getJobType() == RestoreJobType.Recover_VM || job.getJobType() == RestoreJobType.Recover_VM_HYPERV || job.getJobType() == RestoreJobType.Recover_VMWARE_VAPP)
			return packageRecoverVMJob(job,hostname);

		if (job.getJobType() == RestoreJobType.GRT_Exchange)
			return packageExchangeGRTJob(job, hostname);
		
		if(job.getJobType() == RestoreJobType.VM_RESTORE_FILE_TO_ORIGINAL || job.getJobType() == RestoreJobType.VM_RESTORE_EXCHANGE_TO_ORIGINAL || job.getJobType() == RestoreJobType.VM_RESTORE_SQLSERVER_TO_ORIGINAL
				|| job.getJobType() == RestoreJobType.VM_RESTORE_FILE_TO_ALTER_VM || job.getJobType() == RestoreJobType.VM_RESTORE_EXCHANGE_TO_ALTER_VM || job.getJobType() == RestoreJobType.VM_RESTORE_SQLSERVER_TO_ALTER_VM)
			return packageVMRestoreFileToOriginalJob(job,hostname,job.getJobType());
		
		if(job.getJobType() == RestoreJobType.ActiveDirectory)
			return packageADJob(job, hostname);
		return null;
	}

	private JJobScript packageADJob(RestoreJob job, String hostname) {
		JJobScript result = new JJobScript();

		result.setUsJobType(1);
		setUserPwd(job, result);
		result.setRPSDataStoreDisplayName(job.getRpsDataStoreDisplayName());
		result.setRPSDataStoreName(job.getRpsDataStoreName());
		result.setRpsPolicyID(job.getRpsPolicy());
		if(job.getSrcRpsHost() != null)
			result.setRpsHostname(job.getSrcRpsHost().getRhostname());
		if (!StringUtil.isEmptyOrNull(job.getDestinationPath())) 
		{
			packageJobDest(job, result);
		}

		List<JJobScriptNode> nodeList = new ArrayList<JJobScriptNode>();
		for (RestoreJobNode node : job.getNodes()) {
			JJobScriptNode jJobScriptNode = new JJobScriptNode();
			jJobScriptNode.setPwszNodeName(hostname);
			jJobScriptNode.setUlSessNum(node.getSessionNumber());
			jJobScriptNode.setPwszSessPath(job.getSessionPath());
			jJobScriptNode.setPwszEncryptPasswordRestore(node.getEncryptPassword());
			
			List<JJobScriptRestoreVolApp> volumes = new ArrayList<JJobScriptRestoreVolApp>();
			for (RestoreJobItem item : node.getJobItems()) {
				JJobScriptRestoreVolApp volume = new JJobScriptRestoreVolApp();
				volume.setUlSubSessNum(item.getSubSessionNum());
				volume.setPwszPath("\\\\" + hostname.toLowerCase() + "\\"
						+ item.getPath());
				volume.setUlFileSystem(QJDFS_BLI_AD_GRT);				

				List<JJobScriptVolAppItem> entries = new ArrayList<JJobScriptVolAppItem>();
				if (item.getEntries() != null) {
					for (RestoreJobItemEntry entry : item.getEntries()) {
						JJobScriptVolAppItem volAppItem = new JJobScriptVolAppItem(entry.getPath(),
								ASTIO_TRAVERSEDIR);
						
						List<JJobScriptADItem> pADItemList = new ArrayList<JJobScriptADItem>();	
						if(entry.getAdItems()!=null){
							for (RestoreJobADItem adItem : entry.getAdItems())
							{
								JJobScriptADItem jADItem = new JJobScriptADItem();
								jADItem.setId(adItem.getId());
								jADItem.setAllChild(adItem.isAllChild());
								jADItem.setAllAttribute(adItem.isAllAttribute());
								if(adItem.getAttrNames()!=null){
									jADItem.setAttrNumber(adItem.getAttrNames().size());
									StringBuffer sbuf = new StringBuffer();
									for(String name : adItem.getAttrNames()){
										sbuf.append(name).append(JJobScriptADItem.attrSeparator);
									}
									sbuf.deleteCharAt(sbuf.length()-1);
									jADItem.setAttrNames(sbuf.toString());
								}
								pADItemList.add(jADItem);
							}
						}
						volAppItem.setuADItemNum(pADItemList.size());
						volAppItem.setpADItemList(pADItemList);
						
						entries.add(volAppItem);
					}
				}
				volume.setNVolItemAppComp(entries.size());
				volume.setPVolItemAppCompList(entries);
				
				// package AD restore option here
				RestoreADOption optionModel = job.getAdOption()==null? new RestoreADOption(): job.getAdOption();
				JJobScriptRestoreOptionAD option = new JJobScriptRestoreOptionAD();
					
				if(!optionModel.isSkipRenamedObject())
					option.setUlOptions(option.getUlOptions()|JJobScriptRestoreOptionAD.AD_RESTORE_OPTION_RENAMED_OBJECT);
				if(!optionModel.isSkipMovedObject())
					option.setUlOptions(option.getUlOptions()|JJobScriptRestoreOptionAD.AD_RESTORE_OPTION_MOVED_OBJECT);
				if(!optionModel.isSkipDeletedObject())
					option.setUlOptions(option.getUlOptions()|JJobScriptRestoreOptionAD.AD_RESTORE_OPTION_LOST_OBJECT);
				volume.setAdOption(option);
				
				volumes.add(volume);
			}

			jJobScriptNode.setNVolumeApp(volumes.size());
			jJobScriptNode.setPRestoreVolumeAppList(volumes);
			nodeList.add(jJobScriptNode);
		}
		result.setNNodeItems(nodeList.size());
		result.setPAFNodeList(nodeList);
		
		return result;
	}

	private JJobScript packageRecoverVMJob(RestoreJob job,String hostname){
		JJobScript result = new JJobScript();
		result.setUlJobAttribute(job.getJobLauncher());
		result.setLauncherInstanceUUID(job.getVmInstanceUUID());
		result.setUsJobType(job.getJobType());
		setUserPwd(job, result);
		result.setDwMasterJobId(job.getMasterJobId());
		result.setNNodeItems(1);
		result.setPwszDestPath(job.getDestinationPath());
		result.setFOptions(job.getRestoreType());
		packageRecoverVMOption(result,job);
		result.setRPSDataStoreDisplayName(job.getRpsDataStoreDisplayName());
		result.setRPSDataStoreName(job.getRpsDataStoreName());
		result.setRpsPolicyID(job.getRpsPolicy());
		if(job.getSrcRpsHost() != null) {
			result.setRpsHostname(job.getSrcRpsHost().getRhostname());
		}
		
		return result;
	}
	
	private JJobScript packageVMRestoreFileToOriginalJob(RestoreJob job,String hostname,int restoreType){
		JJobScript result = null;
		if(restoreType == RestoreJobType.VM_RESTORE_FILE_TO_ORIGINAL || restoreType == RestoreJobType.VM_RESTORE_FILE_TO_ALTER_VM){
			result = packageFileSystemJob(job,hostname);
		}else if (restoreType == RestoreJobType.VM_RESTORE_EXCHANGE_TO_ORIGINAL || restoreType == RestoreJobType.VM_RESTORE_EXCHANGE_TO_ALTER_VM){
			result = packageExchangeJob(job,hostname);
		}else if (restoreType == RestoreJobType.VM_RESTORE_SQLSERVER_TO_ORIGINAL || restoreType == RestoreJobType.VM_RESTORE_SQLSERVER_TO_ALTER_VM){
			result =  packageSQLServerJob(job, hostname);
		}
		result.setUsJobType(Constants.AF_JOBTYPE_VM_RESTORE_FILE_TO_ORIGINAL_OR_ALTERVM);
		packageRecoverVMOption(result,job);
		return result;
	}
	
	private void packageRecoverVMOption(JJobScript jobscript, RestoreJob job){
		RecoverVMOption recoverVMOption = job.getRecoverVMOption();
		if(recoverVMOption!=null){
			JJobScriptRecoverVMNode pRecoverVMNode = packageRecoverVMNode(job);
			if (null != pRecoverVMNode) {
				List<JJobScriptRecoverVMNode> pList = new ArrayList<JJobScriptRecoverVMNode>();
				pList.add(pRecoverVMNode);
				jobscript.setpRecoverVMNodeList(pList);
			}
		} 
	}
	
	private JJobScriptRecoverVMNode packageRecoverVMNode(RestoreJob job) {
		RecoverVMOption recoverVMOption = job.getRecoverVMOption();
		if(null != recoverVMOption){
			JJobScriptRecoverVMNode pRecoverVMNode = new JJobScriptRecoverVMNode();
			pRecoverVMNode.setJobId(job.getJobId());
			pRecoverVMNode.setEsxServerName(recoverVMOption.getEsxServerName());
			pRecoverVMNode.setOriginalLocation(recoverVMOption.isOriginalLocation()==true ? 1:0);
			pRecoverVMNode.setPoweronAfterRestore(recoverVMOption.isPowerOnAfterRestore()==true ? 1:0);
			pRecoverVMNode.setOverwriteExistingVM(recoverVMOption.isOverwriteExistingVM()==true ? 1:0);
			pRecoverVMNode.setGenerateNewVMID(recoverVMOption.isGenerateNewVMinstID()==true ? 1 : 0);
			pRecoverVMNode.setVmName(recoverVMOption.getVmName());
			pRecoverVMNode.setVmUsername(recoverVMOption.getVmUsername());
			pRecoverVMNode.setVmPassword(recoverVMOption.getVmPassword());
			pRecoverVMNode.setUlSessNum(recoverVMOption.getSessionNumber());
			pRecoverVMNode.setVmDataStore(recoverVMOption.getVmDataStore());
			pRecoverVMNode.setVmDataStoreId(recoverVMOption.getVmDataStoreId());
			pRecoverVMNode.setDiskDataStore(ConvertToJJobScriptDiskDataStore(recoverVMOption.getRestoreDiskDataStores()));
			pRecoverVMNode.setVMNetworkConfig(ConvertToJJobScriptVMNetworkConfigInfo(recoverVMOption.getVMNetworkConfig()));
			pRecoverVMNode.setVc(ConvertToJJobScriptBackupVC(recoverVMOption.getVc()));
			pRecoverVMNode.setPwszSessPath(job.getSessionPath());
			pRecoverVMNode.setVcName(recoverVMOption.getVcName());
			pRecoverVMNode.setVmDiskCount(recoverVMOption.getVmDiskCount());
			pRecoverVMNode.setPwszNodeName(recoverVMOption.getVmInstanceUUID());
			pRecoverVMNode.setEncryptionPassword(recoverVMOption.getEncryptPassword());
			pRecoverVMNode.setResourcePoolName(recoverVMOption.getResourcePoolName());
			if (recoverVMOption.isRegisterAsClusterHyperVVM()) {
				pRecoverVMNode.setFOptions(pRecoverVMNode.getFOptions()	| OPTION_HYPERV_VM_R_REGISTER2CLUSTER);
			}
			if (recoverVMOption.isGenerateNewVMinstID()){
				pRecoverVMNode.setFOptions(pRecoverVMNode.getFOptions() | OPTION_GENERATE_NEW_VM_ID);
			}
			
			pRecoverVMNode.setCpuCount(recoverVMOption.getCpuCount());
			pRecoverVMNode.setMemorySize(recoverVMOption.getMemorySize());
			pRecoverVMNode.setStoragePolicyId(recoverVMOption.getStoragePolicyId());
			pRecoverVMNode.setStoragePolicyName(recoverVMOption.getStoragePolicyName());
			pRecoverVMNode.setVmIdInVApp(recoverVMOption.getVmIdInVApp());
			
			List<RestoreJob> childJobList = job.getChildVMRestoreJobList();
			if (childJobList != null && childJobList.size() > 0) {
				pRecoverVMNode.setNetworkMappingInfo(convertToVAppSessionInfoString(job));
				List<JJobScriptRecoverVMNode> childVMNodeList = new ArrayList<JJobScriptRecoverVMNode>();
				for (RestoreJob childJob : childJobList) {
					JJobScriptRecoverVMNode childVMNode = packageRecoverVMNode(childJob);
					if (childVMNode != null) {
						childVMNodeList.add(childVMNode);
					}
				}
				pRecoverVMNode.setChildVMNodeList(childVMNodeList);
			}
			
			return pRecoverVMNode;
		} else {
			return null;
		}
	}
	
	private String convertToVAppSessionInfoString(RestoreJob vAppRestoreJob) {
		RecoverVMOption recoverVMOption = vAppRestoreJob.getRecoverVMOption();
		if(null != recoverVMOption){
			VAppSessionInfo sessionInfo = new VAppSessionInfo();
			VCloudVApp vApp = new VCloudVApp();
			vApp.setName(recoverVMOption.getVmName());
			vApp.setId(recoverVMOption.getVmIdInVApp());
			sessionInfo.setvApp(vApp);
			
			VMNetworkConfig[] vAppNetworkConfigArray = recoverVMOption.getVMNetworkConfig();
			if (vAppNetworkConfigArray != null && vAppNetworkConfigArray.length > 0) {
				List<VCloudNetwork> vAppNetworks = new ArrayList<>();
				vApp.setvAppNetworks(vAppNetworks);
				for (VMNetworkConfig neteorkConfig : vAppNetworkConfigArray) {
					VCloudNetwork temp = convertVMNetworkConfigToVCloudNetwork(neteorkConfig);
					if (temp != null) {
						vAppNetworks.add(temp);
					}
				}
			}
			
			List<RestoreJob> childRestoreJobList = vAppRestoreJob.getChildVMRestoreJobList();
			if (childRestoreJobList != null && childRestoreJobList.size() > 0) {
				 List<VCloudVM> vmList = new ArrayList<>();
				 sessionInfo.setVmList(vmList);
				
				 for (RestoreJob childRestoreJob : childRestoreJobList) {
					RecoverVMOption childRecoverVMOption = childRestoreJob.getRecoverVMOption();
					if (childRecoverVMOption == null) {
						continue;
					} else {
						VMNetworkConfig[] childVMNetworkConfigArray = childRecoverVMOption.getVMNetworkConfig();
						if (childVMNetworkConfigArray != null && childVMNetworkConfigArray.length > 0) {
							VCloudVM vCloudVM = new VCloudVM();
							vCloudVM.setName(childRecoverVMOption.getVmName());
							vCloudVM.setId(childRecoverVMOption.getVmIdInVApp());
							vmList.add(vCloudVM);
							
							List<VCloudNetwork> childVMNetworks = new ArrayList<>();
							vCloudVM.setVmNetworkCards(childVMNetworks);
							for (VMNetworkConfig neteorkConfig : childVMNetworkConfigArray) {
								VCloudNetwork temp = convertVMNetworkConfigToVCloudNetwork(neteorkConfig);
								if (temp != null) {
									childVMNetworks.add(temp);
								}
							}
						}
						childRecoverVMOption.setVMNetworkConfig(null);
					}
				}
			}
			
			return sessionInfo.toXml();
		}
		return null;
	}
	
	private VCloudNetwork convertVMNetworkConfigToVCloudNetwork(VMNetworkConfig networkConfig) {
		if (networkConfig == null) {
			return null;
		}
		
		VCloudNetwork vCloudNetwork = new VCloudNetwork();
		vCloudNetwork.setId(networkConfig.getId());
		vCloudNetwork.setName(networkConfig.getLabel());
		vCloudNetwork.setNetworkAdapterType(networkConfig.getAdapterType());
		vCloudNetwork.setParentNetworkId(networkConfig.getParentId());
		vCloudNetwork.setParentNetwork(networkConfig.getParentName());
		
		return vCloudNetwork;
	}
	
	private JJobScriptBackupVC ConvertToJJobScriptBackupVC(VirtualCenter vc){
		if(vc == null){
			return null;
		}
		
		JJobScriptBackupVC jobscriptVC = new JJobScriptBackupVC();
		jobscriptVC.setIgnoreCertificate(1);
		jobscriptVC.setPassword(vc.getPassword());
		jobscriptVC.setPort(vc.getPort());
		jobscriptVC.setProtocol(vc.getProtocol());
		jobscriptVC.setUsername(vc.getUsername());
		jobscriptVC.setVcName(vc.getVcName());
		return jobscriptVC;
	}
	
	private List<JJobScriptDiskDataStore> ConvertToJJobScriptDiskDataStore(RestoreDiskDataStore[] restoreDiskDataStores){
		if(restoreDiskDataStores==null || restoreDiskDataStores.length==0){
			return null;
		}
		List<JJobScriptDiskDataStore> result = new ArrayList<JJobScriptDiskDataStore>();
		JJobScriptDiskDataStore temp = null;
		for(RestoreDiskDataStore diskDataStore : restoreDiskDataStores){
			temp = new JJobScriptDiskDataStore();
			temp.setDataStore(diskDataStore.getDataStore());
			temp.setDisk(diskDataStore.getDisk());
			temp.setUlDiskType(diskDataStore.getDiskType());
			temp.setUlQuickRecovery(diskDataStore.getQuickRecovery());
			result.add(temp);
		}
		
		return result;
	}
	
	private List<JJobScriptVMNetworkConfigInfo> ConvertToJJobScriptVMNetworkConfigInfo(VMNetworkConfig[] vmNetworkConfigs){
		if(vmNetworkConfigs==null || vmNetworkConfigs.length==0){
			return null;
		}
		List<JJobScriptVMNetworkConfigInfo> result = new ArrayList<JJobScriptVMNetworkConfigInfo>();
		JJobScriptVMNetworkConfigInfo temp = null;
		for(VMNetworkConfig vmnetworkCfg : vmNetworkConfigs){
			temp = new JJobScriptVMNetworkConfigInfo();
			temp.setBackingInfoType(vmnetworkCfg.getBackingInfoType());
			temp.setDeviceName(vmnetworkCfg.getDeviceName());
			temp.setDeviceType(vmnetworkCfg.getDeviceType());
			temp.setLabel(vmnetworkCfg.getLabel());
			temp.setPortgroupKey(vmnetworkCfg.getPortgroupKey());
			temp.setPortgroupName(vmnetworkCfg.getPortgroupName());
			temp.setSwitchName(vmnetworkCfg.getSwitchName());
			temp.setSwitchUUID(vmnetworkCfg.getSwitchUUID());
			
			temp.setId(vmnetworkCfg.getId());
			temp.setAdapterType(vmnetworkCfg.getAdapterType());
			temp.setParentId(vmnetworkCfg.getParentId());
			temp.setParentName(vmnetworkCfg.getParentName());
			
			result.add(temp);
		}
		
		return result;
	}
	
	private JJobScript packageExchangeJob(RestoreJob job, String hostname) {
		JJobScript result = new JJobScript();
		
		result.setLauncherInstanceUUID(job.getVmInstanceUUID());
		result.setUsJobType(1);
		result .setUlJobAttribute(job.getJobLauncher());
		setUserPwd(job, result);
		result.setRPSDataStoreDisplayName(job.getRpsDataStoreDisplayName());
		result.setRPSDataStoreName(job.getRpsDataStoreName());
		result.setRpsPolicyID(job.getRpsPolicy());
		if(job.getSrcRpsHost() != null)
			result.setRpsHostname(job.getSrcRpsHost().getRhostname());

		RestoreExchangeOption optionModel = job.getRestoreExchangeOption();
		//set FOption
		if (job.getRestoreType() == DestTypeContratct.DumpFile.getValue()) {
			result.setPwszDestPath(job.getDestinationPath());
			if(optionModel != null 
			    && optionModel.getReplayLogOnDB() != null && optionModel.getReplayLogOnDB())
			{
				result.setFOptions(DestTypeContratct.NonExchange.getValue());
			}
			else
			{
				result.setFOptions(DestTypeContratct.DumpFile.getValue());
			}
		} 
		else {
			result.setFOptions(job.getRestoreType());
			if(job.getRestoreType() == DestTypeContratct.RSGRDB.getValue())
			   result.setPwszDestPath(job.getExchRDBName());
		}
		

		int dismountAndMountOption = 0;
		if(optionModel != null 
				&& optionModel.getDismounAndMountDB() != null && optionModel.getDismounAndMountDB()) 
			dismountAndMountOption = 1; 

		List<JJobScriptNode> nodeList = new ArrayList<JJobScriptNode>();
		for (RestoreJobNode node : job.getNodes()) {
			JJobScriptNode jJobScriptNode = new JJobScriptNode();
			jJobScriptNode.setPwszNodeName(hostname);
			if(job.getJobLauncher()==1){
				jJobScriptNode.setPwszNodeName(job.getVmInstanceUUID());
			}
			jJobScriptNode.setUlSessNum(node.getSessionNumber());
			jJobScriptNode.setPwszSessPath(job.getSessionPath());
			jJobScriptNode.setPwszEncryptPasswordRestore(node.getEncryptPassword());

			List<JJobScriptRestoreVolApp> volumes = new ArrayList<JJobScriptRestoreVolApp>();
			for (RestoreJobItem item : node.getJobItems()) {
				JJobScriptRestoreVolApp volume = new JJobScriptRestoreVolApp();
				volume.setUlSubSessNum(item.getSubSessionNum());
				volume.setPwszPath("\\\\" + hostname.toLowerCase() + "\\"
						+ item.getPath());
				volume.setUlFileSystem(QJDFS_BLI_NT_RESTORE_APP);
				volume.setFOptions(dismountAndMountOption);

				List<JJobScriptVolAppItem> entries = new ArrayList<JJobScriptVolAppItem>();
				if (item.getEntries() != null) {
					for (RestoreJobItemEntry entry : item.getEntries()) {
						JJobScriptVolAppItem volAppItem = new JJobScriptVolAppItem(entry.getPath(),
								ASTIO_TRAVERSEDIR);
						entries.add(volAppItem);
					}
				}

				volume.setNVolItemAppComp(entries.size());
				volume.setPVolItemAppCompList(entries);
				volumes.add(volume);
			}

			jJobScriptNode.setNVolumeApp(volumes.size());
			jJobScriptNode.setPRestoreVolumeAppList(volumes);
			nodeList.add(jJobScriptNode);
		}
		result.setNNodeItems(nodeList.size());
		result.setPAFNodeList(nodeList);

		return result;
	}

	private void setUserPwd(RestoreJob job, JJobScript result) {
		if (job == null || result == null)
			return;

		if (job.getUserName() != null && job.getUserName().trim().length() > 0) {
			result.setPwszUserName(job.getUserName());
			if (job.getPassword() != null) {
				result.setPwszPassword(job.getPassword());
			}
		}
	}

	private JJobScript packageSQLServerJob(RestoreJob job, String hostname) {
		JJobScript result = new JJobScript();
		result.setUsJobType(1);
		result .setUlJobAttribute(job.getJobLauncher());
		setUserPwd(job, result);
		result.setLauncherInstanceUUID(job.getVmInstanceUUID());
		result.setRPSDataStoreDisplayName(job.getRpsDataStoreDisplayName());
		result.setRPSDataStoreName(job.getRpsDataStoreName());
		result.setRpsPolicyID(job.getRpsPolicy());
		if(job.getSrcRpsHost() != null)
			result.setRpsHostname(job.getSrcRpsHost().getRhostname());
//		boolean isAlternativeLocation = false;
//		if (!StringUtil.isEmptyOrNull(job.getDestinationPath())) {
//			isAlternativeLocation = true;
//		}
		result.setFOptions(job.getRestoreType());
		if (job.getRestoreType() == DestTypeContratct.DumpFile.getValue()) {
			result.setPwszDestPath(job.getDestinationPath());
		} 
		
		List<JJobScriptNode> nodeList = new ArrayList<JJobScriptNode>();
		for (RestoreJobNode node : job.getNodes()) {
			JJobScriptNode jJobScriptNode = new JJobScriptNode();
			jJobScriptNode.setPwszNodeName(hostname);
			if(job.getJobLauncher()==1){
				jJobScriptNode.setPwszNodeName(job.getVmInstanceUUID());
			}
			jJobScriptNode.setUlSessNum(node.getSessionNumber());
			jJobScriptNode.setPwszSessPath(job.getSessionPath());
			jJobScriptNode.setPwszEncryptPasswordRestore(node.getEncryptPassword());

			List<JJobScriptRestoreVolApp> volumes = new ArrayList<JJobScriptRestoreVolApp>();
			for (RestoreJobItem item : node.getJobItems()) {
				JJobScriptRestoreVolApp volume = new JJobScriptRestoreVolApp();
				volume.setUlSubSessNum(item.getSubSessionNum());
				volume.setPwszPath("\\\\" + hostname.toLowerCase() + "\\"
						+ item.getPath());
				volume.setUlFileSystem(QJDFS_BLI_NT_RESTORE_APP);

				List<JJobScriptVolAppItem> entries = new ArrayList<JJobScriptVolAppItem>();
				if (item.getEntries() != null) {
					for (RestoreJobItemEntry entry : item.getEntries()) {
						entries.add(new JJobScriptVolAppItem(entry.getPath(),
								ASTIO_TRAVERSEDIR));
					}
					//set destination path and new db name if restore type is AlterLoc.
					if(job.getRestoreType() == DestTypeContratct.AlterLoc.getValue())
					{
						RestoreSQLNewDest[] destList = job.getRestoreSQLNewDestList();
						if(destList != null)
						{
							Map<String, RestoreSQLNewDest> pathToDest = new HashMap<String, RestoreSQLNewDest>();
							for(RestoreSQLNewDest newDest : destList) {
								pathToDest.put(newDest.getDbPath(), newDest);
							}
							
							for(JJobScriptVolAppItem entry : entries) {
								RestoreSQLNewDest newDest = pathToDest.get(entry.getPwszFileorDir());
								if(newDest != null){
									String restoreDestPath = getDBPath(newDest);
									entry.setPwszCompRestPath(restoreDestPath);
									entry.setPwszCompRestName(newDest.getNewDbName());
								}
							}
						}
					}
				}

				volume.setNVolItemAppComp(entries.size());
				volume.setPVolItemAppCompList(entries);
				volumes.add(volume);
			}

			jJobScriptNode.setNVolumeApp(volumes.size());
			jJobScriptNode.setPRestoreVolumeAppList(volumes);
			nodeList.add(jJobScriptNode);
		}
		result.setNNodeItems(nodeList.size());
		result.setPAFNodeList(nodeList);

		return result;
	}

	private String getDBPath(RestoreSQLNewDest newDest) {
		String restoreDestPath = newDest.getRestoreDestPath();
		if(!StringUtil.isEmptyOrNull(restoreDestPath))
		{
			StringBuilder newPath = new StringBuilder(restoreDestPath);
			if(!restoreDestPath.endsWith("\\") && !restoreDestPath.endsWith("/"))
				newPath.append("\\");
			newPath.append(newDest.getInstanceName());
			newPath.append("\\");
			newPath.append(StringUtil.isEmptyOrNull(newDest.getNewDbName()) ? newDest.getDbName() : newDest.getNewDbName());
			return newPath.toString();
		}
		return restoreDestPath;
	}

	private void packageJobDest(RestoreJob job, JJobScript result) {
		if (job == null || result == null)
			return;

		result.setPwszDestPath(job.getDestinationPath());

		if (job.getDestUser() != null && job.getDestUser().trim().length() > 0) {
			result.setPwszUserName_2(job.getDestUser());

			if (job.getDestPass() != null) {
				result.setPwszPassword_2(job.getDestPass());
			}
		}
	}

	private JJobScript packageFileSystemJob(RestoreJob job, String hostname) {
		JJobScript result = new JJobScript();
		result.setUsJobType(1);
		result .setUlJobAttribute(job.getJobLauncher());
		setUserPwd(job, result);
		result.setLauncherInstanceUUID(job.getVmInstanceUUID());
		result.setRPSDataStoreDisplayName(job.getRpsDataStoreDisplayName());
		result.setRPSDataStoreName(job.getRpsDataStoreName());
		result.setRpsPolicyID(job.getRpsPolicy());
		if(job.getSrcRpsHost() != null)
			result.setRpsHostname(job.getSrcRpsHost().getRhostname());

		if (!StringUtil.isEmptyOrNull(job.getDestinationPath())) {
			packageJobDest(job, result);
		}

		List<JJobScriptNode> nodeList = new ArrayList<JJobScriptNode>();
		for (RestoreJobNode node : job.getNodes()) {// only one host node.
			JJobScriptNode jJobScriptNode = new JJobScriptNode();
			jJobScriptNode.setPwszNodeName(hostname);
			if(job.getJobLauncher()==1){
				jJobScriptNode.setPwszNodeName(job.getVmInstanceUUID());
			}
			jJobScriptNode.setUlSessNum(node.getSessionNumber());
			jJobScriptNode.setPwszSessPath(job.getSessionPath());
			jJobScriptNode.setPwszEncryptPasswordRestore(node.getEncryptPassword());

			List<JJobScriptRestoreVolApp> volumes = new ArrayList<JJobScriptRestoreVolApp>();
			for (RestoreJobItem item : node.getJobItems()) {// for disk, there may be two or more items like C: F:.
				JJobScriptRestoreVolApp volume = new JJobScriptRestoreVolApp();
				volume.setUlSubSessNum(item.getSubSessionNum());
				volume.setPwszPath("\\\\" + hostname + "\\" + item.getPath());
				volume.setUlFileSystem(QJDFS_BLI_NT_RESTORE_FILE);
				if (job.getFileSystemOption().isCreateBaseFolder() == true)
					result.setFOptions(result.getFOptions()
							| QJDTO_R_CREATEWHOLEPATH);

				if (job.getFileSystemOption().isOverwriteExistingFiles()) {
					result.setFOptions(result.getFOptions()
							| QJDTO_R_ONCONFLICT_REPLACE);
					if (job.getFileSystemOption().isReplaceActiveFiles()) {
						result.setFOptions(result.getFOptions()
								| QJDTO_R_REPLACE_ACTIVE);
					}
				}else if (job.getFileSystemOption().isRenameFile()){
					result.setFOptions(result.getFOptions() | QJDTO_R_ONCONFLICT_RENAME);
				}

				List<JJobScriptVolAppItem> entries = new ArrayList<JJobScriptVolAppItem>();
				if (item.getEntries() != null) {// for each disk, there maybe many folders/Files.
					for (RestoreJobItemEntry entry : item.getEntries()) {
						if (entry.getType() == CatalogType.File)
							entries.add(new JJobScriptVolAppItem(entry
									.getPath(), FILEENTRY));
						else if (entry.getType() == CatalogType.Folder)
							entries.add(new JJobScriptVolAppItem(entry
									.getPath(), ASTIO_TRAVERSEDIR));
					}
				}

				volume.setNVolItemAppComp(entries.size());
				volume.setPVolItemAppCompList(entries);
				volumes.add(volume);
			}

			jJobScriptNode.setNVolumeApp(volumes.size());
			jJobScriptNode.setPRestoreVolumeAppList(volumes);
			nodeList.add(jJobScriptNode);
		}

		result.setNNodeItems(nodeList.size());
		result.setPAFNodeList(nodeList);

		return result;
	}

private JJobScript packageExchangeGRTJob(RestoreJob job, String hostname) {
		JJobScript result = new JJobScript();

		result.setUsJobType(1);
		setUserPwd(job, result);
		result.setRPSDataStoreDisplayName(job.getRpsDataStoreDisplayName());
		result.setRPSDataStoreName(job.getRpsDataStoreName());
		result.setRpsPolicyID(job.getRpsPolicy());
		if(job.getSrcRpsHost() != null)
			result.setRpsHostname(job.getSrcRpsHost().getRhostname());
		if (!StringUtil.isEmptyOrNull(job.getDestinationPath())) 
		{
			packageJobDest(job, result);
		}

		RestoreExchangeGRTOption optionModel = job.getRestoreExchangeGRTOption();

		List<JJobScriptNode> nodeList = new ArrayList<JJobScriptNode>();
		for (RestoreJobNode node : job.getNodes()) {
			JJobScriptNode jJobScriptNode = new JJobScriptNode();
			jJobScriptNode.setPwszNodeName(hostname);
			jJobScriptNode.setUlSessNum(node.getSessionNumber());
			jJobScriptNode.setPwszSessPath(job.getSessionPath());
			jJobScriptNode.setPwszEncryptPasswordRestore(node.getEncryptPassword());

			List<JJobScriptRestoreVolApp> volumes = new ArrayList<JJobScriptRestoreVolApp>();
			for (RestoreJobItem item : node.getJobItems()) {
				JJobScriptRestoreVolApp volume = new JJobScriptRestoreVolApp();
				volume.setUlSubSessNum(item.getSubSessionNum());
				volume.setPwszPath("\\\\" + hostname.toLowerCase() + "\\"
						+ item.getPath());
				volume.setUlFileSystem(QJDFS_BLI_EXCH_GRT);				

				List<JJobScriptVolAppItem> entries = new ArrayList<JJobScriptVolAppItem>();
				if (item.getEntries() != null) {
					for (RestoreJobItemEntry entry : item.getEntries()) {
						JJobScriptVolAppItem volAppItem = new JJobScriptVolAppItem(entry.getPath(),
								ASTIO_TRAVERSEDIR);
						
						List<JJobScriptExchSubItem> exchSubItemList = new ArrayList<JJobScriptExchSubItem>();						
						for (RestoreJobExchSubItem subItem : entry.getExchSubItems())
						{
							JJobScriptExchSubItem jSubItem = new JJobScriptExchSubItem();
														
							jSubItem.setUlItemType(subItem.getUlItemType());
							jSubItem.setPwszItemName(subItem.getPwszItemName());
							jSubItem.setPwszMailboxName(subItem.getMailboxName());
//							jSubItem.setUl_hMailboxID(subItem.getUl_hMailboxID());
//							jSubItem.setUl_lMailboxID(subItem.getUl_lMailboxID());
//							jSubItem.setUl_hFolderID(subItem.getUl_hFolderID());
//							jSubItem.setUl_lFolderID(subItem.getUl_lFolderID());
//							jSubItem.setUl_hMsgID(subItem.getUl_hMsgID());
//							jSubItem.setUl_lMsgID(subItem.getUl_lMsgID());	
							jSubItem.setPwszExchangeObjectIDs(subItem.getExchangeObjectID());
							jSubItem.setPwszDescription(subItem.getPwszDescription());							
							
							exchSubItemList.add(jSubItem);
						}
						
						volAppItem.setnExchSubItemList(exchSubItemList.size());
						volAppItem.setpExchSubItemList(exchSubItemList);
						
						entries.add(volAppItem);
					}
				}
				
				volume.setNVolItemAppComp(entries.size());
				volume.setPVolItemAppCompList(entries);
				
				// package Exchange GRT restore option here
				JJobScriptRestoreOptionExch restoreOptionExch = new JJobScriptRestoreOptionExch();
				restoreOptionExch.setUlOptions(optionModel.getOption());
				restoreOptionExch.setUlServerVersion(optionModel.getServerVersion());
				restoreOptionExch.setPwszFolder(optionModel.getFolder());
				restoreOptionExch.setPwszAlternateServer(optionModel.getAlternateServer());
				restoreOptionExch.setPwszUser(optionModel.getUserName());
				restoreOptionExch.setPwszUserPW(optionModel.getPassword());		
				
				volume.setpRestoreOption_Exch(restoreOptionExch);
				
				volumes.add(volume);
			}

			jJobScriptNode.setNVolumeApp(volumes.size());
			jJobScriptNode.setPRestoreVolumeAppList(volumes);
			nodeList.add(jJobScriptNode);
		}
		result.setNNodeItems(nodeList.size());
		result.setPAFNodeList(nodeList);

		return result;
	}
}
