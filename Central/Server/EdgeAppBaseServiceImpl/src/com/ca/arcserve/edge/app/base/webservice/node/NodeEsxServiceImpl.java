package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.vcloudmanager.VCloudManager;
import com.ca.arcflash.ha.vcloudmanager.VCloudManagerError;
import com.ca.arcflash.ha.vcloudmanager.VCloudManagerException;
import com.ca.arcflash.ha.vcloudmanager.VcloudManagerConnectionCache;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudObject;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudOrganization;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVApp;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVCenter;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVDC;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVM;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsxHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsxVerifyStatus;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.INodeEsxService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySettingForESX;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredVM;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ESXServer;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMVerifyStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryService;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.EsxDiscoveryService;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerService;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerServiceFactory;

public class NodeEsxServiceImpl implements INodeEsxService {
	
	private IEdgeEsxDao esxDao;
	private DiscoveryService discoveryService;
	private EsxDiscoveryService esxDiscoveryService;
	IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
	private IEdgeGatewayLocalService gatewayService;
	private static final Logger logger = Logger.getLogger(NodeEsxServiceImpl.class);
	
	public NodeEsxServiceImpl() {
		esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
		discoveryService = DiscoveryService.getInstance();
		esxDiscoveryService = EsxDiscoveryService.getInstance();
	}
	
	public void setDao(IEdgeEsxDao dao) {
		if (dao == null) {
			throw new NullPointerException();
		}
		
		esxDao = dao;
	}
	
	public void setDiscoveryService(DiscoveryService service) {
		if (service == null) {
			throw new NullPointerException();
		}
		
		discoveryService = service;
	}
	
	public void setEsxDiscoveryService(EsxDiscoveryService service) {
		if (service == null) {
			throw new NullPointerException();
		}
		
		esxDiscoveryService = service;
	}
	
	private IEdgeGatewayLocalService getGatewayService()
	{
		if (this.gatewayService == null)
			this.gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
		return this.gatewayService;
	}

	@Override
	public void addEsxSource(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		assertValidEsxSource(esxOption);
		int id = assertEsxSourceNotExist(esxOption);
		updateEsxSource(id, esxOption);
	}

	@Override
	public void cancelEsxDiscovery() throws EdgeServiceFault {
		esxDiscoveryService.cancel();
	}

	@Override
	public void deleteEsxSource(int id) throws EdgeServiceFault {
		this.getGatewayService().unbindEntity(id, EntityType.VSphereEntity);
		esxDao.as_edge_esx_delete(id);
	}

