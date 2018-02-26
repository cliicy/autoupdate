/**
 * 
 */
package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception;

/**
 * @author lijwe02
 * 
 */
public interface DeploymentExceptionErrorCode {
	int InvalidD2DConnectInfoException = 1;
	int SetDeployStatusException = 2;
	int MarkNodeAsMonitorException = 3;
	int InvokeD2DWebServiceAPIException = 4;
	int GetPolicyUuidException = 5;
}
