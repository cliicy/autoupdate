package com.ca.arcserve.edge.app.base.webservice;


public interface IEdgeAERPService {
	String[] registerEntitlementDetails(String name, String company, String contactNumber, String emailID, String netSuiteId);
	String submitAERPJob();
	String isActivated();
	String cancelRegistration(String name, String company, String contactNumber, String emailID, String netSuiteId);
}
