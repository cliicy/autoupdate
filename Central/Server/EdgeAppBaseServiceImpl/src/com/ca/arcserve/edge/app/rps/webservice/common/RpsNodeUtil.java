package com.ca.arcserve.edge.app.rps.webservice.common;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;

public class RpsNodeUtil {
	
	private static IRpsNodeDao nodeDao = DaoFactory.getDao(IRpsNodeDao.class);
	
	public static EdgeRpsNode getNodeById(int nodeid){
		List<EdgeRpsNode> hosts = new ArrayList<EdgeRpsNode>();
		nodeDao.as_edge_rps_node_list(nodeid, hosts);
		if(hosts.isEmpty())
			return null;
		return hosts.get(0);
	}
	
	public static void buildRpsConnectInfo(RPSReplicationSettings settings) {
		if (settings == null || settings.getHostId() <= 0) {
			return;
		}
		
		List<EdgeRpsNode> rpsNodes = new ArrayList<EdgeRpsNode>();
		nodeDao.as_edge_rps_node_list(settings.getHostId(), rpsNodes);
		
		if (rpsNodes.isEmpty()) {
			return;
		}
		
		settings.setHostName(rpsNodes.get(0).getNode_name());
		settings.setUserName(rpsNodes.get(0).getUsername());
		settings.setPassword(rpsNodes.get(0).getPassword());
		settings.setProtocol(rpsNodes.get(0).getProtocol() == Protocol.Http.ordinal() ? 0 : 1);
		settings.setPort(rpsNodes.get(0).getPort());
		//defect 763907
		settings.setUuid(rpsNodes.get(0).getUuid());
	}
	
	public static void buildRpsConnectInfo(RpsHost host) {
		if (host == null || host.getRhostId() <= 0) {
			return;
		}
		
		List<EdgeRpsNode> rpsNodes = new ArrayList<EdgeRpsNode>();
		nodeDao.as_edge_rps_node_list(host.getRhostId(), rpsNodes);
		
		if (rpsNodes.isEmpty()) {
			return;
		}
		
		host.setRhostname(rpsNodes.get(0).getNode_name());
		host.setUsername(rpsNodes.get(0).getUsername());
		host.setPassword(rpsNodes.get(0).getPassword());
		host.setHttpProtocol(rpsNodes.get(0).getProtocol() == Protocol.Http.ordinal());
		host.setPort(rpsNodes.get(0).getPort());
		//defect 763907
		host.setUuid(rpsNodes.get(0).getUuid());
	}
	
}
