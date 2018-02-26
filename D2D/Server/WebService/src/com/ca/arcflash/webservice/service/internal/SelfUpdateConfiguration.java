package com.ca.arcflash.webservice.service.internal;

import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcflash.webservice.service.CommonService;

public class SelfUpdateConfiguration {
	
		synchronized public void Write(String filePath,
			AutoUpdateSettings autoUpdateSettings) throws Exception {       
			
		INIFile objINI = null;
		objINI = new INIFile(filePath);
		
		// adding default sections/keys and its values if APMSettings.ini does not exists
		if(!objINI.isbIsFileExists())
		{
			//DownloadServer
			objINI.setStringProperty("DownloadServer", "Protocol", "HTTP", null);
			objINI.setStringProperty("DownloadServer", "Port", "80", null);
			
			//DOWNLOADCONFIG
			objINI.setStringProperty("DOWNLOADCONFIG", "RetryCount", "3", null);
			objINI.setStringProperty("DOWNLOADCONFIG", "TimeOutForEachTry", "60", null);
			objINI.setStringProperty("DOWNLOADCONFIG", "PacketCount", "16", null);
			
			//Debug
			objINI.setStringProperty("Debug", "DebugLevel", "3", null);
			objINI.setStringProperty("Debug", "LogFileSize", "5", null);
			
		}
			
		//DownloadServer in case staging server is selected
		
		objINI.removeProperty("DownloadServer", "ServerCount");
		for(int i=1; i <= 5; i++)
		{
			String strTemp = "StagingServer#";
			strTemp += Integer.toString(i+1);
			objINI.removeSection(strTemp);
		}
		
		StagingServerSettings[] StagingServers = autoUpdateSettings.getStagingServers();
		
		if(StagingServers != null)
		{
			objINI.setStringProperty("DownloadServer", "ServerCount", Integer.toString(StagingServers.length), null);
			
			for(int i=0; i < StagingServers.length; i++)
			{
				String strTemp = "StagingServer#"; 
				strTemp += Integer.toString(i+1);
				objINI.setStringProperty(strTemp, "ServerName", StagingServers[i].getStagingServer(), null);
				objINI.setStringProperty(strTemp, "ServerPort", Integer.toString(StagingServers[i].getStagingServerPort()), null);
			}
		}
	    if(autoUpdateSettings.getServerType() == 0)
	    {
	    	objINI.setStringProperty("DownloadServer", "Port", "80", null);
	    	objINI.setStringProperty("DownloadServer", "ServerType", "0", null);
	    }
	    else if(autoUpdateSettings.getServerType() == 1) 
	    {
	    	objINI.setStringProperty("DownloadServer", "ServerType", "1", null);
	    }
	    
	    //ActiveUpdate
	    boolean bScheduleType = autoUpdateSettings != null ? autoUpdateSettings.isScheduleType() : false;
	    if(bScheduleType)
	    {
	    	objINI.setStringProperty("Schedule", "AutoCheckUpdate", "1" , null);
	    	objINI.setStringProperty("Schedule", "DisableActiveUpdate", "0" , null);
	    	objINI.setStringProperty("Schedule", "WeekDay", Integer.toString(autoUpdateSettings.getScheduledWeekDay()), null);
	    	int iHour = autoUpdateSettings.getScheduledHour();
	    	iHour += 1;
	    	if(iHour == 24)
	    	{
	    		iHour = 0;
	    	}
	    	String strHour = Integer.toString(iHour);
	    	objINI.setStringProperty("Schedule", "Hour", strHour, null);
	    }
	    else
	    {
	    	objINI.setStringProperty("Schedule", "AutoCheckUpdate", "0" , null);
	    	objINI.setStringProperty("Schedule", "DisableActiveUpdate", "1" , null);
	    }
	    
	    //ProxySettings
	    boolean bUseProxy = autoUpdateSettings.getproxySettings() != null ? autoUpdateSettings.getproxySettings().isUseProxy() : false;
	    if(bUseProxy)
	    {
	    	objINI.setStringProperty("ProxySettings", "UseProxy", "1", null);
	    	objINI.setStringProperty("ProxySettings", "ProxyServer",autoUpdateSettings.getproxySettings().getProxyServerName() , null);
		    objINI.setIntegerProperty("ProxySettings", "ProxyPort",autoUpdateSettings.getproxySettings().getProxyServerPort(), null);
		    if(autoUpdateSettings.getproxySettings().isProxyRequiresAuth())
		    {
		    	objINI.setStringProperty("ProxySettings", "ProxyRequireAuth", "1", null);
		    	objINI.setStringProperty("ProxySettings", "ProxyUserName",autoUpdateSettings.getproxySettings().getProxyUserName() , null);
			    objINI.setStringProperty("ProxySettings", "ProxyPassword",CommonService.getInstance().getNativeFacade().encrypt(autoUpdateSettings.getproxySettings().getProxyPassword()), null);
		    }
		    else
		    {
		    	objINI.setStringProperty("ProxySettings", "ProxyRequireAuth", "0", null);
		    }
	    }
	    else
	    {
	    	objINI.setStringProperty("ProxySettings", "UseProxy", "0", null);
	    }
	    
	    /* Save changes back to strFile */
	    objINI.save();
	    objINI = null;
	}   
}
