package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupLocationInfo;

public interface IEdgeLinuxPlanService {
	
	public List<String> getPrepostScriptList(int linuxD2DServerId) throws EdgeServiceFault;
	
	public boolean validateBackupLocation(int linuxD2DServerId,LinuxBackupLocationInfo locationInfo) throws EdgeServiceFault;

}
