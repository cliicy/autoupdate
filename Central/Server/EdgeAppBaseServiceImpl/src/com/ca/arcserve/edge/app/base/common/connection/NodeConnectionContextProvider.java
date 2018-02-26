package com.ca.arcserve.edge.app.base.common.connection;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;

public class NodeConnectionContextProvider implements IConnectionContextProvider {
	private static Logger logger = Logger.getLogger(NodeConnectionContextProvider.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	private int nodeId;
	
	public NodeConnectionContextProvider(int nodeId) {
		this.nodeId = nodeId;
	}
	
	@Override
	public ConnectionContext create() throws EdgeServiceFault {
		EdgeHost host = getHost();
		EdgeConnectInfo connectInfo = getConnectInfo();
		
		String protocol = connectInfo.getProtocol() == 2 ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, host.getRhostname(), connectInfo.getPort());
		
		context.buildAuthUuid(connectInfo.getAuthUuid());
		context.buildCredential(connectInfo.getUsername(), connectInfo.getPassword(), "");
		
		GatewayEntity gateway = gatewayService.getGatewayByHostId(nodeId);
		logger.debug("[NodeConnectionContextProvider] the gateway id for the node: "+nodeId+" is "+gateway.getId());
		
		context.setGateway(gateway);
		
		return context;
	}
	
	private EdgeHost getHost() throws EdgeServiceFault {
		List<EdgeHost> outHost = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(nodeId, 1, outHost);
		
		if (outHost.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NOTFOUND, "Cannot find the node by id " + nodeId);
		}
		
		return outHost.get(0);
	}
	
	private EdgeConnectInfo getConnectInfo() throws EdgeServiceFault {
		List<EdgeConnectInfo> outConnectInfo = new ArrayList<EdgeConnectInfo>();
		connectInfoDao.as_edge_connect_info_list(nodeId, outConnectInfo);
		
		if (outConnectInfo.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NOTFOUND, "Cannot find the connection info by host id " + nodeId);
		}
		
		return outConnectInfo.get(0);
	}

	@Override
	public void updateUuid(String nodeUuid, String authUuid) {
		connectInfoDao.as_edge_connect_info_updateUuidByHostId(nodeId, nodeUuid, authUuid);
	}

}
