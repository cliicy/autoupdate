package com.ca.arcserve.edge.app.base.webservice;


import java.util.Date;

import com.ca.arcflash.instantvm.HypervisorType;
import com.ca.arcflash.instantvm.PrecheckResult;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVHDOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.ProtectedNodeWithRecoveryPoints;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.RecoveryPointInfoForInstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.RecoveryServerResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVHDOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVMOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StopInstantVHDOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StopInstantVMOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.MonitorHyperVInfo;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;

public interface IInstantVMService {
	InstantVMOperationResult startInstantVM( StartInstantVMOperation operationPara )  throws EdgeServiceFault; 
	InstantVMOperationResult stopInstantVM( StopInstantVMOperation operationPara )  throws EdgeServiceFault; 
	InstantVMPagingResult getInstantVMPagingNodes(InstantVMPagingConfig config, InstantVMFilter filter);
	long powerOnIVM(String instantVMJobUUID, String ivmUUID) throws EdgeServiceFault;
	long powerOffIVM(String instantVMJobUUID, String ivmUUID) throws EdgeServiceFault;
	MonitorHyperVInfo getMonitorHyperVInfoFromConsole(HostConnectInfo monitorInfo) throws EdgeServiceFault;
	InstantVHDOperationResult startInstantVHD(StartInstantVHDOperation para) throws EdgeServiceFault;
	InstantVHDOperationResult stopInstantVHD(StopInstantVHDOperation para) throws EdgeServiceFault;
	RecoveryPointInfoForInstantVM getRecoveryPointInfo(int rpsNodeId, RecoveryPointInformationForCPM rp) throws EdgeServiceFault;
	ProtectedNodeWithRecoveryPoints getRecoveryPointsByNode(DestinationBrowser browser, ProtectedNodeInDestination node, Date beginTime, Date endTime) throws EdgeServiceFault;
	/*
	 * return code:
	 * 		0 not registered yet
	 * 		1 registered already with same Edge host
	 * 		2 registered with different Edge host
	 */
	int queryEdgeMgrStatusForNode(int nodeID) throws EdgeServiceFault;
	int saveVMWareInfoToDB(DiscoveryESXOption esxOption) throws EdgeServiceFault;
	int saveHyperVInfoToDB(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault;
//	PrecheckResult checkRecoveryServer(HypervisorType type, NodeRegistrationInfo regInfo)  throws EdgeServiceFault;
	RecoveryServerResult validateRecoveryServerConnectAndManage(
			HypervisorType type, Node agent,
			GatewayEntity gateway, boolean isLinux, boolean isRps,
			boolean isHyperV) throws EdgeServiceFault;
	RecoveryServerResult validateRecoveryServerAddRPSAndHypervNode(
			HypervisorType type, NodeDetail detail,
			GatewayEntity gateway, boolean isLinux, boolean isRps)
			throws EdgeServiceFault;
	RecoveryServerResult validateRecoveryServerUpdateNode(
			HypervisorType type, NodeDetail detail,
			GatewayEntity gateway, boolean isLinux) throws EdgeServiceFault;
	RecoveryServerResult validateRecoveryServerVersionAndInstall(
			HypervisorType type, NodeDetail detail,
			GatewayEntity gateway, boolean isLinux) throws EdgeServiceFault;
	RecoveryServerResult serverAndNFScheck(NodeRegistrationInfo regInfo,
			HypervisorType type, boolean addNode) throws EdgeServiceFault;
}
