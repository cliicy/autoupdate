package com.ca.arcflash.ui.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.ws.WebServiceException;

import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.ui.client.common.ICommonRPSService4D2D;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.model.BackupD2DModel;
import com.ca.arcflash.ui.client.model.RpsPolicy4D2DRestoreModel;
import com.ca.arcflash.ui.client.model.rps.RpsDatastore4D2dSettings;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2DSettings;
import com.ca.arcflash.webservice.IFlashService_R16_5;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.data.restore.RpsPolicy4D2DRestore;

public class CommonRPSService4D2DImpl extends BaseServiceImpl implements ICommonRPSService4D2D {

	private static final long serialVersionUID = -7667776991776462269L;

	@Override
	public List<RpsPolicy4D2DSettings> getRPSPolicyList(String hostName,
			String userName, String password, int port, String protocol)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		WebServiceClientProxy client = getServiceClient();
		try {
			if(client != null){
				RpsPolicy4D2D[] policies = client.getFlashService(IFlashService_R16_5.class).getRPSPolicyList(hostName, 
						userName, password, port, protocol);
				if(policies == null)
					return null;
				List<RpsPolicy4D2DSettings> settings = new ArrayList<RpsPolicy4D2DSettings>();
				for(RpsPolicy4D2D policy : policies){
					RpsPolicy4D2DSettings setting = convertToRpsPolicy4D2DSettings(policy);
					if(setting != null)
						settings.add(setting);
				}
				return settings;
			}else{
				return null;
			}
		}catch(WebServiceException e){
			this.proccessAxisFaultException(e);
			return null;
		}
	}	
	
	@Override
	public List<RpsPolicy4D2DRestoreModel> getRPSPolicyList4Restore(String hostName,
			String userName, String password, int port, String protocol)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		WebServiceClientProxy client = getServiceClient();
		try {
			if(client != null){
				RpsPolicy4D2DRestore[] policies = client.getFlashService(IFlashService_R16_5.class).getRPSPolicyList4Restore(hostName, 
						userName, password, port, protocol);
				if(policies == null)
					return null;
				List<RpsPolicy4D2DRestoreModel> settings = new ArrayList<RpsPolicy4D2DRestoreModel>();
				for(RpsPolicy4D2DRestore policy : policies){
					RpsPolicy4D2DRestoreModel setting = (RpsPolicy4D2DRestoreModel) convertToRpsPolicy4D2DSettings(policy);
					if(setting != null){
						setting.d2dList = new ArrayList<BackupD2DModel>();
						for(BackupD2D d2d : policy.getD2dList()) {
							BackupD2DModel model = convertToBackupD2DModel(d2d);
							setting.d2dList.add(model);
						}
						Collections.sort(setting.d2dList, new Comparator<BackupD2DModel>(){

							@Override
							public int compare(BackupD2DModel o1, BackupD2DModel o2) {
								return o1.getHostName().compareTo(o2.getHostName());
							}
						});
						settings.add(setting);
					}
				}
				return settings;
			}else{
				return null;
			}
		}catch(WebServiceException e){
			this.proccessAxisFaultException(e);
			return null;
		}
	}



	@Override
	public List<RpsHostModel> getRPSHostList() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException,
			SessionTimeoutException {
		List<RpsHostModel> rpsHostModelList = new ArrayList<RpsHostModel>();
		List<RpsHost> hostList = getServiceClient().getFlashServiceR16_5().getRpsNodes();
		for(RpsHost host : hostList){
			rpsHostModelList.add(ConvertRpsHostToRpsHostModel(host));
		}
		return rpsHostModelList;
	}
	
	private RpsHostModel ConvertRpsHostToRpsHostModel(RpsHost host){
		RpsHostModel hostModel = new RpsHostModel();
		hostModel.setHostName(host.getRhostname());
		hostModel.setUserName(host.getUsername());
		hostModel.setPassword(host.getPassword());
		hostModel.setPort(host.getPort());
		hostModel.setIsHttpProtocol(host.isHttpProtocol());
		return hostModel;
	}
	
	private RpsPolicy4D2DSettings convertToRpsPolicy4D2DSettings(RpsPolicy4D2D policy){
		if(policy == null)
			return null;
		RpsPolicy4D2DSettings setting = null;
		if(policy instanceof RpsPolicy4D2DRestore){
			setting = new RpsPolicy4D2DRestoreModel();
		}else{
			setting = new RpsPolicy4D2DSettings();
		}
		setting.setCompressionMethod(policy.getCompressionMethod());
		setting.setDataStoreDisplayName(policy.getDataStoreDisplayName());
		setting.setDataStoreId(policy.getDataStoreId());
		setting.setDataStoreName(policy.getDataStoreName());
		setting.setEnableCompression(policy.isEnableCompression());
		setting.setEnableEncryption(policy.isEnableEncryption());
		setting.setEnableGDD(policy.isEnableGDD());
		setting.setEnableReplication(policy.isEnableReplication());
		setting.setEncryptionMethod(policy.getEncryptionMethod());
		setting.setEncryptionPwd(policy.getEncryptionPassword());
		setting.setId(policy.getPolicyUUID());
		setting.setPolicyid(policy.getPolicyId());
		setting.setPolicyName(policy.getPolicyName());
		setting.setStorePassword(policy.getStorePassword());
		setting.setStorePath(policy.getStorePath());
		setting.setDataStoreSharedPath(policy.getDataStoreSharedPath());
		setting.setStoreUser(policy.getStoreUserName());
		setting.setRetentionCount(policy.getRetentionCount());
		setting.setDailyCount(policy.getDailyCount());
		setting.setWeeklyCount(policy.getWeeklyCount());
		setting.setMonthlyCount(policy.getMonthlyCount());
		setting.setDataStoreStatus(policy.getDataStoreOverallStatus());
		return setting;
	}
	
	private BackupD2DModel convertToBackupD2DModel(BackupD2D d2d){
		if(d2d == null)
			return null;
		BackupD2DModel d2dModel = new BackupD2DModel();
		d2dModel.setHostName(d2d.getHostname());
		d2dModel.setDestination(d2d.getFullBackupDestination());
		d2dModel.setDesUsername(d2d.getDesUsername());
		d2dModel.setDesPassword(d2d.getDesPassword());
		d2dModel.setDataStoreName(d2d.getDatastoreName());
		d2dModel.setDataStoreUUID(d2d.getDatastoreUUID());
		d2dModel.setRpsPolicyUUID(d2d.getPolicyUUID());
		d2dModel.setAgentSID(d2d.getD2dSid());
		d2dModel.setAgentUUID(d2d.getLoginUUID());
		d2dModel.setUsername(d2d.getUsername());
		d2dModel.setSourceRPSServerName(d2d.getSourceRPSServerName());
		d2dModel.setDestPlanName(d2d.getDestPlanName());
		return d2dModel;
		
	}

	@Override
	public List<RpsDatastore4D2dSettings> getRPSDatastoreList(String hostName,
			String userName, String password, int port, String protocol)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException {
		try {
			IRPSService4CPM serviceForCPM = RPSWebServiceFactory.getRPSService4CPM(protocol, hostName, port).getServiceForCPM();
			serviceForCPM.validateUser(userName, password, "");
			DataStoreSettingInfo[] ds = serviceForCPM.getDataStoreInstance(null);
			List<RpsDatastore4D2dSettings> dsl=new ArrayList<RpsDatastore4D2dSettings>();
			for(DataStoreSettingInfo d:ds){
				RpsDatastore4D2dSettings dd=new RpsDatastore4D2dSettings();
				dd.setDataStoreDisplayName(d.getDisplayName());
				dd.setDataStoreName(d.getDatastore_name());
				dsl.add(dd);
			}
			return dsl;
		}catch(WebServiceException e){
			this.proccessAxisFaultException(e, false);
			return null;
		}
	}

	@Override
	public Long getDataStoreStatus(RpsHostModel host, String rpsDataStoreUUID) 
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException{
		WebServiceClientProxy client = getServiceClient();
		try {
			if(client != null)
				return client.getFlashServiceR16_5().getDataStoreStatus(
						ConvertDataToModel.convertToData(host), rpsDataStoreUUID);
		}catch(WebServiceException e){
			this.proccessAxisFaultException(e, false);
		}
		return null;
	}
}
