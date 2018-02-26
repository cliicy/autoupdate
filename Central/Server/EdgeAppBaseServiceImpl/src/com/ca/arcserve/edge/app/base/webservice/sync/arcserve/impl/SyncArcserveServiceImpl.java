package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.GDBBranchInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.BranchSiteInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.GetASBUServerInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.IASBUServerInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.SyncARCServerType;

public class SyncArcserveServiceImpl {
	public static final int _maxJobCount = 1;

	public static Boolean InvokeARCserveSync(ASBUJobInfo jobinfo, int rhostId, SyncARCServerType type,
			Boolean bIsFull) throws EdgeServiceFault {

		try {
			IASBUServerInfo serverInfo = GetASBUServerInfo
					.GetServiceInfo(rhostId);
			if (serverInfo == null)
				return false;

			if (serverInfo.get_serverType() == ABFuncServerType.NORNAML_SERVER
					.ordinal() || serverInfo.get_serverType() == ABFuncServerType.STANDALONE_SERVER.ordinal()) {
				if (!addRegularSyncTask(jobinfo, serverInfo, bIsFull))
					return false;

			} else if (serverInfo.get_serverType() == ABFuncServerType.GDB_PRIMARY_SERVER
					.ordinal()) {

				if (type == SyncARCServerType.GDB_SELF_PRIMARY)
					if (!addRegularSyncTask(jobinfo, serverInfo, bIsFull)) {
						ConfigurationOperator.errorMessage("Add GDB_PRIMARY Sync task faile");
					}

				GDBSyncObject gdbSync = GDBSyncFactory.Create(rhostId);
				gdbSync.setFullSyncFlag(bIsFull);
				gdbSync.set_retryInterval(serverInfo.get_retryInterval());
				gdbSync.set_retryTimes(serverInfo.get_retryTimes());
				gdbSync.setJobinfo(jobinfo);
				return gdbSync.Sync();
			} else {
				SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(jobinfo);
				_log.WriteError(serverInfo.get_serverName(), SyncActivityLogMsg.getSyncbranchcannotdofullsyncmsg());
			}
		} catch (Exception e) {
			ConfigurationOperator.debugMessage(e.getMessage(), e);
			throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.ARCserve_Sync_General, e.getMessage());
		}

		return true;
	}

	public static Boolean UnRegisterBranchServer(int rhostID, String serverName)
			throws EdgeServiceFault {
		Boolean bRet = false;
		try {
			IASBUServerInfo serverInfo = GetASBUServerInfo
					.GetServiceInfo(rhostID);
			String strUrl = serverInfo.get_URL();
			IMySyncService syncService = SyncServiceFactory.createSyncService(strUrl, rhostID);
			bRet = syncService.unRegisterBranchServer(serverName);
		} catch (Exception e) {
			ConfigurationOperator.debugMessage(e.getMessage(), e);
			throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.ARCserve_Sync_General, e.getMessage());
		}
		return bRet;
	}

	public static List<GDBBranchInfo> enumBranchServer(int rhostID)
			throws EdgeServiceFault {
		List<BranchSiteInfo> buffer = new LinkedList<BranchSiteInfo>();
		List<GDBBranchInfo> returnBuffer = new ArrayList<GDBBranchInfo>();
		try {
			IASBUServerInfo serverInfo = GetASBUServerInfo
					.GetServiceInfo(rhostID);
			String strUrl = serverInfo.get_URL();
			IMySyncService syncService = SyncServiceFactory.createSyncService(strUrl, rhostID);
			buffer = syncService.enumBranchServer().getBranchSiteInfo();
			for (BranchSiteInfo bsi : buffer) {
				GDBBranchInfo gdbbi = new GDBBranchInfo();
				gdbbi.setBuildNumber(bsi.getBuildNumber());
				gdbbi.setFullSyncStatus(bsi.getFullSyncStatus());
				gdbbi.setId(bsi.getId());
				gdbbi.setIp(bsi.getIp());
				gdbbi.setMajorVersion(bsi.getMajorVersion());
				gdbbi.setMinorVersion(bsi.getMinorVersion());
				gdbbi.setServerName(bsi.getServerName());
				gdbbi.setServicePack(bsi.getServicePack());
				gdbbi.setUuid(bsi.getUuid());
				gdbbi.setUTCOffset(bsi.getUTCOffset());
				returnBuffer.add(gdbbi);
			}

		} catch (Exception e) {
			ConfigurationOperator.debugMessage(e.getMessage(), e);
			throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.ARCserve_Sync_General, e.getMessage());
		}
		return returnBuffer;
	}

	private static Boolean addRegularSyncTask(ASBUJobInfo jobinfo, IASBUServerInfo serverInfo,
			Boolean bIsFull) throws InterruptedException {
		SyncGlitter item = null;
		if (bIsFull) {
			item = new SyncGlitterFull(serverInfo);
		} else {
			item = new SyncGlitterInc(serverInfo);
		}
		
		item.setJobinfo(jobinfo);

		EdgeTask arcserveSyncTask = null;

		if ((arcserveSyncTask = ConfigurationOperator.GetEdgeTask()) == null) {
			ConfigurationOperator
					.debugMessage("[InvokeARCserveSync] Can't Init Edge Task");
			return false;
		}

		if (arcserveSyncTask.getExecuteQueueSize() < _maxJobCount)
			arcserveSyncTask.AddToExecuteQueue(item);
		else {
			ConfigurationOperator.debugMessage("There is more than "
					+ _maxJobCount + " running. Add "
					+ serverInfo.get_serverName() + " to wait queue!");
			arcserveSyncTask.AddToWaitingQueue(item);
		}

		return true;
	}

}
