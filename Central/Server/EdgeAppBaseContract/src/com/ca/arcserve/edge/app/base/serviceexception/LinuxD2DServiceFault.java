package com.ca.arcserve.edge.app.base.serviceexception;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;

public class LinuxD2DServiceFault {
	
	public static final String METHOD_NOT_DEFINED_MESSAGE = "Cannot find dispatch method";

	/**
	 * 
	 */
	private static final long serialVersionUID = 8555135271822741756L;

	public static EdgeServiceFault getFault(String code, String message) {
		EdgeServiceFaultBean b = new EdgeServiceFaultBean(code,message, FaultType.LinuxD2D);
		return new EdgeServiceFault(message,b);
	}
}
