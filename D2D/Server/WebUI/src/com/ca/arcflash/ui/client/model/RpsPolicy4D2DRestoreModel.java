package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2DSettings;

public class RpsPolicy4D2DRestoreModel extends RpsPolicy4D2DSettings {

	private static final long serialVersionUID = 3407042745487975925L;
	
	public List<BackupD2DModel> d2dList;

	public void copy(RpsPolicy4D2DRestoreModel model) {
		setCompressionMethod(model.getCompressionMethod());
		setDataStoreDisplayName(model.getDataStoreDisplayName());
		setDataStoreId(model.getDataStoreId());
		setDataStoreName(model.getDataStoreName());
		setEnableCompression(model.getEnableCompression());
		setEnableEncryption(model.getEnableEncryption());
		setEnableGDD(model.isEnableGDD());
		setEnableReplication(model.isEnableReplication());
		setEncryptionMethod(model.getEncryptionMethod());
		setEncryptionPwd(model.getEncryptionPwd());
		setId(model.getId());
		setPolicyid(model.getPolicyid());
		setPolicyName(model.getPolicyName());
		setStorePassword(model.getStorePassword());
		setStorePath(model.getStorePath());
		setStoreUser(model.getStoreUser());
		setDataStoreSharedPath(model.getDataStoreSharedPath());
		setRetentionCount(model.getRetentionCount());
		setDataStoreStatus(model.getDataStoreStatus());
		if(d2dList == null)
			d2dList = new ArrayList<BackupD2DModel>();
		if(model.d2dList != null) {
			for(BackupD2DModel d2d : model.d2dList) {
				BackupD2DModel dm = new BackupD2DModel();
				dm.setDataStoreName(d2d.getDataStoreName());
				dm.setDataStoreUUID(d2d.getDataStoreUUID());
				dm.setDesPassword(d2d.getDesPassword());
				dm.setDestination(d2d.getDestination());
				dm.setDesUsername(d2d.getDesUsername());
				dm.setHostName(d2d.getHostName());
				dm.setAgentSID(d2d.getAgentSID());
				dm.setAgentUUID(d2d.getAgentUUID());
				dm.setRpsPolicyUUID(d2d.getRpsPolicyUUID());
				d2dList.add(dm);
			}
		}
	}
}
