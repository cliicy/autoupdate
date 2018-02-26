package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.util.OverwriteD2DExtenalLink;

/**
 * @author wanwe14
 *Any one who needs to add a new help link in edge apps must follow these process and rules.
 *If you don't follow the rules, it will not direct to the destinal url and UT will fail.
 *If you have any question, please contact wanwe14@ca.com
 *
 *Suppose you are going to add a new help link "footerHorWidgetFeedLink" for EDGE apps: CentralManager, VCM, VSphere or Report
 *1. Add field footerHorWidgetFeedLink and the get/set functions;
 *2. Go to all redirect_*.php, add below stamentments for the apps that need this help link for different language
 *
 *else if ($item == "footerHorWidgetFeedLinkCM")
 *	{
 *		header( 'Location: http://feeds.ca.com/arcservenews' ) ;
 *	}
 *	else if ($item == "footerHorWidgetFeedLinkVCM")
 *	{
 *		header( 'Location: http://feeds.ca.com/arcservenews' ) ;
 *	}
 *	else if ($item == "footerHorWidgetFeedLinkVSphere")
 *	{
 *		header( 'Location: http://feeds.ca.com/arcservenews' ) ;
 *	}
 *	else if ($item == "footerHorWidgetFeedLinkReport")
 *	{
 *		header( 'Location: http://feeds.ca.com/arcservenews' ) ;
 *	}
 *
 *3. The suffix "CM" for CentralManager, "VCM" for Virtal Standby, "VSphere" for Host based vm back up, and "Report" for Report app.
 *
 */
public class ExternalLinks implements Serializable{

	private static final long serialVersionUID = 1L;

	private String footerHorWidgetFeedLink;
	
	private String footerHorWidgetFacebookLink;
	
	private String footerHorWidgetTwitterLink;

	private String rssJspUrl;
	
	private String brandingPanelVideosLink;
	private String brandingPanelVideosCASupportLink;
	private String brandingPanelCaSupportLink;

	private String brandingPanelProvideYFDLink;
	
	private String brandingPanelProvideYFDLinkInternal;

	private String brandingPanelUserComDLink;
	
	private String brandingPanelExpertAdCenLink;
	
	private String caURLLink;
	
	private String brandingPanelEdgeKnowledgeMenuLink;

	private String brandingPanelEdgeHelpMenuLink;

	private String brandingPanelUserGuideMenuLink;

	private String addServerWindowHelp;
	
	private String addASBUServerWindowHelp;
	
	private String aDDiscoverDialogHelp;
	
	private String addNewGroupWindowHelp;
	
	private String modifyGroupWindowHelp;
	
	private String nodeDetailWindowHelp;
	
	private String settingWindowHelp;
	
	private String nodeWindowHelp;
	
	private String nodeWindowHelpForAppliance;
	
	private String d2dNodeWindowHelp;

	private String autoManagedNodeWindowHelp;

	private String updateNodeHelp;
	
	private String rpsUpdateNodeHelp;

	private String wizardWindowHelp;

	private String nodeImportWindowHelp;
	
	private String nodeHyperVisorWindowHelp;
	
	private String nodeDiscoveryWindowHelp;
	
	private String vmDiscoveryWindowHelp;
	
	private String configurationPanelHelp;
	
	private String configurationDBPanelHelp;
	
	private String configurationASBUSettingHelp;
	
	private String configurationAutoDiscoverySettingHelp;
	
	private String configurationD2DSettingHelp;
	
	private String configurationEdgePreferenceSettingHelp;
	
	private String configurationEdgeUpdateSettingHelp;
	
	@OverwriteD2DExtenalLink(overwriteLinkKey="PreferencesAutoUpdateSettings")
	private String configurationEdgeUpdateProxySettingHelp;
	
	private String configurationEmailServerSettingsHelp;
	
	private String configurationEmailTemplateSettingHelp;
	
	private String configurationNodeDeleteSettingHelp;
	
	private String configurationSRMSettingHelp;
	
	private String configurationITMGMTSettingHelp;
	
	private String configurationAddCustomerHelp;
	
	private String configurationEidtCustomerHelp;

	private String configurationCmDBConfigPanelHelp;
	
	private String defaultHelpLink;
	
	private String asbuSettingRunNowHelp;
	
	private String srmSettingRunNowHelp;
	
	private String credentialWindowHelp;
	
	private String esxWindowHelp;
	
	private String syncNodeDialogHelp;
	
	private String statusMonitorPanelHelp;
	
	private String createPolicyDialogBoxHelp;
	
	private String policyCopyDialogBoxHelp;
	
	private String editPolicyDialogBoxHelp;
	
	private String licenseManagementWindowHelp;

	private String newTabDialogHelp;
	
	private String deployWindowHelp;
	
	private String policyManagementResultWindowHelp;

	private String policyManagementVMCredentialWindowHelp;

	private String policyManagementAssignToNodesDialogHelp;

	private String policyManagementAssignToNodesDialogBox4VMHelp;
	
	private String ImportVSphereVMConfirmOverwriteDialogBoxHelp;
	
	private String vmImportFromVSphereWindowHelp;

	private String emailSchdulerMainHelp;
	
	private String emailSchdulerNewHelp;

	private String emailSchdulerEditHelp;
	
	private String reportChartGenHelp;
	
	private String emailDialogHelp;
	
	private String addServerWindowEditHelp;

	private String introVideoEmbedLink;
	
	private String CASupportIntroVideoEmbedLink;

	private String adminAccountHelp;
	
	private String deployD2DConfigurationHelp;
	
	private String deployD2DForUpdate7NodeWarningLink;
	
	private String releaseNotesURL;
	
	private String nodeDiscoveryCredentialWindowHelp;
	
	private String dataStoreCreateHelp;
	private String dataStoreEditHelp;
	private String planCreateHelp;
	private String planCreateHelpForAppliance;
	private String planEditHelp;
	
	// PFC solution link
	private String PFCTotalHelpLink;
	private String PFCCBTFailSolutionLink;
	private String PFCSharedSCSIDeviceSolutionLink;
	private String PFCCredentialInvalidSolutionLink;
	private String PFCIndepedentDiskSolutionLink;
	private String PFCPhysicalRDMDiskSolutionLink;
	private String PFCVirtualRDMDiskSolutionLink;
	private String PFCVMToolsInvalidSolutionLink;
	private String PFCVIXInvalidSolutionLink;
	private String PFCApplicationFailSolutionLink;
	private String PFCIDEDiskNotSupportSolutionLink;
	private String PFCSATADiskNotSupportSolutionLink;
	private String PFCNotEnoughSCSISlotSolutionLink;
	private String PFCNotSupportESXVersionSolutionLink;
	private String PFCNotSupportDynamicDiskSolutionLink;
	private String PFCNFSDataStoreSolutionLink;
	private String PFCNotSupportStorageSpacesSolutionLink;
	private String PFCHyperVVMCredentialInvalidSolutionLink;
	private String PFCHyperVVMPowerStateInvalidSolutionLink;
	private String PFCHyperVCredentailInvalidSolutionLink;
	private String PFCHyperVInteServiceInvalidSolutionLink;
	private String PFCHyperVInteServiceOutOfDateSolutionLink;
	private String PFCHyperVInteServiceIncompatibleSolutionLink;
	private String PFCHyperVDiskTypeNotSupportedSolutionLink;
	private String PFCHyperVFSTypeNotSupportedSolutionLink;
	private String PFCHyperVScopedSnapshotEnabledSolutionLink;
	private String PFCHyperVPhysicalDiskSolutionLink;
	private String PFCHyperVDiskOnRemoteShareSolutionLink;
	private String PFCHyperVDCCannotGetVMbyGuidSolutionLink;
	private String PFCHyperVDCVMnotRunningSolutionLink;
	private String PFCHyperVDCIntegrationServiceNotOKSolutionLink;
	private String PFCHyperVDCannotAccessVMSolutionLink;
	
	// learn more link
	private String learnMoreLink;
	
	@OverwriteD2DExtenalLink(overwriteLinkKey="BackUpNowHelp")
	private String backupNowHelp;
	
	private String liveChatLink;
	
	private String nodeImportFromRHAWindowHelp;
	private String importRemoteNodesFromFileWindowHelp;
	
	private String createRemotePolicyDialogBoxHelp;
	private String editRemotePolicyDialogBoxHelp;
	private String configureConverterHelp;
	private String standbyNetworkConfigureDialogHelp;
	private String setSessionPasswordWindowHelp;
	
	// RHA Integration
	
	private String rhaCreateScenarioWizardHelp;
	private String rhaFullSystemPlatformSettingsDialogBoxHelp;
	private String rhaNetworkAdapterMappingDialogBoxHelp;
	private String rhaRemoteInstallAddHostDialogBoxHelp;
	private String rhaRemoteInstallEditInstallTargetDialogBoxHelp;
	private String rhaRemoteInstallEditInstallSettingsDialogBoxHelp;
	private String rhaRemoteInstallViewLogsDialogBoxHelp;
	private String rhaBMRRestoreReverseReplicationDialogBoxHelp;
	private String rhaTrialVersionDownloadUrl;
	
