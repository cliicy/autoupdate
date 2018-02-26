package com.ca.arcserve.edge.app.base.serviceinfo;

import java.util.HashMap;
import java.util.Map;


import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DReSyncService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeSRMService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeService;



@SuppressWarnings("unchecked")
public class ServiceInfoConstants  implements IServiceInfoConstants{
	String f_ServiceInfoPath = IServiceInfoConstants.DEFAULT_SERVICE_LIST_PATH;

	private static Map<String,Class> id2ServiceInterface=new HashMap<String,Class>();
	public static final String SERVICE_BINDING_SOAP11 = "soap11";
	public static final String SERVICE_BINDING_SOAP12 = "soap12";
	public static final String SERVICE_BINDING_REST = "rest";
	public static final String SERVICE_LIST_PATH = "/ListService";


	public static final String SERVICE_EDGE_PROPER_NAMESPACE="http://webservice.edge.arcserve.ca.com/";
	public static final String SERVICE_EDGE_PROPER_PORT_NAME="EdgeServiceImplHttpSoap11Endpoint";
	public static final String SERVICE_EDGE_PROPER_SERVICE_NAME="EdgeServiceImpl";
	
	public static final String SERVICE_EDGE_CONSOLE_PROPER_PORT_NAME="EdgeServiceConsoleImplHttpSoap11Endpoint";
	public static final String SERVICE_EDGE_CONSOLE_PROPER_SERVICE_NAME="EdgeServiceConsoleImpl";

	public static final String SERVICE_ID_EDGE_PROPER = "IEdgeService";
	public static final String SERVICE_ID_EDGE_SRM_PROPER = "IEdgeSRMService";



	public static final String SERVICE_ID_EDGE_D2D_RESYNC = "IEdgeD2DReSyncService";
	public static final String SERVICE_ID_EDGE_D2D_REG = "IEdgeD2DRegService";
	
	//Service Names
	public static final String AGENT_SERVICE_NAME = "CASAD2DWebSvc";
	public static final String ASBU_SERVICE_NAME = "CASASBUWebSvc";

	static{
		id2ServiceInterface.put(SERVICE_ID_EDGE_PROPER, IEdgeService.class);
		id2ServiceInterface.put(SERVICE_ID_EDGE_SRM_PROPER, IEdgeSRMService.class);
		id2ServiceInterface.put(SERVICE_ID_EDGE_D2D_RESYNC, IEdgeD2DReSyncService.class);
		id2ServiceInterface.put(SERVICE_ID_EDGE_D2D_REG, IEdgeD2DRegService.class);
	}
	@Override
	public String getServiceInfoPath() {
		// TODO Auto-generated method stub
		return f_ServiceInfoPath;
	}
	@Override
	public void setServiceInfoPath(String serviceInfoPath) {
		f_ServiceInfoPath = serviceInfoPath;

	}
	@Override
	public  Class<?> getServiceInterfaceClass(String serviceID){
		return id2ServiceInterface.get(serviceID);
	}
	@Override
	public void registerID(String serviceID, Class<?> serviceInterface) {
		id2ServiceInterface.put(serviceID, serviceInterface);

	}
}
