package com.ca.arcflash.webservice.service;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class ServiceContext {

	private static final ServiceContext instance = new ServiceContext();
	private String homeFolderPath;
	private String dataFolderPath;
	private String logFolderPath;
	private String subscriptionConfigurationFilePath;
	private String backupConfigurationFilePath;
	private String preferencesConfigurationFilePath;
	private String autuUpdateSettingsXmlFilePath;
	private String trustedHostFilePath;
	private String localMachineName;
	private String executeHistoryFilePath;
	private String retryPolicyFilePath;
	private String cloudVendorInfoFilePath;
    private String tomcatFilePath;
	private String apmSettingsIniFilePath;
	private String D2DPMClientXMLFilePath;
	private String virtualCenterFilePath;
	// October sprint -
	private String StorageApplianceConfigurationFilePath;
	private String vsphereBackupConfigurationFolderPath;
	@Deprecated private int vSphereMaxJobNum=-1; // separate the max job num of VMware and Hyper-V, this value = vmwareMaxJobNum + hypervMaxJobNum
	private int vmwareMaxJobNum=-1;
	private int hypervMaxJobNum=-1;
	private int sessionDismountTime = 10*60;//in seconds

	private String archiveSourcePoliciesFilePath;
	private String archiveConfigurationFilePath;
	private String archiveSourceDeleteConfigurationFilePath;
	private String archiveUIConfigurationFilePath;
	
	private String scheduledExportConfigurationPath;
	private String binFolderPath;
	
	private String companyName;
	private String productNameD2D;
	private final String DEFAULT_COMPANY_NAME = "CA";
	
	private Properties d2dProperties = null;
	
	private AtomicBoolean serviceStoped = new AtomicBoolean(false);
	
	private String archiveToTapeFilePath;
	
	private String vsphereScheduleExportConfigurationFolderPath;
	
	private String diagInfoFilePath;
	
	private static final Logger logger = Logger.getLogger(ServiceContext.class);
	
	private ServiceContext(){

	}

	public static ServiceContext getInstance(){
		return instance;
	}
	
	public String getHomeFolderPath() {
		return homeFolderPath;
	}

	public void setHomeFolderPath(String homeFolderPath) {
		this.homeFolderPath = homeFolderPath;
	}

	public String getDataFolderPath() {
		return dataFolderPath;
	}

	public void setDataFolderPath(String dataFolderPath) {
		this.dataFolderPath = dataFolderPath;
	}
	
	public String getLogFolderPath() {
		return logFolderPath;
	}
	
	public void setLogFolderPath(String logFolderPath) {
		this.logFolderPath = logFolderPath;
	}

	public String getSubscriptionConfigurationFilePath() {
		return subscriptionConfigurationFilePath;
	}

	public void setSubscriptionConfigurationFilePath(
			String subscriptionConfigurationFilePath) {
		this.subscriptionConfigurationFilePath = subscriptionConfigurationFilePath;
	}

	public String getBackupConfigurationFilePath() {
		return backupConfigurationFilePath;
	}

	public void setBackupConfigurationFilePath(String backupConfigurationFilePath) {
		this.backupConfigurationFilePath = backupConfigurationFilePath;
	}

	public String getPreferencesConfigurationFilePath() {
		return preferencesConfigurationFilePath;
	}

	public void setPreferencesConfigurationFilePath(String in_PreferencesConfigurationFilePath) {
		this.preferencesConfigurationFilePath = in_PreferencesConfigurationFilePath;
	}
	
	public String getAutoUpdateSettingsFilePath() {
		return autuUpdateSettingsXmlFilePath;
	}

	public void setAutoUpdateSettingsFilePath(String in_autoUpdateSettingsFilePath) {
		this.autuUpdateSettingsXmlFilePath = in_autoUpdateSettingsFilePath;
	}

	public String getTrustedHostFilePath() {
		return trustedHostFilePath;
	}

	public void setTrustedHostFilePath(String trustedHostFilePath) {
		this.trustedHostFilePath = trustedHostFilePath;
	}

	public String getLocalMachineName() {
		return localMachineName;
	}

	public void setLocalMachineName(String localMachineName) {
		this.localMachineName = localMachineName;
	}

	public String getExecuteHistoryFilePath() {
		return executeHistoryFilePath;
	}

	public void setExecuteHistoryFilePath(String executeHistoryFilePath) {
		this.executeHistoryFilePath = executeHistoryFilePath;
	}

	public String getRetryPolicyFilePath() {
		return retryPolicyFilePath;
	}

	public void setRetryPolicyFilePath(String retryPolicyFilePath) {
		this.retryPolicyFilePath = retryPolicyFilePath;

	}
	public String getTomcatFilePath() {
		return tomcatFilePath;
	}

	public void setTomcatFilePath(String tomcatFilePath) {
		this.tomcatFilePath = tomcatFilePath;
	}

	public void setApmSettingsIniFilePath(String apmSettingsIniFilePath) {
		this.apmSettingsIniFilePath = apmSettingsIniFilePath;
	}

	public String getApmSettingsIniFilePath() {
		return apmSettingsIniFilePath;
	}

	public void setD2DPMClientXMLFilePath(String in_D2DPMClientXMLFilePath) {
		this.D2DPMClientXMLFilePath = in_D2DPMClientXMLFilePath;
	}

	public String getD2DPMClientXMLFilePath() {
		return D2DPMClientXMLFilePath;
	}

	public String getVirtualCenterFilePath() {
		return virtualCenterFilePath;
	}

	public void setVirtualCenterFilePath(String virtualCenterFilePath) {
		this.virtualCenterFilePath = virtualCenterFilePath;
	}


//	public int getvSphereMaxJobNum() {
//		return vSphereMaxJobNum;
//	}
	
	// get max job number in real time, so that we don't have to 
	// restart service after changing max job number 
	public int getvSphereMaxJobNum() {
		return getHypervMaxJobNum() + getVmwareMaxJobNum();
	}

	

	public void setvSphereMaxJobNum(int vSphereMaxJobNum) {
		this.vSphereMaxJobNum = vSphereMaxJobNum;
	}

	public String getVsphereBackupConfigurationFolderPath() {
		return vsphereBackupConfigurationFolderPath;
	}

	public void setVsphereBackupConfigurationFolderPath(
			String vsphereBackupConfigurationFolderPath) {
		this.vsphereBackupConfigurationFolderPath = vsphereBackupConfigurationFolderPath;
	}
	public String getArchiveConfigurationFilePath() {
		return archiveConfigurationFilePath;
	}

	public void setArchiveConfigurationFilePath(String archiveConfigurationFilePath) {
		this.archiveConfigurationFilePath = archiveConfigurationFilePath;
	}
	public String getArchiveSourcePoliciesFilePath() {
		return archiveSourcePoliciesFilePath;
	}

	public void setArchiveSourcePoliciesFilePath(String in_filePath) {
		this.archiveSourcePoliciesFilePath = in_filePath;
	}

	public String getScheduledExportConfigurationPath() {
		return scheduledExportConfigurationPath;
	}

	public void setScheduledExportConfigurationPath(
			String scheduledExportConfigurationPath) {
		this.scheduledExportConfigurationPath = scheduledExportConfigurationPath;
	}

	public String getCompanyName() {
		if(companyName == null || companyName.isEmpty())
			return DEFAULT_COMPANY_NAME;
		else
			return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getProductNameD2D() {
		if(productNameD2D == null || productNameD2D.isEmpty())
			return WebServiceMessages.getResource("ProductNameD2D");
		else
			return productNameD2D;
	}

	public void setProductNameD2D(String productNameD2D) {
		this.productNameD2D = productNameD2D;
	}

	public Properties getD2dProperties() {
		return d2dProperties;
	}

	public void setD2dProperties(Properties d2dProperties) {
		this.d2dProperties = d2dProperties;
	}
	
	public String getCloudVendorInfoFilePath() {
		return cloudVendorInfoFilePath;
	}

	public void setCloudVendorInfoFilePath(String cloudVendorInfoFilePath) {
		this.cloudVendorInfoFilePath = cloudVendorInfoFilePath;
	}
	
	public void setArchiveUIConfigurationFilePath(
			String archiveUIConfigurationFilePath) {
		this.archiveUIConfigurationFilePath = archiveUIConfigurationFilePath;
	}
	public String getArchiveUIConfigurationFilePath() {
		return archiveUIConfigurationFilePath;
	}
	
	public String getBinFolderPath() {
    	return binFolderPath;
    }

	public void setBinFolderPath(String binFolderPath) {
    	this.binFolderPath = binFolderPath;
    }

	public int getSessionDismountTime() {
		return sessionDismountTime;
	}

	public void setSessionDismountTime(int sessionDismountTime) {
		this.sessionDismountTime = sessionDismountTime;
	}

	public boolean isServiceStoped() {
		return serviceStoped.get();
	}

	public void setServiceStoped(boolean serviceStoped) {
		this.serviceStoped.getAndSet(serviceStoped);
	}

	public String getArchiveToTapeFilePath() {
		return archiveToTapeFilePath;
	}

	public void setArchiveToTapeFilePath(String archiveToTapeFilePath) {
		this.archiveToTapeFilePath = archiveToTapeFilePath;
	}
	
	// October sprint 
	public String getStorageApplianceConfigurationFilePath() {
		return StorageApplianceConfigurationFilePath;
	}

	public void setStorageApplianceConfigurationFilePath(
			String storageApplianceConfigurationFilePath) {
		StorageApplianceConfigurationFilePath = storageApplianceConfigurationFilePath;
	}
	public String getVsphereScheduleExportConfigurationFolderPath() {
		return vsphereScheduleExportConfigurationFolderPath;
	}

	public void setVsphereScheduleExportConfigurationFolderPath(
			String vsphereScheduleExportConfigurationFolderPath) {
		this.vsphereScheduleExportConfigurationFolderPath = vsphereScheduleExportConfigurationFolderPath;
	}
	
	public String getDiagInfoCollectorConfigurationFilePath() {
		return diagInfoFilePath;
	}

	public void setDiagInfoCollectorConfigurationFilePath(String logConfigurationFilePath) {
		this.diagInfoFilePath = logConfigurationFilePath;
	}

	public String getArchiveSourceDeleteConfigurationFilePath() {
		return archiveSourceDeleteConfigurationFilePath;
	}

	public void setArchiveSourceDeleteConfigurationFilePath(
			String archiveSourceDeleteConfigurationFilePath) {
		this.archiveSourceDeleteConfigurationFilePath = archiveSourceDeleteConfigurationFilePath;
	}

//	public int getVmwareMaxJobNum()
//	{
//		return vmwareMaxJobNum;
//	}
	
	// get max job number in real time, so that we don't have to 
	// restart service after changing max job number
	public int getVmwareMaxJobNum()
	{
		int maxJobNum = 4; // default value
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try
		{
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String max = registry.getValue(handle, RegConstants.REGISTRY_KEY_VMWARE_MAX_JOB_NUM);
			
			if (max != null && !max.isEmpty())
			{
				int temp = Integer.parseInt(max);
				if (temp > 0)
				{
					maxJobNum = temp;
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to parse " + RegConstants.REGISTRY_KEY_VMWARE_MAX_JOB_NUM, e);
		}
		finally
		{
			try
			{
				if (handle != 0)
				{
					registry.closeKey(handle);
				}
			}
			catch (Exception e)
			{
			}
			
			ServiceContext.getInstance().setVmwareMaxJobNum(maxJobNum);			
			logger.info("VMware max job num " + maxJobNum);
		}
		
		return maxJobNum;
	}


	public void setVmwareMaxJobNum(int vmwareMaxJobNum)
	{
		this.vmwareMaxJobNum = vmwareMaxJobNum;
	}

//	public int getHypervMaxJobNum()
//	{
//		return hypervMaxJobNum;
//	}
	
	// get max job number in real time, so that we don't have to 
	// restart service after changing max job number
	public int getHypervMaxJobNum()
	{
		int maxJobNum = 10; // default value
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try
		{
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String max = registry.getValue(handle, RegConstants.REGISTRY_KEY_HYPERV_MAX_JOB_NUM);
			
			if (max != null && !max.isEmpty())
			{
				int temp = Integer.parseInt(max);
				if (temp > 0)
				{
					maxJobNum = temp;
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to parse " + RegConstants.REGISTRY_KEY_HYPERV_MAX_JOB_NUM, e);
		}
		finally
		{
			try
			{
				if (handle != 0)
				{
					registry.closeKey(handle);
				}
			}
			catch (Exception e)
			{
			}
			
			ServiceContext.getInstance().setHypervMaxJobNum(maxJobNum);			
			logger.info("Hyper-V max job num " + maxJobNum);
		}
		
		return maxJobNum;
	}


	public void setHypervMaxJobNum(int hypervMaxJobNum)
	{
		this.hypervMaxJobNum = hypervMaxJobNum;
	}
	
}