	private String planScheduleAddHelp;
	private String planScheduleAddThrottleHelp;
	private String planScheduleAddMergeHelp;
	private String planScheduleAddReplicationHelp;
	private String planScheduleAddReplicationThrottleHelp;
	private String planScheduleAddReplicationMergeHelp;
	private String planBackupTaskSelectSourceNodeHelp;
	private String planVsphereBackupTaskSelectSourceNodeHelp;
	private String emailSettingBackupHelp;
	private String emailSettingReplicationHelp;
	private String emailSettingVSphereHelp;
	private String emailSettingConversionHelp;
	private String fileCopySettingHelp;
	private String fileCopyAddHelp;
	private String cloudConfigurationSettingsHelp;
	private String dataStoreEstimatedHelp;
	
	private String backupLink;
	private String restoreLink;
	private String copRecoveryPointLink;
	private String mergeLink;
	private String conversionLink;
	private String vmBackupLink;
	private String vmRecoveryLink;
	private String vmCatalogLink;
	private String vmMergeLink;
	private String catalogLink;
	private String grtCatalogLink;
	private String fileCopyBackupLink;
	private String fileCopyPurgeLink;
	private String fileCopyRecoveryLink;
	private String fileCopyCatalogLink;
	private String ondemandCatalogLink;
	private String ondemandVMCatalogLink;
	private String replicationOutLink;
	private String replicationInLink;
	private String rpsMergeLink;
	private String rpsCoversionLink;
	private String bmrLink;

	private String virtualStandbyRecoveryPointSnapshotsHelp;
	private String virtualStandbyBackupSettingEmailHelp;
	private String virtualStandbyJobMonitorURL;
	
	private String dataSeedingHelp;
	private String datastoreImportLink;
	private String dataStoreDetailHelp;
	private String specifyHypervisorHelp;
	private String sharedFolderHelp;
	
	private String dataMigrationHelp;
	
	private String sendFeedbackHelpMenuLink;
	private String windowsAgentGuideHelpMenuLink;
	private String linuxAgentGuideHelpMenuLink;
	
	private String siteStateHelp;
	private String siteAddHelp;
	private String siteModifyHelp;
	private String siteUpdateUrlHelp;
	
	private String exchangeGranularRestoreUtility;
	
	/**Instant VM*/
	private String instantVMRecoveryPoint;	
	private String instantVMHypervisor;
	private String instantVMRecoveryServer;
	private String instantVMSettings;
	private String instantVMAddHyperVServer;	
	private String instantVMAddEsxServer;
	private String dnsUpateSettingWindow;
	private String ipV4AddressDialog;
	private String networkAdapterWindow;
	private String sessionPassordValidationWindow;
	private String updateDNSWindow;
	/**Instant VM*/
	
	private String configurationWizardHelp;
	
	private String siteUpdateUrlEmailSettingHelp;
	
	/**
	 * key of helplink for Merge now window
	 */
	private String mergeNowWindowHelp;
	
	/**
	 * key of helplink for Replicate now window
	 */
	private String replicateNowWindowHelp;
	
	/**
	 * key of helplink for Filecopy now window
	 */
	private String filecopyNowWindowHelp;
	
	/**
	 * key of helplink for Filearchive now window
	 */
	private String filearchiveNowWindowHelp;
	
	public String getFilearchiveNowWindowHelp() {
		return filearchiveNowWindowHelp;
	}
	public void setFilearchiveNowWindowHelp(String filearchiveNowWindowHelp) {
		this.filearchiveNowWindowHelp = filearchiveNowWindowHelp;
	}
	public String getMergeNowWindowHelp() {
		return mergeNowWindowHelp;
	}
	public void setMergeNowWindowHelp(String mergeNowWindowHelp) {
		this.mergeNowWindowHelp = mergeNowWindowHelp;
	}
	public String getReplicateNowWindowHelp() {
		return replicateNowWindowHelp;
	}
	public void setReplicateNowWindowHelp(String replicateNowWindowHelp) {
		this.replicateNowWindowHelp = replicateNowWindowHelp;
	}
	public String getFilecopyNowWindowHelp() {
		return filecopyNowWindowHelp;
	}
	public void setFilecopyNowWindowHelp(String filecopyNowWindowHelp) {
		this.filecopyNowWindowHelp = filecopyNowWindowHelp;
	}
	
	public String getConfigurationWizardHelp() {
		return configurationWizardHelp;
	}
	public void setConfigurationWizardHelp(String configurationWizardHelp) {
		this.configurationWizardHelp = configurationWizardHelp;
	}
	public String getBackupLink() {
		return backupLink;
	}
	public void setBackupLink(String backupLink) {
		this.backupLink = backupLink;
	}
	public String getRestoreLink() {
		return restoreLink;
	}
	public void setRestoreLink(String restoreLink) {
		this.restoreLink = restoreLink;
	}
	public String getCopRecoveryPointLink() {
		return copRecoveryPointLink;
	}
	public void setCopRecoveryPointLink(String copRecoveryPointLink) {
		this.copRecoveryPointLink = copRecoveryPointLink;
	}
	public String getMergeLink() {
		return mergeLink;
	}
	public void setMergeLink(String mergeLink) {
		this.mergeLink = mergeLink;
	}
	public String getConversionLink() {
		return conversionLink;
	}
	public void setConversionLink(String conversionLink) {
		this.conversionLink = conversionLink;
	}
	public String getVmBackupLink() {
		return vmBackupLink;
	}
	public void setVmBackupLink(String vmBackupLink) {
		this.vmBackupLink = vmBackupLink;
	}
	public String getVmRecoveryLink() {
		return vmRecoveryLink;
	}
	public void setVmRecoveryLink(String vmRecoveryLink) {
		this.vmRecoveryLink = vmRecoveryLink;
	}
	public String getVmCatalogLink() {
		return vmCatalogLink;
	}
	public void setVmCatalogLink(String vmCatalogLink) {
		this.vmCatalogLink = vmCatalogLink;
	}
	public String getVmMergeLink() {
		return vmMergeLink;
	}
	public void setVmMergeLink(String vmMergeLink) {
		this.vmMergeLink = vmMergeLink;
	}
	public String getCatalogLink() {
		return catalogLink;
	}
	public void setCatalogLink(String catalogLink) {
		this.catalogLink = catalogLink;
	}
	public String getFileCopyBackupLink() {
		return fileCopyBackupLink;
	}
	public void setFileCopyBackupLink(String fileCopyBackupLink) {
		this.fileCopyBackupLink = fileCopyBackupLink;
	}
	public String getFileCopyPurgeLink() {
		return fileCopyPurgeLink;
	}
	public void setFileCopyPurgeLink(String fileCopyPurgeLink) {
		this.fileCopyPurgeLink = fileCopyPurgeLink;
	}
	public String getFileCopyRecoveryLink() {
		return fileCopyRecoveryLink;
	}
	public void setFileCopyRecoveryLink(String fileCopyRecoveryLink) {
		this.fileCopyRecoveryLink = fileCopyRecoveryLink;
	}
	public String getFileCopyCatalogLink() {
		return fileCopyCatalogLink;
	}
	public void setFileCopyCatalogLink(String fileCopyCatalogLink) {
		this.fileCopyCatalogLink = fileCopyCatalogLink;
	}
	public String getOndemandCatalogLink() {
		return ondemandCatalogLink;
	}
	public void setOndemandCatalogLink(String ondemandCatalogLink) {
		this.ondemandCatalogLink = ondemandCatalogLink;
	}
	public String getOndemandVMCatalogLink() {
		return ondemandVMCatalogLink;
	}
	public void setOndemandVMCatalogLink(String ondemandVMCatalogLink) {
		this.ondemandVMCatalogLink = ondemandVMCatalogLink;
	}
	public String getReplicationOutLink() {
		return replicationOutLink;
	}
	public void setReplicationOutLink(String replicationOutLink) {
		this.replicationOutLink = replicationOutLink;
	}
	public String getReplicationInLink() {
		return replicationInLink;
	}
	public void setReplicationInLink(String replicationInLink) {
		this.replicationInLink = replicationInLink;
	}
	public String getRpsMergeLink() {
		return rpsMergeLink;
	}
	public void setRpsMergeLink(String rpsMergeLink) {
		this.rpsMergeLink = rpsMergeLink;
	}
	public String getRpsCoversionLink() {
		return rpsCoversionLink;
	}
	public void setRpsCoversionLink(String rpsCoversionLink) {
		this.rpsCoversionLink = rpsCoversionLink;
	}
	public String getGrtCatalogLink() {
		return grtCatalogLink;
	}
	public void setGrtCatalogLink(String grtCatalogLink) {
		this.grtCatalogLink = grtCatalogLink;
	}
	public String getBmrLink() {
		return bmrLink;
	}
	public void setBmrLink(String bmrLink) {
		this.bmrLink = bmrLink;
	}
	
