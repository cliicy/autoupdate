package com.ca.arcserve.edge.app.base.webservice.srm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.srmagent.ISrmAgentService;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.ConsoleUrlUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.SrmJob;
import com.ca.arcserve.edge.app.base.schedulers.SrmPkiMonitorJob;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class SrmServiceImpl {

	public static final int DEFAULT_SRM_PROBE_TIMEOUT = 18000000; // half hour for SRM probe
	private static Logger _debuglog = Logger.getLogger(SrmServiceImpl.class);
	
	public static void ProbeNow() {
		SrmJob job = new SrmJob();
	    ScheduleData data = new ScheduleData();
	    data.setScheduleName("Srm UI Probe Job");
	    try {
			SchedulerUtilsImpl.getInstance().runNow(job, data , null);
		} 
	    catch (EdgeSchedulerException e) {
			_debuglog.info(e);
		}
	}

	public static boolean IsProbeDone() {
		EdgeTask srmTask = EdgeTaskFactory.getInstance().getTask(
	              EdgeTaskFactory.EDGE_TASK_SRM);
	    int waitingItemCount = srmTask.getWaitingQueueSize();
	    int executeItemCount = srmTask.getExecuteQueueSize();

	    return (waitingItemCount == 0 && executeItemCount == 0);
	}

	/*
	 * Probe several nodes which is user selected at the same time
	 */
	public static void ProbeNodes(List<Integer> nodesIDList) {
		SrmJob job = new SrmJob();
		job.setProbeNodesIDList(nodesIDList);

		ScheduleData data = new ScheduleData();
		data.setScheduleName("Srm Run Now Job");

		try {
			SchedulerUtilsImpl.getInstance().runNow( job, data , null );
		} catch (EdgeSchedulerException e) {
			_debuglog.info(e);
		}
	}

	public static boolean InvokeGetSrmInfo(int hostID, String protocol, String host,
			int port, int command) {
		if (_debuglog.isDebugEnabled()) {
			_debuglog.debug("InvokeGetSrmInfo:protocol=" + protocol + " host=" + host + " port=" + port);
		}
		
		IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
		try (D2DConnection connection = connectionFactory.createD2DConnection(hostID)) {
			connection.setConnectTimeout(DEFAULT_SRM_PROBE_TIMEOUT);
			connection.setRequestTimeout(DEFAULT_SRM_PROBE_TIMEOUT);
			connection.connect();
			
			return InvokeAndDealWithSrmInfo(connection.getService(), host, command);
		} catch(Throwable t) {
			 if (t.getCause() instanceof SQLException) {
				AddActivityLog(hostID, "SRM_PROBE_STATUS_FAILED_DATABASE");
			} else {
				AddActivityLog(hostID, "SRM_PROBE_STATUS_FAILED_NETWORK");
			}
			
			_debuglog.error("error on invokeGetSrmInfo", t);
			return false;
		}
	}

	private static void updateNodeStatusToDatabase(int hostID, int flag) {
		NodeManagedStatus status = null;
		switch(flag) {
		case 1:
			status = NodeManagedStatus.Managed;
			break;
		case 2:
			status = NodeManagedStatus.Unmanaged;			
			break;
		default:
			status = NodeManagedStatus.Unknown;
			break;
		}
		//Update managed status to database , and then do some other operations , for example : remove license
		EdgeCommonUtil.changeNodeManagedStatus(hostID, status);
	}
	
	private static void updateTimeZoneToDatabase(int hostID, int rawOffset) {
		IEdgeHostMgrDao m_idao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		m_idao.as_edge_host_update_timezone_by_id(hostID, rawOffset);
	}

	private static boolean checkEdgeServer(D2DConnection connection, int hostID, String host) throws Exception {
		String edgeHostName = EdgeCommonUtil.getLocalFqdnName();
		String edgeUUID = CommonUtil.retrieveCurrentAppUUID();
		
		int flag = connection.getService().QueryEdgeMgrStatus(edgeUUID, CommonUtil.getApplicationTypeForD2D(), edgeHostName);
		updateNodeStatusToDatabase(hostID, flag);

		if (flag == 1) {
			// update the time zone info
			String timeZoneID = connection.getService().QueryD2DTimeZoneID();
			TimeZone t = TimeZone.getTimeZone(timeZoneID);
			int rawOffsetByMinute = t.getRawOffset() / 60000;
			updateTimeZoneToDatabase(hostID, rawOffsetByMinute);
		} else
		// means target D2D node is managed by another Edge server
		// see definition D2DEdgeRegistration.getRegStatus()
		if (flag == 2) {
			IActivityLogService _iSyncActivityLog = new ActivityLogServiceImpl();
			ActivityLog         _log              = new ActivityLog();
			_log.setModule(Module.Common);
			_log.setSeverity(Severity.Warning);
			_log.setNodeName(host);
			_log.setTime(new Date(System.currentTimeMillis()));
			
			try {
				long jobid = Long.parseLong(Thread.currentThread().getName());
				_log.setJobId(jobid);
			} catch (NumberFormatException e) {
				_debuglog.debug(e.getMessage());
			}
			
			try (Formatter fmt = new Formatter()) {
				EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
				String CurRegisteredEdgeHostName = edgeInfo == null ? "" : edgeInfo.getEdgeHostName();	
				if (edgeInfo != null) {				
					String consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
					if(!StringUtil.isEmptyOrNull(consoleName))
						CurRegisteredEdgeHostName = consoleName;
				}
				fmt.format(EdgeCMWebServiceMessages.getResource("SRM_PROBE_STATUS_NOT_MANAGED_NODE"), 
						CurRegisteredEdgeHostName);
				_log.setMessage(fmt.toString());
			}
			
			try {
				_iSyncActivityLog.addLog(_log);
			} catch (EdgeServiceFault e) {
				_debuglog.error(e.getMessage(), e);
			}

			return true;
		}

		return false;
	}
	
	public static boolean IsManagedByDifferentEdge(int hostID, String protocol, String host, int port) {
		IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
		try (D2DConnection connection = connectionFactory.createD2DConnection(hostID)) {
			connection.setConnectTimeout(DEFAULT_SRM_PROBE_TIMEOUT);
			connection.setRequestTimeout(DEFAULT_SRM_PROBE_TIMEOUT);
			connection.connect();
			
			return checkEdgeServer(connection, hostID, host);
		} catch(Throwable t) {
			_debuglog.debug("check edge server failed.", t);
			
			if (t.getCause() instanceof SQLException) {
				AddActivityLog(hostID, "SRM_PROBE_STATUS_FAILED_DATABASE");
			} else {
				AddActivityLog(hostID, "SRM_PROBE_STATUS_FAILED_NETWORK");	
			}
			
			return true;
		}
	}

	private static boolean InvokeAndDealWithSrmInfo(ISrmAgentService srmAgentService, String host, int command) {
		String srmXml = srmAgentService.GetSrmInfo(command);

		if ( srmXml != null && srmXml.length() > 0 )
		{
			switch ( command ){
			case SrmCommand.GET_HARDWARE_INFO | SrmCommand.GET_SOFTWARE_INFO:
				parseSrmXmlBuffer(host, srmXml, 0);
				break;
			default:
				processXml(host, srmXml, command);
			}

			return true;
		}
		return false;
	}

	private static boolean processXml ( String host, String xmlStr, int command ) {
		if ( xmlStr == null || xmlStr.length() == 0 ) {
			return false;
		}

		XmlProcessor xmlProc = new XmlProcessor(host, xmlStr);
		switch ( command ){
		case SrmCommand.GET_HARDWARE_INFO:
			xmlProc.processHarewareInfo();
			break;
		case SrmCommand.GET_SOFTWARE_INFO:
			xmlProc.processSoftwareInfo();
			break;
		case SrmCommand.GET_SERVERPKI_INFO:
			xmlProc.processServerPKI();
			break;
		default:
			return false;
		}

		return true;
	}

	/**
	 * recursive calling to process the same format of xml buffer until all xml buffer been processed
	 * return - the offset of srmXmlBuffer we have processed
	 */
	private static int parseSrmXmlBuffer(String host, String srmXmlBuffer, int beginIndex) {
		if ( srmXmlBuffer == null || srmXmlBuffer.length() == 0
				|| beginIndex < 0 || beginIndex >= srmXmlBuffer.length() ) {
			return -1;
		}

		int srmInfoTypePos = srmXmlBuffer.indexOf(' ', beginIndex);
		if ( srmInfoTypePos < 0 ) {
			return -1;
		}

		int srmInfoType = Integer.parseInt(srmXmlBuffer.substring(beginIndex, srmInfoTypePos));
		int srmXmlLenPos = srmXmlBuffer.indexOf(' ', srmInfoTypePos+1);

		if ( srmXmlLenPos < 0 ) {
			return -1;
		}

		int srmXmlLen = Integer.parseInt(srmXmlBuffer.substring(srmInfoTypePos+1, srmXmlLenPos));
		if ( srmXmlLen <= 0 ) {
			return -1;
		}

		int srmXmlEndIndex = srmXmlLenPos + 1 + srmXmlLen;
		if ( srmXmlEndIndex > srmXmlBuffer.length() ) {
			return -1;
		}

		String xmlContent = srmXmlBuffer.substring(srmXmlLenPos+1, srmXmlEndIndex);
		if ( !processXml(host, xmlContent, srmInfoType) )
		{
			return -1;
		}

		//recursive calling
		parseSrmXmlBuffer(host, srmXmlBuffer, srmXmlEndIndex);

		return srmXmlEndIndex;
	}
	
	private static void AddActivityLog(int hostID, String messageCode) {
		AddActivityLog(hostID, messageCode, "");
	}
	
	private static void AddActivityLog(int hostID, String messageCode, String errorMessage) {
		IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list_for_srm(hostID, hosts);
		if (hosts.size() == 0 || hosts.get(0) == null) {
			_debuglog.debug("No relation record to host id[" + hostID + "]");
			return;
		}

		IActivityLogService _iSyncActivityLog = new ActivityLogServiceImpl();
		ActivityLog _log = new ActivityLog();

		_log.setModule(Module.Common);
		_log.setSeverity(Severity.Warning);
		_log.setNodeName(hosts.get(0).getRhostname());
		_log.setTime(new Date(System.currentTimeMillis()));

		try {
			long jobid = Long.parseLong(Thread.currentThread().getName());
			if (jobid == SrmPkiMonitorJob.SRM_PKI_MONITOR_JOB_ID)
				return;
			
			_log.setJobId(jobid);
		} catch (NumberFormatException e) {
			_debuglog.debug(e.getMessage());
		}

		try (Formatter fmt = new Formatter()) {
			fmt.format(EdgeCMWebServiceMessages.getResource(messageCode), hosts.get(0).getRhostname());
			_log.setMessage(fmt.toString() + errorMessage);
		}

		try {
			_iSyncActivityLog.addLog(_log);
		} catch (EdgeServiceFault e) {
			_debuglog.error(e.getMessage(), e);
		}

	}
}
