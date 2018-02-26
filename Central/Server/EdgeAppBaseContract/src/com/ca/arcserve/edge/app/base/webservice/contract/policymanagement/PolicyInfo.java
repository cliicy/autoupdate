/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatusForPlan;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * Statistic information and status information of a policy / plan.
 * 
 * @author lijwe02
 * 
 */
@XmlSeeAlso( {
	PolicyTypes.class,
	PlanTaskType.class
	} )
public class PolicyInfo implements Serializable, BeanModelTag {
	private int policyId;
	private String policyUuid;
	private int policyType;
	private PlanStatus policyStatus;
	private int protectedNodeCount;
	private int successNodeCount;
	private int failedNodeCount;
	private int deployingNodeCount;
	private int deployingD2DNodeCount;
	private int toBeDeployedNodeCount;
	private int scheduleDeployedNodeCount;
	private String policyName;
	private static final long serialVersionUID = 1L;
	private PlanEnableStatus enabled;
	private JobStatusForPlan jobStatusForPlan;
	private String enableMessage;
	private String deployErrorMessage;
	private int activeJobCount;
	private int sucNodeCount;
	private int warningNodeCount;
	private int errNodeCount;
	private int contentflag;
	private int planDeployReason;
	private int gatewayId;
	private String siteName;
	
	public JobStatusForPlan getJobStatusForPlan() {
		return jobStatusForPlan;
	}

	public void setJobStatusForPlan(JobStatusForPlan jobStatusForPlan) {
		this.jobStatusForPlan = jobStatusForPlan;
	}

	public PolicyInfo() {

	}

	/**
	 * Get ID of the policy.
	 * 
	 * @return
	 */
	public int getPolicyId() {
		return policyId;
	}

	/**
	 * Set ID of the policy.
	 * 
	 * @param policyId
	 */
	public void setPolicyId(int policyId) {
		this.policyId = policyId;
	}

	/**
	 * Get type of the policy. See {@link PolicyTypes} for the definitions.
	 * 
	 * @return
	 */
	public int getPolicyType() {
		return policyType;
	}

	/**
	 * Set type of the policy. See {@link PolicyTypes} for the definitions.
	 * 
	 * @param policyType
	 */
	public void setPolicyType(int policyType) {
		this.policyType = policyType;
	}

	/**
	 * Get policy name.
	 * 
	 * @return
	 */
	public String getPolicyName() {
		return policyName;
	}

	/**
	 * Set policy name.
	 * 
	 * @param policyName
	 */
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	/**
	 * Get status of the policy.
	 * 
	 * @return
	 */
	public PlanStatus getPolicyStatus() {
		return policyStatus;
	}

	/**
	 * Set status of the policy.
	 * 
	 * @param policyStatus
	 */
	public void setPolicyStatus(PlanStatus policyStatus) {
		this.policyStatus = policyStatus;
	}

	/**
	 * Get number of nodes who is using the policy.
	 * 
	 * @return
	 */
	public int getProtectedNodeCount() {
		return protectedNodeCount;
	}

	/**
	 * Set number of nodes who is using the policy.
	 * 
	 * @param protectedNodeCount
	 */
	public void setProtectedNodeCount(int protectedNodeCount) {
		this.protectedNodeCount = protectedNodeCount;
	}

	/**
	 * Get the number of nodes to whom the policy was deployed successfully.
	 * 
	 * @return
	 */
	public int getSuccessNodeCount() {
		return successNodeCount;
	}

	/**
	 * Set the number of nodes to whom the policy was deployed successfully.
	 * 
	 * @param successNodeCount
	 */
	public void setSuccessNodeCount(int successNodeCount) {
		this.successNodeCount = successNodeCount;
	}

	/**
	 * Get the number of nodes to whom the policy was failed to be deployed.
	 * 
	 * @return
	 */
	public int getFailedNodeCount() {
		return failedNodeCount;
	}

	/**
	 * Set the number of nodes to whom the policy was failed to be deployed.
	 * 
	 * @param failedNodeCount
	 */
	public void setFailedNodeCount(int failedNodeCount) {
		this.failedNodeCount = failedNodeCount;
	}

	/**
	 * Get the number of nodes to whom the policy is deploying.
	 * 
	 * @return
	 */
	public int getDeployingNodeCount() {
		return deployingNodeCount;
	}

	/**
	 * Set the number of nodes to whom the policy is deploying.
	 * 
	 * @param deployingNodeCount
	 */
	public void setDeployingNodeCount(int deployingNodeCount) {
		this.deployingNodeCount = deployingNodeCount;
	}

	/**
	 * Get the number of nodes to whom is waiting for the policy to be deployed.
	 * 
	 * @return
	 */
	public int getToBeDeployedNodeCount() {
		return toBeDeployedNodeCount;
	}