	@Override
	public String discoverNodesFromESX(DiscoveryESXOption[] esxOptions)
			throws EdgeServiceFault {
		return esxDiscoveryService.startDiscovery(esxOptions);
	}
	@Override
	public PagingResult<DiscoveredVM> getDiscoveredVMs(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		if (filter == null || config == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		int[] output = new int[1];
		List<EdgeEsxHost> esxHostList = new LinkedList<EdgeEsxHost>();
		
		try {
			esxDao.as_edge_host_getListFromEsxDiscoveryResult(
					filter.getNamePattern().toUpperCase(), 0,
					filter.isShowHidden() ? 0 : IEdgeEsxDao.ESX_HOST_STATUS_VISIBLE,
					config.getStartIndex(), config.getCount(),
					config.getSortColumn(), config.isAsc(),
					output, esxHostList);
		} catch (Exception e) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		List<DiscoveredVM> vmList = new LinkedList<DiscoveredVM>();
		for (EdgeEsxHost host : esxHostList) {
			DiscoveredVM vm = new DiscoveredVM();
			
			vm.setId(host.getRhostid());
			vm.setHostname(host.getRhostname());
			vm.setVmName(host.getVmName());
			
			String hostname = host.getHostname();
			// if this is vCenter type , we need to add the ESX host info		
			if(host.getServertype()==  2){
				hostname = EdgeCMWebServiceMessages.getMessage("importNodes_HyperVisorInfo", 
						hostname, host.getEsxHost()); // vCenterName(ESXName)
			}
			
			vm.setHyperVisor(hostname);
			
			vmList.add(vm);
		}
		
		PagingResult<DiscoveredVM> retval = new PagingResult<DiscoveredVM>();
		
		retval.setStartIndex(config.getStartIndex());
		retval.setTotalCount(output[0]);
		retval.setData(vmList);
		
		return retval;
	}

	@Override
	public DiscoveryMonitor getEsxDiscoveryMonitor() throws EdgeServiceFault {
		return esxDiscoveryService.getDiscoveryMonitor();
	}

	@Override
	public List<DiscoveryESXOption> getEsxSourceList() throws EdgeServiceFault {
		List<EdgeEsx> esxList = new LinkedList<EdgeEsx>();
		esxDao.as_edge_esx_getById(0, esxList);
		
		List<DiscoveryESXOption> retval = new LinkedList<DiscoveryESXOption>();
		if (esxList.isEmpty()) {
			return retval;
		}
		
		for (EdgeEsx esx : esxList) {
			DiscoveryESXOption esxOption = new DiscoveryESXOption();
			
			esxOption.setId(esx.getId());
			esxOption.setEsxServerName(esx.getHostname());
			esxOption.setEsxUserName(esx.getUsername());
			esxOption.setEsxPassword(esx.getPassword());
			esxOption.setProtocol(Protocol.parse(esx.getProtocol()));
			esxOption.setPort(esx.getPort());
			
			retval.add(esxOption);
		}
		
		return retval;
	}

	@Override
	public List<DiscoveryVirtualMachineInfo> getVMVirtualMachineList(
			DiscoveryESXOption esxOption) throws EdgeServiceFault {
		return discoveryService.getVMVirtualMachineList(esxOption);
	}

	@Override
	public void hideDiscoverdVMs(int[] vmIds) throws EdgeServiceFault {
		if (vmIds == null || vmIds.length == 0) {
			return;
		}
		
		for (int vmId : vmIds) {
			esxDao.as_edge_vsphere_vm_detail_updateStatus(vmId, IEdgeEsxDao.ESX_HOST_STATUS_INVISIBLE);
		}
	}

	@Override
	public void updateEsxSource(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		assertValidEsxSource(esxOption);
		
		List<EdgeEsx> esxList = new ArrayList<EdgeEsx>();
		esxDao.as_edge_esx_getById(esxOption.getId(), esxList);
		
		if (esxList.isEmpty()) {
			return;
		}
		
		EdgeEsx esx = esxList.get(0);
		if (!esx.getHostname().equalsIgnoreCase(esxOption.getEsxServerName())) {
			assertEsxSourceNotExist(esxOption);
		}
		
		updateEsxSource(esxOption.getId(), esxOption);
	}
	
	private void assertValidEsxSource(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		if (esxOption == null || esxOption.getEsxServerName() == null || esxOption.getEsxServerName().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
	}
	
	private int assertEsxSourceNotExist(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		List<EdgeEsx> esxList = new LinkedList<EdgeEsx>();
		
		int id = 0;
		esxDao.as_edge_esx_getByName(esxOption.getGatewayId().getRecordId(), esxOption.getEsxServerName(), esxList);
		
		if (!esxList.isEmpty() && esxList.get(0).getVisible() != -1) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_EsxSourceExist, "");
		}
		if(!esxList.isEmpty())
			id = esxList.get(0).getId();
		return id;
	}
	
	private void updateEsxSource(int id, DiscoveryESXOption esxOption) throws EdgeServiceFault {
		esxOption.setGatewayId(getGatewayService().getGatewayByEntityId(id, EntityType.VSphereEntity).getId());
		discoveryService.validateEsxAccount(esxOption);
		
		int[] output = new int[1];
		esxDao.as_edge_esx_update(id, 
				esxOption.getEsxServerName(),
				esxOption.getEsxUserName(),
				esxOption.getEsxPassword(),
				esxOption.getProtocol().ordinal(),
				esxOption.getPort(),
				0,
				0,
				"",
				"",
				output);
		
		// update server type
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
		int type = vmwareService.updateEsxServerType(esxOption);
		vmwareService.close();
		esxDao.as_edge_esx_update_type(id, type);
	}

	@Override
	public List<ESXServer> getEsxNodeList(DiscoveryESXOption esxOption)
			throws EdgeServiceFault {
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
		ArrayList<ESXNode> nodes = vmwareService.getEsxNodeList(esxOption);
		vmwareService.close();
		
		List<ESXServer> result = new LinkedList<ESXServer>();
		for (ESXNode node:nodes){
			ESXServer server = new ESXServer();
			server.setEsxName(node.getEsxName());
			server.setDataCenter(node.getDataCenter());
			server.setClusterName(node.getClusterName());
			server.setiNoncompatableHW(node.getiNoncompatableHW());
			server.setiNonWindows(node.getiNonWindows());
			server.setiTotalVMCount(node.getiTotalVMCount());
			server.setiWindows(node.getiWindows());
			server.setSkipNode(node.isSkipNode());
			
			result.add(server);
		}
		
		return result;
	}

