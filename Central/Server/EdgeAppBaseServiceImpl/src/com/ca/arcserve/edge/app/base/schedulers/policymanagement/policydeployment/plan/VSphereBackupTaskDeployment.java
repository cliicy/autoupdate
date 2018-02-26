package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService.SettingsTypes;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVSphereProxyInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployUIWarningWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentLogWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception.DeploymentException;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.D2DConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.GetPolicyContentXmlException;

public class VSphereBackupTaskDeployment extends TaskDeploymentExceptionHandler implements ITaskDeployment {

	private static Logger logger = Logger.getLogger(VSphereBackupTaskDeployment.class);
	private PolicyManagementServiceImpl policyManagementServiceImpl= PolicyManagementServiceImpl.getInstance();
	private PolicyDeploymentLogWriter activityLogWriter = PolicyDeploymentLogWriter.getInstance();
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHostMgrDao edgeHostMgrDao = DaoFactory.getDao( IEdgeHostMgrDao.class );
	private PolicyDeployUIWarningWriter warningErrorMessageWriter = new PolicyDeployUIWarningWriter();
	private IEdgeHyperVDao edgeHypervDao= DaoFactory.getDao(IEdgeHyperVDao.class);
	private long edgeTaskId = PolicyManagementServiceImpl.getTaskId();
	
	@Override
	public boolean deployTask(PolicyDeploymentTask task, UnifiedPolicy plan, boolean updateDeployStatusOnSuccess) {
		logger.info("Start deploy unified vm backup policy");
		boolean result = deployUnifiedPolicy(task, plan, updateDeployStatusOnSuccess);
		logger.info("End deploy unified vm backup policy");
		return result;
	}

