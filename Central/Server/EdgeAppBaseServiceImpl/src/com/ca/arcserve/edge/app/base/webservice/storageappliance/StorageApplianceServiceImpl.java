package com.ca.arcserve.edge.app.base.webservice.storageappliance;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.vsphere.StorageAppliance;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.appdaos.IStorageApplianceDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IStorageApplianceService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageAppliancePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageAppliancePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceValidationResponse;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceValidationResponse.Reason;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.D2DConnectInfo;

public class StorageApplianceServiceImpl implements IStorageApplianceService {
	//Jan sprint
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	private IStorageApplianceDao infraDao = DaoFactory
			.getDao(IStorageApplianceDao.class);
	
	// Feb sprint part2
	private IEdgeVCMDao vsphereDao = DaoFactory.getDao( IEdgeVCMDao.class );
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private static final Logger logger = Logger.getLogger(StorageApplianceServiceImpl.class);

	@Override
	public void AddStorageAppliance(StorageApplianceInfo info, GatewayId gatewayId)
			throws EdgeServiceFault {
		int id = info.getId() < 0 ? 0 : info.getId();
		int[] output = new int[1];
		infraDao.as_edge_infrastructure_add_storage_appliance(id,
				info.getHostname(), info.getDataIp(), info.getUsername(), info.getPassword(),
				info.getProtocol().ordinal(), info.getPort(),info.getMode().getId(),output);
		
		if(gatewayId!=null)
			gatewayService.bindEntity(gatewayId, output[0], EntityType.StorageArray);
		// Feb sprint part2
		if(output != null)
			updateHBBUProxyforInfrastructure(gatewayId);
		
	}
	
	// Feb sprint part2 sending the saved storage
	public void updateHBBUProxyforInfrastructure(GatewayId gatewayId)
	{
		//Dec sprint
		List<HBBUProxyForStorageAppliance> proxys = new ArrayList<HBBUProxyForStorageAppliance>(); 
		vsphereDao.as_edge_vsphere_proxy_Info_list_for_StorageAppliance( proxys );
			
		// Get StorageAppliance details from DB
		List<StorageApplianceInfo> infrastructureList = new ArrayList<StorageApplianceInfo>();
		List<StorageAppliance> storageApplianceList = new ArrayList<StorageAppliance>();
		infraDao.as_edge_infrastructure_getInfrastructureList(infrastructureList);
		for (StorageApplianceInfo s : infrastructureList)
		{
			GatewayEntity gatewayEntity = null;
			try {
				gatewayEntity = gatewayService.getGatewayByEntityId(s.getId(), EntityType.StorageArray);
			} catch (EdgeServiceFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(gatewayEntity.getId().getRecordId() == gatewayId.getRecordId()) // if localgateway id and SA's gateway id are same then only push SA to proxy
			{
				StorageAppliance objStorageAppliance = new StorageAppliance();
				objStorageAppliance.setServerName(s.getHostname());
				objStorageAppliance.setDataIP(s.getDataIp());
				objStorageAppliance.setUsername(s.getUsername());
				objStorageAppliance.setPassword(s.getPassword());
				objStorageAppliance.setProtocol(s.getProtocol().toString());
				objStorageAppliance.setPort(s.getPort()+"");
				objStorageAppliance.setSystemMode(s.getMode().toString());
				storageApplianceList.add(objStorageAppliance);
			}
		}
		if(proxys.size() != 0)
		{
			for( HBBUProxyForStorageAppliance proxy : proxys ) {
				if(proxy.getMajorversion() >=6)
				{
					D2DConnectInfo d2dConnectInfo = PolicyManagementServiceImpl.getInstance().new D2DConnectInfo();
					d2dConnectInfo.setHostName( proxy.getHostname() );
					d2dConnectInfo.setPort( proxy.getPort());
					if(proxy.getProtocol() == 1)
						d2dConnectInfo.setProtocol("HTTP");
					else
						d2dConnectInfo.setProtocol("HTTPS");
						
					d2dConnectInfo.setUsername( proxy.getUsername());
					d2dConnectInfo.setDomain( EdgeCommonUtil.getDomainName(proxy.getUsername()));
					d2dConnectInfo.setPassword( proxy.getPassword() );
					d2dConnectInfo.setUuid( proxy.getUuid() );
					d2dConnectInfo.setManaged( true );
					
					GatewayEntity gateway = null;
					try {
						//210755
						gateway = gatewayService.getGatewayByEntityId( proxy.getId(), EntityType.Node );
					} catch (EdgeServiceFault e1) {
						// TODO Auto-generated catch block
						logger.error("getting gateway from host id is failed with error : " + e1.getMessage());
					}
					if(gateway.getId().getRecordId() == gatewayId.getRecordId()) // try to push to SA details to proxy only if it belong to current gateway
					{
						ConnectionContext context = new ConnectionContext(d2dConnectInfo.getProtocol(), d2dConnectInfo.getHostName(), d2dConnectInfo.getPort());
						context.buildCredential(d2dConnectInfo.getUsername(), d2dConnectInfo.getPassword(), d2dConnectInfo.getDomain());
						context.setGateway(gateway);
						context.setAuthUuid(proxy.getAuthUuid());
						//context.setAuthUuid(null);
						
						try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
							connection.connect();
							connection.getService().saveStorageAppliance(storageApplianceList);
							connection.close();
						}catch(EdgeServiceFault e) { 
							logger.error("D2D service cannot connect to this Edge service failed with error: " + e.getMessage());
						}
					}
				}
			}
		}
	}

