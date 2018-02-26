package com.ca.arcflash.webservice.service.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.data.archive.JJobScriptArchiveInfo;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
// October sprint 
import com.ca.arcflash.webservice.data.vsphere.StorageAppliance;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JJobScriptBackupOptionExch;
import com.ca.arcflash.webservice.jni.model.JJobScriptBackupOptionSp;
import com.ca.arcflash.webservice.jni.model.JJobScriptBackupVC;
import com.ca.arcflash.webservice.jni.model.JJobScriptBackupVM;
import com.ca.arcflash.webservice.jni.model.JJobScriptBackupVol;
import com.ca.arcflash.webservice.jni.model.JJobScriptNode;
// October sprint 
import com.ca.arcflash.webservice.jni.model.JJobScriptStorageAppliance;
import com.ca.arcflash.webservice.jni.model.JJobScriptVSphereNode;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class BackupJobConverter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(BackupJobConverter.class);

	public static final int QJDTO_B_PURGELOG_SQL = 0x00000100; // Purge SQL's log files when backup job succeed
	public static final int QJDTO_B_PURGELOG_EXCH =0x00000200; // Purge Exchange's log files when backup job succeed
	
	public static final int QJDTO_B_RUN_JOB = 0x00001000;
	public static final int QJDTO_B_FAIL_JOB = 0x00002000;	
	public static final int QJDTO_B_DISABLE_CATALOG = 0x00000400;	
	public static final int BACKUP_SET_START = 0x00010000;
	
	public static final int QJDTO_B_DISK_FORMAT_D2D  = 0x01000000; // CA VHD format
	public static final int QJDTO_B_DISK_FORMAT_D2D2 = 0x02000000; // CA new VHD format, internal call it merge phase 2 format
	
	public static final int QJDTO_B_DISK_FORMAT_GDD = 0x10000000; // CA Dedup format
	
	public static final int QJDTO_B_CHECK_RECOVERYPOINT = 0x00004000; // For VMware, check recovery point/disk when backup job succeed
	
	public JJobScript convert(BackupConfiguration configuration, int backupType, String hostname) throws ServiceException{
		return convert(configuration, backupType,hostname, false, false, false, false, null);
	}
	public JJobScript convert(BackupConfiguration configuration,
			int backupType, String hostname, boolean isDaily, boolean isWeekly, boolean isMonthly, boolean isRPSEnable, ArchiveConfiguration archiveConfig) throws ServiceException {
		if (configuration == null)
			return null;

		JJobScript result = new JJobScript();
		result.setUsJobType(0);
		
		logger.debug("convert() - backupType = " + backupType);
		result.setUsJobMethod(backupType);
		
		result.setPwszDestPath(configuration.getDestination());
		result.setDwCompressionLevel(configuration.getCompressionLevel());
		result.setDwEncryptType(configuration.getEncryptionAlgorithm());
		result.setPwszEncryptPassword(configuration.getEncryptionKey());
//		if(!configuration.isD2dOrRPSDestType() && StringUtil.isJustEmptyOrNull(configuration.getEncryptionKey())){
//			// if to RPS and session pwd is null, set a default session pwd as the encrypt key
//			result.setPwszEncryptPassword(defaultSessionPwd);
//			
//		}
		
		result.setDwThrottlingByKB(BackupService.getInstance().getThrottling());
		
		if (configuration.getUserName() != null
				&& configuration.getUserName().trim().length() > 0) {
			result.setPwszUserName(configuration.getUserName());
			if (configuration.getPassword() != null) {
				result.setPwszPassword(configuration.getPassword());
			}
		}
		result.setUsRestPoint(configuration.getRetentionCount());

		result.setNNodeItems(1);
		result.setPAFNodeList(new ArrayList<JJobScriptNode>(1));
		JJobScriptNode node = new JJobScriptNode();
		node.setPwszNodeName(hostname);
		result.getPAFNodeList().add(node);
		
		BackupVolumes backupVolumes = configuration.getBackupVolumes();
		if(backupVolumes == null || backupVolumes.isFullMachine() 
			|| backupVolumes.getVolumes() == null || backupVolumes.getVolumes().size() == 0) {
			node.setNVolumeApp(0);
		}
		else
		{
			String[] volumes = backupVolumes.getVolumes().toArray(new String[0]);
			node.setNVolumeApp(volumes.length);
			List<JJobScriptBackupVol> list = new ArrayList<JJobScriptBackupVol>();
			for (int i = 0; i < volumes.length; i++) {
				JJobScriptBackupVol backupVol = new JJobScriptBackupVol();
				backupVol.setPwszVolName(volumes[i]);
				list.add(backupVol);
			}
			node.setPBackupVolumeList(list);
		}
		
		JJobScriptBackupOptionExch backupOptionExch = new JJobScriptBackupOptionExch();
		if (isExchangeGRTFuncEnabled()) {
			backupOptionExch.setUlOptions(configuration.getExchangeGRTSetting());			
		} else {
			backupOptionExch.setUlOptions(2);	
		}
		node.setpBackupOption_Exch(backupOptionExch);
		
		JJobScriptBackupOptionSp backupOptionSp = new JJobScriptBackupOptionSp();
		backupOptionSp.setUlOptions(configuration.getSharePointGRTSetting());
		node.setpBackupOption_Sp(backupOptionSp);
		
		result.setPwszAfterJob(configuration.getCommandAfterBackup());
		result.setPwszBeforeJob(configuration.getCommandBeforeBackup());

		result.setPwszPostSnapshotCmd(configuration.getCommandAfterSnapshot());		
		result.setPwszPrePostUser(configuration.getPrePostUserName());
		result.setPwszPrePostPassword(configuration.getPrePostPassword());
		
		if (configuration.isEnablePreExitCode())
		{
			if (configuration.isSkipJob())
				result.setFOptions(QJDTO_B_FAIL_JOB);
			else
				result.setFOptions(QJDTO_B_RUN_JOB);
		}	

		PeriodSchedule p = null;
		if(configuration.getAdvanceSchedule() != null)
			p = configuration.getAdvanceSchedule().getPeriodSchedule();
		
		if(p != null && p.isEnabled() && (isDaily||isWeekly||isMonthly)){
			if(p.getDaySchedule() != null && isDaily)
				if(!p.getDaySchedule().isGenerateCatalog())
					result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);	
			
			if(p.getWeekSchedule() != null && isWeekly)
				if(!p.getWeekSchedule().isGenerateCatalog())
					result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);	
			
			if(p.getMonthSchedule() != null && isMonthly)
				if(!p.getMonthSchedule().isGenerateCatalog())
						result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);			
		}else{// advanced settings.
			if(!configuration.isGenerateCatalog() && !isRPSEnable){
				result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);
			}
		}
		
		if(isDaily){
			result.setFOptions(result.getFOptions() | PeriodRetentionValue.QJDTO_B_Backup_Daily);			
		}
		if(isWeekly){
			result.setFOptions(result.getFOptions() | PeriodRetentionValue.QJDTO_B_Backup_Weekly);			
		}
		if(isMonthly){
			result.setFOptions(result.getFOptions() | PeriodRetentionValue.QJDTO_B_Backup_Monthly);			
		}		
		
		//Duplicate???
		/*if(!configuration.isGenerateCatalog())
		{
			result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);
		}*/
		
		if (configuration.getBackupDataFormat() > 0) {
			result.setFOptions(result.getFOptions() | QJDTO_B_DISK_FORMAT_D2D2);
		}else{
			result.setFOptions(result.getFOptions() | QJDTO_B_DISK_FORMAT_D2D);
		}		
		
		// check recovery point for daily/weekly/monthly
		if((isDaily || isWeekly || isMonthly) && configuration.getAdvanceSchedule() != null && configuration.getAdvanceSchedule().getPeriodSchedule() != null) {
			PeriodSchedule periodSchedule = configuration.getAdvanceSchedule().getPeriodSchedule();
			if(periodSchedule != null) {
				if(isDaily && periodSchedule.getDaySchedule() != null && periodSchedule.getDaySchedule().isCheckRecoveryPoint()) {
					result.setFOptions(result.getFOptions() | QJDTO_B_CHECK_RECOVERYPOINT);
				}
				if(isWeekly && periodSchedule.getWeekSchedule() != null && periodSchedule.getWeekSchedule().isCheckRecoveryPoint()) {
					result.setFOptions(result.getFOptions() | QJDTO_B_CHECK_RECOVERYPOINT);
				}
				if(isMonthly && periodSchedule.getMonthSchedule() != null && periodSchedule.getMonthSchedule().isCheckRecoveryPoint()) {
					result.setFOptions(result.getFOptions() | QJDTO_B_CHECK_RECOVERYPOINT);
				}
			}
		}
		
		// check recovery point for custom/manual or Retain by Recovery Sets
		if(!(isDaily || isWeekly || isMonthly) && configuration.isCheckRecoveryPoint()) {
			result.setFOptions(result.getFOptions() | QJDTO_B_CHECK_RECOVERYPOINT);
		}
		
		result.setUsPreExitCode(configuration.getPreExitCode());
		result.setSoftwareOrHardwareSnapshotType(configuration.isSoftwareOrHardwareSnapshotType());
		result.setFailoverToSoftwareSnapshot(configuration.isFailoverToSoftwareSnapshot());
		result.setUseTransportableSnapshot(configuration.isUseTrasportableSnapshot());
		result.setDwSqlLogDays(configuration.getPurgeSQLLogDays());
		result.setDwExchangeLogDays(configuration.getPurgeExchangeLogDays());
		//fanda03 fix 102889
		result.setPreAllocationSpace(configuration.getPreAllocationBackupSpace());
		if(!configuration.isD2dOrRPSDestType()){
			BackupRPSDestSetting rpsSetting = configuration.getBackupRpsDestSetting();
			result.setRPSDataStoreDisplayName(rpsSetting.getRPSDataStoreDisplayName());
			result.setRPSDataStoreName(rpsSetting.getRPSDataStore());
			result.setRPSDataStoreDisplayName(rpsSetting
					.getRPSDataStoreDisplayName());
			result.setRpsPolicyID(rpsSetting.getRPSPolicyUUID());
			result.setRpsPolicyName(rpsSetting.getRPSPolicy());
			result.setRpsHostname(rpsSetting.getRpsHost().getRhostname());
			result.setRpsSID(rpsSetting.getRpsHost().getUuid());
			RpsPolicy4D2D policy = BackupService.getInstance().getRPSPolicy();
			if(policy != null && policy.isEnableGDD()){
				result.setFOptions(result.getFOptions() | QJDTO_B_DISK_FORMAT_GDD);
			}
			if(policy != null && configuration.isEnableEncryption()){
				String hashKey = BackupService.getInstance().getNativeFacade().getRPSDataStoreHashKey(
						policy.getDataStoreSharedPath(), configuration.getUserName(), 
						configuration.getPassword(), 0, policy.getEncryptionPassword());
				result.setPwszVDiskPassword(hashKey);
				if(StringUtil.isEmptyOrNull(hashKey)){
					String message = WebServiceMessages.getResource("backupNotRunFailHashKey", 
							rpsSetting.getRPSDataStoreDisplayName());
					BackupService.getInstance().getNativeFacade().addLogActivity(
							Constants.AFRES_AFALOG_ERROR, Constants.AFRES_AFJWBS_GENERAL, 
							new String[]{message, "","","",""});
					throw new ServiceException(message, "");
				}
			}
		}
		if(archiveConfig != null){
			JJobScriptArchiveInfo archiveInfo = new JJobScriptArchiveInfo();
			archiveInfo.setbFileCopyFeatureEnabled(archiveConfig.isbArchiveAfterBackup());
			if(archiveConfig.isbArchiveAfterBackup()){
				if(ArchiveConfigurationConstants.SCHEDULE_MODE_ADVANCED.equalsIgnoreCase(archiveConfig.getStrScheduleMode())){
					archiveInfo.setDwFileCpySchType(ArchiveConfigurationConstants.AFFILECOPY_TYPE_ADV_SCHEDULE);
					archiveInfo.setbDailyBackup(archiveConfig.isbDailyBackup());
					archiveInfo.setbWeeklyBackup(archiveConfig.isbWeeklyBackup());
					archiveInfo.setbMonthlyBackup(archiveConfig.isbMonthlyBackup());
				}else{
					archiveInfo.setDwFileCpySchType(ArchiveConfigurationConstants.AFFILECOPY_TYPE_N_BACKUP);
					archiveInfo.setbDailyBackup(false);
					archiveInfo.setbWeeklyBackup(false);
					archiveInfo.setbMonthlyBackup(false);
					archiveInfo.setDwSubmitArchiveAfterNBackups(archiveConfig.getiArchiveAfterNBackups());
				}
			}
			result.setArchiveInfo(archiveInfo);
		}
		return result;
	}
	
	private JJobScriptBackupVM convertToJJobScriptBackupVM(BackupVM vm) {
		JJobScriptBackupVM jobScriptVM = new JJobScriptBackupVM();
		
		/*jobScriptVM.setOsPassword(vm.getDesPassword());
		jobScriptVM.setOsUsername(vm.getDesUsername());*/
		
		jobScriptVM.setOsPassword(vm.getPassword());
		jobScriptVM.setOsUsername(vm.getUsername());
		jobScriptVM.setVmHostName(vm.getVmHostName());
		jobScriptVM.setVmInstanceUUID(vm.getInstanceUUID());
		jobScriptVM.setState(1);
		jobScriptVM.setVmName(vm.getVmName());
		jobScriptVM.setVmType(vm.getVmType());
		jobScriptVM.setVmUUID(vm.getUuid());
		jobScriptVM.setVmVMX(vm.getVmVMX());
		return jobScriptVM;
	}
	
	public JJobScript convert(VMBackupConfiguration configuration,
			int backupType, String hostname,VirtualMachine virtualmachine, boolean isDaily, boolean isWeekly, boolean isMonthly, RpsPolicy4D2D rpsPolicy, boolean isRPSCatalogEnable) {
		if (configuration == null)
			return null;

		JJobScript result = new JJobScript();
		if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal())
			result.setUsJobType(Constants.AF_JOBTYPE_VM_BACKUP);
		else if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware_VApp.ordinal())
			result.setUsJobType(Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP);
		else if (configuration.getBackupVM().getVmType() == BackupVM.Type.HyperV_Cluster.ordinal())
			result.setUsJobType(Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP);
		else 
			result.setUsJobType(Constants.AF_JOBTYPE_HYPERV_VM_BACKUP);
		logger.debug("convert() - backupType = " + backupType);
		result.setUsJobMethod(backupType);
		
		BackupVM backupVM = configuration.getBackupVM();
		
		result.setPwszDestPath(backupVM.getDestination());
		result.setPwszUserName(backupVM.getDesUsername());
		result.setPwszPassword(backupVM.getDesPassword());
		result.setDwCompressionLevel(configuration.getCompressionLevel());
		if (configuration.getUserName() != null
				&& configuration.getUserName().trim().length() > 0) {
			result.setPwszUserName(configuration.getUserName());
			if (configuration.getPassword() != null) {
				result.setPwszPassword(configuration.getPassword());
			}
		}
		result.setUsRestPoint(configuration.getRetentionCount());

		result.setNNodeItems(1);
		result.setPAFNodeList(new ArrayList<JJobScriptNode>(1));
		//October sprint 
		List<StorageAppliance> storageApplianceList = configuration.getStorageApplianceList();
		List<JJobScriptStorageAppliance> jstorageApplinaceList = new ArrayList<JJobScriptStorageAppliance>();
		if(storageApplianceList != null && storageApplianceList.size() > 0) {
			for(StorageAppliance memberAppliance : storageApplianceList) {
				JJobScriptStorageAppliance obj = new JJobScriptStorageAppliance();
				//Dec sprint
				obj.setPwszSystemMode(memberAppliance.getSystemMode());
				obj.setPwszDataIP(memberAppliance.getDataIP());
				obj.setPwszNodeName(memberAppliance.getServerName());
				obj.setPwszUserName(memberAppliance.getUsername());
				obj.setPwszPassword(memberAppliance.getPassword());
				obj.setPwszProtocol(memberAppliance.getProtocol());
				obj.setPwszPort(memberAppliance.getPort());
				jstorageApplinaceList.add(obj);
			}
		}
		result.setpAFStorageApplianceList(jstorageApplinaceList);
		result.setnStorageApplianceItems(jstorageApplinaceList.size());
		result.setpVSphereNodeList(new ArrayList<JJobScriptVSphereNode>(1));
		
		JJobScriptVSphereNode vsphereNode = new JJobScriptVSphereNode();
		vsphereNode.setPwszNodeName(backupVM.getInstanceUUID());
		JJobScriptBackupVM vm = convertToJJobScriptBackupVM(backupVM);
		
		JJobScriptBackupVC vc = new JJobScriptBackupVC();
		vc.setIgnoreCertificate(1);
		vc.setPassword(backupVM.getEsxPassword());
		vc.setUsername(backupVM.getEsxUsername());
		vc.setPort(backupVM.getPort());
		vc.setProtocol(backupVM.getProtocol());
		vc.setVcName(backupVM.getEsxServerName());
		
		vsphereNode.setVc(vc);
		vsphereNode.setVm(vm);
		
		if (backupVM.getVmType() == BackupVM.Type.VMware.ordinal()){
			if (configuration.getVmwareTransports()!=null && configuration.getVmwareTransports().size()>0){
				StringBuffer transportMode = new StringBuffer();
				for (int i=0;i< configuration.getVmwareTransports().size();i++){
					if (i>0)
						transportMode.append(":");
					transportMode.append(configuration.getVmwareTransports().get(i));
				}
				vsphereNode.setTransportMode(transportMode.toString());
			}
		}
		
		if (backupVM.getVAppVCInfos() != null && backupVM.getVAppVCInfos().length > 0) {
			List<JJobScriptBackupVC> jvAppVCInfos = new ArrayList<JJobScriptBackupVC>();
			for (VirtualCenter vc4VApp : backupVM.getVAppVCInfos()) {
				JJobScriptBackupVC jvc4VApp = new JJobScriptBackupVC();
				
				jvc4VApp.setIgnoreCertificate(1);
				jvc4VApp.setPassword(vc4VApp.getPassword());
				jvc4VApp.setUsername(vc4VApp.getUsername());
				jvc4VApp.setPort(vc4VApp.getPort());
				jvc4VApp.setProtocol(vc4VApp.getProtocol());
				jvc4VApp.setVcName(vc4VApp.getVcName());
				
				jvAppVCInfos.add(jvc4VApp);
			}
			result.setVAppVCCount(jvAppVCInfos.size());
			result.setVAppVCInfos(jvAppVCInfos);
		}
		
		if(backupVM.getVAppMemberVMs() != null && backupVM.getVAppMemberVMs().length > 0) {
			List<JJobScriptBackupVM> jmemberVMs = new ArrayList<JJobScriptBackupVM>();
			for(BackupVM memberVM : backupVM.getVAppMemberVMs()) {
				jmemberVMs.add(convertToJJobScriptBackupVM(memberVM));
			}
			vsphereNode.setvAppChildVMList(jmemberVMs);
		}

		vsphereNode.setRunCommandEvenFailed(configuration.isRunCommandEvenFailed());
		
		if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal()) {
			vsphereNode.setVmwareQuiescenceMethod(configuration.getVmwareQuiescenceMethod());
			vsphereNode.setHyperVSnapshotConsistencyType(configuration.getVmwareQuiescenceMethod());
		} else {
			int hyperSnapshotVConssitencyType = configuration.getHyperVConsistentSnapshotType() == 4 ? 3 : configuration.getHyperVConsistentSnapshotType();
			vsphereNode.setHyperVSnapshotConsistencyType(hyperSnapshotVConssitencyType);
		}
		vsphereNode.setHyperVSnapshotSeparationIndividually(configuration.isHyperVSnapshotSeparationIndividually());
		JJobScriptBackupOptionExch backupOptionExch = new JJobScriptBackupOptionExch();
		if (isExchangeGRTFuncEnabled()) {
			backupOptionExch.setUlOptions(configuration.getExchangeGRTSetting());			
		} else {
			backupOptionExch.setUlOptions(2);	
		}	
		vsphereNode.setpBackupOption_Exch(backupOptionExch);
		
		result.getpVSphereNodeList().add(vsphereNode);
		
		result.setPwszAfterJob(configuration.getCommandAfterBackup());
		result.setPwszBeforeJob(configuration.getCommandBeforeBackup());
		
		result.setPwszPostSnapshotCmd(configuration.getCommandAfterSnapshot());		
		result.setPwszPrePostUser(configuration.getPrePostUserName());
		result.setPwszPrePostPassword(configuration.getPrePostPassword());
		
		result.setDwEncryptType(configuration.getEncryptionAlgorithm());
		result.setPwszEncryptPassword(configuration.getEncryptionKey());
		result.setDwThrottlingByKB(configuration.getThrottling());
		
		////fanda03 fix issue 102889
		result.setPreAllocationSpace( configuration.getPreAllocationBackupSpace() );
		if (configuration.isEnablePreExitCode())
		{
			if (configuration.isSkipJob())
				result.setFOptions(QJDTO_B_FAIL_JOB);
			else
				result.setFOptions(QJDTO_B_RUN_JOB);
		}
		
		try {
			PeriodSchedule p = null;
			if(configuration.getAdvanceSchedule() != null)
				p = configuration.getAdvanceSchedule().getPeriodSchedule();
			
			if(p != null && p.isEnabled() && (isDaily || isWeekly || isMonthly)){
				if(p.getDaySchedule() != null && isDaily)
					if(!p.getDaySchedule().isGenerateCatalog())
						result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);	
				
				if(p.getWeekSchedule() != null && isWeekly)
					if(!p.getWeekSchedule().isGenerateCatalog())
						result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);	
				
				if(p.getMonthSchedule() != null && isMonthly)
					if(!p.getMonthSchedule().isGenerateCatalog())
							result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);			
			}
			
			if(isDaily){
				logger.debug("set Options: + Daily");
				result.setFOptions(result.getFOptions() | PeriodRetentionValue.QJDTO_B_Backup_Daily);			
			}
			if(isWeekly){
				logger.debug("set Options: + Weekly");
				result.setFOptions(result.getFOptions() | PeriodRetentionValue.QJDTO_B_Backup_Weekly);			
			}
			if(isMonthly){
				logger.debug("setFOptions: + Monthly");
				result.setFOptions(result.getFOptions() | PeriodRetentionValue.QJDTO_B_Backup_Monthly);			
			}		
		} catch (Exception e) {
			
		}
		
		if(!configuration.isGenerateCatalog() && !isRPSCatalogEnable)
		{
			result.setFOptions(result.getFOptions() | QJDTO_B_DISABLE_CATALOG);
		}
		
		/*HBBU also needs support old format
		// Always use NEW format for HBBU. Don't support old format
		result.setFOptions(result.getFOptions() | QJDTO_B_DISK_FORMAT_D2D2);
		*/
		if (configuration.getBackupDataFormat() > 0) {
			result.setFOptions(result.getFOptions() | QJDTO_B_DISK_FORMAT_D2D2);
		}else{
			result.setFOptions(result.getFOptions() | QJDTO_B_DISK_FORMAT_D2D);
		}
		
		// check recovery point for daily/weekly/monthly
		if((isDaily || isWeekly || isMonthly) && configuration.getAdvanceSchedule() != null && configuration.getAdvanceSchedule().getPeriodSchedule() != null) {
			PeriodSchedule periodSchedule = configuration.getAdvanceSchedule().getPeriodSchedule();
			if(periodSchedule != null) {
				if(isDaily && periodSchedule.getDaySchedule() != null && periodSchedule.getDaySchedule().isCheckRecoveryPoint()) {
					result.setFOptions(result.getFOptions() | QJDTO_B_CHECK_RECOVERYPOINT);
				}
				if(isWeekly && periodSchedule.getWeekSchedule() != null && periodSchedule.getWeekSchedule().isCheckRecoveryPoint()) {
					result.setFOptions(result.getFOptions() | QJDTO_B_CHECK_RECOVERYPOINT);
				}
				if(isMonthly && periodSchedule.getMonthSchedule() != null && periodSchedule.getMonthSchedule().isCheckRecoveryPoint()) {
					result.setFOptions(result.getFOptions() | QJDTO_B_CHECK_RECOVERYPOINT);
				}
			}
		}
		
		// check recovery point for custom/manual or Retain by Recovery Sets
		if(!(isDaily || isWeekly || isMonthly) && configuration.isCheckRecoveryPoint()) {
			result.setFOptions(result.getFOptions() | QJDTO_B_CHECK_RECOVERYPOINT);
		}
		
		if(!configuration.isD2dOrRPSDestType()) {
			BackupRPSDestSetting rpsSetting = configuration.getBackupRpsDestSetting();
			result.setRPSDataStoreDisplayName(rpsSetting.getRPSDataStoreDisplayName());
			result.setRPSDataStoreName(rpsSetting.getRPSDataStore());
			result.setRPSDataStoreDisplayName(rpsSetting.getRPSDataStoreDisplayName());
			result.setRpsPolicyID(rpsSetting.getRPSPolicyUUID());
			result.setRpsPolicyName(rpsSetting.getRPSPolicy());
			result.setRpsHostname(rpsSetting.getRpsHost().getRhostname());
			result.setRpsSID(rpsSetting.getRpsHost().getUuid());
			if(rpsPolicy != null && rpsPolicy.isEnableGDD()){
				result.setFOptions(result.getFOptions() | QJDTO_B_DISK_FORMAT_GDD);
			}

			if(rpsPolicy != null && configuration.isEnableEncryption()){
				String hashKey = BackupService.getInstance().getNativeFacade().getRPSDataStoreHashKey(
						rpsPolicy.getDataStoreSharedPath(), configuration.getUserName(), 
						configuration.getPassword(), 0, rpsPolicy.getEncryptionPassword());
				result.setPwszVDiskPassword(hashKey);
				if(StringUtil.isEmptyOrNull(hashKey)){
					String message = WebServiceMessages.getResource("backupNotRunFailHashKey", 
							rpsSetting.getRPSDataStoreDisplayName());
					BackupService.getInstance().getNativeFacade().addVMLogActivity(
							Constants.AFRES_AFALOG_ERROR, Constants.AFRES_AFJWBS_GENERAL, 
							new String[]{message, "","","",""}, backupVM.getInstanceUUID());
					return null;
				}
			}
		}
		
		result.setUsPreExitCode(configuration.getPreExitCode());
		result.setSoftwareOrHardwareSnapshotType(configuration.isSoftwareOrHardwareSnapshotType());
		result.setFailoverToSoftwareSnapshot(configuration.isFailoverToSoftwareSnapshot());
		result.setUseTransportableSnapshot(configuration.isUseTrasportableSnapshot());
		result.setDwSqlLogDays(configuration.getPurgeSQLLogDays());
		result.setDwExchangeLogDays(configuration.getPurgeExchangeLogDays());
		result.setLauncherInstanceUUID(virtualmachine.getVmInstanceUUID());
		
		return result;
	}
	
	private boolean isExchangeGRTFuncEnabled() {
		boolean result = false;
		
		try {
			String envValue = System.getenv("ARCUDP_EN_EXGRT");
			if (null == envValue) {
				return result;
			}	
			result = Boolean.TRUE.toString().equalsIgnoreCase(envValue); 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return result;
	}
}
