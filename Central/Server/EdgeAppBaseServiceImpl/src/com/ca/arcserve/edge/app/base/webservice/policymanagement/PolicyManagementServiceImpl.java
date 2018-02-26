package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.rps.webservice.data.RpsArchiveConfiguationWrapper;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.replication.CAProxy;
import com.ca.arcflash.rps.webservice.replication.CAProxySelector;
import com.ca.arcflash.rps.webservice.replication.HttpProxy;
import com.ca.arcflash.rps.webservice.replication.ManualReplicationItem;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveSourceItem;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.D2DConfiguration;
import com.ca.arcflash.webservice.data.backup.HBBUConfiguration;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.ResourcePool;
import com.ca.arcflash.webservice.data.vsphere.SavePolicyWarning;
import com.ca.arcflash.webservice.data.vsphere.StorageAppliance;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereProxy;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyCheckStatus;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeService_Oolong;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;
import com.ca.arcserve.edge.app.asbu.dao.IASBUDao;
import com.ca.arcserve.edge.app.base.appdaos.AuthUuidWrapper;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.EdgeIntegerValue;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicyGroup;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicyHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicyHostUuid;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVSphereProxyInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeTaskIdDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.appdaos.IStorageApplianceDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.D2DFacade;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.ExternalLinkManager;
import com.ca.arcserve.edge.app.base.common.IEdgeExternalLinks;
import com.ca.arcserve.edge.app.base.common.ObjectDeepComparer;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.AgentInstallationInPlanHelper;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployingCache;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentScheduler;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.deploytaskrunner.UnifiedDeployTaskRunner;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.RpsSettingTaskDeployment;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.IPolicyManagementService;
import com.ca.arcserve.edge.app.base.webservice.action.ActionTaskManager;
import com.ca.arcserve.edge.app.base.webservice.asbuintegration.ASBUDestinationManager;
import com.ca.arcserve.edge.app.base.webservice.contract.action.BackupNowTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.EdgeASBUServer;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ItemOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.NodeStatusUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.RebootType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeInfoList4VM;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeProtectionStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VCMConverterType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.AssignPolicyCheckResultCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.AssignPolicyResultCodes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.BackupPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.BackupPolicySummary;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.DeletePolicyResultCodes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.NodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ParsedBackupPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlolicyPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ArchiveToTapeSettingsWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ArchiveToTapeSettingsWrapper.ArchiveToTapeSourceType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ConversionTask;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.FileCopySettingWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.NonPlanContent;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.ProxyConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VSphereProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeD2DRegServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeD2DRegServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.destinationmanagement.ShareFolderManageServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.IRemoteNodeService;
import com.ca.arcserve.edge.app.base.webservice.node.IRemoteNodeServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.node.ImportNodesJob;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.vcm.VCMServiceImpl;
import com.ca.arcserve.edge.app.msp.webservice.IEdgeMsp4ClientService;
import com.ca.arcserve.edge.app.msp.webservice.contract.MspReplicationDestination;
import com.ca.arcserve.edge.app.rps.webservice.common.ReplicateRpsPolicyUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.job.filecopyJob.ManualFilecopyParam;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.job.replicationJob.ManualReplicationRPSParam;
import com.ca.arcserve.edge.app.rps.webservice.datastore.DataStoreManager;
import com.ca.arcserve.edge.webservice.msp.MspWebServiceFactory;

public class PolicyManagementServiceImpl implements IPolicyManagementService
{
	private static long edgeTaskId = 0;
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	public class PolicyXmlSectionNames
	{
		public static final String SubscriptionSettings			= "SubscriptionSettings";
		public static final String BackupSettings				= "BackupSettings";
		public static final String ArchivingSettings			= "ArchivingSettings";
		public static final String ScheduledExportSettings		= "ScheduledExportSettings";
		public static final String VirtualConversionSettings	= "VirtualConversionSettings";
		public static final String VMBackupSettings				= "VMBackupSettings";
		public static final String PreferencesSettings			= "PreferencesSettings";
		public static final String RpsSettings 					= "RpsSettings";
	}

	private static PolicyManagementServiceImpl instance = null;

	protected static final Logger logger = Logger.getLogger( PolicyManagementServiceImpl.class );

	private PolicyUtil policyUtil = PolicyUtil.getInstance();

	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeEsxDao edgeEsxDao  = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao edgeHypervDao= DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgeVCMDao edgeVCMDao = DaoFactory.getDao(IEdgeVCMDao.class);
	private IASBUDao asbuDao = DaoFactory.getDao(IASBUDao.class);
	//Feb sprint
	private IStorageApplianceDao infraDao = DaoFactory.getDao(IStorageApplianceDao.class);

	private NodeServiceImpl nodeService = null;
	private VCMServiceImpl vcmService = null;
	private JobHistoryServiceImpl jobService = null;
	
	private IActivityLogService activityLogService = null;
	private ShareFolderManageServiceImpl sharedFolderService = null;
	private D2DFacade d2dFacade = D2DFacade.getInstance();
	private PolicyXmlUtilities policyXmlUtilities = PolicyXmlUtilities.getInstance();
	private NodeManagementFacade nodeManFacade = NodeManagementFacade.getInstance();
	private PolicyDeploymentScheduler policyDeploymentScheduler = null;
	private IEdgeD2DRegService d2dRegService = new EdgeD2DRegServiceImpl();
	private CommonServiceFacade commonServiceFacade = CommonServiceFacade.getInstance();
	private NativeFacade nativeFacade = null;
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private static IEdgeExternalLinks edgeExternalLinks = ExternalLinkManager.getInstance().getLinks(IEdgeExternalLinks.class);
	
	//////////////////////////////////////////////////////////////////////////

	public static synchronized void setNextTaskId() {
		long[] taskId = new long[1];
		IEdgeTaskIdDao taskIdDao = DaoFactory.getDao(IEdgeTaskIdDao.class);
		try {
			taskIdDao.as_edge_get_next_taskid(taskId);
			edgeTaskId = taskId[0];
		} catch(Exception e) {
			logger.debug(e);
			edgeTaskId = 0;
		}
	}
	
	public static synchronized long getTaskId() {
		return edgeTaskId;
	}
	
	public static synchronized void cleanTaskId() {
		edgeTaskId = 0;
	}
	
	public PolicyManagementServiceImpl(){}

	//////////////////////////////////////////////////////////////////////////

	public static synchronized PolicyManagementServiceImpl getInstance()
	{
		if (instance == null)
			instance = new PolicyManagementServiceImpl();

		return instance;
	}

	//////////////////////////////////////////////////////////////////////////

	public Logger getLogger()
	{
		return logger;
	}

	//////////////////////////////////////////////////////////////////////////

	private NodeServiceImpl getNodeService()
	{
		if (this.nodeService == null)
			this.nodeService = new NodeServiceImpl();

		return this.nodeService;
	}

	//////////////////////////////////////////////////////////////////////////

	private VCMServiceImpl getVCMService()
	{
		if (this.vcmService == null)
			this.vcmService = new VCMServiceImpl();

		return this.vcmService;
	}
	
	private JobHistoryServiceImpl getJobService()
	{
		if (this.jobService == null)
			this.jobService = new JobHistoryServiceImpl();
		
		return this.jobService;
	}

	//////////////////////////////////////////////////////////////////////////

	private IActivityLogService getActivityLogService()
	{
		if (this.activityLogService == null)
			this.activityLogService = new ActivityLogServiceImpl();

		return this.activityLogService;
	}

	private ShareFolderManageServiceImpl getSharedFolderService(){
		if (this.sharedFolderService == null)
			this.sharedFolderService = new ShareFolderManageServiceImpl();

		return this.sharedFolderService;
	}
	
	public NativeFacade getNativeFacade()
	{
		if (this.nativeFacade == null)
			this.nativeFacade = new NativeFacadeImpl();
		
		return this.nativeFacade;
	}

	//////////////////////////////////////////////////////////////////////////