	public String getStandbyNetworkConfigureDialogHelp() {
		return standbyNetworkConfigureDialogHelp;
	}
	public void setStandbyNetworkConfigureDialogHelp(
			String standbyNetworkConfigureDialogHelp) {
		this.standbyNetworkConfigureDialogHelp = standbyNetworkConfigureDialogHelp;
	}
	public String getLiveChatLink() {
		return liveChatLink;
	}
	public void setLiveChatLink(String liveChatLink) {
		this.liveChatLink = liveChatLink;
	}
	public String getCaURLLink() {
		return caURLLink;
	}
	public void setCaURLLink(String caURLLink) {
		this.caURLLink = caURLLink;
	}
	
	public String getConfigurationITMGMTSettingHelp() {
		return configurationITMGMTSettingHelp;
	}
	public void setConfigurationITMGMTSettingHelp(
			String configurationITMGMTSettingHelp) {
		this.configurationITMGMTSettingHelp = configurationITMGMTSettingHelp;
	}
	
	public String getNodeImportWindowHelp() {
		return nodeImportWindowHelp;
	}
	public void setNodeImportWindowHelp(String nodeImportWindowHelp) {
		this.nodeImportWindowHelp = nodeImportWindowHelp;
	}
	
	public String getNodeHyperVisorWindowHelp() {
		return nodeHyperVisorWindowHelp;
	}
	public void setNodeHyperVisorWindowHelp(String nodeHyperVisorWindowHelp) {
		this.nodeHyperVisorWindowHelp = nodeHyperVisorWindowHelp;
	}
	
	public String getConfigurationCmDBConfigPanelHelp() {
		return configurationCmDBConfigPanelHelp;
	}
	public void setConfigurationCmDBConfigPanelHelp(
			String configurationCmDBConfigPanelHelp) {
		this.configurationCmDBConfigPanelHelp = configurationCmDBConfigPanelHelp;
	}
	public String getConfigurationSRMSettingHelp() {
		return configurationSRMSettingHelp;
	}
	public void setConfigurationSRMSettingHelp(String configurationSRMSettingHelp) {
		this.configurationSRMSettingHelp = configurationSRMSettingHelp;
	}

	public String getConfigurationNodeDeleteSettingHelp() {
		return configurationNodeDeleteSettingHelp;
	}
	public void setConfigurationNodeDeleteSettingHelp(
			String configurationNodeDeleteSettingHelp) {
		this.configurationNodeDeleteSettingHelp = configurationNodeDeleteSettingHelp;
	}
	public String getConfigurationEmailTemplateSettingHelp() {
		return configurationEmailTemplateSettingHelp;
	}
	public void setConfigurationEmailTemplateSettingHelp(
			String configurationEmailTemplateSettingHelp) {
		this.configurationEmailTemplateSettingHelp = configurationEmailTemplateSettingHelp;
	}
	public String getConfigurationEmailServerSettingsHelp() {
		return configurationEmailServerSettingsHelp;
	}
	public void setConfigurationEmailServerSettingsHelp(
			String configurationEmailServerSettingsHelp) {
		this.configurationEmailServerSettingsHelp = configurationEmailServerSettingsHelp;
	}
	public String getConfigurationEdgeUpdateSettingHelp() {
		return configurationEdgeUpdateSettingHelp;
	}
	public void setConfigurationEdgeUpdateSettingHelp(
			String configurationEdgeUpdateSettingHelp) {
		this.configurationEdgeUpdateSettingHelp = configurationEdgeUpdateSettingHelp;
	}
	public String getConfigurationEdgePreferenceSettingHelp() {
		return configurationEdgePreferenceSettingHelp;
	}
	public void setConfigurationEdgePreferenceSettingHelp(
			String configurationEdgePreferenceSettingHelp) {
		this.configurationEdgePreferenceSettingHelp = configurationEdgePreferenceSettingHelp;
	}
	public String getConfigurationD2DSettingHelp() {
		return configurationD2DSettingHelp;
	}
	public void setConfigurationD2DSettingHelp(String configurationD2DSettingHelp) {
		this.configurationD2DSettingHelp = configurationD2DSettingHelp;
	}

	public String getConfigurationAutoDiscoverySettingHelp() {
		return configurationAutoDiscoverySettingHelp;
	}
	public void setConfigurationAutoDiscoverySettingHelp(
			String configurationAutoDiscoverySettingHelp) {
		this.configurationAutoDiscoverySettingHelp = configurationAutoDiscoverySettingHelp;
	}
	public String getConfigurationASBUSettingHelp() {
		return configurationASBUSettingHelp;
	}
	public void setConfigurationASBUSettingHelp(String configurationASBUSettingHelp) {
		this.configurationASBUSettingHelp = configurationASBUSettingHelp;
	}
	
