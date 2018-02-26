package com.ca.arcserve.edge.app.base.webservice.instantvm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.RecoveryPointWithNodeInfo;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcserve.edge.app.base.appdaos.EdgeInstantVM;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeInstantVMDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
import com.ca.arcserve.linuximaging.webservice.data.JobScript;
import com.ca.arcserve.linuximaging.webservice.data.JobStatus;
import com.ca.arcserve.linuximaging.webservice.edge.SynchronizeContext;

public class InstantVMServiceUtil {
	
	private static final Logger logger = Logger.getLogger(InstantVMServiceUtil.class);
	public static final String IVM_CONFIG_SUFFIX = ".xml";
	public static final String IVM_WINDOWS_DIR = "InstantVM\\Windows\\";
	public static final String IVM_LINUX_DIR = "InstantVM\\Linux\\";
	public static final String IVM_CONFIG_FOLDER_WINDOWS = EdgeCommonUtil.EdgeInstallPath + EdgeCommonUtil.EdgeCONFIGURATION_DIR + IVM_WINDOWS_DIR;
	public static final String IVM_CONFIG_FOLDER_LINUX = EdgeCommonUtil.EdgeInstallPath + EdgeCommonUtil.EdgeCONFIGURATION_DIR + IVM_LINUX_DIR;
	public static final String IVM_CONFIG_FOLDER_TEMP = EdgeCommonUtil.EdgeInstallPath + EdgeCommonUtil.EdgeCONFIGURATION_DIR + "InstantVM\\Temp\\";
	public static final String IP_SEPARATOR=".";
	
	private static IEdgeInstantVMDao instantVMDao = DaoFactory.getDao(IEdgeInstantVMDao.class);
	private static ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	
	public static <T> void saveObject(T t) {
		File config = new File(InstantVMServiceUtil.IVM_CONFIG_FOLDER_TEMP);
		if(!config.exists()){
			config.mkdirs();
		}
		JAXB.marshal(t, InstantVMServiceUtil.IVM_CONFIG_FOLDER_TEMP + t.getClass().getSimpleName() + InstantVMServiceUtil.IVM_CONFIG_SUFFIX);
	}
	
	public static <T> void saveObject(String path, T t){
		JAXB.marshal(t, path);
	}
	
	public static <T> T loadObject(String path, Class<T> type){
		return JAXB.unmarshal(path, type);
	}
	
	@Deprecated
	public static List<InstantVM> getInstantVMListFromFile() {
		List<InstantVM> result = new ArrayList<InstantVM>();
		File dir = new File (InstantVMServiceUtil.IVM_CONFIG_FOLDER_WINDOWS);
		if(!dir.exists() || !dir.isDirectory()){
			return result;
		}
		File[] files = dir.listFiles();
		if(files==null || files.length==0){
			return result;
		}
		for(File file : files){
			InstantVM vm =loadInstantVM(file);
			if(vm!=null){
				result.add(vm);
			}
		}
		return result;
	}
	
	public static List<InstantVM> getInstantVMListFromDB() {
		List<InstantVM> result = new ArrayList<InstantVM>();
		
		List<EdgeInstantVM> dbResult = new ArrayList<EdgeInstantVM>();		
		instantVMDao.as_edge_instantVM_getVMList(dbResult);
		
		for (EdgeInstantVM edgeInstantVM : dbResult) {
			InstantVM instantVM = parseXmlStringToObject(edgeInstantVM.getXmlContent(), InstantVM.class);
			
//			instantVM.setRecoveryServer(edgeInstantVM.getRecoveryServerName());
//			instantVM.getDetail().setDataStore(edgeInstantVM.getDataStoreName());
//			instantVM.getDetail().setRpsServer(edgeInstantVM.getRpsServerName());
//			instantVM.getDetail().setSharedFolder(edgeInstantVM.getShareFolderPath());
			
			if(!StringUtil.isEmptyOrNull(edgeInstantVM.getRecoveryServerName())){
				instantVM.setRecoveryServer(edgeInstantVM.getRecoveryServerName());
			}
			if(!StringUtil.isEmptyOrNull(edgeInstantVM.getDataStoreName())){
				instantVM.getDetail().setDataStore(edgeInstantVM.getDataStoreName());
			}
			if(!StringUtil.isEmptyOrNull(edgeInstantVM.getRpsServerName())){
				instantVM.getDetail().setRpsServer(edgeInstantVM.getRpsServerName());
			}
			if(!StringUtil.isEmptyOrNull(edgeInstantVM.getShareFolderPath())){
				instantVM.getDetail().setSharedFolder(edgeInstantVM.getShareFolderPath());
			}
			
			result.add(instantVM);
		}		
		
		return result;
	}

