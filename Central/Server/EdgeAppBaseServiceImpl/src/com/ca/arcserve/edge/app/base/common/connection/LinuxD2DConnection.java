package com.ca.arcserve.edge.app.base.common.connection;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.IWebServiceProvider;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.VersionInfo;

public class LinuxD2DConnection extends Connection<BaseWebServiceClientProxy, ILinuximagingService> {
	
	private static Logger logger = Logger.getLogger(LinuxD2DConnection.class);
	
	public static final String LinuxD2DServicePart = "/WebServiceImpl/services/LinuximagingServiceImpl";
	
	public LinuxD2DConnection(IConnectionContextProvider contextProvider, IWebServiceProvider<BaseWebServiceClientProxy> serviceProvider) {
		super(contextProvider, serviceProvider);
	}

	public void connect(boolean login) throws EdgeServiceFault {
		super.connect(login);
		UDPApplication application = UDPApplication.getInstance();
		if(application != null){
			if(application.getApplicationType() == UDPApplication.UDPApplicationType.Console){
				checkVersion();
			}
		}
	}
	
	private void checkVersion() throws EdgeServiceFault{
		VersionInfo versionInfo = this.getService().getVersionInfo();
		if(versionInfo.getVersion() == null || versionInfo.getVersion().compareTo("6.0") < 0){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_Linux_D2D_Server_Version_Low,"Linux D2D version is not matched.");
		}
	}
	
	@Override
	protected ILinuximagingService getService(BaseWebServiceClientProxy clientProxy) {
		return (ILinuximagingService) clientProxy.getService();
	}

	@Override
	protected void loginWithCredential(ILinuximagingService service, ConnectionContext context) {
		String username = context.getUsername();
		if(username == null || username.isEmpty()){
			return;
		}
		
		if (context.getDomain() != null && !context.getDomain().isEmpty()) {
			username = context.getDomain() + "\\" + username;
		}
		
		String encryptedAuthUuid = service.validateUser(username, context.getPassword());
		if (encryptedAuthUuid == null) {
			throw new RuntimeException(EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential, "Failed to login to Linux D2D"));
		}
		
		if (!isAutoUpdateUuid()) {
			return;
		}
		
		
		String nodeUuid = "";
		List<ServerInfo> serverInfos = service.getD2DServerInfoList();
		if (serverInfos != null && !serverInfos.isEmpty()) {
			nodeUuid = serverInfos.get(0).getUuid();
		}
		
		// Linux D2D use encrypted UUID to validate, so need not to decrypt it here.
		getContextProvider().updateUuid(nodeUuid, encryptedAuthUuid);
	}

	@Override
	protected void loginWithUuid(ILinuximagingService service, ConnectionContext context) {
		int result = service.validateByKey(context.getAuthUuid());
		if (result != 0) {
			throw new RuntimeException(EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential, "Failed to login to Linux D2D"));
		}
	}
	
	@Override
	protected boolean isInvalidUuidException(Exception exception) {
		if (!(exception instanceof RuntimeException)) {
			return false;
		} else if (!(exception.getCause() instanceof EdgeServiceFault)) {
			return false;
		}
		
		EdgeServiceFault fault = (EdgeServiceFault) exception.getCause();
		return fault.getFaultInfo() != null && EdgeServiceErrorCode.Node_D2D_Reg_InvalidCredential.equals(fault.getFaultInfo().getCode());
	}

	@Override
	protected String[] getServiceName() {
		String realServiceName = ""; //can't detect linux service status 
		String displayName = EdgeCMWebServiceMessages.getMessage("productNameAgent");
		return new String[]{realServiceName,displayName};
	}

	@Override
	protected String getMessageSubject() {
		return NodeExceptionUtil.getLinuxServerMessageSubject();
	}
}
