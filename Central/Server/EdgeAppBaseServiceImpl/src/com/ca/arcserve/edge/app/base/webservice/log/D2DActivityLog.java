package com.ca.arcserve.edge.app.base.webservice.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.listener.service.event.ActivityLogEvent;
import com.ca.arcflash.listener.service.event.FlashEvent.Source;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcserve.edge.app.asbu.dao.IASBUDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.EdgeASBUServer;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.d2d.D2DEdgeServiceImpl;

public class D2DActivityLog {
	
	private static Logger logger = Logger.getLogger(D2DActivityLog.class);
	private IASBUDao asbuDao = DaoFactory.getDao(IASBUDao.class);
	
	private IActivityLogService logService = new ActivityLogServiceImpl();	
	
	private String version;
	private int productType;
	private Date utcTime;
	private Date localTime;
	private int jobId;
	private int logLevel;
	private int jobType;
	private String serverId;
	private String agentId;
	private String sourceRPSId;
	private String targetRPSId;
	private String sourceDataStoreUUID;
	private String targetDataStoreUUID;
	private String planUUID;
	private String targetPlanUUID; // target plan uuid is only for replication job
	private String message;

	public int save(ActivityLogEvent event) {
		if (event == null || event.getEventArg() == null || event.getEventArg().getLogs() == null) {
			logger.debug("D2DActivityLog.save(ActivityLogEvent) failed, the logs are null.");
			return 0;
		}
		
		String[] lines = event.getEventArg().getLogs().split("\n");
		
		DaoFactory.beginTrans();
		
		try {
			for (String line : lines) {
				int errorCode = parse(line.trim());
				if (errorCode == 0) {
					saveSingle(event);
				}
			}
		} catch (Exception e) {
			logger.error("D2DActivityLog.save failed.");
			DaoFactory.rollbackTrans();
			return 1;
		} finally {
			if (!DaoFactory.isTransEnd()) {
				DaoFactory.commitTrans();
			}
		}
		
		return 0;
	}

	private void saveSingle(ActivityLogEvent event) throws Exception {
		int severity;
		
		switch (logLevel % 3) {
		case 0:
			severity = Severity.Information.getValue();
			break;
		case 1:
			severity = Severity.Warning.getValue();
			break;
		default:
			severity = Severity.Error.getValue();
			break;
		}
		
		int serverHostId = 0;
		int targetHostId = 0;
		int sourceRpsHostId = 0;
		int targetRpsHostId = 0;
		
		if (event.getSource() == Source.RPS) {
			serverHostId = D2DEdgeServiceImpl.getRpsHostId(serverId);
		} else if (event.getSource() == Source.D2D) {
			serverHostId = D2DEdgeServiceImpl.getD2DHostId(serverId);
			if(jobType == JobType.JOBTYPE_CONVERSION && serverHostId == 0){
				serverHostId = D2DEdgeServiceImpl.getRpsHostId(serverId);
			}
		} else if (event.getSource() == Source.ASBU) {
			List<EdgeASBUServer> servers = new ArrayList<>();
			asbuDao.findConnectionInfoByUUID(serverId, servers); 
			if(servers.size() > 0){
				serverHostId = servers.get(0).getHostId();
			}
		} else {
			logger.debug("Invalid log event source, type = " + event.getSource());
			throw new Exception("invalid event source.");
		}
		
		if (serverHostId == 0) {
			logger.info("cannot find the server id, source = " + event.getSource() + ", server uuid = " + serverId);
			return;
		}
		
		if (event.getSource() == Source.RPS) {
			if (jobType >= 1000) {
				// get RPS ID for the log of itself
				targetHostId = D2DEdgeServiceImpl.getRpsHostId(agentId);				
			} else {
				targetHostId = getTargetHostIdForJobLogs();
			}
		} else {
			targetHostId = getTargetHostIdForJobLogs();
		}
		
		if (sourceRPSId != null && !sourceRPSId.isEmpty()) {
			sourceRpsHostId = D2DEdgeServiceImpl.getRpsHostId(sourceRPSId);
		}
		
		if (targetRPSId != null && !targetRPSId.isEmpty()) {
			targetRpsHostId = D2DEdgeServiceImpl.getRpsHostId(targetRPSId);
		}
		
		logService.addD2dLog(version, productType, utcTime, localTime, severity, jobId, jobType, serverHostId, 
				targetHostId, sourceRpsHostId, targetRpsHostId, sourceDataStoreUUID, targetDataStoreUUID, planUUID, targetPlanUUID, message.trim());
	}

	private int getTargetHostIdForJobLogs() {
		int targetHostId;
		targetHostId = D2DEdgeServiceImpl.getVisibleVmHostId(agentId);
		if (targetHostId == 0) {
			targetHostId = D2DEdgeServiceImpl.getD2DHostId(agentId);
		}
		return targetHostId;
	}

	private int parse(String line) {
		String[] fields = line.split("\t",16);
		if (fields.length != 16) {
			logger.error("D2DActivityLog.parse() - cannot split line into 16 fields, value = " + line);
			return 3;
		}

		try {
			version = fields[0];
			productType = Integer.parseInt(fields[1]);
			utcTime = new Date(Long.parseLong(fields[2]));
			localTime = new Date(Long.parseLong(fields[3]));
			if (fields[4].trim().isEmpty()) {
				jobId = 0;
			} else {
				jobId = Integer.parseInt(fields[4]);
			}
			logLevel = Integer.parseInt(fields[5]);
			jobType = Integer.parseInt(fields[6]);
			serverId = fields[7];
			agentId =  fields[8];
			sourceRPSId = fields[9];
			targetRPSId = fields[10];
			sourceDataStoreUUID = fields[11];
			targetDataStoreUUID = fields[12];
			planUUID = fields[13];
			targetPlanUUID = fields[14];
			String [] messageArray = fields[15].split("$$$");
			message = messageArray[0].substring(0, fields[15].length() - 3);
			
		} catch (Exception e) {
			logger.error("D2DActivityLog.parse() - parse log failed, error message = " + e.getMessage() + ", value = " + line,e);
			return 4;
		}
		
		return 0;
	}

}
