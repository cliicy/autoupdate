package com.ca.arcserve.edge.app.msp.webservice.contract;

import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.common.BasePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;

public class PlanPagingResult extends BasePagingResult {

	private static final long serialVersionUID = 917258609109034160L;
	
	private List<PolicyInfo> plans;

	public List<PolicyInfo> getPlans() {
		return plans;
	}

	public void setPlans(List<PolicyInfo> plans) {
		this.plans = plans;
	}

}
