package com.ca.arcflash.webservice.service.internal;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.jni.model.JBackupItem;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.webservice.data.restore.CopyJob;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.edge.data.d2dstatus.AgentOsInfoType;
import com.ca.arcflash.webservice.jni.model.JBackupInfo;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JJobScriptNode;

public class RecoveryPointConverter {
	private static final Logger logger = Logger.getLogger(RecoveryPointConverter.class);

	private int getTimeZoneOffset(Date date) {
		logger.debug("getTimeZoneOffset:"+ date );
		TimeZone tz = Calendar.getInstance().getTimeZone();
		int offset = tz.getOffset(date.getTime());
		logger.debug("getTimeZoneOffset:"+ offset );
		return offset;
	}
	public RecoveryPoint[] convert2RecoveryPoints(JRestorePoint[] restorePoints) {
		logger.debug(restorePoints==null?"restorePoints are null":("restorePoints length:"+restorePoints.length));
		if (restorePoints==null || restorePoints.length==0)
			return new RecoveryPoint[0];
		
		List<RecoveryPoint> result = new ArrayList<RecoveryPoint>(restorePoints.length);
		for(JRestorePoint point : restorePoints){
			logger.debug("Object JRestorePoint:"+point);	
			logger.debug("getDwAgentBackupType:"+point.getDwAgentBackupType());
			logger.debug("getDwAgentOSType:"+point.getDwAgentOSType());
			logger.debug("getDwAgentBackupType:"+point.getDwAgentBackupType());
			try{
				if ((point.getDwAgentBackupType() == AgentOsInfoType.AgentBackupType.BT_LOCAL_D2D 
						&& point.getDwAgentOSType() == AgentOsInfoType.AgentOSType.Windows)
						|| point.getDwAgentBackupType() == AgentOsInfoType.AgentBackupType.EBT_HBBU)
					result.add(convertRestorePoint2RecoveryPoint(point));
			}catch(Exception e){
				logger.error("Error during convert recovery point", e);
			}
		}
		logger.debug(("result length:"+result.size()));	
		return result.toArray(new RecoveryPoint[0] );
	}
	
	public RecoveryPoint convertRestorePoint2RecoveryPoint(JRestorePoint point) throws Exception{		
		RecoveryPoint result = new RecoveryPoint();
		result.setSessionID(Long.parseLong(point.getSessionID()));
		result.setTime(BackupConverterUtil.string2Date(point.getDate()+" "+point.getTime()));

		result.setTimeZoneOffset(this.getTimeZoneOffset(result.getTime()));

		result.setDataSize(StringUtil.string2Long(point.getDataSize(),0));
		result.setBackupStatus(BackupConverterUtil.string2BackupStatus(point.getBackupStatus()));
		result.setBackupType(BackupConverterUtil.string2BackupType(point.getBackupType()));
		result.setName(point.getName());
		result.setPath(point.getPath());
		result.setScheduledTime(point.getUllScheduledTime());
		result.setBackupDest(point.getBackupDest());
		result.setLogicalSize(StringUtil.string2Long(point.getLogicalSize(), 0));
		result.setDefaultSessPwd(point.isDefaultSessPwd());
		result.setVmHypervisor(point.getDwVMHypervisor());
		result.setAgentBackupType(point.getDwAgentBackupType());
		// add by wanhu08, for instantvm
		result.setAgentOSType(point.getDwAgentOSType());
		//<huvfe01>added for vm recovery
		result.setVmEsxHost(point.getVmEsxHost());
		result.setVmName(point.getVmName());
		result.setVmvCenter(point.getVmvCenter());
		
		try {
			if(!StringUtil.isEmptyOrNull(point.getEncryptType()))
				result.setEncryptType(Integer.parseInt(point.getEncryptType()));
		}catch(NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}
		
		result.setEncryptPasswordHash(point.getEncryptPasswordHash());
		result.setSessionGuid(point.getSessionGuid());
		
		// if the major version >= 2, this session will be OK for Exchange GRT
		result.setSessionVersion(point.getMajorVersion() - 2);
		result.setFsCatalogStatus(point.getFsCatalogStaus());
		
		result.setPeriodRetentionFlag(point.getDwBKAdvSchFlag());
		
		logger.debug("RecoveryPoint::" + StringUtil.convertObject2String(result));
		
		List<RecoveryPointItem> recoveryPointItems = new ArrayList<RecoveryPointItem>();
		if (point.getItems()!=null && point.getItems().size()>0){
			for(JBackupItem item : point.getItems()){
				try{
					recoveryPointItems.add(convertBackupItem2RecoveryPointItem(item));
				}catch(Exception e){
					logger.error("Error during convert backup item", e);
				}
			}
		}
		
		logger.debug("ReconveryPointsItems::" + StringUtil.convertList2String(recoveryPointItems));
		result.setItems(recoveryPointItems);
		result.setBackupSetFlag(point.getBackupSetFlag());
		return result;
	}
	
