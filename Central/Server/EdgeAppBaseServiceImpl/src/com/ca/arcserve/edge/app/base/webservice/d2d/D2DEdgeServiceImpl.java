package com.ca.arcserve.edge.app.base.webservice.d2d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.listener.service.IFlashListener;
import com.ca.arcflash.listener.service.event.ActivityLogEvent;
import com.ca.arcflash.listener.service.event.DataSyncEvent;
import com.ca.arcflash.listener.service.event.DatastoreStatusChangeEvent;
import com.ca.arcflash.listener.service.event.DatastoreStatusChangeEventArg;
import com.ca.arcflash.listener.service.event.FlashEvent.Source;
import com.ca.arcflash.listener.service.event.JobEvent;
import com.ca.arcflash.listener.service.event.JobEventArg;
import com.ca.arcflash.listener.service.event.JobHistoryEvent;
import com.ca.arcflash.rps.webservice.data.RpsArchiveConfiguationWrapper;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreStatus;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.datastore.RPSDataStoreUtil;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DInfo;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DStatus;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DType;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.toedge.IEdgeD2DJobService;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgeNetworkConfiguration;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVCMConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeD2DDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao.JobHistoryProductType;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.arcserve.edge.common.annotation.NonSecured;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DService;
import com.ca.arcserve.edge.app.base.webservice.alert.AlertManager;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.RepJobMonitor4Edge;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.VCMStorage;
import com.ca.arcserve.edge.app.base.webservice.contract.email.EmailTemplateFeature;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DBackupJobStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DActiveSyncLogXMLParser;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DArchiveXMLParser;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DBackupCacheXmlParser;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DBackupDataSynchronizer;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DBaseXmlParser;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DJobStatusXmlParser;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DSyncMessage;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DSyncTaskIDMap;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DVCMXMLParser;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DVMInfoXMLParser;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DConversionJobsStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DJobsStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DMergeJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DStatusCommonService;
import com.ca.arcserve.edge.app.base.webservice.dataSync.DataSyncEventHandlerFactory;
import com.ca.arcserve.edge.app.base.webservice.dataSync.IDataSyncEventHandler;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMManager;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryHandler;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.log.D2DActivityLog;
import com.ca.arcserve.edge.app.base.webservice.node.NetworkAdapterInfoUtil;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.notify.StatusUtil;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsConnectionInfoDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.webservice.datastore.DataStoreManager;

public class D2DEdgeServiceImpl implements IEdgeD2DService, IEdgeD2DJobService, IFlashListener {
	private static final Logger logger = Logger
			.getLogger(D2DEdgeServiceImpl.class);
	private NodeServiceImpl nodeService = new NodeServiceImpl();
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IEdgeConnectInfoDao dao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IRpsDataStoreDao dsDao = null;
	public void setDao(IEdgeConnectInfoDao dao) {
		this.dao = dao;
	}

	public void setHostMgrDao(IEdgeHostMgrDao hostMgrDao) {
		this.hostMgrDao = hostMgrDao;
	}
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeVSBDao vsbDao = null;
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	
	private IActivityLogService activityLogService = new ActivityLogServiceImpl();

	@Override
	public String getBackupConfigurationXML(String afguid, String buildNumber,
			String majorVersion, String minorVersion) throws EdgeServiceFault {
		IEdgeD2DDao idao = DaoFactory.getDao(IEdgeD2DDao.class);
		String[] backupXML = new String[1];
		idao.getBackupConfigurationXML(afguid, buildNumber, majorVersion,
				minorVersion, backupXML);
		return backupXML[0];
	}

	@Override
	public int reportBackupConfigurationXML(String afguid, String buildNumber,
			String majorVersion, String minorVersion,
			String backupConfigurationXML) throws EdgeServiceFault {
		IEdgeD2DDao idao = DaoFactory.getDao(IEdgeD2DDao.class);
		return idao.reportBackupConfigurationXML(afguid, buildNumber,
				majorVersion, minorVersion, backupConfigurationXML);
	}
	
	private String getHostnameById(int nodeId) {
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(nodeId, 1, hostList);
		if (hostList.size() > 0) {
			if(StringUtil.isEmptyOrNull(hostList.get(0).getRhostname())){
				if(StringUtil.isEmptyOrNull(hostList.get(0).getVmname()))
					return "";
				else 
					return EdgeCMWebServiceMessages.getMessage("unknown_vm", hostList.get(0).getVmname());
			} else 				
				return hostList.get(0).getRhostname();
		} else {
			logger.debug("D2DEdgeServiceImpl.getHostnameById(): cannot get node name of d2d host id " + nodeId);
			return "";
		}
	}
	
	private String getVMNameById(int nodeId) {
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(nodeId, 1, hostList);
		if (hostList.size() > 0) {
			return hostList.get(0).getVmname();
		} else {
			logger.debug("D2DEdgeServiceImpl.getVMNameById(): cannot get the vitrual machine name [nodeId= " + nodeId + "]");
			return "";
		}
	}

	private static int GetConnInfoByUUID(String uuid, int[] rhostid,
			String[] hostname, String[] protocol, int[] port) {
		int result = 0;
		int[] protocolN = new int[1];

		IEdgeConnectInfoDao connInfoDao = DaoFactory
				.getDao(IEdgeConnectInfoDao.class);
		result = connInfoDao.as_edge_GetConnInfoByUUID(uuid, rhostid, hostname,
				protocolN, port);
		if (result == 0) {
			if (protocolN[0] == Protocol.Https.ordinal())
				protocol[0] = "https";
			else
				protocol[0] = "http";
		}

		return result;
	}
	
	public static int GetRpsConnInfoByUUID(String uuid, int[] rhostid, 
			String[] hostname, String[] protocol, int[] port) {
		int[] protocolN = new int[1];
		IRpsConnectionInfoDao dao = DaoFactory.getDao(IRpsConnectionInfoDao.class);
		
		int result = dao.as_edge_rps_GetConnInfoByUUID(uuid, rhostid, hostname, protocolN, port);
		if (result == 0) {
			protocol[0] = protocolN[0] == Protocol.Https.ordinal() ? "https" : "http";
		} else if (result != 100) {	// 100: not found
			logger.warn("GetRpsConnInfoByUUID failed, return code = " + result);
		}

		return result;
	}

	@Override
	public int D2DSyncActiveLogXML(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag)throws EdgeServiceFault
	{
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (result == 100) {
			logger.error("Calling GetConnInfoByUUID() failed, cannot find D2D node infomration");
			return 3;
		} else if (result != 0) {
			logger.error("Calling GetConnInfoByUUID() failed.");
			return -1;
		}

		D2DActiveSyncLogXMLParser syncer = new D2DActiveSyncLogXMLParser();
		syncer.setBranchid(rhostid[0]);
		syncer.setHostName(hostname[0]);
		syncer.setTaskId(edgeTaskId);
		result = syncer.processActivityLogTrans(xmlContent, cleanFlag);

		return result;
	}

	@Override
	public int D2DSyncXML(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag)
			throws EdgeServiceFault {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (result == 100) {
			logger.error("Calling GetConnInfoByUUID() failed, cannot find D2D node infomration");
			return 3;
		} else if (result != 0) {
			logger.error("Calling GetConnInfoByUUID() failed.");
			return -1;
		}

		D2DBackupCacheXmlParser syncer = new D2DBackupCacheXmlParser(xmlContent);
		syncer.setBranchid(rhostid[0]);
		syncer.setHostName(hostname[0]);
		syncer.setTaskId(edgeTaskId);
		result = syncer.processBackupCacheContent(cleanFlag);


		return result;
	}

	@Override
	public int D2DSyncJobStatus(String xmlContent, @NotPrintAttribute String uuid)
			throws EdgeServiceFault {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (result == 100) {
			return 3;
		} else if (result != 0) {
			return -1;
		}

//		ServiceInfoList serviceInfoList = WebServiceFactory.getServiceInfoList(
//				protocol[0], hostname[0], port[0]);
//		ServiceInfo featureServiceInfo = WebServiceFactory
//				.getFeatureServiceInfo(
//						ServiceInfoConstants.SERVICE_ID_D2D_PROPER_V2,
//						serviceInfoList);
//		WebServiceClientProxy webService = WebServiceFactory.getFlassService(
//				protocol[0], hostname[0], port[0],
//				ServiceInfoConstants.SERVICE_ID_D2D_PROPER_V2,
//				featureServiceInfo);
//		ID2DReSyncService d2dReSyncService = webService.getServiceV2();

		D2DJobStatusXmlParser jobStatusParser = new D2DJobStatusXmlParser();
		jobStatusParser.setBranchid(rhostid[0]);
		return jobStatusParser.processJobStatus(xmlContent);
	}

