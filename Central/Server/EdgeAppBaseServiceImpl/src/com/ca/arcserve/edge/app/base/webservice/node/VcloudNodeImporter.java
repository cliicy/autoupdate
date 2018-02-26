package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfoForVcloud;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityRelationType;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerService;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerServiceFactory;

public class VcloudNodeImporter {
	private static final Logger logger = Logger.getLogger(VcloudNodeImporter.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private NodeRegistrationInfoForVcloud node;
	private NodeServiceImpl nodeServiceImpl;
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	
	public VcloudNodeImporter(NodeRegistrationInfoForVcloud node, NodeServiceImpl nodeServiceImpl){
		this.node = node;
		this.nodeServiceImpl = nodeServiceImpl;
	}
	
	public int ImportVcloudNode()throws EdgeServiceFault{
		VsphereEntity entity = node.getvCloudEntity();
		if(entity == null)
			return 0;
		return saveVcloudResource(entity);
	}
	
	public int saveVcloudResource(VsphereEntity vCloudRecource)throws EdgeServiceFault{
		if(vCloudRecource == null)
			return 0;
		
		VsphereEntityType entityType = vCloudRecource.getEntityType();
		if(entityType == VsphereEntityType.vAPP &&  vCloudRecource.getStatus() == 6){
			logger.info("vApp: "+vCloudRecource.getName()+" is not exsit, so not import it to console.");
			return 0;
		}
		
		int entityId  = 0;
		if(entityType == VsphereEntityType.vm){
			saveVcloudParent(vCloudRecource);
			VM_Info vmInfo = getVMInfoFromVcenter(vCloudRecource);
			if(vmInfo == null || vmInfo.getVMvmInstanceUUID() == null){
				logger.info("vm: "+vCloudRecource.getName()+" is not exsit, so not import it to console.");
				return 0;
			}
			entityId = saveToEsxDBTable(vCloudRecource, vmInfo.getVMvmInstanceUUID());
			saveToVmDetailDBTable(entityId,vmInfo);
			int vmHostId = saveToHostDBTable(vCloudRecource, vmInfo);
			if(vmHostId != 0 && entityId != 0)
				esxDao.as_edge_vsphere_entity_host_map_update(vmHostId, entityId);
		}else if (entityType == VsphereEntityType.vAPP ) {
			//VsphereEntity vdcEntity = vCloudRecource.getParent();
			//VsphereEntity orgEntity = vdcEntity.getParent();
			//VsphereEntity vCloudEntity = orgEntity.getParent();
			entityId = saveToEsxDBTable(vCloudRecource, null);
			int vappHostId = saveToHostDBTable(vCloudRecource, null);
			if(vappHostId != 0 && entityId != 0)
				esxDao.as_edge_vsphere_entity_host_map_update(vappHostId, entityId);
			saveVcloudParent(vCloudRecource);
			saveVcloudChild(vCloudRecource);
		}
		else if (entityType == VsphereEntityType.virtualDataCenter) {
			//VsphereEntity orgEntity = vCloudRecource.getParent();
			//VsphereEntity vCloudEntity = orgEntity.getParent();
			entityId = saveToEsxDBTable(vCloudRecource, null);
			saveVcloudParent(vCloudRecource);
			savePeerEntity(vCloudRecource);
			saveVcloudChild(vCloudRecource);
		}else if(entityType == VsphereEntityType.organization){
			//VsphereEntity vCloudEntity = vCloudRecource.getParent();
			entityId = saveToEsxDBTable(vCloudRecource, null);
			saveVcloudParent(vCloudRecource);
			saveVcloudChild(vCloudRecource);
		}else {
			entityId = saveToEsxDBTable(vCloudRecource, null);
			saveVcloudChild(vCloudRecource);
		}
		return entityId;
	}
	
	private void saveVcloudParent(VsphereEntity vCloudRecource){
		if(vCloudRecource == null || vCloudRecource.getEntityType() == VsphereEntityType.vCloudDirector){
			return;
		}
		int childId = vCloudRecource.getId();
		VsphereEntity parentEntity = vCloudRecource.getParent();
		int vParentId = saveToEsxDBTable(parentEntity, null);
		esxDao.as_edge_vsphere_entity_map_update(childId, vParentId, VsphereEntityRelationType.child_parent.ordinal());
		if(parentEntity.getEntityType() == VsphereEntityType.virtualDataCenter){
			savePeerEntity(parentEntity);
		}
		saveVcloudParent(vCloudRecource.getParent());
	}
	
	private void saveVcloudChild(VsphereEntity vCloudRecource)throws EdgeServiceFault{
		if(vCloudRecource == null || vCloudRecource.getChildren()==null || vCloudRecource.getChildren().isEmpty()){
			return;
		}
		List<VsphereEntity> childEntities = vCloudRecource.getChildren();
		for(VsphereEntity childEntity : childEntities){
			int childId = 0;
			if(childEntity.getEntityType() == VsphereEntityType.vm){
				VM_Info vmInfo = getVMInfoFromVcenter(childEntity);
				if(vmInfo == null)
					return;
				childId = saveToEsxDBTable(childEntity, vmInfo.getVMvmInstanceUUID());
				saveToVmDetailDBTable(childId,vmInfo);
				int vmHostId = saveToHostDBTable(childEntity, vmInfo);
				if(childId != 0 && vmHostId != 0)
					esxDao.as_edge_vsphere_entity_host_map_update(vmHostId, childId);
			}else if (childEntity.getEntityType() == VsphereEntityType.vAPP) {
				childId = saveToEsxDBTable(childEntity,null);
				int vappHostId = saveToHostDBTable(childEntity, null);
				if(vappHostId != 0 && childId != 0)
					esxDao.as_edge_vsphere_entity_host_map_update(vappHostId, childId);
			}else if(childEntity.getEntityType() == VsphereEntityType.virtualDataCenter){
				childId = saveToEsxDBTable(childEntity,null);
				savePeerEntity(childEntity);
			}else {
				childId = saveToEsxDBTable(childEntity,null);
			}
			esxDao.as_edge_vsphere_entity_map_update(childId, vCloudRecource.getId(),  VsphereEntityRelationType.child_parent.ordinal());
			saveVcloudChild(childEntity);
		}
	}

	private VM_Info getVMInfoFromVcenter(VsphereEntity vm){
		try
		{
			List<VsphereEntity> vCenters = vm.getVcenters();
			VsphereEntity vCenter = vCenters.get(0);
			String url = vCenter.getUrl();
			String protocol = url.substring(0, url.indexOf("://")).trim();
			int port = Integer.valueOf(url.substring(url.lastIndexOf(":")+1).trim());
			vCenter.setProtocol(Protocol.parse(protocol));
			vCenter.setPort(port);
			
			DiscoveryESXOption esxOption = new DiscoveryESXOption();
			esxOption.setEsxServerName( vCenter.getName() );
			esxOption.setEsxUserName( vCenter.getUserName() );
			esxOption.setEsxPassword( vCenter.getPassword() );
			esxOption.setProtocol( Protocol.parse( protocol ) );
			esxOption.setPort( port );
			esxOption.setIgnoreCertificate( true );
			
			IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
			IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( vm.getGatewayId() );
			return vmwareService.getVMInfoByMoId(esxOption, vm.getMoRef());
		}
		catch (Exception e)
		{
			logger.error( "getVMInfoFromVcenter(): Error getting VM info. VM: " + vm.getFullName(), e );
			return null;
		}
	}
	
	private void savePeerEntity(VsphereEntity vCloudRecource){
		List<VsphereEntity> vCenters = vCloudRecource.getVcenters();
		VsphereEntity vcenterEntity = vCenters.get(0);
		if(vcenterEntity != null){
			int vCenterId = saveToEsxDBTable(vcenterEntity, null);
			esxDao.as_edge_vsphere_entity_map_update(vCloudRecource.getId(), vCenterId, VsphereEntityRelationType.peer.ordinal());
			vcenterEntity.setId(vCenterId);
		}
	}
	
	private int saveToEsxDBTable(VsphereEntity vCloudRecource, String instanceUUID){
		if(StringUtil.isEmptyOrNull(instanceUUID))
			instanceUUID = vCloudRecource.getEntityId();
		List<EdgeEsx> resourceList = new ArrayList<EdgeEsx>();
		esxDao.as_edge_esx_getVsphereEntityByUuidAndName(vCloudRecource.getGatewayId().getRecordId(), instanceUUID,vCloudRecource.getName(), resourceList);
		int resourceId = resourceList.isEmpty()?0:resourceList.get(0).getId();
		int[] output = new int[1];
		esxDao.as_edge_esx_update(resourceId, 
				vCloudRecource.getName(), 
				vCloudRecource.getUserName(), 
				vCloudRecource.getPassword(), 
				vCloudRecource.getProtocol()==null?1:vCloudRecource.getProtocol().ordinal(), 
				vCloudRecource.getPort(),
				vCloudRecource.getEntityType().getValue(),
				0,
				getDescriptionBytype(vCloudRecource.getEntityType()),
				instanceUUID,
				output);
		if (resourceId == 0) {
			resourceId = output[0];
			EdgeFactory.getBean(IEdgeGatewayLocalService.class).bindEntity( vCloudRecource.getGatewayId(), resourceId, EntityType.VSphereEntity );
		}
		vCloudRecource.setId(resourceId);
		return resourceId;
	}
	
	private void saveToVmDetailDBTable(int entityId, VM_Info vminfo){
		if(entityId == 0 || vminfo == null)
			return;
		esxDao.as_edge_vsphere_vm_detail_update(entityId, IEdgeEsxDao.ESX_HOST_STATUS_VISIBLE, vminfo.getVMName(), 
				vminfo.getVMUUID(), vminfo.getvmEsxHost(),  vminfo.getVMVMX(), vminfo.getvmGuestOS());
	}
	
	private int saveToHostDBTable(VsphereEntity vCloudRecource, VM_Info vminfo){
		String instanceUUID = vCloudRecource.getEntityId();
		String vmName = vCloudRecource.getName();
		String vmHostName = vCloudRecource.getName();
		String vmIp = "";
		String vmOs = "";
		int hostType = HostTypeUtil.setVappType(0);
		int isVisible = 1;
		if(vminfo != null){
			instanceUUID = vminfo.getVMvmInstanceUUID();
			vmName = vminfo.getVMName();
			vmHostName = vminfo.getVMHostName();
			if(!StringUtil.isEmptyOrNull(vmHostName))
				vmHostName = vmHostName.toLowerCase();
			vmIp = vminfo.getVMIP();
			vmOs = vminfo.getvmGuestOS();
			hostType = HostTypeUtil.setVmOfVappType(0);
			isVisible = 2;
		}
		
		List<String> fqdnNameList = new ArrayList<String>();
		if(vCloudRecource.getGatewayId() != null && vCloudRecource.getGatewayId().isValid()){
			try {
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( vCloudRecource.getGatewayId());
				fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(vmHostName);
			} catch (Exception e) {
				logger.error("[VcloudNodeImporter] saveToHostDBTable() get fqdn name failed.",e);
			}
		}
		String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
		
		int[] ids = new int[1];
		int[] output = new int[1];
		int nodeID = -1;
		esxDao.as_edge_host_getHostByInstanceUUID(vCloudRecource.getGatewayId().getRecordId(), instanceUUID, ids);
		if(ids[0] > 0){
			nodeID = ids[0];
			String message = EdgeCMWebServiceMessages.getMessage("importNodes_NodeAlreadyExist", vmName);
			logger.debug(message);
			nodeServiceImpl.addActivityLogForImportNodes(Severity.Warning, ImportNodeType.vCloud, message);
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			hostMgrDao.as_edge_host_list(nodeID, 1, hosts);
			if(hosts.isEmpty()){
				
				//List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(vmHostName);
				hostMgrDao.as_edge_host_update(-1, new Date(), vmHostName, null,
							vmIp, vmOs, null, 2, 0, "", hostType, 1, fqdnNames, output);
				nodeID = output[0];
				connectionInfoDao.as_edge_connect_info_update(output[0], node.getUsername(), node.getPassword(),
							"", 0, 0, 0,
							"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());
			}else {
				if (hosts.get(0).getIsVisible() != isVisible) {
					hostMgrDao.as_edge_host_set_visible(ids[0], isVisible);
				}
				if(vminfo == null && !HostTypeUtil.isVapp(hosts.get(0).getRhostType())){
					hostMgrDao.as_edge_host_update_rhosttype_by_id(ids[0], HostTypeUtil.setVappType(hosts.get(0).getRhostType()));
				}else if (vminfo != null && !HostTypeUtil.isVmOfVapp(hosts.get(0).getRhostType())) {
					hostMgrDao.as_edge_host_update_rhosttype_by_id(ids[0], HostTypeUtil.setVmOfVappType(hosts.get(0).getRhostType()));
				}
			}
		} else {
			
//			List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(vmHostName);
			hostMgrDao.as_edge_host_update(-1, new Date(), vmHostName, null,
						vmIp, vmOs, null, isVisible, 0, "", hostType, 1, fqdnNames, output);
			nodeID = output[0];
			connectionInfoDao.as_edge_connect_info_update(output[0], node.getUsername(), node.getPassword(),
						"", 0, 0, 0,
						"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());
		}
		return nodeID;
	}
	
	public static String getDescriptionBytype(VsphereEntityType type){
		switch (type) {
		case vCloudDirector:
			return EdgeCMWebServiceMessages.getMessage("vcloudDesc");
		case organization:
			return EdgeCMWebServiceMessages.getMessage("vcloudOrganizationDesc");
		case vCenter:
			return EdgeCMWebServiceMessages.getMessage("vcenterDesc");
		case virtualDataCenter:
			return EdgeCMWebServiceMessages.getMessage("vcloudVdcDesc");
		case vAPP:
			return EdgeCMWebServiceMessages.getMessage("vcloudVappDesc");
		case vm:
			return EdgeCMWebServiceMessages.getMessage("vCloudVMDesc");
		default:
			return "";
		}
	}
}
