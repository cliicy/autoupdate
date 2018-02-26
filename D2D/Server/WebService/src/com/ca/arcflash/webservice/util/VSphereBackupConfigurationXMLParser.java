package com.ca.arcflash.webservice.util;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.D2DTime;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereProxy;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;

public class VSphereBackupConfigurationXMLParser {

	private static XMLBeanMapper<VSphereBackupConfiguration> vSphereMapper;
	private static XMLBeanMapper<VMBackupConfiguration> vmBackupMapper;
	private static XMLBeanMapper<BackupEmail> backupEmailMapper;
	private static XMLBeanMapper<BackupSchedule> backupScheduleMapper;
	private static XMLBeanMapper<BackupVM> backupVMMapper;
	private static XMLBeanMapper<VSphereProxy> proxyMapper;
	private static XMLBeanMapper<D2DTime> startTimeMapper;
	private static XMLBeanMapper<RetentionPolicy> retentionMapper;
	private static XMLBeanMapper<BackupRPSDestSetting> backupRpsDestSettingMapper;
	private static XMLBeanMapper<RpsHost> rpsHostMapper;
	private static XMLBeanMapper<VirtualCenter> vcMapper;

	static {
		try {
			vSphereMapper = new XMLBeanMapper<VSphereBackupConfiguration>(
					VSphereBackupConfiguration.class);
			vmBackupMapper = new XMLBeanMapper<VMBackupConfiguration>(
					VMBackupConfiguration.class);
			backupEmailMapper = new XMLBeanMapper<BackupEmail>(
					BackupEmail.class);
			backupScheduleMapper = new XMLBeanMapper<BackupSchedule>(
					BackupSchedule.class);
			backupVMMapper = new XMLBeanMapper<BackupVM>(BackupVM.class);
			proxyMapper = new XMLBeanMapper<VSphereProxy>(VSphereProxy.class);
			startTimeMapper = new XMLBeanMapper<D2DTime>(D2DTime.class);
			retentionMapper = new XMLBeanMapper<RetentionPolicy>(RetentionPolicy.class);
			backupRpsDestSettingMapper = new XMLBeanMapper<BackupRPSDestSetting>(BackupRPSDestSetting.class);
			rpsHostMapper = new XMLBeanMapper<RpsHost>(RpsHost.class);
			vcMapper = new XMLBeanMapper<VirtualCenter>(VirtualCenter.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public Document saveXML(VSphereBackupConfiguration configuration,BackupVM vm,boolean isSaveProxy) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();

		Element rootElement = vSphereMapper.saveBean(configuration, doc,
				"VMBackupConfiguration");
		doc.appendChild(rootElement);

		if (configuration.getEmail() == null)
			configuration.setEmail(new BackupEmail());
		Element emailElement = backupEmailMapper.saveBean(configuration.getEmail(), doc);
		rootElement.appendChild(emailElement);

		if (configuration.getFullBackupSchedule() == null)
			configuration.setFullBackupSchedule(new BackupSchedule());
		Element fullSheduleElement = backupScheduleMapper.saveBean(configuration.getFullBackupSchedule(), doc,"FullBackupSchedule");
		rootElement.appendChild(fullSheduleElement);

		if (configuration.getIncrementalBackupSchedule() == null)
			configuration.setIncrementalBackupSchedule(new BackupSchedule());
		Element incrementalSheduleElement = backupScheduleMapper.saveBean(configuration.getIncrementalBackupSchedule(), doc,"IncrementalBackupSchedule");
		rootElement.appendChild(incrementalSheduleElement);

		if (configuration.getResyncBackupSchedule() == null)
			configuration.setResyncBackupSchedule(new BackupSchedule());
		Element resyncSheduleElement = backupScheduleMapper.saveBean(configuration.getResyncBackupSchedule(), doc,"ResyncBackupSchedule");
		rootElement.appendChild(resyncSheduleElement);
		
		if(configuration.getStartTime() == null) 
			configuration.setStartTime(new D2DTime());
		Element startTimeElement = startTimeMapper.saveBean(configuration.getStartTime(), doc, "StartTime");
		rootElement.appendChild(startTimeElement);
		
		if(configuration.getRetentionPolicy() != null) {
			Element retentionElement = retentionMapper.saveBean(configuration.getRetentionPolicy(), doc, "RetentionPolicy");
			rootElement.appendChild(retentionElement);
		}
		
		if(configuration.getBackupRpsDestSetting() == null)
			configuration.setBackupRpsDestSetting(new BackupRPSDestSetting());
		Element backupRpsDestSettingElement = 	backupRpsDestSettingMapper.saveBean(configuration.getBackupRpsDestSetting() , doc, "BackupRpsDestSetting");
		rootElement.appendChild(backupRpsDestSettingElement);
		
		if(configuration.getBackupRpsDestSetting().getRpsHost() == null)
			configuration.getBackupRpsDestSetting().setRpsHost(new RpsHost());
		
		RpsHost rpsHost = configuration.getBackupRpsDestSetting().getRpsHost();
		Element rpsHostElement = rpsHostMapper.saveBean(rpsHost , doc, "rpsHost");
		backupRpsDestSettingElement.appendChild(rpsHostElement);

		if(vm != null){
			Element oneVM = backupVMMapper.saveBean(vm, doc, "VM");
			rootElement.appendChild(oneVM);	
			if(vm.getVAppVCInfos() != null && vm.getVAppVCInfos().length > 0) {
				for(VirtualCenter vc : Arrays.asList(vm.getVAppVCInfos())) {
					Element vcEle = vcMapper.saveBean(vc, doc, "VirtualCenter");
					oneVM.appendChild(vcEle);
				}
			}
			
			if(vm.getVAppMemberVMs() != null && vm.getVAppMemberVMs().length > 0) {
				for(BackupVM memberVM : Arrays.asList(vm.getVAppMemberVMs())) {
					Element vmEle = backupVMMapper.saveBean(memberVM, doc, "MemberVM");
					oneVM.appendChild(vmEle);
				}
			}
		}
		if(isSaveProxy && configuration.getvSphereProxy()!=null){
			Element proxy = proxyMapper.saveBean(configuration.getvSphereProxy(), doc, "VSphereProxy");
			rootElement.appendChild(proxy);	
		}
		//wanqi06
		if(configuration.getAdvanceSchedule() != null){
			Element advScheduleElement = AdvanceScheduleXMLParser.getElement(configuration.getAdvanceSchedule(), doc);
			rootElement.appendChild(advScheduleElement);
		}
		
		if (configuration.getArchiveConfig() != null && vm != null && !StringUtil.isEmptyOrNull(vm.getInstanceUUID())) {
			VSphereArchiveToTapeUtils.saveArchiveToTape(vm.getInstanceUUID(), configuration.getArchiveConfig());
		}
		
		return doc;
	}
	
	public VMBackupConfiguration loadXML(Document doc) throws Exception {
		Element vmNode = doc.getDocumentElement();
		VMBackupConfiguration config = vmBackupMapper.loadBean(vmNode);

		NodeList childNodeList = vmNode.getChildNodes();
		for (int j = 0; j < childNodeList.getLength(); j++) {
			Node childNode = childNodeList.item(j);
			if ("BackupEmail".equals(childNode.getNodeName())) {
				config.setEmail(backupEmailMapper.loadBean(childNode));
			}
			if ("FullBackupSchedule".equals(childNode.getNodeName())) {
				config.setFullBackupSchedule(backupScheduleMapper
						.loadBean(childNode));
			}
			if ("IncrementalBackupSchedule".equals(childNode.getNodeName())) {
				config.setIncrementalBackupSchedule(backupScheduleMapper
						.loadBean(childNode));
			}
			if ("ResyncBackupSchedule".equals(childNode.getNodeName())) {
				config.setResyncBackupSchedule(backupScheduleMapper
						.loadBean(childNode));
			}
			if ("VM".equals(childNode.getNodeName())) {
				config.setBackupVM(backupVMMapper.loadBean(childNode));
				
				NodeList vcNodeList = doc.getElementsByTagName("VirtualCenter");
				if(vcNodeList != null && vcNodeList.getLength() > 0) {
					ArrayList<VirtualCenter> vcList = new ArrayList<VirtualCenter>();
					for(int i = 0; i < vcNodeList.getLength(); ++i) {
						Node vcNode = vcNodeList.item(i);
						VirtualCenter vc = vcMapper.loadBean(vcNode);
						vcList.add(vc);
					}
					config.getBackupVM().setVAppVCInfos(vcList.toArray(new VirtualCenter[vcList.size()]));
				}
				
				NodeList vmNodeList = doc.getElementsByTagName("MemberVM");
				if(vmNodeList != null && vmNodeList.getLength() > 0) {
					ArrayList<BackupVM> vmList = new ArrayList<BackupVM>();
					for(int i = 0; i < vmNodeList.getLength(); ++i) {
						Node memberVMNode = vmNodeList.item(i);
						BackupVM vm = backupVMMapper.loadBean(memberVMNode);
						vmList.add(vm);
					}
					config.getBackupVM().setVAppMemberVMs(vmList.toArray(new BackupVM[vmList.size()]));
				}
			}
			
			if("VSphereProxy".equals(childNode.getNodeName())){
				config.setVSphereProxy(proxyMapper.loadBean(childNode));
			}
			
			if("StartTime".equals(childNode.getNodeName())) {
				config.setStartTime(startTimeMapper.loadBean(childNode));
			}
			
			if("RetentionPolicy".equals(childNode.getNodeName())) {
				config.setRetentionPolicy(retentionMapper.loadBean(childNode));
			}
			
			//BackupRpsDestSetting
			if("BackupRpsDestSetting".equals(childNode.getNodeName())){
				BackupRPSDestSetting backupRpsDestSetting = backupRpsDestSettingMapper.loadBean(childNode);
				config.setBackupRpsDestSetting(backupRpsDestSetting);
				NodeList rpsHostNodeList = doc.getElementsByTagName("rpsHost");
				if (rpsHostNodeList != null && rpsHostNodeList.getLength() > 0 ){
					backupRpsDestSetting.setRpsHost(rpsHostMapper.loadBean(rpsHostNodeList.item(0)));
				}
			}
		}
		
		//wanqi06 load the advance schedule
		AdvanceSchedule advanceSchedule = AdvanceScheduleXMLParser.getAdvanceScheduleFromXML(doc);
		if(advanceSchedule!=null){
			config.setAdvanceSchedule(advanceSchedule);
		}
		
		if(config.getBackupVM() != null) {
			ArchiveConfig archiveConfig = VSphereArchiveToTapeUtils.getArchiveToTapeConfig(config.getBackupVM().getInstanceUUID());
			if(archiveConfig!=null){
				config.setArchiveConfig(archiveConfig);
			}
		}
		return config;
	}
}
