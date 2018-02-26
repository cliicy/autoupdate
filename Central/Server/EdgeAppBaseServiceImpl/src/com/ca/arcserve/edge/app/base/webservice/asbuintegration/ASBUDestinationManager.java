package com.ca.arcserve.edge.app.base.webservice.asbuintegration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.RPSDataStoreSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcflash.rps.webservice.data.policy.RpsEmailAlertSettings;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcserve.edge.app.asbu.dao.IASBUDao;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUAuthenticationType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ArchiveToTapeAdvance;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ArchiveToTapeDestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ArchiveToTapeSettingsWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.BeforeAfterJobOption;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.EjectMediaOption;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.JobLogOption;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.JobVerifiicationOption;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.asbu.webservice.IArchiveToTapeService;
import com.ca.asbu.webservice.data.archive2tape.cfg.ArchiveSchedule;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJob;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobAdvanceOption;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobDestinationConfig;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobEncryCompConfig;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobPrePostJobCmdConfig;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobSaveJobOption;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobScheduleConfig;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobScheduleDownStream;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobSourceConfig;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveJobVerification;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveSourceAgentInfo;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveSourceAgentStandalone;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveSourceAgentToRPS;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveSourceHBBUToAgentProxy;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveSourceHBBUToRPS;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveSourceLinuxToRPS;
import com.ca.asbu.webservice.data.archive2tape.job.ArchiveUDPSchedule;
import com.ca.asbu.webservice.data.archive2tape.job.EmailSetting;
import com.ca.asbu.webservice.data.archive2tape.job.NodeInfo;
import com.ca.asbu.webservice.data.archive2tape.job.RPSInfo;
import com.ca.asbu.webservice.data.archive2tape.job.VMInfo;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumArchiveJobMethod;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumArchiveJobPrePostJobOption;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumArchiveJobScheduleMethod;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumArchiveJobStatus;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumArchiveJobTimeUnit;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumArchiveJobVerificationOption;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumArchiveMediaUsageMode;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumArchiveSourceType;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumEjectMediaOnCompletion;
import com.ca.asbu.webservice.data.archive2tape.job.constant.EnumJobLogOption;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

public class ASBUDestinationManager {
	private static final Logger logger = Logger
			.getLogger(ASBUDestinationManager.class);
	private static ASBUDestinationManager instance = new ASBUDestinationManager();
	private IASBUDao asbuDao = DaoFactory.getDao(IASBUDao.class);
	private INodeService nodeService = new NodeServiceImpl();
	private IActivityLogService activityLogService = new ActivityLogServiceImpl();
	private IConnectionFactory connectionFactory = EdgeFactory
			.getBean(IConnectionFactory.class);

	private ASBUDestinationManager() {
	}

	public static ASBUDestinationManager getInstance() {
		return instance;
	}

	public void bindConnectInfo(EdgeHost host, ArcserveConnectInfo connectInfo) {
		connectInfo.setProtocol(Protocol.parse(host.getD2dProtocol()));
		connectInfo.setPort(Integer.valueOf(host.getD2dPort()));
		connectInfo.setCauser(host.getUsername());
		connectInfo.setCapasswd(host.getPassword());
		connectInfo
				.setAuthmode(ASBUAuthenticationType.WINDOWS.getValue() == host
						.getAuthMode() ? ABFuncAuthMode.WINDOWS
						: ABFuncAuthMode.AR_CSERVE);
	}

	public void bindConnectInfo(ConnectionContext context,
			ArcserveConnectInfo connectInfo) {
		connectInfo.setProtocol(Protocol.parse(Integer.valueOf(context
				.getProtocol())));
		connectInfo.setPort(Integer.valueOf(context.getPort()));
		connectInfo.setCauser(context.getUsername());
		connectInfo.setCapasswd(context.getPassword());
		connectInfo
				.setAuthmode(ASBUAuthenticationType.WINDOWS.getValue() == context
						.getAuthenticationType() ? ABFuncAuthMode.WINDOWS
						: ABFuncAuthMode.AR_CSERVE);
	}

	public EdgeHost getServer(int serverId) {
		List<ConnectionContext> context = new ArrayList<>();
		asbuDao.findConnectionInfoByHostId(serverId, context);
		if (CollectionUtils.isNotEmpty(context)) {
			List<EdgeHost> servers = new ArrayList<>();
			asbuDao.getASBUServer(serverId, servers);
			if (servers != null && servers.size() == 1) {
				return servers.get(0);
			}
		}
		logger.error("there are duplicated server under the same domain.");
		return null;
	}

