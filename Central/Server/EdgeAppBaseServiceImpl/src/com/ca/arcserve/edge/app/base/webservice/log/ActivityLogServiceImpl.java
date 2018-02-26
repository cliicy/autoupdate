package com.ca.arcserve.edge.app.base.webservice.log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeLog;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeLogDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.SqlUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogExportMessage;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.SortColumn;

public class ActivityLogServiceImpl implements IActivityLogService {

	private static IEdgeLogDao logDao = DaoFactory.getDao(IEdgeLogDao.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static Logger logger = Logger.getLogger(ActivityLogServiceImpl.class);
	
	@Override
	public LogPagingResult getUnifiedLogs(LogPagingConfig config, LogFilter filter) throws EdgeServiceFault {
		ensureValid(config);
		int[] totalCount = new int[1];
		
		int productType = getFilterValue(filter.getProductType());
		List<EdgeLog> logs = new ArrayList<EdgeLog>();
		if (productType > 0) {
			filter.setNodeName("");
			filter.setServerName("");
		}
		logger.info("[ActivityLogServiceImpl] getUnifiedLogs: Start getting activity logs from database. Start time :" + new Date());
		logDao.as_edge_log_getPagingList(productType, filter.getNodeId(),
				filter.getNodeName(), filter.getServerName(), filter.getDataStoreUUID() == null ? "" : filter.getDataStoreUUID(), filter.getSeverity().getValue(), filter.getJobId(),
				filter.getJobType().getValue(),filter.getServerId(), filter.getMessage(), getServerDate(filter.getTimeFilter()),
				config.getSortColumn().getValue(), config.getAsc().booleanValue(),
				config.getStartIndex(), config.getCount(),
				totalCount, logs);
		
		logger.info("[ActivityLogServiceImpl] getUnifiedLogs: finish getting activity logs from database successfully. Finish time : " + new Date());
		LogPagingResult retval = new LogPagingResult();

		retval.setStartIndex(config.getStartIndex());
		retval.setTotalCount(totalCount[0]);
		retval.setData(convert(logs));

		return retval;
	}
	
	@Override
	public void generateExportLogFile( LogPagingConfig config, LogFilter filter, String exportIdentifier  ) throws EdgeServiceFault {
		LogExportController.generateExportFile(config, filter, exportIdentifier );
	}
	@Override
	public LogExportMessage logExportCommunicate( LogExportMessage request, String exportIdentifier ) throws EdgeServiceFault {
		return LogExportController.logExportCommunicate( request, exportIdentifier);
	}
	
	private Date getServerDate(BaseFilter dashboardFilter) {
		Date date;
		if (dashboardFilter.getType() == 2) {
			if (dashboardFilter.getUnit() == 1) {
				date = CommonUtil.getLastMinutes(dashboardFilter.getAmount());
			} else if (dashboardFilter.getUnit() == 2) {
				date = CommonUtil.getLastHours(dashboardFilter.getAmount());
			} else {
				date = CommonUtil.getLastDays(dashboardFilter.getAmount());
			}
		} else if (dashboardFilter.getType() == 4) {
			date = CommonUtil.getSomeDate(1970, 1, 1);
		} else {
			date = CommonUtil.toDate(dashboardFilter.getServerTimeStemp());				
		}
		return date;
	}
	
	private void ensureValid(LogPagingConfig config) {
		if (config.getAsc() == null) {
			config.setAsc(Boolean.FALSE);
		}
		
		if (config.getCount() < 1) {
			config.setCount(1);
		}
		
		if (config.getStartIndex() < 0) {
			config.setStartIndex(0);
		}
		
		if (config.getSortColumn() == null || config.getSortColumn() == SortColumn.None) {
			config.setSortColumn(SortColumn.Time);
		}
	}
	
	private int getFilterValue(LogProductType type) {
		return type == null ? -1 : type.getValue();
	}
	
	private List<ActivityLog> convert(List<EdgeLog> logs) {
		List<ActivityLog> retval = new ArrayList<ActivityLog>();

		String localHostName = EdgeCommonUtil.getLocalFqdnName();

		for (EdgeLog log : logs) {
			ActivityLog al = new ActivityLog();
			al.setId(log.getId());
			al.setSeverity(Severity.parse(log.getSeverity()));
			al.setTime(log.getLogUtcTime());
			al.setModule(Module.Common);
			al.setNodeName(log.getNodeName());
			al.setTargetNodeName(log.getTargetNodeName());
			al.setTargetVMName(log.getTargetVMName());
			al.setJobId(log.getJobId());
			if (log.getProductType() == LogProductType.CPM.ordinal() || log.getJobType() >= 1000) {
				al.setJobType(-1);
			} else {						
				al.setJobType(log.getJobType());
			}
			al.setMessage(log.getMessageText());
			al.setProductType(LogProductType.parse(log.getProductType()));
			if (log.getProductType() == LogProductType.CPM.getValue()) {
				al.setNodeName(localHostName);
			}
			// BUG 755011 
			// add start
			al.setSiteName(log.getSiteName());
			// add end

			retval.add(al);
		}

		return retval;
	}
	
	@Override
	public long addLog(ActivityLog log) throws EdgeServiceFault {
		int targetHostId = log.getHostId();
		if (targetHostId == 0) {
			targetHostId = getTargetHostId(log.getNodeName());
		}
		return addCpmLog(log.getSeverity(), log.getJobId(), targetHostId, log.getMessage());
	}
	
	public long addLogForCancelJob(ActivityLog log) throws EdgeServiceFault {
		int targetHostId = log.getHostId();
		if (targetHostId == 0) {
			targetHostId = getTargetHostId(log.getNodeName());
		}
		return addLog(log.getSeverity(), log.getProductType(), log.getJobId(), log.getJobType(), targetHostId, log.getMessage());
	}
	
	@Override
	public long addD2dLog(String version, int productType, Date utcTime,
			Date localTime, int severity, long jobId, int jobType,
			int serverHostId, int targetHostId, int sourceRpsHostId,
			int targetRpsHostId, String sourceDataStoreUUID,
			String targetDataStoreUUID, String planUUID, String targetPlanUUID,
			String message) throws EdgeServiceFault {
		long[] logId = new long[1];	
		logDao.as_edge_log_add(version, productType, utcTime, localTime, severity, jobId, jobType,
				serverHostId, targetHostId, sourceRpsHostId, targetRpsHostId, sourceDataStoreUUID, targetDataStoreUUID, planUUID, targetPlanUUID, message.trim(), logId);
		return logId[0];
	}
	
	private long addCpmLog(Severity severity, long jobId, int targetHostId, String message) {
		String version = CommonUtil.getVersionString();
		Date date = new Date();
		int cpmNodeId = 0;
		try {
			cpmNodeId = getTargetHostId(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			logger.error("addCpmLog can't find CPM host name.");
		}
		long[] logId = new long[1];
		logDao.as_edge_log_add(version == null ? "" : version, LogProductType.CPM.getValue(), date, date, severity.getValue(),
				0, 2000, cpmNodeId, targetHostId, 0, 0, "", "","","", message.trim(), logId);
		return logId[0];
	}
	
	private long addLog(Severity severity,LogProductType productType, long jobId, int jobType, int targetHostId, String message) {
		String version = CommonUtil.getVersionString();
		Date date = new Date();
		int cpmNodeId = 0;
		try {
			cpmNodeId = getTargetHostId(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			logger.error("addCpmLog can't find CPM host name.");
		}
		long[] logId = new long[1];
		logDao.as_edge_log_add(version == null ? "" : version, productType.getValue(), date, date, severity.getValue(),
				jobId, jobType, cpmNodeId, targetHostId, 0, 0, "", "","","", message.trim(), logId);
		return logId[0];
	}
	
	
	
	private int getTargetHostId(String nodeName) {
		if (nodeName == null || nodeName.trim().isEmpty()) {
			return 0;
		}
		
		int[] hostId = new int[1];
		hostMgrDao.as_edge_host_getIdByHostname(nodeName, hostId);
		
		return hostId[0];
	}
	
	@Override
	public void addUnifiedLog(LogAddEntity entity) {
		addCpmLog(entity.getSeverity(), entity.getJobId(), entity.getTargetHostId(), entity.getMessage());
	}

	@Override
	public void deleteUnifiedLogs(LogFilter filter) throws EdgeServiceFault {
//		int productType = getFilterValue(filter.getProductType());
		// Only support delete all log or delete log older than date
		if (filter.getEndDate() == null) {
			logDao.as_edge_log_deleteAll(0, 0);
		} else {
			Date date = CommonUtil.toDate(filter.getEndDate());
			logDao.as_edge_log_deleteOld(0, 0, date);
		}
	}

	@Override
	public LogPagingResult getUnifiedLogsById(List<Long> logIds, LogPagingConfig config) throws EdgeServiceFault {
		ensureValid(config);
		int[] totalCount = new int[1];
		List<EdgeLog> logs = new ArrayList<EdgeLog>();
		List<Integer> ids = new ArrayList<Integer>();
		for(Long logId : logIds){
			ids.add(Integer.valueOf(logId.toString()));
		}
		String logIdArray = SqlUtil.marshal(ids);		
		logDao.as_edge_log_getPagingListById(logIdArray, config.getStartIndex(), config.getCount(), totalCount, logs);

		LogPagingResult retval = new LogPagingResult();
		retval.setStartIndex(config.getStartIndex());
		retval.setTotalCount(totalCount[0]);
		retval.setData(convert(logs));

		return retval;
	}
	
}
