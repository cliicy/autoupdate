package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VMInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.VCMConnectionInfo;


public interface IVSphereService extends com.ca.arcflash.webservice.toedge.IEdgeVSphereService {
	VMInfo getVMNodesFromVSphere() throws EdgeServiceFault;
	
	VMInfo getVMNodesFromVSphere2(VCMConnectionInfo vcmConnection) throws EdgeServiceFault;
}
