package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeGDBSyncStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSyncDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryApplication;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfBranchSiteInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.BranchSiteInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ASBUSyncUtil;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class GDBSyncObject {
	private List<BranchSiteInfo> branchList = null;
	private boolean fullSyncFlag = false;
	private int _retryTimes;
	private int _retryInterval;
	private IEdgeConnectInfoDao edao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeSyncDao syncDao = DaoFactory.getDao(IEdgeSyncDao.class);
	private ASBUJobInfo jobinfo = null;
	private static final Logger logger = Logger.getLogger(GDBSyncObject.class);
	
	public ASBUJobInfo getJobinfo() {
		return jobinfo;
	}

	public void setJobinfo(ASBUJobInfo jobinfo) {
		this.jobinfo = jobinfo;
	}

	public boolean isFullSyncFlag() {
		return fullSyncFlag;
	}

	public void setFullSyncFlag(boolean fullSyncFlag) {
		this.fullSyncFlag = fullSyncFlag;
	}

	public List<BranchSiteInfo> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<BranchSiteInfo> branchList) {
		this.branchList = branchList;
	}

	private Hashtable<Integer, Integer> branchHostID2GdbIDTable = new Hashtable<Integer, Integer>();

	private int gdbHostID = 0;
	public int getGdbRhostid() {
		return gdbHostID;
	}

	public void setGdbRhostid(int gdbHostID) {
		this.gdbHostID = gdbHostID;
	}

	private String gdbUrl = null;
	public String getGdbUrl() {
		return gdbUrl;
	}

	public void setGdbUrl(String gdbUrl) {
		this.gdbUrl = gdbUrl;
	}

	public GDBSyncObject() {
		// nothing to do
	}

	public int get_retryTimes() {
		return _retryTimes;
	}

	public void set_retryTimes(int retryTimes) {
		_retryTimes = retryTimes;
	}

	public int get_retryInterval() {
		return _retryInterval;
	}

	public void set_retryInterval(int retryInterval) {
		_retryInterval = retryInterval;
	}

	private void UpdateGDBServerInfo(boolean isLocalBranch) {
		List<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
		edao.as_edge_arcserve_connect_info_list(gdbHostID, infos);
		int gdb_branchid = gdbHostID;

		if (!isLocalBranch)
			gdb_branchid = 0;

		edao.as_edge_arcserve_connect_info_update_gdb(gdbHostID, infos.get(0)
				.getCauser(), infos.get(0).getCapasswd(), infos.get(0)
				.getAuthmode(), infos.get(0).getProtocol(), infos.get(0)
				.getPort(), infos.get(0).getType(), infos.get(0).getVersion(),
				gdb_branchid, infos.get(0).getManaged());
	}

