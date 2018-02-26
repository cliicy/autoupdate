package com.ca.arcflash.ui.server;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.model.EdgeLicenseInfo;
import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.ha.model.SummaryModel;
import com.ca.arcflash.ha.model.VCMConfigStatus;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VSphereProxyServer;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.serviceinfo.ServiceInfo;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.serviceinfo.ServiceInfoList;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.LogEntry;
import com.ca.arcflash.ui.client.model.LogEntryType;
import com.ca.arcflash.ui.server.servlet.SessionConstants;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.ID2DCSFlashService;
import com.ca.arcflash.webservice.IFlashServiceV2;
import com.ca.arcflash.webservice.ServiceProviders;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.data.VMwareServer;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.activitylog.ActivityLog;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogType;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 *	We need to use this Class in any of the following two possibilities:
 *		1. access one VC client's web service from VCM when switching to the VC client 
 *		2. access web service of its own from its own web UI.
 *  In the first circumstance, this class will automatically switch the web service client by
 *  {@link #connectMoniteeServer connectMoniteeServer}.
 *  
 */
public class ColdStandbyServiceImpl extends BaseServiceImpl implements ColdStandbyService{
	
	private static final long serialVersionUID = -6439595995442833807L;

	private static final Logger logger = Logger.getLogger(ColdStandbyServiceImpl.class);
	
	private String GetDomainName(String strUserInput)
	{
		String strDomain = "";
		if (strUserInput == null || strUserInput.isEmpty())
			return strDomain;
		
		int pos = strUserInput.indexOf("\\");
		if (pos == -1){
			
		}else{
			strDomain = strUserInput.substring(0, pos);
		}
		return strDomain;
	}
	
	private String GetUserName(String strUserInput)
	{
		String strUser = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strUser;
		
		int pos = strUserInput.indexOf("\\");
		if (pos == -1){
			strUser = strUserInput;
		}else{
			strUser = strUserInput.substring(pos+1);
		}
		return strUser;
	}
	
	private void setMonitorClientForEdge(WebServiceClientProxy client) {
		this.getThreadLocalRequest().getSession(true).setAttribute(SessionConstants.STRING_MONITORPROXY,client);
	}
	
	@Override
	public String testMonitorConnection(String serverName, int port, ConnectionProtocol protocol,	String username, String password, boolean isSaveProxy) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("testMonitorConnection(String, int, String, String) - start"); //$NON-NLS-1$
		if (logger.isDebugEnabled()){
			logger.debug("server name:"+serverName);
			logger.debug("port:"+port);
			logger.debug("protocal:"+protocol);
			logger.debug("username:"+username);
		}
		
		String uuid =null;
		try {
			WebServiceClientProxy client = ServiceProviders.getRemoteFlashServiceProvider().create(
					protocol==ConnectionProtocol.HTTP?"http:":"https:", serverName, port, ServiceInfoConstants.SERVICE_ID_D2D_V2);
			uuid = client.getService().validateUser(GetUserName(username), password, GetDomainName(username));
			
			if(isSaveProxy){
				setMonitorClientForEdge(client);
			}
			
			//check the SaaS node
			VersionInfo versionInfo = client.getService().getVersionInfo();
			if((versionInfo!=null)&&(versionInfo.getProductType()!=null)&&(versionInfo.getProductType().equals("1"))){
				String locale = this.getServerLocale();
				String erorMsg = MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
								+ FlashServiceErrorCode.VCM_MONITOR_IS_SAAS_NODE, locale), serverName);
				throw new BusinessLogicException(FlashServiceErrorCode.VCM_MONITOR_IS_SAAS_NODE, erorMsg);
			}
		} catch (WebServiceException exception) {
			logger.error("testMonitorConnection(String, int, String, String)", exception); //$NON-NLS-1$
			if(exception.getCause() instanceof UnknownHostException	){
				String locale = this.getServerLocale();
				throw new ServiceConnectException(
						FlashServiceErrorCode.VCM_MONITOR_UnkownHost,
						ResourcesReader.getResource("ServiceError_"
								+ FlashServiceErrorCode.VCM_MONITOR_UnkownHost,
								locale));
			}
			else if (exception.getCause() instanceof ConnectException || exception.getCause() instanceof SocketException
				|| exception.getCause() instanceof SSLHandshakeException) {
				String locale = this.getServerLocale();
				throw new ServiceConnectException(
						FlashServiceErrorCode.VCM_MONITOR_FailConnectService,
						ResourcesReader.getResource("ServiceError_"
								+ FlashServiceErrorCode.VCM_MONITOR_FailConnectService,
								locale));
			}
			
			proccessAxisFaultException(exception);
		}

		logger.debug("testMonitorConnection(String, int, String, String) - end"); //$NON-NLS-1$
		
		return uuid;
	}
	
	@Override
	public int vcmValidateUserByUUID(String uuid) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("vcmValidateUserByUUID(String) - start"); //$NON-NLS-1$
		if (logger.isDebugEnabled()){
			logger.debug("uuid:"+uuid);
		}
		int result = 0;
		WebServiceClientProxy client = getMonitorClientProxy();
		try{
			if(client!=null){
				result = client.getServiceV2().validateUserByUUID(uuid);
			}
			else {
				logger.error("the client is null");
			}

		}catch(WebServiceException exception){
			logger.error("vcmValidateUserByUUID()", exception); //$NON-NLS-1$
			if (exception.getCause() instanceof ConnectException || exception.getCause() instanceof SocketException
					|| exception.getCause() instanceof UnknownHostException	) {
					String locale = this.getServerLocale();
					throw new ServiceConnectException(
							FlashServiceErrorCode.VCM_FAILED_CONNECT_MONITOR_BACKEND,
							ResourcesReader.getResource("ServiceError_"
									+ FlashServiceErrorCode.VCM_FAILED_CONNECT_MONITOR_BACKEND,
									locale));
				}
				
			proccessAxisFaultException(exception);
		}
		return result;
	}


	@Override
	public void startHeartBeat(String afGuid) throws BusinessLogicException,	ServiceConnectException, ServiceInternalException {
		logger.debug("startHeartBeat() - start"); //$NON-NLS-1$

		try {
			 getD2DCSService().startHeartBeat(afGuid);
		} catch (WebServiceException e) {
			logger.error("startHeartBeat()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("startHeartBeat() - end"); //$NON-NLS-1$
	}

	@Override
	public String[] getESXHostDataStoreList(String esxServer, String username,	String passwod, String protocol, int port, BaseModel esxHost)
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("getESXHostDataStoreList(String, String, String, String, int, VWWareESXNode) - start"); //$NON-NLS-1$
		
		try {
			VWWareESXNode[] nodes = covertFromESXNodeToBaseModel(new BaseModel[]{esxHost});
			
			String[] storages =  getD2DCSService().getESXHostDataStoreList(esxServer, username, passwod, protocol, port, nodes[0]);

			logger.debug("getESXHostDataStoreList(String, String, String, String, int, VWWareESXNode) - end"); //$NON-NLS-1$
			return storages;
		} catch (WebServiceException e) {
			logger.error("getESXHostDataStoreList(String, String, String, String, int, VWWareESXNode)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("getESXHostDataStoreList(String, String, String, String, int, VWWareESXNode) - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public BaseModel[] getESXNodeList(String esxServer, String username, String passwod, String protocol, int port)
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("getESXNodeList(String, String, String, String, int) - start"); //$NON-NLS-1$

		try {
		
			VWWareESXNode[] nodes =  getD2DCSService().getESXNodeList(esxServer, username, passwod, protocol, port);
			
			logger.debug("getESXNodeList(String, String, String, String, int) - end"); //$NON-NLS-1$
			return covertFromESXNodeToBaseModel(nodes);
		} catch (WebServiceException e) {
			logger.error("getESXNodeList(String, String, String, String, int)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("getESXNodeList(String, String, String, String, int) - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public NetworkAdapter[] getProdServerNetworkAdapters()
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("getProdServerNetworkAdapters() - start"); //$NON-NLS-1$

		try {
		
			NetworkAdapter[] networkAdapters= getD2DCSService().getProdServerNetworkAdapters();
			
			logger.debug("getProdServerNetworkAdapters() - end"); //$NON-NLS-1$
			
			return networkAdapters;
		} catch (WebServiceException e) {
			logger.error("getProdServerNetworkAdapters()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("getProdServerNetworkAdapters() - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public DiskModel[] getProductionServerDiskList()
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("getProductionServerDiskList() - start"); //$NON-NLS-1$

		try {
		
			DiskModel[] vmDisks=getD2DCSService().getProductionServerDiskList();
			
			logger.debug("getProductionServerDiskList() - end"); //$NON-NLS-1$
			
			return vmDisks;
			//return null;
		} catch (WebServiceException e) {
			logger.error("getProductionServerDiskList()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("getProductionServerDiskList() - end"); //$NON-NLS-1$
		return null;
	}
	
	@Override
	public  VMStorage[] getVmStorages(String host, String username, String password,
            String protocol, boolean ignoreCertAuthentidation, long viPort,
            String esxName,String dcName, String[] storageNames)    
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("getVmStorages(String, String, String,String, boolean,viPort,VWWareESXNode, String[]) - start"); //$NON-NLS-1$

		try {
		
			VWWareESXNode esxNode=new VWWareESXNode(esxName,dcName);
			
			VMStorage[] vmStorage =  getD2DCSService().getVmStorages(host, username, password, protocol, ignoreCertAuthentidation, viPort, esxNode, storageNames);
			
			logger.debug("getVmStorages(String, String, String,String, boolean,viPort,VWWareESXNode, String[]) - end"); //$NON-NLS-1$
			
			return vmStorage;
		} catch (WebServiceException e) {
			logger.error("getVmStorages(String, String, String,String, boolean,viPort,VWWareESXNode, String[])", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("getVmStorages(String, String, String,String, boolean,viPort,VWWareESXNode, String[]) - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public FailoverJobScript getFailoverJobScript(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getFailoverJobScript() - start"); //$NON-NLS-1$

		try {
			
			String script =  getD2DCSService().getFailoverJobScript(vmInstanceUUID);
			FailoverJobScript returnFailoverJobScript = CommonUtil.unmarshal(script, FailoverJobScript.class);
			logger.debug("getFailoverJobScript() - end"); //$NON-NLS-1$
			return returnFailoverJobScript;
		} catch (WebServiceException e) {
			logger.error("getFailoverJobScript()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		} catch (JAXBException e) {
			logger.warn("getFailoverJobScript() - exception ignored", e); //$NON-NLS-1$
		}

		logger.debug("getFailoverJobScript() - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public HeartBeatJobScript getHeartBeatJobScript(String afGuid) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getHeartBeatJobScript() - start"); //$NON-NLS-1$

		try {
		
			String script =  getD2DCSService().getHeartBeatJobScript(afGuid);
			HeartBeatJobScript returnHeartBeatJobScript = CommonUtil.unmarshal(script, HeartBeatJobScript.class);
			logger.debug("getHeartBeatJobScript() - end"); //$NON-NLS-1$
			return returnHeartBeatJobScript;
		} catch (WebServiceException e) {
			logger.error("getHeartBeatJobScript()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		} catch (JAXBException e) {
			logger.warn("getHeartBeatJobScript() - exception ignored", e); //$NON-NLS-1$
		}

		logger.debug("getHeartBeatJobScript() - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public ReplicationJobScript getReplicationJobScript(String afGuid) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("getReplicationJobScript() - start"); //$NON-NLS-1$

		try {
			
			String script =  getD2DCSService().getReplicationJobScript(afGuid);
			ReplicationJobScript returnReplicationJobScript = CommonUtil.unmarshal(script, ReplicationJobScript.class);
			logger.debug("getReplicationJobScript() - end"); //$NON-NLS-1$
			return returnReplicationJobScript;
		}catch (WebServiceException e) {
			logger.error("getReplicationJobScript()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		} catch (Exception e) {
			logger.warn("getReplicationJobScript() - exception ignored", e); //$NON-NLS-1$
		}

		logger.debug("getReplicationJobScript() - end"); //$NON-NLS-1$
		return null;
	}
	
	private JobScriptCombo getJobScriptCombo(ID2DCSFlashService service, String vmInstanceUUID) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getJobScriptCombo() - start");

		try {
			String script = service.getJobScriptCombo(vmInstanceUUID);
			JobScriptCombo jobCombo = CommonUtil.unmarshal(script,
					JobScriptCombo.class);
			logger.debug("getJobScriptCombo() - end");
			return jobCombo;
		} catch (WebServiceException e) {
			logger.error("getJobScriptCombo()", e);
			proccessAxisFaultException(e);
		} catch (Exception e) {
			logger.warn("getJobScriptCombo() - exception ignored", e);
		}

		logger.debug("getJobScriptCombo() - end");
		return null;
	}
	
	@Override
	public JobScriptCombo getJobScriptCombo(String vmInstanceUUID) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
//		return getJobScriptCombo(getD2DCSService(), vmInstanceUUID);
		JobScriptCombo agentResult = getJobScriptCombo(getD2DCSService(), vmInstanceUUID);	
		if (agentResult.getFailoverJobScript() != null && !agentResult.getFailoverJobScript().getBackupToRPS())
			return agentResult;
		FailoverJobScript failoverJobScript = getFailoverJobScriptEx(vmInstanceUUID);
		if (failoverJobScript != null && failoverJobScript.getBackupToRPS()) {
			if (vmInstanceUUID == null || vmInstanceUUID == "") {
				if (getCurrentMonitee() != null)
					vmInstanceUUID = getCurrentMonitee().getUuid();
			}
			JobScriptCombo converterResult = getJobScriptCombo(getConverterService(failoverJobScript), vmInstanceUUID);
			if (!getCurrentMonitee().isRemoteNode())
				converterResult.setHbJobScript(agentResult.getHbJobScript());
			return converterResult;
		}
		else
			return agentResult;
	}
	
	@Override
	public JobScriptCombo getLocalJobScriptCombo(String vmInstanceUUID) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		return getJobScriptCombo(getServiceClient().getServiceV2(), vmInstanceUUID);
	}

	@Override
	public void setJobScriptCombo(JobScriptCombo jc)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		logger.debug("setJobScriptCombo() - start"); //$NON-NLS-1$

		try {
			 getD2DCSService().setJobScriptCombo(CommonUtil.marshal(jc));
		} catch (WebServiceException e) {
			logger.error("setJobScriptCombo(JobScriptCombo)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		} catch (JAXBException e) {
			logger.warn("setJobScriptCombo(JobScriptCombo) - exception ignored", e); //$NON-NLS-1$
		}

		logger.debug("setJobScriptCombo() - end"); //$NON-NLS-1$
		
	}
	@Override
	public void startReplication(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("startReplication() - start"); //$NON-NLS-1$

		try {
			FailoverJobScript failoverJobScript = getFailoverJobScriptEx(vmInstanceUUID);
			if (failoverJobScript != null && failoverJobScript.getBackupToRPS())
				getConverterService(failoverJobScript).startReplication(failoverJobScript.getAFGuid());
			else
				getD2DCSService().startReplication(vmInstanceUUID);
		} catch (WebServiceException e) {
			logger.error("startReplication()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("startReplication() - end"); //$NON-NLS-1$
	}

	@Override
	public void cancelReplication(String afGuid) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("cancelReplication() - start"); 
		
		try {
			FailoverJobScript failoverJobScript = getFailoverJobScriptEx(afGuid);
			
			if (failoverJobScript != null && failoverJobScript.getBackupToRPS())
				getConverterService(failoverJobScript).cancelReplication(failoverJobScript.getAFGuid());
			else
				getD2DCSService().cancelReplication(afGuid);
			
		} catch (WebServiceException e) {
			logger.error("cancelReplication()", e); 
			proccessAxisFaultException(e);
		}

		logger.debug("cancelReplication() - end");
	}
	
	@Override
	public RepJobMonitor getReplicaJobMonitor(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getReplicaJobMonitor() - start"); //$NON-NLS-1$

		try {
			FailoverJobScript failoverJobScript = getFailoverJobScriptEx(vmInstanceUUID);
			
			RepJobMonitor monitor = null;
			if (failoverJobScript.getBackupToRPS())
				monitor = getConverterService(failoverJobScript).getRepJobMonitor(failoverJobScript.getAFGuid());
			else
				monitor = getD2DCSService().getRepJobMonitor(vmInstanceUUID);

			if (logger.isDebugEnabled())
				logger.debug(monitor);

			logger.debug("getReplicaJobMonitor() - end"); //$NON-NLS-1$
			return monitor;
		} catch (WebServiceException e) {
			logger.error("getReplicaJobMonitor()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		
		logger.debug("getReplicaJobMonitor() - end"); //$NON-NLS-1$
		return null;
	}
	
	private FailoverJobScript getFailoverJobScriptEx(String vmInstanceUUID) {
		if (vmInstanceUUID == null || vmInstanceUUID == "") {
			if (getCurrentMonitee() != null)
				vmInstanceUUID = getCurrentMonitee().getUuid();
		}
		
		String jobScript =  getServiceClient().getServiceV2().getFailoverJobScript(vmInstanceUUID);
		FailoverJobScript failoverJobScript = null;
		try {
			failoverJobScript = CommonUtil.unmarshal(jobScript, FailoverJobScript.class);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return failoverJobScript;
	}
	
	private IFlashServiceV2 getConverterService(FailoverJobScript jobScript)
	{		
		WebServiceClientProxy proxy = null;
		ServiceInfoList serviceInfoList = null;
		serviceInfoList = WebServiceFactory.getServiceInfoList(jobScript.getConverterProtocol(), 
				jobScript.getConverterHostname(), jobScript.getConverterPort());
		ServiceInfo featureServiceInfo = WebServiceFactory.getFeatureServiceInfo(ServiceInfoConstants.SERVICE_ID_D2D_V2, serviceInfoList);
		proxy = WebServiceFactory.getFlassService(jobScript.getConverterProtocol(),
				jobScript.getConverterHostname(), jobScript.getConverterPort(),
				ServiceInfoConstants.SERVICE_ID_D2D_V2,featureServiceInfo);
		proxy.getServiceV2().validateUserByUUID(jobScript.getConverterUUID());
		return proxy.getServiceV2();
	}
	
	private BaseModel[] covertFromESXNodeToBaseModel(VWWareESXNode[] nodes){
		if(nodes == null || nodes.length == 0){
			return null;
		}
		BaseModel[] beans = new BaseModel[nodes.length];
		for(int i=0; i < nodes.length; i++){
			BaseModel model = new BaseModel();
			model.set("dataCenter", nodes[i].getDataCenter());
			model.set("esxNode", nodes[i].getEsxName());
			beans[i] = model;
		}
		return beans;
	}
	
	private VWWareESXNode[] covertFromESXNodeToBaseModel(BaseModel[] models){
		if(models == null || models.length == 0){
			return null;
		}
		VWWareESXNode[] nodes = new VWWareESXNode[models.length];
		for(int i=0; i<models.length; i++){
			VWWareESXNode node = new VWWareESXNode();
			node.setDataCenter((String)models[i].get("dataCenter"));
			node.setEsxName((String)models[i].get("esxNode"));
			nodes[i] = node;
		}
		return nodes;
	}

	@Override
	public int getVMwareServerType(String host, String username,String password, String protocol,int port) 
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getVMwareServerType(String, String, String, String) - start"); //$NON-NLS-1$
		if (logger.isDebugEnabled()){
			logger.debug("host:"+host);
			logger.debug("username:"+username);
			logger.debug("protocol:"+protocol);
		}
		
		try {
			
			VMwareServer serverType = getD2DCSService().getVMwareServerType(host, username, password,protocol,port);
			
			if (logger.isDebugEnabled())
				logger.debug("result:"+ serverType==null?"null":serverType.getVmtype());
			logger.debug("getVMwareServerType(String, String, String, String) - end"); //$NON-NLS-1$
			return serverType.getVmtype();
		} catch (WebServiceException e) {
			logger.error("getVMwareServerType(String, String, String, String)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("getVMwareServerType(String, String, String, String) - end"); //$NON-NLS-1$
		return 0;
	}

	@Override
	public String getESXServerVersion(String host, String username,	String password, String protocol,int port) 
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("getESXServerVersion(String, String, String, String) - start"); //$NON-NLS-1$
		if (logger.isDebugEnabled()){
			logger.debug("host:"+host);
			logger.debug("username:"+username);
			logger.debug("protocol:"+protocol);
		}
		
		try {
			
			String version = getD2DCSService().getESXServerVersion(host, username, password,protocol,port);
			
			logger.debug("result:"+ version);
			logger.debug("getESXServerVersion(String, String, String, String) - end"); //$NON-NLS-1$
			return version;
		} catch (WebServiceException e) {
			logger.error("getESXServerVersion(String, String, String, String)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		
		logger.debug("getESXServerVersion(String, String, String, String) - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public VMSnapshotsInfo[] getSnapshots(String vmInstanceUUID) throws BusinessLogicException,ServiceConnectException, ServiceInternalException {
		try {
			VMSnapshotsInfo[] snapShots = getD2DCSService().getSnapshotsForProductionServer(vmInstanceUUID);
			if(snapShots == null || snapShots.length==0)
				return null;
			
			logger.debug("getSnapshots() - end"); //$NON-NLS-1$
			return snapShots;
		} catch (WebServiceException e) {
			logger.error("getSnapshots()", e);
			proccessAxisFaultException(e);
		}
		
		return null;
	}

	@Override
	public String[] getESXNodeNetworkAdapterTypes(String host, String username, String password, String protocol, int port, BaseModel esxHost) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getESXNodeNetworkAdapterTypes(String, String, String, String, int, BaseModel) - start"); //$NON-NLS-1$
		if (logger.isDebugEnabled()){
			logger.debug("host:"+host);
			logger.debug("username:"+username);
			logger.debug("protocol:"+protocol);
			logger.debug("port:"+port);
		}
		
		try{
			VWWareESXNode[] nodes = covertFromESXNodeToBaseModel(new BaseModel[]{esxHost});
			String[] result = getD2DCSService().getAdapterTypes(host, username, password, protocol, true, port, nodes[0]);
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(result));

			logger.debug("getESXNodeNetworkAdapterTypes(String, String, String, String, int, BaseModel) - end"); //$NON-NLS-1$
			return result;
		}catch(WebServiceException e){
			logger.error("getESXNodeNetworkAdapterTypes(String, String, String, String, int, BaseModel)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		

		logger.debug("getESXNodeNetworkAdapterTypes(String, String, String, String, int, BaseModel) - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public String[] getESXNodeNetworkConnections(String host, String username, String password, String protocol, int port, BaseModel esxHost) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getESXNodeNetworkConnections(String, String, String, String, int, BaseModel) - start"); //$NON-NLS-1$
		if (logger.isDebugEnabled()){
			logger.debug("host:"+host);
			logger.debug("username:"+username);
			logger.debug("protocol:"+protocol);
			logger.debug("port:"+port);
		}
		
		try{
			VWWareESXNode[] nodes = covertFromESXNodeToBaseModel(new BaseModel[]{esxHost});
			String[] result = getD2DCSService().getVirtualNetworkList(host, username, password, protocol, true, port, nodes[0]);
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(result));

			logger.debug("getESXNodeNetworkConnections(String, String, String, String, int, BaseModel) - end"); //$NON-NLS-1$
			return result;
		}catch(WebServiceException e){
			logger.error("getESXNodeNetworkConnections(String, String, String, String, int, BaseModel)", e); //$NON-NLS-1$

			proccessAxisFaultException(e);
		}
		

		logger.debug("getESXNodeNetworkConnections(String, String, String, String, int, BaseModel) - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public ESXServerInfo getESXNodeSupportedInfo(String host, String username, String password, String protocol, int port, BaseModel esxHost)
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("getESXNodeSupportedInfo(String, String, String, String, int, BaseModel) - start"); //$NON-NLS-1$
		if (logger.isDebugEnabled()){
			logger.debug("host:"+host);
			logger.debug("username:"+username);
			logger.debug("protocol:"+protocol);
			logger.debug("port:"+port);
		}
		
		try{
			VWWareESXNode[] nodes = covertFromESXNodeToBaseModel(new BaseModel[]{esxHost});
			ESXServerInfo result = getD2DCSService().getESXServerInfo(host, username, password, protocol, true, port, nodes[0]);
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertObject2String(result));
			
			logger.debug("getESXNodeSupportedInfo(String, String, String, String, int, BaseModel) - end"); //$NON-NLS-1$
			return result;
		}catch(WebServiceException e){
			logger.error("getESXNodeSupportedInfo(String, String, String, String, int, BaseModel)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("getESXNodeSupportedInfo(String, String, String, String, int, BaseModel) - end"); //$NON-NLS-1$
		return null;
	}

	@Override
	public Integer[] getStates(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try{
			FailoverJobScript failoverJobScript = getFailoverJobScriptEx(afGuid);
			
			Integer result[] = null;
			if (failoverJobScript != null && failoverJobScript.getBackupToRPS()) {
				Integer resultHeartbeat[] = null;
				Integer resultReplication[] = null;
				resultReplication = getConverterService(failoverJobScript).getStatesThis(failoverJobScript.getAFGuid());
				resultHeartbeat = getD2DCSService().getStatesThis(afGuid);
				if (failoverJobScript.getState() == FailoverJobScript.REGISTERED){
					resultHeartbeat[0] |= HeartBeatJobScript.STATE_REGISTERED;
				}else if (failoverJobScript.getState() == FailoverJobScript.UNREGISTERED){
					resultHeartbeat[0] |= HeartBeatJobScript.STATE_UNREGISTERED;
				}

				result = new Integer[]{resultHeartbeat[0], resultReplication[1]};
			}
			else
				result = getD2DCSService().getStatesThis(afGuid);
			
			logger.debug("Heart Beat State:"+result);

			return result;
		}catch(WebServiceException e){
			logger.error("getStates()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		return new Integer[]{HeartBeatJobScript.STATE_NO_EXIST};
	}

	@Override
	protected void proccessAxisFaultException(WebServiceException arg_exception)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		
		//switching to a monitee and the monitee is down during accessing
		if (arg_exception.getCause()!=null && 
				arg_exception.getCause() instanceof ConnectException
				&& getMoniteeServiceClient() != null) {
			throw generateException(FlashServiceErrorCode.VCM_CONNECT_CLIENT_FAIL);
		}
			
		super.proccessAxisFaultException(arg_exception);
	}

	@Override
	public void pauseHeartBeat(String afGuid) throws BusinessLogicException,	ServiceConnectException, ServiceInternalException {
		logger.debug("pauseHeartBeat() - start"); //$NON-NLS-1$

		try{
			// Connect to D2D agent directly. "pauseHeartBeatThis(afGuid)" is used for converter.
			int result = getD2DCSService().pauseHeartBeatForD2D(afGuid);
			logger.debug("Result:"+result);
		}catch(WebServiceException e){
			logger.error("pauseHeartBeat()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("pauseHeartBeat() - end"); //$NON-NLS-1$
	}

	@Override
	public void stopHeartBeat(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("stopHeartBeat() - start"); //$NON-NLS-1$

		try{
			int result = getD2DCSService().stopHeartBeatThis(afGuid, null);
			logger.debug("Result:"+result);
		}catch(WebServiceException e){
			logger.error("stopHeartBeat()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("stopHeartBeat() - end"); //$NON-NLS-1$
	}

	@Override
	public void resumeHeartBeat(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try{
			// Connect to D2D agent directly. "resumeHeartBeatThis(afGuid)" is used for converter.
			int result = getD2DCSService().resumeHeartBeatForD2D(afGuid);
			logger.debug("Result:"+result);
		}catch(WebServiceException e){
			logger.error("resumeHeartBeat()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
	}

	@Override
	public void disableAutoOfflieCopy(String vmInstanceUUID) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try{
			FailoverJobScript failoverJobScript = getFailoverJobScriptEx(vmInstanceUUID);
			
			int result = 0;
			if (failoverJobScript != null && failoverJobScript.getBackupToRPS())
				result = getConverterService(failoverJobScript).enableAutoOfflieCopy(failoverJobScript.getAFGuid(),  false);
			else
				result = getD2DCSService().enableAutoOfflieCopy(vmInstanceUUID,false);
			logger.debug("Result:"+result);
		}catch(WebServiceException e){
			logger.error("disableAutoOfflieCopy()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		
	}

	@Override
	public String[] getHypervNetworkAdapterTypes()throws BusinessLogicException,
	ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = getMonitorClientProxy();
		try{
			if(client!=null) {
				String[] types = client.getServiceV2().getHypervNetworkAdapterTypes();
				logger.debug("Result:"+types);
				return types;
			}
			else {
				logger.error("the client is null");
			}
		
		}catch(WebServiceException e){
			logger.error("getHypervNetworkAdapterTypes()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		return null;
	}

	@Override
	public String[] getHypervNetworks(String host, String username, String password) throws BusinessLogicException,
	ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = getMonitorClientProxy();
		try{
			if(client!=null){
				String[] networks=client.getServiceV2().getHypervNetworksFromMonitor(host, username, password);
				
				//String[] networks = getD2DCSService().getHypervNetworks(host, username, password);
				logger.debug("Result:"+networks);
				return networks;
			}

		}catch(WebServiceException e){
			logger.error("getHypervNetworks()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		return null;
	}

	@Override
	public void enableAutoOfflieCopy(String vmInstanceUUID) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try{		
			FailoverJobScript failoverJobScript = getFailoverJobScriptEx(vmInstanceUUID);
			
			int result = 0;
			if (failoverJobScript != null && failoverJobScript.getBackupToRPS())
				result = getConverterService(failoverJobScript).enableAutoOfflieCopy(failoverJobScript.getAFGuid(),true);
			else
				result = getD2DCSService().enableAutoOfflieCopy(vmInstanceUUID,true);
			logger.debug("Result:"+result);
		}catch(WebServiceException e){
			logger.error("enableAutoOfflieCopy()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
	}
	
	protected IFlashServiceV2 getD2DCSService() throws BusinessLogicException{
		IFlashServiceV2 service = null;
		
		WebServiceClientProxy moniteeServiceClient = null;
		if(getCurrentMonitee() != null) {
			moniteeServiceClient = getMoniteeServiceClient();
			if(moniteeServiceClient == null) 
				throw generateException(FlashServiceErrorCode.VCM_CONNECT_CLIENT_FAIL);
		}
		else	
			moniteeServiceClient = getServiceClient();
//			moniteeServiceClient = getLocalWebServiceClient();
		
		service = moniteeServiceClient.getServiceV2();
		return service;
	}
	
	@Override
	public ESXServerInfo getHyperVSupportedInfo(String host, String username,String password) throws BusinessLogicException,ServiceConnectException, ServiceInternalException {
		logger.debug("getHyperVInfo(String, String, String, String) - start"); //$NON-NLS-1$
		if (logger.isDebugEnabled()){
			logger.debug("host:"+host);
			logger.debug("username:"+username);
		}
		
		try{
			WebServiceClientProxy client = getMonitorClientProxy();
			if(client!=null) {
				ESXServerInfo result = client.getServiceV2().getHypervInfo(host, username, password);
				if (logger.isDebugEnabled())
					logger.debug(StringUtil.convertObject2String(result));
				
				logger.debug("getHyperVInfo(String, String, String, String) - end"); //$NON-NLS-1$
				return result;
			}
			else {
				logger.error("getHyperVInfo(String, String, String, String) client is null");
				return null;
			}
			
		}catch(WebServiceException e){
			logger.error("getHyperVInfo(String, String, String, String)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("getHyperVInfo(String, String, String, String) - end"); //$NON-NLS-1$
		return null;
	}
	
	@Override
	public ARCFlashNodesSummary queryFlashNodesSummary() throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("queryHeartBeatModel() - start");

		try {
			 ARCFlashNodesSummary result = getD2DCSService().getARCFlashNodesSummary();
			logger.debug("queryHeartBeatModel() - end");
			return result;
		} catch (WebServiceException e) {
			if(e instanceof SOAPFaultException){
				SOAPFaultException se = (SOAPFaultException)e;
				if (se.getFault()!=null && MonitorWebServiceErrorCode.Common_NULL_HeartBeat.equals(se.getFault().getFaultCodeAsQName().getLocalPart()) )
					return null;
			}
			logger.error("queryFlashNodesSummary(String, int, String, String)", e);
			proccessAxisFaultException(e);
		} catch(Exception e){
			logger.error("queryFlashNodesSummary(String, int, String, String)", e);
			throw new BusinessLogicException();
		}
		
		return null;
	}

	@Override
	public void startFailover(String vmInstanceUUID, VMSnapshotsInfo vmSnapInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("startFailover(VMSnapshotsInfo) - start"); //$NON-NLS-1$

		try {
			
			getD2DCSService().startFailoverForProductionServer(vmInstanceUUID,vmSnapInfo);
			
			logger.debug("startFailover(VMSnapshotsInfo) - end"); 
		}catch (WebServiceException e) {
			logger.error("startFailover(VMSnapshotsInfo)", e); 
			proccessAxisFaultException(e);
		}
		logger.debug("startFailover(VMSnapshotsInfo) - end"); //$NON-NLS-1$
	}
	
	@Override
	public boolean isFailoverJobFinishOfProductServer(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException 
	{
		logger.debug("isFailoverJobFinishOfProductServer() - start"); 
		try {
			boolean isFinished = getD2DCSService().isFailoverJobFinishOfProductServer(vmInstanceUUID);
			logger.debug("isFailoverJobFinishOfProductServer() - end " + isFinished); 
			
			return isFinished;
		} catch(WebServiceException e) {
			logger.error("isFailoverJobFinishOfProductServer()", e); 
			proccessAxisFaultException(e);
			return false;
		}
	}
	
	@Override
	public void connectMoniteeServer(ARCFlashNode monitee)
	throws BusinessLogicException,ServiceConnectException,ServiceInternalException{
		boolean oldD2D = false;
		String hostname = null;
		String hostProtocol = null;
		String hostport = null;
		String uuid = null;
		
		FailoverJobScript jobScript = getFailoverJobScriptEx(monitee.getUuid());
		
		//if monitee.isMonitor() is true, it means user switches to the current monitor 
		//from formerly selected monitee.  
		if(monitee.isMonitor()) {
			setCurrentMonitee(null);
			setMoniteeServiceClient(null);
			return;
		}
		else if(monitee.isVSphereManagedVM()) {
			VSphereProxyServer proxy = monitee.getVSphereproxyServer();
			if(proxy != null) {
				hostname = proxy.getVSphereProxyName();
				hostProtocol = proxy.getVSphereProxyProtocol();
				hostport = proxy.getVSphereProxyPort();
				uuid = jobScript.getVSphereproxyServer().getVSphereUUID();
			}
		}
		else if (jobScript.getBackupToRPS() && monitee.isRemoteNode()) {
			hostname = jobScript.getConverterHostname();
			hostProtocol = jobScript.getConverterProtocol();
			hostport = jobScript.getConverterPort()+"";
			uuid = jobScript.getConverterUUID();
		}
		else if((monitee.getState() & 0x00008000) > 0){ //this should used HeartBeatJobScript.STATE_DOWN after Eric releases that file.
			setMoniteeServiceClient(null);
			setCurrentMonitee(monitee);
			logger.info("monitee.getState():" + (monitee.getState() & 0x00008000));
			throw generateException(FlashServiceErrorCode.VCM_CONNECT_CLIENT_FAIL);
		}
		else {
			hostname = monitee.getHostname();
			hostProtocol = monitee.getHostProtocol();
			hostport = monitee.getHostport();
			uuid = jobScript.getAgentUUID();
		}
		
		if(StringUtil.isEmptyOrNull(hostname) || StringUtil.isEmptyOrNull(hostProtocol) 
				|| StringUtil.isEmptyOrNull(hostport) || StringUtil.isEmptyOrNull(uuid)) {
			logger.error("Fail to connect to monitee: monitee: " + monitee);
			setMoniteeServiceClient(null);
			setCurrentMonitee(monitee);
			
			throw generateException(FlashServiceErrorCode.VCM_CONNECT_CLIENT_FAIL);
		}
		
		WebServiceClientProxy client = null;
		try {
			String serviceID = ServiceInfoConstants.SERVICE_ID_D2D_PROPER;
			ServiceInfoList serviceInfoList = null;
			
			try{
				serviceInfoList = WebServiceFactory.getServiceInfoList(hostProtocol,hostname,
					Integer.parseInt(hostport));
			
			}catch(WebServiceException e1){
				//Refresh the DNS of localhost and try again
				logger.info("Fails to connect to monitee " + monitee + ". Refresh the DNS and try again.");
				getServiceClient().getServiceV2().getIpAddressFromDns(hostname);
				try {
					serviceInfoList = WebServiceFactory.getServiceInfoList(hostProtocol,hostname,
							Integer.parseInt(hostport));
				}
				catch(WebServiceException e){
					if(e.getMessage().equals(FlashServiceErrorCode.Common_Service_FAIL_TO_GETLIST)){
						//we think it is old D2D
						client = WebServiceFactory.getFlassService(hostProtocol,hostname,
								Integer.parseInt(hostport),serviceID);
						oldD2D = true;
					}else{
						throw e;
					}
				}
			}
			
			if(!oldD2D){
				serviceID = ServiceInfoConstants.SERVICE_ID_D2D_V2;
				ServiceInfo featureServiceInfo = WebServiceFactory.getFeatureServiceInfo(serviceID, serviceInfoList);
				client = WebServiceFactory.getFlassService(hostProtocol,hostname,
						Integer.parseInt(hostport),serviceID,featureServiceInfo);
			}
			
			client.getService().validateUserByUUID(uuid);
			
		} catch (WebServiceException ex) {
			logger.info("Fails to connect to monitee " + monitee);
			if (ex.getCause() instanceof ProtocolException
					|| ex.getCause() instanceof ConnectException
					|| ex.getCause() instanceof SocketException
					|| ex.getCause() instanceof SSLException // add for that it cannot connect server when we change protocol, for issue 20015152
					|| ex.getCause() instanceof UnknownHostException) {
				logger.debug("Exception:" + ex.getCause());
				throw generateException(FlashServiceErrorCode.Common_CantConnectRemoteServer);
			}else{
				proccessAxisFaultException(ex);
			}
		} 
		finally {
			this.setMoniteeServiceClient(client);
			setCurrentMonitee(monitee);
		}

	}

	@Override
	public SummaryModel getProductionServerSummaryModel(String vmInstanceUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		// TODO modify it into debug
		logger.info("getProductionServerSummaryModel() - start"); 
		try {
			SummaryModel model = getD2DCSService().getProductionServerSummaryModel(vmInstanceUUID);
			
			if(logger.isDebugEnabled()) {
				logger.debug("model:" + StringUtil.convertObject2String(model));
				logger.debug("getProductionServerSummaryModel() - end"); //$NON-NLS-1$
			}
			// TODO modify it into debug
			if(logger.isInfoEnabled()) {
				StringBuilder msg = new StringBuilder();
				msg.append("SummaryModel ");
				if(model == null){
					msg.append("is null.");
				}
				else {
					msg.append("is not null.")
					.append("; snapshot:").append(model.getSnapshots() == null ? 0 : model.getSnapshots().size())
					.append("; storage:").append(model.getStorages() == null ? 0 : model.getStorages().size())
					.append("; lic:").append(model.getLicenseInfo() == null ? 0 : 
						(model.getLicenseInfo().getVSphereVMLicense() + " " + model.getLicenseInfo().getPhysicalMachineLicense()));
				}
				
				logger.info(msg.toString());
				
				logger.debug("getProductionServerSummaryModel() - end"); 
			}
			
			// TODO modify it into debug
			return model;
		} catch (Exception e) {
			logger.error("getSummaryModel(String, String, String)", e);
			throw new BusinessLogicException();
		}
	
	}

	@Override
	public boolean isHostAMD64Platform()throws BusinessLogicException,ServiceConnectException,ServiceInternalException{
		boolean isAMD64=false;
		logger.debug("isHostAMD64Platform() - start"); //$NON-NLS-1$

		try {
			
			short cpuArch=getD2DCSService().GetHostProcessorArchitectural();
			logger.debug("isHostAMD64Platform() - end"); //$NON-NLS-1$
			if(cpuArch==9){
				isAMD64=true;
			}
		} catch (Exception e) {
			logger.error("isHostAMD64Platform()", e);
			throw new BusinessLogicException();
		}

		logger.debug("isHostAMD64Platform() - end"); //$NON-NLS-1$
		return isAMD64;
	}
	
	@Override
	public BackupSettingsModel getBackupConfiguration() {
		logger.debug("getBackupConfiguration() - start");
		try
		{
			BackupConfiguration bc = getD2DCSService().getBackupConfiguration();
			
			if (bc != null)
			{
				BackupSettingsModel model = ConvertDataToModel.ConvertBackupConfigToModel(bc);
				return model;
			}
			return null;
			
		}
		catch (Exception e)
		{
			logger.debug(e.toString());
		}
		logger.debug("getBackupConfiguration() - end");
		return null;
	}
	
	
	//get the volume detail
	@Override
	public List<FileModel> getVolumesWithDetails(String backupDest, String usr, String pwd) {
		logger.debug("getVolumesWithDetails() enter");
		try
		{
			Volume[] volumes = getD2DCSService().getVolumesWithDetails(backupDest, usr, pwd);
			List<FileModel> modelList = new ArrayList<FileModel>();
			for (int i = 0; i < volumes.length; i++)
			{
				modelList.add(ConvertDataToModel.ConvertToVolumeModel(volumes[i]));
			}
			logger.debug("volumes:" + StringUtil.convertList2String(modelList));
			logger.debug("getVolumesWithDetails() end");
			return modelList;				
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		return null;
	}

	@Override
	public String getRunningSnapShotGuidForProduction(String vmInstanceUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {

		logger.debug("getRunningSnapShotGuidForProduction() - start"); 
		try {
			String snapshotUid = getD2DCSService().getRunningSnapShotGuidForProduction(vmInstanceUUID);
			
			if(logger.isDebugEnabled()) {
				logger.debug("snapshotUid:" + snapshotUid);
				logger.debug("getRunningSnapShotGuidForProduction() - end"); //$NON-NLS-1$
			}
			return snapshotUid;
		} catch (Exception e) {
			logger.error("getRunningSnapShotGuidForProduction(VMSnapshotsInfo)", e);
			throw new BusinessLogicException();
		}
	}

	@Override
	public int shutDownVM(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("shutDownVM() - start"); 
		try {
			int ret = getD2DCSService().shutdownVMForProductServer(vmInstanceUUID);
			
			if(logger.isDebugEnabled()) {
				logger.debug("return:" + ret);
			}
			else if(ret > 0)
				logger.error("return:" + ret);
			
			return ret;
		} catch (Exception e) {
			logger.error("shutDownVM()", e);
			throw new BusinessLogicException();
		}
	}

	@Override
	public boolean isHyperVRoleInstalled(String serverName,String protocol,Integer port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("isHyperVRoleInstalled() - start"); 
		
		boolean bResult = false;
		WebServiceClientProxy client = getMonitorClientProxy();
		try{
			if(client!=null) {
				bResult = client.getServiceV2().isHyperVRoleInstalled();
				return bResult;
			}
			else {
				logger.error("the client is null");
			}
		
		}catch(WebServiceException e){
			logger.error("getHypervNetworkAdapterTypes()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		
		if(!bResult) {
			setMonitorClientForEdge(null);
		}
		return bResult;
	}

	@Override
	public void deleteActivityLog(Date date) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			getD2DCSService().deleteActivityLogs(date);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
	}

	@Override
	public PagingLoadResult<LogEntry> getActivityLogs(PagingLoadConfig config)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {

		int start = config.getOffset();
		int count = config.getLimit();

		try {
			ActivityLogResult activityLogResult = getD2DCSService().getActivityLogs(start, count);
			int total = (int) activityLogResult.getTotalCount();

			List<LogEntry> resultList = new ArrayList<LogEntry>();
			addActivityLogResult(activityLogResult, true, resultList);
			
			String instUUID = null;
			if (getCurrentMonitee() != null){
				instUUID = getCurrentMonitee().getUuid();
			}
			if (instUUID != null){
				FailoverJobScript failoverJobScript = getFailoverJobScriptEx(instUUID);
				if (failoverJobScript != null && failoverJobScript.getBackupToRPS()){
					VirtualMachine vm = new VirtualMachine();
					vm.setVmInstanceUUID(instUUID);
					ActivityLogResult activityLogResult2 =  getConverterService(failoverJobScript).getVMActivityLogs(start, count, vm);
					
					total += (int) activityLogResult2.getTotalCount();
					addActivityLogResult(activityLogResult2, true, resultList);
				}
			}
			return new BasePagingLoadResult<LogEntry>(resultList, start, total);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		return null;
	}
	private void addActivityLogResult(ActivityLogResult activityLogResult, boolean getD2dName, List<LogEntry> resultList){
		if (activityLogResult.getLogs() != null) {
			for (ActivityLog log : activityLogResult.getLogs()) {
				int type = LogEntryType.Information;
				if (log.getType() == ActivityLogType.Information)
					type = LogEntryType.Information;
				else if (log.getType() == ActivityLogType.Warning)
					type = LogEntryType.Warning;
				else
					type = LogEntryType.Error;

				LogEntry entry = new LogEntry(type, log.getTime(),getD2dName ? log.getD2dName() : "", 
						log.getMessage(), log.getJobID());
				entry.setTimeZoneOffset(log.getTimeZoneOffset());
				resultList.add(entry);
			}
		}
	}
	@Override
	public PagingLoadResult<LogEntry> getVMActivityLogs(
			PagingLoadConfig config, BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		int start = config.getOffset();
		int count = config.getLimit();

		try {
			ActivityLogResult activityLogResult =  getD2DCSService().getVMActivityLogs(start, 
										count, CommonServiceImpl.ConvertToVirtualMachine(vmModel));
			int total = (int) activityLogResult.getTotalCount();
			List<LogEntry> resultList = new ArrayList<LogEntry>();

			if (activityLogResult.getLogs() != null) {
				for (ActivityLog log : activityLogResult.getLogs()) {
					int type = LogEntryType.Information;
					if (log.getType() == ActivityLogType.Information)
						type = LogEntryType.Information;
					else if (log.getType() == ActivityLogType.Warning)
						type = LogEntryType.Warning;
					else
						type = LogEntryType.Error;

					LogEntry entry = new LogEntry(type, log.getTime(),"", 
							log.getMessage(), log.getJobID());
					entry.setTimeZoneOffset(log.getTimeZoneOffset());
					resultList.add(entry);
				}
			}
			return new BasePagingLoadResult<LogEntry>(resultList, start, total);
		}catch(BusinessLogicException e) {
			logger.warn("Fails to get the correct webservice client for the Monitee [" + getCurrentMonitee() +
					"] selected from Monitor, error:" + e.getMessage());
			throw e;
		}
		catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}

		return null;
	}
	
	@Override
	public void deleteVMActivityLog(Date date, BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			 getD2DCSService().deleteVMActivityLogs(date, CommonServiceImpl.ConvertToVirtualMachine(vmModel));
		} catch(BusinessLogicException e) {
			logger.warn("Fails to get the correct webservice client for the Monitee [" + getCurrentMonitee() +
					"] selected from Monitor, error:" + e.getMessage());
			throw e;
		}catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
		
	}
	
	@Override
	public boolean isVMWareVMNameExist(String host, String username, String password,
			 String protocol, boolean ignoreCertAuthentidation, long viPort,String esxName,
			 String dcName, String vmName) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		
		boolean isFound=false;
		logger.debug("isVMWareVMNameExist() - start"); //$NON-NLS-1$

		try {
			VWWareESXNode esxNode=new VWWareESXNode(esxName,dcName);
			
			isFound = getD2DCSService().isVMWareVMNameExist(host, username, password, protocol, 
					ignoreCertAuthentidation, viPort, esxNode, vmName);
			logger.debug("isVMWareVMNameExist() - end"); //$NON-NLS-1$
			return isFound;
		} catch (Exception e) {
			logger.error("isVMWareVMNameExist():"+e.getMessage());
			throw new BusinessLogicException();
		}
		
	}

	public boolean isHyperVVMNameExist(String vmName) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		
		boolean isFound=false;
		logger.debug("isHyperVVMNameExist() - start"); //$NON-NLS-1$

		try {
			WebServiceClientProxy clientProxy = getMonitorClientProxy();
			if(clientProxy == null){
				logger.error("The client is null");
				return isFound;
			}
			
			String vmGUID = clientProxy.getServiceV2().isHyperVVMNameExist(vmName);
			isFound = !StringUtil.isEmptyOrNull(vmGUID);
			logger.debug("isHyperVVMNameExist() - end"); //$NON-NLS-1$
			return isFound;
		} catch (Exception e) {
			logger.error("isHyperVVMNameExist():"+e.getMessage());
			throw new BusinessLogicException();
		}
	}

	@Override
	public int getReplicationQueueSize(String afGuid) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		int count = 0;
		FailoverJobScript failoverJobScript = getFailoverJobScriptEx(afGuid);
		try{
			if (failoverJobScript.getBackupToRPS())
				count = getConverterService(failoverJobScript).getReplicationQueueSize(failoverJobScript.getAFGuid());
			else
				count = getD2DCSService().getReplicationQueueSize(afGuid);
		}catch (WebServiceException exception) {
			logger.error("getReplicationQueueSize", exception);
			proccessAxisFaultException(exception);
		}catch (Exception e) {
			logger.error("unknown error in getReplicationQueueSize: " + e.getMessage());
		}
		return count;
	}

	@Override
	public void forceNextReplicationMerge(String afGuid,Boolean force)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		FailoverJobScript failoverJobScript = getFailoverJobScriptEx(afGuid);
		try{
			if (failoverJobScript.getBackupToRPS())
				getConverterService(failoverJobScript).forceNextReplicationMerge(failoverJobScript.getAFGuid(),force);
			else
				getD2DCSService().forceNextReplicationMerge(afGuid, force);
		}catch (WebServiceException e) {
			logger.error("forceNextReplicationMerge", e);
		}catch (Exception e) {
			logger.error("unknown error in forceNextReplicationMerge: " + e.getMessage());
		}
		
	}

	@Override
	public VCMConfigStatus getVCMConfigStatus(String vmInstanceUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {

		logger.debug("getVCMConfigStatus() - start"); 
		try {
			VCMConfigStatus ret = getD2DCSService().getVCMConfigStatus(vmInstanceUUID);
			
			if(logger.isDebugEnabled()) {
				logger.debug("return:" + ret);
			}
			
			return ret;
		} 
		catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
		catch (Exception e) {
			logger.error("getVCMConfigStatus() ends with error", e);
			throw new BusinessLogicException();
		}
	
		return null;
	}
	
	@Override
	public boolean isHostOSGreaterEqualW2K8SP2(String serverName,String protocol,Integer port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("isHostOSGreaterEqualW2K8SP2() - start"); 
		
		boolean bResult = false;
		WebServiceClientProxy client = getMonitorClientProxy();
		try{
			if(client!=null) {
				bResult = client.getServiceV2().isHostOSGreaterEqualW2K8SP2();
				return bResult;
			}
			else {
				logger.error("the client is null");
			}
		
		}catch(WebServiceException e){
			logger.error("getHypervNetworkAdapterTypes()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		if(!bResult) {
			setMonitorClientForEdge(null);
		}
		return bResult;
	}

	@Override
	public int[] checkPathIsSupportHyperVVM(List<String> paths) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("checkPathIsSupportHyperVVM() - start"); 
		
		int[] result = null;
		WebServiceClientProxy client = getMonitorClientProxy();
		try{
			if(client!=null) {
				result = client.getServiceV2().checkPathIsSupportHyperVVM(paths.toArray(new String[0]));
				return result;
			}
			else {
				logger.error("the client is null");
			}
		
		}catch(WebServiceException e){
			logger.error("checkPathIsSupportHyperVVM()", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}
		return result;
	}


	private String getNomalizedStr(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}
	@Override
	public long vcmValidateSource(String path, String domain, String user, String pwd, boolean isNeedCreateFolder)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		long ret = 0;
		try {
			path = getNomalizedStr(path);
			domain = getNomalizedStr(domain);
			user = getNomalizedStr(user);
			pwd = getNomalizedStr(pwd);

			if (domain.trim().length() == 0) {
				int indx = user.indexOf('\\');
				if (indx > 0) {
					domain = user.substring(0, indx);
					user = user.substring(indx + 1);
				}
			}
			
			WebServiceClientProxy client = getMonitorClientProxy();
			if(client!=null){
				ret = client.getServiceV2().validateSourceGenFolder(path, domain, user, pwd, isNeedCreateFolder);
			}
			else{
				logger.error("the client is null");
			}
			
		} catch (WebServiceException exception) {
			/*if (exception instanceof SOAPFaultException) {
				SOAPFaultException e = (SOAPFaultException) exception;
				if (e.getFault() != null
						&& FlashServiceErrorCode.RestoreJob_SourceInvalid
								.equals(e.getFault().getFaultCodeAsQName()
										.getLocalPart())) {
					BusinessLogicException ex = this
							.generateException(FlashServiceErrorCode.RestoreJob_SourceInvalid);
					String errMsg = MessageFormatEx.format(
							ex.getDisplayMessage(), exception.getMessage());
					ex.setDisplayMessage(errMsg);
					throw ex;
				}
			}*/
			{
				proccessAxisFaultException(exception);
			}
		}
		return ret;
	}
	
	@Override
	public boolean checkResourcePoolExist(String esxServer, String username,
			String passwod, String protocol,int port, String esxName,String dcName, String resPoolRef)
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("checkResourcePoolExist(String, String, String, String, int, VWWareESXNode) - start"); //$NON-NLS-1$
		
		try {
			VWWareESXNode esxNode = new VWWareESXNode(esxName, dcName);
			
			return  getD2DCSService().checkResourcePoolExist(esxServer, username, passwod, protocol, port, esxNode,resPoolRef);

		} catch (WebServiceException e) {
			logger.error("checkResourcePoolExist(String, String, String, String, int, VWWareESXNode)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		}

		logger.debug("checkResourcePoolExist(String, String, String, String, int, VWWareESXNode) - end"); //$NON-NLS-1$
		return true;
	}

	@Override
	public EdgeLicenseInfo  getConversionLicense(String aFGuid) 
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException{

		logger.debug("getConversionLicense() - start"); 
		try {
			EdgeLicenseInfo ret = null;
			
			FailoverJobScript failoverJobScript = getFailoverJobScriptEx(aFGuid);

			if (failoverJobScript != null && failoverJobScript.getBackupToRPS())
				ret = getConverterService(failoverJobScript).getConversionLicense(failoverJobScript.getAFGuid());
			else
				ret = getD2DCSService().getConversionLicense(aFGuid);
			
			if(logger.isDebugEnabled()) {
				logger.debug("return:" + ret);
			}
			
			return ret;
		} 
		catch (WebServiceException exception) {
			logger.error("getConversionLicense() ends with WebServiceException ", exception);
			proccessAxisFaultException(exception);
		}
		catch (BusinessLogicException e) {
			throw e;
		}
		catch (Exception e) {
			logger.error("getConversionLicense() ends with error", e);
		}
	
		return null;
	}

	@Override
	public EsxServerInformation getEsxServerInformation(String esxServer, String username, String passwod,
			String protocol, int port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		try {
			EsxServerInformation esxServerInfo = getD2DCSService().getEsxServerInformation(esxServer, username,
					passwod, protocol, port);
			return esxServerInfo;
		} catch (WebServiceException e) {
			logger.error("Failed to get the esx server information", e);
			proccessAxisFaultException(e);
		}
		return null;
	}

	@Override
	public EsxHostInformation getEsxHostInformation(String host, String username, String password, String protocol,
			int port, BaseModel esxHost) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			VWWareESXNode node = new VWWareESXNode();
			node.setDataCenter((String) esxHost.get("dataCenter"));
			node.setEsxName((String) esxHost.get("esxNode"));
			return getD2DCSService().getEsxHostInformation(host, username, password, protocol, port, node);
		} catch (WebServiceException e) {
			logger.error("Failed to get the esx host information", e);
			proccessAxisFaultException(e);
		}
		return null;
	}
}
