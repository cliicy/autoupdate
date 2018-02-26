package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import javax.xml.ws.Holder;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncTranInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncTranInfoTransferType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

//import javax.xml.ws.Holder;

//import com.ca.arcserve.edge.app.base.app.base.webservice.sync.arcserve.client.SyncTranInfo;
//import com.ca.arcserve.edge.app.base.app.base.webservice.sync.arcserve.client.SyncTranInfoTransferType;

public class BranchIncSyncTask extends BranchSyncTask {


	@Override
	public void run() {
		// TODO Auto-generated method stub
		/* TO-TEST*/
		SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(super.getJobInfo());
		String fmtMsg = String.format(SyncActivityLogMsg.getSyncgdbincstartMsg(),
							getGdbServerInfo().getRhostname(), getBranchSiteInfo().getServerName());
		ConfigurationOperator.debugMessage(fmtMsg);
		_log.WriteInformation(getBranchSiteInfo().getServerName(), fmtMsg);
	//	IEdgeSyncDao iDao = DaoFactory.getDao(IEdgeSyncDao.class);

		IMySyncService syncService = null;
		try {
			syncService = SyncServiceFactory.createSyncService(this.getGdbUrl(), this.getGdbServerInfo().getRhostid());
		} catch (Exception e) {
			fmtMsg = String.format(SyncActivityLogMsg.getSyncgdbincfailedMsg(),
					getGdbServerInfo().getRhostname(), getBranchSiteInfo().getServerName());
			ConfigurationOperator.debugMessage(fmtMsg);
			ConfigurationOperator.errorMessage(e.getMessage(), e);

			_log.WriteError(getBranchSiteInfo().getServerName(), fmtMsg);

			return;
		}

		Holder<SyncTranInfo> syncTranInfo = new Holder<SyncTranInfo>();
		syncTranInfo.value = new SyncTranInfo();

        syncTranInfo.value.setType(SyncTranInfoTransferType.GDB_SYNC);
        syncTranInfo.value.setBranchID((long)getBranchSiteInfo().getId());
		syncTranInfo.value.setMaxTransactionCount(10000);
		syncTranInfo.value.setMaxTransferCount(10000);
		syncTranInfo.value.setStartID(new Long(1));

		Holder<byte[]> transferDataResult = new Holder<byte[]>();

		try {
			ConfigurationOperator.debugMessage(SyncActivityLogMsg.getSyncgdbincdatatransferstartMsg());
			//_log.WriteInformation(getBranchSiteInfo().getServerName(), SyncActivityLogMsg.getSyncgdbincdatatransferstartMsg());

			ImportIncDataToDataBase importIncData = new ImportIncDataToDataBase(getBranchHostID());
			do {
				syncService.incrementalSyncDataTransfer(syncTranInfo, transferDataResult);;
				importIncData.ImportIncrementalData(transferDataResult.value);
				syncService.syncIncrementalEnd(syncTranInfo.value, importIncData.getlastSuccessTranID());
			} while(transferDataResult.value.length > 0);

			IEdgeHostMgrDao iHostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			iHostDao.as_edge_host_update_timezone_by_id(getBranchHostID(),
					syncTranInfo.value.getUTCOffset());

			ConfigurationOperator.debugMessage(SyncActivityLogMsg.getSyncgdbincdatatransferendMsg());
			//_log.WriteInformation(getBranchSiteInfo().getServerName(), SyncActivityLogMsg.getSyncgdbincdatatransferendMsg());

			fmtMsg = String.format(SyncActivityLogMsg.getSyncgdbincsucceedMsg(),
					getGdbServerInfo().getRhostname(), getBranchSiteInfo().getServerName());
			ConfigurationOperator
					.debugMessage("Last successful transaction ID: "
							+ importIncData.getlastSuccessTranID());
			_log.WriteInformation(getBranchSiteInfo().getServerName(), fmtMsg);
		} catch (SyncDB_Exception e) {
			fmtMsg = String.format(SyncActivityLogMsg.getSyncgdbincfailedMsg(),
					getGdbServerInfo().getRhostname(), getBranchSiteInfo().getServerName());
			ConfigurationOperator.debugMessage(fmtMsg);
			ConfigurationOperator.errorMessage(e.getMessage(), e);
			_log.WriteError(getBranchSiteInfo().getServerName(), fmtMsg);
		}
	}
}
