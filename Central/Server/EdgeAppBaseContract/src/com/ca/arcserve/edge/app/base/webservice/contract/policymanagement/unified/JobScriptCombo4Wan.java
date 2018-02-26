package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class JobScriptCombo4Wan extends JobScriptCombo {

	private static final long serialVersionUID = -3202583750207528677L;

	GatewayId gatewayid;

	public GatewayId getGatewayid() {
		return gatewayid;
	}

	public void setGatewayid(GatewayId gatewayid) {
		this.gatewayid = gatewayid;
	}
	
}