	public String getReleaseNotesURL() {
		return releaseNotesURL;
	}
	public void setReleaseNotesURL(String releaseNotesURL) {
		this.releaseNotesURL = releaseNotesURL;
	}
	public String getFooterHorWidgetFeedLink() {
		return footerHorWidgetFeedLink;
	}
	public void setFooterHorWidgetFeedLink(String footerHorWidgetFeedLink) {
		this.footerHorWidgetFeedLink = footerHorWidgetFeedLink;
	}
	public String getFooterHorWidgetFacebookLink() {
		return footerHorWidgetFacebookLink;
	}
	public void setFooterHorWidgetFacebookLink(String footerHorWidgetFacebookLink) {
		this.footerHorWidgetFacebookLink = footerHorWidgetFacebookLink;
	}
	public String getFooterHorWidgetTwitterLink() {
		return footerHorWidgetTwitterLink;
	}
	public void setFooterHorWidgetTwitterLink(String footerHorWidgetTwitterLink) {
		this.footerHorWidgetTwitterLink = footerHorWidgetTwitterLink;
	}
	public String getRssJspUrl() {
		return rssJspUrl;
	}
	public void setRssJspUrl(String rssJspUrl) {
		this.rssJspUrl = rssJspUrl;
	}
	public String getBrandingPanelVideosLink() {
		return brandingPanelVideosLink;
	}
	public void setBrandingPanelVideosLink(String brandingPanelVideosLink) {
		this.brandingPanelVideosLink = brandingPanelVideosLink;
	}
	public String getBrandingPanelVideosCASupportLink() {
		return brandingPanelVideosCASupportLink;
	}
	public void setBrandingPanelVideosCASupportLink(
			String brandingPanelVideosCASupportLink) {
		this.brandingPanelVideosCASupportLink = brandingPanelVideosCASupportLink;
	}
	public String getBrandingPanelCaSupportLink() {
		return brandingPanelCaSupportLink;
	}
	public void setBrandingPanelCaSupportLink(String brandingPanelCaSupportLink) {
		this.brandingPanelCaSupportLink = brandingPanelCaSupportLink;
	}
	public String getBrandingPanelProvideYFDLink() {
		return brandingPanelProvideYFDLink;
	}
	public void setBrandingPanelProvideYFDLink(String brandingPanelProvideYFDLink) {
		this.brandingPanelProvideYFDLink = brandingPanelProvideYFDLink;
	}
	public String getBrandingPanelProvideYFDLinkInternal() {
		return brandingPanelProvideYFDLinkInternal;
	}
	public void setBrandingPanelProvideYFDLinkInternal(
			String brandingPanelProvideYFDLinkInternal) {
		this.brandingPanelProvideYFDLinkInternal = brandingPanelProvideYFDLinkInternal;
	}
	public String getBrandingPanelUserComDLink() {
		return brandingPanelUserComDLink;
	}
	public void setBrandingPanelUserComDLink(String brandingPanelUserComDLink) {
		this.brandingPanelUserComDLink = brandingPanelUserComDLink;
	}
	public String getBrandingPanelExpertAdCenLink() {
		return brandingPanelExpertAdCenLink;
	}
	public void setBrandingPanelExpertAdCenLink(String brandingPanelExpertAdCenLink) {
		this.brandingPanelExpertAdCenLink = brandingPanelExpertAdCenLink;
	}
	public String getBrandingPanelEdgeHelpMenuLink() {
		return brandingPanelEdgeHelpMenuLink;
	}
	public void setBrandingPanelEdgeHelpMenuLink(
			String brandingPanelEdgeHelpMenuLink) {
		this.brandingPanelEdgeHelpMenuLink = brandingPanelEdgeHelpMenuLink;
	}
	public String getBrandingPanelEdgeKnowledgeMenuLink() {
		return brandingPanelEdgeKnowledgeMenuLink;
	}
	public void setBrandingPanelEdgeKnowledgeMenuLink(
			String brandingPanelEdgeKnowledgeMenuLink) {
		this.brandingPanelEdgeKnowledgeMenuLink = brandingPanelEdgeKnowledgeMenuLink;
	}
	public String getBrandingPanelUserGuideMenuLink() {
		return brandingPanelUserGuideMenuLink;
	}
	public void setBrandingPanelUserGuideMenuLink(
			String brandingPanelUserGuideMenuLink) {
		this.brandingPanelUserGuideMenuLink = brandingPanelUserGuideMenuLink;
	}
	public String getAddServerWindowHelp() {
		return addServerWindowHelp;
	}
	public void setAddServerWindowHelp(String addServerWindowHelp) {
		this.addServerWindowHelp = addServerWindowHelp;
	}
	public String getaDDiscoverDialogHelp() {
		return aDDiscoverDialogHelp;
	}
	public void setaDDiscoverDialogHelp(String aDDiscoverDialogHelp) {
		this.aDDiscoverDialogHelp = aDDiscoverDialogHelp;
	}
	public String getAddNewGroupWindowHelp() {
		return addNewGroupWindowHelp;
	}
	public String getModifyGroupWindowHelp() {
		return modifyGroupWindowHelp;
	}
	public void setAddNewGroupWindowHelp(String addNewGroupWindowHelp) {
		this.addNewGroupWindowHelp = addNewGroupWindowHelp;
	}
	public String getNodeDetailWindowHelp() {
		return nodeDetailWindowHelp;
	}
	public void setNodeDetailWindowHelp(String nodeDetailWindowHelp) {
		this.nodeDetailWindowHelp = nodeDetailWindowHelp;
	}
	public String getSettingWindowHelp() {
		return settingWindowHelp;
	}
	public void setSettingWindowHelp(String settingWindowHelp) {
		this.settingWindowHelp = settingWindowHelp;
	}
	public String getNodeWindowHelp() {
		return nodeWindowHelp;
	}
	public String getNodeWindowHelpForAppliance() {
		return nodeWindowHelpForAppliance;
	}
	public void setNodeWindowHelpForAppliance(String nodeWindowHelpForAppliance) {
		this.nodeWindowHelpForAppliance = nodeWindowHelpForAppliance;
	}
	public String getD2DNodeWindowHelp() {
		return d2dNodeWindowHelp;
	}
	public String getAutoManagedNodeWindowHelp() {
		return autoManagedNodeWindowHelp;
	}
	public void setNodeWindowHelp(String nodeWindowHelp) {
		this.nodeWindowHelp = nodeWindowHelp;
	}
	public String getWizardWindowHelp() {
		return wizardWindowHelp;
	}
	public String getNodeDiscoveryWindowHelp() {
		return nodeDiscoveryWindowHelp;
	}
	public void setNodeDiscoveryWindowHelp(String nodeDiscoveryWindowHelp) {
		this.nodeDiscoveryWindowHelp = nodeDiscoveryWindowHelp;
	}
	public String getVmDiscoveryWindowHelp() {
		return vmDiscoveryWindowHelp;
	}
	public void setVmDiscoveryWindowHelp(String vmDiscoveryWindowHelpVSphere) {
		this.vmDiscoveryWindowHelp = vmDiscoveryWindowHelpVSphere;
	}
	public String getVmImportFromVSphereWindowHelp() {
		return vmImportFromVSphereWindowHelp;
	}
	public void setVmImportFromVSphereWindowHelp(
			String vmImportFromVSphereWindowHelp) {
		this.vmImportFromVSphereWindowHelp = vmImportFromVSphereWindowHelp;
	}
	public void setWizardWindowHelp(String wizardWindowHelp) {
		this.wizardWindowHelp = wizardWindowHelp;
	}
	public String getConfigurationPanelHelp() {
		return configurationPanelHelp;
	}
	public void setConfigurationPanelHelp(String configurationPanelHelp) {
		this.configurationPanelHelp = configurationPanelHelp;
	}
	public String getDefaultHelpLink() {
		return defaultHelpLink;
	}
	public void setDefaultHelpLink(String defaultHelpLink) {
		this.defaultHelpLink = defaultHelpLink;
	}
	public String getAsbuSettingRunNowHelp() {
		return asbuSettingRunNowHelp;
	}
	public void setAsbuSettingRunNowHelp(String asbuSettingRunNowHelp) {
		this.asbuSettingRunNowHelp = asbuSettingRunNowHelp;
	}
	public String getSrmSettingRunNowHelp() {
		return srmSettingRunNowHelp;
	}
	public void setSrmSettingRunNowHelp(String srmSettingRunNowHelp) {
		this.srmSettingRunNowHelp = srmSettingRunNowHelp;
	}
	public String getCredentialWindowHelp() {
		return credentialWindowHelp;
	}
	public void setCredentialWindowHelp(String credentialWindowHelp) {
		this.credentialWindowHelp = credentialWindowHelp;
	}
	public String getEsxWindowHelp() {
		return esxWindowHelp;
	}
	public void setEsxWindowHelp(String esxWindowHelp) {
		this.esxWindowHelp = esxWindowHelp;
	}
	public String getSyncNodeDialogHelp() {
		return syncNodeDialogHelp;
	}
	public void setSyncNodeDialogHelp(String syncNodeDialogHelp) {
		this.syncNodeDialogHelp = syncNodeDialogHelp;
	}
	public String getStatusMonitorPanelHelp() {
		return statusMonitorPanelHelp;
	}
	public void setStatusMonitorPanelHelp(String statusMonitorPanelHelp) {
		this.statusMonitorPanelHelp = statusMonitorPanelHelp;
	}
	public String getEditPolicyDialogBoxHelp() {
		return editPolicyDialogBoxHelp;
	}
	public void setEditPolicyDialogBoxHelp(String editPolicyDialogBoxHelp) {
		this.editPolicyDialogBoxHelp = editPolicyDialogBoxHelp;
	}
	public String getCreatePolicyDialogBoxHelp() {
		return createPolicyDialogBoxHelp;
	}
	public void setCreatePolicyDialogBoxHelp(String createPolicyDialogBoxHelp) {
		this.createPolicyDialogBoxHelp = createPolicyDialogBoxHelp;
	}
	public String getPolicyCopyDialogBoxHelp(){
		return policyCopyDialogBoxHelp;
	}
	public void setPolicyCopyDialogBoxHelp(String policyCopyDialogBoxHelp){
		this.policyCopyDialogBoxHelp = policyCopyDialogBoxHelp;
	}
	public String getLicenseManagementWindowHelp() {
		return licenseManagementWindowHelp;
	}
	public void setLicenseManagementWindowHelp(String licenseManagementWindowHelp) {
		this.licenseManagementWindowHelp = licenseManagementWindowHelp;
	}
	public String getNewTabDialogHelp() {
		return newTabDialogHelp;
	}
	public void setNewTabDialogHelp(String newTabDialogHelp) {
		this.newTabDialogHelp = newTabDialogHelp;
	}
	public String getDeployWindowHelp() {
		return deployWindowHelp;
	}
	public void setDeployWindowHelp(String deployWindowHelp) {
		this.deployWindowHelp = deployWindowHelp;
	}
	public String getPolicyManagementResultWindowHelp() {
		return policyManagementResultWindowHelp;
	}
	public void setPolicyManagementResultWindowHelp(
			String policyManagementResultWindowHelp) {
		this.policyManagementResultWindowHelp = policyManagementResultWindowHelp;
	}
	public String getPolicyManagementVMCredentialWindowHelp() {
		return policyManagementVMCredentialWindowHelp;
	}
	public void setPolicyManagementVMCredentialWindowHelp(
			String policyManagementVMCredentialWindowHelp) {
		this.policyManagementVMCredentialWindowHelp = policyManagementVMCredentialWindowHelp;
	}
	public String getPolicyManagementAssignToNodesDialogHelp() {
		return policyManagementAssignToNodesDialogHelp;
	}
	public void setPolicyManagementAssignToNodesDialogHelp(
			String policyManagementAssignToNodesDialogHelp) {
		this.policyManagementAssignToNodesDialogHelp = policyManagementAssignToNodesDialogHelp;
	}
	public String getPolicyManagementAssignToNodesDialogBox4VMHelp() {
		return policyManagementAssignToNodesDialogBox4VMHelp;
	}
	public void setPolicyManagementAssignToNodesDialogBox4VMHelp(
			String policyManagementAssignToNodesDialogBox4VMHelp) {
		this.policyManagementAssignToNodesDialogBox4VMHelp = policyManagementAssignToNodesDialogBox4VMHelp;
	}
	public String getImportVSphereVMConfirmOverwriteDialogBoxHelp() {
		return ImportVSphereVMConfirmOverwriteDialogBoxHelp;
	}
	public void setImportVSphereVMConfirmOverwriteDialogBoxHelp(
			String importVSphereVMConfirmOverwriteDialogBoxHelp) {
		ImportVSphereVMConfirmOverwriteDialogBoxHelp = importVSphereVMConfirmOverwriteDialogBoxHelp;
	}
	public String getEmailSchdulerMainHelp() {
		return emailSchdulerMainHelp;
	}
	public void setEmailSchdulerMainHelp(String emailSchdulerMainHelp) {
		this.emailSchdulerMainHelp = emailSchdulerMainHelp;
	}
	public String getEmailSchdulerNewHelp() {
		return emailSchdulerNewHelp;
	}
	public void setEmailSchdulerNewHelp(String emailSchdulerNewHelp) {
		this.emailSchdulerNewHelp = emailSchdulerNewHelp;
	}
	public String getEmailSchdulerEditHelp() {
		return emailSchdulerEditHelp;
	}
	public void setEmailSchdulerEditHelp(String emailSchdulerEditHelp) {
		this.emailSchdulerEditHelp = emailSchdulerEditHelp;
	}
	public String getEmailDialogHelp() {
		return emailDialogHelp;
	}
	public void setEmailDialogHelp(String emailDialogHelp) {
		this.emailDialogHelp = emailDialogHelp;
	}
	public String getConfigurationDBPanelHelp() {
		return configurationDBPanelHelp;
	}
	public void setConfigurationDBPanelHelp(String configurationDBPanelHelp) {
		this.configurationDBPanelHelp = configurationDBPanelHelp;
	}
	public String getUpdateNodeHelp() {
		return updateNodeHelp;
	}
	public void setUpdateNodeHelp(String updateNodeHelp) {
		this.updateNodeHelp = updateNodeHelp;
	}
	public String getRpsUpdateNodeHelp() {
		return rpsUpdateNodeHelp;
	}
	public void setRpsUpdateNodeHelp(String rpsUpdateNodeHelp) {
		this.rpsUpdateNodeHelp = rpsUpdateNodeHelp;
	}
	public String getAddServerWindowEditHelp() {
		return addServerWindowEditHelp;
	}
	public void setAddServerWindowEditHelp(String addServerWindowEditHelp) {
		this.addServerWindowEditHelp = addServerWindowEditHelp;
	}
	public String getIntroVideoEmbedLink() {
		return introVideoEmbedLink;
	}
	public void setIntroVideoEmbedLink(String introVideoEmbedLink) {
		this.introVideoEmbedLink = introVideoEmbedLink;
	}
	public String getCASupportIntroVideoEmbedLink() {
		return CASupportIntroVideoEmbedLink;
	}
	public void setCASupportIntroVideoEmbedLink(String cASupportIntroVideoEmbedLink) {
		CASupportIntroVideoEmbedLink = cASupportIntroVideoEmbedLink;
	}
	public String getAdminAccountHelp() {
		return adminAccountHelp;
	}
	public void setAdminAccountHelp(String adminAccountHelp) {
		this.adminAccountHelp = adminAccountHelp;
	}
	
