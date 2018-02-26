package com.ca.arcserve.edge.app.base.webservice.test;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

/**
 * NodeService test case
 * 
 * @author zhati04
 *
 */
public class NodeServiceTestCase extends AbstractTestCase{
	
	private static final Logger logger = Logger.getLogger(NodeServiceTestCase.class);
	private static final INodeService service = new NodeServiceImpl();
	
	@Test
	public void testGetNodeListByIDs(){
		try {
			List<Node> nodes = service.getNodeListByIDs(Arrays.asList(28));
			if(CollectionUtils.isNotEmpty(nodes)){
				for(Node node : nodes){
					logger.debug("Hostname is " + node.getHostname());
					logger.debug("Protocol is " + Protocol.parse(node.getD2dProtocol()));
					logger.debug("Port is " + node.getD2dPort());
					logger.debug("UUID is " + node.getD2DUUID());
					logger.debug("Instance UUID is " + node.getVmInstanceUUID());
					logger.debug("AUTH UUID is " + node.getAuthUUID());
					logger.debug("Hypervisor server hostname is " + node.getHyperVisor());
				}
			}
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(),e);
		}
	}

}
