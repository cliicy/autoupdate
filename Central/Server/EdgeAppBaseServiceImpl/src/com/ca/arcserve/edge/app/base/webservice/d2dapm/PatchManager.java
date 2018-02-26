package com.ca.arcserve.edge.app.base.webservice.d2dapm;

import org.apache.log4j.Logger;

//import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.ca.arcflash.webservice.data.PM.PMResponse;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.PM.ProxySettings;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IEdgeApmForD2D;
import com.ca.arcserve.edge.app.base.webservice.IEdgeApmForEdge;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.ApmResponse;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.BIPatchInfoEdge;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.EdgePatchType;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.PatchInfoEdge;
import com.ca.arcserve.edge.app.base.webservice.d2dapm.communicate.APMBackendStatus;

public class PatchManager implements IEdgeApmForEdge, IEdgeApmForD2D {
	
	private static Logger logger = Logger.getLogger(PatchManager.class);
	private static PatchManager instance = new PatchManager();
	
	private PatchManager() {
	}
	
	public static PatchManager getInstance() {
		return instance;
	}

	@Override
	public PMResponse SubmitAPMRequestD2D(int RequestType) throws EdgeServiceFault {
		PMResponse response = new PMResponse();
		logger.debug("oooo [PatchManager] after creating PMResponse.");
		response.setIsRequestFailed(false);
		response.setM_iResponseError(0);
		logger.debug("oooo [PatchManager] after setting somethings.");
		return response;
	}

	@Override
	public AutoUpdateSettings GetEdgeUpdateSettings() throws EdgeServiceFault {
		try {
			logger.debug("[PatchManager] Begin to get edge update settings.");
			AutoUpdateSettings autoUpdateSettings = PmSettings.getInstance().load();
			logger.debug("[PatchManager] End to get edge update settings.");
			return autoUpdateSettings;
		} catch (Exception e) {
			logger.error("failed to get CPM auto update settings.", e);
	    	throw EdgeServiceFault.getFault(EdgeServiceErrorCode.AutoUpdateConfig_Edge_Fail_Load, e.getMessage());
		}
	}

	@Override
	public void SetEdgeUpdateSettings(AutoUpdateSettings updateConfig) throws EdgeServiceFault {
		try {
			PmSettings.getInstance().save(updateConfig);
		} catch (Exception e) {
	    	logger.error("failed to save CPM auto update settings.", e);
	    	throw EdgeServiceFault.getFault(EdgeServiceErrorCode.AutoUpdateConfig_Edge_Fail_Save, e.getMessage());
		}
	}

	@Override
	public AutoUpdateSettings testDownloadServerConnnectionEdge(AutoUpdateSettings updateSettings) throws EdgeServiceFault {
		logger.debug("[oooo console PatchManager]: Begin to test download server connection.");
		AutoUpdateSettings autoUpdateSettings = updateSettings;
		switch(updateSettings.getServerType()) {
		case 0: autoUpdateSettings = testCAServer(updateSettings);
			break;
		case 1: autoUpdateSettings = testStagingServer(updateSettings);
			break;
		default : autoUpdateSettings = updateSettings;
		}
		logger.debug("[oooo console PatchManager]: End to test download server connection.");
		return autoUpdateSettings;
	}
	
	
	
	private AutoUpdateSettings testCAServer(AutoUpdateSettings settings) {
		ProxySettings proxyConfig = settings.getproxySettings();

		boolean enableProxy = proxyConfig != null ? proxyConfig.isUseProxy() : false; 
		String proxyServerName = enableProxy ? proxyConfig.getProxyServerName() : "";
		String proxyPort = enableProxy ? Integer.toString(proxyConfig.getProxyServerPort()) : "";
		String proxyUserName = enableProxy ? proxyConfig.getProxyUserName() : "";
		String proxyPassword = enableProxy ? proxyConfig.getProxyPassword() : "";

		long connectionStatus = ApmUtility.testCAServer(proxyServerName, proxyPort, proxyUserName, proxyPassword);
		settings.setiCAServerStatus(connectionStatus == 0 ? 1 : 0);
		
		return settings;
	}
	
	private AutoUpdateSettings testStagingServer(AutoUpdateSettings settings) {
		if(settings == null){
			logger.error("[PatchManager] testStagingServer() failed. Heve no settings.");
			return settings;
		}
		StagingServerSettings[] stagingServers = settings.getStagingServers();
		for (int i = 0; i < stagingServers.length; ++i) {
			long connectionStatus = ApmUtility.testStagingServer(stagingServers[i].getStagingServer(), stagingServers[i].getStagingServerPort());
			stagingServers[i].setStagingServerStatus(connectionStatus == 0 ? 1 : 0);
		}
		
		return settings;
	}

	//added by cliicy.luo to add Hotfix menu-item
	private AutoUpdateSettings testBIStagingServer(AutoUpdateSettings settings) {
		if(settings == null){
			logger.error("oooo concole [PatchManager] testStagingServer() failed. Heve no settings.");
			return settings;
		}
		StagingServerSettings[] stagingServers = settings.getStagingServers();
		for (int i = 0; i < stagingServers.length; ++i) {
			long connectionStatus = ApmUtility.testBIStagingServer(stagingServers[i].getStagingServer(), stagingServers[i].getStagingServerPort());
			stagingServers[i].setStagingServerStatus(connectionStatus == 0 ? 1 : 0);
		}
		
		return settings;
	}
	
