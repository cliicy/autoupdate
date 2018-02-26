package com.ca.arcserve.edge.app.base.serviceexception;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;

public class RpsServiceFault {

	public static EdgeServiceFault getRpsFault(boolean remote, String code, String message) {
		FaultType faultType = remote ? FaultType.RPSRemote:FaultType.RPS;
		EdgeServiceFaultBean bean = new EdgeServiceFaultBean(code,message,faultType);
		return new EdgeServiceFault(message, bean);
	}



}
