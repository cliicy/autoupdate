package com.ca.arcserve.edge.app.base.common.connection;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.asbu.webservice.ASBUServiceImpl;
import com.ca.arcserve.edge.app.asbu.webservice.IASBUService;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;

public class ASBUConnectionContextProvider implements IConnectionContextProvider {
	private static final Logger logger = Logger.getLogger(ASBUConnectionContextProvider.class);
	private IASBUService asbuService = new ASBUServiceImpl();
	private int nodeId;
	public ASBUConnectionContextProvider(int nodeId){
		if(logger.isDebugEnabled()){
			logger.debug("Constrct ASBUConnectionContextProvider which Node id is " + nodeId);
		}
		this.nodeId = nodeId;
	}
	@Override
	public ConnectionContext create() throws EdgeServiceFault {
		if(logger.isDebugEnabled()){
			logger.debug("Create ASBU ConnectionContext which node id is " + nodeId);
		}
		return asbuService.getASBUConnectInfo(nodeId);
	}

	@Override
	public void updateUuid(String nodeUuid, String authUuid) {
		throw new RuntimeException("ASBU cannot support uuid login now, so it no need to update uuid");
	}

}
