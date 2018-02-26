package com.ca.arcflash.webservice.edge.d2dreg;

import com.ca.arcflash.webservice.toedge.IEdgeRegConfig;

public class EdgeD2DRegConfigImpl implements IEdgeRegConfig {

	@Override
	public EdgeRegInfo getEdgeRegInfo() {
		BaseEdgeRegistration register = new D2DEdgeRegistration();
		EdgeRegInfo regInfo = register.getEdgeRegInfo(ApplicationType.CentralManagement);
		if(regInfo == null)
			return new EdgeRegInfo();
		return regInfo;
	}

}
