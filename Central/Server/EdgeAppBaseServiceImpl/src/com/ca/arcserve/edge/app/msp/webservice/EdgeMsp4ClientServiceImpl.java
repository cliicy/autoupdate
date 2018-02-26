package com.ca.arcserve.edge.app.msp.webservice;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.ca.arcflash.common.IDirectWebServiceImpl;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.SessionWrapper;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceProxyFactory;
import com.ca.arcserve.edge.app.base.webservice.IServiceSecure;
import com.ca.arcserve.edge.app.msp.webservice.contract.Customer;
import com.ca.arcserve.edge.app.msp.webservice.contract.MspReplicationDestination;
import com.ca.arcserve.edge.webservice.msp.IMsp4RpsService;
import com.ca.arcserve.edge.webservice.msp.data.RemoteNodeRegInfo;

@WebService(endpointInterface="com.ca.arcserve.edge.app.msp.webservice.IEdgeMsp4ClientService")
public class EdgeMsp4ClientServiceImpl implements IEdgeMsp4ClientService, IDirectWebServiceImpl, IServiceSecure, ICustomerContext {
	
	private IMspPlan4ClientService planService = EdgeWebServiceProxyFactory.createProxy(new MspPlanServiceImpl(this), IMspPlan4ClientService.class, this);
	private IMsp4RpsService msp4RpsService = EdgeWebServiceProxyFactory.createProxy(new Msp4RpsServiceImpl(), IMsp4RpsService.class, this);
	
	@Resource
	private WebServiceContext context;
	
	@Override
	public void setSession(HttpSession session) {
		throw new RuntimeException("Do not support set session.");
	}

	@Override
	public HttpSession getSession() {
		if (context == null) {
			return null;
		}
		
		Object requestProperty = context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
		
		if (requestProperty != null	&& requestProperty instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) requestProperty;
			return request.getSession(true);
		}
		
		return null;
	}
	
	@Override
	public void checkSession() throws EdgeServiceFault {
		SessionWrapper wrapper = new SessionWrapper(this.getSession());
		if (!wrapper.isValid() || wrapper.getAttribute(MspSessionAttributes.CustomerUsername) == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_NOT_LOGIN, "Not login");
		}
	}

	@Override
	public void validateCustomer(String username, String password) throws EdgeServiceFault {
		MspCustomerServiceImpl customerService = new MspCustomerServiceImpl();
		Customer customer = customerService.getCustomerByName(username);
		customer.setPassword(password);
		customerService.validateCustomer(customer);
		
		HttpSession session = this.getSession();
		SessionWrapper wrapper = new SessionWrapper(session);
		wrapper.setAttribute(MspSessionAttributes.CustomerUsername, username);
		wrapper.setAttribute(MspSessionAttributes.CustomerPassword, password);
		wrapper.setAttribute(MspSessionAttributes.CustomerId, customer.getId());
	}
	
	@Override
	public int getCustomerId() throws EdgeServiceFault {
		HttpSession session = this.getSession();
		SessionWrapper wrapper = new SessionWrapper(session);
		Object idObject = wrapper.getAttribute(MspSessionAttributes.CustomerId);
		if (!(idObject instanceof Integer)) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_NOT_LOGIN, "Not login");
		}
		
		return (int) idObject;
	}

	@Override
	public List<MspReplicationDestination> getMspReplicationDestinations() throws EdgeServiceFault {
		return planService.getMspReplicationDestinations();
	}

	@Override
	public RPSReplicationSettings getRemoteRpsReplicationSettings(String planUuid) throws EdgeServiceFault {
		return msp4RpsService.getRemoteRpsReplicationSettings(planUuid);
	}

	@Override
	public void registerRemoteNodes(List<RemoteNodeRegInfo> nodes) throws EdgeServiceFault {
		msp4RpsService.registerRemoteNodes(nodes);
	}
	
	@Override
	public void validateisRemoteConsole(String localConsoleFQDNName) throws EdgeServiceFault {
		planService.validateisRemoteConsole(localConsoleFQDNName);
	}


}
