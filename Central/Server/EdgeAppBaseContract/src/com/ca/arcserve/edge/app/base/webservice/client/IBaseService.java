package com.ca.arcserve.edge.app.base.webservice.client;

import javax.jws.WebService;

import com.ca.arcflash.webservice.toedge.IEdgeVaildate;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IEdgeCommonService;

@WebService(targetNamespace="http://webservice.edge.arcserve.ca.com/")
public interface IBaseService extends IEdgeCommonService, IEdgeVaildate{
	/**
	 * Validate the user in local machine. This method will call native library by JNI.
	 * @param username user name
	 * @param password password
	 * @param domain domain or machine name.
	 * @return int In Edge , the top layer doesn't need the UUID any more
	 * @throws EdgeServiceFault
	 */
	int validateUser(String username, String password, String domain) throws EdgeServiceFault;
	
	/**
	 * Get the current machine's defaultUser
	 * @return defaultUser Name
	 * @throws EdgeServiceFault
	 */
	String getDefaultUser() throws EdgeServiceFault;
}
