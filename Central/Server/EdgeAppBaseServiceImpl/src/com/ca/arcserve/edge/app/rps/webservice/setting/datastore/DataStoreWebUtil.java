package com.ca.arcserve.edge.app.rps.webservice.setting.datastore;

import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.serviceexception.RpsServiceFault;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;

public class DataStoreWebUtil {

	public static EdgeServiceFault generateD2DException(String errorCode, Object... errorMessageParameters) {
		String errorMessage = D2DWebServiceErrorMessages.getMessage(errorCode, errorMessageParameters);
		EdgeServiceFault fault = RpsServiceFault.getRpsFault(true, errorCode, errorMessage);
		fault.getFaultInfo().setMessageParameters(errorMessageParameters);
		return fault;
	}

	public static EdgeServiceFault generateException(String errorCode,
			String errorMsg, Object[] messageParameters) {

		EdgeServiceFaultBean faultInfo = new EdgeServiceFaultBean(errorCode,
				errorMsg);

		if (messageParameters != null)
			faultInfo.setMessageParameters(messageParameters);

		EdgeServiceFault ex = new EdgeServiceFault(errorMsg, faultInfo);
		return ex;
	}

	public static RPSConnection getWebservice(int rpsnodeid) throws EdgeServiceFault {
		IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
		RPSConnection connection = connectionFactory.createRPSConnection(rpsnodeid);
		connection.connect();
		return connection;
	}

}
