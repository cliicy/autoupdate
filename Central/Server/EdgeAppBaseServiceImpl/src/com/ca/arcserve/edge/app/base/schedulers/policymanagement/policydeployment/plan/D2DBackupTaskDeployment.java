package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployUIWarningWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentLogWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception.DeploymentException;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public class D2DBackupTaskDeployment extends TaskDeploymentExceptionHandler implements ITaskDeployment{
	
	private static Logger logger = Logger.getLogger(D2DBackupTaskDeployment.class);
	private static PolicyManagementServiceImpl policyManagementServiceImpl = PolicyManagementServiceImpl.getInstance();
	private static PolicyDeploymentLogWriter activityLogWriter = PolicyDeploymentLogWriter.getInstance();
	private static PolicyDeployUIWarningWriter warningErrorMessageWriter = new PolicyDeployUIWarningWriter();
	private long edgeTaskId = PolicyManagementServiceImpl.getTaskId();
	
	@Override
	public boolean deployTask(PolicyDeploymentTask argument, UnifiedPolicy plan, boolean updateDeployStatusOnSuccess) {
		boolean result = false;
		String nodeName = "";
		try {
			EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo(argument.getHostId());
			nodeName = hostInfo.getRhostname();
			
			List<PolicyDeploymentError> errorList = policyManagementServiceImpl.deployUnifiedBackupPolicy(argument.getHostId(), argument.getPolicyId(),plan.getUuid());
			
			boolean hasError = DeployUtil.hasError(errorList);
			
			if (hasError || updateDeployStatusOnSuccess) {
				DeployUtil.setDeployStatus(argument, !hasError);
			}
			
			activityLogWriter.addDeploymentLogs(edgeTaskId, argument, errorList);
			
			if (!DeployUtil.hasError(errorList)) {
				if (updateDeployStatusOnSuccess) {
					activityLogWriter.addDeploymentSucceedLog(edgeTaskId, argument, nodeName);
				}
			} else {
				activityLogWriter.addDeploymentFailedLog(edgeTaskId, argument, nodeName);
			}
			
			warningErrorMessageWriter.addWarningErrorMessageFromD2D(argument, errorList, false);
			
			result = !hasError;
			DeployUtil.forceCheckAgentStatus(argument.getHostId());
			
		}catch (Exception e){
			super.handleException("deployD2DTask",argument, e, argument.getHostId(), nodeName);
		}
		return result;
	}

	@Override
	public boolean removeTask(PolicyDeploymentTask argument, boolean updateDeployStatusOnSuccess) {
		boolean result = false;
		String nodeName = "";
		try {
			EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo(argument.getHostId());
			nodeName = hostInfo.getRhostname();
			
			boolean keepCurrentSettings = (argument.getDeployFlags() & PolicyDeployFlags.KeepCurrentSettingsWhenUnassin) != 0;
			
			List<PolicyDeploymentError> errorList = policyManagementServiceImpl.removeUnifiedBackupPolicy(argument.getHostId(), keepCurrentSettings);
			
			boolean hasError = DeployUtil.hasError(errorList);
			
			if (hasError || updateDeployStatusOnSuccess) {
				DeployUtil.setDeployStatus(argument, !hasError);
			}
			
			activityLogWriter.addDeploymentLogs(edgeTaskId, argument, errorList);
			
			if (!DeployUtil.hasError(errorList)) {
				if (updateDeployStatusOnSuccess) {
					activityLogWriter.addDeploymentSucceedLog(edgeTaskId, argument, null);
				}
			} else {
				activityLogWriter.addDeploymentFailedLog(edgeTaskId, argument, null);
			}
			
			warningErrorMessageWriter.addWarningErrorMessageFromD2D(argument, errorList, true);
			
			result = !hasError;
		}catch (Exception e){
			super.handleException("removeD2DTask",argument, e, argument.getHostId(), nodeName);
		}
		
		return result;
	}


	@Override
	void updateDeployStatus(PolicyDeploymentTask argument,int policyDeployStatus) {
		try {
			DeployUtil.setDeployStatus(argument, false,policyDeployStatus);
		} catch (DeploymentException e) {
			logger.error("[D2DBackupTaskDeployment]:updateDeployStatus() failed.",e);
		}
	}

	@Override
	void writeActivityLogAndDeployMessage(PolicyDeploymentTask argument,
			String message, String nodeName) {
		activityLogWriter.addDeploymentFailedLog(edgeTaskId, argument,
				null, message);
		warningErrorMessageWriter.addErrorMessage(argument,
				null, message);
	}


	@Override
	String getMessageSubject() {
		return EdgeCMWebServiceMessages.getResource("node");
	}
}
