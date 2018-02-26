package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;

import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;

public class ParsedBackupPolicy implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private BackupPolicy generalInfo;
	private SubscriptionConfiguration subscriptionConfiguration;
	private BackupConfiguration backupSettings;
	private ArchiveConfiguration archiveConfiguration;
	private ScheduledExportConfiguration scheduledExportConfiguration;
	private String vcmSettings;
	private PreferencesConfiguration preferencesSettings;
	private VMBackupConfiguration vmBackupConfiguration;
	
	public BackupPolicy getGeneralInfo()
	{
		return generalInfo;
	}
	
	public void setGeneralInfo( BackupPolicy generalInfo )
	{
		this.generalInfo = generalInfo;
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

	public void setArchiveConfiguration( ArchiveConfiguration archiveConfiguration )
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

	public String getVcmSettings()
	{
		return vcmSettings;
	}

	public void setVcmSettings( String vcmSettings )
	{
		this.vcmSettings = vcmSettings;
	}

	public PreferencesConfiguration getPreferencesSettings()
	{
		return preferencesSettings;
	}

	public void setPreferencesSettings( PreferencesConfiguration preferencesSettings )
	{
		this.preferencesSettings = preferencesSettings;
	}


	
	public void setVmBackupConfiguration(VMBackupConfiguration vmBackupConfiguration)
	{
		this.vmBackupConfiguration = vmBackupConfiguration;
	}
	
	public VMBackupConfiguration getVmBackupConfiguration()
	{
		return this.vmBackupConfiguration;
	}

	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionConfiguration = subscriptionConfiguration;
	}
	
	
}
