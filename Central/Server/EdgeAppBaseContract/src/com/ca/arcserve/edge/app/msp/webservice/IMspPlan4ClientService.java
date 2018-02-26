package com.ca.arcserve.edge.app.msp.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.msp.webservice.contract.MspReplicationDestination;

public interface IMspPlan4ClientService {
	
	List<MspReplicationDestination> getMspReplicationDestinations() throws EdgeServiceFault;
	void validateisRemoteConsole(String localConsoleFQDNName)throws EdgeServiceFault;
	
}
