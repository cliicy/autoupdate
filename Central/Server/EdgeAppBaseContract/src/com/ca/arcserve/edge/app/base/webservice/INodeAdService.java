package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOption;

public interface INodeAdService {

	// Auto discovery
	/**
	 * 
	 * @param options
	 * @return discovering job uuid
	 * @throws EdgeServiceFault
	 */
	String discoverNodesFromAD(DiscoveryOption[] options) throws EdgeServiceFault;
	
	/**
	 * @return
	 * @throws EdgeServiceFault
	 */
	DiscoveryMonitor getDiscoveryMonitor() throws EdgeServiceFault;
	void cancelDiscovery() throws EdgeServiceFault;
	
	// Source management
	void addADSource(DiscoveryOption option) throws EdgeServiceFault;
	int addADSourceforWizard(DiscoveryOption option) throws EdgeServiceFault;
	void updateADSource(DiscoveryOption option) throws EdgeServiceFault;
	void deleteADSource(int id) throws EdgeServiceFault;
	List<DiscoveryOption> getADSourceList() throws EdgeServiceFault;
	
	// Add nodes from discovery result
	void hideDiscoverdNodes(int[] nodeIds) throws EdgeServiceFault;
	PagingResult<DiscoveredNode> getDiscoveredNodes(DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config) throws EdgeServiceFault;
	PagingResult<DiscoveredNode> getDiscoveryADResult(DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config) throws EdgeServiceFault;
}
