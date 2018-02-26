package com.ca.arcserve.edge.app.base.serviceexception;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;

public class D2DServiceFault{

	public static EdgeServiceFault getFault(String code, String message){
		EdgeServiceFaultBean b = new EdgeServiceFaultBean(code,message,FaultType.D2D);
		return new EdgeServiceFault(message, b);
	}

}
