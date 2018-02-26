package com.ca.arcserve.edge.app.base.webservice.gateway;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.arcserve.edge.common.annotation.NonSecured;
import com.ca.arcflash.rps.webservice.replication.CAProxy;
import com.ca.arcflash.rps.webservice.replication.CAProxySelector;
import com.ca.arcflash.rps.webservice.replication.HttpProxy;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcserve.edge.app.base.appdaos.EdgeAD;
import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeGatewayEntity;
import com.ca.arcserve.edge.app.base.appdaos.EdgeGatewayUpdateStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeSiteInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeGatewayDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSphereDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.eventcenter.EdgeEventCenter;
import com.ca.arcserve.edge.app.base.common.eventcenter.events.GatewayHostRegisteredEvent;
import com.ca.arcserve.edge.app.base.common.eventcenter.events.GatewayHostRegisteredEventForConsole;
import com.ca.arcserve.edge.app.base.common.eventcenter.events.NewSiteCreatedEvent;
import com.ca.arcserve.edge.app.base.common.udpapplication.ConsoleApplication;
import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfo;
import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeCommonService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeConfigurationService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeGatewayService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeLinuxD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.IGatewayHostService;
import com.ca.arcserve.edge.app.base.webservice.action.ActionTaskManager;
import com.ca.arcserve.edge.app.base.webservice.actioncenter.ActionCenter;
import com.ca.arcserve.edge.app.base.webservice.actioncenter.exceptions.ActionCenterException;
import com.ca.arcserve.edge.app.base.webservice.alert.AlertManager;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.client.IWebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.common.EdgeCommonServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.action.SendRegistrationEmailsParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionCategory;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItem;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItemId;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionSeverity;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConfigurationParam;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeRegistryInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion.IllegalVersionFormatException;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;
import com.ca.arcserve.edge.app.base.webservice.contract.email.EmailTemplateFeature;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayHostHeartbeatParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayHostHeartbeatResponse2;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayLoginInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUnregistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdateStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdateStatusCode;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdatesInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GenerateGatewayRegStrParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteAction;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo.SiteStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.UpdateDownloadFile;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EsxVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeD2DRegServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeLinuxD2DRegServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;
import com.ca.arcserve.edge.app.base.webservice.gateway.settings.ConsoleMessageServiceSettings;
import com.ca.arcserve.edge.app.base.webservice.gateway.settings.GatewayMessageServiceSettings;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.CommonServiceFacade;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductImageInsurer;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageRegistry;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageRegistry.PackageInfo;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageRegistry.ProductPackageState;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageType;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.download.DownloadResults.DownloadResult.DownloadResultValue;
import com.ca.arcserve.edge.app.rps.webservice.rps.IEdgeRPSRegService;
import com.ca.arcserve.edge.app.rps.webservice.rpsReg.EdgeRPSRegServiceImpl;
import com.ca.arcserve.edge.webservice.jni.model.HttpDownloadResult;

public class EdgeGatewayBean implements IEdgeGatewayService, IEdgeGatewayLocalService {
	
	private static Logger logger = Logger.getLogger( EdgeGatewayBean.class );
	
	public static final int DEFAULT_MQBROKER_PORT = 61616;
	public static final int DEFAULT_HEARTBEAT_INTERVAL = 30 * 60; // in seconds
	public static final double INTERVAL_WEIGHT = 1.16667;
	public static final int DEFAULT_GATEWAY_UPGRADE_TIMEOUT = 30 * 60; // 30 minutes, in seconds
	
	private IEdgeGatewayDao gatewayDao = DaoFactory.getDao(IEdgeGatewayDao.class);
	private IEdgeConnectInfoDao connInfoDao = DaoFactory.getDao( IEdgeConnectInfoDao.class );
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao( IEdgeHostMgrDao.class );
	private IEdgeSettingDao settingDao = DaoFactory.getDao( IEdgeSettingDao.class );
	private IEdgeVSphereDao vSphereDao = DaoFactory.getDao(IEdgeVSphereDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgeAdDao adDao = DaoFactory.getDao(IEdgeAdDao.class);
	private String localGatewayUuid = CommonUtil.retrieveCurrentAppUUID();
	private IActivityLogService activityLogService = new ActivityLogServiceImpl();
	
	private static List<Integer> nodesToUpdateRegFile = new ArrayList<>();
	
	@Override
	public GatewayEntity getGatewayById(GatewayId gatewayId) throws EdgeServiceFault {
		if ((gatewayId == null) || !gatewayId.isValid()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.GATEWAY_NotFound, "Cannot find the gateway by id  " + gatewayId);
		}
		
		List<EdgeGatewayEntity> gateways = new ArrayList<EdgeGatewayEntity>();
		gatewayDao.as_edge_gateway_getById(gatewayId.getRecordId(), gateways);
		if (gateways.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.GATEWAY_NotFound, "Cannot find the gateway by id " + gatewayId);
		}
		
		GatewayEntity gateway = convertDaoGatewayEntity( gateways.get(0) );
		gateway.setLocalGateway(gateway.getUuid().equalsIgnoreCase(localGatewayUuid));
		
