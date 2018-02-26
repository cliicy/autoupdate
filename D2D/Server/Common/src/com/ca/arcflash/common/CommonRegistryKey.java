package com.ca.arcflash.common;

public class CommonRegistryKey {
	
	private static final String D2DREGISTRY_ROOT	 =	"SOFTWARE\\Arcserve\\Unified Data Protection\\Engine";
	private static final String RPSREGISTRY_ROOT	 =	"SOFTWARE\\Arcserve\\Unified Data Protection\\Engine";
	private static final String VSBREGISTRY_ROOT	 =	"SOFTWARE\\Arcserve\\Unified Data Protection\\Engine\\OfflineCopy";
	private static final String HBBUREGISTRY_ROOT	 =	"SOFTWARE\\Arcserve\\Unified Data Protection\\Engine";
	
	public static String getD2DRegistryRoot() {
		return D2DREGISTRY_ROOT;
	}
	public static String getRPSRegistryRoot() {
		return RPSREGISTRY_ROOT;
	}
	public static String getVSBRegistryRoot() {
		return VSBREGISTRY_ROOT;
	}
	public static String getHBBURegistryRoot() {
		return HBBUREGISTRY_ROOT;
	}
	
}
