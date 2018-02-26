package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeAD;
import com.ca.arcserve.edge.app.base.appdaos.EdgeADHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.INodeAdService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySettingForAD;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOption;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryService;

public class NodeAdServiceImpl implements INodeAdService {
	
	private IEdgeAdDao adDao;
	private DiscoveryService discoveryService;
	private static final Logger logger = Logger.getLogger(NodeAdServiceImpl.class);
	
	private IEdgeGatewayLocalService gatewayService;
	
	public NodeAdServiceImpl() {
		adDao = DaoFactory.getDao(IEdgeAdDao.class);
		discoveryService = DiscoveryService.getInstance();
	}
	
	public void setDao(IEdgeAdDao dao) {
		if (dao == null) {
			throw new NullPointerException();
		}
		
		adDao = dao;
	}
	
	public void setDiscoveryService(DiscoveryService service) {
		if (service == null) {
			throw new NullPointerException();
		}
		
		discoveryService = service;
	}
	
	private IEdgeGatewayLocalService getGatewayService()
	{
		if (this.gatewayService == null)
			this.gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
		return this.gatewayService;
	}

	@Override
	public void addADSource(DiscoveryOption setting) throws EdgeServiceFault {
		assertValidAdSource(setting);
		if (setting.getId() > 0) {
			updateAdSource(setting.getId(), setting);
		} else {			
		assertAdSourceNotExist(setting);
		updateAdSource(0, setting);
		}
	}
	
	@Override
	public int addADSourceforWizard(DiscoveryOption option) throws EdgeServiceFault {
		assertValidAdSource(option);
		int[] output = new int[1];
		int id = 0;
		adDao.as_edge_ad_isExist(option.getUserName(), option.getComputerNameFilter(), output);
		if (output[0] > 0) {
			option.setId(output[0]);
		} else {
			option.setId(0);
		}
		id = updateAdSource(option.getId(), option);
		return id;
	}

	@Override
	public void cancelDiscovery() throws EdgeServiceFault {
		discoveryService.cancel();
	}

	@Override
	public void deleteADSource(int id) throws EdgeServiceFault {
		if(id > 0){
			this.getGatewayService().unbindEntity(id, EntityType.AD);
			adDao.as_edge_ad_delete(id);
		}
	}
	@Override
	public String discoverNodesFromAD(DiscoveryOption[] options)
			throws EdgeServiceFault {
		return discoveryService.discoverNodeFromAD(options);
	}

	@Override
	public List<DiscoveryOption> getADSourceList() throws EdgeServiceFault {
		List<EdgeAD> adList = new LinkedList<EdgeAD>();
		adDao.as_edge_ad_getById(0, adList);
		
		List<DiscoveryOption> retval = new LinkedList<DiscoveryOption>();
		if (adList.isEmpty()) {
			return retval;
		}
		
		for (EdgeAD ad : adList) {
			DiscoveryOption option = new DiscoveryOption();
			
			option.setId(ad.getId());
			option.setUserName(ad.getUsername());
			option.setPassword(ad.getPassword());
			option.setComputerNameFilter(ad.getFilter());
			option.setTargetComputerName(ad.getDomainControler());
			retval.add(option);
		}
		
		return retval;
	}

