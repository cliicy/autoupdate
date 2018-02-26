package com.ca.arcserve.edge.app.base.schedulers;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DNodeStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.QueryD2DStatusJob.Status;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IEdgeGatewayService;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult.NodeManagedStatusByConsole;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public class D2DQueryTask implements Runnable {
	
	private static Logger log = Logger.getLogger(D2DQueryTask.class);

	private EdgeD2DNodeStatus info;
	private EdgeHost host;
	
	private long freeDiskSpaceToSave = -1;
	private double freeDiskPercent = 0;
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayService gatewayService = EdgeFactory.getBean( IEdgeGatewayService.class );
	private INodeService nodeService = new NodeServiceImpl();

	public D2DQueryTask(EdgeD2DNodeStatus info, EdgeHost host) {
		this.info = info;
		this.host = host;
	}

	@Override
	public void run() {
		log.debug("QueryD2DStatusJob's QueryTask running, hostname = " + info.getRhostname());
		
		Status status = Status.NA;
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(info.getRhostid())) {
			connection.connect();
			
//			if (host.getD2dManagedStatus() == NodeManagedStatus.Managed.ordinal()) {  //this value may not be the latest status
				//fix bug764204 chang.liu
				//check if the node is managed by this console
				NodeRegistrationInfo nodeRegistInfo = new NodeRegistrationInfo();
				GatewayId gatewayId = nodeService.getGatewayByHostId(host.getRhostid()).getId();
				nodeRegistInfo.setD2dPort(info.getPort());
				nodeRegistInfo.setNodeName(info.getRhostname());
				nodeRegistInfo.setD2dProtocol(Protocol.parse(info.getProtocol()));
				nodeRegistInfo.setUsername(host.getUsername());
				nodeRegistInfo.setPassword(host.getPassword());
				nodeRegistInfo.setGatewayId(gatewayId);
				NodeManageResult result = nodeService.queryNodeManagedStatus(nodeRegistInfo);
				
				if(result.getManagedStatus() == NodeManagedStatusByConsole.ManagedByCurrentConsle){ //agent is managed by current console
					if (!checkEdgeService4D2D(connection)) {
						status = Status.ERROR_D2D_CANNOT_ACCESS_EDGE;
						
						//update RegConfigPM.xml in agent
						log.info("[QueryD2DStatusJob]Agent is managed by current console, but agent can not connect to console, update RegConfigPM file.");
						gatewayService.updateRegInfoOfExistingNode(gatewayId, info.getRhostid());	
					} 
				} else {
					status = Status.ERROR_D2D_CANNOT_ACCESS_EDGE; //agent is not managed by current console
					log.info("[QueryD2DStatusJob]Agent is not managed by current console.");
				}
//			}
			
			if (status == Status.NA) {
				status = checkStatus(connection);
			}
		} catch (EdgeServiceFault e) {
			log.debug("connect to D2D failed, error message = " + e.getMessage());
			status = Status.ERRORWEBSERVICE;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Host name: " + info.getRhostname() + ", update_D2D_status: " + status.toString() + ", freeDiskSpaceToSave: "+freeDiskSpaceToSave);
		}
		
		try {
			IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostDao.as_edge_update_D2D_status(info.getRhostid(), status.ordinal(), freeDiskSpaceToSave);
		} catch (Exception e) {
			log.error("update d2d status failed.", e);
		}
	}
	
	private boolean checkEdgeService4D2D(D2DConnection connection) {
		try {
			return connection.getService().checkEdgeConnection(ApplicationType.CentralManagement);
		} catch (Exception e) {
			log.error("check edge service for D2D failed.", e);
			return false;
		}
	}

	private Status checkStatus(D2DConnection connection) {
		Status st = Status.FIT;

		boolean isDesinationValid = true;
		freeDiskPercent = 0;
		int retBkpInfoSum = 0;
		try {
			BackupInformationSummary summary = connection.getService().getBackupInformationSummary();
			if (summary == null) {
				isDesinationValid = false;
				retBkpInfoSum = GetLocalVolumnInformation(connection);
			} else {
				isDesinationValid = true;

				double totalVolumnSize = (summary.getDestinationCapacity()
						.getTotalVolumeSize())
						/ (1024 * 1024);
				double freeDiskSpace = (summary.getDestinationCapacity()
						.getTotalFreeSize())
						/ (1024 * 1024);
				if (totalVolumnSize != 0) {
					freeDiskPercent = ((freeDiskSpace * 1.0) / (totalVolumnSize * 1.0)) * 100;
					freeDiskSpaceToSave=summary.getDestinationCapacity().getTotalFreeSize();
				} else {
					freeDiskPercent = -1;
				}
			}

		} catch (WebServiceException e1) {
			log.error("checkStatus - getBackupInformationSummary:", e1);
			retBkpInfoSum = -1;
		}

		if (retBkpInfoSum == 0) {
			if (freeDiskPercent < 0.0) {
				if (!isDesinationValid) {
					st = Status.WARNINGNOSETTING; // Warning... (If destination setting
					// is not ready.)
				} else {
					st = Status.ERRORNOTACCESS;// (If destination setting is ready
					// but remove or mis configured it later)
				}
			} else if (freeDiskPercent <= 1.0) {
				st = Status.WARNINGLOWFREEDISK; // (Warning - low freedisk space....)
			} else {
				if (!isDesinationValid) {
					st = Status.WARNINGNOSETTING; // Warning... (If destination setting
					// is not ready.)
				} else {
					st = Status.FIT; // Success....
				}
			}
		} else {
			st = Status.ERRORWEBSERVICE; // ERROR
		}

		return st;
	}

	private int GetLocalVolumnInformation(D2DConnection connection) {
		try {
			Volume[] volumes = connection.getService().getVolumes();
			for (int i = 0; i < volumes.length; i++) {
				if (i == 0) {// Temporary code to get destination volume.
					double freeDiskSpace = (volumes[i].getFreeSize())
							/ (1024 * 1024);
					double totalVolumnSize = (volumes[i].getSize())
							/ (1024 * 1024);
					if (totalVolumnSize != 0.0) {
						freeDiskPercent = ((freeDiskSpace * 1.0) / (totalVolumnSize * 1.0)) * 100;
					} else {
						freeDiskPercent = -1;
					}
				}
				if (volumes[i] != null)
					volumes[i] = null;
			}
		} catch (WebServiceException e1) {
			log.error("GetLocalVolumnInformation - getVolumes:", e1);
			return -1;
		}
		return 0;
	}
}