	@Override
	@NonSecured
	public int promoteEmailToEdge(List<CommonEmailInformation> infoList)
			throws EdgeServiceFault {
		logger.info("start receive alert from webservice with info size: " +  infoList == null ? "null" : infoList.size() );
		EdgeEmailService service = EdgeEmailService.GetInstance();
		for (CommonEmailInformation cei : infoList) {

			// if the alert message need be sent by edge server
			if (cei.isSendViaEdge()) {
				service.SendMailWithGlobalSetting(cei.getSendhost(), cei
						.getSubject(), cei.getContent(), EmailTemplateFeature.D2D);
			}

			cei.setEventType( cei.getEventType()^cei.getProductType() ); 
			AlertManager alertMgr = AlertManager.getInstance();
			alertMgr.SaveAlertInfo(cei);
		}
		return 0;
	}

	@Override
	public int D2DEndSync(boolean fullSyncMode, long edgeTaskId, @NotPrintAttribute String uuid, boolean result)
			throws EdgeServiceFault {
		int ret = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		ret = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (ret == 100) {
			logger.error("Calling GetConnInfoByUUID() failed, cannot find D2D node infomration");
			return 3;
		} else if (ret != 0) {
			logger.error("Calling GetConnInfoByUUID() failed.");
			return -1;
		}

		D2DBackupDataSynchronizer syncer = new D2DBackupDataSynchronizer();

		if(result) {
			ret = syncer.UpdateSyncHistory(rhostid[0], 1,
					D2DBackupDataSynchronizer.SYNC_STATUS_SUCCEED);

			if(ret == 0) {
				if(fullSyncMode)
					D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Information,
							D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_SUCCEEDED);
				else
				if(!fullSyncMode)
					D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Information,
							D2DSyncMessage.EDGE_D2D_SYNC_SUCCEEDED);
			}
			else {
				if(fullSyncMode)
					D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Error,
							D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_FAILED);
				else
				if(!fullSyncMode)
					D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Error,
							D2DSyncMessage.EDGE_D2D_SYNC_FAILED);
			}
		}
		else{
			ret = syncer.UpdateSyncHistory(rhostid[0], 1,
					D2DBackupDataSynchronizer.SYNC_STATUS_FAILED);

			if(fullSyncMode)
				D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Error,
						D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_FAILED);
			else
			if(!fullSyncMode)
				D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Error,
						D2DSyncMessage.EDGE_D2D_SYNC_FAILED);
		}

		return 0;
	}
	
	@Override
	public long D2DStartSync(boolean fullSyncMode, @NotPrintAttribute String uuid)
			throws EdgeServiceFault {
		int ret = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		ret = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (ret == 100) {
			logger.error("Calling GetConnInfoByUUID() failed, cannot find D2D node infomration");
			return -3;
		} else if (ret != 0) {
			logger.error("Calling GetConnInfoByUUID() failed.");
			return -1;
		}

		long edgeTaskId = 0;
		if(fullSyncMode)
			edgeTaskId = D2DSyncTaskIDMap.getCurrentTaskId(rhostid[0]);
		else
			edgeTaskId = D2DSyncTaskIDMap.getNextTaskId(rhostid[0]);
		
		D2DBackupDataSynchronizer syncer = new D2DBackupDataSynchronizer();

		int result = syncer.UpdateSyncHistory(rhostid[0], 1,
						D2DBackupDataSynchronizer.SYNC_STATUS_IN_PROGRESS);
		if(result != 0) {
			if(fullSyncMode)
				D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Error,
					D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_FAILED);
			else
			if(!fullSyncMode)
				D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Error,
						D2DSyncMessage.EDGE_D2D_SYNC_FAILED);

			return -1;
		}
		else{
			if(fullSyncMode)
				D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Information,
						D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_START);
			else
			if(!fullSyncMode)
				D2DBaseXmlParser.writeActivityLog(hostname[0], edgeTaskId, Severity.Information,
						D2DSyncMessage.EDGE_D2D_SYNC_START);
		}

		return edgeTaskId;
	}

	@Override
	public int D2DSyncVMInfo(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag) throws EdgeServiceFault  {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (result == 100) {
			logger.error("Calling GetConnInfoByUUID() failed, cannot find D2D node infomration");
			return 3;
		} else if (result != 0) {
			logger.error("Calling GetConnInfoByUUID() failed.");
			return -1;
		}

		D2DVMInfoXMLParser syncer = new D2DVMInfoXMLParser();
		syncer.setBranchid(rhostid[0]);
		syncer.setHostName(hostname[0]);
		syncer.setTaskId(edgeTaskId);
		result = syncer.process(xmlContent, cleanFlag);

		return result;
	}

	@Override
	public int D2DSyncTempVMHost(long edgeTaskId,String vmTempHostXml,@NotPrintAttribute String uuid, boolean isFullsync) throws EdgeServiceFault  {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (result == 100) {
			logger.error("Calling GetConnInfoByUUID() failed, cannot find D2D node infomration");
			return 3;
		} else if (result != 0) {
			logger.error("Calling GetConnInfoByUUID() failed.");
			return -1;
		}

		D2DVMInfoXMLParser syncer = new D2DVMInfoXMLParser();
		syncer.setBranchid(rhostid[0]);
		syncer.setHostName(hostname[0]);
		syncer.setTaskId(edgeTaskId);
		result = syncer.processVMTempHostInfo(vmTempHostXml, isFullsync);
		return result;
	}

	@Override
	public int D2DSyncArchive(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag)
			throws EdgeServiceFault {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (result == 100) {
			logger.error("Calling GetConnInfoByUUID() failed, cannot find D2D node infomration");
			return 3;
		} else if (result != 0) {
			logger.error("Calling GetConnInfoByUUID() failed.");
			return -1;
		}

		D2DArchiveXMLParser syncer = new D2DArchiveXMLParser();
		syncer.setBranchid(rhostid[0]);
		syncer.setHostName(hostname[0]);
		syncer.setTaskId(edgeTaskId);
		result = syncer.process(xmlContent, cleanFlag);

		return result;
	}

	@Override
	public int D2DSyncVCM(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag)
			throws EdgeServiceFault {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];

		result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		if (result == 100) {
			logger.error("Calling GetConnInfoByUUID() failed, cannot find D2D node infomration");
			return 3;
		} else if (result != 0) {
			logger.error("Calling GetConnInfoByUUID() failed.");
			return -1;
		}

		D2DVCMXMLParser syncer = new D2DVCMXMLParser();
		syncer.setBranchid(rhostid[0]);
		syncer.setHostName(hostname[0]);
		syncer.setTaskId(edgeTaskId);
		result = syncer.process(xmlContent, cleanFlag);

		return result;
	}

	@NonSecured
	@Override
	public int validateUserByUUID(String uuid) throws EdgeServiceFault {
		EdgeWebServiceImpl impl = new EdgeWebServiceImpl();
		return impl.validateUserByUUID(uuid);
	}

	@Override
	public boolean isManagedByEdge(String uuid) throws EdgeServiceFault {
		IEdgeConnectInfoDao icdao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		IEdgeVCMDao vcmDao = DaoFactory.getDao(IEdgeVCMDao.class);
		List<EdgeD2DHost> d2dNodes = new ArrayList<EdgeD2DHost>();
		List<EdgeD2DHost> proxies = new ArrayList<EdgeD2DHost>();
		try {
			// Check d2d nodes
			icdao.as_edge_d2dhost_list(d2dNodes);
			for (EdgeD2DHost d2d : d2dNodes) {
				if (d2d.getUuid() != null && d2d.getUuid().equalsIgnoreCase(uuid)) {
					return true;
				}
			}

			// Check proxies
			vcmDao.as_edge_vsphere_proxy_list(proxies);
			for (EdgeD2DHost proxy : proxies) {
				if (proxy.getUuid() != null && proxy.getUuid().equalsIgnoreCase(uuid)) {
					return true;
				}
			}

			// Check VSB Converter
			IEdgeVSBDao vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
			int[] count = new int[1];
			vsbDao.as_edge_vsb_converter_getUsageCount(uuid, count);
			if (count[0] > 0) {
				return true;
			}
		} catch (DaoException dao) {
			logger.error("[D2DEdgeServiceImpl] isManagedByEdge, DB failed, uuid : " + uuid, dao);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_Dao_Execption, dao.getMessage());
		}
		return false;
	}

