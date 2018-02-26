package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeAppInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgePreferenceConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ExternalLinks;

public interface IEdgeCommonService {
	EdgeVersionInfo getVersionInformation() throws EdgeServiceFault; 
	EdgeAppInfo getAppInformation() throws EdgeServiceFault;
	EdgePreferenceConfigInfo getPreferenceInformation() throws EdgeServiceFault;
	PatchInfo getD2DPatchInformation() throws EdgeServiceFault;
	ExternalLinks getExternalLinksForInternal(String language, String country) throws EdgeServiceFault;
	Boolean IsApplianceNotConfig(String domain, String userName, String password)throws EdgeServiceFault;
	void ApplianceFactoryReset(boolean preserve, boolean autoReboot)throws EdgeServiceFault;
	String getConsoleHostName();
}
