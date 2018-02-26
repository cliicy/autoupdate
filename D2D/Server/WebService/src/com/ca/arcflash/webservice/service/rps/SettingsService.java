package com.ca.arcflash.webservice.service.rps;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.RPSVersionInfo;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreCommonInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.data.restore.RpsPolicy4D2DRestore;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupSummaryConverter;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class SettingsService {
	private static final Logger logger = Logger.getLogger(SettingsService.class);
	private BackupSummaryConverter backupSummaryConverter = new BackupSummaryConverter();
	private RpsPolicy4D2D tempPolicy;
	
	private static class ServiceInstance {
		public static SettingsService INSTANCE = new SettingsService();
	}
	
	private SettingsService() {}
	
	public static SettingsService instance() {
		return ServiceInstance.INSTANCE;
	}
	
	public RpsPolicy4D2D[] getRPSPolicyList(String hostName,
			String userName, String password, int port, String protocol, String rpsLoginUUID) throws ServiceException {
		if(logger.isDebugEnabled()){
			logger.debug("Get rps policy from: " + hostName);
			logger.debug("userName: " + userName);
			logger.debug("port: " + port);
			logger.debug("protocol: " + protocol);
		}
		
		try {
			BackupService.getInstance().validateRPSHost(hostName, userName, password, port);
			
			IRPSService4D2D client = RPSServiceProxyManager
					.getRPSServiceClient(hostName, userName, password, port,
							protocol, rpsLoginUUID);
			if(client !=  null){
				RpsPolicy4D2D[] policies = client.getRPSPolicyList();
				List<RpsPolicy4D2D> rpsPolicies = new ArrayList<RpsPolicy4D2D>();
				
				for(RpsPolicy4D2D policy : policies){
//					RpsPolicy4D2D newPolicy = newPolicy(policy);
					String encPwd = policy.getEncryptionPassword();
					if(!StringUtil.isEmptyOrNull(encPwd)){
						encPwd = WSJNI.AFDecryptStringEx(encPwd);
					}
					policy.setEncryptionPassword(encPwd);
					String storePwd = policy.getStorePassword();
					if(!StringUtil.isEmptyOrNull(storePwd)){
						storePwd = WSJNI.AFDecryptStringEx(storePwd);
					}
					policy.setStorePassword(storePwd);
					rpsPolicies.add(policy);
					if(logger.isDebugEnabled())
						logger.debug(StringUtil.convertObject2String(policy));
				}
				return rpsPolicies.toArray(new RpsPolicy4D2D[0]);
			}
		} catch(WebServiceException we) {			
			logger.error("Failed to get rps policy", we);
			ServiceUtils.processWebServiceException(we, hostName);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetPolicy", hostName),
					FlashServiceErrorCode.Common_General_Message);
		} catch(ServiceException se) {
			throw se;
		} catch(Exception e){
			logger.error("Failed to get rps policy", e);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetPolicy", hostName),
					FlashServiceErrorCode.Common_General_Message);
		}
		return null;
	}
	
	public RpsPolicy4D2DRestore[] getRPSPolicyList4Restore(String hostName,
			String userName, String password, int port, String protocol) throws ServiceException {
		if(logger.isDebugEnabled()){
			logger.debug("Get rps policy from: " + hostName);
			logger.debug("userName: " + userName);
			logger.debug("port: " + port);
			logger.debug("protocol: " + protocol);
		}
		
		try {
			BackupService.getInstance().validateRPSHost(hostName, userName, password, port);
			IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(hostName, userName, password, port, protocol);
			if(client !=  null){
				List<RpsPolicy4D2DRestore> rpsPolicies = new ArrayList<RpsPolicy4D2DRestore>();
				RpsPolicy4D2DRestore[] policies = client.getRPSPolicyList4Restore();
				if(policies == null)
					return null;
				
				for(RpsPolicy4D2DRestore policy : policies){
					RpsPolicy4D2DRestore setting = policy;
					List<BackupD2D> d2d4Policy = new ArrayList<BackupD2D>();					
					for(BackupD2D d2d : policy.getD2dList()) {
						if(StringUtil.isEmptyOrNull(d2d.getDesUsername())){
							if(!userName.contains("\\"))
								d2d.setDesUsername(hostName + "\\" + userName);
							else
								d2d.setDesUsername(userName);
							d2d.setDesPassword(password);
						}else {
							d2d.setDesPassword(WSJNI.AFDecryptStringEx(d2d.getDesPassword()));
						}
						d2d4Policy.add(d2d);
					}
					if(!d2d4Policy.isEmpty()){
						setting.setD2dList(d2d4Policy);
						rpsPolicies.add(setting);
					}
				}
				return rpsPolicies.toArray(new RpsPolicy4D2DRestore[0]);
			}
		} catch(WebServiceException we){
			logger.error("failed to connect rps server.", we);
			throw new ServiceException(WebServiceMessages.getResource("cannotConnectService", new Object[]{hostName}),
						FlashServiceErrorCode.Common_CannotConnectRPSService);
		} catch(ServiceException se) {
			throw se;
		} catch(Exception e){
			logger.error("Failed to get rps policy", e);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetPolicy", hostName),
					FlashServiceErrorCode.Common_General_Message);
		}
		return null;
	}
	
	public RpsPolicy4D2D getRPSDataStore(String hostName,
			String userName, String password, int port, String protocol, 
			String dataStoreUUID, String rpsLoginUUID) throws ServiceException{
		if(logger.isDebugEnabled()){
			logger.debug("Get rps data store from: " + hostName);
			logger.debug("userName: " + userName);
			logger.debug("port: " + port);
			logger.debug("protocol: " + protocol);
			logger.debug("policyUUID: " + dataStoreUUID);
		}
		try {
			IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(hostName, userName, 
					password, port, protocol, rpsLoginUUID);
			if(client !=  null){
				DataStoreStatusListElem[] dataStores = client.getDataStoreStatus(dataStoreUUID);
				if(dataStores == null || dataStores.length == 0)
					return null;
				else {
					DataStoreStatusListElem dsStatus = dataStores[0];					
					RpsPolicy4D2D policy = new RpsPolicy4D2D();
					policy.setDataStoreName(dataStoreUUID);
					policy.setDataStoreOverallStatus(dsStatus.getDataStoreStatus().getOverallStatus());
					if(dsStatus.getDataStoreStatus().getOverallStatus() 
							!= DataStoreRunningState.ABNORMAL_BLOCK_ALL.getValue())
						this.setDataStore4Policy(policy, dsStatus.getDataStoreSetting());
					return policy;
				}	
			}
		} catch(WebServiceException we) {			
			logger.error("Failed to get rps data store", we);
			ServiceUtils.processWebServiceException(we, hostName);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetDataStore", hostName), 
					FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);
		} catch(ServiceException se) {
			throw se;
		} catch(Exception e){
			logger.error("Failed to get data store", e);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetDataStore", hostName),
					FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);
		}
		
		return null;
	}
	
	public RpsPolicy4D2D getRPSPolicy(String hostName,
			String userName, String password, int port, String protocol, 
			String policyUUID, String rpsLoginUUID, boolean withDataStore) throws ServiceException {
		if(logger.isDebugEnabled()){
			logger.debug("Get rps policy from: " + hostName);
			logger.debug("userName: " + userName);
			logger.debug("port: " + port);
			logger.debug("protocol: " + protocol);
			logger.debug("policyUUID: " + policyUUID);
		}
		try {
			IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(hostName, userName, 
					password, port, protocol, rpsLoginUUID);
			if(client !=  null){
				RpsPolicy4D2D policy = client.getRPSPolicy4D2D(policyUUID, withDataStore);
				if(policy == null)
					return null;
				else if(withDataStore) {
					/*if (policy.getDataStoreOverallStatus() != DataStoreRunningState.ABNORMAL_BLOCK_ALL.getValue()
							&& StringUtil.isEmptyOrNull(policy.getStorePath())){
						//data store not exist.
						String message = WebServiceMessages.getResource("rpsDatastoreNotExist", 
								policy.getDataStoreName(), hostName);
						throw new ServiceException(message, FlashServiceErrorCode.Backup_RPS_Datastore_NotExist);
					}
*/
					String encPwd = policy.getEncryptionPassword();
					if(!StringUtil.isEmptyOrNull(encPwd)){
						encPwd = WSJNI.AFDecryptStringEx(encPwd);
					}
					policy.setEncryptionPassword(encPwd);
					String storePwd = policy.getStorePassword();
					if(!StringUtil.isEmptyOrNull(storePwd)){
						storePwd = WSJNI.AFDecryptStringEx(storePwd);
					}
					policy.setStorePassword(storePwd);
				}		
				return policy;
			}
		} catch(WebServiceException we) {			
			logger.error("Failed to get rps policy", we);
			ServiceUtils.processWebServiceException(we, hostName);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetPolicy", hostName), 
					FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);
		} catch(ServiceException se) {
			throw se;
		} catch(Exception e){
			logger.error("Failed to get rps policy", e);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetPolicy", hostName),
					FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);
		}
		
		return null;
	}
	
	public String getRpsServerUUID(String host, String userName, String password, 
			String protocol, int port){
		try {
			IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(host, userName, 
					password, port, protocol, "");
			if(client != null){
				return CommonService.getInstance().getNativeFacade()
						.decrypt(client.establishTrust(userName, password, ""));
				// return client.validateUser(userName, password, "");
			}
		}catch(Exception e){
			logger.error("Failed to get rps server uuid");
		}
		return null;
	}
	
	public RPSDataStoreInfo getRpsDataStoreInfo(RpsHost host, String dataStoreUUID)
	{
		if(host == null)
			return new RPSDataStoreInfo();
		try {
			
			RPSDataStoreInfo rpsDataStoreInfo = null;

			IRPSService4D2D client = RPSServiceProxyManager.getServiceByHost(host);
			if(client != null)
			{					
				if(!StringUtil.isEmptyOrNull(dataStoreUUID))
				{
					rpsDataStoreInfo = backupSummaryConverter.convert(client.getDataStoreStatus(dataStoreUUID));
				}
				
				if(rpsDataStoreInfo == null)
					rpsDataStoreInfo = new RPSDataStoreInfo();
				
				String rpsServerId = client.getRPSServerID();
				rpsDataStoreInfo.setRpsServerId(rpsServerId);
				
				return rpsDataStoreInfo;
			}
		}catch(Exception e){
			logger.error("Failed to get rps data store info");
		}
		return new RPSDataStoreInfo();
	}
	
	
	/**
	 * Get rps server unique id
	 * @param host
	 * @return
	 */
	public String getRpsServerID(RpsHost host){
		if(host == null)
			return null;
		try {

			IRPSService4D2D client = RPSServiceProxyManager.getServiceByHost(host);
			if(client != null){
				return client.getRPSServerID();
			}
		}catch(Exception e){
			logger.error("Failed to get rps server uuid");
		}
		return null;
	}
	//there are several cases backup job cannot run if backup to RPS
	//1. Cannot connect to RPS webservice.
	//2. RPS policy not exist.
	//3. RPS datastore service stopped.
	//4. RPS datastore not running.
	//5. RPS version is older than Agent /proxy
	public RpsPolicy4D2D checkRPS4Backup(BackupRPSDestSetting setting) throws ServiceException {
		logger.debug("Check RPS server and datastore status for backup job");
		
		if(setting == null)
			return null;
		
		RpsHost host = setting.getRpsHost();
		String protocol = host.isHttpProtocol() ? "http:" : "https:";
		RpsPolicy4D2D policy = SettingsService.instance().getRPSPolicy(host.getRhostname(), host.getUsername(), 
				host.getPassword(), host.getPort(), protocol, setting.getRPSPolicyUUID(), host.getUuid(), true);
		if(policy == null){
			//policy not exist
			String message = WebServiceMessages.getResource("rpsPolicyNotExist", setting.getRPSPolicy(), 
					setting.getRpsHost().getRhostname());
			throw new ServiceException(message, FlashServiceErrorCode.BackupConfig_ERR_Policy_Not_Exist);
		}
		
		this.tempPolicy = policy;
		
		if(policy.getDataStoreOverallStatus() != DataStoreRunningState.RUNNING.getValue()){
			//datastore not running.
			String message = WebServiceMessages.getResource("rpsDatastoreNotRunning", 
					setting.getRPSDataStoreDisplayName(), setting.getRpsHost().getRhostname());
			throw new ServiceException(message, FlashServiceErrorCode.Backup_RPS_Datastore_stopped);
		}
		
		// check if the RPS version is older than agent /proxy
		String oldRpsMsg = checkOldRps(host);
		if (oldRpsMsg != null && !oldRpsMsg.isEmpty())
		{
			throw new ServiceException(oldRpsMsg, FlashServiceErrorCode.Backup_RPSIsOlderThanAgent);
		}
		
		logger.debug("Check RPS server and datastore status for backup job end");
		return policy;
	}
	