	@Override
	public void SetEdgeHotfixSettings(AutoUpdateSettings updateConfig) throws EdgeServiceFault {
		try {
			PmSettings.getInstance().save(updateConfig);
		} catch (Exception e) {
	    	logger.error("oooo console failed to save CPM auto update settings.", e);
	    	throw EdgeServiceFault.getFault(EdgeServiceErrorCode.AutoUpdateConfig_Edge_Fail_Save, e.getMessage());
		}
	}
	
	@Override
	public AutoUpdateSettings testDownloadBIServerConnnectionEdge(AutoUpdateSettings updateSettings) throws EdgeServiceFault {
		logger.debug("[oooo console PatchManager]: Begin to test hotfix download server connection.");
		AutoUpdateSettings autoUpdateSettings = updateSettings;
		switch(updateSettings.getServerType()) {
		case 0: autoUpdateSettings = testCAServer(updateSettings);
			break;
		case 1: autoUpdateSettings = testBIStagingServer(updateSettings);
			break;
		default : autoUpdateSettings = updateSettings;
		}
		logger.debug("[oooo console PatchManager]: End to test hotfix download server connection.");
		return autoUpdateSettings;
	}
	
	//added by cliicy.luo to add Hotfix menu-item
	
	
	@Override
	public int getPatchManagerStatusEdge() throws EdgeServiceFault {
		return ApmUtility.getStatus().ordinal();
	}

	@Override
	public PatchInfoEdge[] getPatchInfoesEdge() throws EdgeServiceFault {
		logger.debug("[PatchManager] Begin to get patch infos for edge.");
		PatchInfo patchInfo = ApmUtility.getEdgePatchInfo();
		PatchInfoEdge patchInfoEdge = new PatchInfoEdge(patchInfo);
		patchInfoEdge.setPatchType(EdgePatchType.PATCH_CM);
		PatchInfoEdge[] patchInfoEdges = new PatchInfoEdge[] { patchInfoEdge };
		logger.debug("[PatchManager] End to get patch infos for edge.");
		return patchInfoEdges;
	}

	@Override
	public int installPatchEdge() throws EdgeServiceFault {
		logger.info("begin to install patches.");
		
		try {
			return ApmUtility.installPatch();
		} catch (Throwable e) {
			logger.error("fail to install patch.", e);
			return -1;
		}
	}

	@Override
	public ApmResponse[] checkUpdateEdge() throws EdgeServiceFault {
		ensureUpdateServerAvailable();
		
		int result = ApmUtility.checkUpdates();
		
		ApmResponse response = new ApmResponse();
		response.setPatchType(EdgePatchType.PATCH_CM);
		response.setErrorCode(result);
		response.setMessage(ApmUtility.getErrorMessage(result));
		
		return new ApmResponse[] { response };
	}
	
	private static void ensureUpdateServerAvailable() throws EdgeServiceFault {
		APMBackendStatus backendStatus = ApmUtility.getStatus();
		
		if (APMBackendStatus.NOT_RUNNING == backendStatus) {
			String message = "Edge apm back-end exe is not running.";
			logger.warn(message);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.APM_BACKEND_DEAD, message);
		} else if (APMBackendStatus.BUSY == backendStatus) {
			int retry = 10;
			
			do {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.APM_FailCheckUpdate, e.getMessage());
				}
				
				--retry;
			} while (APMBackendStatus.BUSY == ApmUtility.getStatus() && retry > 0);
			
			if (retry == 0) {
				String message = "Edge apm back-end is busy.";
				logger.warn(message);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.APM_BACKEND_BUSY, message);
			}
		}
	}
	
	//added by cliicy.luo to add Hotfix menu-item
	@Override
	public PatchInfoEdge[] getHotfixInfoesEdge() throws EdgeServiceFault {
		logger.debug("[PatchManager] Begin to get patch infos for edge.");
		BIPatchInfo patchInfo = ApmUtility.getEdgeHotfixInfo();
		if (patchInfo==null)
			return null;
		BIPatchInfoEdge patchInfoEdge = new BIPatchInfoEdge(patchInfo);
		patchInfoEdge.setPatchType(EdgePatchType.PATCH_CM);
		logger.debug("[PatchManager] End to get patch infos for edge.");
		return patchInfoEdge.aryPatchInfoM;
	}

	
	@Override
	public PatchInfoEdge[] getHotfix_Edgine() throws EdgeServiceFault {
		logger.debug("[PatchManager] Begin to get patch infos for edge.");
		BIPatchInfo patchInfo = ApmUtility.getEdgine_HotfixInfo();
		if (patchInfo==null)
			return null;
		BIPatchInfoEdge patchInfoEdge = new BIPatchInfoEdge(patchInfo);
		patchInfoEdge.setPatchType(EdgePatchType.PATCH_CM_ENGINE);
		logger.debug("[PatchManager] End to get patch infos for edge.");
		return patchInfoEdge.aryPatchInfoM;
	}
	
	@Override
	public ApmResponse[] checkHotfixEdge() throws EdgeServiceFault {
		ensureUpdateServerAvailable();
		
		int result = ApmUtility.checkHotfix();
		
		ApmResponse response = new ApmResponse();
		response.setPatchType(EdgePatchType.PATCH_CM);
		response.setErrorCode(result);
		response.setMessage(ApmUtility.getErrorMessage(result));
		
		return new ApmResponse[] { response };
	}

	
	
	//added by cliicy.luo to add Hotfix menu-item
}