	public String getDeployD2DConfigurationHelp()
	{
		return deployD2DConfigurationHelp;
	}

	public void setDeployD2DConfigurationHelp( String deployD2DConfigurationHelp )
	{
		this.deployD2DConfigurationHelp = deployD2DConfigurationHelp;
	}
	
	public String getNodeDiscoveryCredentialWindowHelp() {
		return nodeDiscoveryCredentialWindowHelp;
	}

	public void setNodeDiscoveryCredentialWindowHelp(String nodeDiscoveryCredentialWindowHelp) {
		this.nodeDiscoveryCredentialWindowHelp = nodeDiscoveryCredentialWindowHelp;
	}
	public String getPFCCBTFailSolutionLink() {
		return PFCCBTFailSolutionLink;
	}
	public void setPFCCBTFailSolutionLink(String pFCCBTFailSolutionLink) {
		this.PFCCBTFailSolutionLink = pFCCBTFailSolutionLink;
	}
	public String getPFCSharedSCSIDeviceSolutionLink() {
		return PFCSharedSCSIDeviceSolutionLink;
	}
	public void setPFCSharedSCSIDeviceSolutionLink(
			String pFCSharedSCSIDeviceSolutionLink) {
		this.PFCSharedSCSIDeviceSolutionLink = pFCSharedSCSIDeviceSolutionLink;
	}
	public String getPFCCredentialInvalidSolutionLink() {
		return PFCCredentialInvalidSolutionLink;
	}
	public void setPFCCredentialInvalidSolutionLink(
			String pFCCredentialInvalidSolutionLink) {
		this.PFCCredentialInvalidSolutionLink = pFCCredentialInvalidSolutionLink;
	}
	public String getPFCIndepedentDiskSolutionLink() {
		return PFCIndepedentDiskSolutionLink;
	}
	public void setPFCIndepedentDiskSolutionLink(
			String pFCIndepedentDiskSolutionLink) {
		this.PFCIndepedentDiskSolutionLink = pFCIndepedentDiskSolutionLink;
	}
	public String getPFCPhysicalRDMDiskSolutionLink() {
		return PFCPhysicalRDMDiskSolutionLink;
	}
	public void setPFCPhysicalRDMDiskSolutionLink(
			String pFCPhysicalRDMDiskSolutionLink) {
		this.PFCPhysicalRDMDiskSolutionLink = pFCPhysicalRDMDiskSolutionLink;
	}
	public String getPFCVirtualRDMDiskSolutionLink() {
		return PFCVirtualRDMDiskSolutionLink;
	}
	public void setPFCVirtualRDMDiskSolutionLink(
			String pFCVirtualRDMDiskSolutionLink) {
		this.PFCVirtualRDMDiskSolutionLink = pFCVirtualRDMDiskSolutionLink;
	}
	public String getPFCVMToolsInvalidSolutionLink() {
		return PFCVMToolsInvalidSolutionLink;
	}
	public void setPFCVMToolsInvalidSolutionLink(
			String pFCVMToolsInvalidSolutionLink) {
		this.PFCVMToolsInvalidSolutionLink = pFCVMToolsInvalidSolutionLink;
	}
	public String getPFCVIXInvalidSolutionLink() {
		return PFCVIXInvalidSolutionLink;
	}
	public void setPFCVIXInvalidSolutionLink(String pFCVIXInvalidSolutionLink) {
		this.PFCVIXInvalidSolutionLink = pFCVIXInvalidSolutionLink;
	}
	public String getPFCApplicationFailSolutionLink() {
		return PFCApplicationFailSolutionLink;
	}
	public void setPFCApplicationFailSolutionLink(
			String pFCApplicationFailSolutionLink) {
		this.PFCApplicationFailSolutionLink = pFCApplicationFailSolutionLink;
	}
	public String getPFCIDEDiskNotSupportSolutionLink() {
		return PFCIDEDiskNotSupportSolutionLink;
	}
	public void setPFCIDEDiskNotSupportSolutionLink(
			String pFCIDEDiskNotSupportSolutionLink) {
		this.PFCIDEDiskNotSupportSolutionLink = pFCIDEDiskNotSupportSolutionLink;
	}
	public String getPFCSATADiskNotSupportSolutionLink() {
		return PFCSATADiskNotSupportSolutionLink;
	}
	public void setPFCSATADiskNotSupportSolutionLink(
			String pFCSATADiskNotSupportSolutionLink) {
		this.PFCSATADiskNotSupportSolutionLink = pFCSATADiskNotSupportSolutionLink;
	}
	public String getPFCNotEnoughSCSISlotSolutionLink() {
		return PFCNotEnoughSCSISlotSolutionLink;
	}
	public void setPFCNotEnoughSCSISlotSolutionLink(
			String pFCNotEnoughSCSISlotSolutionLink) {
		this.PFCNotEnoughSCSISlotSolutionLink = pFCNotEnoughSCSISlotSolutionLink;
	}
	public String getPFCNotSupportESXVersionSolutionLink() {
		return PFCNotSupportESXVersionSolutionLink;
	}
	public void setPFCNotSupportESXVersionSolutionLink(
			String pFCNotSupportESXVersionSolutionLink) {
		this.PFCNotSupportESXVersionSolutionLink = pFCNotSupportESXVersionSolutionLink;
	}
	public String getPFCNotSupportDynamicDiskSolutionLink() {
		return PFCNotSupportDynamicDiskSolutionLink;
	}
	public void setPFCNotSupportDynamicDiskSolutionLink(
			String pFCNotSupportDynamicDiskSolutionLink) {
		this.PFCNotSupportDynamicDiskSolutionLink = pFCNotSupportDynamicDiskSolutionLink;
	}
	public String getPFCTotalHelpLink() {
		return PFCTotalHelpLink;
	}
	public void setPFCTotalHelpLink(String pFCTotalHelpLink) {
		PFCTotalHelpLink = pFCTotalHelpLink;
	}
	public String getPFCNFSDataStoreSolutionLink() {
		return PFCNFSDataStoreSolutionLink;
	}
	public void setPFCNFSDataStoreSolutionLink(
			String pFCNFSDataStoreSolutionLink) {
		PFCNFSDataStoreSolutionLink = pFCNFSDataStoreSolutionLink;
	}
	public String getLearnMoreLink() {
		return learnMoreLink;
	}
	public void setLearnMoreLink(String learnMoreLink) {
		this.learnMoreLink = learnMoreLink;
	}

	public String getNodeImportFromRHAWindowHelp() {
		return nodeImportFromRHAWindowHelp;
	}

	public void setNodeImportFromRHAWindowHelp(String nodeImportFromRHAWindowHelp) {
		this.nodeImportFromRHAWindowHelp = nodeImportFromRHAWindowHelp;
	}
	
	public String getImportRemoteNodesFromFileWindowHelp() {
		return importRemoteNodesFromFileWindowHelp;
	}