	private static InstantVM loadInstantVM(File file) {
		InstantVM config = null;
		try {
			config = JAXB.unmarshal(file,InstantVM.class);
		} catch (Exception e) {
			logger.error(e);
		}
		return config;
	}

	public static void save(InstantVM vm) {
		if(vm==null)
			return;
		
		saveToFile(vm);//for debugging, will remove in the future
		
		saveToDB(vm);
	}

	private static void saveToFile(InstantVM vm) {
		String path = getConfigFolder(vm);
		File config = new File(path);
		if(!config.exists()){
			config.mkdirs();
		}
		JAXB.marshal(vm, path + vm.getUuid() + InstantVMServiceUtil.IVM_CONFIG_SUFFIX);
	}
	
	private static void saveToDB(InstantVM vm) {
		if(vm == null)
			return;		
		
		instantVMDao.as_edge_instantVM_update(vm.getUuid(), 
				vm.getName(),
				vm.getNodeId(),
				vm.getRecoveryServerId(),
				vm.getDetail().getRpsServerId(),
				vm.getDetail().getDataStoreUuid(), 
				vm.getDetail().getSharedFolderId(),
				vm.getGatewayId(),
				parseObjectToXmlString(vm));		
	}	

	private static String getConfigFolder(InstantVM vm) {
		String path = InstantVMServiceUtil.IVM_CONFIG_FOLDER_WINDOWS;
		if(vm.getRecoveryServerType()==InstantVM.RECOVERY_SERVER_TYPE_LINUX){
			path = InstantVMServiceUtil.IVM_CONFIG_FOLDER_LINUX;
		}
		return path;
	}

	public static void remove(InstantVM vm) {
		if(vm==null)
			return;
		
		removeFromFile(vm);//for debugging, will remove in the future
		
		removeFromDB(vm);
	}

	private static void removeFromFile(InstantVM vm) {
		String path = getConfigFolder(vm);
		File file = new File(path + vm.getUuid() + InstantVMServiceUtil.IVM_CONFIG_SUFFIX);
		file.delete();
	}
	
	private static void removeFromDB(InstantVM vm) {
		if(vm==null)
			return;
		instantVMDao.as_edge_instantVM_delete(vm.getUuid());
	}
	
	public static String printObject(Object object){
		if(object == null)
			return null;
		String xml = parseObjectToXmlString(object);
		return EdgeCommonUtil.encryptXml(xml);
	}
	
	public static String parseObjectToXmlString(Object object) {
		if(object == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JAXB.marshal(object, baos);
		return baos.toString();
	}
	
	public static <T> T parseXmlStringToObject(String xml, Class<T> type) {
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
		T result = null;
		
		try {
			result = JAXB.unmarshal(bais, type); 
		} catch (Exception e) {
			logger.error(e);
		}
		
		return result;
	} 
	
	
	public static long convert2JobStatus(int status) {
		switch (status) {
		case JobStatus.READY:
			return SynchronizeContext.JOBSTATUS_SKIPPED;
		case JobStatus.IDLE:
			return SynchronizeContext.JOBSTATUS_IDLE;
		case JobStatus.FINISHED:
			return SynchronizeContext.JOBSTATUS_FINISHED;
		case JobStatus.CANCELLED:
			return SynchronizeContext.JOBSTATUS_CANCELLED;
		case JobStatus.FAILED:
			return SynchronizeContext.JOBSTATUS_FAILED;
		case JobStatus.INCOMPLETE:
			return SynchronizeContext.JOBSTATUS_INCOMPLETE;
		case JobStatus.ACTIVE:
			return SynchronizeContext.JOBSTATUS_ACTIVE;
		case JobStatus.WAITING:
			return SynchronizeContext.JOBSTATUS_WAITING;
		case JobStatus.CRASHED:
			return SynchronizeContext.JOBSTATUS_CRASH;
		case JobStatus.NEEDREBOOT:
			return SynchronizeContext.JOBSTATUS_STOP;
		case JobStatus.FAILED_NO_LICENSE:
			return SynchronizeContext.JOBSTATUS_LICENSE_FAILED;
		default:
			return SynchronizeContext.JOBSTATUS_MISSED;
		}
	}
	
	public static long convert2JobMethod(int jobType) {
		switch (jobType) {
		case JobScript.BACKUP:
			return SynchronizeContext.All;
		case JobScript.BACKUP_FULL:
			return SynchronizeContext.Full;
		case JobScript.BACKUP_INCREMENTAL:
			return SynchronizeContext.Incremental;
		case JobScript.BACKUP_VERIFY:
			return SynchronizeContext.Resync;
		case JobScript.RESTORE:
		case JobScript.RESTORE_FILE:
		case JobScript.RESTORE_VM:
		case JobScript.RESTORE_VOLUME:
			return SynchronizeContext.All;
		case JobScript.RESTORE_BMR:
			return SynchronizeContext.All;
		default:
			return SynchronizeContext.Unknown;
		}
	}
	
	public static int convert2JobType(int jobType) {
		switch (jobType) {
		case JobScript.BACKUP:
		case JobScript.BACKUP_FULL:
		case JobScript.BACKUP_INCREMENTAL:
		case JobScript.BACKUP_VERIFY:
			return SynchronizeContext.JOBTYPE_BACKUP;
		case JobScript.RESTORE:
		case JobScript.RESTORE_FILE:
		case JobScript.RESTORE_VOLUME:
			return SynchronizeContext.JOBTYPE_RESTORE;
		case JobScript.RESTORE_BMR:
			return SynchronizeContext.JOBTYPE_BMR;
		case JobScript.RESTORE_VM:
			return SynchronizeContext.JOBTYPE_VM_RECOVERY;
		default:
			return SynchronizeContext.JOBTYPE_NONE;
		}
	}
	
	public static void generateInstantVMActivityLog(Severity severity, int nodeId, String nodename, String message, long jobType) {
		ActivityLog log = new ActivityLog();
		log.setHostId(nodeId);
		log.setNodeName(nodename!=null? nodename : "");
		log.setModule(Module.InstantVM);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);
		if(jobType >= 0){
			log.setJobType((int)jobType);
		}
		try {
			logService.addLog(log);
		} catch (Exception e) {
			logger.error("Generate activity log for instant VM failed.",e);
		}
	}
	
