package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;

public class EdgeWebServiceContext {
	private static EdgeApplicationType applicationType = EdgeApplicationType.CentralManagement;

	public static EdgeApplicationType getApplicationType() {
		return applicationType;
	}

	public static void setApplicationType(EdgeApplicationType applicationType) {
		EdgeWebServiceContext.applicationType = applicationType;
	}
}
