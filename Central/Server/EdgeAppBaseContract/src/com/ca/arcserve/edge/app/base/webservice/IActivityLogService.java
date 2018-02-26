package com.ca.arcserve.edge.app.base.webservice;

import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogExportMessage;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingResult;

public interface IActivityLogService {
	
	LogPagingResult getUnifiedLogs(LogPagingConfig config, LogFilter filter) throws EdgeServiceFault;
	
	/**
	 * Compatible method to add CPM activity log. Use <code>addUnifiedLog</code> since R17 Oolong version.
	 * <P/>
	 * Note, the node name will be treated as target node name.
	 */
	long addLog(ActivityLog log) throws EdgeServiceFault;
	
	long addD2dLog(String version, int productType, Date utcTime, Date localTime, int severity, long jobId, 
			int jobType, int serverHostId, int targetHostId, int sourceRpsHostId, int targetRpsHostId, String sourceDataStoreUUID,
			String targetDataStoreUUID, String planUUID, String targetPlanUUID, String message) throws EdgeServiceFault;
			
	void addUnifiedLog(LogAddEntity entity) throws EdgeServiceFault;
	
	void deleteUnifiedLogs(LogFilter filter) throws EdgeServiceFault;
	
	void generateExportLogFile( LogPagingConfig config, LogFilter filter, String exportIdentifier  ) throws EdgeServiceFault;
	
	LogExportMessage logExportCommunicate( LogExportMessage request, String exportIdentifier ) throws EdgeServiceFault;
	
	LogPagingResult getUnifiedLogsById(List<Long> logIds, LogPagingConfig config) throws EdgeServiceFault;

}
