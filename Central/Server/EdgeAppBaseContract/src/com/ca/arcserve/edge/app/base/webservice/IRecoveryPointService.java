package com.ca.arcserve.edge.app.base.webservice;

import java.util.Date;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.RecoveryPointWithNodeInfo;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.SharedFolderBrowseParam;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.MachineConfigure;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;

public interface IRecoveryPointService {
	List<RecoveryPoint>  getRecoveryPointsByTimePeriod(int rpsNodeId , ProtectedNodeInDestination node,Date beginTime,Date endTime)throws EdgeServiceFault;
	RecoveryPointItem[] getRecoveryPointItems(RecoveryPoint recoveryPoint, int rpsNodeId, ProtectedNodeInDestination node) throws EdgeServiceFault;
	boolean validateRecoveryPointPassword( RecoveryPointInformationForCPM recoveryPoint ) throws EdgeServiceFault;
	MachineConfigure getRecoveryPointMachineConfig( RecoveryPointInformationForCPM recoveryPoint ) throws EdgeServiceFault;
	/**
	 * If protectedNode attribution  = empty, this API  return all recovery_points in a shared destination
	 * if protectedNode attribution in SharedFolderBrowseParam in not null; the recovery points of this node  will return,
	 * @param param; 
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws EdgeServiceFault
	 */
	List<RecoveryPointWithNodeInfo> getGroupedRecoveryPointsFromSharedFolder( SharedFolderBrowseParam param,  Date beginTime,Date endTime )  throws EdgeServiceFault;
	/**
	 *both sharedFolder and protectedNode attribution in SharedFolderBrowseParam cannot empty!
	 */
	List<RecoveryPoint> getLinuxRecoveryPoints( SharedFolderBrowseParam param,Date startDate,Date endDate ) throws EdgeServiceFault;
	
	List<RecoveryPoint>  getRecoveryPointsByNodeList(int rpsNodeId , List<ProtectedNodeInDestination> nodeList,Date beginTime,Date endTime)throws EdgeServiceFault;
}