	public void encryptBackupConfiguration(BackupConfiguration config) {
		config.setAdminPassword(getNativeFacade().AFEncryptString(config.getAdminPassword()));
		config.setEncryptionKey(getNativeFacade().AFEncryptString(config.getEncryptionKey()));
		config.setPrePostPassword(getNativeFacade().AFEncryptString(config.getPrePostPassword()));
		config.setPassword(getNativeFacade().AFEncryptString(config.getPassword()));

		if (config.getEmail() != null) {
			config.getEmail().setProxyPassword(getNativeFacade().AFEncryptString(config.getEmail().getProxyPassword()));
			config.getEmail().setMailPassword(getNativeFacade().AFEncryptString(config.getEmail().getMailPassword()));
		}
		if(config.getBackupRpsDestSetting() != null 
				&& config.getBackupRpsDestSetting().getRpsHost() != null 
				&& !StringUtil.isEmptyOrNull(config.getBackupRpsDestSetting().getRpsHost().getPassword()))
		{
			config.getBackupRpsDestSetting().getRpsHost().setPassword(getNativeFacade().AFEncryptString(
					config.getBackupRpsDestSetting().getRpsHost().getPassword()));
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings( "serial" )
	public class BadConfigurationException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public class BadBackupConfigurationException extends BadConfigurationException {}
	
	@SuppressWarnings( "serial" )
	public class BadArchiveConfigurationException extends BadConfigurationException {}
	
	@SuppressWarnings( "serial" )
	public class BadScheduledExportConfigurationException extends BadConfigurationException {}
	
	@SuppressWarnings( "serial" )
	public class BadPreferencesConfigurationException extends BadConfigurationException {}
	
	@SuppressWarnings( "serial" )
	public class BadVcmConfigurationException extends BadConfigurationException {}
	
	@SuppressWarnings( "serial" )
	public class BadVMBackupConfigurationException extends BadConfigurationException {}

	@SuppressWarnings( "serial" )
	public class BadRpsConfigurationException extends BadConfigurationException {}

	@SuppressWarnings( "serial" )
	public static class NoPolicyGeneralInfoException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class InvalidEditSessionException extends Exception {}

	private static String xmlDocumentToString( Document xmlDocument )
	{
		try
		{
			if (xmlDocument == null)
				return "";

			Transformer t = TransformerFactory.newInstance().newTransformer();
			//t.setOutputProperty( OutputKeys.DOCTYPE_SYSTEM, SystemIdentifier );
			//t.setOutputProperty( OutputKeys.DOCTYPE_PUBLIC, PublicIdentifier );
			t.setOutputProperty( OutputKeys.INDENT, "no" );
			t.setOutputProperty( OutputKeys.METHOD, "xml" );
			t.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
			t.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );

			StringWriter stringWriter = new StringWriter();
			t.transform( new DOMSource( xmlDocument ), new StreamResult( stringWriter ) );
			return stringWriter.toString();
		}
		catch (Exception e)
		{
			logger.error( "xmlDocumentToString() failed.", e );
			return "";
		}
	}

	@Override
	public List<ItemOperationResult> deleteUnifiedPolicies( List<Integer> idList )
		throws EdgeServiceFault
	{
		List<ItemOperationResult> resultList = new ArrayList<ItemOperationResult>();

		//boolean needDeploy=false;
		for (Integer policyId : idList)
		{
			ItemOperationResult result = new ItemOperationResult();
			result.setItemId( policyId );
			result.setResultCode( DeletePolicyResultCodes.Successful );
			resultList.add( result );

			try
			{
				edgePolicyDao.as_edge_plan_group_map_delete(-1,0,policyId);
				edgePolicyDao.deletePlanDestinationMap(policyId, 0, -1,-1);
				List<EdgeHost> hostList = new ArrayList<EdgeHost>();
				edgePolicyDao.as_edge_host_list_bypolicyid( policyId, hostList );
				edgePolicyDao.as_edge_policy_updateStatus(policyId, PlanStatus.Deleting);
				if (hostList.size() > 0)
				{
					edgePolicyDao.as_edge_policy_updateMapStatus(policyId, 0, PolicyDeployReasons.PolicyUnassigned, PolicyDeployStatus.ToBeDeployed, PolicyDeployFlags.DeletePlan, PlanEnableStatus.Enable.getValue());
					getPolicyDeploymentScheduler().doDeploymentNowByPlanId(policyId);
				}
				else
				{
					UnifiedPolicy uPolicy = loadUnifiedPolicyById(policyId);
					RpsSettingTaskDeployment deploy = new RpsSettingTaskDeployment();
					deploy.deleteRpsPolicySettings(uPolicy);
					
					ASBUDestinationManager.getInstance().deleteASBUSettings(uPolicy);
					
					edgePolicyDao.as_edge_policy_remove( policyId );
				}

			}
			catch (Exception e)
			{
				edgePolicyDao.as_edge_policy_updateStatus(policyId, PlanStatus.DeleteFailed);
				logger.error( "deletePolicies() failed.", e );
				throw e;
			}
			logger.info( "deletePolicies() unbindEntity policyId: "+policyId);
			gatewayService.unbindEntity(policyId, EntityType.Policy);
		}

		return resultList;		
	}
	
	//////////////////////////////////////////////////////////////////////////

	private VSphereBackupConfigurationXMLDAO vShpereXmlDAOInstance = null;

	private synchronized VSphereBackupConfigurationXMLDAO getVSphereBackupConfigurationXMLDAO()
	{
		if(vShpereXmlDAOInstance==null)
			vShpereXmlDAOInstance = new VSphereBackupConfigurationXMLDAO();
		return vShpereXmlDAOInstance;

	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public ParsedBackupPolicy getParsedBackupPolicy( int policyId )
		throws EdgeServiceFault
	{
		if (policyId <= 0)
			throw EdgeServiceFault.getFault(
				EdgeServiceErrorCode.PolicyManagement_BadParameters, "" );

		// get policy from database
		EdgePolicy daoPolicy = getEdgePolicy(policyId, true);

		ParsedBackupPolicy parsedPolicy = new ParsedBackupPolicy();

		BackupPolicy policy = new BackupPolicy();
		policy.setId( daoPolicy.getId() );
		policy.setName( daoPolicy.getName() );
		policy.setType( daoPolicy.getType() );
		policy.setContentFlag( daoPolicy.getContentflag() );
		policy.setVersion( daoPolicy.getVersion() );
		policy.setCreationTime( daoPolicy.getCreationtime() );
		policy.setModificationTime( daoPolicy.getModifiedtime() );
		policy.setPolicyProductType(daoPolicy.getProducttype());

		parsedPolicy.setGeneralInfo( policy );

		String policyXml = daoPolicy.getPolicyxml();
		Document xmlDocument = policyUtil.xmlStringToDocument( policyXml );

		Document subDocument;

		try {
			if ((policy.getContentFlag() & BackupPolicySummary.PolicyContentFlag.Subscription) != 0)
			{
				subDocument = policyUtil.getSectionDocument( xmlDocument, PolicyXmlSectionNames.SubscriptionSettings );
				SubscriptionConfiguration subscriptionConfiguration = this.d2dFacade.xmlDocumentToSubScriptionConfiguration( subDocument );
				parsedPolicy.setSubscriptionConfiguration(subscriptionConfiguration);
			}
			
			if ((policy.getContentFlag() & BackupPolicySummary.PolicyContentFlag.Backup) != 0)
			{
				subDocument = policyUtil.getSectionDocument( xmlDocument, PolicyXmlSectionNames.BackupSettings );
				BackupConfiguration backupConfig = this.d2dFacade.xmlDocumentToBackupConfiguration( subDocument );
				parsedPolicy.setBackupSettings( backupConfig );
			}

			if ((policy.getContentFlag() & BackupPolicySummary.PolicyContentFlag.Archiving) != 0)
			{
				subDocument = policyUtil.getSectionDocument( xmlDocument, PolicyXmlSectionNames.ArchivingSettings );
				ArchiveConfiguration configuration = (subDocument == null) ?
					null : this.d2dFacade.xmlDocumentToArchiveConfiguration( subDocument );
				parsedPolicy.setArchiveConfiguration( configuration );
			}

			// Scheduled Export
			if ((policy.getContentFlag() & BackupPolicySummary.PolicyContentFlag.ScheduledExport) != 0)
			{
				subDocument = policyUtil.getSectionDocument( xmlDocument, PolicyXmlSectionNames.ScheduledExportSettings );
				ScheduledExportConfiguration configuration = (subDocument == null) ?
					null : this.d2dFacade.xmlDocumentToScheduledExportConfiguration( subDocument );
				parsedPolicy.setScheduledExportConfiguration( configuration );
			}

			if ((policy.getContentFlag() & BackupPolicySummary.PolicyContentFlag.VirtualConversion) != 0)
			{
				subDocument = policyUtil.getSectionDocument( xmlDocument, PolicyXmlSectionNames.VirtualConversionSettings );
				String vcmSettings = this.policyXmlUtilities.getVCMSettingsFromXmlDocument( subDocument );
				parsedPolicy.setVcmSettings( vcmSettings );
			}

			if ((policy.getContentFlag() & BackupPolicySummary.PolicyContentFlag.VMBackup) != 0)
			{
				subDocument = policyUtil.getSectionDocument( xmlDocument, PolicyXmlSectionNames.VMBackupSettings );
				VMBackupConfiguration vMBackupConfiguration = this.d2dFacade.xmlDocumentToVMBackupConfiguration( subDocument );
				parsedPolicy.setVmBackupConfiguration(vMBackupConfiguration);
			}

			if ((policy.getContentFlag() & BackupPolicySummary.PolicyContentFlag.Rps) != 0)
			{
				subDocument = policyUtil.getSectionDocument( xmlDocument, PolicyXmlSectionNames.RpsSettings );
				VMBackupConfiguration vMBackupConfiguration = this.d2dFacade.xmlDocumentToVMBackupConfiguration( subDocument );
				parsedPolicy.setVmBackupConfiguration(vMBackupConfiguration);
			}

			if ((policy.getContentFlag() & BackupPolicySummary.PolicyContentFlag.Preferences) != 0)
			{
				subDocument = policyUtil.getSectionDocument( xmlDocument, PolicyXmlSectionNames.PreferencesSettings );
				PreferencesConfiguration preferencesConfig =
					this.d2dFacade.xmlDocumentToPreferencesSettings( subDocument );
				parsedPolicy.setPreferencesSettings( preferencesConfig );
			}
		} catch (Exception e) {
			logger.error( "getParsedBackupPolicy()", e );
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}

		return parsedPolicy;
	}

	@Override
	public List<com.ca.arcserve.edge.app.base.webservice.contract.node.Node>
		getNodesByPolicy(
		int policyId ) throws EdgeServiceFault
	{
		List<com.ca.arcserve.edge.app.base.webservice.contract.node.Node> nodeList =
				new ArrayList<com.ca.arcserve.edge.app.base.webservice.contract.node.Node>();

		List<EdgeHost> edgeHostList = new ArrayList<EdgeHost>();
		edgePolicyDao.as_edge_host_list_bypolicyid( policyId, edgeHostList );

		for (EdgeHost edgeHost : edgeHostList)
			nodeList.add( nodeManFacade.convertDaoNode2ContractNode( edgeHost ) );

		return nodeList;
	}

	@SuppressWarnings( "serial" )
	public class GetPolicyContentXmlException extends Exception {}

	@SuppressWarnings( "serial" )
	public class GetPolicyUuidException extends Exception {}

	@SuppressWarnings( "serial" )
	public class GetD2DConnectInfoException extends Exception {}

	@SuppressWarnings( "serial" )
	public static class FailedToGetD2DVersionException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class UnsatisfiedD2DVersionException extends Exception
	{
		private int d2dMajorVersion = 0;
		private int d2dMinorVersion = 0;
		private int requiredMajorVersion = 0;
		private int requiredMinorVersion = 0;
		
		public UnsatisfiedD2DVersionException(
			int d2dMajorVersion, int d2dMinorVersion,
			int requiredMajorVersion, int requiredMinorVersion )
		{
			this.d2dMajorVersion = d2dMajorVersion;
			this.d2dMinorVersion = d2dMinorVersion;
			this.requiredMajorVersion = requiredMajorVersion;
			this.requiredMinorVersion = requiredMinorVersion;
		}

		public int getD2dMajorVersion()
		{
			return d2dMajorVersion;
		}

		public int getD2dMinorVersion()
		{
			return d2dMinorVersion;
		}

		public int getRequiredMajorVersion()
		{
			return requiredMajorVersion;
		}

		public int getRequiredMinorVersion()
		{
			return requiredMinorVersion;
		}
	}
	
	@SuppressWarnings( "serial" )
	public class NodeIsNotManagedException extends Exception {}

	//////////////////////////////////////////////////////////////////////////

	public class D2DConnectInfo
	{
		private String hostName;
		private int port;
		private String protocol;
		private String username;
		private String password;
		private String domain;
		private String uuid;
		private String authUuid;
		private boolean isManaged;
		private boolean importfromvshpere;
		private int hostId; //add for issue 149443 <zhaji22>
		private VCMConverterType vcmConverterType = null; // Add converter type for conversion job
		
		private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;

		public String getHostName()
		{
			return hostName;
		}

		public void setHostName( String hostName )
		{
			this.hostName = hostName;
		}

		public int getPort()
		{
			return port;
		}

		public void setPort( int port )
		{
			this.port = port;
		}

		public String getProtocol()
		{
			return protocol;
		}

		public void setProtocol( String protocol )
		{
			this.protocol = protocol;
		}

		public String getUsername()
		{
			return username;
		}

		public void setUsername( String username )
		{
			this.username = username;
		}

		public String getPassword()
		{
			return password;
		}

		public void setPassword( String password )
		{
			this.password = password;
		}

		public String getDomain()
		{
			return domain;
		}

		public void setDomain( String domain )
		{
			this.domain = domain;
		}

		public String getUuid()
		{
			return uuid;
		}

		public void setUuid( String uuid )
		{
			this.uuid = uuid;
		}

		public boolean isManaged()
		{
			return isManaged;
		}

		public void setManaged( boolean isManaged )
		{
			this.isManaged = isManaged;
		}

		public boolean isImportfromvshpere() {
			return importfromvshpere;
		}

		public void setImportfromvshpere(boolean importfromvshpere) {
			this.importfromvshpere = importfromvshpere;
		}
		public int getHostId() {
			return hostId;
		}

		public void setHostId(int hostId) {
			this.hostId = hostId;
		}

		public VCMConverterType getVcmConverterType() {
			return vcmConverterType;
		}

		public void setVcmConverterType(VCMConverterType vcmConverterType) {
			this.vcmConverterType = vcmConverterType;
		}

		public String getAuthUuid() {
			return authUuid;
		}

		public void setAuthUuid(String authUuid) {
			this.authUuid = authUuid;
		}

		public GatewayId getGatewayId() {
			return gatewayId;
		}

		public void setGatewayId(GatewayId gatewayId) {
			this.gatewayId = gatewayId;
		}
	}

	//////////////////////////////////////////////////////////////////////////

	public String getPolicyContentXml( int policyId ) throws
		GetPolicyContentXmlException
	{
		try
		{
			EdgePolicy daoPolicy = getEdgePolicy(policyId, true);

			logger.trace( "getPolicyContentXml() ok." );

			return daoPolicy.getPolicyxml();
		}
		catch (Exception e)
		{
			logger.error( "getPolicyContentXml() failed.", e );
			throw new GetPolicyContentXmlException();
		}
	}

	//////////////////////////////////////////////////////////////////////////

	public D2DConnectInfo getD2DConnectInfo( int hostId ) throws
		GetD2DConnectInfoException
	{
		try
		{
			List<EdgeHost> hostList = new ArrayList<EdgeHost>();
			int isVisible = 1;
			this.hostMgrDao.as_edge_host_list( hostId, isVisible, hostList );
			EdgeHost hostInfo = hostList.get( 0 );

			List<EdgeConnectInfo> connectInfoList = new ArrayList<EdgeConnectInfo>();
			this.connectInfoDao.as_edge_connect_info_list( hostId, connectInfoList );
			EdgeConnectInfo connectInfo = connectInfoList.get( 0 );

			D2DConnectInfo d2dConnectInfo = new D2DConnectInfo();
			d2dConnectInfo.setHostName( hostInfo.getRhostname() );
			d2dConnectInfo.setPort( connectInfo.getPort() );
			d2dConnectInfo.setProtocol( connectInfo.getProtocol() == 1 ? "http" : "https" );
			d2dConnectInfo.setUuid( connectInfo.getUuid() );
			d2dConnectInfo.setManaged( connectInfo.getManaged() == 1 );
			d2dConnectInfo.setPassword( connectInfo.getPassword() );
			d2dConnectInfo.setUsername(connectInfo.getUsername());
			d2dConnectInfo.setHostId(hostId);
			d2dConnectInfo.setAuthUuid(connectInfo.getAuthUuid());
			logger.trace( "getD2DConnectInfo() ok." );
			
			GatewayEntity gateway = gatewayService.getGatewayByHostId(hostId);
			d2dConnectInfo.setGatewayId(gateway.getId());

			return d2dConnectInfo;
		}
		catch (Exception e)
		{
			logger.error( "getD2DConnectInfo() failed.", e );
			throw new GetD2DConnectInfoException();
		}
	}

	//////////////////////////////////////////////////////////////////////////

	private void checkManageStatus( D2DConnectInfo connectInfo ) throws
		NodeIsNotManagedException
	{
		if (!connectInfo.isManaged())
		{
			logger.trace( "Node is not managed by Edge." );
			throw new NodeIsNotManagedException();
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// To make the code easy to be understood, we defines constants for policy
	// type values on both Edge side and D2D side. In order to make the value
	// always matches and represents the same meaning, we use following function
	// to ensure a specific policy type value translate to correct corresponding
	// value of D2D side.

	private int ensurePolicyTypeMatches( int policyType )
	{
		switch (policyType)
		{
		case PolicyTypes.BackupAndArchiving:
			return ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving;

		case PolicyTypes.VCM:
			return ID2DPolicyManagementService.PolicyTypes.VCM;
			
		case PolicyTypes.RemoteVCM:
			return ID2DPolicyManagementService.PolicyTypes.RemoteVCM;

		case PolicyTypes.VMBackup:
			return ID2DPolicyManagementService.PolicyTypes.VMBackup;
		}

		String logMessage = String.format( "Unknown policy type: %d", policyType );
		logger.error( logMessage );

		return 0;
	}

	public void writeActivityLog(
		Severity severity, String nodeName, String message )
	{
		try
		{
			ActivityLog log = new ActivityLog();
			log.setJobId( 0 );
			log.setModule( Module.PolicyManagement );
			log.setSeverity( severity );
			log.setNodeName( nodeName );
			log.setMessage( message );
			log.setTime( new Date() );
			this.getActivityLogService().addLog( log );
		}
		catch (Exception e)
		{
			logger.error( "writeActivityLog(): Error writting activity log. (Node name: '" +
				nodeName + "', Message: '" + message + "')", e );
		}
	}
	
	//////////////////////////////////////////////////////////////////////////

	public void removePolicyFromNodeImmedately(
		int nodeId, int policyType, int flags ) throws Exception
	{
		this.removePolicyFromSingleNode( nodeId, policyType, flags );
		getPolicyDeploymentScheduler().doDeploymentNowByHostId(nodeId);
	}

	//////////////////////////////////////////////////////////////////////////
	
	private void removePolicyFromSingleNode(
		int nodeId, int policyType, int flags ) throws Exception
	{
		edgePolicyDao.unassignPolicy( nodeId, policyType, flags );
		
		List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
		edgePolicyDao.getHostPolicyMap( nodeId, policyType, mapList );
		
		//added for delete node
		if (mapList.size() >= 0 && policyType == PolicyTypes.Unified) {
			for (EdgeHostPolicyMap map : mapList) {
				if (map.getHostId() == nodeId) {
					UnifiedPolicy oldPlan = loadUnifiedPolicyById(map.getPolicyId());
					if (oldPlan == null) {
						continue;
					}
					oldPlan.setId(map.getPolicyId());
					//List<Integer> nodeList = oldPlan.getNodes();
					List<Integer> nodeList = new ArrayList<Integer>();
					for(ProtectedResourceIdentifier identifier: oldPlan.getProtectedResources()){
						if(ProtectedResourceType.node == identifier.getType()){
							nodeList.add(identifier.getId());
						}
					}
					
					if (nodeList != null && nodeList.size() > 0) {
						List<Integer> newNodeList = new ArrayList<Integer>();
						for (Integer id : nodeList) {
							if (id != nodeId) {
								newNodeList.add(id);
							}
						}
						if (!(newNodeList.containsAll(nodeList) && nodeList.containsAll(newNodeList))) {
							//oldPlan.setNodes(newNodeList);
							List<ProtectedResourceIdentifier> identifiers = new ArrayList<ProtectedResourceIdentifier>();
							for(int id : newNodeList){
								ProtectedResourceIdentifier identifier = new ProtectedResourceIdentifier();
								identifier.setId(id);
								identifier.setType(ProtectedResourceType.node);
								identifiers.add(identifier);
							}
							oldPlan.setProtectedResources(identifiers);
							saveUnifiedPolicy(oldPlan, false);
						}
					}
				}
			}
		}
		
		if(mapList.size()==0&&flags==PolicyDeployFlags.UnregisterNodeAfterUnassign){
			this.getNodeService().unregisterD2D( nodeId );
		}
		hostMgrDao.as_edge_host_deleteD2DStatusInfo(nodeId);
		edgePolicyDao.deletePolicyDeployWarningErrorMessage( nodeId, policyType );
	}
	
	//////////////////////////////////////////////////////////////////////////

	@Override
	public List<ItemOperationResult> redeployPolicyToNodes(
			List<Integer> nodeIdList,
			int policyType,
			int policyId // Policy Id is not used in the function, pass -1 means not use the policy id passed in
		) throws EdgeServiceFault
	{
		List<ItemOperationResult>
		resultList = new ArrayList<ItemOperationResult>();

		for (Integer nodeId : nodeIdList)
		{
			ItemOperationResult result = new ItemOperationResult();
			result.setItemId( nodeId );
			result.setResultCode( AssignPolicyResultCodes.Successful );
	
			try
			{
				redeployPolicyToOneNode( nodeId, policyType );
			}
			catch (Exception e)
			{
				logger.error( "redeployPolicyToNodes() failed.", e );
	
				result.setResultCode(
					AssignPolicyResultCodes.Failed_DBOperationError );
			}
			if(policyId == -1){
				getPolicyDeploymentScheduler().doDeploymentNowByHostId(nodeId);
			}else {
				getPolicyDeploymentScheduler().doDeploymentNowByHostIdAndPolicyId(nodeId, policyId);
			}
			resultList.add( result );
		}
		return resultList;
	}

	//////////////////////////////////////////////////////////////////////////

	private void redeployPolicyToOneNode(
		int nodeId, int policyType ) throws Exception
	{
		List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
		this.edgePolicyDao.getHostPolicyMap( nodeId, policyType, mapList );
		if (mapList.size() == 0) {
			// if node has no policy, then just return
			return;
		}
		EdgeHostPolicyMap map = mapList.get( 0 );
		int deployReason =
				(map.getDeployStatus() == PolicyDeployStatus.DeployedSuccessfully) ?
				PolicyDeployReasons.ReDeployManually : map.getDeployReason();
		redeployPolicyToOneNode(nodeId, policyType, deployReason);
	}
	
	private void redeployPolicyToOneNode(
			int nodeId, int policyType, int deployReason) throws Exception
		{
			boolean isInTransaction = false;
			boolean isTransactionCompleted = false;

			try
			{
				this.edgePolicyDao.beginTrans();
				isInTransaction = true;

				List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
				this.edgePolicyDao.getHostPolicyMap( nodeId, policyType, mapList );
				if (mapList.size() == 0) {
					// if node has no policy, then just return
					return;
				}
				EdgeHostPolicyMap map = mapList.get( 0 );

				map.setDeployStatus( PolicyDeployStatus.ToBeDeployed );
				map.setDeployReason( deployReason );
				map.setDeployFlags( 0 );
				map.setTryCount( 0 );
				
				try {
					checkRpsVersion(null,map.getPolicyId());
				} catch (EdgeServiceFault e) {
					logger.warn("redeployPolicyToOneNode:"+nodeId+" "+e.getFaultInfo().getMessage());
					edgePolicyDao.deletePolicyDeployWarningErrorMessage(nodeId,policyType);
					edgePolicyDao.setPolicyDeployWarningErrorMessage(nodeId, getPolicyTypeByApplicationType(), "", e.getFaultInfo().getMessage());
					return;
				}

				this.edgePolicyDao.setHostPolicyMap( nodeId, policyType,
					map.getPolicyId(), map.getDeployStatus(), map.getDeployReason(),
					map.getDeployFlags(), map.getTryCount(), map.getLastSuccDeploy() );
				edgePolicyDao.as_edge_policy_updateStatus(map.getPolicyId(), PlanStatus.Deploying);
				edgePolicyDao.deletePolicyDeployWarningErrorMessage(nodeId,policyType);
				isTransactionCompleted = true;
			}
			catch (Exception e)
			{
				logger.error( "redeployPolicyToOneNode() failed.", e );
				throw e;
			}
			finally
			{
				if (isInTransaction)
				{
					if (isTransactionCompleted)
						this.edgePolicyDao.commitTrans();
					else // not completed
						this.edgePolicyDao.rollbackTrans();
				}
			}
		}

	public boolean checkRpsVersion(UnifiedPolicy plan, int planId) throws EdgeServiceFault {
		if(plan == null)
			plan = loadUnifiedPolicyById(planId);
		int rpsPolicyCount = plan.getRpsPolices().size();
		if (rpsPolicyCount == 0) {
			return true;
		}
		for (int i = rpsPolicyCount - 2; i >= 0; --i) {
			RPSPolicyWrapper currentRpsPolicyWrapper = plan.getRpsPolices().get(i);
			RPSPolicy currentRpsPolicy = currentRpsPolicyWrapper.getRpsPolicy();
			int hostid=currentRpsPolicy.getRpsSettings().getRpsReplicationSettings().getHostId();
			String hostname=currentRpsPolicy.getRpsSettings().getRpsReplicationSettings().getHostName();
			
			if(EdgeCommonUtil.compareWithConsoleVersion(hostid)<0){
				throw getRpsVersionLowerError(hostname);
			}
		}
		if(plan.getBackupConfiguration()!=null){
			if(!plan.getBackupConfiguration().isD2dOrRPSDestType()){
				int hostid=plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
				String hostname=plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				if(EdgeCommonUtil.compareWithConsoleVersion(hostid)<0){
					throw getRpsVersionLowerError(hostname);
				}
			}
		}
		if(plan.getVSphereBackupConfiguration()!=null){
			if(!plan.getVSphereBackupConfiguration().isD2dOrRPSDestType()){
				int hostid=plan.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
				String hostname=plan.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				if(EdgeCommonUtil.compareWithConsoleVersion(hostid)<0){
					throw getRpsVersionLowerError(hostname);
				}
			}
		}
		return true;
	}

	private EdgeServiceFault getRpsVersionLowerError(String destinationRpsHostName){
		logger.debug("The version of destination RPS "+destinationRpsHostName+" is lower than current console, this is not allowed.");
		if(destinationRpsHostName == null)
			destinationRpsHostName = "";
		String[] parameters = new String[]{destinationRpsHostName ,edgeExternalLinks.rpsUpgrade()};
		EdgeServiceFault targetVersionSmallerFault = EdgeServiceFault.
				getFault(EdgeServiceErrorCode.policyManagement_Backup_TargetRps_SmallerThan_SourceNode,
				parameters, "Destination Rps version is lower than current consle.");
		String errorMessage = WebServiceFaultMessageRetriever.
				getErrorMessage( DataFormatUtil.getServerLocale(),(targetVersionSmallerFault.getFaultInfo()));
		targetVersionSmallerFault.getFaultInfo().setMessage(errorMessage);
		return targetVersionSmallerFault;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public void redeployPolicies( int policyType, List<Integer> policyIdList )
		throws EdgeServiceFault
	{
		PlanRedeployService.getInstance().redeploy(policyIdList);
	}

	//////////////////////////////////////////////////////////////////////////
	
	public PolicyDeploymentScheduler getPolicyDeploymentScheduler()
	{
		if (policyDeploymentScheduler == null)
			policyDeploymentScheduler = PolicyDeploymentScheduler.getInstance();
		
		return policyDeploymentScheduler;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void doPolicyDeploymentNow(
		) throws EdgeServiceFault
	{
		logger.info( "PolicyManagementServiceImpl.doPolicyDeploymentNow(): Launch policy deployment immediately." );
		getPolicyDeploymentScheduler().doDeploymentNow();
	}


	//////////////////////////////////////////////////////////////////////////

	private void addDynamicGroups( List<NodeGroup> groupList )
	{
		NodeGroup nodeGroup;

		nodeGroup = new NodeGroup();
		nodeGroup.setId( NodeGroup.UNGROUP );
		nodeGroup.setName( "" );
		nodeGroup.setComments( "" );
		groupList.add( nodeGroup );

		nodeGroup = new NodeGroup();
		nodeGroup.setId( NodeGroup.SQLSERVER );
		nodeGroup.setName( "" );
		nodeGroup.setComments( "" );
		groupList.add( nodeGroup );

		nodeGroup = new NodeGroup();
		nodeGroup.setId( NodeGroup.EXCHANGE );
		nodeGroup.setName( "" );
		nodeGroup.setComments( "" );
		groupList.add( nodeGroup );

		// disable D2DOD - lijwe02 >>
		/*nodeGroup = new NodeGroup();
		nodeGroup.setId( NodeGroup.D2D );
		nodeGroup.setName( "" );
		nodeGroup.setComments( "" );
		groupList.add( nodeGroup );

		nodeGroup = new NodeGroup();
		nodeGroup.setId( NodeGroup.D2DOD );
		nodeGroup.setName( "" );
		nodeGroup.setComments( "" );
		groupList.add( nodeGroup );*/
		// disable D2DOD - lijwe02 <<
	}

	private boolean canNodeBeAssignedWithPolicy(
		int hostId, int hostType, int hostAppStatus, VSphereProxyInfo proxyInfo,
		EdgePolicy policy )
	{
		if (EdgeWebServiceContext.getApplicationType() ==
			EdgeApplicationType.VirtualConversionManager)
		{
			if (HostTypeUtil.isVMImportFromVSphere( hostType ) && !HostTypeUtil.isNodeImportFromRPSReplica( hostType ))
			{// local HBBU node
				if (proxyInfo == null)
					return false;
			}
			else // added manually
			{
				if ((ApplicationUtil.isD2DODInstalled(hostAppStatus)||ApplicationUtil.isD2DInstalled( hostAppStatus ))
						&&!isNodeManaged( hostId )){
					return false;
				}
			}
			
			if (!(HostTypeUtil.isNodeImportFromRHA( hostType ) || HostTypeUtil.isNodeImportFromRPSReplica( hostType )) 
					&& (policy.getType() != PolicyTypes.VCM)) // local node & local policy 
				return false;
			
			if ((HostTypeUtil.isNodeImportFromRHA( hostType ) || HostTypeUtil.isNodeImportFromRPSReplica( hostType )) 
					&& (policy.getType() != PolicyTypes.RemoteVCM)) // remote node & remote policy
				return false;
				
		}
		else if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.CentralManagement)
		{
			if ((ApplicationUtil.isD2DODInstalled(hostAppStatus)||ApplicationUtil.isD2DInstalled( hostAppStatus ))
					&& !isNodeVersionLow(hostId) &&!isNodeManaged( hostId ))
				return false;
			
			if (policy.getProducttype()==ProductType.D2DOD && ApplicationUtil.isD2DInstalled(hostAppStatus)) {
				return false;
			} 
			
			if(policy.getProducttype()==ProductType.D2D && ApplicationUtil.isD2DODInstalled(hostAppStatus)){
				return false;
			}
		}
		
		return true;
	}

	private boolean isNodeVersionLow(int nodeId){
		try
		{
			List<EdgeConnectInfo> connectInfoList = new LinkedList<EdgeConnectInfo>();
			this.connectInfoDao.as_edge_connect_info_list( nodeId, connectInfoList );
			EdgeConnectInfo connectInfo = connectInfoList.get( 0 );

			double d2dVersion = 0;
			try{
	    		d2dVersion = Double.parseDouble(connectInfo.getMajorversion()+"."+connectInfo.getMinorversion());
	    	}catch (Exception e){
	    		logger.error(e);
	    		return false;
	    	}
			
			return d2dVersion!=ImportNodesJob.REQUIRED_D2D_VERSION;
		}
		catch (Exception e)
		{
			logger.error( "isNodeManaged()", e );
			return false;
		}
	}

	private boolean isNodeManaged( int nodeId )
	{
		try
		{
			List<EdgeConnectInfo> connectInfoList = new LinkedList<EdgeConnectInfo>();
			this.connectInfoDao.as_edge_connect_info_list( nodeId, connectInfoList );
			EdgeConnectInfo connectInfo = connectInfoList.get( 0 );

			return (NodeManagedStatus.parseInt( connectInfo.getManaged() ) == NodeManagedStatus.Managed);
		}
		catch (Exception e)
		{
			logger.error( "isNodeManaged()", e );
			return false;
		}
	}

	public NodeInfoList4VM getNodesWhoIsUsingPolicy4VM( int policyId )
		throws EdgeServiceFault
	{
		NodeInfoList4VM nodeInfoList = new NodeInfoList4VM();

		// get node groups

		List<NodeGroup> groupList = this.nodeManFacade.getNodeGroups();
		addDynamicGroups( groupList );
		nodeInfoList.setGroupList( groupList );


		 List<EdgeEsxVmInfo> esxHostList = new ArrayList<EdgeEsxVmInfo>();

		// get nodes

		List<EdgePolicyHost> hostList = new ArrayList<EdgePolicyHost>();
		this.edgePolicyDao.getNodesWhoIsUsingPolicy( policyId, hostList );

		List<NodeInfo> nodeList = new ArrayList<NodeInfo>();
		for (EdgePolicyHost daoHost : hostList)
		{
			NodeInfo nodeInfo = new NodeInfo();
			nodeInfo.setNodeId( daoHost.getHostId() );
			nodeInfo.setNodeName( daoHost.getHostName() );
			nodeInfo.setPolicyId( policyId );
			nodeInfo.setPolicyName( daoHost.getPolicyName() );
			nodeInfo.setDeployStatus( daoHost.getDeployStatus() );
			nodeInfo.setTryCount( daoHost.getTryCount() );
			nodeList.add( nodeInfo );

			List<EdgeHost> detailedHostList = new ArrayList<EdgeHost>();
			int isVisible = 1;
			this.hostMgrDao.as_edge_host_list(
				daoHost.getHostId(), isVisible, detailedHostList );
			EdgeHost edgeHost = detailedHostList.get( 0 );
			if (edgeHost != null)
			{
				nodeInfo.setDetailedNodeInfo(
					this.nodeManFacade.convertDaoNode2ContractNode( edgeHost ) );
			}

			List<EdgeIntegerValue> intValueList = new ArrayList<EdgeIntegerValue>();
			this.hostMgrDao.as_edge_host_get_node_groups( daoHost.getHostId(), intValueList );

			List<Integer> groupIdList = new ArrayList<Integer>();
			for (EdgeIntegerValue intValue : intValueList)
				groupIdList.add( intValue.getValue() );

			if (ApplicationUtil.isExchangeInstalled( daoHost.getAppStatus() ))
				groupIdList.add( NodeGroup.EXCHANGE );

			if (ApplicationUtil.isSQLInstalled( daoHost.getAppStatus() ))
				groupIdList.add( NodeGroup.SQLSERVER );

			// disable D2DOD - lijwe02 >>
			/*if (ApplicationUtil.isD2DInstalled(daoHost.getAppStatus())){
				groupIdList.add(NodeGroup.D2D);
			}

			if (ApplicationUtil.isD2DODInstalled(daoHost.getAppStatus())){
				groupIdList.add(NodeGroup.D2DOD);
			}*/
			// disable D2DOD - lijwe02 <<
			
			if (groupIdList.size() == 0)
				groupIdList.add( NodeGroup.UNGROUP );

			List<EdgeEsxVmInfo>  hostMapInfo = new ArrayList<EdgeEsxVmInfo>();
			edgeEsxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(daoHost.getHostId(), hostMapInfo);
			nodeInfo.setGroupIdList( groupIdList );
			if(!hostMapInfo.isEmpty()){
				esxHostList.add(hostMapInfo.get(0));
			}else {
				logger.error("[PolicyManagementServiceImpl] getNodesWhoIsUsingPolicy4VM() can not find the vminfo for node: "+daoHost.getHostId());
			}
		}
		nodeInfoList.setNodeList( nodeList );
		return nodeInfoList;
	}
	
	public void regEdgeToProxy(int policyType, int policyId, int hostId, boolean forceFlag) throws EdgeServiceFault{
		try{
			ProxyConnectInfo proxyConnectInfo = null;
			switch(policyType){
			case PolicyTypes.BackupAndArchiving:
				this.d2dRegService.UpdateRegInfoToD2D(null,hostId, forceFlag);
				return;
			case PolicyTypes.VCM:
				EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo( hostId );
				if (HostTypeUtil.isVMImportFromVSphere( hostInfo.getRhostType() ))
				{
					VSphereProxyInfo proxyInfo = getVCMService().getVSphereProxyInfoByHostId( hostId );
					if (proxyInfo != null)
					{
						GatewayEntity gateway = this.gatewayService.getGatewayByEntityId(
							proxyInfo.getvSphereProxyId(), EntityType.VMBackupProxy );
						proxyConnectInfo = new ProxyConnectInfo(gateway.getId(),
								proxyInfo.getVSphereProxyName(),proxyInfo.getvSphereProxyPort(),
								proxyInfo.getVSphereProxyProtocol() == Protocol.Https ? "https" : "http",
								EdgeCommonUtil.getUserName(proxyInfo.getVSphereProxyUsername()),proxyInfo.getVSphereProxyPassword(),
								EdgeCommonUtil.getDomainName(proxyInfo.getVSphereProxyUsername()),proxyInfo.getVSphereProxyUuid());
					}
				}else{
					this.d2dRegService.UpdateRegInfoToD2D(null,hostId, forceFlag);
					return;
				}
				break;
			case PolicyTypes.VMBackup:
				String policyXml = this.getPolicyContentXml( policyId );
		
				VMBackupConfiguration vMBackupConfiguration = policyUtil.getVMBackupConfiguration(policyXml, PolicyXmlSectionNames.VMBackupSettings);
		
				D2DConnectInfo d2dConnectInfo = new D2DConnectInfo();
				d2dConnectInfo.setHostName( vMBackupConfiguration.getVSphereProxy().getVSphereProxyName() );
				d2dConnectInfo.setPort( vMBackupConfiguration.getVSphereProxy().getVSphereProxyPort());
				d2dConnectInfo.setProtocol( vMBackupConfiguration.getVSphereProxy().getVSphereProxyProtocol() );
				d2dConnectInfo.setUsername(EdgeCommonUtil.getUserName(vMBackupConfiguration.getVSphereProxy().getVSphereProxyUsername() ));
				d2dConnectInfo.setDomain(EdgeCommonUtil.getDomainName(vMBackupConfiguration.getVSphereProxy().getVSphereProxyUsername() ));
				d2dConnectInfo.setPassword( vMBackupConfiguration.getVSphereProxy().getVSphereProxyPassword() );
				d2dConnectInfo.setUuid( null );
				d2dConnectInfo.setManaged( true );
				
				GatewayEntity gateway = this.gatewayService.getGatewayByHostId(
					vMBackupConfiguration.getVSphereProxy().getVSphereProxyHostID() );
				proxyConnectInfo = new ProxyConnectInfo(gateway.getId(), 
					d2dConnectInfo.getHostName(),d2dConnectInfo.getPort(),d2dConnectInfo.getProtocol(),
					d2dConnectInfo.getUsername(),d2dConnectInfo.getPassword(),d2dConnectInfo.getDomain(),d2dConnectInfo.getUuid());
				break;
			default:
				throw new RuntimeException("Should not run to here");
			}

		// Regist proxy
		this.d2dRegService.UpdateRegInfoToProxy(proxyConnectInfo, forceFlag);
		}catch(EdgeServiceFault e){
			throw e;
		} catch (GetPolicyContentXmlException e) {
			throw new EdgeServiceFault(e.getMessage(),
					new EdgeServiceFaultBean(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, e.getMessage()));
		} catch (Exception e) {
			throw new EdgeServiceFault(e.getMessage(),
					new EdgeServiceFaultBean(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, e.getMessage()));
		}
	}
	
	public String getPolicyUuid(int policyId) throws GetPolicyUuidException {
		try
		{
			EdgePolicy daoPolicy = getEdgePolicy(policyId, false);

			logger.trace( "getPolicyUuid() ok." );

			return daoPolicy.getUuid();
		}
		catch (Exception e)
		{
			logger.error( "getPolicyUuid() failed.", e );
			throw new GetPolicyUuidException();
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public IEdgeD2DRegService getRegService(){
		return  new EdgeD2DRegServiceImpl();
	}
	
	public List<PolicyDeploymentError> deployPolicyVM(
		int hostId, int policyType, int policyId, List<Integer> vmIds, UnifiedPolicy plan ) throws
		GetPolicyContentXmlException,
		GetD2DConnectInfoException,
		NodeIsNotManagedException,
		EdgeServiceFault,
		Exception
	{
		logger.info( "[PolicyManagementServiceImpl]deployPolicyVM(): Begin to deploy policy: "+plan.getName()+" plan id is:"+policyId );
		
		String policyXml = null;
		boolean isPlan4HBBU = false;
		if (plan != null) {
			VSphereBackupConfiguration vSphereConf = plan.getVSphereBackupConfiguration();
			if (vSphereConf != null) {
				policyType = ID2DPolicyManagementService.PolicyTypes.VMBackup;
				PreferencesConfiguration preferencesConf = plan.getPreferencesConfiguration();
				if (preferencesConf != null) {
					BackupEmail preferEmail = preferencesConf.getEmailAlerts();
					vSphereConf.setEmail(preferEmail);
					if (preferEmail != null) {
						vSphereConf.setEnableSpaceNotification(preferEmail.isEnableSpaceNotification());
						vSphereConf.setSpaceMeasureNum(preferEmail.getSpaceMeasureNum());
						vSphereConf.setSpaceMeasureUnit(preferEmail.getSpaceMeasureUnit());
					}
				}
				isPlan4HBBU = true;
			}
		}

		if (isPlan4HBBU) {
			policyXml = marshalLegacyPolicy(plan);
		} else {
			policyType = ensurePolicyTypeMatches( policyType );
			policyXml = this.getPolicyContentXml( policyId );
		}
		String policyUuid = getPolicyUuid(policyId);

		VMBackupConfiguration vMBackupConfiguration = policyUtil.getVMBackupConfiguration(policyXml, PolicyXmlSectionNames.VMBackupSettings);

		D2DConnectInfo d2dConnectInfo = new D2DConnectInfo();
		d2dConnectInfo.setHostName( vMBackupConfiguration.getVSphereProxy().getVSphereProxyName() );
		d2dConnectInfo.setPort( vMBackupConfiguration.getVSphereProxy().getVSphereProxyPort());
		d2dConnectInfo.setProtocol( vMBackupConfiguration.getVSphereProxy().getVSphereProxyProtocol() );
		d2dConnectInfo.setUsername( EdgeCommonUtil.getUserName(vMBackupConfiguration.getVSphereProxy().getVSphereProxyUsername() ));
		d2dConnectInfo.setDomain( EdgeCommonUtil.getDomainName(vMBackupConfiguration.getVSphereProxy().getVSphereProxyUsername() ));
		d2dConnectInfo.setPassword( vMBackupConfiguration.getVSphereProxy().getVSphereProxyPassword() );
		d2dConnectInfo.setUuid( null );
		d2dConnectInfo.setManaged( true );
		
		if(vMBackupConfiguration.getAdminUserName()==null || vMBackupConfiguration.getAdminUserName().isEmpty()){
			if(d2dConnectInfo.getDomain()!=null && !d2dConnectInfo.getDomain().isEmpty())
				vMBackupConfiguration.setAdminUserName(d2dConnectInfo.getDomain()+"\\"+d2dConnectInfo.getUsername());
			else
				vMBackupConfiguration.setAdminUserName(d2dConnectInfo.getUsername());
			vMBackupConfiguration.setAdminPassword(d2dConnectInfo.getPassword());
		}
		
		ConnectionContext context = new ConnectionContext(d2dConnectInfo.getProtocol(), d2dConnectInfo.getHostName(), d2dConnectInfo.getPort());
		context.buildCredential(d2dConnectInfo.getUsername(), d2dConnectInfo.getPassword(), d2dConnectInfo.getDomain());
		VSphereProxy proxy = plan.getVSphereBackupConfiguration().getvSphereProxy();
		GatewayEntity gateway = gatewayService.getGatewayByHostId( proxy.getVSphereProxyHostID() );
		context.setGateway(gateway);
		
		String proxyNodeUuid;
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			proxyNodeUuid = connection.getNodeUuid();
//			policyUtil.checkD2DVersion(connection.getService());
		}

		// Regist proxy
		boolean forceFlag = false;
		IEdgeD2DRegService regService = getRegService();
		ProxyConnectInfo proxyConnectInfo = new ProxyConnectInfo(gateway.getId(), 
				d2dConnectInfo.getHostName(),d2dConnectInfo.getPort(),d2dConnectInfo.getProtocol(),
				d2dConnectInfo.getUsername(),d2dConnectInfo.getPassword(),d2dConnectInfo.getDomain(),d2dConnectInfo.getUuid());
		regService.UpdateRegInfoToProxy(proxyConnectInfo, forceFlag);
		
		Map<Integer, String> vmRunningJob = getJobService().checkRunningVMJobForHbbuPlan(vmIds, proxy.getVSphereProxyHostID());

		int backupVMSize = vmIds.size();
		if(vmRunningJob != null && !vmRunningJob.isEmpty()){
			logger.info("[PolicyManagementServiceImpl]deployPolicyVM(): exist running backupjob vm!" );
			for(Integer vmId: vmIds)
			{
				if(vmRunningJob.containsKey(vmId)){// backup job is running, cannnot to deploy
					logger.info("[PolicyManagementServiceImpl]deployPolicyVM(): exist running backupjob vm, vmId : " +  vmId);
					backupVMSize--;
				}
			}
		}
		BackupVM[] backupvmList = new BackupVM[backupVMSize];
		Map<Integer, BackupVM> vmMap = new HashMap<Integer, BackupVM>();
		
		int i = 0;
		for(Integer vmId: vmIds)
		{
			if(vmRunningJob != null && vmRunningJob.containsKey(vmId)){// backup job is running, cannnot to deploy
				continue;
			}
			BackupVM backupVM = this.constructBackupVm(vmId, vMBackupConfiguration);
			if(backupVM == null)
				continue;
			backupvmList[i++] = backupVM;
			vmMap.put(vmId, backupVM);
		}
		checkManageStatus( d2dConnectInfo );
		
		VSphereBackupConfiguration vc = plan.getVSphereBackupConfiguration();
		vc.setBackupVMList(backupvmList);
		/*if(plan.getArchiveToTapeSettings() != null){
			vc.setArchiveConfig(plan.getArchiveToTapeSettings().getArchiveConfig());
		}*/
		if(plan.getArchiveToTapeSettingsWrapperList() != null && !plan.getArchiveToTapeSettingsWrapperList().isEmpty()){
			for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : plan.getArchiveToTapeSettingsWrapperList()){
				if(archiveToTapeWrapper.getArchiveToTapeSourceType() == ArchiveToTapeSourceType.BackUp){
					vc.setArchiveConfig(archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveConfig());
					break;
				}
			}
		}
		
		for (Map.Entry<Integer, BackupVM> entry : vmMap.entrySet()) {
			PolicyDeployingCache.getInstance().cache(proxyNodeUuid, entry.getKey(), entry.getValue().getInstanceUUID());
		}
		
		List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();
		
		try
		{
			if(CollectionUtils.isNotEmpty(plan.getProtectedResources()) && backupVMSize > 0){// add the condition that backupVMSize > 0, issue 763908 & 764678
				List<AuthUuidWrapper> wrappers = new ArrayList<AuthUuidWrapper>();
				List<EdgeHost> hostList = new LinkedList<EdgeHost>();
				IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
				hostMgrDao.as_edge_host_list(plan.getVSphereBackupConfiguration().getvSphereProxy().getVSphereProxyHostID(), 1, hostList);
				if(CollectionUtils.isNotEmpty(hostList)){
					connectionInfoDao.as_edge_connect_info_getAuthUuid(hostList.get(0).getD2DUUID(), wrappers);
					if (!wrappers.isEmpty()) {
						if(logger.isDebugEnabled()){
							logger.debug("connect d2d successful");
							if(vc.getArchiveConfig() != null && vc.getArchiveConfig().getSource() != null && CollectionUtils.isNotEmpty(vc.getArchiveConfig().getSource().getSourceItems())){
								for(ArchiveSourceItem item : vc.getArchiveConfig().getSource().getSourceItems()){
									logger.debug("archive ArchiveSourceItem ConfigTime is " + item.getConfigTime());
									logger.debug("archive ArchiveSourceItem DailyItem is backup type is " + item.getDailyItem()!= null ? item.getDailyItem().getBkpType() : "null");
									logger.debug("archive ArchiveSourceItem DailyItem is start time is " + item.getDailyItem()!= null ? item.getDailyItem().getStartTime() : "null");
								}
							}
						}
						
						Map<String, String> policyUuids = new HashMap<String, String>();
						for (BackupVM vm : backupvmList) {
							if(vm == null){// issue 765063
								continue;
							}
							policyUuids.put(vm.getInstanceUUID(), policyUuid);
						}
						
						context.buildAuthUuid(wrappers.get(0).getAuthUuid());
						
						SavePolicyWarning[] savePolicyWarnings;
						
						try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
							connection.connect();
							// Feb sprint
							// Get StorageAppliance details from DB
							List<StorageApplianceInfo> infrastructureList = new ArrayList<StorageApplianceInfo>();
							List<StorageAppliance> storageApplianceList = new ArrayList<StorageAppliance>();
							infraDao.as_edge_infrastructure_getInfrastructureList(infrastructureList);
							
							for (StorageApplianceInfo s : infrastructureList)
							{
								GatewayEntity gatewayEntity = null;
								try {
									gatewayEntity = gatewayService.getGatewayByEntityId(s.getId(), EntityType.StorageArray);
								} catch (EdgeServiceFault e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(gatewayEntity.getId().getRecordId() == gateway.getId().getRecordId()) // if gateway of SA and proxy are same the save xml
								{
									StorageAppliance objStorageAppliance = new StorageAppliance();
									objStorageAppliance.setServerName(s.getHostname());
									objStorageAppliance.setDataIP(s.getDataIp());
									objStorageAppliance.setUsername(s.getUsername());
									objStorageAppliance.setPassword(s.getPassword());
									objStorageAppliance.setProtocol(s.getProtocol().toString());
									objStorageAppliance.setPort(s.getPort()+"");
									objStorageAppliance.setSystemMode(s.getMode().toString());
									storageApplianceList.add(objStorageAppliance);
								}
							}
							connection.getService().saveStorageAppliance(storageApplianceList);
							HBBUConfiguration conf = new HBBUConfiguration();
							conf.setVSphereBackupConfiguration(vc);
							conf.setScheduledExportConfiguration(plan.getExportConfiguration());
							conf.setPlanUUID(plan.getUuid());
							logger.info( "[PolicyManagementServiceImpl]deployPolicyVM(): Invoke proxy API to save plan: "+plan.getName()+" plan id is:"+policyId );
							savePolicyWarnings = connection.getService().saveHBBUConfiguration(conf);
						}
						
						if (savePolicyWarnings != null) {
							enumerateWarningErros(savePolicyWarnings, policyUuids, errorList, ID2DPolicyManagementService.SettingsTypes.VMBackupSettings);
						}
					}
				}
			}
			this.addErrorForRunningJobVM(vmRunningJob, vMBackupConfiguration, plan.getName(), errorList, ID2DPolicyManagementService.SettingsTypes.VMBackupSettings);
		}
		catch (Exception e)
		{
			logger.error( "[PolicyManagementServiceImpl]deployPolicyVM() failed, the plan is: "+plan.getName()+" plan id is:"+policyId,e );
			for (Map.Entry<Integer, BackupVM> entry : vmMap.entrySet()) {
				PolicyDeployingCache.getInstance().clear(proxyNodeUuid, entry.getKey());
			}
			
			throw e;
		}
		logger.info( "[PolicyManagementServiceImpl]deployPolicyVM() Deploying policy "+plan.getName()+" finished. the plan id is: "+policyId +" the error count is:"+ errorList.size() );
		return errorList;
	}
	
	private BackupVM constructBackupVm(int vmId, VMBackupConfiguration vMBackupConfiguration){
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		this.hostMgrDao.as_edge_host_list(vmId, 1, hosts);
		if(hosts.size() == 0)
			return null;
		BackupVM backupVM = new BackupVM();
		backupVM.setDesPassword(vMBackupConfiguration.getPassword());
		backupVM.setDestination(vMBackupConfiguration.getDestination());
		backupVM.setDesUsername(vMBackupConfiguration.getUserName());
		String protocol = "Http";
		if(HostTypeUtil.isVapp(hosts.get(0).getRhostType())){
			
			List<EdgeEsx> vappEntities = new ArrayList<EdgeEsx>();
			edgeEsxDao.as_edge_esx_getEntityByHostId(vmId, vappEntities);
			List<EdgeEsx> vclouds = new ArrayList<EdgeEsx>();
			edgeEsxDao.as_edge_esx_getHypervisorByHostId(vmId,vclouds);
			if(vappEntities.isEmpty() || vclouds.isEmpty()){
				logger.warn("Can't apply the plan to vapp: "+hosts.get(0).getVmname()+" because the information is incomplete.");
				return null;
			}
			
			EdgeEsx vapp = vappEntities.get(0);
			EdgeEsx vcloud = vclouds.get(0);
			
			backupVM.setEsxPassword(vcloud.getPassword());
			backupVM.setEsxServerName(vcloud.getHostname());
			backupVM.setEsxUsername(vcloud.getUsername());
			backupVM.setInstanceUUID(vapp.getUuid());
			backupVM.setPassword(vapp.getPassword()); //If need use the password of connect info, please change this
			backupVM.setPort(vcloud.getPort());
			if(vcloud.getProtocol() == 2)
				protocol = "Https";
			backupVM.setProtocol(protocol);
			backupVM.setUsername(vapp.getUsername());//If need use the user name of connect info, please change this
			backupVM.setUuid(vapp.getUuid());
			backupVM.setVmHostName("");
			backupVM.setVmName(vapp.getHostname());
			backupVM.setVmVMX("");
			backupVM.setVmType(BackupVM.Type.VMware_VApp.ordinal());

			List<EdgeEsx> vCenters = new ArrayList<EdgeEsx>();
			this.edgeEsxDao.as_edge_vsphere_entity_map_getVcentersByvApp(vapp.getId(),vCenters);
			if(vCenters.isEmpty()){
				logger.warn("Can't apply the plan to vapp: "+hosts.get(0).getVmname()+" because the vcenter information is incomplete.");
				return null;
			}
			
			VirtualCenter[] vcArray = new VirtualCenter[1];
			EdgeEsx vcenter = vCenters.get(0);
			VirtualCenter vc = new VirtualCenter();
			vc.setPassword(vcenter.getPassword());
			vc.setPort(vcenter.getPort());
			vc.setProtocol((vcenter.getProtocol()==2?"Https":"http"));
			vc.setUsername(vcenter.getUsername());
			vc.setVcName(vcenter.getHostname());
			vcArray[0] = vc;
			backupVM.setVAppVCInfos(vcArray);
			
			//find member VM
			List<EdgeEsxHostMapInfo> vmList = new ArrayList<EdgeEsxHostMapInfo>();
			edgeEsxDao.as_edge_vsphere_vm_detail_getVMByvApp(vapp.getId(), vmList);
			int memiIndex = 0;
			BackupVM[] memberVMs= new BackupVM[vmList.size()];
			for(EdgeEsxHostMapInfo vmInfo : vmList){
				BackupVM memberVM = new BackupVM();
				memberVM.setDesPassword(vMBackupConfiguration.getPassword());
				memberVM.setDestination(vMBackupConfiguration.getDestination());
				memberVM.setDesUsername(vMBackupConfiguration.getUserName());
				memberVM.setEsxPassword(vc.getPassword());
				memberVM.setEsxServerName(vc.getVcName());
				memberVM.setEsxUsername(vc.getUsername());
				memberVM.setInstanceUUID(vmInfo.getVmInstanceUuid());
				memberVM.setPort(vc.getPort());
				memberVM.setProtocol(vc.getProtocol());
				memberVM.setUuid(vmInfo.getVmUuid());
				memberVM.setVmHostName(getHostnameById(vmInfo.getHostId()));
				memberVM.setVmName(vmInfo.getVmName());
				memberVM.setVmVMX(vmInfo.getVmXPath());
				memberVM.setGroupInstanceUUID(vapp.getUuid());
				memberVM.setVmType(BackupVM.Type.VMware.ordinal());
				memberVMs[memiIndex++]=memberVM;
			}
			backupVM.setVAppMemberVMs(memberVMs);			
		}
		else if (HostTypeUtil.isVMWareVirtualMachine(hosts.get(0).getRhostType())){
			List<EdgeEsxVmInfo> vmEntities = new ArrayList<EdgeEsxVmInfo>();
			edgeEsxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(vmId, vmEntities);
			List<EdgeEsx> esxServers = new ArrayList<EdgeEsx>();
			edgeEsxDao.as_edge_esx_getHypervisorByHostId(vmId,esxServers);
			if(vmEntities.isEmpty() || esxServers.isEmpty()){
				logger.warn("Can't apply the plan to vm: "+hosts.get(0).getVmname()+" because the information is incomplete.");
				return null;
			}
			EdgeEsx edgeEsx = esxServers.get(0);
			EdgeEsxVmInfo vm = vmEntities.get(0);
			
			backupVM.setEsxPassword(edgeEsx.getPassword());
			backupVM.setEsxServerName(edgeEsx.getHostname());
			backupVM.setEsxUsername(edgeEsx.getUsername());
			backupVM.setInstanceUUID(vm.getVmInstanceUuid());
			backupVM.setPassword(vm.getPassword());
			backupVM.setPort(edgeEsx.getPort());
			if(edgeEsx.getProtocol() == 2)
				protocol = "Https";
			backupVM.setProtocol(protocol);
			backupVM.setUsername(vm.getUserName());
			backupVM.setUuid(vm.getVmUuid());
			backupVM.setVmHostName(getHostnameById(vmId));
			backupVM.setVmName(vm.getVmName());
			backupVM.setVmVMX(vm.getVmXPath());
			backupVM.setVmType(BackupVM.Type.VMware.ordinal());
		}else{
			EdgeHyperVHostMapInfo aHostMap = new EdgeHyperVHostMapInfo();
			EdgeHyperV edgeHyperV = new EdgeHyperV();
			List <EdgeHyperVHostMapInfo>  hostMapInfo = new ArrayList<EdgeHyperVHostMapInfo>();
			this.edgeHypervDao.as_edge_hyperv_host_map_getById(vmId, hostMapInfo);
			if(hostMapInfo!=null&&hostMapInfo.size()>0)
			 {
				aHostMap =  hostMapInfo.get(0);
				List<EdgeHyperV> hyperVList = new ArrayList<EdgeHyperV>();
				this.edgeHypervDao.as_edge_hyperv_getById(aHostMap.getHyperVId(),hyperVList);
			    if(hyperVList!=null&&hyperVList.size()>0)
			    {
			    	edgeHyperV = hyperVList.get(0);
			    }
			 }
			
			backupVM.setEsxPassword(edgeHyperV.getPassword());
			backupVM.setEsxServerName(edgeHyperV.getHostname());
			backupVM.setEsxUsername(edgeHyperV.getUsername());
			backupVM.setInstanceUUID(aHostMap.getVmInstanceUuid());
			backupVM.setPassword(aHostMap.getPassword());
			backupVM.setPort(edgeHyperV.getPort());
			if(edgeHyperV.getProtocol() == 2)
				protocol = "Https";
			backupVM.setProtocol(protocol);
			backupVM.setUsername(aHostMap.getUserName());
			backupVM.setUuid(aHostMap.getVmUuid());
			backupVM.setVmHostName(getHostnameById(vmId));
			backupVM.setVmName(aHostMap.getVmName());
			if(HostTypeUtil.isHyperVClusterVM(hosts.get(0).getRhostType()))
				backupVM.setVmType(BackupVM.Type.HyperV_Cluster.ordinal());
			else
				backupVM.setVmType(BackupVM.Type.HyperV.ordinal());
		}
		return backupVM;
	}
	
	private void enumerateWarningErros(SavePolicyWarning[] warnings, Map<String, String> policyUuids, List<PolicyDeploymentError> errorList, int type) {
		for (SavePolicyWarning warning : warnings) {
			BackupVM vm = warning.getVm();
			if (vm == null)
				continue;
			if (warning.getType() == Constants.AFRES_AFALOG_WARNING) {
				addWarning(
						warning.getVm().getInstanceUUID(),
						warning.getWarningCode(),
						warning.getWarningMessages(),
						type, errorList);
			} else {
				addError(
						warning.getVm().getInstanceUUID(),
						warning.getWarningCode(),
						warning.getWarningMessages(),
						type, errorList);
				policyUuids.remove(warning.getVm().getInstanceUUID());
			}
		}
	}
	
	private void addErrorForRunningJobVM(Map<Integer, String> vmRunningJob, VMBackupConfiguration vMBackupConfiguration, String planName, List<PolicyDeploymentError> errorList, int type) {
		if(vmRunningJob == null || vmRunningJob.isEmpty())
			return;
		for(Map.Entry<Integer, String> entry : vmRunningJob.entrySet()){
			int vmId = entry.getKey();
			BackupVM backupVM = this.constructBackupVm(vmId, vMBackupConfiguration);
			if(backupVM == null)
				continue;
			String severName = entry.getValue();
			logger.info("[PolicyManagementServiceImpl]deployPolicyVM(): exist running backupjob vm, vmId : " +  vmId + ", planName : " + planName + ", severName : " + severName);
			Object[] errorParameters = new Object[] {planName, severName};
			addError(backupVM.getInstanceUUID(),
					EdgeServiceErrorCode.PolicyManagement_Deploy_VMBackupJob_Running,
					errorParameters,
					type, errorList);
		}
	}
	
	private void addWarning(String vmInstanceUuid, String errorCode, Object[] errorParameters , int type, List<PolicyDeploymentError> errorList) {
		addError(PolicyDeploymentError.ErrorTypes.Warning, vmInstanceUuid, errorCode, errorParameters, type, errorList);
	}
	private void addError(int errorType, String vmInstanceUuid, String errorCode, Object[] errorParameters,  int type, List<PolicyDeploymentError> errorList) {
		PolicyDeploymentError error = new PolicyDeploymentError();
		
		error.setVmInstanceUuid(vmInstanceUuid);
		error.setPolicyType(ID2DPolicyManagementService.PolicyTypes.VMBackup);
		error.setSettingsType(type);
		error.setErrorType(errorType);
		error.setErrorCode(errorCode);
		error.setErrorParameters(errorParameters);
		
		errorList.add(error);
	}
	
	private void addError(String vmInstanceUuid, String errorCode, Object[] errorParameters , int type, List<PolicyDeploymentError> errorList) {
		addError(PolicyDeploymentError.ErrorTypes.Error, vmInstanceUuid, errorCode, errorParameters, type, errorList);
	}

	private String getHostnameById(int hostId) {
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();

		try {
			hostMgrDao.as_edge_host_list(hostId, 1, hostList);
		} catch (Exception e) {
			logger.error("hostMgrDao.as_edge_host_list() failed.");
			return null;
		}

		if (hostList.isEmpty()) {
			logger.error("Cannot get hostname by id.");
			return null;
		}

		return hostList.get(0).getRhostname();
	}

	//////////////////////////////////////////////////////////////////////////
	
	public List<PolicyDeploymentError> removePolicyVM(D2DConnectInfo d2dConnectInfo,int policyType,boolean keepCurrentSettings ,List<Integer> vmIds, boolean deleteError) 
			throws FailedToGetD2DVersionException,UnsatisfiedD2DVersionException, EdgeServiceFault, Exception
	{
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		String vmInstanceUuids= "";
		for(Integer vmId: vmIds)
		{
			hosts.clear();
			this.hostMgrDao.as_edge_host_list(vmId, 1, hosts);
			
			if (hosts.size()==0)
				continue;
			
			if (HostTypeUtil.isVMWareVirtualMachine(hosts.get(0).getRhostType())){
				List <EdgeEsxVmInfo>  vmList = new ArrayList<EdgeEsxVmInfo>();
				EdgeEsxVmInfo vmInfo = new EdgeEsxVmInfo();
				this.edgeEsxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(vmId, vmList);
				if(deleteError){
					edgePolicyDao.deletePolicyDeployWarningErrorMessage(vmId, policyType);
				}
				if(vmList!=null && vmList.size()>0)
				 {
					vmInfo =  vmList.get(0);
					vmInstanceUuids+=vmInfo.getVmInstanceUuid()+",";
				 }
			}else{
				List <EdgeHyperVHostMapInfo>  hostMapInfo = new ArrayList<EdgeHyperVHostMapInfo>();
				EdgeHyperVHostMapInfo aHostMap = new EdgeHyperVHostMapInfo();
				this.edgeHypervDao.as_edge_hyperv_host_map_getById(vmId, hostMapInfo);
				if(deleteError){
					edgePolicyDao.deletePolicyDeployWarningErrorMessage(vmId, policyType);
				}
				if(hostMapInfo!=null&&hostMapInfo.size()>0)
				 {
					aHostMap =  hostMapInfo.get(0);
					vmInstanceUuids+=aHostMap.getVmInstanceUuid()+",";
				 }
			}
		}

		if(vmInstanceUuids.endsWith(","))
		{
			vmInstanceUuids = vmInstanceUuids.substring(0, vmInstanceUuids.length()-1);
		}
		
		ConnectionContext context = new ConnectionContext(d2dConnectInfo.getProtocol(), d2dConnectInfo.getHostName(), d2dConnectInfo.getPort());
		context.buildAuthUuid(d2dConnectInfo.getAuthUuid());
		context.buildCredential(d2dConnectInfo.getUsername(), d2dConnectInfo.getPassword(), d2dConnectInfo.getDomain());
		GatewayEntity gateway = gatewayService.getGatewayById(d2dConnectInfo.getGatewayId());
		context.setGateway(gateway);
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
//			policyUtil.checkD2DVersion(connection.getService());
			return connection.getService().removePolicy(
					policyType, keepCurrentSettings, policyUtil.getLocalEdgeID(), CommonUtil.getApplicationTypeForD2D(), vmInstanceUuids);
		}
	}

	public List<PolicyDeploymentError> removePolicyVM(
		int hostId, int policyType, int policyId, boolean keepCurrentSettings ,List<Integer> vmIds)
		throws GetD2DConnectInfoException,
		NodeIsNotManagedException,
		EdgeServiceFault, GetPolicyContentXmlException,Exception
	{

		D2DConnectInfo d2dConnectInfo = new D2DConnectInfo();
		policyType = ensurePolicyTypeMatches( policyType );
		EdgeApplicationType applicationType = EdgeWebServiceContext.getApplicationType();
		if (applicationType == EdgeApplicationType.CentralManagement && policyType == PolicyTypes.VMBackup) {
			List<EdgeVSphereProxyInfo> lstProxy = getHBBUPlanProxyByHostId(hostId);
			d2dConnectInfo.setHostName( lstProxy.get(0).getHostname());
			d2dConnectInfo.setPort( lstProxy.get(0).getPort());
			d2dConnectInfo.setProtocol( lstProxy.get(0).getProtocol()==Protocol.Https.ordinal()?"https":"http");
			d2dConnectInfo.setUsername( lstProxy.get(0).getUsername());
			d2dConnectInfo.setPassword( lstProxy.get(0).getPassword() );
			d2dConnectInfo.setUuid( null );
			d2dConnectInfo.setManaged( true );
		} else {
			String policyXml = this.getPolicyContentXml( policyId );
			Document subDocument = policyUtil.getSectionDocument( policyUtil.xmlStringToDocument( policyXml ), PolicyXmlSectionNames.VMBackupSettings );
			VMBackupConfiguration vMBackupConfiguration = getVSphereBackupConfigurationXMLDAO().XMLDocumentToVMBackupConfiguration(subDocument);
			d2dConnectInfo.setHostName( vMBackupConfiguration.getVSphereProxy().getVSphereProxyName() );
			d2dConnectInfo.setPort( vMBackupConfiguration.getVSphereProxy().getVSphereProxyPort());
			d2dConnectInfo.setProtocol( vMBackupConfiguration.getVSphereProxy().getVSphereProxyProtocol() );
			d2dConnectInfo.setUsername( vMBackupConfiguration.getVSphereProxy().getVSphereProxyUsername() );
			d2dConnectInfo.setPassword( vMBackupConfiguration.getVSphereProxy().getVSphereProxyPassword() );
			d2dConnectInfo.setUuid( null );
			d2dConnectInfo.setManaged( true );
		}
		
		UnifiedPolicy plan = getUnifiedPolicyById(policyId);
		VSphereProxy proxy = plan.getVSphereBackupConfiguration().getvSphereProxy();
		GatewayEntity gateway = this.gatewayService.getGatewayByHostId( proxy.getVSphereProxyHostID() );
		d2dConnectInfo.setGatewayId(gateway.getId());
		
		return removePolicyVM(d2dConnectInfo, policyType, keepCurrentSettings, vmIds,true);
	}
	
	public void clearPolicyDeployingCache(int policyId, List<Integer> successfulVmIdList, List<Integer> errorVmIdList, UnifiedPolicy plan)
			throws EdgeServiceFault, GetPolicyContentXmlException,Exception{
		logger.info( "PolicyManagementServiceImpl.clearDeploingCache(): Begin to Clear DeloyingCache." );
		
		String policyXml = null;
		if (plan != null && plan.getVSphereBackupConfiguration() != null) {
			policyXml = marshalLegacyPolicy(plan);
			
		} else {
			policyXml = this.getPolicyContentXml( policyId );
		}
		Document subDocument = policyUtil.getSectionDocument( policyUtil.xmlStringToDocument( policyXml ), PolicyXmlSectionNames.VMBackupSettings );

		VMBackupConfiguration vMBackupConfiguration = getVSphereBackupConfigurationXMLDAO().XMLDocumentToVMBackupConfiguration(subDocument);
		String proxyName = vMBackupConfiguration.getVSphereProxy().getVSphereProxyName();
		String userName = vMBackupConfiguration.getVSphereProxy().getVSphereProxyUsername();
		String password = vMBackupConfiguration.getVSphereProxy().getVSphereProxyPassword();
		String protocol = vMBackupConfiguration.getVSphereProxy().getVSphereProxyProtocol();
		int port = vMBackupConfiguration.getVSphereProxy().getVSphereProxyPort();
		String nodeUuid = null;
		ConnectionContext context = new ConnectionContext(protocol, proxyName, port);
		context.buildCredential(userName, password, "");
		VSphereProxy proxy = plan.getVSphereBackupConfiguration().getvSphereProxy();
		GatewayEntity gateway = gatewayService.getGatewayByHostId( proxy.getVSphereProxyHostID() );
		context.setGateway(gateway);
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();			
			nodeUuid = connection.getNodeUuid();
		}
		catch (Exception e) {
			String faultMessage = e.getMessage(); 
			if(e instanceof SOAPFaultException){
				faultMessage = ((SOAPFaultException) e).getFault().getFaultString();
			}		
			if(e instanceof EdgeServiceFault){
				throw e;
			}else {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_FailedToConnectD2DService,faultMessage);
			}
		}
		
		for (Integer vmId : successfulVmIdList) {
			PolicyDeployingCache.getInstance().clear(nodeUuid, vmId);
		}
		
		for (Integer vmId : errorVmIdList) {
			PolicyDeployingCache.getInstance().clear(nodeUuid, vmId);
		}
		
		logger.info( "PolicyManagementServiceImpl.saveVSphereProxy(): End Clear DeloyingCache.");
	}
	
	
	public boolean checkNeedToDoUndeploy(int vmId, List<EdgeVSphereProxyInfo> proxyList)
	throws EdgeServiceFault
	{
		this.edgeVCMDao.as_edge_vsphere_proxy_getByHostId(vmId,proxyList);
		if(proxyList.size()>0)
		{
			return true;
		}

		return false;
	}
	
	public static int getPolicyTypeByApplicationType()
	{
		return PolicyTypes.Unified;
	}
	
	/**
	 * 0 policy ok
	 * 1 d2d have policy, but not same, will redeploy
	 * 2 d2d don't have policy
	 * 3 policy is failed before, will redeploy
	 * @param d2dUuid
	 * @param policyUuid
	 * @return
	 */
	public int doCheckPolicyStatus(String d2dUuid, String policyUuid, boolean justcheck)throws EdgeServiceFault{
		List<EdgePolicyHostUuid> policies = new ArrayList<EdgePolicyHostUuid>();
		edgePolicyDao.as_edge_policy_by_hostUuid(d2dUuid, DaoFactory.getEncrypt().encryptString(d2dUuid), policies);
		if(policies.size() == 0){
			edgePolicyDao.findPlanByHostUUID(d2dUuid, DaoFactory.getEncrypt().encryptString(d2dUuid), policies);
		}
		
		if(policies.size() ==0)
			return PolicyCheckStatus.NOPOLICY;

		EdgePolicyHostUuid p = policies.get(0);
		for (EdgePolicyHostUuid policy : policies) {
			if (policy.getPolicyUuid().equals(policyUuid)) {
				p = policy;
				break;
			}
		}
		
		if(justcheck){
			return checkStatus(p.getPolicyUuid().equals(policyUuid), p.getDeployStatus());
		}


		if(p.getPolicyUuid().equals(policyUuid)){
			switch(p.getDeployStatus()){
				case PolicyDeployStatus.ToBeDeployed:
					getPolicyDeploymentScheduler().doDeploymentNowByHostIdAndPolicyId(p.getHostId(), p.getPolicyId());
					return PolicyCheckStatus.POLICYDEPLOYING;
				case PolicyDeployStatus.Deploying:
				case PolicyDeployStatus.DeployingD2D:
				case PolicyDeployStatus.DeployD2DRebooting:
				case PolicyDeployStatus.DeployD2DSucceed:
					return PolicyCheckStatus.POLICYDEPLOYING;
				case PolicyDeployStatus.DeployedSuccessfully:
					return PolicyCheckStatus.SAMEPOLICY;
				case PolicyDeployStatus.DeployFaileBecauseOtherEdge:
				case PolicyDeployStatus.DeploymentFailed:
					edgePolicyDao.as_edge_policy_resetTrycount(p.getHostId(), p.getPolicyType(), p.getPolicyId() );
					getPolicyDeploymentScheduler().doDeploymentNowByHostIdAndPolicyId(p.getHostId(), p.getPolicyId());
					return PolicyCheckStatus.POLICYFAILED;
			}
		}else{
			switch(p.getDeployStatus()){
			case PolicyDeployStatus.ToBeDeployed:
				getPolicyDeploymentScheduler().doDeploymentNowByHostIdAndPolicyId(p.getHostId(), p.getPolicyId());
				return PolicyCheckStatus.DIFFERENTPOLICY;
			case PolicyDeployStatus.Deploying:
			case PolicyDeployStatus.DeployingD2D:
			case PolicyDeployStatus.DeployD2DRebooting:
			case PolicyDeployStatus.DeployD2DSucceed:
				return PolicyCheckStatus.DIFFERENTPOLICY;
			case PolicyDeployStatus.DeployedSuccessfully:
			case PolicyDeployStatus.DeployFaileBecauseOtherEdge:
			case PolicyDeployStatus.DeploymentFailed:
				edgePolicyDao.as_edge_policy_resetTrycount(p.getHostId(), p.getPolicyType(), p.getPolicyId() );
				getPolicyDeploymentScheduler().doDeploymentNowByHostIdAndPolicyId(p.getHostId(), p.getPolicyId());
				return PolicyCheckStatus.DIFFERENTPOLICY;
			}
		}
		return PolicyCheckStatus.UNKNOWN;
	}
	
	private int checkStatus(boolean policysame, int deploystatus){
		if(policysame){
			switch(deploystatus){
				case PolicyDeployStatus.ToBeDeployed:
					return PolicyCheckStatus.POLICYDEPLOYING;
				case PolicyDeployStatus.Deploying:
				case PolicyDeployStatus.DeployingD2D:
				case PolicyDeployStatus.DeployD2DRebooting:
				case PolicyDeployStatus.DeployD2DSucceed:
					return PolicyCheckStatus.POLICYDEPLOYING;
				case PolicyDeployStatus.DeployedSuccessfully:
					return PolicyCheckStatus.SAMEPOLICY;
				case PolicyDeployStatus.DeployFaileBecauseOtherEdge:
				case PolicyDeployStatus.DeploymentFailed:
					return PolicyCheckStatus.POLICYFAILED;
			}
		}else{
			switch(deploystatus){
			case PolicyDeployStatus.ToBeDeployed:
				return PolicyCheckStatus.POLICYDEPLOYING;
			case PolicyDeployStatus.Deploying:
			case PolicyDeployStatus.DeployingD2D:
			case PolicyDeployStatus.DeployD2DRebooting:
			case PolicyDeployStatus.DeployD2DSucceed:
				return PolicyCheckStatus.POLICYDEPLOYING;
			case PolicyDeployStatus.DeployedSuccessfully:
				return PolicyCheckStatus.UNKNOWN;
			case PolicyDeployStatus.DeployFaileBecauseOtherEdge:
			case PolicyDeployStatus.DeploymentFailed:
				return PolicyCheckStatus.POLICYFAILED;
			}
		}
		return PolicyCheckStatus.UNKNOWN;
	}
	
	public void redeployPolicy2RightNodes(int policyType, int policyId, int hostId) throws EdgeServiceFault{
		List<Integer> nodeIdList = new LinkedList<Integer>();
		switch(policyType){
		case PolicyTypes.BackupAndArchiving:
			nodeIdList.add( hostId );
			redeployPolicyToNodes(nodeIdList, policyType, policyId);
			break;
		case PolicyTypes.VMBackup:
			List<EdgePolicyHost> hostList = new ArrayList<EdgePolicyHost>();
			this.edgePolicyDao.getNodesWhoIsUsingPolicy( policyId, hostList );
			for (EdgePolicyHost edgePolicyHost : hostList) {
				nodeIdList.add( edgePolicyHost.getHostId() );
			}
			redeployPolicyToNodes(nodeIdList, policyType, policyId);
			break;
		case PolicyTypes.VCM:
			List<EdgeHost> hostList1 = new ArrayList<EdgeHost>();
			int isVisible = 1; // true
			DaoFactory.getDao( IEdgeHostMgrDao.class ).as_edge_host_list( hostId, isVisible, hostList1 );
			EdgeHost hostInfo = hostList1.size() > 0 ? hostList1.get( 0 ) : null;
			if(HostTypeUtil.isVMImportFromVSphere( hostInfo.getRhostType() )){
				this.edgePolicyDao.as_edge_policy_findVcmNodesFromVsp( hostId, policyId, nodeIdList );
				redeployPolicyToNodes(nodeIdList, policyType, policyId);
			}else{
				nodeIdList.add( hostId );
				redeployPolicyToNodes(nodeIdList, policyType, policyId);
			}
			break;
		}
	}

	private EdgePolicy getEdgePolicy(int policyId, boolean containPolicyDetail) throws EdgeServiceFault {
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		int isWithPolicyDetail = containPolicyDetail ? 1 : 0;
		edgePolicyDao.as_edge_policy_list(policyId, isWithPolicyDetail, policyList);
		if (policyList.size() > 0) {
			return policyList.get(0);
		} else {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, "");
		}
	}

	@Override
	public String getEdgePolicyName(int policyId) throws EdgeServiceFault {
		EdgePolicy daoPolicy = getEdgePolicy(policyId, false);
		return daoPolicy.getName();
	}

	@Override
	public int copyEdgePolicy(int policyId, String newPolicyName) throws EdgeServiceFault {
		if (newPolicyName == null || newPolicyName.trim().equals("")) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_BadPolicyName, "");
		}

