package com.ca.arcserve.edge.app.base.webservice.d2dapm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.d2dapm.communicate.APMBackendStatus;

public class ApmUtility {
	
	private static final int APM_TYPE_D2D = 0;
	private static final int APM_TYPE_CPM = 1;
	
	private static Logger logger = Logger.getLogger(ApmUtility.class);
	private static NativeFacade nativeFacade = new NativeFacadeImpl();
	
	public static NativeFacade getNativeFacade() {
		return nativeFacade;
	}
	
	public static String getUpdateSettingPath() {
		return nativeFacade.getUpdateSettingsFileEx(APM_TYPE_CPM);
	}
	
	public static long testCAServer(String proxyServer, String proxyPort, String proxyUsername, String proxyPassword) {
		return nativeFacade.testUpdateServerConnectionEx(
				0, "", "80", proxyServer, proxyPort, proxyUsername, proxyPassword, APM_TYPE_CPM);
	}
	
	public static long testStagingServer(String server, int port) {
		return nativeFacade.testUpdateServerConnectionEx(
				1, server, String.valueOf(port), "", "", "", "", APM_TYPE_CPM);
	}
	
	//added by cliicy.luo to add Hotfix menu-item
	public static long testBIStagingServer(String server, int port) {
		return nativeFacade.testBIUpdateServerConnectionEx(
				1, server, String.valueOf(port), "", "", "", "", APM_TYPE_CPM);
	}
	//added by cliicy.luo to add Hotfix menu-item
	
	public static APMBackendStatus getStatus() {
		if(nativeFacade.IsPatchManagerRunning("")){
			if (nativeFacade.IsPatchManagerBusyEx("", APM_TYPE_CPM)) { 
				return APMBackendStatus.BUSY;
			}
			return APMBackendStatus.OK;
		} else {
			return APMBackendStatus.NOT_RUNNING;
		}	
	}
	
	public static PatchInfo getEdgePatchInfo() throws EdgeServiceFault {
		return getPatchInfo(APM_TYPE_CPM);
	}
	
	public static PatchInfo getD2DPatchInfo() throws EdgeServiceFault {
		return getPatchInfo(APM_TYPE_D2D);
	}
	
	private static PatchInfo getPatchInfo(int productType) throws EdgeServiceFault {
		String statusXmlPath = nativeFacade.getUpdateStatusFileEx(productType);
		
		try {
			return com.ca.arcflash.common.CommonUtil.getPatchInfo(statusXmlPath);
		} catch (Exception e) {
			logger.error("get patch info failed, type = " + productType + ", file = " + statusXmlPath, e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.APM_FAIL_GET_PATCHINFO, e.getMessage());
		}
	}
	
	public static int checkUpdates() {
		return (int) nativeFacade.checkUpdateEx(APM_TYPE_CPM);
	}
	
	//added by cliicy.luo to add Hotfix menu-item
	public static int checkHotfix() {
		//return (int) nativeFacade.checkUpdateEx(APM_TYPE_CPM);
		return (int) nativeFacade.checkBIUpdateEx(APM_TYPE_CPM);
	}
	
	public static BIPatchInfo getEdgeHotfixInfo() throws EdgeServiceFault {
		return getHotfixInfo(APM_TYPE_CPM);
	}
	
	private static BIPatchInfo getHotfixInfo(int productType) throws EdgeServiceFault {
		String statusXmlPath = nativeFacade.getBIUpdateStatusFileEx(productType);
		
		try {
			return com.ca.arcflash.common.CommonUtil.getBIPatchInfo(statusXmlPath);
		} catch (Exception e) {
			logger.error("get patch info failed, type = " + productType + ", file = " + statusXmlPath, e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.APM_FAIL_GET_PATCHINFO, e.getMessage());
		}
	}
	
	public static BIPatchInfo getEdgine_HotfixInfo() throws EdgeServiceFault {
		return getHotfixInfo(APM_TYPE_D2D);
	}
	

	//added by cliicy.luo to add Hotfix menu-item
	
	public static int installPatch() {
		return (int) nativeFacade.installUpdateEx(APM_TYPE_CPM);
	}
	
	public static String getErrorMessage(int errorCode) {
		return nativeFacade.getUpdateErrorMessageEx(errorCode, APM_TYPE_CPM);
	}
	
	public static List<String> getLogFiles() {
		ArrayList<String> files = new ArrayList<String>();
		
		try {
			WSJNI.getFilePaths4Sync(1, 2, 6, null, files);
		} catch (Throwable e) {
			logger.error("get APM log files failed.", e);
		}
		
		return files;
	}
	
}
