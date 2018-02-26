package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import javax.xml.ws.Holder;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeD2DSyncDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.ChangeStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncTranInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncTranInfoTransferType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ASBUSyncUtil;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.IASBUServerInfo;

public class SyncGlitterInc extends SyncGlitter {
	static final int MAXFAILTIMES = 5;

	public SyncGlitterInc(IASBUServerInfo serverInfo) {
		super.SetBranchID(serverInfo.get_branchID());
		super.SetName(serverInfo.get_serverName());
		super.SetSyncURL(serverInfo.get_URL());
		super.SetSyncPath(serverInfo.get_branchID());
		super.set_retryTimes(serverInfo.get_retryTimes());
		super.set_retryInterval(serverInfo.get_retryInterval());
	}

	@Override
	public void run() {
		SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(super.getJobinfo());
		ConfigurationOperator.debugMessage("Incremental Sync Start. Server: "
				+ super.GetSyncURL());
		
		IMySyncService syncService = null;
		 IEdgeHostMgrDao iDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		try {
			syncService = SyncServiceFactory.createSyncService(super.GetSyncURL(), super.GetBranchID());
		} catch (Exception e) {
			_log.WriteError(super.GetName(), SyncActivityLogMsg
					.getServiceerrorMsg());
			return;
		}

		Holder<SyncTranInfo> syncTranInfo = new Holder<SyncTranInfo>();
		syncTranInfo.value = new SyncTranInfo();
		syncTranInfo.value.setType(SyncTranInfoTransferType.REGULAR_SYNC);
		syncTranInfo.value.setMaxTransactionCount(10000);
		syncTranInfo.value.setMaxTransferCount(10000);
		syncTranInfo.value.setStartID(new Long(1));
		syncTranInfo.value.setBranchID((long) 0);
		syncTranInfo.value.setIsDBChanged(0);
		Holder<byte[]> transferDataResult = new Holder<byte[]>();

		_log.WriteInformation(super.GetName(), SyncActivityLogMsg
				.getSyncincsyncstartMsg());
		
		int failTimes = 0;
		do {
			ConfigurationOperator
					.debugMessage("Incremental Data Transfer Begin.");
			/*
			 * _log.WriteInformation(super.GetName(), SyncActivityLogMsg
			 * .getSyncgdbincdatatransferstartMsg());
			 */
			
			syncService.incrementalSyncDataTransfer(syncTranInfo,
					transferDataResult);
			iDao.as_edge_host_update_timezone_by_id(super.GetBranchID(),
					syncTranInfo.value.getUTCOffset());
			if (syncTranInfo.value.getIsDBChanged() > 0) {
				IEdgeD2DSyncDao synDao = DaoFactory.getDao(IEdgeD2DSyncDao.class);
				synDao.as_edge_update_change_status(
						EdgeSyncComponents.ARCserve_Backup.getValue(), super
								.GetBranchID(), ChangeStatus.REFULLSYNC
								.getValue());
				_log.WriteError(super.GetName(), SyncActivityLogMsg
						.getSyncincsyncdbischangedMsg());
				return;
			}

			ConfigurationOperator
					.debugMessage("Incremental Data Transfer End.");
			/*
			 * _log.WriteInformation(super.GetName(), SyncActivityLogMsg
			 * .getSyncgdbincdatatransferendMsg());
			 */
			
			ImportIncDataToDataBase importIncData = new ImportIncDataToDataBase(
					super.GetBranchID());
			try {
				importIncData.ImportIncrementalData(transferDataResult.value);
				syncService.syncIncrementalEnd(syncTranInfo.value,
						importIncData.getlastSuccessTranID());
			} catch (SyncDB_Exception e) {
				_log.WriteError(super.GetName(), SyncActivityLogMsg
						.getSyncincsyncwarningMsg());
				ConfigurationOperator.errorMessage(e.getMessage(), e);
				
				/*
				 * Set max failed times to avoid 
				 * fail continue print error and never end.
				 */
				if(failTimes++ >= MAXFAILTIMES)
					break;
			}

			ConfigurationOperator
					.debugMessage("IncSync Successful, last successful tran id: "
							+ importIncData.getlastSuccessTranID());
		} while (transferDataResult.value.length > 0);

		_log.WriteInformation(super.GetName(), SyncActivityLogMsg
				.getSyncincsyncendMsg());
		
		ASBUSyncUtil syncUtil = ASBUSyncUtil.getASBUSyncUtil(this.GetBranchID());
		syncUtil.UpdateFullSyncStatus(SyncStatus.FINISHED);
	}

}
