package com.ca.arcserve.edge.app.base.webservice.contract.node;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum DiscoveryApplication implements IBit {
	EDGE_DISCOVERY_NODE_APPLICATION_NONE	(0x00000000),
	EDGE_DISCOVERY_NODE_APPLICATION_D2D	(0x00000001),
	EDGE_DISCOVERY_NODE_APPLICATION_BACKUP	(0x00000002),
	EDGE_DISCOVERY_NODE_APPLICATION_RHA	(0x00000004),
	EDGE_DISCOVERY_NODE_APPLICATION_RPS	(0x00000008),
	EDGE_DISCOVERY_NODE_APPLICATION_SQL	(0x00000010),
	EDGE_DISCOVERY_NODE_APPLICATION_EXCH	(0x00000020),
	EDGE_DISCOVERY_NODE_APPLICATION_SPS	(0x00000040),
	EDGE_DISCOVERY_NODE_APPLICATION_D2DOD (0x00000080),
	EDGE_DISCOVERY_NODE_APPLICATION_LINUXD2D (0x00000100),
	EDGE_DISCOVERY_NODE_APPLICATION_CONSOLE (0x00000200);
	
	private final int value;
	private DiscoveryApplication(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static DiscoveryApplication parse(int value) {
		switch (value) {
		case 0x00000000:
			return EDGE_DISCOVERY_NODE_APPLICATION_NONE;
		case 0x00000001:
			return EDGE_DISCOVERY_NODE_APPLICATION_D2D;
		case 0x00000002:
			return EDGE_DISCOVERY_NODE_APPLICATION_BACKUP;
		case 0x00000004:
			return EDGE_DISCOVERY_NODE_APPLICATION_RHA;
		case 0x00000008:
			return EDGE_DISCOVERY_NODE_APPLICATION_RPS;
		case 0x00000010:
			return EDGE_DISCOVERY_NODE_APPLICATION_SQL;
		case 0x00000020:
			return EDGE_DISCOVERY_NODE_APPLICATION_EXCH;
		case 0x00000040:
			return EDGE_DISCOVERY_NODE_APPLICATION_SPS;
		case 0x00000080:
			return EDGE_DISCOVERY_NODE_APPLICATION_D2DOD;
		case 0x00000100:
			return EDGE_DISCOVERY_NODE_APPLICATION_LINUXD2D;
		case 0x00000200:
			return EDGE_DISCOVERY_NODE_APPLICATION_CONSOLE;
		default:
			return null;
		}
	}	
	public static boolean isD2DInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D.getValue();
	}
	public static boolean isD2DODInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD.getValue();
	}
	
	public static boolean isConsoleInstalled(int appStatus)
	{
		return (appStatus & DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_CONSOLE.getValue()) == DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_CONSOLE.getValue();
	}
}