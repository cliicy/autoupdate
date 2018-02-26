package com.ca.arcserve.edge.app.base.webservice.dataSync;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.listener.service.event.DataSyncEvent;
import com.ca.arcflash.listener.service.event.DataSyncEventType;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoUtils;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.d2d.D2DEdgeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.BackupInfo;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.TimeStamp;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.BackupInfoPaser;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryHandler;

public class CustomBackupJobhistoryDataSyncHandler extends JobHistoryHandler implements IDataSyncEventHandler{
	
	private static CustomBackupJobhistoryDataSyncHandler instance = null;
	private static Logger logger = Logger.getLogger(CustomBackupJobhistoryDataSyncHandler.class);
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
	
	private CustomBackupJobhistoryDataSyncHandler(){
	}
	
	public static CustomBackupJobhistoryDataSyncHandler getInstance() {
		if(instance == null){
			instance = new CustomBackupJobhistoryDataSyncHandler();
		}
		return instance;
	}
	
	@Override
	public int saveDataRecord(DataSyncEvent event) {
		if(StringUtil.isEmptyOrNull(event.getEventArg().getDataRecord())){
			logger.info("CustomBackupJobhistoryDataSyncHandler.saveDataRecord(): data record is null or empty.");
			return 0;
		}
		String[] lines = event.getEventArg().getDataRecord().split("\n");
		DaoFactory.beginTrans();
		try {
			for (String line : lines) {
				int errorCode = parseDataLine(line);
				if (errorCode == 0 && isMsp()) {//just save the Replication from customer job history
					SaveRecord(event);
				}
			}
		} catch (Exception e) {
			logger.error("saveDataRecord failed, insert job history into DB failed.", e);
			DaoFactory.rollbackTrans();
			return 1;
		} finally {
			if (!DaoFactory.isTransEnd()) {
				DaoFactory.commitTrans();
			}
		}
		return 0;
	}
	
	private int parseDataLine(String line) {
		String[] fields = line.split("\t" ,DataSyncEventType.CustomBackupJobHistoryEvent.getValue());
		if (fields.length != DataSyncEventType.CustomBackupJobHistoryEvent.getValue()) {
			logger.error("CustomBackupJobhistoryDataSyncHandler.parseDataLine() - cannot split line into "+
			DataSyncEventType.CustomBackupJobHistoryEvent.getValue()+"fields, value = " + line +" parsed length: " + fields.length );
			return 3;
		}
		try {
			serverId = fields[0];
			agentId  = fields[1];
			targetPlanUUID = fields[2];
			jobId = Integer.parseInt(fields[3]);
			jobExpandDetail = fields[4];
			String backupInfoXml = BackupInfoPaser.decodeBackupInfoXml(fields[4]);
			if(StringUtil.isEmptyOrNull(backupInfoXml)){
				logger.info("CustomBackupJobhistoryDataSyncHandler.parseDataLine() - backupInfoXml is null or empty.");
				return 3;
			}
			BackupInfo bkInfo = BackupInfoPaser.ParseBackupInfo(backupInfoXml);
			if(bkInfo == null){
				logger.info("CustomBackupJobhistoryDataSyncHandler.parseDataLine() - backupinfo object is null.");
				return 3;
			}
			jobType = JobType.JOBTYPE_BACKUP;
			jobMethod = BackupConverterUtil.string2BackupType(bkInfo.getBackupDetail().getBackupType());
			jobStatus = BackupConverterUtil.string2BackupStatus(bkInfo.getBackupStatus().getStatus());
			jobUTCStartDate = getDate(bkInfo.getTimeStamp());
		} catch (Exception e) {
			logger.error("JobHistoryHandler.parse() - parse failed, value = " + line, e);
			return 4;
		}
		return 0;
	}
	
	private void SaveRecord(DataSyncEvent event) throws Exception {
		int serverHostId = -1;//msp case , no server
		int targetHostId = 0;
		int sourceRpsHostId = 0;
		int targetRpsHostId = 0;
		
//		if (event.getSource() == Source.RPS) {
//			serverHostId = D2DEdgeServiceImpl.getRpsHostId(serverId);
//		} else if (event.getSource() == Source.D2D) {
//			serverHostId = D2DEdgeServiceImpl.getD2DHostId(serverId);
//		} 
		
		targetHostId = D2DEdgeServiceImpl.getVisibleVmHostId(agentId);
		if (targetHostId == 0) {
			targetHostId = D2DEdgeServiceImpl.getD2DHostId(agentId);
		}
		
		if (targetHostId == 0) {
			logger.debug("cannot find the agent id, source = " + event.getSource() + ", agent uuid = " + agentId);
			throw new Exception("cannot find the agent id by uuid.");
		}
		
		long[] jobHistoryId = new long[1];
		String planUUID = targetPlanUUID;
		
		//Init the data which is no use
		version = "5.0";
		jobLocalStartDate = jobUTCStartDate;
		jobUTCEndDate = new Date();
		jobLocalEndDate = new Date();
		sourceDataStoreUUID = "";
		targetDataStoreUUID = "";
		//End init
		jobHistoryDao.as_edge_d2dJobHistory_add(version, jobId, jobType, jobMethod, jobStatus, jobUTCStartDate, jobLocalStartDate, 
				jobUTCEndDate, jobLocalEndDate, serverHostId, targetHostId, sourceRpsHostId, targetRpsHostId, sourceDataStoreUUID, targetDataStoreUUID, planUUID,targetPlanUUID, productType,1, jobHistoryId);
		expandHandler.parseJobExpandDetail( jobHistoryId[0],  jobType , jobStatus, jobExpandDetail, true );
	}
	
	private Date getDate(TimeStamp tStamp)throws Exception{
		if(tStamp == null)
			return null;
		String date = tStamp.getDate();
		String time = tStamp.getTime();
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date datetime;
		datetime = dFormat.parse(date+" "+time);
		return DaoUtils.fromUTC(datetime);
	}
	
	private boolean isMsp(){
		if(StringUtil.isEmptyOrNull(targetPlanUUID))
			return false;
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		EdgePolicy edgePolicy;
		edgePolicyDao.as_edge_policy_list_by_uuid(targetPlanUUID, policyList);
		if (policyList.size() > 0) {
			edgePolicy = policyList.get(0);
			if(Utils.hasMSPServerReplicationTask(edgePolicy.getContentflag())){
				return true;
			}
		} 
		return false;
	}
}