	public RecoveryPointItem convertBackupItem2RecoveryPointItem(JBackupItem item){
		RecoveryPointItem result = new RecoveryPointItem();
		result.setVolumeOrAppType(item.getType());
		result.setCatalogFilePath(item.getCatalogFilePath());
		result.setDisplayName(item.getDisplayName());
		result.setGuid(item.getGuid());
		result.setVolDataSizeB(StringUtil.string2Long(item.getVolDataSizeB(),0));
		result.setSubSessionID(Long.parseLong(item.getSubSessionID()));
		result.setChildrenCount(item.getChildrenCount());
		if (item.isBootVol()) {
			result.setVolAttr(RecoveryPointItem.BootVol);
		}
		if (item.isSysVol()) {
			result.setVolAttr(result.getVolAttr() | RecoveryPointItem.SysVol);
		}
		if (item.isRefsVol()) {
			result.setVolAttr(result.getVolAttr() | RecoveryPointItem.RefsVol);
		}
		if (item.isNTFSVol()) {
			result.setVolAttr(result.getVolAttr() | RecoveryPointItem.NtfsVol);
		}
		if (item.isDedupVol()) {
			result.setVolAttr(result.getVolAttr() | RecoveryPointItem.DedupVol);
		}
		result.setHasReplicaDB(item.isHasReplicaDB());
		return result;
	}
	
	public RecoveryPoint[] convert2RecoveryPointsFromBackupInfo(JBackupInfo[] backupInfos) {
		return convert2RecoveryPointsFromBackupInfo(backupInfos, false);
	}
	
	public RecoveryPoint[] convert2RecoveryPointsFromBackupInfo(JBackupInfo[] backupInfos, boolean useBackupSet) {
		if (backupInfos==null || backupInfos.length==0)
			return new RecoveryPoint[0];
		
		List<RecoveryPoint> result = new ArrayList<RecoveryPoint>();
		for(JBackupInfo point : backupInfos){
			try{
				RecoveryPoint rp = convert2RecoveryPointFromBackupInfo(point);
				if(useBackupSet) {
					rp.setBackupSetFlag(point.getBackupSetFlag());
				}
				if(rp != null)
					result.add(rp);
			}catch(Exception e){
				logger.error("Error during convert recovery point", e);
			}
		}
			
		return result.toArray(new RecoveryPoint[0] );
	}
	
