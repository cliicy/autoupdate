package com.ca.arcflash.webservice.service;

import com.ca.arcflash.common.CommonRegistryKey;

public class RegConstants {
	public static final String REGISTRY_EXCHANGE2013 = "SOFTWARE\\Microsoft\\ExchangeServer\\v15";
	public static final String REGISTRY_VERSION_ROOTKEY = CommonRegistryKey.getD2DRegistryRoot() + "\\Version";
	public static final String REGISTRY_INSTALLPATH = CommonRegistryKey.getD2DRegistryRoot() + "\\InstallPath";
	public static final String REGISTRY_WEBSERVICE = CommonRegistryKey.getD2DRegistryRoot() + "\\WebService";
	public static final String REGISTRY_VSB_ROOTKEY = CommonRegistryKey.getVSBRegistryRoot();
	public static final String REGISTRY_OS_NAME = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
	public static final String REGISTRY_KEY_OS_NAME = "ProductName";
	public static final String REGISTRY_KEY_MAJORVERSION = "Major";
	public static final String REGISTRY_KEY_MINORVERSION = "Minor";
	public static final String REGISTRY_KEY_BUILDNUMBER = "Build";
	public static final String REGISTRY_KEY_DisplayVersion = "DisplayVersion";
	public static final String REGISTRY_KEY_UPDATENUMBER = "UpdateVersionNumber";
	public static final String REGISTRY_KEY_UPDATEBUILDNUMBER = "UpdateBuildNumber";
	public static final String REGISTRY_KEY_PRODUCTTYPE = "ProductType";
	public static final String REGISTRY_KEY_GUID = "GUID";
	public static final String REGISTRY_KEY_NODEID = "NodeID";
	public static final String REGISTRY_KEY_PATH = "Path";
	public static final String REGISTRY_URL = "URL";
	public static final String REGISTRY_SERVER_PORT = "Port";
	public static final String REGISTRY_KEY_VSPHERE_MAX_JOB_NUM = "VMMaxJobNum";
	public static final String REGISTRY_KEY_VMWARE_MAX_JOB_NUM = "VMwareMaxJobNum";
	public static final String REGISTRY_KEY_HYPERV_MAX_JOB_NUM = "HyperVMaxJobNum";
	public static final String REGISTRY_KEY_SESSION_DISMOUNT_TIME = "SessionDismountTime";
	public static final String REGISTRY_SQLSERVER = "SOFTWARE\\Microsoft\\Microsoft SQL Server";
	public static final String REGISTRY_EXCHANGESERVER = "SOFTWARE\\Microsoft\\ExchangeServer";
	public static final String REGISTRY_CHECK_UPDATE_TIMEOUT = "CheckUpdateTimeout";
	public static final String REGISTRY_CACHE_LICENSE_EXPIRATION = "CacheLicenseExpiration";
	public static final String REGISTRY_PROXY_INSTALL_VOLUME_ALERT_THRESHOLD = "InstallPathFreeSpaceAlertThresholdInMB";
}