		return gateway;
	}

	@Override
	public void addNode(GatewayId gatewayId, int hostId) throws EdgeServiceFault {
		bindEntity(gatewayId, hostId, EntityType.Node);
	}
	
	@Override
	public void bindEntity(GatewayId gatewayId, int entityId, EntityType entityType) {
		logger.info( "bindEntity(): Bind entity to gateway. Gateway ID: " + gatewayId +
			", Entity type: " + entityType + ", Entity ID: " + entityId );
		if (gatewayId.isValid() && entityId > 0) {
			gatewayDao.as_edge_gateway_entity_map_addOrUpdate(gatewayId.getRecordId(), entityId, entityType);
		}
	}
	
	@Override
	public void unbindEntity(int entityId, EntityType entityType) throws EdgeServiceFault {
		logger.info( "unbindEntity(): Unbind entity from gateway. Entity type: " + entityType + ", Entity ID: " + entityId );
		gatewayDao.as_edge_gateway_entity_map_delete(entityId, entityType);
	}

	@Override
	public GatewayEntity getGatewayByHostId(int hostId) throws EdgeServiceFault {
		return getGatewayByEntityId(hostId, EntityType.Node);
	}
	
	@Override
	public GatewayEntity getGatewayByEntityId(int entityId, EntityType entityType) throws EdgeServiceFault {
		List<IntegerId> gatewayRecIds = new ArrayList<IntegerId>();
		gatewayDao.as_edge_gateway_entity_map_getGatewayId(entityId, entityType, gatewayRecIds);
		if (gatewayRecIds.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.GATEWAY_NotFound, "Cannot find the gateway by entity id  " + entityId + ", type = " + entityType);
		}
		
		int gatewayRecId = gatewayRecIds.get(0).getId();
		GatewayEntity gateway = getGatewayById(new GatewayId( gatewayRecId ));
		
		return gateway;
	}
	
	@Override
	public GatewayRegistrationResult addOrUpdateGateway(GatewayEntity gateway) throws EdgeServiceFault {
		if (gateway == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "the gateway is null.");
		}
		
		if (gateway.getName() == null || gateway.getName().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "the gateway name is null or empty.");
		}
		
		if (gateway.getUuid() == null || gateway.getUuid().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "the gateway uuid is null or empty.");
		}
		
		String gatewayDescription = gateway.getDescription() == null ? "" : gateway.getDescription();
		
		GatewayRegistrationResult result = new GatewayRegistrationResult();
		
		if (!gateway.getId().isValid()) // add
		{
			int[] newGatewayRecId = new int[1];
			gatewayDao.as_edge_gateway_add(gateway.getName(), gatewayDescription, gateway.getUuid(), newGatewayRecId);
			if (newGatewayRecId[0] == GatewayId.INVALID_RECORD_ID) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.GATEWAY_AlreadyExist, "Duplicated gateway with name " + gateway.getName());
			}
			result.setGatewayId( new GatewayId( newGatewayRecId[0] ) );
		}
		else // update
		{
			gatewayDao.as_edge_gateway_update(gateway.getId().getRecordId(), gateway.getName(), gatewayDescription, gateway.getUuid());
		}
		
		result.setConsoleUuid( CommonUtil.retrieveCurrentAppUUIDWithDecrypt( false ) );
		return result;
	}

	@Override
	public void deleteGateway(List<Integer> gatewayIds) throws EdgeServiceFault {
		if (gatewayIds != null) {
			for (int id : gatewayIds) {
				gatewayDao.as_edge_gateway_delete(id);
			}
		}
	}

	@Override
	public PagingResult<GatewayEntity> getPagingGateway(PagingConfig config) throws EdgeServiceFault {
		List<EdgeGatewayEntity> daoGateways = new ArrayList<EdgeGatewayEntity>();
		gatewayDao.as_edge_gateway_getById(0, daoGateways);
		
		List<GatewayEntity> gateways = new ArrayList<GatewayEntity>();
		for (EdgeGatewayEntity daoGateway : daoGateways)
			gateways.add( convertDaoGatewayEntity( daoGateway ) );
		
		PagingResult<GatewayEntity> result = PagingResult.create(config, gateways);
		for (GatewayEntity gateway : result.getData()) {
			gateway.setLocalGateway(gateway.getUuid().equalsIgnoreCase(localGatewayUuid));
		}
		
		return result;
	}
	
	@Override
	public GatewayEntity getLocalGateway() {
		List<EdgeGatewayEntity> gatewayList = new ArrayList<>();
		this.gatewayDao.getLocalGateway( gatewayList );
		if (gatewayList.size() > 0){
			EdgeGatewayEntity daoGatewayEntity = gatewayList.get( 0 );
			return convertDaoGatewayEntity(daoGatewayEntity);
		}
		return null;
	}
	
	private GatewayEntity convertDaoGatewayEntity( EdgeGatewayEntity daoEntity )
	{
		if (daoEntity == null)
			throw new IllegalArgumentException();
		
		EdgeSimpleVersion hostVersion = null;
		if (daoEntity.getHostVersion() != null)
		{
			try
			{
				hostVersion = EdgeSimpleVersion.parseVersionString( daoEntity.getHostVersion() );
			}
			catch (IllegalVersionFormatException e)
			{
				// Don't print error logs since this may cause confusion. The version will
				// be empty until the gateway host logged in at lease once.
				//
				//logger.error( "Error parsing host version. Version string: " + daoEntity.getHostVersion(), e );
			}
		}
		if (hostVersion == null)
			hostVersion = new EdgeSimpleVersion();
		
		GatewayEntity entity = new GatewayEntity();
		entity.setId( new GatewayId( daoEntity.getId() ) );
		entity.setName( daoEntity.getName() );
		entity.setDescription( daoEntity.getDescription() );
		entity.setUuid( daoEntity.getUuid() );
		entity.setHostUuid( daoEntity.getHostUuid() );
		entity.setHostName( daoEntity.getHostName() );
		//entity.setHostVersion( hostVersion );
		entity.setHostVersion( daoEntity.getHostVersion() );
		entity.setLocal( daoEntity.getIsLocal() != 0 );
		entity.setLastContactTime( daoEntity.getLastContactTime() );
		entity.setCreateTime( daoEntity.getCreateTime() );
		entity.setUpdateTime( daoEntity.getUpdateTime() );
		entity.setLocalGateway( daoEntity.isLocalGateway() );
		
		return entity;
	}


	@Override
	public List<GatewayEntity> getAllGateways() throws EdgeServiceFault {
		PagingConfig config = new PagingConfig();
		config.setStartIndex(0);
		config.setCount(Integer.MAX_VALUE);
		
		return getPagingGateway(config).getData();
	}
	
	@Override
	public List<GatewayEntity> getAllValidGateways() throws EdgeServiceFault {
		PagingConfig config = new PagingConfig();
		config.setStartIndex(0);
		config.setCount(Integer.MAX_VALUE);
		List<GatewayEntity> list = new ArrayList<GatewayEntity>();
		for (GatewayEntity gateway:getPagingGateway(config).getData()) {
			if(gateway.getHostUuid()!=null)
				list.add(gateway);
		}
		return list;
	}		
	
	private static Object updateSiteLockObj = new Object();

	@Override
	public SiteId createSite( SiteInfo siteInfo ) throws EdgeServiceFault
	{
		int siteRecId = SiteId.INVALID_RECORD_ID;
		
		validateSiteInfo( siteInfo );
		
		synchronized (updateSiteLockObj)
		{
			// check name duplication
			
			if (isSiteNameInUse( siteInfo.getName(), null ))
			{
				logger.error( "Error create site. Site name is in use. Site name: " + siteInfo.getName() );
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_SiteNameIsInUse,
					new Object[] { siteInfo.getName() }, "GATEWAY_SiteNameIsInUse" );
			}
			
			// insert database record
			
			UUID gatewayUuid = UUID.randomUUID();
			
			int[] idArray = new int[1];
			int isLocal = 0; // false
			this.gatewayDao.createSite(
				siteInfo.getName(), siteInfo.getDescription(), gatewayUuid.toString(), isLocal,siteInfo.getAddress(), 
				siteInfo.getEmail(),siteInfo.getHeartbeatInterval(), idArray );
			siteRecId = idArray[0];
		}
			
		// write audit log
		
		String message = EdgeCMWebServiceMessages.getMessage(
			"gateway_NewSiteAdded", siteInfo.getName() );
		//this.writeAuditLog( message );
		logger.debug("createSite:message:"+message);
		
		// send event
		
		List<EdgeSiteInfo> daoSiteInfoList = new ArrayList<>();
		this.gatewayDao.getSiteInfo( siteRecId, daoSiteInfoList );
		SiteInfo completeSiteInfo = this.convertDaoSiteInfo( daoSiteInfoList.get( 0 ) );
		
		List<EdgeGatewayEntity> daoGatewayList = new ArrayList<>();
		this.gatewayDao.as_edge_gateway_getById( completeSiteInfo.getGatewayId().getRecordId(), daoGatewayList );
		GatewayEntity gateway = this.convertDaoGatewayEntity( daoGatewayList.get( 0 ) );
		
		EdgeEventCenter.getInstance().publishEvent(
			new NewSiteCreatedEvent( completeSiteInfo, gateway ), this );
		
		SiteId siteId = new SiteId( siteRecId );
		
		// add action items
		
		this.addSiteActionItem( siteId, SiteAction.RegisterGatewayHost, ActionSeverity.Medium, "aaa" );
		
		return siteId;
	}
	
	private boolean isSiteNameInUse( String siteName, SiteId ignoreId )
	{
		List<EdgeSiteInfo> siteInfoList = new ArrayList<>();
		this.gatewayDao.querySites( siteName, siteInfoList );
		if (siteInfoList.size() == 0)
			return false;
		
		if ((ignoreId != null) &&
			(siteInfoList.size() == 1) && (siteInfoList.get( 0 ).getId() == ignoreId.getRecordId()))
			return false;
		
		return true;
			
	}

	@Override
	public void updateSite( SiteId siteId, SiteInfo siteInfo )
		throws EdgeServiceFault
	{
		validateSiteId( siteId );
		validateSiteInfo( siteInfo );
		
		synchronized (updateSiteLockObj)
		{
			// load old site info
			
			List<EdgeSiteInfo> daoSiteInfoList = new ArrayList<>();
			this.gatewayDao.getSiteInfo( siteId.getRecordId(), daoSiteInfoList );
			if (daoSiteInfoList.size() == 0)
			{
				logger.error( "The specified site cannot be found. Site ID: " + siteId );
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_SiteNotFound,
					"GATEWAY_SiteNotFound" );
			}
			EdgeSiteInfo oldSiteInfo = daoSiteInfoList.get( 0 );
			
			// check name duplication
			
			if (isSiteNameInUse( siteInfo.getName(), siteId ))
			{
				logger.error( "Error create site. Site name is in use. Site name: " + siteInfo.getName() );
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_SiteNameIsInUse,
					new Object[] { siteInfo.getName() }, "GATEWAY_SiteNameIsInUse" );
			}
			
			// if registrationKey changed NoNeed set HeartBeatInterval,for user will restart gatewayService
			if (siteInfo.getRegistrationText().equals(oldSiteInfo.getRegistrationText())
					&& oldSiteInfo.getHeartbeatInterval() != siteInfo.getHeartbeatInterval()) {				
				if(siteInfo.getHeartbeatInterval()==0)
					siteInfo.setHeartbeatInterval(DEFAULT_HEARTBEAT_INTERVAL);		
				logger.debug("siteInfo siteStatus="+siteInfo.getSiteStatus());
				// gateway first registration ,no need set. for gatewayService no ready. 
				if(siteInfo.getSiteStatus()!=SiteStatus.UN_KOWN 
						&& siteInfo.getSiteStatus()!=SiteStatus.NERVER_CONNECT){							
					if(siteInfo.getSiteStatus()!=SiteStatus.OFF_LINE){
						this.writeActivityLog( Module.AuditLogger, Severity.Warning, EdgeCMWebServiceMessages.getMessage(
								"gateway_GatewaySetHeartbeatIntervalInvalid", oldSiteInfo.getName()) );
					} else {
						logger.debug("begin to set gateway HeartBeat_Interval gatewayId= "+siteInfo.getGatewayId() +" interval="+siteInfo.getHeartbeatInterval());					
						this.writeActivityLog( Module.Common, Severity.Information, EdgeCMWebServiceMessages.getMessage(
								"gateway_GatewaySetHeartbeatInterval", siteInfo.getName(),siteInfo.getHeartbeatInterval()));
						this.setGatewayHeartbeatInterval(siteInfo.getGatewayId(), siteInfo.getHeartbeatInterval());
						logger.debug("End to set gateway HeartBeat_Interval");
					}
				}
			}
						
			// update database			
			this.gatewayDao.updateSite(
				siteId.getRecordId(), siteInfo.getName(), siteInfo.getDescription()==null?"":siteInfo.getDescription(),
				siteInfo.getAddress()==null?"":siteInfo.getAddress(), siteInfo.getEmail()==null?"":siteInfo.getEmail(),
				siteInfo.getConsoleHostName()==null?"":siteInfo.getConsoleHostName(),siteInfo.getConsoleProtocol(), 
				siteInfo.getConsoleProt(), siteInfo.getGatewayProtocol(), siteInfo.getGatewayPort(),
				siteInfo.getGatewayUsername()==null?"":siteInfo.getGatewayUsername(),
				siteInfo.getGatewayPassword()==null?"":siteInfo.getGatewayPassword(),
				siteInfo.getRegistrationText()==null?"":siteInfo.getRegistrationText(), 
				siteInfo.getHeartbeatInterval());
			
			// write audit log
			if(oldSiteInfo.getUpdateTime()!=null){	
				writeAuditLog( EdgeCMWebServiceMessages.getMessage(
						"gateway_SiteUpdated", oldSiteInfo.getName(), siteInfo.getName()) );
			}
			else{		
				writeAuditLog( EdgeCMWebServiceMessages.getMessage(
						"gateway_NewSiteAdded", siteInfo.getName()) );
			}
			logger.debug("updateSite: siteName="+siteInfo.getName());
		}
	}

	@Override
	public void deleteSite( SiteId siteId ) throws EdgeServiceFault
	{
		validateSiteId( siteId );
		
		synchronized (updateSiteLockObj)
		{
			// check if the site is in use
			
			List<EdgeSiteInfo> siteInfoList = new ArrayList<>();
			this.gatewayDao.getSiteInfo( siteId.getRecordId(), siteInfoList );
			if (siteInfoList.size() == 0)
				return;
			
			EdgeSiteInfo daoSiteInfo = siteInfoList.get( 0 );
			
			if (isGatewayInUse( daoSiteInfo.getGatewayId() ))
			{
				String message = EdgeCMWebServiceMessages.getMessage(
					"gateway_SiteCannotBeDeleted", daoSiteInfo.getName() );
				this.writeAuditLog( message );
				
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_SiteIsInUse,
					new Object[] { daoSiteInfo.getName() }, "GATEWAY_SiteIsInUse" );
			}
			
			// delete the site
			
			this.gatewayDao.deleteSite( siteId.getRecordId() );
			
			// write audit log
			if(daoSiteInfo.getUpdateTime()!=null){
				String message = EdgeCMWebServiceMessages.getMessage(
					"gateway_SiteDeleted", daoSiteInfo.getName() );
				this.writeAuditLog( message );
			}
			logger.debug("deleteSite: siteName="+daoSiteInfo.getName());
		}
	}
	
	private void validateSiteId( SiteId siteId ) throws EdgeServiceFault
	{
		if ((siteId == null) || !siteId.isValid())
		{
			logger.error( "Invalid site ID. siteId: " + siteId );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_InvalidSiteId,
				"GATEWAY_InvalidSiteId" );
		}
	}
	
	private void validateSiteInfo( SiteInfo siteInfo ) throws EdgeServiceFault
	{
		if ((siteInfo == null) || (siteInfo.getName() == null) || siteInfo.getName().trim().isEmpty())
		{
			logger.error( "Invalid site info. siteInfo: " + siteInfo );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_InvalidSiteInfo,
				"GATEWAY_InvalidSiteInfo" );
		}
	}
	
	private boolean checkEsxNodeBinding(int gatewayRecId){
		List<EsxVSphere> vmESXInfoList = new LinkedList<EsxVSphere>();
		String vsphereTypes = "("+VsphereEntityType.esxServer.getValue()+","+VsphereEntityType.vCenter.getValue()+")";
		vSphereDao.as_edge_vsphere_vmESXInfolist(gatewayRecId, vsphereTypes, vmESXInfoList);
		for (EsxVSphere vSphereServer:vmESXInfoList) {
			if(vSphereServer.getIsAutoDiscovery()==1){
				return true;
			} else {
				List<EdgeEsxVmInfo> vmListInDB = new LinkedList<EdgeEsxVmInfo>();
				esxDao.as_edge_vsphere_vm_detail_getVMByEsxServerId(vSphereServer.getId(), vmListInDB);
				for (EdgeEsxVmInfo esxVm:vmListInDB) {
					if(esxVm.getIsVisible()==1)
						return true;
				}
			}
		}
		return false;
	}
	
	private void removeEsxInVisibleNode(int gatewayRecId) throws EdgeServiceFault{
		List<EsxVSphere> vmESXInfoList = new LinkedList<EsxVSphere>();
		String vsphereTypes = "("+VsphereEntityType.esxServer.getValue()+","+VsphereEntityType.vCenter.getValue()+")";
		vSphereDao.as_edge_vsphere_vmESXInfolist(gatewayRecId, vsphereTypes, vmESXInfoList);
		for (EsxVSphere vSphereServer:vmESXInfoList) {
			
			List<EdgeEsxVmInfo> vmListInDB = new LinkedList<EdgeEsxVmInfo>();
			esxDao.as_edge_vsphere_vm_detail_getVMByEsxServerId(vSphereServer.getId(), vmListInDB);
			for (EdgeEsxVmInfo esx:vmListInDB) {
				this.unbindEntity(esx.getHostId(), EntityType.Node);
				hostMgrDao.as_edge_host_remove(esx.getHostId());
			}				
			// esx
			esxDao.as_edge_esx_delete(vSphereServer.getId());
			unbindEntity(vSphereServer.getId(), EntityType.VSphereEntity);				
			
		}
	}
	
	private boolean checkHypervNodeBinding(int gatewayRecId){
		List<EsxVSphere> lstResult = new ArrayList<EsxVSphere>();
		vSphereDao.as_edge_vsphere_vmHyperVInfolist(gatewayRecId,lstResult);
		for (EsxVSphere vSphereServer:lstResult) {
			if(vSphereServer.getIsAutoDiscovery()==1){
				return true;
			} else {
				List<EdgeHyperVHostMapInfo> hostMaps = new ArrayList<EdgeHyperVHostMapInfo>();
				hyperVDao.as_edge_hyperv_host_map_list_by_hypervid(vSphereServer.getId(), hostMaps);
				for (EdgeHyperVHostMapInfo host:hostMaps) {
					if(host.getIsVisible()==1)
						return true;
				}
			}
		}
		return false;
	}
	
	private void removeHypervInVisibleNode(int gatewayRecId) throws EdgeServiceFault{
		List<EsxVSphere> lstResult = new ArrayList<EsxVSphere>();
		vSphereDao.as_edge_vsphere_vmHyperVInfolist(gatewayRecId,lstResult);
		for (EsxVSphere vSphereServer:lstResult) {			
			List<EdgeHyperVHostMapInfo> hostMaps = new ArrayList<EdgeHyperVHostMapInfo>();
			hyperVDao.as_edge_hyperv_host_map_list_by_hypervid(vSphereServer.getId(), hostMaps);
			for (EdgeHyperVHostMapInfo host:hostMaps) {				
				this.unbindEntity(host.getHostId(), EntityType.Node);
				hostMgrDao.as_edge_host_remove(host.getHostId());
			}
			unbindEntity( vSphereServer.getId(), EntityType.HyperVServer );
			hostMgrDao.as_edge_group_hyperv_remove(vSphereServer.getId(),NodeGroup.HYPERV);			
		}
	}
	
	private boolean checkAdNodeBinding(int gatewayRecId){
		List<EdgeAD> adList = new LinkedList<EdgeAD>();
		adDao.as_edge_ad_getByGatewayId(gatewayRecId, adList);
		for (EdgeAD ad:adList) {
			if(ad.getIsAutoDiscovery()==1){
				return true;
			} else {
				List<IntegerId> nodeIds = new ArrayList<IntegerId>();
				adDao.as_edge_ad_host_map_getNodeIdsbyadId(ad.getId(), nodeIds);				
				StringBuilder sb = new StringBuilder();				
				for (IntegerId id:nodeIds) {
					sb.append(id.getId()).append(" ");
				}
				if(!sb.toString().trim().equals("")){
					List<EdgeHost> hosts = new LinkedList<EdgeHost>();
					hostMgrDao.as_edge_host_list_by_ids(sb.toString(), hosts);
					for (EdgeHost host:hosts) {
						if(host.getIsVisible()==1)
							return true;
					}
				}
			}
		}
		return false;
	}
	
	private void removeAdInVisibleNode(int gatewayRecId) throws EdgeServiceFault{
		List<EdgeAD> adList = new LinkedList<EdgeAD>();
		adDao.as_edge_ad_getByGatewayId(gatewayRecId, adList);
		for (EdgeAD ad:adList) {			
			List<IntegerId> nodeIds = new ArrayList<IntegerId>();
			adDao.as_edge_ad_host_map_getNodeIdsbyadId(ad.getId(), nodeIds);				
			for (IntegerId id:nodeIds) {
				this.unbindEntity(id.getId(), EntityType.Node);
				hostMgrDao.as_edge_host_remove(id.getId());
			}
			unbindEntity(ad.getId(), EntityType.AD);
			adDao.as_edge_ad_delete(ad.getId());
		}
	}
	
	private boolean isGatewayInUse( int gatewayRecId )throws EdgeServiceFault
	{
		int[] refCount = new int[1];
		this.gatewayDao.getGatewayReferenceCount( gatewayRecId, refCount );
		//return (refCount[0] > 0);
		if(refCount[0]<=0)
			return false;	
		
		// Find Esx		
		if(checkEsxNodeBinding(gatewayRecId))
			return true;		
		// Find Hyperv
		if(checkHypervNodeBinding(gatewayRecId))
			return true;		
		// Find AD
		if(checkAdNodeBinding(gatewayRecId))
			return true;		
		// check visible node bing to this gateway
		List<IntegerId> ids = new ArrayList<>();
		this.gatewayDao.getVisibleEntities( EntityType.Node.getValue(),gatewayRecId, ids );
		if(ids!=null && ids.size()>0){
			return true;
		}
		
		for (EntityType entityType : EntityType.values())
		{
			//no need to check AD/HyperVServer/ VSphereEntity/Node
			if(entityType == EntityType.AD
					||entityType == EntityType.HyperVServer
					||entityType == EntityType.VSphereEntity
					||entityType == EntityType.Node)
				continue;
			this.gatewayDao.getGatewayEntityTypeReferenceCount( gatewayRecId,entityType.getValue(), refCount );
			if(refCount[0]>0)
				return true;
		}
		
		// delete all IsVisible Node and DiscoverySetting
		removeEsxInVisibleNode(gatewayRecId);
		removeAdInVisibleNode(gatewayRecId);
		removeHypervInVisibleNode(gatewayRecId);		
		
		// check whether entity Existing
		logger.info( "Begin to Find unExisting entities gateway.id="+ gatewayRecId);	
		List<IntegerId> idList = new ArrayList<>();
		for (EntityType entityType : EntityType.values())
		{
			logger.info( "unBind entities of type " + entityType  );			
			idList.clear();
			this.gatewayDao.getEntitiesDidntExisting( entityType.getValue(),gatewayRecId, idList );
			for (IntegerId id : idList)
			{
				this.unbindEntity( id.getId(), entityType );
			}
		}
		this.gatewayDao.getGatewayReferenceCount( gatewayRecId, refCount );
		return (refCount[0] > 0);
	}

	@Override
	public SiteInfo getSite( SiteId siteId ) throws EdgeServiceFault
	{
		validateSiteId( siteId );
		
		List<EdgeSiteInfo> daoSiteInfoList = new ArrayList<>();
		this.gatewayDao.getSiteInfo( siteId.getRecordId(), daoSiteInfoList );
		if (daoSiteInfoList.size() == 0)
			return null;
		
		return convertDaoSiteInfo( daoSiteInfoList.get( 0 ) );
	}

	@Override
	public List<SiteInfo> querySites( SiteFilter filter )
		throws EdgeServiceFault
	{
		if (filter == null)
			filter = new SiteFilter();
		
		if (filter.getNamePattern() == null)
			filter.setNamePattern( "*" );
		
		filter.setNamePattern( filter.getNamePattern().replace( "*", "%" ) );
		
		List<EdgeSiteInfo> daoSiteInfoList = new ArrayList<>();
		this.gatewayDao.querySites( filter.getNamePattern(), daoSiteInfoList );
		
		List<SiteInfo> siteInfoList = new ArrayList<>();
		for (EdgeSiteInfo daoSiteInfo : daoSiteInfoList)
			siteInfoList.add( convertDaoSiteInfo( daoSiteInfo ) );
		
		return siteInfoList;
	}
	
	private Version parseDbVersionString( String verString )
	{
		Version version = new Version();
		try
		{
			if(StringUtil.isEmptyOrNull(verString))
				return version;
			EdgeSimpleVersion simpleVersion = EdgeSimpleVersion.parseVersionString( verString );
			version.setMajorVersion( simpleVersion.getMajorVersion() );
			version.setMinorVersion( simpleVersion.getMinorVersion() );
			version.setBuildNumber( Integer.toString( simpleVersion.getBuildNumber() ) );
			version.setUpdateInfo( simpleVersion.getUpdateNumber(), simpleVersion.getUpdateBuildNumber() );
		}
		catch (Exception e)
		{
			logger.error( "Error parsing version string '" + verString + "'", e );
		}
		return version;
	}
	
	private SiteInfo convertDaoSiteInfo( EdgeSiteInfo daoSiteInfo )
	{
		SiteInfo info = new SiteInfo();
		
		info.setId( new SiteId( daoSiteInfo.getId() ) );
		info.setName( daoSiteInfo.getName() );
		info.setDescription( daoSiteInfo.getDescription() );
		info.setGatewayId( new GatewayId( daoSiteInfo.getGatewayId() ) );
		info.setLocal( daoSiteInfo.getIsLocal() != 0 );
		info.setCreateTime( daoSiteInfo.getCreateTime() );
		info.setUpdateTime( daoSiteInfo.getUpdateTime() );
		info.setAddress(daoSiteInfo.getAddress());
		info.setEmail(daoSiteInfo.getEmail());
		info.setConsoleHostName(daoSiteInfo.getConsoleHostname());
		info.setConsoleProtocol(daoSiteInfo.getConsoleProtocol());
		info.setConsoleProt(daoSiteInfo.getConsolePort());
		info.setGatewayProtocol(daoSiteInfo.getGatewayProtocol());
		info.setGatewayPort(daoSiteInfo.getGatewayPort());
		info.setGatewayUsername(daoSiteInfo.getGatewayUsername());
		info.setGatewayPassword(daoSiteInfo.getGatewayPassword());
		info.setRegistrationText(daoSiteInfo.getRegistrationText());
		info.setLastContactTime(daoSiteInfo.getLastContactTime());
		info.setGatewayHostName(daoSiteInfo.getGatewayHostName());
		info.setHeartbeatInterval(daoSiteInfo.getHeartbeatInterval());
		info.setUpgradeStatus(daoSiteInfo.getUpgradeStatus());
		info.setUpgradeDetailMessage(daoSiteInfo.getUpgradeDetailMessage());
		info.setUpgradeStartTime(daoSiteInfo.getUpgradeStartTime());
		info.setLastReportStatusTime( daoSiteInfo.getLastReportStatusTime() );
		info.setUpgradeTime(daoSiteInfo.getUpgradeTime());
		Version version = parseDbVersionString( daoSiteInfo.getGatewayVersion() );
		info.setGatewayVersion(version);
		info.setVersionSame(populateSiteVersion(version));
		info.setUpgradingTimeout( daoSiteInfo.isUpgradingTimeout() );
		
		SiteInfo.SiteStatus status = populateSiteStatus(daoSiteInfo);
		info.setSiteStatus(status);
		return info;
	}
		
	private boolean populateSiteVersion(Version version){
		EdgeSimpleVersion consoVersion = getConsoleVersion();		
		if(consoVersion==null)
			return true;
		if(consoVersion.getMajorVersion()==version.getMajorVersion()
				&&consoVersion.getMinorVersion()==version.getMinorVersion()
				&&consoVersion.getBuildNumber()==Version.parseBuildNumber(version.getBuildNumber())
				&&consoVersion.getUpdateNumber()==version.getUpdateNumber()
				&&consoVersion.getUpdateBuildNumber()==version.getUpdateBuildNumber()
				){
			return true;
		}
		return false;
	}
	private SiteInfo.SiteStatus populateSiteStatus(EdgeSiteInfo site){
		
		if (site==null) 
			return SiteInfo.SiteStatus.UN_KOWN;		
		if(StringUtil.isEmptyOrNull(site.getHostUuid()))
			return SiteInfo.SiteStatus.NERVER_CONNECT;
		if(site.getLastContactTime()==null)
			return SiteInfo.SiteStatus.NERVER_CONNECT;
		
		Calendar calLast = Calendar.getInstance();
		Calendar calCur = Calendar.getInstance();
		calLast.setTime(site.getLastContactTime());
		calCur.setTime(site.getCurrentTime()); 
		Long lLast = calLast.getTimeInMillis();
		Long lCur = calCur.getTimeInMillis();
		Long sec = Math.abs(lCur - lLast)/1000;
		Double interval = 0.0;
		if(site.getHeartbeatInterval()==0) // no set heartbeatinterval 
			interval = DEFAULT_HEARTBEAT_INTERVAL*INTERVAL_WEIGHT;
		else	
			interval = site.getHeartbeatInterval()*INTERVAL_WEIGHT;		
		//if( sec > (DEFAULT_HEARTBEAT_CHECKWAIT + DEFAULT_HEARTBEAT_INTERVAL) )
		if( sec > interval){
			return SiteInfo.SiteStatus.OFF_LINE;
		} else {
			return SiteInfo.SiteStatus.ON_LINE;
			/*
			if(site.getUpgradeStatus()!=0 
					&& site.getUpgradeStatus() != GatewayUpdateStatusCode.NoNeedToUpdate.getValue()){
				if(site.getUpgradeStatus()==GatewayUpdateStatusCode.NotifyingGatewayToUpgrade.getValue()){
					return SiteInfo.SiteStatus.NotifyingGatewayToUpdate;
				} else if(site.getUpgradeStatus()==GatewayUpdateStatusCode.FailedToNotifyGatewayToUpgrade.getValue()){
					return SiteInfo.SiteStatus.FailedToNotifyUpgradation;
				} else if(site.getUpgradeStatus()==GatewayUpdateStatusCode.GettingUpdateInfo.getValue()){
					return SiteInfo.SiteStatus.GettingUpdateInfo;
				} else if( site.getUpgradeStatus()==GatewayUpdateStatusCode.FailedToGetUpdateInfo.getValue()){
					return SiteInfo.SiteStatus.FailedToGetUpdateInfo;
				} else if( site.getUpgradeStatus()==GatewayUpdateStatusCode.DownloadingUpdates.getValue()){
					return SiteInfo.SiteStatus.DownloadingUpdates;
				} else if( site.getUpgradeStatus()==GatewayUpdateStatusCode.FailedToDownloadUpdates.getValue()){
					return SiteInfo.SiteStatus.FailedToDownloadUpdates;
				} else if( site.getUpgradeStatus()==GatewayUpdateStatusCode.InstallingUpdates.getValue()){
					return SiteInfo.SiteStatus.InstallingUpdates;
				} else if( site.getUpgradeStatus()==GatewayUpdateStatusCode.FailedToInstallUpdates.getValue()){
					return SiteInfo.SiteStatus.FailedToInstallUpdates;
				} else if( site.getUpgradeStatus()==GatewayUpdateStatusCode.UpdatedSuccessfully.getValue()){
					//return SiteInfo.SiteStatus.UpdatedSuccessfully;
					return SiteInfo.SiteStatus.ON_LINE;
				} else if( site.getUpgradeStatus()==GatewayUpdateStatusCode.UpdatedSuccessfullyNeedReboot.getValue()){
					return SiteInfo.SiteStatus.UpdatedSuccessfullyNeedReboot;
				}
			}			
			return SiteInfo.SiteStatus.ON_LINE;	
			*/		
		}
	}
	
	@Override
	public String generateGatewayRegistrationString(
		GenerateGatewayRegStrParam param ) throws EdgeServiceFault
	{
		String consoleUuid = CommonUtil.retrieveCurrentAppUUIDWithDecrypt( true );
		GatewayEntity gateway = this.getGatewayById( param.getGatewayId() );
		
		GatewayRegistrationString regStr = new GatewayRegistrationString();
		
		regStr.setRegSvrHostName( param.getRegSvrHostName() );
		regStr.setRegSvrPort( param.getRegSvrPort() );
		regStr.setRegSvrProtocol( param.getRegSvrProtocol() );
		regStr.setConsoleUuid( consoleUuid );
		regStr.setGatewayUuid( gateway.getUuid() );
		regStr.setGatewayHostProtocol( param.getGatewayProtocol() );
		regStr.setGatewayHostPort( param.getGatewayPort() );
		regStr.setGatewayHostUsername( param.getGatewayUsername() );
		regStr.setGatewayHostPassword( param.getGatewayPassword() );
		
		return regStr.toEncodedString();
	}
	
	private int getSiteRecIdByGatewayId( int gatewayRecId )
	{
		List<IntegerId> idList = new ArrayList<>();
		this.gatewayDao.getSiteByGatewayId( gatewayRecId, idList );
		return idList.get( 0 ).getId();
	}
	
	@Override
	public SiteId getSiteIdByGatewayId( GatewayId gatewayId )
	{
		if (gatewayId == null)
			throw new IllegalArgumentException( "gatewayId is null" );
		
		int siteRecId = getSiteRecIdByGatewayId( gatewayId.getRecordId() );
		return new SiteId( siteRecId );
	}
	
	private static Object gatewayRegistrationLockObj = new Object();

	/**
	 * Possible error code:
	 * - EdgeServiceErrorCode.GATEWAY_InvalidRegInfo
	 * - EdgeServiceErrorCode.GATEWAY_GatewayHostLoginFailed
	 * - EdgeServiceErrorCode.GATEWAY_GatewayNotFound
	 * - EdgeServiceErrorCode.GATEWAY_CannotRegisterToLocalGateway
	 * - EdgeServiceErrorCode.GATEWAY_GatewayRegisteredToAnotherHost
	 * - EdgeServiceErrorCode.Common_Service_Dao_Execption
	 */
	@Override
	public GatewayRegistrationResult registerGatewayHost(
		GatewayRegistrationInfo regInfo ) throws EdgeServiceFault
	{
		logger.info( "EdgeGatewayBean.registerGatewayHost() enter." );
		
		validateRegInfo( regInfo );
		
		synchronized (gatewayRegistrationLockObj)
		{
			EdgeGatewayEntity gatewayEntity = validateRegInfo2( regInfo );
			
			if (gatewayEntity.getIsLocal() != 0)
			{
				logger.error(
					"The gateway is a local gateway which can only be registered internally. " +
					"Gateway UUID: " + regInfo.getGatewayUuid() );
				throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.GATEWAY_CannotRegisterToLocalGateway,
					"GATEWAY_CannotRegisterToLocalGateway" );
			}
			
			GatewayUpdateStatusCode upgradeStatus = null;
			boolean isUpgradingTimeout = false;
			if (this.isGatewayRegistered( gatewayEntity ))
			{
				List<EdgeGatewayUpdateStatus> statusList = new ArrayList<>();
				this.gatewayDao.getGatewayUpdateStatus( gatewayEntity.getId(), statusList );
				if (statusList.size() > 0)
				{
					EdgeGatewayUpdateStatus status = statusList.get( 0 );
					upgradeStatus = GatewayUpdateStatusCode.fromValue( status.getUpdateStatus() );
					isUpgradingTimeout = status.isTimeout();
				}
			}
			
			if (!this.isGatewayRegistered( gatewayEntity ))
			{
				try
				{
					this.gatewayDao.registerGatewayHost(
						gatewayEntity.getId(), regInfo.getHostName(), regInfo.getHostUuid() );
				}
				catch (Exception e)
				{
					logger.error( "Gateway host was not registered due to DAO error. Host UUID: " + regInfo.getHostUuid() );
					throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_Dao_Execption,
						"Common_Service_Dao_Execption" );
				}
				
				// write audit log

				String message = EdgeCMWebServiceMessages.getMessage(
					"gateway_GatewayHostRegistered", regInfo.getHostName(), gatewayEntity.getName() );
				this.writeAuditLog( message );
				
				publishGatewayRegisteredEvent( new GatewayId( gatewayEntity.getId() ) );
				
				// delete action items
				
				int siteRecId = getSiteRecIdByGatewayId( gatewayEntity.getId() );
				SiteId siteId = new SiteId( siteRecId );
				deleteSiteActionItem( siteId, SiteAction.RegisterGatewayHost );
			}
			else if ((upgradeStatus != null) && upgradeStatus.isInProgressStatus() && !isUpgradingTimeout)
			{
				int siteRecId = getSiteRecIdByGatewayId( gatewayEntity.getId() );
				SiteId siteId = new SiteId( siteRecId );
				SiteInfo siteInfo = getSite( siteId );
				
				logger.error( "Cannot register to the gateway when it's upgrading. Gateway UUID: " +
					regInfo.getGatewayUuid() + ", Registered host: " + gatewayEntity.getHostName() );
				throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.GATEWAY_CannotRegisterWhenUpgrading,
					new Object[] { siteInfo.getName(), gatewayEntity.getHostName() },
					"GATEWAY_CannotRegisterWhenUpgrading" );
			}
			else if (gatewayEntity.getHostUuid().equalsIgnoreCase( regInfo.getHostUuid() ))
			{
				if (upgradeStatus != null)
				{
					logger.info( "The gateway has old update status info, will clear it." );
					
					try
					{
						this.gatewayDao.deleteGatewayUpdateStatus( gatewayEntity.getId() );
						logger.info( "Gateway update info was cleared." );
						
						// write audit log

						String message = EdgeCMWebServiceMessages.getMessage(
							"gateway_GatewayHostRegistered", regInfo.getHostName(), gatewayEntity.getName() );
						this.writeAuditLog( message );
					}
					catch (Exception e)
					{
						logger.error( "Gateway update info was not cleared due to DAO error. Host UUID: " +
							regInfo.getHostUuid() );
						
						// write audit log

						String message = EdgeCMWebServiceMessages.getMessage(
							"gateway_GatewayHostRegisteredWithClearingOldInfoError", regInfo.getHostName(), gatewayEntity.getName() );
						this.writeAuditLog( Severity.Warning, message );
					}
				}
				else // no update status info
				{
					// write audit log

					String message = EdgeCMWebServiceMessages.getMessage(
						"gateway_GatewayHostRegistered", regInfo.getHostName(), gatewayEntity.getName() );
					this.writeAuditLog( message );
				}
			}
			else if (regInfo.isOverwriteOld()) // overwrite the old host
			{
				try
				{
					this.gatewayDao.registerGatewayHost(
						gatewayEntity.getId(), regInfo.getHostName(), regInfo.getHostUuid() );
				}
				catch (Exception e)
				{
					logger.error( "Gateway host was not registered due to DAO error. Host UUID: " + regInfo.getHostUuid() );
					throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_Dao_Execption,
						"Common_Service_Dao_Execption" );
				}
				
				boolean clearOldDataSucceed = true;
				
				if (upgradeStatus != null)
				{
					try
					{
						this.gatewayDao.deleteGatewayUpdateStatus( gatewayEntity.getId() );
						logger.info( "Gateway update info was cleared." );
					}
					catch (Exception e)
					{
						clearOldDataSucceed = false;
						
						logger.error( "Gateway update info was not cleared due to DAO error. Host UUID: " +
							regInfo.getHostUuid() );
					}
				}
				
				// write audit log

				String message = EdgeCMWebServiceMessages.getMessage(
					clearOldDataSucceed ? "gateway_GatewayHostOverwritten" : "gateway_GatewayHostOverwrittenWithClearingOldInfoError",
					regInfo.getHostName(), gatewayEntity.getName(), gatewayEntity.getHostName() );
				this.writeAuditLog( message );
				
				publishGatewayRegisteredEvent( new GatewayId( gatewayEntity.getId() ) );

				// delete action items
				
				int siteRecId = getSiteRecIdByGatewayId( gatewayEntity.getId() );
				SiteId siteId = new SiteId( siteRecId );
				deleteSiteActionItem( siteId, SiteAction.RegisterGatewayHost );
				
				sendGatewayReRegisteredWarning( gatewayEntity );
			}
			else // gateway was registered to another host and DON'T overwrite
			{
				int siteRecId = getSiteRecIdByGatewayId( gatewayEntity.getId() );
				SiteId siteId = new SiteId( siteRecId );
				SiteInfo siteInfo = getSite( siteId );
				
				logger.error( "The specified gateway is registered to another host. Gateway UUID: " +
					regInfo.getGatewayUuid() + ", Old host: " + gatewayEntity.getHostName() +
					", New host: " + regInfo.getHostName() );
				throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.GATEWAY_GatewayRegisteredToAnotherHost,
					new Object[] { gatewayEntity.getHostName(), siteInfo.getName() },
					"GATEWAY_GatewayRegisteredToAnotherHost" );
			}
		}
		
		// The console UUID and gateway UUID are already in the registration
		// information, but I don't want the client code depending on this, so
		// here I will still return these UUIDs, and the client code should
		// get this information from the returned value.
		
		GatewayRegistrationResult result = new GatewayRegistrationResult();
		result.setConsoleUuid( regInfo.getConsoleUuid() );
		result.setGatewayUuid( regInfo.getGatewayUuid() );
		// remote gateway can connect console without certificate, so the code is commented
		/*EdgeBrokerKeyStoreUtils brokerKSService = new EdgeBrokerKeyStoreUtils(WindowsRegistryUtils.getUDPInstallationPath());
		File inFile = new File(brokerKSService.getBrokerKSPath()); 
		if(inFile.exists()){
			FileInputStream fileInputStream;
			byte[] filea = null;
			try {
				fileInputStream = new FileInputStream(inFile);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
				int i;  
				while ((i = fileInputStream.read()) != -1) {  
				   byteArrayOutputStream.write(i);  
				}  
				fileInputStream.close();  
				filea = byteArrayOutputStream.toByteArray();  
				
				byteArrayOutputStream.close();  
			} catch (Exception e) {
				logger.error("get server broker keystore failed", e);
			}  
			result.setBrokerCert(filea);
		}*/
		logger.info( "EdgeGatewayBean.registerGatewayHost() exit." );
		
		return result;
	}
	
	private void publishGatewayRegisteredEvent( GatewayId gatewayId )
	{
		try
		{
			GatewayEntity gateway = this.getGatewayById( gatewayId );
			EdgeEventCenter.getInstance().publishEvent(
				new GatewayHostRegisteredEventForConsole( gateway ), this );
		}
		catch (Exception e)
		{
			logger.error( this.getClass().getSimpleName() +
				".publishGatewayRegisteredEvent(): Error publishing gateway registered event. Gateway ID: " + gatewayId,
				e );
		}
	}
	
	private void validateRegInfo( GatewayRegistrationInfo regInfo ) throws EdgeServiceFault
	{
		if ((regInfo == null) || (regInfo.getConsoleUuid() == null) || (regInfo.getGatewayUuid() == null) ||
			(regInfo.getHostUuid() == null) || (regInfo.getHostUuid().isEmpty()))
		{
			logger.error( "Invalid gateway reg info. regInfo: " + regInfo );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_InvalidRegInfo,
				"GATEWAY_InvalidRegInfo" );
		}
	}
	
	private EdgeGatewayEntity validateRegInfo2( GatewayRegistrationInfo regInfo ) throws EdgeServiceFault
	{
		return validateRegInfo2( regInfo.getConsoleUuid(), regInfo.getGatewayUuid() );
	}
	
	private EdgeGatewayEntity validateRegInfo2( String inputConsoleUuid, String inputGatewayUuid ) throws EdgeServiceFault
	{
		String consoleUuid = CommonUtil.retrieveCurrentAppUUIDWithDecrypt( true );
		if (!consoleUuid.equalsIgnoreCase( inputConsoleUuid ))
		{
			logger.error( "Failed to login gateway host." );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayHostLoginFailed,
				"GATEWAY_GatewayHostLoginFailed" );
		}
		
		List<EdgeGatewayEntity> gatewayList = new ArrayList<>();
		this.gatewayDao.getGatewayByUuid( inputGatewayUuid, gatewayList );
		if (gatewayList.size() == 0)
		{
			logger.error( "The specified gateway cannot be found. Gateway UUID: " + inputGatewayUuid );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayNotFound,
				"GATEWAY_GatewayNotFound" );
		}
		
		return gatewayList.get( 0 );
	}

	private void validateUnregInfo( GatewayUnregistrationInfo unregInfo ) throws EdgeServiceFault
	{
		if ((unregInfo == null) || (unregInfo.getConsoleUuid() == null) || (unregInfo.getGatewayUuid() == null) ||
			(unregInfo.getHostUuid() == null))
		{
			logger.error( "Invalid gateway unreg info. unregInfo: " + unregInfo );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_InvalidUnregInfo,
				"GATEWAY_InvalidUnregInfo" );
		}
	}
	
	private EdgeGatewayEntity validateUnregInfo2( GatewayUnregistrationInfo unregInfo ) throws EdgeServiceFault
	{
		return validateRegInfo2( unregInfo.getConsoleUuid(), unregInfo.getGatewayUuid() );
	}
	
	private void sendGatewayReRegisteredWarning( EdgeGatewayEntity gatewayEntity )
	{
		List<IntegerId> siteIdList = new ArrayList<>();
		this.gatewayDao.getSiteByGatewayId( gatewayEntity.getId(), siteIdList );
		if (siteIdList.size() == 0)
		{
			logger.error( "Cannot find a site who is using gateway { id = " + gatewayEntity.getId() + " }." );
			return;
		}
		
		List<EdgeSiteInfo> siteInfoList = new ArrayList<>();
		this.gatewayDao.getSiteInfo( siteIdList.get( 0 ).getId(), siteInfoList );
		if (siteIdList.size() == 0)
		{
			logger.error( "Error getting information of site { id = " + siteIdList.get( 0 ).getId() + " }." );
			return;
		}
		
		EdgeSiteInfo siteInfo = siteInfoList.get( 0 );
		
		// write activity log

		String message = EdgeCMWebServiceMessages.getMessage(
			"gateway_GatewayHostOverwritten_ResourceUpdateWarning", siteInfo.getName() );
		this.writeActivityLog( Module.GatewayManagement, Severity.Warning, message );
		
		// send alert email and UDP alert
		
		GatewayOverwrittenAlertContentProvider contentProvider =
			new GatewayOverwrittenAlertContentProvider( siteInfo, gatewayEntity );
		sendGatewayOverwrittenAlertEmail( contentProvider );
		sendUdpAlert( contentProvider );
		
		//sendGatewayOverwrittenAlertEmail( siteInfo, gatewayEntity );
		//sendUdpAlert( siteInfo, gatewayEntity );
	}
	
	/**
	 * Possible error codes:
	 * - EdgeServiceErrorCode.GATEWAY_InvalidUnregInfo
	 * - EdgeServiceErrorCode.GATEWAY_GatewayHostLoginFailed
	 * - EdgeServiceErrorCode.GATEWAY_GatewayNotFound
	 * - EdgeServiceErrorCode.GATEWAY_GatewayHostNotRegistered
	 * - EdgeServiceErrorCode.Common_Service_Dao_Execption
	 */
	@Override
	public void unregisterGatewayHost( GatewayUnregistrationInfo unregInfo )
		throws EdgeServiceFault
	{
		logger.info( "EdgeGatewayBean.unregisterGatewayHost() enter." );
		
		validateUnregInfo( unregInfo );
		
		synchronized (gatewayRegistrationLockObj)
		{
			EdgeGatewayEntity gatewayEntity = validateUnregInfo2( unregInfo );
			
			if (!gatewayEntity.getHostUuid().equals( unregInfo.getHostUuid() ))
			{
				logger.error( "Gateway host was not registered to the specified gateway. Host UUID: " + unregInfo.getHostUuid() );
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayHostNotRegistered,
					"GATEWAY_GatewayHostNotRegistered" );
			}
			
			try
			{
				this.gatewayDao.registerGatewayHost( gatewayEntity.getId(), "", "" );
			}
			catch (Exception e)
			{
				logger.error( "Gateway host was not unregistered due to DAO error. Host UUID: " + unregInfo.getHostUuid() );
				throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_Dao_Execption,
					"Common_Service_Dao_Execption" );
			}
			
			// write audit log

			String message = EdgeCMWebServiceMessages.getMessage(
				"gateway_GatewayHostUnregistered", gatewayEntity.getName(), gatewayEntity.getHostName() );
			this.writeAuditLog( message );
		}
		
		logger.info( "EdgeGatewayBean.unregisterGatewayHost() exit." );
	}
	
	@Override
	@NonSecured
	public GatewayConnectInfo gatewayLogin( GatewayLoginInfo loginInfo )
		throws EdgeServiceFault
	{
		logger.info( "EdgeGatewayBean.gatewayLogin() enter. loginInfo: " + loginInfo );
		
		ConsoleApplication consoleApp = (ConsoleApplication) UDPApplication.getInstance();
		assert consoleApp != null : "This method is only for console and the console app should not be null.";
		if ((consoleApp == null) || !consoleApp.isReady())
		{
			logger.error( "Console app is not ready yet." );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_Webservice_NotReady,
				"Common_Service_Webservice_NotReady" );
		}
		
		if ((loginInfo == null) || (loginInfo.getConsoleUuid() == null) ||
			(loginInfo.getGatewayUuid() == null) || (loginInfo.getHostUuid() == null))
		{
			logger.error( "Invalid gateway login info. loginInfo: " + loginInfo );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_InvalidLoginInfo,
				"GATEWAY_InvalidLoginInfo" );
		}
		