	public void setImportRemoteNodesFromFileWindowHelp(String importRemoteNodesFromFileWindowHelp) {
		this.importRemoteNodesFromFileWindowHelp = importRemoteNodesFromFileWindowHelp;
	}
	public String getPFCNotSupportStorageSpacesSolutionLink() {
		return PFCNotSupportStorageSpacesSolutionLink;
	}
	public void setPFCNotSupportStorageSpacesSolutionLink(
			String pFCNotSupportStorageSpacesSolutionLink) {
		PFCNotSupportStorageSpacesSolutionLink = pFCNotSupportStorageSpacesSolutionLink;
	}
	public String getCreateRemotePolicyDialogBoxHelp() {
		return createRemotePolicyDialogBoxHelp;
	}
	public void setCreateRemotePolicyDialogBoxHelp(String createRemotePolicyDialogBoxHelp) {
		this.createRemotePolicyDialogBoxHelp = createRemotePolicyDialogBoxHelp;
	}
	public String getEditRemotePolicyDialogBoxHelp() {
		return editRemotePolicyDialogBoxHelp;
	}
	public void setEditRemotePolicyDialogBoxHelp(String editRemotePolicyDialogBoxHelp) {
		this.editRemotePolicyDialogBoxHelp = editRemotePolicyDialogBoxHelp;
	}
	public String getConfigureConverterHelp() {
		return configureConverterHelp;
	}
	public void setConfigureConverterHelp(String configureConverterHelp) {
		this.configureConverterHelp = configureConverterHelp;
	}
	public String getSetSessionPasswordWindowHelp() {
		return setSessionPasswordWindowHelp;
	}
	public void setSetSessionPasswordWindowHelp(String setSessionPasswordWindowHelp) {
		this.setSessionPasswordWindowHelp = setSessionPasswordWindowHelp;
	}
	public String getDeployD2DForUpdate7NodeWarningLink() {
		return deployD2DForUpdate7NodeWarningLink;
	}
	public void setDeployD2DForUpdate7NodeWarningLink(
			String deployD2DForUpdate7NodeWarningLink) {
		this.deployD2DForUpdate7NodeWarningLink = deployD2DForUpdate7NodeWarningLink;
	}
	public String getReportChartGenHelp() {
		return reportChartGenHelp;
	}
	public void setReportChartGenHelp(String reportChartGenHelp) {
		this.reportChartGenHelp = reportChartGenHelp;
	}
	public String getPFCHyperVVMCredentialInvalidSolutionLink() {
		return PFCHyperVVMCredentialInvalidSolutionLink;
	}
	public void setPFCHyperVVMCredentialInvalidSolutionLink(String pFCHyperVVMCredentialInvalidSolutionLink) {
		PFCHyperVVMCredentialInvalidSolutionLink = pFCHyperVVMCredentialInvalidSolutionLink;
	}
	public String getPFCHyperVVMPowerStateInvalidSolutionLink() {
		return PFCHyperVVMPowerStateInvalidSolutionLink;
	}
	public void setPFCHyperVVMPowerStateInvalidSolutionLink(String pFCHyperVVMPowerStateInvalidSolutionLink) {
		PFCHyperVVMPowerStateInvalidSolutionLink = pFCHyperVVMPowerStateInvalidSolutionLink;
	}
	public String getPFCHyperVInteServiceInvalidSolutionLink() {
		return PFCHyperVInteServiceInvalidSolutionLink;
	}
	public void setPFCHyperVInteServiceInvalidSolutionLink(String pFCHyperVInteServiceInvalidSolutionLink) {
		PFCHyperVInteServiceInvalidSolutionLink = pFCHyperVInteServiceInvalidSolutionLink;
	}
	public String getPFCHyperVInteServiceOutOfDateSolutionLink() {
		return PFCHyperVInteServiceOutOfDateSolutionLink;
	}
	public void setPFCHyperVInteServiceOutOfDateSolutionLink(String pFCHyperVInteServiceOutOfDateSolutionLink) {
		PFCHyperVInteServiceOutOfDateSolutionLink = pFCHyperVInteServiceOutOfDateSolutionLink;
	}
	public String getPFCHyperVCredentailInvalidSolutionLink() {
		return PFCHyperVCredentailInvalidSolutionLink;
	}
	public void setPFCHyperVCredentailInvalidSolutionLink(String pFCHyperVCredentailInvalidSolutionLink) {
		PFCHyperVCredentailInvalidSolutionLink = pFCHyperVCredentailInvalidSolutionLink;
	}
	public String getPFCHyperVInteServiceIncompatibleSolutionLink() {
		return PFCHyperVInteServiceIncompatibleSolutionLink;
	}
	public void setPFCHyperVInteServiceIncompatibleSolutionLink(String pFCHyperVInteServiceIncompatibleSolutionLink) {
		PFCHyperVInteServiceIncompatibleSolutionLink = pFCHyperVInteServiceIncompatibleSolutionLink;
	}
	public String getPFCHyperVDiskTypeNotSupportedSolutionLink() {
		return PFCHyperVDiskTypeNotSupportedSolutionLink;
	}
	public void setPFCHyperVDiskTypeNotSupportedSolutionLink(String pFCHyperVDiskTypeNotSupportedSolutionLink) {
		PFCHyperVDiskTypeNotSupportedSolutionLink = pFCHyperVDiskTypeNotSupportedSolutionLink;
	}
	public String getPFCHyperVFSTypeNotSupportedSolutionLink() {
		return PFCHyperVFSTypeNotSupportedSolutionLink;
	}
	public void setPFCHyperVFSTypeNotSupportedSolutionLink(String pFCHyperVFSTypeNotSupportedSolutionLink) {
		PFCHyperVFSTypeNotSupportedSolutionLink = pFCHyperVFSTypeNotSupportedSolutionLink;
	}
	public String getPFCHyperVScopedSnapshotEnabledSolutionLink() {
		return PFCHyperVScopedSnapshotEnabledSolutionLink;
	}
	public void setPFCHyperVScopedSnapshotEnabledSolutionLink(String pFCHyperVScopedSnapshotEnabledSolutionLink) {
		PFCHyperVScopedSnapshotEnabledSolutionLink = pFCHyperVScopedSnapshotEnabledSolutionLink;
	}
	public String getPFCHyperVPhysicalDiskSolutionLink() {
		return PFCHyperVPhysicalDiskSolutionLink;
	}
	public void setPFCHyperVPhysicalDiskSolutionLink(String pFCHyperVPhysicalDiskSolutionLink) {
		PFCHyperVPhysicalDiskSolutionLink = pFCHyperVPhysicalDiskSolutionLink;
	}
	public String getPFCHyperVDiskOnRemoteShareSolutionLink() {
		return PFCHyperVDiskOnRemoteShareSolutionLink;
	}
	public void setPFCHyperVDiskOnRemoteShareSolutionLink(String pFCHyperVDiskOnRemoteShareSolutionLink) {
		PFCHyperVDiskOnRemoteShareSolutionLink = pFCHyperVDiskOnRemoteShareSolutionLink;
	}
	public String getPFCHyperVDCCannotGetVMbyGuidSolutionLink() {
		return PFCHyperVDCCannotGetVMbyGuidSolutionLink;
	}
	public void setPFCHyperVDCCannotGetVMbyGuidSolutionLink(String pFCHyperVDCCannotGetVMbyGuidSolutionLink) {
		PFCHyperVDCCannotGetVMbyGuidSolutionLink = pFCHyperVDCCannotGetVMbyGuidSolutionLink;
	}
	public String getPFCHyperVDCVMnotRunningSolutionLink() {
		return PFCHyperVDCVMnotRunningSolutionLink;
	}
	public void setPFCHyperVDCVMnotRunningSolutionLink(String pFCHyperVDCVMnotRunningSolutionLink) {
		PFCHyperVDCVMnotRunningSolutionLink = pFCHyperVDCVMnotRunningSolutionLink;
	}
	public String getPFCHyperVDCIntegrationServiceNotOKSolutionLink() {
		return PFCHyperVDCIntegrationServiceNotOKSolutionLink;
	}
	public void setPFCHyperVDCIntegrationServiceNotOKSolutionLink(String pFCHyperVDCIntegrationServiceNotOKSolutionLink) {
		PFCHyperVDCIntegrationServiceNotOKSolutionLink = pFCHyperVDCIntegrationServiceNotOKSolutionLink;
	}
	public String getPFCHyperVDCannotAccessVMSolutionLink() {
		return PFCHyperVDCannotAccessVMSolutionLink;
	}
	public void setPFCHyperVDCannotAccessVMSolutionLink(String pFCHyperVDCannotAccessVMSolutionLink) {
		PFCHyperVDCannotAccessVMSolutionLink = pFCHyperVDCannotAccessVMSolutionLink;
	}
	
	
	public String getRhaCreateScenarioWizardHelp()
	{
		return rhaCreateScenarioWizardHelp;
	}
	
	public void setRhaCreateScenarioWizardHelp( String rhaCreateScenarioWizardHelp )
	{
		this.rhaCreateScenarioWizardHelp = rhaCreateScenarioWizardHelp;
	}
	