	@Override
	public PagingResult<DiscoveredNode> getDiscoveredNodes(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		if (filter == null || config == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		int[] output = new int[1];
		List<EdgeADHost> adHostList = new LinkedList<EdgeADHost>();

		String hostnamePattern = filter.getNamePattern()==null?"":filter.getNamePattern().toUpperCase();
		String domainPattern = filter.getDomainPattern()==null?"":filter.getDomainPattern().toUpperCase();
		
		try {
			adDao.as_edge_host_getListFromADDiscoveryResult(hostnamePattern, 
															domainPattern, 
															0,
															filter.isShowHidden() ? 0 : IEdgeAdDao.AD_HOST_STATUS_VISIBLE, 
															config.getStartIndex(), 
															config.getCount(), 
															config.getSortColumn(),
															config.isAsc(),
															output,
															adHostList);
		} catch (Exception e) {
			throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		List<DiscoveredNode> nodeList = new LinkedList<DiscoveredNode>();
		for (EdgeADHost host : adHostList) {
			DiscoveredNode node = new DiscoveredNode();
			
			node.setId(host.getRhostid());
			node.setHostname(host.getRhostname());
			node.setDomain(host.getDomainname());
			
			GatewayEntity gateway = this.getGatewayService().getGatewayByEntityId( host.getRhostid(), EntityType.Node );
			node.setGatewayId( gateway.getId() );
			
			nodeList.add(node);
		}
		
		PagingResult<DiscoveredNode> retval = new PagingResult<DiscoveredNode>();
		
		retval.setStartIndex(config.getStartIndex());
		retval.setTotalCount(output[0]);
		retval.setData(nodeList);
		
		return retval;
	}

	@Override
	public DiscoveryMonitor getDiscoveryMonitor() throws EdgeServiceFault {
		return discoveryService.cloneDiscoveryMonitor();
	}

	@Override
	public void hideDiscoverdNodes(int[] nodeIds) throws EdgeServiceFault {
		if (nodeIds == null) {
			return;
		}
		
		for (int nodeId : nodeIds) {
			adDao.as_edge_ad_host_map_updateStatus(0, nodeId, IEdgeAdDao.AD_HOST_STATUS_INVISIBLE);
		}
	}

	@Override
	public void updateADSource(DiscoveryOption option) throws EdgeServiceFault {
		assertValidAdSource(option);
		
		List<EdgeAD> adList = new ArrayList<EdgeAD>();
		adDao.as_edge_ad_getById(option.getId(), adList);
		
		if (adList.isEmpty()) {
			return;
		}
		
		EdgeAD ad = adList.get(0);
		if (!ad.getUsername().equalsIgnoreCase(option.getUserName())
				|| !ad.getFilter().equalsIgnoreCase(option.getComputerNameFilter())) {
			assertAdSourceNotExist(option);
		}
		
		updateAdSource(option.getId(), option);
	}
	
	private void assertValidAdSource(DiscoveryOption option) throws EdgeServiceFault {
		if (option == null || option.getUserName() == null || option.getUserName().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_InvalidUser, "");
		}
		
		if (option.getComputerNameFilter() == null) {
			option.setComputerNameFilter("");
		}
		
		if (option.getComputerNameFilter().length() > 32) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "the computer name filter length is larger than 32.");
		}
	}
	
	private void assertAdSourceNotExist(DiscoveryOption option) throws EdgeServiceFault {
		int[] output = new int[1];
		adDao.as_edge_ad_isExist(option.getUserName(), option.getComputerNameFilter(), output);
		if (output[0] == 1) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ADSourceExist, "");
		}
	}
	
	private int updateAdSource(int id, DiscoveryOption option) throws EdgeServiceFault {
		discoveryService.validateADAccount(
				option.getGatewayId(),
				option.getTargetComputerName(), 
				option.getUserName(), 
				option.getPassword());
		
		int[] output = new int[1];
		adDao.as_edge_ad_update(
				id, option.getUserName(), option.getPassword(), 
				option.getComputerNameFilter(), option.getTargetComputerName(), output);
		if(id == 0){ //insert
			this.getGatewayService().bindEntity(option.getGatewayId(), output[0], EntityType.AD);
		}
		return output[0];
	}

	public int addDiscoverySettingForAD(DiscoverySettingForAD setting) throws EdgeServiceFault {
		assertValidAdSource(setting);
		int id = 0;
		if (setting.getId() == 0) {
			id = assertAdSourceNotExist(setting);
			if(id != 0){
				List<EdgeAD> adList = new ArrayList<EdgeAD>();
				adDao.as_edge_ad_getById(id, adList);
				if(!adList.isEmpty() && adList.get(0).getIsAutoDiscovery()==0){
					setAutoDiscoveryFlag(id, 1);
				}else {
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ADSourceExist, "");
				}
			}
		} else {
			id = setting.getId();
		}
		return updateAdSource(id, setting);
	}
	
	private void assertValidAdSource(DiscoverySettingForAD setting) throws EdgeServiceFault {
		if (setting == null || setting.getUsername() == null || setting.getUsername().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_InvalidUser, "");
		}
		if (setting.getFilter() == null) {
			setting.setFilter("");
		}
		if (setting.getFilter().length() > 32) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "the computer name filter length is larger than 32.");
		}
	}
	private int assertAdSourceNotExist(DiscoverySettingForAD setting) throws EdgeServiceFault {
		int[] output = new int[1];
		output[0] = 0;
		adDao.as_edge_ad_isExist(setting.getUsername(), setting.getFilter(), output);
		return output[0];
//		if (output[0] == 1) {
//			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ADSourceExist, "");
//		}
	}
	private int updateAdSource(int id, DiscoverySettingForAD setting) throws EdgeServiceFault {
		discoveryService.validateADAccount(setting.getGatewayId(), setting.getDomainControler(), setting.getUsername(), setting.getPassword());
		int[] output = new int[1];
		adDao.as_edge_ad_update(id, setting.getUsername(), setting.getPassword(), setting.getFilter(), setting.getDomainControler(), output);
		if(id==0){
			if(setting.getGatewayId() == null){
				logger.error("[NodeAdServiceImpl]updateAdSource() insert ad, but have no gateway id. the ad is: "+setting.getHostname());
			}else {
				this.getGatewayService().bindEntity(setting.getGatewayId(),output[0],EntityType.AD);
			}
		}
		return output[0];
	}
	
	@Override
	public PagingResult<DiscoveredNode> getDiscoveryADResult(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		if (filter == null || config == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		int[] output = new int[1];
		List<EdgeADHost> adHostList = new LinkedList<EdgeADHost>();

		String hostnamePattern = filter.getNamePattern()==null?"":filter.getNamePattern().toUpperCase();
		String domainPattern = filter.getDomainPattern()==null?"":filter.getDomainPattern().toUpperCase();
		
		try {
			adDao.as_edge_getDiscoveryADResult(hostnamePattern, 
															domainPattern, 
															0,
															filter.isShowHidden() ? 0 : IEdgeAdDao.AD_HOST_STATUS_VISIBLE, 
															config.getStartIndex(), 
															config.getCount(), 
															config.getSortColumn(),
															config.isAsc(),
															output,
															adHostList);
		} catch (Exception e) {
			throw EdgeServiceFault.getFault(
					EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		List<DiscoveredNode> nodeList = new LinkedList<DiscoveredNode>();
		for (EdgeADHost host : adHostList) {
			DiscoveredNode node = new DiscoveredNode();
			
			node.setId(host.getRhostid());
			node.setHostname(host.getRhostname());
			node.setDomain(host.getDomainname());
			
			GatewayEntity gateway = this.getGatewayService().getGatewayByEntityId( host.getRhostid(), EntityType.Node );
			node.setGatewayId( gateway.getId() );
			
			nodeList.add(node);
		}
		
		PagingResult<DiscoveredNode> retval = new PagingResult<DiscoveredNode>();
		
		retval.setStartIndex(config.getStartIndex());
		retval.setTotalCount(output[0]);
		retval.setData(nodeList);
		
		return retval;
	}
	
	public void deleteAdSetting(int id)throws EdgeServiceFault{
		if(id>0){
			//get node from ad_host_map
			List<IntegerId> nodeIds = new ArrayList<IntegerId>();
			adDao.as_edge_ad_host_map_getNodeIdsbyadId(id, nodeIds);
			
			if(nodeIds.size()>0){
				for(IntegerId nodeId:nodeIds){
					if(nodeId.getId()>0){
						int[] output = new int[1];
						adDao.as_edge_host_delete_unvisible_node(nodeId.getId(), output);
						if(output[0] == 0){  //if it is unvisible node, it is already deleted in as_edge_host table, unbind Entity
							this.getGatewayService().unbindEntity(nodeId.getId(), EntityType.Node);
						}
					}
				}
			}
			adDao.as_edge_ad_delete(id);
			this.getGatewayService().unbindEntity(id, EntityType.AD);
		}
	}
	
	public void setAutoDiscoveryFlag(int id, int isAutoDiscovery){
		adDao.as_edge_ad_update_auto_discovery_flag(id,isAutoDiscovery);
	}
}
