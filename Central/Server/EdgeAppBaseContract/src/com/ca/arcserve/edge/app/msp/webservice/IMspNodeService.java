package com.ca.arcserve.edge.app.msp.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IMspNodeService {
	
	void importNodesFromRPS(int rpsHostId) throws EdgeServiceFault;

}
