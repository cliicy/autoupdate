package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.io.File;

import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ASBUSyncUtil;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class SyncGDB2EdgeDBTask implements ImportTaskBase/*
														 * implements
														 * IEdgeTaskItem
														 */{

	private SyncFileQueueItem fileItem;
	private ASBUJobInfo jobinfo = null;
	private static final int maxRtryTimes = 10;

	public SyncFileQueueItem getDataFileItem() {
		return fileItem;
	}

	public void SetConfiguration(ASBUJobInfo jobinfo, SyncFileQueueItem dataFileItem) {
		this.fileItem = dataFileItem;
		this.jobinfo = jobinfo;
	}

	public String getTableName(String dataFileName) {
		if (dataFileName == null || dataFileName.length() == 0) {
			return null;
		}

		dataFileName.replace('/', '\\');
		int fileNameStart = dataFileName.lastIndexOf('\\') + 1;
		int fileNameEnd = dataFileName.lastIndexOf('.');
		if (fileNameEnd > fileNameStart && fileNameEnd < dataFileName.length()) {
			String tableName = dataFileName.substring(fileNameStart,
					fileNameEnd);
			return ConfigurationOperator._PrefixTableName + tableName;
		}

		return null;
	}

	public String getFormatFilePath(String tableName) {
		if (tableName == null || tableName.length() == 0) {
			return null;
		}

		try {
			
			String edgeConfigPath = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement);

			String formatFileDir = edgeConfigPath
					+ ConfigurationOperator._FormatFileDirectory;
			String fileName = formatFileDir + tableName
					+ ConfigurationOperator._FormatFileExtension;

			File formatFile = new File(fileName);
			if (formatFile.exists()) {
				return fileName;
			}
		} catch (Exception e) {
			ConfigurationOperator.debugMessage("getFormatFilePath: " + e.getMessage());
		}
		return null;
	}

	// @Override
	public void run() {
		try {

			SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(jobinfo);
			// If it is last file write finished catalog in database
			if (fileItem.isLastFileFlag()) {
				ASBUSyncUtil syncUtil = ASBUSyncUtil.getASBUSyncUtil(fileItem
						.getRhostid());
				_log.WriteInformation(fileItem.getBranchName(), String.format(
						SyncActivityLogMsg.getSyncgdbfullsucceedMsg(), fileItem
								.getBranchName()));
				syncUtil.UpdateFullSyncStatus(SyncStatus.FINISHED);
				return;
			} else {
				/*
				 * _log.WriteInformation(fileItem.getBranchName(),
				 * String.format(
				 * SyncActivityLogMsg.getSyncimportfileintodatabaseMsg(),
				 * fileItem .getFileName()));
				 */
				ConfigurationOperator.debugMessage("NodeName: "
						+ fileItem.getBranchName()
						+ " | "
						+ String.format(SyncActivityLogMsg
								.getSyncimportfileintodatabaseMsg(), fileItem
								.getFileName()));
			}

			String tableName = this.getTableName(fileItem.getFileName());
			String formatFileName = this.getFormatFilePath(tableName);
			String dataFileName = fileItem.getFolder() + fileItem.getFileName();
			
			if (tableName.compareToIgnoreCase("sync_as_aspathname") == 0) {
				tableName = "sync_as_aspathnamew";
			}

			/*if (fileItem.isLastFileFlag()) {
				// output a log, then return
				ConfigurationOperator.debugMessage(fileItem.getBranchName()
						+ " sync is done");
				return;
			}*/

			if (tableName == null || fileItem == null || formatFileName == null) {
				return;
			}
			
			BCPInvokeTaskImpl bcpInvoke = BCPInvokeTaskImpl.GetBCPInvokeTask();
			bcpInvoke.setJobinf(jobinfo);
			if (bcpInvoke.run(fileItem.getBranchName(), tableName,
					formatFileName, dataFileName)) {
				// Thread.sleep(1000); // make sure SQL close data file handle.
				File f = new File(dataFileName);
				int rtryTimes = 0;
				while (!f.delete() && rtryTimes < maxRtryTimes) {
					Thread.sleep(100);
					rtryTimes++;
				}
			}
		} catch (Throwable e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
		}
	}

}
