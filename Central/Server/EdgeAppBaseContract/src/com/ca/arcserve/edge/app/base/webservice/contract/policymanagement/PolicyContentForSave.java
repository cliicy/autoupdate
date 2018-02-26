package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.rps.webservice.data.policy.RPSConfiguration;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.JobScriptCombo4Wan;

public class PolicyContentForSave
{
	// General information
	private String policyName;
	private int policyType;
	private int contentFlag;
	private int nodeId;
	private String policyGuid;
	private int policyProductType;
	
	// Individual settings
	private BackupConfiguration backupSettings;
	private ArchiveConfiguration archiveConfiguration;
	private ScheduledExportConfiguration scheduledExportConfiguration;
	private PreferencesConfiguration preferencesSettings;
	private JobScriptCombo4Wan vcmConfiguration;
	private VSphereBackupConfiguration vmBackupSettings;
	private RPSPolicy rpsSettings;
	private SubscriptionConfiguration subscriptionSettings;
	
	public String getPolicyName()
	{
		return policyName;
	}
	
	public void setPolicyName( String policyName )
	{
		this.policyName = policyName;
	}
	
	public int getPolicyType()
	{
		return policyType;
	}
	
	public void setPolicyType( int policyType )
	{
		this.policyType = policyType;
	}
	
	public int getContentFlag()
	{
		return contentFlag;
	}
	
	public void setContentFlag( int contentFlag )
	{
		this.contentFlag = contentFlag;
	}
	
	public BackupConfiguration getBackupSettings()
	{
		return backupSettings;
	}
	
	public void setBackupSettings( BackupConfiguration backupSettings )
	{
		this.backupSettings = backupSettings;
	}
	
	public ArchiveConfiguration getArchiveConfiguration()
	{
		return archiveConfiguration;
	}
	
	public void setArchiveConfiguration(
		ArchiveConfiguration archiveConfiguration )
	{
		this.archiveConfiguration = archiveConfiguration;
	}
	
	public ScheduledExportConfiguration getScheduledExportConfiguration()
	{
		return scheduledExportConfiguration;
	}
	
	public void setScheduledExportConfiguration(
		ScheduledExportConfiguration scheduledExportConfiguration )
	{
		this.scheduledExportConfiguration = scheduledExportConfiguration;
	}
	
	public PreferencesConfiguration getPreferencesSettings()
	{
		return preferencesSettings;
	}
	
	public void setPreferencesSettings(
		PreferencesConfiguration preferencesSettings )
	{
		this.preferencesSettings = preferencesSettings;
	}
	
	public JobScriptCombo4Wan getVcmConfiguration()
	{
		return vcmConfiguration;
	}
	
	public void setVcmConfiguration( JobScriptCombo4Wan vcmConfiguration )
	{
		this.vcmConfiguration = vcmConfiguration;
	}
	
	public VSphereBackupConfiguration getVmBackupSettings()
	{
		return vmBackupSettings;
	}
	
	public void setVmBackupSettings(
		VSphereBackupConfiguration vmBackupSettings )
	{
		this.vmBackupSettings = vmBackupSettings;
	}

	public RPSPolicy getRpsSettings() {
		return rpsSettings;
	}

	public void setRpsSettings(RPSPolicy rpsSettings) {
		this.rpsSettings = rpsSettings;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getPolicyGuid() {
		return policyGuid;
	}

	public void setPolicyGuid(String policyGuid) {
		this.policyGuid = policyGuid;
	}

	public int getPolicyProductType() {
		return policyProductType;
	}

	public void setPolicyProductType(int policyProductType) {
		this.policyProductType = policyProductType;
	}

	public SubscriptionConfiguration getSubscriptionSettings() {
		return subscriptionSettings;
	}

	public void setSubscriptionSettings(SubscriptionConfiguration subscriptionSettings) {
		this.subscriptionSettings = subscriptionSettings;
	}
	
}