//	public RpsPolicy4D2D checkPolcy4Job(RpsHost host, String rpsPolicyUUID, long jobType) throws ServiceException {
//		BackupRPSDestSetting setting = new BackupRPSDestSetting();
//		setting.setRpsHost(host);
//		setting.setRPSPolicyUUID(rpsPolicyUUID);
//		return checkPolicy4Job(setting, jobType);
//	}
	
	public RpsPolicy4D2D checkDataStore4Job(RpsHost host, String rpsDataStoreUUID, long jobType) throws ServiceException {
		if(logger.isDebugEnabled())
			logger.debug("Check data store for job " + jobType
					+ "RPS data store is " + rpsDataStoreUUID);
		
		if(host == null || StringUtil.isEmptyOrNull(rpsDataStoreUUID))
			return null;
		
		String protocol = host.isHttpProtocol() ? "http:" : "https:";
		RpsPolicy4D2D policy = this.getRPSDataStore(host.getRhostname(), host.getUsername(), 
				host.getPassword(), host.getPort(), protocol, rpsDataStoreUUID, host.getUuid());
		
		if(policy == null){
			//policy not exist
			String message = WebServiceMessages.getResource("rpsDataStoreNotExist",  
					rpsDataStoreUUID, host.getRhostname());
			throw new ServiceException(message, FlashServiceErrorCode.BackupConfig_ERR_Policy_Not_Exist);
		}
		
		if(policy.getDataStoreOverallStatus() != DataStoreRunningState.RUNNING.getValue()){
			if(readOnlyJob(jobType)
					&& policy.getDataStoreOverallStatus() == DataStoreRunningState.ABNORMAL_RESTORE_ONLY.getValue()){
				return policy;
			}else {
				//datastore not running.
				String sJobType = ServiceUtils.jobType2String(jobType);
				String message = WebServiceMessages.getResource("rpsJobNotRunningDataStoreStopped", 
						policy.getDataStoreDisplayName(), host.getRhostname(), sJobType);
				if(jobType == JobType.JOBTYPE_RESTORE)
					message = WebServiceMessages.getResource("rpsDatastoreNotRunningRestore", policy.getDataStoreDisplayName(), 
						host.getRhostname());
				
				throw new ServiceException(message, FlashServiceErrorCode.Backup_RPS_Datastore_stopped);
			}
		}
		
		logger.debug("Check RPS server and datastore status for backup job end");
		return policy;
	}
	
	private boolean readOnlyJob(long jobType){
		if(jobType == JobType.JOBTYPE_RESTORE
				|| jobType == JobType.JOBTYPE_VM_RECOVERY
				|| jobType == JobType.JOBTYPE_CATALOG_FS
				|| jobType == JobType.JOBTYPE_CATALOG_FS_ONDEMAND
				|| jobType == JobType.JOBTYPE_CATALOG_GRT
				|| jobType == JobType.JOBTYPE_VM_CATALOG_FS
				|| jobType == JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND
				|| jobType == JobType.JOBTYPE_COPY)
			return true;
		else
			return false;
	}
	
	/*public RpsPolicy4D2D checkPolicy4Job(BackupRPSDestSetting setting, long jobType) throws ServiceException {
		logger.debug("Check RPS server and datastore status for restore job");
		
		if(setting == null || setting.getRpsHost() == null 
				|| StringUtil.isEmptyOrNull(setting.getRPSPolicyUUID())){
			logger.error("Invalid parameter, return null");
			return null;
		}
		RpsHost host = setting.getRpsHost();
		String policyUUID = setting.getRPSPolicyUUID();
		String protocol = host.isHttpProtocol() ? "http:" : "https:";
		RpsPolicy4D2D policy = SettingsService.instance().getRPSPolicy(host.getRhostname(), host.getUsername(), 
				host.getPassword(), host.getPort(), protocol, policyUUID, true);
		if(policy == null){
			//policy not exist
			String message = WebServiceMessages.getResource("rpsPolicyNotExist",  
					policyUUID, host.getRhostname());
			throw new ServiceException(message, FlashServiceErrorCode.BackupConfig_ERR_Policy_Not_Exist);
		}
		
		if(policy.getDataStoreOverallStatus() != DataStoreRunningState.RUNNING.getValue()){
			if(readOnlyJob(jobType)
					&& policy.getDataStoreOverallStatus() == DataStoreRunningState.ABNORMAL_RESTORE_ONLY.getValue()){
				return policy;
			}else {
				//datastore not running.
				String sJobType = ServiceUtils.jobType2String(jobType);
				String message = WebServiceMessages.getResource("rpsJobNotRunningDataStoreStopped", 
						policy.getDataStoreDisplayName(), setting.getRpsHost().getRhostname(), sJobType);
				if(jobType == JobType.JOBTYPE_RESTORE)
					message = WebServiceMessages.getResource("rpsDatastoreNotRunningRestore", policy.getDataStoreDisplayName(), 
						host.getRhostname());
				
				throw new ServiceException(message, FlashServiceErrorCode.Backup_RPS_Datastore_stopped);
			}
		}
		
		logger.debug("Check RPS server and datastore status for backup job end");
		return policy;
	}*/
	
	public void notifyRPS4IPChange(List<String> ipList) {
		logger.debug("Notify rps for d2d ip address changed.");
		Set<RpsHost> rpsHosts = new HashSet<RpsHost>();
		getRpsHostOfD2D(rpsHosts);
		getRpsHostOfVSphere(rpsHosts);
		for(RpsHost host : rpsHosts) {
			if (ServiceContext.getInstance().getLocalMachineName()
					.equalsIgnoreCase(host.getRhostname()))
				continue;
			try {
				IRPSService4D2D client = RPSServiceProxyManager.getServiceByHost(host);
				if(client != null){
					client.handleD2DIPChange(CommonService.getInstance().getNodeUUID(), ipList);
				}
			} catch(Throwable t) {			
				logger.error("Failed to notify RPS for ip change", t);
			} 
		}
		logger.debug("Notify rps for d2d ip address changed end.");
	}

	private void getRpsHostOfVSphere(Set<RpsHost> rpsHosts) {
		try {
			for(String vmInstanceUUID : VSphereService.getInstance().getAllConfiguratedVMs()){
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(vmInstanceUUID);
				VMBackupConfiguration vmConf;
				vmConf = VSphereService.getInstance().getVMBackupConfiguration(vm);
				if(vmConf != null && !vmConf.isD2dOrRPSDestType() 
						&& vmConf.getBackupRpsDestSetting() != null){
					RpsHost host = vmConf.getBackupRpsDestSetting().getRpsHost();
					if(host != null){
						rpsHosts.add(host);
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("Failed to get rps host for hbbu");
		}
	}

	private void getRpsHostOfD2D(Set<RpsHost> rpsHosts) {
		try {
			BackupConfiguration backupConf = BackupService.getInstance().getBackupConfiguration();
			if(backupConf != null && !backupConf.isD2dOrRPSDestType() 
					&& backupConf.getBackupRpsDestSetting() != null){
				RpsHost host = backupConf.getBackupRpsDestSetting().getRpsHost();
				if(host != null){
					rpsHosts.add(host);
				}
			}
		} catch (ServiceException e) {
			logger.error("Failed to get rps host for d2d", e);
		}
	}
	
	 public void setDataStore4Policy(RpsPolicy4D2D setting, DataStoreSettingInfo datastore){
    	DataStoreCommonInfo commonSetting = datastore.getDSCommSetting();
 		//
 		setting.setEnableEncryption(datastore.getEnableEncryption() == 1);
 		setting.setEncryptionMethod(datastore.getEncryptionMethod());
 		String encPassword = datastore.getEncryptionPwd();
 		if(!StringUtil.isEmptyOrNull(encPassword)){
 			encPassword = WSJNI.AFDecryptStringEx(encPassword);
		}
 		setting.setEncryptionPassword(encPassword);
 		//
 		setting.setEnableCompression(datastore.getEnableCompression() == 1);
 		setting.setCompressionMethod(datastore.getCompressionMethod());
 		setting.setEnableGDD(datastore.getEnableGDD() == 1);
 		//
 		if(commonSetting != null){
 			setting.setStorePath(commonSetting.getStorePath());
 	 		setting.setDataStoreSharedPath(commonSetting.getStoreSharedName());
 	 		setting.setStoreUserName(commonSetting.getUser());
 	 		String password = commonSetting.getPassword();
 	 		if(!StringUtil.isEmptyOrNull(password)){
 	 			password = WSJNI.AFDecryptStringEx(password);
 			}
 	 		setting.setStorePassword(password);
 		}
 		//
 		setting.setDataStoreDisplayName(datastore.getDisplayName());
 		setting.setDataStoreId(datastore.getDatastore_id());
 		setting.setDataStoreName(datastore.getDatastore_name()); 	
    }

	public long getDataStoreStatus(RpsHost host, String dataStoreUUID) throws ServiceException {
		if(logger.isDebugEnabled())
			logger.debug("Get rps data store status for " + host.getRhostname()
				+ " datastore is " + dataStoreUUID);
		if(host == null || StringUtil.isEmptyOrNull(dataStoreUUID))
			return -1;
		IRPSService4D2D client = RPSServiceProxyManager.getServiceByHost(host);
		if(client !=  null){
			DataStoreStatusListElem[] status = client.getDataStoreStatus(dataStoreUUID);
			if(status == null || status.length == 0)
				return -1;
			else
				return status[0].getDataStoreStatus().getOverallStatus();
		}
		return -1;
	}
	
	public DataStoreStatusListElem getRPSDataStoreInfo(String hostName,
			String userName, String password, int port, String protocol, 
			String dataStoreUUID, String rpsLoginUUID) throws ServiceException{
		if(logger.isDebugEnabled()){
			logger.debug("Get rps data store from: " + hostName);
			logger.debug("userName: " + userName);
			logger.debug("port: " + port);
			logger.debug("protocol: " + protocol);
			logger.debug("policyUUID: " + dataStoreUUID);
		}
		try {
			IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(hostName, userName, 
					password, port, protocol, rpsLoginUUID);
			if(client !=  null){
				DataStoreStatusListElem[] dataStores = client.getDataStoreStatus(dataStoreUUID);
				if(dataStores == null || dataStores.length == 0)
					return null;
				else {
					return dataStores[0];
				}	
			}
		} catch(WebServiceException we) {			
			logger.error("Failed to get rps data store", we);
			ServiceUtils.processWebServiceException(we, hostName);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetDataStore", hostName), 
					FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);
		} catch(ServiceException se) {
			throw se;
		} catch(Exception e){
			logger.error("Failed to get data store", e);
			throw new ServiceException(WebServiceMessages.getResource("failedToGetDataStore", hostName),
					FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);
		}
		
		return null;
	}
	
	public long getRPSDataStoreVersion(RpsHost rpsHost, String dataStoreUUID) {
		try {
			String protocol = rpsHost.isHttpProtocol() ? "http:" : "https:";
			DataStoreStatusListElem datastore = SettingsService.instance()
					.getRPSDataStoreInfo(rpsHost.getRhostname(),
							rpsHost.getUsername(), rpsHost.getPassword(),
							rpsHost.getPort(), protocol, dataStoreUUID,
							rpsHost.getUuid());
			if (datastore != null && datastore.getDataStoreSetting() != null) {
				return datastore.getDataStoreSetting().getVersion();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}
	
	public RpsPolicy4D2D getTempPolicy() {
		return this.tempPolicy;
	}
	
	
	// added for Bug 760516:HBBU Tungsten proxy can backup VM to RPS of Oolong update3
	// check if the target RPS is older than agent.(by comparing the major version and minor version)
	// this should work for both proxy and agent	
	public String checkOldRps(RpsHost rpsHost)
	{
		String result = null;
		boolean isOldRps = false;

		do
		{
			if (rpsHost == null)
			{
				logger.error("rpsHost is null");
				break;
			}

			try
			{
				IRPSService4D2D rpsClient = RPSServiceProxyManager.getServiceByHost(rpsHost);
				RPSVersionInfo rpsVer = rpsClient.getVersionInfo();
				VersionInfo agentVer = CommonService.getInstance().getVersionInfo();

				if (rpsVer == null)
				{
					logger.error("rpsVersionInfo is null");
					break;
				}

				if (agentVer == null)
				{
					logger.error("agentVersionInfo is null");
					break;
				}

				if (Integer.parseInt(rpsVer.getMajorVersion()) < Integer.parseInt(agentVer.getMajorVersion()))
				{
					isOldRps = true;
				}
				else if (Integer.parseInt(rpsVer.getMajorVersion()) == Integer.parseInt(agentVer.getMajorVersion()))
				{
					if (Integer.parseInt(rpsVer.getMinorVersion()) < Integer.parseInt(agentVer.getMinorVersion()))
					{
						isOldRps = true;
					}
				}

				String agentName = "";
				try
				{
					agentName = InetAddress.getLocalHost().getHostName();
				}
				catch (UnknownHostException e)
				{
					logger.error("Failed to get agent host name", e);
				}

				String agentVersion = agentVer.getMajorVersion() + "." + agentVer.getMinorVersion();
				String rpsName = rpsHost.getRhostname();
				String rpsVersion = rpsVer.getMajorVersion() + "." + rpsVer.getMinorVersion();

				if (isOldRps)
				{
					// rpsVersionIsOlderThanAgent=The ^AU_ProductName_SERVER_SHORT^ %s (%s) is older than the ^AU_ProductName_AGENT_SHORT^ %s (%s). 
					result = String.format(WebServiceMessages.getResource("rpsVersionIsOlderThanAgent"), rpsName, rpsVersion, agentName, agentVersion);
					logger.error(result);					
				}
				else
				{
					logger.info("RPS: " + rpsName + " (" + rpsVersion + "), Agent: " + agentName + " (" + agentVersion + ")");
				}
			}
			catch (Exception e)
			{
				logger.error("Failed to check RPS version", e);
			}
		}
		while (false);

		return result;
	}
}
