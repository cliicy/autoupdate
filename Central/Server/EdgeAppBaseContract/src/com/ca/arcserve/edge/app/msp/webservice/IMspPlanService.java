package com.ca.arcserve.edge.app.msp.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.msp.webservice.contract.PlanPagingConfig;
import com.ca.arcserve.edge.app.msp.webservice.contract.PlanPagingResult;

public interface IMspPlanService {
	
	PlanPagingResult getAvailableMspPlans(int customerId, PlanPagingConfig config) throws EdgeServiceFault;
	void assignMspPlans(int customerId, List<Integer> mspPlanIds) throws EdgeServiceFault;

}