//		EdgeSimpleVersion consoleVersion = getConsoleVersion();
//		if (loginInfo.getHostVersion().compareTo( consoleVersion ) < 0)
//		{
//			logger.error( "Gateway host's version is lower than console. Console version: " + consoleVersion +
//				", Host version: " + loginInfo.getHostVersion() );
//			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayHostVersionLow,
//				"GATEWAY_GatewayHostVersionLow" );
//		}
		
		String consoleUuid = CommonUtil.retrieveCurrentAppUUIDWithDecrypt( true );
		if (!consoleUuid.equalsIgnoreCase( loginInfo.getConsoleUuid() ))
		{
			logger.error( "Failed to login gateway host. regInfo: " + loginInfo );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayHostLoginFailed,
				"GATEWAY_GatewayHostLoginFailed" );
		}
		
		List<EdgeGatewayEntity> gatewayList = new ArrayList<>();
		this.gatewayDao.getGatewayByUuid( loginInfo.getGatewayUuid(), gatewayList );
		if (gatewayList.size() == 0)
		{
			logger.error( "The specified gateway cannot be found. Gateway UUID: " + loginInfo.getGatewayUuid() );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayNotFound,
				"GATEWAY_GatewayNotFound" );
		}
		
		EdgeGatewayEntity daoGateway = gatewayList.get( 0 );
		
		if ((daoGateway.getHostUuid() == null) || daoGateway.getHostUuid().trim().isEmpty())
		{
			logger.error( "The specified gateway is not registered to a host. Gateway UUID: " +
				loginInfo.getGatewayUuid() );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayHostNotRegistered,
				"GATEWAY_GatewayHostNotRegistered" );
		}
		
		if (!daoGateway.getHostUuid().equalsIgnoreCase( loginInfo.getHostUuid() ))
		{
			logger.error( "The specified gateway is registered to another host. Gateway UUID: " +
				loginInfo.getGatewayUuid() );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayRegisteredToAnotherHost,
				"GATEWAY_GatewayRegisteredToAnotherHost" );
		}
		
		this.gatewayDao.updateGatewayHostVersion(
			daoGateway.getId(), loginInfo.getHostVersion().toVersionString() );
		
		GatewayId gatewayId = new GatewayId( daoGateway.getId() );
		updateLastContactTime( gatewayId );
		
		// check version
		
		EdgeSimpleVersion consoleVersion = getConsoleVersion();
		boolean needToUpdate = doesGatewayNeedToUpdate( consoleVersion, loginInfo.getHostVersion() );
		if (needToUpdate)
		{
			GatewayUpdateStatus statusInfo = new GatewayUpdateStatus();
			statusInfo.setStatusCode( GatewayUpdateStatusCode.NotifyingGatewayToUpgrade );
			reportGatewayUpdateStatusInternal( gatewayId, statusInfo, false );
		}
		else // ! needToUpdate
		{
			setGatewayUpdateStatusWhenGatewayLogin( daoGateway.getId(), loginInfo.isHasRebootFlag() );
		}
		
		GatewayEntity gateway = this.convertDaoGatewayEntity( daoGateway );
		GatewayConnectInfo connInfo = new GatewayConnectInfo();
		String url = consoleApp.getMessageServiceSettings().getClientBrokerURL();
		url = url.replace( ConsoleMessageServiceSettings.PLACEHOLDER_BROKER_PROTOCOL, consoleApp.getMessageServiceSettings().getProtocol() );
		url = url.replace( ConsoleMessageServiceSettings.PLACEHOLDER_BROKER_PORT, String.valueOf(consoleApp.getMessageServiceSettings().getPort()) );//added by li yongfeng to fix the problem about debug
		connInfo.setMqBrokerUrl( url );
		EdgeRegistryInfo edgeInfo = CommonUtil.getApplicationRegistryInfo();
		connInfo.setMqBrokerPort( consoleApp.getMessageServiceSettings().getProtocol().equalsIgnoreCase("tcp") ?  consoleApp.getMessageServiceSettings().getPort() : edgeInfo.getWebServerPort());
		connInfo.setMqBrokerUsername( getMqBrokerUsername() );
		connInfo.setMqBrokerPassword( getMqBrokerPassword() );
		connInfo.setFromConsoleRequestQueue( getQueueName( gateway, QueueType.InvocFromConsoleReq ) );
		connInfo.setFromConsoleResponseQueue( getQueueName( gateway, QueueType.InvocFromConsoleResp ) );
		connInfo.setToConsoleRequestQueue( getQueueName( gateway, QueueType.InvocToConsoleReq ) );
		connInfo.setToConsoleResponseQueue( getQueueName( gateway, QueueType.InvocToConsoleResp ) );
		
		logger.debug("gatewayLogin connInfo.setHeartbeatInterval="+daoGateway.getHeartbeatInterval());
		if(daoGateway.getHeartbeatInterval()==0)
			connInfo.setHeartbeatInterval( DEFAULT_HEARTBEAT_INTERVAL );
		else 
			connInfo.setHeartbeatInterval( daoGateway.getHeartbeatInterval() );
		
		SiteId siteId = this.getSiteIdByGatewayId( gateway.getId() );
		SiteInfo siteInfo = this.getSite( siteId );
		connInfo.setSiteName( siteInfo.getName() );
		
		connInfo.setNeedToUpdate( needToUpdate );
		connInfo.setConsoleVersion( consoleVersion );
		
		return connInfo;
	}
	
	public static String getQueueName( GatewayEntity gateway, QueueType queueType )
	{
		String queueName = "ArcserveUDP." + gateway.getQueueBaseNameForGateway();
		
		if (queueType != null) {
			queueName += "." + queueType.getName();
		}
		
		return queueName;
	}
	
	private void writeAuditLog( String message )
	{
		writeAuditLog( Severity.Information, message );
	}
	
	private void writeAuditLog( Severity serverity, String message )
	{
		writeActivityLog( Module.AuditLogger, serverity, message );
	}
	
	public void writeActivityLog( Module module, Severity severity, String message )
	{
		try
		{
			ActivityLog log = new ActivityLog();
			log.setJobId( 0 );
			log.setModule( Module.PolicyManagement );
			log.setSeverity( severity );
			log.setHostId( 0 );
			log.setNodeName( "" );
			log.setMessage( message );
			log.setTime( new Date() );
			this.activityLogService.addLog( log );
		}
		catch (Exception e)
		{
			logger.error(
				"Error writting activity log. Severity: " + severity + ", Message: " + message, e );
		}
	}
	
	private static Object ensureLocalSiteLockObj = new Object();
	private static final String CONSOLE_HOST_FOR_LOCALGATEWAY = "localhost";

	@Override
	public void ensureLocalSite() throws Exception
	{
		try
		{
			logger.info( "ensureLocalSite(): Begin ensuring local site and gateway." );
			
			synchronized (ensureLocalSiteLockObj)
			{
				String consoleUuid = CommonUtil.retrieveCurrentAppUUIDWithDecrypt( true );
				String hostUuid = consoleUuid;
				String hostName = "localhost";
				boolean noLocalSite = true;
				boolean regenerateSettings = false;
				
				EdgeGatewayEntity gateway = null;
				
				List<EdgeGatewayEntity> gatewayList = new ArrayList<>();
				this.gatewayDao.getLocalGateway( gatewayList );
				if (gatewayList.size() > 0)
				{
					gateway = gatewayList.get( 0 );
					if (gateway.getHostUuid().equals( hostUuid ))
					{
						logger.info( "ensureLocalSite(): Local gateway exists and was registered OK." );
						
						regenerateSettings = ensureLocalGatewaySettingsFile( gateway, consoleUuid, hostUuid );
						if (!regenerateSettings)
							return;
					}
					
					noLocalSite = false;
				}
				
				if (noLocalSite)
				{
					logger.info( "ensureLocalSite(): There is no local gateway, create one." );
					
					// create site and gateway
					
					//String siteName = 'Local Site';
					String siteName = EdgeCMWebServiceMessages.getMessage("localSiteName");
					String description = "";
					String address = "";
					String email = "";
					String gatewayUuid = UUID.randomUUID().toString();
					
					int[] idArray = new int[1];
					int isLocal = 1; // true
					this.gatewayDao.createSite( siteName, description, gatewayUuid, isLocal, address, email,DEFAULT_HEARTBEAT_INTERVAL,idArray );
					int siteRecId = idArray[0];
					
					List<EdgeSiteInfo> daoSiteInfoList = new ArrayList<>();
					this.gatewayDao.getSiteInfo( siteRecId, daoSiteInfoList );
					EdgeSiteInfo siteInfo = daoSiteInfoList.get( 0 );
					
					// register gateway host
					
					this.gatewayDao.registerGatewayHost( siteInfo.getGatewayId(), hostName, hostUuid );
					
					// generate gateway settings file
					
					gatewayList = new ArrayList<>();
					this.gatewayDao.as_edge_gateway_getById( siteInfo.getGatewayId(), gatewayList );
					gateway = gatewayList.get( 0 );
					
					generateLocalGatewaySettingsFile( gateway, consoleUuid, hostUuid );
					
					publishGatewayRegisteredEvent( new GatewayId( gateway.getId() ) );
					
					logger.info( "ensureLocalSite(): Local site and gateway created. Site ID: " + siteRecId );
					
					// cannot handle upgrade to Tungsten computer need reboot, this case may stop the UpdateLagacyNodesRunnable to run
					// so when LocalGateway announceGatewayHostReady call method bindExistingResourcesToLocalGateway.
					//bindExistingResourcesToLocalGateway( gateway );
				}
				else if (regenerateSettings)
				{
					// do nothing, just notify the gateway's web service
				}
				else // there's local site, usually when UDP got re-installed without overwriting the database
				{
					logger.info( "ensureLocalSite(): Local gateway exists but need to be registered with new info." );
					
					assert gateway != null : "The gateway object should not be null.";
					
					// re-register gateway host with new host UUID
					
					this.gatewayDao.registerGatewayHost( gateway.getId(), hostName, hostUuid );
					
					// update gateway settings file
					
					generateLocalGatewaySettingsFile( gateway, consoleUuid, hostUuid );
					
					publishGatewayRegisteredEvent( new GatewayId( gateway.getId() ) );
					
					logger.info( "ensureLocalSite(): Successfully registered local gateway with new info." );
				}
				
				GatewayRegistrationResult localGatewayRegistrationResult = new GatewayRegistrationResult();
				localGatewayRegistrationResult.setConsoleUuid( consoleUuid );
				localGatewayRegistrationResult.setGatewayId( new GatewayId( gateway.getId() ) );
				localGatewayRegistrationResult.setGatewayUuid( gateway.getUuid() );
				
				notifyTheLocalRegistration( localGatewayRegistrationResult );
			}
		}
		catch (Exception e)
		{
			logger.error( "ensureLocalSite(): Error ensuring local site and gateway.", e );
			throw e;
		}
	}
	
	/**
	 * 
	 * @param gateway
	 * @param consoleUuid
	 * @param hostUuid
	 * @return	True if the settings file was regenerated, otherwise returns false.
	 */
	private boolean ensureLocalGatewaySettingsFile(
		EdgeGatewayEntity gateway, String consoleUuid, String hostUuid )
	{
		EdgeRegistryInfo edgeInfo = CommonUtil.getApplicationRegistryInfo();
		
		GatewayMessageServiceSettings gatewaySettings = GatewayMessageServiceSettings.load();
		
		if (!gatewaySettings.getConsoleHost().equals( CONSOLE_HOST_FOR_LOCALGATEWAY ) ||
			gatewaySettings.getConsolePort() != edgeInfo.getWebServerPort() ||
			!gatewaySettings.getConsoleProtocol().equals( edgeInfo.getWebServerProtocol() ) ||
			!gatewaySettings.getConsoleUuid().equals( hostUuid ) ||
			!gatewaySettings.getGatewayUuid().equals( gateway.getUuid() ) ||
			!gatewaySettings.getHostUuid().equals( hostUuid ))
		{
			logger.info( "ensureLocalSite(): " +
				"There are problems in the local gateway settings file. The settings: " + gatewaySettings );
			
			if (gatewaySettings.isDebugMode())
			{
				logger.info( "ensureLocalSite(): " +
					"The local gateway settings file indicates debug mode, so will not re-generate the file." );
				return false;
			}

			generateLocalGatewaySettingsFile( gateway, consoleUuid, hostUuid );
			logger.info( "ensureLocalSite(): Re-generated the local gateway settings file." );
			return true;
		}
		
		logger.info( "ensureLocalSite(): Verified local gateway settings file and it's OK." );
		
		return false;
	}
	
	private void generateLocalGatewaySettingsFile(
		EdgeGatewayEntity gateway, String consoleUuid, String hostUuid )
	{
		EdgeRegistryInfo edgeInfo = CommonUtil.getApplicationRegistryInfo();
		
		GatewayMessageServiceSettings gatewaySettings = new GatewayMessageServiceSettings();
		gatewaySettings.setConsoleHost( CONSOLE_HOST_FOR_LOCALGATEWAY );
		gatewaySettings.setConsolePort( edgeInfo.getWebServerPort() );
		gatewaySettings.setConsoleProtocol( edgeInfo.getWebServerProtocol() );
		gatewaySettings.setConsoleUuid( hostUuid );
		gatewaySettings.setGatewayUuid( gateway.getUuid() );
		gatewaySettings.setHostUuid( hostUuid );
		gatewaySettings.save();
	}
	
	private void bindExistingResourcesToLocalGateway( EdgeGatewayEntity gateway ) throws EdgeServiceFault
	{
		logger.info( "Begin to bind existing entities to local gateway." );
		
		GatewayId gatewayId = new GatewayId( gateway.getId() );
		
		List<IntegerId> idList = new ArrayList<>();
		List<Integer> nodesToUpdateRegFile_local = new ArrayList<>();
		for (EntityType entityType : EntityType.values())
		{
			logger.info( "Bind entities of type " + entityType + " to local gateway..." );
			
			idList.clear();
			this.gatewayDao.getEntitiesDidntBindToGateway( entityType.getValue(), idList );
			for (IntegerId id : idList)
			{
				this.bindEntity( gatewayId, id.getId(), entityType );
				
				if (entityType == EntityType.Node)
				{
					logger.info( "bindExistingResourcesToLocalGateway , EntityType.Node, nodeid : " + id.getId());
					List<EdgeConnectInfo> connInfoList = new ArrayList<>();
					this.connInfoDao.as_edge_connect_info_list( id.getId(), connInfoList );
					if (connInfoList.isEmpty()){
						logger.info( "bindExistingResourcesToLocalGateway , connect info is empty, nodeid : " + id.getId());
						continue;
					}
					logger.info( "bindExistingResourcesToLocalGateway, nodeid : " + id.getId() + ", node manage info : " + connInfoList.get( 0 ).getManaged() + ", node rpsmanage info :" + connInfoList.get( 0 ).getRpsmanaged());
					if (connInfoList.get( 0 ).getManaged() == 1 || connInfoList.get( 0 ).getRpsmanaged() == 1)
						nodesToUpdateRegFile_local.add( id.getId() );
					logger.info( "bindExistingResourcesToLocalGateway, entityType : Node, nodesToUpdateRegFile_local size :" + (nodesToUpdateRegFile_local ==null ? 0 : nodesToUpdateRegFile_local.size()));
				}
			}
		}
		
		// Record those nodes whose registration file needs to be updated, and
		// do the update when the local gateway host announces it's ready.
		
		synchronized (nodesToUpdateRegFile)
		{
			logger.info( "bindExistingResourcesToLocalGateway, nodesToUpdateRegFile_local size :" + (nodesToUpdateRegFile_local ==null ? 0 : nodesToUpdateRegFile_local.size()));
			nodesToUpdateRegFile.addAll( nodesToUpdateRegFile_local );
		}
		
		logger.info( "Finish binding existing entities to local gateway." );
	}
	
	@Override
	public void updateRegInfoOfExistingNode( GatewayId gatewayId, int nodeId )
	{
		String logPrefix = this.getClass().getSimpleName() + ".updateRegInfoOfExistingNode(): ";
	
		try
		{
			logger.info( logPrefix + "Update registeration info of legacy node. Node ID: " + nodeId );
			
			GatewayEntity gateway = this.getGatewayById( gatewayId );

			List<EdgeHost> hostList = new ArrayList<>();
			this.hostMgrDao.as_edge_host_list( nodeId, 1, hostList );
			if (hostList.size() == 0)
			{
				logger.error( logPrefix + "Error getting node info. Node ID: " + nodeId );
				return;
			}
			
			EdgeHost host = hostList.get( 0 );
			
			if ((host.getProtectionTypeBitmap() & ProtectionType.WIN_D2D.getValue()) != 0)
			{
				updateRegInfoOfExistingD2DNode( gateway, nodeId, host );
				updateRegInfoOfExistingASBUNode( gateway, nodeId, host );
			}
			if ((host.getProtectionTypeBitmap() & ProtectionType.LINUX_D2D_SERVER.getValue()) != 0)
			{
				updateRegInfoOfExistingLinuxNode( gateway, nodeId, host );
			}
			if ((host.getProtectionTypeBitmap() & ProtectionType.RPS.getValue()) != 0)
			{
				updateRegInfoOfExistingRPSNode( gateway, nodeId, host );
			}
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error updating registeration info. Node ID: " + nodeId, e );
		}
	}

	private void updateRegInfoOfExistingD2DNode( GatewayEntity gateway, int nodeId, EdgeHost host )
	{
		String logPrefix = this.getClass().getSimpleName() + ".updateRegInfoOfExistingD2DNode(): ";
		
		try
		{
			if (!ApplicationUtil.isD2DInstalled( host.getAppStatus() ))
				logger.warn( logPrefix + "d2d seems not installed:" + nodeId +"("+host.getAppStatus()+")" );
			
			List<EdgeConnectInfo> connInfoList = new ArrayList<>();
			this.connInfoDao.as_edge_connect_info_list( nodeId, connInfoList );
			if (connInfoList.size() == 0)
			{
				logger.error( logPrefix + "Error getting connection info. Node ID: " + nodeId );
				return;
			}
			
			EdgeConnectInfo connInfo = connInfoList.get( 0 );
			Protocol protocol = connInfo.getProtocol() == 1 ? Protocol.Http : Protocol.Https;
		
			ConnectionContext context = new ConnectionContext( protocol, connInfo.getRhostname(), connInfo.getPort() );
			context.buildCredential( connInfo.getUsername(), connInfo.getPassword(), "" );
			context.setGateway( gateway );
			
			IEdgeD2DRegService regService = EdgeD2DRegServiceFactory.create();
			regService.UpdateRegInfoToD2D( context, nodeId, true );
			
			logger.info( logPrefix + "Updating registeration info successfully. Node ID: " + nodeId );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error updating registeration info. Node ID: " + nodeId, e );
		}
	}
	
	private void updateRegInfoOfExistingASBUNode( GatewayEntity gateway, int nodeId, EdgeHost host )
	{
		String logPrefix = this.getClass().getSimpleName() + ".updateRegInfoOfExistingASBUNode(): ";
		
		try
		{
			if (!ApplicationUtil.isArcserveInstalled( host.getAppStatus() ))
				return;
			
			EdgeWebServiceImpl serviceImpl = new EdgeWebServiceImpl();
			
			List<EdgeArcserveConnectInfo> connInfoList = new ArrayList<>();
			this.connInfoDao.as_edge_arcserve_connect_info_list( nodeId, connInfoList );
			if (connInfoList.size() == 0)
			{
				logger.error( logPrefix + "Error getting connection info. Node ID: " + nodeId );
				return;
			}
			
			EdgeArcserveConnectInfo connInfo = connInfoList.get( 0 );
			Protocol protocol = connInfo.getProtocol() == 1 ? Protocol.Http : Protocol.Https;
			String password = (connInfo.getCapasswd() == null) ? "" : connInfo.getCapasswd();
			
			ABFuncAuthMode arcserveAuthMode = ABFuncAuthMode.values()[connInfo.getAuthmode()];
			String sessionID = serviceImpl.ConnectARCserve(gateway, host.getRhostname(), connInfo.getCauser(),
				password, arcserveAuthMode, connInfo.getPort(), protocol );
			
			ABFuncServerType arcserveBackupType = serviceImpl.GetServerType( sessionID );
			if ((arcserveBackupType != ABFuncServerType.BRANCH_PRIMARY) &&
				(arcserveBackupType != ABFuncServerType.ARCSERVE_MEMBER) &&
				(arcserveBackupType != ABFuncServerType.UN_KNOWN))
			{
				// check whether it can be marked as managed
				String uuid = CommonUtil.retrieveCurrentAppUUID();
				
				String arcserveUUID = serviceImpl.MarkArcserveManageStatus(
					sessionID, uuid, true, ABFuncManageStatus.MANAGED );
			}
			
			logger.info( logPrefix + "Updating registeration info successfully. Node ID: " + nodeId );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error updating registeration info. Node ID: " + nodeId, e );
		}
	}
	
	private void updateRegInfoOfExistingLinuxNode( GatewayEntity gateway, int nodeId, EdgeHost host )
	{
		String logPrefix = this.getClass().getSimpleName() + ".updateRegInfoOfExistingLinuxNode(): ";
		
		try
		{
			List<EdgeConnectInfo> connInfoList = new ArrayList<>();
			this.connInfoDao.as_edge_connect_info_list( nodeId, connInfoList );
			if (connInfoList.size() == 0)
			{
				logger.error( logPrefix + "Error getting connection info. Node ID: " + nodeId );
				return;
			}
			
			EdgeConnectInfo connInfo = connInfoList.get( 0 );
			String protocol = connInfo.getProtocol() == 1 ? "http" : "https";
			
			IEdgeLinuxD2DRegService regService = new EdgeLinuxD2DRegServiceImpl();
			regService.RegInfoToLinuxD2D( gateway.getId(),
				connInfo.getRhostname(), connInfo.getPort(), protocol, connInfo.getUsername(), connInfo.getPassword(),
				true, false );
			
			logger.info( logPrefix + "Updating registeration info successfully. Node ID: " + nodeId );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error updating registeration info. Node ID: " + nodeId, e );
		}
	}
	
	private IEdgeRPSRegService rpsRegService = null;
	
	private synchronized IEdgeRPSRegService getRpsRegService()
	{
		if (this.rpsRegService == null)
			this.rpsRegService = new EdgeRPSRegServiceImpl();
		return this.rpsRegService;
	}
	
	private void updateRegInfoOfExistingRPSNode( GatewayEntity gateway, int nodeId, EdgeHost host )
	{
		String logPrefix = this.getClass().getSimpleName() + ".updateRegInfoOfExistingRPSNode(): ";
		
		try
		{
			List<EdgeConnectInfo> connInfoList = new ArrayList<>();
			this.connInfoDao.as_edge_connect_info_list( nodeId, connInfoList );
			if (connInfoList.size() == 0)
			{
				logger.error( logPrefix + "Error getting connection info. Node ID: " + nodeId );
				return;
			}
			
			EdgeConnectInfo connInfo = connInfoList.get( 0 );
			Protocol protocol = connInfo.getProtocol() == 1 ? Protocol.Http : Protocol.Https;
			
			ConnectionContext context = new ConnectionContext( protocol, connInfo.getRhostname(), connInfo.getPort() );
			context.buildCredential( connInfo.getUsername(), connInfo.getPassword(), "" );
			context.setGateway( gateway );
			
			getRpsRegService().UpdateRegInfoToRpsServer( context, nodeId, true );
			
			logger.error( logPrefix + "Updating registeration info successfully. Node ID: " + nodeId );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error updating registeration info. Node ID: " + nodeId, e );
		}
	}

	private void notifyTheLocalRegistration( GatewayRegistrationResult regResult )
	{
		try
		{
			EdgeRegistryInfo edgeRegInfo = CommonUtil.getApplicationRegistryInfo();
			notifyRegResultToGatewayHost( regResult, "localhost",
				edgeRegInfo.getWebServerPort(), edgeRegInfo.getWebServerProtocol() );
		}
		catch (Exception e)
		{
			logger.error( "Error notifying the local gateway service regarding the registration.", e );
		}
	}
	
	@SuppressWarnings( "serial" )
	public static class FailedToConnectGatewayHostException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class FailedToNotifyGatewayHostException extends Exception {}
	
	public static void notifyRegResultToGatewayHost( GatewayRegistrationResult regResult,
		String hostName, int port, String protocol ) throws
		FailedToConnectGatewayHostException,
		FailedToNotifyGatewayHostException
	{
		logger.info( "Begin to notify gateway's web service." );
		
		assert regResult != null : "Invalid registration result.";
		
		IEdgeGatewayService edgeService = null;
		
		for (int i = 0; i < 10; i ++)
		{
			try
			{
				edgeService = connectGatewayHost( hostName, port, protocol );
				break;
			}
			catch (Exception e)
			{
				logger.error( "Cannot connect to the gateway host. Tries: " + (i + 1), e );
			}
			
			try
			{
				Thread.sleep( 3000 );
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
		
		if (edgeService == null)
			throw new FailedToConnectGatewayHostException();
		
		try
		{
			edgeService.onGatewayHostRegistered( regResult );
		}
		catch (Exception e)
		{
			logger.error( "Failed to notify the gateway host.", e );
			throw new FailedToNotifyGatewayHostException();
		}
		
		logger.info( "Successfully notified gateway's web service." );
	}

	@SuppressWarnings( "deprecation" )
	private static IEdgeGatewayService connectWebService(
		String hostname, int port, String protocol, String portName, String serviceName, String contextPath, GatewayProxyInfo proxyInfo, boolean staticWSDL) throws Exception
	{
		BaseWebServiceClientProxy webService = null;
		
		// connect web service
		
		try
		{
			String wsdlPath = "";
			
//			wsdlPath = String.format( contextPath + "services/%s?wsdl", serviceName );
			wsdlPath = String.format(  "services/%s?wsdl", serviceName );

			// Set ServiceInfo
			ServiceInfo serviceInfo = new ServiceInfo();
			serviceInfo.setBindingType( ServiceInfoConstants.SERVICE_BINDING_SOAP11 );
			serviceInfo.setNamespace( ServiceInfoConstants.SERVICE_EDGE_PROPER_NAMESPACE );
			serviceInfo.setPortName( portName );
			serviceInfo.setServiceName( serviceName );

			if (!protocol.endsWith( ":" ))
				protocol = protocol + ":";
			
			String wsdlURL = protocol + "//" + hostname + ":" + port
				+ "/" //CommonUtil.CENTRAL_MANAGER_CONTEXT_PATH
				+ wsdlPath;
			serviceInfo.setWsdlURL( wsdlURL );
			
			logger.info("wsdl url :" + wsdlURL);

			// Set ServiceInfoConstants
			String serviceID = ServiceInfoConstants.SERVICE_ID_EDGE_PROPER;
			
			IWebServiceFactory serviceFactory = new BaseWebServiceFactory();
			if(staticWSDL){// local wsdl, used when gateway connect console
				webService = serviceFactory.getWebServiceByProxy(
						protocol, hostname, port, serviceID, serviceInfo, proxyInfo, IEdgeGatewayService.class);
			}else{// remote wsdl, used when console connect local gateway
				webService = serviceFactory.getWebService(
						protocol, hostname, port, serviceID, serviceInfo, IEdgeGatewayService.class );
			}	
			
			if (webService == null)
				throw new Exception();
		}
		catch (Exception e)
		{
			throw e;
		}
		
		return (IEdgeGatewayService) webService.getService();
	}
	
	/**
	 * Connect to the gateway service of the console's web service.
	 * 
	 * @param hostname
	 * @param port
	 * @param protocol
	 * @return
	 * @throws Exception
	 */
	public static IEdgeGatewayService connectConsole( String hostname, int port, String protocol, GatewayProxyInfo proxyInfo) throws Exception
	{
		String portName = ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_PORT_NAME;
		String serviceName = ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_SERVICE_NAME;
		if(proxyInfo != null){
			logger.info("EdgeGatewayBean, connectConsole,  GatewayProxyInfo:"  + proxyInfo.toString());
			if(!StringUtil.isEmptyOrNull(proxyInfo.getServer()) && proxyInfo.getPort() != 0){
				HttpProxy clientHttpProxy = new HttpProxy(proxyInfo.getServer(), proxyInfo.getPort(), proxyInfo.getUsername(), proxyInfo.getPassword());
				CAProxy proxy = new CAProxy();
				proxy.setTargetHost(hostname);
				proxy.setHttpProxy(clientHttpProxy);
				CAProxySelector.getInstance().registryProxy(proxy);
			}else{
				proxyInfo = null;
			}
		}
		return connectWebService( hostname, port, protocol, portName, serviceName, getConsoleContext(), proxyInfo, true);
		//return connectWebService( hostname, port, protocol, portName, serviceName, "" );
	}
	
	// Following is the solution for local debugging. Console started console can only
	// run in context "/" while the context is "management/" in real build. So, put
	// a text file ConsoleContext.txt into the configuration folder and specify the
	// context path in the first line.
	
	private static final String CONSOLE_CONTEXT_FILENAME = "ConsoleContext.txt";
	private static final String DEFAULT_CONSOLE_CONTEXT = "management/";
	
	private static String getConsoleContext()
	{
		try
		{
			String filePath = getConsoleContextFilePath();
			File file = new File( filePath );
			if (!file.exists())
			{
				logger.info( "The console context customization file doesn't exist, will use the default one '" +
					DEFAULT_CONSOLE_CONTEXT + "'" );
				return DEFAULT_CONSOLE_CONTEXT;
			}
			
			BufferedReader reader = new BufferedReader( new FileReader( filePath ));
			String context = reader.readLine();
			reader.close();

			context = (context == null) ? "" : context.trim();
			logger.info( "Customized concole context specified, the value is '" + context + "'" );
			
			return context;
		}
		catch (Exception e)
		{
			logger.info( "Error reading the console context customization file, will use the default one '" +
				DEFAULT_CONSOLE_CONTEXT + "'" );
			return DEFAULT_CONSOLE_CONTEXT;
		}
	}
	
	public static String getConsoleContextFilePath()
	{
		String folderPath = CommonUtil.getConfigurationFolder( EdgeApplicationType.CentralManagement );
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		return folderPath + CONSOLE_CONTEXT_FILENAME;
	}
	
	/**
	 * Connect to the gateway service of the gateway host's web service.
	 * 
	 * @param hostName
	 * @param port
	 * @param protocol
	 * @return
	 * @throws Exception
	 */
	public static IEdgeGatewayService connectGatewayHost( String hostName, int port, String protocol ) throws Exception
	{
		String portName = ServiceInfoConstants.SERVICE_EDGE_PROPER_PORT_NAME;
		String serviceName = ServiceInfoConstants.SERVICE_EDGE_PROPER_SERVICE_NAME;
		
		return connectWebService( hostName, port, protocol, portName, serviceName, "gateway/", null, false);
	}

	@Override
	public void onGatewayHostRegistered( GatewayRegistrationResult regResult )
		throws EdgeServiceFault
	{
		EdgeEventCenter.getInstance().publishEvent(
			new GatewayHostRegisteredEvent( regResult.getConsoleUuid(), regResult.getGatewayUuid() ), this );
	}
	
	private static final String MESSAGESERVICE_USERNAME = "UDPMessageServiceUser";

	@Override
	public boolean validateMessageServiceCredential( String username, String password )
	{
		if ((username == null) || (password == null))
			return false;
		
		if (!username.equals( this.getMqBrokerUsername() ))
			return false;
		
		if (!password.equals( this.getMqBrokerPassword() ))
			return false;
		
		return true;
	}

	@Override
	public String getMqBrokerUsername()
	{
		return MESSAGESERVICE_USERNAME;
	}

	@Override
	public String getMqBrokerPassword()
	{
		return CommonUtil.retrieveCurrentAppUUIDWithDecrypt( false );
	}

	@Override
	public boolean isGatewayRegistered( GatewayEntity gateway )
	{
		if (gateway == null)
			return false;
		
		if ((gateway.getHostUuid() == null) || gateway.getHostUuid().trim().isEmpty())
			return false;
		
		return true;
	}

	private boolean isGatewayRegistered( EdgeGatewayEntity daoGateway )
	{
		GatewayEntity gateway = this.convertDaoGatewayEntity( daoGateway );
		return isGatewayRegistered( gateway );
	}

	@Override
	public SitePagingResult pageQuerySites(SiteFilter filter,
			SitePagingConfig loadConfig) throws EdgeServiceFault {

		if (filter == null)
			filter = new SiteFilter();		
		if (filter.getNamePattern() == null)
			filter.setNamePattern( "*" );
		if(!filter.getNamePattern().contains("*") && !filter.getNamePattern().contains("?"))
			filter.setNamePattern( filter.getNamePattern()+"*" );
		
		filter.setNamePattern( filter.getNamePattern().replace( "*", "%" ).replace( "?", "_" ) );
				
		List<EdgeSiteInfo> daoSiteInfoList = new ArrayList<>();
		int[] totalCount = new int[1];
		this.gatewayDao.pageQuerySites(
				filter.getIsGatewayValid(),
				filter.getNamePattern(),
				loadConfig.getStartIndex(),
				loadConfig.getPageSize(),
				loadConfig.getSortOrder().value(),
				loadConfig.getSortColumn().value(),
				totalCount, 
				daoSiteInfoList);
		
		List<SiteInfo> siteInfoList = new ArrayList<>();
		for (EdgeSiteInfo daoSiteInfo : daoSiteInfoList){
			siteInfoList.add( convertDaoSiteInfo( daoSiteInfo ) );
		}
		
		SitePagingResult result = new SitePagingResult();
		result.setData(siteInfoList);
		result.setTotalCount(totalCount[0]);
		result.setStartIndex(loadConfig.getStartIndex());
		if( (totalCount[0] - loadConfig.getStartIndex()) < loadConfig.getPageSize())
			result.setCount(totalCount[0] - loadConfig.getStartIndex());
		else 
			result.setCount(loadConfig.getPageSize());
		return result;
	}
	
	@Override
	public GatewayHostHeartbeatResponse2 gatewayHostHeartbeat( GatewayHostHeartbeatParam param )
		throws EdgeServiceFault
	{
		if ((param == null) || (param.getGatewayUuid() == null) || param.getGatewayUuid().isEmpty() ||
			(param.getHostUuid() == null) || param.getHostUuid().isEmpty())
		{
			logger.error( "Invalid gateway host heartbeat parameter. Param: " + param );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_InvalidHeartbeatParam,
				"GATEWAY_InvalidHeartbeatParam" );
		}
		
		List<EdgeGatewayEntity> gatewayList = new ArrayList<>();
		this.gatewayDao.getGatewayByUuid( param.getGatewayUuid(), gatewayList );
		if (gatewayList.size() == 0)
		{
			logger.error( "The specified gateway cannot be found. Gateway UUID: " + param.getGatewayUuid() );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayNotFound,
				"GATEWAY_GatewayNotFound" );
		}
		
		EdgeGatewayEntity daoGateway = gatewayList.get( 0 );
		
		if ((daoGateway.getHostUuid() == null) || daoGateway.getHostUuid().trim().isEmpty())
		{
			logger.error( "The specified gateway is not registered to a host. Gateway UUID: " +
				param.getGatewayUuid() );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayHostNotRegistered,
				"GATEWAY_GatewayHostNotRegistered" );
		}
		
		if (!daoGateway.getHostUuid().equalsIgnoreCase( param.getHostUuid() ))
		{
			logger.error( "The specified gateway is registered to another host. Gateway UUID: " +
				param.getGatewayUuid() );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.GATEWAY_GatewayRegisteredToAnotherHost,
				"GATEWAY_GatewayRegisteredToAnotherHost" );
		}
		
		updateLastContactTime( new GatewayId( daoGateway.getId() ) );
		
		EdgeSimpleVersion consoleVersion = getConsoleVersion();
		boolean needToUpdate = doesGatewayNeedToUpdate( consoleVersion, param.getHostVersion() );
		
		// After discussion with Yongjun, there is no chance that console to change
		// the update status here, and currently gateway will not launch upgrade even
		// when it get the response which tell it need to upgrade, so it's no need
		// to change the upgrade status here.
		//
		// Bo Pang
		// 2015-11-24
		
