package com.ca.arcserve.edge.app.base.common;

import com.ca.arcflash.webservice.data.ApplicationStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryApplication;

public class ApplicationUtil {

	public static int setSQLInstalled(int appStatus)
	{
		return appStatus | DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_SQL.getValue();
	}

	public static boolean isSQLInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_SQL.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_SQL.getValue();
	}
	
	public static int setExchangeInstalled(int appStatus)
	{
		return appStatus | DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_EXCH.getValue();
	}
	
	public static boolean isExchangeInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_EXCH.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_EXCH.getValue();
	}
	
	public static int setD2DODInstalled(int appStatus)
	{
		return appStatus | DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD.getValue();
	}
	
	public static boolean isD2DODInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD.getValue();
	}
	
	public static int setD2DInstalled(int appStatus)
	{
		return appStatus | DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D.getValue();
	}

	public static boolean isD2DInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D.getValue();
	}
	
	public static int setRPSInstalled(int appStatus)
	{
		return appStatus | DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_RPS.getValue();
	}

	public static boolean isRPSInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_RPS.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_RPS.getValue();
	}

	public static int setArcserveInstalled(int appStatus)
	{
		return appStatus | DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_BACKUP.getValue();
	}

	public static boolean isArcserveInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_BACKUP.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_BACKUP.getValue();
	}
	
	public static boolean isD2DProductFamilyInstalled(int appStatus){
		return isD2DInstalled(appStatus) || isD2DODInstalled(appStatus);
	}
	
	public static int setLinuxD2DInstalled(int appStatus)
	{
		return appStatus | DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_LINUXD2D.getValue();
	}
	
	public static boolean isLinuxD2DInstalled(int appStatus){
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_LINUXD2D.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_LINUXD2D.getValue();
	}
	
	public static int setConsoleInstalled(int appStatus)
	{
		return appStatus | DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_CONSOLE.getValue();
	}
	
	public static boolean isConsoleInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_CONSOLE.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_CONSOLE.getValue();
	}
	
	public static int getValue(ApplicationStatus appStatus) {
		int value = 0;
		
		if (appStatus.isSqlInstalled()) {
			value = setSQLInstalled(value);
		}
		
		if (appStatus.isExchangeInstalled()) {
			value = setExchangeInstalled(value);
		}
		
		if (appStatus.isArcserveInstalled()) {
			value = setArcserveInstalled(value);
		}
		
		if (appStatus.isD2dInstalled()) {
			value = setD2DInstalled(value);
		}
		
		if (appStatus.isD2dODInstalled()) {
			value = setD2DODInstalled(value);
		}
		
		return value;
	}
	
}
