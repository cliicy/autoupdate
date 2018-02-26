package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

public class PolicyDeployFlags
{
	public static final int KeepCurrentSettingsWhenUnassin	= 0x00000001;
	public static final int NeedRegisterFirst				= 0x00000002;
	public static final int UnregisterNodeAfterUnassign		= 0x00000004;
	
	public static final int ModifyPlan						= 0x00000008;
	public static final int DeletePlan						= 0x00000010;
	public static final int RedeployPlan					= 0x00000100;
	
	public static final int BackupTaskDeleted				= 0x00000020;
	public static final int VMBackupTaskDeleted				= 0x00000040;
	public static final int ConversionTaskDeleted			= 0x00000080;
	public static final int ArchiveToTapeTaskDeleted		= 0x00000200;
}
