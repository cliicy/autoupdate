package com.ca.arcserve.edge.app.msp.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface ICustomerContext {
	
	int getCustomerId() throws EdgeServiceFault;

}
