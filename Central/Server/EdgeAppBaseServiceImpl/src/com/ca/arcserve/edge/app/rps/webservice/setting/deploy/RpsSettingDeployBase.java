package com.ca.arcserve.edge.app.rps.webservice.setting.deploy;

import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsPolicyDao;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployReason;
import com.ca.arcserve.edge.app.rps.webservice.i18n.EdgeRPSWebServiceMessages;
import com.ca.arcserve.edge.app.rps.webservice.policy.PolicyLogProxy;

public abstract class RpsSettingDeployBase {
	IRpsPolicyDao policyDao = DaoFactory.getDao(IRpsPolicyDao.class);
	IRpsNodeDao nodeDao = DaoFactory.getDao(IRpsNodeDao.class);
	IRpsDataStoreDao datastoreDao = DaoFactory.getDao(IRpsDataStoreDao.class);

	int nodeid = 0;

	public abstract void doDeploy() throws EdgeServiceFault;

	protected void outputSuccMessageToActivityLog(String policyname,
			String host, PolicyDeployReason reason) {
		String assignMessage = "", unAssignMessage = "", formatStr = "";
		Module m = Module.RpsManagement;

		if (this instanceof PolicyDeploy || this instanceof PolicyRemove) {
			assignMessage = "POLICY_DEPLOYMENT_RESULT_MESSAGE";
			unAssignMessage = "POLICY_UNASSIGN_RESULT_MESSAGE";
			m = Module.RpsPolicyManagement;
		} else if (this instanceof DataStoreDeploy
				|| this instanceof DataStoreRemove) {
			assignMessage = "DATASTORE_DEPLOYMENT_RESULT_MESSAGE";
			unAssignMessage = "DATASTORE_UNASSIGN_RESULT_MESSAGE";
			m = Module.RpsDataStoreSetting;
		}

		if (reason.equals(PolicyDeployReason.Assign))
			formatStr = EdgeRPSWebServiceMessages.getMessage(assignMessage);
		else
			formatStr = EdgeRPSWebServiceMessages.getMessage(unAssignMessage);

		String outputStr = String.format(formatStr, policyname, host,
				EdgeRPSWebServiceMessages
						.getMessage("POLICY_DEPLOYMENT_SUCCEED"), "");

		PolicyLogProxy.getInstance().addSuccessLog(m, host, outputStr);
	}
}
