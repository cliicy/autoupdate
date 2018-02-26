package com.ca.arcserve.edge.app.asbu.webservice;

import java.util.List;

import com.arcserve.asbu.webservice.archive2tape.udp.ASBUInfo;
import com.arcserve.asbu.webservice.archive2tape.udp.ASBUStatus;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUDeviceInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaGroupInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaPoolSet;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUSyncResult;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.DeleteASBUBackupServerResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ArcserveConnectInfo;

public interface IASBUService {
	ASBUSyncResult createOrUpdateASBUServers(ArcserveConnectInfo connectInfo, String hostName) throws EdgeServiceFault;
	
	List<DeleteASBUBackupServerResult> deleteASBUDomain(int domainId) throws EdgeServiceFault;
	
	List<ASBUServerInfo> getASBUServerList(GatewayId gatewayId, int domainId) throws EdgeServiceFault;
	
	List<ASBUServerInfo> getASBUServerListWithoutGroup(GatewayId gatewayId, int domainId) throws EdgeServiceFault;
	
	List<ASBUMediaGroupInfo> getASBUMediaGroupList(int serverId) throws EdgeServiceFault;
	
	int getASBUPlanCount(int serverId, String mediaGroupName) throws EdgeServiceFault;
	
	List<ASBUMediaInfo> getASBUMediaList(int serverId, int groupNum) throws EdgeServiceFault;
	
	List<ASBUMediaPoolSet> getASBUMediaPoolSet(int serverId, String groupName)throws EdgeServiceFault;
	
	ASBUStatus checkASBUStatus(ASBUInfo info) throws EdgeServiceFault;
	
	int checkPlanStatus(String d2dUuid, String policyUuid, boolean justcheck) throws EdgeServiceFault;
	
	List<ASBUDeviceInformation> getASBUDeviceList(int serverId, int groupNum) throws EdgeServiceFault;
	
	ConnectionContext getASBUConnectInfo(int nodeId) throws EdgeServiceFault;
	
	ASBUServerStatusInfo getAsbuServerStatus(int serverId) throws EdgeServiceFault;
}
