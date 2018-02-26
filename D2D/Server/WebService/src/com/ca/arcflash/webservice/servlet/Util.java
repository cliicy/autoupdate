package com.ca.arcflash.webservice.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RegConstants;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.internal.PatchManager;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class Util {
	private static final Logger logger = Logger.getLogger(Util.class);
	
	private static String strPatchManagerPath = "";
	private static Process pPatchManagerProcess = null;
	
	/**
	 * the TimeZone.getID() is not consistent across platform. This function is
	 * used to generate the GMT ID of the rawOffset
	 * 
	 * @param rawOffset
	 * @return
	 */
	public static String getGMTIDString(int rawOffset) {
		int offsetHour = rawOffset / (60 * 60 * 1000);
		int ab = rawOffset > 0 ? rawOffset : 0 - rawOffset;
		ab = ab % (60 * 60 * 1000);
		int offsetMinutes = ab / (60 * 1000);

		String hour = "";
		{
			if (offsetHour > 0) {
				hour = "+";

			} else {
				hour = "-";
				offsetHour = 0 - offsetHour;
			}
			if (offsetHour < 10) {
				hour += "0";

			}
			hour += offsetHour;
		}

		String minute = "";
		{
			if (offsetMinutes < 10) {
				minute += "0";
			}
			minute += offsetMinutes;
		}

		String string = String.format("GMT%s:%s", hour, minute);
		return string;
	}

	public static String getAgentHomePath() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(RegConstants.REGISTRY_INSTALLPATH);
			String strD2DHomePath = registry.getValue(handle, RegConstants.REGISTRY_KEY_PATH);
			return strD2DHomePath;
		} catch (Exception ex) {
			logger.error("contextInitialized() - end " + ex.getMessage(), ex);
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}
				;
			}
		}
		return null;
	}
	
	public static void logTimeZoneInfo(NativeFacade nativeFacade) {
		try{
			TimeZone c = java.util.Calendar.getInstance().getTimeZone();
			String zoneID = Util.getGMTIDString(c.getRawOffset());
			boolean dayLight = c.useDaylightTime();
			//AFRES_AFJWBS_GENERAL is a universal in AFMsg, defined as %s
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_INFO,Constants.AFRES_AFJWBS_GENERAL,new String[]{WebServiceMessages.getResource("serverTimeZone",zoneID,""+dayLight),"","","",""});
			VersionInfo vInfo = CommonService.getInstance().getVersionInfo();
			if(vInfo.getUpdateNumber()== null || vInfo.getUpdateNumber().equals("")){
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_INFO,Constants.AFRES_AFJWBS + 16, 
						new String[] {ServiceContext.getInstance().getProductNameD2D() ,vInfo.getDisplayVersion(), vInfo.getMajorVersion(),vInfo.getMinorVersion(),vInfo.getBuildNumber()});
			}else{
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_INFO,Constants.AFRES_AFJWBS + 18, 
						new String[] {ServiceContext.getInstance().getProductNameD2D() ,vInfo.getDisplayVersion(), vInfo.getMajorVersion(), 
					vInfo.getMinorVersion(), vInfo.getBuildNumber(), vInfo.getUpdateNumber()});
			}
		}catch(Exception e){
			logger.error("contextInitialized() - end "+ e.getMessage(),e);
		}
	}
	
	public static Process launchPatchManager() {
//		try {
//			String strD2DHomePath = Util.getAgentHomePath();
//			String strPatchManagerPath = null;
//			if (!StringUtil.isEmptyOrNull(strD2DHomePath)) {
//				strPatchManagerPath = strD2DHomePath + "\\Update Manager\\D2DUpdateManager.exe";
//			}
//			pPatchManagerProcess = Runtime.getRuntime().exec(strPatchManagerPath);
//			return pPatchManagerProcess;
//
//		} catch (Exception ex) {
//		}
		return null;
	}
	
	public static void destroyPathManager(){
//		try {
//			pPatchManagerProcess.destroy();
//			PatchManager.getInstance().terminate();
//			// Thread.sleep(2000);
//			/*
//			 * String cmdLine =
//			 * "cmd /c \"taskkill.exe /F /IM D2DUpdateManager.exe\""; try {
//			 * Runtime.getRuntime().exec(cmdLine); } catch (IOException e) {
//			 * e.printStackTrace(); }
//			 */
//
//			logger.info("PatchManager terminated");
//
//			pPatchManagerProcess.waitFor();
//			logger.info("Exit pPatchManagerProcess.waitFor");
//
//			pPatchManagerProcess.exitValue();
//		} catch (Throwable e) {
//			logger.error("Failed to stop AgPkiMon.exe: " + e.getMessage());
//		}
	}
	
	/*
	 * This method must remain for future customer requrirement
	 */
	public void configureNetworkAdapter() {

		logger.info("configureNetworkAdapter.");

		String failoverJobScriptString = null;
		try {
			logger.debug("Unmarshal failover jobscript.");
			failoverJobScriptString = CommonUtil.getFailoverJobScript();
			if (StringUtil.isEmptyOrNull(failoverJobScriptString)) {
				logger.debug("No failover jobscript is injected into registry.");
				return;
			}
		} catch (Exception e) {
			logger.error("Failed to get failover jobscript from registry.");
			return;
		}

		try {

			FailoverJobScript failoverJobScript = CommonUtil
					.unmarshal(failoverJobScriptString, FailoverJobScript.class);
			if (failoverJobScript.getFailoverMechanism().size() == 0) {
				logger.debug("No failover mechanism.");
				return;
			}

			Virtualization virtualType = failoverJobScript.getFailoverMechanism().get(0);
			List<NetworkAdapter> adapters = null;

			adapters = virtualType.getNetworkAdapters();

			if (adapters == null) {
				logger.error("adaters is null in replication jobscript.");
				return;
			}

			NativeFacade facade = BackupService.getInstance().getNativeFacade();

			Map<String, String> machineAdapters = facade.GetHostAdapterList();
			if (machineAdapters == null || machineAdapters.size() == 0) {
				logger.error("Can not get adapter list from vm.");
				return;
			}

			String[] machineAdapterNames = machineAdapters.values().toArray(new String[0]);

			for (Entry<String, String> entry : machineAdapters.entrySet()) {
				logger.info(entry.getKey());
				logger.info(entry.getValue());
			}
			logger.info("Configure network.");
			int adapterCount = machineAdapterNames.length < adapters.size() ? machineAdapterNames.length : adapters
					.size();
			for (int i = 0; i < adapterCount; i++) {

				NetworkAdapter adapter = adapters.get(i);
				String adapterName = machineAdapterNames[i];
				logger.info(adapterName);
				logger.info(adapter.getIP());

				if (adapter.isDynamicIP()) {
					facade.EnableHostDHCP(adapterName);
				} else {

					List<String> ipAddresses = adapter.getIP();
					List<String> ipMasks = new ArrayList<String>();
					ipMasks.add(adapter.getSubnetMask());
					facade.EnableHostStatic(adapterName, ipAddresses, ipMasks);

					List<String> gateways = new ArrayList<String>();
					gateways.add(adapter.getGateway());
					facade.SetHostGateways(adapterName, gateways, new ArrayList<Integer>());

					facade.EnableHostDNS(adapterName);

					List<String> dnses = new ArrayList<String>();
					dnses.add(adapter.getPreferredDNS());
					dnses.add(adapter.getAlternateDNS());

					facade.SetHostDNSServerSearchOrder(adapterName, dnses);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to configure nic card");
			logger.error(e.getMessage());
		}

	}
	
}