//		if (needToUpdate)
//		{
//			if (isItOkayToSetGatewayUpdateStatus( daoGateway.getId() ))
//			{
//				String message = String.format(
//					"Version of gateway '%s' is %s while console version is %s. " +
//					"The gateway need to be updated.",
//					daoGateway.getName(), param.getHostVersion().toVersionString(), consoleVersion.toVersionString() );
//				logger.info( message );
//				
//				this.gatewayDao.setGatewayUpdateStatus(
//					daoGateway.getId(), GatewayUpdateStatusCode.NotifyingGatewayToUpdate.getValue(),
//					"", null, 0 );
//			}
//		}
		
		GatewayHostHeartbeatResponse2 response = new GatewayHostHeartbeatResponse2();
		response.setNeedToUpdate( needToUpdate );
		response.setConsoleVersion( consoleVersion );
		
		return response;
	}
	
	@Override
	public void updateLastContactTime( GatewayId gatewayId )
	{
		GatewayContactTimeUpdater.getInstance().updateGateway( gatewayId );
	}
	
	private EmailTemplateSetting getEmailTemplateSetting() throws Exception
	{
		String logPrefix = this.getClass().getSimpleName() + ".sendAlertEmail(): ";
		
		EmailTemplateSetting emailTemplate = null;
		IEdgeConfigurationService configService = new ConfigurationServiceImpl();
		
		try
		{
			int templateId = EmailTemplateFeature.D2DPolicy;
			emailTemplate = configService.getEmailTemplateSetting( templateId );
		}
		catch (Exception e)
		{
			throw new Exception( logPrefix + "Error loading email template.", e );
		}
		
		return emailTemplate;
	}
	
	private void sendGatewayOverwrittenAlertEmail( AlertContentProvider contentProvider )
	{
		String logPrefix = this.getClass().getSimpleName() + ".sendAlertEmail(): ";
		logger.info( logPrefix + "Send alert email." );
		
		try
		{
			int templateId = EmailTemplateFeature.D2DPolicy;
			
			// load email template
			
			EmailTemplateSetting emailTemplate = getEmailTemplateSetting();
			if (emailTemplate == null)
			{
				logger.info( logPrefix + "No email template, cancel sending alert email." );
				return;
			}
			
			// build email contents
			
			EdgeEmailService emailService = EdgeEmailService.GetInstance();
			String udpHost = emailService.getHostName();
			
			String subject = contentProvider.getAlertSubject( emailTemplate.getSubject() );
			
			String contents = contentProvider.getAlertContents(
				emailTemplate.getSubject(), udpHost, (emailTemplate.getHtml_flag() == 1) );
			
			// send email
			
			emailService.SendMailWithGlobalSetting( udpHost, subject, contents, templateId );
			
			logger.info( logPrefix + "Alert email was sent." );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error sending alert email.", e );
		}
	}
	
	private void sendUdpAlert( AlertContentProvider contentProvider )
	{
		String logPrefix = this.getClass().getSimpleName() + ".sendUdpAlert(): ";
		logger.info( logPrefix + "Send UDP alert." );
		
		try
		{
			// load email template
			
			EmailTemplateSetting emailTemplate = getEmailTemplateSetting();
			if (emailTemplate == null)
			{
				logger.info( logPrefix + "No email template, cancel sending alert email." );
				return;
			}
			
			// build alert contents
			
			EdgeEmailService emailService = EdgeEmailService.GetInstance();
			String udpHost = emailService.getHostName();
			
			String subject = contentProvider.getAlertSubject( emailTemplate.getSubject() );
			
			String contents = contentProvider.getAlertContents(
				emailTemplate.getSubject(), udpHost, true );
			
			// send alert
			
			Date date = new Date();
			
			AlertManager.getInstance().saveAlertToDB(
				udpHost, udpHost, -1L, CommonEmailInformation.EVENT_TYPE.UDP_GATEWAY_EVENT.getValue(),
				subject, contents, date, CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );
			
			logger.info( logPrefix + "UDP alert was sent." );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error sending UDP alert.", e );
		}
	}

	private static interface AlertContentProvider
	{
		String getAlertSubject( String baseTitle );
		String getAlertContents( String baseTitle, String udpHost, boolean isInHtml ) throws Exception;
	}
	
	private static abstract class BaseAlertContentProvider implements AlertContentProvider
	{
		protected String readTemplate( String templateFile ) throws IOException
		{
			String templateFileName = templateFile;
			InputStream stream = this.getClass().getResourceAsStream( templateFileName );
			BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
			StringBuilder strBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
			{
				strBuilder.append( line );
				strBuilder.append( "\n" );
			}
			reader.close();
			return strBuilder.toString();
		}
		
		@Override
		public String getAlertContents( String baseTitle, String udpHost, boolean isInHtml )
			throws Exception
		{
			String template = null;
			
			String templateFile = isInHtml ?
				"/resources/AlertEmailHtmlTemplate.html" : "/resources/AlertEmailPlainTextTemplate.txt";
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat( MessageReader.getDateFormat( "timeDateFormat" ) );
			Date now = new Date();
			
			String title = getAlertSubject( baseTitle );
			String contents = this.getAlertMessage();
			String signature = EdgeCMWebServiceMessages.getMessage( "alertEmail_Signature" );
			String copyright = EdgeCMWebServiceMessages.getMessage( "alertEmail_Copyright" );
			
			template = readTemplate( templateFile );
			template = template.replace( "%TITLE%", title );
			template = template.replace( "%TIME%", dateFormatter.format( now ) );
			template = template.replace( "%CONTENTS%", contents );
			template = template.replace( "%SIGNATURE%", signature );
			template = template.replace( "%COPYRIGHT%", copyright );
			
			return template;
		}

		protected abstract String getAlertMessage();
	}
	
	private static class GatewayOverwrittenAlertContentProvider extends BaseAlertContentProvider
	{
		private EdgeSiteInfo siteInfo;
		private EdgeGatewayEntity gatewayEntity;
		
		public GatewayOverwrittenAlertContentProvider( EdgeSiteInfo siteInfo, EdgeGatewayEntity gatewayEntity )
		{
			this.siteInfo = siteInfo;
			this.gatewayEntity = gatewayEntity;
		}
		
		@Override
		public String getAlertSubject( String baseTitle )
		{
			return EdgeCMWebServiceMessages.getMessage(
				"gateway_GatewayHostOverwritten_AlertTitle", baseTitle );
		}

		@Override
		protected String getAlertMessage()
		{
			return EdgeCMWebServiceMessages.getMessage(
				"gateway_GatewayHostOverwritten_AlertContents", siteInfo.getName() );
		}
	}
	
	private static class GatewayUpgradationFailureAlertContentProvider extends BaseAlertContentProvider
	{
		private EdgeSiteInfo siteInfo;
		private String errorMessage;
		
		public GatewayUpgradationFailureAlertContentProvider( EdgeSiteInfo siteInfo, String errorMessage )
		{
			this.siteInfo = siteInfo;
			this.errorMessage = errorMessage;
		}
		
		@Override
		public String getAlertSubject( String baseTitle )
		{
			return EdgeCMWebServiceMessages.getMessage(
				"gateway_GatewayHostUpgradationFail_AlertTitle", baseTitle );
		}

		@Override
		protected String getAlertMessage()
		{
			return EdgeCMWebServiceMessages.getMessage(
				"gateway_GatewayHostUpgradationFail_AlertContents", siteInfo.getName(), errorMessage );
		}
	}
	
