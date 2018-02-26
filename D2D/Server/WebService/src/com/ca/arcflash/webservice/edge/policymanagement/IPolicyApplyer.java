package com.ca.arcflash.webservice.edge.policymanagement;

import java.util.List;

import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;

public interface IPolicyApplyer
{
	void applyPolicy(
		List<PolicyDeploymentError> errorList, String policyUuid,
		String policyXml,String parameter );
	
	void unapplyPolicy(
		List<PolicyDeploymentError> errorList,
		boolean keepCurrentSettings ,String parameter );
}