	/**
	 * Set the number of nodes to whom is waiting for the policy to be deployed.
	 * 
	 * @param toBeDeployedNodeCount
	 */
	public void setToBeDeployedNodeCount(int toBeDeployedNodeCount) {
		this.toBeDeployedNodeCount = toBeDeployedNodeCount;
	}

	/**
	 * Get the UUID of the policy.
	 * 
	 * @return
	 */
	public String getPolicyUuid() {
		return policyUuid;
	}

	/**
	 * Set the UUID of the policy.
	 * 
	 * @param policyUuid
	 */
	public void setPolicyUuid(String policyUuid) {
		this.policyUuid = policyUuid;
	}

	/**
	 * Get the enabling status of the policy.
	 * 
	 * @return
	 */
	public PlanEnableStatus getEnabled() {
		return enabled;
	}

	/**
	 * Set the enabling status of the policy.
	 * 
	 * @param enabled
	 */
	public void setEnabled(PlanEnableStatus enabled) {
		this.enabled = enabled;
	}

	/**
	 * Get error message for enabling or disabling the policy.
	 * 
	 * @return
	 */
	public String getEnableMessage() {
		return enableMessage;
	}

	/**
	 * Set error message for enabling or disabling the policy.
	 * 
	 * @param enableMessage
	 */
	public void setEnableMessage(String enableMessage) {
		this.enableMessage = enableMessage;
	}

	/**
	 * Get error message for deploying the policy.
	 * 
	 * @return
	 */
	public String getDeployErrorMessage() {
		return deployErrorMessage;
	}

	/**
	 * Set error message for deploying the policy.
	 * 
	 * @param deployErrorMessage
	 */
	public void setDeployErrorMessage(String deployErrorMessage) {
		this.deployErrorMessage = deployErrorMessage;
	}

	/**
	 * Get number of jobs for the policy is running.
	 * 
	 * @return
	 */
	public int getActiveJobCount() {
		return activeJobCount;
	}

	/**
	 * Set number of jobs for the policy is running.
	 * 
	 * @param activeJobCount
	 */
	public void setActiveJobCount(int activeJobCount) {
		this.activeJobCount = activeJobCount;
	}

	/**
	 * Get number of nodes that are using the policy whose protection status
	 * is successful.
	 * 
	 * @return
	 */
	public int getSucNodeCount() {
		return sucNodeCount;
	}

	/**
	 * Set number of nodes that are using the policy whose protection status
	 * is successful.
	 * 
	 * @param sucNodeCount
	 */
	public void setSucNodeCount(int sucNodeCount) {
		this.sucNodeCount = sucNodeCount;
	}

	/**
	 * Get number of nodes that are using the policy whose protection status
	 * is warning.
	 * 
	 * @return
	 */
	public int getWarningNodeCount() {
		return warningNodeCount;
	}

	/**
	 * Set number of nodes that are using the policy whose protection status
	 * is warning.
	 * 
	 * @param warningNodeCount
	 */
	public void setWarningNodeCount(int warningNodeCount) {
		this.warningNodeCount = warningNodeCount;
	}

	/**
	 * Get number of nodes that are using the policy whose protection status
	 * is error.
	 * 
	 * @return
	 */
	public int getErrNodeCount() {
		return errNodeCount;
	}

	/**
	 * Set number of nodes that are using the policy whose protection status
	 * is error.
	 * 
	 * @param errNodeCount
	 */
	public void setErrNodeCount(int errNodeCount) {
		this.errNodeCount = errNodeCount;
	}

	/**
	 * Get number of nodes to whom the policy was scheduled to deploy.
	 * 
	 * @return
	 */
	public int getScheduleDeployedNodeCount() {
		return scheduleDeployedNodeCount;
	}

	/**
	 * Set number of nodes to whom the policy was scheduled to deploy.
	 * 
	 * @param scheduleDeployedNodeCount
	 */
	public void setScheduleDeployedNodeCount(int scheduleDeployedNodeCount) {
		this.scheduleDeployedNodeCount = scheduleDeployedNodeCount;
	}

	/**
	 * Get the bitmap which indicates what configurations are set in the
	 * policy. See {@link PlanTaskType} for the definitions.
	 * 
	 * @return
	 */
	public int getContentflag() {
		return contentflag;
	}

	/**
	 * Set the bitmap which indicates what configurations are set in the
	 * policy. See {@link PlanTaskType} for the definitions.
	 * 
	 * @param contentflag
	 */
	public void setContentflag(int contentflag) {
		this.contentflag = contentflag;
	}

	public int getDeployingD2DNodeCount() {
		return deployingD2DNodeCount;
	}

	public void setDeployingD2DNodeCount(int deployingD2DNodeCount) {
		this.deployingD2DNodeCount = deployingD2DNodeCount;
	}

	public int getPlanDeployReason() {
		return planDeployReason;
	}

	public void setPlanDeployReason(int planDeployReason) {
		this.planDeployReason = planDeployReason;
	}

	public int getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
}
