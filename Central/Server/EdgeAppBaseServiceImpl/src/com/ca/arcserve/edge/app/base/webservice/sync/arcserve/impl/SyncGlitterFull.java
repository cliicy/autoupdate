package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.Holder;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSyncDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfstring;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncFileType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ASBUSyncUtil;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.IASBUServerInfo;

public class SyncGlitterFull extends SyncGlitter {

	IMySyncService syncService = null;
	ASBUSyncUtil syncUtil = null;
	List<String> fileNameList = null;
	SyncGDB2EdgeDBMonitor syncMonitor = null;
	String errFileName = "";
	
	public SyncGlitterFull(IASBUServerInfo serverInfo) {
		super.SetBranchID(serverInfo.get_branchID());
		super.SetName(serverInfo.get_serverName());
		super.SetSyncPath(serverInfo.get_branchID());
		super.SetSyncURL(serverInfo.get_URL());
		super.set_retryTimes(serverInfo.get_retryTimes());
		super.set_retryInterval(serverInfo.get_retryInterval());

		syncUtil = ASBUSyncUtil.getASBUSyncUtil(super.GetBranchID());
	}

	@Override
	public void run() {
		SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(super.getJobinfo());
		syncMonitor = SyncGDB2EdgeDBMonitor.GetInstance(super.getJobinfo());
		int[] retryTimes = new int[1];
		syncUtil.UpdateFullSyncStatus(SyncStatus.RUNNING);
		ConfigurationOperator.debugMessage("Full Sync Start. Server: "
				+ super.GetSyncURL());

		retryTimes[0] = 0;
		while (!dumpStart()) {
			if (IsRetryContinue(retryTimes)) {
				_log.WriteError(super.GetName(), String.format(
						SyncActivityLogMsg.getSyncfulldumpdatabaseretrymsg(),
						super.GetName()));
				continue;
			}
			return;
		}

		retryTimes[0] = 0;
		while (!GettingFileList()) {
			if (IsRetryContinue(retryTimes))
			{
				/*_log.WriteError(super.GetName(), String.format(
						SyncActivityLogMsg.getSyncfullgetfilelistretrymsg(),
						super.GetName()));*/
				ConfigurationOperator.errorMessage(String.format(
						SyncActivityLogMsg.getSyncfullgetfilelistretrymsg(),
						super.GetName()));
				continue;
			}
			_log.WriteError(super.GetName(), SyncActivityLogMsg.getSyncfailed());
			syncUtil.UpdateFullSyncStatus(SyncStatus.FAILED);
			return;
		}

		retryTimes[0] = 0;
		while (!ImportFile()) {
			if (IsRetryContinue(retryTimes))
			{
				_log.WriteError(super.GetName(), String.format(
						SyncActivityLogMsg.getSyncfulltransferfileregrymsg(),
						errFileName, super.GetName()));
				continue;
			}
			return;
		}

		// wait for the sync complete. 
		while(!syncMonitor.IsQueueEmpty()){
			try {
				Thread.sleep(1000*3);// sleep 3 seconds
			} catch (InterruptedException e) {
			}
		}
		
		ConfigurationOperator.debugMessage("Full sync for ASBU end");
	}

	private boolean IsRetryContinue(int[] retryTimes) {
		retryTimes[0]++;
		if (retryTimes[0] >= super.get_retryTimes())
			return false;
		try {
			Thread.sleep(super.get_retryInterval());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			ConfigurationOperator.debugMessage("Thread sleep exception: "
					+ e.getMessage());
		}
		return true;
	}

	private boolean dumpStart() {
		SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(super.getJobinfo());
		try {
			Integer bRet = -1;
			if (syncService == null)
				syncService = SyncServiceFactory.createSyncService(super.GetSyncURL(), super.GetBranchID());
			ConfigurationOperator.debugMessage("Full Dump Start.");
			_log.WriteInformation(super.GetName(),
					SyncActivityLogMsg.getSyncfullstartedMsg());
			Holder<Integer> fullDumpDataBaseResult = new Holder<Integer>();
			Holder<Integer> serverUTCOffset = new Holder<Integer>();
			syncService.fullDumpDataBase(serverUTCOffset, fullDumpDataBaseResult);
			bRet = fullDumpDataBaseResult.value;
			 IEdgeHostMgrDao iDao = DaoFactory.getDao(IEdgeHostMgrDao.class);

			iDao.as_edge_host_update_timezone_by_id(super.GetBranchID(), serverUTCOffset.value);
			if (bRet != 0) {
				_log.WriteInformation(super.GetName(),
						SyncActivityLogMsg.getDumpdatabasefailedMsg());
				ConfigurationOperator
						.debugMessage("Full Dump in ASBU side failed.");
				syncUtil.UpdateFullSyncStatus(SyncStatus.FAILED);
				return false;
			}
		} catch (Exception e) {
			_log.WriteError(super.GetName(), SyncActivityLogMsg.getServiceerrorMsg());
			syncUtil.UpdateFullSyncStatus(SyncStatus.FAILED);
			return false;
		}
		return true;
	}

