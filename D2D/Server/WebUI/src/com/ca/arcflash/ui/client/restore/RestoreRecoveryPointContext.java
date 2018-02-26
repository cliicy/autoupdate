package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobType;

public class RestoreRecoveryPointContext {
	public final static ArrayList<Integer> AllExchangeTypes = CatalogModelType.allExchangeTypes;
	private static List<GridTreeNode> RestoreRecvPointSources;
	private static RestoreJobType RestoreType;
	private static RestoreJobModel restoreModel;

	public static RestoreJobModel getRestoreModel() {
		if (restoreModel == null) {
			restoreModel = new RestoreJobModel();
			restoreModel.setJobType(1);
		}
		return restoreModel;
	}

	public static void setRestoreModel(RestoreJobModel restoreModel) {
		RestoreRecoveryPointContext.restoreModel = restoreModel;
	}

	public static List<GridTreeNode> getRestoreSources() {
		return RestoreRecvPointSources;
	}

	public static void setRestoreSources(List<GridTreeNode> restoreSources) {
		RestoreRecvPointSources = restoreSources;
	}

	public static RestoreJobType getRestoreType() {
		if (RestoreType == null && RestoreRecvPointSources != null) {
			return RestoreUtil.getJobType(RestoreRecvPointSources);
		}
		return RestoreType;
	}

	public static void setRestoreType(RestoreJobType restoreType) {
		RestoreType = restoreType;
	}

}