	public void deleteASBUSettings(UnifiedPolicy policy)
			throws EdgeServiceFault {
		if (policy.getArchiveToTapeSettingsWrapperList() == null || policy.getArchiveToTapeSettingsWrapperList().isEmpty())
			return;

		Map<Integer, List<String>> archiveToTapeTaskIdListMap = new HashMap<Integer, List<String>>();
		for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : policy.getArchiveToTapeSettingsWrapperList()){
			int serverId = archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId();
			List<String> archiveToTapeTaskIdList = new ArrayList<String>();
			if(archiveToTapeTaskIdListMap.containsKey(serverId)){
				archiveToTapeTaskIdList = archiveToTapeTaskIdListMap.get(serverId);
			}
			String archiveToTapeTaskId = archiveToTapeWrapper.getArchiveToTapeUUID();
			if(archiveToTapeTaskId != null && !archiveToTapeTaskId.equals("")){
				archiveToTapeTaskIdList.add(archiveToTapeTaskId);
				archiveToTapeTaskIdListMap.put(serverId, archiveToTapeTaskIdList);
			}
		}
		doDeleteASBUSettings(archiveToTapeTaskIdListMap);
	}
	
	//  used by update plan
	public void deleteASBUSettings(Map<String, Integer> archiveUUIDMap)
			throws EdgeServiceFault {
		if (archiveUUIDMap == null || archiveUUIDMap.isEmpty())
			return;

		Map<Integer, List<String>> archiveToTapeTaskIdListMap = new HashMap<Integer, List<String>>();
		for(Entry<String, Integer> archiveUUIDEntry : archiveUUIDMap.entrySet()){
			List<String> archiveToTapeTaskIdList = new ArrayList<String>();
			if(archiveToTapeTaskIdListMap.containsKey(archiveUUIDEntry.getValue())){
				archiveToTapeTaskIdList = archiveToTapeTaskIdListMap.get(archiveUUIDEntry.getValue());
			}
			String archiveToTapeTaskId = archiveUUIDEntry.getKey();
			if(archiveToTapeTaskId != null && !archiveToTapeTaskId.equals("")){
				archiveToTapeTaskIdList.add(archiveToTapeTaskId);
				archiveToTapeTaskIdListMap.put(archiveUUIDEntry.getValue(), archiveToTapeTaskIdList);
			}
		}
		doDeleteASBUSettings(archiveToTapeTaskIdListMap);
	}
	
	private void doDeleteASBUSettings(Map<Integer, List<String>> archiveToTapeTaskIdListMap)
			throws EdgeServiceFault {
		for(Entry<Integer, List<String>> archiveToTapeTaskIdEntry : archiveToTapeTaskIdListMap.entrySet()){
			int serverId = archiveToTapeTaskIdEntry.getKey();
			String message = EdgeCMWebServiceMessages.getMessage( "asbu_setting_delete_successfully" );
			Severity severity= Severity.Information;
			try{
				EdgeHost host = getServer(serverId);
				if (host != null) {
					ArcserveConnectInfo connectInfo = new ArcserveConnectInfo();
					bindConnectInfo(host, connectInfo);
					IArchiveToTapeService archiveToTapeService = null;
					try (com.ca.arcserve.edge.app.base.common.connection.ASBUConnection connection = connectionFactory
							.createASBUConnection(host.getRhostid())) {
						archiveToTapeService = initArchiveToTapeService(connection);
						archiveToTapeService.deleteArchiveJob(archiveToTapeTaskIdEntry.getValue());
					}
					logger.info("ASBUDestinationManager.deleteASBUSettings -  deleting ASBU policy successful, archiveToTapeTaskId"+
							archiveToTapeTaskIdEntry.getValue().toString());
				}
				
			}catch(Exception ex){
				message = ex.getMessage();
				if(message != null && !message.equals("") && message.indexOf("@") > 0){//contain @, this is Business exception from asbu. need to write activity log.
					message = message.substring(0, message.lastIndexOf("@"));
					message = EdgeCMWebServiceMessages.getMessage( "asbu_setting_delete_failed" )+ " " + message;
				}else{
					message = null; // other msgs, not be converted, if write activity log, uses will not understand them.
				}
				logger.warn("ASBUDestinationManager.deleteASBUSettings - ignore deleting ASBU policy failure, archiveToTapeTaskId"+
						archiveToTapeTaskIdEntry.getValue().toString() +", error message = " + ex.getMessage());			
				severity = Severity.Error;
			}
			if(message != null){
				createASBUActivityLog(serverId, message, severity);
			}
		}
	}

	private void createASBUActivityLog(int hostId, String message, Severity severity) throws EdgeServiceFault{
		try
		{
			ActivityLog log = new ActivityLog();
			log.setModule(Module.ASBUConnectServer);
			log.setHostId(hostId);
			log.setMessage(message);
			log.setSeverity(severity);
			log.setTime(new Date());
			activityLogService.addLog( log );
		}
		catch (Exception e)
		{
			logger.error( "writeActivityLog(): Error writting activity log. (Message: '" + message + "')", e );
		}
	}
	
	public void doASBUSettingsDeployment(UnifiedPolicy policy)
			throws EdgeServiceFault {
		if(policy.getArchiveToTapeSettingsWrapperList() == null)
			return;
		Map<Integer, List<ArchiveJob>> archiveJobListMap = new HashMap<Integer, List<ArchiveJob>>();
		for(int i = policy.getArchiveToTapeSettingsWrapperList().size() - 1; i >= 0; --i){
			ArchiveToTapeSettingsWrapper archiveToTapeWrapper= policy.getArchiveToTapeSettingsWrapperList().get(i);
			int serverId = archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId();
			ArchiveJob jobScript = convertSettingtoJobScript(policy, archiveToTapeWrapper);
			List<ArchiveJob> jobScriptList = new ArrayList<ArchiveJob>();
			if(archiveJobListMap.containsKey(serverId)){
				jobScriptList = archiveJobListMap.get(serverId);
			}
			jobScriptList.add(jobScript);
			archiveJobListMap.put(serverId, jobScriptList);
		}
		for(Entry<Integer, List<ArchiveJob>> archiveJobEntry : archiveJobListMap.entrySet()){
			int serverId = archiveJobEntry.getKey();
			try{
				EdgeHost host = getServer(serverId);
				if (host != null) {
					ArcserveConnectInfo connectInfo = new ArcserveConnectInfo();
					bindConnectInfo(host, connectInfo);
					IArchiveToTapeService archiveToTapeService = null;
					try (com.ca.arcserve.edge.app.base.common.connection.ASBUConnection connection = connectionFactory
							.createASBUConnection(host.getRhostid())) {
						archiveToTapeService = initArchiveToTapeService(connection);
						archiveToTapeService.submitArchiveJob(archiveJobEntry.getValue());
					}
					logger.info("ASBUDestinationManager.doASBUSettingsDeployment -  submiting ASBU policy successful");
				}
			}catch(Exception ex){
				logger.error("ASBUDestinationManager.doASBUSettingsDeployment - submiting ASBU policy failure, error message = " + ex.getMessage());
				if(ex instanceof SOAPFaultException){
					String message = ex.getMessage();
					if(message != null && !message.equals("") && message.indexOf("@") > 0){// contain @, this is Business exception from asbu. need to write activity log.
						message = message.substring(0, message.lastIndexOf("@"));
						createASBUActivityLog(serverId, EdgeCMWebServiceMessages.getMessage( "asbu_submit_setting__failed" ) + " " + message, Severity.Error);
					}
					SOAPFaultException se = (SOAPFaultException) ex;
					EdgeServiceFaultBean efb = new EdgeServiceFaultBean(se.getFault().getFaultCode(), message, FaultType.ASBU);// convert to edge fault of asbu
					throw new EdgeServiceFault(message, efb);
				}
				if(ex instanceof WebServiceException){
					EdgeServiceFaultBean efb = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_CantConnect_ASBUServiceError, ex.getMessage(), FaultType.ASBU);// convert to edge fault of asbu
					throw new EdgeServiceFault(ex.getMessage(), efb);
				}
				throw ex;// WebServiceExceptionHandler will convert it
			}
			createASBUActivityLog(serverId, EdgeCMWebServiceMessages.getMessage( "asbu_submit_setting_successfully" ), Severity.Information);	
		}
	}

	public IArchiveToTapeService initArchiveToTapeService(
			com.ca.arcserve.edge.app.base.common.connection.ASBUConnection connection)
			throws EdgeServiceFault {
		try {
			connection.connect();
			return connection.getService();
			// IArchiveToTapeService service =
			// mock(IArchiveToTapeService.class);
			// when(service.getServerList()).thenReturn(mockASBUServerList());
			// return service;
		} catch (ServerSOAPFaultException e) {
			throw e;
		} catch (SOAPFaultException ex) {
			logger.error(ex);
			EdgeServiceFaultBean efb = new EdgeServiceFaultBean(ex.getFault().getFaultCode(), ex.getMessage(), FaultType.ASBU);
			throw new EdgeServiceFault(ex.getMessage(), efb);
		}catch (WebServiceException ee) {
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.ASBU_PortOrHostNameError, "", FaultType.ASBU);
			bean.setMessageParameters(new String[]{connection.getContextProvider().create().getHost() + ":" + connection.getContextProvider().create().getPort()});
			logger.error(ee);
			throw new EdgeServiceFault(ee.getMessage(), bean);
		}
	}

	public int parseAuthenticationType(ABFuncAuthMode authMode) {
		return authMode == ABFuncAuthMode.WINDOWS ? ASBUAuthenticationType.WINDOWS
				.getValue() : ASBUAuthenticationType.ARCSERVE_BACKUP.getValue();
	}

	public ArchiveJob convertSettingtoJobScript(UnifiedPolicy policy, ArchiveToTapeSettingsWrapper archiveToTapeWrapper)
			throws EdgeServiceFault {

		// init ASBU model
		ArchiveJob jobScript = new ArchiveJob();
		ArchiveJobSourceConfig sourceConfig = new ArchiveJobSourceConfig();
		jobScript.setJobSource(sourceConfig);

		String planGlobalUUID = archiveToTapeWrapper.getArchiveToTapeSettings().getPlanGlobalUUID();
		String sourceTaskId = archiveToTapeWrapper.getArchiveToTapeSettings().getSource().getTaskId();
		String archiveToTapeTaskId = archiveToTapeWrapper.getArchiveToTapeUUID();// add by Neo
		com.ca.asbu.webservice.data.archive2tape.cfg.ArchiveConfig conf = new com.ca.asbu.webservice.data.archive2tape.cfg.ArchiveConfig();
		conf.setPlanVersionUUID(policy.getUuid());
		//conf.setPlanGlobalUUID(policy.getArchiveToTapeSettings().getPlanGlobalUUID());
		conf.setPlanGlobalUUID(planGlobalUUID);
		conf.setDisabled(!policy.isEnable());
		conf.setArchiveToTapeTaskId(archiveToTapeTaskId);
		// set archive job schedule
		conf.setSchedule(getArchiveSchedule(policy, archiveToTapeWrapper));

		if (policy.getBackupConfiguration() != null
				&& policy.getLinuxBackupsetting() == null) {

			setArchiveJobSourceConfigForWindowAgent(sourceConfig, policy, conf, sourceTaskId);

		} else if (policy.getVSphereBackupConfiguration() != null) {

			setArchiveJobSourceConfigForHostBasedVM(sourceConfig, policy, conf, sourceTaskId);

		} else if (policy.getBackupConfiguration() != null
				&& policy.getLinuxBackupsetting() != null) {

			setArchiveJobSourceConfigForLinuxAgent(sourceConfig, policy, conf, sourceTaskId);

		}

		if (logger.isDebugEnabled()) {
			logger.debug("plan version uuid is " + conf.getPlanVersionUUID());
			logger.debug("plan global uuid is " + conf.getPlanGlobalUUID());
			logger.debug("policy id is " + conf.getPolicyId());
		}

		// end add new code

		jobScript.setArchiveConfig(conf);
		// set archive job destination
		jobScript.setJobDestination(getArchiveJobDestinationConfig(policy, archiveToTapeWrapper));

		// set archive schedule
		jobScript.setJobschedule(getArchiveJobScheduleConfig(policy, archiveToTapeWrapper));

		// set planUUID
		jobScript.setPlanVersionUUID(policy.getUuid());
		
		//jobScript.setPlanGlobalUUID(policy.getArchiveToTapeSettings().getPlanGlobalUUID());
		jobScript.setPlanGlobalUUID(planGlobalUUID);

		// set plan name
		jobScript.setPlanName(policy.getName());

		// set Job init Status
		jobScript.setJobInitStatus(EnumArchiveJobStatus.UDP_ARCHIVE_JOB_STATUS_HOLD);

		jobScript.setJobAdvanceOption(getArchiveJobAdvanceOption(policy, archiveToTapeWrapper));

		jobScript.setArchiveToTapeTaskId(archiveToTapeTaskId);
		return jobScript;
	}

	private List<Node> getNodeList(UnifiedPolicy policy)
			throws EdgeServiceFault {

		List<ProtectedResourceIdentifier> identifiers = policy
				.getProtectedResources();

		if (CollectionUtils.isEmpty(identifiers)) {
			logger.info("No nodes are added into plan!");
			return new ArrayList<Node>(0);
		}

		List<Integer> idList = new ArrayList<Integer>();
		for (ProtectedResourceIdentifier identifier : identifiers) {
			idList.add(identifier.getId());
		}

		List<Node> nodes = nodeService.getNodeListByIDs(idList);

		return nodes;
	}

	private void setArchiveJobSourceConfigForLinuxAgent(
			ArchiveJobSourceConfig sourceConfig, UnifiedPolicy policy,
			com.ca.asbu.webservice.data.archive2tape.cfg.ArchiveConfig conf, String sourceTaskID)
			throws EdgeServiceFault {

		// linux backup to RPS
		// bind node info

		List<ArchiveSourceAgentInfo> agentList = new ArrayList<ArchiveSourceAgentInfo>();

		List<Node> nodes = getNodeList(policy);
		if (CollectionUtils.isNotEmpty(nodes)) {
			for (Node node : nodes) {
				agentList.add(bindLinuxAgentInfo(node));
			}
		}

		ArchiveSourceLinuxToRPS sourceLinuxToRPS = new ArchiveSourceLinuxToRPS();
		sourceLinuxToRPS.setAgentList(agentList);

		if (CollectionUtils.isNotEmpty(policy.getRpsPolices())) {

			int rpsPolicyCount = policy.getRpsPolices().size();
			for (int i = rpsPolicyCount - 1; i >= 0; i--) {
				if (StringUtil.isNotEmpty(sourceTaskID)) {
					RPSPolicyWrapper rpsPolicy = policy.getRpsPolices().get(i);
					if (sourceTaskID.equals(rpsPolicy.getTaskId())) {
						conf.setPolicyId(rpsPolicy.getRpsPolicy().getId());

						RPSInfo achiveRPSInfo = null;
						if (i == 0) {
							// archive source is backup destination
							achiveRPSInfo = getRPSInfo(policy);
						} else {
							// archive source is replication destination
							achiveRPSInfo = getRPSInfo(policy.getRpsPolices()
									.get(i - 1).getRpsPolicy());
							RPSDataStoreSettings dsSettings = rpsPolicy.getRpsPolicy().getRpsSettings()
							        .getRpsDataStoreSettings();
							achiveRPSInfo.setDataStoreId(dsSettings.getDataStoreName());
						}

						sourceConfig
								.setSourceType(EnumArchiveSourceType.SOURCETYPE_LINUX_TO_RPS);
						sourceLinuxToRPS.setRpsInfo(achiveRPSInfo);
						sourceConfig.setSourceLinuxToRPS(sourceLinuxToRPS);

					}
				}
			}
		}

	}

	private void setArchiveJobSourceConfigForHostBasedVM(
			ArchiveJobSourceConfig sourceConfig, UnifiedPolicy policy,
			com.ca.asbu.webservice.data.archive2tape.cfg.ArchiveConfig conf, String sourceTaskId)
			throws EdgeServiceFault {

		List<VMInfo> vmInfoList = new ArrayList<VMInfo>();

		// bind node info
		List<Node> nodes = getNodeList(policy);
		if (CollectionUtils.isNotEmpty(nodes)) {
			for (Node node : nodes) {
				VMInfo vmInfo = bindVMInfo(node);
				vmInfoList.add(vmInfo);
			}
		}

		if (CollectionUtils.isEmpty(policy.getRpsPolices())) {
			// backup to share folder
			sourceConfig.setSourceType(EnumArchiveSourceType.SOURCETYPE_HBBU_BACKUP_TO_D2D_PROXY);
			ArchiveSourceHBBUToAgentProxy hbbuToAgentProxy = bindHBBUProxyInfo(policy);
			hbbuToAgentProxy.setVmList(vmInfoList);
			sourceConfig.setSourceHBBUToAgentProxy(hbbuToAgentProxy);

		} else {
			// backup to RPS
			
			ArchiveSourceHBBUToRPS hbbuToRPS = new ArchiveSourceHBBUToRPS();
			hbbuToRPS.setVmList(vmInfoList);
			
			int rpsPolicyCount = policy.getRpsPolices().size();
			for (int i = rpsPolicyCount - 1; i >= 0; i--) {
				if (StringUtil.isNotEmpty(sourceTaskId)) {
					RPSPolicyWrapper rpsPolicy = policy.getRpsPolices().get(i);
					if (sourceTaskId.equals(rpsPolicy.getTaskId())) {
						conf.setPolicyId(rpsPolicy.getRpsPolicy().getId());

						RPSInfo achiveRPSInfo = null;
						if (i == 0) {
							// archive source is backup destination
							achiveRPSInfo = getHBBURPSInfo(policy);
						} else {
							// archive source is replication destination
							achiveRPSInfo = getRPSInfo(policy.getRpsPolices()
									.get(i - 1).getRpsPolicy());
							RPSDataStoreSettings dsSettings = rpsPolicy.getRpsPolicy().getRpsSettings()
							        .getRpsDataStoreSettings();
							achiveRPSInfo.setDataStoreId(dsSettings.getDataStoreName());
						}

						sourceConfig
								.setSourceType(EnumArchiveSourceType.SOURCETYPE_HBBU_BACKUP_TO_RPS);
						hbbuToRPS.setRpsInfo(achiveRPSInfo);
						sourceConfig.setSourceHBBUToRPS(hbbuToRPS);

					}
				}
			}

		}

	}

	private void setArchiveJobSourceConfigForWindowAgent(
			ArchiveJobSourceConfig sourceConfig, UnifiedPolicy policy,
			com.ca.asbu.webservice.data.archive2tape.cfg.ArchiveConfig conf, String sourceTaskId)
			throws EdgeServiceFault {

		// this is for windows agent case

		List<ArchiveSourceAgentInfo> agentList = new ArrayList<ArchiveSourceAgentInfo>();

		List<Node> nodes = getNodeList(policy);
		if (CollectionUtils.isNotEmpty(nodes)) {
			for (Node node : nodes) {
				agentList.add(bindAgentInfo(node));
			}
		}

		ArchiveSourceAgentToRPS sourceToRPS = new ArchiveSourceAgentToRPS();
		sourceToRPS.setAgentList(agentList);

		if (CollectionUtils.isEmpty(policy.getRpsPolices())) {
			// backup destination is share folder
			sourceConfig.setSourceType(EnumArchiveSourceType.STANDALONE_D2D);
			ArchiveSourceAgentStandalone standalone = new ArchiveSourceAgentStandalone();
			standalone.setAgentList(agentList);
			sourceConfig.setSourceAgentStandalone(standalone);

		} else {
			int rpsPolicyCount = policy.getRpsPolices().size();
			for (int i = rpsPolicyCount - 1; i >= 0; i--) {
				if (StringUtil.isNotEmpty(sourceTaskId)) {
					RPSPolicyWrapper rpsPolicy = policy.getRpsPolices().get(i);
					if (sourceTaskId.equals(rpsPolicy.getTaskId())) {
						conf.setPolicyId(rpsPolicy.getRpsPolicy().getId());

						RPSInfo achiveRPSInfo = null;
						if (i == 0) {
							// archive source is backup destination
							achiveRPSInfo = getRPSInfo(policy);
						} else {
							// archive source is replication destination
							achiveRPSInfo = getRPSInfo(policy.getRpsPolices()
									.get(i - 1).getRpsPolicy());
							RPSDataStoreSettings dsSettings = rpsPolicy.getRpsPolicy().getRpsSettings()
							        .getRpsDataStoreSettings();
							achiveRPSInfo.setDataStoreId(dsSettings.getDataStoreName());
						}

						sourceConfig
								.setSourceType(EnumArchiveSourceType.SOURCETYPE_RPS_BACKUP);
						sourceToRPS.setRpsInfo(achiveRPSInfo);
						sourceConfig.setSourceAgentToRPS(sourceToRPS);

					}
				}
			}
		}

	}

	private ArchiveJobDestinationConfig getArchiveJobDestinationConfig(
			UnifiedPolicy policy, ArchiveToTapeSettingsWrapper archiveToTapeWrapper) {

		ArchiveToTapeDestinationInfo destInfo = archiveToTapeWrapper
				.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo();
		ArchiveJobDestinationConfig destConfig = new ArchiveJobDestinationConfig();
		destConfig.setDestServerName(destInfo.getDestServerName());
		destConfig
				.setEnableMultiplexing(destInfo.getEnableMultiplexing() == 1 ? true
						: false);
		destConfig.setMaxStreamNum(destInfo.getMaxStreamNum());
		destConfig.setGroupName(destInfo.getGrpName());
		destConfig.setMediaName(destInfo.getMediaName());
		destConfig.setPrimaryServer(destInfo.getDomainName());

		return destConfig;

	}

	private ArchiveSchedule getArchiveSchedule(UnifiedPolicy policy, ArchiveToTapeSettingsWrapper archiveToTapeWrapper) {

		ArchiveSchedule schedule = new ArchiveSchedule();
		List<DailyScheduleDetailItem> itemList = archiveToTapeWrapper
				.getArchiveToTapeSettings().getAdvanceSchedule()
				.getDailyScheduleDetailItems();
		List<com.ca.asbu.webservice.data.archive2tape.cfg.DailyScheduleDetailItem> asbuItemList = new ArrayList<com.ca.asbu.webservice.data.archive2tape.cfg.DailyScheduleDetailItem>();
		if (CollectionUtils.isNotEmpty(itemList)) {
			for (DailyScheduleDetailItem item : itemList) {
				ArrayList<com.ca.asbu.webservice.data.archive2tape.cfg.ScheduleDetailItem> asbuScheduleDetailItemList = new ArrayList<>();
				com.ca.asbu.webservice.data.archive2tape.cfg.DailyScheduleDetailItem detailItem = new com.ca.asbu.webservice.data.archive2tape.cfg.DailyScheduleDetailItem();
				detailItem.setDayOfWeek(item.getDayofWeek());
				if (CollectionUtils.isNotEmpty(item.getScheduleDetailItems())) {
					for (ScheduleDetailItem scheduleDetailItem : item
							.getScheduleDetailItems()) {
						com.ca.asbu.webservice.data.archive2tape.cfg.ScheduleDetailItem asbuScheduleDetailItem = new com.ca.asbu.webservice.data.archive2tape.cfg.ScheduleDetailItem();
						asbuScheduleDetailItem.setInterval(scheduleDetailItem
								.getInterval());
						asbuScheduleDetailItem
								.setIntervalUnit(scheduleDetailItem
										.getIntervalUnit());
						asbuScheduleDetailItem.setJobType(scheduleDetailItem
								.getJobType());
						asbuScheduleDetailItem
								.setRepeatEnabled(scheduleDetailItem
										.isRepeatEnabled());
						com.ca.asbu.webservice.data.archive2tape.cfg.DayTime startTime = new com.ca.asbu.webservice.data.archive2tape.cfg.DayTime(
								scheduleDetailItem.getStartTime().getHour(),
								scheduleDetailItem.getStartTime().getMinute());
						com.ca.asbu.webservice.data.archive2tape.cfg.DayTime endTime = new com.ca.asbu.webservice.data.archive2tape.cfg.DayTime(
								scheduleDetailItem.getEndTime().getHour(),
								scheduleDetailItem.getEndTime().getMinute());
						asbuScheduleDetailItem.setStartTime(startTime);
						asbuScheduleDetailItem.setEndTime(endTime);
						asbuScheduleDetailItemList.add(asbuScheduleDetailItem);
					}
				}
				detailItem.setScheduleDetailItems(asbuScheduleDetailItemList);
				asbuItemList.add(detailItem);
			}
		}

		schedule.setDayItems(asbuItemList);

		return schedule;

	}

	private ArchiveJobScheduleConfig getArchiveJobScheduleConfig(
			UnifiedPolicy policy, ArchiveToTapeSettingsWrapper archiveToTapeWrapper) {

		ArchiveJobScheduleConfig scheduleConfig = new ArchiveJobScheduleConfig();
		/*AdvanceSchedule archiveSchedule = archiveToTapeWrapper.getArchiveToTapeSettings()
				.getAdvanceSchedule();*/
		scheduleConfig
				.setJobScheduleMethod(EnumArchiveJobScheduleMethod.UDP_ARCHIVE_JOB_SCDEDULE_DOWNSTREAM);
		scheduleConfig
				.setJobMethod(EnumArchiveJobMethod.UDP_ARCHIVE_JOB_METHOD_FULL);
		ArchiveJobScheduleDownStream jobSchedule = new ArchiveJobScheduleDownStream();
		jobSchedule.setRetentionName(archiveToTapeWrapper.getArchiveToTapeSettings()
				.getSchedule().getMediaPoolSet().getName());
		jobSchedule.setSharedMediaPool(archiveToTapeWrapper.getArchiveToTapeSettings()
				.getSchedule().isSharedMediaPool());

		// set archive schedule
		jobSchedule.setArchiveSchedule(getArchiveUDPSchedules(policy, archiveToTapeWrapper));

		jobSchedule
				.setMediaMode(archiveToTapeWrapper.getArchiveToTapeSettings()
						.getArchiveMediaUsageMode() == 1 ? EnumArchiveMediaUsageMode.UDP_ARCHIVE_MEDIA_OVERWRITE_SAME_BLANK_ANY_MEDIA
						: EnumArchiveMediaUsageMode.DP_ARCHIVE_MEDIA_APPEND);

		scheduleConfig.setJobSchedule(jobSchedule);

		return scheduleConfig;

	}

	private ArchiveJobAdvanceOption getArchiveJobAdvanceOption(
			UnifiedPolicy policy, ArchiveToTapeSettingsWrapper archiveToTapeWrapper) {

		// set advance information

		ArchiveToTapeAdvance advance = archiveToTapeWrapper.getArchiveToTapeSettings()
				.getAdvance();

		ArchiveJobAdvanceOption advanceOption = new ArchiveJobAdvanceOption();
		advanceOption.setDisableFileEstimate(advance.isDisableFileEstimate());
		if (advance.getEjectMediaOption() == EjectMediaOption.EJECT_MEDIA) {
			advanceOption
					.setEjectMediaOption(EnumEjectMediaOnCompletion.EJECT_MEDIA_ON_COMPLETION_EJECT);
		} else if (advance.getEjectMediaOption() == EjectMediaOption.NOT_EJECT_MEDIA) {
			advanceOption
					.setEjectMediaOption(EnumEjectMediaOnCompletion.EJECT_MEDIA_ON_COMPLETION_NOT_EJECT);
		} else {
			advanceOption
					.setEjectMediaOption(EnumEjectMediaOnCompletion.EJECT_MEDIA_ON_COMPLETION_USE_DEVICE_SETTING);
		}

		// set email settings
		advanceOption.setEmailSetting(getEmailSetting(advance));

		// set encrypt compress config
		advanceOption
				.setEncryptCompressConfig(getArchiveJobEncryCompConfig(advance));

		if (advance.getJobLogOption() == JobLogOption.LOG_ALL_ACTIVITY) {
			advanceOption.setJobLogOption(EnumJobLogOption.LOG_ALL_ACTIVITY);
		} else if (advance.getJobLogOption() == JobLogOption.LOG_SUMMARY_ONLY) {
			advanceOption.setJobLogOption(EnumJobLogOption.LOG_SUMMARY_ONLY);
		} else {
			advanceOption.setJobLogOption(EnumJobLogOption.LOG_DISABLED);
		}

		// set pre-post job script
		advanceOption.setJobPrePostCmdConfig(getArchiveJobPrePostJobCmdConfig(advance));

		// set verification
		advanceOption.setJobVerificationConfig(getArchiveJobVerification(advance));

		// set save job option
		advanceOption.setSaveJobDataOption(getArchiveJobSaveJobOption(advance));

		return advanceOption;

	}

	private List<ArchiveUDPSchedule> getArchiveUDPSchedules(UnifiedPolicy policy, ArchiveToTapeSettingsWrapper archiveToTapeWrapper) {

		PeriodSchedule periodSchedule = archiveToTapeWrapper.getArchiveToTapeSettings()
				.getAdvanceSchedule().getPeriodSchedule();

		List<ArchiveUDPSchedule> udpScheduleList = new ArrayList<ArchiveUDPSchedule>();
		// daily
		ArchiveUDPSchedule dailyUdpSchedule = new ArchiveUDPSchedule();
		dailyUdpSchedule
				.setRetentionUnit(EnumArchiveJobTimeUnit.UDP_ARCHIVE_JOB_TIMEUNIT_DAY);
		if (periodSchedule != null && periodSchedule.getDaySchedule() != null) {
			dailyUdpSchedule.setSelectArchive(archiveToTapeWrapper.getArchiveToTapeSettings()
					.getArchiveConfig().getSource().getSourceItems().get(0)
					.getDailyItem().isEnabled());
			dailyUdpSchedule.setRetentionValue(periodSchedule.getDaySchedule()
					.getRetentionCount());
		}
		udpScheduleList.add(dailyUdpSchedule);

		// weekly
		ArchiveUDPSchedule weeklyUdpSchedule = new ArchiveUDPSchedule();
		weeklyUdpSchedule
				.setRetentionUnit(EnumArchiveJobTimeUnit.UDP_ARCHIVE_JOB_TIMEUNIT_WEEK);
		if (periodSchedule != null && periodSchedule.getWeekSchedule() != null) {
			weeklyUdpSchedule.setSelectArchive(archiveToTapeWrapper
					.getArchiveToTapeSettings().getArchiveConfig().getSource()
					.getSourceItems().get(0).getWeeklyItem().isEnabled());
			weeklyUdpSchedule.setRetentionValue(periodSchedule
					.getWeekSchedule().getRetentionCount());
		}
		udpScheduleList.add(weeklyUdpSchedule);

		// monthly
		ArchiveUDPSchedule monthlyUdpSchedule = new ArchiveUDPSchedule();
		monthlyUdpSchedule
				.setRetentionUnit(EnumArchiveJobTimeUnit.UDP_ARCHIVE_JOB_TIMEUNIT_MONTH);
		if (periodSchedule != null && periodSchedule.getMonthSchedule() != null) {
			monthlyUdpSchedule.setSelectArchive(archiveToTapeWrapper
					.getArchiveToTapeSettings().getArchiveConfig().getSource()
					.getSourceItems().get(0).getMonthlyItem().isEnabled());
			monthlyUdpSchedule.setRetentionValue(periodSchedule
					.getMonthSchedule().getRetentionCount());
		}
		udpScheduleList.add(monthlyUdpSchedule);

		// custom
		ArchiveUDPSchedule customUdpSchedule = new ArchiveUDPSchedule();
		customUdpSchedule
				.setRetentionUnit(EnumArchiveJobTimeUnit.UDP_ARCHIVE_JOB_TIMEUNIT_DAY);
		customUdpSchedule.setSelectArchive(false);
		udpScheduleList.add(customUdpSchedule);

		return udpScheduleList;
	}

	private ArchiveJobSaveJobOption getArchiveJobSaveJobOption(
			ArchiveToTapeAdvance advance) {

		ArchiveJobSaveJobOption saveJobOption = new ArchiveJobSaveJobOption();
		saveJobOption.setAppendASDB(advance.getSaveJobOption().isAppendData());
		saveJobOption.setAppendJobScript(advance.getSaveJobOption()
				.isAppendJobScript());
		saveJobOption.setAppendCatalogFile(advance.getSaveJobOption()
				.isAppendCatalogFile());

		return saveJobOption;

	}

	private ArchiveJobVerification getArchiveJobVerification(
			ArchiveToTapeAdvance advance) {

		ArchiveJobVerification verification = new ArchiveJobVerification();
		if (advance.getJobVerificationOption() == JobVerifiicationOption.NONE) {
			verification
					.setVerifyOption(EnumArchiveJobVerificationOption.UDP_ARCHIVE_JOB_VERIFICATION_NONE);
		} else if (advance.getJobVerificationOption() == JobVerifiicationOption.SCAN) {
			verification
					.setVerifyOption(EnumArchiveJobVerificationOption.UDP_ARCHIVE_JOB_VERIFICATION_SCAN);
		} else if (advance.getJobVerificationOption() == JobVerifiicationOption.CACULATE) {
			verification
					.setVerifyOption(EnumArchiveJobVerificationOption.UDP_ARCHIVE_JOB_VERIFICATION_CHECK_CRC);
		} else {
			verification
					.setVerifyOption(EnumArchiveJobVerificationOption.UDP_ARCHIVE_JOB_VERIFICATION_COMPAREDATA);
		}

		return verification;

	}

	private ArchiveJobPrePostJobCmdConfig getArchiveJobPrePostJobCmdConfig(
			ArchiveToTapeAdvance advance) {

		ArchiveJobPrePostJobCmdConfig prePostCmdConfig = new ArchiveJobPrePostJobCmdConfig();
		prePostCmdConfig.setbCheckExitCode(advance.getCommandConfig()
				.isOnExitCode());
		prePostCmdConfig.setCmdPass(advance.getCommandConfig().getPassword());
		prePostCmdConfig.setCmdUser(advance.getCommandConfig().getUsername());
		prePostCmdConfig.setEnablePreJobCmd(advance.getCommandConfig()
				.isEnableBeforeJob());
		prePostCmdConfig.setEnablePostJobCmd(advance.getCommandConfig()
				.isEnableAfterJob());
		Set<EnumArchiveJobPrePostJobOption> afterJobOption = new HashSet<>();
		Set<BeforeAfterJobOption> afterJobOptions = advance.getCommandConfig()
				.getAfterJobOption();
		if (afterJobOptions
				.contains(BeforeAfterJobOption.DISABLE_POSTCMD_ON_JOB_FAIL)) {
			afterJobOption
					.add(EnumArchiveJobPrePostJobOption.DISABLE_POSTCMD_ON_JOB_FAIL);
		}
		if (afterJobOptions
				.contains(BeforeAfterJobOption.DISABLE_POSTCMD_ON_JOB_INCOMPLETE)) {
			afterJobOption
					.add(EnumArchiveJobPrePostJobOption.DISABLE_POSTCMD_ON_JOB_INCOMPLETE);
		}
		if (afterJobOptions
				.contains(BeforeAfterJobOption.DISABLE_POSTCMD_ON_JOB_COMPLETE)) {
			afterJobOption
					.add(EnumArchiveJobPrePostJobOption.DISABLE_POSTCMD_ON_JOB_COMPLETE);
		}
		prePostCmdConfig.setPostcmdOptions(afterJobOption);
		prePostCmdConfig.setPostJobCmd(advance.getCommandConfig()
				.getAfterJobCommand());
		Set<EnumArchiveJobPrePostJobOption> preJobOption = new HashSet<>();
		if (advance.getCommandConfig().getBeforeJobOption() == BeforeAfterJobOption.RUN_JOB) {
			preJobOption
					.add(EnumArchiveJobPrePostJobOption.RUN_JOB_ON_PRECMD_EXIT);
		} else if (advance.getCommandConfig().getBeforeJobOption() == BeforeAfterJobOption.SKIP_JOB_ON_PRECMD_EXIT) {
			preJobOption
					.add(EnumArchiveJobPrePostJobOption.SKIP_JOB_ON_PRECMD_EXIT);
		} else {
			preJobOption
					.add(EnumArchiveJobPrePostJobOption.SKIP_POSTCMD_ON_PRECMD_EXIT);
		}
		prePostCmdConfig.setPreCmdOptions(preJobOption);
		prePostCmdConfig.setPreJobCmd(advance.getCommandConfig()
				.getBeforeJobCommand());
		prePostCmdConfig.setPreJobCmdExitCode(advance.getCommandConfig()
				.getExitCode());

		return prePostCmdConfig;

	}

	private ArchiveJobEncryCompConfig getArchiveJobEncryCompConfig(
			ArchiveToTapeAdvance advance) {

		ArchiveJobEncryCompConfig encryptionCompressionConfig = new ArchiveJobEncryCompConfig();
		encryptionCompressionConfig.setEnableCompress(advance
				.getEncryptionCompression().isEnableCompression());
		encryptionCompressionConfig.setEnableEncrypt(advance
				.getEncryptionCompression().isEnableEncryption());
		encryptionCompressionConfig.setSessPass(advance
				.getEncryptionCompression().getSessionPassword());

		return encryptionCompressionConfig;

	}

	private EmailSetting getEmailSetting(ArchiveToTapeAdvance advance) {

		EmailSetting emailSetting = new EmailSetting();
		RpsEmailAlertSettings eas = advance.getEmailAlertConfig()
				.getEmailsetting();
		emailSetting.setEnableEmailOnArchiveCancel(advance
				.getEmailAlertConfig().isAlertCanceledByUser());
		emailSetting.setEnableEmailOnArchiveFail(advance.getEmailAlertConfig()
				.isAlertJobFailed());
		;
		emailSetting.setEnableEmailOnArchiveFormatTape(advance
				.getEmailAlertConfig().isAlertFormatBlankTape());
		emailSetting.setEnableEmailOnArchiveNoMedia(advance
				.getEmailAlertConfig().isAlertMediaInAvailable());
		emailSetting.setEnableEmailOnArchiveIncomplete(advance
				.getEmailAlertConfig().isAlertJobInComplete());//
		emailSetting.setEnableEmailOnArchiveSucceed(advance
				.getEmailAlertConfig().isAlertJobComplete());
		emailSetting.setEnableEmailOnArchiveCrash(advance.getEmailAlertConfig()
				.isAlertCrashed());
		if (eas != null) {
			emailSetting.setEnableHTMLFormat(eas.isEnableHTMLFormat());
			emailSetting.setEnableProxy(eas.isEnableProxy());
			emailSetting.setEnableSettings(eas.isEnableSettings());
			emailSetting.setEnableSsl(eas.isEnableSsl());
			emailSetting.setEnableTls(eas.isEnableTls());
			emailSetting.setFromAddress(eas.getFromAddress());
			emailSetting.setMailAuth(eas.isMailAuth());
			emailSetting.setMailPassword(eas.getMailPassword());
			emailSetting.setMailServiceName(eas.getMailServiceName());
			emailSetting.setMailUser(eas.getMailUser());
			emailSetting.setProxyAddress(eas.getProxyAddress());
			emailSetting.setProxyAuth(eas.isProxyAuth());
			emailSetting.setProxyPassword(eas.getProxyPassword());
			emailSetting.setProxyPort(eas.getProxyPort());
			emailSetting.setProxyUsername(eas.getProxyUsername());
			emailSetting.setRecipients(eas.getRecipients());
			emailSetting.setSmtp(eas.getSmtp());
			emailSetting.setSmtpPort(eas.getSmtpPort());
			emailSetting.setSubject(eas.getSubject());
			emailSetting.setUrl(eas.getUrl());
		}

		return emailSetting;

	}

	private RPSInfo getRPSInfo(RPSPolicy rpsPolicy) {
		RPSInfo rpsInfo = new RPSInfo();
		RPSReplicationSettings settings = rpsPolicy.getRpsSettings()
				.getRpsReplicationSettings();
		/*RPSDataStoreSettings dsSettings = rpsPolicy.getRpsSettings()
		        .getRpsDataStoreSettings();*/
		rpsInfo.setHostName(settings.getHostName());
		rpsInfo.setUserPass(settings.getPassword());
		rpsInfo.setUserName(settings.getUserName());
		rpsInfo.setPort(settings.getPort());
		rpsInfo.setProtocol(settings.getProtocol() == 0 ? "Http" : "Https");
		rpsInfo.setRPSId(settings.getUuid());
		//rpsInfo.setDataStoreId(dsSettings.getDataStoreName());
		return rpsInfo;
	}
	private RPSInfo getHBBURPSInfo(UnifiedPolicy policy){
		RPSInfo rpsInfo = new RPSInfo();
		RpsHost rpsHost = policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost();
		String dataStoreUUID =  policy.getVSphereBackupConfiguration()
				.getBackupRpsDestSetting().getRPSDataStore();
		rpsInfo.setHostName(rpsHost.getRhostname());
		rpsInfo.setUserPass(rpsHost.getPassword());
		rpsInfo.setUserName(rpsHost.getUsername());
		rpsInfo.setPort(rpsHost.getPort());
		rpsInfo.setProtocol(rpsHost.isHttpProtocol() ? "Http" : "Https");
		rpsInfo.setRPSId(rpsHost.getUuid());
		rpsInfo.setDataStoreId(dataStoreUUID);
		return rpsInfo;
	}
	private RPSInfo getRPSInfo(UnifiedPolicy policy) {
		RPSInfo rpsInfo = new RPSInfo();
		RpsHost rpsHost = policy.getBackupConfiguration()
				.getBackupRpsDestSetting().getRpsHost();
		String dataStoreUUID =  policy.getBackupConfiguration()
				.getBackupRpsDestSetting().getRPSDataStore();
		rpsInfo.setHostName(rpsHost.getRhostname());
		rpsInfo.setUserPass(rpsHost.getPassword());
		rpsInfo.setUserName(rpsHost.getUsername());
		rpsInfo.setPort(rpsHost.getPort());
		rpsInfo.setProtocol(rpsHost.isHttpProtocol() ? "Http" : "Https");
		rpsInfo.setRPSId(rpsHost.getUuid());
		rpsInfo.setDataStoreId(dataStoreUUID);
		return rpsInfo;
	}

	private RPSInfo bindMSPRPSInfo(UnifiedPolicy policy) {
		RPSInfo rpsInfo = new RPSInfo();
		RPSReplicationSettings settings = policy
				.getMspServerReplicationSettings();
		rpsInfo.setHostName(settings.getHostName());
		rpsInfo.setUserPass(settings.getPassword());
		rpsInfo.setUserName(settings.getUserName());
		rpsInfo.setPort(settings.getPort());
		rpsInfo.setProtocol(settings.getProtocol() == 0 ? "Http" : "Https");
		return rpsInfo;
	}

	private VMInfo bindVMInfo(Node node) {
		VMInfo vmInfo = new VMInfo();
		vmInfo.setHypervisorServer(node.getHyperVisor());
		vmInfo.setInstanceUUID(node.getVmInstanceUUID());
		// TODO maybe need to modify futher
		vmInfo.setName(node.getVmName());
		vmInfo.setUuid(node.getVmInstanceUUID());
		//For fix issue 212102, asbu's entry name is empty
		vmInfo.setHostName(node.getVmName());
		return vmInfo;
	}

	private ArchiveSourceHBBUToAgentProxy bindHBBUProxyInfo(UnifiedPolicy policy) {
		NodeInfo proxyInfo = new NodeInfo();
		ArchiveSourceHBBUToAgentProxy hbbuToAgentProxy = new ArchiveSourceHBBUToAgentProxy();
		String hostName = policy.getVSphereBackupConfiguration()
				.getvSphereProxy().getVSphereProxyName();
		if(hostName.contains(".")){
			String checkedHostName = CommonUtil.getHostNameByIp(hostName);
			if(!StringUtil.isEmptyOrNull(checkedHostName)){
				hostName = checkedHostName;
			}
		}
		proxyInfo.setHostName(hostName);
		proxyInfo.setUserName(policy.getVSphereBackupConfiguration()
				.getvSphereProxy().getVSphereProxyUsername());
		proxyInfo.setUserPass(policy.getVSphereBackupConfiguration()
				.getvSphereProxy().getVSphereProxyPassword());
		hbbuToAgentProxy.setProxyInfo(proxyInfo);
		return hbbuToAgentProxy;
	}

	private ArchiveSourceAgentInfo bindAgentInfo(Node node) {
		ArchiveSourceAgentInfo agentInfo = new ArchiveSourceAgentInfo();
		agentInfo.setAgentId(node.getD2DUUID());
		String hostName = node.getHostname();
		if(hostName.contains(".")){
			String checkedHostName = CommonUtil.getHostNameByIp(hostName);
			if(!StringUtil.isEmptyOrNull(checkedHostName)){
				hostName = checkedHostName;
			}
		}
		agentInfo.setHostName(hostName);
		agentInfo.setHostIP(node.getIpaddress());
		if (node.getD2dPort() != null) {
			agentInfo.setPort(Integer.parseInt(node.getD2dPort()));
		}
		agentInfo.setProtocol((node.getD2dProtocol() == 1) ? "http" : "https");
		agentInfo.setUserName(node.getUsername());
		agentInfo.setUserPass(node.getPassword());
		if (node.getAuthUUID() != null)
		{
			agentInfo.setLoginId(WSJNI.AFDecryptString(node.getAuthUUID()));
		}
		return agentInfo;
	}
	
	private ArchiveSourceAgentInfo bindLinuxAgentInfo(Node node) {
		ArchiveSourceAgentInfo agentInfo = new ArchiveSourceAgentInfo();
		agentInfo.setAgentId(node.getD2DUUID());
		String hostName = node.getHostname();
		if(hostName.contains(".")){
			String checkedHostName = CommonUtil.getHostNameByIp(hostName);
			if(!StringUtil.isEmptyOrNull(checkedHostName)){
				hostName = checkedHostName;
			}
		}
		agentInfo.setHostName(hostName);
		agentInfo.setHostIP(node.getIpaddress());
		agentInfo.setUserName(node.getUsername());
		agentInfo.setUserPass(node.getPassword());
		return agentInfo;
	}

	private List<com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo> mockASBUServerList() {
		List<com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo> servers = new ArrayList<>();
		com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo serverInfo1 = new com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo();
		serverInfo1.setServerName("zhata01-hv6");
		serverInfo1
				.setServerClass(com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo.SERVER_CLASS_MEMBER);
		com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo serverInfo2 = new com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo();
		serverInfo2.setServerName("zhata01-hv7");
		serverInfo2
				.setServerClass(com.ca.asbu.webservice.data.archive2tape.ASBUServerInfo.SERVER_CLASS_PRIMARY);
		servers.add(serverInfo1);
		servers.add(serverInfo2);
		return servers;
	}
}
