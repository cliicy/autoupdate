package com.ca.arcserve.edge.app.base.webservice.syncmonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;

public class EdgeNodesMap {


	public static  synchronized EdgeNodesMap getInstance () {
		if (instance == null) {
			instance = new EdgeNodesMap();
		}

		return instance;
	}

	public EdgeNodesMap() {
		nodeMap = new Hashtable<Integer, EdgeNodesMapItem>();
	}

	/**
	 * @return the configuration
	 */
	public IEdgeSyncMonitorConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(IEdgeSyncMonitorConfiguration configuration) {
		this.configuration = configuration;
	}

	public void refresh() {
		// Retrieve all edge managed nodes
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		m_idao.as_edge_host_list(0, 1, hosts);
		if (hosts.isEmpty()) {
			// Log no host
			_log.debug("[EdgeNodesMap] no host info.");
			return;
		}

		for(EdgeHost host : hosts) {

			EdgeNodesMapItem item = new EdgeNodesMapItem();

			// check component
			if (hasD2DComponent(host.getRhostid())) {
				item.setComponent(EdgeSyncComponents.ARCserve_D2D);
			}

			if (hasASBUComponent(host.getRhostid())) {
				item.setComponent(EdgeSyncComponents.ARCserve_Backup);
			}

			if (item.getComponent(0) == null) {
				nodeMap.remove(host.getRhostid());
				_log.debug("[EdgeNodesMap] no D2D or ASBU on this node : " + host.getRhostname());
				continue;
			}

			// put them in the map
			item.setHost(host);

			if (!nodeMap.containsKey(host.getRhostid())) {
				nodeMap.put(host.getRhostid(), item);

				_log.debug("[EdgeNodesMap] add new node : " + host.getRhostname());
			} else {
				EdgeNodesMapItem oldItem = nodeMap.get(host.getRhostid());
				item.setLastSendAlertSucceedTime(oldItem.getLastSendAlertSucceedTime());
				nodeMap.remove(host.getRhostid());
				nodeMap.put(host.getRhostid(), item);

				_log.debug("[EdgeNodesMap] update node : " + host.getRhostname());
			}
		}

		// cleanup the not existed host in the node map
		boolean removeFlag = true;
		List<Integer> toRemove = new ArrayList<Integer>();
		for (Integer hostId : nodeMap.keySet()) {
			removeFlag = true;
			for(EdgeHost host : hosts) {
				if (host.getRhostid() == hostId) {
					removeFlag = false;
					break;
				}
			}

			if (removeFlag) {
				toRemove.add(hostId);
			}
		}
		for(Integer hostId:toRemove){
			nodeMap.remove(hostId);
			_log.debug("[EdgeNodesMap] remove node from nodeMap, ID : " + hostId);
		}
	}



	/*-
	 * check all nodes sync status, if some nodes match the alert condition,
	 * raise a sync alert
	 */
	public void checkAll() {

			for (EdgeNodesMapItem item : nodeMap.values()) {
				try {
					EdgeSyncChecker checker = new EdgeSyncChecker(configuration);
					checker.bindHost(item.getHost(), item.getAllComponents());
					checker.setLastSendAlertTime(item.getLastSendAlertSucceedTime());

					// there can be enhanced to multi thread
					checker.checkStatus();
					item.setLastSendAlertSucceedTime(checker.getLastSendAlertTime());
				} catch (Exception e) {
					// Log error
					_log.debug("[EdgeNodesMap] check all nodes happen exception. " + e.getMessage());
				}

			}
	}

	public boolean hasD2DComponent(int hostId) {
		List<EdgeConnectInfo> connectInfos = new ArrayList<EdgeConnectInfo>();
		m_condao.as_edge_connect_info_list(hostId, connectInfos);
		return (!connectInfos.isEmpty()
				&& connectInfos.get(0).getManaged() == NodeManagedStatus.Managed.ordinal());
	}

	public boolean hasASBUComponent(int hostId) {
		List<EdgeArcserveConnectInfo> connectInfos = new ArrayList<EdgeArcserveConnectInfo>();
		m_condao.as_edge_arcserve_connect_info_list(hostId, connectInfos);
		return (!connectInfos.isEmpty()
				&& connectInfos.get(0).getManaged() == NodeManagedStatus.Managed.ordinal());
	}

	public int getNodesCountInMap() {
		return nodeMap.size();
	}

	private static EdgeNodesMap instance = null;
	private static IEdgeHostMgrDao m_idao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static IEdgeConnectInfoDao m_condao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private Hashtable<Integer, EdgeNodesMapItem> nodeMap;
	private IEdgeSyncMonitorConfiguration configuration;
	private static Logger _log = Logger.getLogger(EdgeNodesMap.class);

}