	public String getRhaFullSystemPlatformSettingsDialogBoxHelp()
	{
		return rhaFullSystemPlatformSettingsDialogBoxHelp;
	}
	
	public void setRhaFullSystemPlatformSettingsDialogBoxHelp(
		String rhaFullSystemPlatformSettingsDialogBoxHelp )
	{
		this.rhaFullSystemPlatformSettingsDialogBoxHelp = rhaFullSystemPlatformSettingsDialogBoxHelp;
	}
	
	public String getRhaNetworkAdapterMappingDialogBoxHelp()
	{
		return rhaNetworkAdapterMappingDialogBoxHelp;
	}
	
	public void setRhaNetworkAdapterMappingDialogBoxHelp(
		String rhaNetworkAdapterMappingDialogBoxHelp )
	{
		this.rhaNetworkAdapterMappingDialogBoxHelp = rhaNetworkAdapterMappingDialogBoxHelp;
	}
	
	public String getRhaRemoteInstallAddHostDialogBoxHelp()
	{
		return rhaRemoteInstallAddHostDialogBoxHelp;
	}
	
	public void setRhaRemoteInstallAddHostDialogBoxHelp(
		String rhaRemoteInstallAddHostDialogBoxHelp )
	{
		this.rhaRemoteInstallAddHostDialogBoxHelp = rhaRemoteInstallAddHostDialogBoxHelp;
	}
	
	public String getRhaRemoteInstallEditInstallTargetDialogBoxHelp()
	{
		return rhaRemoteInstallEditInstallTargetDialogBoxHelp;
	}
	
	public void setRhaRemoteInstallEditInstallTargetDialogBoxHelp(
		String rhaRemoteInstallEditInstallTargetDialogBoxHelp )
	{
		this.rhaRemoteInstallEditInstallTargetDialogBoxHelp = rhaRemoteInstallEditInstallTargetDialogBoxHelp;
	}
	
	public String getRhaRemoteInstallEditInstallSettingsDialogBoxHelp()
	{
		return rhaRemoteInstallEditInstallSettingsDialogBoxHelp;
	}
	
	public void setRhaRemoteInstallEditInstallSettingsDialogBoxHelp(
		String rhaRemoteInstallEditInstallSettingsDialogBoxHelp )
	{
		this.rhaRemoteInstallEditInstallSettingsDialogBoxHelp = rhaRemoteInstallEditInstallSettingsDialogBoxHelp;
	}
	
	public String getRhaRemoteInstallViewLogsDialogBoxHelp()
	{
		return rhaRemoteInstallViewLogsDialogBoxHelp;
	}
	
	public void setRhaRemoteInstallViewLogsDialogBoxHelp(
		String rhaRemoteInstallViewLogsDialogBoxHelp )
	{
		this.rhaRemoteInstallViewLogsDialogBoxHelp = rhaRemoteInstallViewLogsDialogBoxHelp;
	}
	
	public String getRhaBMRRestoreReverseReplicationDialogBoxHelp()
	{
		return rhaBMRRestoreReverseReplicationDialogBoxHelp;
	}
	
	public void setRhaBMRRestoreReverseReplicationDialogBoxHelp(
		String rhaBMRRestoreReverseReplicationDialogBoxHelp )
	{
		this.rhaBMRRestoreReverseReplicationDialogBoxHelp = rhaBMRRestoreReverseReplicationDialogBoxHelp;
	}
	
	public String getRhaTrialVersionDownloadUrl()
	{
		return rhaTrialVersionDownloadUrl;
	}
	
	public void setRhaTrialVersionDownloadUrl( String rhaTrialVersionDownloadUrl )
	{
		this.rhaTrialVersionDownloadUrl = rhaTrialVersionDownloadUrl;
	}
	
	public String getPlanScheduleAddHelp()
	{
		return planScheduleAddHelp;
	}
	
	public void setPlanScheduleAddHelp(
		String planScheduleAddHelp )
	{
		this.planScheduleAddHelp = planScheduleAddHelp;
	}
	
	public String getPlanScheduleAddThrottleHelp()
	{
		return planScheduleAddThrottleHelp;
	}
	
	public void setPlanScheduleAddThrottleHelp(
		String planScheduleAddThrottleHelp )
	{
		this.planScheduleAddThrottleHelp = planScheduleAddThrottleHelp;
	}
	
	public String getPlanScheduleAddMergeHelp()
	{
		return planScheduleAddMergeHelp;
	}
	
	public void setPlanScheduleAddMergeHelp(
		String planScheduleAddMergeHelp )
	{
		this.planScheduleAddMergeHelp = planScheduleAddMergeHelp;
	}
		
	public String getPlanScheduleAddReplicationHelp()
	{
		return planScheduleAddReplicationHelp;
	}
	
	public void setPlanScheduleAddReplicationHelp(
		String planScheduleAddReplicationHelp )
	{
		this.planScheduleAddReplicationHelp = planScheduleAddReplicationHelp;
	}
	
	public String getPlanScheduleAddReplicationThrottleHelp()
	{
		return planScheduleAddReplicationThrottleHelp;
	}
	
	public void setPlanScheduleAddReplicationThrottleHelp(
		String planScheduleAddReplicationThrottleHelp )
	{
		this.planScheduleAddReplicationThrottleHelp = planScheduleAddReplicationThrottleHelp;
	}
	
	public String getPlanScheduleAddReplicationMergeHelp()
	{
		return planScheduleAddReplicationMergeHelp;
	}
	
	public void setPlanScheduleAddReplicationMergeHelp(
		String planScheduleAddReplicationMergeHelp )
	{
		this.planScheduleAddReplicationMergeHelp = planScheduleAddReplicationMergeHelp;
	}
	
	public String getConfigurationAddCustomerHelp() {
		return configurationAddCustomerHelp;
	}
	
	public void setConfigurationAddCustomerHelp(String configurationAddCustomerHelp) {
		this.configurationAddCustomerHelp = configurationAddCustomerHelp;
	}
	
	public String getConfigurationEidtCustomerHelp() {
		return configurationEidtCustomerHelp;
	}
	
	public void setConfigurationEidtCustomerHelp(
			String configurationEidtCustomerHelp) {
		this.configurationEidtCustomerHelp = configurationEidtCustomerHelp;
	}
	
	public String getPlanBackupTaskSelectSourceNodeHelp() {
		return planBackupTaskSelectSourceNodeHelp;
	}
	
	public void setPlanBackupTaskSelectSourceNodeHelp(
			String planBackupTaskSelectSourceNodeHelp) {
		this.planBackupTaskSelectSourceNodeHelp = planBackupTaskSelectSourceNodeHelp;
	}
	
	public String getPlanVsphereBackupTaskSelectSourceNodeHelp() {
		return planVsphereBackupTaskSelectSourceNodeHelp;
	}
	public void setPlanVsphereBackupTaskSelectSourceNodeHelp(
			String planVsphereBackupTaskSelectSourceNodeHelp) {
		this.planVsphereBackupTaskSelectSourceNodeHelp = planVsphereBackupTaskSelectSourceNodeHelp;
	}
	public String getEmailSettingBackupHelp() {
		return emailSettingBackupHelp;
	}
	
	public void setEmailSettingBackupHelp(
			String emailSettingBackupHelp) {
		this.emailSettingBackupHelp = emailSettingBackupHelp;
	}
	
	public String getEmailSettingReplicationHelp() {
		return emailSettingReplicationHelp;
	}
	
	public void setEmailSettingReplicationHelp(
			String emailSettingReplicationHelp) {
		this.emailSettingReplicationHelp = emailSettingReplicationHelp;
	}
		
	public String getEmailSettingVSphereHelp() {
		return emailSettingVSphereHelp;
	}
	
	public void setEmailSettingVSphereHelp(
			String emailSettingVSphereHelp) {
		this.emailSettingVSphereHelp = emailSettingVSphereHelp;
	}
	public String getEmailSettingConversionHelp() {
		return emailSettingConversionHelp;
	}
	
	public void setEmailSettingConversionHelp(
			String emailSettingConversionHelp) {
		this.emailSettingConversionHelp = emailSettingConversionHelp;
	}
	
	public String getFileCopySettingHelp() {
		return fileCopySettingHelp;
	}
	
	public void setFileCopySettingHelp(String fileCopySettingHelp) {
		this.fileCopySettingHelp = fileCopySettingHelp;
	}
	
	public String getFileCopyAddHelp() {
		return fileCopyAddHelp;
	}
	
	public void setFileCopyAddHelp(String fileCopyAddHelp) {
		this.fileCopyAddHelp = fileCopyAddHelp;
	}
	
	public String getCloudConfigurationSettingsHelp() {
		return cloudConfigurationSettingsHelp;
	}
	
	public void setcloudConfigurationSettingsHelp(String cloudConfigurationSettingsHelp) {
		this.cloudConfigurationSettingsHelp = cloudConfigurationSettingsHelp;
	}

	public String getDataStoreEstimatedHelp() {
		return dataStoreEstimatedHelp;
	}
	