	private boolean GettingFileList() {

		SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(super.getJobinfo());
		try {
			int failedTimes = 0;
			ConfigurationOperator.debugMessage("Full Dump End.");

			while (true) {
				ArrayOfstring array = syncService.getSyncFileList();

				if (array != null && (fileNameList = array.getString()) != null)
					break;

				failedTimes++;
				if (failedTimes >= ConfigurationOperator.getDefaultchecktimes())
					return false;
				Thread.sleep(ConfigurationOperator.getDefaultcheckinterval());
			}

			/*
			 * Clear All branch information before import into database.
			 */
			/*_log.WriteInformation(super.GetName(),
					SyncActivityLogMsg.getSyncfullendMsg());*/
			ConfigurationOperator.debugMessage("The ARCserve Sync dump database finished");
			IEdgeSyncDao iDao = DaoFactory.getDao(IEdgeSyncDao.class);

			iDao.as_edge_sync_delete_branch(super.GetBranchID());
		} catch (Exception e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
			return false;
		}

		return true;
	}

	private boolean ImportFile() {
		SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(super.getJobinfo());
		Iterator<String> iterFile = fileNameList.iterator();
		IEdgeSyncDao iDao = DaoFactory.getDao(IEdgeSyncDao.class);
		iDao.as_edge_sync_delete_branch(super.GetBranchID());
		while (iterFile.hasNext()) {
			String sourcePath = iterFile.next();
			int i = sourcePath.lastIndexOf('\\');
			String fileName = sourcePath.substring(i + 1);
			String outputPath = super.GetSyncPath() + super.GetBranchID()
					+ "\\";

			ConfigurationOperator.debugMessage("Get file from :" + sourcePath);
			ConfigurationOperator
					.debugMessage("[Out Path: " + outputPath + "]");

			CreateFilePath(outputPath);

			try {
				TransferFile(outputPath, fileName, sourcePath);
			} catch (Exception e) {
				errFileName = fileName;
				if (e instanceof IOException)
					_log.WriteError(super.GetName(), String.format(
							SyncActivityLogMsg.getSyncTransferFileErrorMsg(),
							outputPath + fileName, super.GetName(),
							e.getLocalizedMessage()));
				else
					_log.WriteWarning(super.GetName(),
							"[" + outputPath + fileName + "] "
									+ SyncActivityLogMsg.getSyncfileerrorMsg());
				ConfigurationOperator.errorMessage(e.getMessage(), e);
				return false;
			}

			ImportIntoDatabase(fileName, outputPath);
		}
		AddEndItem();
		return true;
	}

	private void CreateFilePath(String outputPath) {
		File dir = null;
		dir = new File(outputPath);
		if (!dir.exists() || !dir.isDirectory()) {
			if (!dir.mkdirs()) {
				ConfigurationOperator.errorMessage("Create directory : "
						+ dir.getName() + " failed");
			}
		}
	}

	private Holder<SyncFileType> GetSyncFileInfo(String sourcePath) {
		Holder<SyncFileType> syncFileInfo = new Holder<SyncFileType>();
		syncFileInfo.value = new SyncFileType();
		syncFileInfo.value.setStrFileName(sourcePath);
		syncFileInfo.value.setStartOffset(0);
		syncFileInfo.value.setReadSize(0);
		syncFileInfo.value.setMaxSendSize(1024 * 1024);
		return syncFileInfo;
	}

	private void TransferFile(String outputPath, String filename,
			String sourcePath) throws Exception {
		FileOutputStream writeFile = new FileOutputStream(outputPath + filename);
		Holder<SyncFileType> syncFileInfo = GetSyncFileInfo(sourcePath);
		do {
			Holder<byte[]> buffer = new Holder<byte[]>();
			syncService.transferData(syncFileInfo, buffer);
			if (buffer.value == null || syncFileInfo.value.getReadSize() <= 0) {
				syncService.syncFileEnd(syncFileInfo.value.getStrFileName());
				break;
			}
			writeFile.write(buffer.value, 0, syncFileInfo.value.getReadSize());
		} while (syncFileInfo.value.getReadSize() > 0);

		if (writeFile != null)
			writeFile.close();
	}

	private void ImportIntoDatabase(String filename, String outputPath) {
		SyncFileQueueItem item = new SyncFileQueueItem();
		item.setFolder(outputPath);
		item.setFileName(filename);
		item.setBranchName(super.GetName());
		item.setRhostid(super.GetBranchID());
		item.setSyncType(ConfigurationOperator._ArcserveType);
		syncMonitor.AddToFileQueue(item);
	}

	private void AddEndItem() {
		SyncFileQueueItem item = new SyncFileQueueItem();
		item.setLastFileFlag(true);
		item.setBranchName(super.GetName());
		item.setSyncType(ConfigurationOperator._ArcserveType);
		item.setRhostid(super.GetBranchID());
		syncMonitor.AddToFileQueue(item);
	}

}
