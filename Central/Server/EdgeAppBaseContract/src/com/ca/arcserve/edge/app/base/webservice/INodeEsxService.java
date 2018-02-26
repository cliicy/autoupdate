package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredVM;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ESXServer;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMVerifyStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntity;

public interface INodeEsxService {

	// Auto discovery
	/**
	 * return the Discovery Job UUID
	 * @param esxOptions
	 * @return
	 * @throws EdgeServiceFault
	 */
	String discoverNodesFromESX(DiscoveryESXOption[] esxOptions) throws EdgeServiceFault;
	
	/**
	 * 
	 * @return
	 * @throws EdgeServiceFault
	 */
	DiscoveryMonitor getEsxDiscoveryMonitor() throws EdgeServiceFault;
	
		
	void cancelEsxDiscovery() throws EdgeServiceFault;
	
	// Source management
	void addEsxSource(DiscoveryESXOption esxOption) throws EdgeServiceFault;
	void updateEsxSource(DiscoveryESXOption esxOption) throws EdgeServiceFault;
	void deleteEsxSource(int id) throws EdgeServiceFault;
	List<DiscoveryESXOption> getEsxSourceList() throws EdgeServiceFault;
	
	// Add nodes from discovery result
	void hideDiscoverdVMs(int[] vmIds) throws EdgeServiceFault;
	PagingResult<DiscoveredVM> getDiscoveredVMs(DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config) throws EdgeServiceFault;
	
	// Discovery
	List<DiscoveryVirtualMachineInfo> getVMVirtualMachineList(DiscoveryESXOption esxOption) throws EdgeServiceFault;
	
	List<ESXServer> getEsxNodeList(DiscoveryESXOption esxOption) throws EdgeServiceFault;
	
	public VMVerifyStatus getVMVerifyStatus(int id) throws EdgeServiceFault;
	
	List<ESXServer> getDiscoveryEsxServers(DiscoveryESXOption esxOption) throws EdgeServiceFault;
	List<DiscoveryVirtualMachineInfo> getVmList(DiscoveryESXOption esxOption, ESXServer esxServer) throws EdgeServiceFault;

	DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(DiscoveryESXOption esxOption, boolean recursive) throws EdgeServiceFault;
	
	//Vcloud
	VsphereEntity getVcloudResource(VsphereEntity vcloudEntity)throws EdgeServiceFault;
	
	EsxServerInformation getEsxServerInformation(DiscoveryESXOption esxOption) throws EdgeServiceFault;
	EsxHostInformation getEsxHostInformation(DiscoveryESXOption esxOption, VWWareESXNode esxNode) throws EdgeServiceFault;
}