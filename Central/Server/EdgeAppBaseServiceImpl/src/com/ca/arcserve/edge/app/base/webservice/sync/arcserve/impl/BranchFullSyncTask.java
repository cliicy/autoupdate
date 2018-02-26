package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.io.IOException;
import java.util.List;

import javax.xml.ws.Holder;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeSyncDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfstring;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class BranchFullSyncTask extends BranchSyncTask {

	
	private IMySyncService syncService = null;
	private SyncEnvChecker checker = null;
	private SyncGDB2EdgeDBMonitor syncMonitor = null;
	@Override
	public void run() {

		SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(super.getJobInfo());
		syncMonitor = SyncGDB2EdgeDBMonitor.GetInstance(super.getJobInfo());
		
		String fmtMsg = "";

		fmtMsg = String
				.format(SyncActivityLogMsg.getSyncgdbfullstartedMsg(), this
						.getGdbServerInfo().getRhostname(), this.getBranchSiteInfo()
						.getServerName());
		_log.WriteInformation(this.getGdbServerName(), fmtMsg);

		// Clear up the old record
		IEdgeSyncDao edao = DaoFactory.getDao(IEdgeSyncDao.class);
		edao.as_edge_sync_delete_branch(this.getBranchHostID());

		// create saving local folder
		checker = new SyncEnvChecker(this.getGdbServerInfo().getRhostid(), this
				.getBranchHostID());
		if (!checker.checkFolder()) {
			checker.createFolder();
		}

		// Start the sync operation
		try {
			int[] retryTimes = new int[1];
			// Require data file list
			retryTimes[0] = 0;
			while (!DumpDatabase(_log)) {
				if (IsRetryContinue(retryTimes)) {
					_log.WriteError(super.getGdbServerName(), String
							.format(SyncActivityLogMsg
									.getSyncfulldumpdatabaseretrymsg(), super
									.getBranchSiteInfo().getServerName()));
					continue;
				}
				return;
			}

			ArrayOfstring array = null;
			retryTimes[0] = 0;
			while ((array = GetFileList(syncService)) == null) {
				if (IsRetryContinue(retryTimes)) {
					ConfigurationOperator.errorMessage(SyncActivityLogMsg
							.getSyncgdbfullgetfilelisterrorMsg());
					continue;
				}
				fmtMsg = String.format(SyncActivityLogMsg
						.getSyncgdbfullfailedMsg(), this.getGdbServerInfo()
						.getRhostname(), this.getBranchSiteInfo()
						.getServerName())
						+ SyncActivityLogMsg
								.getSyncgdbfullgetfilelisterrorMsg();
				ConfigurationOperator.errorMessage(fmtMsg);
				_log.WriteError(this.getGdbServerName(), fmtMsg);
				return;
			}

			TransferFile(_log, array);

			AddEndItem();

			// wait for the sync complete. 
			while(!syncMonitor.IsQueueEmpty()){
				try {
					Thread.sleep(1000*3);// sleep 3 seconds
				} catch (InterruptedException e) {
				}
			}
			
		} catch (Exception e) {
			fmtMsg = String.format(
					SyncActivityLogMsg.getSyncgdbfullfailedMsg(), this
							.getGdbServerInfo().getRhostname(), this.getBranchSiteInfo()
							.getServerName());
			ConfigurationOperator.errorMessage(fmtMsg);
			ConfigurationOperator.errorMessage(e.getMessage());
			_log.WriteError(this.getGdbServerName(), fmtMsg);
		}

	}

	private ArrayOfstring GetFileList(IMySyncService syncService) {
		ArrayOfstring array = null;
		int failedTimes = 0;

		while (true) {
			try {
				array = syncService.syncFileList(this.getBranchHostID(), this
						.getBranchSiteInfo().getId());

				if (array != null && array.getString() != null)
					break;

				failedTimes++;
				if (failedTimes >= ConfigurationOperator.getDefaultchecktimes())
					return null;
				Thread.sleep(ConfigurationOperator.getDefaultcheckinterval());
			} catch (Exception e) {
				ConfigurationOperator.errorMessage(e.getMessage());
				return null;
			}
		}

		return array;
	}

	private boolean DumpDatabase(SyncASBUActivityLog log) {
		
		String fmtErrorMsg = "";
		fmtErrorMsg = String.format(SyncActivityLogMsg
				.getSyncgdbfullfailedMsg(), this.getGdbServerInfo().getRhostname(), this
				.getBranchSiteInfo().getServerName())
				+ SyncActivityLogMsg.getSyncgdbfullwebserviceerrorMsg();
		try {
			syncService = SyncServiceFactory.createSyncService(this.getGdbUrl(), this.getGdbServerInfo().getRhostid());
			Holder<Integer> status = new Holder<Integer>();
			Holder<Integer> timeoffset = new Holder<Integer>();

			syncService.syncGDBDatabase(this.getBranchHostID(),
					this.getBranchSiteInfo().getId(), timeoffset, status);

			if (status.value != 0) {
				ConfigurationOperator.errorMessage(fmtErrorMsg);
				log.WriteError(this.getGdbServerName(), fmtErrorMsg);
				return false;
			}
		} catch (Exception e) {
			ConfigurationOperator.errorMessage(e.getMessage());
			log.WriteError(this.getGdbServerName(), fmtErrorMsg);
			return false;
		}
		return true;
	}

	private boolean TransferFile(SyncASBUActivityLog log, ArrayOfstring array) {
		List<String> filelist = array.getString();
		int[] retryTimes = new int[1];
		int i = 0;


		while (i < filelist.size()) {		
			SyncDataWritter writter = new SyncDataWritter(syncService);
			try {
				String s = filelist.get(i);
				// Read data file and save it to local disk
				writter.setPath(s, checker.getFolderPath());
				if (!writter.Write()) {
					ConfigurationOperator.errorMessage("write file " + s
							+ " failed.");
					if (IsRetryContinue(retryTimes)) {
						log.WriteError(super.getGdbServerName(), String.format(
								SyncActivityLogMsg.getSyncfulltransferfileregrymsg(),
								s, super.getBranchSiteInfo().getServerName()));
						continue;
					}
				} else {
					ConfigurationOperator.debugMessage("write file " + s
							+ " succeed.");

					// Put the written file name to sync file queue
					SyncFileQueueItem item = new SyncFileQueueItem();
					item.setFolder(checker.getFolderPath());
					item.setFileName(writter.getFileName());
					item
							.setBranchName(this.getBranchSiteInfo()
									.getServerName());
					item.setRhostid(this.getBranchHostID());
					item.setSyncType(ConfigurationOperator._GDBType);
					syncMonitor.AddToFileQueue(item);
				}

				retryTimes[0] = 0;
				i++;

			} catch (Exception e) {
				if (IsRetryContinue(retryTimes)) {
					if (e instanceof IOException)
						log.WriteError(
								super.getGdbServerName(),
								String.format(
										SyncActivityLogMsg
												.getSyncTransferFileErrorMsg(),
										writter.getOutputPath()
												+ ConfigurationOperator._ZipFileExtension,
										super.getBranchSiteInfo()
												.getServerName(), e
												.getLocalizedMessage()));
					else
						log.WriteError(super.getGdbServerName(), String.format(
								SyncActivityLogMsg
										.getSyncfulltransferfileregrymsg(),
								super.getBranchSiteInfo().getServerName()));
					continue;
				}
			}
		}
		return true;
	}

	private void AddEndItem() {
		SyncFileQueueItem item = new SyncFileQueueItem();
		item.setLastFileFlag(true);
		item.setBranchName(this.getBranchSiteInfo().getServerName());
		item.setSyncType(ConfigurationOperator._GDBType);
		syncMonitor.AddToFileQueue(item);
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
}
