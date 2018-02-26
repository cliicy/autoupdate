package com.ca.arcserve.edge.app.rps.mockd2d;

import javax.jws.WebService;

import com.ca.arcflash.rps.webservice.RPSService4CPMImpl;

@WebService(endpointInterface="com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM")
public class RpsMockD2DServiceImpl extends RPSService4CPMImpl {

	@Override
	public int validateUserByUUID(String uuid) {
		return 0;
	}
	
	@Override
	protected void checkSession() {
		// Never check session for mock implementation
	}
	
}
