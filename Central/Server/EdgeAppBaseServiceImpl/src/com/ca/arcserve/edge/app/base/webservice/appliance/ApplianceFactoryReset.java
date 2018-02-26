package com.ca.arcserve.edge.app.base.webservice.appliance;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.util.CommonUtil;

public class ApplianceFactoryReset {
	private static Logger logger = Logger.getLogger(ApplianceFactoryReset.class);
	
	public static boolean applianceFactoryReset(boolean preserve, boolean autoreboot) {
		String cmd1 = "powershell.exe";
		String cmd2 = "& \'" + CommonUtil.BaseEdgeInstallPath + "Appliance\\arcserve_factoryreset.ps1\' -preserve_data " + preserve + " -auto_reboot " + autoreboot;
		
		logger.info("[ApplianceFactoryReset]: Begin to Reset Appliance.");
		try {
			String[] commands = {cmd1, cmd2};
			Process p = Runtime.getRuntime().exec(commands);
			ReadStream s1 = new ReadStream("[ApplianceFactoryReset]-stdin", p.getInputStream());
			ReadStream s2 = new ReadStream("[ApplianceFactoryReset]-stderr", p.getErrorStream());
			EdgeExecutors.getCachedPool().execute(s1);
			EdgeExecutors.getCachedPool().execute(s2);
			int result = p.waitFor();
			if(result == 1)
				return false;
			logger.info("[ApplianceFactoryReset]: Appliance Factory Reset finish.");
			return true;
		} catch (Exception e) {
			logger.error( "[ApplianceFactoryReset]: Appliance Factory Reset failed.", e);
			return false;
		}
	}
}
