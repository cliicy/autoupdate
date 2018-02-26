package com.ca.arcserve.edge.app.base.schedulers;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
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
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;

public class VMQueryTask implements Runnable {
	
	private static Logger log = Logger.getLogger(VMQueryTask.class);
	
	private EdgeD2DNodeStatus info;
	private EdgeHost proxyNode;
	private EdgeHost vmNode;
	
	private long freeDiskSpaceToSave = -1;
	private double freeDiskPercent = 0;
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);

	public VMQueryTask(EdgeHost proxyNode, EdgeHost vmNode, EdgeD2DNodeStatus info) {
		this.proxyNode = proxyNode;
		this.vmNode = vmNode;
		this.info = info;
	}
	
	@Override
	public void run() {
		log.debug(Utils.getMessage("QueryProxyStatusJob's QueryTask running, hostname: {0}", info.getRhostname()));
		
		Status status = Status.NA;
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(info.getRhostid())) {
			connection.connect();
			
			if (proxyNode.getD2dManagedStatus() == NodeManagedStatus.Managed.ordinal()) {
				if (!checkEdgeService4D2D(connection)) {
					status = Status.ERROR_D2D_CANNOT_ACCESS_EDGE;
				}
			}
			
			if (status == Status.NA) {
				status = checkStatus(connection);
			}
		} catch (EdgeServiceFault e) {
			log.debug("connect to VM backup proxy failed, error message = " + e.getMessage());
			status = Status.ERRORWEBSERVICE;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("update_vm_node_status: " + status.toString() + ", freeDiskSpaceToSave: " + freeDiskSpaceToSave);
		}
		
		try {
			IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostDao.as_edge_update_D2D_status(vmNode.getRhostid(), status.ordinal(), freeDiskSpaceToSave);
		} catch (Exception e) {
			log.error("update vm status failed.", e);
		}
	}
	
	private boolean checkEdgeService4D2D(D2DConnection connection) {
		try {
			if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.CentralManagement) {
				return connection.getService().checkEdgeConnection(ApplicationType.CentralManagement);
			} else if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.VirtualConversionManager) {
				return connection.getService().checkEdgeConnection(ApplicationType.VirtualConversionManager);
			} else if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.vShpereManager) {
				return connection.getService().checkEdgeConnection(ApplicationType.vShpereManager);
			} else {
				log.error("Invalid application type: " + EdgeWebServiceContext.getApplicationType());
				return false;
			}
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
			VirtualMachine vm = new VirtualMachine();
			vm.setVmUUID(vmNode.getVmInstanceUuid());
			vm.setVmInstanceUUID(vmNode.getVmInstanceUuid());
			vm.setVmHostName(vmNode.getVmname());
			BackupInformationSummary summary = connection.getService().getVMBackupInformationSummary(vm);
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