//	private boolean isNodeBeingManagedAndWithUuid(EdgeD2DHost d2d, String uuid) {
//		if (NodeManagedStatus.parseInt(d2d.getManaged()) != NodeManagedStatus.Managed) {
//			if (d2d.getUuid() != null && d2d.getUuid().equals(uuid)) {
//				logger.info("Managed status check, the node exists but not managed by CPM!");
//				return false;
//			}
//		}
//		// To avoid the proxy's uuid is null, we need add one more condition
//		if (d2d.getUuid() != null && d2d.getUuid().equals(uuid)) {
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public int checkPolicyStatus(String d2dUuid, String policyUuid, boolean justcheck)
			throws EdgeServiceFault {
		return PolicyManagementServiceImpl.getInstance().doCheckPolicyStatus(d2dUuid, policyUuid, justcheck);
	}

	@Override
	public int syncBackupJobsStatus(String vmUuid, JobMonitor jobMonitor)
			throws EdgeServiceFault {
		int[] vmHostId = new int[1];
		esxDao.as_edge_host_getHostByInstanceUUID(0, vmUuid, vmHostId);	//TODO: gateway
		if (jobMonitor.getJobPhase() == Constants.JobExitPhase || jobMonitor.getJobPhase() == Constants.BackupJob_Phase_PROC_EXIT) {
//			D2DJobsStatusCache.getD2DBackupJobsStatusCache().remove(String.valueOf(vmHostId[0]));
			D2DStatusInfo statusInfo = jobMonitor.getD2DStatusInfo();
			if (statusInfo!=null) {
				D2DStatusCommonService.getD2DStatusCommonServiceInstance().saveD2DStatusInfo(vmHostId[0], statusInfo);
			}
		} 
		
		if(jobMonitor.getJobPhase() == Constants.PHASE_BACKUP_PHASE_WAITING){ // phase waiting should not override other phase
			D2DBackupJobStatusInfo last = D2DJobsStatusCache.getD2DBackupJobsStatusCache().get(String.valueOf(vmHostId[0]));
			if(last!=null && last.getJobPhase() != Constants.PHASE_BACKUP_PHASE_WAITING){
				return 0;
			}
		}
		
		D2DBackupJobStatusInfo backupInfo = D2DJobsStatusCache.getD2DBackupJobsStatusCache().get(String.valueOf(vmHostId[0]));
		if (backupInfo != null && backupInfo.getJobPhase() != jobMonitor.getJobPhase()) {
			if (jobMonitor.getJobPhase() == Constants.JobExitPhase || jobMonitor.getJobPhase() == Constants.BackupJob_Phase_PROC_EXIT) {
				D2DJobsStatusCache.getD2DBackupJobsStatusCache().updateJobPhase(D2DJobsStatusCache.JOB_PHASE, vmHostId[0]);
			} else {					
				D2DJobsStatusCache.getD2DBackupJobsStatusCache().updateJobPhase((int) jobMonitor.getJobPhase(), vmHostId[0]);
			}
		}
		D2DJobsStatusCache.getD2DBackupJobsStatusCache().put(String.valueOf(vmHostId[0]), convertToD2DBackupJobStatusInfo(vmHostId[0], jobMonitor));
				
		return 0;
	}
	
	@Override
	public int syncBackupJobsStatusAll(List<String> vmUuid, List<JobMonitor> jobMonitor)
			throws EdgeServiceFault {
		for (int i = 0; i < vmUuid.size(); i++) {
			String uuid = vmUuid.get(i);
			syncBackupJobsStatus(uuid, jobMonitor.get(i));
		}
		return 0;
	}
	
	private D2DBackupJobStatusInfo convertToD2DBackupJobStatusInfo(int hostId, JobMonitor jobMonitor) {
		D2DBackupJobStatusInfo backupJobStatusInfo = new D2DBackupJobStatusInfo();
		backupJobStatusInfo.setNodeId(hostId);
		backupJobStatusInfo.setBackupStartTime(jobMonitor.getBackupStartTime());
		backupJobStatusInfo.setCompressLevel(jobMonitor.getCompressLevel());
		backupJobStatusInfo.setCtBKJobName(jobMonitor.getCtBKJobName());
		backupJobStatusInfo.setCtBKStartTime(jobMonitor.getCtBKStartTime());
		backupJobStatusInfo.setCtCurCatVol(jobMonitor.getCtCurCatVol());
		backupJobStatusInfo.setCurrentProcessDiskName(jobMonitor.getCurrentProcessDiskName());
		backupJobStatusInfo.setCurVolMntPoint(jobMonitor.getCurVolMntPoint());
		backupJobStatusInfo.setCtDWBKJobID(jobMonitor.getCtDWBKJobID());
		backupJobStatusInfo.setDwBKSessNum(jobMonitor.getDwBKSessNum());
		backupJobStatusInfo.setElapsedTime(jobMonitor.getElapsedTime());
		backupJobStatusInfo.setEncInfoStatus(jobMonitor.getEncInfoStatus());
		backupJobStatusInfo.setEstimateBytesDisk(jobMonitor.getEstimateBytesDisk());
		backupJobStatusInfo.setEstimateBytesJob(jobMonitor.getEstimateBytesJob());
		backupJobStatusInfo.setFlags(jobMonitor.getFlags());
		backupJobStatusInfo.setId(jobMonitor.getJobId());
		backupJobStatusInfo.setJobMethod(jobMonitor.getJobMethod());
		backupJobStatusInfo.setJobPhase(jobMonitor.getJobPhase());
		backupJobStatusInfo.setJobStatus(jobMonitor.getJobStatus());
		backupJobStatusInfo.setJobType(jobMonitor.getJobType());
		backupJobStatusInfo.setnProgramCPU(jobMonitor.getnProgramCPU());
		backupJobStatusInfo.setnReadSpeed(jobMonitor.getnReadSpeed());
		backupJobStatusInfo.setnSystemCPU(jobMonitor.getnSystemCPU());
		backupJobStatusInfo.setnSystemReadSpeed(jobMonitor.getnSystemReadSpeed());
		backupJobStatusInfo.setnSystemWriteSpeed(jobMonitor.getnSystemWriteSpeed());
		backupJobStatusInfo.setnWriteSpeed(jobMonitor.getnWriteSpeed());
		backupJobStatusInfo.setProductType(jobMonitor.getProductType());
		backupJobStatusInfo.setSessionID(jobMonitor.getSessionID());
		backupJobStatusInfo.setThrottling(jobMonitor.getThrottling());
		backupJobStatusInfo.setTotalSizeRead(jobMonitor.getTotalSizeRead());
		backupJobStatusInfo.setTotalSizeWritten(jobMonitor.getTotalSizeWritten());
		backupJobStatusInfo.setTransferBytesDisk(jobMonitor.getTransferBytesDisk());
		backupJobStatusInfo.setTransferBytesJob(jobMonitor.getTransferBytesJob());
		backupJobStatusInfo.setTransferMode(jobMonitor.getTransferMode());
		backupJobStatusInfo.setUlMergedSession(jobMonitor.getUlMergedSessions());
		backupJobStatusInfo.setUlProcessedFolder(jobMonitor.getUlProcessedFolder());
		backupJobStatusInfo.setUlTotalFolder(jobMonitor.getUlProcessedFolder());
		backupJobStatusInfo.setUlTotalMergedSessions(jobMonitor.getUlTotalMegedSessions());
		backupJobStatusInfo.setVmInstanceUUID(jobMonitor.getVmInstanceUUID());
		backupJobStatusInfo.setVolMethod(jobMonitor.getVolMethod());
		backupJobStatusInfo.setWszEDB(jobMonitor.getWszEDB());
		backupJobStatusInfo.setWszMailFolder(jobMonitor.getWszMailFolder());
		backupJobStatusInfo.setWzBKBackupDest(jobMonitor.getWzBKBackupDest());
		backupJobStatusInfo.setWzBKDestPassword(jobMonitor.getWzBKDestPassword());
		backupJobStatusInfo.setWzBKDestUsrName(jobMonitor.getWzBKDestUsrName());
		if (jobMonitor.getD2DStatusInfo()!=null) {			
			backupJobStatusInfo.setD2DStatusInfo(convertToD2DStatusInfo(jobMonitor.getD2DStatusInfo()));
		}
		return backupJobStatusInfo;
	}
	
	private com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo convertToD2DStatusInfo(D2DStatusInfo info) {
		com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo statusInfo = new com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo();
		statusInfo.setBackupConfiged(info.isBackupConfiged());
		statusInfo.setDestinationAccessible(info.isDestinationAccessible());
		statusInfo.setDestinationEstimatedBackupCount(info.getDestinationEstimatedBackupCount());
		statusInfo.setDestinationFreeSpace(info.getDestinationFreeSpace());
		statusInfo.setDestinationPath(info.getDestinationPath());
		statusInfo.setDestinationStatus(NodeServiceImpl.dbValueToD2DStatus(info.getDestinationStatus().ordinal()));
		statusInfo.setDriverInstalled(info.isDriverInstalled());
		statusInfo.setEstimatedValue(NodeServiceImpl.dbValueToD2DEstimatedValue(info.getEstimatedValue().ordinal()));
		statusInfo.setLastBackupJobStatus(NodeServiceImpl.dbValueToD2DJobStatus(info.getLastBackupJobStatus().ordinal()));
		statusInfo.setLastBackupStartTime(info.getLastBackupStartTime());
		statusInfo.setLastBackupStatus(NodeServiceImpl.dbValueToD2DStatus(info.getLastBackupStatus().ordinal()));
		statusInfo.setLastBackupType(NodeServiceImpl.dbValueToD2DBackupType(info.getLastBackupType().ordinal()));
		statusInfo.setOverallStatus(NodeServiceImpl.dbValueToD2DStatus(info.getOverallStatus().ordinal()));
		statusInfo.setRecoveryPointCount(info.getRecoveryPointCount());
		statusInfo.setRecoveryPointMounted(info.getRecoveryPointMounted());
		statusInfo.setRecoveryPointRetentionCount(info.getRecoveryPointRetentionCount());
		statusInfo.setRecoveryPointStatus(NodeServiceImpl.dbValueToD2DStatus(info.getRecoveryPointStatus().ordinal()));
		statusInfo.setIsUseBackupSets(info.getIsUseBackupSets());
		statusInfo.setRestarted(info.isRestarted());

		List<VCMStorage> vcmStorageList = new LinkedList<VCMStorage>();
		for (com.ca.arcflash.webservice.edge.data.d2dstatus.VCMStorage d2dStorage : info.getDestinationVCMStorages())
		{
			VCMStorage storage = new VCMStorage();
			storage.setName( d2dStorage.getName() );
			storage.setFreeSize( d2dStorage.getFreeSize() );
			storage.setColdStandySize(d2dStorage.getColdStandSize());
			storage.setOtherSize(d2dStorage.getOtherSize());
			storage.setTotalSize(d2dStorage.getTotalSize());
			vcmStorageList.add( storage );
		}
		statusInfo.setDestinationVCMStorages( vcmStorageList.toArray( new VCMStorage[0] ) );
		return statusInfo;
	}

	@Override
	public int syncConversionJobInfo(String uuid, RepJobMonitor repJobMonitor, D2DStatusInfo d2dStatusInfo) {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];
		esxDao.as_edge_host_getHostByInstanceUUID(0, uuid, rhostid);	//TODO: gateway
		if (rhostid[0]==0) {			
			result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
			if (result == 100) {
				return 3;
			} else if (result != 0) {
				return -1;
			}
		}
		if (d2dStatusInfo!=null) {
			D2DStatusCommonService.getD2DStatusCommonServiceInstance().saveD2DStatusInfo(rhostid[0], d2dStatusInfo);
		}
		repJobMonitor.setNodeId(rhostid[0]);
		RepJobMonitor4Edge conversionInfo = D2DConversionJobsStatusCache.getJobsStatusCache().get(String.valueOf(rhostid[0]));
		if (conversionInfo != null && conversionInfo.getRepPhase() != repJobMonitor.getRepPhase()) {
			if (repJobMonitor.getRepPhase() == Constants.JobExitPhase || repJobMonitor.getRepPhase() == Constants.BackupJob_Phase_PROC_EXIT) {
				D2DConversionJobsStatusCache.getJobsStatusCache().updateJobPhase(D2DConversionJobsStatusCache.JOB_PHASE, rhostid[0]);
			} else {					
				D2DConversionJobsStatusCache.getJobsStatusCache().updateJobPhase(repJobMonitor.getRepPhase(), rhostid[0]);
			}
		}
		D2DConversionJobsStatusCache.getJobsStatusCache().put(String.valueOf(rhostid[0]), convertRepJobMonitor4Edge(repJobMonitor, d2dStatusInfo));
		return 0;
	}
	
	private RepJobMonitor4Edge convertRepJobMonitor4Edge(RepJobMonitor repJobMonitor, D2DStatusInfo d2dStatusInfo) {
		RepJobMonitor4Edge repJobMonitor4Edge = new RepJobMonitor4Edge();
		repJobMonitor4Edge.setCurrentJobCancelled(repJobMonitor.isCurrentJobCancelled());
		repJobMonitor4Edge.setCurrentSnapshotCount(repJobMonitor.getCurrentSnapshotCount());
		repJobMonitor4Edge.setFirst(repJobMonitor.isFirst());
		repJobMonitor4Edge.setId(repJobMonitor.getId());
		repJobMonitor4Edge.setNodeId(repJobMonitor.getNodeId());
		repJobMonitor4Edge.setRepElapsedTime(repJobMonitor.getRepElapsedTime());
		repJobMonitor4Edge.setRepJobElapsedTime(repJobMonitor.getRepJobElapsedTime());
		repJobMonitor4Edge.setRepJobID(repJobMonitor.getRepJobID());
		repJobMonitor4Edge.setRepJobStartTime(repJobMonitor.getRepJobStartTime());
		repJobMonitor4Edge.setRepPhase(repJobMonitor.getRepPhase());
		repJobMonitor4Edge.setRepSessionBackupTime(repJobMonitor.getRepSessionBackupTime());
		repJobMonitor4Edge.setRepSessionName(repJobMonitor.getRepSessionName());
		repJobMonitor4Edge.setRepStartNanoTime(repJobMonitor.getRepStartNanoTime());
		repJobMonitor4Edge.setRepStartTime(repJobMonitor.getRepStartTime());
		repJobMonitor4Edge.setRepTotalSize(repJobMonitor.getRepTotalSize());
		repJobMonitor4Edge.setRepTransAfterResume(repJobMonitor.getRepTransAfterResume());
		repJobMonitor4Edge.setRepTransBeforeResume(repJobMonitor.getRepTransBeforeResume());
		repJobMonitor4Edge.setRepTransedSize(repJobMonitor.getRepTransedSize());
		repJobMonitor4Edge.setTargetMachine(repJobMonitor.getTargetMachine());
		repJobMonitor4Edge.setToRepSessions(repJobMonitor.getToRepSessions());
		repJobMonitor4Edge.setToRepSessionsSize(repJobMonitor.getToRepSessionsSize());
		repJobMonitor4Edge.setTotalSessionNumbers(repJobMonitor.getTotalSessionNumbers());
		if (d2dStatusInfo!=null) {			
			repJobMonitor4Edge.setInfo(convertToD2DStatusInfo(d2dStatusInfo));
		}
		return repJobMonitor4Edge;
	}

	@Override
	public int syncD2DStatusInfo(String uuid, ApplicationType appType, D2DStatusInfo d2dStatusInfo)
			throws EdgeServiceFault {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];
		if (ApplicationType.CentralManagement == appType) {
			result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
			if (result == 100) {
				return 3;
			} else if (result != 0) {
				return -1;
			}
		} else if (ApplicationType.vShpereManager == appType) {
			esxDao.as_edge_host_getHostByInstanceUUID(0, uuid, rhostid);	//TODO: gateway
		} else if (ApplicationType.VirtualConversionManager == appType) {
			esxDao.as_edge_host_getHostByInstanceUUID(0, uuid, rhostid);	//TODO: gateway
			if (rhostid[0] == 0) {
				hyperVDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(0, uuid, rhostid);	//TODO: need gateway
				if (rhostid[0] == 0) {
					result = GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
					if (result == 100) {
						return 3;
					} else if (result != 0) {
						return -1;
					}
				}
			}
		}
		if (d2dStatusInfo != null) {
			if (ApplicationType.VirtualConversionManager == appType) {
				D2DStatusCommonService.getD2DStatusCommonServiceInstance().saveVSBStatusInfo(rhostid[0], d2dStatusInfo);
			} else {
				D2DStatusCommonService.getD2DStatusCommonServiceInstance().saveD2DStatusInfo(rhostid[0], d2dStatusInfo);
			}
		} else {
			logger.info("sync status failed and application type is " + appType.name() + " UUID = " + uuid);
		}
		return 0;
	}

	@Override
	public int syncVSphereStatusAll(List<D2DStatusInfo> statusInfoList)
			throws EdgeServiceFault {
		int[] vmHostId = new int[1];
		for(D2DStatusInfo info : statusInfoList) {
			esxDao.as_edge_host_getHostByInstanceUUID(0, info.getUuid(), vmHostId);	//TODO: gateway
			if(vmHostId[0] != 0)
				D2DStatusCommonService.getD2DStatusCommonServiceInstance().saveD2DStatusInfo(vmHostId[0], info);
		} 
		return 0;
	}

	@Override
	public int syncVCMStatusAll(List<D2DStatusInfo> statusInfoList)
			throws EdgeServiceFault {
		int result = 0;
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];
		for(D2DStatusInfo info : statusInfoList) {			
			esxDao.as_edge_host_getHostByInstanceUUID(0, info.getUuid(), rhostid);	//TODO: gateway
			if (rhostid[0] == 0) {
				hyperVDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(0, info.getUuid(), rhostid);	//TODO: need gateway
			}
			if (rhostid[0]==0) {			
				result = GetConnInfoByUUID(info.getUuid(), rhostid, hostname, protocol, port);
				if (result == 100) {
					return 3;
				} else if (result != 0) {
					return -1;
				}
			}
			if (rhostid[0] != 0) 
				D2DStatusCommonService.getD2DStatusCommonServiceInstance().saveD2DStatusInfo(rhostid[0], info);
		}
		return 0;
	}

	@Override
	public int syncADRConfigureToVCM(String uuid, ADRConfigure adrConfigInfo) throws EdgeServiceFault {
		int[] hostId = new int[1];
		esxDao.as_edge_host_getHostByInstanceUUID(0, uuid, hostId);	//TODO: gateway
		if (hostId[0] == 0) { // If host is not esx vm, then we will check hyper-v nodes
			hyperVDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(0, uuid, hostId);	//TODO: need gateway
		}
		if (hostId[0] == 0) { // If node is not both esx and hyper-v vm, we assume this node is a general node. Then we query in as_edge_connect_info.	
			hostMgrDao.as_edge_host_getHostIdByUuid(uuid, 1, hostId);
		}
		logger.info("syncADRConfigureToVCM host ID = " + hostId[0] + " , UUID = " + uuid);
		if (hostId[0] != 0 && adrConfigInfo != null && adrConfigInfo.getNetadapters() != null 
				&& adrConfigInfo.getNetadapters().size() > 0) {			
			if (adrConfigInfo.isNetworkAdapterInfoFromPolicy()) {
				
				List<EdgeNetworkConfiguration> sourceMachineAdapter = new ArrayList<EdgeNetworkConfiguration>();
				hostMgrDao.as_edge_vcm_sourceMachineAdapter_selectById(hostId[0], sourceMachineAdapter);
				if (sourceMachineAdapter.size() == 0) {
					insertSourceMachineAdapter(hostId[0], adrConfigInfo);
				}
			} else {				
				// delete all record
				hostMgrDao.as_edge_vcm_sourceMachineAdapter_deleteByHostId(hostId[0]);
				insertSourceMachineAdapter(hostId[0], adrConfigInfo);
			}
			return 0;
		} else {
			return -1;
		}
	}
	
	private void insertSourceMachineAdapter(int hostId, ADRConfigure adrConfigInfo) {
		List<EdgeNetworkConfiguration> configurationList = NetworkAdapterInfoUtil.getEdgeNetworkConfigurationFromADRConfigure(adrConfigInfo);
		for (EdgeNetworkConfiguration config : configurationList) {
			hostMgrDao.as_edge_vcm_sourceMachineAdapter_insert(hostId, config.getAdapterDesc(), config.getMacAddress(),
					config.getIsDHCP(), config.getIpStr(), config.getGatewayStr(), config.getDnsStr(),
					config.getWinsStr());
		}
	}

	// Not used
	@Override
	public List<IPSetting> getIPSettingFromVCM(String uuid) throws EdgeServiceFault {
		int[] hostId = new int[1];
		hostMgrDao.as_edge_host_getHostIdByUuid(uuid, 1, hostId);
		if (hostId[0] != 0) {
			Node node = new Node();
			node.setId(hostId[0]);
			return nodeService.getIPSettingFromVCM(node, null);
		}
		return null;
	}

	@Override
	public void syncMergeJob(MergeStatus[] statusArray) throws EdgeServiceFault {
		if (statusArray == null || statusArray.length == 0) {
			return;
		}
		
		for (MergeStatus status : statusArray) {
			int[] hostId = new int[1];
			
			if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.CentralManagement) {
				int[] rhostid = new int[1];
				String[] hostname = new String[1];
				String[] protocol = new String[1];
				int[] port = new int[1];

				int result = GetConnInfoByUUID(status.getUUID(), rhostid, hostname, protocol, port);
				if (result == 0) {
					hostId[0] = rhostid[0];
				}
			} else if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.vShpereManager) {
				esxDao.as_edge_host_getHostByInstanceUUID(0, status.getUUID(), hostId);	//TODO: gateway
			}
			
			if (hostId[0] <= 0) {
				continue;
			}
			
			D2DMergeJobStatusCache.getInstance().add(hostId[0], status);
		}
	}

	@Override
	public String validateUserByUser(String username, String password, String domain) throws EdgeServiceFault {
		EdgeWebServiceImpl impl = new EdgeWebServiceImpl();
		return impl.validateUserByUser(username, password, domain);
	}

	/**
	 * Get host id by uuid, first check d2d node then check vm
	 * 
	 * @param uuid
	 *            The uuid, it might node uuid or vminstance uuid
	 * @return The host id in the host table
	 */
	public static int getHostId(String uuid) {
		int hostId = getD2DHostId(uuid);
		if (hostId == 0) {
			return getVmHostId(uuid);
		}
		return hostId;
	}

	public static int getVmHostId(String vmInstanceUuid) {
		int[] rhostid = new int[1];
		esxDao.as_edge_host_getHostByInstanceUUID(0, vmInstanceUuid, rhostid);	//TODO: gateway
		return rhostid[0];
	}
	
	public static int getVisibleVmHostId(String vmInstanceUuid) {
		int[] rhostid = new int[1];
		esxDao.as_edge_host_getVisibleHostByInstanceUUID(0, vmInstanceUuid, rhostid);	//TODO: gateway
		return rhostid[0];
	}
	
	public static int getVmHostId(int gatewayId, String vmInstanceUuid) {
		int[] rhostid = new int[1];
		esxDao.as_edge_host_getVisibleHostByInstanceUUID(gatewayId, vmInstanceUuid, rhostid);
		return rhostid[0];
	}
	
	public static int getD2DHostId(String uuid) {
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];
		
		GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		return rhostid[0];
	}
	
	private int getLinuxBackupServerIdByNodeId(int nodeId){
		List<EdgeConnectInfo> linuxD2DInfo = new ArrayList<EdgeConnectInfo>();
		dao.as_edge_linux_d2d_server_by_hostid(nodeId, linuxD2DInfo);
		if (linuxD2DInfo != null && linuxD2DInfo.size() > 0) {
			return linuxD2DInfo.get(0).getHostid();
		}else{
			return 0;
		}
	}
	
	public static int getRpsHostId(String uuid) {
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];
		
		GetRpsConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		
		return rhostid[0];
	}

	@Override
	public int handleJobEvent(JobEvent event) {
		logger.debug("D2DEdgeServiceImpl.handleJobEvent(JobEvent) Enter, Source = " + event.getSource());
		FlashJobMonitor jobMonitor = null;
		int runningServerId = 0;
		if (event.getSource() == Source.D2D) {
			for (JobEventArg jobStatus : event.getJobMonitors()) {
				logger.debug("Source.D2D "+jobStatus.toString());
				jobMonitor = jobStatus.getJobMonitor();
				if(!isValidJobMonitor(jobMonitor)){
					continue;
				}
				int hostId = 0;
				if(jobMonitor.getJobType()==JobType.JOBTYPE_START_INSTANT_VM ||jobMonitor.getJobType()==JobType.JOBTYPE_STOP_INSTANT_VM){
					InstantVMManager.getInstance().handleJobMonitor(jobMonitor, JobHistoryProductType.D2D);
					continue;
				}
				if (jobStatus.isForVM()) {
					logger.debug("isForVM VmInstanceUuid"+jobStatus.getVmInstanceUuid());
					// get the proxy node id.
					runningServerId = getD2DHostId(jobStatus.getRunningServerUuid());
					int gatewayId = 0;
					if(runningServerId>0){					
						try {
							GatewayEntity gatewayEntity = gatewayService.getGatewayByHostId(runningServerId);
							gatewayId = gatewayEntity.getId().getRecordId();
							logger.debug("D2DEdgeServiceImpl.handleJobEvent(JobEvent) gatewayId="+gatewayId+" runningServerId="+runningServerId);
						} catch (EdgeServiceFault e) {
							logger.error("D2DEdgeServiceImpl.handleJobEvent(JobEvent) catch an error when getGatewayByHostId runningServerId="+runningServerId,e);
							gatewayId = 0;
						}
					}
					hostId = getVmHostId(gatewayId,jobStatus.getVmInstanceUuid());
					if (hostId == 0) {
						hostId = getD2DHostId(jobStatus.getVmInstanceUuid());
						logger.debug("isForVM but infact is D2DUUID hostId ="+hostId);
					}
					jobMonitor.setVmInstanceUUID(jobStatus.getVmInstanceUuid());
					logger.debug("isForVM runningServerId ="+runningServerId+" getRunningServerUuid="+jobStatus.getRunningServerUuid());
				} else {
					logger.debug("!isForVM ServerUuid"+jobStatus.getServerUuid());
					hostId = getD2DHostId(jobStatus.getServerUuid());
					runningServerId = hostId;
					jobMonitor.setD2dUuid(jobStatus.getServerUuid());
					logger.debug("!isForVM ServerUuid"+jobStatus.getServerUuid()+" hostId="+hostId+" runningServerId="+runningServerId);
				}
				if (hostId == 0) {
					logger.error("Failed to find node for job event: JobType:" + jobMonitor.getJobType()
							+ " serverUuid:" + jobStatus.getServerUuid() + " vmInstanceUuid:"
							+ jobStatus.getVmInstanceUuid());
					return 0; // defect 99689
				}
				StringBuilder sb = new StringBuilder();
				sb.append("D2D").append("-").append(hostId).append("-").append(runningServerId)
					.append("-").append(jobMonitor.getJobType())
					.append("-").append(jobMonitor.getJobId());
				/*
				 * We assemble the key in cache by "D2D-nodeId-jobType".
				 * But for recovery VM job, D2D can do multiple recovery VM jobs in the same time.
				 * So need append job id to handle the special case.
				 */
				if (jobMonitor.getJobType() == JobType.JOBTYPE_VM_RECOVERY) {
					sb.append("-").append(jobMonitor.getJobId());
				}
				if(!StringUtil.isEmptyOrNull(jobMonitor.getDataStoreUUID())){
					List<EdgeRpsDataStore> dataStores = new ArrayList<EdgeRpsDataStore>();
					getRpsDataStoreDao().as_edge_rps_dedup_setting_list_all(dataStores);
					for (EdgeRpsDataStore store:dataStores) {
						if(jobMonitor.getDataStoreUUID().contains(store.getDatastore_uuid())){
							jobMonitor.setTargetRPSId(store.getNode_id());
							break;
						}
					}
				}
				
				jobMonitor.setJobMonitorId(sb.toString());
				jobMonitor.setNodeId(hostId);
				jobMonitor.setAgentNodeName(getHostnameById(hostId));
				jobMonitor.setRunningServerId(runningServerId);
				jobMonitor.setHistoryProductType(JobHistoryProductType.D2D.getValue());
				logger.debug("D2D source, cache key is " + sb.toString());
				D2DAllJobStatusCache.getD2DAllJobStatusCache().put(sb.toString(), jobMonitor);
				logger.debug("D2D jobMonitor - "+jobMonitor.toString());
				logger.debug("D2D jobMonitor - Name: " + jobMonitor.getD2dServerName() + "; UUID: " + jobStatus.getServerUuid() + "; VMUUID: " + jobStatus.getVmInstanceUuid()
						+ "; Key: " + jobMonitor.getJobMonitorId() + " Job Id = " +jobMonitor.getJobId() + "; isFinished: " + jobMonitor.isFinished());
			}
		} else if (event.getSource() == Source.RPS) {
			for (JobEventArg jobStatus : event.getJobMonitors()) {
				logger.debug("Source.RPS "+jobStatus.toString());
				jobMonitor = jobStatus.getJobMonitor();
				if(!isValidJobMonitor(jobMonitor)){
					continue;
				}
				if(jobMonitor.getJobType()==JobType.JOBTYPE_START_INSTANT_VM ||jobMonitor.getJobType()==JobType.JOBTYPE_STOP_INSTANT_VM ||jobMonitor.getJobType() == JobType.JOBTYPE_LINUX_INSTANT_VM){
					jobMonitor.setRunningOnRPS(jobStatus.isRunningOnRPS());
					InstantVMManager.getInstance().handleJobMonitor(jobMonitor, JobHistoryProductType.RPS);
					continue;
				}
				int hostId = getRpsHostId(event.getServerUuid());
				int nodeId = 0;
				if (jobStatus.isForVM()) {
					logger.debug("isForVM VmInstanceUuid"+jobStatus.getVmInstanceUuid());
					int gatewayId = 0;
					if(hostId>0){ 				
						try {
							GatewayEntity gatewayEntity = gatewayService.getGatewayByHostId(hostId);
							gatewayId = gatewayEntity.getId().getRecordId();
							logger.debug("D2DEdgeServiceImpl.handleJobEvent(JobEvent) gatewayId="+gatewayId+" hostId="+hostId);
						} catch (EdgeServiceFault e) {
							logger.error("D2DEdgeServiceImpl.handleJobEvent(JobEvent) catch an error when getGatewayByHostId hostId="+hostId,e);
							gatewayId = 0;
						}
					}					
					nodeId = getVmHostId(gatewayId,jobStatus.getVmInstanceUuid());
					if (nodeId == 0) {
						nodeId = getD2DHostId(jobStatus.getVmInstanceUuid());
						logger.debug("isForVM but in fact is D2DUUID nodeId="+nodeId);
					}
					jobMonitor.setVmInstanceUUID(jobStatus.getVmInstanceUuid());
					logger.debug("isForVM nodeId="+nodeId);
				} else if(jobMonitor.isLinuxNode()){
					nodeId = getD2DHostId(jobMonitor.getD2dUuid());
					if (nodeId == 0) {
						nodeId = getVisibleVmHostId(jobMonitor.getD2dUuid());
						jobMonitor.setVmInstanceUUID(jobMonitor.getD2dUuid());
						logger.debug("!isForVM but in fact is VMUUID nodeId="+nodeId);
					}
				} else {
					logger.debug("!isForVM ServerUuid="+jobStatus.getServerUuid());
					nodeId = getD2DHostId(jobStatus.getServerUuid());
					if (nodeId == 0) {
						nodeId = getVisibleVmHostId(jobStatus.getServerUuid());
						jobMonitor.setVmInstanceUUID(jobStatus.getServerUuid());
						logger.debug("!isForVM but in fact is VMUUID nodeId="+nodeId);
					}					
					jobMonitor.setD2dUuid(jobStatus.getServerUuid());
					logger.debug("!isForVM nodeId="+nodeId);
				}
				
				if (jobStatus.isRunningOnRPS()) {
					if (jobMonitor.getJobType() == JobType.JOBTYPE_RPS_REPLICATE || jobMonitor.getJobType() == JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND
							|| jobMonitor.getJobType() == JobType.JOBTYPE_RPS_DATA_SEEDING || jobMonitor.getJobType() == JobType.JOBTYPE_RPS_DATA_SEEDING_IN) {						
						jobMonitor.setSourceRPSId(hostId);
					} 
					runningServerId = hostId;
				} else {
					if (jobStatus.isForVM()) {
						// get the proxy node id.
						runningServerId = getD2DHostId(jobStatus.getRunningServerUuid());
					} else {
						if(jobMonitor.isLinuxNode()){
							runningServerId = getLinuxBackupServerIdByNodeId(nodeId);
						}else{
							runningServerId = nodeId;
						}
					}
				}
				
				// get target RPS ID
				if ((jobMonitor.getJobType() == JobType.JOBTYPE_RPS_REPLICATE) 
						&& !StringUtil.isEmptyOrNull(jobMonitor.getTargetRpsUUID())) {					
					int targetRPSId = 0;
					if (event.getServerUuid().equals(jobMonitor.getTargetRpsUUID())) {
						targetRPSId = hostId;
					} else {
						targetRPSId = getRpsHostId(jobMonitor.getTargetRpsUUID());
					}
					jobMonitor.setTargetRPSId(targetRPSId);
				}
				if (jobMonitor.getJobType() == JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND
						|| jobMonitor.getJobType() == JobType.JOBTYPE_RPS_DATA_SEEDING_IN) {
					jobMonitor.setTargetRPSId(hostId);
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append("RPS").append("-").append(hostId).append("-").append(runningServerId).append("-").append(nodeId)
				  .append("-").append(jobMonitor.getJobType())
				  .append("-").append(jobMonitor.getJobId());
				if (jobMonitor.getJobType() == JobType.JOBTYPE_VM_RECOVERY) {
					sb.append("-").append(jobMonitor.getJobId());
				} else if (nodeId == 0 && (jobMonitor.getJobType() == JobType.JOBTYPE_RPS_DATA_SEEDING 
						|| jobMonitor.getJobType() == JobType.JOBTYPE_RPS_DATA_SEEDING_IN || jobMonitor.getJobType() == JobType.JOBTYPE_RPS_PURGE_DATASTORE)) {
					// Since the node id for unprotected node is 0, we need append node or vm instance uuid to distinguish the key.
					sb.append("-").append(jobStatus.getJobMonitor().getJobUUID());
				}
				if(nodeId == 0){
					sb.append("-"+jobMonitor.getD2dServerName());
				}

				jobMonitor.setJobMonitorId(sb.toString());
				jobMonitor.setNodeId(nodeId);
				jobMonitor.setAgentNodeName(getHostnameById(nodeId));
				jobMonitor.setRunningServerId(runningServerId);
				jobMonitor.setRunningOnRPS(jobStatus.isRunningOnRPS());
				jobMonitor.setHistoryProductType(JobHistoryProductType.RPS.getValue());
				logger.debug("RPS source, cache key is " + sb.toString());
				D2DAllJobStatusCache.getD2DAllJobStatusCache().put(sb.toString(), jobMonitor);
				logger.debug("RPS jobMonitor - "+jobMonitor.toString());
				logger.debug("RPS jobMonitor - Name: " + jobMonitor.getD2dServerName() + "; UUID: " + jobStatus.getServerUuid() + "; VMUUID: " + jobStatus.getVmInstanceUuid()
						+ "; Key: " + jobMonitor.getJobMonitorId() + " Job Id = " +jobMonitor.getJobId() + "; isFinished: " + jobMonitor.isFinished());
				// For unprotected node, user cannot see the job monitor in node view, so we don't save another copy for node view.
				if (nodeId != 0 && (jobStatus.isRunningOnRPS() || jobMonitor.getJobStatus() == JobStatus.JOBSTATUS_WAITING)) {
					StringBuilder d2dStr = new StringBuilder();
					d2dStr.append("D2D").append("-").append(nodeId).append("-").append(runningServerId)
					.append("-").append(jobMonitor.getJobType())
					.append("-").append(jobMonitor.getJobId());
					jobMonitor.setJobMonitorId(d2dStr.toString());
					jobMonitor.setTargetRPSId(hostId);
					jobMonitor.setAgentNodeName(getHostnameById(nodeId));
					logger.debug("D2D@RPS jobMonitor - "+jobMonitor.toString());
					D2DAllJobStatusCache.getD2DAllJobStatusCache().put(d2dStr.toString(), jobMonitor);
				}
			}
		} else {
			logger.error("Invalid JobEvent source, type = " + event.getSource());
			return 1;
		}
		
		logger.debug("D2DEdgeServiceImpl.handleJobEvent(JobEvent) Leave.");
		
		return 0;
	}

	private boolean isValidJobMonitor(FlashJobMonitor jobMonitor) {
		if(jobMonitor==null){
			return false;
		}
		if (jobMonitor.getJobId() == -1) {
			logger.debug("The job id is -1 in job monitor:" + jobMonitor);
			return false;
		}
		if(jobMonitor.getStartTime()==0){
			logger.debug("The job start time is 0 in job monitor:" + jobMonitor);
			return false;
		}
		return true;
	}

	@Override
	public int handleActivityLogEvent(ActivityLogEvent event) {
		logger.debug("D2DEdgeServiceImpl.handleActivityLogEvent(ActivityLogEvent) Enter, Source = " + event.getSource());
		
		D2DActivityLog log = new D2DActivityLog();
		int result = log.save(event);
		
		logger.debug("D2DEdgeServiceImpl.handleActivityLogEvent(ActivityLogEvent) Leave, result = " + result);
		
		return result;
	}
	
	@Override
	public int handleJobHistoryEvent(JobHistoryEvent event) {
		logger.debug("D2DEdgeServiceImpl.handleJobHistoryEvent(JobHistoryEvent) Enter, Source = " 
				+ event.getSource() + " Job history is = " + event.getEventArg().getJobHistoryRecord());
		if (event.getSource()==Source.ASBU) {
			logger.info("D2DEdgeServiceImpl.handleJobHistoryEvent(JobHistoryEvent) Enter, Source = " 
					+ event.getSource() + " Job history is = " + event.getEventArg().getJobHistoryRecord());
		}
		JobHistoryHandler handler = new JobHistoryHandler();
		int result = handler.saveJobHistory(event);
		logger.debug("D2DEdgeServiceImpl.handleJobHistoryEvent(JobHistoryEvent) Leave, result = " + result); 
		return result;
	}

	@Override
	public int checkUserByUUID(String uuid) {
		EdgeWebServiceImpl impl = new EdgeWebServiceImpl();
		return impl.checkUserByUUID(uuid);
	}
	
	@Override
	public int handleDatastoreStatusChangeEvent(DatastoreStatusChangeEvent event) {
		logger.debug("D2DEdgeServiceImpl.handleDatastoreStatusChangeEvent(DatastoreStatusChangeEvent) Enter, Source = " + event.getSource());
		
		int result = 0;
		
		for (DatastoreStatusChangeEventArg eventArg : event.getChangedStatusArgs()) {
			try {
				String key="datastore-"+eventArg.getDatastoreUuid();
				logger.info("receive "+key);
				if(RPSDataStoreUtil.DataStoreManagementService.equals(eventArg.getDatastoreUuid())){
					String nodeUuid=eventArg.getServerUuid();
					int nodeid=getRpsNodeByUuid(nodeUuid, 4);
					if(nodeid==0){
						logger.info("nodeUuid error:"+nodeUuid);
						continue;
					}
					List<DataStoreSettingInfo> ds=getDatastoreByNode(nodeid);
					if(ds==null)
						continue;
					for(DataStoreSettingInfo d:ds){
						StatusUtil.setDatastoreStatus(event.getServerUuid(), d.getDatastore_name(), eventArg.getState(), eventArg.getFaultBean());
						StatusUtil.setDatastoreSummary(event.getServerUuid(), d.getDatastore_name(), eventArg.getDataStoreStatus() );
					}
				}else{
					StatusUtil.setDatastoreStatus(event.getServerUuid(), eventArg.getDatastoreUuid(), eventArg.getState(), eventArg.getFaultBean());
					StatusUtil.setDatastoreSummary(event.getServerUuid(), eventArg.getDatastoreUuid(), getStatusFromEvent(eventArg));
				}
			} catch (EdgeServiceFault e) {
				logger.error("D2DEdgeServiceImpl.handleDatastoreStatusChangeEvent(DatastoreStatusChangeEvent) - notify changed status failed.", e);
				result = 1;
			}
		}
		
		return result;
	}
	
	private DataStoreStatus getStatusFromEvent(DatastoreStatusChangeEventArg event){
		if(event.getDataStoreStatus()!=null)
			return event.getDataStoreStatus();
		DataStoreRunningState state = event.getState();
		EdgeServiceFaultBean fault = event.getFaultBean();
		DataStoreStatus dss = event.getDataStoreStatus();
		String uuid=event.getServerUuid()+"_"+ event.getDatastoreUuid();
		if( state !=null ) {
			if( dss ==null ) {
				logger.debug( "DataStoreStatus " +uuid +" don't have status info! with running status = "+ state.getValue() );
				dss = new DataStoreStatus();
			}
			dss.setOverallStatus( state.getValue() );
			dss.setElapsedTimeInSec(event.getElapsedTimeInSec());
			dss.setEstRemainTimeInSec(event.getEstRemainTimeInSec());
			if( fault!=null &&  fault.getCode() != null ) {
				long errorCode =0;
				try {
					errorCode= Long.parseLong( fault.getCode() );
				}
				catch( Exception e ){ 
					logger.error("DataStoreStatus " +uuid +" failed to parse status error code:" +fault.getCode() );
				}
				dss.setStatusErrorCode( errorCode );
				dss.setStatusErrorMessage( fault.getMessage()!=null ? fault.getMessage(): "" );
			}
			//debug
			logger.debug("DataStoreStatus " +uuid +" volume total size: " + 
					 ( dss.getCommonStoreStatus()!=null ? dss.getCommonStoreStatus().getDataVolumeTotalSize() :"" ) ) ;
		}
		else {  //state == null! it means we cannot get state from cache; it's a special status; only meaningful in cpm; and we assume in this case, datastorestatus must also be null
			if( dss ==null ) {
				logger.debug( "DataStoreStatus " +uuid +" don't have status info! with running status also not exist!" );
				dss = new DataStoreStatus();
			}
			dss.setOverallStatus( DataStoreRunningState.NOTGET.getValue() );
		}
		return dss;
	}

	private int getRpsNodeByUuid(String nodeUuid, int protectiontype ) {
		int[] hostId=new int[1];
		hostMgrDao.as_edge_host_getHostIdByUuid(nodeUuid, protectiontype,hostId);
		return hostId[0];
	}

	private List<DataStoreSettingInfo> getDatastoreByNode(int nodeid) {
		return DataStoreManager.getInstance().getDataStoreByNodeId(nodeid);
	}

	public IEdgeVSBDao getVSBDao() {
		if (vsbDao == null) {
			vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
		}
		return vsbDao;
	}
	
	public IRpsDataStoreDao getRpsDataStoreDao() {
		if (dsDao == null) {
			dsDao = DaoFactory.getDao(IRpsDataStoreDao.class);
		}
		return dsDao;
	}
	
	@Override
	public int handleDataSyncEvent(DataSyncEvent event) {
		logger.debug("D2DEdgeServiceImpl.handleDataSyncEvent(DataSyncEvent) Enter, Source = " 
				+ event.getSource() + " Event is = " + event.getEventType());
		IDataSyncEventHandler handler = DataSyncEventHandlerFactory.getDataSyncEventHandler(event.getEventType());
		int result = 0;
		if(handler != null){
			result = handler.saveDataRecord(event);
			logger.debug("D2DEdgeServiceImpl.handleDataSyncEvent(DataSyncEvent) the eventhandler is null."); 
		}
		logger.debug("D2DEdgeServiceImpl.handleDataSyncEvent(DataSyncEvent) Leave, result = " + result); 
		return result;
	}

	@Override
	public boolean isConverterSameForNode(String converterUuid, String nodeUuid) throws EdgeServiceFault {
		try {
			if (StringUtil.isEmptyOrNull(converterUuid) || StringUtil.isEmptyOrNull(nodeUuid)) {
				logger.error("The Converter uuid=" + converterUuid + " nodeUuid=" + nodeUuid + " one of them is empty.");
				return false;
			}
			int hostId = getHostId(nodeUuid);
			if (hostId > 0) {
				List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
				getVSBDao().as_edge_vsb_converter_getByHostId(hostId, converterList);
				if (converterList.size() > 0) {
					for (EdgeVCMConnectInfo converter : converterList) {
						if (converterUuid.equalsIgnoreCase(converter.getUuid())) {
							return true;
						}
					}
					logger.error("Failed to get converter with uuid:" + converterUuid);
				}
			} else {
				logger.error("Failed to get node with uuid:" + nodeUuid);
			}
			return false;
		} catch (Exception e) {
			logger.error("Failed to check whether converter is same for node.", e);
			// We will return true, so the vsb task won't be removed
			return true;
		}
	}

	@Override
	public D2DStatus checkD2DStatus(D2DInfo d2dInfo) throws EdgeServiceFault {
		if (d2dInfo == null || d2dInfo.getType() == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid parameters, D2D info is null or D2D type is null.");
		}
		
		if (StringUtil.isEmptyOrNull(d2dInfo.getNodeUuid())) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid parameters: node UUID is null or empty.");
		}
		
//		if (StringUtil.isEmptyOrNull(d2dInfo.getPlanUuid())) {
//			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid parameters: plan UUID is null or empty.");
//		}

		if (D2DType.WindowsProxy==d2dInfo.getType() && StringUtil.isEmptyOrNull(d2dInfo.getInstanceUuid())) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid parameters: instance UUID is null or empty.");
		}
		
		switch (d2dInfo.getType()) {
		case WindowsD2D: return checkWindowsD2DStatus(d2dInfo);
		case WindowsProxy: return checkWindowsProxyStatus(d2dInfo);
		case VSB: return checkVSBStatus(d2dInfo);
		case RPS: return checkRPSStatus(d2dInfo);
		default:
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid D2D type: " + d2dInfo.getType());
		}
	}
	
	private D2DStatus checkPlanAssignment(int hostId, String planUuid) {
		
		logger.debug( "checkPlanAssignment(): enter" );
		logger.debug( "checkPlanAssignment(): hostId = " + hostId + ", planUuid = '" + planUuid + "'" );
		
		List<EdgeHostPolicyMap> policyMaps = new ArrayList<EdgeHostPolicyMap>();
		policyDao.getHostPolicyMap(hostId, PolicyTypes.Unified, policyMaps);
		if (policyMaps.isEmpty()) {
			logger.debug( "checkPlanAssignment(): No policy map, returns " + D2DStatus.NoPolicy );
			return D2DStatus.NoPolicy;
		}
		
		List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
		policyDao.as_edge_policy_list(policyMaps.get(0).getPolicyId(), 0, policies);
		if (policies.isEmpty()) {
			logger.debug( "checkPlanAssignment(): Cannot find policy, returns " + D2DStatus.NoPolicy );
			return D2DStatus.NoPolicy;
		}
		
		if (!policies.get(0).getUuid().equalsIgnoreCase(planUuid)) {
			logger.debug( "checkPlanAssignment(): Returns " + D2DStatus.PolicyChanged );
			return D2DStatus.PolicyChanged;
		}
		
		logger.debug( "checkPlanAssignment(): Returns " + D2DStatus.Ok );
		return D2DStatus.Ok;
	}
	
	private void writeActivityLog( Severity severity, int nodeid, String message )
	{
		try{
			LogAddEntity log = new LogAddEntity();
			log.setJobId( 0 );
			log.setTargetHostId( nodeid );
			log.setSeverity( severity );
			log.setMessage( message );
			this.getActivityLogService().addUnifiedLog( log );
		}
		catch (Exception e)
		{
			logger.error( "writeActivityLog(): Error writting activity log. (Node id: '" +
				nodeid + "', Message: '" + message + "')", e );
		}
	}
	
	private IActivityLogService getActivityLogService()
	{
		return this.activityLogService;
	}
	
	private D2DStatus checkWindowsD2DStatus(D2DInfo d2dInfo) {
		
		logger.debug( "checkWindowsD2DStatus(): enter" );
		//logger.info( "checkWindowsD2DStatus(): d2dInfo.getNodeUuid() = '" + d2dInfo.getNodeUuid() + "'" );
		//logger.info( "checkWindowsD2DStatus(): encrypted d2dInfo.getNodeUuid() = '" + DaoFactory.getEncrypt().encryptString( d2dInfo.getNodeUuid() ) + "'" );
		//logger.info( "checkWindowsD2DStatus(): d2dInfo.getPlanUuid() = '" + d2dInfo.getPlanUuid() + "'" );
		
		List<EdgeD2DHost> hostList = new ArrayList<EdgeD2DHost>();
		
		hostMgrDao.getHostByUUID( d2dInfo.getNodeUuid(), hostList );
		if (hostList.size() == 0)
		{
			logger.debug( "checkWindowsD2DStatus(): Returns " + D2DStatus.NodeDeleted );
			return D2DStatus.NodeDeleted;
		}
		
		if (hostList.size() > 1) // multiple node for the same uuid? write activity log
		{
			logger.warn( "checkWindowsD2DStatus(): Duplicated nodes detected." );
			
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (EdgeD2DHost host : hostList)
			{
				if (i > 0) sb.append( ", " );
				sb.append( host.getRhostname() );
				i ++;
			}
			
			String logMessage = EdgeCMWebServiceMessages.getMessage(
				"duplicatedNodesDetected", sb.toString() );
			logger.warn( "checkWindowsD2DStatus(): " + logMessage );
			writeActivityLog( Severity.Warning, hostList.get( 0 ).getRhostid(),
				logMessage );
		}
		
		// Get host ID via UUID and order them by the last update time of their
		// Host-Policy map in descending order. If there are multiple records
		// for one node, the 1st one must be the one the customer operated
		// most recently.
		
		hostList.clear();
		policyDao.getHostByUUIDPerPlanUsage( d2dInfo.getNodeUuid(), hostList );
		if (hostList.size() == 0)
		{
			logger.debug( "checkWindowsD2DStatus(): Returns " + D2DStatus.NoPolicy );
			return D2DStatus.NoPolicy;
		}
		
		int hostId = hostList.get( 0 ).getRhostid();
		logger.debug( "checkWindowsD2DStatus(): hostId = " + hostId );
		
		D2DStatus result = checkPlanAssignment( hostId, d2dInfo.getPlanUuid() );
		if (result != D2DStatus.Ok)
		{
			logger.debug( "checkWindowsD2DStatus(): Returns " + result );
			return result;
		}
		
		logger.debug( "checkWindowsD2DStatus(): Returns " + D2DStatus.Ok );
		return D2DStatus.Ok;
	}
	
	private D2DStatus checkWindowsProxyStatus(D2DInfo d2dInfo) {
		int hostId = getD2DHostId(d2dInfo.getNodeUuid());
		if (hostId == 0) {
			return D2DStatus.NodeDeleted;
		}
		
		List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
		policyDao.as_edge_policy_list_byProxyHostId(hostId, policies);
		if (policies.isEmpty()) {
			return D2DStatus.NotWindowsProxy;
		}
		
		int vmHostId = getVmHostId(d2dInfo.getInstanceUuid());
		if (vmHostId == 0) {
			return D2DStatus.InstanceDeleted;
		}
		
		D2DStatus result = checkPlanAssignment(vmHostId, d2dInfo.getPlanUuid());
		if (result != D2DStatus.Ok) {
			return result;
		}
		
		return D2DStatus.Ok;
	}
	
	/**
	 * We will just check whether the converter is same or not, converter will use old method to check plan status
	 * 
	 * @param d2dInfo
	 *            The information of converter and node and plan
	 * @return The check result, if converter is not same, then return VSBConverterChanged, otherwise return Ok
	 * @throws EdgeServiceFault
	 *             Failed to check the vsb status
	 */
	private D2DStatus checkVSBStatus(D2DInfo d2dInfo) throws EdgeServiceFault {
		// int hostId = getHostId(d2dInfo.getNodeUuid());
		// if (hostId == 0) {
		// return D2DStatus.NodeDeleted;
		// }

		// D2DStatus result = checkPlanAssignment(hostId, d2dInfo.getPlanUuid());
		// if (result != D2DStatus.Ok) {
		// return result;
		// }

		if (!isConverterSameForNode(d2dInfo.getConverterUuid(), d2dInfo.getNodeUuid())) {
			return D2DStatus.VSBConverterChanged;
		}

		return D2DStatus.Ok;
	}
	
	private D2DStatus checkRPSStatus(D2DInfo d2dInfo) throws EdgeServiceFault {
		int hostId = getRpsHostId(d2dInfo.getNodeUuid());
		if (hostId == 0) {
			return D2DStatus.NodeDeleted;
		}
		
		List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
		policyDao.as_edge_policy_list_by_uuid(d2dInfo.getPlanUuid(), policies);
		if (policies.isEmpty()) {
			return D2DStatus.NoPolicy;
		}
		
		RPSPolicy matchedRpsPolicy = null;
		UnifiedPolicy plan = PolicyManagementServiceImpl.getInstance().getUnifiedPolicyById(policies.get(0).getId());
		for (RPSPolicyWrapper rpsPolicy : plan.getRpsPolices()) {
			if (d2dInfo.getInstanceUuid().equalsIgnoreCase(rpsPolicy.getRpsPolicy().getId())) {
				matchedRpsPolicy = rpsPolicy.getRpsPolicy();
				break;
			}
		}
		
		if (matchedRpsPolicy == null) {
			return D2DStatus.NoPolicy;
		}
		
		return D2DStatus.Ok;
	}

	@Override
	public List<String> validateProtectedResource(String serverUUID,
			String policyUUID, String policyGlobalUUID, String archiveUUID)
			throws EdgeServiceFault {
		return PolicyManagementServiceImpl.getInstance().getProtectedResourceUuids(serverUUID, policyUUID, policyGlobalUUID, archiveUUID);
	}

	@Override
	public List<RpsArchiveConfiguationWrapper> getRpsArchiveConfiguationSummary(
			String planUUID) {
		return PolicyManagementServiceImpl.getInstance().getRpsArchiveConfiguationSummary(planUUID);
	}
	
}