//	private void sendGatewayOverwrittenAlertEmail( EdgeSiteInfo siteInfo, EdgeGatewayEntity gatewayEntity )
//	{
//		String logPrefix = this.getClass().getSimpleName() + ".sendAlertEmail(): ";
//		logger.info( logPrefix + "Send alert email." );
//		
//		try
//		{
//			int templateId = EmailTemplateFeature.D2DPolicy;
//			
//			// load email template
//			
//			EmailTemplateSetting emailTemplate = getEmailTemplateSetting();
//			if (emailTemplate == null)
//			{
//				logger.info( logPrefix + "No email template, cancel sending alert email." );
//				return;
//			}
//			
//			// build email contents
//			
//			EdgeEmailService emailService = EdgeEmailService.GetInstance();
//			String udpHost = emailService.getHostName();
//			
//			String subject = getAlertSubject( emailTemplate.getSubject() );
//			
//			String contents = getAlertEmailContents(
//				emailTemplate.getSubject(), udpHost, siteInfo, gatewayEntity,
//				(emailTemplate.getHtml_flag() == 1) );
//			
//			// send email
//			
//			emailService.SendMailWithGlobalSetting( udpHost, subject, contents, templateId );
//			
//			logger.info( logPrefix + "Alert email was sent." );
//		}
//		catch (Exception e)
//		{
//			logger.error( logPrefix + "Error sending alert email.", e );
//		}
//	}
//	
//	private String getAlertEmailContents( String baseTitle, String udpHost,
//		EdgeSiteInfo siteInfo, EdgeGatewayEntity gatewayEntity, boolean isInHtml ) throws IOException
//	{
//		String template = null;
//		
//		String templateFile = isInHtml ?
//			"/resources/AlertEmailHtmlTemplate.html" : "/resources/AlertEmailPlainTextTemplate.txt";
//		
//		SimpleDateFormat dateFormatter = new SimpleDateFormat( MessageReader.getDateFormat( "timeDateFormat" ) );
//		Date now = new Date();
//		
//		String title = getAlertSubject( baseTitle );
//		String contents = EdgeCMWebServiceMessages.getMessage(
//			"gateway_GatewayHostOverwritten_AlertContents", siteInfo.getName() );
//		String signature = EdgeCMWebServiceMessages.getMessage( "alertEmail_Signature" );
//		String copyright = EdgeCMWebServiceMessages.getMessage( "alertEmail_Copyright" );
//		
//		template = readTemplate( templateFile );
//		template = template.replace( "%TITLE%", title );
//		template = template.replace( "%TIME%", dateFormatter.format( now ) );
//		template = template.replace( "%CONTENTS%", contents );
//		template = template.replace( "%SIGNATURE%", signature );
//		template = template.replace( "%COPYRIGHT%", copyright );
//		
//		return template;
//	}
//	
//	private String getAlertSubject( String baseTitle )
//	{
//		return EdgeCMWebServiceMessages.getMessage(
//			"gateway_GatewayHostOverwritten_AlertTitle", baseTitle );
//	}
//	
//	private String readTemplate( String templateFile ) throws IOException
//	{
//		String templateFileName = templateFile;
//		InputStream stream = this.getClass().getResourceAsStream( templateFileName );
//		BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
//		StringBuilder strBuilder = new StringBuilder();
//		String line;
//		while ((line = reader.readLine()) != null)
//		{
//			strBuilder.append( line );
//			strBuilder.append( "\n" );
//		}
//		reader.close();
//		return strBuilder.toString();
//	}
//	
//	private void sendUdpAlert( EdgeSiteInfo siteInfo, EdgeGatewayEntity gatewayEntity )
//	{
//		String logPrefix = this.getClass().getSimpleName() + ".sendUdpAlert(): ";
//		logger.info( logPrefix + "Send UDP alert." );
//		
//		try
//		{
//			// load email template
//			
//			EmailTemplateSetting emailTemplate = getEmailTemplateSetting();
//			if (emailTemplate == null)
//			{
//				logger.info( logPrefix + "No email template, cancel sending alert email." );
//				return;
//			}
//			
//			// build alert contents
//			
//			EdgeEmailService emailService = EdgeEmailService.GetInstance();
//			String udpHost = emailService.getHostName();
//			
//			String subject = getAlertSubject( emailTemplate.getSubject() );
//			
//			String contents = getAlertEmailContents(
//				emailTemplate.getSubject(), udpHost, siteInfo, gatewayEntity,
//				true );
//			
//			// send alert
//			
//			Date date = new Date();
//			
//			AlertManager.getInstance().saveAlertToDB(
//				udpHost, udpHost, -1L, CommonEmailInformation.EVENT_TYPE.UDP_GATEWAY_EVENT.getValue(),
//				subject, contents, date, CommonEmailInformation.PRODUCT_TYPE.CPM.getValue() );
//			
//			logger.info( logPrefix + "UDP alert was sent." );
//		}
//		catch (Exception e)
//		{
//			logger.error( logPrefix + "Error sending UDP alert.", e );
//		}
//	}

	@Override
	public Boolean sendRegistrationEmail( SiteInfo siteInfo, EmailServerSetting setting)
			throws EdgeServiceFault {
		logger.info( "sendRegistrationEmail start." );
		try
		{
			Boolean flg = EdgeEmailService.GetInstance().sendTestMail(setting);	
			if(flg){
				writeActivityLog( Module.Common, Severity.Information, EdgeCMWebServiceMessages.getMessage(
						"gateway_SiteSendRegistrationEmailSucc", siteInfo.getName()) );
			} else {
				writeActivityLog( Module.Common, Severity.Error, EdgeCMWebServiceMessages.getMessage(
						"gateway_SiteSendRegistrationEmailFail", siteInfo.getName() ) );
			}
		}
		catch (EdgeServiceFault e)
		{
			logger.error( "sendRegistrationEmail error ", e );
			writeActivityLog( Module.Common, Severity.Error, EdgeCMWebServiceMessages.getMessage(
					"gateway_SiteSendRegistrationEmailFail", siteInfo.getName() ) );
			throw e;
		}
		return false;
	}

	@Override
	public Integer sendRegistrationEmails(List<SiteId> siteParams, String consoleURL)
			throws EdgeServiceFault {
		
		SendRegistrationEmailsParameter parameter = new SendRegistrationEmailsParameter();
		parameter.setModule(Module.SendRegistrationEmails);
		for(SiteId siteid : siteParams){
			parameter.getEntityIds().add(siteid.getRecordId());
			parameter.setConsoleURL(consoleURL);
		}
		ActionTaskManager<Integer> manager = new ActionTaskManager<Integer>(parameter);
		return manager.doAction();
	}
	
	private void setGatewayHeartbeatInterval( GatewayId gatewayId, int intervalInSecs )
	{
		IGatewayHostServiceFactory serviceFactory = EdgeFactory.getBean( IGatewayHostServiceFactory.class );
		IGatewayHostService gatewayHostService = serviceFactory.createGatewayHostService( gatewayId );
		gatewayHostService.setHeartbeatInterval( intervalInSecs );
	}
	
	private ActionItemId addSiteActionItem( SiteId siteId, SiteAction action, ActionSeverity severity, String description )
	{
		try
		{
			return ActionCenter.getInstance().addActionItem(
				ActionCategory.SiteManagement, action.getValue(), severity, description, siteId );
		}
		catch (Exception e)
		{
			logger.error( "Error adding action item for site. Site ID: " + siteId + ", Action: " + action, e );
			return null;
		}
	}
	
	private void deleteSiteActionItem( SiteId siteId, SiteAction action )
	{
		try
		{
			ActionCenter.getInstance().deleteActionItem(
				ActionCategory.SiteManagement, siteId, action.getValue() );
		}
		catch (Exception e)
		{
			logger.error( "Error deleting action item for site. Site ID: " + siteId + ", Action: " + action, e );
		}
	}
	
	private List<ActionItem> getSiteActionItems( SiteId siteId ) throws ActionCenterException
	{
		return ActionCenter.getInstance().getActionItems( ActionCategory.SiteManagement, siteId );
	}

	/**
	 * In fact, this function should not be put here. But I didn't find a better
	 * place for it. It will be moved to a better place later.
	 */
	@Override
	public void checkFirstRuns()
	{
		String logPrefix = this.getClass().getSimpleName() + ".checkFirstRuns(): ";

		try
		{
			logger.info( logPrefix + "Begin to check first runs ..." );
			
			// Get UUIDs
			
			String consoleUuid = CommonUtil.retrieveCurrentAppUUIDWithDecrypt( false );
			
			String consoleUuidInDB = "";
			String[] values = new String[1];
			this.settingDao.as_edge_configuration_getById( ConfigurationParam.ConsoleUuid.getValue(), values );
			if (values[0] != null)
				consoleUuidInDB = values[0];
			
			// Get versions
			
			EdgeSimpleVersion consoleVersion = getConsoleVersion();
			
			EdgeSimpleVersion consoleVersionInDB = new EdgeSimpleVersion();
			values = new String[1];
			this.settingDao.as_edge_configuration_getById( ConfigurationParam.ConsoleVersion.getValue(), values );
			if (values[0] != null)
				consoleVersionInDB = EdgeSimpleVersion.parseVersionString( values[0] );
			
			// Compare UUIDs and versions
			
			 // not the first run after installation or upgrading
			if (consoleUuid.equals( consoleUuidInDB ) && consoleVersion.equals( consoleVersionInDB ))
			{
				logger.info( logPrefix +
					"Both console UUID and version are same as those info in database, it's not the first run, just exit." );
				return;
			}
			
			generateProductPackageRegistryForLocalGateway( consoleVersion );
			
			// Update current UUID and version to DB
			
			logger.info( logPrefix + "Update new console UUID and version into database ..." );
			
			checkConsoleUUID(consoleUuid, consoleUuidInDB);
			
			this.settingDao.as_edge_configuration_insertOrUpdate(
				ConfigurationParam.ConsoleVersion.getValue(), ConfigurationParam.ConsoleVersion.name(), consoleVersion.toVersionString() );
			
			logger.info( logPrefix + "Console UUID and version got updated successfully." );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error checking first runs.", e );
		}
	}
	
	private void checkConsoleUUID(String consoleUuid, String consoleUuidInDB){
		// if console uuid in DB is null, get uuid from registry into DB
		// else if console uuid in the registry and DB is not same, update consoel uuid in registry by consoleUuidInDB
		// when console fresh setup without overwrite DB, console uuid will not change.
		if(consoleUuidInDB == null || consoleUuidInDB.equalsIgnoreCase("")){
			logger.info("console uuid is null in DB as_edge_configuration! update DB!");
			this.settingDao.as_edge_configuration_insertOrUpdate(
				ConfigurationParam.ConsoleUuid.getValue(), ConfigurationParam.ConsoleUuid.name(), consoleUuid );
		}else if(!consoleUuid.equals(consoleUuidInDB)){
			logger.info("console uuid in the registry and DB is not same! update registry!");
			CommonUtil.setApplicationExtentionKey(WindowsRegistry.VALUE_NAME_GUID, consoleUuidInDB);
		}
	}
	
	private void generateProductPackageRegistryForLocalGateway( EdgeSimpleVersion packageVersion )
	{
		String logPrefix = this.getClass().getSimpleName() + ".generateProductPackageRegistryForLocalGateway(): ";
		
		try
		{
			logger.info( logPrefix + "Begin to generate product package registry for local gateway ..." );
			
			ProductPackageRegistry packageRegistry = ProductPackageRegistry.load();
			
			PackageInfo packageInfo;
			
			packageInfo = new PackageInfo();
			packageInfo.setPackageState( ProductPackageState.Ready );
			packageInfo.getPackageVersion().copy( packageVersion );
			packageInfo.getPackageVersion().setUpdateNumber( 0 );
			packageInfo.getPackageVersion().setUpdateBuildNumber( 0 );
			packageInfo.setStateTime( new Date() );
			packageRegistry.setPackageInfo( ProductPackageType.GMPackage, packageInfo );
			packageRegistry.save();
			
			packageInfo = new PackageInfo();
			packageInfo.setPackageState( ProductPackageState.Ready );
			packageInfo.getPackageVersion().copy( packageVersion );
			packageInfo.setStateTime( new Date() );
			packageRegistry.setPackageInfo( ProductPackageType.UpdatePackage, packageInfo );
			packageRegistry.save();
			
			logger.info( logPrefix +
				"Product package registry for local gateway generated for version " +
					packageVersion.toVersionString() + " succesfully." );
		}
		catch (Throwable t)
		{
			logger.error( logPrefix + "Error generating product package registry for local gateway.", t );
		}
	}
	
	public static EdgeSimpleVersion getConsoleVersion()
	{
		try
		{
			IEdgeCommonService commonService = new EdgeCommonServiceImpl();
			EdgeVersionInfo consoleVersion = commonService.getVersionInformation();
			
			EdgeSimpleVersion simpleVersion = new EdgeSimpleVersion();
			simpleVersion.setMajorVersion( consoleVersion.getMajorVersion() );
			simpleVersion.setMinorVersion( consoleVersion.getMinorVersion() );
			simpleVersion.setBuildNumber( consoleVersion.getBuildNumber() );
			simpleVersion.setUpdateNumber( parseInt( consoleVersion.getUpdateNumber() ) );
			simpleVersion.setUpdateBuildNumber( parseInt( consoleVersion.getUpdateBuildNumber() ) );
			
			return simpleVersion;
		}
		catch (Exception e)
		{
			logger.error( "Error getting console version info.", e );
			return null;
		}
	}

	private static int parseInt( String string )
	{
		return (string == null) ? 0 : Integer.parseInt( string );
	}
	
	@Override
	public void announceGatewayHostReady( String gatewayUuid, String gatewayHostUuid ) throws EdgeServiceFault
	{
		List<EdgeGatewayEntity> gatewayList = new ArrayList<>();
		this.gatewayDao.getGatewayByUuid( gatewayUuid, gatewayList );
		if (gatewayList.isEmpty())
		{
			logger.warn( "Gateway host (uuid: " + gatewayHostUuid + ") registered for gateway (uuid: " +
				gatewayUuid + ") announces it's ready. But we cannot find that gateway." );
			return;
		}
		
		EdgeGatewayEntity gateway = gatewayList.get( 0 );
		logger.info( "Gateway host (uuid: " + gatewayHostUuid + ") registered for gateway (id: " +
			gateway.getId() + ") announces it's ready." );
		
		if ((gateway.getIsLocal() == 1))
		{
			bindExistingResourcesToLocalGateway(gateway);
			if(!nodesToUpdateRegFile.isEmpty()){
				logger.info( "Have legacy nodes need to update their registration file, submit a thread to do that." );
				EdgeExecutors.getCachedPool().submit( new UpdateLagacyNodesRunnable() );
			}
		}
	}
	
	class UpdateLagacyNodesRunnable implements Runnable
	{
		@Override
		public void run()
		{
			GatewayEntity gateway = getLocalGateway();
			
			synchronized (nodesToUpdateRegFile)
			{
				for (int nodeId : nodesToUpdateRegFile)
					updateRegInfoOfExistingNode( gateway.getId(), nodeId );
				
				nodesToUpdateRegFile.clear();
			}
		}
	}
	
	private static final String CONSOLE_CONTEXT							= "management/";
	
	private static final String PLACEHOLDER_CONSOLE_HOME				= "$CONSOLE_HOME";
	private static final String PLACEHOLDER_UPDATE_HOME					= "$UPDATE_HOME";
	
	private static final String GATEWAY_PKG_DOWNLOAD_ROOT				= "/" + CONSOLE_CONTEXT + "download/Gateway/";
	private static final String GATEWAY_PKG_CONSOLE_LOCAL_ROOT			= "$CONSOLE_HOME\\Deployment\\Gateway\\";
	
	private static final String GM_PACKAGE_FILENAME						= "Arcserve_Unified_Data_Protection_Gateway.exe";
	private static final String GM_PACKAGE_DOWNLOAD_PATH				= GATEWAY_PKG_DOWNLOAD_ROOT + GM_PACKAGE_FILENAME;
	private static final String GM_PACKAGE_CONSOLE_LOCAL_PATH			= GATEWAY_PKG_CONSOLE_LOCAL_ROOT + GM_PACKAGE_FILENAME;
	private static final String GM_PACKAGE_GATEWAY_LOCAL_PATH			= PLACEHOLDER_UPDATE_HOME + "\\" + GM_PACKAGE_FILENAME;
	
	private static final String UPDATE_INFO_FILE						= "$CONSOLE_HOME\\Deployment\\Gateway\\Update\\AvailableUpdateInfo.xml";
	
	private static final String INSTALL_UPDATES_EXE_FILENAME			= "InstallUpdates.exe";
	private static final String INSTALL_UPDATES_EXE_DOWNLOAD_PATH		= GATEWAY_PKG_DOWNLOAD_ROOT + INSTALL_UPDATES_EXE_FILENAME;
	private static final String INSTALL_UPDATES_EXE_CONSOLE_LOCAL_PATH	= GATEWAY_PKG_CONSOLE_LOCAL_ROOT + INSTALL_UPDATES_EXE_FILENAME;
	private static final String INSTALL_UPDATES_EXE_GATEWAY_LOCAL_PATH	= PLACEHOLDER_UPDATE_HOME + "\\" + INSTALL_UPDATES_EXE_FILENAME;
	
	private static final String GATEWAY_UPDATE_PKG_CONSOLE_LOCAL_ROOT	= GATEWAY_PKG_CONSOLE_LOCAL_ROOT + "Update\\";
	private static final String UPDATE_INFO_XML_PATH					= GATEWAY_UPDATE_PKG_CONSOLE_LOCAL_ROOT + "AvailableUpdateInfo.xml";
	
	@Override
	public void doGatewayUpdate( List<GatewayId> gatewayIdList ) throws EdgeServiceFault
	{
		logger.info( "Begin to launch gateway upgrade. Gateway ID list: " + gatewayIdList );
		
		GatewayUpdateStatus statusInfo = null;
		String message = null;
		
		IGatewayHostServiceFactory serviceFactory = EdgeFactory.getBean( IGatewayHostServiceFactory.class );
		for (GatewayId gatewayId : gatewayIdList )
		{
			if (!gatewayId.isValid())
			{
				logger.error( "Request to upgrade a gateway using an invalid gateway ID. Gateway ID: " + gatewayId );
				continue;
			}
			
			GatewayEntity gateway = this.getGatewayById( gatewayId ); // validate gateway id
			if (gateway == null)
			{
				logger.error( "Request to upgrade a gateway but failed to find it with the specified gateway ID. Gateway ID: " +
					gatewayId );
				continue;
			}
			
			// change upgrading status (including write activity log if needed)
			
			statusInfo = new GatewayUpdateStatus();
			statusInfo.setStatusCode( GatewayUpdateStatusCode.NotifyingGatewayToUpgrade );
			reportGatewayUpdateStatusInternal( gatewayId, statusInfo, false );
			
			// invoke corresponding API
			
			try
			{
				IGatewayHostService gatewayHostService = serviceFactory.createGatewayHostService( gatewayId );
				gatewayHostService.doGatewayUpdate();
				
				// Set the status again, the function will update gateway's last contact time for
				// upgrading. The success of above call means the gateway returned acknowledgement
				// and return the call, it's the of cause the gateway's last contact time.
				reportGatewayUpdateStatusInternal( gatewayId, statusInfo, false );
				
				logger.info( "Notified gateway to do upgrade. Gateway ID: " + gatewayId );
			}
			catch (Exception e)
			{
				logger.error( "Error notifying gateway to do upgrade. Gateway ID: " + gatewayId, e );
				
				// set failed status
				
				statusInfo = new GatewayUpdateStatus();
				statusInfo.setStatusCode( GatewayUpdateStatusCode.FailedToNotifyGatewayToUpgrade );
				
				IMessageServiceModule msgSvcModule = EdgeFactory.getBean( IMessageServiceModule.class );
				message = msgSvcModule.getLocalizedMessageOfException( e );
				if (message == null)
					message = "";
				
				message = EdgeCMWebServiceMessages.getMessage(
					"upgradeGateway_DetailedMessage_FailedToNotifyUpgradation", message );
				
				statusInfo.setDetailedMessage( message );
				reportGatewayUpdateStatus( gatewayId, statusInfo );
			}
		}
	}
	
	private boolean doesGatewayNeedToUpdate( EdgeSimpleVersion consoleVersion, EdgeSimpleVersion hostVersion )
	{
		return (consoleVersion.compareTo( hostVersion ) != 0);
	}

	@Override
	public GatewayUpdatesInfo getGatewayUpdateInfo( Version gatewayVersion )
		throws EdgeServiceFault
	{
		String logPrefix = this.getClass().getSimpleName() + ".getGatewayUpdateInfo(): ";
		
		if (gatewayVersion == null)
		{
			logger.error( logPrefix + "gatewayVersion is null." );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_BadParameter_IsNull,
				"gatewayVersion is null." );
		}
		
		CommonServiceFacade commonServiceFacade = CommonServiceFacade.getInstance();
		EdgeVersionInfo versionInfo = commonServiceFacade.getVersionInformation();
		
		Version consoleVersion = new Version();
		consoleVersion.setMajorVersion( versionInfo.getMajorVersion() );
		consoleVersion.setMinorVersion( versionInfo.getMinorVersion() );
		consoleVersion.setBuildNumber( Integer.toString( versionInfo.getBuildNumber() ) );
		consoleVersion.setUpdateInfo( parseStringNumber( versionInfo.getUpdateNumber() ),
			parseStringNumber( versionInfo.getUpdateBuildNumber() ) );
		
		GatewayUpdatesInfo response = new GatewayUpdatesInfo();
		response.setGmPackagePath( "" );
		response.setUpdatePackagePath( "" );
		
		int result = 0;
		UpdateDownloadFile updateFile;

		// InstallUpdates.exe
		
		updateFile = new UpdateDownloadFile();
		updateFile.setDownloadUrl( INSTALL_UPDATES_EXE_DOWNLOAD_PATH );
		updateFile.setLocalPath( INSTALL_UPDATES_EXE_GATEWAY_LOCAL_PATH );
		updateFile.setFileSize( getUpdateFileSize( INSTALL_UPDATES_EXE_CONSOLE_LOCAL_PATH ) );
		updateFile.setMD5FileUrl( "" );
		updateFile.setMd5FileSize( 0 );
		response.getUpdateFiles().add( updateFile );
		
		result = compareGMVersion( consoleVersion, gatewayVersion );
		if (result > 0)
		{
			// need new full package
			updateFile = new UpdateDownloadFile();
			updateFile.setDownloadUrl( GM_PACKAGE_DOWNLOAD_PATH );
			updateFile.setLocalPath( GM_PACKAGE_GATEWAY_LOCAL_PATH );
			updateFile.setFileSize( getUpdateFileSize( GM_PACKAGE_CONSOLE_LOCAL_PATH ) );
			updateFile.setMD5FileUrl( "" );
			updateFile.setMd5FileSize( 0 );
			response.getUpdateFiles().add( updateFile );
			
			response.setGmPackagePath( updateFile.getLocalPath() );
		}
		
		if (((result > 0) && (consoleVersion.getUpdateNumber() > 0)) ||
			(compareUpdateVersion( consoleVersion, gatewayVersion ) > 0))
		{
			String xmlPath = getConsoleLocalPath( UPDATE_INFO_XML_PATH );
			SetupUpdateInfo updateInfo = loadSetupUpdateInfo( xmlPath );
			if (updateInfo != null)
			{
				if (updateInfo.File.size() == 0)
				{
					logger.error( logPrefix + "No update file info in '" + xmlPath + "'" );
				}
				else
				{
					SetupFile setupFile = updateInfo.File.get( 0 );
					
					// need new update package
					updateFile = new UpdateDownloadFile();
					updateFile.setDownloadUrl( GATEWAY_PKG_DOWNLOAD_ROOT + "Update/" + setupFile.SourceFileURL );
					updateFile.setLocalPath( PLACEHOLDER_UPDATE_HOME + "\\Update\\" + setupFile.SourceFileURL );
					updateFile.setFileSize( getUpdateFileSize( GATEWAY_UPDATE_PKG_CONSOLE_LOCAL_ROOT + setupFile.SourceFileURL ) );
					updateFile.setMD5FileUrl( "" );
					updateFile.setMd5FileSize( 0 );
					response.getUpdateFiles().add( updateFile );
					
					response.setUpdatePackagePath( updateFile.getLocalPath() );
				}
			}
		}
		
		response.setConsoleVersion( consoleVersion );
		response.setPathOfInstallUpdatesExe( "InstallUpdates.exe" );
		
		return response;
	}
	
	// Returns:
	//  > 0		v1 > v2
	//	= 0		v1 = v2
	//	< 0		v1 < v2
	private int compareGMVersion( Version v1, Version v2 )
	{
		int result = 0;
		
		result = v1.getMajorVersion() - v2.getMajorVersion();
		if (result != 0)
			return result;
		
		result = v1.getMinorVersion() - v2.getMinorVersion();
		if (result != 0)
			return result;
		
		int buildNumber1 = parseStringNumber( v1.getBuildNumber() );
		int buildNumber2 = parseStringNumber( v2.getBuildNumber() );
		result = buildNumber1 - buildNumber2;
		if (result != 0)
			return result;
		
		return 0;
	}
	
	// Returns:
	//  > 0		v1 > v2
	//	= 0		v1 = v2
	//	< 0		v1 < v2
	private int compareUpdateVersion( Version v1, Version v2 )
	{
		int result = 0;
		
		result = v1.getUpdateNumber() - v2.getUpdateNumber();
		if (result != 0)
			return result;
		
		result = v1.getUpdateBuildNumber() - v2.getUpdateBuildNumber();
		if (result != 0)
			return result;
		
		return 0;
	}
	
	private int parseStringNumber( String numInStr )
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

	private long getFileSize( String filePath )
	{
		File file = new File( filePath );
		if (!file.exists())
			return -1;
		
		return file.length();
	}
	
	private String getConsoleLocalPath( String path )
	{
		String consoleHome = CommonUtil.BaseEdgeInstallPath;
		if (consoleHome.endsWith( "\\" ))
			consoleHome = consoleHome.substring( 0, consoleHome.length() - 1 );
		return path.replace( PLACEHOLDER_CONSOLE_HOME, consoleHome );
	}
	
	long getUpdateFileSize( String filePath )
	{
		return getFileSize( getConsoleLocalPath( filePath ) );
	}
	
	enum InstallUpdatesErrorCode
	{
		NoError						( 0 ),
		ErrorLaunchIntallUpdatesExe	( 1 ),
		ErrorLaunchingPackage		( 2 ),
		PackageReturnsError			( 3 );
		
		private int value;
		
		InstallUpdatesErrorCode( int value )
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return this.value;
		}
		
		public static InstallUpdatesErrorCode fromValue( int value )
		{
			for (InstallUpdatesErrorCode item : InstallUpdatesErrorCode.values())
			{
				if (item.value == value)
					return item;
			}
			return null;
		}
	}
	
	@Override
	public void reportGatewayUpdateStatus( GatewayId gatewayId, GatewayUpdateStatus statusInfo )
		throws EdgeServiceFault
	{
		this.reportGatewayUpdateStatusInternal( gatewayId, statusInfo, true );
	}
	
	public void reportGatewayUpdateStatusInternal(
		GatewayId gatewayId, GatewayUpdateStatus statusInfo, boolean checkStatusOrder )
		throws EdgeServiceFault
	{
		String logPrefix = this.getClass().getSimpleName() + ".reportGatewayUpdateStatus(): ";
		
		logger.info( logPrefix + "Enter. GatewayId: " + gatewayId + ", statusInfo: " + statusInfo );
		
		if ((gatewayId == null) || !gatewayId.isValid())
		{
			logger.error( logPrefix + "gatewayId is null or is invalid." );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_BadParameter_IsNull,
				"gatewayId is null or is invalid." );
		}
		
		if (statusInfo == null)
		{
			logger.error( logPrefix + "statusInfo is null." );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_BadParameter_IsNull,
				"statusInfo is null." );
		}
		
		// --------------------------------------------------------
		// check current status value in db
		// --------------------------------------------------------
		
		EdgeGatewayUpdateStatus statusInDb = null;
		List<EdgeGatewayUpdateStatus> statusList = new ArrayList<>();
		this.gatewayDao.getGatewayUpdateStatus( gatewayId.getRecordId(), statusList );
		if (statusList.size() > 0)
			statusInDb = statusList.get( 0 );
		
		if (checkStatusOrder && (statusInDb != null))
		{
			GatewayUpdateStatusCode currentStatus =
				GatewayUpdateStatusCode.fromValue( statusInDb.getUpdateStatus() );
			if (statusInfo.getStatusCode().getOrder() < currentStatus.getOrder())
			{
				logger.warn( logPrefix + "New status is more previous than the status in DB. " +
					"Status in DB: " + currentStatus + ", New status: " + statusInfo.getStatusCode() );
				return;
			}
		}
		
		// --------------------------------------------------------
		// set action flags and messages according to status
		// --------------------------------------------------------
		
		String detailedMessage = statusInfo.getDetailedMessage();
		Date startUpdateTime = null;
		Date reportTime = new Date();
		int hasStartTime = 0;
		boolean writeActivityLog = false;
		boolean sendAlert = false;
		boolean dontUpdate = false;
		
		if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.NotifyingGatewayToUpgrade)
		{
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_NotifyingGatewayToUpdate" );

			startUpdateTime = new Date();
			hasStartTime = 1;
			writeActivityLog = true;
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.FailedToNotifyGatewayToUpgrade)
		{
			detailedMessage = statusInfo.getDetailedMessage();
			
			writeActivityLog = true;
			sendAlert = true;
			
			logger.info( logPrefix + "Failed to notify gateway to do upgrade, and error message is: " +
				detailedMessage );
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.GettingUpdateInfo)
		{
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_GettingUpdateInfo" );
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.FailedToGetUpdateInfo)
		{
			logger.info( logPrefix +
				"Failed to get update info, get error message according to error code. Error code: " +
				statusInfo.getErrorCode() );
			
			detailedMessage = getGatewayUpdaterErrorMessage( statusInfo.getErrorCode() );
			
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_FailedToGetUpdateInfo", detailedMessage );
			
			writeActivityLog = true;
			sendAlert = true;
			
			logger.info( logPrefix + "Failed to get update info, and error message is: " +
				detailedMessage );
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.DownloadingUpdates)
		{
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_DownloadingUpdates" );
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.FailedToDownloadUpdates)
		{
			logger.info( logPrefix +
				"Failed to download updates, get error message according to error code. Error code: " +
				statusInfo.getErrorCode() );
			
			HttpDownloadResult httpDownloadResult = new HttpDownloadResult();
			httpDownloadResult.setErrorCategory( statusInfo.getErrorCode() );
			httpDownloadResult.setErrorCode( 0 );
			
			DownloadResultValue resultValue = ProductImageInsurer.httpDownloadResultToResultValue( httpDownloadResult );
			detailedMessage = ProductImageInsurer.getErrorMessage( resultValue );
			
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_FailedToDownloadUpdates", detailedMessage );
			
			writeActivityLog = true;
			sendAlert = true;
			
			logger.info( logPrefix + "Failed to download updates, and error message is: " +
				detailedMessage );
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.InstallingUpdates)
		{
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_InstallingUpdates" );
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.FailedToInstallUpdates)
		{
			logger.error( logPrefix +
				"Failed to install updates, get error message according to error code. Error code: " +
				statusInfo.getErrorCode() );
			
			detailedMessage = getInstallUpgradeDetailedMessage( statusInfo );
			
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_FailedToInstallUpdates", detailedMessage );
			
			writeActivityLog = true;
			sendAlert = true;
			
			logger.info( logPrefix + "Failed to install updates, and error message is: " +
				detailedMessage );
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.UpdatedSuccessfully)
		{
			if (statusInDb.getUpdateStatus() == GatewayUpdateStatusCode.NoNeedToUpdate.getValue())
				dontUpdate = true;
			
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_UpdatedSuccessfully",
				getLocalizedVersionString( statusInfo.getGatewayVersion() ) );
			
			writeActivityLog = true;
		}
		else if (statusInfo.getStatusCode() == GatewayUpdateStatusCode.UpdatedSuccessfullyNeedReboot)
		{
			logger.info( logPrefix + "Upgrade successfully, but reboot is needed." );
			
			detailedMessage = EdgeCMWebServiceMessages.getMessage(
				"upgradeGateway_DetailedMessage_UpdatedSuccessfullyNeedReboot",
				getLocalizedVersionString( statusInfo.getGatewayVersion() ) );
			
			writeActivityLog = true;
		}
		
		// --------------------------------------------------------
		// set status to database
		// --------------------------------------------------------
		
		if (!dontUpdate)
		{
			this.gatewayDao.setGatewayUpdateStatus( gatewayId.getRecordId(),
				statusInfo.getStatusCode().getValue(), detailedMessage, startUpdateTime, hasStartTime,
				reportTime );
		}
		
		// --------------------------------------------------------
		// write activity logs, send alert and email alert
		// --------------------------------------------------------
		
		if (writeActivityLog || sendAlert)
		{
			List<IntegerId> siteIdList = new ArrayList<>();
			this.gatewayDao.getSiteByGatewayId( gatewayId.getRecordId(), siteIdList );
			if (siteIdList.size() == 0)
			{
				logger.error( "Cannot find a site who is using gateway { id = " + gatewayId.getRecordId() + " }." );
				return;
			}
			
			List<EdgeSiteInfo> siteInfoList = new ArrayList<>();
			this.gatewayDao.getSiteInfo( siteIdList.get( 0 ).getId(), siteInfoList );
			if (siteIdList.size() == 0)
			{
				logger.error( "Error getting information of site { id = " + siteIdList.get( 0 ).getId() + " }." );
				return;
			}
			
			EdgeSiteInfo siteInfo = siteInfoList.get( 0 );
			
			// write activity log
			
			if (writeActivityLog)
			{
				String logMessage = EdgeCMWebServiceMessages.getMessage(
					"upgradeGateway_ActivityLog", siteInfo.getName(), detailedMessage );
				Severity severity = getActivityLogSeverityPerUpdateStatus( statusInfo.getStatusCode() );
				this.writeActivityLog( Module.GatewayManagement, severity, logMessage );
			}
			
			// send alert
			
			if (sendAlert)
			{
				GatewayUpgradationFailureAlertContentProvider contentProvider =
					new GatewayUpgradationFailureAlertContentProvider( siteInfo, detailedMessage );
				sendGatewayOverwrittenAlertEmail( contentProvider );
				sendUdpAlert( contentProvider );
			}
		}
	}
	
	private String getLocalizedVersionString( Version version )
	{
		String verString = "";
		
		if (version == null)
			return verString;
		
		if (version.getUpdateNumber() == 0)
		{
			verString = EdgeCMWebServiceMessages.getMessage( "VersionString",
				version.getMajorVersion(), version.getMinorVersion(), version.getBuildNumber() );
		}
		else // has update
		{
			verString = EdgeCMWebServiceMessages.getMessage( "VersionString_WithUpdate",
				version.getMajorVersion(), version.getMinorVersion(), version.getBuildNumber(),
				version.getUpdateNumber(), version.getUpdateBuildNumber() );
		}
		
		return verString;
	}
	
	private enum GatewayUpdaterErrorCode
	{
		OK						( 0 ),
		BadParameter			( -1 ),
		NoEnoughMemory			( -2 ),
		InvalidConnectionInfo	( -3 ),
		FailedToOpenHttpSession	( -4 ),
		FailedToConnect			( -5 ),
		FailedToSendHttpRequest	( -6 ),
		ErrorHttpStatus			( -7 ),
		FailedToReceiveData		( -8 ),
		FailedToUnmarshalData	( -9 );
		
		private int value;
		
		GatewayUpdaterErrorCode( int value )
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return this.value;
		}
		
		public static GatewayUpdaterErrorCode fromValue( int value )
		{
			for (GatewayUpdaterErrorCode item : GatewayUpdaterErrorCode.values())
			{
				if (item.value == value)
					return item;
			}
			return null;
		}
	};

	private String getGatewayUpdaterErrorMessage( int errorCode )
	{
		GatewayUpdaterErrorCode code = GatewayUpdaterErrorCode.fromValue( errorCode );
		
		String messageKey = null;
		switch (code)
		{
		case BadParameter:
			messageKey = "upgradeGateway_UpdaterError_BadParameter";
			break;
			
		case NoEnoughMemory:
			messageKey = "upgradeGateway_UpdaterError_NoEnoughMemory";
			break;
			
		case InvalidConnectionInfo:
			messageKey = "upgradeGateway_UpdaterError_InvalidConnectionInfo";
			break;
			
		case FailedToOpenHttpSession:
			messageKey = "upgradeGateway_UpdaterError_FailedToOpenHttpSession";
			break;
			
		case FailedToConnect:
			messageKey = "upgradeGateway_UpdaterError_FailedToConnect";
			break;
			
		case FailedToSendHttpRequest:
			messageKey = "upgradeGateway_UpdaterError_FailedToSendHttpRequest";
			break;
			
		case ErrorHttpStatus:
			messageKey = "upgradeGateway_UpdaterError_ErrorHttpStatus";
			break;
			
		case FailedToReceiveData:
			messageKey = "upgradeGateway_UpdaterError_FailedToReceiveData";
			break;
			
		case FailedToUnmarshalData:
			messageKey = "upgradeGateway_UpdaterError_FailedToUnmarshalData";
			break;
			
		default:
		}
		
		String message = (messageKey == null) ? "" : EdgeCMWebServiceMessages.getMessage( messageKey );
		return message;
	}
	
	private String getInstallUpgradeDetailedMessage( GatewayUpdateStatus statusInfo )
	{
		String message = "";
		
		switch (statusInfo.getMessageType())
		{
		case UpdaterMessage:
		case InstallerMessage:
			{
				InstallUpdatesErrorCode errorCode =
					InstallUpdatesErrorCode.fromValue( statusInfo.getErrorCode() );
				switch (errorCode)
				{
				case ErrorLaunchIntallUpdatesExe:
					message = EdgeCMWebServiceMessages.getMessage( "upgradeGateway_ErrorLaunchIntallUpdatesExe" );
					break;
					
				case ErrorLaunchingPackage:
					message = EdgeCMWebServiceMessages.getMessage( "upgradeGateway_ErrorLaunchingPackage" );
					break;
					
				default:
				}
			}
			break;
			
		case GmPackageMessage:
			if (statusInfo.getErrorCode() == 5) // INSTUPDERR_ERROR_EXTRACTING_PACKAGE
				message = EdgeCMWebServiceMessages.getMessage( "upgradeGateway_UpdaterError_FailedToExtractSetupPackage" );
			else
				message = statusInfo.getDetailedMessage();
			break;
			
		case UpdatePackageMessage:
			{
				int exitCode = statusInfo.getErrorCode();
				String messageKey = String.format( "upgradeGateway_SetupRet_0x%1$X", exitCode );
				message = EdgeCMWebServiceMessages.getMessage( messageKey );
				
				if (message.trim().isEmpty())
					message = EdgeCMWebServiceMessages.getMessage( "upgradeGateway_SetupRet_Unknown", exitCode );
			}
			break;
		}
		
		return message;
	}
	
	private Severity getActivityLogSeverityPerUpdateStatus( GatewayUpdateStatusCode statusCode )
	{
		switch (statusCode)
		{
		case FailedToNotifyGatewayToUpgrade:
		case FailedToGetUpdateInfo:
		case FailedToDownloadUpdates:
		case FailedToInstallUpdates:
			return Severity.Error;
			
		default:
			return Severity.Information;
		}
	}

	@Override
	public GatewayId getGatewayIdByUuid( String gatewayUuid )
		throws EdgeServiceFault
	{
		String logPrefix = this.getClass().getSimpleName() + ".getGatewayIdByUuid(): ";
		
		if ((gatewayUuid == null) || gatewayUuid.trim().isEmpty())
		{
			logger.error( logPrefix + "gatewayUuid is null or empty." );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_BadParameter_IsNull,
				"gatewayUuid is null." );
		}
		
		List<EdgeGatewayEntity> gatewayList = new ArrayList<>();
		this.gatewayDao.getGatewayByUuid( gatewayUuid, gatewayList );
		if (gatewayList.size() == 0)
			return null;
		
		EdgeGatewayEntity gateway = gatewayList.get( 0 );
		return new GatewayId( gateway.getId() );
	}
	
	private void setGatewayUpdateStatusWhenWebSvcStart( int gatewayRecordId )
	{
		String logPrefix = this.getClass().getSimpleName() + ".setGatewayUpdateStatusWhenWebSvcStart(): ";
		
		List<EdgeGatewayUpdateStatus> statusList = new ArrayList<>();
		this.gatewayDao.getGatewayUpdateStatus( gatewayRecordId, statusList );
		
		EdgeGatewayUpdateStatus status =
			(statusList.size() > 0) ? statusList.get( 0 ) : new EdgeGatewayUpdateStatus();
			
		if (status.getUpdateStatus() == GatewayUpdateStatusCode.UpdatedSuccessfully.getValue())
		{
			this.gatewayDao.setGatewayUpdateStatus(
				gatewayRecordId, GatewayUpdateStatusCode.NoNeedToUpdate.getValue(),
				"", null, 0, null );
			
			logger.info( logPrefix + "Set update status of gateway (id: " + gatewayRecordId + ") to " +
				GatewayUpdateStatusCode.NoNeedToUpdate );
		}
	}
	
	private boolean needToNotifyGatewayToUpdateWhenWebSvcStart( int gatewayRecordId )
	{
		String logPrefix = this.getClass().getSimpleName() + ".needToNotifyGatewayToUpdateWhenWebSvcStart(): ";
		
		List<EdgeGatewayUpdateStatus> statusList = new ArrayList<>();
		this.gatewayDao.getGatewayUpdateStatus( gatewayRecordId, statusList );
		
		EdgeGatewayUpdateStatus status =
			(statusList.size() > 0) ? statusList.get( 0 ) : new EdgeGatewayUpdateStatus();
			
		if ((status.getUpdateStatus() == GatewayUpdateStatusCode.NotifyingGatewayToUpgrade.getValue()) ||
			(status.getUpdateStatus() == GatewayUpdateStatusCode.GettingUpdateInfo.getValue()) ||
			(status.getUpdateStatus() == GatewayUpdateStatusCode.DownloadingUpdates.getValue()) ||
			(status.getUpdateStatus() == GatewayUpdateStatusCode.InstallingUpdates.getValue()))
		{
			logger.info( logPrefix + "The update status of gateway " + gatewayRecordId + " is " +
				status.getUpdateStatus() + ", don't notify it to update." );
			return false;
		}
		
		logger.info( logPrefix + "The update status of gateway " + gatewayRecordId + " is " +
			status.getUpdateStatus() + ", should notify it to do update." );
		return true;
	}
	
	// hasRebootFlag: True if the gateway need a reboot, false if it doesn't need
	private void setGatewayUpdateStatusWhenGatewayLogin( int gatewayRecordId, boolean hasRebootFlag )
	{
		String logPrefix = this.getClass().getSimpleName() + ".setGatewayUpdateStatusWhenGatewayLogin(): ";
		
		List<EdgeGatewayUpdateStatus> statusList = new ArrayList<>();
		this.gatewayDao.getGatewayUpdateStatus( gatewayRecordId, statusList );
		
		EdgeGatewayUpdateStatus status =
			(statusList.size() > 0) ? statusList.get( 0 ) : new EdgeGatewayUpdateStatus();
			
		String logMessage = String.format(
			"The old status for the gateway (ID: %d) is %d, hasRebootFlag is %s",
			gatewayRecordId, status.getUpdateStatus(), Boolean.toString( hasRebootFlag ) );
		logger.info( logPrefix + logMessage );
		
		boolean willReset = false;
		
		if (status.getUpdateStatus() == GatewayUpdateStatusCode.UpdatedSuccessfully.getValue())
			willReset = true;
		
		if ((status.getUpdateStatus() == GatewayUpdateStatusCode.UpdatedSuccessfullyNeedReboot.getValue()) && !hasRebootFlag)
			willReset = true;
		
		if (willReset)
		{
			GatewayUpdateStatusCode newStatus = GatewayUpdateStatusCode.NoNeedToUpdate;
			this.gatewayDao.setGatewayUpdateStatus( gatewayRecordId, newStatus.getValue(), "", null, 0, null );
			
			logger.info( logPrefix + "Set update status of gateway (id: " + gatewayRecordId + ") to " + newStatus );
		}
	}
	
	@Override
	public void checkGatewayVersions()
	{
		String logPrefix = this.getClass().getSimpleName() + ".checkGatewayVersions(): ";
		
		logger.info( logPrefix + "Begin to check gateway versions..." );
		
		try
		{
			List<GatewayId> gatewayIdList = new ArrayList<>();
			EdgeSimpleVersion consoleVersion = getConsoleVersion();
			
			List<GatewayEntity> gatewayList = this.getAllValidGateways();
			for (GatewayEntity gateway : gatewayList)
			{
				if (gateway.isLocal())
					continue;
				
				EdgeSimpleVersion hostVersion = null;
				try
				{
					if (gateway.getHostVersion() == null)
					{
						logger.warn( logPrefix + "Host version string is null. Gateway: " +
							gateway.getId().getRecordId() );
					}
					else // version string is not null
					{
						hostVersion = EdgeSimpleVersion.parseVersionString( gateway.getHostVersion() );
					}
				}
				catch (IllegalVersionFormatException e)
				{
					logger.error( logPrefix + "Invalid host version string. Gateway: " +
						gateway.getId().getRecordId() + ", Version string: '" + gateway.getHostVersion() + "'" );
				}
				if (hostVersion == null)
					hostVersion = new EdgeSimpleVersion();
				
				if (this.doesGatewayNeedToUpdate( consoleVersion, hostVersion ))
				{
					String message = String.format(
						"Version of gateway '%s' is %s while console version is %s. " +
						"The gateway need to be updated, will launch its upgradation later automatically.",
						gateway.getName(), hostVersion.toVersionString(), consoleVersion.toVersionString() );
					logger.info( logPrefix + message );
					
					if (needToNotifyGatewayToUpdateWhenWebSvcStart( gateway.getId().getRecordId() ))
						gatewayIdList.add( gateway.getId() );
				}
				else // no need to update
				{
					setGatewayUpdateStatusWhenWebSvcStart( gateway.getId().getRecordId() );
				}
			}
			
			this.doGatewayUpdate( gatewayIdList );
			
			logger.info( logPrefix + "Finished checking gateway versions." );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error checking gateway version.", e );
		}
	}
	
	static class SetupFile
	{
		public String SourceFileURL;
		public String DestFilePath;
		public String Checksum;
		public long Size;
	}
	
	@XmlRootElement(name = "UpdateInfo")
	static class SetupUpdateInfo
	{
		public String Version;
		@XmlElementWrapper( name = "UpdateFiles" )
		public List<SetupFile> File = new ArrayList<>();
	}
	// Sample XML:
	// -------------------------------------------------------------------------------------
	//	<?xml version="1.0" encoding="UTF-8"?>
	//	<UpdateInfo>
	//		<Product>Arcserve UDP Console Update</Product>
	//		<Code>1001</Code>
	//		<Version>6.0.0.3673.1.256</Version>
	//		<ID>Arcserve_Unified_Data_Protection_Gateway_Update_1</ID>
	//		<PublishedDate>11/02/2015</PublishedDate>
	//		<RebootRequired>No</RebootRequired>
	//		<LastRebootableUpdateVersion>0</LastRebootableUpdateVersion>
	//		<RequiredVersionOfAutoUpdate>0</RequiredVersionOfAutoUpdate>
	//		<Desc>
	//			<ENU>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</ENU>
	//			<JPN>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</JPN>
	//			<GRM>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</GRM>
	//			<ITA>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</ITA>
	//			<SPA>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</SPA>
	//			<FRN>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</FRN>
	//			<CHS>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</CHS>
	//			<CHT>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</CHT>
	//			<PRB>This Update includes several modifications and enhancements to improve the quality and performance of Arcserve UDP Console.</PRB>
	//		</Desc>
	//		<ReleaseNotes>
	//			<ENU>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1</ENU>
	//			<JPN>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1_ja</JPN>
	//			<GRM>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1_de </GRM>
	//			<ITA>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1_it</ITA>
	//			<SPA>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1_es</SPA>
	//			<FRN>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1_fr</FRN>
	//			<CHS>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1_zh</CHS>
	//			<CHT>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1_zh_TW</CHT>
	//			<PRB>http://www.arcservedocs.com/arcserveudp/serverupdates60.php?item=readme_v6u1_pt</PRB>
	//		</ReleaseNotes>
	//		<UpdateFiles>
	//			<File Flags="1">
	//				<SourceFileURL>Arcserve_Unified_Data_Protection_Gateway_Update_1.exe</SourceFileURL>
	//				<DestFilePath>Arcserve_Unified_Data_Protection_Gateway_Update_1.exe</DestFilePath>
	//				<Checksum>a4668c3a868f64981c2fca9ce29a445c</Checksum>
	//				<Size>12357024</Size>
	//			</File>
	//		</UpdateFiles>
	//		<PostDownloadActions/>
	//	</UpdateInfo>

	public SetupUpdateInfo loadSetupUpdateInfo( String filePath )
	{
		SetupUpdateInfo updateInfo = null;
		
		File settingFile = new File( filePath );
		if (!settingFile.exists())
			return null;
		
		try
		{
			updateInfo = JAXB.unmarshal( settingFile, SetupUpdateInfo.class );
		}
		catch (Exception e)
		{
			logger.error( "Error loading Setup's update info file. Path: " + filePath, e );
			return null;
		}
		
		return updateInfo;
	}
	
	private static WindowsRegistry windowsRegistry = new WindowsRegistry();
	private static Integer gatewayUpgradeTimeout = null;
	
	public static int getGatewayUpgradeTimeout()
	{
		if (gatewayUpgradeTimeout == null)
		{
			int handle = 0;
			boolean isKeyOpenned = false;
			int timeout = 0;
	
			try
			{
				handle = windowsRegistry.openKey( WindowsRegistry.KEY_NAME_ROOT_WEBSERVER );
				isKeyOpenned = true;
				
				timeout = windowsRegistry.getIntValue(
					handle, WindowsRegistry.VALUE_NAME_GATEWAY_UPGRADE_TIMEOUT, 0 );
			}
			catch (Exception e)
			{
				logger.error( "Error loading gateway upgrade timeout value from registry, default values will be used.", e );
			}
			finally
			{
				if (timeout == 0)
					timeout = DEFAULT_GATEWAY_UPGRADE_TIMEOUT;
				
				if (isKeyOpenned)
				{
					try { windowsRegistry.closeKey( handle ); } catch (Exception e) {}
				}
			}
			
			gatewayUpgradeTimeout = new Integer( timeout );
		}
		
		return gatewayUpgradeTimeout;
	}
	
	public static void main( String[] args )
	{
		try
		{
			SetupUpdateInfo updateInfo0 = new SetupUpdateInfo();
			updateInfo0.Version = "vvv";
			
			SetupFile setupFile;
			
			setupFile = new SetupFile();
			setupFile.SourceFileURL = "source.exe";
			updateInfo0.File.add( setupFile );
			
			JAXB.marshal( updateInfo0, "E:\\Temp\\Console_Update\\CM\\GATEWAY\\Update\\AvailableUpdateInfo0.xml" );
			
			EdgeGatewayBean obj = new EdgeGatewayBean();
			SetupUpdateInfo updateInfo = obj.loadSetupUpdateInfo( "E:\\Temp\\Console_Update\\CM\\GATEWAY\\Update\\AvailableUpdateInfo.xml" );
			System.out.println( "Done" );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
