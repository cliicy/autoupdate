package com.ca.arcserve.edge.app.base.util;

public interface WindowsRegistryNames {
	
	String keyUdpRoot();
	String keyRoot();
	
	String keyCPMRoot();
	String keyReportRoot();
	String keyVCMRoot();
	String keyHBBURoot();
	String keyWebServerRoot();
	String keyMessageServiceRoot();
	String keyIdentityServerRoot();
	String keyProxyRoot();
	String keyAgentWebServiceRoot();
	
	String keyARCserveNode();
	
	String d2dUpdateFrequency();
	String asbuUpdateFrequency();
	
	String udpHomePath();
	String installationPath();
	String adminUser();
	String adminPassword();
	
	String consoleFqdnName();
	
	String webServerVersion();
	String webServerPath();
	String webServerJREPath();
	String webServerPort();
	String webServerUrl();
	String WebServerUpdate();
	
	String appVersion();
	String appPath();
	String appUpdate();
	String updateExitCode();
	String restartServiceAfterPatch();
	String appNewsFeed();
	String appSocialNetworking();
	String appVideo();
	String updateVersionNumber();
	String updateBuildNumber();
	String deployAgreeFlag();
	String deployShowLicenseFlag();
	String deployMaxThreadCount();
	String policyAgreeFlag();
	String policyShowLicenseFlag();
	
	String d2dSyncJobInterval();
	String d2dSyncJobDisable();
	String d2dSyncJobConcurrent();
	String guid();
	
	String arcserveSyncPath();
	
	String showDeleteNodeUI();
	
	String updateMaximumThreadCount();
	
	String enableImportRemoteNodesFromFile();
	
	String keyAPMD2DVersion();
	String keyAPMEdgeVersion();
	String apmD2DMajorVersion();
	String apmD2DMinorVersion();
	
	String autoAddedLocalAgentFlag();
	String autoAddedLocalRpsFlag();
	
	String asbuRegularGroupFSDFlag();

	String brokerProtocol();
	String messageServicePort();
	String proxyServer();
	String proxyPort();
	String proxyType();
	String proxyUsername();
	String proxyPassword();
	String proxyRequireAccount();
	
	String keyHARoot();
	String valueCSConnectTimeout();
	String valueCSRequestTimeout();
	String valueCSDataCollectInterval();
	String valueCSDataPurgeInterval();
	String valueCSDataRetentionDays();
	String pageSize();
	
	String gatewayHostHeartbeatInterval();
	String gatewayUpgradeTimeout();
}
