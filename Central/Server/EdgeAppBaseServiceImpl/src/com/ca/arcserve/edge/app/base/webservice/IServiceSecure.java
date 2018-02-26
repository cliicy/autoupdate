package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IServiceSecure {
	
	void checkSession() throws EdgeServiceFault;

}