	public void setDataStoreEstimatedHelp(String dataStoreEstimatedHelp) {
		this.dataStoreEstimatedHelp = dataStoreEstimatedHelp;
	}
	public String getDataStoreCreateHelp() {
		return dataStoreCreateHelp;
	}
	public void setDataStoreCreateHelp(String dataStoreCreateHelp) {
		this.dataStoreCreateHelp = dataStoreCreateHelp;
	}
	public String getDataStoreEditHelp() {
		return dataStoreEditHelp;
	}
	public void setDataStoreEditHelp(String dataStoreEditHelp) {
		this.dataStoreEditHelp = dataStoreEditHelp;
	}
	public String getPlanCreateHelp() {
		return planCreateHelp;
	}
	public void setPlanCreateHelp(String planCreateHelp) {
		this.planCreateHelp = planCreateHelp;
	}
	public String getPlanCreateHelpForAppliance() {
		return planCreateHelpForAppliance;
	}
	public void setPlanCreateHelpForAppliance(String planCreateHelp) {
		this.planCreateHelpForAppliance = planCreateHelp;
	}
	public String getPlanEditHelp() {
		return planEditHelp;
	}
	public void setPlanEditHelp(String planEditHelp) {
		this.planEditHelp = planEditHelp;
	}

	public String getVirtualStandbyRecoveryPointSnapshotsHelp() {
		return virtualStandbyRecoveryPointSnapshotsHelp;
	}

	public void setVirtualStandbyRecoveryPointSnapshotsHelp(String virtualStandbyRecoveryPointSnapshotsHelp) {
		this.virtualStandbyRecoveryPointSnapshotsHelp = virtualStandbyRecoveryPointSnapshotsHelp;
	}

	public String getVirtualStandbyBackupSettingEmailHelp() {
		return virtualStandbyBackupSettingEmailHelp;
	}

	public void setVirtualStandbyBackupSettingEmailHelp(String virtualStandbyBackupSettingEmailHelp) {
		this.virtualStandbyBackupSettingEmailHelp = virtualStandbyBackupSettingEmailHelp;
	}

	public String getVirtualStandbyJobMonitorURL() {
		return virtualStandbyJobMonitorURL;
	}

	public void setVirtualStandbyJobMonitorURL(String virtualStandbyJobMonitorURL) {
		this.virtualStandbyJobMonitorURL = virtualStandbyJobMonitorURL;
	}
	public String getDataSeedingHelp() {
		return dataSeedingHelp;
	}
	public void setDataSeedingHelp(String dataSeedingHelp) {
		this.dataSeedingHelp = dataSeedingHelp;
	}
	public String getDatastoreImportLink() {
		return datastoreImportLink;
	}
	public void setDatastoreImportLink(String datastoreImportLink) {
		this.datastoreImportLink = datastoreImportLink;
	}
	public String getDataStoreDetailHelp() {
		return dataStoreDetailHelp;
	}
	public void setDataStoreDetailHelp(String dataStoreDetailHelp) {
		this.dataStoreDetailHelp = dataStoreDetailHelp;
	}
	public String getSpecifyHypervisorHelp() {
		return specifyHypervisorHelp;
	}
	public void setSpecifyHypervisorHelp(String specifyHypervisorHelp) {
		this.specifyHypervisorHelp = specifyHypervisorHelp;
	}
	
	public String getSharedFolderHelp() {
		return sharedFolderHelp;
	}
	public void getSharedFolderHelp(String sharedFolderHelp) {
		this.sharedFolderHelp = sharedFolderHelp;
	}
	public String getDataMigrationHelp() {
		return dataMigrationHelp;
	}
	public void setDataMigrationHelp(String dataMigrationHelp) {
		this.dataMigrationHelp = dataMigrationHelp;
	}
	public String getSendFeedbackHelpMenuLink() {
		return sendFeedbackHelpMenuLink;
	}
	public void setSendFeedbackHelpMenuLink(String sendFeedbackHelpMenuLink) {
		this.sendFeedbackHelpMenuLink = sendFeedbackHelpMenuLink;
	}
	public String getWindowsAgentGuideHelpMenuLink() {
		return windowsAgentGuideHelpMenuLink;
	}
	public void setWindowsAgentGuideHelpMenuLink(
			String windowsAgentGuideHelpMenuLink) {
		this.windowsAgentGuideHelpMenuLink = windowsAgentGuideHelpMenuLink;
	}
	public String getLinuxAgentGuideHelpMenuLink() {
		return linuxAgentGuideHelpMenuLink;
	}
	public void setLinuxAgentGuideHelpMenuLink(String linuxAgentGuideHelpMenuLink) {
		this.linuxAgentGuideHelpMenuLink = linuxAgentGuideHelpMenuLink;
	}
	public String getSiteStateHelp() {
		return siteStateHelp;
	}
	public void setSiteStateHelp(String siteStateHelp) {
		this.siteStateHelp = siteStateHelp;
	}

	public String getExchangeGranularRestoreUtility() {
		return exchangeGranularRestoreUtility;
	}
	public void setExchangeGranularRestoreUtility(String exchangeGranularRestoreUtility) {
		this.exchangeGranularRestoreUtility = exchangeGranularRestoreUtility;
	}
	
	public String getInstantVMRecoveryPoint() {
		return instantVMRecoveryPoint;
	}
	public void setInstantVMRecoveryPoint(String instantVMRecoveryPoint) {
		this.instantVMRecoveryPoint = instantVMRecoveryPoint;
	}
	public String getInstantVMHypervisor() {
		return instantVMHypervisor;
	}
	public void setInstantVMHypervisor(String instantVMHypervisor) {
		this.instantVMHypervisor = instantVMHypervisor;
	}
	public String getInstantVMRecoveryServer() {
		return instantVMRecoveryServer;
	}
	public void setInstantVMRecoveryServer(String instantVMRecoveryServer) {
		this.instantVMRecoveryServer = instantVMRecoveryServer;
	}
	public String getInstantVMSettings() {
		return instantVMSettings;
	}
	public void setInstantVMSettings(String instantVMSettings) {
		this.instantVMSettings = instantVMSettings;
	}
	public String getSiteAddHelp() {
		return siteAddHelp;
	}
	public void setSiteAddHelp(String siteAddHelp) {
		this.siteAddHelp = siteAddHelp;
	}
	public String getSiteModifyHelp() {
		return siteModifyHelp;
	}
	public void setSiteModifyHelp(String siteModifyHelp) {
		this.siteModifyHelp = siteModifyHelp;
	}
	public String getSiteUpdateUrlHelp() {
		return siteUpdateUrlHelp;
	}
	public void setSiteUpdateUrlHelp(String siteUpdateUrlHelp) {
		this.siteUpdateUrlHelp = siteUpdateUrlHelp;
	}
	public String getInstantVMAddHyperVServer() {
		return instantVMAddHyperVServer;
	}
	public void setInstantVMAddHyperVServer(String instantVMAddHyperVServer) {
		this.instantVMAddHyperVServer = instantVMAddHyperVServer;
	}
	public String getInstantVMAddEsxServer() {
		return instantVMAddEsxServer;
	}
	public void setInstantVMAddEsxServer(String instantVMAddEsxServer) {
		this.instantVMAddEsxServer = instantVMAddEsxServer;
	}
	public String getDnsUpateSettingWindow() {
		return dnsUpateSettingWindow;
	}
	public void setDnsUpateSettingWindow(String dnsUpateSettingWindow) {
		this.dnsUpateSettingWindow = dnsUpateSettingWindow;
	}
	public String getIpV4AddressDialog() {
		return ipV4AddressDialog;
	}
	public void setIpV4AddressDialog(String ipV4AddressDialog) {
		this.ipV4AddressDialog = ipV4AddressDialog;
	}
	public String getNetworkAdapterWindow() {
		return networkAdapterWindow;
	}
	public void setNetworkAdapterWindow(String networkAdapterWindow) {
		this.networkAdapterWindow = networkAdapterWindow;
	}
	public String getSessionPassordValidationWindow() {
		return sessionPassordValidationWindow;
	}
	public void setSessionPassordValidationWindow(
			String sessionPassordValidationWindow) {
		this.sessionPassordValidationWindow = sessionPassordValidationWindow;
	}
	public String getUpdateDNSWindow() {
		return updateDNSWindow;
	}
	public void setUpdateDNSWindow(String updateDNSWindow) {
		this.updateDNSWindow = updateDNSWindow;
	}
	
	public String getAddASBUServerWindowHelp() {
		return addASBUServerWindowHelp;
	}
	public void setAddASBUServerWindowHelp(String addASBUServerWindowHelp) {
		this.addASBUServerWindowHelp = addASBUServerWindowHelp;
	}
	public String getSiteUpdateUrlEmailSettingHelp() {
		return siteUpdateUrlEmailSettingHelp;
	}
	public void setSiteUpdateUrlEmailSettingHelp(
			String siteUpdateUrlEmailSettingHelp) {
		this.siteUpdateUrlEmailSettingHelp = siteUpdateUrlEmailSettingHelp;
	}
	
}
