package com.ca.arcserve.edge.app.base.webservice.jobhistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.listener.service.event.FlashEvent.Source;
import com.ca.arcflash.listener.service.event.JobHistoryEvent;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcserve.edge.app.asbu.dao.IASBUDao;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.EdgeASBUServer;
import com.ca.arcserve.edge.app.base.webservice.d2d.D2DEdgeServiceImpl;

public class JobHistoryHandler {
	
	protected IEdgeJobHistoryDao jobHistoryDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);
	private static Logger logger = Logger.getLogger(JobHistoryHandler.class);
	protected JobHistoryExpandDetailHandler expandHandler = new JobHistoryExpandDetailHandler();
	
	private IASBUDao asbuDao = DaoFactory.getDao(IASBUDao.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	
	protected String version;
	protected int productType;
	protected long jobId;
	protected long jobType;
	protected long jobMethod;
	protected long jobStatus;
	protected Date jobUTCStartDate;
	protected Date jobLocalStartDate;
	protected Date jobUTCEndDate;
	protected Date jobLocalEndDate;
	protected String serverId;
	protected String agentId;
	protected String sourceRPSId;
	protected String targetRPSId;
	protected String sourceDataStoreUUID;
	protected String targetDataStoreUUID;
	protected String sourcePlanUUID;
	protected String targetPlanUUID;
	protected String jobExpandDetail;

	public int saveJobHistory(JobHistoryEvent event) {
		if (event.getEventArg().getJobHistoryRecord() == null) {
			logger.info("D2DEdgeServiceImpl.handleJobHistoryEvent(JobHistoryEvent) failed, the job history data is null.");
			return 0;
		}
		String[] lines = event.getEventArg().getJobHistoryRecord().split("\n");
		
		DaoFactory.beginTrans();
		
		try {
			for (String line : lines) {
				int errorCode = parse(line);
				if (errorCode == 0) {
					save(event);
				}
			}
		} catch (Exception e) {
			logger.error("saveJobHistory failed, insert job history into DB failed.",e);
			DaoFactory.rollbackTrans();
			return 1;
		} finally {
			if (!DaoFactory.isTransEnd()) {
				DaoFactory.commitTrans();
			}
		}
		
		return 0;
	}
	
	private int parse(String line) {
		String[] fields = line.split("\t" ,19 );
		if (fields.length != 19) {
			logger.error("JobHistoryHandler.parse() - cannot split line into 19 fields, value = " + line +" parsed length: " + fields.length );
			return 3;
		}
		try {
			version = fields[0].trim();
			productType = Integer.parseInt(fields[1]);
			jobId = Long.parseLong(fields[2]);
			jobType = Long.parseLong(fields[3]);
			jobMethod = Long.parseLong(fields[4]);
			jobStatus = Long.parseLong(fields[5]);
			jobUTCStartDate = new Date(Long.parseLong(fields[6]));
			jobLocalStartDate = new Date(Long.parseLong(fields[7]));
			jobUTCEndDate = new Date(Long.parseLong(fields[8]));
			jobLocalEndDate = new Date(Long.parseLong(fields[9]));
			serverId = fields[10];
			agentId =  fields[11];
			sourceRPSId = fields[12];
			targetRPSId = fields[13];
			sourceDataStoreUUID = fields[14];
			targetDataStoreUUID = fields[15];
			sourcePlanUUID = fields[16].trim();
			targetPlanUUID = fields[17];
			jobExpandDetail = getJobDetail( jobType,fields , line );
			
			
		} catch (Exception e) {
			logger.error("JobHistoryHandler.parse() - parse failed, value = " + line, e);
			return 4;
		}
		
		return 0;
	}
	
	private void save(JobHistoryEvent event) throws Exception {
		int serverHostId = 0;
		int targetHostId = 0;
		int sourceRpsHostId = 0;
		int targetRpsHostId = 0;
		if (event.getSource() == Source.RPS) {
			serverHostId = D2DEdgeServiceImpl.getRpsHostId(serverId);
		} else if (event.getSource() == Source.D2D) {
			serverHostId = D2DEdgeServiceImpl.getD2DHostId(serverId);
		} else if(event.getSource() == Source.ASBU){
			List<EdgeASBUServer> servers = new ArrayList<>();
			asbuDao.findConnectionInfoByUUID(serverId, servers); 
			if(servers.size() > 0){
				serverHostId = servers.get(0).getHostId();
			}
		} else {
			logger.error("Invalid log event source, type = " + event.getSource());
			throw new Exception("invalid event source.");
		}
		if (serverHostId == 0) {
			//logger.info("cannot find the server id, source = " + event.getSource() + ", server uuid = " + serverId);
			throw new Exception("cannot find the server id.");
		}
		targetHostId = D2DEdgeServiceImpl.getVisibleVmHostId(agentId);
		if (targetHostId == 0) {
			targetHostId = D2DEdgeServiceImpl.getD2DHostId(agentId);
		}
		
		if (sourceRPSId != null && !sourceRPSId.isEmpty()) {
			sourceRpsHostId = D2DEdgeServiceImpl.getRpsHostId(sourceRPSId);
		}
		
		if (targetRPSId != null && !targetRPSId.isEmpty()) {
			targetRpsHostId = D2DEdgeServiceImpl.getRpsHostId(targetRPSId);
		}
		long[] jobHistoryId = new long[1];
		String planUUID = jobType == JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND ? targetPlanUUID : sourcePlanUUID;
		if(jobUTCEndDate.equals(new Date(0))){
			String agentName = "";
			List<EdgeHost> hostList = new ArrayList<EdgeHost>();
			hostMgrDao.as_edge_host_list(targetHostId, 1, hostList);
			if (hostList.size() > 0) {
				agentName =  hostList.get(0).getRhostname();
				if (agentName==null||"".equals(agentName.trim())) {
					agentName = EdgeCMWebServiceMessages.getMessage("unknown_vm", hostList.get(0).getVmname());
				}
			}
			jobHistoryDao.as_edge_d2dJobHistory_monitor_insert(jobId, jobType, jobMethod, jobStatus, targetHostId, serverHostId, sourceRpsHostId, targetRpsHostId, jobUTCStartDate, productType, 0, planUUID, targetPlanUUID, "", agentName, "", agentId);
			return;
		}
		// delete monitor operation will exec in trigger 'as_edge_d2dJobHistory_updateLastJob' 
		// so no need run 'as_edge_d2dJobHistory_monitor_delete' here 		
		//jobHistoryDao.as_edge_d2dJobHistory_monitor_delete(jobId, jobType, targetHostId, serverHostId);
		jobHistoryDao.as_edge_d2dJobHistory_add(version, jobId, jobType, jobMethod, jobStatus, jobUTCStartDate, jobLocalStartDate, 
				jobUTCEndDate, jobLocalEndDate, serverHostId, targetHostId, sourceRpsHostId, targetRpsHostId, sourceDataStoreUUID, targetDataStoreUUID, planUUID,targetPlanUUID, productType, 0 ,jobHistoryId);
		expandHandler.parseJobExpandDetail( jobHistoryId[0], jobType , jobStatus, jobExpandDetail, false );
		
	}
	private String  getJobDetail(  long jobType,  String[] fields , String rawLine ){
		String expandField = "";
		if( jobType == JobType.JOBTYPE_BACKUP || jobType == JobType.JOBTYPE_VM_BACKUP || jobType ==JobType.JOBTYPE_CONVERSION ) {
			
			if( fields.length>=JobHistoryExpandDetailHandler.JobDetailFieldIndex ) {
				expandField =  fields[ JobHistoryExpandDetailHandler.JobDetailFieldIndex -1 ];
			}
			else {
				logger.error( "JobHistoryHandler: " + "the job event not contain expand field information with "
						+ " jobType: " +jobType  + " jobStatus: " +jobStatus +" line: " + rawLine );
			}
		}
		return expandField;
	}
	
}
