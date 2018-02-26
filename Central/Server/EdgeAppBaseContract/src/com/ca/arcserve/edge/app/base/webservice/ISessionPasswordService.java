/**
 * Created on Dec 13, 2012 4:38:24 PM
 */
package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;

/**
 * @author lijwe02
 * 
 */
public interface ISessionPasswordService {
	/**
	 * 
	 * Query session password for specify host
	 * 
	 * @param hostId
	 *            The specify host id
	 * @return The password list related to the host, if no password was set, return the size is zero
	 * @throws EdgeServiceFault
	 *             Exception occurred when get session passwords for host
	 */
	List<SessionPassword> getSessionPasswordForHost(int hostId) throws EdgeServiceFault;

	/**
	 * 
	 * Save the session password to database
	 * 
	 * @param hostIdList
	 *            The host id list for set session password
	 * @param passwordList
	 *            The password list
	 * @param override
	 *            Indicate whethe override the original password
	 * @throws EdgeServiceFault
	 *             exception on save session password
	 */
	void saveSessionPassword(List<Integer> hostIdList, List<SessionPassword> passwordList, boolean override)
			throws EdgeServiceFault;
}
