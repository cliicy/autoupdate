package com.ca.arcserve.edge.app.base.webservice.action;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ManageMultiNodesParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;

public class ManageNodeTaskRunner extends AbstractTaskRunner<Integer>{
	private static final Logger logger = Logger.getLogger(ManageNodeTaskRunner.class);
	protected NodeDetail node;
	public ManageNodeTaskRunner(int nodeId, ActionTaskParameter<Integer> parameter, CountDownLatch doneSignal, ActionTaskManager<Integer> manager){
		super(nodeId, parameter, doneSignal, manager);
	}

	@Override
	protected void excute() {
		try {
			node = nodeService.getNodeDetailInformation(entityKey);
			NodeRegistrationInfo nodeRegistrationInfo = new NodeRegistrationInfo();
			
			RemoteNodeInfo nodeInfo = new RemoteNodeInfo();
			nodeInfo.setD2DInstalled(node.isD2dInstalled());
			nodeInfo.setARCserveBackInstalled(node.isArcserveInstalled());
			nodeRegistrationInfo.setNodeInfo(nodeInfo);
			
			nodeRegistrationInfo.setId(node.getId());
			nodeRegistrationInfo.setD2dProtocol(Protocol.parse(node.getD2dProtocol()));
			nodeRegistrationInfo.setNodeName(node.getHostname());
			nodeRegistrationInfo.setD2dPort(Integer.parseInt(node.getD2dPort()));
			nodeRegistrationInfo.setUsername(node.getUsername());
			nodeRegistrationInfo.setPassword(node.getPassword());
			GatewayEntity gateway = gatewayService.getGatewayByHostId( node.getId() );
			nodeRegistrationInfo.setGatewayId(gateway.getId());
			nodeRegistrationInfo.setRegisterD2D(node.isD2dInstalled());
			
			nodeRegistrationInfo.setRegisterARCserveBackup(node.isArcserveInstalled());
			if(node.getArcserveConnectInfo() != null){
				nodeRegistrationInfo.setAbAuthMode(node.getArcserveConnectInfo().getAuthmode());
				nodeRegistrationInfo.setCarootPassword(node.getArcserveConnectInfo().getCapasswd());
				nodeRegistrationInfo.setCarootUsername(node.getArcserveConnectInfo().getCauser());
				nodeRegistrationInfo.setArcservePort(node.getArcserveConnectInfo().getPort());
				nodeRegistrationInfo.setArcserveProtocol(node.getArcserveConnectInfo().getProtocol());
			}
			ManageMultiNodesParameter manageParameter = (ManageMultiNodesParameter)parameter;
			nodeService.markNodeAsManaged(nodeRegistrationInfo, manageParameter.isForceManage());
			addSucceedEntities(entityKey);
		} catch (Exception e) {
			logger.error("[ManageNodeTaskRunner] excute() failed.", e);
			String nodeName = node.getHostname();
			if(StringUtil.isEmptyOrNull(nodeName))
				nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmName());
			long logId = NodeExceptionUtil.generateActivityLogByException(Module.ManageMultipleNodes,node,"updateMultiNodes_Log", e);
			addFailedEntities(entityKey,logId);
		}
	}

}
