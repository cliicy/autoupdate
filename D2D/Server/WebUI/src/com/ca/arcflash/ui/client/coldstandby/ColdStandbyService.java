package com.ca.arcflash.ui.client.coldstandby;

import java.util.Date;
import java.util.List;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.ha.model.EdgeLicenseInfo;
import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.ha.model.SummaryModel;
import com.ca.arcflash.ha.model.VCMConfigStatus;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.LogEntry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service/coldStandby")
public interface ColdStandbyService extends RemoteService{
	
	String testMonitorConnection(String serverName, int port, ConnectionProtocol protocol,  String username, String password,boolean isSaveProxy) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	int vcmValidateUserByUUID(String uuid)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	HeartBeatJobScript getHeartBeatJobScript(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	ReplicationJobScript getReplicationJobScript(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void startHeartBeat(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void startReplication(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void cancelReplication(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	NetworkAdapter[] getProdServerNetworkAdapters()  throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	DiskModel[] getProductionServerDiskList()  throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	VMStorage[] getVmStorages(String host, String username, String password,
            String protocol, boolean ignoreCertAuthentidation, long viPort,
            String esxName,String dcName, String[] storageNames)  throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	BaseModel[] getESXNodeList(String esxServer,String username,String passwod,String protocol,int port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	String[] getESXHostDataStoreList(String esxServer,String username,String passwod, String protocol,int port,BaseModel esxHost) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	RepJobMonitor getReplicaJobMonitor(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	
	JobScriptCombo getJobScriptCombo(String vmInstanceUUID)  throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	JobScriptCombo getLocalJobScriptCombo(String vmInstanceUUID)  throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void setJobScriptCombo(JobScriptCombo jc)   throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	int getVMwareServerType(String host,String username,String password, String protocol,int port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	String getESXServerVersion(String host,String username,String password, String protocol,int port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	ESXServerInfo getESXNodeSupportedInfo(String host,String username,String password, String protocol, int port, BaseModel esxHost) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	String[] getESXNodeNetworkAdapterTypes(String host, String username, String password, String protocol, int port, BaseModel esxHost) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	String[] getESXNodeNetworkConnections(String host, String username, String password, String protocol, int port, BaseModel esxHost) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	/**
	 * 
	 * @return  heartbeat state at index 0, 
	 * 			offline copy state(0 auto disabled, 1 auto enabled) at index 1
	 * @throws BusinessLogicException
	 * @throws ServiceConnectException
	 * @throws ServiceInternalException
	 */
	Integer[] getStates(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void pauseHeartBeat(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void stopHeartBeat(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void resumeHeartBeat(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	void disableAutoOfflieCopy(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	void enableAutoOfflieCopy(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	ESXServerInfo getHyperVSupportedInfo(String hypervServerName,
			String username, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	String[] getHypervNetworkAdapterTypes() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	String[] getHypervNetworks(String host, String username, String password) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	
	ARCFlashNodesSummary queryFlashNodesSummary() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	FailoverJobScript getFailoverJobScript(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	VMSnapshotsInfo[] getSnapshots(String uuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void startFailover(String vmInstanceUUID, VMSnapshotsInfo vmSnapInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	boolean isFailoverJobFinishOfProductServer(String vmInstanceUUID)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	/*
	 * connect to specific monitee web service to retrieve information
	 */
	void connectMoniteeServer(ARCFlashNode monitee) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	boolean isHostAMD64Platform() throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;

	//Get Prod server summary panel
	SummaryModel getProductionServerSummaryModel(String vmInstanceUUID)  throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	//get backup configuration
	BackupSettingsModel getBackupConfiguration() throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	//get the volume detail
	List<FileModel> getVolumesWithDetails(String backupDest, String usr, String pwd) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	String getRunningSnapShotGuidForProduction(String vmInstanceUUID)throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	int shutDownVM(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	boolean isHyperVRoleInstalled(String serverName,String protocol,Integer port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	PagingLoadResult<LogEntry> getActivityLogs(PagingLoadConfig config) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void deleteActivityLog(Date date) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;	
	
	PagingLoadResult<LogEntry> getVMActivityLogs(PagingLoadConfig config, BackupVMModel vmModel)
								throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void deleteVMActivityLog(Date date, BackupVMModel vmModel)	
								throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	boolean isVMWareVMNameExist(String host, String username, String password,
			 String protocol, boolean ignoreCertAuthentidation, long viPort,String esxName,
			 String dcName, String vmName) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;	

	boolean isHyperVVMNameExist(String vmName)throws BusinessLogicException, ServiceConnectException, ServiceInternalException;	
	
	/**
	 * @return the count of sessions in monitee has not been replicated
	 * @throws BusinessLogicException
	 * @throws ServiceConnectException
	 * @throws ServiceInternalException
	 */
	int getReplicationQueueSize(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;	
	
	/**Next repliation job will merge all sessions to one. 
	 * The effect of this call would not be taken after the reboot of web service.
	 * @throws BusinessLogicException
	 * @throws ServiceConnectException
	 * @throws ServiceInternalException
	 */
	void forceNextReplicationMerge(String afGuid,Boolean force) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	VCMConfigStatus getVCMConfigStatus(String vmInstanceUUID) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
		
	boolean isHostOSGreaterEqualW2K8SP2(String serverName,String protocol,Integer port) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	long vcmValidateSource(String path, String domain, String user, String pwd, boolean isNeedCreateFolder)throws BusinessLogicException, ServiceConnectException,
		ServiceInternalException ;

	boolean checkResourcePoolExist(String esxServer, String username,
			String passwod, String protocol, int port, String esxName,String dcName, String resPoolRef)
			throws BusinessLogicException, ServiceConnectException,	ServiceInternalException;
	
	EdgeLicenseInfo  getConversionLicense(String aFGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	int[] checkPathIsSupportHyperVVM(List<String> paths) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	EsxServerInformation getEsxServerInformation(String esxServer, String username, String passwod, String protocol, int port)
			throws BusinessLogicException, ServiceConnectException, ServiceInternalException;

	EsxHostInformation getEsxHostInformation(String host, String username, String password, String protocol, int port,
			BaseModel esxHost) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
}