/*	public void UpdateBranchSiteInfoList() {
		// enum all branch info of specific GDB from database
		List<EdgeArcserveConnectInfo> orgBranchInfoList = new ArrayList<EdgeArcserveConnectInfo>();
		edao.as_edge_arcserve_connInfo_list_by_gdbbranchid(gdbHostID,
				orgBranchInfoList);

		List<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
		edao.as_edge_arcserve_connect_info_list(gdbHostID, infos);
		orgBranchInfoList.add(infos.get(0));

		for (EdgeArcserveConnectInfo eaci : orgBranchInfoList) {
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			edao.as_edge_host_list(eaci.getHostid(),
					ConfigurationOperator._IsVisible, hosts);

			int[] ids = new int[1];
			for (BranchSiteInfo bsi : branchList) {
				if (hosts.size() > 0) {
					if (bsi.getServerName().equalsIgnoreCase(
							hosts.get(0).getRhostname())) {

						if (eaci.getHostid() == gdbHostID) {
							UpdateGDBServerInfo(true);
							ids[0] = gdbHostID;
						}

						else if (IsGDBTask(eaci.getType())) {
							// update branch list into database
							edao.as_edge_host_update(eaci.getHostid(), hosts
									.get(0).getTopologyid(), hosts.get(0)
									.getMachinetype(), hosts.get(0)
									.getNetworktype(),
									hosts.get(0).getOstype(), Calendar
											.getInstance().getTime(), bsi
											.getServerName(), hosts.get(0)
											.getAppName(), bsi.getIp(), hosts
											.get(0).getOsdesc(), hosts.get(0)
											.getOsver(), hosts.get(0)
											.getHwtype(), hosts.get(0)
											.getRhostsflags(), hosts.get(0)
											.getTier(), bsi.getMajorVersion(),
									bsi.getMinorVersion(),
									bsi.getServicePack(), bsi.getBuildNumber(),
									hosts.get(0).getIsVisible(), hosts.get(0)
											.getAppStatus(), hosts.get(0)
											.getServerPrincipalName(), ids);
						}

						branchHostID2GdbIDTable.put(bsi.getId(), ids[0]);
						break;
					}
				}
			}

			// the branch in database doesn't exist in the retrieved branch list
			// clean up it
			if (ids[0] == 0) {
				if (eaci.getHostid() == gdbHostID) {
					UpdateGDBServerInfo(false);
				} else {
					// delete branch which doesn't in the current branch site
					// list
					edao.as_edge_host_remove(eaci.getHostid());

					// delete the GDB server and branch site relationship
					edao.as_edge_arcserve_connect_remove(eaci.getHostid());
				}
				// delete the synchronized data
				edao.as_edge_sync_delete_branch(eaci.getHostid());
			}
		}

		// insert or update the new branch info into database
		for (int j = 0; j < branchList.size(); j++) {
			Integer rhostid = branchHostID2GdbIDTable.get(branchList.get(j)
					.getId());
			if (rhostid == null) {
				rhostid = 0;
			} else {
				continue;
			}

			BranchSiteInfo bsi = branchList.get(j);
			int[] ids = new int[1];
			edao.as_edge_host_update(rhostid, 0, 0, 0, 0, Calendar
					.getInstance().getTime(), bsi.getServerName(), "", bsi
					.getIp(), "", "", "", 0, 0, bsi.getMajorVersion(), bsi
					.getMinorVersion(), bsi.getServicePack(), bsi
					.getBuildNumber(), ConfigurationOperator._IsVisible, 0, "",
					ids);

			branchHostID2GdbIDTable.put(branchList.get(j).getId(), ids[0]);

			edao.as_edge_arcserve_connect_info_update_gdb(ids[0], "", "", 0, 0,
					0, ABFuncServerType.BRANCH_PRIMARY.ordinal(), bsi
							.getMajorVersion()
							+ "." + bsi.getMinorVersion(), gdbHostID, 0);

			if (edao.as_edge_arcserve_gdb_can_branch_become_validate(rhostid,
					gdbHostID) == 0)
				branchHostID2GdbIDTable.remove(branchList.get(j).getId());
			else
				edao.as_edge_arcserve_gdb_update_branch_flags(rhostid,
						gdbHostID, 1);
		}
	}*/

	public void UpdateBranchSiteInfoList() {
		List<EdgeArcserveConnectInfo> orgBranchInfoList = new ArrayList<EdgeArcserveConnectInfo>();
		edao.as_edge_arcserve_connInfo_list_by_gdbbranchid(gdbHostID,
				orgBranchInfoList);

		List<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
		edao.as_edge_arcserve_connect_info_list(gdbHostID, infos);
		orgBranchInfoList.add(infos.get(0));

		for (BranchSiteInfo bsi : branchList) {
			boolean isNewBranch = true;
			for (EdgeArcserveConnectInfo eaci : orgBranchInfoList) {
				List<EdgeHost> hosts = new ArrayList<EdgeHost>();
				hostDao.as_edge_host_list(eaci.getHostid(),
						ConfigurationOperator._IsVisible, hosts);
				if (hosts.size() <= 0)
					continue;
				if (bsi.getServerName().equalsIgnoreCase(
						hosts.get(0).getRhostname())) {

					if (IsGDBTask(eaci.getType())) {
						if (eaci.getHostid() == gdbHostID) {
							UpdateGDBServerInfo(true);
							hostDao.as_edge_host_update_timezone_by_id(gdbHostID, bsi.getUTCOffset());
						} else {
							UpdateBranchList(eaci, bsi, hosts.get(0));
						}
						branchHostID2GdbIDTable.put(bsi.getId(), eaci
								.getHostid());
					}

					isNewBranch = false;
					break;
				}
			}

			// insert or update the new branch info into database
			if (isNewBranch) {
				int[] ids = new int[1];
				int[] hostId = new int[1];
				int hostAppType = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_BACKUP
						.getValue();
				List<EdgeHost> hosts = new ArrayList<EdgeHost>();
				hostDao.as_edge_host_getIdByHostname(bsi.getServerName(),
						hostId);
				if (hostId != null && hostId[0] != 0) {
					hostDao.as_edge_host_list(hostId[0],
							ConfigurationOperator._IsVisible, hosts);
					if (!hosts.isEmpty()) {
						hostAppType = hostAppType | hosts.get(0).getAppStatus();
					}
				}

				String hostName = bsi.getServerName();
				if(!StringUtil.isEmptyOrNull(hostName))
					hostName = hostName.toLowerCase();
				
//				List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
//				String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
//              have no gateway information, so can not know the true fqdn name
				
				hostDao.as_edge_host_update(0, Calendar
						.getInstance().getTime(), hostName, "", 
						bsi.getIp(), "","", ConfigurationOperator._IsVisible, hostAppType, "",
						HostType.EDGE_NODE_PHYSICS_MACHINE.getValue(), ProtectionType.WIN_D2D.getValue(), "", ids);
				
				hostDao.as_edge_host_update_timezone_by_id(ids[0], bsi.getUTCOffset());

				branchHostID2GdbIDTable.put(bsi.getId(), ids[0]);

				edao.as_edge_arcserve_connect_info_update_gdb(ids[0], "", "",
						0, 0, 0, ABFuncServerType.BRANCH_PRIMARY.ordinal(), bsi
								.getMajorVersion()
								+ "." + bsi.getMinorVersion(), gdbHostID, 0);


				if (edao.as_edge_arcserve_gdb_can_branch_become_validate(
						ids[0], gdbHostID) == 0)
					branchHostID2GdbIDTable.remove(bsi.getId());
				else
					edao.as_edge_arcserve_gdb_update_branch_flags(ids[0],
							gdbHostID, 1);
			}

		}


		// the branch in database doesn't exist in the retrieved branch list
		// clean it
		for (EdgeArcserveConnectInfo eaci : orgBranchInfoList) {
			if (branchHostID2GdbIDTable.containsValue(eaci.getHostid()))
				continue;

			if (IsGDBTask(eaci.getType())
					&& edao.as_edge_arcserve_gdb_can_branch_become_validate(
							eaci.getHostid(), gdbHostID) != 0) {
				if (eaci.getHostid() == gdbHostID) {
					UpdateGDBServerInfo(false);
				} else {
					// delete branch which doesn't in the current branch site
					// list
					hostDao.as_edge_host_remove(eaci.getHostid());
					logger.info("GDBSyncObject.UpdateBranchSiteInfoList(): delete node, nodeId:"+ eaci.getHostid());
					// delete the GDB server and branch site relationship
					edao.as_edge_arcserve_connect_remove(eaci.getHostid());
				}
				syncDao.as_edge_sync_delete_branch(eaci.getHostid());
			}
		}

	}

	public boolean Sync() {

		try {
			// Populate GDB branch server list
			IMySyncService syncService = SyncServiceFactory.createSyncService(this.gdbUrl, this.getGdbRhostid());
			ArrayOfBranchSiteInfo aobsi = syncService.enumBranchServer();
			branchList = aobsi.getBranchSiteInfo();
			if (branchList == null || branchList.isEmpty()) {
				ConfigurationOperator.debugMessage(this.getClass().getName()
						+ ": Get branch server from GDB server failed.");
				return false;
			}

			// Update branch server into database
			UpdateBranchSiteInfoList();

			if (branchHostID2GdbIDTable.isEmpty()) {
				ConfigurationOperator.debugMessage(this.getClass().getName()
						+ ": branchHostID2GdbIDTable is empty");
				return false;
			}

			// Create branch sync task and put in Execute Task Queue
			EdgeTask gdbSyncTask = null;

			if ((gdbSyncTask = ConfigurationOperator.GetEdgeTask()) == null) {
				ConfigurationOperator.debugMessage(this.getClass().getName()
						+ ": EdgeTaskInit failed");
				return false;
			}

			for (BranchSiteInfo bsi : branchList) {
				// Always to create sync task by flag
				if(!branchHostID2GdbIDTable.containsKey(bsi.getId()))
					continue;

				BranchSyncTask task = BranchSyncTaskFactory
						.Create(fullSyncFlag ? EdgeGDBSyncStatus
								.toInt(EdgeGDBSyncStatus.GDB_Full_Sync_Failed) // full
								// sync
								: bsi.getFullSyncStatus()); // incremental sync
				if (task != null) {
					InitSyncTask(task, bsi);
					gdbSyncTask.AddToWaitingQueue(task);
				} else {
					ConfigurationOperator.debugMessage(this.getClass()
							.getName()
							+ ": Invalied sync type - "
							+ bsi.getFullSyncStatus());
				}
			}
			ConfigurationOperator.debugMessage(this.getClass().getName()
					+ ": GDB branch sync is ready...");
			ASBUSyncUtil syncUtil = ASBUSyncUtil.getASBUSyncUtil(gdbHostID);
			syncUtil.UpdateFullSyncStatus(SyncStatus.FINISHED);
			return true;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	private void InitSyncTask(BranchSyncTask task, BranchSiteInfo bsi) {

		if (task != null) {
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			hostDao.as_edge_host_list(gdbHostID, ConfigurationOperator._IsVisible, hosts);
			if(!hosts.isEmpty())
				task.setGdbServerInfo(hosts.get(0));
			task.setBranchHostID(branchHostID2GdbIDTable.get(bsi.getId()));
			task.setBranchSiteInfo(bsi);
			task.setGdbUrl(this.gdbUrl);
			task.set_retryInterval(_retryInterval);
			task.set_retryTimes(_retryTimes);
			task.setJobInfo(jobinfo);
		} else {
			ConfigurationOperator.debugMessage(this.getClass().getName()
					+ ": Invalied sync type - " + bsi.getFullSyncStatus());
		}
	}

	private boolean IsGDBTask(int type) {
		if (type != ABFuncServerType.BRANCH_PRIMARY.ordinal()
				&& type != ABFuncServerType.GDB_PRIMARY_SERVER.ordinal())
			return false;
		return true;
	}

	private int[] UpdateBranchList(EdgeArcserveConnectInfo eaci,
			BranchSiteInfo bsi, EdgeHost host) {
		int[] ids = new int[1];
		
		String hostName = bsi.getServerName();
		if(!StringUtil.isEmptyOrNull(hostName))
			hostName = hostName.toLowerCase();
		
//		List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
//		String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
//      have no gateway information, so cannot know the fqdn name
		
		hostDao.as_edge_host_update(
						eaci.getHostid(),
						Calendar.getInstance().getTime(),
						hostName,
						host.getNodeDescription(),
						bsi.getIp(),
						host.getOsdesc(),
						host.getOstype(),
						host.getIsVisible(),
						host.getAppStatus()
						| DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_BACKUP.getValue(), 
						host.getServerPrincipalName(),
						HostType.EDGE_NODE_PHYSICS_MACHINE.getValue(),
						host.getProtectionTypeBitmap(),
						"",
						ids);
		hostDao.as_edge_host_update_timezone_by_id(eaci.getHostid(), bsi
				.getUTCOffset());
		return ids;
	}
}
