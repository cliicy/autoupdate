package com.ca.arcflash.ui.client.common;

import java.util.List;

import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.model.ProxySettingsModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2D;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service/rps")
public interface IConfigRPSInD2DSideService extends RemoteService{
	List<RpsPolicy4D2D> getRPSPolicyList(String hostName, String userName, String password, int port, String protocol,ProxySettingsModel proxy) throws BusinessLogicException, ServiceConnectException, ServiceInternalException, SessionTimeoutException;     
	/**
	 * dedup and catalog parameter, so we can do backend 2 and 3 operation.
	 * @param hostName
	 * @param userName
	 * @param password
	 * @param port
	 * @param protocol
	 * @return
	 * @throws BusinessLogicException
	 * @throws ServiceConnectException
	 * @throws ServiceInternalException
	 * @throws SessionTimeoutException
	 */	
	List<RpsHostModel> getRPSHostList();
	
}
