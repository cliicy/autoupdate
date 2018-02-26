package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.HashMap;
import java.util.Map;

public class PolicyEditSession
{
	public class Keys
	{
		public static final String Id							= "Id";
		public static final String GeneralInformation			= "GeneralInformation";
		public static final String BackupSettings				= "BackupSettings";
		public static final String ArchivingSettings			= "ArchivingSettings";
		public static final String ScheduledExportSettings		= "ScheduledExportSettings";
		public static final String VirtualConvertionSettings	= "VirtualConvertionSettings";
		public static final String VMBackupSettings				= "VMBackupSettings";
		public static final String PreferencesSettings			= "PreferencesSettings";
		public static final String subscriptionSettings			= "SubscriptionSettings";
		public static final String RpsSettings					= "RpsSettings";
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private Map<String, Object> valueMap;
	
	//////////////////////////////////////////////////////////////////////////
	
	public PolicyEditSession()
	{
		valueMap = new HashMap<String, Object>();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public Object getValue( String key )
	{
		return this.valueMap.get( key );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public void setValue( String key, Object value )
	{
		this.valueMap.put( key, value );
	}
}
