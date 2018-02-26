package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResultForLinux;

public interface IEdgeLinuxNodeService {

	RegistrationNodeResult registerLinuxNode(NodeRegistrationInfo registrationNodeInfo,boolean isForce) throws EdgeServiceFault;
	
	RegistrationNodeResult registerLinuxD2DServer(NodeRegistrationInfo registrationNodeInfo,boolean isForce,boolean isClearExistingData) throws EdgeServiceFault;
	
	RegistrationNodeResult validateLinuxNode(NodeRegistrationInfo d2dServer,NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault;
	
	RegistrationNodeResultForLinux validateLinuxD2DServer(NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault;
	
	int backupLinuxNode(int nodeId,String nodeName, int backupType) throws EdgeServiceFault;
	
	//linux log collection
	int collectDiagnosticInfo(DiagInfoCollectorConfiguration diagObj, int hostid, boolean isLinuxBackupServer, String authUUID, String hostname);
	
	int cancelLinuxJob(int nodeId,String jobUUID) throws EdgeServiceFault;
	
	HostConnectInfo getLinuxD2DServerInfoByHostId(int hostId) throws EdgeServiceFault;
	
	boolean checkLinuxD2DServerCanBeDeleted(int[] node);

	int validateManaged(NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault;

	String getLinuxVersionInfo(int nodeId) throws EdgeServiceFault;
}
