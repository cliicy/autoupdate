package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.CatalogItemModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint;
import com.ca.arcflash.ui.client.model.ExchVersion;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RecoveryPointItemModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RestoreArchiveJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobType;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.extjs.gxt.ui.client.store.TreeStore;

public class RestoreContext {
	public final static ArrayList<Integer> AllExchangeTypes = CatalogModelType.allExchangeTypes;
	private static List<GridTreeNode> restoreRecvPointSources;
	private static List<CatalogItemModel> restoreSearchSources;
	private static TreeStore<GridTreeNode> restoreRecvPointTreeStore;
	private static RestoreJobType restoreType;
	private static RestoreJobModel restoreModel;
	private static RestoreArchiveJobModel restoreArchiveModel;
	private static ExchVersion exchVersion = null;
	private static RecoveryPointModel recoveryPointModel;
	private static BackupSettingsModel backupModel;
	private static BackupVMModel backupVMModel;
	private static BackupVMModel vmModel;
	private static Map<String, EncrypedRecoveryPoint> encrypedRecoveryPoints;
	private static List<RecoveryPointModel> restoreSearchSelectedSessions;
	private static Map<GridTreeNode, RecoveryPointItemModel> rootItemMap;

	//archive
	private static List<ArchiveGridTreeNode> restoreArchiveNodes;
	private static TreeStore<ArchiveGridTreeNode> restoreArchiveTreeStore;
	private static List<CatalogItemModel> restoreSearchArchiveSources;
	public static boolean isBackupToDataStore;
	
	public static void init() {
		cleanUp();
	}

	public static void destory() {
		cleanUp();
	}

	private static void cleanUp() {
		restoreRecvPointSources = null;
		restoreSearchSources = null;
		restoreRecvPointTreeStore = null;
		restoreType = null;
		restoreModel = null;
		exchVersion = null;
		recoveryPointModel = null;
		backupModel = null;
		backupVMModel = null;
		vmModel = null;
		restoreSearchSelectedSessions= null;
		//archive
		restoreArchiveNodes = null;
		restoreArchiveTreeStore = null;
		rootItemMap = null;
	}

	public static Map<GridTreeNode, RecoveryPointItemModel> getRootItemMap() {
		return rootItemMap;
	}

	public static void setRootItemMap(
			Map<GridTreeNode, RecoveryPointItemModel> rootItemMap) {
		RestoreContext.rootItemMap = rootItemMap;
	}

	public static TreeStore<GridTreeNode> getRestoreRecvPointTreeStore() {
		return restoreRecvPointTreeStore;
	}

	public static void setRestoreRecvPointTreeStore(
			TreeStore<GridTreeNode> restoreRecvPointTreeStore) {
		RestoreContext.restoreRecvPointTreeStore = restoreRecvPointTreeStore;
	}

	public static List<GridTreeNode> getRestoreRecvPointSources() {
		return restoreRecvPointSources;
	}

	public static List<CatalogItemModel> getRestoreSearchSources() {
		return restoreSearchSources;
	}

	public static void setRestoreSearchSources(
			List<CatalogItemModel> restoreSearchSources) {
		RestoreContext.restoreSearchSources = restoreSearchSources;
	}

	public static void setRestoreRecvPointSources(
			List<GridTreeNode> restoreRecvPointSources) {
		RestoreContext.restoreRecvPointSources = restoreRecvPointSources;
	}

	public static RestoreJobModel getRestoreModel() {
		if (restoreModel == null) {
			restoreModel = new RestoreJobModel();
			restoreModel.setJobType(1);			
		}
		return restoreModel;
	}

	public static void setRestoreModel(RestoreJobModel restoreModel) {
		RestoreContext.restoreModel = restoreModel;
	}

	public static RestoreJobType getRestoreType() {
		if (restoreType == null && restoreRecvPointSources != null) {
			return RestoreUtil.getJobType(restoreRecvPointSources);
		}
		return restoreType;
	}

	public static void setRestoreType(RestoreJobType restoreType) {
		RestoreContext.restoreType = restoreType;
	}

	public static ExchVersion getExchVersion() {
		return exchVersion;
	}

	public static void setExchVersion(ExchVersion exchVersion) {
		RestoreContext.exchVersion = exchVersion;
	}

	public static RecoveryPointModel getRecoveryPointModel() {
		return recoveryPointModel;
	}
			
	public static void setRecoveryPointModel(RecoveryPointModel pointModel) {
		recoveryPointModel = pointModel;
	}
	
	public static BackupSettingsModel getBackupModel() {
		return backupModel;
	}

	public static void setBackupModel(BackupSettingsModel backupModel) {
		RestoreContext.backupModel = backupModel;
	}

	public static Map<String, EncrypedRecoveryPoint> getEncrypedRecoveryPoints() {
		return encrypedRecoveryPoints;
	}

	public static void setEncrypedRecoveryPoints(
			Map<String, EncrypedRecoveryPoint> encrypedRecoveryPoints) {
		RestoreContext.encrypedRecoveryPoints = encrypedRecoveryPoints;
	}
	
	public static BackupVMModel getBackupVMModel() {
		return backupVMModel;
	}

	public static void setBackupVMModel(BackupVMModel backupVMModel) {
		RestoreContext.backupVMModel = backupVMModel;
	}
	
	public static BackupVMModel getVMModel() {
		return vmModel;
	}

	public static void setVMModel(BackupVMModel backupVMModel) {
		RestoreContext.vmModel = backupVMModel;
	}

    public static RestoreJobType getRestoreSearchType() {
		return RestoreUtil.getRestoreSearchType(restoreSearchSources);
    }
    
    public static List<CatalogItemModel> getRestoreArchiveSearchSources() {
		return restoreSearchArchiveSources;
	}

	public static void setRestoreArchiveSearchSources(
			List<CatalogItemModel> restoreSearchSources) {
		RestoreContext.restoreSearchArchiveSources = restoreSearchSources;
	}
	
	public static List<ArchiveGridTreeNode> getRestoreSelectedArchiveNodes() {
		return restoreArchiveNodes;
	}
	
	public static void setRestoreSelectedArchiveNodes(
			List<ArchiveGridTreeNode> in_restoreSelectedArchiveNodes) {
		RestoreContext.restoreArchiveNodes = in_restoreSelectedArchiveNodes;
	}
	
	public static TreeStore<ArchiveGridTreeNode> getRestoreArchiveTreeStore() {
		return restoreArchiveTreeStore;
	}

	public static void setRestoreArchiveTreeStore(
			TreeStore<ArchiveGridTreeNode> in_restoreArchiveTreeStore) {
		RestoreContext.restoreArchiveTreeStore = in_restoreArchiveTreeStore;
	}
	
	public static RestoreArchiveJobModel getRestoreArchiveJobModel() {
		if (restoreArchiveModel == null) {
			restoreArchiveModel = new RestoreArchiveJobModel();
			restoreArchiveModel.setJobType(1);			
		}
		return restoreArchiveModel;
	}

	public static void setRestoreArchiveJobModel(RestoreArchiveJobModel in_archiveRestoreModel) {
		RestoreContext.restoreArchiveModel = in_archiveRestoreModel;
	}
}
