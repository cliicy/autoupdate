package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.scheduler.VSPhereJobLauncherForGroupOrSingleVM;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.util.VSphereBackupConfigurationXMLParser;

public class VSphereBackupConfigurationXMLDAO extends XMLDAO {
	private static final Logger logger = Logger.getLogger(VSphereBackupConfigurationXMLDAO.class);
	private VSphereBackupConfigurationXMLParser xmlParser = new VSphereBackupConfigurationXMLParser();
	
	synchronized public void saveVM(String folderPath,VSphereBackupConfiguration configuration,BackupVM vm)throws Exception{
		
		String vcFolder=folderPath;
		checkDirectory(vcFolder);
		
		//parser and save
		Document xmlDocument=VSphereBackupConfigurationToDocument(configuration,vm,false);			
			
		String filePath = vcFolder +"\\"+ vm.getInstanceUUID()+".xml";
		doc2XmlFile(xmlDocument, filePath);
		
	}
	
	public Document VSphereBackupConfigurationToDocument(VSphereBackupConfiguration configuration,BackupVM vm,boolean isSaveProxy) throws Exception{
		String plainPrePostPassword = null;
		String plainPassword = null;
		String plainProxyPassword = null;
		String plainMailPassword = null;
		String plainOsPassword = null;
		String plainEsxPassword = null;
		String plainDesPassword = null;
		String plainEncryptionKey = null;
		String plainvSpherePassword = null;
		String plainRPSPassword = null;
		String plainvAppVCenterPassword = null;
		List<String> plainChildVMEsxPasswordList = null;
		
		boolean bNeedResetPrePost = false;
		
		if (vm != null && !StringUtil.isEmptyOrNull(vm.getUsername()) && vm.getPassword() != null 
				&& StringUtil.isEmptyOrNull(configuration.getPrePostUserName())) {
			plainPrePostPassword = vm.getPassword();
			configuration.setPrePostUserName(vm.getUsername());
			configuration.setPrePostPassword(CommonService.getInstance().getNativeFacade().encrypt(vm.getPassword()));
			bNeedResetPrePost = true;
		} else if (configuration.getPrePostPassword() != null) {
			plainPrePostPassword = configuration.getPrePostPassword();
			configuration.setPrePostPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getPrePostPassword()));	
		}
		if (configuration.getPassword() != null)
		{
			plainPassword = configuration.getPassword();
			configuration.setPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getPassword()));
		}
		if (configuration.getEmail() != null && configuration.getEmail().getProxyPassword() != null)
		{
			plainProxyPassword = configuration.getEmail().getProxyPassword();
			configuration.getEmail().setProxyPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getEmail().getProxyPassword()));
		}
		if (configuration.getEmail() != null && configuration.getEmail().getMailPassword() != null)
		{
			plainMailPassword = configuration.getEmail().getMailPassword();
			configuration.getEmail().setMailPassword(
				CommonService.getInstance().getNativeFacade().encrypt(configuration.getEmail().getMailPassword()));
		}
		
		if(isSaveProxy && configuration.getvSphereProxy()!=null && configuration.getvSphereProxy().getVSphereProxyPassword()!=null){
			plainvSpherePassword = configuration.getvSphereProxy().getVSphereProxyPassword();
			configuration.getvSphereProxy().setVSphereProxyPassword(CommonService.getInstance().getNativeFacade().encrypt(configuration.getvSphereProxy().getVSphereProxyPassword()));
		}
		
		
		if(vm!=null){
			if(vm.getEsxPassword()!=null){
				plainEsxPassword = vm.getEsxPassword();
				vm.setEsxPassword(CommonService.getInstance().getNativeFacade().encrypt(vm.getEsxPassword()));
			}
			if(vm.getPassword()!=null){
				plainOsPassword = vm.getPassword();
				vm.setPassword(CommonService.getInstance().getNativeFacade().encrypt(vm.getPassword()));
			}
			if(!StringUtil.isEmptyOrNull(vm.getDesPassword())){
				plainDesPassword = vm.getDesPassword();
				vm.setDesPassword(CommonService.getInstance().getNativeFacade().encrypt(vm.getDesPassword()));
			}
			if(vm.getVAppVCInfos()!=null && vm.getVAppVCInfos()[0].getPassword()!=null) {
				plainvAppVCenterPassword = vm.getVAppVCInfos()[0].getPassword();
				vm.getVAppVCInfos()[0].setPassword(CommonService.getInstance().getNativeFacade().encrypt(plainvAppVCenterPassword));
			}
			if(vm.getVAppMemberVMs() != null) {
				plainChildVMEsxPasswordList = new ArrayList<String>();
				for(BackupVM childVM : vm.getVAppMemberVMs()) {
					if(childVM.getEsxPassword()!=null) {
						plainChildVMEsxPasswordList.add(childVM.getEsxPassword());
						childVM.setEsxPassword(CommonService.getInstance().getNativeFacade().encrypt(childVM.getEsxPassword()));
					}
				}
			}
		}
		
		if (!StringUtil.isJustEmptyOrNull(configuration.getEncryptionKey()))
		{
			plainEncryptionKey = configuration.getEncryptionKey();
			configuration.setEncryptionKey(
					CommonService.getInstance().getNativeFacade().encrypt(plainEncryptionKey));
			
		}
		
		if(configuration.getBackupRpsDestSetting() != null 
				&& configuration.getBackupRpsDestSetting().getRpsHost() != null 
				&& configuration.getBackupRpsDestSetting().getRpsHost().getPassword() != null){
			plainRPSPassword = configuration.getBackupRpsDestSetting().getRpsHost().getPassword();
			configuration.getBackupRpsDestSetting().getRpsHost().setPassword(
					CommonService.getInstance().getNativeFacade().encrypt(plainRPSPassword));
		}
		
		Document xmlDocument=xmlParser.saveXML(configuration,vm,isSaveProxy);			
		
		if (bNeedResetPrePost)
		{
			configuration.setPrePostUserName(null);
			configuration.setPrePostPassword(null);
		}
		else if (configuration.getPrePostPassword() != null){
			configuration.setPrePostPassword(plainPrePostPassword);
		}
		
		if (configuration.getPassword() != null){
			configuration.setPassword(plainPassword);
		}
		
		if (configuration.getEmail() != null && configuration.getEmail().getProxyPassword() != null){
			configuration.getEmail().setProxyPassword(plainProxyPassword);
		}
		if (configuration.getEmail() != null && configuration.getEmail().getMailPassword() != null){
			configuration.getEmail().setMailPassword(plainMailPassword);
		}
		
		if(isSaveProxy && configuration.getvSphereProxy()!=null && configuration.getvSphereProxy().getVSphereProxyPassword()!=null){
			configuration.getvSphereProxy().setVSphereProxyPassword(plainvSpherePassword);
		}
		
		if(vm!=null){
			if(vm.getEsxPassword()!=null){
				vm.setEsxPassword(plainEsxPassword);
			}
			if(vm.getPassword()!=null){
				vm.setPassword(plainOsPassword);
			}
			if(!StringUtil.isEmptyOrNull(vm.getDesPassword())){
				vm.setDesPassword(plainDesPassword);
			}
			if(vm.getVAppVCInfos()!=null && vm.getVAppVCInfos()[0].getPassword()!=null) {
				vm.getVAppVCInfos()[0].setPassword(plainvAppVCenterPassword);
			}
			if(vm.getVAppMemberVMs() != null && plainChildVMEsxPasswordList != null) {
				int index = 0;
				for(BackupVM childVM : vm.getVAppMemberVMs()) {
					if(childVM.getEsxPassword()!=null) {
						childVM.setEsxPassword(plainChildVMEsxPasswordList.get(index++));
					}
				}
			}
		}
		if (!StringUtil.isJustEmptyOrNull(configuration.getEncryptionKey()))
		{
			configuration.setEncryptionKey(plainEncryptionKey);
		}
		
		if(configuration.getBackupRpsDestSetting() != null 
				&& configuration.getBackupRpsDestSetting().getRpsHost() != null 
				&& configuration.getBackupRpsDestSetting().getRpsHost().getPassword() != null){
			configuration.getBackupRpsDestSetting().getRpsHost().setPassword(plainRPSPassword);
		}
		
		return xmlDocument;
	}

	public VMBackupConfiguration XMLDocumentToVMBackupConfiguration(Document xmlDocument)throws Exception{
		
		VMBackupConfiguration config = xmlParser.loadXML(xmlDocument);	
		
		if (config.getPrePostPassword() != null)
		{
			config.setPrePostPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getPrePostPassword()));
		}
		if (config.getPassword() != null)
		{
			config.setPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getPassword()));
		}
		if (config.getEmail() != null && config.getEmail().getProxyPassword() != null)
		{
			config.getEmail().setProxyPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getEmail().getProxyPassword()));
		}
		if (config.getEmail() != null && config.getEmail().getMailPassword() != null)
		{
			config.getEmail().setMailPassword(
				CommonService.getInstance().getNativeFacade().decrypt(config.getEmail().getMailPassword()));
		}
		
		if(config.getVSphereProxy()!=null){
			if(config.getVSphereProxy().getVSphereProxyPassword()!=null && !config.getVSphereProxy().getVSphereProxyPassword().equals("")){
				config.getVSphereProxy().setVSphereProxyPassword(CommonService.getInstance().getNativeFacade().decrypt(config.getVSphereProxy().getVSphereProxyPassword()));
			}
		}
		
		if(config.getBackupVM()!=null){
			if(config.getBackupVM().getPassword()!= null && !config.getBackupVM().getPassword().equals("")){
				config.getBackupVM().setPassword(CommonService.getInstance().getNativeFacade().decrypt(config.getBackupVM().getPassword()));
			}
			
			if(config.getBackupVM().getEsxPassword()!= null){
				config.getBackupVM().setEsxPassword(CommonService.getInstance().getNativeFacade().decrypt(config.getBackupVM().getEsxPassword()));
			}
			
			if(config.getBackupVM().getDesPassword()!= null && !config.getBackupVM().getDesPassword().equals("")){
				config.getBackupVM().setDesPassword(CommonService.getInstance().getNativeFacade().decrypt(config.getBackupVM().getDesPassword()));
			}
			if(config.getBackupVM().getVAppVCInfos()!=null && config.getBackupVM().getVAppVCInfos().length > 0) {
				String vAppVCPassword = config.getBackupVM().getVAppVCInfos()[0].getPassword();
				config.getBackupVM().getVAppVCInfos()[0].setPassword(CommonService.getInstance().getNativeFacade().decrypt(vAppVCPassword));
			}
			if(config.getBackupVM().getVAppMemberVMs() != null) {
				for(BackupVM childVM : config.getBackupVM().getVAppMemberVMs()) {
					childVM.setEsxPassword(CommonService.getInstance().getNativeFacade().decrypt(childVM.getEsxPassword()));
				}
			}
		}
		
		if(!StringUtil.isJustEmptyOrNull(config.getEncryptionKey()))
		{
			config.setEncryptionKey(
					CommonService.getInstance().getNativeFacade().decrypt(config.getEncryptionKey()));
		}
		
		if(config.getBackupRpsDestSetting() != null 
				&& config.getBackupRpsDestSetting().getRpsHost() != null
				&& config.getBackupRpsDestSetting().getRpsHost().getPassword() != null){
			String pwd = config.getBackupRpsDestSetting().getRpsHost().getPassword();
			config.getBackupRpsDestSetting().getRpsHost().setPassword(
					CommonService.getInstance().getNativeFacade().decrypt(pwd));
		}
		return config;		
	}
	
	private void checkDirectory(String vcFolder) {
		File file=new File(vcFolder);
		
		if(!file.exists())
			file.mkdirs();
	}
	
	synchronized public VMBackupConfiguration get(String folderPath, VirtualMachine vm) throws Exception{	
		String vmInstanceUUID = vm.getVmInstanceUUID();
		String filePath=folderPath+"\\"+vmInstanceUUID+".xml";
		File file = new File(filePath);
		if (!file.exists()) {
			logger.info("File does not exist: " + filePath);
			return null;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		db = dbf.newDocumentBuilder();		
		Document doc = db.parse(file);
		
		VMBackupConfiguration config = XMLDocumentToVMBackupConfiguration(doc);	
		
		return config;		
	}


//	synchronized public VMBackupConfiguration get(String folderPath, VirtualMachine vm) throws Exception
//	{
//		VMBackupConfiguration config = null;
//		Map<String, List<String>> uuidOfAllVM = VSPhereJobLauncherForGroupOrSingleVM.getUUIDOfAllVM(null);
//		for (Entry<String , List<String>> entry : uuidOfAllVM.entrySet())
//		{
//			String groupName = entry.getKey();
//			List<String> vmUUIDInGroup = entry.getValue();
//			for (String instanceUUID : vmUUIDInGroup)
//			{
//				if (instanceUUID.compareToIgnoreCase(vm.getVmInstanceUUID()) == 0)
//				{
//					String filePath=folderPath+"\\";
//					if (groupName != null)
//						filePath += groupName + "\\" + instanceUUID +".xml";
//					else
//						filePath += instanceUUID +".xml";
//
//					File file = new File(filePath);
//					
//					if (!file.exists()) {
//						logger.info("File does not exist: " + filePath);
//						return null;
//					}
//					
//					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//					DocumentBuilder db;
//					db = dbf.newDocumentBuilder();		
//					Document doc = db.parse(file);
//					
//					config = XMLDocumentToVMBackupConfiguration(doc);	
//				}
//			}
//		}
//		
//		return config;		
//	}
	
	public VSphereBackupConfiguration VMConfigToVSphereConfig(VMBackupConfiguration vmConfig){
		if(vmConfig == null){
			return null;
		}
		VSphereBackupConfiguration vsphereConfig = new VSphereBackupConfiguration();
		vsphereConfig.setIncrementalBackupSchedule(vmConfig.getIncrementalBackupSchedule());
		vsphereConfig.setFullBackupSchedule(vmConfig.getFullBackupSchedule());
		vsphereConfig.setResyncBackupSchedule(vmConfig.getResyncBackupSchedule());
		//shaji02
		vsphereConfig.setAdvanceSchedule(vmConfig.getAdvanceSchedule());
		vsphereConfig.setDestination(vmConfig.getDestination());
		vsphereConfig.setUserName(vmConfig.getUserName());
		vsphereConfig.setPassword(vmConfig.getPassword());
		vsphereConfig.setRetentionCount(vmConfig.getRetentionCount());
		vsphereConfig.setCompressionLevel(vmConfig.getCompressionLevel());
		vsphereConfig.setEnableEncryption(vmConfig.isEnableEncryption());
		vsphereConfig.setEncryptionAlgorithm(vmConfig.getEncryptionAlgorithm());
		vsphereConfig.setEncryptionKey(vmConfig.getEncryptionKey());
		vsphereConfig.setThrottling(vmConfig.getThrottling());
		vsphereConfig.setCommandBeforeBackup(vmConfig.getCommandBeforeBackup());
		vsphereConfig.setCommandAfterBackup(vmConfig.getCommandAfterBackup());
		vsphereConfig.setCommandAfterSnapshot(vmConfig.getCommandAfterSnapshot());
		vsphereConfig.setEnablePreExitCode(vmConfig.isEnablePreExitCode());
		vsphereConfig.setPreExitCode(vmConfig.getPreExitCode());
		vsphereConfig.setSkipJob(vmConfig.isSkipJob());
		vsphereConfig.setPrePostUserName(vmConfig.getPrePostUserName());
		vsphereConfig.setPrePostPassword(vmConfig.getPrePostPassword());
		vsphereConfig.setChangedBackupDest(vmConfig.isChangedBackupDest());
		vsphereConfig.setChangedBackupDestType(vmConfig.getChangedBackupDestType());
		vsphereConfig.setBackupStartTime(vmConfig.getBackupStartTime());
		vsphereConfig.setStartTime(vmConfig.getStartTime());
		vsphereConfig.setFailoverToSoftwareSnapshot(vmConfig.isFailoverToSoftwareSnapshot());
		vsphereConfig.setUseTrasportableSnapshot(vmConfig.isUseTrasportableSnapshot());
		vsphereConfig.setSoftwareOrHardwareSnapshotType(vmConfig.isSoftwareOrHardwareSnapshotType());
		vsphereConfig.setPurgeSQLLogDays(vmConfig.getPurgeSQLLogDays());
		vsphereConfig.setPurgeExchangeLogDays(vmConfig.getPurgeExchangeLogDays());
		vsphereConfig.setEnableSpaceNotification(vmConfig.isEnableSpaceNotification());
		vsphereConfig.setSpaceMeasureNum(vmConfig.getSpaceMeasureNum());
		vsphereConfig.setSpaceMeasureUnit(vmConfig.getSpaceMeasureUnit());
		vsphereConfig.setAllowSendEmail(vmConfig.isAllowSendEmail());
		vsphereConfig.setHasSendEmail(vmConfig.isHasSendEmail());
		vsphereConfig.setSpaceSavedAfterCompression(vmConfig.getSpaceSavedAfterCompression());
		vsphereConfig.setGrowthRate(vmConfig.getGrowthRate());
		vsphereConfig.setAdminUserName(vmConfig.getAdminUserName());
		vsphereConfig.setAdminPassword(vmConfig.getAdminPassword());
		vsphereConfig.setEmail(vmConfig.getEmail());
		vsphereConfig.setRetentionPolicy(vmConfig.getRetentionPolicy());
		vsphereConfig.setGenerateCatalog(vmConfig.isGenerateCatalog());
		vsphereConfig.setExchangeGRTSetting(vmConfig.getExchangeGRTSetting());
		
		//fanda03 fix issue 102889
		vsphereConfig.setPreAllocationBackupSpace(vmConfig.getPreAllocationBackupSpace());
		//
		vsphereConfig.setD2dOrRPSDestType(vmConfig.isD2dOrRPSDestType());
		vsphereConfig.setBackupRpsDestSetting(vmConfig.getBackupRpsDestSetting());
		vsphereConfig.setBackupDataFormat(vmConfig.getBackupDataFormat());
		
		//flag for enable/disable plan
		vsphereConfig.setDisablePlan(vmConfig.isDisablePlan());
		
		vsphereConfig.setVmwareTransports(vmConfig.getVmwareTransports());
		
		vsphereConfig.setCheckRecoveryPoint(vmConfig.isCheckRecoveryPoint());
		vsphereConfig.setVmwareQuiescenceMethod(vmConfig.getVmwareQuiescenceMethod());
		vsphereConfig.setHyperVConsistentSnapshotType(vmConfig.getHyperVConsistentSnapshotType());
		vsphereConfig.setHyperVSnapshotSeparationIndividually(vmConfig.isHyperVSnapshotSeparationIndividually());
		
		return vsphereConfig;
	}
}