	@Override
	public StorageAppliancePagingResult getInfrastructureListByPaging(
			StorageAppliancePagingConfig config) throws EdgeServiceFault {
		int[] totalCount = new int[1];
		List<StorageApplianceInfo> infrastructureList = new ArrayList<StorageApplianceInfo>();

		infraDao.as_edge_infrastructure_getInfrastructureList_by_paging(config
				.getPagesize(), config.getStartpos(), config.getOrderType()
				.value(), config.getGatewayId(),  "id", totalCount, infrastructureList);

		StorageAppliancePagingResult result = new StorageAppliancePagingResult();
		result.setData(infrastructureList);
		result.setStartIndex(config.getStartpos());
		result.setTotalCount(totalCount[0]);

		return result;
	}

	@Override
	public void deleteInfrastructures(int[] infrastructuresIds, GatewayId gatewayId)
			throws EdgeServiceFault {
		for (int i = 0; i < infrastructuresIds.length; i++) {
			infraDao.as_edge_infrastructure_delete(infrastructuresIds[i]);
			logger.info( "deleteInfrastructures() unbindEntity infrastructureIdToDelete: "+ infrastructuresIds[i]);
			gatewayService.unbindEntity(infrastructuresIds[i], EntityType.StorageArray);
		}
		
		// Feb sprint part2
		updateHBBUProxyforInfrastructure(gatewayId);
		
	}

	@Override
	public StorageApplianceInfo getInfrastructureById(int infraId)
			throws EdgeServiceFault {
		
		List<StorageApplianceInfo> infrastructureList = new ArrayList<StorageApplianceInfo>();
		infraDao.as_edge_infrastructure_get_by_id(infraId, infrastructureList);
		return infrastructureList.get(0);
	}
	//For AQA to use
	@Override
	public StorageApplianceInfo getInfrastructureByHostnames(String serverIp, String dataIp)
			throws EdgeServiceFault {
		List<StorageApplianceInfo> infrastructureList = new ArrayList<StorageApplianceInfo>();
		infraDao.as_edge_infrastructure_get_by_Hostnames(serverIp,dataIp,infrastructureList);
		return infrastructureList.get(0);
	}
	//Jan sprint
	@Override
	public StorageApplianceValidationResponse validateNASServer(GatewayId gatewayId, StorageApplianceInfo info) throws EdgeServiceFault {
		StorageApplianceValidationResponse response = new StorageApplianceValidationResponse();
		List<StorageApplianceInfo> infrastructureList = new ArrayList<StorageApplianceInfo>();
		//210009
		infraDao.as_edge_infrastructure_get_duplicate(info.getHostname(), info.getDataIp(), info.getUsername(), info.getPassword(), info.getProtocol().ordinal(), info.getPort(), infrastructureList);
		boolean bDuplicate = false;
		for (StorageApplianceInfo s : infrastructureList)
		{
			GatewayEntity gatewayEntity = null;
			try {
				gatewayEntity = gatewayService.getGatewayByEntityId(s.getId(), EntityType.StorageArray);
			} catch (EdgeServiceFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(gatewayEntity.getId().getRecordId() == gatewayId.getRecordId()) // if of SA to be added and current gateway are same then only duplicate
			{
				bDuplicate = true;
			}
		}
		if(bDuplicate){
			response.setReason(Reason.DUPLICATE);
			return response;
		}
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
		//NativeFacade nativeFacade = new NativeFacadeImpl();
		int statusCode = nativeFacade.validateNASServer(info.getHostname(), info.getUsername(), info.getPassword(), String.valueOf(info.getPort()), info.getProtocol().name());
		if(statusCode != 0){
			response.setReason(Reason.AUTHENTICTION);
			return response;
		}
		
		response.setReason(Reason.SUCCESS);
		return response;
	}

}
