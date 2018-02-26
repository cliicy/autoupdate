package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeDiscoveryItem;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IDiscoveryService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoveryHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoveryItem;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySetting;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySettingForAD;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySettingForESX;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySettingForHyperV;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting.SettingType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.Task;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.node.NodeAdServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeEsxServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;

public class DiscoveryServiceImpl implements IDiscoveryService {
	private IEdgeAdDao adDao = DaoFactory.getDao(IEdgeAdDao.class);
	private NodeEsxServiceImpl esxService = new NodeEsxServiceImpl();
	private NodeAdServiceImpl adService = new NodeAdServiceImpl();
	private NodeServiceImpl nodeServiceImpl = new NodeServiceImpl();
	private static IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	@Override
	public List<DiscoveryItem> getDiscoveryItemList() throws EdgeServiceFault {
		List<EdgeDiscoveryItem> list = new ArrayList<EdgeDiscoveryItem>();
		adDao.as_edge_discovery_list(list);
		List<DiscoveryItem> result = new ArrayList<DiscoveryItem>();
		// get all tasks
		List<Task> taskList = TaskMonitor.getTasksByModule(Module.Discovery);
		for (EdgeDiscoveryItem item : list) {
			DiscoveryItem tmp = new DiscoveryItem();
			DiscoverySetting discoverySetting;
			switch (SettingType.parseInt(item.getDiscoveryType())) {
			case AD:
				discoverySetting = new DiscoverySettingForAD();
				break;
			case ESX:
				discoverySetting = new DiscoverySettingForESX();
				((DiscoverySettingForESX) discoverySetting).setProtocol(Protocol.parse(item.getProtocol()));
				((DiscoverySettingForESX) discoverySetting).setPort(item.getPort());
				break;
			case HYPERV:
				discoverySetting = new DiscoverySettingForHyperV();
				((DiscoverySettingForHyperV) discoverySetting).setHypervType(HypervProtectionType.parse(item.getType()));
				break;
			default:
				discoverySetting = new DiscoverySettingForAD();
				break;
			}
			discoverySetting.setId(item.getId());
			discoverySetting.setHostname(item.getHostname());
			discoverySetting.setUsername(item.getUsername());
			discoverySetting.setPassword(item.getPassword());
			discoverySetting.setFilter(item.getFilter());
			
			GatewayEntity gateway;
			switch (SettingType.parseInt(item.getDiscoveryType())) {
				case ESX:
					gateway = gatewayService.getGatewayByEntityId(item.getId(), EntityType.VSphereEntity );					
					break;
				case HYPERV:
					gateway = gatewayService.getGatewayByEntityId(item.getId(), EntityType.HyperVServer );
					break;
				case AD:
				default:
					gateway = gatewayService.getGatewayByEntityId(item.getId(), EntityType.AD );
					break;
			}
			SiteId siteId = gatewayService.getSiteIdByGatewayId(gateway.getId());
			SiteInfo site = gatewayService.getSite(siteId);
			discoverySetting.setSiteName( site.getName());
			
			DiscoveryHistory discoveryHistory = new DiscoveryHistory();
			discoveryHistory.setId(item.getHistoryId());
			discoveryHistory.setStatus(DiscoveryStatus.parse(item.getJobStatus()));
			discoveryHistory.setStartTime(item.getStartTime());
			discoveryHistory.setEndTime(item.getEndTime());
			discoveryHistory.setResult(item.getResult());
			discoveryHistory.setExistNodeNum(item.getNodesNum());
			
			for (Task task : taskList) {
				DiscoveryHistory taskDiscoveryHistory = (DiscoveryHistory)task.getDetails().getRawData();
				if (item.getId() == taskDiscoveryHistory.getId() && SettingType.parseInt(item.getDiscoveryType()) == taskDiscoveryHistory.getDiscoveryType()) {
					discoveryHistory.setStatus(taskDiscoveryHistory.getStatus());
				}
			}
			
			tmp.setDiscoveryHistory(discoveryHistory);
			tmp.setDiscoverySetting(discoverySetting);
			result.add(tmp);
		}
		return result;
	}

	@Override
	public void saveDiscoverySetting(DiscoverySetting setting)
			throws EdgeServiceFault {
		if (setting instanceof DiscoverySettingForAD) {
			adService.addDiscoverySettingForAD((DiscoverySettingForAD)setting);
		} else if (setting instanceof DiscoverySettingForESX) {
			esxService.addDiscoverySettingForESX((DiscoverySettingForESX)setting);
		} else if (setting instanceof DiscoverySettingForHyperV) {
			nodeServiceImpl.addOrUpdateDiscoveryForHyperV((DiscoverySettingForHyperV)setting);
		}
	}

	@Override
	public void deleteDiscoverySetting(List<DiscoverySetting> settings)
			throws EdgeServiceFault {
		for (DiscoverySetting discoverySetting : settings) {
			if (discoverySetting instanceof DiscoverySettingForAD) {
				//adService.deleteAdSetting(discoverySetting.getId());
				adService.setAutoDiscoveryFlag(discoverySetting.getId(), 0);
			} else if (discoverySetting instanceof DiscoverySettingForESX) {
				esxService.setAutoDiscoveryForEsx(discoverySetting.getId(),0);
			} else if (discoverySetting instanceof DiscoverySettingForHyperV){
				nodeServiceImpl.setAutoDiscoveryForHyperv(discoverySetting.getId(),0);
			}
		}
	}

	@Override
	public void runDiscoveryJob(List<DiscoverySetting> settings)
			throws EdgeServiceFault {
		if(settings == null || settings.isEmpty())
			return;
		List<DiscoverySetting> adSettings = new ArrayList<DiscoverySetting>();
		List<DiscoverySetting> esxSettings = new ArrayList<DiscoverySetting>();
		List<DiscoverySetting> hypervSettings = new ArrayList<DiscoverySetting>();
		for (DiscoverySetting dSetting : settings) {
			if(dSetting instanceof DiscoverySettingForAD){
				adSettings.add(dSetting);
			}else if (dSetting instanceof DiscoverySettingForESX) {
				esxSettings.add(dSetting);
			}else if (dSetting instanceof DiscoverySettingForHyperV) {
				hypervSettings.add(dSetting);
			}
		}
		if(!adSettings.isEmpty()){
			DiscoveryManager.getInstance().doAutoDiscoveryForAD(adSettings);
		}
		if(!esxSettings.isEmpty()){
			DiscoveryManager.getInstance().doEsxAutoDiscovery4ManualTrigger(esxSettings);
		}
		if(!hypervSettings.isEmpty()){
			DiscoveryManager.getInstance().doHyperVAutoDiscovery4ManualTrigger(hypervSettings);
		}
	}
	
}
