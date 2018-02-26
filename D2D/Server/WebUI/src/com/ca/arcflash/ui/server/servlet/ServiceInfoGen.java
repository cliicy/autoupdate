package com.ca.arcflash.ui.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.serviceinfo.ServiceInfo;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.serviceinfo.ServiceInfoList;

/**
 * Servlet implementation class ServiceInfoGen
 */
public class ServiceInfoGen extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceInfoGen() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServiceInfoList s = new ServiceInfoList();
		List<ServiceInfo> services= new ArrayList<ServiceInfo>();


		ServiceInfo d2d = new ServiceInfo();
		d2d.setBindingType(ServiceInfoConstants.SERVICE_BINDING_SOAP11);
		d2d.setNamespace(ServiceInfoConstants.SERVICE_D2D_PROPER_NAMESPACE);
		d2d.setPortName(ServiceInfoConstants.SERVICE_D2D_PROPER_PORT_NAME);
		d2d.setServiceName(ServiceInfoConstants.SERVICE_D2D_PROPER_SERVICE_NAME);
		String localName = request.getServerName();
		int localport = request.getLocalPort();
		String protocol = request.getProtocol();
		if(request.isSecure())
			protocol = "https";
		else protocol = "http";
		String wsdlUrl = protocol+"://"+localName.toLowerCase()+":"+localport+"/WebServiceImpl/services/FlashServiceImpl?wsdl";
		d2d.setWsdlURL(wsdlUrl);
		List<String> idList= new ArrayList<String>();
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_PROPER);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_V2);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_R16_U4);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_EDGE_SRMAGENT_PROPER);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_RESYNC);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_REG);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_POCY_MAG);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_VC_PROPER);

		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_ARCHIVE);
		idList.add(ServiceInfoConstants.SERVICE_ID_ARCHIVE);

		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE_CM);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE_VCM);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE_VSPHERE);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE);
		idList.add(ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE_R16_U4);

		d2d.setServiceIDList(idList);


		services.add(d2d);
		s.setServices(services);

		String marshal="";
		try {
			marshal = CommonUtil.marshal(s);
		} catch (JAXBException e) {

		}
		response.getWriter().print(marshal);
		response.getWriter().flush();

	}

}
