package com.ca.arcflash.ui.client.coldstandby;


import java.util.Date;
import java.util.List;

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
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.LogEntry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ColdStandbyServiceAsync {

	void testMonitorConnection(String serverName, int port, ConnectionProtocol protocol, String username,
			String password,boolean isSaveProxy, AsyncCallback<String> callback);
	
	void vcmValidateUserByUUID(String uuid, AsyncCallback<Integer> callback);

	void startHeartBeat(String afGuid,AsyncCallback<Void> callback);

	void getHeartBeatJobScript(String afGuid,AsyncCallback<HeartBeatJobScript> callback);

	void getReplicationJobScript(String afGuid,AsyncCallback<ReplicationJobScript> callback);

	void startReplication(String vmInstanceUUID, AsyncCallback<Void> callback);
	
	void cancelReplication(String afGuid,AsyncCallback<Void> callback);

	void getProdServerNetworkAdapters(AsyncCallback<NetworkAdapter[]> callback);
	
	void getProductionServerDiskList(AsyncCallback<DiskModel[]> callback);
	
	void getVmStorages(String host, String username, String password,
            String protocol, boolean ignoreCertAuthentidation, long viPort,
            String esxName,String dcName, String[] storageNames,AsyncCallback<VMStorage[]> callback);
	
	void getESXNodeList(String esxServer, String username, String passwod,
			String protocol, int port, AsyncCallback<BaseModel[]> callback);

	void getESXHostDataStoreList(String esxServer, String username,
			String passwod, String protocol, int port, BaseModel esxHost,
			AsyncCallback<String[]> callback);

	void getReplicaJobMonitor(String vmInstanceUUID, AsyncCallback<RepJobMonitor> callback);

	void getJobScriptCombo(String vmInstanceUUID, AsyncCallback<JobScriptCombo> callback);
	void getLocalJobScriptCombo(String vmInstanceUUID, AsyncCallback<JobScriptCombo> callback);

	void setJobScriptCombo(JobScriptCombo jc, AsyncCallback<Void> callback);

	void getVMwareServerType(String host, String username, String password,
			String protocol,int port, AsyncCallback<Integer> callback);

	void getESXServerVersion(String host, String username, String password,
			String protocol,int port, AsyncCallback<String> callback);

	void getESXNodeNetworkAdapterTypes(String host, String username, String password, String protocol, int port, BaseModel esxHost, AsyncCallback<String[]> callback);

	void getESXNodeNetworkConnections(String host, String username, String password, String protocol, int port, BaseModel esxHost, AsyncCallback<String[]> callback);

	void getESXNodeSupportedInfo(String host, String username, String password,
			String protocol, int port, BaseModel esxHost,
			AsyncCallback<ESXServerInfo> callback);

	void getStates(String afGuid,AsyncCallback<Integer[]> callback);

	void pauseHeartBeat(String afGuid,AsyncCallback<Void> callback);

	void stopHeartBeat(String afGuid,AsyncCallback<Void> callback);

	void resumeHeartBeat(String afGuid,AsyncCallback<Void> callback);

	void enableAutoOfflieCopy(String vmInstanceUUID, AsyncCallback<Void> callback);

	void disableAutoOfflieCopy(String vmInstanceUUID, AsyncCallback<Void> callback);

	void getHyperVSupportedInfo(String hypervServerName,
			String username, String password,
			AsyncCallback<ESXServerInfo> callback);
	
	void getHypervNetworkAdapterTypes(AsyncCallback<String[]> callback);
	
	void getHypervNetworks(String host, String username, String password, AsyncCallback<String[]> callback);
	
	void getFailoverJobScript(String vmInstanceUUID, AsyncCallback<FailoverJobScript> callback);

	void queryFlashNodesSummary(AsyncCallback<ARCFlashNodesSummary> callback);

	void getSnapshots(String vmInstanceUUID, AsyncCallback<VMSnapshotsInfo[]> callback);

	void startFailover(String vmInstanceUUID, VMSnapshotsInfo vmSnapInfo, AsyncCallback<Void> callback);
	
	void isFailoverJobFinishOfProductServer(String vmInstanceUUID, AsyncCallback<Boolean> callback);
	
	void connectMoniteeServer(ARCFlashNode monitee, AsyncCallback<Void> callback);
	
	void isHostAMD64Platform(AsyncCallback<Boolean> callback);

	void getProductionServerSummaryModel(String vmInstanceUUID, AsyncCallback<SummaryModel> callback);
	
	void getBackupConfiguration(AsyncCallback<BackupSettingsModel> callback);
	
	void getVolumesWithDetails(String backupDest, String usr, String pwd, AsyncCallback<List<FileModel>> callback);
	
	void getRunningSnapShotGuidForProduction(String vmInstanceUUID, AsyncCallback<String> callback);
	
	void shutDownVM(String vmInstanceUUID, AsyncCallback<Integer> callback); 
	
	void isHyperVRoleInstalled(String serverName,String protocol,Integer port, AsyncCallback<Boolean> callback);
	
	void getActivityLogs(PagingLoadConfig config, AsyncCallback<PagingLoadResult<LogEntry>> callback);
	
	void deleteActivityLog(Date date,AsyncCallback<Void> callback);
	
	void getVMActivityLogs(PagingLoadConfig config, BackupVMModel vmModel, AsyncCallback<PagingLoadResult<LogEntry>> callback);
	
	void deleteVMActivityLog(Date date, BackupVMModel vmModel, AsyncCallback<Void> callback);
	
	void isVMWareVMNameExist(String host, String username, String password,
			 String protocol, boolean ignoreCertAuthentidation, long viPort,
			 String esxName,String dcName, String vmName, AsyncCallback<Boolean> callback);

	void isHyperVVMNameExist(String vmName, AsyncCallback<Boolean> callback);

	void getReplicationQueueSize(String afGuid,AsyncCallback<Integer> callback);

	void forceNextReplicationMerge(String afGuid,Boolean force, AsyncCallback<Void> callback);

	void getVCMConfigStatus(String vmInstanceUUID, AsyncCallback<VCMConfigStatus> callback);
	
	void isHostOSGreaterEqualW2K8SP2(String serverName,String protocol,Integer port, AsyncCallback<Boolean> callback);
	
	void vcmValidateSource(String path, String domain, String user, String pwd, boolean isNeedCreateFolder,
			AsyncCallback<Long> callback);
	
	void checkResourcePoolExist(String esxServer, String username,
			String passwod, String protocol,int port, String esxName,String dcName, String resPoolRef,AsyncCallback<Boolean> callback);
	
	void  getConversionLicense(String aFGuid, AsyncCallback<EdgeLicenseInfo> callback);
	
	void checkPathIsSupportHyperVVM(List<String> paths, AsyncCallback<int[]> callback);

	void getEsxServerInformation(String esxServer, String username, String passwod, String protocol, int port,
			AsyncCallback<EsxServerInformation> callback);

	void getEsxHostInformation(String host, String username, String password, String protocol, int port,
			BaseModel esxHost, AsyncCallback<EsxHostInformation> callback);
}