		// fetch the policy object from database
		EdgePolicy edgePolicy = getEdgePolicy(policyId, true);

		// check duplicate policy name
		int id = getPolicyIdByName(newPolicyName);
		if (id > 0 && id != policyId) {
			//throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_NameDuplicated, "");
			GatewayEntity gateway = gatewayService.getGatewayByEntityId(id, EntityType.Policy);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_NameDuplicatedInSite,
					new Object[] { gateway.getName() }, "");
			
		}

		int savePolicyId = 0;
		int[] newPolicyId = new int[1];

		String uuid = UUID.randomUUID().toString();

		edgePolicyDao.as_edge_policy_update(savePolicyId, newPolicyName, edgePolicy.getPolicyxml(),
				edgePolicy.getType(), edgePolicy.getContentflag(), edgePolicy.getVersion(), uuid, edgePolicy.getProducttype(),
				edgePolicy.getEnablestatus().getValue(), newPolicyId);

		return newPolicyId[0];
	}

	@Override
	public int getPolicyIdByName(String policyName) throws EdgeServiceFault {
		if (policyName == null || policyName.length() == 0) {
			return -1;
		}
		int[] id = new int[1];
		edgePolicyDao.as_edge_policy_getid_by_name(policyName, id);
		return id[0];
	}

	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean redeployPoliciesEx(int policyType)
		throws EdgeServiceFault
	{
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		int isWithPolicyDetail = 0;
		edgePolicyDao.as_edge_policy_list_bytype( policyType, isWithPolicyDetail, policyList );

		if (policyList.size() == 0)
			return false;
	
		List<Integer> policyIdList = new LinkedList<Integer>();
		for (EdgePolicy policyInfo : policyList)
			policyIdList.add( policyInfo.getId());
	
		this.redeployPolicies(policyType, policyIdList);
		return true;
	}
	
	@Override
	public AssignPolicyCheckResultCode canNodesBeAssignedWithPolicy(
			List<Integer> nodeIdList,
			int policyType,
			int policyId
		) throws EdgeServiceFault
	{
		int cans = 0;
		int cannots = 0;
		
		EdgePolicy policy = getEdgePolicy( policyId, false );
		
		int itemIndex = -1;
		StringBuilder strBuilder = new StringBuilder();
		int itemCount = 0;
		
		for (Integer nodeId : nodeIdList)
		{
			itemIndex ++;
			
			if (itemIndex != 0)
				strBuilder.append( "," );

			strBuilder.append( nodeId.toString() );
			
			itemCount ++;
			
			if ((itemCount < 100) && (itemIndex < nodeIdList.size() - 1))
				continue;
			
			String idListString = "(" + strBuilder + ")";
			List<EdgeHost> hostList = new LinkedList<EdgeHost>();
			this.hostMgrDao.as_edge_hosts_list( idListString, hostList );
			
			for (EdgeHost host : hostList)
			{
				VSphereProxyInfo proxyInfo = null;
				
				if ((EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.VirtualConversionManager) &&
					HostTypeUtil.isVMImportFromVSphere( host.getRhostType() ))
					proxyInfo = vcmService.getVSphereProxyInfoByHostId( host.getRhostid() );
				
				if (canNodeBeAssignedWithPolicy(
					host.getRhostid(), host.getRhostType(), host.getAppStatus(),
					proxyInfo, policy ))
				{
					cans ++;
				}
				else // cannot be assigned
				{
					cannots ++;
				}
				
				if ((cans > 0) && (cannots > 0))
					return AssignPolicyCheckResultCode.SomeCannotBeAssigned;
			}
			
			strBuilder.delete( 0, strBuilder.length() );
			itemCount = 0;
		}
		
		return (cans > 0) ?
			AssignPolicyCheckResultCode.AllCanBeAssigned:
			AssignPolicyCheckResultCode.NoneCanBeAssigned;
	}

	@Override
	public AssignPolicyCheckResultCode canNodesOfGroupsBeAssignedWithPolicy(
			List<Integer> groupIdList,
			int policyType,
			int policyId
		) throws EdgeServiceFault
	{
		return null;
	}
	
	public BackupPolicy getPolicyInfo(
		int policyId, boolean needDetails ) throws EdgeServiceFault
	{
		EdgePolicy daoPolicy = this.getEdgePolicy( policyId, needDetails );
		
		BackupPolicy policy = new BackupPolicy();
		policy.setId( daoPolicy.getId() );
		policy.setName( daoPolicy.getName() );
		policy.setPolicyXML( daoPolicy.getPolicyxml() );
		policy.setType( daoPolicy.getType() );
		policy.setContentFlag( daoPolicy.getContentflag() );
		policy.setVersion( daoPolicy.getVersion() );
		policy.setCreationTime( daoPolicy.getCreationtime() );
		policy.setModificationTime( daoPolicy.getModifiedtime() );
		policy.setPolicyProductType(daoPolicy.getProducttype());
		
		return policy;
	}
	
	@Override
	public List<PolicyInfo> getHostPolicies(int hostId) {
		List<PolicyInfo> policyInfoList = new ArrayList<PolicyInfo>();
		this.edgePolicyDao.as_edge_policy_list_by_hostId(hostId, policyInfoList);
		return policyInfoList;
	}
	
	private List<Integer> getProtectedResources( UnifiedPolicy policy )
	{
		List<Integer> nodeIds = new ArrayList<Integer>();
		for (ProtectedResourceIdentifier identifier : policy.getProtectedResources())
		{
			if (identifier.getType() == ProtectedResourceType.node)
				nodeIds.add( identifier.getId() );
		}
		return nodeIds;
	}
	
	private void setProtectedResources( UnifiedPolicy policy, List<Integer> nodeIds )
	{
		policy.getProtectedResources().clear();
		for (int nodeId : nodeIds)
		{
			ProtectedResourceIdentifier identifier = new ProtectedResourceIdentifier();
			identifier.setType( ProtectedResourceType.node );
			identifier.setId( nodeId );
			policy.getProtectedResources().add( identifier );
		}
	}
	
	@Override
	public int createUnifiedPolicy(UnifiedPolicy policy) throws EdgeServiceFault {
		
		logger.info( "createUnifiedPolicy(): Begin to create plan." );
		logger.info( "createUnifiedPolicy(): " + policy );
		
		policy.setId(0);
		validate(policy);

		/*
		 * When the policy is saving, some nodes added to it may be deleted
		 * already, so we'll filter those nodes out at first.
		 */
		List<Integer> nodeIds = getProtectedResources( policy );
		List<Integer> visibleNodeIds = getVisibleNodeIds( nodeIds );
		setProtectedResources( policy, visibleNodeIds );
		
		boolean isVMBackup = (policy.getVSphereBackupConfiguration() == null) ? false : true;
		boolean isLinuxBackup = policy.getLinuxBackupsetting() == null ? false : true;
		//boolean isArchiveToTape = policy.getArchiveToTapeSettings() == null ? false : true;
		boolean isArchiveToTape = policy.getArchiveToTapeSettingsWrapperList().isEmpty() ? false : true;
		int newPolicyId;

		policy.setUuid(UUID.randomUUID().toString());
		RpsSettingTaskDeployment.setRpsPolicyUuid(policy, true);
		RpsSettingTaskDeployment.setPlanUuid(policy);
		
		int assignedNodes = 0;
		RpsSettingTaskDeployment deploy = new RpsSettingTaskDeployment();
		deploy.createRpsPolicySettings(policy);
		
		if(isArchiveToTape){
			//policy.getArchiveToTapeSettings().setPlanGlobalUUID(policy.getUuid());// The first time created UUID is the plan GloableUUID. 
			for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : policy.getArchiveToTapeSettingsWrapperList()){	
				archiveToTapeWrapper.getArchiveToTapeSettings().setPlanGlobalUUID(policy.getUuid());// The first time created UUID is the plan GloableUUID. 
				//archiveToTapeWrapper.setArchiveToTapeUUID(UUID.randomUUID().toString());// add ArchiveToTapeUUID
			}
			ASBUDestinationManager.getInstance().doASBUSettingsDeployment(policy);
		}
		TaskType backupTaskType = TaskType.BackUP;
		if(isLinuxBackup){
			newPolicyId = LinuxPlanManagmentHelper.saveLinuxUnifiedPolicy(this, policy, true);
			getPolicyDeploymentScheduler().doDeploymentNowByPlanId(newPolicyId);
			backupTaskType = TaskType.LinuxBackUP;
		}else if(isVMBackup) {
			newPolicyId = VSpherePlanManagementHelper.saveVMUnifiedPolicy(this, policy, null,true);
			policy.setId(newPolicyId);
			assignedNodes = assignUnifiedPolicy(policy, policy.getProtectedResources());
			backupTaskType = TaskType.VSphereBackUP;
		}else{
			newPolicyId = saveUnifiedPolicy(policy, true);
			policy.setId(newPolicyId);
			assignedNodes = assignUnifiedPolicy(policy, policy.getProtectedResources());
			backupTaskType = TaskType.BackUP;
		}
		if(isArchiveToTape){
			for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : policy.getArchiveToTapeSettingsWrapperList()){	
				int serverId = archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId();
				String mediaGroupName = archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getGrpName();
				dealPlanDestinationMap(newPolicyId, serverId, mediaGroupName ,backupTaskType);
			}
			/*int serverId = policy.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId();
			dealPlanDestinationMap(newPolicyId, serverId);*/
		}
		
		if (assignedNodes > 0) {
			edgePolicyDao.as_edge_policy_updateStatus(newPolicyId, PlanStatus.Deploying);
		}
		
		logger.info( "createUnifiedPolicy(): Creating plan finished. Plan ID: " + newPolicyId );
		if(policy.getGatewayId()==null||(!policy.getGatewayId().isValid())){
			logger.warn( "createUnifiedPolicy(): policy has no config gatewayId. so setLocalGateway. Plan ID: " + newPolicyId);			
			policy.setGatewayId(gatewayService.getLocalGateway().getId());
		}
		logger.info( "createUnifiedPolicy(): bindEntity. Plan ID: " + newPolicyId +" gatewayId: "+ policy.getGatewayId());
		gatewayService.bindEntity(policy.getGatewayId(), newPolicyId, EntityType.Policy);
		
		getSharedFolderService().updateSharedFolderAndPlanMap( newPolicyId, policy );
		return newPolicyId;
	}
	
	private int parseRegisterNumber( String numInStr )
	{
		try
		{
			return Integer.parseInt( numInStr );
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
	public int saveUnifiedPolicy(UnifiedPolicy policy, boolean policyContentChanged) throws EdgeServiceFault {
		String uuid = policy.getUuid();
		
		EdgeVersionInfo versionInfo = this.commonServiceFacade.getVersionInformation();
		
		Version version = new Version();
		version.setMajorVersion( versionInfo.getMajorVersion() );
		version.setMinorVersion( versionInfo.getMinorVersion() );
		version.setBuildNumber( Integer.toString( versionInfo.getBuildNumber() ) );
		version.setUpdateInfo( parseRegisterNumber( versionInfo.getUpdateNumber() ),
			parseRegisterNumber( versionInfo.getUpdateBuildNumber() ) );
		
		policy.setGeneratorVersion( version );
		
		setFilecopyCatalogPathInfo(policy);
		
		String policyXml;
		
		try {
			policyXml = CommonUtil.marshal(policy);
		} catch (JAXBException e) {
			logger.error("marshal unified policy failed, error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "marshal unified policy content failed.");
		}
		
		policyXml=EdgeCommonUtil.encryptXml(policyXml);
		
		int[] newPolicyId = new int[1];
		edgePolicyDao.as_edge_policy_update(
			policy.getId(),
			policy.getName(),
			policyXml,
			PolicyTypes.Unified,
			getTaskBitmap(policy),	// policy content flag
			version.toString(),
			uuid,
			0,	// product type
			policy.isEnable() ? PlanEnableStatus.Enable.getValue():PlanEnableStatus.Disable.getValue(),
			newPolicyId);
		
		return newPolicyId[0];
	}
	
	private void setFilecopyCatalogPathInfo(UnifiedPolicy policy) 
	{
		if(policy.getBackupConfiguration()!=null && !policy.getBackupConfiguration().isD2dOrRPSDestType() && (policy.getTaskList().contains(TaskType.FileCopy) || policy.getTaskList().contains(TaskType.FILE_ARCHIVE)))
		{
			int rpsPolicyCount = policy.getRpsPolices().size();
			
			//Process the filecopy having parent as replication task
			for (int i = rpsPolicyCount - 1; i > 0; i--) {
				
				RPSPolicyWrapper previousRpsPolicyWrapper = policy.getRpsPolices().get(i - 1);
				RPSPolicy previous = previousRpsPolicyWrapper.getRpsPolicy();
				
				RPSPolicyWrapper currentRpsPolicyWrapper = policy.getRpsPolices().get(i);
				RPSPolicy currentRpsPolicy = currentRpsPolicyWrapper.getRpsPolicy();
				
				//if the filecopy or filearchive config exist for current rps policy then set the filecopy catalog path as datastore shared path
				if(currentRpsPolicy.getFileArchiveConfiguration()!=null || currentRpsPolicy.getFileCopyConfiguration()!=null){
					int rHostId = previous.getRpsSettings().getRpsReplicationSettings().getHostId();
					String rHostUser = previous.getRpsSettings().getRpsReplicationSettings().getUserName();
					String rHostPwd = previous.getRpsSettings().getRpsReplicationSettings().getPassword();
					String dsName = currentRpsPolicy.getRpsSettings().getRpsDataStoreSettings().getDataStoreName();
					
					setFCCatalogSttings(currentRpsPolicy, rHostId, rHostUser, rHostPwd, dsName);
				}
			}
			
			//now process the first rps policy 
			if(policy.getRpsPolices().get(0).getRpsPolicy().getFileArchiveConfiguration()!=null || policy.getRpsPolices().get(0).getRpsPolicy().getFileCopyConfiguration()!=null)
			{
				RpsHost backupRPSHost = policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost();
				int rHostId = backupRPSHost.getRhostId();
				String rHostUser = backupRPSHost.getUsername();
				String rHostPwd = backupRPSHost.getPassword();
				String dsName = policy.getBackupConfiguration().getBackupRpsDestSetting().getRPSDataStore();
				
				setFCCatalogSttings(policy.getRpsPolices().get(0).getRpsPolicy(), rHostId, rHostUser, rHostPwd, dsName);
			}
		}
	}

	private void setFCCatalogSttings(RPSPolicy rpsPolicy, int rHostId, String rHostUser,String rHostPwd, String dsName) 
	{
		DataStoreSettingInfo d = null;
		try {
			d = DataStoreManager.getDataStoreManager().getDataStoreByGuid(rHostId, dsName);
		} catch (EdgeServiceFault e) {
			logger.error(e);
		}
		
		if (d != null) {
			String fcCatPath = d.getDSCommSetting().getStoreSharedName();
			String fcCatUser = (d.getDSCommSetting().getUser()==null || d.getDSCommSetting().getUser().isEmpty()) ? rHostUser : d.getDSCommSetting().getUser();
			String fcCatPwd = (d.getDSCommSetting().getPassword()==null || d.getDSCommSetting().getPassword().isEmpty()) ? rHostPwd : d.getDSCommSetting().getPassword();
			
			if(rpsPolicy.getFileCopyConfiguration()!=null){
				rpsPolicy.getFileCopyConfiguration().setStrCatalogPath(fcCatPath);
				rpsPolicy.getFileCopyConfiguration().setStrCatalogDirUserName(fcCatUser);
				rpsPolicy.getFileCopyConfiguration().setStrCatalogDirPassword(fcCatPwd);
			}
			
			if(rpsPolicy.getFileArchiveConfiguration()!=null){
				rpsPolicy.getFileArchiveConfiguration().setStrCatalogPath(fcCatPath);
				rpsPolicy.getFileArchiveConfiguration().setStrCatalogDirUserName(fcCatUser); 
				rpsPolicy.getFileArchiveConfiguration().setStrCatalogDirPassword(fcCatPwd);
			}
		}
		
	}

	public int getTaskBitmap(UnifiedPolicy policy) {
		int bitmap = 0;
		
		if (policy.getBackupConfiguration() != null) {
			bitmap = Utils.setBit(bitmap, PlanTaskType.WindowsD2DBackup, true);
			//set catalog flag
			BackupConfiguration configuration = policy.getBackupConfiguration();
			PeriodSchedule periodSchedule = configuration.getAdvanceSchedule().getPeriodSchedule();
			EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();
			EveryWeekSchedule weekSchedule = periodSchedule.getWeekSchedule();
			EveryMonthSchedule monthSchedule = periodSchedule.getMonthSchedule();
			if(configuration.isGenerateCatalog()
					|| daySchedule.isGenerateCatalog()
					|| weekSchedule.isGenerateCatalog()
					|| monthSchedule.isGenerateCatalog()){
				bitmap = Utils.setBit(bitmap, PlanTaskType.FileSystemCatalog, true);
			}
			if(configuration.getExchangeGRTSetting()==1L){
				bitmap = Utils.setBit(bitmap, PlanTaskType.GRTCatalog, true);
			}
		}
		
		if (policy.getVSphereBackupConfiguration() != null) {
			bitmap = Utils.setBit(bitmap, PlanTaskType.WindowsVMBackup, true);
			//set catalog flag
			VSphereBackupConfiguration configuration = policy.getVSphereBackupConfiguration();
			PeriodSchedule periodSchedule = configuration.getAdvanceSchedule().getPeriodSchedule();
			EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();
			EveryWeekSchedule weekSchedule = periodSchedule.getWeekSchedule();
			EveryMonthSchedule monthSchedule = periodSchedule.getMonthSchedule();
			if(configuration.isGenerateCatalog()
				|| daySchedule.isGenerateCatalog()
				|| weekSchedule.isGenerateCatalog()
				|| monthSchedule.isGenerateCatalog()){
				bitmap = Utils.setBit(bitmap, PlanTaskType.FileSystemCatalog, true);
			}
		}
		
		if (policy.getLinuxBackupsetting() != null) {
			bitmap = Utils.setBit(bitmap, PlanTaskType.LinuxBackup, true);
			bitmap = Utils.setBit(bitmap, PlanTaskType.WindowsD2DBackup, false);
		}
		
		ConversionTask conversionTask = policy.getConversionConfiguration();
		if (conversionTask != null) {
			int policyType = conversionTask.getTaskType();
			PlanTaskType taskType = PolicyTypes.getPlanTaskType(policyType);
			if (taskType == null) {
				logger.error("Unknown policy type for conversion task:" + policyType);
			} else {
				bitmap = Utils.setBit(bitmap, taskType, true);
			}
		}
		
		if (policy.getRpsPolices().size() > 1) {
			bitmap = Utils.setBit(bitmap, PlanTaskType.Replication, true);
		}
		
		if (policy.getMspServerReplicationSettings() != null) {
			bitmap = Utils.setBit(bitmap, PlanTaskType.MspServerReplication, true);
		}
		
		if (policy.getMspServer() != null) {
			bitmap = Utils.setBit(bitmap, PlanTaskType.MspClientReplication, true);
		}
		
		if(policy.getFileCopySettingsWrapper() != null && !policy.getFileCopySettingsWrapper().isEmpty()){
			bitmap = Utils.setBit(bitmap, PlanTaskType.FileCopy, true);
		}
		
		if(policy.getFileArchiveConfiguration() != null ){
			bitmap = Utils.setBit(bitmap, PlanTaskType.FileArchive, true);
		}
		
		if(policy.getExportConfiguration() != null){
			bitmap = Utils.setBit(bitmap, PlanTaskType.CopyRecoveryPoints, true);
		}
		
		if(policy.getArchiveToTapeSettingsWrapperList() != null && !policy.getArchiveToTapeSettingsWrapperList().isEmpty()){
			bitmap = Utils.setBit(bitmap, PlanTaskType.Archive2Tape, true);
		}
		
		return bitmap;
	}
	
	private void validate(UnifiedPolicy policy) throws EdgeServiceFault {
		if (policy == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_BadParameters, "policy is null.");
		}
		
		if (StringUtil.isEmptyOrNull(policy.getName())) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_BadPolicyName, "policy name is null or empty.");
		}		
			
		int existPolicyId = getPolicyIdByName(policy.getName());
		if (existPolicyId > 0 && existPolicyId != policy.getId()) {
			//throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_NameDuplicated, "policy name has alreay existed.");
			GatewayEntity gateway = gatewayService.getGatewayByEntityId(existPolicyId, EntityType.Policy);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_NameDuplicatedInSite,
					new Object[] { gateway.getName() }, "policy name has alreay existed.");
		}
		
		checkRpsVersion(policy, policy.getId());
	}
	
	public int assignUnifiedPolicy(UnifiedPolicy policy, List<ProtectedResourceIdentifier> identifiers) throws EdgeServiceFault {
		
		return  assignUnifiedPolicy(policy, identifiers,true);
	}
	
	public int assignUnifiedPolicy(UnifiedPolicy policy, List<ProtectedResourceIdentifier> identifiers, boolean isNeedtoDeploy) throws EdgeServiceFault {
		int assignedNodes = 0;
		
		for (ProtectedResourceIdentifier identifier : identifiers) {
			ProtectedResourcePlanAssigner assigner = ProtectedResourcePlanAssigner.create(identifier.getType());
			assignedNodes += assigner.assign(policy, identifier.getId(), 0);
		}
		
		if (assignedNodes > 0 && isNeedtoDeploy) {
			getPolicyDeploymentScheduler().doDeploymentNowByPlanId(policy.getId());
		}
		
		return assignedNodes;
	}

	/**
	 * When planB add nodeA which belong to planA
	 * After assign the planB to nodeA , we should update the status of planA
	 * Just update it when planA's status is deploying / modifying / deleting but the overall status is deploy-finished
	 * @param plan
	 */
	public void updateOldPlanStatus(UnifiedPolicy plan) {
		int status = UnifiedDeployTaskRunner.getOverallDeployStatus(plan.getId());
		int[] result = new int[1];
		edgePolicyDao.as_edge_policy_getStatus(plan.getId(),result);
		if (status == 1) {	// failed
			if(result[0] == PlanStatus.Deploying.getValue()){
				edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeployFailed);
			}else if (result[0] == PlanStatus.Modifying.getValue()) {
				edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.ModifyFailed);
			}else if(result[0] == PlanStatus.Deleting.getValue()){
				edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeleteFailed);
			}
		} else if (status == 2) {	// success
			if(result[0] == PlanStatus.Deploying.getValue()){
				edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeploySuccess);
			}else if (result[0] == PlanStatus.Modifying.getValue()) {
				edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.ModifySucess);
			}
		}
	}
	
	public UnifiedPolicy loadUnifiedPolicyById(int planId) throws EdgeServiceFault{
		return getUnifiedPolicyById(planId);
	}
	
	public UnifiedPolicy getUnifiedPolicyById (int policyId) throws EdgeServiceFault {
		EdgePolicy edgePolicy = getEdgePolicy(policyId, true);
		try {
			return convertPlanXml(edgePolicy);
		} catch (JAXBException e) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}
	}
	
	private UnifiedPolicy handleOldVersionPlan(
		EdgePolicy edgePolicy, UnifiedPolicy policy ) throws JAXBException
	{
		/**
		 * In theory, we need to do some process for plans that saved by UDP
		 * earlier than 1857.584, but according to the test, we don't need to
		 * do this, all the settings can be extracted correctly.
		 * 
		 * I'm still not sure about the exact reason, so I leave following
		 * codes here commented.
		 * 
		 * Pang, Bo (panbo01)
		 * 2014-09-11
		 */
		
		/*
		Version generatorVersion = policy.getGeneratorVersion();
		if ((generatorVersion == null) || // an old version policy, generated by UDP v5u1 or earlier version
			(generatorVersion.compareTo( new Version( 5, 0, 0, "1897", 2, 584 ) ) > 0))
			return policy;
		
		if (policy.getBackupConfiguration() != null) // d2d backup
			return policy;
		
		String policyXml = edgePolicy.getPolicyxml();
		policyXml = policyXml.replaceAll( "<ns5:destination>*</ns5:destination>", "" );
		policyXml = policyXml.replaceAll( "<ns5:d2dOrRPSDestType>*</ns5:d2dOrRPSDestType>", "" );
		policyXml = policyXml.replaceAll( "<ns5:encryptionKey>*</ns5:encryptionKey>", "" );
		policyXml = policyXml.replace( "ns2:destination", "ns5:destination" );
		policyXml = policyXml.replace( "ns2:d2dOrRPSDestType", "ns5:d2dOrRPSDestType" );
		policyXml = policyXml.replace( "ns2:encryptionKey", "ns5:encryptionKey" );
		policyXml = policyXml.replace( "ns2:backuprpsdestsetting", "ns5:backuprpsdestsetting" );
		
		return CommonUtil.unmarshal(policyXml, UnifiedPolicy.class);
		*/
		
		return policy;
	}
	
	private UnifiedPolicy convertPlanXml(EdgePolicy edgePolicy)
			throws JAXBException {
		String policyXml = edgePolicy.getPolicyxml();
		policyXml=EdgeCommonUtil.decryptXml(policyXml);
		UnifiedPolicy p= CommonUtil.unmarshal(policyXml, UnifiedPolicy.class);
		p = handleOldVersionPlan( edgePolicy, p );
		p.setId(edgePolicy.getId());
		p.setUuid(edgePolicy.getUuid());
		//p.getNodes().clear();
		p.getProtectedResources().clear();
		// Set enable status
		if (edgePolicy.getEnablestatus() == PlanEnableStatus.Enable) {
			p.setEnable(true);
			if (p.getBackupConfiguration() != null) {				
				p.getBackupConfiguration().setDisablePlan(false);
			} else if (p.getVSphereBackupConfiguration() != null) {
				p.getVSphereBackupConfiguration().setDisablePlan(false);
			}
		} else if (edgePolicy.getEnablestatus() == PlanEnableStatus.Disable) {
			p.setEnable(false);
			if (p.getBackupConfiguration() != null) {				
				p.getBackupConfiguration().setDisablePlan(true);
			} else if (p.getVSphereBackupConfiguration() != null) {
				p.getVSphereBackupConfiguration().setDisablePlan(true);
			}
		}
		for (RPSPolicyWrapper rpsPolicy : p.getRpsPolices()) {
			RpsNodeUtil.buildRpsConnectInfo(rpsPolicy.getRpsPolicy().getRpsSettings().getRpsReplicationSettings());
			ReplicateRpsPolicyUtil.buildSiteName(rpsPolicy);
		}
		
		if (p.getMspServerReplicationSettings() != null) {
			RpsNodeUtil.buildRpsConnectInfo(p.getMspServerReplicationSettings());
		} else if (p.getBackupConfiguration() != null && !p.getBackupConfiguration().isD2dOrRPSDestType()) {
			RpsNodeUtil.buildRpsConnectInfo(p.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost());
		} else if (p.getVSphereBackupConfiguration() != null && !p.getVSphereBackupConfiguration().isD2dOrRPSDestType()) {
			RpsNodeUtil.buildRpsConnectInfo(p.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost());
		}
		
		List<EdgeHostPolicyMap> maps = new ArrayList<EdgeHostPolicyMap>();
		edgePolicyDao.as_edge_plan_getDeployList(edgePolicy.getId(),0, maps);
		for (EdgeHostPolicyMap map : maps) {
			ProtectedResourceIdentifier identifier = new ProtectedResourceIdentifier();
			identifier.setType(ProtectedResourceType.node);
			identifier.setId(map.getHostId());
			p.getProtectedResources().add(identifier);
		}
		List<EdgePolicyGroup> groupMaps = new ArrayList<EdgePolicyGroup>();
		edgePolicyDao.as_edge_plan_group_map_getProtecteGroupResource(edgePolicy.getId(), -1, groupMaps);
		for(EdgePolicyGroup group : groupMaps){
			ProtectedResourceIdentifier identifier = new ProtectedResourceIdentifier();
			identifier.setType(ProtectedResourceType.parseInt(group.getGroupType()));
			identifier.setId(group.getGroupId());
			p.getProtectedResources().add(identifier);
		}
		loadLinuxD2DServer(p);
		return p;
	}
	
	private void loadLinuxD2DServer(UnifiedPolicy policy){
		if(policy.getLinuxBackupsetting()!=null){
			int linuxD2DServerId = policy.getLinuxBackupsetting().getLinuxD2DServerId();
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			hostMgrDao.as_edge_host_list(linuxD2DServerId, 1, hosts);
			if(hosts.size() > 0){
				policy.getLinuxBackupsetting().setLinuxD2DServerName(hosts.get(0).getRhostname());
			}
		}
	}
	
	public void ensureD2DManaged(ID2D4EdgeService_Oolong d2dService, D2DConnectInfo hostConnectInfo) throws EdgeServiceFault {
		int manageStatus = d2dService.QueryEdgeMgrStatus(policyUtil.getLocalEdgeID(), CommonUtil.getApplicationTypeForD2D(), EdgeCommonUtil.getLocalFqdnName());
		
		if (manageStatus == 2) {	// managed by another Console
			logger.info("deployUnifiedBackupPolicy failed, the node was managed by another server. Managing it and try again. " + "hostID = " + hostConnectInfo.getHostId() + ", hostname = " + hostConnectInfo.getHostName());
			EdgeD2DRegServiceFactory.create().UpdateRegInfoToD2D(null,hostConnectInfo.getHostId(), false);
			EdgeCommonUtil.changeNodeManagedStatus(hostConnectInfo.getHostId(), NodeManagedStatus.Managed);
		} else if (manageStatus == 0) {	// not managed by any Console
			RemoteNodeInfo returnRemoteNodeInfo = getNodeService().queryRemoteNodeInfo(hostConnectInfo.getGatewayId(), hostConnectInfo.getHostId(),
					hostConnectInfo.getHostName(), hostConnectInfo.getUsername(), hostConnectInfo.getPassword(),
					hostConnectInfo.getProtocol(),hostConnectInfo.getPort());
			
			NodeRegistrationInfo registreationNode = new NodeRegistrationInfo();
			registreationNode.setNodeInfo(returnRemoteNodeInfo);
			registreationNode.setNodeName(hostConnectInfo.getHostName());
			registreationNode.setUsername(hostConnectInfo.getUsername());
			registreationNode.setPassword(hostConnectInfo.getPassword());
			registreationNode.setCarootUsername(hostConnectInfo.getUsername());
			registreationNode.setCarootPassword(hostConnectInfo.getPassword());
			registreationNode.setD2dPort(hostConnectInfo.getPort());
			Protocol d2DProtocol = Protocol.Http;
			if ("https".equalsIgnoreCase(hostConnectInfo.getProtocol())) {
				d2DProtocol = Protocol.Https;
			}
			registreationNode.setD2dProtocol(d2DProtocol);
			registreationNode.setId(hostConnectInfo.getHostId());
			registreationNode.setRegisterD2D(true);
			
			getNodeService().updateNode(false, registreationNode,false,false); //Should invoke the API which have not print activity log
		}
	}
	
	private D2DConfiguration getD2DConfiguration(int policyId, D2DConnectInfo hostConnectInfo) throws EdgeServiceFault {
		UnifiedPolicy unifiedPolicy = loadUnifiedPolicyById(policyId);
		
		// Set the plan UUID to backup configuration
		if (unifiedPolicy.getBackupConfiguration() != null) {
			unifiedPolicy.getBackupConfiguration().setPlanId(unifiedPolicy.getUuid());
		}
		
		// Set necessary default administrator account
		if(unifiedPolicy.getBackupConfiguration().getAdminUserName()==null || unifiedPolicy.getBackupConfiguration().getAdminUserName().isEmpty()){
			if(hostConnectInfo.getDomain()!=null && !hostConnectInfo.getDomain().isEmpty())
				unifiedPolicy.getBackupConfiguration().setAdminUserName(hostConnectInfo.getDomain()+"\\"+hostConnectInfo.getUsername());
			else
				unifiedPolicy.getBackupConfiguration().setAdminUserName(hostConnectInfo.getUsername());
			unifiedPolicy.getBackupConfiguration().setAdminPassword(hostConnectInfo.getPassword());
		}

		if(unifiedPolicy.getBackupConfiguration().getPrePostUserName()==null || unifiedPolicy.getBackupConfiguration().getPrePostUserName().isEmpty()){
			unifiedPolicy.getBackupConfiguration().setPrePostUserName(unifiedPolicy.getBackupConfiguration().getAdminUserName());
			unifiedPolicy.getBackupConfiguration().setPrePostPassword(unifiedPolicy.getBackupConfiguration().getAdminPassword());
		}
		
		// Mark the staging server as Console type all the time
		StagingServerSettings[] stagingServerSettings = unifiedPolicy.getPreferencesConfiguration().getupdateSettings().getStagingServers();
		if (stagingServerSettings != null) {
			for (StagingServerSettings setting : stagingServerSettings) {
				setting.setUsingConsoleAsStagingServer(true);
			}
		}
		
		return unifiedPolicy.toD2DConfiguration();
	}
	
	public List<PolicyDeploymentError> deployUnifiedBackupPolicy(int nodeId, int policyId, String planUUID) 
			throws GetD2DConnectInfoException, EdgeServiceFault , SOAPFaultException{
		List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();
		D2DConnectInfo hostConnectInfo = getD2DConnectInfo(nodeId);
		D2DConnection connection = connectionFactory.createD2DConnection(nodeId);
		connection.connect();
		ensureD2DManaged(connection.getService(), hostConnectInfo);
		D2DConfiguration d2dConfiguration = getD2DConfiguration(policyId, hostConnectInfo);
		//connection.getService().saveD2DConfiguration(d2dConfiguration);
		String edgeUUID = CommonUtil.retrieveCurrentAppUUID();
		errorList = connection.getService().deployD2DConfigurationFromEdge(d2dConfiguration, planUUID, edgeUUID);
		return errorList;
	}
	
	private String marshalLegacyPolicy(UnifiedPolicy policy) throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
		Document xmlDoc = docBuilder.newDocument();

		Element rootElement = xmlDoc.createElement("BackupPolicy");
		xmlDoc.appendChild(rootElement);
		
		if (policy.getVSphereBackupConfiguration() != null) {
			//VSpherePlanManagementHelper.encryptVSphereBackupConfiguration(this, policy.getVSphereBackupConfiguration());
			Document backupSettingDoc = this.d2dFacade.VSphereBackupConfigurationToXmlDocument(policy.getVSphereBackupConfiguration());
			buildLegacyPolicy(xmlDoc, rootElement, PolicyXmlSectionNames.VMBackupSettings, backupSettingDoc);
		} else {
			encryptBackupConfiguration(policy.getBackupConfiguration());
			Document backupSettingDoc = this.d2dFacade.backupConfigurationToXmlDocument(policy.getBackupConfiguration());
			buildLegacyPolicy(xmlDoc, rootElement, PolicyXmlSectionNames.BackupSettings, backupSettingDoc);
		}
		
		Document preferenceSettingDoc = this.d2dFacade.PreferencesConfigurationToXmlDocument(policy.getPreferencesConfiguration());
		buildLegacyPolicy(xmlDoc, rootElement, PolicyXmlSectionNames.PreferencesSettings, preferenceSettingDoc);
		
		/*if(policy.getFileCopyConfiguration()!=null){
			Document archiveSettingDoc = this.d2dFacade.archiveConfigurationToXmlDocument( policy.getFileCopyConfiguration() );
			buildLegacyPolicy(xmlDoc, rootElement, PolicyXmlSectionNames.ArchivingSettings, archiveSettingDoc);
		}*/
		
		if(policy.getExportConfiguration()!=null){
			Document scheduledSettingDoc = this.d2dFacade.ScheduledExportConfigurationToXmlDocument( policy.getExportConfiguration() );
			buildLegacyPolicy(xmlDoc, rootElement, PolicyXmlSectionNames.ScheduledExportSettings, scheduledSettingDoc);
		}
		
		
		
		/*if(policy.getArchiveToTapeSettings()!= null){
			
		}*/
		
		return xmlDocumentToString(xmlDoc);
	}
	
	private String marshalLegacyVSBPolicy(ConversionTask conversionTask) throws Exception {
		DocumentBuilderFactory buildFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = buildFactory.newDocumentBuilder();
		Document xmlDoc = docBuilder.newDocument();
		
		Element rootElement = xmlDoc.createElement("BackupPolicy");
		xmlDoc.appendChild(rootElement);
		
		String vcmSettingsContent = d2dFacade.vcmConfigurationToString(conversionTask.getConversionJobScript());
		Document vcmXmlDocument=policyXmlUtilities.generateVCMSettingsXmlDocument(vcmSettingsContent);
		buildLegacyPolicy(xmlDoc, rootElement,
				com.ca.arcflash.webservice.edge.policymanagement.PolicyXmlObject.PolicyXmlSectionNames.VCMSettings,
				vcmXmlDocument);
		
		return xmlDocumentToString(xmlDoc);
	}
	
	private void buildLegacyPolicy(Document xmlDoc, Element rootElement, String sectionName, Document settingDoc) {
		Element sectionElement = xmlDoc.createElement(sectionName);
		rootElement.appendChild(sectionElement);
		sectionElement.appendChild(xmlDoc.importNode(settingDoc.getDocumentElement(), true));
	}

	@Override
	public List<PolicyInfo> getPlanList() throws EdgeServiceFault {
		logger.debug("######## begin getPlanList");
		List<PolicyInfo> policyList = new ArrayList<PolicyInfo>(); 
		edgePolicyDao.as_edge_plan_getPlanList(policyList);
		for (PolicyInfo pInfo : policyList) {
			List<EdgeHostPolicyMap> list = new ArrayList<EdgeHostPolicyMap>();
			edgePolicyDao.as_edge_plan_getDeployList(pInfo.getPolicyId(),-1, list);
			List<Integer> listNodeId = new ArrayList<Integer>();			
			for (EdgeHostPolicyMap item : list) {
				if (item.getHostId() > 0) {
					listNodeId.add(item.getHostId());
					if (item.getDeployStatus() == PolicyDeployStatus.ToBeDeployed) {
						pInfo.setToBeDeployedNodeCount(pInfo.getToBeDeployedNodeCount() + 1);
					}else if (item.getDeployStatus() == PolicyDeployStatus.ToBeDeployAsScheduled) {
						pInfo.setScheduleDeployedNodeCount(pInfo.getScheduleDeployedNodeCount()+1);
					}else if (item.getDeployStatus() == PolicyDeployStatus.DeployedSuccessfully) {
						pInfo.setSuccessNodeCount(pInfo.getSuccessNodeCount() + 1);
					} else if (PolicyDeployStatus.isFailed(item.getDeployStatus())) {
						pInfo.setFailedNodeCount(pInfo.getFailedNodeCount() + 1);
					} else {
						pInfo.setDeployingNodeCount(pInfo.getDeployingNodeCount() + 1);
					}
				}							
			}
			
			logger.debug("######## updateFailedPlanStatus");
			updateFailedPlanStatus(pInfo);
			logger.debug("######## updateActiveJobCount");
			updateActiveJobCount(pInfo);
			logger.debug("######## updateNodeStatus");
			updateNodeStatus(listNodeId, pInfo);
		}
		return policyList;
	}
	
	@Override
	public PolicyPagingResult getPlanListByPaging(PlolicyPagingConfig config) throws EdgeServiceFault {
		
		logger.debug("######## begin getPlanList");
		
		int[] totalCount = new int[1];
		List<PolicyInfo> policyList = new ArrayList<PolicyInfo>(); 
		
		edgePolicyDao.as_edge_plan_getPlanList_by_paging(config.getPagesize(),config.getStartpos(),
				config.getOrderType().value(),config.getOrderCol().value(),config.getGatewayId(),totalCount,policyList);
		for (PolicyInfo pInfo : policyList) {
			List<EdgeHostPolicyMap> list = new ArrayList<EdgeHostPolicyMap>();
			edgePolicyDao.as_edge_plan_getDeployList(pInfo.getPolicyId(),-1, list);
			List<Integer> listNodeId = new ArrayList<Integer>();			
			for (EdgeHostPolicyMap item : list) {
				if (item.getHostId() > 0) {
					listNodeId.add(item.getHostId());
					if (item.getDeployStatus() == PolicyDeployStatus.ToBeDeployed) {
						pInfo.setToBeDeployedNodeCount(pInfo.getToBeDeployedNodeCount() + 1);
					}else if (item.getDeployStatus() == PolicyDeployStatus.ToBeDeployAsScheduled) {
						pInfo.setScheduleDeployedNodeCount(pInfo.getScheduleDeployedNodeCount()+1);
					}else if (item.getDeployStatus() == PolicyDeployStatus.DeployedSuccessfully) {
						pInfo.setSuccessNodeCount(pInfo.getSuccessNodeCount() + 1);
					} else if (PolicyDeployStatus.isFailed(item.getDeployStatus())) {
						pInfo.setFailedNodeCount(pInfo.getFailedNodeCount() + 1);
					} else if(item.getDeployStatus() == PolicyDeployStatus.DeployingD2D){
						pInfo.setDeployingD2DNodeCount(pInfo.getDeployingD2DNodeCount()+1);
					}else{
						pInfo.setDeployingNodeCount(pInfo.getDeployingNodeCount() + 1);
					}
				}							
			}
			if(!list.isEmpty()){
				pInfo.setPlanDeployReason(list.get(0).getDeployReason()); //plan deploy reason = planhostmap 's deploy reason
			}
			
			logger.debug("######## updateFailedPlanStatus");
			updateFailedPlanStatus(pInfo);
			logger.debug("######## updateActiveJobCount");
			updateActiveJobCount(pInfo);
			logger.debug("######## updateNodeStatus");
			updateNodeStatus(listNodeId, pInfo);
		}
		
		PolicyPagingResult result = new PolicyPagingResult();
		result.setData(policyList);
		result.setStartIndex(config.getStartpos());
		result.setTotalCount(totalCount[0]);
		
		return result;
	}
	
	private void updateFailedPlanStatus(PolicyInfo pInfo) {
		PlanStatus status = pInfo.getPolicyStatus();
		if (status != PlanStatus.DeployFailed && status != PlanStatus.ModifyFailed && status != PlanStatus.DeleteFailed) {
			return;
		}
		
		int[] deployStatus = new int[1];
		edgePolicyDao.as_edge_policy_getOverallDeployStatus(pInfo.getPolicyId(), deployStatus);
		if (deployStatus[0] == 2) {
			edgePolicyDao.as_edge_policy_updateStatus(pInfo.getPolicyId(), PlanStatus.DeploySuccess);
		}
	}

	private void updateActiveJobCount(PolicyInfo pInfo) {
		List<JobHistory> lstHistory = new ArrayList<JobHistory>();
		edgePolicyDao.as_edge_plan_getActiveJobCount(pInfo.getPolicyUuid(), lstHistory);
		pInfo.setActiveJobCount(lstHistory.size());
	}
	
	private void updateNodeStatus(List<Integer> lstNodeList, PolicyInfo pInfo) throws EdgeServiceFault {
		List<Node> lstNodes = getNodeService().getNodeListByIDs(lstNodeList);
		int successfulCount = 0;
		int warningCount = 0;
		int errorCount = 0;
		for (Node node : lstNodes) {
			NodeProtectionStatus nodeStatus = NodeStatusUtil.getNodeProtectionStatus(node);
			switch (nodeStatus) {
				case ProtectedSuccessful:
					successfulCount++;
					break;
				case ProtectedWithError:
					errorCount++;
					break;
				default:
					warningCount++;
					break;
			}
		}
		pInfo.setSucNodeCount(successfulCount);
		pInfo.setWarningNodeCount(warningCount);
		pInfo.setErrNodeCount(errorCount);
	}
	
	public IEdgePolicyDao getEdgePolicyDao() {
		return edgePolicyDao;
	}
	
	public List<PolicyDeploymentError> deployVSBTask(PolicyDeploymentTask policyDeploymentTask,
			D2DConnectInfo converterConnectInfo, ConversionTask vsbTask, String deployParameters)
			throws GetPolicyContentXmlException, GetD2DConnectInfoException, NodeIsNotManagedException,
			GetPolicyUuidException, EdgeServiceFault, Exception{
		int policyType = vsbTask.getTaskType();
		String policyUuid = getPolicyUuid(policyDeploymentTask.getPolicyId());
		
		ConnectionContext context = new ConnectionContext(converterConnectInfo.getProtocol(), converterConnectInfo.getHostName(), converterConnectInfo.getPort());
		context.buildAuthUuid(converterConnectInfo.getAuthUuid());
		context.buildCredential(converterConnectInfo.getUsername(), converterConnectInfo.getPassword(), converterConnectInfo.getDomain());
		GatewayEntity gateway = gatewayService.getGatewayById(converterConnectInfo.getGatewayId());
		context.setGateway(gateway);
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			// Update the UUID for the converter
			converterConnectInfo.setAuthUuid(connection.getAuthUuid());
			converterConnectInfo.setUuid(connection.getNodeUuid());
			
//			policyUtil.checkD2DVersion(connection.getService());

			List<PolicyDeploymentError> d2dErrorList = new ArrayList<PolicyDeploymentError>();// add for issue 149443
																								// <zhaji22>

			// if (PolicyTypes.isVCMPolicy(policyType)) {
			// VCMPolicyDeployParameters parametersObj = com.ca.arcflash.common.CommonUtil.unmarshal(deployParameters,
			// VCMPolicyDeployParameters.class);
			// if user have customize the ip setting , then customize is right and ignore the policy setting
			// if (parametersObj.getIpSettings() == null || parametersObj.getIpSettings().size() == 0) {
			// // if it is a vcm policy , check whether the nicType defined in policy is suitable for this node
			// // if it is not suitable , change it to new nicType which is supported
			// String result = checkNicTypeOfPolicy(policyId, hostConnectInfo.getHostId(), policyType,
			// d2dErrorList);
			// if (!StringUtil.isEmptyOrNull(result))
			// policyXml = result;// add for issue 149443 <zhaji22>
			// }
			// }

			String policyXml = marshalLegacyVSBPolicy(vsbTask);
			List<PolicyDeploymentError> tmpList = connection.getService().deployPolicy(policyType,
					policyUuid, policyXml, policyUtil.getLocalEdgeID(), CommonUtil.getApplicationTypeForD2D(),
					deployParameters);
			d2dErrorList.addAll(tmpList);
			return d2dErrorList;
		} catch (Exception e) {
			logger.error("deployPolicy() failed.", e);
			throw e;
		}
	}
	
	public List<PolicyDeploymentError> removeVSBTask(PolicyDeploymentTask policyDeploymentTask,
			D2DConnectInfo converterConnectInfo, int taskType, String deployParameters)
			throws GetD2DConnectInfoException, NodeIsNotManagedException,
			FailedToGetD2DVersionException, UnsatisfiedD2DVersionException,
			EdgeServiceFault, Exception {
		ConnectionContext context = new ConnectionContext(converterConnectInfo.getProtocol(), converterConnectInfo.getHostName(), converterConnectInfo.getPort());
		context.buildAuthUuid(converterConnectInfo.getAuthUuid());
		context.buildCredential(converterConnectInfo.getUsername(), converterConnectInfo.getPassword(), converterConnectInfo.getDomain());
		GatewayEntity gateway = gatewayService.getGatewayById(converterConnectInfo.getGatewayId());
		context.setGateway(gateway);
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
//			policyUtil.checkD2DVersion(connection.getService());
			
			return connection.getService().removePolicy(taskType, false,
					policyUtil.getLocalEdgeID(), CommonUtil.getApplicationTypeForD2D(), deployParameters);
		} catch (Exception e) {
			logger.error("removePolicy() failed.", e);
			throw e;
		}
	}
	
	public List<PolicyDeploymentError> removeUnifiedBackupPolicy(int nodeId, boolean keepCurrentSettings) throws Exception {
		int policyType = ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving;

		try (D2DConnection connection = connectionFactory.createD2DConnection(nodeId)) {
			connection.connect();
			
			return connection.getService().removePolicy(
				policyType, keepCurrentSettings, policyUtil.getLocalEdgeID(),
				CommonUtil.getApplicationTypeForD2D(), "");
		} catch (Exception e) {
			logger.error("removeUnifiedBackupPolicy() failed.", e);
			throw e;
		}
	}

	@Override
	public void updateUnifiedPolicy(UnifiedPolicy policy) throws EdgeServiceFault {
		
		final String FUNC_NAME = "PolicyManagementServiceImpl.updateUnifiedPolicy()";
		
		logger.info( FUNC_NAME + ": Begin to update plan." );
		logger.info( FUNC_NAME + ": New plan info: " + policy );
		
		validate(policy);
		
		if (policy.getId() == 0) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_BadParameters, "update unified policy failed, the policy id is 0.");
		}

		UnifiedPolicy oldPolicy = loadUnifiedPolicyById(policy.getId());
		logger.info( FUNC_NAME + ": Old plan info: " + oldPolicy );
		
		RpsSettingTaskDeployment deployment = new RpsSettingTaskDeployment();
		RpsSettingTaskDeployment.setRpsPolicyUuid(policy, false);
		RpsSettingTaskDeployment.setPlanUuid(policy);
		
		Map<String, Integer> archiveUUIDMap= setArchiveToTapeSettings(oldPolicy, policy);//ASBU add ArchiveUUID
		
		boolean planContentChanged = !ObjectDeepComparer.isDeepEquals(policy, oldPolicy, NonPlanContent.class);
		if(planContentChanged){
			policy.setUuid(UUID.randomUUID().toString());
			RpsSettingTaskDeployment.setPlanUuid(policy);
			updateDestinationTable(oldPolicy, policy);
		}		
		
		createArchiveToTapeSettings(policy);//ASBU create
		deployment.createRpsPolicySettings(policy);//RPS create
		TaskType backupTaskType = TaskType.BackUP;
		if (oldPolicy.getVSphereBackupConfiguration() != null) {
			VSpherePlanManagementHelper.saveVMUnifiedPolicy(this, policy, oldPolicy, planContentChanged);
			backupTaskType = TaskType.VSphereBackUP;
		} else if(oldPolicy.getLinuxBackupsetting() != null){
			// policy name change is regarding as policy change for linux case
			if(!policy.getName().equals(oldPolicy.getName())){
				planContentChanged = true;
			}
			LinuxPlanManagmentHelper.updateLinuxUnifiedPolicy(this, oldPolicy,policy, planContentChanged);
			backupTaskType = TaskType.LinuxBackUP;
		} else {
			saveUnifiedPolicy(policy, planContentChanged);
			backupTaskType = TaskType.BackUP;
		}
		
		boolean needToDeploy = updateNodeDeployStatus(oldPolicy, policy, planContentChanged);
		logger.info( FUNC_NAME + ": Need to deploy the plan? " + needToDeploy );
		
		edgePolicyDao.as_edge_policy_updateStatus(policy.getId(), needToDeploy ? PlanStatus.Modifying : PlanStatus.ModifySucess);
		
		if(oldPolicy.getArchiveToTapeSettingsWrapperList() != null){// ASBU delete as_edge_plan_destination_map for old plan
			edgePolicyDao.deletePlanDestinationMap(oldPolicy.getId(), 0, PlanDestinationType.ASBU.ordinal(),-1);
		}
		if(policy.getArchiveToTapeSettingsWrapperList() != null){ // ASBU update as_edge_plan_destination_map for new plan
			for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : policy.getArchiveToTapeSettingsWrapperList()){	
				int serverId = archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId();
				String mediaGroupName = archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getGrpName();
				dealPlanDestinationMap(oldPolicy.getId(), serverId, mediaGroupName, backupTaskType);
			}
		}
		
		deployment.deleteOldRpsPolicySettings(oldPolicy, policy);//RPS delete
		deleteArchiveToTapeSettings(archiveUUIDMap);//ASBU delete
		
		if (needToDeploy) {
			getPolicyDeploymentScheduler().doDeploymentNowByPlanId(policy.getId());
		}
		
		logger.info( FUNC_NAME + ": Updating plan finished." );
	}

	private List<Integer> getVisibleNodeIds( List<Integer> nodeIdList ) throws EdgeServiceFault
	{
		List<EdgeHost> hostList = getNodeService().getEdgeHostByIDs( nodeIdList, null );
		List<Integer> visibleNodeIdList = new ArrayList<Integer>();
		for (EdgeHost host : hostList)
			visibleNodeIdList.add( host.getRhostid() );
		return visibleNodeIdList;
	}
	
	@Override
	public List<ValuePair<Integer,Integer>> getPlanIdsWithTheSameNodeIds(UnifiedPolicy policy) {
		List<ValuePair<Integer,Integer>> planId_nodeId_list = new ArrayList<ValuePair<Integer,Integer>>();
//		Map<Integer,List<Integer>> plan_nodesMap = new HashMap<Integer,List<Integer>>();
//		List<Integer> nodeIds = getProtectedResources( policy );
//		if (nodeIds != null) {
//			for (int nodeId : nodeIds) {
//				int[] ids = new int[1]; 
//				edgePolicyDao.as_edge_policy_getPlanIdsByNodeIds(nodeId, ids);
//				if(ids[0] > 0){
//					if(plan_nodesMap.containsKey(ids[0])){
//						plan_nodesMap.get(ids[0]).add(nodeId);
//					}else {
//						List<Integer> nodeIdOfThisPlan = new ArrayList<Integer>();
//						nodeIdOfThisPlan.add(nodeId);
//						plan_nodesMap.put(ids[0], nodeIdOfThisPlan);
//					}
//				}
//			}
//		}
//		
//		return plan_nodesMap;
		List<Integer> nodeIds = getProtectedResources( policy );
		if (nodeIds != null) {
			for (int nodeId : nodeIds) {
				int[] ids = new int[1]; 
				edgePolicyDao.as_edge_policy_getPlanIdsByNodeIds(nodeId, ids);
				if(ids[0] > 0){
					ValuePair<Integer, Integer> planId_nodeIdPair = new ValuePair<Integer,Integer>(ids[0],nodeId);
					planId_nodeId_list.add(planId_nodeIdPair);
				}
			}
		}
		return planId_nodeId_list;
	}
	
	private void updateDestinationTable(UnifiedPolicy oldPolicy, UnifiedPolicy policy) throws EdgeServiceFault{
		
		/*
		 * When the policy is saving, some nodes added to it may be deleted
		 * already, so we'll filter those nodes out at first.
		 */
		List<Integer> nodeIds = getProtectedResources( policy );
		List<Integer> visibleNodeIds = getVisibleNodeIds( nodeIds );
		setProtectedResources( policy, visibleNodeIds );
		/*///handle archive to tape; it's written by tony 
		if(oldPolicy.getArchiveToTapeSettings()!=null && policy.getArchiveToTapeSettings() == null){
			//delete
			try{
				ASBUDestinationManager.getInstance().deleteASBUSettings(oldPolicy);
			} catch( WebServiceException|EdgeServiceFault ex)
			{
				//Defect 204538
				logger.error(ex);
				if (oldPolicy.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo() != null)
				{
					createASBUActivityLog(oldPolicy.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId(), EdgeCMWebServiceMessages.getResource("asbu_server_connect_error", oldPolicy.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getDestServerName()));
				}
			}
		}else if(policy.getArchiveToTapeSettings() != null){
			String[] planGlobalUuid = new String[1];
			edgePolicyDao.as_edge_policy_getGlobalUuid(oldPolicy.getId(), planGlobalUuid);
			policy.getArchiveToTapeSettings().setPlanGlobalUUID(planGlobalUuid[0]);
			ASBUDestinationManager.getInstance().doASBUSettingsDeployment(policy);
			int serverId = policy.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId();
			dealPlanDestinationMap(oldPolicy.getId(), serverId);
		}*/
		//handle shared folder fanda03;
		getSharedFolderService().updateSharedFolderAndPlanMap( policy.getId(), policy );
	}
	
	private void dealPlanDestinationMap(int planId, int serverId, String mediaGroupName, TaskType taskType){
		edgePolicyDao.addOrUpdatePlanDestinationMap(planId, serverId, PlanDestinationType.ASBU.ordinal(), mediaGroupName, taskType.ordinal());
	}
	
	/**
	 * 
	 * @param oldPolicy
	 * @param policy
	 * @return    need to delete ArchiveToTapeSettings MAP
	 * @throws EdgeServiceFault
	 */
	private Map<String, Integer> setArchiveToTapeSettings(UnifiedPolicy oldPolicy, UnifiedPolicy policy) throws EdgeServiceFault{
		Map<String, Integer> archiveUUIDMap = new HashMap<String, Integer> ();
		if(policy.getArchiveToTapeSettingsWrapperList() != null){
			String[] planGlobalUuid = new String[1];
			edgePolicyDao.as_edge_policy_getGlobalUuid(oldPolicy.getId(), planGlobalUuid);
			//Map<String, String> archiveToTapeUUIDMap = new HashMap<String,String> ();
			for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : oldPolicy.getArchiveToTapeSettingsWrapperList()){
				//String taskId = archiveToTapeWrapper.getTaskId();
				int serverId = archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId();
				String archiveToTapeUUID = archiveToTapeWrapper.getArchiveToTapeUUID();
				//archiveToTapeUUIDMap.put(taskId, archiveToTapeUUID);
				archiveUUIDMap.put(archiveToTapeUUID, serverId);
			}
			for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : policy.getArchiveToTapeSettingsWrapperList()){
				//add planGlobalUuid
				archiveToTapeWrapper.getArchiveToTapeSettings().setPlanGlobalUUID(planGlobalUuid[0]);
				// add ArchiveUUID
				String archiveToTapeUUID_New = archiveToTapeWrapper.getArchiveToTapeUUID();
				int serverId = archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId();
				if(archiveUUIDMap.containsKey(archiveToTapeUUID_New)){
					// if asbu server change , delete ArchiveToTapeSettings on the old server .otherwise, 
					// if asbu server not change , only to modify the ArchiveToTapeSettings on the same server, do not need to delete
					if(serverId == archiveUUIDMap.get(archiveToTapeUUID_New)){
						archiveUUIDMap.remove(archiveToTapeUUID_New);
					}
				}
				/*String taskId = archiveToTapeWrapper.getTaskId();
				
				if(archiveToTapeUUIDMap.containsKey(taskId)){
					// task is not change , use old ArchiveToTapeUUID
					archiveToTapeWrapper.setArchiveToTapeUUID(archiveToTapeUUIDMap.get(taskId));
					if(archiveUUIDMap.containsKey(archiveToTapeUUIDMap.get(taskId))){
						// if asbu server change , delete ArchiveToTapeSettings on the old server .otherwise, 
						// if asbu server not change , only to modify the ArchiveToTapeSettings on the same server, do not need to delete
						if(serverId == archiveUUIDMap.get(archiveToTapeUUIDMap.get(taskId))){
							archiveUUIDMap.remove(archiveToTapeUUIDMap.get(taskId));
						}
					}
				}else{
					// task is change , create new ArchiveToTapeUUID
					archiveToTapeWrapper.setArchiveToTapeUUID(UUID.randomUUID().toString());
				}*/
			}
		}
		return archiveUUIDMap;
	}
	
	private void createArchiveToTapeSettings(UnifiedPolicy policy) throws EdgeServiceFault {
		if(policy.getArchiveToTapeSettingsWrapperList() != null){
			ASBUDestinationManager.getInstance().doASBUSettingsDeployment(policy);
		}
	}
	
	private void deleteArchiveToTapeSettings(Map<String, Integer> archiveUUIDMap) throws EdgeServiceFault {
		if(archiveUUIDMap != null && !archiveUUIDMap.isEmpty()){
			ASBUDestinationManager.getInstance().deleteASBUSettings(archiveUUIDMap);
		}
	}
	
	private Map<ProtectedResourceType, Set<Integer>> convertProtectedResourceIdentifiers(List<ProtectedResourceIdentifier> identifiers) {
		Map<ProtectedResourceType, Set<Integer>> map = new HashMap<ProtectedResourceType, Set<Integer>>();
		
		for (ProtectedResourceIdentifier identifier : identifiers) {
			if (!map.containsKey(identifier.getType())) {
				map.put(identifier.getType(), new HashSet<Integer>());
			}
			
			map.get(identifier.getType()).add(identifier.getId());
		}
		
		return map;
	}
	
	private boolean updateNodeDeployStatus(UnifiedPolicy oldPolicy, UnifiedPolicy newPolicy, boolean planContentChanged) throws EdgeServiceFault {
		int nodeCount = 0;
		
		if (newPolicy.getMspServerReplicationSettings() != null) {	// replicate from customer
			newPolicy.setProtectedResources( oldPolicy.getProtectedResources() );
		}
		
		Map<ProtectedResourceType, Set<Integer>> oldResources = convertProtectedResourceIdentifiers(oldPolicy.getProtectedResources());
		Map<ProtectedResourceType, Set<Integer>> newResources = convertProtectedResourceIdentifiers(newPolicy.getProtectedResources());
		
		for (ProtectedResourceIdentifier identifier : oldPolicy.getProtectedResources()) {
			// deleted sources
			if (!newResources.containsKey(identifier.getType()) || !newResources.get(identifier.getType()).contains(identifier.getId())) {
				nodeCount += ProtectedResourcePlanAssigner.create(identifier.getType()).unassign(newPolicy, identifier.getId(), PolicyDeployFlags.ModifyPlan);
			}
		}
		
		for (ProtectedResourceIdentifier identifier : newPolicy.getProtectedResources()) {
			// content changed sources
			if (oldResources.containsKey(identifier.getType()) && oldResources.get(identifier.getType()).contains(identifier.getId())) {
				// MSP replication plan without VSB task
				if (oldPolicy.getMspServerReplicationSettings() != null && oldPolicy.getConversionConfiguration() == null
						&& newPolicy.getMspServerReplicationSettings() != null && newPolicy.getConversionConfiguration() == null) {
					continue;
				}
				
				if (planContentChanged) {
					nodeCount += ProtectedResourcePlanAssigner.create(identifier.getType()).updateOnPlanChanged(oldPolicy, newPolicy, identifier.getId(), PolicyDeployFlags.ModifyPlan);
				} else {
					boolean deploySettingChanged = !ObjectDeepComparer.isDeepEquals(newPolicy.getDeployD2Dsetting(), oldPolicy.getDeployD2Dsetting(), (Class[]) null);
					if (deploySettingChanged && identifier.getType() == ProtectedResourceType.node
							&& AgentInstallationInPlanHelper.isNeedInstallAgent(identifier.getId(), newPolicy)) {
						nodeCount += ProtectedResourcePlanAssigner.create(identifier.getType()).updateOnPlanChanged(oldPolicy, newPolicy, identifier.getId(), PolicyDeployFlags.ModifyPlan);
					}
				}
			} else {	// new added sources
				nodeCount += ProtectedResourcePlanAssigner.create(identifier.getType()).assign(newPolicy, identifier.getId(), PolicyDeployFlags.ModifyPlan);
			}
		}
		
		return nodeCount > 0;
	}
	
	@Override
	public List<PolicyInfo> getPlansByNodeNameIp (int gatewayid, String name, String ip ) throws EdgeServiceFault {
		List<PolicyInfo> policys = new ArrayList<PolicyInfo>();
		int[] hostids = new int[1];
		this.hostMgrDao.as_edge_host_getIdByHostnameIp(gatewayid, name, ip, 1,hostids );
		int id= hostids[0];
		if( id !=0 ) {
			this.edgePolicyDao.as_edge_policy_list_by_hostId( id, policys );
		}
		return policys;
	}
	@Override	
	public long testConnectionToCloud(ArchiveCloudDestInfo in_cloudInfo)  throws EdgeServiceFault{
		
		long connectionStatus;
		
		try {

			in_cloudInfo.setEncodedCloudBucketName("");// to fix issue 750672--
														// empty bucket getting
														// created in amazon s3
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( new GatewayId(in_cloudInfo.getGatewayId()) );
			
			connectionStatus = nativeFacade.testConnection(in_cloudInfo);
		} catch (Exception e) {
			logger.error("testConnectionToCloud error: " + e.getMessage(), e);
			throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.Common_ErrorOccursInService, "");			
		}
		return connectionStatus;
		
	}
	@Override	
	public String GetArchiveDNSHostName() throws EdgeServiceFault{
		String hostName;
		try {
			hostName = ArchiveService.getInstance().GetArchiveDNSHostName();
		} catch (Exception e) {
			logger.error("GetArchiveDNSHostName error: " + e.getMessage(), e);
			throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.Common_ErrorOccursInService, "");			
		}
		return hostName;	
	}
	
	@Override
	public List<CloudProviderInfo> getCloudProviderInfos()  throws EdgeServiceFault{
		List<CloudProviderInfo> cloudProviderInfo;
		try {
			cloudProviderInfo = ArchiveService.getInstance().getCloudProviderInfo();
		} catch (Exception e) {
			logger.error("getCloudProviderInfos error: " +e.getMessage(), e);
			throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.Common_ErrorOccursInService, "");
		}
		return cloudProviderInfo;
	}

	@Override
	public UnifiedPolicy loadUnifiedPolicyByUuid(String uuid)
			throws EdgeServiceFault {
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		EdgePolicy edgePolicy;
		edgePolicyDao.as_edge_policy_list_by_uuid(uuid, policyList);
		if (policyList.size() > 0) {
			edgePolicy = policyList.get(0);
		} else {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, "");
		}
		
		try {
			return convertPlanXml(edgePolicy);
		} catch (JAXBException e) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}
	}
	
	private UnifiedPolicy loadUnifiedPolicyByGlobalUuid(String globalUuid)
			throws EdgeServiceFault {
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		EdgePolicy edgePolicy;
		int[] policyIds = new int[1];;
		edgePolicyDao.as_edge_policy_getId(globalUuid, policyIds);
		if (policyIds[0] > 0) {
			edgePolicyDao.as_edge_policy_list(policyIds[0], 1, policyList);
			edgePolicy = policyList.get(0);
		} else {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, "");
		}
		
		try {
			return convertPlanXml(edgePolicy);
		} catch (JAXBException e) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}
	}

	@Override
	public List<MspReplicationDestination> getMspReplicationDestinations(String localFQDNName, RpsHost mspServer, HttpProxy clientHttpProxy) throws EdgeServiceFault {
		if (clientHttpProxy == null) {
			CAProxySelector.getInstance().unRegistryProxy(mspServer.getRhostname());
		} else {
			CAProxy proxy = new CAProxy();
			proxy.setTargetHost(mspServer.getRhostname());
			proxy.setHttpProxy(clientHttpProxy);
			CAProxySelector.getInstance().registryProxy(proxy);
		}
		
		String protocol = mspServer.isHttpProtocol() ? "http" : "https";
		IEdgeMsp4ClientService service;
		
		try {
			service = MspWebServiceFactory.create(protocol, mspServer.getRhostname(), mspServer.getPort(), IEdgeMsp4ClientService.class);
		} catch (Exception e) {
			logger.debug("getMspReplicationDestinations failed, cannot create the web service proxy. Error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.MSP_CannotConnectMSP , e.getMessage());
		}
		
		service.validateCustomer(mspServer.getUsername(), mspServer.getPassword());
//		service.validateisRemoteConsole(localFQDNName);
		
		return service.getMspReplicationDestinations();
	}

	public void deployPlanByNodeId(int node, int policyDeployReason) throws EdgeServiceFault{
		List<PolicyInfo> lstPolicies = getHostPolicies(node);
		if (lstPolicies != null && lstPolicies.size() != 0) {
			PolicyInfo policyInfo = lstPolicies.get(0);
			int policyId = policyInfo.getPolicyId();
			
			try {
				checkRpsVersion(null, policyId);
			} catch (EdgeServiceFault e) {
				logger.warn("deployPlanByNodeId:"+node+" "+e.getFaultInfo().getMessage());
				edgePolicyDao.deletePolicyDeployWarningErrorMessage(node,PolicyTypes.Unified);
				edgePolicyDao.as_edge_policy_updateStatus(policyId, PlanStatus.DeployRpsPolicyFailed);
				edgePolicyDao.as_edge_policy_updateStatus_4node(policyId, PolicyDeployStatus.CreateRPSPolicy_Failed);
				edgePolicyDao.as_edge_policy_setDeployErrorMessage(policyId, e.getFaultInfo().getMessage());
				return;
			}
			
			edgePolicyDao.as_edge_policy_updateMapStatus(policyId, node, policyDeployReason, PolicyDeployStatus.ToBeDeployed, PolicyDeployFlags.RedeployPlan, policyInfo.getEnabled().getValue());
			this.getPolicyDeploymentScheduler().doDeploymentNowByHostId(node);
		}
	}
	
	public synchronized void deployHBBUPlanByProxyNodeId(int proxyNodeId) throws EdgeServiceFault {
		List<EdgePolicy> planList = new ArrayList<EdgePolicy>();
		edgePolicyDao.as_edge_policy_list_byProxyHostId(proxyNodeId, planList);
		if (planList.isEmpty()) {
			return;
		}
		EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo(proxyNodeId);
		List<EdgeConnectInfo> connectInfoList = new ArrayList<EdgeConnectInfo>();
		connectInfoDao.as_edge_connect_info_list(proxyNodeId, connectInfoList);
		if(connectInfoList==null || connectInfoList.isEmpty())
			return;
		
		EdgeConnectInfo proxyConnectInfo = connectInfoList.get(0);
		if(proxyConnectInfo==null || proxyConnectInfo.getProtocol()==0 || proxyConnectInfo.getPort()==0)
			return;
		
		List<UnifiedPolicy> needToRedeployPlanList = new ArrayList<UnifiedPolicy>();
		List<Integer>  needToRedeployPlanIdList = new ArrayList<Integer>(); 
		
		for (EdgePolicy edgePolicy : planList) {
			UnifiedPolicy plan = null;
			try {
				plan = convertPlanXml(edgePolicy);
				
			} catch (JAXBException e) {
				logger.error("Failed to convert xml to UnifiedPolicy.");
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			}
		
			try {
				checkRpsVersion(plan, edgePolicy.getId());
			} catch (EdgeServiceFault e) {
				logger.warn("deployHBBUPlanByProxyNodeId: "+proxyNodeId+" "+e.getFaultInfo().getMessage());
				for (ProtectedResourceIdentifier identifier : plan.getProtectedResources()) {
					edgePolicyDao.deletePolicyDeployWarningErrorMessage(identifier.getId(),PolicyTypes.Unified);
				}
				edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeployRpsPolicyFailed);
				edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.CreateRPSPolicy_Failed);
				edgePolicyDao.as_edge_policy_setDeployErrorMessage(plan.getId(), e.getFaultInfo().getMessage());
				continue;
			}
			
			VSphereBackupConfiguration vsConf = plan.getVSphereBackupConfiguration();
			String hostProtocol = "HTTP";
			if(proxyConnectInfo.getProtocol()==Protocol.Https.ordinal()){
				hostProtocol = "HTTPS";
			}
			
			if (vsConf != null) {
				VSphereProxy proxy = vsConf.getvSphereProxy();
				if (!proxyConnectInfo.getUsername().equals(proxy.getVSphereProxyUsername())
						|| !proxyConnectInfo.getPassword().equals(proxy.getVSphereProxyPassword())
						||  proxyConnectInfo.getPort() != proxy.getVSphereProxyPort()
						|| !hostProtocol.equalsIgnoreCase(proxy.getVSphereProxyProtocol())
						|| edgePolicy.getStatus()==PlanStatus.DeployFailed	//defect 193781
						|| edgePolicy.getStatus()==PlanStatus.ModifyFailed
						|| edgePolicy.getStatus()==PlanStatus.DeployRpsPolicyFailed
						|| edgePolicy.getStatus()==PlanStatus.DeployAsbuPolicyFailed
						|| edgePolicy.getStatus() == PlanStatus.Deploying) {
					proxy.setVSphereProxyHostID(proxyNodeId);
					proxy.setVSphereProxyName(hostInfo.getRhostname());
					proxy.setVSphereProxyUsername(proxyConnectInfo.getUsername());
					proxy.setVSphereProxyPassword(proxyConnectInfo.getPassword());
					proxy.setVSphereProxyPort(proxyConnectInfo.getPort());
					proxy.setVSphereProxyProtocol(hostProtocol);
					needToRedeployPlanList.add(plan); //if proxy credencial changed , then need re-save plan
					needToRedeployPlanIdList.add(plan.getId());
				}
			}
		}
		
		for (UnifiedPolicy plan : needToRedeployPlanList) {
			VSpherePlanManagementHelper.saveVMUnifiedPolicy(this, plan, null, true);
		}
		
		if(!needToRedeployPlanIdList.isEmpty()){
			redeployPolicies(PolicyTypes.Unified, needToRedeployPlanIdList);
		}

	}
	
	public List<EdgeVSphereProxyInfo> getHBBUPlanProxyByHostId(int hostId) throws EdgeServiceFault
	{
		List<EdgeVSphereProxyInfo> proxyList = new ArrayList<EdgeVSphereProxyInfo>();
		edgeVCMDao.as_edge_vsphere_proxy_getByHostId(hostId, proxyList);
		
		if (proxyList.size()==0){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Backup_ProxyNotFound, "");
		}
		
		return proxyList;
	}
	
	public void undeployHBBUPlanByHostIdIgnoreException(int planId, int hostId) {
		try {
			List<EdgeVSphereProxyInfo> proxyList = getHBBUPlanProxyByHostId(hostId);
			EdgeVSphereProxyInfo proxy = proxyList.get(0);

			D2DConnectInfo d2dConnectInfo = new PolicyManagementServiceImpl().new D2DConnectInfo();
			d2dConnectInfo.setHostName(proxy.getHostname());
			d2dConnectInfo.setPort(proxy.getPort());
			d2dConnectInfo.setProtocol(proxy.getProtocol()==Protocol.Https.ordinal()?"https":"http");
			d2dConnectInfo.setUsername(proxy.getUsername());
			d2dConnectInfo.setPassword(proxy.getPassword());
			d2dConnectInfo.setUuid(null);

			List<Integer> deletedVMIds = new ArrayList<Integer>();
			deletedVMIds.add(hostId);
			
			GatewayEntity gateway = this.gatewayService.getGatewayByEntityId( proxy.getId(), EntityType.VMBackupProxy );
			d2dConnectInfo.setGatewayId(gateway.getId());

			removePolicyVM(d2dConnectInfo, PolicyTypes.VMBackup, false, deletedVMIds,true);
		} catch (Exception e) {
			logger.error("Failed to undeploy VM backup plan [id=" + planId + "] for host [id= " + hostId + "]", e);
		}
	}

	@Override
	public void enablePolicies(final boolean value, List<Integer> policyIdList
			,final RebootType nodeInstallType, final Date installtime)
			throws EdgeServiceFault {
		PlanPauseResumeService.getInstance().enablePolicies(value, policyIdList);
	}

	@Override
	public List<Integer> getPlanIds() throws EdgeServiceFault {
		List<EdgePolicy> plans=new ArrayList<EdgePolicy>();
		edgePolicyDao.as_edge_policy_list(0, 0, plans);
		List<Integer> rs=new ArrayList<Integer>();
		for(EdgePolicy p:plans){
			rs.add(p.getId());
		}
		return rs;
	}

	@Override
	public ResourcePool[] getResourcePool( GatewayId gatewayId,
		VirtualCenter vc, ESXServer esxServer, ResourcePool parentResourcePool )
		throws EdgeServiceFault
	{
		IRemoteNodeServiceFactory serviceFactory = EdgeFactory.getBean( IRemoteNodeServiceFactory.class );
		IRemoteNodeService remoteService = serviceFactory.createRemoteNodeService( gatewayId );
		return remoteService.getResourcePool( vc, esxServer, parentResourcePool );
	}
	
	@Override
	public List<Integer> getLowVersionNodeIdsByPlanIds(List<Integer> planIds)
			throws EdgeServiceFault {
		if(planIds == null)
			return null;
		List<Integer> nodeIds = new ArrayList<Integer>();
		for(int planId : planIds){
			List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
			int isWithDetails = 1; // without details
			this.edgePolicyDao.as_edge_policy_list( planId, isWithDetails, policyList );
			if(policyList.isEmpty())
				continue;
			
			EdgePolicy policy = policyList.get(0);
			if(Utils.hasHbbuBackupTask(policy.getContentflag())){//HBBU plan
				List<EdgeConnectInfo> lstProxy = new ArrayList<EdgeConnectInfo>();
				connectInfoDao.as_edge_proxy_by_policyid(planId, lstProxy);
				if(lstProxy.isEmpty())
					continue;
				nodeIds.add(lstProxy.get(0).getHostid());
			}else if(Utils.hasLiunxBackupTask(policy.getContentflag())){//linux plan
				continue;
			}else{//D2D plan
				List<EdgePolicyHost> hostList = new ArrayList<EdgePolicyHost>();
				this.edgePolicyDao.getNodesWhoIsUsingPolicy( planId, hostList );
				for(EdgePolicyHost policyHost : hostList){
					nodeIds.add(policyHost.getHostId());
				}
			}
		}
		return nodeService.getNodesNeedRemoteDeploy(nodeIds);
	}
	
	public List<String> getProtectedResourceUuids(String serverUUID, String policyUUID, String policyGlobalUUID, String archiveUUID) throws EdgeServiceFault {
		String msg = "Not found protected resource, please check input parameters";
		UnifiedPolicy unifiedPolicy = new UnifiedPolicy();
		try{
			unifiedPolicy = loadUnifiedPolicyByGlobalUuid(policyGlobalUUID);
		}catch(Exception e){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, "Not found plan by policyGlobalUUID, policyGlobalUUID :" + policyGlobalUUID);
		}
		if(unifiedPolicy == null)
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, "Not found plan by policyGlobalUUID, policyGlobalUUID :" + policyGlobalUUID);
		List<EdgeASBUServer> hostList = new ArrayList<EdgeASBUServer>();
		//hostMgrDao.getHostByUUID(serverUUID, hostList);
		asbuDao.findConnectionInfoByUUID(serverUUID, hostList);
		boolean isHBBU = false;
		if (unifiedPolicy.getVSphereBackupConfiguration() != null) {
			isHBBU = true;
			logger.info("getProtectedResourceUuids, plan is HBBU!");
		}
        if (hostList.size() > 0) {
        	if(unifiedPolicy.getArchiveToTapeSettingsWrapperList()!=null && !unifiedPolicy.getArchiveToTapeSettingsWrapperList().isEmpty()){
        		for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : unifiedPolicy.getArchiveToTapeSettingsWrapperList()){
        			if(archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveToTapeDestinationInfo().getServerId() == hostList.get(0).getHostId()
        					&& archiveToTapeWrapper.getArchiveToTapeUUID().equals(archiveUUID)){
        				//List<Node> nodes = getNodesByPolicy(unifiedPolicy.getId());
        				List<Node> nodes = getNodeList(unifiedPolicy);
    					List<String> nodeUuidList = new ArrayList<String>();
    					for (Node item : nodes)
    					{
    						/*if(HostTypeUtil.isVMWareVirtualMachine(item.getRhostType())){
    							nodeUuidList.add(item.getVmInstanceUUID());
    						}else if(HostTypeUtil.isHyperVVirtualMachine(item.getRhostType())){
    							nodeUuidList.add(item.getVmInstanceUUID());
    						}else{
    							nodeUuidList.add(item.getD2DUUID());
    						}*/
    						if(isHBBU){
    							nodeUuidList.add(item.getVmInstanceUUID());
    						}else{
    							nodeUuidList.add(item.getD2DUUID());
    						}
    						logger.info("getProtectedResourceUuids START, nodeID:" +item.getId());
    						logger.info("getProtectedResourceUuids, hostType:" +item.getRhostType());
    						logger.info("getProtectedResourceUuids, D2DUUID:" +item.getD2DUUID());
    						logger.info("getProtectedResourceUuids END, VmInstanceUUID:" +item.getVmInstanceUUID());
    					}
    					return nodeUuidList;
        			}
        		}
        		msg = "Not found archive to tape task by serverUUID and archiveUUID";
        	}else
        		msg = "Not found archive to tape task in plan, planId : " + unifiedPolicy.getId() + ", planName" + unifiedPolicy.getName();
         }else
        	 msg = "Not found host by serverUUID, serverUUID :" + serverUUID;
		throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, msg);
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

		List<Node> nodes = this.getNodeService().getNodeListByIDs(idList);

		return nodes;
	}
	
	public List<RpsArchiveConfiguationWrapper> getRpsArchiveConfiguationSummary(String planUUID)
	{
		return getRpsArchiveConfigSummary(planUUID, true);
	}
	
	
	public List<RpsArchiveConfiguationWrapper> getRpsArchiveConfigSummary(String planUUID, boolean needToEncrPwds)
	{
		
		List<RpsArchiveConfiguationWrapper> resultList=new ArrayList<RpsArchiveConfiguationWrapper>();

		if(planUUID==null || planUUID.isEmpty()){
			logger.error("Given plan UUID is empty. Hence returning NULL");
			return null;
		}
		
		UnifiedPolicy plan = null;
		try {
			plan = loadUnifiedPolicyByUuid(planUUID);
		} catch (EdgeServiceFault e) {
			logger.error(e);
		}
		
		if(plan==null){
			logger.error("Could not retrieve plan by using plan uuid: " + planUUID);
			return null;
		}
		
		if(plan.getBackupConfiguration()==null || plan.getBackupConfiguration().isD2dOrRPSDestType()){
			logger.error("Given plan with uuid: " + planUUID + " is not backing up to RPS");
			return null;
		}
		
		int rpsPolicyCount = plan.getRpsPolices().size();
		
		logger.info("Given plan with uuid: " + planUUID + " have number of RPS policies: " + rpsPolicyCount);
		
		if(rpsPolicyCount==0){
			logger.error("Given plan with uuid: " + planUUID + " does not have any RPS policies");
			return null;
		}
		
		for (int i = rpsPolicyCount - 1; i > 0; i--) {
			RPSPolicyWrapper previousRpsPolicyWrapper = plan.getRpsPolices().get(i - 1);
			RPSPolicy previous = previousRpsPolicyWrapper.getRpsPolicy();
			RPSPolicyWrapper currentRpsPolicyWrapper = plan.getRpsPolices().get(i);
			RPSPolicy currentRpsPolicy = currentRpsPolicyWrapper.getRpsPolicy();
			
			//if the filecopy or filearchive config exist for current rps policy then create RpsArchiveConfiguationWrapper object
			if(currentRpsPolicy.getFileArchiveConfiguration()!=null || currentRpsPolicy.getFileCopyConfiguration()!=null){
				//in the previous rps policy, the replication dest host info will be there...
				RpsHost rpsHost = new RpsHost();
				rpsHost.setRhostname(previous.getRpsSettings().getRpsReplicationSettings().getHostName());
				rpsHost.setUsername(previous.getRpsSettings().getRpsReplicationSettings().getUserName());
				rpsHost.setPassword(previous.getRpsSettings().getRpsReplicationSettings().getPassword());
				rpsHost.setUuid(previous.getRpsSettings().getRpsReplicationSettings().getUuid());
				int protocol = previous.getRpsSettings().getRpsReplicationSettings().getProtocol();
				rpsHost.setHttpProtocol(protocol==0 ? true : false);
				rpsHost.setPort(previous.getRpsSettings().getRpsReplicationSettings().getPort());
				rpsHost.setRhostId(previous.getRpsSettings().getRpsReplicationSettings().getHostId());
				
				RpsArchiveConfiguationWrapper rpsArchiveConfig = new RpsArchiveConfiguationWrapper();
				rpsArchiveConfig.setHost(rpsHost);
				rpsArchiveConfig.setFileCopyConfiguration(currentRpsPolicy.getFileCopyConfiguration());
				rpsArchiveConfig.setFileArchiveConfiguration(currentRpsPolicy.getFileArchiveConfiguration());
				rpsArchiveConfig.setPolicyId(currentRpsPolicy.getId());
				resultList.add(rpsArchiveConfig);
			}
		}
		
		//now process the first rps policy
		
		if(plan.getRpsPolices().get(0).getRpsPolicy().getFileArchiveConfiguration()!=null || 
				plan.getRpsPolices().get(0).getRpsPolicy().getFileCopyConfiguration()!=null){
			RpsHost backupRPSHost = plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost();
			RpsArchiveConfiguationWrapper rpsArchiveConfig = new RpsArchiveConfiguationWrapper();
			rpsArchiveConfig.setHost(backupRPSHost);
			rpsArchiveConfig.setFileCopyConfiguration(plan.getRpsPolices().get(0).getRpsPolicy().getFileCopyConfiguration());
			rpsArchiveConfig.setFileArchiveConfiguration(plan.getRpsPolices().get(0).getRpsPolicy().getFileArchiveConfiguration());
			rpsArchiveConfig.setPolicyId(plan.getRpsPolices().get(0).getRpsPolicy().getId());
			resultList.add(rpsArchiveConfig);
		}
		
		if(needToEncrPwds)
		{
		
			//Encrypt all the passwords nw
			for(RpsArchiveConfiguationWrapper configResult: resultList){
				//encrypt the rpshost pwd
				configResult.getHost().setPassword(WSJNI.AFEncryptString(configResult.getHost().getPassword()));
				
				//encrypt StrArchiveDestinationPassword of filecopyConfiguration
				if(configResult.getFileCopyConfiguration()!=null && configResult.getFileCopyConfiguration().getStrArchiveDestinationPassword()!=null){
					configResult.getFileCopyConfiguration().
						setStrArchiveDestinationPassword(WSJNI.AFEncryptString(configResult.getFileCopyConfiguration().getStrArchiveDestinationPassword()));
				}
				
				//encrypt cloudVendorPassword in ArchiveCloudDestInfo of filecopyConfiguration
				if(configResult.getFileCopyConfiguration()!=null && configResult.getFileCopyConfiguration().getCloudConfig()!=null &&
						configResult.getFileCopyConfiguration().getCloudConfig().getcloudVendorPassword()!=null){
					configResult.getFileCopyConfiguration().getCloudConfig().
						setcloudVendorPassword(WSJNI.AFEncryptString(configResult.getFileCopyConfiguration().getCloudConfig().getcloudVendorPassword()));
				}
			
				//encrypt cloudCertificatePassword in ArchiveCloudDestInfo of filecopyConfiguration
				if(configResult.getFileCopyConfiguration()!=null && configResult.getFileCopyConfiguration().getCloudConfig()!=null &&
						configResult.getFileCopyConfiguration().getCloudConfig().getcloudCertificatePassword()!=null){
					configResult.getFileCopyConfiguration().getCloudConfig().
						setcloudCertificatePassword(WSJNI.AFEncryptString(configResult.getFileCopyConfiguration().getCloudConfig().getcloudCertificatePassword()));
				}
				
				//encrypt cloudProxyPassword in ArchiveCloudDestInfo of filecopyConfiguration
				if(configResult.getFileCopyConfiguration()!=null && configResult.getFileCopyConfiguration().getCloudConfig()!=null &&
						configResult.getFileCopyConfiguration().getCloudConfig().getcloudProxyPassword()!=null){
					configResult.getFileCopyConfiguration().getCloudConfig().
						setcloudProxyPassword(WSJNI.AFEncryptString(configResult.getFileCopyConfiguration().getCloudConfig().getcloudProxyPassword()));
				}
				
				//encrypt StrArchiveDestinationPassword of filearchiveConfiguration
				if(configResult.getFileArchiveConfiguration()!=null && configResult.getFileArchiveConfiguration().getStrArchiveDestinationPassword()!=null){
					configResult.getFileArchiveConfiguration().
						setStrArchiveDestinationPassword(WSJNI.AFEncryptString(configResult.getFileArchiveConfiguration().getStrArchiveDestinationPassword()));
				}
				
				//encrypt cloudVendorPassword in ArchiveCloudDestInfo of filearchiveConfiguration
				if(configResult.getFileArchiveConfiguration()!=null && configResult.getFileArchiveConfiguration().getCloudConfig()!=null &&
						configResult.getFileArchiveConfiguration().getCloudConfig().getcloudVendorPassword()!=null){
					configResult.getFileArchiveConfiguration().getCloudConfig().
						setcloudVendorPassword(WSJNI.AFEncryptString(configResult.getFileArchiveConfiguration().getCloudConfig().getcloudVendorPassword()));
				}
			
				//encrypt cloudCertificatePassword in ArchiveCloudDestInfo of filearchiveConfiguration
				if(configResult.getFileArchiveConfiguration()!=null && configResult.getFileArchiveConfiguration().getCloudConfig()!=null &&
						configResult.getFileArchiveConfiguration().getCloudConfig().getcloudCertificatePassword()!=null){
					configResult.getFileArchiveConfiguration().getCloudConfig().
						setcloudCertificatePassword(WSJNI.AFEncryptString(configResult.getFileArchiveConfiguration().getCloudConfig().getcloudCertificatePassword()));
				}
				
				//encrypt cloudProxyPassword in ArchiveCloudDestInfo of filearchiveConfiguration
				if(configResult.getFileArchiveConfiguration()!=null && configResult.getFileArchiveConfiguration().getCloudConfig()!=null &&
						configResult.getFileArchiveConfiguration().getCloudConfig().getcloudProxyPassword()!=null){
					configResult.getFileArchiveConfiguration().getCloudConfig().
						setcloudProxyPassword(WSJNI.AFEncryptString(configResult.getFileArchiveConfiguration().getCloudConfig().getcloudProxyPassword()));
				}
			}
		}
		return resultList;
	}
	
	public List<ManualReplicationRPSParam> getReplicationRpsParamsByPolicyId(int policyId) throws EdgeServiceFault
	{
		logger.debug("getReplicationRpsParamsByPolicyId policyId="+policyId);
		List<ManualReplicationRPSParam> resultList = new ArrayList<ManualReplicationRPSParam>();
		if(policyId<=0){
			logger.error("getReplicationRpsParamsByPolicyId: Given policyId is empty. Hence returning NULL");
			return null;
		}
		UnifiedPolicy policy = null;
		try {
			policy = this.loadUnifiedPolicyById(policyId);
		} catch (EdgeServiceFault e) {
			logger.error("getReplicationRpsParamsByPolicyId cath an error when loadUnifiedPolicyById "+e.getMessage());
		}
		if(policy==null){
			logger.error("getReplicationRpsParamsByPolicyId: Could not retrieve plan by using policyId: " + policyId);
			return null;
		}
		int bitMap = this.getTaskBitmap(policy);
		if(!Utils.hasReplicationTask(bitMap)){
			logger.error("getReplicationRpsParamsByPolicyId: policy does not contain repliation task: " + policyId);
			return resultList;
		}
		if(!Utils.hasBackupTask(bitMap)) {
			logger.error("getReplicationRpsParamsByPolicyId: policy does not contain backup task: " + policyId);
			return resultList;
		}	
		List<String> nodeUUIDList = new ArrayList<String>();
		if(policy.getProtectedResources().size()>0){
			List<EdgeHost> hostList = new ArrayList<EdgeHost>();
			try {
				hostMgrDao.as_edge_host_list(0, 1, hostList);
			} catch (Exception e) {
				logger.error("getReplicationRpsParamsByPolicyId hostMgrDao.as_edge_host_list() failed.");
			}
			if (hostList.isEmpty()) {
				logger.error("getReplicationRpsParamsByPolicyId Cannot get hostname by id.");
			} else {				
				for (ProtectedResourceIdentifier resource:policy.getProtectedResources()) {
					for (EdgeHost edgeHost : hostList) {
						if(edgeHost.getRhostid()==resource.getId()){
							if(!StringUtil.isEmptyOrNull(edgeHost.getVmInstanceUuid())){								
								nodeUUIDList.add(edgeHost.getVmInstanceUuid());
								logger.debug("getReplicationRpsParamsByPolicyId addResourceNode id="+edgeHost.getRhostid()+" uuid="+edgeHost.getVmInstanceUuid());
							}else {
								nodeUUIDList.add(edgeHost.getD2DUUID());
								logger.debug("getReplicationRpsParamsByPolicyId addResourceNode id="+edgeHost.getRhostid()+" uuid="+edgeHost.getD2DUUID());
							}
							break;
						}
					}
				}
				if (!nodeUUIDList.isEmpty()) {
					logger.debug("getReplicationRpsParamsByPolicyId nodeUUIDList size="+nodeUUIDList.size());					
				}
			}
		}
		try {							
			int sourceRpsId = 0;
			String sourceRpsHostname = null;
			if(Utils.hasHbbuBackupTask(bitMap)){
				logger.debug("getReplicationRpsParamsByPolicyId Utils.hasHbbuBackupTask bitMap="+bitMap);
				if(policy.getVSphereBackupConfiguration().getBackupRpsDestSetting()!=null){								
					if(policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost()!=null){									
						sourceRpsId = policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
						sourceRpsHostname = policy.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
					}
				}
			} else {
				logger.debug("getReplicationRpsParamsByPolicyId !Utils.hasHbbuBackupTask bitMap="+bitMap);
				if(policy.getBackupConfiguration().getBackupRpsDestSetting()!=null){								
					if(policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost()!=null){									
						sourceRpsId = policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
						sourceRpsHostname = policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
					}
				}
			}
			logger.debug("getReplicationRpsParamsByPolicyId sourceRpsId="+sourceRpsId+",sourceRpsHostname="+sourceRpsHostname);
			
			for (int i = 0; i < policy.getRpsPolices().size(); i++) {
				boolean isMspReplicate = false;
				logger.debug("getReplicationRpsParamsByPolicyId i="+i+",size="+policy.getRpsPolices().size());
				RPSPolicy current = policy.getRpsPolices().get(i).getRpsPolicy();
				RPSPolicy next = null;
				// MSP Replication
				if(i + 1>=policy.getRpsPolices().size() && policy.getMspReplicationDestination()!=null){	
					logger.debug("getReplicationRpsParamsByPolicyId MSPReplication i="+i+",size="+policy.getRpsPolices().size());
					next = policy.getMspReplicationDestination().getReplicationRpsPolicy();
					isMspReplicate = true;
				} else {	
					if(i + 1>=policy.getRpsPolices().size()){
						logger.debug("getReplicationRpsParamsByPolicyId not MSPReplication return Result size="+resultList.size());
						return resultList;
					}
					logger.debug("getReplicationRpsParamsByPolicyId i="+i+",size="+policy.getRpsPolices().size());
					next = policy.getRpsPolices().get(i + 1).getRpsPolicy();
				}
				if(current==null||next==null){
					logger.error("getReplicationRpsParamsByPolicyId is not replicationTask, RpsPolices.size="+policy.getRpsPolices()+",i="+i);
					return null;
				}
				if(i==0){					
					ManualReplicationRPSParam param = new ManualReplicationRPSParam();	
					param.setNodeUuidList(nodeUUIDList);
					param.setSrcRpsHostId(sourceRpsId);
					param.setSrcRpsHostName(sourceRpsHostname);
					param.setSrcDataStoreUUID(current.getRpsSettings().getRpsDataStoreSettings().getDataStoreName());
					param.setSrcDataStoreName(current.getRpsSettings().getRpsDataStoreSettings().getDataStoreDisplayName());					
					param.setDstRpsHostId(current.getRpsSettings().getRpsReplicationSettings().getHostId());
					param.setDstRpsHostName(current.getRpsSettings().getRpsReplicationSettings().getHostName());
					param.setDstDataStoreUUID(next.getRpsSettings().getRpsDataStoreSettings().getDataStoreName());
					param.setDstDataStoreName(next.getRpsSettings().getRpsDataStoreSettings().getDataStoreDisplayName());
					param.setMspReplicate(isMspReplicate);
					ManualReplicationItem item = new ManualReplicationItem();
					param.setReplicationItem(item);
					//item.setNodeUUID(nodeUUID); // may be has many protect node so no set this
					item.setPolicyUUID(current.getId());					
					resultList.add(param);
					logger.debug("getReplicationRpsParamsByPolicyId i="+i+"add item="+param.toString());
				} 
				
				if(next.getRpsSettings().getRpsReplicationSettings().isEnableReplication()){
					boolean isMspReplicate2 = false;
					RPSPolicy next2 = null;
					// MSP Replication
					if(i + 2>=policy.getRpsPolices().size() && policy.getMspReplicationDestination()!=null){
						logger.debug("getReplicationRpsParamsByPolicyId MSPReplication i="+i+",size="+policy.getRpsPolices().size());
						next2 = policy.getMspReplicationDestination().getReplicationRpsPolicy();
						isMspReplicate2 = true;
					} else {	
						logger.debug("getReplicationRpsParamsByPolicyId i="+i+",size="+policy.getRpsPolices().size());
						next2 = policy.getRpsPolices().get(i + 2).getRpsPolicy();					
					}
					if(next2==null){
						logger.error("getReplicationRpsParamsByPolicyId is not replicationTask 2, RpsPolices.size="+policy.getRpsPolices()+",i="+i);
						return null;
					}	
					ManualReplicationRPSParam param = new ManualReplicationRPSParam();		
					param.setNodeUuidList(nodeUUIDList);
					param.setSrcRpsHostId(current.getRpsSettings().getRpsReplicationSettings().getHostId());
					param.setSrcRpsHostName(current.getRpsSettings().getRpsReplicationSettings().getHostName());
					param.setSrcDataStoreUUID(next.getRpsSettings().getRpsDataStoreSettings().getDataStoreName());
					param.setSrcDataStoreName(next.getRpsSettings().getRpsDataStoreSettings().getDataStoreDisplayName());				
					
					param.setDstRpsHostId(next.getRpsSettings().getRpsReplicationSettings().getHostId());
					param.setDstRpsHostName(next.getRpsSettings().getRpsReplicationSettings().getHostName());
					param.setDstDataStoreUUID(next2.getRpsSettings().getRpsDataStoreSettings().getDataStoreName());
					param.setDstDataStoreName(next2.getRpsSettings().getRpsDataStoreSettings().getDataStoreDisplayName());
					param.setMspReplicate(isMspReplicate2);
					ManualReplicationItem item = new ManualReplicationItem();
					param.setReplicationItem(item);
					//item.setNodeUUID(nodeUUID); // may be has many protect node so no set this
					item.setPolicyUUID(next.getId());					
					resultList.add(param);
					logger.debug("getReplicationRpsParamsByPolicyId i="+i+"add item="+param.toString());
				}
			}		
		} catch (Exception e) {
			logger.error("getReplicationRpsParamsByPolicyId cath an error ", e);
			return null;
			//throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_BadPolicyContent, "Unable to retrieve plan. planId:" + policyId);
		}
		return resultList;
	}
		
	public List<ManualReplicationRPSParam> getReplicationRpsParamsByPolicyName(String policyName) throws EdgeServiceFault
	{
		logger.debug("getReplicationRpsParamsByPolicyName policyId="+policyName);
		if(StringUtil.isEmptyOrNull(policyName)){
			logger.error("getReplicationRpsParamsByPolicyName: Given policyName is empty. Hence returning NULL");
			return null;
		}
		int policyId = this.getPolicyIdByName(policyName);
		if(policyId<=0){
			logger.error("getReplicationRpsParamsByPolicyName getPolicyIdByName policyName="+policyName+" policyId="+policyId);
			return null;
		}
		logger.debug("getReplicationRpsParamsByPolicyName getPolicyIdByName policyName="+policyName+" policyId="+policyId);
		return getReplicationRpsParamsByPolicyId(policyId);
	}

	@Override
	public Integer backupNodesByPolicyIdList(List<Integer> policyIdList, int backupType, String jobName)
			throws EdgeServiceFault {
		if(policyIdList==null||policyIdList.isEmpty()){
			logger.error("backupNodesByPolicyIdList policyIdList isNull or IsEmpty");
			return -1;
		}	
		logger.debug("backupNodesByPolicyIdList :"+policyIdList+", backupType:"+backupType+", jobName:"+jobName);
		
		List<Integer> nodeIds = new ArrayList<Integer>();
		for (int policyId:policyIdList) {
			List<EdgeHost> edgeHostList = new ArrayList<EdgeHost>();
			edgePolicyDao.as_edge_host_list_bypolicyid( policyId, edgeHostList );
			for (EdgeHost node:edgeHostList) {
				nodeIds.add(node.getRhostid());
			}
		}
		logger.debug("backupNodesByPolicyIdList nodeIds.size="+nodeIds.size()+", nodeIds:"+nodeIds);
		BackupNowTaskParameter parameter = new BackupNowTaskParameter();
		parameter.setModule(Module.SubmitD2DJob);
		parameter.setBackupType(backupType);
		parameter.setJobName(jobName);
		parameter.getEntityIds().addAll(nodeIds);
		ActionTaskManager<Integer> actionTaskManager = new ActionTaskManager<Integer>(parameter);
		return actionTaskManager.doAction();
	}

	@Override
	public List<Integer> checkRPSVersion(List<Integer> policyIdList)
			throws EdgeServiceFault {
		List<Integer> returnpolicyIdList = new ArrayList<Integer>();
		List<UnifiedPolicy> plans = PlanPauseResumeService.getInstance().getValidPlans(policyIdList);
		if(plans == null || plans.size() == 0){
			return returnpolicyIdList;
		}
		EdgeServiceFault rpsVersionLowException = null;
		for (UnifiedPolicy plan : plans) {
			try{
				String rpsHostName = getRPSNameByCheckRpsVersion(plan);
				if(rpsHostName != null && !rpsHostName.equalsIgnoreCase("")){
					String[] parameters = new String[]{rpsHostName};
					rpsVersionLowException = EdgeServiceFault.
							getFault(EdgeServiceErrorCode.policyManagement_Pause_Rps_Version_Low,
							parameters, "Destination Rps version is lower than current consle.");
					String errorMessage = WebServiceFaultMessageRetriever.
							getErrorMessage( DataFormatUtil.getServerLocale(),(rpsVersionLowException.getFaultInfo()));
					rpsVersionLowException.getFaultInfo().setMessage(errorMessage);
					PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Error, "", errorMessage);
					logger.error("PlanPauseResumeService.enablePolicies(checkRpsVersion) failed, error message = " + errorMessage);
					continue;
				}
			} catch (Exception e) {
				String errorMessage = e.getMessage();
				if (e instanceof SOAPFaultException) {
					errorMessage = ((SOAPFaultException) e).getFault().getFaultString();
				}
				PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Error, "", errorMessage);
				logger.error("PlanPauseResumeService.enablePolicies(checkRpsVersion) failed, error message = " + errorMessage, e);
				continue;
			}
			returnpolicyIdList.add(plan.getId());
		}
		if(returnpolicyIdList.size() == 0){
			if(rpsVersionLowException == null){
				for (UnifiedPolicy plan : plans) {// check again, throw exception directly
					checkRpsVersion(plan, 0);
				}
			}else if(policyIdList != null && policyIdList.size() == 1){
				throw rpsVersionLowException;
			}
		}
		return returnpolicyIdList;
	}
	
	private String getRPSNameByCheckRpsVersion(UnifiedPolicy plan) throws EdgeServiceFault {
		int rpsPolicyCount = plan.getRpsPolices().size();
		if (rpsPolicyCount == 0) {
			return "";
		}
		for (int i = rpsPolicyCount - 2; i >= 0; --i) {
			RPSPolicyWrapper currentRpsPolicyWrapper = plan.getRpsPolices().get(i);
			RPSPolicy currentRpsPolicy = currentRpsPolicyWrapper.getRpsPolicy();
			int hostid=currentRpsPolicy.getRpsSettings().getRpsReplicationSettings().getHostId();
			String hostname=currentRpsPolicy.getRpsSettings().getRpsReplicationSettings().getHostName();
			
			if(EdgeCommonUtil.compareWithConsoleVersion(hostid)<0){
				return hostname;
			}
		}
		if(plan.getBackupConfiguration()!=null){
			if(!plan.getBackupConfiguration().isD2dOrRPSDestType()){
				int hostid=plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
				String hostname=plan.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				if(EdgeCommonUtil.compareWithConsoleVersion(hostid)<0){
					return hostname;
				}
			}
		}
		if(plan.getVSphereBackupConfiguration()!=null){
			if(!plan.getVSphereBackupConfiguration().isD2dOrRPSDestType()){
				int hostid=plan.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostId();
				String hostname=plan.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				if(EdgeCommonUtil.compareWithConsoleVersion(hostid)<0){
					return hostname;
				}
			}
		}
		return "";
	}

	@Override
	public List<ManualFilecopyParam> getFilecopyParamsByPolicyId(long policyId) throws EdgeServiceFault
	{
		logger.debug("getFilecopyParamsByPolicyId policyId="+policyId);
		if(policyId<=0){
			return null;
		}
		List<ManualFilecopyParam> resultList = new ArrayList<ManualFilecopyParam>();
		if(policyId<=0){
			logger.error("getFilecopyParamsByPolicyId: Given policyId is empty. Hence returning NULL");
			return null;
		}
		UnifiedPolicy policy = null;
		try {
			policy = this.loadUnifiedPolicyById((int)policyId);
		} catch (EdgeServiceFault e) {
			logger.error("getFilecopyParamsByPolicyId catch an error when loadUnifiedPolicyById "+e.getMessage());
		}
		if(policy==null){
			logger.error("getFilecopyParamsByPolicyId: Could not retrieve plan by using policyId: " + policyId);
			return null;
		}
		int bitMap = this.getTaskBitmap(policy);
		if(!Utils.hasFileCopyTask(bitMap)){
			logger.error("getFilecopyParamsByPolicyId: policy does not contain filecopy task: " + policyId);
			return resultList;
		}
		if(!Utils.hasBackupTask(bitMap)) {
			logger.error("getFilecopyParamsByPolicyId: policy does not contain backup task: " + policyId);
			return resultList;
		}	
		List<String> nodeUUIDList = new ArrayList<String>();
		List<Integer> nodeIdList = new ArrayList<Integer>();
		if(policy.getProtectedResources().size()>0){
			List<EdgeHost> hostList = new ArrayList<EdgeHost>();
			try {
				hostMgrDao.as_edge_host_list(0, 1, hostList);
			} catch (Exception e) {
				logger.error("getFilecopyParamsByPolicyId hostMgrDao.as_edge_host_list() failed.");
			}
			if (hostList.isEmpty()) {
				logger.error("getFilecopyParamsByPolicyId Cannot get hostname by id.");
			} else {				
				for (ProtectedResourceIdentifier resource:policy.getProtectedResources()) {
					for (EdgeHost edgeHost : hostList) {
						if(edgeHost.getRhostid()==resource.getId()){
							if(!StringUtil.isEmptyOrNull(edgeHost.getVmInstanceUuid())){								
								nodeUUIDList.add(edgeHost.getVmInstanceUuid());
								nodeIdList.add(edgeHost.getRhostid());
								logger.debug("getFilecopyParamsByPolicyId addResourceNode id="+edgeHost.getRhostid()+" uuid="+edgeHost.getVmInstanceUuid());
							}else {
								nodeUUIDList.add(edgeHost.getD2DUUID());
								nodeIdList.add(edgeHost.getRhostid());
								logger.debug("getFilecopyParamsByPolicyId addResourceNode id="+edgeHost.getRhostid()+" uuid="+edgeHost.getD2DUUID());
							}
							break;
						}
					}
				}
				if (!nodeUUIDList.isEmpty()) {
					logger.debug("getFilecopyParamsByPolicyId nodeUUIDList size="+nodeUUIDList.size());					
				}
			}
		}
		try {							
			
			int rpsPolicyCount = policy.getRpsPolices().size();
			
			//Process the filecopy having parent as replication task
			for (int i = rpsPolicyCount - 1; i > 0; i--) {
				
				RPSPolicyWrapper previousRpsPolicyWrapper = policy.getRpsPolices().get(i - 1);
				RPSPolicy previous = previousRpsPolicyWrapper.getRpsPolicy();
				
				RPSPolicyWrapper currentRpsPolicyWrapper = policy.getRpsPolices().get(i);
				RPSPolicy currentRpsPolicy = currentRpsPolicyWrapper.getRpsPolicy();
				
				//if the filecopy or filearchive config exist for current rps policy then set the filecopy catalog path as datastore shared path
				if(currentRpsPolicy.getFileCopyConfiguration()!=null){
					int rHostId = previous.getRpsSettings().getRpsReplicationSettings().getHostId();
					String rHostName = previous.getRpsSettings().getRpsReplicationSettings().getHostName();
					
					ManualFilecopyParam param = new ManualFilecopyParam();	
					param.setNodeUuidList(nodeUUIDList);
					param.setNodeIdList(nodeIdList);
					param.setSrcRpsHostId(rHostId);
					param.setSrcRpsHostName(rHostName);
					param.setSrcDataStoreUUID(currentRpsPolicy.getRpsSettings().getRpsDataStoreSettings().getDataStoreName());
					param.setSrcDataStoreName(currentRpsPolicy.getRpsSettings().getRpsDataStoreSettings().getDataStoreDisplayName());
					param.setbArchiveToDrive(currentRpsPolicy.getFileCopyConfiguration().isbArchiveToDrive());
					if(param.isbArchiveToDrive())
						param.setStrArchiveToDrivePath(currentRpsPolicy.getFileCopyConfiguration().getStrArchiveToDrivePath());
					else
						param.setStrCloudBucket(currentRpsPolicy.getFileCopyConfiguration().getCloudConfig().getAccountName());
					param.setSrcPolicyUUID(currentRpsPolicy.getId());
					param.setParentTaskId(currentRpsPolicy.getFileCopyConfiguration().getSelectedSourceId());
					resultList.add(param);
					logger.debug("getFilecopyParamsByPolicyId i="+i+"add item="+param.toString());
				}
			}
			//check if backup dest is RPS
			if(!policy.getBackupConfiguration().isD2dOrRPSDestType()){
				//now process the first rps policy 
				if(policy.getRpsPolices().get(0).getRpsPolicy().getFileCopyConfiguration()!=null)
				{
					RpsHost backupRPSHost = policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost();
					int rHostId = backupRPSHost.getRhostId();
					String rHostName = backupRPSHost.getRhostname();
					
					ManualFilecopyParam param = new ManualFilecopyParam();	
					param.setNodeUuidList(nodeUUIDList);
					param.setNodeIdList(nodeIdList);
					param.setSrcRpsHostId(rHostId);
					param.setSrcRpsHostName(rHostName);
					param.setSrcDataStoreUUID(policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsDataStoreSettings().getDataStoreName());
					param.setSrcDataStoreName(policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsDataStoreSettings().getDataStoreDisplayName());
					param.setbArchiveToDrive(policy.getRpsPolices().get(0).getRpsPolicy().getFileCopyConfiguration().isbArchiveToDrive());
					if(param.isbArchiveToDrive())
						param.setStrArchiveToDrivePath(policy.getRpsPolices().get(0).getRpsPolicy().getFileCopyConfiguration().getStrArchiveToDrivePath());
					else
						param.setStrCloudBucket(policy.getRpsPolices().get(0).getRpsPolicy().getFileCopyConfiguration().getCloudConfig().getAccountName());
					param.setSrcPolicyUUID(policy.getRpsPolices().get(0).getRpsPolicy().getId());
					param.setParentTaskId(policy.getRpsPolices().get(0).getRpsPolicy().getFileCopyConfiguration().getSelectedSourceId());
					resultList.add(param);
					logger.debug("getFilecopyParamsByPolicyId i="+0+"add item="+param.toString());
				}
				
				//reverse the result list to get the proper order
				Collections.reverse(resultList);
				
			}
			//if backup dest is shared path
			else{
				ManualFilecopyParam param = new ManualFilecopyParam();	
				param.setNodeUuidList(nodeUUIDList);
				param.setNodeIdList(nodeIdList);
				param.setSrcRpsHostId(0); //0 in case of non-rps
				param.setSrcRpsHostName(null); //null in case of non-rps
				param.setSrcDataStoreUUID(null); //null in case of non-rps
				param.setSrcDataStoreName(policy.getBackupConfiguration().getDestination()); //destination path in case of non-rps
				param.setbArchiveToDrive(policy.getFileCopySettingsWrapper().get(0).getArchiveConfiguration().isbArchiveToDrive());
				if(param.isbArchiveToDrive())
					param.setStrArchiveToDrivePath(policy.getFileCopySettingsWrapper().get(0).getArchiveConfiguration().getStrArchiveToDrivePath());
				else
					param.setStrCloudBucket(policy.getFileCopySettingsWrapper().get(0).getArchiveConfiguration().getCloudConfig().getAccountName());
				param.setSrcPolicyUUID(null); //null in case of non-rps
				param.setParentTaskId(policy.getFileCopySettingsWrapper().get(0).getArchiveConfiguration().getSelectedSourceId());
				resultList.add(param);
			}
			
			//now set the currentTaskId values
			for(ManualFilecopyParam fcParam: resultList){
				for(FileCopySettingWrapper fcWrapper: policy.getFileCopySettingsWrapper()){
					if(fcWrapper.getArchiveConfiguration().getSelectedSourceId()!=null && fcWrapper.getArchiveConfiguration().getSelectedSourceId().equalsIgnoreCase(fcParam.getParentTaskId())){
						fcParam.setCurrentTaskId(fcWrapper.getTaskId());
						break;
					}
				}
			}
			
			
		} catch (Exception e) {
			logger.error("getFilecopyParamsByPolicyId cath an error ", e);
			return null;
		}
		return resultList;
	}

	@Override
	public List<ManualFilecopyParam> getFileArchiveParamsByPolicyId(
			long policyId) throws EdgeServiceFault {
		logger.debug("getFileArchiveParamsByPolicyId policyId="+policyId);
		if(policyId<=0){
			return null;
		}
		List<ManualFilecopyParam> resultList = new ArrayList<ManualFilecopyParam>();
		if(policyId<=0){
			logger.error("getFileArchiveParamsByPolicyId: Given policyId is empty. Hence returning NULL");
			return null;
		}
		UnifiedPolicy policy = null;
		try {
			policy = this.loadUnifiedPolicyById((int)policyId);
		} catch (EdgeServiceFault e) {
			logger.error("getFileArchiveParamsByPolicyId catch an error when loadUnifiedPolicyById "+e.getMessage());
		}
		if(policy==null){
			logger.error("getFileArchiveParamsByPolicyId: Could not retrieve plan by using policyId: " + policyId);
			return null;
		}
		int bitMap = this.getTaskBitmap(policy);
		if(!Utils.hasFileArchiveTask(bitMap)){
			logger.error("getFileArchiveParamsByPolicyId: policy does not contain filearchive task: " + policyId);
			return resultList;
		}
		if(!Utils.hasBackupTask(bitMap)) {
			logger.error("getFileArchiveParamsByPolicyId: policy does not contain backup task: " + policyId);
			return resultList;
		}	
		List<String> nodeUUIDList = new ArrayList<String>();
		List<Integer> nodeIdList = new ArrayList<Integer>();
		if(policy.getProtectedResources().size()>0){
			List<EdgeHost> hostList = new ArrayList<EdgeHost>();
			try {
				hostMgrDao.as_edge_host_list(0, 1, hostList);
			} catch (Exception e) {
				logger.error("getFileArchiveParamsByPolicyId hostMgrDao.as_edge_host_list() failed.");
			}
			if (hostList.isEmpty()) {
				logger.error("getFileArchiveParamsByPolicyId Cannot get hostname by id.");
			} else {				
				for (ProtectedResourceIdentifier resource:policy.getProtectedResources()) {
					for (EdgeHost edgeHost : hostList) {
						if(edgeHost.getRhostid()==resource.getId()){
							if(!StringUtil.isEmptyOrNull(edgeHost.getVmInstanceUuid())){								
								nodeUUIDList.add(edgeHost.getVmInstanceUuid());
								nodeIdList.add(edgeHost.getRhostid());
								logger.debug("getFileArchiveParamsByPolicyId addResourceNode id="+edgeHost.getRhostid()+" uuid="+edgeHost.getVmInstanceUuid());
							}else {
								nodeUUIDList.add(edgeHost.getD2DUUID());
								nodeIdList.add(edgeHost.getRhostid());
								logger.debug("getFileArchiveParamsByPolicyId addResourceNode id="+edgeHost.getRhostid()+" uuid="+edgeHost.getD2DUUID());
							}
							break;
						}
					}
				}
				if (!nodeUUIDList.isEmpty()) {
					logger.debug("getFileArchiveParamsByPolicyId nodeUUIDList size="+nodeUUIDList.size());					
				}
			}
		}
		try {							
			//Note: Filearchive is not applicable for replication tasks. It is only for backup task.
			//Check backup dest is RPS
			if(!policy.getBackupConfiguration().isD2dOrRPSDestType()){
				//process the first rps policy 
				if(policy.getRpsPolices().get(0).getRpsPolicy().getFileArchiveConfiguration()!=null)
				{
					RpsHost backupRPSHost = policy.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost();
					int rHostId = backupRPSHost.getRhostId();
					String rHostName = backupRPSHost.getRhostname();
					
					ManualFilecopyParam param = new ManualFilecopyParam();	
					param.setNodeUuidList(nodeUUIDList);
					param.setNodeIdList(nodeIdList);
					param.setSrcRpsHostId(rHostId);
					param.setSrcRpsHostName(rHostName);
					param.setSrcDataStoreUUID(policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsDataStoreSettings().getDataStoreName());
					param.setSrcDataStoreName(policy.getRpsPolices().get(0).getRpsPolicy().getRpsSettings().getRpsDataStoreSettings().getDataStoreDisplayName());
					param.setbArchiveToDrive(policy.getRpsPolices().get(0).getRpsPolicy().getFileArchiveConfiguration().isbArchiveToDrive());
					if(param.isbArchiveToDrive())
						param.setStrArchiveToDrivePath(policy.getRpsPolices().get(0).getRpsPolicy().getFileArchiveConfiguration().getStrArchiveToDrivePath());
					else
						param.setStrCloudBucket(policy.getRpsPolices().get(0).getRpsPolicy().getFileArchiveConfiguration().getCloudConfig().getAccountName());
					param.setSrcPolicyUUID(policy.getRpsPolices().get(0).getRpsPolicy().getId());
					param.setParentTaskId(policy.getRpsPolices().get(0).getRpsPolicy().getFileArchiveConfiguration().getSelectedSourceId());
					resultList.add(param);
					logger.debug("getFileArchiveParamsByPolicyId i="+0+"add item="+param.toString());
				}
			}
			//if backup dest is shared path
			else{
				ManualFilecopyParam param = new ManualFilecopyParam();	
				param.setNodeUuidList(nodeUUIDList);
				param.setNodeIdList(nodeIdList);
				param.setSrcRpsHostId(0); //0 in case of non-rps
				param.setSrcRpsHostName(null); //null in case of non-rps
				param.setSrcDataStoreUUID(null); //null in case of non-rps
				param.setSrcDataStoreName(policy.getBackupConfiguration().getDestination()); //destination path in case of non-rps
				param.setbArchiveToDrive(policy.getFileArchiveConfiguration().isbArchiveToDrive());
				if(param.isbArchiveToDrive())
					param.setStrArchiveToDrivePath(policy.getFileArchiveConfiguration().getStrArchiveToDrivePath());
				else
					param.setStrCloudBucket(policy.getFileArchiveConfiguration().getCloudConfig().getAccountName());
				param.setSrcPolicyUUID(null); //null in case of non-rps
				param.setParentTaskId(policy.getFileArchiveConfiguration().getSelectedSourceId());
				resultList.add(param);
			}
			
			//now set the currentTaskId values
		} catch (Exception e) {
			logger.error("getFilecopyParamsByPolicyId cath an error ", e);
			return null;
		}
		return resultList;
	}
}