package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.D2DServiceUtils;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.NodeConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.VMConnectionContextProvider;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class MergeJobManager {
	
	private static MergeJobManager instance = new MergeJobManager();
	
	private static Logger logger = Logger.getLogger(MergeJobManager.class);
	private IActivityLogService logService = new ActivityLogServiceImpl();
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	public static MergeJobManager getInstance() { 
		return instance;
	}
	
	private MergeJobManager() {
	}
	
	public int changeMergeJob(int nodeId, boolean enable) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(nodeId)) {
			connection.connect();
			
			if (enable) {
				return connection.getService().resumeMergeEx(MergeAPISource.MANUALLY);
			} else {
				return connection.getService().pauseMergeEx(MergeAPISource.MANUALLY);
			}
		}
	}

	public void pauseMultipleMergeJob(final int[] nodeIds) throws EdgeServiceFault {
		startChangeMergeJob(nodeIds, new Runnable() {
			
			@Override
			public void run() {
				for (int id : nodeIds) {
					ConnectionContext context;
					
					try {
						context = new NodeConnectionContextProvider(id).create();
					} catch (Exception e) {
						logger.warn("Failed to get the connection context for node id " + id + ". " + e.getMessage());
						continue;
					}
					
					try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
						connection.connect();
						connection.getService().pauseMergeEx(MergeAPISource.MANUALLY);
						addLog(Severity.Information, context.getHost(), EdgeCMWebServiceMessages.getResource("mergeJob_Pause_Successful"));
					} catch (SOAPFaultException e) {
						addLog(Severity.Error, context.getHost(), EdgeCMWebServiceMessages.getMessage("mergeJob_Pause_ServerFail", D2DServiceUtils.getD2DErrorMessage(e)));
					} catch (WebServiceException e) {
						addLog(Severity.Error, context.getHost(), EdgeCMWebServiceMessages.getResource("mergeJob_Pause_ConnectD2DFail"));
					} catch (Exception e) {
						logger.error("pause merge job failed for node id " + id, e);
						addLog(Severity.Error, context.getHost(), EdgeCMWebServiceMessages.getResource("mergeJob_Pause_Fail"));
					}
				}
			}
			
		});
	}
	
	private void addLog(Severity severity, String nodeName, String message) {
		ActivityLog log = new ActivityLog();
		
		if (nodeName == null || nodeName.isEmpty()) {
			nodeName = EdgeCMWebServiceMessages.getResource("policyDeployment_UnknownNode");
		}
		
		log.setNodeName(nodeName);
		log.setSeverity(severity);
		log.setModule(Module.MergeJob);
		log.setMessage(message);
		
		try {
			logService.addLog(log);
		} catch (Exception e) {
			logger.error("Add log failed.", e);
		}
	}
	
	private void startChangeMergeJob(int[] nodeIds, Runnable task) {
		if (nodeIds == null || nodeIds.length == 0) {
			return;
		}
		
		EdgeExecutors.getCachedPool().submit(task);
	}

	public void resumeMultipleMergeJob(final int[] nodeIds) throws EdgeServiceFault {
		startChangeMergeJob(nodeIds, new Runnable() {
			
			@Override
			public void run() {
				for (int id : nodeIds) {
					ConnectionContext context;
					
					try {
						context = new NodeConnectionContextProvider(id).create();
					} catch (Exception e) {
						logger.warn("Failed to get connection context for node id " + id + ". " + e.getMessage());
						continue;
					}
					
					try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
						connection.connect();
						connection.getService().resumeMergeEx(MergeAPISource.MANUALLY);
						addLog(Severity.Information, context.getHost(), EdgeCMWebServiceMessages.getResource("mergeJob_Resume_Successful"));
					} catch (SOAPFaultException e) {
						addLog(Severity.Error, context.getHost(), EdgeCMWebServiceMessages.getMessage("mergeJob_Resume_ServerFail", D2DServiceUtils.getD2DErrorMessage(e)));
					} catch (WebServiceException e) {
						addLog(Severity.Error, context.getHost(), EdgeCMWebServiceMessages.getResource("mergeJob_Resume_ConnectD2DFail"));
					} catch (Exception e) {
						logger.error("resume merge job failed for node id " + id, e);
						addLog(Severity.Error, context.getHost(), EdgeCMWebServiceMessages.getResource("mergeJob_Resume_Fail"));
					}
				}
			}
			
		});
	}
	
	public int changeVMMergeJob(int vmHostId, boolean enable) throws EdgeServiceFault {
		String vmInstanceUuid = getVmInstanceUuid(vmHostId);
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new VMConnectionContextProvider(vmHostId))) {
			connection.connect();
			
			if (enable) {
				return connection.getService().resumeVMMergeEx(MergeAPISource.MANUALLY, vmInstanceUuid);
			} else {
				return connection.getService().pauseVMMergeEx(MergeAPISource.MANUALLY, vmInstanceUuid);
			}
		}
	}
	
	private String getVmInstanceUuid(int vmHostId) throws EdgeServiceFault {
		List<EdgeEsxVmInfo> vmList = new LinkedList<>();
		esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(vmHostId, vmList);
		if(vmList.isEmpty()){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		return vmList.get(0).getVmInstanceUuid();
	}

	public void pauseMultipleVMMergeJob(final int[] vmHostIds) throws EdgeServiceFault {
		startChangeMergeJob(vmHostIds, new Runnable() {
			
			@Override
			public void run() {
				for (int id : vmHostIds) {
					ConnectionContext context;
					String vmInstanceUuid;
					
					try {
						context = new VMConnectionContextProvider(id).create();
						vmInstanceUuid = getVmInstanceUuid(id);
					} catch (Exception e) {
						logger.warn("Failed to get the connection context or instance UUID for VM id " + id + ". " + e.getMessage());
						continue;
					}
					
					try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
						connection.connect();
						connection.getService().pauseVMMergeEx(MergeAPISource.MANUALLY, vmInstanceUuid);
						addLog(Severity.Information, getHostname(id), EdgeCMWebServiceMessages.getResource("mergeJob_Pause_Successful"));
					} catch (SOAPFaultException e) {
						addLog(Severity.Error, getHostname(id), EdgeCMWebServiceMessages.getMessage("mergeJob_Pause_ServerFail", D2DServiceUtils.getD2DErrorMessage(e)));
					} catch (WebServiceException e) {
						addLog(Severity.Error, getHostname(id), EdgeCMWebServiceMessages.getMessage("mergeJob_Pause_ConnectProxyFail", context.getHost()));
					} catch (Exception e) {
						logger.error("pause vm merge job failed.", e);
						addLog(Severity.Error, getHostname(id), EdgeCMWebServiceMessages.getResource("mergeJob_Pause_Fail"));
					}
				}
			}
			
		});
	}
	
	private String getHostname(int nodeId) {
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(nodeId, 1, hostList);
		return hostList.isEmpty() ? "" : hostList.get(0).getRhostname();
	}

	public void resumeMultipleVMMergeJob(final int[] vmHostIds) throws EdgeServiceFault {
		startChangeMergeJob(vmHostIds, new Runnable() {
			
			@Override
			public void run() {
				for (int id : vmHostIds) {
					ConnectionContext context;
					String vmInstanceUuid;
					
					try {
						context = new VMConnectionContextProvider(id).create();
						vmInstanceUuid = getVmInstanceUuid(id);
					} catch (Exception e) {
						logger.warn("Failed to get the connection context or instance uuid for VM id " + id + ". " + e.getMessage());
						continue;
					}
					
					try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
						connection.connect();
						connection.getService().resumeVMMergeEx(MergeAPISource.MANUALLY, vmInstanceUuid);
						addLog(Severity.Information, getHostname(id), EdgeCMWebServiceMessages.getResource("mergeJob_Resume_Successful"));
					} catch (SOAPFaultException e) {
						addLog(Severity.Error, getHostname(id), EdgeCMWebServiceMessages.getMessage("mergeJob_Resume_ServerFail", D2DServiceUtils.getD2DErrorMessage(e)));
					} catch (WebServiceException e) {
						addLog(Severity.Error, getHostname(id), EdgeCMWebServiceMessages.getMessage("mergeJob_Resume_ConnectProxyFail", context.getHost()));
					} catch (Exception e) {
						logger.error("resume vm merge job failed.", e);
						addLog(Severity.Error, getHostname(id), EdgeCMWebServiceMessages.getResource("mergeJob_Resume_Fail"));
					}
				}
			}
			
		});
	}

}
