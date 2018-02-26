package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;

public class ImportNodeFromFileJob extends ImportNodesJob {
	
	@Override
	protected void importAll() {
		List<Runnable> tasks = new ArrayList<Runnable>();
		
		for (final NodeRegistrationInfo node : nodes) {
			tasks.add(new Runnable() {
				
				@Override
				public void run() {
					importSingle(node);
				}
				
			});
		}
		
		EdgeExecutors.submitAndWaitTermination(tasks);
	}

	@Override
	protected int importSingle(NodeRegistrationInfo node) {
		
		//check exists
//		int hostId = nodeService.getHostId(node.getNodeName(), null); //ipaddress will be discarded in future
//		if (hostId > 0){
//			String message = EdgeCMWebServiceMessages.getResource("importNodeFromFileExist" , node.getNodeName());
//			logger.debug(message);
//			nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Warning, type, message);
//			return hostId;
//		}else{
//			return super.importSingle(node);
//		}
		return super.importSingle(node);
	}
}
