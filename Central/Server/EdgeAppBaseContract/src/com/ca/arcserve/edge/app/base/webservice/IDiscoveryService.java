package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoveryItem;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySetting;

public interface IDiscoveryService {
	List<DiscoveryItem> getDiscoveryItemList() throws EdgeServiceFault;
	void saveDiscoverySetting(DiscoverySetting setting) throws EdgeServiceFault;
	/**
	 * Currently, not really to delete the hypervisor,
	 * Just only flag them, they cann't do the auto discovery any more
	 * @param settings
	 * @throws EdgeServiceFault
	 */
	void deleteDiscoverySetting(List<DiscoverySetting> settings) throws EdgeServiceFault;
	void runDiscoveryJob(List<DiscoverySetting> settings) throws EdgeServiceFault;
}
