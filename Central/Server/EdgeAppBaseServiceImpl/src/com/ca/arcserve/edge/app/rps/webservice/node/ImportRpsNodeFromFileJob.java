package com.ca.arcserve.edge.app.rps.webservice.node;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.node.ImportNodeFromFileJob;

public class ImportRpsNodeFromFileJob extends ImportRpsNodesJob {
	
	private static final Logger logger = Logger.getLogger(ImportNodeFromFileJob.class);
	
	@Override
	protected int importSingle(NodeRegistrationInfo node) {
		
		//check exists
		int hostId = rpsNodeService.getNode_Id(node.getGatewayId().getRecordId(), node.getNodeName(), null); //ipaddress will be discarded in future
		if (hostId > 0){
			String message = EdgeCMWebServiceMessages.getResource("importNodeFromFileExist" , node.getNodeName());
			logger.debug(message);
			rpsNodeService.addActivityLogForImportNodes(Severity.Warning, type, message);
			return hostId;
		}else{
			return super.importSingle(node);
		}
	}

}
