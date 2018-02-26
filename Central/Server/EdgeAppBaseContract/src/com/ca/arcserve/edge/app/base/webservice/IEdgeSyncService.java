package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import javax.jws.WebService;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.GDBBranchInfo;
@WebService(targetNamespace="http://webservice.sync.edge.arcserve.ca.com/")
public interface IEdgeSyncService {
	Boolean InvokeFullARCserveSync(int[] rhostID)  throws EdgeServiceFault;
	Boolean InvokeIncARCserveSync(int[] rhostID,boolean bIsAutoConvert) throws EdgeServiceFault;
	List<GDBBranchInfo> enumBranchServer(int rhostID) throws EdgeServiceFault;
	Boolean unRegisterBranchServer(int rhostID, String serverName) throws EdgeServiceFault;
	Integer[] GetInvalidHost(int[] rhostID) throws EdgeServiceFault;
	boolean checkARCserveNodeManageStatus(String guid) throws EdgeServiceFault;
	public void submitARCserveFullSyncForGroup(int groupID, int groupType) throws EdgeServiceFault;
	public void submitARCserveIncrementalSyncForGroup(int groupID, int groupType) throws EdgeServiceFault;
	public void resyncRecoveryPointSummaryInfo() throws EdgeServiceFault;
}