	@Override
	public boolean removeTask(PolicyDeploymentTask task, boolean updateDeployStatusOnSuccess) {
		logger.info("Start remove unified vm backup policy");
		boolean result = removeUnifiedPolicy(task, updateDeployStatusOnSuccess);
		logger.info("End remove unified vm backup policy");
		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean deployUnifiedPolicy(PolicyDeploymentTask task, UnifiedPolicy plan, boolean updateDeployStatusOnSuccess) {
		List<Integer> vmIds = (List<Integer>) task.getTaskParameters();
		int proxyHostId = plan.getVSphereBackupConfiguration().getvSphereProxy().getVSphereProxyHostID();
		String proxyHostname = plan.getVSphereBackupConfiguration().getvSphereProxy().getVSphereProxyName();
		List<PolicyDeploymentError> errorList = null;
		boolean result = false;

		try {
			List<EdgeConnectInfo> lstProxy = new ArrayList<EdgeConnectInfo>();
			connectInfoDao.as_edge_proxy_by_policyid(task.getPolicyId(), lstProxy);
			if (lstProxy.size() > 0) {				
				proxyHostname = lstProxy.get(0).getRhostname();
			}
			checkUndeployment(vmIds, proxyHostname, task);

			errorList = policyManagementServiceImpl.deployPolicyVM(task.getHostId(), task.getPolicyType(), task.getPolicyId(), vmIds, plan);
			Map<Integer, List<PolicyDeploymentError>> deployResult = getDeployResult(vmIds, errorList);
			//saveVSphereProxy(plan, deployResult);
			clearPolicyDeployingCache(plan, deployResult);			
			updateDeployStatusAndLog(edgeTaskId, task, deployResult, proxyHostname, updateDeployStatusOnSuccess);
			this.warningErrorMessageWriter.addWarningErrorMessageFromD2D4VM(vmIds, errorList, proxyHostname, false);
			
			result = isDeploymentSuccessful(deployResult);
		} catch (Exception e) {
			super.handleException("deployVspereTask",task, e, proxyHostId, proxyHostname);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean removeUnifiedPolicy(PolicyDeploymentTask task, boolean updateDeployStatusOnSuccess) {
		String proxyHostname = "";
		int proxyId = 0;
		boolean result = false;
		try {
			List<EdgeVSphereProxyInfo> proxyList = this.policyManagementServiceImpl.getHBBUPlanProxyByHostId(task.getHostId());
			proxyHostname = proxyList.get(0).getHostname();
			proxyId = proxyList.get(0).getId();
			List<Integer> vmIds = (List<Integer>) task.getTaskParameters();
			
			boolean keepCurrentSettings = 
				(task.getDeployFlags() & PolicyDeployFlags.KeepCurrentSettingsWhenUnassin) != 0;

			List<PolicyDeploymentError> errorList =
				this.policyManagementServiceImpl.removePolicyVM(
					task.getHostId(),
					PolicyTypes.VMBackup,
					task.getPolicyId(),
					keepCurrentSettings,
					vmIds);
			
			Map<Integer, List<PolicyDeploymentError>> deployResult = getDeployResult(vmIds, errorList);
			updateDeployStatusAndLog(edgeTaskId, task, deployResult, proxyHostname, updateDeployStatusOnSuccess);
			this.warningErrorMessageWriter.addWarningErrorMessageFromD2D4VM(vmIds, errorList, proxyHostname, true);
			
			if (isDeploymentSuccessful(deployResult)) {
				result = true;
			}
		} catch (Exception e) {
			super.handleException("removeVsphereTask",task, e, proxyId, proxyHostname);
		}
		
		if ((task.getDeployFlags() & PolicyDeployFlags.UnregisterNodeAfterUnassign) != 0) {
			for (int nodeId : this.getVmIdList(task)) {
				try {
					// defect 218413 
					//this.edgePolicyDao.deleteHostPolicyMap(nodeId, task.getPolicyType());//Redundant code
					//this.edgeHostMgrDao.as_edge_host_remove(task.getHostId());// A task may have any node
					this.edgeHostMgrDao.as_edge_host_remove(nodeId);
					logger.info("VSphereBackupTaskDeployment.removeUnifiedPolicy(): delete node, nodeId:" + nodeId);
				} catch (Exception e) {
					logger.warn("removeUnifiedPolicy(): Deleting host policy map and unregister " +
						"D2D after unassign policy failed." ,e);
				}
			}
		}
		return result;
	}
	
	private void clearPolicyDeployingCache(UnifiedPolicy plan, Map<Integer, List<PolicyDeploymentError>> deployResult)
			throws EdgeServiceFault, GetPolicyContentXmlException, Exception {
		logger.info( "clearDeployResult(): Begin to clear deloy result." );
		List<Integer> successfulVmIdList = new ArrayList<Integer>();
		List<Integer> errorVmIdList = new ArrayList<Integer>();

		for (Integer vmId : deployResult.keySet()) {
			List<PolicyDeploymentError> vmErrorList = deployResult.get(vmId);
			if (!DeployUtil.hasError(vmErrorList)) {
				successfulVmIdList.add(vmId);
			} else {
				errorVmIdList.add(vmId);
			}
		}
		this.policyManagementServiceImpl.clearPolicyDeployingCache(plan.getId(), successfulVmIdList, errorVmIdList, plan);
		logger.info( "clearDeployResult(): end clear deloy result." );
	}
	
	private void updateDeployStatusAndLog(long edgeTaskId2, PolicyDeploymentTask task,
			Map<Integer, List<PolicyDeploymentError>> deployResult, String proxyHostname,
			boolean updateDeployStatusOnSuccess) throws DeploymentException {
		logger.info( "updateDeployStatusAndLog(): Begin to update deployment status and logs." );
		for (Integer vmId : deployResult.keySet()) {
			task.setHostId(vmId);
			List<PolicyDeploymentError> vmErrorList = deployResult.get(vmId);

			boolean hasError = DeployUtil.hasError(vmErrorList);
			logger.info( "updateDeployStatusAndLog(): VM ID: " + vmId +
				", Error list size: " + vmErrorList.size() + ", Has errors: " + hasError );
			if (hasError || updateDeployStatusOnSuccess) {
				DeployUtil.setDeployStatus(task, !hasError);
			}

			this.activityLogWriter.addDeploymentLogs4VM(edgeTaskId, task, vmErrorList, proxyHostname);

			if (hasError) {
				this.activityLogWriter.addDeploymentFailedLog(edgeTaskId, task, null);
			} else {
				if (updateDeployStatusOnSuccess) {
					this.activityLogWriter.addDeploymentSucceedLog(edgeTaskId, task, null);
				}
			}
		}
		
		logger.info( "updateDeployStatusAndLog(): Updating deployment status and logs finished." );
	}
	
	private void checkUndeployment(List<Integer> vmIds, String proxyHostname, PolicyDeploymentTask task) throws EdgeServiceFault {
		for (Integer vmid : vmIds) {
			List<EdgeVSphereProxyInfo> list = new ArrayList<EdgeVSphereProxyInfo>();
			boolean needUndeploy = this.policyManagementServiceImpl.checkNeedToDoUndeploy(vmid, list);
			
			if (!needUndeploy || list.isEmpty()) {
				continue;
			}
			
			EdgeVSphereProxyInfo proxy = list.get(0);
			if (proxyHostname.equals(proxy.getHostname())) {
				continue;
			}
			
			D2DConnectInfo d2dConnectInfo = new PolicyManagementServiceImpl().new D2DConnectInfo();
			
			d2dConnectInfo.setHostName(proxy.getHostname());
			d2dConnectInfo.setPort(proxy.getPort());
			d2dConnectInfo.setProtocol(proxy.getProtocol()==Protocol.Https.ordinal()?"https":"http");
			d2dConnectInfo.setUsername(proxy.getUsername());
			d2dConnectInfo.setPassword(proxy.getPassword());
			d2dConnectInfo.setUuid(null);
			
			List<Integer> oldVmIds = new ArrayList<Integer>();
			oldVmIds.add(vmid);
			
			try {
				policyManagementServiceImpl.removePolicyVM(d2dConnectInfo, PolicyTypes.VMBackup, false, oldVmIds,true);
			} catch (Exception e) {
				logger.error("checkUndeployment:Remove policy VM mapping failed.",e);
			}
		}
	}
	
	private boolean isWholePolicyError(PolicyDeploymentError error) {
		if (error.getSettingsType() == SettingsTypes.WholePolicy
				&& error.getErrorType() == PolicyDeploymentError.ErrorTypes.Error) {
			return true;
		}
		return false;
	}
	
	private Map<Integer, List<PolicyDeploymentError>> getDeployResult(List<Integer> vmIdList, List<PolicyDeploymentError> errorList) throws Exception {
		Map<Integer, List<PolicyDeploymentError>> result = new HashMap<Integer, List<PolicyDeploymentError>>();
		
		for (Integer vmId : vmIdList) {
			if (!result.containsKey(vmId)) {
				result.put(vmId, new ArrayList<PolicyDeploymentError>());
			}
		}
		
		// when this is the whole error
		if (errorList.size() == 1) {
			PolicyDeploymentError error = errorList.get(0);
			List<PolicyDeploymentError> tempErrorList = new ArrayList<PolicyDeploymentError>();
			
			if (error.getVmInstanceUuid() == null && isWholePolicyError(error)) {
				List<EdgeHost> hosts = new LinkedList<EdgeHost>();
				for (int hostId : vmIdList) {
					hosts.clear();
					edgeHostMgrDao.as_edge_host_list(hostId, 1, hosts);
					if (hosts.size() == 0) {
						continue;
					}
					EdgeHost host = hosts.get(0);

					PolicyDeploymentError tempError = new PolicyDeploymentError();
					tempError.setErrorCode(error.getErrorCode());
					tempError.setErrorParameters(error.getErrorParameters());
					tempError.setErrorType(error.getErrorType());
					tempError.setHostType(error.getHostType());
					tempError.setNodeName(error.getNodeName());
					tempError.setPolicyType(error.getPolicyType());
					tempError.setSettingsType(error.getSettingsType());
					tempError.setVmInstanceUuid(host.getVmInstanceUuid());
					
					tempErrorList.add(tempError);
				}
			}
			
			if (!tempErrorList.isEmpty()) {
				errorList.clear();
				errorList.addAll(tempErrorList);
			}
		}
		
		for (PolicyDeploymentError error : errorList) {
			int[] vmId = new int[1];
			esxDao.as_edge_host_getHostByInstanceUUID(0, error.getVmInstanceUuid(), vmId);	//TODO: find gateway
			if (vmId[0] == 0) {
				edgeHypervDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(0, error.getVmInstanceUuid(), vmId);	//TODO: find gateway
			}
			
			if (vmId[0] == 0) {
				continue;
			}
			
			if (result.containsKey(vmId[0])) {
				result.get(vmId[0]).add(error);
			}
		}
		
		return result;
	}
	
	private boolean isDeploymentSuccessful(Map<Integer, List<PolicyDeploymentError>> deployResult) {
		for (Integer vmId : deployResult.keySet()) {
			List<PolicyDeploymentError> vmErrorList = deployResult.get(vmId);
			if (vmErrorList.isEmpty()) {
				continue;
			}
			
			for (PolicyDeploymentError error : vmErrorList) {
				if (error.getErrorType() == PolicyDeploymentError.ErrorTypes.Error) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void setDeployStatusForAll(PolicyDeploymentTask task, boolean isSuccessful, boolean forceunassign, int status) {
		List<Integer> vmIdList = this.getVmIdList(task);
		
		for (Integer vmId : vmIdList) {
			task.setHostId(vmId);
			try {
				DeployUtil.setDeployStatus( task, isSuccessful, isSuccessful ? 0 : status, forceunassign );
			} catch (Exception e) {
				// donothing
				//set deploy status, ignor exception
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getVmIdList(PolicyDeploymentTask task) {
		return (List<Integer>) task.getTaskParameters();
	}

	@Override
	void updateDeployStatus(PolicyDeploymentTask task, int policyDeployStatus) {
		setDeployStatusForAll(task, false, false,PolicyDeployStatus.DeploymentFailed);
	}

	@Override
	void writeActivityLogAndDeployMessage(PolicyDeploymentTask task,
			String message, String nodeName) {
		this.activityLogWriter.addDeploymentFailedLog4VM(edgeTaskId, task, message);
		this.warningErrorMessageWriter.addErrorMessage4VM(task, nodeName, message);
	}

	@Override
	String getMessageSubject() {
		return EdgeCMWebServiceMessages.getResource("proxy");
	}
}
