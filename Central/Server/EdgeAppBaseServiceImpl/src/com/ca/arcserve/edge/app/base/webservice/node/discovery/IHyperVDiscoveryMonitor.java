package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;

public interface IHyperVDiscoveryMonitor {
	
	void onTaskStart();
	void onTaskSuccessful();
	void onTaskFail(String errorCode, String errorMessage);
	
	void onDiscoveryStart(DiscoveryHyperVOption hyperVOption);
	void onDiscoverySuccessful(DiscoveryHyperVOption hyperVOption, List<DiscoveryVirtualMachineInfo> vmList);
	void onDiscoveryFail(DiscoveryHyperVOption hyperVOption, List<DiscoveryVirtualMachineInfo> vmList, String errorCode, String errorMessage);
	void onDiscoveryUpdate(DiscoveryVirtualMachineInfo vm);

}
