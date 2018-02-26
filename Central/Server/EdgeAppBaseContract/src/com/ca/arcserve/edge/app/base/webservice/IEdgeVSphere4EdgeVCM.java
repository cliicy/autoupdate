package com.ca.arcserve.edge.app.base.webservice;

import javax.jws.WebService;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.client.IBaseService;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VMInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.VCMConnectionInfo;
@WebService(targetNamespace="http://webservice.edge.arcserve.ca.com/")
public interface IEdgeVSphere4EdgeVCM extends IBaseService{
	VMInfo getVMNodesFromVSphere() throws EdgeServiceFault;
	VMInfo getVMNodesFromVSphere2(VCMConnectionInfo vcmConnection) throws EdgeServiceFault;
}