	@Override
	public VMVerifyStatus getVMVerifyStatus(int id) throws EdgeServiceFault {
		VMVerifyStatus result = new VMVerifyStatus();
		
		List<EdgeEsxVerifyStatus> esxVerifyStatusList = new LinkedList<EdgeEsxVerifyStatus>();
		esxDao.as_edge_esx_verify_status_getById(id,esxVerifyStatusList);
		
		try {
			if(esxVerifyStatusList.size()>0){
				int status = esxVerifyStatusList.get(0).getStatus();
				String detail = esxVerifyStatusList.get(0).getDetail();
				result.setStatus(status);
				if(esxVerifyStatusList.get(0).getDetail()!=null){
					VMVerifyStatus verifyStatus = CommonUtil.unmarshal(detail, VMVerifyStatus.class);
					if(verifyStatus != null){
						result.setDetails(verifyStatus.getDetails());	
					}
				}
			}
		} catch (Exception e) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		return result;
	}
	
	@Override
	public List<ESXServer> getDiscoveryEsxServers(DiscoveryESXOption esxOption) throws EdgeServiceFault {

		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
		List<ESXNode> esxNodes = vmwareService.getEsxNodeListWithOriginal(esxOption);	
		vmwareService.close();
		
		List<ESXServer> result = new LinkedList<ESXServer>();
		for (ESXNode node : esxNodes){
			ESXServer server = new ESXServer();
			
			server.setEsxName(node.getEsxName());
			server.setDataCenter(node.getDataCenter());
			server.setClusterName(node.getClusterName());
			server.setiNoncompatableHW(node.getiNoncompatableHW());
			server.setiNonWindows(node.getiNonWindows());
			server.setiTotalVMCount(node.getiTotalVMCount());
			server.setiWindows(node.getiWindows());
			server.setSkipNode(node.isSkipNode());
			
			result.add(server);
		}
			
		return result;
	}

	@Override
	public List<DiscoveryVirtualMachineInfo> getVmList(DiscoveryESXOption esxOption, ESXServer esxServer) throws EdgeServiceFault {
		
		ESXNode esxNode = new ESXNode();
		esxNode.setEsxName(esxServer.getEsxName());
		esxNode.setDataCenter(esxServer.getDataCenter());
		esxNode.setClusterName(esxServer.getClusterName());
		esxNode.setiNoncompatableHW(esxServer.getiNoncompatableHW());
		esxNode.setiNonWindows(esxServer.getiNonWindows());
		esxNode.setiTotalVMCount(esxServer.getiTotalVMCount());
		esxNode.setiWindows(esxServer.getiWindows());
		esxNode.setSkipNode(esxServer.isSkipNode());
		
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
		List<DiscoveryVirtualMachineInfo> rst = vmwareService.getVmList(esxOption, esxNode,  Module.ImportNodesFromHypervisor, false);
		vmwareService.close();
		
		return rst;
	}
		