	public static boolean isIPv4Address(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}

		String[] temp = value.split(IP_SEPARATOR);
		if (temp.length != 4) {
			return false;
		}
		for (String str : temp) {
			int num = -1;
			try {
				num = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				return false;
			}
			if (num < 0 || num > 255) {
				return false;
			}
		}
		
		return true;
	}
	
	public static RecoveryPointWithNodeInfo filterRecoveryPointWithNodeInfoByProtectedNode(List<RecoveryPointWithNodeInfo> rpWIthNode, ProtectedNodeInDestination node) {
		if(rpWIthNode == null || rpWIthNode.size() == 0 || node == null)
			return null;
		
		boolean ipAddress = isIPv4Address(node.getNodeName());
		if(ipAddress){
			return filterRecoveryPointWithNodeInfoByNodeUUID(rpWIthNode, node.getNodeUuid());
		}else{//hostname or vmname@hypervisor
			RecoveryPointWithNodeInfo result = filterRecoveryPointWithNodeInfoByNodeName(rpWIthNode, node.getNodeName());
			if(result == null){
				result = filterRecoveryPointWithNodeInfoByNodeUUID(rpWIthNode, node.getNodeUuid());
			}
			return result;
		}
		
	}
	
	private static RecoveryPointWithNodeInfo filterRecoveryPointWithNodeInfoByNodeName(List<RecoveryPointWithNodeInfo> rpWIthNode, String nodeName) {
		if (nodeName == null || nodeName.isEmpty()) {
			return null;
		}
		for(RecoveryPointWithNodeInfo protectedNode : rpWIthNode){
			if(nodeName.equalsIgnoreCase(protectedNode.getNodeName())){
				return protectedNode;
			}
		}
		return null;
	}
	
	private static RecoveryPointWithNodeInfo filterRecoveryPointWithNodeInfoByNodeUUID(List<RecoveryPointWithNodeInfo> rpWIthNode, String nodeUuid) {
		if (nodeUuid == null || nodeUuid.isEmpty()) {
			return null;
		}
		for(RecoveryPointWithNodeInfo protectedNode : rpWIthNode){
			if(protectedNode == null || protectedNode.getRecoveryPoints() == null || protectedNode.getRecoveryPoints().size() == 0){
				continue;
			}else{
				String uuid = protectedNode.getRecoveryPoints().get(0).getNodeUuid();
				if(nodeUuid.equalsIgnoreCase(uuid)){
					return protectedNode;
				}
			}
		}
		return null;
	}
	
	public static boolean loadDestiantionInfo(BackupConfiguration backupConfig, ProtectedNodeInDestination node){
		if(backupConfig == null || backupConfig.getDestination() == null){
			return false;
		}
		node.setDestination(backupConfig.getDestination());
		node.setUsername(backupConfig.getUserName());
		node.setPassword(backupConfig.getPassword());
		return true;
	}
	
	public static boolean loadDestiantionInfo(VMBackupConfiguration vmConfig, ProtectedNodeInDestination node){
		if(vmConfig == null || vmConfig.getBackupVM() == null || vmConfig.getBackupVM().getDestination() == null){
			return false;
		}
		node.setDestination(vmConfig.getBackupVM().getDestination());
		node.setUsername(vmConfig.getBackupVM().getDesUsername());
		node.setPassword(vmConfig.getBackupVM().getDesPassword());
		return true;
	}
	
}
