package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.EnumMap;

import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOperatingSystem;

public class DiscoveryOperatingSystemMap {
	private static EnumMap<DiscoveryOperatingSystem, String> osMap = null;
	
	static
	{
		Initialize();
	}
	
	private static void Initialize() {
		if( osMap == null )
		{
			osMap = new EnumMap<DiscoveryOperatingSystem, String>(DiscoveryOperatingSystem.class);
			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_ALL, "All OSs");
			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_SERVER_ALL, "All Servers");
			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_SERVER_2000, "Windows 2000 Server");
			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_SERVER_2003, "Windows Server 2003");
			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_SERVER_2008, "Windows Server 2008");

			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_DESKTOP_ALL, "All Desktops");
			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_DESKTOP_XP, "Windows XP");
			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_DESKTOP_VISTA, "Windows Vista");
			osMap.put(DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_DESKTOP_WIN7, "Windows 7");
		}
	}
	
	public static String getOperatingSystemName(DiscoveryOperatingSystem osID)
	{
		String osName = "All Servers";

		if(osID == null)  return osName;
		
		if( osMap.containsKey(osID) ) 
			osName = osMap.get(osID);
		
		return osName;
	}
}