	public RecoveryPoint convert2RecoveryPointFromBackupInfo(JBackupInfo backupInfo){
		RecoveryPoint result = new RecoveryPoint();
		if(!BackupConverterUtil.validateDataFormat(backupInfo.getDate() + " " + backupInfo.getTime()))
			return null;
		Date string2Date = BackupConverterUtil.string2Date(backupInfo.getDate()+" "+backupInfo.getTime());
		result.setTime(string2Date);
		
		result.setTimeZoneOffset(this.getTimeZoneOffset(result.getTime()));
		if(!StringUtil.isEmptyOrNull(backupInfo.getLogicalSize()))
			result.setLogicalSize(Long.parseLong(backupInfo.getLogicalSize()));
		result.setDataSize(Long.parseLong(backupInfo.getSize()));
		result.setBackupStatus(BackupConverterUtil.string2BackupStatus(backupInfo.getStatus()));
		result.setBackupType(BackupConverterUtil.string2BackupType(backupInfo.getType()));
		result.setName(backupInfo.getName());
		if(!StringUtil.isEmptyOrNull(backupInfo.getbackupSessionID()))
			result.setSessionID(Long.parseLong(backupInfo.getbackupSessionID()));
		result.setFsCatalogStatus((int)backupInfo.getCatalogFlag());
		result.setBackupDest(backupInfo.getBackupDest());
		result.setPeriodRetentionFlag(backupInfo.getPeriodRetentionFlag());
		return result;
	}
	
	public JJobScript convert2JobScript(CopyJob job, String hostname)
		throws UnknownHostException {		
		JJobScript result = new JJobScript();
		
		result.setUsJobType(job.getJobType());
		result.setUlJobAttribute(job.getJobLauncher());
		setSrcUserPwd(job, result);

		packageDest(job, result);

		result.setDwCompressionLevel(job.getCompressionLevel());
		result.setDwEncryptType(job.getEncryptType());
		result.setPwszEncryptPassword(job.getEncryptPassword());
		result.setDwEncryptTypeCopySession(job.getEncryptTypeCopySession());
		result.setPwszEncryptPasswordCopySession(job.getEncryptPasswordCopySession());
		result.setBRetainEncryptionAsSource(job.getRetainEncryptionAsSource());
		// backup to datastore and session password not provided, often in MSP case.
		if(!StringUtil.isEmptyOrNull(job.getDataStoreHashKey()) && StringUtil.isEmptyOrNull(job.getEncryptPassword())){
			result.setPwszVDiskPassword(job.getDataStoreHashKey());
			result.setPwszEncryptPassword(null);
		}else{
			result.setPwszEncryptPassword(job.getEncryptPassword());
		}

		// Copy Jobs have only one job script node
		List<JJobScriptNode> nodeList = new ArrayList<JJobScriptNode>();
		JJobScriptNode node = new JJobScriptNode();
		node.setPwszNodeName(job.getVmInstanceUUID());
		//node.setPwszNodeName(hostname);
		node.setPwszSessPath(job.getSessionPath());
		node.setUlSessNum(job.getSessionNumber());
		// node.setPwszUserName(job.getUserName());
		// node.setPwszUserPW(job.getPassword());
		nodeList.add(node);
		result.setPAFNodeList(nodeList);
		result.setNNodeItems(1);
		result.setUsRestPoint(job.getRestPoint());
		result.setLauncherInstanceUUID(job.getVmInstanceUUID());
		result.setRPSDataStoreDisplayName(job.getRpsDataStoreDisplayName());
		result.setRPSDataStoreName(job.getRpsDataStore());
		result.setRpsHostname(job.getRpsHostname());
		return result;
	}

	private void setSrcUserPwd(CopyJob job, JJobScript result) {
		if (job == null || result == null)
			return;

		if (job.getUserName() != null && job.getUserName().trim().length() > 0) {
			result.setPwszUserName(job.getUserName());
			if (job.getPassword() != null) {
				result.setPwszPassword(job.getPassword());
			}
		}
	}

	private void packageDest(CopyJob job, JJobScript result) {
		if (job == null || result == null)
			return;

		result.setPwszDestPath(job.getDestinationPath());

		if (job.getDestinationUserName() != null
				&& job.getDestinationUserName().trim().length() > 0) {
			result.setPwszUserName_2(job.getDestinationUserName());
			if (job.getDestinationPassword() != null) {
				result.setPwszPassword_2(job.getDestinationPassword());
			}
		}
	}
}