	@Override
	public DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(DiscoveryESXOption esxOption, boolean recursive) throws EdgeServiceFault {
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( getValidEsxGateWayId(esxOption) );
		DiscoveryVmwareEntityInfo rst = vmwareService.getVmwareTreeRootEntity(esxOption, recursive, esxOption.getEsxServerName());
		vmwareService.close();
		return rst;
	}
	private GatewayId getValidEsxGateWayId(DiscoveryESXOption esxOption) throws EdgeServiceFault{ 
		//invalid gateway;
		GatewayId gateWayId = null;
		if(!esxOption.getGatewayId().isValid()){
			int id = esxOption.getId(); 
			gateWayId = getGatewayService().getGatewayByEntityId(id, EntityType.VSphereEntity).getId();  ////esx use entitytype = 4;! 
		}
		else {
			gateWayId = esxOption.getGatewayId();
		}
		return gateWayId;
	}
	@Override
	public VsphereEntity getVcloudResource(VsphereEntity vcloudEntity)
			throws EdgeServiceFault {
		try {
			String protocol = vcloudEntity.getProtocol().equals(Protocol.Https)?"https":"http";
			VCloudManager vCloudManager = VcloudManagerConnectionCache.getVcloudManagerConnection(vcloudEntity.getName()
					, vcloudEntity.getUserName(), vcloudEntity.getPassword()
					, protocol, vcloudEntity.getPort());
			vcloudEntity.getChildren().clear();
			vcloudEntity.getVcenters().clear();
			vcloudEntity.setEntityType(VsphereEntityType.vCloudDirector);
			//getVcenters
			List<VCloudVCenter> vCenters = vCloudManager.getVCenters();
			if(vCenters != null && !vCenters.isEmpty()){
				for (VCloudVCenter vCenter : vCenters) {
					VsphereEntity vCenterEntity = convertVobjectToVsphereEntity(vCenter);
					vcloudEntity.getVcenters().add(vCenterEntity);
				}
			}
			//get organizations
			List<VCloudOrganization> orgs = vCloudManager.getOrganizations();
			if(orgs == null || orgs.isEmpty())
				return null;
			for (VCloudObject org : orgs) {
				VsphereEntity orgEntity = convertVobjectToVsphereEntity(org);
				vcloudEntity.getChildren().add(orgEntity);
				orgEntity.setParent(vcloudEntity);
				//get vdcs
				List<VCloudVDC> orgVDCs = vCloudManager.getVDCs(org.getId());
				if(orgVDCs == null || orgVDCs.isEmpty())
					continue;
				for (VCloudVDC vdc : orgVDCs) {
					VsphereEntity vdcEntity = convertVobjectToVsphereEntity(vdc);
					orgEntity.getChildren().add(vdcEntity);
					vdcEntity.setParent(orgEntity);
					//get vcenter
					VCloudVCenter vCenter = vCloudManager.getVCenter(vdc.getvCenterId());
					VsphereEntity vcenterEntity = convertVobjectToVsphereEntity(vCenter);
					vdcEntity.getVcenters().clear();
					vdcEntity.getVcenters().add(vcenterEntity);
					//get vApps
					List<VCloudVApp> vApps = vCloudManager.getVApps(vdc.getId());
					if(vApps == null || vApps.isEmpty())
						continue;
					for (VCloudVApp vApp : vApps) {
						VsphereEntity vappEntity = convertVobjectToVsphereEntity(vApp);
						if(vappEntity.getStatus() != 6){
							vappEntity.setEntityType(VsphereEntityType.vAPP);
							vappEntity.getVcenters().clear();
							vappEntity.getVcenters().add(vcenterEntity);
							vdcEntity.getChildren().add(vappEntity);
							vappEntity.setParent(vdcEntity);
							//get vms
							List<VCloudVM> vms = vCloudManager.getVMs(vApp.getId());
							if(vms == null || vms.isEmpty())
								continue;
							for (VCloudVM vm : vms) {
								VsphereEntity vmEntity = convertVobjectToVsphereEntity(vm);
								vmEntity.setEntityType(VsphereEntityType.vm);
								vmEntity.getVcenters().clear();
								vmEntity.getVcenters().add(vcenterEntity);
								vappEntity.getChildren().add(vmEntity);
								vmEntity.setParent(vappEntity);
							}
						}
						else {
							logger.info("vApp: "+vappEntity.getName()+" is not exsit.");
						}
					}
				}
			}	
			return vcloudEntity;
		} catch (VCloudManagerException e) {
			logger.error("[NodeEsxServiceImpl]:getVcloudResource() failed.",e);
			String edgeServiceError = EdgeServiceErrorCode.Common_Service_General;
			if(e.getError() == VCloudManagerError.INVALID_USERNAME_FORMAT){
				edgeServiceError = EdgeServiceErrorCode.Node_vCloud_Invalid_UserName;
			}else if (e.getError() == VCloudManagerError.ACCESS_DENIED) {
				edgeServiceError = EdgeServiceErrorCode.Node_vCloud_Access_Denied;
			}else if (e.getError() == VCloudManagerError.CONNECTION_FAILED) {
				edgeServiceError = EdgeServiceErrorCode.Node_vCloud_Connect_Failed;
			}
			EdgeServiceFaultBean faultInfo = new EdgeServiceFaultBean(
					edgeServiceError, "Fail to get vcloud resources.");
			throw new EdgeServiceFault("", faultInfo);
		}catch (Exception e) {
			logger.error("[NodeEsxServiceImpl]:getVcloudResource() failed.",e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
	}
	
	private VsphereEntity convertVobjectToVsphereEntity(VCloudObject object){
		VsphereEntity entity = new VsphereEntity();
		entity.setName(object.getName());
		entity.setHref(object.getHref());
		entity.setEntityId(object.getId());
		if(object instanceof VCloudOrganization){
			VCloudOrganization organization = (VCloudOrganization)object;
			entity.setFullName(organization.getFullName());
			entity.setDescription(organization.getDescription());
			entity.setEntityType(VsphereEntityType.organization);
		}else if (object instanceof VCloudVDC) {
			entity.setEntityType(VsphereEntityType.virtualDataCenter);
			//if VDC have another properties you used, please add code here
		}else if (object instanceof VCloudVCenter) {
			VCloudVCenter vcenter = (VCloudVCenter)object;
			entity.setUserName(vcenter.getUsername());
			entity.setVersion(vcenter.getVersion());
			String url = vcenter.getUrl();
			String protocol = url.substring(0, url.indexOf("://")).trim();
			int port = Integer.valueOf(url.substring(url.lastIndexOf(":")+1).trim());
			entity.setProtocol(Protocol.parse(protocol));
			entity.setPort(port);
			entity.setUrl(url);
			entity.setEntityType(VsphereEntityType.vCenter);
		}else if (object instanceof VCloudVApp) {
			VCloudVApp vApp = (VCloudVApp)object;
			entity.setDescription(vApp.getDescription());
			entity.setStatus(vApp.getStatus());
			entity.setEntityType(VsphereEntityType.vAPP);
		}else if(object instanceof VCloudVM){
			VCloudVM vm = (VCloudVM)object;
			entity.setMoRef(vm.getMoRef());
			entity.setEntityType(VsphereEntityType.vm);
		}
		return entity;
	}

	@Override
	public EsxServerInformation getEsxServerInformation(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( getValidEsxGateWayId(esxOption) );
		try {
			return vmwareService.getEsxServerInformation(esxOption);
		} finally {
			vmwareService.close();
		}
	}

	@Override
	public EsxHostInformation getEsxHostInformation(DiscoveryESXOption esxOption, 
			VWWareESXNode esxNode) throws EdgeServiceFault {
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService(getValidEsxGateWayId(esxOption));
		try {
			return vmwareService.getEsxHostInformation(esxOption, esxNode);
		} finally {
			vmwareService.close();
		}
	}
	public int addDiscoverySettingForESX(DiscoverySettingForESX setting) throws EdgeServiceFault {
		assertValidEsxSource(setting);
		int id = 0;
		if (setting.getId() == 0) {
			id = assertEsxSourceNotExist(setting);
		} else {
			id = setting.getId();
		}
		return updateEsxSource(id, setting);
	}
	private void assertValidEsxSource(DiscoverySettingForESX setting) throws EdgeServiceFault {
		if (setting == null || setting.getHostname() == null || setting.getHostname().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
	}
	private int assertEsxSourceNotExist(DiscoverySettingForESX setting) throws EdgeServiceFault {
		List<EdgeEsx> esxList = new LinkedList<EdgeEsx>();
		int id = 0;
		esxDao.as_edge_esx_getByName(setting.getGatewayId().getRecordId(), setting.getHostname(), esxList);
		if (!esxList.isEmpty() && esxList.get(0).getVisible() != -1) {
			if(esxList.get(0).getIsAutoDiscovery()==0){
				setAutoDiscoveryForEsx( esxList.get(0).getId(), 1);
			}else {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_EsxSourceExist, "");
			}
		}
		if(!esxList.isEmpty())
			id = esxList.get(0).getId();
		return id;
	}
	private int updateEsxSource(int id, DiscoverySettingForESX setting) throws EdgeServiceFault {
		DiscoveryESXOption esxOption = new DiscoveryESXOption();
		esxOption.setEsxServerName(setting.getHostname());
		esxOption.setEsxUserName(setting.getUsername());
		esxOption.setEsxPassword(setting.getPassword());
		esxOption.setProtocol(setting.getProtocol());
		esxOption.setPort(setting.getPort());
		esxOption.setGatewayId(setting.getGatewayId());
		discoveryService.validateEsxAccount(esxOption);
		int[] output = new int[1];
		esxDao.as_edge_esx_update(id, 
				setting.getHostname(),
				setting.getUsername(),
				setting.getPassword(),
				setting.getProtocol().ordinal(),
				setting.getPort(),
				0,
				0, "", "", output);
		id=output[0];
		IVmwareManagerService vmwareService = null;
		int type = 0;
		try {
			IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
			vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
			type = vmwareService.updateEsxServerType(esxOption);
		} catch (Exception e) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		} finally{
			if (vmwareService != null) {
				try {
					vmwareService.close();
				} catch (Exception e) {
				}
			}
		}
		esxDao.as_edge_esx_update_type(id, type);
		if(setting.getId() == 0){
			if(setting.getGatewayId() == null){
				logger.error("[NodeEsxServiceImpl]updateEsxSource() insert esx,"
						+ " but have no gateway id. the esx id is: "+id+", the esx name is:"+setting.getHostname());
			}else {
				this.getGatewayService().bindEntity(setting.getGatewayId(), id, EntityType.VSphereEntity);
			}
		}
		return id;
	}

	public void setAutoDiscoveryForEsx(int id, int isAutoDiscovery) throws EdgeServiceFault {
		esxDao.as_edge_esx_update_auto_discovery_flag(id, isAutoDiscovery);
	}
}
