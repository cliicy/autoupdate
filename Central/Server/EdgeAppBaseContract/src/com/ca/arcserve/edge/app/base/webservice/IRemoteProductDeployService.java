package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.DeployStatusInfo;

//This service will run in the msp remote proxy for MSP case
public interface IRemoteProductDeployService {
	void startDeployProcess(DeployTargetDetail target)throws EdgeServiceFault;
	DeployStatusInfo getDeployStatus(DeployTargetDetail target)throws EdgeServiceFault;
	int getDeployProcessExitValue(DeployTargetDetail target)throws EdgeServiceFault;
	String getTargetUUID(DeployTargetDetail target)throws EdgeServiceFault;
}
