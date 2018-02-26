/**
 * Created on Dec 14, 2012 6:24:48 PM
 */
package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeSessionPasswordDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.VSBTaskDeployment;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.ISessionPasswordService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;

/**
 * @author lijwe02
 * 
 */
public class SessionPasswordServiceImpl implements ISessionPasswordService {
	private static final Logger logger = Logger.getLogger(SessionPasswordServiceImpl.class);
	private IEdgeSessionPasswordDao sessionPasswordDao = DaoFactory.getDao(IEdgeSessionPasswordDao.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.arcserve.edge.app.base.webservice.ISessionPasswordService#getSessionPasswordForHost(int)
	 */
	@Override
	public List<SessionPassword> getSessionPasswordForHost(int hostId) throws EdgeServiceFault {
		List<SessionPassword> passwordList = new ArrayList<SessionPassword>();
		sessionPasswordDao.as_edge_session_password_getByHostId(hostId, passwordList);
		return passwordList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.arcserve.edge.app.base.webservice.ISessionPasswordService#saveSessionPassword(int, java.util.List)
	 */
	@Override
	public void saveSessionPassword(List<Integer> hostIdList, List<SessionPassword> passwordList, boolean override)
			throws EdgeServiceFault {
		if (hostIdList == null) {
			logger.error("The host id list is empty.");
			return;
		}
		for (Integer hostId : hostIdList) {
			if (override) {
				List<SessionPassword> oldPasswordList = new ArrayList<SessionPassword>();
				sessionPasswordDao.as_edge_session_password_getByHostId(hostId, oldPasswordList);
				for (SessionPassword oldPassword : oldPasswordList) {
					boolean found = false;
					for (SessionPassword newPassword : passwordList) {
						if (oldPassword.getId() == newPassword.getId()) {
							found = true;
							break;
						}
					}
					if (!found) {
						sessionPasswordDao.as_edge_session_password_deleteById(oldPassword.getId());
					}
				}
			}
			for (SessionPassword sessionPassword : passwordList) {
				int[] newId = new int[1];
				sessionPasswordDao.as_edge_session_password_cu(sessionPassword.getId(), hostId,
						sessionPassword.getPassword(), sessionPassword.getPwdComment(), newId);
			}
		}
		
		VSBTaskDeployment vsbTaskDeployer = new VSBTaskDeployment();
		vsbTaskDeployer.updateSessionPasswordToConverter(hostIdList);
	}

}
