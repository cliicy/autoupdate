package com.ca.arcflash.ui.client.common;

import java.util.List;

import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.model.RpsPolicy4D2DRestoreModel;
import com.ca.arcflash.ui.client.model.rps.RpsDatastore4D2dSettings;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2DSettings;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service/rpsd2d")
public interface ICommonRPSService4D2D extends RemoteService {
	List<RpsPolicy4D2DSettings> getRPSPolicyList(String hostName, String userName,
			String password, int port, String protocol)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException;     

	List<RpsPolicy4D2DRestoreModel> getRPSPolicyList4Restore(String hostName, String userName,
			String password, int port, String protocol)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException;     
	
	List<RpsHostModel> getRPSHostList() 
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException, SessionTimeoutException;

	List<RpsDatastore4D2dSettings> getRPSDatastoreList(String hostName, String userName,
			String password, int port, String protocol)
					throws BusinessLogicException, ServiceConnectException,
					ServiceInternalException, SessionTimeoutException;
	
	Long getDataStoreStatus(RpsHostModel host, String rpsDataStoreUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException;
}
