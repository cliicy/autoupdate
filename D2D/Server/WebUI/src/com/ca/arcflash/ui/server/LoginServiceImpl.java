package com.ca.arcflash.ui.server;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ca.arcflash.common.Base64;
import com.ca.arcflash.common.BucketNameEncoder;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JMountRecoveryPointParams;
import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.rps.webservice.data.RpsArchiveConfiguationWrapper;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.DailyScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.MergeDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ThrottleModel;
import com.ca.arcflash.ui.client.common.SettingsTypesForUI;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.model.*;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.restore.RecoveryPointsPanel;
import com.ca.arcflash.ui.client.restore.RestoreConstants;
import com.ca.arcflash.ui.client.restore.ad.ADOptionModel;
import com.ca.arcflash.ui.server.servlet.ContextListener;
import com.ca.arcflash.ui.server.servlet.SessionConstants;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.IFlashServiceV2;
import com.ca.arcflash.webservice.IFlashService_Oolong1;
import com.ca.arcflash.webservice.IFlashService_R16_5;
import com.ca.arcflash.webservice.IFlashService_R16_U4;
import com.ca.arcflash.webservice.IFlashService_R16_U6;
import com.ca.arcflash.webservice.ServiceProviders;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.D2DOnRPS;
import com.ca.arcflash.webservice.data.D2DTime;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DateFormat;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.EdgeInfo;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.MergeDetailItem;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.RetentionSetting;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.ThrottleItem;
import com.ca.arcflash.webservice.data.VMwareServer;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.GeneralSettings;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.PM.ProxySettings;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcflash.webservice.data.ad.ADAttribute;
import com.ca.arcflash.webservice.data.ad.ADNode;
import com.ca.arcflash.webservice.data.ad.ADNodeFilter;
import com.ca.arcflash.webservice.data.ad.ADPagingConfig;
import com.ca.arcflash.webservice.data.ad.ADPagingResult;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationDetailsConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationVolumeConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDiskDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveFileItem;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceFiltersConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceInfoConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveVolItemAppComp;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.data.archive.RestoreJobArchiveNode;
import com.ca.arcflash.webservice.data.archive.RestoreJobArchiveVolumeNode;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.backup.ApplicationComponent;
import com.ca.arcflash.webservice.data.backup.ApplicationWriter;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.backup.D2DConfiguration;
import com.ca.arcflash.webservice.data.backup.SRMPkiAlertSetting;
import com.ca.arcflash.webservice.data.browse.File;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Folder;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.catalog.ArchiveCatalogItem;
import com.ca.arcflash.webservice.data.catalog.ArchiveFileVerionDetail;
import com.ca.arcflash.webservice.data.catalog.CatalogItem;
import com.ca.arcflash.webservice.data.catalog.CatalogJobPara;
import com.ca.arcflash.webservice.data.catalog.GRTBrowsingContext;
import com.ca.arcflash.webservice.data.catalog.GRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.MsgSearchRec;
import com.ca.arcflash.webservice.data.catalog.PagedCatalogItem;
import com.ca.arcflash.webservice.data.catalog.PagedExchangeDiscoveryItem;
import com.ca.arcflash.webservice.data.catalog.PagedGRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.catalog.SearchResult;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.login.LoginDetail;
import com.ca.arcflash.webservice.data.login.LoginRole;
import com.ca.arcflash.webservice.data.login.RolePrivilege;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.restore.AlternativePath;
import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.data.restore.CatalogInfo;
import com.ca.arcflash.webservice.data.restore.CatalogInfo_EDB;
import com.ca.arcflash.webservice.data.restore.CopyJob;
import com.ca.arcflash.webservice.data.restore.DestTypeContratct;
import com.ca.arcflash.webservice.data.restore.ExchangeDiscoveryItem;
import com.ca.arcflash.webservice.data.restore.FileSystemOption;
import com.ca.arcflash.webservice.data.restore.MountedRecoveryPointItem;
import com.ca.arcflash.webservice.data.restore.RecoverVMOption;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.data.restore.RestoreADOption;
import com.ca.arcflash.webservice.data.restore.RestoreDiskDataStore;
import com.ca.arcflash.webservice.data.restore.RestoreExchangeGRTOption;
import com.ca.arcflash.webservice.data.restore.RestoreExchangeOption;
import com.ca.arcflash.webservice.data.restore.RestoreJob;
import com.ca.arcflash.webservice.data.restore.RestoreJobADItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobExchSubItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobItemEntry;
import com.ca.arcflash.webservice.data.restore.RestoreJobNode;
import com.ca.arcflash.webservice.data.restore.RestoreSQLNewDest;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.BackupVMOriginalInfo;
import com.ca.arcflash.webservice.data.vsphere.DataStore;
import com.ca.arcflash.webservice.data.vsphere.Disk;
import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.HyperVHostStorage;
import com.ca.arcflash.webservice.data.vsphere.ResourcePool;
import com.ca.arcflash.webservice.data.vsphere.StandNetworkConfigInfo;
import com.ca.arcflash.webservice.data.vsphere.VAppChildBackupVMRestorePointWrapper;
import com.ca.arcflash.webservice.data.vsphere.VCloudDirector;
import com.ca.arcflash.webservice.data.vsphere.VCloudOrg;
import com.ca.arcflash.webservice.data.vsphere.VCloudVC;
import com.ca.arcflash.webservice.data.vsphere.VCloudVDCStorageProfile;
import com.ca.arcflash.webservice.data.vsphere.VCloudVirtualDC;
import com.ca.arcflash.webservice.data.vsphere.VDSInfo;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMItem;
import com.ca.arcflash.webservice.data.vsphere.VMNetworkConfig;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereProxy;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.data.vsphere.vDSPortGroup;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService.SettingsTypes;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoginServiceImpl extends BaseServiceImpl implements LoginService {

	private static final long serialVersionUID = 15808852420484067L;
	private static final Logger logger = Logger.getLogger(LoginServiceImpl.class);

	public static final String XML_ITEM = "item";
	public static final String XML_TITLE = "title";
	public static final String XML_LINK = "link";
	public static final String XML_DESCRIPTION = "description";

	private static final String STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME		=	"Volume";
	private static final String STRING_RECOVERYPOINT_ITEM_TYPE_APPLICATION	=	"Application";
//	private static final String METHOD_POST = "POST";    ///D2D Lite Integration
	
	private static final long BUCKET_LENGTH_ERROR = 10000;//if length is null or if length is not in between 3 and 63
	private static final long BUCKET_ERROR = 10001;//if contains special chars or capital letters
	private static final long BUCKET_SEQUENCE_ERROR = 10002;//if contains . or - adjacent
	private static final long BUCKET_IPFORMAT_ERROR = 10003;//if it is in IP address format
	private static final long BUCKET_FORMAT_SUCCESS = 0;//if successful
	private static final long BUCKET_EXCEPTION = 10005;//in case of exception
	private static final String UNDEFINED_OPERATION_NAME = "Undefined operation name";
	
    public static String cloudBucketD2DArchiveLabel = "d2dfilecopy-";	//r16
	public static String cloudBucketD2DF2CLabel = "d2dfc-v2-";	
	public static String cloudBucketD2DLabel = "d2d-filecopy-";  //r16.5
	public static String cloudBucketARCserveLabel = "arcserve-"; //r17
	
	public LoginServiceImpl() {
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.debug("init(ServletConfig) - start");

		super.init(config);

		logger.debug("init(ServletConfig) - end");
	}

	@Override
	public Boolean checkSession() {
		if ( this.getThreadLocalRequest().getSession(true).getAttribute(SessionConstants.SRING_USERNAME) == null
				&& this.getThreadLocalRequest().getSession(true).getAttribute(SessionConstants.SRING_UUID) == null)
			
			return Boolean.FALSE;
		return Boolean.TRUE;
	}

	@Override
	public String getLogonUser() {
		HttpSession session = getThreadLocalRequest().getSession(true);
		String domainName = (String)session.getAttribute(SessionConstants.SRING_DOMAIN);
		String userName = (String)session.getAttribute(SessionConstants.SRING_USERNAME);
		if(domainName != null && domainName.length() > 0 && userName != null)
			userName = domainName + "\\" + userName;
		LoginDetail detail = (LoginDetail) session.getAttribute(SessionConstants.SRING_LOGIN_DETAIL);
		if(detail!=null){
			if(detail.isInternalLogin())userName=detail.getUsername();
		}
		return userName;
	}

	@Override
	public Boolean validateUser(String protocol,String host, int port, String domain, String username, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("validateUser(String, String, String, String) - start");

		if (StringUtil.isEmptyOrNull(username))
			throw this.generateException(FlashServiceErrorCode.Login_UsernameRequired);

		if (StringUtil.isEmptyOrNull(password))
			throw this.generateException(FlashServiceErrorCode.Login_PasswordRequired);

		WebServiceClientProxy client = null;
		try
		{
			if(host.equals("localhost"))
				host = getLocalhostName();
			client = ServiceProviders.getLocalFlashServiceProvider().create(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_R16_5);
			//setServiceClient must follow getFlashServiceV2 method so that it can get correct session
			this.setServiceClient(client);
			client.getService().validateUser(username, password, domain);
			setLocalWebServiceClient(client);

		}catch(WebServiceException exception){
			logger.error("Error occurs during validate user...");
			logger.error(exception.getMessage());
			if (exception.getCause() instanceof Error && exception.getMessage().startsWith(UNDEFINED_OPERATION_NAME)) {
				throw generateException(FlashServiceErrorCode.EDGE_D2D_INTERFACE_MISMATCH);
			}
			proccessAxisFaultException(exception);
		}

		HostInfo hostInfo = new HostInfo();
		hostInfo.setName(host);
		hostInfo.setUsername(username);
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_DOMAIN, domain);
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_USERNAME, username);
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_PASSWORD, password);
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_UUID, "");

		logger.debug("validateUser(String, String, String, String) - end");

		return Boolean.TRUE;
	}

	public Boolean validateUser(HttpServletRequest req, String protocol,String host, int port, String domain, String username, String password,String logindetail) throws BusinessLogicException, ServiceConnectException, ServiceInternalException     ///D2D Lite Integration
        {
		logger.debug("validateUser(HttpServletRequest, String, String, int, String, String, String, String) - start");

		if (StringUtil.isEmptyOrNull(username))
			throw this.generateException(FlashServiceErrorCode.Login_UsernameRequired);

		if (StringUtil.isEmptyOrNull(password))
			throw this.generateException(FlashServiceErrorCode.Login_PasswordRequired);

		HttpSession session = req.getSession();
		WebServiceClientProxy client = null;
		LoginDetail detail=null;
		try
		{
			logger.debug(protocol + host + port);
			client = ServiceProviders.getLocalFlashServiceProvider().create(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_R16_5);
			//setServiceClient must follow getFlashServiceV2 method so that it can get correct session
			this.setServiceClient(req, client);
			detail=client.getService().validateUserWithDetail(username, password, domain,logindetail);			
			setLocalWebServiceClient(client);

		}catch(WebServiceException exception){
			logger.error("Error occurs during validate user...");
			logger.error(exception.getMessage());
			if (exception.getCause() instanceof Error && exception.getMessage().startsWith(UNDEFINED_OPERATION_NAME)) {
				throw generateException(FlashServiceErrorCode.EDGE_D2D_INTERFACE_MISMATCH);
			}
			proccessAxisFaultException(exception);
		}

		HostInfo hostInfo = new HostInfo();
		hostInfo.setName(host);
		hostInfo.setUsername(username);
		session.setAttribute(
				SessionConstants.SRING_DOMAIN, domain);
		session.setAttribute(
				SessionConstants.SRING_USERNAME, username);
		session.setAttribute(
				SessionConstants.SRING_PASSWORD, password);	
		session.setAttribute(SessionConstants.SRING_UUID, "");
		session.setAttribute(SessionConstants.SRING_LOGIN_DETAIL, detail);
		logger.debug("validateUser(HttpServletRequest, String, String, int, String, String, String, String) - end");

		return Boolean.TRUE;
	}

	@Override
	public Boolean validateUserByUuid(String uuid, String host, int port,
			String protocol) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("validateUserByUuid(String, String, int, String) - start");

		if (StringUtil.isEmptyOrNull(uuid))
			throw this.generateException(FlashServiceErrorCode.Login_UUIDRequired);

		WebServiceClientProxy client = null;
		try
		{
			client = ServiceProviders.getLocalFlashServiceProvider().create(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_R16_5);
			//setServiceClient must follow getFlashServiceV2 method so that it can get correct session
			this.setServiceClient(client);
			client.getService().validateUserByUUID(uuid);

			setLocalWebServiceClient(client);
		}catch(WebServiceException exception){
			logger.error("Error occurs during validate user by uuid...");
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception, false);
		}

		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_DOMAIN, "");
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_USERNAME, "");
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_UUID, uuid);
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_PASSWORD, "");

		logger.debug("validateUserByUuid(String, String, int, String) - end");

		return Boolean.TRUE;
	}

	public Boolean validateUserByUuid(HttpServletRequest req,String uuid, String host, int port,
			String protocol,String logindetail) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("validateUserByUuid(HttpServletRequest,String, String, int, String, String) - start");

		if (StringUtil.isEmptyOrNull(uuid))
			throw this.generateException(FlashServiceErrorCode.Login_UUIDRequired);
		HttpSession session = req.getSession();
		WebServiceClientProxy client = null;
		LoginDetail detail=null;
		try
		{
			client = ServiceProviders.getLocalFlashServiceProvider().create(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_R16_5);
			//setServiceClient must follow getFlashServiceV2 method so that it can get correct session
			this.setServiceClient(req,client);
			detail=client.getService().validateUserByUUIDWithDetail(uuid,logindetail);
			setLocalWebServiceClient(client);
		}catch(WebServiceException exception){
			logger.error("Error occurs during validate user by uuid...");
			logger.error(exception.getMessage());
			proccessAxisFaultException(req,exception);
		}
		
		session.setAttribute(SessionConstants.SRING_DOMAIN, "");
		session.setAttribute(SessionConstants.SRING_USERNAME, "");
		session.setAttribute(SessionConstants.SRING_PASSWORD, "");
		session.setAttribute(SessionConstants.SRING_UUID, uuid);
		session.setAttribute(SessionConstants.SRING_LOGIN_DETAIL, detail);
		logger.debug("validateUserByUuid(String, String, int, String, String) - end");

		return Boolean.TRUE;
	}

	
	@Override
	public void saveLocaleSession(String locale) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_LOCALE, locale);
	}

	@Override
	public BackupSettingsModel getBackupConfiguration() {
		logger.debug("getBackupConfiguration() - start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				BackupConfiguration bc = client.getService().getBackupConfiguration();

				if (bc != null)
				{
					BackupSettingsModel model = ConvertToModel(bc);
					return model;
				}
				return null;
			}
			else
			{
				logger.debug("getBackupConfiguration() - client was null");
			}
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		
		logger.debug("getBackupConfiguration() - end");
		return null;
	}

	private D2DTime covertToD2DTime(D2DTimeModel model) {
		if(model != null) {
			D2DTime time = new D2DTime();
			time.setYear(model.getYear());
			time.setMonth(model.getMonth());
			time.setDay(model.getDay());
			if (model.getHour()!=null)
				time.setHour(model.getHour());
			time.setMinute(model.getMinute());
			time.setAmPM(model.getAMPM());
			time.setHourOfday(model.getHourOfDay());
			return time;
		}
		return null;
	}
	
	private BackupRPSDestSetting convertToRpsSettings(BackupRPSDestSettingsModel model){
		if(model == null || model.rpsHost == null)
			return null;
		BackupRPSDestSetting backupRpsDestSetting = new BackupRPSDestSetting();
		RpsHost rpsHost = new RpsHost();
		rpsHost.setRhostname(model.rpsHost.getHostName());
		rpsHost.setUsername(model.rpsHost.getUserName());
		rpsHost.setPassword(model.rpsHost.getPassword());
		rpsHost.setHttpProtocol(model.rpsHost.getIsHttpProtocol());
		rpsHost.setPort(model.rpsHost.getPort());
		backupRpsDestSetting.setRpsHost(rpsHost );
		backupRpsDestSetting.setRPSPolicy(model.getRpsPolicy());
		backupRpsDestSetting.setRPSPolicyUUID(model.getRpsPolicyUUID());
		
		return backupRpsDestSetting;
	}
	
	private BackupConfiguration ConvertToConfiguration(BackupSettingsModel model)
	{
		BackupConfiguration bc = new BackupConfiguration();
		bc.setEmail(new BackupEmail());
		bc.setSharePointGRTSetting(model.getSharePointGRTSetting());
		bc.setPrePostPassword(model.getActionsPassword());
		bc.setPrePostUserName(model.getActionsUserName());
		bc.setBackupDataFormat(model.getBackupDataFormat());
		bc.setWindowsDeduplicationRate(model.getWindowsDeduplicationRate());

		if(model.isBackupToRps() != null && model.isBackupToRps() 
				&& model.rpsDestSettings != null && model.rpsDestSettings.rpsHost != null){
			bc.setD2dOrRPSDestType(false);
			bc.setBackupRpsDestSetting(convertToRpsSettings(model.rpsDestSettings) );
		}
		
		bc.setCommandAfterBackup(model.getCommandAfterBackup());
		bc.setRunCommandEvenFailed(model.getRunCommandEvenFailed());//lds
		bc.setCommandBeforeBackup(model.getCommandBeforeBackup());
		bc.setCommandAfterSnapshot(model.getCommandAfterSnapshot());
		bc.setDestination(model.getDestination());
		bc.setUserName(model.getDestUserName());
		bc.setPassword(model.getDestPassword());
		bc.setRetentionCount(model.getRetentionCount());
		bc.setCompressionLevel(model.getCompressionLevel());
		bc.setEnableEncryption(model.getEnableEncryption());
		bc.setEncryptionAlgorithm(model.getEncryptionAlgorithm());
		bc.setEncryptionKey(model.getEncryptionKey());
		bc.setChangedBackupDest(model.getChangedBackupDest());
		bc.setChangedBackupDestType(model.getChangedBackupDestType());
		bc.setPurgeSQLLogDays(model.getPurgeSQLLogDays());
		bc.setSoftwareOrHardwareSnapshotType(model.isSoftwareOrHardwareSnapshotType());
		bc.setFailoverToSoftwareSnapshot(model.isFailoverToSoftwareSnapshot());
		bc.setUseTrasportableSnapshot(model.isUseTransportableSnapshot());
		bc.setPurgeExchangeLogDays(model.getPurgeExchangeLogDays());
		bc.setExchangeGRTSetting(model.getExchangeGRTSetting());
		bc.setBackupStartTime(model.getBackupStartTime());
		bc.setAdminUserName(model.getAdminUserName());
		bc.setAdminPassword(model.getAdminPassword());
		bc.setEnableSpaceNotification(model.getEnableSpaceNotification() != null ? model.getEnableSpaceNotification() : false);
		bc.setThrottling(model.getThrottling());
		bc.setGenerateCatalog(model.getGenerateCatalog());
		bc.setCheckRecoveryPoint(model.getCheckRecoveryPoint() != null ? model.getCheckRecoveryPoint() : false);
		bc.setPreAllocationBackupSpace(model.getPreAllocationValue());
		bc.setStartTime(covertToD2DTime(model.startTime));
		if(model.isBackupToRps() == null || !model.isBackupToRps())
			bc.setRetentionPolicy(convertToRetentionPolicy(model.retentionPolicy));
		if(model.retentionPolicy != null) {
			bc.setRetentionCount(model.retentionPolicy.getRetentionCount());
		}
		
		if(model.getSpaceSavedAfterCompression() != null)
			bc.setSpaceSavedAfterCompression(model.getSpaceSavedAfterCompression());
		if(model.getGrowthRate() != null)
			bc.setGrowthRate(model.getGrowthRate());

		if (model.getEnableSpaceNotification()!=null && model.getEnableSpaceNotification() == true){
		      bc.setSpaceMeasureNum(model.getSpaceMeasureNum());
		      bc.setSpaceMeasureUnit(model.getSpaceMeasureUnit());
		}

		if (model.getEnablePreExitCode() != null && model.getEnablePreExitCode() == true)
		{
			bc.setEnablePreExitCode(true);
			if (model.getPreExitCode() != null)
			{
				bc.setPreExitCode(model.getPreExitCode());
			}
			if (model.getSkipJob() != null)
			{
				bc.setSkipJob(model.getSkipJob());
			}
			else
			{
				bc.setSkipJob(false);
			}
		}
		else
		{
			bc.setEnablePreExitCode(false);
		}
		return bc;
	}


	private RetentionPolicy convertToRetentionPolicy(RetentionPolicyModel model) {
		if(model != null) { 
			RetentionPolicy schedule = new RetentionPolicy();
			schedule.setUseBackupSet(model.isUseBackupSet());
			if(model.isUseBackupSet()){					
				schedule.setBackupSetCount(model.getBackupSetCount());
				schedule.setUseWeekly(model.isUseWeekly());
				if(model.isUseWeekly())
					schedule.setDayOfWeek(model.getDayOfWeek());
				else
					schedule.setDayOfMonth(model.getDayOfMonth());
				schedule.setStartWithFirst(model.isStartWithFirst());								
			}else {
				schedule.setUseTimeRange(model.isUseTimeRange());
				if(model.isUseTimeRange()) {
					schedule.setEndHour(model.getEndTimeHour());
					schedule.setEndMinutes(model.getEndTimeMinutes());
					schedule.setStartHour(model.getStartTimeHour());
					schedule.setStartMinutes(model.getStartTimeMinutes());					
				}				
			}															
			return schedule;
		}else {
			return null;
		}
	}
	
	private BackupSchedule ConvertToBackupSchedule(
			BackupScheduleModel model) {
		if(model == null) return null;
		BackupSchedule schedule = new BackupSchedule();

		schedule.setEnabled(model.isEnabled());
		if (model.isEnabled())
		{
			schedule.setInterval(model.getInterval());
			schedule.setIntervalUnit(model.getIntervalUnit());
		}

		return schedule;
	}

	@SuppressWarnings("unused")
	private BackupEmail ConvertToBackupEmail(BackupSettingsModel model) {
		BackupEmail email = new BackupEmail();


		Boolean enableEmailOnMissedJob = model.getEnableEmailOnMissedJob();
		Boolean enableEmail = model.getEnableEmail();
		Boolean enableEmailOnSuccess = model.getEnableEmailOnSuccess();
		Boolean eanbleEmailThreshold = model.getEnableSpaceNotification();
		Boolean enableNotifyOnNewUpdates = model.getEnableNotifyOnNewUpdates();
		Boolean enableEmailOnMergeFailure = model.getEnableEmailOnMergeFailure();
		Boolean enableEmailOnMergeSuccess = model.getEnableEmailOnMergeSuccess();
		Boolean enableEmailOnRecoveryPointCheckFailure = model.getEnableEmailOnRecoveryPointCheckFailure(); // lds
		
		if ((enableEmail != null && enableEmail == true)
				 || (enableEmailOnSuccess != null && enableEmailOnSuccess == true)
				|| (eanbleEmailThreshold != null && eanbleEmailThreshold == true)
				|| (enableEmailOnMissedJob != null && enableEmailOnMissedJob)
				|| (enableNotifyOnNewUpdates != null && enableNotifyOnNewUpdates)
				 || (enableEmailOnMergeFailure != null && enableEmailOnMergeFailure)
				 || (enableEmailOnMergeSuccess != null && enableEmailOnMergeSuccess)
				 || (enableEmailOnRecoveryPointCheckFailure != null && enableEmailOnRecoveryPointCheckFailure))   //lds
		{
			if (model.getContent() != null)
			{
				email.setContent(model.getContent());
			}
			email.setEnableEmail(enableEmail);
			email.setEnableEmailOnSuccess(enableEmailOnSuccess);

			if (enableEmailOnMergeFailure != null)
			{
				email.setEnableEmailOnMergeFailure(enableEmailOnMergeFailure);
			}
			
			if (enableEmailOnMergeSuccess != null)
			{
				email.setEnableEmailOnMergeSuccess(enableEmailOnMergeSuccess);
			}

			if((enableEmailOnMissedJob != null ))
			{
				email.setEnableEmailOnMissedJob(enableEmailOnMissedJob);
			}
			if((enableEmailOnRecoveryPointCheckFailure != null)){
				email.setEnableEmailOnRecoveryPointCheckFailure(enableEmailOnRecoveryPointCheckFailure);
			}

			if (model.isEnableProxy() != null)
			{
				email.setEnableProxy(model.isEnableProxy());
			}

			if (model.getEnableHTMLFormat() != null)
			{
				email.setEnableHTMLFormat(model.getEnableHTMLFormat());
			}

			if (model.getFromAddress() != null)
			{
				email.setFromAddress(model.getFromAddress());
			}

			if (model.getProxyAddress() != null)
			{
				email.setProxyAddress(model.getProxyAddress());
			}

			if (model.getProxyPassword() != null)
			{
				email.setProxyPassword(model.getProxyPassword());
			}

			if (model.getProxyPort() != null)
			{
				email.setProxyPort(model.getProxyPort());
			}

			if (model.getProxyUsername() != null)
			{
				email.setProxyUsername(model.getProxyUsername());
			}

			if (model.getSubject() != null)
			{
				email.setSubject(model.getSubject());
			}
			/** alert email PR */
			String protocol = this.getThreadLocalRequest().getProtocol();
			protocol = protocol.substring(0,protocol.indexOf("/"));
			protocol = protocol.toLowerCase();
			String host =  this.getThreadLocalRequest().getLocalAddr();
			String port = ""+this.getThreadLocalRequest().getLocalPort();
			email.setUrl(protocol+"://"+host+(port.isEmpty()?"":":"+port)+this.getThreadLocalRequest().getContextPath()+"/");

			if(model.getMailService()!=null)
			{
				email.setMailServiceName(model.getMailService());
			}

			if(model.getMailPwd()!=null)
			{
				email.setMailPassword(model.getMailPwd());
			}

			if(model.getSmtpPort()!=null)
			{
				email.setSmtpPort(model.getSmtpPort());

			}

			if(model.isEnableSsl()!=null)
			{
				email.setEnableSsl(model.isEnableSsl());
			}

			if(model.isEnableTls()!=null)
			{
				email.setEnableTls(model.isEnableTls());
			}

			if(model.getMailUser()!=null)
			{
				email.setMailUser(model.getMailUser());
			}

			if(model.isEnableMailAuth()!=null)
			{
				email.setMailAuth(model.isEnableMailAuth());
			}

			if(model.isEnableProxyAuth()!=null)
			{
				email.setProxyAuth(model.isEnableProxyAuth());
			}

			if (model.getSMTP() != null)
			{
				email.setSmtp(model.getSMTP());
			}

			if (model.Recipients != null)
			{
				String[] rec = new String[model.Recipients.size()];
				for (int i = 0; i < model.Recipients.size(); i++)
				{
					rec[i] = model.Recipients.get(i);
				}
				email.setRecipients(model.Recipients);
			}
			else
			{
				email.setRecipients(new ArrayList<String>(0));
			}

		}
		else
		{
			email.setEnableEmail(false);
			email.setEnableEmailOnSuccess(false);
			email.setEnableSrmPkiAlert(false);
			email.setEnableEmailOnMergeFailure(false);
			email.setEnableEmailOnMergeSuccess(false);
			email.setEnableEmailOnRecoveryPointCheckFailure(false);
		}

		return email;
	}

	public D2DTimeModel convertToD2DTimeMode(D2DTime time) {
		if(time == null)
			return null;
		D2DTimeModel model = new D2DTimeModel();
		model.setYear(time.getYear());
		model.setMonth(time.getMonth());
		model.setDay(time.getDay());
		model.setHour(time.getHour());
		model.setMinute(time.getMinute());
		model.setAMPM(time.getAmPM());
		model.setHourOfDay(time.getHourOfday());
		return model;
	}
	
	public BackupSettingsModel ConvertToModel(BackupConfiguration bc) {
		BackupSettingsModel model = new BackupSettingsModel();
		//Convert to Model Object and Return
		if(!bc.isD2dOrRPSDestType() 
				&& bc.getBackupRpsDestSetting() != null 
				&& bc.getBackupRpsDestSetting().getRpsHost() != null){
			BackupRPSDestSetting backupRpsDestSetting = bc.getBackupRpsDestSetting();	
			model.rpsDestSettings = convertToRpsSettingsModel(backupRpsDestSetting);
		}
	
		model.setGenerateCatalog(bc.isGenerateCatalog());
		model.setBackupToRps(!bc.isD2dOrRPSDestType());
		model.setSharePointGRTSetting(bc.getSharePointGRTSetting());
		model.setDestination(bc.getDestination());
		model.setDestUserName(bc.getUserName());
		model.setDestPassword(bc.getPassword());
		model.setCommandAfterBackup(bc.getCommandAfterBackup());
		model.setRunCommandEvenFailed(bc.isRunCommandEvenFailed());
		model.setCommandBeforeBackup(bc.getCommandBeforeBackup());
		model.setCommandAfterSnapshot(bc.getCommandAfterSnapshot());
		model.setChangedBackupDest(bc.isChangedBackupDest());
		model.setChangedBackupDestType(bc.getChangedBackupDestType());
		model.setBackupStartTime(bc.getBackupStartTime());

		model.setActionsUserName(bc.getPrePostUserName());
		model.setActionsPassword(bc.getPrePostPassword());

		model.setRetentionCount(bc.getRetentionCount());
		model.setCompressionLevel(bc.getCompressionLevel());
		model.setEnableEncryption(bc.isEnableEncryption());
		model.setEncryptionAlgorithm(bc.getEncryptionAlgorithm());
		model.setEncryptionKey(bc.getEncryptionKey());
		model.setPreExitCode(bc.getPreExitCode());
		model.setSkipJob(bc.isSkipJob());
		model.setEnablePreExitCode(bc.isEnablePreExitCode());

		
		model.setSoftwareOrHardwareSnapshotType(bc.isSoftwareOrHardwareSnapshotType());
		model.setFailoverToSoftwareSnapshot(bc.isFailoverToSoftwareSnapshot());
		model.setUseTransportableSnapshot(bc.isUseTrasportableSnapshot());
		
		model.setPurgeSQLLogDays(bc.getPurgeSQLLogDays());
		model.setPurgeExchangeLogDays(bc.getPurgeExchangeLogDays());
		model.setExchangeGRTSetting(bc.getExchangeGRTSetting());

		model.setAdminUserName(bc.getAdminUserName());
		model.setAdminPassword(bc.getAdminPassword());

		model.setEnableSpaceNotification(bc.isEnableSpaceNotification());
		model.setSpaceMeasureNum(bc.getSpaceMeasureNum());
		model.setSpaceMeasureUnit(bc.getSpaceMeasureUnit());
		model.setSpaceSavedAfterCompression(bc.getSpaceSavedAfterCompression());
		model.setGrowthRate(bc.getGrowthRate());
		model.setThrottling(bc.getThrottling());
		model.startTime = convertToD2DTimeMode(bc.getStartTime());
		if(bc.isD2dOrRPSDestType())
			model.retentionPolicy = convertToRetentionPolicyModel(bc.getRetentionPolicy());
		if(bc.getRetentionPolicy() == null) {
			model.retentionPolicy = new RetentionPolicyModel();
			model.retentionPolicy.setUseTimeRange(false);
			model.retentionPolicy.setUseBackupSet(false);
		}
		model.setMajorVersion(bc.getMajorVersion());
		model.setMinorVersion(bc.getMinorVersion());
		if(model.retentionPolicy != null)
			model.retentionPolicy.setRetentionCount(bc.getRetentionCount());
		
		if(bc.getPreAllocationBackupSpace() >= 0)
			model.setPreAllocationValue(bc.getPreAllocationBackupSpace());
		
		if (bc.getEmail() != null)
		{
			model.setContent(bc.getEmail().getContent());
			model.setEnableEmailOnMissedJob(bc.getEmail().isEnableEmailOnMissedJob());
			model.setEnableEmail(bc.getEmail().isEnableEmail());
			model.setEnableEmailOnSuccess(bc.getEmail().isEnableEmailOnSuccess());
			model.setEnableEmailOnMergeFailure(bc.getEmail().isEnableEmailOnMergeFailure());
			model.setEnableEmailOnMergeSuccess(bc.getEmail().isEnableEmailOnMergeSuccess());
			model.setEnableEmailOnRecoveryPointCheckFailure(bc.getEmail().isEnableEmailOnRecoveryPointCheckFailure());
			model.setEnableProxy(bc.getEmail().isEnableProxy());
			model.setFromAddress(bc.getEmail().getFromAddress());
			model.setProxyAddress(bc.getEmail().getProxyAddress());
			model.setProxyPassword(bc.getEmail().getProxyPassword());
			model.setProxyPort(bc.getEmail().getProxyPort());
			model.setProxyUsername(bc.getEmail().getProxyUsername());
			model.setSubject(bc.getEmail().getSubject());
			model.setSMTP(bc.getEmail().getSmtp());
			model.setEnableHTMLFormat(bc.getEmail().isEnableHTMLFormat());
			/** alert email PR */
			model.setMailPwd(bc.getEmail().getMailPassword());
			model.setMailService(bc.getEmail().getMailServiceName());
			model.setEnableSsl(bc.getEmail().isEnableSsl());
			model.setEnableTls(bc.getEmail().isEnableTls());
			model.setMailUser(bc.getEmail().getMailUser());
			model.setSmtpPort(bc.getEmail().getSmtpPort());
			model.setEnableMailAuth(bc.getEmail().isMailAuth());
			model.setEnableProxyAuth(bc.getEmail().isProxyAuth());

			model.Recipients = new ArrayList<String>();
			if (bc.getEmail().getRecipients() != null)
			{

					model.Recipients.addAll(bc.getEmail().getRecipients());

			}
		}

		if (bc.getIncrementalBackupSchedule() != null)
		{
			model.incrementalSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getIncrementalBackupSchedule());
		}
		if (bc.getFullBackupSchedule() != null)
		{
			model.fullSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getFullBackupSchedule());
		}
		if (bc.getResyncBackupSchedule() != null)
		{
			model.resyncSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getResyncBackupSchedule());
		}
		if(bc.getBackupVolumes() != null)
		{
			model.backupVolumes = ConvertToBackupSettingsVolumeModel(bc.getBackupVolumes());
		}
/*		if(bc.getUpdateSettings() != null)
		{
			model.autoUpdateSettings = ConvertToAutoUpdateSettingsModel(bc.getUpdateSettings());
		}*/

		//SRM Alert Setting
		//wanqi06
		model.advanceScheduleModel = ConvertDataToModel.convertToAdvanceScheduleModel(bc.getAdvanceSchedule());
		
		model.srmAlertSetting = ConvertToSRMPkiAlertSettingModel(bc.getSrmPkiAlertSetting());
		
		model.setBackupDataFormat(bc.getBackupDataFormat());
		
		model.setWindowsDeduplicationRate(bc.getWindowsDeduplicationRate());
		// zhazh06. retention policy
//		model.retentionModel = ConvertDataToModel.ConvertToRetentionModel(bc.getBackupRetention());

		model.setCheckRecoveryPoint(bc.isCheckRecoveryPoint());
		return model;
	}

	/*private UpdateSettingsModel ConvertToAutoUpdateSettingsModel(AutoUpdateSettings in_UpdateSettings)
	{
		UpdateSettingsModel autoUpdateSettingsModel = new UpdateSettingsModel();

		autoUpdateSettingsModel.setDownloadServerType(in_UpdateSettings.getServerType());

		int istagingServersCount = in_UpdateSettings.getStagingServers().length;
		StagingServerModel[] stagingServers = new StagingServerModel[istagingServersCount];
		StagingServerSettings[] servers = in_UpdateSettings.getStagingServers();
		for(int iIndex = 0;iIndex < istagingServersCount;iIndex++)
		{
			StagingServerModel stagingServer = new StagingServerModel();
			stagingServer.setStagingServer(servers[iIndex].getStagingServer());
			stagingServer.setStagingServerPort(servers[iIndex].getStagingServerPort());
			stagingServers[iIndex] = stagingServer;
		}
		autoUpdateSettingsModel.setStagingServers(stagingServers);

		autoUpdateSettingsModel.setAutoCheckupdate(in_UpdateSettings.getScheduleType());
		autoUpdateSettingsModel.setScheduledWeekDay(in_UpdateSettings.getScheduledWeekDay());
		autoUpdateSettingsModel.setScheduledHour(in_UpdateSettings.getScheduledHour());

		ProxySettingsModel proxySettingsModel = new ProxySettingsModel();
		ProxySettings proxyConfig = in_UpdateSettings.getproxySettings();

		proxySettingsModel.setUseProxy(proxyConfig.isUseProxy());
		proxySettingsModel.setProxyServerName(proxyConfig.getProxyServerName());
		proxySettingsModel.setProxyPort(proxyConfig.getProxyServerPort());
		proxySettingsModel.setProxyRequiresAuth(proxyConfig.isProxyRequiresAuth());
		proxySettingsModel.setProxyUserName(proxyConfig.getProxyUserName());
		proxySettingsModel.setProxyPassword(proxyConfig.getProxyPassword());

		autoUpdateSettingsModel.setproxySettings(proxySettingsModel);

		return autoUpdateSettingsModel;
	}*/
	
	private BackupRPSDestSettingsModel convertToRpsSettingsModel(BackupRPSDestSetting backupRpsDestSetting){
		if(backupRpsDestSetting == null)
			return null;
		BackupRPSDestSettingsModel rpsDestSettings = new BackupRPSDestSettingsModel();
		rpsDestSettings.rpsHost = new RpsHostModel();
		rpsDestSettings.rpsHost.setHostName(backupRpsDestSetting.getRpsHost().getRhostname());
		rpsDestSettings.rpsHost.setUserName(backupRpsDestSetting.getRpsHost().getUsername());
		rpsDestSettings.rpsHost.setPassword(backupRpsDestSetting.getRpsHost().getPassword());
		rpsDestSettings.rpsHost.setPort(backupRpsDestSetting.getRpsHost().getPort());
		rpsDestSettings.rpsHost.setIsHttpProtocol(backupRpsDestSetting.getRpsHost().isHttpProtocol());
		rpsDestSettings.setRpsPolicy(backupRpsDestSetting.getRPSPolicy());
		rpsDestSettings.setRpsPolicyUUID(backupRpsDestSetting.getRPSPolicyUUID());
		rpsDestSettings.setRPSDataStoreName(backupRpsDestSetting.getRPSDataStoreDisplayName());
		rpsDestSettings.setRPSDataStoreUUID(backupRpsDestSetting.getRPSDataStore());
		return rpsDestSettings;
	}

	private RetentionPolicyModel convertToRetentionPolicyModel(RetentionPolicy schedule) {
		if(schedule == null)
			return null;
		RetentionPolicyModel model = new RetentionPolicyModel();
		model.setUseBackupSet(schedule.isUseBackupSet());		
		if(schedule.isUseBackupSet()) {				
			model.setBackupSetCount(schedule.getBackupSetCount());
			model.setUseWeekly(schedule.isUseWeekly());
			if(schedule.isUseWeekly())
				model.setDayOfWeek(schedule.getDayOfWeek());
			else
				model.setDayOfMonth(schedule.getDayOfMonth());
			model.setStartWithFirst(schedule.isStartWithFirst());
														
		}else {
			model.setUseTimeRange(schedule.isUseTimeRange());
			if(schedule.isUseTimeRange()) {
				model.setEndTimeHour(schedule.getEndHour());
				model.setEndTimeMinutes(schedule.getEndMinutes());
				model.setStartTimeHour(schedule.getStartHour());
				model.setStartTimeMinutes(schedule.getStartMinutes());
			}
		}										
		return model;
	}
	
	private BackupVolumeModel ConvertToBackupSettingsVolumeModel(
			BackupVolumes backupVolumes) {
		BackupVolumeModel model = null;
		
		if(backupVolumes != null)
		{
			model =new BackupVolumeModel();
			model.setIsFullMachine(backupVolumes.isFullMachine());
			
			//String[] volumes = backupVolumes.getVolumes();
			/*if(volumes != null && volumes.length > 0) {
				model.selectedVolumesList = java.util.Arrays.asList(volumes);
			}*/
			if(backupVolumes.getVolumes().size() != 0)
			{
				model.selectedVolumesList = backupVolumes.getVolumes();
			}
			
		}
		return model;
	}

	private BackupScheduleModel ConvertToBackupSettingsScheduleModel(
			BackupSchedule schedule) {
		BackupScheduleModel model = new BackupScheduleModel();

		model.setEnabled(schedule.isEnabled());
		if (schedule.isEnabled())
		{
			model.setInterval(schedule.getInterval());
			model.setIntervalUnit(schedule.getIntervalUnit());
		}

		return model;
	}

	private SRMAlertSettingModel ConvertToSRMPkiAlertSettingModel(
			SRMPkiAlertSetting alertSetting) {

		if ( alertSetting == null )
			return null;

		SRMAlertSettingModel model = new SRMAlertSettingModel();
		model.setValidsrm(alertSetting.isValidsrm());
		model.setUseglobalpolicy(alertSetting.isUseglobalpolicy());
		model.setValidpkiutl(alertSetting.isValidpkiutl());
		model.setValidalert(alertSetting.isValidalert());

		model.setCpuinterval(alertSetting.getCpuinterval());
		model.setCpumaxalertnum(alertSetting.getCpumaxalertnum());
		model.setCpusampleamount(alertSetting.getCpusampleamount());
		model.setCputhreshold(alertSetting.getCputhreshold());

		model.setMemoryinterval(alertSetting.getMemoryinterval());
		model.setMemorymaxalertnum(alertSetting.getMemorymaxalertnum());
		model.setMemorysampleamount(alertSetting.getMemorysampleamount());
		model.setMemorythreshold(alertSetting.getMemorythreshold());

		model.setDiskinterval(alertSetting.getDiskinterval());
		model.setDiskmaxalertnum(alertSetting.getDiskmaxalertnum());
		model.setDisksampleamount(alertSetting.getDisksampleamount());
		model.setDiskthreshold(alertSetting.getDiskthreshold());

		model.setNetworkinterval(alertSetting.getNetworkinterval());
		model.setNetworkmaxalertnum(alertSetting.getNetworkmaxalertnum());
		model.setNetworksampleamount(alertSetting.getNetworksampleamount());
		model.setNetworkthreshold(alertSetting.getNetworkthreshold());

		model.setUpdatetime( System.currentTimeMillis() );

		return model;
	}

	@Override
	public long saveBackupConfiguration(BackupSettingsModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		
		return checkBackupConfiguration(model, true);
	}
	
	@Override
	public long validateBackupConfiguration(BackupSettingsModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		return checkBackupConfiguration(model, false);
	}
	
	@Override
	public long validateRpsDestSettings(BackupSettingsModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;

		client = this.getServiceClient();
		if (client != null) {
			BackupConfiguration configuration = ConvertToConfiguration(model);
			try {
				client.getFlashServiceR16_5().validateRpsDestSetting(configuration);
				return 0;
			}catch(WebServiceException e){
				logger.error(e.getMessage());
				proccessAxisFaultException(e);
			}
		}
		return 0;
	}

	private long checkBackupConfiguration(BackupSettingsModel model, boolean isSave)
	throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException {
		long ret = -1;
		logger.debug("checkBackupConfiguration() - start");
		WebServiceClientProxy client = null;

			client = this.getServiceClient();
		if (client != null) {

			BackupConfiguration configuration = ConvertToConfiguration(model);

			BackupSchedule fSched = ConvertToBackupSchedule(model.fullSchedule);
			configuration.setFullBackupSchedule(fSched);

			BackupSchedule iSched = ConvertToBackupSchedule(model.incrementalSchedule);
			configuration.setIncrementalBackupSchedule(iSched);

			BackupSchedule rSced = ConvertToBackupSchedule(model.resyncSchedule);
			configuration.setResyncBackupSchedule(rSced);

			//BackupEmail email = ConvertToBackupEmail(model);
			//configuration.setEmail(email);

			//wanqi06
			AdvanceSchedule advanceSchedule = convertToAdvanceSchedule(model.advanceScheduleModel);
			configuration.setAdvanceSchedule(advanceSchedule);
			
			BackupVolumes volumes = ConvertToBackupVolume(model.backupVolumes);
			configuration.setBackupVolumes(volumes);

/*			AutoUpdateSettings autoUpdateSettings = ConvertToSelfUpdateSettings(model.getautoUpdateSettings());
			configuration.setUpdateSettings(autoUpdateSettings);*/

			SRMPkiAlertSetting alertSetting = ConvertToSRMPkiAlertSetting(model.srmAlertSetting);
			configuration.setSrmPkiAlertSetting(alertSetting);
			
			try {
				if(isSave) {
					ret = client.getServiceV2().saveBackupConfiguration(configuration);
				}
				else {
					ret = client.getServiceV2().validateBackupConfiguration(configuration);
				}
				
			} catch (WebServiceException e) {
				logger
						.error("Error occurs during checkBackupConfiguration()...");
				logger.error(e.getMessage());
				proccessAxisFaultException(e);

			}

			if (ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE) - FlashServiceErrorCode.BackupConfig_BASE)) {
				throw this
						.generateException(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE);
			}
			else if(ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING) - FlashServiceErrorCode.BackupConfig_BASE))
				throw generateException(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING);
		} else {
			logger.debug("checkBackupConfiguration() - client was null");
		}

		logger.debug("checkBackupConfiguration() - end");
		return ret;
	}
	
	@Override
	public ScheduledExportSettingsModel getScheduledExportConfiguration() {
		logger.debug("getScheduledExportConfiguration() - start");
		WebServiceClientProxy client = this.getServiceClient();
		if(client == null)
			return null;
		
		try
		{
			ScheduledExportConfiguration configuration = client.getServiceV2().getScheduledExportConfiguration();
			if (configuration != null)
			{
				ScheduledExportSettingsModel model = convertToModel(configuration);
				return model;
			}
			return null;
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		
		logger.debug("getScheduledExportConfiguration() - end");
		return null;
	}
	
	/**
	 * convert ScheduledExportConfiguration to ScheduledExportSettingsModel
	 * 
	 * @param config
	 * @return
	 */
	public ScheduledExportSettingsModel  convertToModel(ScheduledExportConfiguration config) {
		ScheduledExportSettingsModel model = null;
		
		if(config != null) {
			model = new ScheduledExportSettingsModel();
			model.setDestination(config.getDestination());
			model.setDestUserName(config.getDestUserName());
			model.setDestPassword(config.getDestPassword());
			model.setExportInterval(config.getExportInterval());
			model.setKeepRecoveryPoints(config.getKeepRecoveryPoints());
			model.setEncryptionAlgorithm(config.getEncryptionAlgorithm());
			model.setEncryptionKey(config.getEncryptionKey());
			model.setEnableScheduledExport(config.isEnableScheduledExport());
			model.setCompressionLevel(config.getCompressionLevel());
		}
		
		return model;
	}
	
	@Override
	public long saveScheduledExportConfiguration(
			ScheduledExportSettingsModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("saveScheduledExportConfiguration() - start");
		WebServiceClientProxy client = this.getServiceClient();
		if(client == null)
			return 0;
		
		try
		{
			ScheduledExportConfiguration configuration = convertToConfiguration(model);
			return client.getServiceV2().saveScheduledExportConfiguration(configuration);
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		
		logger.debug("saveScheduledExportConfiguration() - end");
		return 0;

	}
	
	private ScheduledExportConfiguration convertToConfiguration(ScheduledExportSettingsModel model) {
		ScheduledExportConfiguration config = null;
		if(model!=null) {
			config = new ScheduledExportConfiguration();
			
			config.setDestination(model.getDestination());
			config.setDestUserName(model.getDestUserName());
			config.setDestPassword(model.getDestPassword());
			config.setEncryptionAlgorithm(model.getEncryptionAlgorithm());
			config.setEncryptionKey(model.getEncryptionKey());
			config.setEnableScheduledExport(model.getEnableScheduledExport());
			config.setExportInterval(model.getExportInterval());
			config.setKeepRecoveryPoints(model.getKeepRecoveryPoints());
			config.setCompressionLevel(model.getCompressionLevel());
		}
		
		return config;
	}
	
	@Override
	public long validateScheduledExportConfiguration(
			ScheduledExportSettingsModel model) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		long ret = -1;
		logger.debug("validateScheduledExportConfiguration() - start");
		
		ret = checkScheduledExportConfiguration(model, false);
		
		logger.debug("validateScheduledExportConfiguration() - end");
		
		return ret;
	}
	
	private long checkScheduledExportConfiguration(
			ScheduledExportSettingsModel model, boolean isSave)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		long ret = -1;

		WebServiceClientProxy client = null;
		client = getServiceClient();
		if (client != null) {
			ScheduledExportConfiguration configuration = convertToConfiguration(model);
			try {
				if (isSave) {
					ret = client.getServiceV2().saveScheduledExportConfiguration(configuration);
				} else {
					ret = client.getServiceV2().validateScheduledExportConfiguration(configuration);
				}
			} catch (WebServiceException exception) {
					proccessAxisFaultException(exception);
			}

		}
		return ret;
	}
		
	private BackupVolumes ConvertToBackupVolume(BackupVolumeModel volumesModel) {
		BackupVolumes volumes = new BackupVolumes();
		if(volumesModel != null) {
			if(volumesModel.getIsFullMachine() == null) {
				if(volumesModel.selectedVolumesList == null || volumesModel.selectedVolumesList.size() == 0)
					volumes.setFullMachine(true);
				else
					volumes.setFullMachine(false);
			}
			else
				volumes.setFullMachine(volumesModel.getIsFullMachine());

			if(volumesModel.selectedVolumesList != null && volumesModel.selectedVolumesList.size() > 0)
				volumes.setVolumes(volumesModel.selectedVolumesList);

		}

		return volumes;
	}

/*	private AutoUpdateSettings ConvertToSelfUpdateSettings(UpdateSettingsModel autoUpdateSettingsModel)
	{
		if(autoUpdateSettingsModel == null)
			return null;

		AutoUpdateSettings autoUpdateSettingsConfig = new AutoUpdateSettings();

		autoUpdateSettingsConfig.setServerType(autoUpdateSettingsModel.getDownloadServerType());
		if(autoUpdateSettingsModel.getDownloadServerType() == 1)
		{
			StagingServerModel[] servers = autoUpdateSettingsModel.getStagingServers();
			int iserversCnt = servers.length;
			StagingServerSettings[] stagingServers = new StagingServerSettings[iserversCnt];
			for(int iIndex = 0;iIndex<iserversCnt;iIndex++)
			{
				StagingServerSettings StagingServer = new StagingServerSettings();
				StagingServer.setStagingServer(servers[iIndex].getStagingServer());
				StagingServer.setStagingServerPort(servers[iIndex].getStagingServerPort());

				stagingServers[iIndex] = StagingServer;
			}
		}

		autoUpdateSettingsConfig.setScheduleType(autoUpdateSettingsModel.getAutoCheckupdate());
		if(autoUpdateSettingsModel.getAutoCheckupdate())
		{
			autoUpdateSettingsConfig.setScheduledWeekDay(autoUpdateSettingsModel.getScheduledWeekDay() != null ? autoUpdateSettingsModel.getScheduledWeekDay() : -1);

			int iScheduledHour = autoUpdateSettingsModel.getScheduledHour() != null ? autoUpdateSettingsModel.getScheduledHour() : -1;
			autoUpdateSettingsConfig.setScheduledHour(iScheduledHour);
		}

		ProxySettings proxyConfig = new ProxySettings();
		ProxySettingsModel proxySettingsModel = autoUpdateSettingsModel.getproxySettings();
		proxyConfig.setUseProxy(proxySettingsModel.getUseProxy());
		if(proxySettingsModel.getUseProxy())
		{
			proxyConfig.setProxyServerName(proxySettingsModel.getProxyServerName());
			proxyConfig.setProxyServerPort(proxySettingsModel.getProxyPort());

			proxyConfig.setProxyRequiresAuth(proxySettingsModel.getProxyRequiresAuth());
			if(proxySettingsModel.getProxyRequiresAuth())
			{
				proxyConfig.setProxyUserName(proxySettingsModel.getProxyUserName());
				proxyConfig.setProxyPassword(proxySettingsModel.getProxyPassword());
			}
		}
		autoUpdateSettingsConfig.setproxySettings(proxyConfig);

		return autoUpdateSettingsConfig;
	}*/

	private SRMPkiAlertSetting ConvertToSRMPkiAlertSetting(
			SRMAlertSettingModel model) {

		if ( model == null )
			return null;

		SRMPkiAlertSetting setting = new SRMPkiAlertSetting();
		setting.setValidsrm(model.isValidsrm());
		setting.setUseglobalpolicy(model.isUseglobalpolicy());
		setting.setValidpkiutl(model.isValidpkiutl());
		setting.setValidalert(model.isValidalert());

		setting.setCpuinterval(model.getCpuinterval());
		setting.setCpumaxalertnum(model.getCpumaxalertnum());
		setting.setCpusampleamount(model.getCpusampleamount());
		setting.setCputhreshold(model.getCputhreshold());

		setting.setMemoryinterval(model.getMemoryinterval());
		setting.setMemorymaxalertnum(model.getMemorymaxalertnum());
		setting.setMemorysampleamount(model.getMemorysampleamount());
		setting.setMemorythreshold(model.getMemorythreshold());

		setting.setDiskinterval(model.getDiskinterval());
		setting.setDiskmaxalertnum(model.getDiskmaxalertnum());
		setting.setDisksampleamount(model.getDisksampleamount());
		setting.setDiskthreshold(model.getDiskthreshold());

		setting.setNetworkinterval(model.getNetworkinterval());
		setting.setNetworkmaxalertnum(model.getNetworkmaxalertnum());
		setting.setNetworksampleamount(model.getNetworksampleamount());
		setting.setNetworkthreshold(model.getNetworkthreshold());

		setting.setUpdatetime( System.currentTimeMillis() / 1000 );

		return setting;
	}
	
	// zhazh06 . retention policy
	private com.ca.arcflash.webservice.data.DayTime ConvertToDayTime(com.ca.arcflash.ui.client.model.DayTimeModel dailyBackupTime) {
		com.ca.arcflash.webservice.data.DayTime time = new com.ca.arcflash.webservice.data.DayTime();
		time.setHour(dailyBackupTime.getHour());
		time.setMinute(dailyBackupTime.getMinutes());
		
		return time;
	}
	
	// zhazh06. retention policy
	private RetentionSetting convertToRetentionSetting(RetentionModel model) {
		RetentionSetting setting = new RetentionSetting();
		if (model.getMonthlyBackupTime() != null)
			setting.setMonthlyBackup(model.getMonthlyBackupTime());

		setting.setWeeklyBackup(model.getWeekBackupTime());

		setting.setDailyUseLastBackup(model.isDailyUseLastBackup());
		if (model.dailyBackupTime != null)
			setting.setDailyBackupTime(ConvertToDayTime(model.dailyBackupTime));

		setting.setMonthlyUseLastBackup(model.isMonthlyUseLastBackup());
		
		return setting;
	}


//	public RecoveryPointModel[] getRecoveryPoints(String destination, String domain, String username, String password,
//			Date beginDate, Date endDate)
//	{
//		logger.debug("getRecoveryPoints() start");
//		WebServiceClient client = null;
//		try
//		{
//			client = this.getServiceClient();
//			if (client != null)
//			{
//				RecoveryPoint[] points = client.getRecoveryPoints(destination, domain, username, password,
//						beginDate, endDate);
//
//				RecoveryPointModel[] t = new RecoveryPointModel[points.length];
//				for (int i = 0; i < points.length; i++)
//				{
//					t[i] = ConvertToModel(points[i]);
//				}
//				logger.debug("getRecoveryPoints() t.length=" + t.length);
//				return t;
//			}
//		}
//		catch (Exception e)
//		{
//			logger.debug(e.getMessage());
//		}
//		logger.debug("getRecoveryPoints() end");
//		return null;
//	}

	private RecoveryPointModel ConvertToModel(RecoveryPoint recoveryPoint) {
		RecoveryPointModel model = new RecoveryPointModel();
		model.setBackupStatus(recoveryPoint.getBackupStatus());
		model.setBackupType(recoveryPoint.getBackupType());
		model.setDataSize(recoveryPoint.getDataSize());
		model.setSessionID(new Long(recoveryPoint.getSessionID()).intValue());
		model.setTime(recoveryPoint.getTime());
		model.setD2DTime(getD2DTime(recoveryPoint.getTime()));
		model.setName(recoveryPoint.getName());
		model.setTimeZoneOffset(recoveryPoint.getTimeZoneOffset());
		model.setPath(recoveryPoint.getPath());
		model.setEncryptionType(recoveryPoint.getEncryptType());
		model.setEncryptPwdHashKey(recoveryPoint.getEncryptPasswordHash());
		model.setAllowNestedValues(true);
		model.setSessionGuid(recoveryPoint.getSessionGuid());
		model.setSessionVersion(recoveryPoint.getSessionVersion());
		model.setFSCatalogStatus(recoveryPoint.getFsCatalogStatus());
		model.setBackupSetFlag(recoveryPoint.getBackupSetFlag());
		model.setCanCatalog(recoveryPoint.isCanCatalog());
		model.setCanMount(recoveryPoint.isCanMount());
		model.setPeriodRetentionFlag(recoveryPoint.getPeriodRetentionFlag());
		model.setDefaultSessPwd(recoveryPoint.isDefaultSessPwd());
		model.setVMHypervisor(recoveryPoint.getVmHypervisor());
		model.setAgentBackupType(recoveryPoint.getAgentBackupType());
		//<huvfe01>added for vm recovery
		model.setVMName(recoveryPoint.getVmName());
		model.setvCenter(recoveryPoint.getVmvCenter());
		model.setESXHost(recoveryPoint.getVmEsxHost());
		
		RecoveryPointItem[] items = recoveryPoint.getItemsArray();
		if (items!=null){
			Arrays.sort(items, new Comparator<RecoveryPointItem>(){

				@Override
				public int compare(RecoveryPointItem arg0, RecoveryPointItem arg1) {
					if(!arg0.getVolumeOrAppType().equals(arg1.getVolumeOrAppType())){
						if (STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME.equals(arg0.getVolumeOrAppType()))
							return -1;
						if (STRING_RECOVERYPOINT_ITEM_TYPE_APPLICATION.equals(arg0.getVolumeOrAppType()))
							return 1;
					}

					return arg0.getDisplayName().compareTo(arg1.getDisplayName());
				}

			});
		}

		model.listOfRecoveryPointItems = new ArrayList<RecoveryPointItemModel>();
		if (items != null){
			for (int i = 0; i < items.length; i++)
			{
				model.listOfRecoveryPointItems.add(ConvertToModel(items[i]));
			}
		}
		
		if(model.listOfRecoveryPointItems == null || model.listOfRecoveryPointItems.size()==0){
			model.setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
		}

		return model;
	}

	private RecoveryPointItemModel ConvertToModel(
			RecoveryPointItem item) {
		RecoveryPointItemModel model = new RecoveryPointItemModel();
		model.setCatalogFilePath(item.getCatalogFilePath());
		model.setDisplayName(item.getDisplayName());
		model.setGuid(item.getGuid());
		model.setSubSessionID(item.getSubSessionID());
		model.setType(item.getVolumeOrAppType());
		model.setVolDataSizeB(item.getVolDataSizeB());
		model.setChildrenCount(item.getChildrenCount());
		model.setVolAttr(item.getVolAttr());
		if(STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME.equals(item.getVolumeOrAppType())){
			if(item.getDisplayName()!=null){
				if(!item.getDisplayName().contains(":")){
					model.setHasDriverLetter(false);
				}else{
					if(item.getDisplayName().split(":").length > 1){
						model.setHasDriverLetter(false);
					}else{
						model.setHasDriverLetter(true);
					}
				}
			}
		}
		model.setHasReplicaDB(item.isHasReplicaDB());
		return model;
	}

	public CatalogItemModel[] getCatalogItems(String catalogFilePath, long parentID)
	{
		logger.debug("getRecoveryPoints() start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				CatalogItem[] items = client.getService().getCatalogItems(catalogFilePath, parentID);
				CatalogItemModel[] model = new CatalogItemModel[items.length];
				for (int i = 0; i < items.length;i++)
				{
					model[i] = ConvertToModel(items[i]);

				}
				return model;
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());

		}
		return null;
	}

	private CatalogItemModel ConvertToModel(CatalogItem item) {
		CatalogItemModel m = new CatalogItemModel();
		
		if(item.getComponentName() != null && item.getName() != null
				&& !item.getComponentName().equals(item.getName())
				&& item.getPath() == null) {
			//for on demand catalog
			int index = item.getComponentName().lastIndexOf(item.getName());
			if(index != -1) {
				String path = item.getComponentName().substring(0, index -1);
				item.setPath(path);
				item.setComponentName(item.getName());
			}
		}
		m.setSessionNumber(item.getSessionNumber());
		m.setSubSessionNumber(item.getSubSessionNumber());
		m.setDate(item.getDate());
		m.setServerTimeZoneOffset(item.getServerTimeZoneOffset());
		m.setId(item.getId());
		m.setName(item.getName());
		m.setPath(item.getPath());
		m.setSize(item.getSize());
		m.setType(item.getType());
		m.setComponentName(item.getComponentName());
		m.setFullPath(item.getPath() + "\\" + item.getName());
		m.setChildrenCount(item.getChildrenCount());
		m.setFullSessionNumber(item.getFullSessNum());
		m.setEncrypted(!StringUtil.isEmptyOrNull(item.getPwdHash())&& !item.isDefaultSessPwd());
		m.setBackupDestination(item.getBackupDest());
		m.setBackupDate(item.getBackupTime());
		m.setBKServerTimeZoneOffset(item.getBkServerTimeZoneOffset());
		m.setBackupJobName(item.getJobName());
		m.setPasswordHash(item.getPwdHash());
		m.setSessionGuid(item.getSessionGuid());
		m.setFullSessionGuid(item.getFullSessionGuid());
		m.setVolAttr(item.getVolAttr());
		m.setDriverLetterAttr(item.getDriverLeterAttr());
		m.setDefaultSessPwd(item.isDefaultSessPwd());
		return m;
	}

	private List<GridTreeNode> getGRTTreeGridChildren(GridTreeNode loadConfig) {

		ArrayList<GridTreeNode> l = new ArrayList<GridTreeNode>();

		if (loadConfig != null) {
			Integer type = loadConfig.getType();
			
			// if this is GRT root node, get the grt catalog path			
			if (CatalogModelType.rootGRTExchangeTypes.contains(type) || type ==CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE) {
				
				String dbIdentify = loadConfig.getPath() + "\\"	+ loadConfig.getComponentName();
				dbIdentify = dbIdentify.substring(dbIdentify.indexOf('\\') + 1);
				
				String backupDestination = loadConfig.getBackupDestination();
				long sessionID = loadConfig.getSessionID();
				long subSessionID = loadConfig.getSubSessionID();

				String msgCatalogPath = this.getMsgCatalogPath(dbIdentify,	backupDestination, sessionID, subSessionID);

				if (msgCatalogPath == null || msgCatalogPath.isEmpty()) {
					return l;
				} else {
					loadConfig.setGrtCatalogFile(msgCatalogPath);
				}
			}
			
			// browse GRT node
			if (CatalogModelType.rootGRTExchangeTypes.contains(type) || CatalogModelType.allGRTExchangeTypes.contains(type) || CatalogModelType.allGRTSPTypes.contains(type) || type==CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE) {
				
				String msgCatalogPath = loadConfig.getGrtCatalogFile();
				if (!msgCatalogPath.isEmpty()) {
					
					long lowSelfid = 1;  // 1 for the root GRT item
					long highSelfid = 0; // 0 for the root GRT item

					GRTCatalogItemModel currentModel = loadConfig.getGrtCatalogItemModel();
					if (currentModel != null) {
						lowSelfid = currentModel.getLowObjSelfid();
						highSelfid = currentModel.getHighObjSelfid();
					}

					// call catalog.dll
					GRTCatalogItemModel[] models = null;
					try {
						models = this.getGRTCatalogItems(msgCatalogPath, lowSelfid, highSelfid);
					} catch (BusinessLogicException e) {
						e.printStackTrace();
					} catch (ServiceConnectException e) {
						e.printStackTrace();
					} catch (ServiceInternalException e) {
						e.printStackTrace();
					}
					
					boolean selectable = true;

					// TODO: Hack since path we're getting null for all CatalogItem
					// paths
					StringBuilder parentPath = new StringBuilder();
					{
						if (loadConfig.getFullPath() != null) {
							parentPath.append(loadConfig.getFullPath());
							parentPath.append("\\");
						}
						parentPath.append(loadConfig.getName());
					}

					for (int i = 0; i < models.length; i++) {
						l.add(ConvertGRTCataLogToTreeModel(models[i], msgCatalogPath,
								loadConfig.getSubSessionID().longValue(), parentPath.toString(), selectable, ""));
					}

					logger.debug("GRTCatalogItemModel length = " + models.length);
				}
			}
		}

		return l;
	}
	
	@Override
	public List<GridTreeNode> getTreeGridChildren(GridTreeNode loadConfig,
			String userName, String password) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException{
		ArrayList<GridTreeNode> l = new ArrayList<GridTreeNode>();

		if (loadConfig != null)
		{
			Integer type = loadConfig.getType();
			if (CatalogModelType.rootGRTExchangeTypes.contains(type) || 
				CatalogModelType.allGRTExchangeTypes.contains(type) || CatalogModelType.allGRTSPTypes.contains(type))
			{
				return getGRTTreeGridChildren(loadConfig);
			}
			else
			{
				String catPath = loadConfig.getCatalogFilePath();
				long parentID = loadConfig.getParentID();
				long subSessionID = loadConfig.getSubSessionID();
				CatalogItemModel[] models = null;
				String volumeGUID = loadConfig.getGuid();
				String parentP = parentID == -1 ? null: 
					loadConfig.getFullPath() + "\\" + loadConfig.getDisplayName();
				WebServiceClientProxy client = this.getServiceClient();
				try{
					if(client != null) {
						CatalogItem[] items = client.getFlashServiceR16_5().getCatalogItemsOolong(
								loadConfig.getBackupDestination(), userName, password, loadConfig.getSessionID(), volumeGUID, 
								catPath, parentID, parentP, loadConfig.getEncryptedKey());
						models = new CatalogItemModel[items.length];
						for (int i = 0; i < items.length;i++)
						{	
							models[i] = ConvertToModel(items[i]);
							models[i].setSessionNumber(loadConfig.getSessionID().intValue());
						}
					}
					
					// if models's length is 0, try to check if it is a sharepoint DB
					if(models != null && models.length==0 && type==CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE){
						return getGRTTreeGridChildren(loadConfig);
					}else{
						boolean selectable = true;
			
						//TODO: Hack since path we're getting null for all CatalogItem paths
						StringBuilder parentPath = new StringBuilder();
						if (loadConfig.getPath() != null && loadConfig.getPath().compareTo(RecoveryPointsPanel.GUID_SQLWRITER) == 0)
						{
							parentPath.append(loadConfig.getPath());
			
							if (parentID == -1)
							{
								selectable = false;
							}
						}
						else
						{
							if (loadConfig.getFullPath() != null)
							{
								parentPath.append(loadConfig.getFullPath());
								parentPath.append("\\");
							}
							parentPath.append(loadConfig.getName());
						}
			
						if(models != null){
//							String dl = this.getServiceClient().getServiceV2().getMntPathFromVolumeGUID(volumeGUID);
							for (int i = 0; i < models.length; i++)
							{
								GridTreeNode node = ConvertCataLogToTreeModel(models[i], catPath, subSessionID, 
										parentPath.toString(), selectable, volumeGUID, userName, password, 
										loadConfig.getBackupDestination());
								node.setHasDriverLetter(loadConfig.isHasDriverLetter());
								node.setVolumeMountPath(loadConfig.getVolumeMountPath());
								String path = node.getPath();
								if(path != null && volumeGUID != null 
										&& path.startsWith(volumeGUID) && node.getVolumeMountPath() != null) {
									path = path.replace(volumeGUID, node.getVolumeMountPath());
									node.setPath(path);
								}
								l.add(node);
							}
						}
			
						logger.debug("CatalogItemModel length = " + models.length);						
					}
				}catch(WebServiceException we) {
					this.proccessAxisFaultException(we);
				}catch(Throwable t) {
					logger.error("Get catalog items error", t);
				}
			}
		}
		
		return l;
	}

	@Override
	public List<GridTreeNode> getTreeGridChildren(GridTreeNode loadConfig) {

		ArrayList<GridTreeNode> l = new ArrayList<GridTreeNode>();

		if (loadConfig != null)
		{
			Integer type = loadConfig.getType();
			if (CatalogModelType.rootGRTExchangeTypes.contains(type) || 
				CatalogModelType.allGRTExchangeTypes.contains(type) || CatalogModelType.allGRTSPTypes.contains(type))
			{
				return getGRTTreeGridChildren(loadConfig);
			}
			else
			{
			String catPath = loadConfig.getCatalogFilePath();
			long parentID = loadConfig.getParentID();
			long subSessionID = loadConfig.getSubSessionID();
			CatalogItemModel[] models = this.getCatalogItems(catPath, parentID);
			// if models's length is 0, try to check if it is a sharepoint DB
			if(models.length==0 && type==CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE){
				return getGRTTreeGridChildren(loadConfig);
			}else{
			boolean selectable = true;

			//TODO: Hack since path we're getting null for all CatalogItem paths
			StringBuilder parentPath = new StringBuilder();
			if (loadConfig.getPath() != null && loadConfig.getPath().compareTo(RecoveryPointsPanel.GUID_SQLWRITER) == 0)
			{
				parentPath.append(loadConfig.getPath());

				if (parentID == -1)
				{
					selectable = false;
				}
			}
			else
			{
				if (loadConfig.getFullPath() != null)
				{
					parentPath.append(loadConfig.getFullPath());
					parentPath.append("\\");
				}
				parentPath.append(loadConfig.getName());
			}

			for (int i = 0; i < models.length; i++)
			{
						l.add(ConvertCataLogToTreeModel(models[i], catPath,
								subSessionID, parentPath.toString(),
								selectable, null, loadConfig.getDestUser(),
								loadConfig.getDestPwd(), loadConfig.getBackupDestination()));
			}

			logger.debug("CatalogItemModel length = " + models.length);
		}
		}
		}
		
		return l;
	}

	@Override
	public List<GridTreeNode> getTreeGridChildrenEx(GridTreeNode loadConfig) {
		
		ArrayList<GridTreeNode> l = new ArrayList<GridTreeNode>();

		if (loadConfig != null)
		{
			Integer type = loadConfig.getType();
			if (CatalogModelType.rootGRTExchangeTypes.contains(type)
					|| CatalogModelType.allGRTExchangeTypes.contains(type)
					|| CatalogModelType.allGRTSPTypes.contains(type))
			{
				return getGRTTreeGridChildren(loadConfig);
			}
			else
			{
				boolean bGoDeeper = false;
				do 
				{
					String catPath = loadConfig.getCatalogFilePath();
					long parentID = loadConfig.getParentID();
					long subSessionID = loadConfig.getSubSessionID();
					CatalogItemModel[] models = this.getCatalogItems(catPath, parentID);
					// if models's length is 0, try to check if it is a sharepoint
					// DB
					if (models.length == 0 && type == CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE)
					{
						return getGRTTreeGridChildren(loadConfig);
					}
					else
					{
						boolean selectable = true;

						// TODO: Hack since path we're getting null for all
						// CatalogItem paths
						StringBuilder parentPath = new StringBuilder();
						if (loadConfig.getPath() != null
								&& loadConfig.getPath().compareTo(RecoveryPointsPanel.GUID_SQLWRITER) == 0)
						{
							parentPath.append(loadConfig.getPath());

							if (parentID == -1)
							{
								selectable = false;
							}
						}
						else
						{
							if (loadConfig.getFullPath() != null)
							{
								parentPath.append(loadConfig.getFullPath());
								parentPath.append("\\");
							}
							parentPath.append(loadConfig.getName());
						}

						for (int i = 0; i < models.length; i++)
						{
							if (models[i] != null && models[i].getType() != null && 
									models[i].getType().intValue() == CatalogModelType.OT_GRT_EXCH_PUBLIC_FOLDERS)
							{
								// hide the public folder for 19483696, because currently there is no mails in public folder
								continue;
							}
							
							l.add(ConvertCataLogToTreeModel(models[i], catPath, subSessionID, parentPath.toString(),
									selectable, null, loadConfig.getDestUser(), loadConfig.getDestPwd(), loadConfig.getBackupDestination()));
						}
						
						if (l.size() == 1 && l.get(0).getType() != null 
								&& CatalogModelType.NonSelectExchangeTypes.contains(l.get(0).getType()))
						{
							bGoDeeper = true;
							loadConfig = l.get(0);
							l.clear();							
						}
						else
						{
							bGoDeeper = false;
						}
						
						logger.debug("CatalogItemModel length = " + models.length);
					}
				}
				while (bGoDeeper);
				
			}
		}

		return l;
	}

	private GridTreeNode ConvertCataLogToTreeModel(
			CatalogItemModel model, String catPath,
			Long subSessionID, String parentPath, boolean selectable, String sessionGUID,
			String destUserName, String destPassword, String destination) {

		String p = model.getPath();

		GridTreeNode node = new GridTreeNode();
		node.setSessionID(model.getSessionNumber().longValue());
		node.setName(model.getComponentName());
		node.setComponentName(model.getComponentName());
		if (model.getType() == CatalogModelType.File)
		{
			node.setSize( model.getSize() );
		}
		//node.setDate(model.getDate().toString());
		node.setParentID(model.getId());
		node.setCatalofFilePath(catPath);
		node.setType(model.getType());
		node.setSubSessionID(new Long(subSessionID).intValue());
		node.setPath(model.getPath());
		node.setFullPath(p);
		node.setSelectable(selectable);
		node.setDisplayName(model.getName());
		node.setChildrenCount(model.getChildrenCount());
		if(sessionGUID != null)
			node.setGuid(sessionGUID);
		node.setChecked(false);
		node.setDate(model.getDate());
		node.setServerTZOffset(model.getServerTimeZoneOffset());
		node.setId(node.toId().hashCode());
		node.setDestPwd(destPassword);
		node.setDestUser(destUserName);
		node.setBackupDestination(destination);
		return node;
	}

	private GridTreeNode ConvertGRTCataLogToTreeModel(
			GRTCatalogItemModel model, String grtCatalogFile,
			Long subSessionID, String parentPath, boolean selectable, String parentDisplayPath) {
		
		GridTreeNode node = new GridTreeNode();
		
		if (model != null)
		{
			
			node.setGrtCatalogItemModel(model); // attach the grt record			
			node.setGrtCatalogFile(grtCatalogFile); // grt catalog file			
			node.setCatalofFilePath(grtCatalogFile); // original catalog file
			node.setSubSessionID(subSessionID.intValue());
			
			node.setType(model.getObjType().intValue());
			node.setSelectable(selectable);
			
			node.setName(model.getObjName());
			node.setDisplayName(model.getObjName());
			node.setComponentName(model.getObjName());
			
			// date
			String strDate = model.getObjDate();
			if (strDate != null)
			{
				try
				{
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dt = dateFormat.parse(strDate);
					node.setDate(dt);
					node.setServerTZOffset(model.getSendTZOffset());
				}
				catch(Exception e)
				{
					
				}
			}
			
			if (model.getObjType().longValue() == CatalogModelType.OT_GRT_EXCH_MESSAGE)
			{
				node.setSize( model.getLowObjSize() );
			}
//			node.setParentID(model.getId());		
			node.setPath(parentPath);
			node.setFullPath(parentPath);
			node.setDisplayPath(parentDisplayPath);
			node.setChildrenCount(model.getChildrenCount());			
			
			node.setChecked(false);
			node.setId(node.toId().hashCode());			
		}
		
		return node;
	}
	
	public Integer submitRestoreJob(RestoreJobModel model) throws BusinessLogicException, ServiceConnectException, ServiceInternalException
	{
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				RestoreJob job = ConvertToRestoreJob(model);
				return client.getService().submitRestoreJob(job);
			}
		}
		catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		return -1;
	}

	private RestoreJob ConvertToRestoreJob(RestoreJobModel model) {
		RestoreJob job = new RestoreJob();
		job.setDestinationPath(model.getDestinationPath());
		job.setSessionPath(model.getSessionPath());
		job.setUserName(model.getUserName());
		job.setPassword(model.getPassword());
		job.setDestPass(model.getDestPass());
		job.setDestUser(model.getDestUser());
		job.setNodes(getRestoreNodesFromModel(model));
		job.setJobType(model.getJobType());
		job.setFileSystemOption(getFileSystemOptionFromModel(model.fileSystemOption));
		job.setRestoreType(convertFOption(model.getDestType()));
		job.setRestoreSQLNewDestList(restoreSQLNewDestList(model));
		job.setRestoreExchangeOption(restoreExchangeOption(model));
		job.setRestoreExchangeGRTOption(restoreExchangeGRTOption(model));
		job.setAdOption(restoreADoption(model));
		job.setExchRDBName(model.getRDBName());
		if(model.getJobLauncher() == null || model.getJobLauncher() == JobLauncher.D2D.getValue()){
			job.setJobLauncher(JobLauncher.D2D.getValue());
		}else{
			job.setJobLauncher(model.getJobLauncher());
			job.setVmInstanceUUID(model.getVMInstanceUUID());
		}
		job.setRecoverVMOption(convertToRecoverVMOption(model.recoverVMOption));
		job.setRpsHostname(model.getRpsHostname());
		job.setRpsDataStoreName(model.getRpsDataStoreName());
		job.setSrcRpsHost(ConvertDataToModel.convertToData(model.sourceRPSHost));
		job.setRpsPolicy(model.getRpsPolicy());
		job.setRpsDataStoreDisplayName(model.getRpsDataStoreDisplayName());
		if(model.getJobId() != null) {
			job.setJobId(model.getJobId());
		}
		if(model.getMasterJobId() != null) {
			job.setMasterJobId(model.getMasterJobId());
		}
		if (model.childRestoreJobList != null && model.childRestoreJobList.size() >0 ) {
			List<RestoreJob> childJobList = new ArrayList<RestoreJob>();
			job.setChildVMRestoreJobList(childJobList);
			for (RestoreJobModel subModel : model.childRestoreJobList) {
				childJobList.add(ConvertToRestoreJob(subModel));
			}
		}
		return job;
	}

	private RecoverVMOption convertToRecoverVMOption(RecoverVMOptionModel model){
		if(model == null){
			return null;
		}
		RecoverVMOption recoverVMOption = new RecoverVMOption();
		recoverVMOption.setEsxServerName(model.getESXServerName());
		recoverVMOption.setOriginalLocation(model.getOriginalLocation());
		recoverVMOption.setOverwriteExistingVM(model.isOverwriteExistingVM());
		recoverVMOption.setGenerateNewVMinstID(model.isGenerateNewInstVMID());
		recoverVMOption.setPowerOnAfterRestore(model.isPowerOnAfterRestore());
		recoverVMOption.setRestoreDiskDataStores(converToRestoreDiskDataStore(model.getDiskDataStore()));
		recoverVMOption.setSessionNumber(model.getSesstionNumber());
		recoverVMOption.setVmName(model.getVMName());
		recoverVMOption.setVmUsername(model.getVMUsername());
		recoverVMOption.setVmPassword(model.getVMPassword());
		recoverVMOption.setVc(ConvertToVirtualCenter(model.getVCModel()));
		recoverVMOption.setVmDataStore(model.getVmDataStore());
		recoverVMOption.setVmDataStoreId(model.getVmDataStoreId());
		recoverVMOption.setVcName(model.getVcName());
		Integer diskCount = model.getVmDiskCount();
		recoverVMOption.setVmDiskCount(diskCount == null ? 0 : diskCount);
		recoverVMOption.setVmUUID(model.getVMUUID());
		recoverVMOption.setVmInstanceUUID(model.getVMInstanceUUID());
		recoverVMOption.setVmIdInVApp(model.getVmIdInVApp());
		recoverVMOption.setEncryptPassword(model.getEncryptPassword());
		recoverVMOption.setResourcePoolName(model.getResourcePoolName());
		recoverVMOption.setVMNetworkConfig(converToVMNetworkConfig(model.getVMNetworkConfigInfoList()));
		recoverVMOption.setRegisterAsClusterHyperVVM(model.isRegisterAsClusterHyperVVM());
		Integer cpuCount = model.getCPUCount();
		recoverVMOption.setCpuCount(model.getCPUCount() == null ? 0 : cpuCount);
		Long size = model.getMemorySize();
		recoverVMOption.setMemorySize(size == null ? 0 : size);
		recoverVMOption.setStoragePolicyId(model.getStorageProfileId());
		recoverVMOption.setStoragePolicyName(model.getStorageProfileName());
		
		return recoverVMOption;
	}

	private RestoreDiskDataStore[] converToRestoreDiskDataStore(List<DiskDataStoreModel> list){
		if(list == null || list.size()==0){
			return null;
		}
		RestoreDiskDataStore[] restoreDiskDataStore = new RestoreDiskDataStore[list.size()];
		RestoreDiskDataStore temp = null;
		int i=0;
		for(DiskDataStoreModel model: list){
			temp = new RestoreDiskDataStore();
			temp.setDataStore(model.getDatastore());
			temp.setDisk(model.getDisk());
			temp.setDiskType(model.getDiskType());
			temp.setQuickRecovery(model.getQuickRecovery());
			restoreDiskDataStore[i++] = temp;
		}
		return restoreDiskDataStore;

	}

	private VMNetworkConfig[] converToVMNetworkConfig(List<VMNetworkConfigInfoModel> list){
		if(list == null || list.size()==0){
			return null;
		}
		VMNetworkConfig[] networkConfigList = new VMNetworkConfig[list.size()];
		VMNetworkConfig temp = null;
		int i=0;
		for(VMNetworkConfigInfoModel model: list){
			temp = new VMNetworkConfig();
			temp.setBackingInfoType(model.getBackingInfoType());
			temp.setDeviceName(model.getDeviceName());
			temp.setDeviceType(model.getDeviceType());
			temp.setLabel(model.getLabel());
			temp.setPortgroupKey(model.getPortgroupKey());
			temp.setPortgroupName(model.getPortgroupName());
			temp.setSwitchName(model.getSwitchName());
			temp.setSwitchUUID(model.getSwitchUUID());
			
			temp.setId(model.getNetworkId());
			temp.setParentId(model.getParentId());
			temp.setParentName(model.getParentName());
			
			networkConfigList[i++] = temp;
		}
		return networkConfigList;

	}
	
	private RestoreExchangeOption restoreExchangeOption(RestoreJobModel model) {
		ExchangeOptionModel optionModel = model.exchangeOption;
		RestoreExchangeOption exOption = null;
		if(optionModel != null)
		{
			exOption = new RestoreExchangeOption();
			exOption.setDismounAndMountDB(optionModel.isDisMoundAndMountDB());
			exOption.setReplayLogOnDB(optionModel.isReplayLogOnDB());
		}
		return exOption;
	}

	private RestoreExchangeGRTOption restoreExchangeGRTOption(RestoreJobModel model)
	{
		ExchangeGRTOptionModel optionModel = model.exchangeGRTOption;
		RestoreExchangeGRTOption exOption = null;
		if(optionModel != null)
		{
			exOption = new RestoreExchangeGRTOption();
			exOption.setOption(optionModel.getOption());
			
			if (optionModel.getServerVersion() != null)
			{
			    exOption.setServerVersion(optionModel.getServerVersion());
			}
			
			exOption.setFolder(optionModel.getFolder());
			exOption.setAlternateServer(optionModel.getAlternateServer());
			exOption.setUserName(optionModel.getUserName());
			exOption.setPassword(optionModel.getPassword());
			exOption.setDefaultE15CAS(optionModel.getDefaultE15CAS());
		}
		return exOption;
	}
	
	private RestoreADOption restoreADoption(RestoreJobModel model) {
		ADOptionModel optionModel = model.adOption;
		RestoreADOption option = new RestoreADOption();
		if(optionModel != null)
		{
			option.setSkipRenamedObject(optionModel.isSkipRenamedObject());
			option.setSkipMovedObject(optionModel.isSkipMovedObject());
			option.setSkipDeletedObject(optionModel.isSkipDeletedObject());
		}
		return option;
	}

	private int convertFOption(Integer destType) {
		if (destType == null)
			return 0;

		if(destType.intValue() == DestType.OrigLoc.getValue())
			return DestTypeContratct.OrigLoc.getValue();
		else if(destType.intValue() == DestType.AlterLoc.getValue())
			return DestTypeContratct.AlterLoc.getValue();
		else if(destType.intValue() == DestType.DumpFile.getValue())
			return DestTypeContratct.DumpFile.getValue();
		else if(destType.intValue() == DestType.ExchRestore2RDB.getValue()
				|| destType.intValue() == DestType.ExchRestore2RSG.getValue())
			return DestTypeContratct.RSGRDB.getValue();
		return 0;
	}

	private List<RestoreSQLNewDest>  restoreSQLNewDestList(RestoreJobModel model) {
		List<SQLModel> destList = model.listOfSQLMode;
		List<RestoreSQLNewDest> newSQLDest = null;
		if(destList != null)
		{
			newSQLDest = new ArrayList<RestoreSQLNewDest>();
			for (SQLModel modelDest : destList) {
				RestoreSQLNewDest dest = new RestoreSQLNewDest();
				dest.setDbPath(modelDest.getPath());
				dest.setInstanceName(modelDest.getInstanceName());
				dest.setDbName(modelDest.getDbName());
				if(!StringUtil.isEmptyOrNull(modelDest.getTranslatedFilePath()))
					dest.setRestoreDestPath(modelDest.getTranslatedFilePath());
				else
					dest.setRestoreDestPath(modelDest.getNewFileLoc());
				dest.setNewDbName(modelDest.getNewDbName());
				newSQLDest.add(dest);
			}
		}
		return newSQLDest;
	}

	private FileSystemOption getFileSystemOptionFromModel(
			FileSystemOptionModel fileSystemOption) {
		if (fileSystemOption == null)
			return null;

		FileSystemOption option = new FileSystemOption();
		option.setOverwriteExistingFiles(fileSystemOption.isOverwriteExistingFiles());
		option.setReplaceActiveFiles(fileSystemOption.isReplaceActiveFiles());
		option.setCreateBaseFolder(fileSystemOption.isCreateBaseFolder());
		option.setRenameFile(fileSystemOption.isRename());

		return option;
	}

	private List<RestoreJobNode> getRestoreNodesFromModel(RestoreJobModel model) {
		if(model.listOfRestoreJobNodes == null){
			return null;
		}
		List<RestoreJobNode> newSQLDest =  new ArrayList<RestoreJobNode>();;
//		RestoreJobNode[] nodes = new RestoreJobNode[ model.listOfRestoreJobNodes.size()];
		for (int i = 0; i < model.listOfRestoreJobNodes.size(); i++)
		{
			newSQLDest.add(ConvertToRestoreJobNode((RestoreJobNodeModel)model.listOfRestoreJobNodes.get(i)));
		}
		return newSQLDest;
	}

	private RestoreJobNode ConvertToRestoreJobNode(RestoreJobNodeModel model) {
		RestoreJobNode n = new RestoreJobNode();
		n.setSessionNumber(model.getSessionNumber());
		n.setJobItems(getRestoreItemsFromModel(model));
		n.setEncryptPassword(model.getEncryptPassword());
		return n;
	}

	// grouping ExchSubItem by JobItemEntry
	private void ReGroupingJobItemEntrys(RestoreJobItemModel model)
	{
		Hashtable<String, ArrayList<RestoreJobItemEntryModel>> ht = new Hashtable<String, ArrayList<RestoreJobItemEntryModel>>();
		
		// grouping by the path of RestoreJobItemEntry
		for (int i = 0; i < model.listOfFiles.size() ; i ++)
		{	
			 RestoreJobItemEntryModel m = (RestoreJobItemEntryModel)model.listOfFiles.get(i);			 
			 if (m != null && m.getPath() != null)
			 {
				 if (ht.containsKey(m.getPath()))
				 {
					ArrayList<RestoreJobItemEntryModel> list = ht.get(m.getPath());
					list.add(m);				
				 }
				 else
				 {
					 ArrayList<RestoreJobItemEntryModel> list = new ArrayList<RestoreJobItemEntryModel>();
					 list.add(m);
					 ht.put(m.getPath(), list);
				 }		
			 }
		}
		
		// merge the ExchSubItems which has same parent RestoreJobItemEntry
		Object[] keys = ht.keySet().toArray();
		RestoreJobItemEntryModel[] items = new RestoreJobItemEntryModel[keys.length];
		for (int i = 0; i < keys.length; i++)
		{
			ArrayList<RestoreJobItemEntryModel> list = (ArrayList<RestoreJobItemEntryModel>)ht.get(keys[i]);
			RestoreJobItemEntryModel mainModel = new RestoreJobItemEntryModel();
			mainModel.listOfExchSubItems = new ArrayList<RestoreJobExchSubItemModel>();
			if (list != null && list.size() > 0)
			{		
				mainModel.setPath(list.get(0).getPath());
				mainModel.setType(list.get(0).getType());
				//for ad retore, list size is 1
				mainModel.listOfADItems = list.get(0).listOfADItems;
				for (int j = 0; j < list.size(); j++)
				{
					RestoreJobItemEntryModel subModel = list.get(j);
					if (subModel.listOfExchSubItems != null &&
							subModel.listOfExchSubItems.size() > 0)
					{
						mainModel.listOfExchSubItems.addAll(subModel.listOfExchSubItems);
					}
				}				
			}
			items[i] = mainModel;
		}
		
		model.listOfFiles.clear();
		for (int i=0; i<items.length; i++)
		{
			model.listOfFiles.add(items[i]);
		}		
		
		return;
	}

	private RestoreJobItem[] getRestoreItemsFromModel(RestoreJobNodeModel model) {

		Hashtable<String, ArrayList<RestoreJobItemModel>> ht = new Hashtable<String, ArrayList<RestoreJobItemModel>>();

		for (int i = 0; i < model.listOfRestoreJobItems.size() ; i ++)
		{
			 RestoreJobItemModel m = (RestoreJobItemModel)model.listOfRestoreJobItems.get(i);
			 if (m != null && m.getPath() != null)
			 {
				 if (ht.containsKey(m.getPath()))
				 {
					ArrayList<RestoreJobItemModel> list = ht.get(m.getPath());
					list.add(m);
				 }
				 else
				 {
					 ArrayList<RestoreJobItemModel> list = new ArrayList<RestoreJobItemModel>();
					 list.add(m);
					 ht.put(m.getPath(), list);
				 }
			 }
		}

		Object[] keys = ht.keySet().toArray();
		RestoreJobItem[] items = new RestoreJobItem[keys.length];
		for (int i = 0; i < keys.length; i++)
		{
			ArrayList<RestoreJobItemModel> list = (ArrayList<RestoreJobItemModel>)ht.get(keys[i]);
			RestoreJobItemModel mainModel = new RestoreJobItemModel();
			mainModel.listOfFiles = new ArrayList<RestoreJobItemEntryModel>();
			if (list != null && list.size() > 0)
			{
				mainModel.setPath(list.get(0).getPath());
				mainModel.setSubSessionNum(list.get(0).getSubSessionNum());
				for (int j = 0; j < list.size(); j++)
				{
					RestoreJobItemModel subModel = list.get(j);
					if (subModel.listOfFiles != null &&
							subModel.listOfFiles.size() > 0)
					{
						mainModel.listOfFiles.addAll(subModel.listOfFiles);
					}
				}
			}
			
			ReGroupingJobItemEntrys(mainModel);
			
			items[i] = ConvertToRestoreJobItem(mainModel);

		}
		return items;
	}

	private RestoreJobItem ConvertToRestoreJobItem(RestoreJobItemModel m) {
		RestoreJobItem item = new RestoreJobItem();
		item.setPath(m.getPath());
		item.setSubSessionNum(m.getSubSessionNum());

		if (m.listOfFiles != null && m.listOfFiles.size() > 0)
		{
			RestoreJobItemEntry[] entries = new RestoreJobItemEntry[m.listOfFiles.size()];
			for (int i = 0; i < m.listOfFiles.size(); i++)
			{
				entries[i] = ConvertToModel((RestoreJobItemEntryModel)m.listOfFiles.get(i));
			}
			item.setEntries(entries);
		}

		return item;
	}

	private RestoreJobItemEntry ConvertToModel(
			RestoreJobItemEntryModel restoreJobItemEntryModel) {

		RestoreJobItemEntry entry = new RestoreJobItemEntry();
		try{
			entry.setPath(restoreJobItemEntryModel.getPath());
			entry.setType(restoreJobItemEntryModel.getType());
			
			if (restoreJobItemEntryModel.listOfExchSubItems != null)
			{
				RestoreJobExchSubItem[] exchSubItems = new RestoreJobExchSubItem[restoreJobItemEntryModel.listOfExchSubItems.size()];
				for (int i=0; i< restoreJobItemEntryModel.listOfExchSubItems.size(); i++)
				{					
					exchSubItems[i] = ConvertToExchSubItem(restoreJobItemEntryModel.listOfExchSubItems.get(i));
				}

				entry.setExchSubItems(exchSubItems);
			}	
			if(restoreJobItemEntryModel.listOfADItems != null){
				List<RestoreJobADItem> list = new ArrayList<RestoreJobADItem>();
				HashMap<Long, RestoreJobADItem> map = new HashMap<Long, RestoreJobADItem>();
				for(GridTreeNode ad : restoreJobItemEntryModel.listOfADItems){
					if(ad.getType()==CatalogModelType.AD_ATTRIBUTE){
						RestoreJobADItem attr = map.get(ad.getParentID());
						if(attr==null){
							attr = new RestoreJobADItem();
							attr.setId(ad.getParentID());
							attr.setAllAttribute(false);
							attr.setAllChild(false);
							attr.setAttrNames(new ArrayList<String>());
							map.put(ad.getParentID(), attr);
						}
						attr.getAttrNames().add(ad.getName());
					}else{
						RestoreJobADItem item = new RestoreJobADItem();
						item.setId(ad.getId());
						list.add(item);
					}
				}
				list.addAll(map.values());
				entry.setAdItems(list.toArray(new RestoreJobADItem[0]));
			}
		}
		catch (Exception e)
		{

		}
		return entry;

	}

	private RestoreJobExchSubItem ConvertToExchSubItem(
			RestoreJobExchSubItemModel exchSubItemModel) {
		
		RestoreJobExchSubItem exchSubItem = null;
		try{
			
			if (exchSubItemModel != null)
			{
				exchSubItem = new RestoreJobExchSubItem();
				
				exchSubItem.setUlItemType(exchSubItemModel.getItemType());
				exchSubItem.setPwszItemName(exchSubItemModel.getItemName());
				exchSubItem.setMailboxName(exchSubItemModel.getMailboxName());
//				exchSubItem.setUl_hMailboxID(exchSubItemModel.getHMailBoxID());
//				exchSubItem.setUl_lMailboxID(exchSubItemModel.getLMailBoxID());
//				exchSubItem.setUl_hFolderID(exchSubItemModel.getHFolderID());
//				exchSubItem.setUl_lFolderID(exchSubItemModel.getLFolderID());
//				exchSubItem.setUl_hMsgID(exchSubItemModel.getHMsgID());
//				exchSubItem.setUl_lMsgID(exchSubItemModel.getLMsgID());
				exchSubItem.setExchangeObjectID(exchSubItemModel.getExchangeObjectID());
				exchSubItem.setPwszDescription(exchSubItemModel.getDescription());				
			}
			
		}
		catch (Exception e)
		{
			
		}
		return exchSubItem;
		
	}

	@Override
	public int closeSearchCatalog(SearchContextModel model) {
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				SearchContext context = ConvertToContext(model);
				return client.getService().closeSearchCatalog(context);
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());

		}
		return -1;
	}

	private SearchContext ConvertToContext(SearchContextModel model) {
		SearchContext c = new SearchContext();
		c.setContextID(model.getContextID());
		c.setTag(model.getTag());
		c.setCurrKind(model.getCurrKind());
		c.setSearchkind(model.getSearchkind());
		c.setExcludeFileSystem(model.isExcludeFileSystem());		
		return c;
	}

	private SearchContextModel ConvertToModel(SearchContext context) {
		SearchContextModel model = new SearchContextModel();
		model.setContextID(context.getContextID());
		model.setTag(context.getTag());
		model.setCurrKind(context.getCurrKind());
		model.setSearchkind(context.getSearchkind());
		model.setExcludeFileSystem(context.isExcludeFileSystem());
		return model;
	}

	@Override
	public SearchResultModel searchNext(SearchContextModel context) 
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				SearchContext c = ConvertToContext(context);
				SearchResult result = client.getService().searchNext(c);
				return ConvertToModel(result);
			}
		}
		catch (Exception e)
		{
			if(e.getCause() instanceof SocketTimeoutException) {
				this.proccessAxisFaultException((WebServiceException)e);
			}
			logger.debug(e.getMessage());

		}
		return null;
	}

	private SearchResultModel ConvertToModel(SearchResult result) {
		SearchResultModel m = new SearchResultModel();
		m.setCurrent(result.getCurrent());
		m.setFound(result.getFound());
		m.setNextKind(result.getNextKind());

		m.listOfItems = new ArrayList<CatalogItemModel>();

		CatalogItem[] items = result.getDetail();
		if (items != null)
		{
			for (int i = 0; i < items.length; i++)
			{
				m.listOfItems.add(ConvertToModel(items[i]));
			}
		}
		MsgSearchRec[] recs = result.getMsgDetail();
		if (recs != null) {
			for (int i = 0; i < recs.length; i++) {
				m.listOfItems.add(ConvertToModel(recs[i]));
			}
		}

		return m;
	}

	private CatalogItemModel ConvertToModel(MsgSearchRec msgSearchRec) {
		CatalogItemModel model = new CatalogItemModel();
		model.setName(msgSearchRec.getMsgRec().getObjName());
		model.setDate(msgSearchRec.getMsgRec().getReceivedTime());
		model.setServerTimeZoneOffset(msgSearchRec.getMsgRec().getReceivedTZOffset());
		model.setSessionNumber((int)msgSearchRec.getSessionNumber());
		model.setSubSessionNumber((int)msgSearchRec.getSubSessionNumber());
		model.setFullPath(msgSearchRec.getMailFullDisplayPath());
		model.setPath(msgSearchRec.getMailFullDisplayPath());
		//model.setSize(msgSearchRec.getMsgRec().getHighObjSize()<<32 + msgSearchRec.getMsgRec().getLowObjSize());
		model.setSize(msgSearchRec.getMsgRec().getItemSize());
		model.setType((int)msgSearchRec.getMsgRec().getObjType());		
		model.setMsgRecModel(convertToModel(msgSearchRec));
		
		model.setFullSessionNumber(msgSearchRec.getFullSessNum());
		model.setEncrypted(msgSearchRec.getEncryptInfo()==0?false:true);
		model.setBackupDestination(msgSearchRec.getbKDest());
		model.setBackupDate(msgSearchRec.getbKTime());
		model.setBKServerTimeZoneOffset(msgSearchRec.getBkTimeZoneOffset());
		model.setBackupJobName(msgSearchRec.getJobName());
		model.setPasswordHash(msgSearchRec.getpWDHash());
		model.setSessionGuid(msgSearchRec.getSessGUID());
		model.setFullSessionGuid(msgSearchRec.getFullSessGUID());
		return model;
	}

	private MsgSearchRecModel convertToModel(MsgSearchRec msgSearchRec) {
		MsgSearchRecModel m = new MsgSearchRecModel();
		m.setEdbDisplayName(msgSearchRec.getEdbDisplayName());
		m.setEdbFullPath(msgSearchRec.getEdbFullPath());
		m.setMailboxOrSameLevelName(msgSearchRec.getMailboxOrSameLevelName());
		m.setMsgRec(ConvertToGRTCatalogItemModel(msgSearchRec.getMsgRec()));
		m.setEdbType(msgSearchRec.getEdbType());
		m.setMailFullDisplayPath(msgSearchRec.getMailFullDisplayPath());
		m.setSessionNumber(msgSearchRec.getSessionNumber());
		m.setSubSessionNumber(msgSearchRec.getSubSessionNumber());		
		return m;
	}

	@Override
	public void createFolder(String parentPath, String subDir, int browseClient)
	   throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		WebServiceClientProxy client = null;
		
		try {
			if(browseClient == 1) {
				client = getMonitorClientProxy();
			} else {
				client = this.getServiceClient();
			}
			
			if (client != null) {
				client.getService().createFolder(parentPath, subDir);
			}
		} catch (WebServiceException exception) {
				proccessAxisFaultException(exception);
		}
	}

	@Override
	public List<FileModel> getFileFolderChildren(String path,
			boolean bIncludeFiles) {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				FileFolderItem item = client.getService().getFileFolder(path);
				List<FileModel> modelList = ConvertToFileModelList(item,
						bIncludeFiles);
				return modelList;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return null;
	}

	@Override
	public List<FileModel> getFileItems(String inputFolder, String user,
			String password, boolean bIncludeFiles,int browseClient)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		
		try {
			if (browseClient ==1) {
				client = getMonitorClientProxy();
			} else {
				client = this.getServiceClient();
			}
			
			if (user == null)
				user = "";
			
			if (password == null)
				password = "";

			if (inputFolder != null && inputFolder.endsWith("\\") && !inputFolder.endsWith("\\\\"))
				inputFolder = inputFolder.substring(0, inputFolder.lastIndexOf("\\"));
			
			if (inputFolder != null && inputFolder.endsWith("/"))
				inputFolder = inputFolder.substring(0, inputFolder.lastIndexOf("/"));
			
			FileFolderItem item = null;
			
			if (client != null) {
				item = client.getService().getFileFolderWithCredentials(inputFolder, user, password);
			}
			
			if (item != null) {
				List<FileModel> modelList = ConvertToFileModelList(item, bIncludeFiles);
				return modelList;
			}
		} catch (WebServiceException exception) {
			logger.debug(exception.getMessage());
			proccessAxisFaultException(exception);
		}
		
		return null;
	}

	private List<FileModel> ConvertToFileModelList(FileFolderItem item,
			boolean includeFiles) {

		List<FileModel> modelList = new ArrayList<FileModel>();

		if (item.getFolders() != null)
		{
			for (int i = 0; i < item.getFolders().length; i++)
			{
				modelList.add(ConvertToModel(item.getFolders()[i]));
			}
		}
		if (includeFiles && item.getFiles() != null)
		{
			for (int i = 0; i < item.getFiles().length; i++)
			{
				modelList.add(ConvertToModel(item.getFiles()[i]));
			}
		}
		return modelList;
	}

	private FileModel ConvertToModel(File file) {
		FileModel m = new FileModel();
		m.setName(file.getName());
		m.setPath(file.getPath());
		m.setType(CatalogModelType.File);
		
		return m;
	}

	private FileModel ConvertToModel(Folder folder) {
		FileModel m = new FileModel();
		m.setName(folder.getName());
		m.setPath(folder.getPath());
		if(folder.getPath().equals("")){
			m.setType(CatalogModelType.File);
		}else{
			m.setType(CatalogModelType.Folder);
		}
		return m;
	}

	@Override
	public List<FileModel> getVolumes(int browseClient) {
		logger.debug("getVolumes() enter");
		WebServiceClientProxy client = null;
		
		try {
			if (browseClient == 1) {
				client = getMonitorClientProxy();
			} else {
				client = this.getServiceClient();
			}
			
			Volume[] volumes = null;
			
			if (client != null) {
				volumes = client.getService().getVolumes();
			}
			
			if (volumes != null) {
				List<FileModel> modelList = new ArrayList<FileModel>();
				
				for (int i = 0; i < volumes.length; i++) {
					modelList.add(ConvertToVolumeModel(volumes[i]));
				}
				
				logger.debug("volumes:" + StringUtil.convertList2String(modelList));
				logger.debug("getVolumes() end");
				
				return modelList;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		
		return null;
	}

	@Override
	public List<FileModel> getVolumesWithDetails(int browseClient, String backupDest, String usr, String pwd) {
		logger.debug("getVolumesWithDetails() enter");
		WebServiceClientProxy client = null;
		
		try {
			if(browseClient == 1) {
				client = getMonitorClientProxy();
			} else {
				client = this.getServiceClient();
			}
			
			Volume[] volumes = null;

			if (client != null) {
				volumes = client.getService().getVolumesWithDetails(backupDest, usr, pwd);
			}
			
			if (volumes != null) {
				List<FileModel> modelList = new ArrayList<FileModel>();
				
				for (int i = 0; i < volumes.length; i++) {
					modelList.add(ConvertToVolumeModel(volumes[i]));
				}
				
				logger.debug("volumes:" + StringUtil.convertList2String(modelList));
				logger.debug("getVolumesWithDetails() end");
				
				return modelList;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		
		return null;
	}

	private FileModel ConvertToVolumeModel(Volume vol) {
		VolumeModel m = new VolumeModel();

		m.setGUID(vol.getGuid());
		m.setName(vol.getName());
		m.setPath(vol.getName());
		m.setType(vol.getType());
		m.setTotalSize(vol.getSize());
		m.setFreeSize(vol.getFreeSize());
		m.setLayout(vol.getLayOut());
		m.setFileSysType(vol.getFsType());
		m.setStatus(vol.getStatus());
		m.setSubStatus(vol.getSubStatus());
		m.setIsShow(vol.getIsShow());
		m.setMsgID(vol.getMsgID());
		m.setDisplayName(vol.getDisplayName());
		m.setIsdeduped(vol.getIsDeduped());
		m.setDataStore(vol.getDatastore());
		if(vol.getIsEmpty() == null)
			m.setIsEmpty(false);
		else
			m.setIsEmpty(vol.getIsEmpty().equals("1")?true:false);
		

		return m;
	}	
	
	@Override
	public SearchContextModel openSearchCatalog(String destination,
			String domain, String userName, String password,
			String sessionPath, String sDir, boolean bCaseSensitive,
			boolean bIncludeSubDir, String pattern)
	throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				SearchContext context = client.getFlashService(IFlashService_R16_U4.class)
				.openSearchCatalogWithCredentials(
				destination, domain, userName, password, 
					sessionPath, sDir, bCaseSensitive, bIncludeSubDir, pattern);
				return ConvertToModel(context);
			}
		}
		catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public SearchContextModel openSearchCatalog(String sessionPath, String dir,
			boolean caseSensitive, boolean includeSubDir, String pattern) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				SearchContext context = client.getService().openSearchCatalog(sessionPath,
						dir, caseSensitive, includeSubDir, pattern);
				return ConvertToModel(context);
			}
		}
		catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		return null;
	}

	private RecoveryPoint convertToRecoveryPoint(RecoveryPointModel model) {
		RecoveryPoint rp = new RecoveryPoint();
		if(model.getArchiveJobStatus() != null)
			rp.setArchiveJobStatus(model.getArchiveJobStatus());
		if(model.getBackupStatus() != null)
			rp.setBackupStatus(model.getBackupStatus());
		if(model.getBackupType() != null)
			rp.setBackupType(model.getBackupType());
		if(model.getDataSize() != null)
			rp.setDataSize(model.getDataSize());
		if(model.getEncryptPwdHashKey() != null)
			rp.setEncryptPasswordHash(model.getEncryptPwdHashKey());
		if(model.getEncryptionType() != null)
			rp.setEncryptType(model.getEncryptionType());
		if(model.getFSCatalogStatus() != null)
			rp.setFsCatalogStatus(model.getFSCatalogStatus());
		if(model.getName() != null)
			rp.setName(model.getName());
		if(model.getPath() != null)
			rp.setPath(model.getPath());
		if(model.getSessionGuid() != null)
			rp.setSessionGuid(model.getSessionGuid());
		if(model.getSessionID() != null)
			rp.setSessionID(model.getSessionID());
		if(model.getSessionVersion() != null)
			rp.setSessionVersion(model.getSessionVersion());
		if(model.getTime() != null)
			rp.setTime(model.getTime());
		if(model.getTimeZoneOffset() != null)
			rp.setTimeZoneOffset(model.getTimeZoneOffset());
		
		return rp;
	}
	
	@SuppressWarnings("unused")
	private RecoveryPointItem convertToRecoveryPointItem(RecoveryPointItemModel model) {
		RecoveryPointItem item = new RecoveryPointItem();
		item.setCatalogFilePath(model.getCatalogFilePath());
		item.setChildrenCount(model.getChildrenCount());
		item.setDisplayName(model.getDisplayName());
		item.setGuid(model.getGuid());
		item.setSubSessionID(model.getSubSessionID());
		item.setVolAttr(model.getVolAttr());
		item.setVolDataSizeB(model.getVolDataSizeB());
		item.setVolumeOrAppType(model.getType());
		
		return item;
	}
	@Override
	public List<HostInfo> getHostInfoList() {
		return null;
	}

	public boolean logout() {
		logger.debug("logout entered");
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		session.invalidate();		
		logger.debug("logout exited");
		return true;
	}

	public Integer submitCopyJob(CopyJobModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				CopyJob job = ConvertToCopyJob(model);
				return client.getService().submitCopyJob(job);
			}
		} catch (WebServiceException exception) {
			logger.error("Error occurs during submitCopyJob()");
			logger.error(exception);
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return -1;
	}

	@SuppressWarnings("unused")
	private BusinessLogicException generateCopyException(WebServiceException exception) {
		BusinessLogicException ex = this
				.generateException(FlashServiceErrorCode.CopyJob_FailedToSubmitCopy);
		String errMsg = MessageFormatEx.format(ex.getDisplayMessage(),
				exception.getMessage());
		ex.setDisplayMessage(errMsg);
		return ex;
	}

	@SuppressWarnings("unused")
	private BusinessLogicException generateValidateDestException(WebServiceException exception) {
		BusinessLogicException ex = this
				.generateException(FlashServiceErrorCode.CopyJob_VaildateCopyDestFailed);
		String errMsg = MessageFormatEx.format(ex.getDisplayMessage(),
				exception.getMessage());
		ex.setDisplayMessage(errMsg);
		return ex;
	}

	private CopyJob ConvertToCopyJob(CopyJobModel model) {
		CopyJob job = new CopyJob();
		job.setSessionPath(model.getSessionPath());
//		// for VHD
//		if (model.getCompressionLevel() == IConstants.COMPRESSIONNONEVHD) {
//			job.setJobType(7);
//		} else {
//			job.setJobType(2);
//		}
		job.setJobType(2);
		job.setRestPoint(model.getRestPoint());
		job.setDestinationPath(model.getDestinationPath());
		job.setCompressionLevel(model.getCompressionLevel());
		job.setEncryptPassword(model.getEncryptPassword());
		if(model.getEncryptTypeCopySession() != null) {
			job.setEncryptTypeCopySession(model.getEncryptTypeCopySession());
			job.setEncryptPasswordCopySession(model.getEncryptPasswordCopySession());
		}
		job.setPassword(model.getPassword());
		job.setUserName(model.getUserName());
		job.setDestinationPassword(model.getDestinationPassword());
		job.setDestinationUserName(model.getDestinationUserName());
		job.setSessionNumber(model.getSessionNumber());
		job.setVmInstanceUUID(model.getVMInstanceUUID());
		job.setJobLauncher(model.getJobLauncher());
		job.setRpsHostname(model.getRpsHostname());
		job.setRpsPolicy(model.getRpsPolicyUUID());
		job.setRpsHost(ConvertDataToModel.convertToData(model.rpsHost));
		job.setRpsDataStore(model.getRPSDataStoreUUID());
		job.setRpsDataStoreDisplayName(model.getRpsDataStoreDisplayName());
		job.setRetainEncryptionAsSource(model.getRetainEncryptionAsSource());
		return job;
	}
	@Override
	public VersionInfoModel getVersionInfo(){
		logger.debug("getVersionInfo() - start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				VersionInfo vInfo = client.getService().getVersionInfo();
				VersionInfoModel vModel = convertToVersionInfoModel(vInfo);
				try {
					boolean showUpdate = client.getFlashService(IFlashService_R16_U4.class).isShowUpdate();
					vModel.setShowUpdate(showUpdate);
				} catch (WebServiceException e) {
					logger.error("Failed to get whether to show update information", e);
				}
				try {
					LicInfo lic = getServiceClient().getServiceV2().getLicenseInfo();
					vModel.setNCE(lic.getBase()==LicInfoModel.LICENSE_BASE_NCE);
				}catch(WebServiceException e) {
					logger.error("Failed to get license info to show NCE information", e);
				}
				return vModel;
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		finally {
			//clear the selected monitee caches for Virtual Standby monitee in case user Refreshes his web page
			//because this is the first function is called.
			setCurrentMonitee(null);
			setMoniteeServiceClient(null);
		}
		logger.debug("getVersionInfo() - end");
		return null;
	}

	private VersionInfoModel convertToVersionInfoModel(VersionInfo vInfo) {
		VersionInfoModel vModel = new VersionInfoModel();
		if (vInfo != null) {
			vModel.setLocale(vInfo.getLocale());
			vModel.setCountry(vInfo.getCountry());
			vModel.setMinorVersion(vInfo.getMinorVersion());
			vModel.setMajorVersion(vInfo.getMajorVersion());
			vModel.setBuildNumber(vInfo.getBuildNumber());
			vModel.setUpdateNumber(vInfo.getUpdateNumber());
			vModel.setUpdateBuildNumber(vInfo.getUpdateBuildNumber());
			vModel.setTimeZoneID(vInfo.getTimeZoneID());
			vModel.setTimeZoneOffset(vInfo.getTimeZoneOffset());
			vModel.setLocalDriverLetters(java.util.Arrays.asList(vInfo.getLocalDriverLetters()));
			vModel.setShowSocialNW(BaseServiceImpl.isShowSocialNW());
			vModel.setLocalADTPackage(vInfo.getLocalADTPackage());
			vModel.setLocalHostName(getLocalhostName());
			vModel.setDedupInstalled(vInfo.isDedupInstalled());
			vModel.setWin8(vInfo.isWin8());
			vModel.setReFsSupported(vInfo.isReFsSupported());
			vModel.setOsName(vInfo.getOsName());
			vModel.setUefiFirmware(vInfo.isUefiFirmware());
			vModel.dataFormat = ConvertToDataFormatModel(vInfo.getDataFormat());
			vModel.setDisplayVserion(vInfo.getDisplayVersion());
			vModel.setSettingConfiged(vInfo.isSettingConfiged());
			try {
				// catch the exception in case the product type is not what we
				// expected.
				vModel.setProductType(Integer.parseInt(vInfo.getProductType()));
			} catch (Exception e) {
				logger.warn("The product type is not correct " + vInfo.getProductType());
			}
			vModel.edgeInfoCM = ConvertToEdgeInfoModel(vInfo.getEdgeInfoCM());
			vModel.edgeInfoVCM = ConvertToEdgeInfoModel(vInfo.getEdgeInfoVCM());
			vModel.edgeInfoVS = ConvertToEdgeInfoModel(vInfo.getEdgeInfoVS());			

			Integer flag = 0;
			Cookie[] cookies = getThreadLocalRequest().getCookies();
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("requestMethod")) {
					flag = 1;
				}
			}
			if (flag != null && flag == 1) {
				vModel.setRequestMethod(RequestMethod.POST);
			} else
				vModel.setRequestMethod(RequestMethod.GET);
		}
		return vModel;
	}
	
	private DataFormatModel ConvertToDataFormatModel(DateFormat df){
		if(df == null)
			return null;
		DataFormatModel model = new DataFormatModel();
		model.setDateFormat(df.getDateFormat());
		model.setShortTimeFormat(df.getShortTimeFormat());
		model.setTimeDateFormat(df.getTimeDateFormat());
		model.setTimeFormat(df.getTimeFormat());
		return model;
	}
	
	private EdgeInfoModel ConvertToEdgeInfoModel(EdgeInfo edgeInfo){
		if(edgeInfo == null)
			return null;
		EdgeInfoModel edgeInfoModel = new EdgeInfoModel();
		edgeInfoModel.setEdgeHostName(edgeInfo.getEdgeHostName());
		edgeInfoModel.setEdgeUrl(edgeInfo.getEdgeUrl());
		return edgeInfoModel;
	}

	@Override
	public Date getServerTime() {
		logger.debug("getServerTime() - start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				Date date = client.getService().getServerTime();
				return date;
			}
			else
			{
				logger.debug("getServerTime() - client was null");
			}
		}
		catch (Exception e)
		{
			logger.error(e.toString(), e);
		}
		logger.debug("getServerTime() - end");
		return null;
	}

	@Override
	public long checkDestinationValid(String path) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("checkDestinationValid() - start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				return client.getService().checkDestinationValid(path);
			}
		}
		catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage(), e);
		}
		return -1;
	}

	@Override
	public long getPathMaxLength() throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getPathMaxLength() - start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				return client.getService().getPathMaxLength();
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage(), e);
		}
		return -1;
	}
	@Override
	public List<NewsItemModel> getNews(String url) {
		List<NewsItemModel> list = new ArrayList<NewsItemModel>();
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			URL u = new URL(url);
			Document doc = builder.parse(u.openStream());
			NodeList allItems = doc.getElementsByTagName(XML_ITEM);

			for (int i = 0; i < allItems.getLength(); i++)
			{
				NewsItemModel news = new NewsItemModel();
				Node itemNode = allItems.item(i);

				for (int j = 0; j < itemNode.getChildNodes().getLength(); j++)
				{
					Node childNode = itemNode.getChildNodes().item(j);
					String nodeName = childNode.getNodeName();
					String textContent = childNode.getTextContent();
					if (nodeName.equals(XML_TITLE))
						news.setTitle(textContent);
					else if (nodeName.equals(XML_LINK))
						news.setLink(textContent);
					else if (nodeName.equals(XML_DESCRIPTION))
						news.setDescription(textContent);
				}
				list.add(news);
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage(), e);
		}
		return list;
	}

	@Override
	public AccountModel getAdminAccount() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getAdminAccount() - start");
		WebServiceClientProxy client = this.getServiceClient();
		AccountModel model = new AccountModel();
		if (client != null) {
			try {
				Account account = getServiceClient().getService().getAdminAccount();
				if(account != null)
				{
					model.setUserName(account.getUserName());
					model.setPassword(account.getPassword());
				}
				return model;
			}
			catch (WebServiceException exception) {
				proccessAxisFaultException(exception);
			}
		}
		logger.debug("getAdminAccount() - start");
		return model;
	}

	@Override
	public String getDefaultUser(String protocol,String host, int port) {
		logger.debug("getDefaultUser() - start");
		WebServiceClientProxy client = this.getServiceClient();
		
		try {
			if(host.equals("localhost"))
				host = getLocalhostName();
			if (client == null) {
				client = WebServiceFactory.getFlassService(protocol,host, port);
				logger.debug("client" + client);
			}

			VersionInfo vInfo = client.getService().getVersionInfo();
			if (vInfo != null) {
				String userName = vInfo.getAdminName();
				logger.debug("userName:" + userName);
				return userName;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("getDefaultUser() - end");
		return null;
	}
	
	
	@Override
	public VersionInfoModel getDefaultUserAndBuild(String protocol,String host, int port) {
		logger.debug("getDefaultUser() - start");
		WebServiceClientProxy client = this.getServiceClient();
		
		try {
			if(host.equals("localhost"))
				host = getLocalhostName();
			if (client == null) {
				client = WebServiceFactory.getFlassService(protocol,host, port);
				logger.debug("client" + client);
			}

			VersionInfo vInfo = client.getService().getVersionInfo();
			
			VersionInfoModel vInfomd = this.convertToVersionInfoModel(vInfo);
			if (vInfo != null) {
				String userName = vInfo.getAdminName();
				logger.debug("userName:" + userName);
				vInfomd.setUserName(vInfo.getAdminName());
				return vInfomd;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("getDefaultUser() - end");
		return null;
	}

	@Override
	public PagingLoadResult<GridTreeNode> getPagingGridTreeNode(
			GridTreeNode parent, PagingLoadConfig pageCfg)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger
				.debug("getPagingGridTreeNode(GridTreeNode, PagingLoadConfig) - start");

		try {
			Integer type = parent.getType();
			if (CatalogModelType.rootGRTExchangeTypes.contains(type)
					|| CatalogModelType.allGRTExchangeTypes.contains(type)) {
				return getPagingGRTTreeGridChildren(parent, pageCfg);
			}

			int start = pageCfg.getOffset();
			int size = pageCfg.getLimit();

			String catPath = parent.getCatalogFilePath();
			long parentID = parent.getParentID();
			long subSessionID = parent.getSubSessionID();
//			String temp = catPath.substring(catPath.length()-10, catPath.length());
			String volumeGUID = parent.getGuid();
			String parentpath = parentID == -1? null : parent.getFullPath() + "\\" + parent.getDisplayName();

			logger.debug("start:" + start);
			logger.debug("size:" + size);
			logger.debug("catPath:" + catPath);
			logger.debug("parentID:" + parentID);
			logger.debug("subSessionID:" + subSessionID);

			WebServiceClientProxy client = this.getServiceClient();
			if (client != null) {
				PagedCatalogItem items = client.getFlashServiceR16_5().getPagedCatalogItemsOolong
				(parent.getBackupDestination(), parent.getDestUser(), parent.getDestPwd(), 
						parent.getSessionID(), volumeGUID, 
						catPath, parentID, parentpath, start, size, parent.getEncryptedKey());
				
				CatalogItem[] catalogs = null;
				if(items != null)
					items.setTotal(parent.getChildrenCount());
					catalogs = items.getCaltalogItems();

				CatalogItemModel[] catalogItemModels = null;
				if (catalogs != null) {
					catalogItemModels = new CatalogItemModel[catalogs.length];
					for (int i = 0; i < catalogs.length; i++) {
						catalogItemModels[i] = ConvertToModel(catalogs[i]);
						catalogItemModels[i].setSessionNumber(parent.getSessionID().intValue());
					}
				}

				ArrayList<GridTreeNode> retlst = new ArrayList<GridTreeNode>();

				if (catalogItemModels != null) {
					boolean selectable = true;

					StringBuilder parentPath = new StringBuilder();
					if (parent.getPath() != null
							&& parent.getPath().compareTo(
									RecoveryPointsPanel.GUID_SQLWRITER) == 0) {
						parentPath.append(parent.getPath());

						if (parentID == -1) {
							selectable = false;
						}
					} else {
						if (parent.getFullPath() != null) {
							parentPath.append(parent.getFullPath());
							parentPath.append("\\");
						}
						parentPath.append(parent.getName());
					}
//					String dl = client.getServiceV2().getMntPathFromVolumeGUID(volumeGUID);
					for (int i = 0; i < catalogItemModels.length; i++) {
						GridTreeNode node = ConvertCataLogToTreeModel(
								catalogItemModels[i], catPath, subSessionID,
								parentPath.toString(), selectable, volumeGUID,
								parent.getDestUser(), parent.getDestPwd(), parent.getBackupDestination()); 
						node.setHasDriverLetter(parent.isHasDriverLetter());
						node.setVolumeMountPath(parent.getVolumeMountPath());
						String path = node.getPath();
						if(path != null && volumeGUID!= null && path.startsWith(volumeGUID)
								&& node.getVolumeMountPath() != null) {
							path = path.replace(volumeGUID, node.getVolumeMountPath());
							node.setPath(path);
						}
						retlst.add(node);
					}

					logger.debug("CatalogItemModel length = "
							+ catalogItemModels.length);
				}

				int totalLen = (int) items.getTotal();

				return new BasePagingLoadResult<GridTreeNode>(retlst, start,
						totalLen);
			}

		} catch (WebServiceException ex) {
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}

		logger
				.debug("getPagingGridTreeNode(GridTreeNode, PagingLoadConfig) - end");

		return null;
	}

	private PagingLoadResult<GridTreeNode> getPagingGRTTreeGridChildren(
			GridTreeNode parent, PagingLoadConfig pageCfg){
		int start = pageCfg.getOffset();
		int size = pageCfg.getLimit();
		ArrayList<GridTreeNode> l = new ArrayList<GridTreeNode>();
		int total = 0;

		if (parent != null) {
			Integer type = parent.getType();

			// if this is GRT root node, get the grt catalog path
			if (CatalogModelType.rootGRTExchangeTypes.contains(type)) {

				String dbIdentify = parent.getPath() + "\\"
						+ parent.getComponentName();
				dbIdentify = dbIdentify.substring(dbIdentify.indexOf('\\') + 1);

				String backupDestination = parent.getBackupDestination();
				long sessionID = parent.getSessionID();
				long subSessionID = parent.getSubSessionID();

				String msgCatalogPath = this.getMsgCatalogPath(dbIdentify,
						backupDestination, sessionID, subSessionID);

				if (msgCatalogPath == null || msgCatalogPath.isEmpty()) {
					return new BasePagingLoadResult<GridTreeNode>(l, start,
							total);
				} else {
					parent.setGrtCatalogFile(msgCatalogPath);
				}
			}

			// browse GRT node
			if (CatalogModelType.rootGRTExchangeTypes.contains(type)
					|| CatalogModelType.allGRTExchangeTypes.contains(type)) {

				String msgCatalogPath = parent.getGrtCatalogFile();
				if (!msgCatalogPath.isEmpty()) {

					long lowSelfid = 1; // 1 for the root GRT item
					long highSelfid = 0; // 0 for the root GRT item

					GRTCatalogItemModel currentModel = parent
							.getGrtCatalogItemModel();
					if (currentModel != null) {
						lowSelfid = currentModel.getLowObjSelfid();
						highSelfid = currentModel.getHighObjSelfid();
					}
					WebServiceClientProxy client = this.getServiceClient();
					if (client != null) {
						PagedGRTCatalogItem items = client.getServiceGRT()
								.getPagedGRTCatalogItems(msgCatalogPath,
										lowSelfid, highSelfid, start, size);

						if (items != null) {
							total = (int) items.getTotal();
							boolean selectable = true;
							// TODO: Hack since path we're getting null for all
							// CatalogItem
							// paths
							StringBuilder parentPath = new StringBuilder();
							{
								if (parent.getFullPath() != null) {
									parentPath.append(parent.getFullPath());
									parentPath.append("\\");
								}
								parentPath.append(parent.getName());
							}
							GRTCatalogItem[] grtCataItems = items
									.getGrtCataItems();
							if (grtCataItems != null && grtCataItems.length > 0) {
								GRTCatalogItemModel[] models = new GRTCatalogItemModel[grtCataItems.length];
								for (int i = 0; i < grtCataItems.length; i++) {
									models[i] = ConvertToGRTCatalogItemModel(grtCataItems[i]);
								}
								for (int i = 0; i < models.length; i++) {
									GridTreeNode pagingNode = ConvertGRTCataLogToTreeModel(
											models[i], msgCatalogPath, parent
											.getSubSessionID()
											.longValue(), parentPath
											.toString(), selectable, "");
									
									//pagingNode.setReferNode(parent.getReferNode());									
									l.add(pagingNode);
								}
							}
						}
						logger.debug("GRTCatalogItemModel length = " + total);
					}
				}
			}
		}

		return new BasePagingLoadResult<GridTreeNode>(l, start, total);
	}


	// create the GRTBrowsingContext
	private GRTBrowsingContext composeGRTBrowsingContext(GridTreeNode parentNode, PagingLoadConfig pageCfg, GRTBrowsingContextModel contextModel) 
	{
		
		GRTBrowsingContext context = new GRTBrowsingContext();
		if (parentNode != null)
		{
			// from GridTreeNode
			String msgCatalogPath = parentNode.getGrtCatalogFile();
			long lowSelfid = 1; // 1 for the root GRT item
			long highSelfid = 0; // 0 for the root GRT item

			GRTCatalogItemModel currentModel = parentNode.getGrtCatalogItemModel();
			if (currentModel != null)
			{
				lowSelfid = currentModel.getLowObjSelfid();
				highSelfid = currentModel.getHighObjSelfid();
			}

			context.setCatalogFilePath(msgCatalogPath);
			context.setLselfid(lowSelfid);
			context.setHselfid(highSelfid);
		}
		
		if (pageCfg != null)
		{
			// from the PagingLoadConfig
			context.setRequestStart(pageCfg.getOffset());
			context.setRequestSize(pageCfg.getLimit());

			context.setSortField(pageCfg.getSortField());

			if (pageCfg.getSortDir() != null)
			{
				switch (pageCfg.getSortDir())
				{
				case ASC:
					context.setSortDir(1);
					break;

				case DESC:
					context.setSortDir(-1);
					break;

				case NONE:
				default:
					context.setSortDir(0);
					break;

				};
			}

		}

		if (contextModel != null)
		{
			{
				context.setTotal(contextModel.getTotal());
				context.setTotalWithoutFilter(contextModel.getTotalWithoutFilter());
			}
			
			if (contextModel.getFolderOnly() != null)
			{
				context.setFolderOnly(contextModel.getFolderOnly());
			}

			if (contextModel.getMailOnly() != null)
			{
				context.setMailOnly(contextModel.getMailOnly());
			}

			if (contextModel.getFilterKeyword() != null)
			{
				context.setFilterKeyword(contextModel.getFilterKeyword());
			}

			if (contextModel.getSearchKeyword() != null)
			{
				context.setSearchKeyword(contextModel.getSearchKeyword());
			}
		}

		return context;
	}
	
	// browse grt children
	@SuppressWarnings("unchecked")
	@Override
	public GRTPagingLoadResult browseGRTCatalog(GridTreeNode parent, PagingLoadConfig pageCfg, GRTBrowsingContextModel contextModel)
	throws BusinessLogicException, ServiceConnectException,ServiceInternalException
	{
		logger.debug("begin >>>>>>>>>>");

		try
		{
			int start = pageCfg.getOffset();
			int total = 0;
			long totalWithoutFilter = 0;
			ArrayList<GridTreeNode> l = new ArrayList<GridTreeNode>();		

			if (parent != null)
			{
				Integer type = parent.getType();

				// if this is GRT root node, get the grt catalog path
				if (CatalogModelType.rootGRTExchangeTypes.contains(type))
				{
					String dbIdentify = parent.getPath() + "\\" + parent.getComponentName();
					dbIdentify = dbIdentify.substring(dbIdentify.indexOf('\\') + 1);

					String backupDestination = parent.getBackupDestination();
					long sessionID = parent.getSessionID();
					long subSessionID = parent.getSubSessionID();

					String msgCatalogPath = this.getMsgCatalogPath(dbIdentify, backupDestination, sessionID, subSessionID);

					if (msgCatalogPath == null || msgCatalogPath.isEmpty())
					{
						return new GRTPagingLoadResult(l, start, total, totalWithoutFilter);
					}
					else
					{
						parent.setGrtCatalogFile(msgCatalogPath);
					}
				}

				// browse GRT node
				if (CatalogModelType.rootGRTExchangeTypes.contains(type)
						|| CatalogModelType.allGRTExchangeTypes.contains(type))
				{
					GRTBrowsingContext context = composeGRTBrowsingContext(parent, pageCfg, contextModel);
					
					if (context != null && context.isValid())
					{
						WebServiceClientProxy client = this.getServiceClient();
						if (client != null)
						{						
							logger.debug("input - GRTBrowsingContext: " + context.toLogString());
							PagedGRTCatalogItem items = client.getServiceGRT().browseGRTCatalog(context);
							
							if (items != null)
							{
								total = (int) items.getTotal();
								totalWithoutFilter = items.getTotalWithoutFilter();
								// TODO: Hack since path we're getting null for all
								// CatalogItem
								// paths
								StringBuilder parentPath = new StringBuilder();
								{
									if (parent.getFullPath() != null)
									{
										parentPath.append(parent.getFullPath());
										parentPath.append("\\");
									}
									parentPath.append(parent.getName());
								}
								
								// display path
								StringBuilder parentDisplayPath = new StringBuilder();
								{
									if (parent.getDisplayPath() != null)
									{
										parentDisplayPath.append(parent.getDisplayPath());
										parentDisplayPath.append("\\");
									}
									parentDisplayPath.append(parent.getDisplayName());
								}
								
								GRTCatalogItem[] grtCataItems = items.getGrtCataItems();
								if (grtCataItems != null && grtCataItems.length > 0)
								{
									GRTCatalogItemModel[] models = new GRTCatalogItemModel[grtCataItems.length];
									for (int i = 0; i < grtCataItems.length; i++)
									{
										models[i] = ConvertToGRTCatalogItemModel(grtCataItems[i]);
									}
									
									for (int i = 0; i < models.length; i++)
									{
										if (isNotFolder(context, type, models[i].getObjType().intValue())) {
											continue;
										}
										// If model is calendar or contact type and is not a folder, then it is an item.  
										if ((CatalogModelType.OT_GRT_EXCH_CALENDAR == models[i].getObjType().intValue()
												|| CatalogModelType.OT_GRT_EXCH_CONTACTS == models[i].getObjType().intValue())
												&& !context.isFolderOnly()) {
											models[i].setObjType(models[i].getObjType() + 100L);
										}												
										GridTreeNode pagingNode = ConvertGRTCataLogToTreeModel(models[i], context.getCatalogFilePath(),
												parent.getSubSessionID().longValue(), parentPath.toString(), true, parentDisplayPath.toString());

										l.add(pagingNode);
									}
								}
								
								logger.debug("output - PagedGRTCatalogItem:" + items.toLogString());								
							}
							
						}
					}
				}
			}

			logger.debug("end <<<<<<<<<<");
			return new GRTPagingLoadResult(l, start, total, totalWithoutFilter);

		}
		catch (WebServiceException ex)
		{
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		logger.debug("end <<<<<<<<<<");
		return null;		
	}
	
	private boolean isNotFolder(GRTBrowsingContext context, Integer parentType, int type) {
		if (!context.isFolderOnly() || CatalogModelType.rootGRTExchangeTypes.contains(parentType)) {
			return false;
		}
		if (CatalogModelType.OT_GRT_EXCH_MAILBOX != parentType || CatalogModelType.OT_GRT_EXCH_MESSAGE == type) {
			return true;
		}		
		return false;			
	}	
	
	@Override
	public RecoveryPointModel[] getRecoveryPointsByServerTimeWithFSCatalogStatus(
			String destination, String domain, String userName, String pwd,
			String serverBeginDate, String serverEndDate, boolean isQueryDetail) {
		RecoveryPointModel[] rpms = null;
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				RecoveryPoint[] points = client.getService().getRecoveryPointsByServerTime(destination,
						domain, userName, pwd, serverBeginDate, serverEndDate, isQueryDetail);
				RecoveryPointModel[] t = new RecoveryPointModel[points.length];
				for (int i = 0; i < points.length; i++) {
					t[i] = ConvertToModel(points[i]);
					if(t[i].listOfRecoveryPointItems == null || t[i].listOfRecoveryPointItems.size() == 0) {
						t[i].listOfRecoveryPointItems = getRecoveryPointItems(destination, domain, userName, pwd, t[i].getPath());
						//t[i].listOfCatalogInfo = checkCatalogExist(destination, t[i].getSessionID());
					}
					
					t[i].setFSCatalogStatus(points[i].getFsCatalogStatus());
					if(t[i].listOfRecoveryPointItems == null || t[i].listOfRecoveryPointItems.size()==0){
						t[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
					}
					
					/*if(t[i].listOfCatalogInfo.size() == 0){
						t[i].setFSCatalogStatus(false);
					}else{
						t[i].setFSCatalogStatus(true);
					}
					boolean done = false;
					for(int j = 0; j < t[i].listOfCatalogInfo.size(); j ++) {
						if(done)
							break;
						CatalogInfoModel cm = t[i].listOfCatalogInfo.get(j);
						for(int k = 0; k < t[i].listOfRecoveryPointItems.size(); k ++) {
							RecoveryPointItemModel ri = t[i].listOfRecoveryPointItems.get(k);
							if(ri.getType().equals(STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME) 
									&& ri.getSubSessionID() == cm.getSubSessNo()) {
								if(cm.getFlag() == 0) {
									t[i].setFSCatalogStatus(false);
									done = true;
									break;
								}
							}else if(ri.getType().equals(STRING_RECOVERYPOINT_ITEM_TYPE_APPLICATION))
								break;
						}
					}*/
				}
				logger.debug("getRecoveryPoints() t.length=" + t.length);
				return t;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger
				.debug("getRecoveryPointsByServerTime(String,String, String, String, String,String) start");
		logger.debug("rpms.length:" + (rpms == null ? "null" : rpms.length));
		return rpms;
	}
	
	@Override
	public RecoveryPointModel[] getRecoveryPointsByServerTimeWithFSCatalogStatus(
			String destination, String domain, String userName, String pwd,
			D2DTimeModel serverBeginDate, D2DTimeModel serverEndDate, boolean isQueryDetail) {
		RecoveryPointModel[] rpms = null;
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				RecoveryPoint[] points = client.getService().getRecoveryPointsByServerTime(destination,
						domain, userName, pwd, convertD2DTimeToString(serverBeginDate),
						convertD2DTimeToString(serverEndDate), isQueryDetail);
				RecoveryPointModel[] t = new RecoveryPointModel[points.length];
				for (int i = 0; i < points.length; i++) {
					t[i] = ConvertToModel(points[i]);
					if(t[i].listOfRecoveryPointItems == null || t[i].listOfRecoveryPointItems.size() == 0) {
						t[i].listOfRecoveryPointItems = getRecoveryPointItems(destination, domain, userName, pwd, t[i].getPath());
						//t[i].listOfCatalogInfo = checkCatalogExist(destination, t[i].getSessionID());
					}
					
					t[i].setFSCatalogStatus(points[i].getFsCatalogStatus());
					if(t[i].listOfRecoveryPointItems == null || t[i].listOfRecoveryPointItems.size()==0){
						t[i].setFSCatalogStatus(RestoreConstants.FSCAT_NOTCREATE);
					}
					
					/*if(t[i].listOfCatalogInfo.size() == 0){
						t[i].setFSCatalogStatus(false);
					}else{
						t[i].setFSCatalogStatus(true);
					}
					boolean done = false;
					for(int j = 0; j < t[i].listOfCatalogInfo.size(); j ++) {
						if(done)
							break;
						CatalogInfoModel cm = t[i].listOfCatalogInfo.get(j);
						for(int k = 0; k < t[i].listOfRecoveryPointItems.size(); k ++) {
							RecoveryPointItemModel ri = t[i].listOfRecoveryPointItems.get(k);
							if(ri.getType().equals(STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME) 
									&& ri.getSubSessionID() == cm.getSubSessNo()) {
								if(cm.getFlag() == 0) {
									t[i].setFSCatalogStatus(false);
									done = true;
									break;
								}
							}else if(ri.getType().equals(STRING_RECOVERYPOINT_ITEM_TYPE_APPLICATION))
								break;
						}
					}*/
				}
				logger.debug("getRecoveryPoints() t.length=" + t.length);
				return t;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger
				.debug("getRecoveryPointsByServerTime(String,String, String, String, String,String) start");
		logger.debug("rpms.length:" + (rpms == null ? "null" : rpms.length));
		return rpms;
	}

	
	@Override
	public RecoveryPointModel[] getRecoveryPointsByServerTimeRPSInfo(String destination, String domain, String userName, String pwd,
			String serverBeginDate, String serverEndDate, boolean isQueryDetail, OndemandInfo4RPS rpsInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getRecoveryPointsByServerTimeRPSInfo(String,String, String, String, String,String) start");
		logger.debug("dest:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);
		logger.debug("pwd:***");
		logger.debug("serverBeginDate:" + serverBeginDate);
		logger.debug("serverEndDate:" + serverEndDate);
		logger.debug("isQueryDetail:" + isQueryDetail);


		RecoveryPointModel[] rpms = null;
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				D2DOnRPS d2donRPS = convert2D2DOnRPS(rpsInfo);
				RecoveryPoint[] points = client.getFlashServiceR16_5().getRecoveryPointsByServerTimeRPSInfo(destination,
						domain, userName, pwd, serverBeginDate, serverEndDate, isQueryDetail, d2donRPS);
				RecoveryPointModel[] t = new RecoveryPointModel[points.length];
				for (int i = 0; i < points.length; i++) {
					t[i] = ConvertToModel(points[i]);
				}
				logger.debug("getRecoveryPoints() t.length=" + t.length);
				return t;
			}
		}
		catch (WebServiceException exception)
		{
			proccessAxisFaultException(exception);
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger
				.debug("getRecoveryPointsByServerTime(String,String, String, String, String,String) start");
		logger.debug("rpms.length:" + (rpms == null ? "null" : rpms.length));
		return rpms;
	}
	@Override
	public RecoveryPointModel[] getRecoveryPointsByServerTimeRPSInfo(String destination, String domain, String userName, String pwd,
			D2DTimeModel serverBeginDate, D2DTimeModel serverEndDate, boolean isQueryDetail, OndemandInfo4RPS rpsInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getRecoveryPointsByServerTimeRPSInfo(String,String, String, String, String,String) start");
		logger.debug("dest:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);
		logger.debug("pwd:***");
		logger.debug("serverBeginDate:" + serverBeginDate);
		logger.debug("serverEndDate:" + serverEndDate);
		logger.debug("isQueryDetail:" + isQueryDetail);


		RecoveryPointModel[] rpms = null;
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				D2DOnRPS d2donRPS = convert2D2DOnRPS(rpsInfo);
				RecoveryPoint[] points = client.getFlashServiceR16_5().getRecoveryPointsByServerTimeRPSInfo(destination,
						domain, userName, pwd, convertD2DTimeToString(serverBeginDate),
						convertD2DTimeToString(serverEndDate), isQueryDetail, d2donRPS);
				RecoveryPointModel[] t = new RecoveryPointModel[points.length];
				for (int i = 0; i < points.length; i++) {
					t[i] = ConvertToModel(points[i]);
				}
				logger.debug("getRecoveryPoints() t.length=" + t.length);
				return t;
			}
		}
		catch (WebServiceException exception)
		{
			proccessAxisFaultException(exception);
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger
				.debug("getRecoveryPointsByServerTime(String,String, String, String, String,String) start");
		logger.debug("rpms.length:" + (rpms == null ? "null" : rpms.length));
		return rpms;
	}
	private D2DOnRPS convert2D2DOnRPS(OndemandInfo4RPS rpsInfo) {
		if(rpsInfo == null) return null;
		D2DOnRPS rps = new D2DOnRPS();
		rps.setAgentUUID(rpsInfo.getAgentUUID());
		rps.setDataStoreUUID(rpsInfo.getDataStoreUUID());
		rps.setRpsHost(ConvertDataToModel.convertToData(rpsInfo.rpsHostInfo));
		return rps;
	}

	@Override
	public RecoveryPointModel[] getRecoveryPointsByServerTime(
			String destination, String domain, String userName, String pwd,
			String serverBeginDate, String serverEndDate, boolean isQueryDetail) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger
				.debug("getRecoveryPointsByServerTime(String,String, String, String, String,String) start");
		logger.debug("dest:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);
		logger.debug("pwd:***");
		logger.debug("serverBeginDate:" + serverBeginDate);
		logger.debug("serverEndDate:" + serverEndDate);
		logger.debug("isQueryDetail:" + isQueryDetail);

//		Date beginDate = this.string2Date(serverBeginDate);
//		Date endDate = this.string2Date(serverEndDate);
//		logger.debug("beginDate:" + beginDate);
//		logger.debug("endDate:" + endDate);

		RecoveryPointModel[] rpms = null;
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				RecoveryPoint[] points = client.getService().getRecoveryPointsByServerTime(destination,
						domain, userName, pwd, serverBeginDate, serverEndDate, isQueryDetail);
				RecoveryPointModel[] t = new RecoveryPointModel[points.length];
				for (int i = 0; i < points.length; i++) {
					t[i] = ConvertToModel(points[i]);
				}
				logger.debug("getRecoveryPoints() t.length=" + t.length);
				return t;
			}
		}
		catch (WebServiceException exception)
		{
			proccessAxisFaultException(exception);
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger
				.debug("getRecoveryPointsByServerTime(String,String, String, String, String,String) start");
		logger.debug("rpms.length:" + (rpms == null ? "null" : rpms.length));
		return rpms;
	}
	
	@Override
	public RecoveryPointModel[] getRecoveryPointsByServerTime(
			String destination, String domain, String userName, String pwd,
			D2DTimeModel serverBeginDate, D2DTimeModel serverEndDate, boolean isQueryDetail) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger
				.debug("getRecoveryPointsByServerTime(String,String, String, String, String,String) start");
		logger.debug("dest:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);
		logger.debug("pwd:***");
		logger.debug("serverBeginDate:" + serverBeginDate);
		logger.debug("serverEndDate:" + serverEndDate);
		logger.debug("isQueryDetail:" + isQueryDetail);

//		Date beginDate = this.string2Date(serverBeginDate);
//		Date endDate = this.string2Date(serverEndDate);
//		logger.debug("beginDate:" + beginDate);
//		logger.debug("endDate:" + endDate);

		RecoveryPointModel[] rpms = null;
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {				
				RecoveryPoint[] points = client.getService().getRecoveryPointsByServerTime(destination,
						domain, userName, pwd, convertD2DTimeToString(serverBeginDate),
						convertD2DTimeToString(serverEndDate), isQueryDetail);
				RecoveryPointModel[] t = new RecoveryPointModel[points.length];
				for (int i = 0; i < points.length; i++) {
					t[i] = ConvertToModel(points[i]);
				}
				logger.debug("getRecoveryPoints() t.length=" + t.length);
				return t;
			}
		}
		catch (WebServiceException exception)
		{
			proccessAxisFaultException(exception);
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
		}
		logger
				.debug("getRecoveryPointsByServerTime(String,String, String, String, String,String) start");
		logger.debug("rpms.length:" + (rpms == null ? "null" : rpms.length));
		return rpms;
	}	

	@Override
	public AlternativePathModel[] checkSQLAlternateLocation(String[] basePath,
			String[] instName, String[] dbName)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("checkSQLAlternateLocation(String[],String[], String[], String[]) start");
		logger.debug("basePath:" + StringUtil.convertArray2String(basePath));
		logger.debug("instName:" + StringUtil.convertArray2String(instName));
		logger.debug("dbName:" + StringUtil.convertArray2String(dbName));

		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				AlternativePath[] ret = client.getService().checkSQLAlternateLocation(basePath, instName, dbName);
				AlternativePathModel[] model = convertAlternativePathToModel(ret);
				logger.debug(StringUtil.convertArray2String(model));
				logger.debug("checkSQLAlternateLocation returns value - end");
				return model;

			}
		}catch(WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		logger.debug("checkSQLAlternateLocation returns null - end");
		return null;
	}

	private AlternativePathModel[] convertAlternativePathToModel(
			AlternativePath[] ret) {
		if(ret == null || ret.length == 0)
			return null;

		AlternativePathModel[] model = new AlternativePathModel[ret.length];
		for (int i = 0; i < ret.length; i++) {
			model[i] = new AlternativePathModel();
			model[i].setAlterPath(ret[i].getAlterPath());
			model[i].setMaxPathLength(ret[i].getMaxPathLength());
		}
		return model;
	}

	@Override
	public ApplicationModel[] getExcludedAppComponents(String[] volumes)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getExcludedAppComponents(String[]) start");
		logger.debug("volumes:" + StringUtil.convertArray2String(volumes));
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				ApplicationWriter[] ret = client.getService().getExcludedAppComponents(volumes);
				ApplicationModel[] model = application2Model(ret);
				logger.debug(StringUtil.convertArray2String(model));
				logAppModel(model);
				logger.debug("checkSQLAlternateLocation returns value - end");
				return model;
			}
		}catch(WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
		logger.debug("getExcludedAppComponents returns null - end");
		return null;
	}

	private void logAppModel(ApplicationModel[] model) {
		StringBuilder str = new StringBuilder();
		ApplicationModel[]  result = model;
		if(result != null)
		{
			str.append("fetched application writer number:" + result.length);
			for (int i = 0; i < result.length; i++) {
				str.append("\n number " + i + ":");
				str.append("\n   name:" + result[i].getAppName());
				str.append("\n   affected mnt:");
				String[] mnt = result[i].getAffectedMnt();
				for (int j = 0; j < (mnt == null ? 0 : mnt.length); j++) {
					str.append(mnt[j]).append(",");
				}
				str.append("\n   component:");
				ApplicationComponentModel[] comp = result[i].getComponents();
				for (int k = 0; k < (comp == null ? 0 : comp.length); k++) {
					str.append("\n     comp name:" + comp[k].getName());
					str.append("\n     comp affectd mnt:");
					mnt = comp[k].getAffectedMnt();
					for (int j = 0; j < (mnt == null ? 0 : mnt.length); j++) {
						str.append(mnt[j]).append(",");
					}
					str.append("\n     comp files:");
					mnt = comp[k].getFileList();
					for (int j = 0; j < (mnt == null ? 0 : mnt.length); j++) {
						str.append("\n        " + mnt[j]);
					}
				}
			}
		}
		else
			str.append("fetched application writer number: 0");
		logger.debug(str.toString());
	}

	private ApplicationModel[] application2Model(ApplicationWriter[] ret) {
		ApplicationModel[] appModel = new ApplicationModel[ret.length];
		for (int i = 0; i < ret.length; i++) {
			appModel[i] = new ApplicationModel();
			appModel[i].setAppName(ret[i].getAppName());
			appModel[i].setAffectedMnt(ret[i].getAffectedMnt());
			ApplicationComponent[] components = ret[i].getComponentList();
			if(components != null){
				ApplicationComponentModel[] compModel = new ApplicationComponentModel[components.length];
				for (int j = 0; j < components.length; j++) {
					compModel[j] = new ApplicationComponentModel();
					compModel[j].setName(components[j].getName());
					compModel[j].setAffectedMnt(components[j].getAffectedMnt());
					compModel[j].setFileList(components[j].getFileList());
				}
				appModel[i].setComponents(compModel);
			}

		}
		return appModel;
	}


	@Override
	public List<RecoveryPointItemModel> getRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		logger.debug("getRecoveryPointItems(String, String, String, String, String) - start");
		logger.debug("dest:" + dest);
		logger.debug("domain:" + domain);
		logger.debug("user:" + user);
		logger.debug("pwd:");
		logger.debug("subPath:" + subPath);

		List<RecoveryPointItemModel>  recItemModelList = new ArrayList<RecoveryPointItemModel>();
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				RecoveryPointItem[] recItems = client.getService().getRecoveryPointItems(dest, domain, user, pwd, subPath);
				if (recItems != null){
					Arrays.sort(recItems, new Comparator<RecoveryPointItem>(){

						@Override
						public int compare(RecoveryPointItem arg0, RecoveryPointItem arg1) {
							if(!arg0.getVolumeOrAppType().equals(arg1.getVolumeOrAppType())){
								if (STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME.equals(arg0.getVolumeOrAppType()))
									return -1;
								if (STRING_RECOVERYPOINT_ITEM_TYPE_APPLICATION.equals(arg0.getVolumeOrAppType()))
									return 1;
							}

							return arg0.getDisplayName().compareTo(arg1.getDisplayName());
						}

					});

					for (RecoveryPointItem item:recItems){
						recItemModelList.add(ConvertToModel(item));
					}
				}
			}
		} catch(WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
		logger.debug("getRecoveryPointItems end");
		logger.debug("recItemModelList.size:" + (recItemModelList == null ? "null" : recItemModelList.size()));
		return recItemModelList;
	}

	@Override
	public boolean ValidateServerName(String ServerName)
			throws BusinessLogicException, ServiceInternalException {
		logger.debug("ValidateServerName() - start");
		WebServiceClientProxy client = null;

		boolean ret = false;
			client = this.getServiceClient();
		if (client != null) {
			try {
				ret = client.getServiceV2().ValidateServerName(ServerName);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			logger.debug("ValidateServerName() - client was null");
		}

		logger.debug("ValidateServerName() - end");
		return ret;
	}

	@Override
	public PreferencesModel getPreferences() {
		logger.debug("getPreferences() - start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				PreferencesConfiguration preferencesConfig = client.getServiceV2().getPreferences();

				if (preferencesConfig != null)
				{
					PreferencesModel model = ConvertToPreferencesModel(preferencesConfig);
					return model;
				}
				return null;
			}
			else
			{
				logger.debug("getPreferences() - client was null");
			}
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		
		logger.debug("getPreferences() - end");
		return null;
	}
	
	public PreferencesModel ConvertToPreferencesModel(
			PreferencesConfiguration in_preferencesConfig) {
		PreferencesModel model = new PreferencesModel();
		model.setEmailAlerts(ConvertEmailAlertsConfig(in_preferencesConfig.getEmailAlerts()));
		model.setGeneralSettings(ConvertGeneralSettingsConfig(in_preferencesConfig.getGeneralSettings()));
		model.setupdateSettings(ConvertAutoUpdateSettingsConfig(in_preferencesConfig.getupdateSettings()));
		if(in_preferencesConfig.getDestCapacity() != null)
			model.setdestCapacityModel(convertDestinationInformation(in_preferencesConfig.getDestCapacity()));
		return model;
	}

	private DestinationCapacityModel convertDestinationInformation(DestinationCapacity source){
		if(source == null){
			return null;
		}
		DestinationCapacityModel model = new DestinationCapacityModel();
		model.setCatalogSize(source.getCatalogSize());
		model.setFullBackupSize(source.getFullBackupSize());
		model.setIncrementalBackupSize(source.getIncrementalBackupSize());
		model.setResyncBackupSize(source.getResyncBackupSize());
		model.setTotalFreeSize(source.getTotalFreeSize());
		model.setTotalVolumeSize(source.getTotalVolumeSize());
		return model;
	}

	private UpdateSettingsModel ConvertAutoUpdateSettingsConfig(
			AutoUpdateSettings in_updateSettings) {

		UpdateSettingsModel updateModel = null;

		if(in_updateSettings != null)
		{
			updateModel = new UpdateSettingsModel();
			updateModel.setDownloadServerType(in_updateSettings.getServerType());
			updateModel.setCAServerStatus(in_updateSettings.getiCAServerStatus());
			//if(in_updateSettings.getServerType() == 1)
			//{
			StagingServerSettings[] stagingServers = in_updateSettings.getStagingServers();
			if(stagingServers != null)
			{
				int istagingServersCount = stagingServers.length;
				if(istagingServersCount > 0)
				{
					StagingServerModel[] serverModels = new StagingServerModel[istagingServersCount];
					for(int iIndex = 0;iIndex < istagingServersCount;iIndex++)
					{
						serverModels[iIndex] = new StagingServerModel();
						serverModels[iIndex].setStagingServer(stagingServers[iIndex].getStagingServer());
						serverModels[iIndex].setStagingServerPort(stagingServers[iIndex].getStagingServerPort());
						serverModels[iIndex].setStagingServerStatus(stagingServers[iIndex].getStagingServerStatus());
						serverModels[iIndex].setStagingServerId(stagingServers[iIndex].getServerId());
					}
					updateModel.setStagingServers(serverModels);
				}
			}
			//}

			updateModel.setAutoCheckupdate(in_updateSettings.isScheduleType());
			if(in_updateSettings.isScheduleType())
			{
				updateModel.setScheduledWeekDay(in_updateSettings.getScheduledWeekDay());
				updateModel.setScheduledHour(in_updateSettings.getScheduledHour());
			}

			ProxySettingsModel proxyModel = new ProxySettingsModel();
			ProxySettings proxyConfig = in_updateSettings.getproxySettings();
			proxyModel.setUseProxy(proxyConfig.isUseProxy());
			if(proxyModel.getUseProxy())
			{
				proxyModel.setProxyServerName(proxyConfig.getProxyServerName());
				proxyModel.setProxyPort(proxyConfig.getProxyServerPort());

				proxyModel.setProxyRequiresAuth(proxyConfig.isProxyRequiresAuth());
				if(proxyConfig.isProxyRequiresAuth())
				{
					proxyModel.setProxyUserName(proxyConfig.getProxyUserName());
					proxyModel.setProxyPassword(proxyConfig.getProxyPassword());
				}
			}

			updateModel.setproxySettings(proxyModel);
		}
		return updateModel;
	}

	private GeneralSettingsModel ConvertGeneralSettingsConfig(
			GeneralSettings in_generalSettings) {
		GeneralSettingsModel generalSettingsModel = null;
		if(in_generalSettings != null)
		{
			generalSettingsModel = new GeneralSettingsModel();
			generalSettingsModel.setUseVideos(in_generalSettings.getUseVideos());
			generalSettingsModel.setTrayNotificationType(in_generalSettings.getTrayIconOption());
			generalSettingsModel.setNewsFeed(in_generalSettings.isbNewsFeed());
			generalSettingsModel.setSocialNetworking(in_generalSettings.isbSocialNetworking());
		}
		return generalSettingsModel;
	}

	private EmailAlertsModel ConvertEmailAlertsConfig(BackupEmail in_emailAlerts) {

		EmailAlertsModel emailModel = null;
		if (in_emailAlerts != null)
		{
			emailModel = new EmailAlertsModel();
			emailModel.setEnableSettings(in_emailAlerts.isEnableSettings());
			emailModel.setContent(in_emailAlerts.getContent());
			emailModel.setEnableEmailOnMissedJob(in_emailAlerts.isEnableEmailOnMissedJob());
			emailModel.setEnableEmail(in_emailAlerts.isEnableEmail());
			emailModel.setEnableEmailOnSuccess(in_emailAlerts.isEnableEmailOnSuccess());
			emailModel.setEnableEmailOnMergeFailure(in_emailAlerts.isEnableEmailOnMergeFailure());
			emailModel.setEnableEmailOnMergeSuccess(in_emailAlerts.isEnableEmailOnMergeSuccess());
			emailModel.setEnableEmailOnRecoveryPointCheckFailure(in_emailAlerts.isEnableEmailOnRecoveryPointCheckFailure());
			emailModel.setEnableSpaceNotification(in_emailAlerts.isEnableSpaceNotification());
			emailModel.setSpaceMeasureNum(in_emailAlerts.getSpaceMeasureNum());
			emailModel.setSpaceMeasureUnit(in_emailAlerts.getSpaceMeasureUnit());

			emailModel.setEnableEmailOnNewUpdates(in_emailAlerts.isNotifyOnNewUpdates());
			emailModel.setEnableSrmPkiAlert(in_emailAlerts.isEnableSrmPkiAlert());
			emailModel.setCpuAlertUtilThreshold(in_emailAlerts.getCpuThreshold());
			emailModel.setMemoryAlertUtilThreshold(in_emailAlerts.getMemoryThreshold());
			emailModel.setDiskAlertUtilThreshold(in_emailAlerts.getDiskThreshold());
			emailModel.setNetworkAlertUtilThreshold(in_emailAlerts.getNetworkThreshold());

			emailModel.setEnableProxy(in_emailAlerts.isEnableProxy());
			emailModel.setFromAddress(in_emailAlerts.getFromAddress());
			emailModel.setProxyAddress(in_emailAlerts.getProxyAddress());
			emailModel.setProxyPassword(in_emailAlerts.getProxyPassword());
			emailModel.setProxyPort(in_emailAlerts.getProxyPort());
			emailModel.setProxyUsername(in_emailAlerts.getProxyUsername());
			emailModel.setSubject(in_emailAlerts.getSubject());
			emailModel.setSMTP(in_emailAlerts.getSmtp());
			emailModel.setEnableHTMLFormat(in_emailAlerts.isEnableHTMLFormat());
			/** alert email PR */
			emailModel.setMailPwd(in_emailAlerts.getMailPassword());
			emailModel.setMailService(in_emailAlerts.getMailServiceName());
			emailModel.setEnableSsl(in_emailAlerts.isEnableSsl());
			emailModel.setEnableTls(in_emailAlerts.isEnableTls());
			emailModel.setMailUser(in_emailAlerts.getMailUser());
			emailModel.setSmtpPort(in_emailAlerts.getSmtpPort());
			emailModel.setEnableMailAuth(in_emailAlerts.isMailAuth());
			emailModel.setEnableProxyAuth(in_emailAlerts.isProxyAuth());


			emailModel.Recipients = new ArrayList<String>();
			if (in_emailAlerts.getRecipients() != null)
			{
				for (int i = 0; i < in_emailAlerts.getRecipients().size(); i++)
				{
					emailModel.Recipients.add(in_emailAlerts.getRecipients().get(i));
				}
			}
		}
		return emailModel;
	}

	@Override
	public long savePreferences(PreferencesModel in_preferencesConfig)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		return checkPreferences(in_preferencesConfig, true);
	}
	
	@Override
	public long validatePreferences(PreferencesModel in_preferencesConfig)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		return checkPreferences(in_preferencesConfig, false);
	}
	
	private long checkPreferences(PreferencesModel in_preferencesConfig, boolean isSave)
	throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException {
		
		long ret = -1;

		if(in_preferencesConfig == null)
		{
			return 0;
		}

		logger.debug("checkPreferences() - start");
		WebServiceClientProxy client = null;

		client = this.getServiceClient();
		if(client != null)
		{
			PreferencesConfiguration Config = convertToPreferencesConfiguration(in_preferencesConfig);
			try {
				
				if(isSave) {
					ret = client.getServiceV2().savePreferences(Config);
				}
				else {
					ret = client.getServiceV2().validatePreferences(Config);
				}
				
			}  catch (SOAPFaultException e) {
				this.proccessAxisFaultException(e);
			}
		}
		return ret;
	}

	private PreferencesConfiguration convertToPreferencesConfiguration(
			PreferencesModel in_PreferencesModel) {
		PreferencesConfiguration preferencesConfig = new PreferencesConfiguration();

		preferencesConfig.setEmailAlerts(ConvertEmailAlerts(in_PreferencesModel.getEmailAlerts()));
		preferencesConfig.setGeneralSettings(ConvertGeneralSettings(in_PreferencesModel.getGeneralSettings()));
		preferencesConfig.setupdateSettings(ConvertDataToModel.convertToData(in_PreferencesModel.getupdateSettings()));

		return preferencesConfig;
	}

	private GeneralSettings ConvertGeneralSettings(GeneralSettingsModel generalSettings) {

		GeneralSettings general = new GeneralSettings();
		Boolean enableNewsFeed = generalSettings.getNewsFeed();
		Boolean enableSocialNW = generalSettings.getSocialNetworking();
		int iTrayIconOption = generalSettings.getTrayNotificationType();
		int iHelpOption = generalSettings.getUseVideos();

		if(generalSettings.getNewsFeed() != null)
		{
			general.setbNewsFeed(enableNewsFeed);
		}
		if(enableSocialNW != null)
		{
			general.setbSocialNetworking(enableSocialNW);
		}
		general.setTrayIconOption(iTrayIconOption);
		general.setUseVideos(iHelpOption);

		return general;
	}

	private BackupEmail ConvertEmailSettings(EmailAlertsModel emailAlerts) {
		BackupEmail email = new BackupEmail();

		if (emailAlerts.getContent() != null)
		{
			email.setContent(emailAlerts.getContent());
		}
	
		if (emailAlerts.isEnableProxy() != null)
		{
			email.setEnableProxy(emailAlerts.isEnableProxy());
		}

		if (emailAlerts.getEnableHTMLFormat() != null)
		{
			email.setEnableHTMLFormat(emailAlerts.getEnableHTMLFormat());
		}

		if (emailAlerts.getFromAddress() != null)
		{
			email.setFromAddress(emailAlerts.getFromAddress());
		}

		if (emailAlerts.getProxyAddress() != null)
		{
			email.setProxyAddress(emailAlerts.getProxyAddress());
		}

		if (emailAlerts.getProxyPassword() != null)
		{
			email.setProxyPassword(emailAlerts.getProxyPassword());
		}

		if (emailAlerts.getProxyPort() != null)
		{
			email.setProxyPort(emailAlerts.getProxyPort());
		}

		if (emailAlerts.getProxyUsername() != null)
		{
			email.setProxyUsername(emailAlerts.getProxyUsername());
		}

		if (emailAlerts.getSubject() != null)
		{
			email.setSubject(emailAlerts.getSubject());
		}
		
		/** alert email PR */
		String protocol = this.getThreadLocalRequest().getProtocol();
		protocol = protocol.substring(0,protocol.indexOf("/"));
		protocol = protocol.toLowerCase();
		String host =  this.getThreadLocalRequest().getLocalAddr();
		String port = ""+this.getThreadLocalRequest().getLocalPort();
		email.setUrl(protocol+"://"+host+(port.isEmpty()?"":":"+port)+this.getThreadLocalRequest().getContextPath()+"/");

		if(emailAlerts.getMailService()!=null)
		{
			email.setMailServiceName(emailAlerts.getMailService());
		}

		if(emailAlerts.getMailPwd()!=null)
		{
			email.setMailPassword(emailAlerts.getMailPwd());
		}

		if(emailAlerts.getSmtpPort()!=null)
		{
			email.setSmtpPort(emailAlerts.getSmtpPort());
		}

		if(emailAlerts.isEnableSsl()!=null)
		{
			email.setEnableSsl(emailAlerts.isEnableSsl());
		}

		if(emailAlerts.isEnableTls()!=null)
		{
			email.setEnableTls(emailAlerts.isEnableTls());
		}

		if(emailAlerts.getMailUser()!=null)
		{
			email.setMailUser(emailAlerts.getMailUser());
		}

		if(emailAlerts.isEnableMailAuth()!=null)
		{
			email.setMailAuth(emailAlerts.isEnableMailAuth());
		}

		if(emailAlerts.isEnableProxyAuth()!=null)
		{
			email.setProxyAuth(emailAlerts.isEnableProxyAuth());
		}

		if (emailAlerts.getSMTP() != null)
		{
			email.setSmtp(emailAlerts.getSMTP());
		}

		if (emailAlerts.Recipients != null)
		{
			String[] rec = new String[emailAlerts.Recipients.size()];
			for (int i = 0; i < emailAlerts.Recipients.size(); i++)
			{
				rec[i] = emailAlerts.Recipients.get(i);
			}
			email.setRecipients(emailAlerts.Recipients);
		}
		else
		{
			email.setRecipients(new ArrayList<String>());
		}

		return email;
	}
	
	private BackupEmail ConvertEmailAlerts(EmailAlertsModel emailAlerts) {

		BackupEmail email = ConvertEmailSettings(emailAlerts);

		Boolean enableSettings = emailAlerts.getEnableSettings();
		Boolean enableEmailOnMissedJob = emailAlerts.getEnableEmailOnMissedJob();
		Boolean enableEmail = emailAlerts.getEnableEmail();
		Boolean enableEmailOnSuccess = emailAlerts.getEnableEmailOnSuccess();
		Boolean enableEmailOnMergeFailure = emailAlerts.getEnableEmailOnMergeFailure();
		Boolean enableEmailOnCheckRPSFailure = emailAlerts.getEnableEmailOnRecoveryPointCheckFailure();
		Boolean enableEmailThreshold = emailAlerts.getEnableSpaceNotification();
		Boolean enableEmailOnNewUpdates = emailAlerts.getEnableEmailOnNewUpdates();
		
		email.setEnableSettings(enableSettings);

		if (emailAlerts.getCpuAlertUtilThreshold() != null)
			email.setCpuThreshold(emailAlerts.getCpuAlertUtilThreshold());
		if (emailAlerts.getMemoryAlertUtilThreshold() != null) 
			email.setMemoryThreshold(emailAlerts.getMemoryAlertUtilThreshold());
		if (emailAlerts.getDiskAlertUtilThreshold() != null)
			email.setDiskThreshold(emailAlerts.getDiskAlertUtilThreshold());
		if (emailAlerts.getNetworkAlertUtilThreshold() != null)
			email.setNetworkThreshold(emailAlerts.getNetworkAlertUtilThreshold());

		email.setEnableEmail(enableEmail);
		email.setEnableEmailOnSuccess(enableEmailOnSuccess);
		email.setEnableEmailOnMergeSuccess(emailAlerts.getEnableEmailOnMergeSuccess() != null ? 
				emailAlerts.getEnableEmailOnMergeSuccess() : false);
		
		if (enableEmailOnMergeFailure != null)
		{
			email.setEnableEmailOnMergeFailure(enableEmailOnMergeFailure);
		}
		
		email.setNotifyOnNewUpdates(enableEmailOnNewUpdates);
		email.setEnableSpaceNotification(enableEmailThreshold);
		email.setSpaceMeasureNum(emailAlerts.getSpaceMeasureNum() != null ? emailAlerts.getSpaceMeasureNum() : 0);
		email.setSpaceMeasureUnit(emailAlerts.getSpaceMeasureUnit() != null ? emailAlerts.getSpaceMeasureUnit() : "");
		email.setEnableSrmPkiAlert(emailAlerts.getEnableSrmPkiAlert() != null ? emailAlerts.getEnableSrmPkiAlert() : false);
			
		if((enableEmailOnMissedJob != null ))
		{
			email.setEnableEmailOnMissedJob(enableEmailOnMissedJob);
		}
		if((enableEmailOnCheckRPSFailure != null)){
			email.setEnableEmailOnRecoveryPointCheckFailure(enableEmailOnCheckRPSFailure);//lds
	
		}
		return email;
	}

	@Override
	public UpdateSettingsModel testDownloadServerConnection(UpdateSettingsModel in_testSettings)
			throws BusinessLogicException, ServiceInternalException {

		UpdateSettingsModel result = null;

		try {
			if(in_testSettings != null){
				AutoUpdateSettings ats = ConvertDataToModel.convertToData(in_testSettings);
				if(ats != null) {
					AutoUpdateSettings updateSettingsConfig = this.getServiceClient().getServiceV2().testDownloadServerConnection(ats);
					result = ConvertAutoUpdateSettingsConfig(updateSettingsConfig);
				}
			}
		} catch (Throwable e) {
			logger.debug(e.toString());
			e.printStackTrace();
		}
		return result;
	}

	//added by cliicy.luo 
	@Override
	public UpdateSettingsModel testBIDownloadServerConnection(UpdateSettingsModel in_testSettings)
			throws BusinessLogicException, ServiceInternalException {

		UpdateSettingsModel result = null;

		try {
			if(in_testSettings != null){
				AutoUpdateSettings ats = ConvertDataToModel.convertToData(in_testSettings);
				if(ats != null) {
					AutoUpdateSettings updateSettingsConfig = this.getServiceClient().getServiceV2().testBIDownloadServerConnection(ats);
					result = ConvertAutoUpdateSettingsConfig(updateSettingsConfig);
				}
			}
		} catch (Throwable e) {
			logger.debug(e.toString());
			e.printStackTrace();
		}
		return result;
	}
	//added by cliicy.luo
	
	@Override
	public VSphereBackupSettingModel getVSphereBackupSetting(VirtualCenterNodeModel vcModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getVSphereBackupSetting() - start");
		WebServiceClientProxy client = null;
		VirtualCenter vc = ConvertToVirtualCenter(vcModel);
		if(vc == null){
			return null;
		}
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				VSphereBackupConfiguration bc = client.getServiceV2().getVSphereBackupConfiguration(vc);

				if (bc != null)
				{
					VSphereBackupSettingModel model = ConvertToVSphereBackupSettingModel(bc);
					long offset = client.getServiceV2().getServerTimezoneOffsetByMillis(bc.getBackupStartTime());
					model.setStartTimezoneOffset(offset);
					return model;
				}
				return null;
			}
			else
			{
				logger.debug("getVSphereBackupSetting() - client was null");
			}
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getVSphereBackupSetting() - end");
		return null;
	}

	private VirtualCenter ConvertToVirtualCenter(VirtualCenterNodeModel model){
		if(model == null){
			return null;
		}
		VirtualCenter vc = new VirtualCenter();
		vc.setVcName(model.getName());
		vc.setPassword((String)model.get("password"));
		vc.setUsername((String)model.get("username"));
		vc.setProtocol((String)model.get("protocol"));
		vc.setPort((Integer)model.get("port"));
		return vc;
	}

	private VSphereBackupSettingModel ConvertToVSphereBackupSettingModel(VSphereBackupConfiguration bc) {
		VSphereBackupSettingModel model = new VSphereBackupSettingModel();
		//Convert to Model Object and Return
		model.setDestUserName(bc.getUserName());
		model.setDestPassword(bc.getPassword());
		model.setCommandAfterBackup(bc.getCommandAfterBackup());
		model.setRunCommandEvenFailed(bc.isRunCommandEvenFailed());
		model.setCommandBeforeBackup(bc.getCommandBeforeBackup());
		model.setCommandAfterSnapshot(bc.getCommandAfterSnapshot());
		model.setChangedBackupDest(bc.isChangedBackupDest());
		model.setChangedBackupDestType(bc.getChangedBackupDestType());
		model.setBackupStartTime(bc.getBackupStartTime());
		model.startTime = this.convertToD2DTimeMode(bc.getStartTime());
		model.retentionPolicy = this.convertToRetentionPolicyModel(bc.getRetentionPolicy());
		if(model.retentionPolicy == null) {
			RetentionPolicyModel retention = new RetentionPolicyModel();
			retention.setUseTimeRange(false);
			//wanqi06 add
			retention.setUseBackupSet(false);
			model.retentionPolicy = retention;
		}
		model.retentionPolicy.setRetentionCount(bc.getRetentionCount());
		model.setActionsUserName(bc.getPrePostUserName());
		model.setActionsPassword(bc.getPrePostPassword());

		model.setRetentionCount(bc.getRetentionCount());
		model.setCompressionLevel(bc.getCompressionLevel());
		model.setEnableEncryption(bc.isEnableEncryption());
		model.setPreExitCode(bc.getPreExitCode());
		model.setSkipJob(bc.isSkipJob());
		model.setEnablePreExitCode(bc.isEnablePreExitCode());

		model.setPurgeSQLLogDays(bc.getPurgeSQLLogDays());
		model.setPurgeExchangeLogDays(bc.getPurgeExchangeLogDays());

		model.setAdminUserName(bc.getAdminUserName());
		model.setAdminPassword(bc.getAdminPassword());

		model.setEnableSpaceNotification(bc.isEnableSpaceNotification());
		model.setSpaceMeasureNum(bc.getSpaceMeasureNum());
		model.setSpaceMeasureUnit(bc.getSpaceMeasureUnit());
		model.setSpaceSavedAfterCompression(bc.getSpaceSavedAfterCompression());
		model.setGrowthRate(bc.getGrowthRate());
		model.setGenerateCatalog(bc.isGenerateCatalog());
		if (bc.getEmail() != null)
		{
			model.setContent(bc.getEmail().getContent());
			model.setEnableEmailOnMissedJob(bc.getEmail().isEnableEmailOnMissedJob());
			model.setEnableEmail(bc.getEmail().isEnableEmail());
			model.setEnableEmailOnSuccess(bc.getEmail().isEnableEmailOnSuccess());
			model.setEnableEmailOnMergeFailure(bc.getEmail().isEnableEmailOnMergeFailure());
			model.setEnableEmailOnMergeSuccess(bc.getEmail().isEnableEmailOnMergeSuccess());
			model.setEnableEmailOnRecoveryPointCheckFailure(bc.getEmail().isEnableEmailOnRecoveryPointCheckFailure());
			model.setEnableProxy(bc.getEmail().isEnableProxy());
			model.setFromAddress(bc.getEmail().getFromAddress());
			model.setProxyAddress(bc.getEmail().getProxyAddress());
			model.setProxyPassword(bc.getEmail().getProxyPassword());
			model.setProxyPort(bc.getEmail().getProxyPort());
			model.setProxyUsername(bc.getEmail().getProxyUsername());
			model.setSubject(bc.getEmail().getSubject());
			model.setSMTP(bc.getEmail().getSmtp());
			model.setEnableHTMLFormat(bc.getEmail().isEnableHTMLFormat());
			/** alert email PR */
			model.setMailPwd(bc.getEmail().getMailPassword());
			model.setMailService(bc.getEmail().getMailServiceName());
			model.setEnableSsl(bc.getEmail().isEnableSsl());
			model.setEnableTls(bc.getEmail().isEnableTls());
			model.setMailUser(bc.getEmail().getMailUser());
			model.setSmtpPort(bc.getEmail().getSmtpPort());
			model.setEnableMailAuth(bc.getEmail().isMailAuth());
			model.setEnableProxyAuth(bc.getEmail().isProxyAuth());


			model.Recipients = new ArrayList<String>();
			if (bc.getEmail().getRecipients() != null)
			{
				for (int i = 0; i < bc.getEmail().getRecipientsAsArray().length; i++)
				{
					model.Recipients.add(bc.getEmail().getRecipientsAsArray()[i]);
				}
			}
		}

		if (bc.getIncrementalBackupSchedule() != null)
		{
			model.incrementalSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getIncrementalBackupSchedule());
		}
		if (bc.getFullBackupSchedule() != null)
		{
			model.fullSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getFullBackupSchedule());
		}
		if (bc.getResyncBackupSchedule() != null)
		{
			model.resyncSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getResyncBackupSchedule());
		}
		if(bc.getBackupVMList() != null)
		{
			model.backupVMList = ConvertToBackupVMList(bc.getBackupVMList());
		}
		return model;
	}

	private List<BackupVMModel> ConvertToBackupVMList(BackupVM[] vmList){
		List<BackupVMModel> vmModelList = new ArrayList<BackupVMModel>();
		for(BackupVM vm : vmList){
			BackupVMModel vmModel = ConvertToBackupVMModel(vm);
			vmModelList.add(vmModel);
		}
		return vmModelList;
	}

	private List<BackupVM> ConvertToBackupVMModelList(List<BackupVMModel> vmModelList){
		if(vmModelList==null || vmModelList.size()==0){
			return new ArrayList<BackupVM>();
		}
		List<BackupVM> vmList = new ArrayList<BackupVM>();
		BackupVM vm = null;
		for(BackupVMModel vmModel : vmModelList){
			vm = new BackupVM();
			vm.setDestination(vmModel.getDestination());
			vm.setEsxPassword(vmModel.getEsxPassword());
			vm.setEsxServerName(vmModel.getEsxServerName());
			vm.setEsxUsername(vmModel.getEsxUsername());
			vm.setProtocol(vmModel.getProtocol());
			vm.setPort(vmModel.getPort());
			vm.setPassword(vmModel.getPassword());
			vm.setUsername(vmModel.getUsername());
			vm.setUuid(vmModel.getUUID());
			vm.setInstanceUUID(vmModel.getVmInstanceUUID());
			vm.setVmName(vmModel.getVMName());
			vm.setVmVMX(vmModel.getVmVMX());
			vm.setVmHostName(vmModel.getVmHostName());
			if(vmModel.getDesPassword()!=null&& !vmModel.getDesPassword().trim().equals(""))
				vm.setDesPassword(vmModel.getDesPassword());
			else
				vm.setDesPassword("");
			if(vmModel.getDesUsername()!=null&& !vmModel.getDesUsername().trim().equals(""))
				vm.setDesUsername(vmModel.getDesUsername());
			else
				vm.setDesUsername("");

			vmList.add(vm);
		}
		return vmList;
	}

	@Override
	public long saveVShpereBackupSetting(
			VSphereBackupSettingModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		long ret = -1;
		logger.debug("saveVShpereBackupSetting() - start");
		WebServiceClientProxy client = null;

			client = this.getServiceClient();
		if (client != null) {

			VSphereBackupConfiguration configuration = ConvertToVSphereConfiguration(model);

			BackupSchedule fSched = ConvertToBackupSchedule(model.fullSchedule);
			configuration.setFullBackupSchedule(fSched);

			BackupSchedule iSched = ConvertToBackupSchedule(model.incrementalSchedule);
			configuration.setIncrementalBackupSchedule(iSched);

			BackupSchedule rSced = ConvertToBackupSchedule(model.resyncSchedule);
			configuration.setResyncBackupSchedule(rSced);

			BackupEmail email = ConvertToBackupEmail(model);
			configuration.setEmail(email);
			
			//wanqi06 adv schedule
			AdvanceSchedule advanceSchedule = convertToAdvanceSchedule(model.advanceScheduleModel);
			configuration.setAdvanceSchedule(advanceSchedule);

			VSphereProxy proxy = ConvertToVSphereProxy(model.vSphereProxyModel);
			configuration.setvSphereProxy(proxy);
			if(model.isBackupToRps() == null || !model.isBackupToRps()){
				RetentionPolicy retention = this.convertToRetentionPolicy(model.retentionPolicy);
				configuration.setRetentionPolicy(retention);
			}
			if(model.retentionPolicy != null) {
				configuration.setRetentionCount(model.retentionPolicy.getRetentionCount());
			}
			
			if(model.backupVMList != null && model.backupVMList.size()>0){
				List<BackupVM> vmList = ConvertToBackupVMModelList(model.backupVMList);
				configuration.setBackupVMList(vmList.toArray(new BackupVM[vmList.size()]));
			}
			
			if(model.isBackupToRps() != null && model.isBackupToRps()){
				configuration.setD2dOrRPSDestType(false);
				configuration.setBackupRpsDestSetting(this.convertToRpsSettings(model.rpsDestSettings));
			}

			try {
				client.getServiceV2().saveVSphereBackupConfiguration(configuration);
			} catch (WebServiceException e) {
				logger
				.error("Error occurs during saveBackupConfiguration()...");
				logger.error(e.getMessage());
				proccessAxisFaultException(e);

	}

			if (ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE) - FlashServiceErrorCode.BackupConfig_BASE)) {
				throw this
						.generateException(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE);
			}
			else if(ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING) - FlashServiceErrorCode.BackupConfig_BASE))
				throw generateException(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING);
		} else {
			logger.debug("saveVShpereBackupSetting() - client was null");
		}

		logger.debug("saveVShpereBackupSetting() - end");
		return ret;
	}

	private VSphereBackupConfiguration ConvertToVSphereConfiguration(VSphereBackupSettingModel model)
	{
		VSphereBackupConfiguration bc = new VSphereBackupConfiguration();
		bc.setEmail(new BackupEmail());

		bc.setPrePostPassword(model.getActionsPassword());
		bc.setPrePostUserName(model.getActionsUserName());

		bc.setCommandAfterBackup(model.getCommandAfterBackup());
		bc.setRunCommandEvenFailed(model.getRunCommandEvenFailed());
		bc.setCommandBeforeBackup(model.getCommandBeforeBackup());
		bc.setCommandAfterSnapshot(model.getCommandAfterSnapshot());
		bc.setDestination(model.getDestination());
		bc.setUserName(model.getDestUserName());
		bc.setPassword(model.getDestPassword());
		bc.setRetentionCount(model.getRetentionCount());
		bc.setCompressionLevel(model.getCompressionLevel());
		bc.setEnableEncryption(model.getEnableEncryption());
		bc.setEncryptionAlgorithm(model.getEncryptionAlgorithm());
		bc.setEncryptionKey(model.getEncryptionKey());
		bc.setThrottling(model.getThrottling());
		bc.setChangedBackupDest(model.getChangedBackupDest());
		bc.setChangedBackupDestType(model.getChangedBackupDestType());
		bc.setPurgeSQLLogDays(model.getPurgeSQLLogDays());
		bc.setPurgeExchangeLogDays(model.getPurgeExchangeLogDays());
		bc.setBackupStartTime(model.getBackupStartTime());
		bc.setStartTime(this.covertToD2DTime(model.startTime));
		bc.setAdminUserName(model.getAdminUserName());
		bc.setAdminPassword(model.getAdminPassword());
		bc.setBackupDataFormat(model.getBackupDataFormat());
		//wanqi06 added
		bc.setRetentionPolicy(convertToRetentionPolicy(model.retentionPolicy));
		//
		
		//fanda03 fix 102889
		bc.setPreAllocationBackupSpace(model.getPreAllocationValue());
		//
		bc.setEnableSpaceNotification(model.getEnableSpaceNotification() != null ? model.getEnableSpaceNotification() : false);
		bc.setGenerateCatalog(model.getGenerateCatalog());
		if(model.getSpaceSavedAfterCompression() != null)
			bc.setSpaceSavedAfterCompression(model.getSpaceSavedAfterCompression());
		if(model.getGrowthRate() != null)
			bc.setGrowthRate(model.getGrowthRate());

		if (model.getEnableSpaceNotification()!=null && model.getEnableSpaceNotification() == true){
		      bc.setSpaceMeasureNum(model.getSpaceMeasureNum());
		      bc.setSpaceMeasureUnit(model.getSpaceMeasureUnit());
		}

		if (model.getEnablePreExitCode() != null && model.getEnablePreExitCode() == true)
		{
			bc.setEnablePreExitCode(true);
			if (model.getPreExitCode() != null)
			{
				bc.setPreExitCode(model.getPreExitCode());
			}
			if (model.getSkipJob() != null)
			{
				bc.setSkipJob(model.getSkipJob());
			}
			else
			{
				bc.setSkipJob(false);
			}
		}
		else
		{
			bc.setEnablePreExitCode(false);
		}
		return bc;
	}
	
	private VSphereProxy ConvertToVSphereProxy(VSphereProxyModel model){
		if(model == null){
			return null;
		}
		VSphereProxy proxy = new VSphereProxy();
		proxy.setVSphereProxyName(model.getVSphereProxyName());
		proxy.setVSphereProxyPassword(model.getVSphereProxyPassword());
		proxy.setVSphereProxyPort(model.getvSphereProxyPort());
		proxy.setVSphereProxyProtocol(model.getVSphereProxyProtocol());
		proxy.setVSphereProxyUsername(model.getVSphereProxyUsername());
		return proxy;
	}
	
	private VSphereProxyModel ConvertToVSphereProxyModel(VSphereProxy proxy){
		if (proxy == null){
			return null;
		}
		VSphereProxyModel model = new VSphereProxyModel();
		model.setVSphereProxyName(proxy.getVSphereProxyName());
		model.setVSphereProxyPassword(proxy.getVSphereProxyPassword());
		model.setVSphereProxyPort(proxy.getVSphereProxyPort());
		model.setVSphereProxyProtocol(proxy.getVSphereProxyProtocol());
		model.setVSphereProxyUsername(proxy.getVSphereProxyUsername());
		return model;
	}
	

	@Override
	public List<VMItemModel> getAllVM(VirtualCenterNodeModel vcModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getAllVM() - start");
		WebServiceClientProxy client = null;
		VirtualCenter vc = ConvertToVirtualCenter(vcModel);
		try{
			client = this.getServiceClient();
			if (client != null) {
				VMItem[] items = client.getServiceV2().getAllVM(vc);
				VMItemModel[] itemModels = ConvertToVMItemModel(items);
				if(itemModels!=null)
				return Arrays.asList(itemModels);
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getAllVM() - end");
		return null;

	}

	private VMItemModel[] ConvertToVMItemModel(VMItem[] items){
		if(items==null || items.length==0){
			return null;
		}
		VMItemModel[] itemModels = new VMItemModel[items.length];
		VMItemModel temp = null;
		for(int i = 0 ; i < items.length ;i++){
			VMItem item = items[i];
			temp = new VMItemModel();
			temp.setVmChoose(false);
			temp.setEsxServer(item.getEsxServerName());
			temp.setVmName(item.getVmName());
			temp.setVMUUID(item.getVmUUID());
			temp.setVmVMX(item.getVmVMX());
			temp.setVmHostName(item.getVmHostName());
			itemModels[i] = temp;
		}
		return itemModels;
	}

	@Override
	public String[] getESXServerDataStore(VirtualCenterModel vc,ESXServerModel esxServerModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getESXServerDataStore() - start");
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				String[] result = client.getServiceV2().getESXServerDataStore(ConvertToVirtualCenter(vc), ConvertToESXServer(esxServerModel));
				return result;
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getESXServerDataStore() - end");
		return null;
	}
	@Override
	public VMStorage[] getVMwareDataStore(VirtualCenterModel vc,ESXServerModel esxServerModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getESXServerDataStore() - start");
		WebServiceClientProxy client = null;
		
		try{
			client = this.getServiceClient();
			if (client != null) {
				DataStore[] result = client.getServiceV2().getVMwareDataStore(ConvertToVirtualCenter(vc), ConvertToESXServer(esxServerModel));
				if(result!=null){
					List<VMStorage> vmStorages = new ArrayList<VMStorage>();
					for(int i = 0 ; i < result.length;i++){
						DataStore dataStore = result[i];
						if (dataStore.isAccessible()) {
							vmStorages.add(ConvertToVMStorage(result[i]));
						}
					}
					if (vmStorages.isEmpty()) {
						return null;
					}
					return vmStorages.toArray(new VMStorage[0]);
				}
				
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getESXServerDataStore() - end");
		return null;
	}
	
	@Override
	public long getRecoveryPointItemChildrenCount(GridTreeNode node) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		long ret = 0;
		WebServiceClientProxy client = this.getServiceClient();
		
		if(client != null) {
			try {
				ret = client.getFlashServiceR16_5().getRecoveryPointItemChildrenCountOolong(node.getSessionID(), 
						node.getCatalogFilePath(), node.getGuid(), node.getBackupDestination(),node.getDestUser(), 
						node.getDestPwd(), node.getEncryptedKey());
			}catch(WebServiceException we) {
				this.proccessAxisFaultException(we);
			}catch(Throwable t) {
				logger.error("Failed to get childcount for " + node.getGuid(), t);
			}
			
		}
		return ret;
	}

	@Override
	public long submitFSOndemandCatalg(List<RecoveryPointModel> sessions,
			String dest, String destUserName, String destPassword, 
			String vmInstanceUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		long ret = 0;
		WebServiceClientProxy client = this.getServiceClient();
		
		if(client != null) {
			try {
				if(vmInstanceUUID == null || vmInstanceUUID.isEmpty()) {
					for(RecoveryPointModel model : sessions) {						
						ret = client.getServiceV3().generateCatalogOnDemandEx(newCatalogPara(model.getSessionID(), 
								dest, destUserName, destPassword, model.getFSCatalogStatus()));
					}
				}else {
					for(RecoveryPointModel model : sessions) {
						ret = client.getServiceV3().generateVMCatalogOnDemand(newCatalogPara(model.getSessionID(), 
								dest, destUserName, destPassword, model.getFSCatalogStatus()), vmInstanceUUID);
					}
				}
			} catch(WebServiceException t) {
				proccessAxisFaultException(t);
			}
		}
		return ret;
	}

@Override
	public long submitFSOndemandCatalog(OndemandInfo4RPS rpsDestInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		long ret = 0;
		WebServiceClientProxy client = this.getServiceClient();
		
		if(client != null) {
			try {
				String vmInstanceUUID = rpsDestInfo.getVmInstanceUUID();
				
				String dest = rpsDestInfo.getDest();
				String destUserName = rpsDestInfo.getDestUserName();
				String destPassword = rpsDestInfo.getDestPassword();
				
				if(vmInstanceUUID == null || vmInstanceUUID.isEmpty()) {					
					for(RecoveryPointModel model : rpsDestInfo.sessions) {						
						ret = client.getServiceV3().generateCatalogOnDemandEx(newCatalogParaEx(model.getSessionID(), 
								dest, destUserName, destPassword, model.getFSCatalogStatus(), rpsDestInfo));
					}
				}else {
					for(RecoveryPointModel model :  rpsDestInfo.sessions) {
						ret = client.getServiceV3().generateVMCatalogOnDemand(newCatalogParaEx(model.getSessionID(), 
								dest, destUserName, destPassword, model.getFSCatalogStatus(), rpsDestInfo), vmInstanceUUID);
					}
				}
			} catch(WebServiceException t) {
				proccessAxisFaultException(t);
			}
		}
		return ret;
	}
	
	private CatalogJobPara newCatalogParaEx(int sessionId, String dest, String userName, String password, int currentStatus, OndemandInfo4RPS rpsDestInfo) {
		CatalogJobPara item = newCatalogPara(sessionId, dest, userName, password,  currentStatus);
		
		item.setDataStoreName(rpsDestInfo.getDataStoreName());
		item.setDataStoreUUID(rpsDestInfo.getDataStoreUUID());
		
		item.setRpsServerName(rpsDestInfo.rpsHostInfo.getHostName());
		item.setRpsUserName(rpsDestInfo.rpsHostInfo.getUserName());
		item.setRpsPassword(rpsDestInfo.rpsHostInfo.getPassword());
		item.setRpsPort(rpsDestInfo.rpsHostInfo.getPort());
		item.setRpsHttp(rpsDestInfo.rpsHostInfo.getIsHttpProtocol());
		item.setAgentName(rpsDestInfo.getAgentName());
		item.setAgentUUID(rpsDestInfo.getAgentUUID());
		item.setAgentSID(rpsDestInfo.getAgentSID());
		return item;
	}

	private CatalogJobPara newCatalogPara(int sessionId, String dest, String userName, String password, int currentStatus) {
		CatalogJobPara item = new CatalogJobPara();
		item.setBackupDestination(dest);
		item.setUserName(userName);
		item.setPassword(password);
		item.setSessionNumber(sessionId);
		item.setCurrentCatalogStatus(currentStatus);		
		return item;
	}
	
	private VMStorage ConvertToVMStorage(DataStore dataStore){
		if(dataStore==null){
			return null;
		}
		VMStorage storage = new VMStorage();
		storage.setName(dataStore.getName());
		storage.setId(dataStore.getId());
		storage.setFreeSize(dataStore.getFreeSize());
		storage.setTotalSize(dataStore.getTotalSize());
		storage.setOtherSize(dataStore.getOtherSize());
		storage.setAccessible(dataStore.isAccessible());
		storage.setMoRef(dataStore.getMoRef());
		return storage;
	}
	
	private ESXServer ConvertToESXServer(ESXServerModel esxServerModel){
		if(esxServerModel==null){
			return null;
		}

		ESXServer server = new ESXServer();
		server.setDataCenter(esxServerModel.getDcName());
		server.setEsxName(esxServerModel.getESXName());
		return server;
	}

	@Override
	public List<ESXServerModel> getESXServer(VirtualCenterModel vcModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getESXServer() - start");
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				ESXServer[] result = client.getServiceV2().getESXServers(ConvertToVirtualCenter(vcModel));
				if(result !=null && result.length>0){
					List<ESXServerModel> models = new ArrayList<ESXServerModel>();
					for(ESXServer server:result){
						models.add(ConvertToESXServerModel(server));
					}
					return models;
				}
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getESXServer() - end");
		return null;

	}

	private ESXServerModel ConvertToESXServerModel(ESXServer server){
		if(server == null){
			return null;
		}

		ESXServerModel serverModel = new ESXServerModel();
		serverModel.setDcName(server.getDataCenter());
		serverModel.setESXName(server.getEsxName());
		return serverModel;

	}

	@Override
	public List<BackupVMModel> getBackupVMModelList(String destination,String domain,
			String username, String password) {
		logger.debug("getBackupVMModelList() - start");
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				BackupVM[] result = client.getServiceV2().getBackupVMList(destination,domain, username, password);
				List<BackupVMModel> modelList = new ArrayList<BackupVMModel>();
				if(result!=null && result.length>0){
					for(BackupVM vm : result){
						modelList.add(ConvertToBackupVMModel(vm));
					}

				}
				return modelList;
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getESXServerDataStore() - end");
		return null;
	}

	@Override
	public boolean checkVMDestination(String destination, String domain,
			String username, String password) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("checkVMDestination() - start");
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				return client.getServiceV2().checkVMDestination(destination, domain, username, password);
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("checkVMDestination() - end");
		return false;
	}

	@Override
	public List<DiskModel> getBackupVMDisk(String destination, String subPath,
			String domain, String username, String password)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("checkVMDestination() - start");
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				Disk[] returnDisk = client.getServiceV2().getBackupVMDisk(destination, subPath, domain, username, password);
				if(returnDisk!=null){
					List<DiskModel> diskModelList = new ArrayList<DiskModel>();
					for(Disk disk:returnDisk){
						diskModelList.add(ConvertToDiskModel(disk));
					}
					return diskModelList;
				}
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		return null;
	}

	@Override
	public List<VMItemModel> getVMItem(VirtualCenterModel vc,
			ESXServerModel esxserver) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getVMItem() - start");
		WebServiceClientProxy client = null;
		try{
			client = this.getServiceClient();
			if (client != null) {
				VirtualMachine[] vmList = client.getServiceV2().getVirtualMachines(ConvertToVirtualCenter(vc), ConvertToESXServer(esxserver));
				if(vmList!=null){
					List<VMItemModel> itemList = new ArrayList<VMItemModel>();
					for(VirtualMachine vm:vmList){
						itemList.add(ConvertToVMItemModel(vm,esxserver));
					}
					return itemList;
				}

			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		return null;
	}

	private VMItemModel ConvertToVMItemModel(VirtualMachine vm,ESXServerModel esxServerModel){
		if(vm == null){
			return null;
		}

		VMItemModel temp = new VMItemModel();
		temp.setVmChoose(false);
		temp.setEsxServer(esxServerModel.getESXName());
		temp.setVmName(vm.getVmName());
		temp.setVMUUID(vm.getVmUUID());
		temp.setVmVMX(vm.getVmVMX());
		temp.setVmHostName(vm.getVmHostName());
		temp.setVmInstanceUUID(vm.getVmInstanceUUID());

		return temp;
	}

	@Override
	public int validateVC(VirtualCenterModel vcModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("validateVC() - start");
		WebServiceClientProxy client = null;
		int ret = -1;
		try{
			client = this.getServiceClient();
			if (client != null) {
				ret = client.getServiceV2().validateVC(ConvertToVirtualCenter(vcModel));
			}
		} catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		return ret;
	}

	@Override
	public int submitRecoveryVMJob(RestoreJobModel model) throws BusinessLogicException, ServiceConnectException, ServiceInternalException
	{
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				RestoreJob job = ConvertToRestoreJob(model);
				return client.getServiceV2().submitRecoveryVMJob(job);
			}
		}
		catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		return -1;
	}

	@Override
	public int submitVMCopyJob(CopyJobModel model)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				CopyJob job = ConvertToCopyJob(model);
				return client.getServiceV2().submitVMCopyJob(job);
			}
		} catch (WebServiceException exception) {
			logger.error("Error occurs during submitCopyJob()");
			logger.error(exception);
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return -1;
	}

	private BackupEmail ConvertToBackupEmail(VSphereBackupSettingModel model) {
		BackupEmail email = new BackupEmail();


		Boolean enableEmailOnMissedJob = model.getEnableEmailOnMissedJob();
		Boolean enableEmail = model.getEnableEmail();
		Boolean enableEmailOnSuccess = model.getEnableEmailOnSuccess();
		Boolean eanbleEmailThreshold = model.getEnableSpaceNotification();
		Boolean enableEmailOnHostNotFound = model.getEnableEmailOnHostNotFound();
		Boolean enableEmailOnDataStoreNotEnough = model.getEnableEmailOnDataStoreNotEnough();
		Boolean enableEmailOnLicensefailure = model.getEnableEmailOnLicensefailure();
		Boolean enableEmailOnMergeFailure = model.getEnableEmailOnMergeFailure();
		Boolean enableEmailOnJobQueue = model.getEnableEmailOnJobQueue();
		Boolean enableEmailOnMergeSuccess = model.getEnableEmailOnMergeSuccess();
		Boolean enableEmailOnCheckRPSFailure = model.getEnableEmailOnRecoveryPointCheckFailure();
		
		if ((enableEmail != null && enableEmail == true)
				 || (enableEmailOnSuccess != null && enableEmailOnSuccess == true)
				|| (eanbleEmailThreshold != null && eanbleEmailThreshold == true)
				|| (enableEmailOnMissedJob != null && enableEmailOnMissedJob)
				|| (enableEmailOnHostNotFound != null && enableEmailOnHostNotFound)
				|| (enableEmailOnDataStoreNotEnough !=null && enableEmailOnDataStoreNotEnough)
				|| (enableEmailOnLicensefailure !=null && enableEmailOnLicensefailure)
				|| (enableEmailOnMergeFailure != null && enableEmailOnMergeFailure)
				|| (enableEmailOnJobQueue != null && enableEmailOnJobQueue)
				|| (enableEmailOnMergeSuccess != null && enableEmailOnMergeSuccess)
				|| (enableEmailOnCheckRPSFailure != null && enableEmailOnCheckRPSFailure))
		{
			if (model.getContent() != null)
			{
				email.setContent(model.getContent());
			}
			email.setEnableEmail(enableEmail);
			email.setEnableEmailOnSuccess(enableEmailOnSuccess);

			if (enableEmailOnMergeFailure != null)
			{
				email.setEnableEmailOnMergeFailure(enableEmailOnMergeFailure);
			}
			if (enableEmailOnMergeSuccess != null)
			{
				email.setEnableEmailOnMergeSuccess(enableEmailOnMergeSuccess);
			}
			if((enableEmailOnMissedJob != null ))
			{
				email.setEnableEmailOnMissedJob(enableEmailOnMissedJob);
			}
			if((enableEmailOnCheckRPSFailure != null)){
				email.setEnableEmailOnRecoveryPointCheckFailure(enableEmailOnCheckRPSFailure);
			}
			if((enableEmailOnHostNotFound != null ))
			{
				email.setEnableEmailOnHostNotFound(enableEmailOnHostNotFound);
			}
			if(enableEmailOnJobQueue != null){
				email.setEnableEmailOnJobQueue(enableEmailOnJobQueue);
			}
			if((enableEmailOnDataStoreNotEnough != null ))
			{
				email.setEnableEmailOnDataStoreNotEnough(enableEmailOnDataStoreNotEnough);
			}
			if((enableEmailOnLicensefailure != null ))
			{
				email.setEnableEmailOnLicensefailure(enableEmailOnLicensefailure);
			}

			if (model.isEnableProxy())
			{
				email.setEnableProxy(model.isEnableProxy());
			}

			if (model.getEnableHTMLFormat() != null)
			{
				email.setEnableHTMLFormat(model.getEnableHTMLFormat());
			}

			if (model.getFromAddress() != null)
			{
				email.setFromAddress(model.getFromAddress());
			}

			if (model.getProxyAddress() != null)
			{
				email.setProxyAddress(model.getProxyAddress());
			}

			if (model.getProxyPassword() != null)
			{
				email.setProxyPassword(model.getProxyPassword());
			}

			if (model.getProxyPort() != null)
			{
				email.setProxyPort(model.getProxyPort());
			}

			if (model.getProxyUsername() != null)
			{
				email.setProxyUsername(model.getProxyUsername());
			}

			if (model.getSubject() != null)
			{
				email.setSubject(model.getSubject());
			}
			/** alert email PR */
			String protocol = this.getThreadLocalRequest().getProtocol();
			protocol = protocol.substring(0,protocol.indexOf("/"));
			protocol = protocol.toLowerCase();
			String host =  this.getThreadLocalRequest().getLocalAddr();
			String port = ""+this.getThreadLocalRequest().getLocalPort();
			email.setUrl(protocol+"://"+host+(port.isEmpty()?"":":"+port)+this.getThreadLocalRequest().getContextPath()+"/");

			if(model.getMailService()!=null)
			{
				email.setMailServiceName(model.getMailService());
			}

			if(model.getMailPwd()!=null)
			{
				email.setMailPassword(model.getMailPwd());
			}

			if(model.getSmtpPort()!=null)
			{
				email.setSmtpPort(model.getSmtpPort());

			}

			if(model.isEnableSsl()!=null)
			{
				email.setEnableSsl(model.isEnableSsl());
			}

			if(model.isEnableTls()!=null)
			{
				email.setEnableTls(model.isEnableTls());
			}

			if(model.getMailUser()!=null)
			{
				email.setMailUser(model.getMailUser());
			}

			if(model.isEnableMailAuth()!=null)
			{
				email.setMailAuth(model.isEnableMailAuth());
			}

			if(model.isEnableProxyAuth()!=null)
			{
				email.setProxyAuth(model.isEnableProxyAuth());
			}

			if (model.getSMTP() != null)
			{
				email.setSmtp(model.getSMTP());
			}

			if (model.Recipients != null)
			{
				String[] rec = new String[model.Recipients.size()];
				for (int i = 0; i < model.Recipients.size(); i++)
				{
					rec[i] = model.Recipients.get(i);
				}
				email.setRecipients(model.Recipients);
			}
			else
			{
				email.setRecipients(new ArrayList<String>(0));
			}

		}
		else
		{
			email.setEnableEmail(false);
			email.setEnableEmailOnSuccess(false);
			email.setEnableEmailOnMergeFailure(false);
			email.setEnableEmailOnMergeSuccess(false);
		}

		return email;
	}

	private VirtualCenter ConvertToVirtualCenter(VirtualCenterModel vcModel){
		if(vcModel ==null){
			return null;
		}
		VirtualCenter vc = new VirtualCenter();
		vc.setPassword(vcModel.getPassword()==null?"":vcModel.getPassword());
		vc.setPort(vcModel.getPort());
		vc.setUsername(vcModel.getUsername());
		vc.setProtocol(vcModel.getProtocol());
		vc.setVcName(vcModel.getVcName());
		return vc;
	}

	private BackupVMModel ConvertToBackupVMModel(BackupVM vm){
		BackupVMModel vmModel = new BackupVMModel();
		vmModel.setDesPassword(vm.getDesPassword());
		vmModel.setDestination(vm.getDestination());
		vmModel.setBrowseDestination(vm.getBrowseDestination());
		vmModel.setDesUsername(vm.getDesUsername());
		vmModel.setEsxPassword(vm.getEsxPassword());
		vmModel.setEsxServerName(vm.getEsxServerName());
		vmModel.setEsxUsername(vm.getEsxUsername());
		vmModel.setPassword(vm.getPassword());
		vmModel.setPort(vm.getPort());
		vmModel.setProtocol(vm.getProtocol());
		vmModel.setUsername(vm.getUsername());
		vmModel.setUUID(vm.getUuid());
		vmModel.setVmInstanceUUID(vm.getInstanceUUID());
		vmModel.setVmHostName(vm.getVmHostName());
		vmModel.setVMName(vm.getVmName());
		vmModel.setVmVMX(vm.getVmVMX());
		vmModel.setVMType(vm.getVmType());
		if(vm.getDisks() != null && vm.getDisks().length > 0) {
			List<DiskModel> diskModelList = new ArrayList<DiskModel>();
			for(Disk disk : vm.getDisks()){
				diskModelList.add(ConvertToDiskModel(disk));
			}
			vmModel.diskList = diskModelList;
		}
		if(vm.getVAppMemberVMs() != null && vm.getVAppMemberVMs().length > 0) {
			for(BackupVM tempVM : vm.getVAppMemberVMs()) {
				BackupVMModel tempModel = ConvertToBackupVMModel(tempVM);
				vmModel.memberVMList.add(tempModel);
			}
		}
		
		vmModel.setCPUCount(vm.getCpuCount());
		vmModel.setMaxCPUCount(vm.getMaxCpuCount());
		vmModel.setMemorySize(vm.getMemorySize());
		vmModel.setMaxMemorySizeGB(vm.getMaxMemorySizeGB());
		vmModel.setStorageProfileId(vm.getStoragePolicyId());
		vmModel.setStorageProfileName(vm.getStoragePolicyName());
		vmModel.setVirtualDataCenterId(vm.getVirtualDataCenterId());
		vmModel.setVirtualDataCenterName(vm.getVirtualDataCenterName());
		vmModel.setVMXDataStoreId(vm.getVmxDataStoreId());
		vmModel.setVMXDataStoreName(vm.getVmxDataStoreName());
		vmModel.setVmIdInVApp(vm.getVmIdInVApp());
		
		return vmModel;
	}
	
	@SuppressWarnings("unused")
	private BackupVMOriginalInfoModel ConvertToBackupVMOrigianlInfoModel(BackupVMOriginalInfo model){
		if(model == null){
			return null;
		}
		
		BackupVMOriginalInfoModel originalInfo = new BackupVMOriginalInfoModel();
		originalInfo.setOriginalEsx(model.getOriginalEsxServer());
		originalInfo.setOriginalResourcePool(model.getOriginalResourcePool());
		originalInfo.setOriginalVcName(model.getOriginalVcName());
		return originalInfo;
	}
	
	private BackupVM ConvertToBackupVM(BackupVMModel vmModel){
		BackupVM vm = new BackupVM();
		vm.setDesPassword(vmModel.getDesPassword());
		vm.setDestination(vmModel.getDestination());
		vm.setBrowseDestination(vmModel.getBrowseDestination());
		vm.setDesUsername(vmModel.getDesUsername());
		vm.setEsxPassword(vmModel.getEsxPassword());
		vm.setEsxServerName(vmModel.getEsxServerName());
		vm.setEsxUsername(vmModel.getEsxUsername());
		vm.setPassword(vmModel.getPassword());
		vm.setPort(vmModel.getPort());
		vm.setProtocol(vmModel.getProtocol());
		vm.setUsername(vmModel.getUsername());
		vm.setUuid(vmModel.getUUID());
		vm.setInstanceUUID(vmModel.getVmInstanceUUID());
		vm.setVmHostName(vmModel.getVmHostName());
		vm.setVmName(vmModel.getVMName());
		vm.setVmVMX(vmModel.getVmVMX());
		return vm;
	}
	
	@SuppressWarnings("unused")
	private BackupVMOriginalInfo ConvertToBackupVMOrigianlInfo(BackupVMOriginalInfoModel model){
		if(model == null){
			return null;
		}
		
		BackupVMOriginalInfo originalInfo = new BackupVMOriginalInfo();
		originalInfo.setOriginalEsxServer(model.getOriginalEsx());
		originalInfo.setOriginalResourcePool(model.getOriginalResourcePool());
		originalInfo.setOriginalVcName(model.getOriginalVcName());
		return originalInfo;
		
	}

	private DiskModel ConvertToDiskModel(Disk disk){
		if(disk==null){
			return null;
		}
		DiskModel diskModel = new DiskModel();
		diskModel.setControllerType(disk.getControllerType());
		diskModel.setDiskNumber(disk.getDiskNumber());
		diskModel.setDiskType(disk.getDiskType());
		diskModel.setPartitionType(disk.getPartitionType());
		diskModel.setSignature(disk.getSignature());
		diskModel.setSize(disk.getSize());
		diskModel.setDiskUrl(disk.getDiskUrl());
		diskModel.setDiskDataStore(disk.getDiskDataStore());
		if(disk.getVolumes()!=null&&disk.getVolumes().length>0){
			List<VMVolumeModel> volumeModelList = new ArrayList<VMVolumeModel>();
			for(com.ca.arcflash.webservice.data.vsphere.Volume volume : disk.getVolumes()){
				volumeModelList.add(ConvertToVMVolumeModel(volume));
			}
			diskModel.volumeModelList = volumeModelList;
		}
		return diskModel;
	}

	private VMVolumeModel ConvertToVMVolumeModel(com.ca.arcflash.webservice.data.vsphere.Volume volume){
		if(volume==null){
			return null;
		}
		VMVolumeModel volumeModel = new VMVolumeModel();
		volumeModel.setDriveLetter(volume.getDriverLetter());
		volumeModel.setVolumeID(volume.getVolumeID());
		return volumeModel;
	}

	@Override
	public VMBackupSettingModel getVMBackupConfiguration(BackupVMModel vmModel) {
		logger.debug("getVMBackupConfiguration() - start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				VMBackupConfiguration bc = client.getServiceV2().getVMBackupConfiguration(ConvertToVirtualMachine(vmModel));

				if (bc != null)
				{
					VMBackupSettingModel model = ConvertToVMBackupSettingModel(bc);
					long offSet = client.getServiceV2().getServerTimezoneOffsetByMillis(bc.getBackupStartTime());
					model.setStartTimezoneOffset(offSet);
					return model;
				}
				return null;
			}
			else
			{
				logger.error("getVMBackupConfiguration() - client was null");
			}
		}
		catch (Exception e)
		{
			logger.error("can't get VM configuration", e);
		}
		logger.debug("getVMBackupConfiguration() - end");
		return null;
	}

	private VirtualMachine ConvertToVirtualMachine(BackupVMModel vmModel){
		if(vmModel == null){
			return null;
		}
		VirtualMachine vm = new VirtualMachine();
		vm.setVmHostName(vmModel.getVmHostName());
		vm.setVmName(vmModel.getVMName());
		vm.setVmUUID(vmModel.getUUID());
		vm.setVmInstanceUUID(vmModel.getVmInstanceUUID());
		return vm;
	}

	public VMBackupSettingModel ConvertToVMBackupSettingModel(VMBackupConfiguration bc){
		VMBackupSettingModel model = new VMBackupSettingModel();
		//Convert to Model Object and Return
		model.setGenerateType(bc.getGenerateType());
		model.setDestination(bc.getDestination());
		model.setDestUserName(bc.getUserName());
		model.setDestPassword(bc.getPassword());
		model.setCommandAfterBackup(bc.getCommandAfterBackup());
		model.setRunCommandEvenFailed(bc.isRunCommandEvenFailed());
		model.setCommandBeforeBackup(bc.getCommandBeforeBackup());
		model.setCommandAfterSnapshot(bc.getCommandAfterSnapshot());
		model.setChangedBackupDest(bc.isChangedBackupDest());
		model.setChangedBackupDestType(bc.getChangedBackupDestType());
		model.setBackupStartTime(bc.getBackupStartTime());
		model.setBackupDataFormat(bc.getBackupDataFormat());
		model.startTime = this.convertToD2DTimeMode(bc.getStartTime());
		model.retentionPolicy = this.convertToRetentionPolicyModel(bc.getRetentionPolicy());
		if(model.retentionPolicy == null) {
			RetentionPolicyModel retention = new RetentionPolicyModel();
			retention.setUseTimeRange(false);
			retention.setUseBackupSet(false);
			//
			model.retentionPolicy = retention;
		}
		model.retentionPolicy.setRetentionCount(bc.getRetentionCount());
		model.setActionsUserName(bc.getPrePostUserName());
		model.setActionsPassword(bc.getPrePostPassword());
		
		model.setThrottling(bc.getThrottling());
		model.setEncryptionAlgorithm(bc.getEncryptionAlgorithm());
		model.setEncryptionKey(bc.getEncryptionKey());

		model.setRetentionCount(bc.getRetentionCount());
		model.setCompressionLevel(bc.getCompressionLevel());
		model.setEnableEncryption(bc.isEnableEncryption());
		model.setPreExitCode(bc.getPreExitCode());
		model.setSkipJob(bc.isSkipJob());
		model.setEnablePreExitCode(bc.isEnablePreExitCode());

		model.setPurgeSQLLogDays(bc.getPurgeSQLLogDays());
		model.setPurgeExchangeLogDays(bc.getPurgeExchangeLogDays());

		model.setAdminUserName(bc.getAdminUserName());
		model.setAdminPassword(bc.getAdminPassword());

		model.setEnableSpaceNotification(bc.isEnableSpaceNotification());
		model.setSpaceMeasureNum(bc.getSpaceMeasureNum());
		model.setSpaceMeasureUnit(bc.getSpaceMeasureUnit());
		model.setSpaceSavedAfterCompression(bc.getSpaceSavedAfterCompression());
		model.setGrowthRate(bc.getGrowthRate());
		model.setGenerateCatalog(bc.isGenerateCatalog());
		model.setExchangeGRTSetting(bc.getExchangeGRTSetting());
		
		///fanda03 fix 102889
		model.setPreAllocationValue( bc.getPreAllocationBackupSpace());
		//
		if (bc.getEmail() != null)
		{
			model.setContent(bc.getEmail().getContent());
			model.setEnableEmailOnMissedJob(bc.getEmail().isEnableEmailOnMissedJob());
			model.setEnableEmail(bc.getEmail().isEnableEmail());
			model.setEnableEmailOnSuccess(bc.getEmail().isEnableEmailOnSuccess());
			model.setEnableEmailOnMergeFailure(bc.getEmail().isEnableEmailOnMergeFailure());
			model.setEnableEmailOnMergeSuccess(bc.getEmail().isEnableEmailOnMergeSuccess());
			model.setEnableEmailOnRecoveryPointCheckFailure(bc.getEmail().isEnableEmailOnRecoveryPointCheckFailure());
			model.setEnableEmailOnDataStoreNotEnough(bc.getEmail().isEnableEmailOnDataStoreNotEnough());
			model.setEnableEmailOnHostNotFound(bc.getEmail().isEnableEmailOnHostNotFound());
			model.setEnableEmailOnJobQueue(bc.getEmail().isEnableEmailOnJobQueue());
			model.setEnableEmailOnLicensefailure(bc.getEmail().isEnableEmailOnLicensefailure());
			model.setEnableProxy(bc.getEmail().isEnableProxy());
			model.setFromAddress(bc.getEmail().getFromAddress());
			model.setProxyAddress(bc.getEmail().getProxyAddress());
			model.setProxyPassword(bc.getEmail().getProxyPassword());
			model.setProxyPort(bc.getEmail().getProxyPort());
			model.setProxyUsername(bc.getEmail().getProxyUsername());
			model.setSubject(bc.getEmail().getSubject());
			model.setSMTP(bc.getEmail().getSmtp());
			model.setEnableHTMLFormat(bc.getEmail().isEnableHTMLFormat());
			/** alert email PR */
			model.setMailPwd(bc.getEmail().getMailPassword());
			model.setMailService(bc.getEmail().getMailServiceName());
			model.setEnableSsl(bc.getEmail().isEnableSsl());
			model.setEnableTls(bc.getEmail().isEnableTls());
			model.setMailUser(bc.getEmail().getMailUser());
			model.setSmtpPort(bc.getEmail().getSmtpPort());
			model.setEnableMailAuth(bc.getEmail().isMailAuth());
			model.setEnableProxyAuth(bc.getEmail().isProxyAuth());


			model.Recipients = new ArrayList<String>();
			if (bc.getEmail().getRecipients() != null)
			{
				for (int i = 0; i < bc.getEmail().getRecipientsAsArray().length; i++)
				{
					model.Recipients.add(bc.getEmail().getRecipientsAsArray()[i]);
				}
			}
		}

		if (bc.getIncrementalBackupSchedule() != null)
		{
			model.incrementalSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getIncrementalBackupSchedule());
		}
		if (bc.getFullBackupSchedule() != null)
		{
			model.fullSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getFullBackupSchedule());
		}
		if (bc.getResyncBackupSchedule() != null)
		{
			model.resyncSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getResyncBackupSchedule());
		}
		if(bc.getBackupVM() != null)
		{
			model.backupVM = ConvertToBackupVMModel(bc.getBackupVM());
		}
		if(bc.getVSphereProxy() != null){
			model.vSphereProxyModel = ConvertToVSphereProxyModel(bc.getVSphereProxy());
		}
		
		//wanqi06
		if(bc.getAdvanceSchedule()!=null){
			model.advanceScheduleModel = ConvertDataToModel.convertToAdvanceScheduleModel(bc.getAdvanceSchedule());
		}

		model.setBackupToRps(!bc.isD2dOrRPSDestType());
		if(!bc.isD2dOrRPSDestType() 
				&& bc.getBackupRpsDestSetting() != null 
				&& bc.getBackupRpsDestSetting().getRpsHost() != null){
			BackupRPSDestSetting backupRpsDestSetting = bc.getBackupRpsDestSetting();	
			model.rpsDestSettings = convertToRpsSettingsModel(backupRpsDestSetting);
		}
		
		if (bc.getScheduledExportConfiguration()!=null){
			model.scheduledExportSettingsModel = this.convertToModel(bc.getScheduledExportConfiguration());
		}
		model.setVmwareTransportModes(bc.getVmwareTransports());
		
		model.setCheckRecoveryPoint(bc.isCheckRecoveryPoint());
		
		model.setVmwareQuiescenceMethod(bc.getVmwareQuiescenceMethod());
		
		model.setHyperVConsistentSnapshotType(bc.getHyperVConsistentSnapshotType());
		
		model.setHyperVSnapshotSeparationIndividually(bc.isHyperVSnapshotSeparationIndividually());

		return model;
	}


	public GRTCatalogItemModel[] getGRTCatalogItems(String catalogFilePath,
			long lowSelfid, long highSelfid) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getRecoveryPoints() start");
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				GRTCatalogItem[] items = client.getServiceGRT().getGRTCatalogItems(
						catalogFilePath, lowSelfid, highSelfid);
				GRTCatalogItemModel[] models = new GRTCatalogItemModel[items.length];
				for (int i = 0; i < items.length; i++) {
					models[i] = ConvertToGRTCatalogItemModel(items[i]);
				}
				return models;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());

		}
		return null;
	}

	private GRTCatalogItemModel ConvertToGRTCatalogItemModel(
			GRTCatalogItem grtItem) {
		GRTCatalogItemModel m = new GRTCatalogItemModel();
		m.setCp_Flag(grtItem.getCp_Flag());
		m.setHighObjBody(grtItem.getHighObjBody());
		m.setHighObjParentid(grtItem.getHighObjParentid());
		m.setHighObjSelfid(grtItem.getHighObjSelfid());
		m.setHighObjSize(grtItem.getHighObjSize());
		m.setLowObjBody(grtItem.getLowObjBody());
		m.setLowObjParentid(grtItem.getLowObjParentid());
		m.setLowObjSelfid(grtItem.getLowObjSelfid());
		m.setLowObjSize(grtItem.getLowObjSize());
		m.setObjDate(grtItem.getObjDate());
		m.setObjFlags(grtItem.getObjFlags());
		m.setObjInfo(grtItem.getObjInfo());
		m.setObjName(Base64.decode(grtItem.getObjName()));
		m.setObjType(grtItem.getObjType());		
		m.setChildrenCount(grtItem.getChildrenCount());
		
		m.setSender(grtItem.getSender());
		m.setReceiver(grtItem.getReceiver());
		m.setSentTime(grtItem.getSentTime());
		m.setSendTZOffset(grtItem.getSendTZOffset());
		m.setReceivedTime(grtItem.getReceivedTime());
		m.setReceivedTZOffset(grtItem.getReceivedTZOffset());
		m.setFlag(grtItem.getFlag());
		m.setItemSize(grtItem.getItemSize());
		return m;
	}

    public String getMsgCatalogPath(String dbIdentify, String backupDestination,
			long sessionNumber, long subSessionNumber)
	{
    	String strLog = "";
    	//strLog += " dbIdentify=" + dbIdentify;
    	strLog += " sessionNumber=" + sessionNumber;
    	strLog += " subSessionNumber=" + subSessionNumber;
    	strLog += " backupDestination=" + backupDestination;
		logger.debug("getMsgCatalogPath() start. " + strLog);
		
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				String ret = client.getServiceGRT().getMsgCatalogPath(dbIdentify, backupDestination,
						sessionNumber, subSessionNumber);
		
				logger.debug("getMsgCatalogPath() end " + ret);
				return ret;
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
			
		}
		return "";
	}
    
    public long d2dExCheckUser(String domain, String user, String password)
	{
		logger.debug("d2dExCheckUser() start");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				long ret = client.getServiceGRT().d2dExCheckUser(domain, user, password);
		
				return ret;
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
			
		}
		
		logger.debug("d2dExCheckUser() end");
		return -1;
}
    
    @Override
    public List<ExchangeDiscoveryModel> getTreeExchangeChildren(ExchangeDiscoveryModel loadConfig, String strUser, String strPassword) throws BusinessLogicException,
	ServiceConnectException, ServiceInternalException  {
    	logger.debug("getTreeExchangeChildren() start");
    	ArrayList<ExchangeDiscoveryModel> arrayModels = new ArrayList<ExchangeDiscoveryModel>();    	
    	
//    	if (loadConfig == null)
//    	{
//    		ExchangeDiscoveryModel root = new ExchangeDiscoveryModel();
//    		root.setType(ExchangeDiscoveryItem.EXCH_DISC_TYPE_ORGANIZATION);
//    		root.setName("Exchange Organization"); 
//    		root.setPath("");
//    		arrayModels.add(root);
//    	}
//    	else
    	{    		
    		WebServiceClientProxy client = null;
    		try {
    			client = this.getServiceClient();
    			if (client != null) {
    				
    				ExchangeDiscoveryItem parentItem = convertModel2Item(loadConfig);
    				
    				ExchangeDiscoveryItem[] items = client.getServiceGRT().getTreeExchangeChildren(parentItem, strUser, strPassword);    				
    				
    				String path = ""; // Exchange Organization's path is empty
    				if (loadConfig != null)
    				{
    					path = loadConfig.getPath() + "\\" + loadConfig.getName();
    				}    				
    				
    				for (int i = 0; i < items.length; i++) {
    					ExchangeDiscoveryModel model = convertItem2Model(items[i]);
    					model.setPath(path);
    					arrayModels.add(model);
    				}    				
    			}
    		} catch (Exception e) {
    			logger.debug(e.getMessage());
    		}    		
    	}   	
    	
    	logger.debug("getTreeExchangeChildren() end");
    	return arrayModels;
    }
    
	@Override
	public PagingLoadResult<ExchangeDiscoveryModel> getPagingTreeExchangeChildren(ExchangeDiscoveryModel loadConfig,
			PagingLoadConfig pageCfg, String strUser, String strPassword) throws BusinessLogicException, ServiceConnectException, ServiceInternalException
	{
		logger.debug("getPagingTreeExchangeChildren() start");
		ArrayList<ExchangeDiscoveryModel> arrayModels = new ArrayList<ExchangeDiscoveryModel>();
		int start = pageCfg.getOffset();
		int size = pageCfg.getLimit();
		int total = 0;

//		if (loadConfig == null)
//		{
//			ExchangeDiscoveryModel root = new ExchangeDiscoveryModel();
//			root.setType(ExchangeDiscoveryItem.EXCH_DISC_TYPE_ORGANIZATION);
//			root.setName("Exchange Organization");
//			root.setPath("");
//			arrayModels.add(root);
//			start = 0;
//			total = 1;
//		}
//		else
		{
			WebServiceClientProxy client = null;
			try
			{
				client = this.getServiceClient();
				if (client != null)
				{
					ExchangeDiscoveryItem parentItem = convertModel2Item(loadConfig);

					PagedExchangeDiscoveryItem items = client.getServiceGRT().getPagedTreeExchangeChildren(parentItem, start, size, strUser, strPassword);
					
					if (items != null && items.getExchangeDiscoveryItems() != null)
					{
						ExchangeDiscoveryItem[] pagedItems = items.getExchangeDiscoveryItems();
						
						String path = ""; // Exchange Organization's path is empty
	    				if (loadConfig != null)
	    				{
	    					path = loadConfig.getPath() + "\\" + loadConfig.getName();
	    				}   
						
						for (int i = 0; i < pagedItems.length; i++)
						{
							ExchangeDiscoveryModel model = convertItem2Model(pagedItems[i]);
							model.setPath(path);
							arrayModels.add(model);
						}
						
						total = (int) items.getTotal();
					}					
				}
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
		}

		logger.debug("getPagingTreeExchangeChildren() end");
		return new BasePagingLoadResult<ExchangeDiscoveryModel>(arrayModels, start, total);
	}
    
	public static ExchangeDiscoveryItem convertModel2Item(ExchangeDiscoveryModel model)
	{
		ExchangeDiscoveryItem item = null;
		
		if (model != null)
		{
			item = new ExchangeDiscoveryItem();
			item.setName(model.getName());
			item.setType(model.getType());
			
			if (model.getDN() != null)
			{
				item.setPwszDN(model.getDN());
			}
			
			if (model.getExVersion() != null)
			{
				item.setnExVersion(model.getExVersion());
			}			
}
		
		return item;
	}
	
	public static ExchangeDiscoveryModel convertItem2Model(ExchangeDiscoveryItem item)
	{
		ExchangeDiscoveryModel model = null;
		
		if (item != null)
		{
			model = new ExchangeDiscoveryModel();
			model.setName(item.getName());
			model.setType(item.getType());
			model.setDN(item.getPwszDN());
			model.setExVersion(item.getnExVersion());
		}
		
		return model;
	}
	
	private CatalogJobPara convertToCatalogJobPara(CatalogJobParaModel model)
	{
		CatalogJobPara item = null;

		if (model != null)
		{
			item = new CatalogJobPara();
			
			item.setBackupDestination(model.getBackupDestination());
			item.setUserName(model.getUserName());
			item.setPassword(model.getPassword());
			item.setSessionNumber(model.getSessionNumber());
			item.setSubSessionNumber(model.getSubSessionNumber());
		//	item.setSessionGUID(model.getSessionGUID());
			item.setEncryptionPassword(model.getEncryptionPassword());
			
			List<String> grtEdbList = new ArrayList<String>();
			if(model.getGRTEdbList()!=null)
				grtEdbList.addAll(model.getGRTEdbList());
			item.setGrtEdbList(grtEdbList);
			item.setVmInstanceUUID(model.getVMInstanceUUID());
		}

		return item;
	}
	
	@Override
	public void submitCatalogJob(CatalogJobParaModel catalogJobModel) throws BusinessLogicException, ServiceInternalException,
			ServiceConnectException
	{
		try
		{
			CatalogJobPara catalogJobPara = convertToCatalogJobPara(catalogJobModel);		
			this.getServiceClient().getServiceGRT().submitCatalogJob(catalogJobPara);
				
		}
		catch (WebServiceException e)
		{
			this.proccessAxisFaultException(e);
		}
	}
	
	@Override
	public long submitFSCatalogJob(CatalogJobParaModel catalogJobModel, String vmInstanceUUID) throws BusinessLogicException, ServiceInternalException,
			ServiceConnectException
	{
		try
		{
			CatalogJobPara catalogJobPara = convertToCatalogJobPara(catalogJobModel);
			return this.getServiceClient().getServiceV2().submitFSCatalogJob(catalogJobPara,vmInstanceUUID);
		}
		catch (WebServiceException e)
		{
			this.proccessAxisFaultException(e);
		}
		
		return 0;
	}
	
	@Override
	public long validateCatalogFileExist(GridTreeNode loadConfig) throws BusinessLogicException, ServiceConnectException, ServiceInternalException
	{
		long catalogFileExist = 0;
		
		try
		{
			if (loadConfig != null)
			{
				Integer type = loadConfig.getType();

				// if this is GRT root node, get the grt catalog path
				if (CatalogModelType.rootGRTExchangeTypes.contains(type))
				{

					String dbIdentify = loadConfig.getPath() + "\\" + loadConfig.getComponentName();
					dbIdentify = dbIdentify.substring(dbIdentify.indexOf('\\') + 1);

					String backupDestination = loadConfig.getBackupDestination();
					long sessionID = loadConfig.getSessionID();
					long subSessionID = loadConfig.getSubSessionID();

					catalogFileExist = this.getServiceClient().getServiceGRT().validateCatalogFileExist(dbIdentify, backupDestination, sessionID, subSessionID);

				}
			}
		}
		catch (WebServiceException ex)
		{
			logger.error("error occurred:", ex);
			proccessAxisFaultException(ex);
		}
		return catalogFileExist;
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean isUsingEdgePolicySettings( int settingsType )
	{
		logger.debug("isUsingEdgePolicySettings() - start");
		
		try
		{
			WebServiceClientProxy client = this.getServiceClient();
			if (client != null)
			{
				settingsType = convertToWebServiceSettingsType( settingsType );
				return client.getServiceV2().isUsingEdgePolicySettings( settingsType );
			}
			else
			{
				logger.debug("isUsingEdgePolicySettings() - client was null");
			}
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		
		logger.debug("isUsingEdgePolicySettings() - end");
		
		return false;
	}

	//////////////////////////////////////////////////////////////////////////

	public static int convertToWebServiceSettingsType( int settingsType )
	{
		switch (settingsType)
		{
		case SettingsTypesForUI.BackupSettings:
			return SettingsTypes.BackupSettings;

		case SettingsTypesForUI.Archiving:
			return SettingsTypes.Archiving;

		case SettingsTypesForUI.VCMSettings:
			return SettingsTypes.VCMSettings;

		case SettingsTypesForUI.VMBackupSettings:
			return SettingsTypes.VMBackupSettings;

		case SettingsTypesForUI.Preferences:
			return SettingsTypes.Preferences;
			
		default:
			return -1;
		}
	}
	
	@Override
	public ArchiveSettingsModel getArchiveConfiguration() throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getArchiveConfiguration() - start");
		IFlashServiceV2 client = null;
		try
		{
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				ArchiveConfiguration archiveConfig = client.getArchiveConfiguration();
				
				if (archiveConfig != null)
				{
					ArchiveSettingsModel model = ConvertToArchiveConfigModel(archiveConfig);
					return model;
				}
				return null;
			}
			else
			{
				logger.debug("getArchiveConfiguration() - web client was null");
			}
		}
		catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		
		logger.debug("getArchiveConfiguration() - end");
		return null;
	}
	
	@Override
	public List<ArchiveSettingsModel> getArchiveConfigurations() throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getArchiveConfiguration() - start");
		IFlashServiceV2 client = null;
		List<ArchiveSettingsModel> configs=new ArrayList<ArchiveSettingsModel>();
		
		String hostname = "localhost";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		
		try
		{
			client = getServiceClient().getServiceV2();
			
			if (client != null){
				ArchiveConfiguration archiveConfig=null;
				ArchiveConfiguration archiveDeleteConfig=null;		
				List<RpsArchiveConfiguationWrapper> list=client.getRpsArchiveConfiguationSummary();		
				for(RpsArchiveConfiguationWrapper wrapper:list){
					
					RpsHost rpshost=wrapper.getHost();
					if(rpshost==null){
						//Local FileCopy,FileArchive
						archiveConfig=wrapper.getFileCopyConfiguration();
						archiveDeleteConfig=wrapper.getFileArchiveConfiguration();
						if (archiveConfig != null)
						{
							ArchiveSettingsModel model = ConvertToArchiveConfigModel(archiveConfig);
							model.setArchiveToRPS(false);
							model.setHostName(hostname);
							model.setType(ArchiveSettingsModel.FILECOPY);
							configs.add(model);
						}
						if (archiveDeleteConfig != null)
						{
							ArchiveSettingsModel model = ConvertToArchiveConfigModel(archiveDeleteConfig);
							model.setArchiveToRPS(false);
							model.setType(ArchiveSettingsModel.FILEARCHIVE);
							model.setHostName(hostname);
							configs.add(model);
						}
					}
					else {
						//RPS FileCopy,FileArchive	
						archiveConfig=wrapper.getFileCopyConfiguration();
						archiveDeleteConfig=wrapper.getFileArchiveConfiguration();
						RpsHostModel host = new RpsHostModel();
						host.setHostName(wrapper.getHost().getRhostname());
						host.setUserName(wrapper.getHost().getUsername());
						host.setPassword(wrapper.getHost().getPassword());
						host.setPort(wrapper.getHost().getPort());
						host.setIsHttpProtocol(wrapper.getHost().isHttpProtocol());
						host.setUUID(wrapper.getHost().getUuid());
						if (archiveConfig != null)
						{
							ArchiveSettingsModel model = ConvertToArchiveConfigModel(archiveConfig);
							model.setArchiveToRPS(true);
							model.setHost(host);
							//model.setHostName(host.getHostName());
							model.setHostName(hostname);
							model.setType(ArchiveSettingsModel.FILECOPY);
							model.setCatalogPath(archiveConfig.getStrCatalogPath());//
							model.setPolicyUUID(wrapper.getPolicyId());
							configs.add(model);
							
						}
						if (archiveDeleteConfig != null)
						{
							ArchiveSettingsModel model = ConvertToArchiveConfigModel(archiveDeleteConfig);
							model.setArchiveToRPS(true);
							model.setHost(host);
							//model.setHostName(host.getHostName());
							model.setHostName(hostname);
							model.setType(ArchiveSettingsModel.FILEARCHIVE);
							model.setCatalogPath(archiveDeleteConfig.getStrCatalogPath());
							model.setPolicyUUID(wrapper.getPolicyId());
							configs.add(model);
						}
					}
				
				}
				
				setAgentHostName(configs);
				
				
			}
			else
			{
				logger.debug("getArchiveConfiguration() - web client was null");
			}
		}
		catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		
		logger.debug("getArchiveConfiguration() - end");
		return configs;
	}
	

	private void setAgentHostName(List<ArchiveSettingsModel> configs) 
	{
		IFlashServiceV2 client = null;
		client = getServiceClient().getServiceV2();
		if (client != null)
		{
			for(ArchiveSettingsModel config: configs){
				
				ArchiveDestinationConfig archiveDestConfig = new ArchiveDestinationConfig();
				
				if(config.getArchiveToDrive())
				{
					archiveDestConfig.setbArchiveToDrive(config.getArchiveToDrive());
					archiveDestConfig.setbArchiveToCloud(config.getArchiveToCloud());
					
					archiveDestConfig.setStrArchiveToDrivePath(config.getArchiveToDrivePath());
					archiveDestConfig.setStrArchiveDestinationUserName(config.getDestinationPathUserName());
					archiveDestConfig.setStrArchiveDestinationPassword(config.getDestinationPathPassword());
				}
				else if(config.getArchiveToCloud())
				{
					archiveDestConfig.setbArchiveToCloud(config.getArchiveToCloud());
					archiveDestConfig.setbArchiveToDrive(config.getArchiveToDrive());
					ArchiveCloudDestInfoModel cloudModel = config.getCloudConfigModel();
					
//					if(cloudModel.getencodedBucketName() != null && cloudModel.getencodedBucketName().length() >0)
//						cloudModel.setcloudBucketName(cloudModel.getencodedBucketName());
					
					ArchiveCloudDestInfo CloudConfig = ConvertCloudConfigModel(cloudModel);
					if(CloudConfig.getEncodedCloudBucketName() != null && CloudConfig.getEncodedCloudBucketName().length() >0)
						CloudConfig.setcloudBucketName(CloudConfig.getEncodedCloudBucketName());
					
					archiveDestConfig.setCloudConfig(CloudConfig);
				}
				archiveDestConfig.setstrHostname(config.getHostName());
				
				ArchiveDestinationDetailsConfig destDetails = client.getArchiveChangedDestinationDetails(archiveDestConfig);
				
				if(destDetails!=null){
					config.setHostName(destDetails.gethostName());
				}
			}
			
		}
		
		
				
	}

	public ArchiveSettingsModel ConvertToArchiveConfigModel(
			ArchiveConfiguration in_archiveConfig) 
	{
		ArchiveSettingsModel archiveConfigModel = new ArchiveSettingsModel();
		
		archiveConfigModel.setArchiveSources(ConvertArchiveSourcesToModel(in_archiveConfig.getArchiveSources()));
		
		archiveConfigModel.setArchivedFileVersionsRetentionCount(in_archiveConfig.getFileVersionRetentionCount());
		archiveConfigModel.setFilesRetentionTime(in_archiveConfig.getFilesRetentionTime());
		archiveConfigModel.setArchiveAfterBackup(in_archiveConfig.isbArchiveAfterBackup());

		if("Simple".equals(in_archiveConfig.getStrScheduleMode())){
			archiveConfigModel.setBackupFrequency(true);
			archiveConfigModel.setArchiveAfterNumberofBackups(in_archiveConfig.getiArchiveAfterNBackups());
		}
		else{
			archiveConfigModel.setBackupSchedule(true);
			archiveConfigModel.setDaily(in_archiveConfig.isbDailyBackup());
			archiveConfigModel.setWeekly(in_archiveConfig.isbWeeklyBackup());
			archiveConfigModel.setMonthly(in_archiveConfig.isbMonthlyBackup());
		}
		archiveConfigModel.setAdvanceSchedule(in_archiveConfig.getAdvanceSchedule());
		
		archiveConfigModel.setPurgeScheduleAvailable(in_archiveConfig.isbPurgeScheduleAvailable());
		archiveConfigModel.setPurgeArchiveItems(in_archiveConfig.isbPurgeArchiveItems());
		archiveConfigModel.setPurgeAfterDays(in_archiveConfig.getiPurgeAfterDays());
		archiveConfigModel.setPurgeStartTime(in_archiveConfig.getlPurgeStartTime());
		
		//archiveConfigModel.setExcludeSystemFiles(in_archiveConfig.isbArchiveExcludeSystemFiles());
		//archiveConfigModel.setExcludeAppFiles(in_archiveConfig.isbArchiveExcludeAppFiles());
		
		archiveConfigModel.setArchiveToDrive(in_archiveConfig.isbArchiveToDrive());
		archiveConfigModel.setArchiveToDrivePath(in_archiveConfig.getStrArchiveToDrivePath());
		archiveConfigModel.setDestinationPathUserName(in_archiveConfig.getStrArchiveDestinationUserName());
		archiveConfigModel.setDestinationPathPassword(in_archiveConfig.getStrArchiveDestinationPassword());
		archiveConfigModel.setArchiveToCloud(in_archiveConfig.isbArchiveToCloud());
		
		ArchiveCloudDestInfoModel cloudConfigModel = ConvertCloudConfig(in_archiveConfig.getCloudConfig());
		archiveConfigModel.setCloudConfigModel(cloudConfigModel);
		
		archiveConfigModel.setRetentiontime(in_archiveConfig.getRetentiontime());
		archiveConfigModel.setCompressionLevel(in_archiveConfig.getCompressionLevel());
		archiveConfigModel.setEncryption(in_archiveConfig.isbEncryption());
		if(in_archiveConfig.isbEncryption())
			archiveConfigModel.setEncryptionPassword(in_archiveConfig.getEncryptionPassword());
		//archiveConfigModel.setSpaceUtilizationValue(in_archiveConfig.getiSpaceUtilization());
		
		//backup volumes
		//archiveConfigModel.setbackupVolumes(ConvertToBackupSettingsVolumeModel(in_archiveConfig.getbackupVolumes()));
		//archiveConfigModel.setbackupDestination(in_archiveConfig.getbackupDestination());
		/*List<FileModel> modelList = new ArrayList<FileModel>();
		Volume[] volumes = in_archiveConfig.getbackupVolumeDetails();
		for (int i = 0; i < volumes.length; i++)
		{
			modelList.add(ConvertToVolumeModel(volumes[i]));
		}
		archiveConfigModel.setbackupVolumes(modelList);*/
		
		archiveConfigModel.setCatalogPath(in_archiveConfig.getStrCatalogPath());
		archiveConfigModel.setCatalogFolderUser(StringUtil.isEmptyOrNull(in_archiveConfig.getStrCatalogDirUserName())? "" : in_archiveConfig.getStrCatalogDirUserName() );
		archiveConfigModel.setCatalogFolderPassword(StringUtil.isEmptyOrNull(in_archiveConfig.getStrCatalogDirPassword())? "" : in_archiveConfig.getStrCatalogDirPassword() );
		
		return archiveConfigModel;
	}

	private ArchiveCloudDestInfoModel ConvertCloudConfig(ArchiveCloudDestInfo in_cloudConfig) 
	{
		ArchiveCloudDestInfoModel CloudConfigModel = null;
		
		if(in_cloudConfig != null)
		{
			CloudConfigModel = new ArchiveCloudDestInfoModel();
			CloudConfigModel.setcloudVendorURL(in_cloudConfig.getcloudVendorURL());
			CloudConfigModel.setcloudVendorUserName(in_cloudConfig.getcloudVendorUserName());
			CloudConfigModel.setcloudVendorPassword(in_cloudConfig.getcloudVendorPassword());
			CloudConfigModel.setcloudVendorType(in_cloudConfig.getcloudVendorType());
			CloudConfigModel.setCloudSubVendorType(in_cloudConfig.getCloudSubVendorType());
			//CloudConfigModel.setVendorCertificatePath(in_cloudConfig.getVendorCertificatePath());
			//CloudConfigModel.setCertificatePassword(in_cloudConfig.getCertificatePassword());
			//CloudConfigModel.setVendorHostname(in_cloudConfig.getVendorHostname());
			//CloudConfigModel.setVendorPort(in_cloudConfig.getVendorPort());
			if(in_cloudConfig.getcloudVendorType() == 0L)
			{
				CloudConfigModel.setrrsFlag(in_cloudConfig.getRRSFlag());
			}
			else
			{
				CloudConfigModel.setrrsFlag(0L);
			}
			CloudConfigModel.setcloudBucketName(in_cloudConfig.getcloudBucketName());
			CloudConfigModel.setencodedBucketName(in_cloudConfig.getEncodedCloudBucketName());
			CloudConfigModel.setcloudBucketRegionName(in_cloudConfig.getcloudBucketRegionName());
			
			CloudConfigModel.setcloudUseProxy(in_cloudConfig.iscloudUseProxy());
			if(in_cloudConfig.iscloudUseProxy())
			{
				CloudConfigModel.setcloudProxyServerName(in_cloudConfig.getcloudProxyServerName());
				CloudConfigModel.setcloudProxyPort(in_cloudConfig.getcloudProxyPort());
				
				CloudConfigModel.setcloudProxyRequireAuth(in_cloudConfig.iscloudProxyRequireAuth());
				if(in_cloudConfig.iscloudProxyRequireAuth())
				{
					CloudConfigModel.setcloudProxyUserName(in_cloudConfig.getcloudProxyUserName());
					CloudConfigModel.setcloudProxyPassword(in_cloudConfig.getcloudProxyPassword());
				}
			}
		}
		return CloudConfigModel;
	}

	private ArchiveSourceInfoModel[] ConvertArchiveSourcesToModel(
			ArchiveSourceInfoConfiguration[] in_archiveSources) 
	{
		if(in_archiveSources == null)
			return null;
		int iSourcesCount = in_archiveSources.length;
		ArchiveSourceInfoModel[] archiveSourcesModelList = new ArchiveSourceInfoModel[iSourcesCount];		
		for(int iarchiveSourceIndex = 0;iarchiveSourceIndex < iSourcesCount;iarchiveSourceIndex++)
		{
			ArchiveSourceInfoConfiguration archiveSourceConfig = in_archiveSources[iarchiveSourceIndex];
			
			archiveSourcesModelList[iarchiveSourceIndex] = new ArchiveSourceInfoModel();
			archiveSourcesModelList[iarchiveSourceIndex].setSourcePath(archiveSourceConfig.getStrSourcePath());
			archiveSourcesModelList[iarchiveSourceIndex].setDisplaySourcePath(archiveSourceConfig.getStrDisplaySourcePath());
			archiveSourcesModelList[iarchiveSourceIndex].setArchiveFiles(archiveSourceConfig.isbArchiveFiles());
			archiveSourcesModelList[iarchiveSourceIndex].setCopyFiles(archiveSourceConfig.isbCopyFiles());
			
			archiveSourcesModelList[iarchiveSourceIndex].setArchiveSourceFilters(ConvertarchiveSourceFiltersToModel(archiveSourceConfig.getArchiveSourceFiltersConfig()));
		}		
		
		return archiveSourcesModelList;
	}


	/*private ArchiveSourceCriteriaModel[] ConvertarchiveSourceCriteriaToModel(
			ArchiveSourceCriteriaConfig[] in_archiveSourceCriteriaConfig) {
		if(in_archiveSourceCriteriaConfig == null)
			return null;
		int iCriteriaCount = in_archiveSourceCriteriaConfig.length;
		ArchiveSourceCriteriaModel[] archiveSourceCriteriaModelList = new ArchiveSourceCriteriaModel[iCriteriaCount];
		
		boolean bSourceCriteriaAdded = false;
		for(int iSourceCriteriaIndex = 0;iSourceCriteriaIndex < iCriteriaCount;iSourceCriteriaIndex++)
		{
			ArchiveSourceCriteriaConfig archiveSourceCriteriaConfig = in_archiveSourceCriteriaConfig[iSourceCriteriaIndex];
			
			archiveSourceCriteriaModelList[iSourceCriteriaIndex] = new ArchiveSourceCriteriaModel();
			archiveSourceCriteriaModelList[iSourceCriteriaIndex].setCriteria(archiveSourceCriteriaConfig.getstrCriteria());
			archiveSourceCriteriaModelList[iSourceCriteriaIndex].setCriteriaType(archiveSourceCriteriaConfig.getstrCriteriaType());
			archiveSourceCriteriaModelList[iSourceCriteriaIndex].setCriteriaOperator(archiveSourceCriteriaConfig.getstrCriteriaOperator());
			
			archiveSourceCriteriaModelList[iSourceCriteriaIndex].setLowerValue(archiveSourceCriteriaConfig.getLowerValue());
			archiveSourceCriteriaModelList[iSourceCriteriaIndex].setLowerUnit(archiveSourceCriteriaConfig.getstrLowerUnit());
			if(archiveSourceCriteriaConfig.getstrCriteriaOperator().compareToIgnoreCase("between") == 0)
			{
				archiveSourceCriteriaModelList[iSourceCriteriaIndex].setHigherValue(archiveSourceCriteriaConfig.getHigherValue());
				archiveSourceCriteriaModelList[iSourceCriteriaIndex].setHigherUnit(archiveSourceCriteriaConfig.getstrHigherUnit());
			}
			int iCriteriaValues = archiveSourceCriteriaConfig.getcriteriaValues().length;
			CriteriaValue[] criteriaValue = new CriteriaValue[iCriteriaValues];
			for(int iCriteriaValueIndex = 0;iCriteriaValueIndex<iCriteriaValues;iCriteriaValueIndex++)
			{
				CriteriaValues archiveCriteriaValues = archiveSourceCriteriaConfig.getcriteriaValues()[iCriteriaValueIndex];
							
				criteriaValue[iCriteriaValueIndex] = new CriteriaValue();
				criteriaValue[iCriteriaValueIndex].setCriteriaValue(archiveCriteriaValues.getCriteriaValue());
				criteriaValue[iCriteriaValueIndex].setCriteriaUnit(archiveCriteriaValues.getCriteriaUnit());
			}
			
			//archiveSourceCriteriaModelList[iSourceCriteriaIndex].setcriteriaValues(criteriaValue);
			
			//archiveSourceFiltersModelList.add(archiveSourceFiltersModel);
			bSourceCriteriaAdded = true;
		}		
		
		return archiveSourceCriteriaModelList;
	}
*/
	private ArchiveSourceFilterModel[] ConvertarchiveSourceFiltersToModel(
			ArchiveSourceFiltersConfiguration[] in_archiveSourceFiltersConfig) 
	{
		if(in_archiveSourceFiltersConfig == null)
			return null;
		int iSourcesCount = in_archiveSourceFiltersConfig.length;
		ArchiveSourceFilterModel[] archiveSourceFiltersModelList = new ArchiveSourceFilterModel[iSourcesCount];
		
		for(int iSourceFiltersIndex = 0;iSourceFiltersIndex < iSourcesCount;iSourceFiltersIndex++)
		{
			ArchiveSourceFiltersConfiguration archiveSourceFiltersConfig = in_archiveSourceFiltersConfig[iSourceFiltersIndex];
			
			archiveSourceFiltersModelList[iSourceFiltersIndex] = new ArchiveSourceFilterModel();
			archiveSourceFiltersModelList[iSourceFiltersIndex].setFilterOrCriteriaType(archiveSourceFiltersConfig.getFilterOrCriteriaType());
			archiveSourceFiltersModelList[iSourceFiltersIndex].setFilterOrCriteriaName(archiveSourceFiltersConfig.getFilterOrCriteriaName());
			archiveSourceFiltersModelList[iSourceFiltersIndex].setFilterOrCriteriaLowerValue(archiveSourceFiltersConfig.getFilterOrCriteriaLowerValue());
			archiveSourceFiltersModelList[iSourceFiltersIndex].setLocFilterOrCriteriaLowerValue(archiveSourceFiltersConfig.getLocFilterOrCriteriaLowerValue());
			
			archiveSourceFiltersModelList[iSourceFiltersIndex].setCriteriaOperator(archiveSourceFiltersConfig.getCriteriaOperator());
			archiveSourceFiltersModelList[iSourceFiltersIndex].setIsCriteria(archiveSourceFiltersConfig.isIsCriteria());
			archiveSourceFiltersModelList[iSourceFiltersIndex].setIsDefaultFilter(archiveSourceFiltersConfig.isIsDefaultFilter());
			
			if(archiveSourceFiltersConfig.getCriteriaOperator().compareToIgnoreCase(ArchiveConstantsModel.OPERATOR_BETWEEN_STRING) == 0)
			{
				archiveSourceFiltersModelList[iSourceFiltersIndex].setFilterOrCriteriaHigherValue(archiveSourceFiltersConfig.getFilterOrCriteriaHigherValue());
			}
		}		
		
		return archiveSourceFiltersModelList;
	}
	
	@Override
	public long saveArchiveConfiguration(
			ArchiveSettingsModel in_ArchiveSettingsModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		return checkArchiveConfiguration(in_ArchiveSettingsModel, true);
	}
	@Override
	public long validateArchiveConfiguration(
			ArchiveSettingsModel in_ArchiveSettingsModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		return checkArchiveConfiguration(in_ArchiveSettingsModel, false);
	}
	
	private long checkArchiveConfiguration(
			ArchiveSettingsModel in_ArchiveSettingsModel, boolean isSave)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		long ret = -1;
		
		if(in_ArchiveSettingsModel == null)
		{
			return ret;
		}
		
		logger.debug("checkArchiveConfiguration() - start");
		IFlashServiceV2 client = null;
		
		client = getServiceClient().getServiceV2();
		if(client != null)
		{
			ArchiveConfiguration archiveConfig = convertToArchiveConfiguration(in_ArchiveSettingsModel);
			try {
				if(isSave) {
					ret = client.saveArchiveConfiguration(archiveConfig);
				}
				else {
					ret =  client.validateArchiveConfiguration( archiveConfig );
				}
				
			} catch (WebServiceException e) {
				logger.error("Error occurs during checkArchiveConfiguration()...");
				logger.error(e.getMessage());		
				proccessAxisFaultException(e);
			}

			if (ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE) - FlashServiceErrorCode.BackupConfig_BASE)) {
				throw this
						.generateException(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE);
			}
			else if(ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING) - FlashServiceErrorCode.BackupConfig_BASE))
				throw generateException(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING);
		}
		logger.debug("checkArchiveConfiguration() - end");
		return ret;
	}

	private ArchiveConfiguration convertToArchiveConfiguration(
			ArchiveSettingsModel in_ArchiveSettingsModel) {
		
		ArchiveConfiguration archiveConfig = new ArchiveConfiguration();
		
		ArchiveSourceInfoConfiguration[] archiveSourceConfigList = ConvertArchiveSourceInformation(in_ArchiveSettingsModel.getArchiveSources());
		archiveConfig.setArchiveSources(archiveSourceConfigList);
		
		archiveConfig.setFileVersionRetentionCount(in_ArchiveSettingsModel.getArchivedFileVersionsRetentionCount());
		archiveConfig.setFilesRetentionTime(in_ArchiveSettingsModel.getFilesRetentionTime());
		archiveConfig.setbArchiveAfterBackup(in_ArchiveSettingsModel.getArchiveAfterBackup());
		
		if(in_ArchiveSettingsModel.isBackupFrequency()){
			archiveConfig.setStrScheduleMode("Simple");
			archiveConfig.setiArchiveAfterNBackups(in_ArchiveSettingsModel.getArchiveAfterNumberofBackups());
		}
		else if(in_ArchiveSettingsModel.isBackupSchedule()){
			archiveConfig.setStrScheduleMode("Advanced");
			archiveConfig.setbDailyBackup(in_ArchiveSettingsModel.isDaily());
			archiveConfig.setbWeeklyBackup(in_ArchiveSettingsModel.isWeekly());
			archiveConfig.setbMonthlyBackup(in_ArchiveSettingsModel.isMonthly());
		}
		archiveConfig.setAdvanceSchedule(in_ArchiveSettingsModel.getAdvanceSchedule());
		
		archiveConfig.setbPurgeArchiveItems(in_ArchiveSettingsModel.getPurgeArchiveItems() != null ? in_ArchiveSettingsModel.getPurgeArchiveItems() : false);
				
		archiveConfig.setbPurgeScheduleAvailable(in_ArchiveSettingsModel.getPurgeScheduleAvailable());
		//if(archiveConfig.isbPurgeArchiveItems())
		{
			archiveConfig.setiPurgeAfterDays(in_ArchiveSettingsModel.getPurgeAfterDays());
			archiveConfig.setlPurgeStartTime(in_ArchiveSettingsModel.getPurgeStartTime());
		}
		
		//archiveConfig.setbArchiveExcludeSystemFiles(in_ArchiveSettingsModel.getExcludeSystemFiles());
		//archiveConfig.setbArchiveExcludeAppFiles(in_ArchiveSettingsModel.getExcludeAppFiles());
		
		archiveConfig.setbArchiveToDrive(in_ArchiveSettingsModel.getArchiveToDrive());
		archiveConfig.setStrArchiveToDrivePath(in_ArchiveSettingsModel.getArchiveToDrivePath());
		archiveConfig.setStrArchiveDestinationUserName(in_ArchiveSettingsModel.getDestinationPathUserName());
		archiveConfig.setStrArchiveDestinationPassword(in_ArchiveSettingsModel.getDestinationPathPassword());
		
		archiveConfig.setbArchiveToCloud(in_ArchiveSettingsModel.getArchiveToCloud());
	
		if(in_ArchiveSettingsModel.getArchiveToCloud())
		{
			ArchiveCloudDestInfo CloudConfig = ConvertCloudConfigModel(in_ArchiveSettingsModel.getCloudConfigModel());
			archiveConfig.setCloudConfig(CloudConfig);
		}
		
		archiveConfig.setRetentiontime(in_ArchiveSettingsModel.getRetentiontime());
		archiveConfig.setCompressionLevel(in_ArchiveSettingsModel.getCompressionLevel());
		archiveConfig.setbEncryption(in_ArchiveSettingsModel.getEncryption());
		if(in_ArchiveSettingsModel.getEncryption())
			archiveConfig.setEncryptionPassword(in_ArchiveSettingsModel.getEncryptionPassword());
		//archiveConfig.setiSpaceUtilization(in_ArchiveSettingsModel.getSpaceUtilizationValue());
		
		BackupVolumes volumes = ConvertToBackupVolume(in_ArchiveSettingsModel.getbackupVolumes());
		archiveConfig.setBackupVolumes(volumes);
		archiveConfig.setbackupDestination(in_ArchiveSettingsModel.getBackupDestination());
		
		return archiveConfig;
	}

	private ArchiveCloudDestInfo ConvertCloudConfigModel(
			ArchiveCloudDestInfoModel in_cloudConfigModel) 
	{
		ArchiveCloudDestInfo CloudConfig = null;
		
		if(in_cloudConfigModel != null)
		{
			CloudConfig = new ArchiveCloudDestInfo();
			CloudConfig.setcloudVendorType(in_cloudConfigModel.getcloudVendorType());
			CloudConfig.setcloudVendorURL(in_cloudConfigModel.getcloudVendorURL());
			CloudConfig.setcloudVendorUserName(in_cloudConfigModel.getcloudVendorUserName());
			CloudConfig.setcloudVendorPassword(in_cloudConfigModel.getcloudVendorPassword());
			CloudConfig.setCloudSubVendorType(in_cloudConfigModel.getCloudSubVendorType());
			//CloudConfig.setVendorCertificatePath(in_cloudConfigModel.getVendorCertificatePath());
			//CloudConfig.setCertificatePassword(in_cloudConfigModel.getCertificatePassword());
			//CloudConfig.setVendorHostname(in_cloudConfigModel.getVendorHostname());
			//CloudConfig.setVendorPort(in_cloudConfigModel.getVendorPort());
			CloudConfig.setcloudBucketName(in_cloudConfigModel.getcloudBucketName());
			if(in_cloudConfigModel.getencodedBucketName() != null && in_cloudConfigModel.getencodedBucketName().length() > 0)
				CloudConfig.setEncodedCloudBucketName(in_cloudConfigModel.getencodedBucketName());
			else
				CloudConfig.setEncodedCloudBucketName(in_cloudConfigModel.getcloudBucketName());
			CloudConfig.setcloudBucketRegionName(in_cloudConfigModel.getcloudBucketRegionName());
			if(in_cloudConfigModel.getcloudVendorType() == 0L)
			{
				CloudConfig.setRRSFlag(in_cloudConfigModel.getrrsFlag());
			}
			else
			{
				CloudConfig.setRRSFlag(0L);
			}
			CloudConfig.setcloudUseProxy(in_cloudConfigModel.getcloudUseProxy());
			if(in_cloudConfigModel.getcloudUseProxy())
			{
				CloudConfig.setcloudProxyServerName(in_cloudConfigModel.getcloudProxyServerName());
				CloudConfig.setcloudProxyPort(in_cloudConfigModel.getcloudProxyPort());
				
				CloudConfig.setcloudProxyRequireAuth(in_cloudConfigModel.getcloudProxyRequireAuth());
				if(in_cloudConfigModel.getcloudProxyRequireAuth())
				{
					CloudConfig.setcloudProxyUserName(in_cloudConfigModel.getcloudProxyUserName());
					CloudConfig.setcloudProxyPassword(in_cloudConfigModel.getcloudProxyPassword());
				}
			}
		}
		return CloudConfig;
	}

	private ArchiveSourceInfoConfiguration[] ConvertArchiveSourceInformation(
			ArchiveSourceInfoModel[] in_archiveSources) 
	{
		int iSourcesCount = 0;
		if(in_archiveSources != null)		
			iSourcesCount = in_archiveSources.length;
		
		ArchiveSourceInfoConfiguration[] archiveSourceConfigList = new ArchiveSourceInfoConfiguration[iSourcesCount];

		for(int iarchiveSourceIndex = 0;iarchiveSourceIndex < iSourcesCount;iarchiveSourceIndex++)
		{
			ArchiveSourceInfoModel archiveSourceModel = in_archiveSources[iarchiveSourceIndex];
			
			ArchiveSourceInfoConfiguration archiveSourceConfig = new ArchiveSourceInfoConfiguration();
			archiveSourceConfig.setStrSourcePath(archiveSourceModel.getSourcePath());
			archiveSourceConfig.setStrDisplaySourcePath(archiveSourceModel.getDispalySourcePath());
			archiveSourceConfig.setbArchiveFiles(archiveSourceModel.getArchiveFiles() != null ? archiveSourceModel.getArchiveFiles() : false);
			archiveSourceConfig.setbCopyFiles(archiveSourceModel.getCopyFiles() != null ? archiveSourceModel.getCopyFiles() : false);
			
			archiveSourceConfig.setArchiveSourceFiltersConfig(ConvertArchiveSourceFilters(archiveSourceModel.getArchiveSourceFilters()));
	//		archiveSourceConfig.setArchiveSourceCriteria(ConvertArchiveSourceCriteria(archiveSourceModel.getArchiveSourceCriterias()));
			
			archiveSourceConfigList[iarchiveSourceIndex] = archiveSourceConfig;
		}		
		
		return archiveSourceConfigList;
	}

	/*private ArchiveSourceCriteriaConfig[] ConvertArchiveSourceCriteria(
			ArchiveSourceCriteriaModel[] in_archiveSourceCriterias) {

		if(in_archiveSourceCriterias == null)
			return null;
		int iCriteriaCount = in_archiveSourceCriterias.length;
		ArchiveSourceCriteriaConfig[] archiveSourceCriteriaConfigList = new ArchiveSourceCriteriaConfig[iCriteriaCount];

		boolean bSourceCriteriaAdded = false;
		for(int iCriteriaIndex = 0;iCriteriaIndex < iCriteriaCount;iCriteriaIndex++)
		{
			ArchiveSourceCriteriaModel archiveSourceCriteriaModel = in_archiveSourceCriterias[iCriteriaIndex];
			
			archiveSourceCriteriaConfigList[iCriteriaIndex] = new ArchiveSourceCriteriaConfig();
			archiveSourceCriteriaConfigList[iCriteriaIndex].setstrCriteria(archiveSourceCriteriaModel.getCriteria());
			archiveSourceCriteriaConfigList[iCriteriaIndex].setCriteriaType(archiveSourceCriteriaModel.getCriteriaType());
			archiveSourceCriteriaConfigList[iCriteriaIndex].setstrCriteriaOperator(archiveSourceCriteriaModel.getCriteriaOperator());
			
			archiveSourceCriteriaConfigList[iCriteriaIndex].setLowerValue(archiveSourceCriteriaModel.getLowerValue());
			archiveSourceCriteriaConfigList[iCriteriaIndex].setLowerUnit(archiveSourceCriteriaModel.getLowerUnit());
			
			if(archiveSourceCriteriaModel.getCriteriaOperator().compareToIgnoreCase("between") == 0)
			{
				archiveSourceCriteriaConfigList[iCriteriaIndex].setHigherValue(archiveSourceCriteriaModel.getHigherValue());
				archiveSourceCriteriaConfigList[iCriteriaIndex].setHigherUnit(archiveSourceCriteriaModel.getHigherUnit());
			}

			int iCriteriaValues = archiveSourceCriteriaModel.getcriteriaValues().length;
			CriteriaValues[] criteriaValueList = new CriteriaValues[iCriteriaValues];
			for(int iCriteriaValueIndex = 0;iCriteriaValueIndex<iCriteriaValues;iCriteriaValueIndex++)
			{
				CriteriaValue archiveCriteriaValue = archiveSourceCriteriaModel.getcriteriaValues()[iCriteriaValueIndex];
							
				criteriaValueList[iCriteriaValueIndex] = new CriteriaValues();
				criteriaValueList[iCriteriaValueIndex].setCriteriaValue(archiveCriteriaValue.getCriteriaValue());
				criteriaValueList[iCriteriaValueIndex].setCriteriaUnit(archiveCriteriaValue.getCriteriaUnit());
			}
			
			archiveSourceCriteriaConfigList[iCriteriaIndex].setcriteriaValues(criteriaValueList);
			bSourceCriteriaAdded = true;
		}		
		
		return archiveSourceCriteriaConfigList;
	}*/

	private ArchiveSourceFiltersConfiguration[] ConvertArchiveSourceFilters(
			ArchiveSourceFilterModel[] in_archiveSourceFilters)
	{
		if(in_archiveSourceFilters == null)
			return null;
		int iSourcesCount = in_archiveSourceFilters.length;
		ArchiveSourceFiltersConfiguration[] archiveSourceFiltersConfigList = new ArchiveSourceFiltersConfiguration[iSourcesCount];

		for(int iSourceFiltersIndex = 0;iSourceFiltersIndex < iSourcesCount;iSourceFiltersIndex++)
		{
			ArchiveSourceFilterModel archiveSourceFilterModel = in_archiveSourceFilters[iSourceFiltersIndex];
			
			archiveSourceFiltersConfigList[iSourceFiltersIndex] = new ArchiveSourceFiltersConfiguration();
			archiveSourceFiltersConfigList[iSourceFiltersIndex].setFilterOrCriteriaType(archiveSourceFilterModel.getFilterOrCriteriaType());
			archiveSourceFiltersConfigList[iSourceFiltersIndex].setFilterOrCriteriaName(archiveSourceFilterModel.getFilterOrCriteriaName());
			archiveSourceFiltersConfigList[iSourceFiltersIndex].setFilterOrCriteriaLowerValue(archiveSourceFilterModel.getFilterOrCriteriaLowerValue());
			archiveSourceFiltersConfigList[iSourceFiltersIndex].setLocFilterOrCriteriaLowerValue(archiveSourceFilterModel.getLocFilterOrCriteriaLowerValue());
		
			archiveSourceFiltersConfigList[iSourceFiltersIndex].setCriteriaOperator(archiveSourceFilterModel.getCriteriaOperator());
			archiveSourceFiltersConfigList[iSourceFiltersIndex].setIsCriteria(archiveSourceFilterModel.getIsCriteria());
			archiveSourceFiltersConfigList[iSourceFiltersIndex].setIsDefaultFilter(archiveSourceFilterModel.getIsDefaultFilter());

			if(archiveSourceFilterModel.getCriteriaOperator().compareToIgnoreCase(ArchiveConstantsModel.OPERATOR_BETWEEN_STRING) == 0)
			{
				archiveSourceFiltersConfigList[iSourceFiltersIndex].setFilterOrCriteriaHigherValue(archiveSourceFilterModel.getFilterOrCriteriaHigherValue());
			}
		}
		
		return archiveSourceFiltersConfigList;
	}

	/*@Override
	public List<String> getArchivedVolumesList(String strArchiveDestination,
			String strUserName, String strPassword)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getArchivedVolumesList() - start");
		WebServiceClient client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				String[] strVolumes = client.getArchivedVolumesList(strArchiveDestination,strUserName,strPassword);
			}
			else
			{
				logger.debug("getArchivedVolumesList() - client was null");
			}
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getArchivedVolumesList() - end");
		return null;
	}*/

	@Override
	public ArchiveRestoreDestinationVolumesModel[] getArchiveDestinationItems(ArchiveDestinationModel archiveDestModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getArchiveDestinationItems(String, String, String) - start");

		ArchiveRestoreDestinationVolumesModel[] archiveDestinationModelsList = null;
		IFlashServiceV2 client = null;
		try {
			ArchiveDestinationConfig config = ConvertArchiveDestModelToconfig(archiveDestModel);
		 	
			
		 	if(config == null)
		 		return null;
		 	
			client = getServiceClient().getServiceV2();
			if (client != null) {
				ArchiveDestinationVolumeConfig[] archiveDestConfig=new ArchiveDestinationVolumeConfig[0];
				if(archiveDestModel!=null)
				if(archiveDestModel.getArchiveToRPS()!=null&&archiveDestModel.getArchiveToRPS()){
					RpsHost host=ConvertRpsHostModelToRpsHost(archiveDestModel.getRpsHostModel());
					String UUID=archiveDestModel.getPolicyUUID();
					archiveDestConfig=client.getArchiveDestinationItemsFromRPS(host,archiveDestModel.getCatalogPath(),archiveDestModel.getCatalogFolderUser(),archiveDestModel.getCatalogFolderPassword(), host.getRhostname(),config);
					archiveDestinationModelsList = ConvertArchiveDestinationObject(archiveDestConfig);
					
				}else{
					archiveDestConfig = client.getArchiveDestinationItems(config);
					archiveDestinationModelsList = ConvertArchiveDestinationObject(archiveDestConfig);
			 	}
				
			}
		} catch(WebServiceException exception) {
			proccessAxisFaultException(exception);
		}  
		logger.debug("getArchiveDestinationItems end");
		return archiveDestinationModelsList;
	}

	private RpsHost ConvertRpsHostModelToRpsHost(RpsHostModel in_rpshostmodel){
		if(in_rpshostmodel==null)return null;
		RpsHost host=new RpsHost();
		host.setHttpProtocol(in_rpshostmodel.getIsHttpProtocol());
		host.setRhostname(in_rpshostmodel.getHostName());
		host.setUsername(in_rpshostmodel.getUserName());
		host.setPassword(in_rpshostmodel.getPassword());
		host.setPort(in_rpshostmodel.getPort());
		host.setUuid(in_rpshostmodel.getUUID());
		return host;
	}
	
	private ArchiveDestinationConfig ConvertArchiveDestModelToconfig(
			ArchiveDestinationModel in_archiveDestModel) {

		if(in_archiveDestModel == null)
			return null;
		
		ArchiveDestinationConfig archiveDestConfig = new ArchiveDestinationConfig();
		
		if(in_archiveDestModel.getArchiveToDrive())
		{
			archiveDestConfig.setbArchiveToDrive(in_archiveDestModel.getArchiveToDrive());
			archiveDestConfig.setbArchiveToCloud(in_archiveDestModel.getArchiveToCloud());
			ArchiveDiskDestInfoModel archiveDiskInfo = in_archiveDestModel.getArchiveDiskDestInfoModel();
			archiveDestConfig.setStrArchiveToDrivePath(archiveDiskInfo.getArchiveDiskDestPath());
			archiveDestConfig.setStrArchiveDestinationUserName(archiveDiskInfo.getArchiveDiskUserName());
			archiveDestConfig.setStrArchiveDestinationPassword(archiveDiskInfo.getArchiveDiskPassword());
		}
		else if(in_archiveDestModel.getArchiveToCloud())
		{
			archiveDestConfig.setbArchiveToCloud(in_archiveDestModel.getArchiveToCloud());
			archiveDestConfig.setbArchiveToDrive(in_archiveDestModel.getArchiveToDrive());
			ArchiveCloudDestInfoModel cloudModel = in_archiveDestModel.getCloudConfigModel();
			if(cloudModel.getencodedBucketName() != null && cloudModel.getencodedBucketName().length() >0)
				cloudModel.setcloudBucketName(cloudModel.getencodedBucketName());
			ArchiveCloudDestInfo CloudConfig = ConvertCloudConfigModel(cloudModel);
			archiveDestConfig.setCloudConfig(CloudConfig);
		}
		archiveDestConfig.setstrHostname(in_archiveDestModel.getHostName());
		return archiveDestConfig;
	}

	private ArchiveRestoreDestinationVolumesModel[] ConvertArchiveDestinationObject(
			ArchiveDestinationVolumeConfig[] archiveDestConfig) {

		ArchiveRestoreDestinationVolumesModel[] volumeModels = null;
		ArchiveDestinationVolumeConfig[] archiveDestVolumes = archiveDestConfig;
		if(archiveDestVolumes != null)
		{
			int iarchiveDestVolumesCount = archiveDestVolumes.length;
			if(iarchiveDestVolumesCount > 0)
			{
				volumeModels = new ArchiveRestoreDestinationVolumesModel[iarchiveDestVolumesCount];
				for(int iIndex = 0;iIndex < iarchiveDestVolumesCount;iIndex++)
				{
					volumeModels[iIndex] = new ArchiveRestoreDestinationVolumesModel();
					volumeModels[iIndex].setvolumeHandle(archiveDestVolumes[iIndex].getvolumeHandle());
					volumeModels[iIndex].setDisplayName(archiveDestVolumes[iIndex].getDisplayName());
					volumeModels[iIndex].setGuid(archiveDestVolumes[iIndex].getGuid());
					volumeModels[iIndex].setChildrenCount(archiveDestVolumes[iIndex].getChildrenCount());
				}
			}
		}
		return volumeModels;
	}

	@Override
	public List<ArchiveGridTreeNode> getArchiveTreeGridChildren(ArchiveDestinationModel archiveDestModel,
			ArchiveGridTreeNode loadConfig) {
		logger.debug("getArchiveTreeGridChildren() start");
		ArrayList<ArchiveGridTreeNode> ArchivetreeNodesList = new ArrayList<ArchiveGridTreeNode>();
		if (loadConfig != null){
			String catPath = loadConfig.getCatalogFilePath();
			long volumeHandle = loadConfig.getVolumeHandle();
			String volumeName=loadConfig.getVolumeName();
			long childcount=loadConfig.getChildrenCount();
			IFlashServiceV2 client = null;
			try{
				client = getServiceClient().getServiceV2();
				if (client != null){
					ArchiveDestinationConfig config = ConvertArchiveDestModelToconfig(archiveDestModel);
					if(config == null)
				 		return null;
					ArchiveCatalogItem[] items=new ArchiveCatalogItem[0];
					ArchiveCatalogItemModel[] models=new ArchiveCatalogItemModel[0];
					
					if(archiveDestModel!=null)
						if(archiveDestModel.getArchiveToRPS()!=null&&archiveDestModel.getArchiveToRPS()){
							RpsHost host=ConvertRpsHostModelToRpsHost(archiveDestModel.getRpsHostModel());
							String UUID=archiveDestModel.getPolicyUUID();
							items=client.getAllArchiveCatalogItemsFromRPS(host,archiveDestModel.getCatalogPath(), archiveDestModel.getCatalogFolderUser(),archiveDestModel.getCatalogFolderPassword(), host.getRhostname(),config,volumeName,catPath);
						}else{
					 		items = client.getArchiveCatalogItems(volumeHandle,catPath);
					 		
					 	}
					
					models = ConvertToArchiveCatalogModels(items);

					
					boolean selectable = true;
					
					for (int i = 0; i < models.length; i++)
					{				
						ArchivetreeNodesList.add(ConvertArchiveCataLogToTreeModel(models[i], catPath, selectable,volumeName));
					}
					
					logger.debug("CatalogItemModel length = " + models.length);
				}
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
		}
		logger.debug("getArchiveTreeGridChildren() end");
		return ArchivetreeNodesList;
	}



	
	@Override
	public long getArchiveTreeGridChildrenCount(ArchiveDestinationModel archiveDestModel,ArchiveGridTreeNode loadConfig) {

		long childCount = 0L;

		if (loadConfig != null)
		{
			String catPath = loadConfig.getCatalogFilePath();
			long volumeHandle = loadConfig.getVolumeHandle();
			String volumeName=loadConfig.getVolumeName();
			long childcount=loadConfig.getChildrenCount();
			IFlashServiceV2 client = null;
			try
			{
				client = getServiceClient().getServiceV2();
				if (client != null)
				{
					ArchiveDestinationConfig config = ConvertArchiveDestModelToconfig(archiveDestModel);
					if(archiveDestModel!=null)
						if(archiveDestModel.getArchiveToRPS()!=null&&archiveDestModel.getArchiveToRPS()){
							RpsHost host=ConvertRpsHostModelToRpsHost(archiveDestModel.getRpsHostModel());
							String UUID=archiveDestModel.getPolicyUUID();
							childCount=client.getArchiveCatalogItemsCountFromRPS(host,archiveDestModel.getCatalogPath(),archiveDestModel.getCatalogFolderUser(),archiveDestModel.getCatalogFolderPassword(), host.getRhostname(),config,volumeName,catPath);
						}else{
							childCount = client.getArchiveChildrenCount(volumeHandle,catPath);
					 		
					 	}
				}
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
				e.printStackTrace();
			}
		}		

		return childCount;
	}



	private ArchiveCatalogItemModel[] ConvertToArchiveCatalogModels(
			ArchiveCatalogItem[] in_archiveCatalogItems) {
		logger.debug("ConvertToArchiveCatalogModels() start");
		
		ArchiveCatalogItemModel[] items = new ArchiveCatalogItemModel[in_archiveCatalogItems.length];
		
		for(int iIndex = 0;iIndex < in_archiveCatalogItems.length ; iIndex++)
		{
			ArchiveCatalogItem catItem = in_archiveCatalogItems[iIndex];
			items[iIndex] = new ArchiveCatalogItemModel();
			items[iIndex].setName(catItem.getName());
			items[iIndex].setVolumeHandle(catItem.getVolumeHandle());
			Long lVersionCount = catItem.getVersionsCount();
			items[iIndex].setVersionsCount(lVersionCount.intValue());
			items[iIndex].setPath(catItem.getPath());
			items[iIndex].setFullPath(catItem.getFullPath());
			items[iIndex].setChildrenCount(catItem.getChildrenCount());
			items[iIndex].setType(catItem.getType());
			
			if(lVersionCount != 0)
			{
				if(catItem.getfileVersionsList() != null)
				{
					ArchiveFileVersionNode[] fileVersionsList = new ArchiveFileVersionNode[catItem.getfileVersionsList().length];
					
					ArchiveFileVerionDetail[] fileVersions = catItem.getfileVersionsList();
					
					for(int iFileIndex = 0;iFileIndex < fileVersions.length;iFileIndex++)
					{
						ArchiveFileVerionDetail FileDetail = fileVersions[iFileIndex];
						
						ArchiveFileVersionNode ArchiveFileNode = new ArchiveFileVersionNode();
						ArchiveFileNode.setVersion(FileDetail.getVersion());
						ArchiveFileNode.setFileSize(FileDetail.getFileSize());
						ArchiveFileNode.setModifiedTime(FileDetail.getModifiedTime());
						ArchiveFileNode.setArchivedTime(FileDetail.getArchivedTime());
						ArchiveFileNode.setFileType(FileDetail.getFileType());
						ArchiveFileNode.setArchivedTZOffset(FileDetail.getArchivedTimeZoneOffset());
						ArchiveFileNode.setModifiedTZOffset(FileDetail.getModifiedTimeZoneOffset());
						fileVersionsList[iFileIndex] = ArchiveFileNode;
					}
					items[iIndex].setfileVersionsList(fileVersionsList);
				}
			}
		}
		
		logger.debug("ConvertToArchiveCatalogModels() end");
		return items;
	}

	private ArchiveGridTreeNode ConvertArchiveCataLogToTreeModel(
			ArchiveCatalogItemModel model, String catPath, boolean selectable, String in_VolumeName) {
		
		ArchiveGridTreeNode node = new ArchiveGridTreeNode();

		if (model.getType() == CatalogModelType.File)
		{
			node.setSize( model.getSize() );
		}
		//node.setDate(model.getDate().toString());
		node.setGuid(model.getName());
		node.setName(model.getName());
		
		node.setVolumeHandle(model.getVolumeHandle());
		node.setCatalogFilePath(model.getFullPath());
		node.setType(model.getType());
		node.setArchiveType(model.getArchiveType());
		node.setVersionsCount(model.getVersionsCount());
		node.setPath(model.getPath());
		node.setFullPath(model.getFullPath());
		node.setSelectable(selectable);
		node.setDisplayName(model.getName());
		node.setVolumeName(in_VolumeName);
		node.setChildrenCount(model.getChildrenCount());
		
		node.setChecked(false);		
		node.setDate(model.getDate());
		//node.setId(node.toId().hashCode());
		
		if(model.getVersionsCount() != 0)
		{
			if(model.getfileVersionsList() != null)
			{
				ArchiveFileVersionNode[] fileVersionsList = new ArchiveFileVersionNode[model.getfileVersionsList().length];
				
				ArchiveFileVersionNode[] fileVersions = model.getfileVersionsList();
				
				for(int iFileIndex = 0;iFileIndex < fileVersions.length;iFileIndex++)
				{
					ArchiveFileVersionNode FileDetail = fileVersions[iFileIndex];
					
					ArchiveFileVersionNode ArchiveFileNode = new ArchiveFileVersionNode();
					ArchiveFileNode.setVersion(FileDetail.getVersion());
					ArchiveFileNode.setFileSize(FileDetail.getFileSize());
					ArchiveFileNode.setModifiedTime(FileDetail.getModifiedTime());
					ArchiveFileNode.setArchivedTime(FileDetail.getArchivedTime());
					ArchiveFileNode.setFileType(FileDetail.getFileType());
					ArchiveFileNode.setArchivedTZOffset(FileDetail.getArchivedTZOffset());
					ArchiveFileNode.setModifiedTZOffset(FileDetail.getModifiedTZOffset());
					
					fileVersionsList[iFileIndex] = ArchiveFileNode;
				}
				node.setfileVersionsList(fileVersionsList);
			}
		}
		return node;
	}

	@Override
	public long submitRestoreArchiveJob(RestoreArchiveJobModel in_ArchiveJob)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.info("in submitRestoreArchiveJob");
		IFlashServiceV2 client = null;
		try
		{
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				logger.info("converting to RestoreArchiveJob");
				RestoreArchiveJob archiveJob = ConvertToRestoreArchiveJob(in_ArchiveJob);
				
				
				RestoreJobArchiveNode[] volumes = ConvertArchiveSelectedNodes(in_ArchiveJob);
				
				logger.info("calling backend to submit archive restore job");
				return client.submitArchiveRestoreJob(archiveJob,volumes[0].getPRestoreVolumeAppList());
			}
		}
		catch(WebServiceException e){
			logger.info("exception occured in submitting restore job");			
			logger.info(e.getMessage());
			proccessAxisFaultException(e);
		}
		catch (Exception e)
		{
			logger.info("Exception occured in submitting restore job");
			logger.info(e.getMessage());
		}
		return -1;
	}
	
	//madra04 
	@Override
	public Boolean ValidateRestoreArchiveJob(RestoreArchiveJobModel in_ArchiveJob)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.info("in validateRestoreArchiveJob");
		IFlashServiceV2 client = null;
		try {
			client = getServiceClient().getServiceV2();
			if (client != null) {
				logger.info("converting to RestoreArchiveJob");
				RestoreArchiveJob archiveJob = ConvertToRestoreArchiveJob(in_ArchiveJob);
				RestoreJobArchiveNode[] volumes = ConvertArchiveSelectedNodes(in_ArchiveJob);
				logger.info("calling backend to validate archive restore job");
				return client.ValidateRestoreArchiveJob(archiveJob, volumes[0]
						.getPRestoreVolumeAppList());
			}
		} catch (WebServiceException e) {
			logger.info("exception occured in validating the restore job");
			logger.info(e.getMessage());
			proccessAxisFaultException(e);
		} catch (Exception e) {
			logger.info("Exception occured in validating restore job");
			logger.debug(e.getMessage());
		}
		return true;
		
	}
	
	@Override
	public Boolean ValidateRestoreJob(RestoreArchiveJobModel in_ArchiveJob)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.info("in validateRestoreArchiveJob");
		IFlashServiceV2 client = null;
		try {
			client = getServiceClient().getServiceV2();
			if (client != null) {
				logger.info("converting to RestoreArchiveJob");
				RestoreArchiveJob archiveJob = ConvertToRestoreArchiveJob(in_ArchiveJob);
				RestoreJobArchiveNode[] volumes = ConvertArchiveSelectedNodes(in_ArchiveJob);
				logger.info("calling backend to validate archive restore job");
				return client.ValidateRestoreJob(archiveJob, volumes[0]
						.getPRestoreVolumeAppList());
			}
		} catch (WebServiceException e) {
			logger.info("exception occured in validating the restore job");
			logger.info(e.getMessage());
			proccessAxisFaultException(e);
		} catch (Exception e) {
			logger.info("Exception occured in validating restore job");
			logger.debug(e.getMessage());
		}
		return true;
		
	}

	private RestoreArchiveJob ConvertToRestoreArchiveJob(
			RestoreArchiveJobModel in_ArchiveJob) {
		RestoreArchiveJob archiveJobDetails = new RestoreArchiveJob();
		
		if (in_ArchiveJob.getArchiveDestType() == CloudVendorType.AmazonS3
				.getValue()
				|| in_ArchiveJob.getArchiveDestType() == CloudVendorType.WindowsAzure
						.getValue()
				|| in_ArchiveJob.getArchiveDestType() == CloudVendorType.Eucalyptus
						.getValue())// cloud
		{
			ArchiveCloudDestInfo cloudInfo = ConvertToArchiveCloudDestConfig(in_ArchiveJob
					.getArchiveCloudInfo());
			archiveJobDetails
					.setArchiveDestType(cloudInfo.getcloudVendorType());
			archiveJobDetails.setArchiveCloudInfo(cloudInfo);
			if(cloudInfo.getcloudVendorType() == 0L)
			{
				archiveJobDetails.setRRSFlag(cloudInfo.getRRSFlag());
			}
			else
			{
				archiveJobDetails.setRRSFlag(0L);
			}
		}
		else if(in_ArchiveJob.getArchiveDestType() == 4)//drive
		{
			archiveJobDetails.setArchiveDestType(4);
			ArchiveDiskDestInfo archiveDiskDestConfig = new ArchiveDiskDestInfo();
			
			ArchiveDiskDestInfoModel diskInfo = in_ArchiveJob.getArchiveDiskInfo();
			
			archiveDiskDestConfig.setArchiveDiskDestPath(diskInfo.getArchiveDiskDestPath());
			archiveDiskDestConfig.setArchiveDiskUserName(diskInfo.getArchiveDiskUserName());
			archiveDiskDestConfig.setArchiveDiskPassword(diskInfo.getArchiveDiskPassword());
			archiveJobDetails.setArchiveDiskInfo(archiveDiskDestConfig);
		}
		
		archiveJobDetails.setEncryptedPassword(in_ArchiveJob.getEncrpytionPassword());
		
		archiveJobDetails.setFileSystemOption(getFileSystemOptionFromModel(in_ArchiveJob.getFileSystemOption()));
		archiveJobDetails.setJobType(in_ArchiveJob.getJobType());
		
		if(in_ArchiveJob.getProductType()!=null)
			archiveJobDetails.setProductType(in_ArchiveJob.getProductType());
		else
			archiveJobDetails.setProductType(JobLauncher.D2D.getValue());
		archiveJobDetails.setrestoreDestType(in_ArchiveJob.getDestType());//mentions original or alternate location
		archiveJobDetails.setarchiveRestoreDestinationPath(in_ArchiveJob.getarchiveRestoreDestinationPath());
		archiveJobDetails.setarchiveRestoreUserName(in_ArchiveJob.getarchiveUserName());
		archiveJobDetails.setarchiveRestorePassword(in_ArchiveJob.getarchivePassword());
		
		archiveJobDetails.setRestoreType(in_ArchiveJob.getRestoreType());//sets restore archives by browse or search
		
		//converting selected archive nodes
		archiveJobDetails.setArchiveNodes(ConvertArchiveSelectedNodes(in_ArchiveJob));
		
		
		//set the datastore path in the case of rps filecopy as the datastore dest is being used for filecopy catalog
		//if(in_ArchiveJob.getProductType()!=null && in_ArchiveJob.getProductType()==JobLauncher.RPS.getValue()){
			archiveJobDetails.setCatalogFolderPath(in_ArchiveJob.getCatalogFolderPath());
			archiveJobDetails.setCatalogFolderUser(in_ArchiveJob.getCatalogFolderUser());
			archiveJobDetails.setCatalogFolderPassword(in_ArchiveJob.getCatalogFolderPassword());
		//}
		
		return archiveJobDetails;
	}


	private RestoreJobArchiveNode[] ConvertArchiveSelectedNodes(
			RestoreArchiveJobModel archiveJobModel) {
		RestoreJobArchiveNode[] archiveNode = new RestoreJobArchiveNode[1];
		
		RestoreJobArchiveVolumeNodeModel[] listofArchiveVolumes = archiveJobModel.listofArchiveVolumes;
		
		RestoreJobArchiveVolumeNode[] archiveVolumeNodes = new RestoreJobArchiveVolumeNode[listofArchiveVolumes.length];
				
		for(int iIndex = 0;iIndex < listofArchiveVolumes.length;iIndex++)
		{
			RestoreJobArchiveVolumeNodeModel VolumeNodeModel = listofArchiveVolumes[iIndex];
			archiveVolumeNodes[iIndex] = new RestoreJobArchiveVolumeNode();
			archiveVolumeNodes[iIndex].setdestVolumName("");//for future purpose
			archiveVolumeNodes[iIndex].setPath(VolumeNodeModel.getdestVolumName());
			archiveVolumeNodes[iIndex].setVolItemAppCompList(ConvertToArchiveVolumeSelectedItems(VolumeNodeModel.ArchiveItemsList));
			archiveVolumeNodes[iIndex].setvolItemCount(archiveVolumeNodes[iIndex].getVolItemAppCompList() != null ? archiveVolumeNodes[iIndex].getVolItemAppCompList().length : 0);			
			//archiveVolumeNodes[iIndex] = volumeNode;
		}
		
		RestoreJobArchiveNode myNode = new RestoreJobArchiveNode();
		//archiveNode[0] = new RestoreJobArchiveNode();
		
		//myNode.setNodeName("kappr01-xp1.ca.com");
		myNode.setPRestoreVolumeAppList(archiveVolumeNodes);
		myNode.setNodeName(archiveJobModel.getSessionPath());// setting the selected nodename 
		
		archiveNode[0] = myNode;
		
		//archiveNode[0].setNodeName("kappr01-xp1.ca.com");
		//archiveNode[0].setPRestoreVolumeAppList(archiveVolumeNodes);
		
		return archiveNode;
	}

	private ArchiveVolItemAppComp[] ConvertToArchiveVolumeSelectedItems(RestoreJobArchiveItemNodeModel[] archiveItemsList) {
		
		if(archiveItemsList == null)
			return null;
		
		int iItemsSelected = 0;
		for (RestoreJobArchiveItemNodeModel restoreJobArchiveItemNodeModel : archiveItemsList) {
			if(restoreJobArchiveItemNodeModel != null)
				iItemsSelected++;
			else
				break;
		}
		
		
		ArchiveVolItemAppComp[] volumeItems = new ArchiveVolItemAppComp[iItemsSelected];
		
		for(int iIndex = 0;iIndex < iItemsSelected;iIndex++)
		{
			RestoreJobArchiveItemNodeModel nodeModel = archiveItemsList[iIndex];
			
			volumeItems[iIndex] = new ArchiveVolItemAppComp();
			volumeItems[iIndex].setfileVersion(nodeModel.getVersion() == null ? 0 : nodeModel.getVersion());
			volumeItems[iIndex].setfileorDirFullPath(nodeModel.getFullPath());
			volumeItems[iIndex].setfOptions(nodeModel.getType());
			
			//volumeItems[iIndex] = selectedItem;
		}
		
		return volumeItems;
	}

	private ArchiveCloudDestInfo ConvertToArchiveCloudDestConfig(
			ArchiveCloudDestInfoModel archiveCloudInfo) {
		ArchiveCloudDestInfo cloudInfo = new ArchiveCloudDestInfo();
		
		cloudInfo.setcloudVendorType(archiveCloudInfo.getcloudVendorType());
		cloudInfo.setcloudVendorURL(archiveCloudInfo.getcloudVendorURL());
		cloudInfo.setcloudBucketName(archiveCloudInfo.getcloudBucketName());
		cloudInfo.setCloudSubVendorType(archiveCloudInfo.getCloudSubVendorType());
		if(archiveCloudInfo.getencodedBucketName() != null && archiveCloudInfo.getencodedBucketName().length() > 0)
			cloudInfo.setEncodedCloudBucketName(archiveCloudInfo.getencodedBucketName());
		else
			cloudInfo.setEncodedCloudBucketName(cloudInfo.getcloudBucketName());
		cloudInfo.setcloudVendorUserName(archiveCloudInfo.getcloudVendorUserName());
		cloudInfo.setcloudVendorPassword(archiveCloudInfo.getcloudVendorPassword());
		if(archiveCloudInfo.getcloudVendorType() == 0)
		{
			cloudInfo.setRRSFlag(archiveCloudInfo.getrrsFlag());
		}
		else
		{
			cloudInfo.setRRSFlag(0L);
		}
		if(archiveCloudInfo.getcloudUseProxy())
		{
			cloudInfo.setcloudUseProxy(archiveCloudInfo.getcloudUseProxy());
			cloudInfo.setcloudProxyServerName(archiveCloudInfo.getcloudProxyServerName());
			cloudInfo.setcloudProxyPort(archiveCloudInfo.getcloudProxyPort());
			
			if(archiveCloudInfo.getcloudProxyRequireAuth())
			{
				cloudInfo.setcloudProxyRequireAuth(archiveCloudInfo.getcloudProxyRequireAuth());
				cloudInfo.setcloudProxyPassword(archiveCloudInfo.getcloudProxyPassword());
				cloudInfo.setcloudProxyUserName(archiveCloudInfo.getcloudProxyUserName());
			}
		}
		return cloudInfo;
		
	}

	@Override
	public List<ArchiveGridTreeNode> getArchivableFilesList(ArchiveSourceInfoModel in_SourceInfo)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		
		IFlashServiceV2 client = null;
		try
		{
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				ArchiveSourceInfoModel[] archiveSourceModels = new ArchiveSourceInfoModel[1];
				archiveSourceModels[0] = in_SourceInfo;
				ArchiveSourceInfoConfiguration[] sourceConfig = ConvertArchiveSourceInformation(archiveSourceModels);
				ArchiveFileItem[] fileItems = client.getArchivableFilesList(sourceConfig);
				return ConvertArchiveFilesToModel(fileItems);
			}
		}catch(WebServiceException ex){
			logger.error("Error occurs during validate user...");
			logger.error(ex.getMessage());
			proccessAxisFaultException(ex);
		}
		
		return null;
	}

	private List<ArchiveGridTreeNode> ConvertArchiveFilesToModel(ArchiveFileItem[] fileItems) {
		List<ArchiveGridTreeNode> ArchiveFileNodesList = new ArrayList<ArchiveGridTreeNode>();
		
		for(int iIndex = 0;iIndex < fileItems.length;iIndex++)
		{
			ArchiveFileItem fileItem = fileItems[iIndex];
			
			ArchiveGridTreeNode fileNode = new ArchiveGridTreeNode();
			
			fileNode.setName(fileItem.getfileName());
			fileNode.setSize(fileItem.getSize());
			fileNode.setDate(fileItem.getmodifiedDate());
			fileNode.setType(fileItem.getType());
			fileNode.setPath(fileItem.getPath());
			fileNode.setfOptions(fileItem.getfOptions());
			ArchiveFileNodesList.add(fileNode);
		}
		
		return ArchiveFileNodesList;
	}

	@Override
	public List<CatalogItemModel> searchArchiveDestinationItems(ArchiveDestinationModel archiveDestDetailsModel, String path,
			long in_lSearchOptions,String strfileName,long in_lIndex,long in_lRequiredItemsCount) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		IFlashServiceV2 client = null;
		ArrayList<CatalogItemModel> ArchivetreeNodesList = new ArrayList<CatalogItemModel>();
		try
		{
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				ArchiveDestinationConfig archiveDestConfig = ConvertArchiveDestModelToconfig(archiveDestDetailsModel);
				
				if(archiveDestConfig == null)
					return null;
				
				ArchiveCatalogItemModel[] models = ConvertToArchiveCatalogModels(client.searchArchiveDestinationItems(archiveDestConfig,path,in_lSearchOptions,strfileName,in_lIndex,in_lRequiredItemsCount));
				
				boolean selectable = true;
				
				for (int i = 0; i < models.length; i++)
				{				
					ConvertArchiveCataLogItemToCatalogItem(models[i], "", selectable,"",ArchivetreeNodesList);
				}
				return ArchivetreeNodesList;
			}
		}
		catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		return null;
	}

	private boolean ConvertArchiveCataLogItemToCatalogItem(ArchiveCatalogItemModel archiveCatalogItemModel, String catPath,
			boolean selectable, String volumeName, ArrayList<CatalogItemModel> archivetreeNodesList) 
	{
		if(archiveCatalogItemModel.getType() == 7)
		{
			if(archiveCatalogItemModel.getVersionsCount() != 0)
			{
				if(archiveCatalogItemModel.getfileVersionsList() != null)
				{
					ArchiveFileVersionNode[] fileVersions = archiveCatalogItemModel.getfileVersionsList();
					
					for(int iFileIndex = 0;iFileIndex < fileVersions.length;iFileIndex++)
					{
						ArchiveFileVersionNode FileDetail = fileVersions[iFileIndex];
						
						CatalogItemModel archiveItem = new CatalogItemModel();
						
						if (archiveCatalogItemModel.getType() == CatalogModelType.File)
						{
							archiveItem.setSize( archiveCatalogItemModel.getSize() );
						}
						else
						{
							archiveItem.setChildrenCount(archiveCatalogItemModel.getChildrenCount());
						}
						
						archiveItem.setName(archiveCatalogItemModel.getName());
						archiveItem.setFullPath(archiveCatalogItemModel.getFullPath());
						archiveItem.setPath(archiveCatalogItemModel.getFullPath());
						archiveItem.setId(archiveCatalogItemModel.getVolumeHandle());
						archiveItem.setArchiveVersion(FileDetail.getVersion());
						archiveItem.setType(archiveCatalogItemModel.getType());
						archiveItem.setSize(FileDetail.getFileSize());
						archiveItem.setDate(FileDetail.getModifiedTime());
						archiveItem.setSubSessionNumber(FileDetail.getVersion());
						archiveItem.setArchiveType(archiveCatalogItemModel.getArchiveType());
						
						archivetreeNodesList.add(archiveItem);
					}
				}
			}
		}
		else//folder
		{
			//ArchiveFileVersionNode FileDetail = fileVersions[iFileIndex];
			
			CatalogItemModel archiveItem = new CatalogItemModel();
			
			archiveItem.setChildrenCount(archiveCatalogItemModel.getChildrenCount());
			
			archiveItem.setName(archiveCatalogItemModel.getName());
			archiveItem.setFullPath(archiveCatalogItemModel.getFullPath());
			archiveItem.setPath(archiveCatalogItemModel.getFullPath());
			archiveItem.setId(archiveCatalogItemModel.getVolumeHandle());
			archiveItem.setArchiveVersion(0);
			archiveItem.setType(archiveCatalogItemModel.getType());
			//archiveItem.setSize(FileDetail.getFileSize());
			//archiveItem.setDate(FileDetail.getModifiedTime());
			archiveItem.setSubSessionNumber(0);
			archiveItem.setArchiveType(archiveCatalogItemModel.getArchiveType());
			
			archivetreeNodesList.add(archiveItem);
		}
		
		return true;
	}

	@Override
	public List<FileModel> getSelectedBackupVolumesInfo()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getSelectedBackupVolumesInfo() - start");
		List<FileModel> volumesList = null;
		IFlashServiceV2 client = null;
		try
		{
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				Volume[] backupVolumes = client.getSelectedBackupVolumes();
				
				if (backupVolumes != null)
				{
					volumesList = new ArrayList<FileModel>();
					for (int i = 0; i < backupVolumes.length; i++)
					{
						volumesList.add(ConvertToVolumeModel(backupVolumes[i]));
					}
				}
			}
			else
			{
				logger.debug("getSelectedBackupVolumesInfo() - web client was null");
			}
		}
		catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getSelectedBackupVolumesInfo() - end");
		return volumesList;
	}
	
	@Override
	public List<FileModel> getFATVolumesInfo()throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getFATVolumesInfo() - start");
		List<FileModel> fatVolumesList = null;
		IFlashServiceV2 client = null;
		try
		{
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				Volume[] backupVolumes = client.getFATVolumesList();
				
				if (backupVolumes != null)
				{
					fatVolumesList = new ArrayList<FileModel>();
					for (int i = 0; i < backupVolumes.length; i++)
					{
						fatVolumesList.add(ConvertToVolumeModel(backupVolumes[i]));
					}
				}
			}
			else
			{
				logger.debug("getFATVolumesInfo() - web client was null");
			}
		}
		catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getFATVolumesInfo() - end");
		return fatVolumesList;
	}

	private CatalogInfoModel convertItem2Model(CatalogInfo item)
	{
		CatalogInfoModel model = null;
		
		if (item != null)
		{
			model = new CatalogInfoModel();
			model.setSubSessNo(item.getSubSessNo());
			model.setFlag(item.getFlag());
			model.setAppFlag(item.getAppFlag());
			
			List<CatalogInfo_EDB> list = item.getEdbCatalogInfoList();
			if (list != null)
			{
				List<CatalogInfo_EDB_Model> modelList = new ArrayList<CatalogInfo_EDB_Model>();
				for (int i=0; i<list.size(); i++)
				{
					CatalogInfo_EDB_Model edbModel = new CatalogInfo_EDB_Model();
					edbModel.setEdbName(list.get(i).getEdbName());
					edbModel.setIsCatalogCreated(list.get(i).isCatalogCreated());
					modelList.add(edbModel);
				}
				
				model.setEdbCatalogInfoList(modelList);
			}
		}
		
		return model;
	}
	
	@Override
	public List<CatalogInfoModel> checkCatalogExist(String destination, long sessionNumber)
			throws BusinessLogicException, ServiceConnectException, ServiceInternalException
	{
		logger.debug("checkCatalogExist() start. destination=" + destination + " sessionNumber=" + sessionNumber);
		ArrayList<CatalogInfoModel> arrayModels = new ArrayList<CatalogInfoModel>();

		{
			WebServiceClientProxy client = null;
			try
			{
				client = this.getServiceClient();
				if (client != null)
				{
					CatalogInfo[] items = client.getServiceGRT().checkCatalogExist(destination, sessionNumber);

					for (int i = 0; i < items.length; i++)
					{
						CatalogInfoModel model = convertItem2Model(items[i]);
						arrayModels.add(model);
					}
				}
			}
			catch (Exception e)
			{
				logger.debug(e.getMessage());
			}
		}

		logger.debug("checkCatalogExist() end");
		return arrayModels;
	}
	
	@Override
	public long saveD2DConfiguration(D2DSettingModel d2dSettingModel) throws BusinessLogicException,
		ServiceConnectException, ServiceInternalException {
		long ret = -1;
		WebServiceClientProxy client = null;
		
		client = this.getServiceClient();
		if(client != null) {
			//d2d backup setting
			BackupSettingsModel backupSettingsModel = d2dSettingModel.getBackupSettingsModel();
			
			BackupConfiguration configuration = ConvertToConfiguration(backupSettingsModel);
		
			BackupSchedule fSched = ConvertToBackupSchedule(backupSettingsModel.fullSchedule);
			configuration.setFullBackupSchedule(fSched);
		
			BackupSchedule iSched = ConvertToBackupSchedule(backupSettingsModel.incrementalSchedule);
			configuration.setIncrementalBackupSchedule(iSched);
		
			BackupSchedule rSced = ConvertToBackupSchedule(backupSettingsModel.resyncSchedule);
			configuration.setResyncBackupSchedule(rSced);
		
			//BackupEmail email = ConvertToBackupEmail(backupSettingsModel);
			//configuration.setEmail(email);
		
			//wanqi06
			if(backupSettingsModel.advanceScheduleModel != null){
				AdvanceSchedule advanceSchedule = convertToAdvanceSchedule(backupSettingsModel.advanceScheduleModel);
				configuration.setAdvanceSchedule(advanceSchedule);
			}
			
			BackupVolumes volumes = ConvertToBackupVolume(backupSettingsModel.backupVolumes);
			configuration.setBackupVolumes(volumes);
		
			/*	AutoUpdateSettings autoUpdateSettings = ConvertToSelfUpdateSettings(model.getautoUpdateSettings());
			configuration.setUpdateSettings(autoUpdateSettings);*/
		
			SRMPkiAlertSetting alertSetting = ConvertToSRMPkiAlertSetting(backupSettingsModel.srmAlertSetting);
			configuration.setSrmPkiAlertSetting(alertSetting);
			
			// zhazh06. retention policy
//			RetentionSetting retetionSettings = convertToRetentionSetting (backupSettingsModel.retentionModel);
//			configuration.setBackupRetention(retetionSettings);
			
			
			//d2d preference
			PreferencesModel preferencesModel = d2dSettingModel.getPreferencesModel();
			PreferencesConfiguration preConfig = null;
			if(preferencesModel!=null) {
				preConfig = convertToPreferencesConfiguration(preferencesModel);
			}
			
			//archive seting model
			ArchiveSettingsModel archiveSettingsModel = d2dSettingModel.getArchiveSettingsModel();
			ArchiveConfiguration archiveConfiguration = null;			
			if(archiveSettingsModel!=null) {
				BackupSettingsModel backupConfig = d2dSettingModel.getBackupSettingsModel();
				archiveSettingsModel.setbackupVolumes(backupConfig.getBackupVolumes());
				archiveSettingsModel.setBackupDestination(backupConfig.getDestination());
				archiveConfiguration = convertToArchiveConfiguration(archiveSettingsModel);
			}
			
			//file archive seting model
			ArchiveSettingsModel fileArchiveSettingsModel = d2dSettingModel.getFileArchiveSettingsModel();
			ArchiveConfiguration fileArchiveConfiguration = null;			
			if(fileArchiveSettingsModel!=null) {
				BackupSettingsModel backupConfig = d2dSettingModel.getBackupSettingsModel();
				fileArchiveSettingsModel.setbackupVolumes(backupConfig.getBackupVolumes());
				fileArchiveSettingsModel.setBackupDestination(backupConfig.getDestination());
				fileArchiveConfiguration = convertToArchiveConfiguration(fileArchiveSettingsModel);
			}
			
			// scheduled export settings model
			ScheduledExportSettingsModel scheduledExportSettingsModel = d2dSettingModel.getScheduledExportSettingsModel();
			ScheduledExportConfiguration scheduledExportConfiguration = null;
			if(scheduledExportSettingsModel != null) {
				scheduledExportConfiguration = convertToConfiguration(scheduledExportSettingsModel);
			}
			
			D2DConfiguration d2dConfiguration = new D2DConfiguration();
			d2dConfiguration.setBackupConfiguration(configuration);
			d2dConfiguration.setPreferencesConfiguration(preConfig);
			d2dConfiguration.setArchiveConfiguration(archiveConfiguration);
			d2dConfiguration.setArchiveDelConfiguration(fileArchiveConfiguration);
			d2dConfiguration.setScheduledExportConfiguration(scheduledExportConfiguration);
		
			try {
				
				ret = client.getServiceV2().saveD2DConfiguration(d2dConfiguration);
				
			} catch (WebServiceException e) {
				logger.error("Error occurs during saveD2DConfiguration()...");
				logger.error(e.getMessage());
				logger.error(e.getStackTrace());		
				proccessAxisFaultException(e);
		
			}
		
			if (ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE) - FlashServiceErrorCode.BackupConfig_BASE)) {
				throw this
						.generateException(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE);
			}
			else if(ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING) - FlashServiceErrorCode.BackupConfig_BASE))
				throw generateException(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING);
			
		}

		return ret;
	}
	
	@Override
	public D2DSettingModel getD2DConfiguration() throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getD2DConfiguration() - start");
		IFlashServiceV2 client = null;
		try
		{
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				D2DConfiguration d2dConfiguration = client.getD2DConfiguration();
				
				if(d2dConfiguration!=null) {
					BackupSettingsModel backupSettingsModel = null;
					PreferencesModel preferencesModel = null;
					ArchiveSettingsModel archiveSettingsModel = null;
					ArchiveSettingsModel fileArchiveSettingsModel = null;
					ScheduledExportSettingsModel scheduledExportSettingsModel = null;
					
					BackupConfiguration backupConfiguration = d2dConfiguration.getBackupConfiguration();
					if(backupConfiguration!=null) {
						backupSettingsModel = ConvertToModel(backupConfiguration);
						try {
							long offSet = client.getServerTimezoneOffsetByMillis(backupConfiguration.getBackupStartTime());
							backupSettingsModel.setStartTimezoneOffset(offSet);
						}catch(Exception e) {
							logger.error("Failed to set timezone offset", e);
						}
					}
					
					PreferencesConfiguration preferencesConfiguration = d2dConfiguration.getPreferencesConfiguration();
					if(preferencesConfiguration!=null) {
						preferencesModel = ConvertToPreferencesModel(preferencesConfiguration);
					}
					ArchiveConfiguration archiveConfiguration = d2dConfiguration.getArchiveConfiguration();
					if(archiveConfiguration!=null) {
						archiveSettingsModel = ConvertToArchiveConfigModel(archiveConfiguration);
					}
					
					ArchiveConfiguration fileArchiveConfiguration = d2dConfiguration.getArchiveDelConfiguration();
					if(fileArchiveConfiguration!=null) {
						fileArchiveSettingsModel = ConvertToArchiveConfigModel(fileArchiveConfiguration);
					}
					
					ScheduledExportConfiguration scheduledExportConfiguration = d2dConfiguration.getScheduledExportConfiguration();
					if(scheduledExportConfiguration!=null) {
						scheduledExportSettingsModel = convertToModel(scheduledExportConfiguration);
					}
					
					D2DSettingModel d2dSettingModel = new D2DSettingModel();
					d2dSettingModel.setBackupSettingsModel(backupSettingsModel);
					d2dSettingModel.setPreferencesModel(preferencesModel);
					d2dSettingModel.setArchiveSettingsModel(archiveSettingsModel);
					d2dSettingModel.setFileArchiveSettingsModel(fileArchiveSettingsModel);
					d2dSettingModel.setScheduledExportSettingsModel(scheduledExportSettingsModel);
					
					return d2dSettingModel;
				}
				
				return null;
			}
			else
			{
				logger.debug("getD2DConfiguration() - web client was null");
			}
		}
		catch (WebServiceException e) {
			
			this.proccessAxisFaultException(e);
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getD2DConfiguration() - end");
		return null;
	}

	public SubscriptionModel ConvertToSubscriptionModel(SubscriptionConfiguration config) {
		SubscriptionModel model=new SubscriptionModel();
		model.setcloudUseProxy(config.isCloudUseProxy());
		if(config.isCloudUseProxy())
		{
			model.setcloudProxyServerName(config.getCloudProxyServerName());
			model.setcloudProxyPort(config.getCloudProxyPort());
			
			model.setcloudProxyRequireAuth(config.isCloudProxyRequireAuth());
			if(config.isCloudProxyRequireAuth())
			{
				model.setcloudProxyUserName(config.getCloudProxyUserName());
				model.setcloudProxyPassword(config.getCloudProxyPassword());
			}
		}
		
		model.setcloudVendorType(config.getCloudVendorType());
		model.setcloudVendorURL(config.getCloudVendorURL());
		model.setStorageKey(config.getStorageKey());
		model.setUserName(config.getUserName());
		model.setPassword(config.getPassword());
		model.setRegion(config.getRegion());
		model.setServerName(config.getServerName());
		return model;
	}

	@Override
	public String GetHostName() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		
		try{
		client = this.getServiceClient();
		if (client != null) {
			return client.getServiceV2().GetArchiveDNSHostName();
		}
		}  catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		return null;
	}

	@Override
	public CloudModel[] getCloudBuckets(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		
		try{
			client = this.getServiceClient();
			if (client != null) {
				ArchiveCloudDestInfo info = ConvertCloudConfigModel(in_cloudInfo);
				//do not send the bucket name to get the buckets list because it will create the bucket if it not exist.
				info.setcloudBucketName("");
				info.setEncodedCloudBucketName("");
				String[] buckets = client.getServiceV2().getCloudBuckets(info);
				if((buckets!=null) && (buckets.length==1) && buckets[0].startsWith("Error_"))
				{
					CloudModel[] modelArray = new CloudModel[1];
					CloudModel cloudModel = new CloudModel();
					cloudModel.setBucketName(buckets[0]);
					Vector<CloudModel> modelVector = new Vector<CloudModel>(1);
					modelVector.add(cloudModel);
					modelVector.copyInto(modelArray);
					return modelArray;				
					
				}
				Vector<CloudModel> vector = new Vector<CloudModel>(buckets.length);
				for (int i = 0; i < buckets.length; i++) {
//					if (buckets[i].startsWith(cloudBucketD2DF2CLabel) || buckets[i].startsWith(cloudBucketD2DLabel)) {
//						String hostName = extractHostName(buckets[i]);
//						String encodedHostName = hostName.replace("-", "%");
//						String actualHostName = "";
//						try {
//							actualHostName = decodeBucketHostName(encodedHostName);
//						} catch (Exception e) {
//							continue;
//						}
//						String bucketName = buckets[i].replace(hostName,actualHostName);
//						CloudModel model = new CloudModel();
//						model.setBucketName(bucketName);
//						model.setEncodedBucketName(buckets[i]);
//						vector.add(model);
//					} else if (buckets[i].startsWith(cloudBucketD2DArchiveLabel)) {
//						CloudModel model = new CloudModel();
//						model.setBucketName(buckets[i]);
//						model.setEncodedBucketName(buckets[i]);
//						vector.add(model);
//					}
					String bucketName = postFormatBucketName(buckets[i]);
					CloudModel model = new CloudModel();
					model.setBucketName(bucketName);
					model.setEncodedBucketName(buckets[i]);
					vector.add(model);
				}
				CloudModel[] decodedBuckets = new CloudModel[vector.size()];
				vector.copyInto(decodedBuckets);
				return decodedBuckets;
			}
			}  catch (WebServiceException e) {
				this.proccessAxisFaultException(e);
			}
			return null;
	}
	
	@Override
	public CloudModel getRegionForBucket(ArchiveCloudDestInfoModel in_cloudInfo)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
			WebServiceClientProxy client = null;
		
		try{
			client = this.getServiceClient();
			if (client != null) {
				
//				if(in_cloudInfo.getcloudBucketName().startsWith(cloudBucketD2DF2CLabel) || in_cloudInfo.getcloudBucketName().startsWith(cloudBucketD2DLabel))
//				{
//					try
//					{
//						//encode the bucket and send
//						String hostName = extractHostName(in_cloudInfo.getcloudBucketName());
//						String encodedHostName = encodeBucketHostName(hostName);
//						String bucketName = in_cloudInfo.getcloudBucketName().replace(hostName, encodedHostName);					
//						in_cloudInfo.setcloudBucketName(bucketName);	
//						in_cloudInfo.setencodedBucketName(bucketName);
//					}
//					catch(Exception e)
//					{
//						logger.info("getRegionForBucket - Exception while encoding the bucket hostname");
//					}
//				}	
				String encodedBucketName = preFormatBucketName(in_cloudInfo.getcloudBucketName());
				in_cloudInfo.setcloudBucketName(encodedBucketName);
				in_cloudInfo.setencodedBucketName(encodedBucketName);
				
				String region =  client.getServiceV2().getRegionForBucket(ConvertCloudConfigModel(in_cloudInfo));
//				String encodedBucketName = in_cloudInfo.getencodedBucketName();
				CloudModel model = new CloudModel();
				model.setRegion(region);
				model.setEncodedBucketName(encodedBucketName);
				return model;
				
			}
			}  catch (WebServiceException e) {
				this.proccessAxisFaultException(e);
			}
			return null;
	}
	

	@Override
	public String[] getCloudRegions(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try{
			client = this.getServiceClient();
			if (client != null) {
//				if(in_cloudInfo.getcloudBucketName().startsWith(cloudBucketD2DF2CLabel)|| in_cloudInfo.getcloudBucketName().startsWith(cloudBucketD2DLabel))
//				{
//					try
//					{
//						//encode the bucket and send
//						String hostName = extractHostName(in_cloudInfo.getcloudBucketName());
//						String encodedHostName = encodeBucketHostName(hostName);
//						String bucketName = in_cloudInfo.getcloudBucketName().replace(hostName, encodedHostName);					
//						in_cloudInfo.setcloudBucketName(bucketName);
//						in_cloudInfo.setencodedBucketName(bucketName);
//					}
//					catch(Exception e)
//					{
//						logger.info("getCloudRegions - Exception while encoding the bucket hostname");
//					}
//				}
				String encodedBucketName = preFormatBucketName(in_cloudInfo.getcloudBucketName());
				in_cloudInfo.setcloudBucketName(encodedBucketName);
				in_cloudInfo.setencodedBucketName(encodedBucketName);
				return client.getServiceV2().getCloudRegions(ConvertCloudConfigModel(in_cloudInfo));
			}
			}  catch (WebServiceException e) {
				this.proccessAxisFaultException(e);
			}
			return null;
	}

	@Override
	public long testConnectionToCloud(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try{
			client = this.getServiceClient();
			if (client != null) {				
//				if(in_cloudInfo.getcloudBucketName().startsWith(cloudBucketD2DF2CLabel) || in_cloudInfo.getcloudBucketName().startsWith(cloudBucketD2DLabel))
//				{
//					try
//					{
//						//encode the bucket and send
//						String hostName = extractHostName(in_cloudInfo.getcloudBucketName());
//						String encodedHostName = encodeBucketHostName(hostName);
//						String bucketName = in_cloudInfo.getcloudBucketName().replace(hostName, encodedHostName);					
//						in_cloudInfo.setcloudBucketName(bucketName);	
//					}
//					catch(Exception e)
//					{
//						logger.info("testConnectionToCloud - Exception while encoding the bucket hostname");
//					}
//				}
				String encodedBucketName = preFormatBucketName(in_cloudInfo.getcloudBucketName());
				in_cloudInfo.setcloudBucketName(encodedBucketName);
				return client.getServiceV2().testConnection(ConvertCloudConfigModel(in_cloudInfo));
			}
			}  catch (WebServiceException e) {
				this.proccessAxisFaultException(e);
			}
			return -2;
	}
	
	@Override
	public CloudModel verifyBucketNameWithCloud(ArchiveCloudDestInfoModel in_cloudInfo) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		CloudModel model = new CloudModel();
		try{
			client = this.getServiceClient();
			if (client != null) {
				long ret = 0L;
				//long ret = validateBucketName(in_cloudInfo.getcloudBucketName());
//				String bucketName = "";
//				try
//				{
//					String hostName = extractHostName(in_cloudInfo.getcloudBucketName());
//					String encodedHostName = encodeBucketHostName(hostName);
//					bucketName = in_cloudInfo.getcloudBucketName().replace(hostName, encodedHostName);
//					in_cloudInfo.setcloudBucketName(bucketName.toString());
//					in_cloudInfo.setencodedBucketName(bucketName.toString());
//					
//				}catch(Exception e)
//				{
//					logger.info("verifyBucketNameWithCloud - Exception while encoding the bucket hostname");
//				}
				String encodedBucketName = preFormatBucketName(in_cloudInfo.getcloudBucketName());
				in_cloudInfo.setcloudBucketName(encodedBucketName);
				in_cloudInfo.setencodedBucketName(encodedBucketName);
				/*StringBuffer bucketName =  new StringBuffer();
				bucketName.append("d2dfc-v2-");
				bucketName.append(encodedHostName+"-");
				bucketName.append(in_cloudInfo.getcloudActualBucketName());*/	
				
				if(in_cloudInfo.getcloudVendorType() == 0 || in_cloudInfo.getcloudVendorType() == 5)
				     ret = validateBucketName(in_cloudInfo.getcloudBucketName(),false);
				else if(in_cloudInfo.getcloudVendorType() == 1)
					 ret = validateBucketNameForAzure(in_cloudInfo.getcloudBucketName(),false);
				
				
				model.setEncodedBucketName(encodedBucketName);
				
				if(ret==0L)
				{
					model.setResult(client.getServiceV2().verifyBucketName(ConvertCloudConfigModel(in_cloudInfo)));
					return model;
				}
				else if (ret!=0L)
				{
					model.setResult(ret);
					return model;
				}
				else
				{
					model.setResult(-1L);
					return model;
				}
			}
			}  catch (WebServiceException e) {
				this.proccessAxisFaultException(e);
			}
			model.setResult(-2L);
			return model;
	}
	
/*	private String extractHostName(String bucketName)
	{		
		int index  =  bucketName.indexOf("-");
		int startIndex = bucketName.indexOf("-", index+1);
		//int endIndex    = bucketName.lastIndexOf("-");		
		return bucketName.substring(startIndex+1);
		
	}
	
	private String encodeBucketHostName(String bucketHostName) throws Exception{		
		return BucketNameEncoder.encodeWithUTF8(bucketHostName);
	}*/
	
	private String decodeBucketHostName(String encodedBucketHostName) throws Exception {
		String hostName = URLDecoder.decode(encodedBucketHostName, "UTF-8");
		return hostName;
	}
	
	private boolean isMinBucketLengthAllowed(String bucket, boolean isForEdge)
	{
		int MIN_BUCKET_LENGTH = 2; 	
		boolean isMinBucketLengthAllowed = false;
			
		if(isForEdge)
		{	
			MIN_BUCKET_LENGTH = 1;
			if(bucket.length() >= MIN_BUCKET_LENGTH)
				isMinBucketLengthAllowed = true;
		}else
		{
			if(bucket.length() > MIN_BUCKET_LENGTH)
				isMinBucketLengthAllowed = true;
		}
		
		return isMinBucketLengthAllowed;
	}
	
	

	@Override
	public long validateBucketName(String bucket, boolean isForEdge) throws BusinessLogicException, ServiceConnectException {
		if (isForEdge) {
			return BUCKET_FORMAT_SUCCESS;
		}
		
		try {					
			if (bucket != null && isMinBucketLengthAllowed(bucket,isForEdge) && bucket.length() <= 63) {
				// Pattern bucketPattern =
				// Pattern.compile("^[\\w][\\w.-]*[\\w]$");
				Pattern bucketPattern = Pattern
						.compile("^[a-z0-9][a-z0-9.-]*[a-z0-9]$");
				Matcher bucketMatch = bucketPattern.matcher(bucket);
				if (bucketMatch.matches()) {
					Pattern squencePattern = Pattern
							.compile("[.][-]|[-][.]|[.][.]|[-][-]");
					Matcher squenceMatcher = squencePattern.matcher(bucket);
					if (!(squenceMatcher.find())) {
						Pattern ipPattern = Pattern
								.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)."
										+ "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){2}"
										+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
						Matcher ipMatch = ipPattern.matcher(bucket);
						if (!(ipMatch.matches()))
							return BUCKET_FORMAT_SUCCESS;
						else
							return BUCKET_IPFORMAT_ERROR;
					} else
						return BUCKET_SEQUENCE_ERROR;

				} else
					return BUCKET_ERROR;

			} else
				return BUCKET_LENGTH_ERROR;

		} catch (Exception e) {
			return BUCKET_EXCEPTION;
		}
	}

	public long validateBucketNameForAzure(String bucket, boolean isForEdge)
				throws BusinessLogicException, ServiceConnectException {try {
					
						
					if (bucket != null && isMinBucketLengthAllowed(bucket,isForEdge) && bucket.length() <= 63) {
					// Pattern bucketPattern =
					// Pattern.compile("^[\\w][\\w.-]*[\\w]$");
					Pattern bucketPattern = Pattern
							.compile("^[a-z0-9][a-z0-9-]*[a-z0-9]$");
					Matcher bucketMatch = bucketPattern.matcher(bucket);
					if (bucketMatch.matches()) {
						Pattern squencePattern = Pattern
								.compile("[-][-]");
						Matcher squenceMatcher = squencePattern.matcher(bucket);
						if (!(squenceMatcher.find())) {
							Pattern ipPattern = Pattern
									.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)."
											+ "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){2}"
											+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
							Matcher ipMatch = ipPattern.matcher(bucket);
							if (!(ipMatch.matches()))
								return BUCKET_FORMAT_SUCCESS;
							else
								return BUCKET_IPFORMAT_ERROR;
						} else
							return BUCKET_SEQUENCE_ERROR;

					} else
						return BUCKET_ERROR;

				} else
					return BUCKET_LENGTH_ERROR;

			} catch (Exception e) {
				return BUCKET_EXCEPTION;
			}
		}
		
	// go through the tree recursively to get all the EDB nodes
	private void getTreeGridChildren_EDB(GridTreeNode currentNode, List<GridTreeNode> edbNodeList)
	{
		if (currentNode != null && currentNode.getType() != null)
		{
			if (CatalogModelType.OT_GRT_EXCH_MBSDB == currentNode.getType().intValue())
			{				
				edbNodeList.add(currentNode);
				return;
			}
			else if (CatalogModelType.OT_GRT_EXCH_PUBLIC_FOLDERS == currentNode.getType().intValue())
			{
				return;
			}
			else
			{
				List<GridTreeNode> childrenNodeList = this.getTreeGridChildren(currentNode);
				
				for (int i=0; i<childrenNodeList.size(); i++)
				{
					// display path
					StringBuilder parentDisplayPath = new StringBuilder();
					
					if (currentNode.getDisplayPath() != null)
					{
						parentDisplayPath.append(currentNode.getDisplayPath());						
					}

					if (currentNode.getType().intValue() == CatalogModelType.OT_VSS_EXCH_WRITER
							|| currentNode.getType().intValue() == CatalogModelType.OT_VSS_EXCH_LOGICALPATH)
					{
						if (parentDisplayPath.length() > 0)
						{
							parentDisplayPath.append("\\");
						}
						parentDisplayPath.append(currentNode.getDisplayName());
					}
					
					childrenNodeList.get(i).setDisplayPath(parentDisplayPath.toString());
					
					getTreeGridChildren_EDB(childrenNodeList.get(i), edbNodeList);
				}				
			}
		}

		return;
	}
	
	private GridTreeNode ConvertGRTRecoveryPointItemToGridTreeNode(RecoveryPointItemModel m) {

		// refer to ExchangeGRTRecoveryPointsPanel
		String GUID_EXCHANGE_WRITER = "Microsoft Exchange Writer";
		String GUID_EXCHANGE_REPLICA_WRITER = "Microsoft Exchange Replica Writer";


		GridTreeNode node = new GridTreeNode();
		node.setDate(null);
		node.setSize(m.getVolDataSizeB());
		node.setName(m.getDisplayName());
		node.setDisplayName(m.getDisplayName());
		node.setCatalofFilePath(m.getCatalogFilePath());
		node.setChildrenCount(m.getChildrenCount());
		// First Level use -1
		node.setParentID(-1l);
		node.setChecked(false);
		node.setSubSessionID(new Long(m.getSubSessionID()).intValue());
		
		if (m.getDisplayName().startsWith(GUID_EXCHANGE_WRITER)
				|| m.getDisplayName().startsWith(GUID_EXCHANGE_REPLICA_WRITER))
		{
			node.setType(CatalogModelType.OT_VSS_EXCH_WRITER);
			node.setPath(m.getGuid());
		}	
		else
		{
			// others will filtered out
			node.setType(-1);
			node.setGuid(m.getGuid());			
		}
		
		return node;
	}

	@Override
	public RecoveryPointResultModel getRecoveryPointItems_EDB(String dest, String domain, String user, String pwd,
			String subPath, long sessionNumber) throws BusinessLogicException, ServiceConnectException, ServiceInternalException
	{
		logger.debug("getRecoveryPointItems_EDB(String, String, String, String, String, long) - start");
		logger.debug("dest:" + dest);
		logger.debug("domain:" + domain);
		logger.debug("user:" + user);
		logger.debug("pwd:");
		logger.debug("subPath:" + subPath);
		logger.debug("sessionNumber:" + sessionNumber);
		
		RecoveryPointResultModel result = new RecoveryPointResultModel();
		
		try
		{
			// Step 1: get the recovery point list
			List<RecoveryPointItemModel> recItemModelList = getRecoveryPointItems(dest, domain, user, pwd, subPath);
			result.setListRecoveryPointItems(recItemModelList);			
			
			// Step 2.1: get the root nodes			
			ArrayList<GridTreeNode> rootNodeList = new ArrayList<GridTreeNode>();			
			for (int i = 0; i < recItemModelList.size(); i++)
			{
				GridTreeNode node = ConvertGRTRecoveryPointItemToGridTreeNode(recItemModelList.get(i));
				
				// filter out non-Exchange nodes
				if (node != null && CatalogModelType.allExchangeTypes.contains(node.getType()))
				{
					rootNodeList.add(node);
				}
			}

			// Step 2.2: go through the tree recursively to get all the EDB nodes
			List<GridTreeNode> edbNodeList = new ArrayList<GridTreeNode>();
			for (int i = 0; i < rootNodeList.size(); i++)
			{
				getTreeGridChildren_EDB(rootNodeList.get(i), edbNodeList);
			}
			
			result.setListEdbNodes(edbNodeList);			
			
			// Step 3: get the catalogInfo 
			List<CatalogInfoModel> catalogInfoModelList = checkCatalogExist(dest, sessionNumber);
			result.setListCatalogInfo(catalogInfoModelList);

		}
		catch (WebServiceException exception)
		{
			proccessAxisFaultException(exception);
		}
		
		logger.debug("result:" + (result.getListEdbNodes() == null ? "null" : result.getListEdbNodes().size()));
		logger.debug("getRecoveryPointItems_EDB end");	
		
		return result;
	}
	
		@Override
		//TO FIX
	public PagingLoadResult<ArchiveGridTreeNode> getArchivePagingGridTreeNode(ArchiveDestinationModel archiveDestModel,
			ArchiveGridTreeNode parent, PagingLoadConfig pageCfg)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		
		ArrayList<ArchiveGridTreeNode> archiveTreeNodesList = new ArrayList<ArchiveGridTreeNode>();
		
		
		int totalCount = 0;	
				
				int start = pageCfg.getOffset();
				int size = pageCfg.getLimit();
				
					if (parent != null)
					{
						String catPath = parent.getCatalogFilePath();
						long volumeHandle = parent.getVolumeHandle();
						String volumeName=parent.getVolumeName();
						IFlashServiceV2 client = null;
						try
						{
							client = getServiceClient().getServiceV2();
							if (client != null)
							{
								// TO DO: change the lines below to get the children count seperately -start
								/*ArchiveCatalogItemModel[] allModels = ConvertToArchiveCatalogModels(client.getArchiveCatalogItems(volumeHandle,catPath));
								totalCount = allModels.length;*/
								Long childCountVal= parent.getChildrenCount();
								totalCount=childCountVal.intValue();
								ArchiveDestinationConfig config = ConvertArchiveDestModelToconfig(archiveDestModel);
								//end
								ArchiveCatalogItem[] items=new ArchiveCatalogItem[0];

								if(archiveDestModel!=null)
									if(archiveDestModel.getArchiveToRPS()!=null&&archiveDestModel.getArchiveToRPS()){
										RpsHost host=ConvertRpsHostModelToRpsHost(archiveDestModel.getRpsHostModel());
										String UUID=archiveDestModel.getPolicyUUID();
										items=client.getArchiveCatalogItemsFromRPS(host,archiveDestModel.getCatalogPath(),archiveDestModel.getCatalogFolderUser(),archiveDestModel.getCatalogFolderPassword(), host.getRhostname(),config,volumeName,catPath,start+1,size);
									}else{
								 		items = client.getArchivePagedCatalogItems(volumeHandle,catPath,start+1,size);
								 		
								 	}
								
								
								ArchiveCatalogItemModel[] models = ConvertToArchiveCatalogModels(items);

								boolean selectable = true;

								for (int i = 0; i < models.length; i++)
								{				
									archiveTreeNodesList.add(ConvertArchiveCataLogToTreeModel(models[i], catPath, selectable,parent.getVolumeName()));
								}

								logger.debug("CatalogItemModel length = " + models.length);
							}
						}
						catch (Exception e)
						{
							logger.debug(e.getMessage());
						}
					}
				//int totalLen = (int) archiveTreeNodesList.size();
				
				logger.debug("getArchiveTreeGridChildren() end");

				return new BasePagingLoadResult<ArchiveGridTreeNode>(archiveTreeNodesList, start,totalCount);		
	}

//	@Override
//	public int validateProxyInfo(VSphereBackupSettingModel proxy)
//			throws BusinessLogicException, ServiceConnectException,
//			ServiceInternalException {
//		int ret = 0;
//		String protocol = proxy.vSphereProxyModel.getVSphereProxyProtocol();
//		String name = proxy.vSphereProxyModel.getVSphereProxyName();
//		String usernameOld = proxy.vSphereProxyModel.getVSphereProxyUsername();
//		String password = proxy.vSphereProxyModel.getVSphereProxyPassword();
//		int port = proxy.vSphereProxyModel.getvSphereProxyPort();
//		String username = getUserName(usernameOld);
//		String domain = getDomainNameFromUserName(usernameOld);
//		
//		/**
//		 * qiubo01: To fix issue - EDGE R16 update 4 create/edit vSphere policy failed with old version proxy.
//		 * This is because IFlashServiceV2 is not a compatible interface.
//		 * When validate a proxy with old version, the web service initialization will fail.
//		 * Instead, use compatible interface ID2D4EdgeVSphere.
//		 */
//		try
//		{
//			ID2D4EdgeVSphere service = ServiceProviders.getRemoteFlashServiceProvider().create(protocol, name, port, ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE).getServiceForEdgeVSphere();
//			service.validateUser(username, password, domain);
//			//check the SaaS node
//			VersionInfo versionInfo = service.getVersionInfo();
//
//			double d2dVersion = 0;
//			double d2dUpdateVersionNumber = 0;
//			try{
//				d2dVersion = Double.parseDouble(versionInfo.getMajorVersion()+ "." + versionInfo.getMinorVersion());
//			}
//			catch(Exception e){
//				d2dVersion = 0;
//			}
//			try{
//				d2dUpdateVersionNumber = Double.parseDouble(versionInfo.getUpdateNumber());
//			}
//			catch(Exception e){
//				d2dUpdateVersionNumber = 0;		
//			}
//			//check version and policy info
//			if (isOlderProxyVersion(d2dVersion, d2dUpdateVersionNumber)) {
//				if(proxy.retentionPolicy.isUseTimeRange() || proxy.retentionPolicy.isUseBackupSet()){
//					String locale = this.getServerLocale();
//					String erorMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
//							+ FlashServiceErrorCode.VSPHERE_LOWER_VERSION_PROXY_WITH_NEW_POLICY, locale), name);
//					throw new ServiceConnectException(FlashServiceErrorCode.VSPHERE_LOWER_VERSION_PROXY_WITH_NEW_POLICY, erorMsg);
//				}
//			}
//			else{
//			if((versionInfo!=null)&&(versionInfo.getProductType()!=null)&&(versionInfo.getProductType().equals("1"))){
//				String locale = this.getServerLocale();
//				String erorMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
//								+ FlashServiceErrorCode.VSPHERE_PROXY_IS_SAAS_NODE, locale), name);
//				throw new BusinessLogicException(FlashServiceErrorCode.VSPHERE_PROXY_IS_SAAS_NODE, erorMsg);
//			}else{
//				//ret = service.checkVIXStatus();
//			}
//			}
//				
//		}catch (WebServiceException exception){
//			logger.error("Error occurs during validateProxyInfo can't connect that webservice");
//			/*if (exception.getCause() instanceof ConnectException || exception.getCause() instanceof SocketException
//					|| exception.getCause() instanceof UnknownHostException	|| exception.getCause() instanceof SSLHandshakeException || exception.getCause() instanceof FileNotFoundException) {
//					String locale = this.getServerLocale();
//					throw new ServiceConnectException(
//							FlashServiceErrorCode.Common_CantConnectService,
//							ResourcesReader.getResource("ServiceError_"
//									+ FlashServiceErrorCode.Common_CantConnectService,
//									locale));
//				}*/
//			String locale = this.getServerLocale();
//			if(exception.getCause() instanceof UnknownHostException){
//				throw new ServiceConnectException(
//						FlashServiceErrorCode.Common_CantConnectRemoteServer,
//						ResourcesReader.getResource("ServiceError_"
//								+ FlashServiceErrorCode.Common_CantConnectRemoteServer,
//								locale)); 
//			}
//			if(exception.getCause() instanceof ConnectException || exception.getCause() instanceof SSLException || exception.getCause() instanceof SocketException){
//				throw new ServiceConnectException(
//						FlashServiceErrorCode.Common_CantConnectService,
//						ResourcesReader.getResource("ServiceError_"
//								+ FlashServiceErrorCode.Common_CantConnectService,
//								locale)); 
//			} 
//			if (exception.getCause() instanceof Error && exception.getMessage().startsWith(UNDEFINED_OPERATION_NAME)) {
//				throw generateException(FlashServiceErrorCode.EDGE_D2D_INTERFACE_MISMATCH);
//			}
//			
//			if (exception instanceof SOAPFaultException) {
//				SOAPFaultException se = (SOAPFaultException) exception;
//				if (se.getFault() != null
//						&& (FlashServiceErrorCode.Login_WrongCredential
//								.equals(se.getFault().getFaultCodeAsQName()
//										.getLocalPart()))) {
//					BusinessLogicException ex = this.generateException(se
//							.getFault().getFaultCodeAsQName().getLocalPart());
//					String errMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
//							+ FlashServiceErrorCode.VSPHERE_PROXY_CREDENTIAL, locale), name);
//					ex.setDisplayMessage(errMsg);
//					throw ex;
//				}
//				
//				if (se.getFault() != null
//						&& (FlashServiceErrorCode.Login_NotAdministrator
//										.equals(se.getFault().getFaultCodeAsQName()
//												.getLocalPart()))) {
//					BusinessLogicException ex = this.generateException(se
//							.getFault().getFaultCodeAsQName().getLocalPart());
//					String errMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
//							+ FlashServiceErrorCode.VSPHERE_PROXY_USER_NOT_ADMINISTRATOR, locale), name);
//					ex.setDisplayMessage(errMsg);
//					throw ex;
//				}
//			}
//			proccessAxisFaultException(exception);
//		}
//		return ret;
//	}
	
	@Override
	public int validateProxyInfo(VSphereBackupSettingModel proxy)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		
		try
		{
			WebServiceClientProxy client = this.getServiceClient();
			if (client != null)
			{
				return client.getServiceV6().validateProxyInfo(
					proxy.vSphereProxyModel.getVSphereProxyName(),
					proxy.vSphereProxyModel.getVSphereProxyProtocol(),
					proxy.vSphereProxyModel.getvSphereProxyPort(),
					proxy.vSphereProxyModel.getVSphereProxyUsername(),
					proxy.vSphereProxyModel.getVSphereProxyPassword(),
					proxy.retentionPolicy.isUseTimeRange(),
					proxy.retentionPolicy.isUseBackupSet() );
			}
			else
			{
				logger.debug("validateProxyInfo() - client was null");
				return 0;
			}
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
			return 0;
		}
	}
	
	private String getDomainNameFromUserName(String strUserInput){
		
		String strDomain = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strDomain;
		
		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
		}
		else 
		{
			// Extract domain part
			strDomain = strUserInput.substring(0, pos);
		}
		return strDomain;
	}
	
	private String getUserName(String strUserInput)
	{
		String strUser = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strUser;
		
		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
			strUser = strUserInput;
		}
		else 
		{
			// Extract user name part
			strUser = strUserInput.substring(pos+1);
		}
		return strUser;
	}

	@Override
	public Boolean installDriver() throws BusinessLogicException,ServiceConnectException,ServiceInternalException{
		try {
			return this.getServiceClient().getServiceV2().isDriverInstalled();
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
			return false;
		}
	}

	@Override
	public Boolean installDriverRestart() throws BusinessLogicException,ServiceConnectException,ServiceInternalException{
		try {
			return this.getServiceClient().getServiceV2().isRestartedAfterDriver();
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
			return false;
		}
	}	
	
	@Override
	public ArchiveDestinationDetailsModel getArchiveChangedDestinationDetails(ArchiveDestinationModel inArchiveDest)throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		
		ArchiveDestinationConfig archiveDestConfig = ConvertArchiveDestModelToconfig(inArchiveDest);
		ArchiveDestinationDetailsConfig config = null;
		ArchiveDestinationDetailsModel model = null;
		IFlashServiceV2 client = null;
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				config = client.getArchiveChangedDestinationDetails(archiveDestConfig);
				model = convertArchiveDestinationDetails(config);
			}
		
		return model;
	}
	
	
	@Override
	public List<ArchiveDestinationDetailsModel> getArchiveChangedDestinationDetailList(List<ArchiveDestinationModel> list)throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		
	
		IFlashServiceV2 client = null;
			client = getServiceClient().getServiceV2();
			List<ArchiveDestinationDetailsModel> result=new ArrayList();
			if (client != null)
				for(ArchiveDestinationModel inArchiveDest:list)
			{
				ArchiveDestinationConfig archiveDestConfig = ConvertArchiveDestModelToconfig(inArchiveDest);
				ArchiveDestinationDetailsConfig config = null;
				ArchiveDestinationDetailsModel model = null;
				config = client.getArchiveChangedDestinationDetails(archiveDestConfig);
				model = convertArchiveDestinationDetails(config);
				result.add(model);
			}
		
		return result;
	}
	

	private ArchiveDestinationDetailsModel convertArchiveDestinationDetails(
			ArchiveDestinationDetailsConfig config) {
		
		ArchiveDestinationDetailsModel model = new ArchiveDestinationDetailsModel();
		model.setCatalogAvailable(config.getCatalogAvailable());
		model.setLastSyncDate(config.getLastSyncDate());
		model.sethostName(config.gethostName());
		
		return model;
	}

	@Override
	public long submitArchiveCatalogSyncJob(RestoreArchiveJobModel in_ArchiveCatalogJobModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.info("in submitArchiveCatalogSyncJob");
		IFlashServiceV2 client = null;
		try
		{
			client = getServiceClient().getServiceV2();
			if (client != null)
			{
				logger.info("converting to Archive catalog job model");
				RestoreArchiveJob archiveCatalogSyncJob = ConvertToArchiveCatalogSyncJob(in_ArchiveCatalogJobModel);
				
				logger.info("calling backend to submit archive catalog sync job");
				//return client.submitArchiveRestoreJob(archiveJob,volumes[0].getPRestoreVolumeAppList());
				return client.submitArchiveCatalogSyncJob(archiveCatalogSyncJob);
			}
		}
		catch(WebServiceException e){
			logger.info("exception occured in submitting archive catalog sync job");
			//51539607558			
			logger.info(e.getMessage());
			proccessAxisFaultException(e);
		}
		catch (Exception e)
		{
			logger.info("Exception occured in submitting archive catalog sync job");
			logger.debug(e.getMessage());
		}
		return -1;
	}
	
	private RestoreArchiveJob ConvertToArchiveCatalogSyncJob(
			RestoreArchiveJobModel in_ArchiveCatalogJob) {
		RestoreArchiveJob archiveJobDetails = new RestoreArchiveJob();
		
		if (in_ArchiveCatalogJob.getArchiveDestType() == CloudVendorType.AmazonS3
				.getValue()
				|| in_ArchiveCatalogJob.getArchiveDestType() == CloudVendorType.WindowsAzure
						.getValue()
				|| in_ArchiveCatalogJob.getArchiveDestType() == CloudVendorType.Eucalyptus
						.getValue())// cloud
		{
			ArchiveCloudDestInfo cloudInfo = ConvertToArchiveCloudDestConfig(in_ArchiveCatalogJob
					.getArchiveCloudInfo());
			archiveJobDetails
					.setArchiveDestType(cloudInfo.getcloudVendorType());
			archiveJobDetails.setArchiveCloudInfo(cloudInfo);
			if(cloudInfo.getcloudVendorType() == 0L)
			{
				archiveJobDetails.setRRSFlag(cloudInfo.getRRSFlag());
			}
			else
			{
				archiveJobDetails.setRRSFlag(0L);
			}
		}
		else if(in_ArchiveCatalogJob.getArchiveDestType() == 4)//drive
		{
			archiveJobDetails.setArchiveDestType(4);
			ArchiveDiskDestInfo archiveDiskDestConfig = new ArchiveDiskDestInfo();
			
			ArchiveDiskDestInfoModel diskInfo = in_ArchiveCatalogJob.getArchiveDiskInfo();
			
			archiveDiskDestConfig.setArchiveDiskDestPath(diskInfo.getArchiveDiskDestPath());
			archiveDiskDestConfig.setArchiveDiskUserName(diskInfo.getArchiveDiskUserName());
			archiveDiskDestConfig.setArchiveDiskPassword(diskInfo.getArchiveDiskPassword());
			archiveJobDetails.setArchiveDiskInfo(archiveDiskDestConfig);
		}
		
		archiveJobDetails.setFileSystemOption(getFileSystemOptionFromModel(in_ArchiveCatalogJob.getFileSystemOption()));
		archiveJobDetails.setJobType(in_ArchiveCatalogJob.getJobType());
		
		if(in_ArchiveCatalogJob.getProductType()!=null)
				archiveJobDetails.setProductType(in_ArchiveCatalogJob.getProductType());
		
		RestoreJobArchiveNode[] archiveNode = new RestoreJobArchiveNode[1];
		archiveNode[0] = new RestoreJobArchiveNode(); 
		archiveNode[0].setNodeName(in_ArchiveCatalogJob.getSessionPath());//session path contains hostname in archive catalog sync job context.
		
		archiveJobDetails.setArchiveNodes(archiveNode);
		
		return archiveJobDetails;
	}
	@Override
	public boolean checkRecoveryVMJobExist(String vmName,String esxServerName){
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				return client.getServiceV2().checkRecoveryVMJobExist(vmName, esxServerName);
			}
		} catch (WebServiceException exception) {
			logger.debug(exception.toString());
		}
		return false;
	}

	@Override
	public boolean checkServerEqualsVMHostName(String destination,
			String domain, String username, String password)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		boolean result = false;
		List<BackupVMModel> vmList = this.getBackupVMModelList(destination, domain, username, password);
		if(vmList !=null && vmList.size()>0){
			BackupVMModel vmModel = vmList.get(0);
			if(vmModel.getVmHostName()!=null){
				String localMachineName;
				try {
					localMachineName = InetAddress.getLocalHost().getHostName();
				} catch (UnknownHostException e) {
					logger.debug("can't get local machine name");
					return false;
				}
				if(vmModel.getVmHostName().equalsIgnoreCase(localMachineName)){
					result = true;
				}
			}
		}
		return result;
	}
	
	private String getLocalhostName(){
		String localMachineName = null;
		try {
			localMachineName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.debug("can't get local machine name");
		}
		return localMachineName;
	}

	@Override
	public boolean validateEmailFromAddress(String address) throws BusinessLogicException,ServiceConnectException,ServiceInternalException{
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				return client.getServiceV2().validateEmailFromAddress(address);
			}
		} catch (WebServiceException exception) {
			logger.error("Error in validateEmailAddress " + exception.getLocalizedMessage());
			proccessAxisFaultException(exception);
		}
		
		return false;
	}

	@Override
	public List<ResourcePoolModel> getResoucePool(VirtualCenterModel vc,
			ESXServerModel esxServerModel,ResourcePoolModel parentResourcePoolModel) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				ResourcePool[] poolArray = client.getServiceV2().getResourcePool(ConvertToVirtualCenter(vc), ConvertToESXServer(esxServerModel),ConvertToResourcePool(parentResourcePoolModel));
				if(poolArray != null){
					List<ResourcePoolModel> poolList = new ArrayList<ResourcePoolModel>();
					for(ResourcePool pool : poolArray){
						poolList.add(ConvertToResourcePoolModel(pool));
					}
					return poolList;
				}
			}
		} catch (WebServiceException exception) {
			logger.error("Error in getResoucePool " + exception.getLocalizedMessage());
			proccessAxisFaultException(exception);
		}
		
		return null;
	}
	
	private ResourcePoolModel ConvertToResourcePoolModel(ResourcePool pool){
		if(pool == null){
			return null;
		}
		ResourcePoolModel poolModel = new ResourcePoolModel();
		poolModel.setPoolName(pool.getPoolName());
		poolModel.setPoolMoref(pool.getPoolMoref());
		poolModel.setParentPoolMoref(pool.getParentPoolMoref());
		return poolModel;
	}
	
	private ResourcePool ConvertToResourcePool(ResourcePoolModel pool){
		if(pool == null){
			return null;
		}
		ResourcePool poolModel = new ResourcePool();
		poolModel.setPoolName(pool.getPoolName());
		poolModel.setPoolMoref(pool.getPoolMoref());
		poolModel.setParentPoolMoref(pool.getParentPoolMoref());
		return poolModel;
	}
	
	@Override
	public long checkServiceStatus(String serviceName) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("checkServiceStatus()");
		long ret = 0;
		try
		{
			ret = getServiceClient().getServiceGRT().checkServiceStatus(serviceName);
		}
		catch (WebServiceException ex)
		{
			logger.error("error occured:" + ex);
			proccessAxisFaultException(ex);
		}
		logger.debug("checkServiceStatus() - exit: " + ret);
		return ret;
	}

	@Override
	public CustomizationModel getCustomizedModel()
			throws BusinessLogicException {
		CustomizationModel cModel = new CustomizationModel();
		readCustomizedFile(cModel);
		return cModel;
	}	
	
	@Override
	public RolePrivilegeModel getRolePrivilegeModel() throws BusinessLogicException {
		HttpSession session = getThreadLocalRequest().getSession(true);
		LoginDetail detail = (LoginDetail) session.getAttribute(SessionConstants.SRING_LOGIN_DETAIL);
		if (detail == null) {
			logger.info("No LoginDetail find in session.UI will display in administrator role");
			detail = new LoginDetail();
			detail.setPassValidation(true);
			detail.setUsername("");
			detail.setRole(LoginRole.ROLE_ADMIN.getDescription());
			detail.setPermissions(null);
			detail.setInternalLogin(false);
			session.setAttribute(SessionConstants.SRING_LOGIN_DETAIL, detail);
		}
		return getPrivilegeByRole(detail);
	}

	private RolePrivilegeModel getPrivilegeByRole(LoginDetail detail) {
		RolePrivilegeModel model = new RolePrivilegeModel();
		if (LoginRole.ROLE_ADMIN.getDescription().equalsIgnoreCase(detail.getRole())) {
			model.setBackupFlag(RolePrivilege.DISPLAY_BACKUPFLAG);
			model.setRestoreFlag(RolePrivilege.DISPLAY_RESTOREFLAG);
			model.setSettingFlag(RolePrivilege.DISPLAY_SETTINGFLAG);
			model.setCopyRecoverPointFlag(RolePrivilege.DISPLAY_COPYRECOVERPOINTFLAG);
			model.setMountRecoverPointFlag(RolePrivilege.DISPLAY_MOUNTRECOVERPOINTFLAG);
			model.setVcmConfigFlag(RolePrivilege.DISPLAY_VCMCONFIGFLAG);
		} else {
			String[] permissions = detail.getPermissions();
			model.setBackupFlag(RolePrivilege.DISPLAY_DISABLED);
			model.setRestoreFlag(RolePrivilege.DISPLAY_DISABLED);
			model.setSettingFlag(RolePrivilege.DISPLAY_DISABLED);
			model.setCopyRecoverPointFlag(RolePrivilege.DISPLAY_DISABLED);
			model.setMountRecoverPointFlag(RolePrivilege.DISPLAY_DISABLED);
			model.setVcmConfigFlag(RolePrivilege.DISPLAY_DISABLED);
			if (permissions != null && permissions.length > 0)
				for (String permission : permissions) {
					if (permission.equalsIgnoreCase(RolePrivilege.EDGE_PERMISSION_RESTORE)) {
						model.setRestoreFlag(RolePrivilege.DISPLAY_RESTOREFLAG);
						model.setCopyRecoverPointFlag(RolePrivilege.DISPLAY_COPYRECOVERPOINTFLAG);
						model.setMountRecoverPointFlag(RolePrivilege.DISPLAY_MOUNTRECOVERPOINTFLAG);
					}
					if (permission.equalsIgnoreCase(RolePrivilege.EDGE_PERMISSION_BACKUP)) {
						model.setBackupFlag(RolePrivilege.DISPLAY_BACKUPFLAG);
					}
				}
		}
		return model;
	}
	
	private void readCustomizedFile(final CustomizationModel cModel) {
		java.io.File path = new java.io.File(ContextListener.PATH_CUSTOMIZATION + "\\" + "Customized.xml");
		try {
			if(!path.exists())
				return;	
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(path, new DefaultHandler() {
				String currentElement;
				StringBuffer currentValue;
				
				@Override
				public void characters(char[] arg0, int arg1, int arg2)
						throws SAXException {
					super.characters(arg0, arg1, arg2);
					currentValue.append(arg0, arg1, arg2);
				}

				@Override
				public void startElement(String uri, String localName, String qname,
						Attributes attrs) throws SAXException {
					super.startElement(uri, localName, qname, attrs);
					currentElement = qname;
					currentValue = new StringBuffer();
				}
				
				@Override
				public void endElement(String arg0, String arg1, String arg2)
						throws SAXException {
					super.endElement(arg0, arg1, arg2);
					fillValue(currentElement, currentValue.toString().trim());
				}

				private void fillValue(String elementName, String value) {
					try {
						Integer iValue = Integer.parseInt(value);
						if(iValue <= 0) {
							cModel.set(elementName, new Boolean(false));
						}else {
							cModel.set(elementName, new Boolean(true));
						}
					}catch(Throwable t) {/*Not a number*/
						cModel.set(elementName, value);
					}
				}
			});
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public BackupVMModel getBackupVMModel(String destination, String domain,
			String username, String password) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getBackupVMModel() - start");
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				BackupVM result = client.getServiceV2().getBackupVM(destination,domain, username, password);
				return ConvertToBackupVMModel(result);
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getBackupVMModel() - end");
		return null;
	}
	
	@Override
	public HashMap<String,CloudVendorInfoModel> getCloudProviderInfo()
	throws BusinessLogicException, ServiceConnectException,
	ServiceInternalException {
			
		WebServiceClientProxy client = null;
		HashMap<String,CloudVendorInfoModel> cloudVendorUIModel = null;
		try{
			client = this.getServiceClient();
			if (client != null) {

				List<CloudProviderInfo> cloudProviderServerInfo = client.getFlashService(IFlashService_R16_U6.class).getCloudProviderInfos();
				
				if(cloudProviderServerInfo != null)
				{
					cloudVendorUIModel = new HashMap<String,CloudVendorInfoModel>();
					
					for(int i = 0 ; i < cloudProviderServerInfo.size() ; i++)
					{	
						CloudVendorInfoModel model = new CloudVendorInfoModel();
						model.setSubVendorType(cloudProviderServerInfo.get(i).getVendorID());
						model.setUrl(cloudProviderServerInfo.get(i).getUrl());
						cloudVendorUIModel.put(cloudProviderServerInfo.get(i).getVendorID()+"", model);
					}
				}
				//return convertToCloudProviderInfoModel(cloudProviderInfo,cloudVendorType,cloudSubVendorType);
				return cloudVendorUIModel;
				
			}
		}  catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private CloudVendorInfoModel convertToCloudProviderInfoModel(CloudProviderInfo cloudProviderInfo, long cloudVendorType,long cloudSubVendorType)	
	{
		if(cloudProviderInfo != null)
		{
			CloudVendorInfoModel cloudVendorInfoModel = new CloudVendorInfoModel(new Long(cloudVendorType).intValue(),"", cloudProviderInfo.getUrl(),new Long(cloudSubVendorType).intValue());
			return cloudVendorInfoModel;
		}
		return null;
	}

	@Override
	public String checkDestChainAccess() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				return client.getServiceV2().checkDestChainAccess();
			}
		}  catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		return "";
	}

	@Override
	public String updateDestAccess(String dest, String user, String pass,
			String domain) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				return client.getServiceV2().updateDestAccess(dest, user, pass, domain);
			}
		}  catch (WebServiceException e) {
			this.proccessAxisFaultException(e);
		}
		return "";
	}
	@Override
	public int getWSPort() {
		return ContextListener.webServicePort;
	}

	@Override
	public VMStorage[] validateRecoveryVMToOriginal(VirtualCenterModel vcModel,
			BackupVMModel backupVMModel,int sessionNum) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("validateVC() - start");
		WebServiceClientProxy client = null;
		try{
			client = this.getServiceClient();
			if (client != null) {
				DataStore[] result = client.getServiceV2().validateRecoveryVMToOriginal(ConvertToVirtualCenter(vcModel),ConvertToBackupVM(backupVMModel),sessionNum);
				if(result!=null){
					VMStorage[] vmStorages = new VMStorage[result.length];
					for(int i = 0 ; i < result.length;i++){
						vmStorages[i] = ConvertToVMStorage(result[i]);
					}
					return vmStorages;
				}
			}
		} catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		return null;
	}	

	@Override
	public boolean testMailSettings(EmailAlertsModel emailConf)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			BackupEmail email = ConvertEmailSettings(emailConf);
			IFlashService_R16_U4 service = getServiceClient()
				.getFlashService(IFlashService_R16_U4.class);
			return service.testEmailSettings(email);
		}catch(WebServiceException exception) {
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException se = (SOAPFaultException) exception;
				if(se.getFault() != null) {
					SOAPFault fault = se.getFault();
					String errorCode = fault.getFaultCodeAsQName().getLocalPart();
					if(errorCode.equals(FlashServiceErrorCode.Common_SendTestMail_Failure)){
						BusinessLogicException ex = new BusinessLogicException();
						ex.setDisplayMessage(fault.getFaultString());	
						throw ex;
					}	
				}	
			}
			proccessAxisFaultException(exception);
		}
		return false;
	}
	
	
	
	@Override
	public int checkVMRecoveryPointESXUefi(VirtualCenterModel vcModel,ESXServerModel esxServerModel,String dest, String domain, String user,String pwd, String subPath)
		throws BusinessLogicException, ServiceConnectException,
		ServiceInternalException, SessionTimeoutException {
		try {
			IFlashService_R16_U4 service = getServiceClient().getFlashService(IFlashService_R16_U4.class);
			return service.checkVMRecoveryPointESXUefi(ConvertToVirtualCenter(vcModel),ConvertToESXServer(esxServerModel), dest, domain, user, pwd, subPath);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return 0;
	}

	@Override
	public boolean isUEFIFirmware() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			IFlashService_R16_U4 service = getServiceClient()
				.getFlashService(IFlashService_R16_U4.class);
			return service.isUEFIFirmware();
		}catch(WebServiceException e) {
			logger.error("isUEFIFirmare call erro", e);
			this.proccessAxisFaultException(e);
		}
		return false;
	}
	
	
	@Override
	public List<MountedRecoveryPointItemModel> getAllMountedRecoveryPointItems()
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		ArrayList<MountedRecoveryPointItemModel> result = new ArrayList<MountedRecoveryPointItemModel>();
		try {
			IFlashService_R16_U4 service = getServiceClient().getFlashService(IFlashService_R16_U4.class);
			MountedRecoveryPointItem[] items = service.getAllMountedRecoveryPointItems();
			for (MountedRecoveryPointItem mountedRecoveryPointItem : items) {
				result.add(convertToModel(mountedRecoveryPointItem));
			}
			return result;
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return result;
	}
	
	@Override
	public List<MountedRecoveryPointItemModel> getMountedRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath, String sessionGuid) 
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		ArrayList<MountedRecoveryPointItemModel> result = new ArrayList<MountedRecoveryPointItemModel>();
		try {
			IFlashService_R16_U4 service = getServiceClient().getFlashService(IFlashService_R16_U4.class);
			MountedRecoveryPointItem[] items = service.getMountedRecoveryPointItems(dest, domain, user, pwd, subPath, sessionGuid);
			for (MountedRecoveryPointItem mountedRecoveryPointItem : items) {
				result.add(convertToModel(mountedRecoveryPointItem));
			}
			return result;
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return result;
	}
		
	@Deprecated
	@Override
	public long mountRecoveryPointItem(String dest,String domain, String user, String pwd, String subPath,  
			String volGUID,int encryptionType,String encryptPassword, String mountPath)
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		try {
			IFlashService_R16_U4 service = getServiceClient().getFlashService(IFlashService_R16_U4.class);
			return service.mountRecoveryPointItem(dest, domain, user, pwd, subPath, volGUID, encryptionType, encryptPassword, mountPath);
		}catch(WebServiceException wsE) {
//			if (wsE instanceof SOAPFaultException) {
//				SOAPFaultException se = (SOAPFaultException) wsE;
//				if(se.getFault() != null) {
//					SOAPFault fault = se.getFault();
//					String errorCode = fault.getFaultCodeAsQName().getLocalPart();
//					if(errorCode.equals(FlashServiceErrorCode.Common_MountVolume_Failure)){
//						BusinessLogicException ex = new BusinessLogicException();
//						ex.setErrorCode(errorCode);
//						ex.setDisplayMessage(se.getMessage());						
//						throw ex;
//					}	
//				}	
//			}
			
			proccessAxisFaultException(wsE);
		}
		return 0;
	}
	
	@Override
	public long mountRecoveryPointItem( JMountRecoveryPointParamsModel jMntParamsModel ) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
			JMountRecoveryPointParams param = new JMountRecoveryPointParams();
			param.setDatastoreName(jMntParamsModel.getDatastoreName());
			param.setDest(jMntParamsModel.getDest());
			param.setDomain(jMntParamsModel.getDomain());
			param.setEncryptionType(jMntParamsModel.getEncryptionType());
			param.setEncryptPassword(jMntParamsModel.getEncryptPassword());
			param.setMountPath(jMntParamsModel.getMountPath());
			param.setPwd(jMntParamsModel.getPwd());
			param.setRpsHostname(jMntParamsModel.getRpsHostname());
			param.setSubPath(jMntParamsModel.getSubPath());
			param.setUser(jMntParamsModel.getUser());
			param.setVolGUID(jMntParamsModel.getVolGUID());			
			IFlashService_Oolong1 service = getServiceClient().getFlashService(IFlashService_Oolong1.class);
			return service.mountRecoveryPointItemEx(param);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return 0;
	}
	
	@Override
	public long disMountRecoveryPointItem(String mountPath, int mountDiskSignature)throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		try {
			IFlashService_R16_U4 service = getServiceClient().getFlashService(IFlashService_R16_U4.class);
			return service.disMountRecoveryPointItem(mountPath, mountDiskSignature);
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return 0;
	}
	
	@Override
	public List<String> getAvailableMountDriveLetters() throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		List<String> result = new ArrayList<String>();
		try {
			IFlashService_R16_U4 service = getServiceClient().getFlashService(IFlashService_R16_U4.class);
			for (String driveLetter : service.getAvailableMountDriveLetters()) {
				result.add(driveLetter);
			}
			return result;
		}catch(WebServiceException wsE) {
			proccessAxisFaultException(wsE);
		}
		return result;
	}
	
	private MountedRecoveryPointItemModel convertToModel(MountedRecoveryPointItem item){
		MountedRecoveryPointItemModel model = new MountedRecoveryPointItemModel();
		model.setVolumeGuid(item.getVolumeGuid());
		model.setVolumePath(item.getVolumePath());
		model.setVolumeSize(item.getVolumeSize());
		model.setSessionID(item.getSessionID());
		model.setSessionGuid(item.getSessionGuid());
		
		model.setMountDiskSignature(item.getMountDiskSignature());
		model.setMountPath(item.getMountPath());
		model.setMountFlag(item.getMountFlag());
		model.setIsReadOnly(item.isReadOnly());
		
		model.setRecoveryPointPath(item.getRecoveryPointPath());
		model.setTime(item.getRecoveryPointDate());
		model.setTimeZoneOffset(item.getTimeZoneOffset());
		
		return model;
	}
	
	@Override
	public SearchContextModel openSearchCatalog(RecoveryPointModel[] models,
			String sessionPath, String sDir, boolean bCaseSensitive,
			boolean bIncludeSubDir, String pattern, String domain, 
			String destUser, String destPwd, String[] encryptedHashKey, String[] encryptedPwd)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			if (client != null)
			{
				RecoveryPoint[] rps = new RecoveryPoint[models.length]; 
				for(int  i = 0; i < models.length; i ++) {
					RecoveryPointModel model = models[i];
					RecoveryPoint rp = convertToRecoveryPoint(model);
					RecoveryPointItem[] items = client.getServiceV2().
						getRecoveryPointItems(sessionPath, domain, destUser, destPwd, model.getPath());
					if(items.length > 0) {
						rp.setItems(Arrays.asList(items));
					}
					rps[i] = rp;
				}
				
				SearchContext context = client.getServiceV3()
						.openSearchCatalogEx(rps,
								rps.length, sessionPath, domain, destUser, destPwd, sDir,
								bCaseSensitive, bIncludeSubDir, pattern,
								encryptedHashKey, encryptedPwd);
				return ConvertToModel(context);
			}
		}
		catch(WebServiceException exception){	
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		return null;
	}
	
	@Override
	public List<FileModel> getVolumes(VirtualCenterModel vcModel,
			VMItemModel vmModel) {
		logger.debug("getVolumes() for vm enter");
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			
			if (client != null)
			{
				Volume[] volumes = getServiceClient().getFlashService(IFlashService_R16_5.class).getVMVolumes(getBackupVM(vcModel,vmModel));
				List<FileModel> modelList = new ArrayList<FileModel>();
				for (int i = 0; i < volumes.length; i++)
				{
					modelList.add(ConvertToVolumeModel(volumes[i]));
				}
				logger.debug("volumes:" + StringUtil.convertList2String(modelList));
				logger.debug("getVolumes() for vm end");
				return modelList;
			}
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		return null;
	}

	@Override
	public void createFolder(String parentPath, String subDir,
			VirtualCenterModel vcModel, VMItemModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			
			client = this.getServiceClient();
			
			if (client != null) {
				getServiceClient().getFlashService(IFlashService_R16_5.class).createVMFolder(parentPath, subDir,getBackupVM(vcModel,vmModel));
			}
		} catch (WebServiceException exception) {
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null
						&& FlashServiceErrorCode.Browser_CreateFolderFailed
								.equals(e.getFault().getFaultCodeAsQName()
										.getLocalPart())) {
					BusinessLogicException ex = this.generateException(e
							.getFault().getFaultCodeAsQName().getLocalPart());
					String errMsg = MessageFormatEx.format(
							ex.getDisplayMessage(), e.getMessage());
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
			}
			{
				proccessAxisFaultException(exception);
			}

		}
		
	}

	@Override
	public List<FileModel> getFileItems(String inputFolder,boolean bIncludeFiles, VirtualCenterModel vcModel,
			VMItemModel vmModel) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			
			client = this.getServiceClient();
			
			if (client != null) {

				if (inputFolder != null && inputFolder.endsWith("\\")
						&& !inputFolder.endsWith("\\\\"))
					inputFolder = inputFolder.substring(0, inputFolder
							.lastIndexOf("\\"));
				if (inputFolder != null && inputFolder.endsWith("/"))
					inputFolder = inputFolder.substring(0, inputFolder
							.lastIndexOf("/"));

				FileFolderItem item = getServiceClient().getFlashService(IFlashService_R16_5.class).getVMFileFolder(
						inputFolder, getBackupVM(vcModel,vmModel));
				List<FileModel> modelList = ConvertToFileModelList(item,
						bIncludeFiles);
				return modelList;
			}
		} catch (WebServiceException exception) {
			logger.debug(exception.getMessage());
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null
						&& FlashServiceErrorCode.Browser_GetFolderFailed
								.equals(e.getFault().getFaultCodeAsQName()
										.getLocalPart())
						|| FlashServiceErrorCode.Browser_GetFolderFailed_Acess_Denied
								.equals(e.getFault().getFaultCodeAsQName()
										.getLocalPart())) {
					BusinessLogicException ex = this
							.generateException(FlashServiceErrorCode.Browser_GetFolderFailed);
					String errMsg = MessageFormatEx.format(
							ex.getDisplayMessage(), e.getMessage());
					ex.setDisplayMessage(errMsg);

					// we dont't need special error message for get folder
					// failed due to privilege
					if (FlashServiceErrorCode.Browser_GetFolderFailed_Acess_Denied
							.equals(e.getFault().getFaultCodeAsQName()
									.getLocalPart()))
						ex
								.setErrorCode(FlashServiceErrorCode.Browser_GetFolderFailed_Acess_Denied);

					throw ex;
				}
			}
			{
				proccessAxisFaultException(exception);
			}

		}
		return null;
	}
	
	private BackupVM getBackupVM(VirtualCenterModel vcModel , VMItemModel vmModel){
		BackupVM backupVM = new BackupVM();
		backupVM.setEsxServerName(vcModel.getVcName());
		backupVM.setEsxUsername(vcModel.getUsername());
		backupVM.setEsxPassword(vcModel.getPassword());
		backupVM.setProtocol(vcModel.getProtocol());
		backupVM.setPort(vcModel.getPort());
		
		backupVM.setVmName(vmModel.getVmName());
		backupVM.setInstanceUUID(vmModel.getVmInstanceUUID());
		backupVM.setUsername(vmModel.getUsername());
		backupVM.setPassword(vmModel.getPassword());
		backupVM.setVmVMX(vmModel.getVmVMX());
		return backupVM;
	}

	@Override
	public List<BackupD2DModel> getBackupD2DList(String hostname,
			String username, String password, String protocol, int port)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		List<BackupD2DModel> d2dList = new ArrayList<BackupD2DModel>();
		RPSWebServiceClientProxy client = null;
		try
		{
			String usernameNew = getUserName(username);
			String domain = getDomainNameFromUserName(username);
			client = RPSWebServiceFactory.getRPSService4D2D(protocol,hostname,port);
			client.getServiceForD2D().validateUser(usernameNew, password, domain);
			List<BackupD2D> backupD2DList = client.getServiceForD2D().getBackupD2DList();
			if(backupD2DList!=null){
				for(BackupD2D d2d : backupD2DList){
					d2dList.add(convertToBackupD2DModel(d2d));
				}
				Collections.sort(d2dList, new Comparator<BackupD2DModel>(){

					@Override
					public int compare(BackupD2DModel o1, BackupD2DModel o2) {
						return o1.getHostName().compareTo(o2.getHostName());
					}
				});
			}
			return d2dList;
		}catch (WebServiceException exception){
			logger.error("Error occurs during getBackupD2DList can't connect that webservice");
			String locale = this.getServerLocale();
			if(exception.getCause() instanceof UnknownHostException){
				throw new ServiceConnectException(
						FlashServiceErrorCode.Common_CantConnectRemoteServer,
						ResourcesReader.getResource("ServiceError_"
								+ FlashServiceErrorCode.Common_CantConnectRemoteServer,
								locale)); 
			}
			if(exception.getCause() instanceof ConnectException || exception.getCause() instanceof SSLHandshakeException || exception.getCause() instanceof SocketException|| exception.getCause() instanceof SSLException || exception.getMessage().startsWith("XML reader error")){
				throw new ServiceConnectException(
						FlashServiceErrorCode.Common_CantConnectService,
						MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
								+ FlashServiceErrorCode.Common_CantConnectService,
								locale),ResourcesReader.getResource("ProductNameRPS", locale))); 
			} 
			if (exception.getCause() instanceof Error && exception.getMessage().startsWith(UNDEFINED_OPERATION_NAME)) {
				throw generateException(FlashServiceErrorCode.EDGE_D2D_INTERFACE_MISMATCH);
			}
			
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException se = (SOAPFaultException) exception;
				if (se.getFault() != null
						&& (FlashServiceErrorCode.Login_WrongCredential
								.equals(se.getFault().getFaultCodeAsQName()
										.getLocalPart()))) {
					BusinessLogicException ex = this.generateException(se
							.getFault().getFaultCodeAsQName().getLocalPart());
					String errMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
							+ FlashServiceErrorCode.VSPHERE_PROXY_CREDENTIAL, locale), hostname);
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
				
				if (se.getFault() != null
						&& (FlashServiceErrorCode.Login_NotAdministrator
										.equals(se.getFault().getFaultCodeAsQName()
												.getLocalPart()))) {
					BusinessLogicException ex = this.generateException(se
							.getFault().getFaultCodeAsQName().getLocalPart());
					String errMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
							+ FlashServiceErrorCode.VSPHERE_PROXY_USER_NOT_ADMINISTRATOR, locale), hostname);
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
			}
			proccessAxisFaultException(exception);
		}
		
		return d2dList;
	}
	
	private BackupD2DModel convertToBackupD2DModel(BackupD2D d2d){
		if(d2d == null)
			return null;
		BackupD2DModel d2dModel = new BackupD2DModel();
		d2dModel.setHostName(d2d.getHostname());
		d2dModel.setDestination(d2d.getDestination());
		d2dModel.setDesUsername(d2d.getDesUsername());
		d2dModel.setDesPassword(d2d.getDesPassword());
		d2dModel.setDataStoreName(d2d.getDatastoreName());
		d2dModel.setRpsPolicyUUID(d2d.getPolicyUUID());
		d2dModel.setDataStoreUUID(d2d.getDatastoreUUID());
		d2dModel.setAgentSID(d2d.getD2dSid());
		d2dModel.setAgentUUID(d2d.getLoginUUID());
		return d2dModel;
		
	}
	
	//wanqi06
	private ScheduleDetailItem convertToDetailItem(ScheduleDetailItemModel model){
		ScheduleDetailItem item = new ScheduleDetailItem();
		item.setJobType(model.getJobType());
		item.setStartTime(ConvertToDayTime(model.startTimeModel));
		item.setEndTime(ConvertToDayTime(model.endTimeModel));
		item.setInterval(model.getInterval());
		item.setIntervalUnit(model.getIntervalUnit());
		item.setRepeatEnabled(model.isRepeatEnabled());
		return item;
	}
	
	private ThrottleItem convertToThrottleItem(ThrottleModel model){
		ThrottleItem item = new ThrottleItem();
		item.setStartTime(ConvertToDayTime(model.startTimeModel));
		item.setEndTime(ConvertToDayTime(model.endTimeModel));
		item.setThrottleValue(model.getThrottleValue());
		item.setUnit(model.getUnit());
		return item;
	}
	
	private MergeDetailItem convertToMergeItem(MergeDetailItemModel model){
		MergeDetailItem item = new MergeDetailItem();
		item.setStartTime(ConvertToDayTime(model.startTimeModel));
		item.setEndTime(ConvertToDayTime(model.endTimeModel));
		return item;
	}
	
	private DailyScheduleDetailItem convertToDailyScheduleDetailItem(DailyScheduleDetailItemModel daylyItemModel){
		DailyScheduleDetailItem daylyItem = new DailyScheduleDetailItem();
		//the index is for day of week, in Calendar, it's from Sunday to Saturday with 1 to 7 value, so here we calculate it
//		daylyItem.setDayofWeek((index + 2) % 7);
//		if(daylyItem.getDayofWeek() == 0) {
//			daylyItem.setDayofWeek(7);
//		}
		
		daylyItem.setDayofWeek(daylyItemModel.dayOfweek);		
		
		ArrayList<ScheduleDetailItem> items = new ArrayList<ScheduleDetailItem>();
		if (daylyItemModel.scheduleDetailItemModels != null) {
			for (ScheduleDetailItemModel itemModel : daylyItemModel.scheduleDetailItemModels) {
				items.add(convertToDetailItem(itemModel));			
			}
		}
		daylyItem.setScheduleDetailItems(items);
		
		ArrayList<ThrottleItem> throttleItems = new ArrayList<ThrottleItem>();
		if (daylyItemModel.throttleModels != null) {
			for (ThrottleModel itemModel : daylyItemModel.throttleModels) {
				throttleItems.add(convertToThrottleItem(itemModel));
			}
		}
		daylyItem.setThrottleItems(throttleItems);
		
		ArrayList<MergeDetailItem> mergeItems = new ArrayList<MergeDetailItem>();
		if (daylyItemModel.mergeModels != null) {
			for (MergeDetailItemModel itemModel : daylyItemModel.mergeModels) {
				mergeItems.add(convertToMergeItem(itemModel));
			}
		}
		daylyItem.setMergeDetailItems(mergeItems);
		return daylyItem;
	}
	
	private AdvanceSchedule convertToAdvanceSchedule(AdvanceScheduleModel model){
		if(model == null) return null;
		ArrayList<DailyScheduleDetailItem> daylyItems = new ArrayList<DailyScheduleDetailItem>();
		for(DailyScheduleDetailItemModel daylyItemModel: model.daylyScheduleDetailItemModel){			
			if ((daylyItemModel.scheduleDetailItemModels != null && daylyItemModel.scheduleDetailItemModels.size() >0)
					|| (daylyItemModel.throttleModels != null && daylyItemModel.throttleModels.size() >0)
					|| (daylyItemModel.mergeModels != null && daylyItemModel.mergeModels.size() >0)){
				daylyItems.add(convertToDailyScheduleDetailItem(daylyItemModel));
			}
			
		}
		
		AdvanceSchedule schedule = null;
		
		if(daylyItems.size() >0){
			schedule = new AdvanceSchedule();
		//	schedule.setEnabled(model.getIsEnableSchedule());
			schedule.setDailyScheduleDetailItems(daylyItems);			
		}
		
		EveryDayScheduleModel dayModel = model.periodScheduleModel.dayScheduleModel;
		EveryWeekScheduleModel weekModel = model.periodScheduleModel.weekScheduleModel;
		EveryMonthScheduleModel monthModel = model.periodScheduleModel.monthScheduleModel;
		
		if(model.periodScheduleModel !=null && (dayModel != null || weekModel != null ||  monthModel!= null)){
			if(schedule == null) schedule = new AdvanceSchedule();
			
			PeriodSchedule periodSchedule = new PeriodSchedule();
			if(dayModel != null){
				EveryDaySchedule daySchedule = new EveryDaySchedule();
				daySchedule.setBkpType(dayModel.getBkpType());
				daySchedule.setEnabled(dayModel.isEnabled());
				daySchedule.setDayTime(ConvertToDayTime(dayModel.getDayTime()));
				daySchedule.setGenerateCatalog(dayModel.isGenerateCatalog());
				daySchedule.setRetentionCount(dayModel.getRetentionCount());
				daySchedule.setCheckRecoveryPoint(dayModel.isCheckRecoveryPoint());
				periodSchedule.setDaySchedule(daySchedule);
			}
			
			if(weekModel != null){				
				EveryWeekSchedule weekSchedule = new EveryWeekSchedule();
				weekSchedule.setBkpType(weekModel.getBkpType());
				weekSchedule.setDayTime(ConvertToDayTime(weekModel.getDayTime()));				
				weekSchedule.setDayOfWeek(weekModel.getDayOfWeek());;
				weekSchedule.setEnabled(weekModel.isEnabled());			
				weekSchedule.setGenerateCatalog(weekModel.isGenerateCatalog());
				weekSchedule.setRetentionCount(weekModel.getRetentionCount());
				weekSchedule.setCheckRecoveryPoint(weekModel.isCheckRecoveryPoint());
				periodSchedule.setWeekSchedule(weekSchedule);
			}
			
			if(monthModel != null){
				EveryMonthSchedule monthSchedule = new EveryMonthSchedule();
				monthSchedule.setBkpType(monthModel.getBkpType());
				monthSchedule.setDayOfMonth(monthModel.getDayOfMonth());
				monthSchedule.setDayOfMonthEnabled(monthModel.isDayOfMonthEnabled());
				monthSchedule.setDayTime(ConvertToDayTime(monthModel.getDayTime()));
				monthSchedule.setEnabled(monthModel.isEnabled());
				monthSchedule.setGenerateCatalog(monthModel.isGenerateCatalog());
				monthSchedule.setRetentionCount(monthModel.getRetentionCount());
				monthSchedule.setWeekDayOfMonth(monthModel.getWeekDayOfMonth());
				monthSchedule.setWeekNumOfMonth(monthModel.getWeekNumOfMonth());
				monthSchedule.setWeekOfMonthEnabled(monthModel.isWeekOfMonthEnabled());
				monthSchedule.setCheckRecoveryPoint(monthModel.isCheckRecoveryPoint());
				periodSchedule.setMonthSchedule(monthSchedule);
			}
			schedule.setPeriodSchedule(periodSchedule);		
		}
		
		if(schedule != null && model.getBackupStartTime() != null) schedule.setScheduleStartTime(model.getBackupStartTime());
		
		
		return schedule;	
	}

	@Override
	public List<VDSInfoModel> getVDSInfoList(VirtualCenterModel vc, String esx)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			List<VDSInfo> vdsList = client.getServiceV2().getVDSInfoList(ConvertToVirtualCenter(vc), esx);
			
			List<VDSInfoModel> result = new LinkedList<VDSInfoModel>();
			for (VDSInfo vds:vdsList){
				VDSInfoModel model = new VDSInfoModel();
				model.setvDSSwitchName(vds.getvDSSwitchName());
				model.setvDSSwitchUUID(vds.getvDSSwitchUUID());
				ArrayList<vDSPortGroup> portGroup = vds.getPortGroups();
				List<vDSPortGroupModel> modelList = new LinkedList<vDSPortGroupModel>();
				for(int i = 0; i < portGroup.size(); ++i) {
					vDSPortGroup group = portGroup.get(i);
					vDSPortGroupModel groupModel = new vDSPortGroupModel();
					groupModel.setvDSPortGroupKey(group.getvDSPortGroupKey());
					groupModel.setvDSPortGroupName(group.getvDSPortGroupName());
					modelList.add(groupModel);
				}
				model.setPortGroups(modelList);
				result.add(model);
			}
			return result;
		}
		catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		
		return null;
	}
	
	@Override
	public List<VMNetworkConfigInfoModel> getVMNetworkConfigList(String rootPath, int sessionNum, String userName, String password)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			List<VMNetworkConfig> networkCfgList = client.getServiceV2().getVMNetworkConfigList(rootPath, sessionNum, userName, password);
			
			List<VMNetworkConfigInfoModel> result = new LinkedList<VMNetworkConfigInfoModel>();
			
			for (VMNetworkConfig networkCfg : networkCfgList){
				VMNetworkConfigInfoModel model = new VMNetworkConfigInfoModel();
				
				model.setBackingInfoType(networkCfg.getBackingInfoType());
				model.setDeviceName(networkCfg.getDeviceName());
				model.setDeviceType(networkCfg.getDeviceType());
				model.setLabel(networkCfg.getLabel());
				model.setPortgroupKey(networkCfg.getPortgroupKey());
				model.setPortgroupName(networkCfg.getPortgroupName());
				model.setSwitchName(networkCfg.getSwitchName());
				model.setSwitchUUID(networkCfg.getSwitchUUID());
				result.add(model);
			}
			
			return result;
		}
		catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		
		return null;
	}
	
	@Override
	public int getVMwareServerType(String host, String username, String password, String protocol, int port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			VMwareServer server = client.getServiceV2().getVMwareServerType(host, username, password, protocol, port);
			if (server == null) {
				return 0;
			} else {
				return server.getVmtype();
			}
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return 0;
	}

	@Override
	public List<VMNetworkStandardConfigInfoModel> getStandardNetworkInfoList(VirtualCenterModel vc, String esx) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			List<StandNetworkConfigInfo> infoList = client.getServiceV2().getStandardNetworkConfigList(ConvertToVirtualCenter(vc), esx);

			List<VMNetworkStandardConfigInfoModel> modelList = new LinkedList<VMNetworkStandardConfigInfoModel>();
			for (StandNetworkConfigInfo info : infoList){
				VMNetworkStandardConfigInfoModel model = new VMNetworkStandardConfigInfoModel();
				model.setNetworkName(info.getNetworkName());
				modelList.add(model);
			}
			return modelList;
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return null;
	}

	@Override
	public void validateHyperV(String host, String user, @NotPrintAttribute String password) throws BusinessLogicException,	ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			client.getServiceV2().validateHyperV(host, user, password);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	

	@Override
	public void validateHyperVAndCheckIfVMExist(String host, String user,
					String password, String vmInstanceUUID, String vmName)
					throws BusinessLogicException, ServiceConnectException,
					ServiceInternalException
	{
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			client.getServiceV2().validateHyperVAndCheckIfVMExist(host, user, password, vmInstanceUUID, vmName);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}

	@Override
	public VMNetworkConfigInfoModel[] getHyperVAvailabeNetworkList(String hostname, String username, String password)
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			VMNetworkConfig[] networkCfgList = client.getServiceV2().getHyperVAvailabeNetworkList(hostname, username, password);
			
			List<VMNetworkConfigInfoModel> result = new LinkedList<VMNetworkConfigInfoModel>();
			
			for (VMNetworkConfig networkCfg : networkCfgList){
				VMNetworkConfigInfoModel model = new VMNetworkConfigInfoModel();
				
				model.setBackingInfoType(networkCfg.getBackingInfoType());
				model.setDeviceName(networkCfg.getDeviceName());
				model.setDeviceType(networkCfg.getDeviceType());
				model.setLabel(networkCfg.getLabel());
				model.setPortgroupKey(networkCfg.getPortgroupKey());
				model.setPortgroupName(networkCfg.getPortgroupName());
				model.setSwitchName(networkCfg.getSwitchName());
				model.setSwitchUUID(networkCfg.getSwitchUUID());
				result.add(model);
			}
			
			return result.toArray(new VMNetworkConfigInfoModel[0]);
		}
		catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		
		return null;
	}

	@Override
	public List<DiskModel> getHyperVBackupVMDisk(String destination, String subPath, String domain, String username, String password)
			throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("checkVMDestination() - start");
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				Disk[] returnDisk = client.getServiceV2().getHyperVBackupVMDisk(destination, subPath, domain, username, password);
				if(returnDisk!=null){
					List<DiskModel> diskModelList = new ArrayList<DiskModel>();
					for(Disk disk:returnDisk){
						diskModelList.add(ConvertToDiskModel(disk));
					}
					return diskModelList;
				}
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		return null;
	}

	@Override
	public List<VMNetworkConfigInfoModel> getHyperVVMNetworkConfigList(String destination, String subPath, String domain, String username, String password)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getHyperVVMNetworkConfigList() - start");
		WebServiceClientProxy client = null;

		try{
			client = this.getServiceClient();
			if (client != null) {
				List<VMNetworkConfig> returnDisk = client.getServiceV2().getHyperVVMNetworkConfigList(destination, subPath, domain, username, password);
				if(returnDisk!=null){
					List<VMNetworkConfigInfoModel> result = new ArrayList<VMNetworkConfigInfoModel>();
					for(VMNetworkConfig networkCfg:returnDisk){
						VMNetworkConfigInfoModel model = new VMNetworkConfigInfoModel();
						
						model.setBackingInfoType(networkCfg.getBackingInfoType());
						model.setDeviceName(networkCfg.getDeviceName());
						model.setDeviceType(networkCfg.getDeviceType());
						model.setLabel(networkCfg.getLabel());
						model.setPortgroupKey(networkCfg.getPortgroupKey());
						model.setPortgroupName(networkCfg.getPortgroupName());
						model.setSwitchName(networkCfg.getSwitchName());
						model.setSwitchUUID(networkCfg.getSwitchUUID());
						model.setHyperVAdapterType(networkCfg.getHyperVAdapterType());
						model.setHyperVAdapterID(networkCfg.getHyperVAdapterID());
						result.add(model);
					}
					return result;
				}
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		return null;
	}

	
	private String preFormatBucketName(String bucketName) {
		logger.debug("preFormatBucketName return: "+bucketName);
		String result=bucketName;
		try {
			String tempWithoutPrefix=""; // host - bucket
			if(bucketName.startsWith(cloudBucketD2DArchiveLabel)){
				return bucketName;
			}else if(bucketName.startsWith(cloudBucketD2DF2CLabel)){
				tempWithoutPrefix=bucketName.substring(cloudBucketD2DF2CLabel.length());
				result = cloudBucketD2DF2CLabel+BucketNameEncoder.encodeWithUTF8(tempWithoutPrefix);
			}else if(bucketName.startsWith(cloudBucketD2DLabel)){
				tempWithoutPrefix=bucketName.substring(cloudBucketD2DLabel.length());
				result = cloudBucketD2DLabel+BucketNameEncoder.encodeWithUTF8(tempWithoutPrefix);
			}else if(bucketName.startsWith(cloudBucketARCserveLabel)){
				tempWithoutPrefix=bucketName.substring(cloudBucketARCserveLabel.length());
				result = cloudBucketARCserveLabel+BucketNameEncoder.encodeWithUTF8(tempWithoutPrefix);
			}
		} catch (UnsupportedEncodingException e) {
			logger.info("Exception while encoding the hostname and bucket", e);
		}
		logger.debug("preFormatBucketName return: "+result);
		return result;
	}
	
	private String postFormatBucketName(String encodedBucketName) {
		logger.debug("postFormatBucketName: "+encodedBucketName);
		String result=encodedBucketName;
		try {
			String tempWithoutPrefix="";
			if(encodedBucketName.startsWith(cloudBucketD2DArchiveLabel)){
				return encodedBucketName;
			}else if(encodedBucketName.startsWith(cloudBucketD2DF2CLabel)){
				tempWithoutPrefix=encodedBucketName.substring(cloudBucketD2DF2CLabel.length()).replace("-", "%");
				result = cloudBucketD2DF2CLabel+decodeBucketHostName(tempWithoutPrefix);
			}else if(encodedBucketName.startsWith(cloudBucketD2DLabel)){
				tempWithoutPrefix=encodedBucketName.substring(cloudBucketD2DLabel.length()).replace("-", "%");
				result = cloudBucketD2DLabel+decodeBucketHostName(tempWithoutPrefix);
			}else if(encodedBucketName.startsWith(cloudBucketARCserveLabel)){
				tempWithoutPrefix=encodedBucketName.substring(cloudBucketARCserveLabel.length()).replace("-", "%");
				result = cloudBucketARCserveLabel+decodeBucketHostName(tempWithoutPrefix);
			}
		} catch (Exception e) {
			logger.info("Exception while encoding the hostname and bucket", e);
		}
		logger.debug("postFormatBucketName return: "+result);
		return result;
	}

	@Override
	public List<String> getE15CASList(String userName, String password) {
		List<String> list = new ArrayList<String>();
		WebServiceClientProxy client = this.getServiceClient();
		if (client != null)
		{
			list = client.getServiceGRT().getE15CASList(userName, password);
		}
		return list;
	}

	@Override
	public String getDefaultE15CAS(String userName, String password) {
		WebServiceClientProxy client = this.getServiceClient();
		if (client != null)
		{
			return client.getServiceGRT().getDefaultE15CAS(userName, password);
		}
		return null;
	}

	@Override
	public List<FileModel> browseHyperVHostFolder(String server, String userName,
			String password, String parentFolder) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("browseHyperVHostFolder() - start");
		WebServiceClientProxy client = null;
		List<FileModel> modelList = new ArrayList<FileModel>();
		Boolean isVolume = false;
		try{
			client = this.getServiceClient();
			if (client != null) {
				if(parentFolder == null) parentFolder = "";
				List<String> folders =  client.getServiceV2().browseHyperVHostFolder(server, userName, password, parentFolder);
				if(parentFolder.isEmpty() || parentFolder.equals("\\")) {
					isVolume = true;
				}
				for(String volume : folders) {
					FileModel model = new FileModel();
					model.setIsVolume(isVolume);
					model.setPath(volume);
					if(!isVolume) {
						int index = volume.lastIndexOf("\\");
						String name = volume.substring(index + 1,  volume.length());
						model.setName(name);
						model.setType(CatalogModelType.Folder);
					} else {
						model.setName(volume);
						model.set("driveType", "localDrive");
					}
					
					modelList.add(model);
				}
				return modelList;
			}
		} catch (WebServiceException exception) {
			logger.debug(exception.getMessage());
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null) {
					if(FlashServiceErrorCode.Browser_HyperVHostPathNotFound.equals(e.getFault().getFaultCodeAsQName().getLocalPart())) {
						BusinessLogicException ex = this.generateException(FlashServiceErrorCode.Browser_HyperVHostPathNotFound);
						throw ex;
					} else {
						BusinessLogicException ex = this.generateException(FlashServiceErrorCode.Browser_HyperVHostGetFolderFailed);
						throw ex;
					}
				}
			}
			proccessAxisFaultException(exception);
		}
		return null;
	}

	@Override
	public boolean createHyperVHostFolder(String server, String userName,
			String password, String path, String folder)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("createHyperVHostFolder() - start");
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				return client.getServiceV2().createHyperVHostFolder(server, userName, password, path, folder);
			}
		} catch (WebServiceException exception) {
			logger.debug(exception.getMessage());
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null) {
					if(FlashServiceErrorCode.Browser_HyperVHostPathNotFound.equals(e.getFault().getFaultCodeAsQName().getLocalPart())) {
						BusinessLogicException ex = this.generateException(FlashServiceErrorCode.Browser_HyperVHostPathNotFound);
						throw ex;
					}  else if(FlashServiceErrorCode.Browser_CreateHyperVHostFolderFailed_AlreadyExist.equals(e.getFault().getFaultCodeAsQName().getLocalPart())) {
						BusinessLogicException ex = this.generateException(FlashServiceErrorCode.Browser_CreateHyperVHostFolderFailed_AlreadyExist);
						throw ex;
					} else {
						BusinessLogicException ex = this.generateException(FlashServiceErrorCode.Browser_CreateHyperVHostFolderFailed);
						throw ex;
					}
				}
			}
			proccessAxisFaultException(exception);
		}
		return false;
	}
	
	@Override
	public List<HyperVHostStorageModel> getHyperVHostStorage(String server, String userName, String password)
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("createHyperVHostFolder() - start");
		WebServiceClientProxy client = null;
		List<HyperVHostStorageModel> modelList = new ArrayList<HyperVHostStorageModel>();
		try {
			client = this.getServiceClient();
			if (client != null) {
				List<HyperVHostStorage> storageList = client.getServiceV2().getHyperVHostStorage(server, userName, password);
				if(storageList!=null) {
					for(HyperVHostStorage storage : storageList) {
						HyperVHostStorageModel model = new HyperVHostStorageModel();
						model.setDrive(storage.getDrive());
						model.setFreeSize(storage.getFreeSize());
						model.setTotalSize(storage.getTotalSize());
						model.setPath(storage.getPath());
						modelList.add(model);
					}
					return modelList;
				}
			}
		} catch (WebServiceException exception) {
			logger.debug(exception.getMessage());
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null) {
					
				}
			}
			proccessAxisFaultException(exception);
		}
		return null;
	}

	@Override
	public int CompareHyperVVersion(String server, String userName,	String password, String sessUserName, String sessPassword, String sessRootPath, int sessNumber)
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("CompareHyperVVersion() - start");
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				return  client.getServiceV2().CompareHyperVVersion(server, userName, password, sessUserName, sessPassword, sessRootPath, sessNumber);
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}
		return 0;
	}

	@Override
	public String getHyperVDefaultFolderOfVHD(String server, String userName,
			String password) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException{
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				return  client.getServiceV2().getHyperVDefaultFolderOfVHD(server, userName, password);
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}
		return null;
	}

	@Override
	public String GetHyperVDefaultFolderOfVM(String server, String userName,
			String password) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException{
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				return  client.getServiceV2().GetHyperVDefaultFolderOfVM(server, userName, password);
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}
		return null;
	}
	
	@Override
	public long getVMVFlashReadCache(String rootPath, int sessionNum, String userName, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
			return this.getServiceClient().getServiceV2().getVMVFlashReadCache(rootPath, sessionNum, userName, password);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return -1;
	}

	@Override
	public long getESXVFlashResource(VirtualCenterModel vcModel, ESXServerModel esxServerModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
			return this.getServiceClient().getServiceV2().getESXVFlashResource(ConvertToVirtualCenter(vcModel), ConvertToESXServer(esxServerModel));
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return -1;
	}

	@Override
	public int getHyperVServerType(String server, String userName,
			String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				return  client.getServiceV2().getHyperVServerType(server, userName, password);
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}
		return -1;
	}

	@Override
	public List<String> getHyperVClusterNodes(String server, String userName,
			String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				return  client.getServiceV2().getHyperVClusterNodes(server, userName, password);
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}
		return null;
	}
	
	@Override
	public int validateVCloud(VCloudDirectorModel directorModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("validateVCloud() - start");
		WebServiceClientProxy client = null;
		int ret = -1;
		try {
			client = this.getServiceClient();
			if (client != null) {
				ret = client.getServiceV2().validateVCloud(convertToVCloudDirector(directorModel));
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}
		return ret;
	}
	
	@Override
	public List<VCloudOrgnizationModel> getVCloudOrganizations(VCloudDirectorModel directorModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getVCloudOrganizations() - start");
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				List<VCloudOrg> orgList = client.getServiceV2().getVCloudOrganizations(convertToVCloudDirector(directorModel));
				return convertToVCloudOrgnizationModelList(orgList);
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}
		
		return new LinkedList<VCloudOrgnizationModel>();
	}
	
	private VCloudDirector convertToVCloudDirector(VCloudDirectorModel directorModel) {
		if (directorModel == null) {
			return null;
		}

		VCloudDirector vCouldCirector = new VCloudDirector();
		vCouldCirector.setName(directorModel.getName());
		vCouldCirector.setUsername(directorModel.getUsername());
		vCouldCirector.setPassword(directorModel.getPassword() == null ? "" : directorModel.getPassword());
		vCouldCirector.setProtocol(directorModel.getProtocol());
		vCouldCirector.setPort(directorModel.getPort() == null ? 0 : directorModel.getPort());

		return vCouldCirector;
	}
	
	private List<VCloudOrgnizationModel> convertToVCloudOrgnizationModelList(List<VCloudOrg> orgList){
		List<VCloudOrgnizationModel> modelList = new LinkedList<VCloudOrgnizationModel>();
		
		if(orgList == null || orgList.isEmpty()){
			return modelList;
		}

		for (VCloudOrg org : orgList) {
			VCloudOrgnizationModel serverModel = new VCloudOrgnizationModel();
			serverModel.setName(org.getName());
			serverModel.setId(org.getId());
			serverModel.setFullName(org.getFullName());
			serverModel.setDescription(org.getDescription());
			serverModel.setVitrualDataCenters(convertToVCloudVirtualDataCenterModelList(org.getVCloudVDCs()));
			
			modelList.add(serverModel);
		}
		
		return modelList;
	}
	
	private List<VCloudVirtualDataCenterModel> convertToVCloudVirtualDataCenterModelList(List<VCloudVirtualDC> vDCList){
		List<VCloudVirtualDataCenterModel> modelList = new LinkedList<VCloudVirtualDataCenterModel>(); 
		if (vDCList == null || vDCList.isEmpty()) {
			return modelList;
		}
		
		for (VCloudVirtualDC vDC : vDCList) {
			VCloudVirtualDataCenterModel serverModel = new VCloudVirtualDataCenterModel();
			serverModel.setName(vDC.getName());
			serverModel.setId(vDC.getId());
			serverModel.setAllocationModel(vDC.getAllocationModel());
			serverModel.setMemoryLimit(getSizeInBytes(vDC.getMemoryLimit(), vDC.getUnitOfMemory()));
			serverModel.setAvailableNetworks(convert2VMNetworkConfigInfoModelList(vDC.getAvailableNetworks()));
			serverModel.setSupportedHardwareVersions(vDC.getSupportedHardwareVersions());
			serverModel.setVCenters(convertToVirtualCenterModelList(vDC.getVCloudVCs()));
			serverModel.setCPUCount(vDC.getCpuCount());
			
			modelList.add(serverModel);
		}
		
		return modelList;
	}
	
	private List<VMNetworkConfigInfoModel> convert2VMNetworkConfigInfoModelList(List<VMNetworkConfig> networkConfigList) {
		List<VMNetworkConfigInfoModel> result = new LinkedList<VMNetworkConfigInfoModel>();
		if (networkConfigList == null || networkConfigList.isEmpty()) {
			return result;
		}
		
		for (VMNetworkConfig networkCfg : networkConfigList){
			VMNetworkConfigInfoModel model = new VMNetworkConfigInfoModel();
			model.setBackingInfoType(networkCfg.getBackingInfoType());
			model.setDeviceName(networkCfg.getDeviceName());
			model.setDeviceType(networkCfg.getDeviceType());
			model.setLabel(networkCfg.getLabel());
			model.setPortgroupKey(networkCfg.getPortgroupKey());
			model.setPortgroupName(networkCfg.getPortgroupName());
			model.setSwitchName(networkCfg.getSwitchName());
			model.setSwitchUUID(networkCfg.getSwitchUUID());
			model.setNetworkId(networkCfg.getId());
			result.add(model);
		}
		
		return result;
	}

	private List<VirtualCenterModel> convertToVirtualCenterModelList(List<VCloudVC> vcList){
		List<VirtualCenterModel> modelList = new LinkedList<VirtualCenterModel>();
		
		if (vcList == null || vcList.isEmpty()) {
			return modelList;
		}
		
		for (VCloudVC vc : vcList) {
			VirtualCenterModel serverModel = new VirtualCenterModel();
			serverModel.setVcName(vc.getHostname());
			serverModel.setId(vc.getId());
			serverModel.setUsername(vc.getUsername());
			serverModel.setPassword(vc.getPassword());
			serverModel.setProtocol(vc.getProtocol());
			serverModel.setPort(vc.getPort());
			
			modelList.add(serverModel);
		}
		
		return modelList;
	}
	
	public List<VCloudStorageProfileModel> getStorageProfilesOfVDC(VCloudDirectorModel directorModel, String vDCId) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getVCloudOrganizations() - start");
		List<VCloudStorageProfileModel> modelList = new ArrayList<VCloudStorageProfileModel>();
		
		if (directorModel == null) {
			return modelList;
		}

		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				List<VCloudVDCStorageProfile> profileList = client.getServiceV2().getStorageProfilesOfVDC(
						convertToVCloudDirector(directorModel), vDCId);
				return convertToVCloudStorageProfileModelList(profileList);
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}

		return modelList;
	}
	
	public List<ESXServerModel> getESXHosts4VAppChildVM(VCloudDirectorModel directorModel, VirtualCenterModel vcModel, String vDCId, String datastoreMoRef) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getESXHosts4VAppChildVM() - start");
		List<ESXServerModel> modelList = new ArrayList<ESXServerModel>();

		if (directorModel == null || vcModel == null || vDCId == null || vDCId.trim().isEmpty()
				|| datastoreMoRef == null) {
			return modelList;
		}

		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				List<ESXServer> serverList = client.getServiceV2().getESXHosts4VAppChildVM(convertToVCloudDirector(directorModel),
						ConvertToVirtualCenter(vcModel), vDCId, datastoreMoRef);
				if (serverList != null && !serverList.isEmpty()) {
					for (ESXServer server : serverList ) {
						ESXServerModel model = ConvertToESXServerModel(server);
						if (model != null) {
							modelList.add(model);
						}
					}
				}
			}
		} catch (WebServiceException exception) {
			logger.error(exception.getMessage());
			proccessAxisFaultException(exception);
		}

		return modelList;
	}
	
	private List<VCloudStorageProfileModel> convertToVCloudStorageProfileModelList(List<VCloudVDCStorageProfile> profileList) {
		List<VCloudStorageProfileModel> modelList = new ArrayList<VCloudStorageProfileModel>(); 
		
		if (profileList == null || profileList.isEmpty()) {
			return modelList;
		}
		
		for (VCloudVDCStorageProfile profile : profileList) {
			VCloudStorageProfileModel model = new VCloudStorageProfileModel();
			model.setName(profile.getName());
			model.setId(profile.getId());
			model.setFreeSize(getSizeInBytes(profile.getLimit() - profile.getRequested(), profile.getUnitsOfLimit()));
			model.setStorages(convertToVMStorageList(profile.getStorages()));
			
			modelList.add(model);
		}
		
		Collections.sort(modelList, new Comparator<VCloudStorageProfileModel>() {
			@Override
			public int compare(VCloudStorageProfileModel o1, VCloudStorageProfileModel o2) {
				if (o1 == null || o1.getName() == null) {
					return -1;
				}
				if (o2 == null || o2.getName() == null) {
					return 1;
				}
				
				return o1.getName().compareTo(o2.getName());
			}
		});
		return modelList;
	}
	
	private long getSizeInBytes(long size, String unit) {
		long sizeInKB = 0L;
		if (size <= 0 || unit == null || unit.trim().isEmpty()) {
			return sizeInKB;
		}

		unit = unit.toUpperCase();
		switch (unit) {
		case "KB":
			sizeInKB = size * 1024;
		case "MB":
			sizeInKB = size * 1024 * 1024;
			break;
		case "GB":
			sizeInKB = size * 1024 * 1024 * 1024;
			break;
		default:
			sizeInKB = size;
		}
		return sizeInKB;
	}
	
	private List<VMStorage> convertToVMStorageList(List<DataStore> dataStoreList) {
		List<VMStorage> modelList = new LinkedList<VMStorage>();
		
		if (dataStoreList == null || dataStoreList.isEmpty()) {
			return modelList;
		}
		
		for (DataStore dataStore : dataStoreList) {
			VMStorage vmStorage = ConvertToVMStorage(dataStore);
			if (vmStorage != null) {
				modelList.add(vmStorage);
			}
		}
		
		Collections.sort(modelList, new Comparator<VMStorage>() {
			@Override
			public int compare(VMStorage o1, VMStorage o2) {
				if (o1 == null || o1.getName() == null) {
					return -1;
				}
				if (o2 == null || o2.getName() == null) {
					return 1;
				}
				
				return o1.getName().compareTo(o2.getName());
			}
		});
			
		return modelList;
	}
	
	@Override
	public VCloudVirtualDataCenterModel getVAppVDCFromSession(String vAppDestination, int vAppSessionNumer, String fullUsername, String password) {
		logger.debug("getVAppVDCFromSession() - start");

		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if (client != null) {
				VCloudVirtualDC vDC = client.getServiceV2().getVAppVDCFromSession(vAppDestination, vAppSessionNumer, fullUsername, password);
				if (vDC == null) {
					return null;
				}
				
				VCloudVirtualDataCenterModel model = new VCloudVirtualDataCenterModel();
// only name and Id are needed to do auto match
				model.setName(vDC.getName());
				model.setId(vDC.getId());
//				model.setAllocationModel(vDC.getAllocationModel());
//				model.setAvailableNetworks(convert2VMNetworkConfigInfoModelList(vDC.getAvailableNetworks()));
//				model.setSupportedHardwareVersions(vDC.getSupportedHardwareVersions());
//				model.setVCenters(convertToVirtualCenterModelList(vDC.getVCloudVCs()));
//				model.setCPUCount(vDC.getCpuCount());
				
				return model;
			}

		} catch (Exception e) {
			logger.debug(e.toString());
		}
		
		return null;
	}
	
	@Override
	public List<VAppBackupVMRecoveryPointModelWrapper> getVAppChildBackupVMsAndRecoveryPoints(String vAppDestination,
			int vAppSessionNumer, String domain, String username, String password) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getVAppChildBackupVMsAndRecoveryPoints() - start");
		List<VAppBackupVMRecoveryPointModelWrapper> modelList = new ArrayList<VAppBackupVMRecoveryPointModelWrapper>();
		
		WebServiceClientProxy client = null;
		try{
			client = this.getServiceClient();
			if (client != null) {
				List<VAppChildBackupVMRestorePointWrapper> wrapperList = client.getServiceV2().getVAppChildBackupVMsAndRecoveryPoints(vAppDestination, vAppSessionNumer, domain, username, password);
				
				if (wrapperList == null || wrapperList.size() == 0) {
					return modelList;
				}
				
				for (VAppChildBackupVMRestorePointWrapper wrapper : wrapperList) {
					BackupVMModel backupVMModel = ConvertToBackupVMModel(wrapper.getBackupVM());
					RecoveryPointModel recoveryPointModel = ConvertToModel(wrapper.getRecoveryPoint());
					modelList.add(new VAppBackupVMRecoveryPointModelWrapper(backupVMModel, recoveryPointModel));
				}
				return modelList;
			}
		}catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getBackupVMModel() - end");
		return null;
	
	}

	@Override
	public Map<String, List<DiskModel>> getVAppChildVMDisks(Map<String, List<String>> pathSubPathMap, String domain,
			String username, String password) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getVAppChildVMDisks() - start");
		Map<String, List<DiskModel>> resultMap = new HashMap<String, List<DiskModel>>();
		
		Set<String> instanceUuidSet = pathSubPathMap.keySet();
		for (String instanceUuid : instanceUuidSet) {
			List<String> pathSubPathList = pathSubPathMap.get(instanceUuid);
			List<DiskModel> vmDiskList = getBackupVMDisk(pathSubPathList.get(0), pathSubPathList.get(1), domain, username, password);
			resultMap.put(instanceUuid, vmDiskList);
		}
		
		return resultMap;
	}

	@Override
	public Map<String, List<VMNetworkConfigInfoModel>> getVAppAndChildVMNetworkConfigLists(String rootPath, int sessionNum, String userName, String password)
			throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getVAppAndChildVMNetworkConfigLists() - start");
		Map<String, List<VMNetworkConfigInfoModel>> resultMap = new HashMap<String, List<VMNetworkConfigInfoModel>>();
		
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			List<VMNetworkConfig> totalNetworkCfgList = client.getServiceV2().getVAppAndChildVMNetworkConfigLists(rootPath, sessionNum, userName, password);
			if (totalNetworkCfgList == null || totalNetworkCfgList.isEmpty()) {
				return resultMap;
			}
			
			for (VMNetworkConfig networkCfg : totalNetworkCfgList) {
				String name = networkCfg.getNodeName();
				List<VMNetworkConfigInfoModel> networkModels = resultMap.get(name);
				if (networkModels == null) {
					networkModels = new ArrayList<>();
					resultMap.put(name, networkModels);
				}

				VMNetworkConfigInfoModel model = new VMNetworkConfigInfoModel();
				networkModels.add(model);

				model.setLabel(networkCfg.getLabel());
				model.setNetworkId(networkCfg.getId());
				model.setAdapterType(networkCfg.getAdapterType());
				model.setParentName(networkCfg.getParentName());
				model.setParentId(networkCfg.getParentId());
			}
			return resultMap;
		} catch(WebServiceException exception){
			resultMap.clear();
			proccessAxisFaultException(exception);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		
		return resultMap;
	}

	@Override
	public List<String> getVMAdapterTypes(VirtualCenterModel vcModel, ESXServerModel esxModel)
			throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getAdapterTypes() - start");
		List<String> typeList = new ArrayList<>();
		
		if (vcModel == null || esxModel == null) {
			return typeList;
		}
		
		WebServiceClientProxy client = null;
		try
		{
			client = this.getServiceClient();
			String[] typeArray = client.getServiceV2().getVMAdapterTypes(ConvertToVirtualCenter(vcModel), ConvertToESXServer(esxModel));
			if (typeArray == null || typeArray.length > 0) {
				Arrays.sort(typeArray, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						if (o1 == null) {
							return -1;
						}
						if (o2 == null) {
							return 1;
						}
						
						return o1.compareTo(o2);
					}
				});
				typeList.addAll(Arrays.asList(typeArray));
			}
		} catch(WebServiceException exception){
			typeList.clear();
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			typeList.clear();
			logger.debug(e.getMessage());
		}
		
		return typeList;
	}

	@Override
	public List<GridTreeNode> getADNodes(GridTreeNode loadConfig) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		List<GridTreeNode> result = new ArrayList<GridTreeNode>();
		try {
			String destination = loadConfig.getBackupDestination();
			String userName = loadConfig.getDestUser();
			String passWord = loadConfig.getDestPwd();
			long sessionNumber = loadConfig.getSessionID();
			long subSessionID = loadConfig.getSubSessionID();
			String encryptedPwd = loadConfig.getEncryptedKey();
			int parentID = loadConfig.getId();
			String path = loadConfig.getPath();
			Integer state = loadConfig.getSelectionType();
			
			ADNode[] nodes = this.getServiceClient().getServiceGRT().getADNodes(destination , userName , passWord, sessionNumber , subSessionID , encryptedPwd , parentID);
			if(nodes!=null){
				for(ADNode node : nodes){
					if(node.getFlags()==ADNode.FLAGS_LEAF){// leaf node, not display in tree
						continue;
					}
					GridTreeNode model = convertADNodeToGridTreeNode(loadConfig, node);
					result.add(model);
				}
			}
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return result;
	}

	private GridTreeNode convertADNodeToGridTreeNode(GridTreeNode parent, ADNode node) {
		GridTreeNode model = new GridTreeNode();
		if(parent.getId()==0){
			model.setSelectable(false);
		}else{
			model.setSelectable(true);
		}
		model.setParentID(Long.valueOf(parent.getId()));
		model.setId((int)node.getId());
		model.setName(node.getName());
		model.setDisplayName(node.getName());
		
		if(node.getType()==ADNode.TYPE_GENERAL){
			model.setType(CatalogModelType.AD_GENERAL);
		}else if(node.getType()==ADNode.TYPE_USERS){
			model.setType(CatalogModelType.AD_USERS);
		}else if(node.getType()==ADNode.TYPE_USER){
			model.setType(CatalogModelType.AD_USER);
		}else if(node.getType()==ADNode.TYPE_COMPUTER){
			model.setType(CatalogModelType.AD_COMPUTER);
		}else if(node.getType()==ADNode.TYPE_OU){
			model.setType(CatalogModelType.AD_OU);
		}else{
			model.setType(CatalogModelType.AD_GENERAL);
		}
	
		String path = parent.getPath();
		if(!StringUtil.isEmptyOrNull(path)){
			path+=" > ";
		}
		model.setPath(path+node.getName());
		model.setChildrenCount(0L);
		if(parent.getSelectionType()!=null){
			model.setSelectionType(parent.getSelectionType());
		}else{
			model.setSelectionType(GridTreeNode.SELECTION_TYPE_NONE);
		}
		if(model.getSelectionType()==GridTreeNode.SELECTION_TYPE_FULL){
			model.setChecked(true);
		}else{
			model.setChecked(false);
		}
		return model;
	}

	@Override
	public List<GridTreeNode> getADAttributes(GridTreeNode loadConfig) throws BusinessLogicException, ServiceConnectException, ServiceInternalException  {
		List<GridTreeNode> result = new ArrayList<GridTreeNode>();
		try {
			String destination = loadConfig.getBackupDestination();
			String userName = loadConfig.getDestUser();
			String passWord = loadConfig.getDestPwd();
			long sessionNumber = loadConfig.getSessionID();
			long subSessionID = loadConfig.getSubSessionID();
			String encryptedPwd = loadConfig.getEncryptedKey();
			int nodeID =loadConfig.getId();
			ADAttribute[] attrs = this.getServiceClient().getServiceGRT().getADAttributes(destination , userName , passWord, sessionNumber , subSessionID , encryptedPwd , nodeID);
			if(attrs!=null){
				for(ADAttribute attribute : attrs){
					GridTreeNode model = convertADAttributeToGridTreeNode(loadConfig, attribute);
					result.add(model);
				}
			}
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return result;
	}

	private GridTreeNode convertADAttributeToGridTreeNode(GridTreeNode parent, ADAttribute attribute) {
		GridTreeNode model = new GridTreeNode();
		model.setParentID(Long.valueOf(parent.getId()));
		model.setName(attribute.getName());
		model.setDisplayName(attribute.getName());
		model.setType(CatalogModelType.AD_ATTRIBUTE);
		model.setSelectable(parent.getSelectable());
		if(parent.getSelectionType()!=null){
			model.setSelectionType(parent.getSelectionType());
		}else{
			model.setSelectionType(GridTreeNode.SELECTION_TYPE_NONE);
		}
		if(model.getSelectionType()==GridTreeNode.SELECTION_TYPE_FULL){
			model.setChecked(true);
		}else{
			model.setChecked(false);
		}
		String path = parent.getPath();
		if(!StringUtil.isEmptyOrNull(path)){
			path+=" > ";
		}
		model.setPath(path+attribute.getName());
		return model;
	}

	@Override
	public PagingLoadResult<GridTreeNode> getADPagingNodes(GridTreeNode parent,
			PagingLoadConfig loadConfig, String nameFilter) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try{
			ADPagingConfig config = new ADPagingConfig();
			config.setStartIndex(loadConfig.getOffset());
			config.setCount(loadConfig.getLimit());
			config.setDestination(parent.getBackupDestination());
			config.setUserName(parent.getDestUser());
			config.setPassWord(parent.getDestPwd());
			config.setSessionNumber(parent.getSessionID());
			config.setSubSessionID(parent.getSubSessionID());
			config.setEncryptedPwd(parent.getEncryptedKey());
			config.setParentID(parent.getId());
			ADNodeFilter filter = new ADNodeFilter();
			filter.setName(nameFilter);
			ADPagingResult result = this.getServiceClient().getServiceGRT().getADPagingNodes(config, filter);
			List<GridTreeNode> list = new ArrayList<GridTreeNode>();
			if (result != null && result.getData() != null && result.getData().size() > 0){
				for(ADNode node : result.getData()){
					GridTreeNode model = convertADNodeToGridTreeNode(parent,node);
					
					list.add(model);
				}
				return new BasePagingLoadResult<GridTreeNode>(list, result.getStartIndex(), result.getTotalCount());
			}
			return new BasePagingLoadResult<GridTreeNode>(list, 0, 0);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return null;
	}
	
	private String convertD2DTimeToString(D2DTimeModel time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(convertD2DTime(time));
	}	
	
	private Date convertD2DTime(D2DTimeModel time) {
		if (time == null) {
			return new Date();
		} else if (time.getYear() > 1900) {
			Calendar cal = Calendar.getInstance();
			cal.set(time.getYear(), time.getMonth(), time.getDay(),
					time.getHourOfDay(), time.getMinute(), time.getSecond());
			cal.set(Calendar.MILLISECOND, 0);
			return new Date(cal.getTimeInMillis());
		} else {
			return new Date();
		}
	}

	@Override
	public List<RpsHostModel> getRPSArchiveDestinations()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		List<RpsHostModel> result = new ArrayList<RpsHostModel>();
		WebServiceClientProxy client = null;
		try {
		
				client = this.getServiceClient();
				BackupConfiguration config = client.getServiceV2().getBackupConfiguration();
				result.add(convertToRpsSettingsModel(config.getBackupRpsDestSetting()).rpsHost);
				
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return result;
	}
	
	private D2DTimeModel getD2DTime(Date date) {
		if (null == date) {
			return null;
		}
		D2DTimeModel time = new D2DTimeModel();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		time.setYear(cal.get(Calendar.YEAR));
		time.setMonth(cal.get(Calendar.MONTH));
		time.setDay(cal.get(Calendar.DATE));
		time.setHour(cal.get(Calendar.HOUR));
		time.setHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
		time.setMinute(cal.get(Calendar.MINUTE));
		time.setSecond(cal.get(Calendar.SECOND));
//		time.setAmPM(-1);				
		return time;
	}

	@Override
	public List<ArchiveDestinationModel> getAllArchiveDestinationDetails(
			List<ArchiveDestinationModel> archiveManualDestList) throws BusinessLogicException, ServiceConnectException, ServiceInternalException 
	{
	
		List<ArchiveDestinationModel> archiveDestinations = new ArrayList<>();
		
		//for the manually added restore sources, call the backend and check if the catalog is present. If so, add it in the restore destination list.
		
		for (ArchiveDestinationModel archiveManualDest : archiveManualDestList) {
			ArchiveDestinationDetailsModel archiveManualDestDetail = getArchiveChangedDestinationDetails(archiveManualDest);
			if (archiveManualDestDetail.getCatalogAvailable()) {
				//Fix for tfs issue 749534 , display bucket name for manual restore cloud destination as decoded.
					if(archiveManualDest.getArchiveToCloud() && archiveManualDest.getCloudConfigModel().getencodedBucketName() != null
							&& archiveManualDest.getCloudConfigModel().getencodedBucketName().length() >0){
					archiveManualDest.getCloudConfigModel().setcloudBucketName(postFormatBucketName(archiveManualDest.getCloudConfigModel().getencodedBucketName()));
				}
				archiveDestinations.add(archiveManualDest);
			}
		}
			
		//Add the actual filecopy destination items to the restore destination list.
		
		List<ArchiveSettingsModel> archiveConfigs = getArchiveConfigurations();
			
		if(archiveConfigs != null&&!archiveConfigs.isEmpty()){
			ArchiveDestinationModel destinationinfo=new ArchiveDestinationModel();
			for(ArchiveSettingsModel archiveConfig:archiveConfigs){
				if(archiveConfig.getArchiveToDrive())
				{
					if(archiveConfig.getArchiveToDrivePath().length() != 0)
					{
						ArchiveDiskDestInfoModel diskinfo=new ArchiveDiskDestInfoModel();
						diskinfo.setArchiveDiskDestPath(archiveConfig.getArchiveToDrivePath());
						diskinfo.setArchiveDiskUserName(archiveConfig.getDestinationPathUserName());
						diskinfo.setArchiveDiskPassword(archiveConfig.getDestinationPathPassword());
						
						destinationinfo = new ArchiveDestinationModel();
						destinationinfo.setArchiveToDrive(true);
						destinationinfo.setArchiveToCloud(false);
						destinationinfo.setArchiveDiskDestInfoModel(diskinfo);
						destinationinfo.setArchiveToRPS(archiveConfig.isArchiveToRPS());
						destinationinfo.setPolicyUUID(archiveConfig.getPolicyUUID());
						destinationinfo.setRpsHostModel(archiveConfig.getHost());
						destinationinfo.setHostName(archiveConfig.getHostName());
						destinationinfo.setArchiveType(archiveConfig.getType());
						destinationinfo.setCatalogPath(archiveConfig.getCatalogPath());
						destinationinfo.setEncryption(archiveConfig.getEncryption());
						destinationinfo.setEncryptionPassword(archiveConfig.getEncryptionPassword());
						
						//if(destinationinfo.getArchiveToRPS()){
						destinationinfo.setCatalogPath(archiveConfig.getCatalogPath());
						destinationinfo.setCatalogFolderUser(archiveConfig.getCatalogFolderUser());
						destinationinfo.setCatalogFolderPassword(archiveConfig.getCatalogFolderPassword());
						archiveDestinations.add(destinationinfo);
						//}
					}
				}
				else if(archiveConfig.getArchiveToCloud())
				{
					ArchiveCloudDestInfoModel cloudConfig = archiveConfig.getCloudConfigModel();
					if(cloudConfig != null)
					{
						destinationinfo = new ArchiveDestinationModel();
						destinationinfo.setCloudConfigModel(archiveConfig.getCloudConfigModel());
						destinationinfo.setArchiveToCloud(true);
						destinationinfo.setArchiveToDrive(false);
						destinationinfo.setArchiveToRPS(archiveConfig.isArchiveToRPS());
						destinationinfo.setArchiveType(archiveConfig.getType());
						destinationinfo.setPolicyUUID(archiveConfig.getPolicyUUID());
						destinationinfo.setRpsHostModel(archiveConfig.getHost());
						destinationinfo.setHostName(archiveConfig.getHostName());
						destinationinfo.setCatalogPath(archiveConfig.getCatalogPath());
						destinationinfo.setEncryption(archiveConfig.getEncryption());
						destinationinfo.setEncryptionPassword(archiveConfig.getEncryptionPassword());
						
						//if(destinationinfo.getArchiveToRPS()){
							destinationinfo.setCatalogPath(archiveConfig.getCatalogPath());
							destinationinfo.setCatalogFolderUser(archiveConfig.getCatalogFolderUser());
							destinationinfo.setCatalogFolderPassword(archiveConfig.getCatalogFolderPassword());
							archiveDestinations.add(destinationinfo);
						//}
					}
				}
			}
			
		}
		return archiveDestinations;
	}

}
