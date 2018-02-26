package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;

public interface IEsxDiscoveryMonitor {
	
	void onTaskStart();
	void onTaskSuccessful();
	void onTaskFail(String errorCode, String errorMessage);
	
	void onDiscoveryStart(DiscoveryESXOption esxOption);
	void onDiscoverySuccessful(DiscoveryESXOption esxOption, List<DiscoveryVirtualMachineInfo> vmList);
	void onDiscoveryFail(DiscoveryESXOption esxOption, List<DiscoveryVirtualMachineInfo> vmList, String errorCode, String errorMessage);
	void onDiscoveryUpdate(DiscoveryVirtualMachineInfo vm);

}
