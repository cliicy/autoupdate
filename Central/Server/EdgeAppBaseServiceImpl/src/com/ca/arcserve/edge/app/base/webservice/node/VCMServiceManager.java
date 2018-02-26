package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VCMConverterType;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;

public class VCMServiceManager {
	private static final Logger logger = Logger.getLogger(VCMServiceManager.class);
	private static final VCMServiceManager instance = new VCMServiceManager();
	private static IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private static IEdgeVSBDao vsbDao = null;
	private static IEdgeHyperVDao hyperVDao = null;
	private static INodeService nodeService = null;
	
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);

	private VCMServiceManager() {

	}

	public static VCMServiceManager getInstance() {
		return instance;
	}
	
	public int getReplicationQueueSize(NodeDetail nodeDetail) throws EdgeServiceFault {
		String nodeUUID = getVCMNodeUuid(nodeDetail);
		try (D2DConnection connection = getConverterService4Edge(nodeDetail)) {
			int replicationQueueSize = connection.getService().getReplicationQueueSize(nodeUUID);
			return replicationQueueSize;			
		}
	}

	public void changeAutoOfflieCopyStatus(NodeDetail nodeDetail, boolean status, boolean forceSmartCopy)
			throws EdgeServiceFault {
		String nodeUUID = getVCMNodeUuid(nodeDetail);
		
		try (D2DConnection connection = getConverterService4Edge(nodeDetail)) {
			if (status) {
				// int sessionCount = service.getReplicationQueueSize(nodeUUID);
				// if (sessionCount > 1)
//				if (forceSmartCopy) {
				connection.getService().forceNextReplicationMerge(nodeUUID, forceSmartCopy);
//				}

				connection.getService().enableAutoOfflieCopy(nodeUUID, true);

				// if (sessionCount >= 1) {
				connection.getService().startReplication(nodeUUID);
				// } else {
				// logger.info("There is no session data need to virtual standby.");
				// }
			} else {
				connection.getService().enableAutoOfflieCopy(nodeUUID, false);
			}
		}
	}

	public void changeHeartBeatStatus(NodeDetail nodeDetail, boolean status) throws EdgeServiceFault {
		String nodeUUID = getVCMNodeUuid(nodeDetail);
		
		try (D2DConnection connection = getConverterService4Edge(nodeDetail)) {
			if (status) {
				connection.getService().resumeHeartBeatThis(nodeUUID);
			} else {
				connection.getService().pauseHeartBeatThis(nodeUUID);
			}
		}
	}

	public String getVCMNodeUuid(NodeDetail nodeDetail) throws EdgeServiceFault {
		HostConnectInfo converter = getNodeService().getVCMConverterByHostId(nodeDetail.getId());
		if (converter == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_ConverterNotFound,
					"Converter not founded for node:" + nodeDetail.getId());
		}
		D2DConnectInfo connectionInfo = nodeDetail.getD2dConnectInfo();
		String nodeUUID = connectionInfo.getUuid();
		VCMConverterType converterType = converter.getConverterType();
		if (VCMConverterType.HBBUProxy == converterType || VCMConverterType.HBBUProxy2RPSServer == converterType
				|| VCMConverterType.RPSServer2RPSServerForHBBU == converterType) {
			if (nodeDetail.isVMwareMachine()) {
				List<EdgeEsxVmInfo> vmList = new LinkedList<>();
				esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(nodeDetail.getId(), vmList);
				
				if (vmList.size() > 0) {
					nodeUUID = vmList.get(0).getVmInstanceUuid();
				} else {
					logger.error("Failed to fetch instance uuid for vmware virtual machine node. "+nodeDetail.getId());
				}
			} else if (nodeDetail.isHyperVMachine()) {
				List<EdgeHyperVHostMapInfo> hostMapInfo = new ArrayList<EdgeHyperVHostMapInfo>();
				getHyperVDao().as_edge_hyperv_host_map_getById(nodeDetail.getId(), hostMapInfo);
				if (hostMapInfo.size() > 0) {
					nodeUUID = hostMapInfo.get(0).getVmInstanceUuid();
				} else {
					logger.error("Failed to fetch instance uuid for hyper-v virtual machine node.");
				}
			}
		}
		return nodeUUID;
	}

	public D2DConnection getConverterService4Edge(NodeDetail nodeDetail) throws EdgeServiceFault {
		HostConnectInfo converter = getNodeService().getVCMConverterByHostId(nodeDetail.getId());
		if (converter == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.EdgeVCM_ConverterNotFound,
					"Converter not founded for node:" + nodeDetail.getId());
		}
		
		String protocol = converter.getProtocol() == Protocol.Https ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, converter.getHostName(), converter.getPort());
		context.buildAuthUuid(converter.getAuthUuid());
		context.buildCredential(converter.getUserName(), converter.getPassword(), "");
		GatewayEntity gateway = gatewayService.getGatewayByEntityId(converter.getId(), EntityType.Converter);
		context.setGateway(gateway);

		D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context));
		
		try {
			connection.connect();
			
			getVsbDao().as_edge_vsb_converter_cu(0, converter.getHostId(), converter.getHostName(), converter.getPort(),
					converter.getProtocol().ordinal(), converter.getUserName(), converter.getPassword(), connection.getNodeUuid(),
					connection.getAuthUuid(), new int[1], new int[1]);
			
			return connection;
		} catch (EdgeServiceFault e) {
			connection.close();
			throw e;
		}
	}

	private INodeService getNodeService() {
		if (nodeService == null) {
			nodeService = new NodeServiceImpl();
		}
		return nodeService;
	}

	public static IEdgeVSBDao getVsbDao() {
		if (vsbDao == null) {
			vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
		}
		return vsbDao;
	}

	public static IEdgeHyperVDao getHyperVDao() {
		if (hyperVDao == null) {
			hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
		}
		return hyperVDao;
	}
}
