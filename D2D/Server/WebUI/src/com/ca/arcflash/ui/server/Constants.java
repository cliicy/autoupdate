package com.ca.arcflash.ui.server;

import com.ca.arcflash.common.CommonRegistryKey;

public class Constants {
	static final String Namespace = "http://webservice.arcflash.ca.com";
	static final String ServiceName = "FlashServiceImpl";
	static final String PortName = "FlashServiceImplHttpSoap11Endpoint";
	static final String SERVICE_PART = "/WebServiceImpl/services/FlashServiceImpl";
	static final String WSDL_NAME = "FlashServiceImpl.wsdl";

	static final String SNamespace = "http://webservice.arcserve.ca.com";
	static final String SServiceName = "WebServiceImpl";
	static final String SPortName = "WebServiceImplHttpSoap11Endpoint";
	// JAXWSProperties.CONNECT_TIMEOUT and REQUEST_TIMEOUT
	static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
	static final String REGISTRY_WEBSERVICE = CommonRegistryKey.getD2DRegistryRoot()+"\\WebService";
	static final String REG_REQUEST_TIMEOUT = "timeoutValue";

	static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";
	static final int TIME_OUT_VALUE = 300 * 1000;
}
