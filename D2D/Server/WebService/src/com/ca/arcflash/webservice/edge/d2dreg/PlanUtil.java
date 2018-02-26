package com.ca.arcflash.webservice.edge.d2dreg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo.ListenerType;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.datasync.BaseDataSyncer;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyApplyerFactory;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class PlanUtil {
	private static final Logger logger = Logger.getLogger(PlanUtil.class);
	
	public static void cleanPlan() {
		if (!isCMPlan()) {
			logger.info("CM didn't push any plan to agent, so remain the standalone setting, not clean it.");
			return;
		}
		List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();

		PolicyApplyerFactory.createPolicyApplyer(
				ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving)
				.unapplyPolicy(errorList, false, "");

		BackupService
				.getInstance()
				.getNativeFacade()
				.addLogActivity(
						Constants.AFRES_AFALOG_WARNING,
						Constants.AFRES_AFJWBS_GENERAL,
						new String[] {
								WebServiceMessages
										.getResource("autoUnassignPolicy"), "",
								"", "", "" });
	}

	/**
	 * clean registration info if CPM didn't manage this agent
	 */
	public static boolean cleanRegInfo4CM() {

		BaseDataSyncer.removeDataSyncFolder();

		BaseEdgeRegistration agentRegToEdge = new D2DEdgeRegistration();

		EdgeRegInfo regInfo = agentRegToEdge
				.getEdgeRegInfo(ApplicationType.CentralManagement);

		if (regInfo == null)
			return false;

		int result = agentRegToEdge.removeEdge(regInfo.getEdgeUUID(),
				ApplicationType.CentralManagement, regInfo.getEdgeHostName(),
				true);

		if (result == 0)
			return true;
		else
			return false;
	}

	/**
	 * clean plan info when no plan assigned this agent in CPM.
	 * <p>
	 * Don't delete RegConfigPM.xml in this method. Agent still be managed by
	 * CPM .
	 */
	public static void cleanPlanInfo4CM() {
		BaseDataSyncer.removeDataSyncFolder();
		cleanPlan();
		BaseEdgeRegistration agentRegToEdge = new D2DEdgeRegistration();
		EdgeRegInfo regInfo = agentRegToEdge
				.getEdgeRegInfo(ApplicationType.CentralManagement);
		FlashListenerInfo listener = FlashListenerInfo.createListenerInfo(
				ListenerType.CPM, null, regInfo.getEdgeUUID());
		ListenerManager.getInstance().removeFlashListener(listener);
	}

	private static boolean isCMPlan() {
		try {
			BackupConfiguration config = BackupService.getInstance()
					.getBackupConfiguration();
			if (config != null && !StringUtil.isEmptyOrNull(config.getPlanId())) {
				return true;
			}
		} catch (ServiceException e) {
			logger.error(e);
		}
		return false;
	}
}
