package com.ca.arcflash.webservice.edge.policymanagement;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;

public class PolicyUsageMarker
{
	class PolicyFlags
	{
		public static final int BackupAndAchiving	= 0x00000001;
		public static final int VCM					= 0x00000002;
		public static final int VMBackup			= 0x00000004;
	}
	
	public class SettingsTypes
	{
		public static final int Preferences					= 1;
		public static final int BackupSettings				= 2;
		public static final int ArchiveSettings				= 3;
		public static final int VirtualConversionSettings	= 4;
		public static final int VMBackupSettings			= 5;
	}
	
	private static PolicyUsageMarker instance = null;
	private static final Logger logger = Logger.getLogger( PolicyUsageMarker.class );
	protected static final LogUtility logUtility = new LogUtility( logger );
	
	//////////////////////////////////////////////////////////////////////////
	
	private PolicyUsageMarker()
	{
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static PolicyUsageMarker getInstance()
	{
		if (instance == null)
			instance = new PolicyUsageMarker();
		
		return instance;
	}

	//////////////////////////////////////////////////////////////////////////
	
//	private String getD2DRegistryHome()
//	{
//		return "SOFTWARE\\Arcserve\\Unified Data Protection\\Engine";
//	}

	//////////////////////////////////////////////////////////////////////////
	
//	private String getPolicyUsageValueName()
//	{
//		return "EdgePolicyUsage";
//	}

	//////////////////////////////////////////////////////////////////////////

//	public synchronized void setUsePolicy( int policyType, boolean usePolicy )
//	{
//		int policyFlag = policyTypeToPolicyFlag( policyType );
//		int savedPolicyFlag = getPolicyUsageFlag();
//		int newPolicyFlag = (savedPolicyFlag & ~policyFlag);
//		newPolicyFlag |= usePolicy ? policyFlag : 0;
//		
//		setPolicyUsageFlag( newPolicyFlag );
//	}

	//////////////////////////////////////////////////////////////////////////

//	public boolean isUsingPolicy( int policyType )
//	{
//		int policyFlag = policyTypeToPolicyFlag( policyType );
//		int savedPolicyFlag = getPolicyUsageFlag();
//		return ((savedPolicyFlag & policyFlag) != 0);
//	}

	//////////////////////////////////////////////////////////////////////////
	
	public boolean isUsingEdgePolicySettings( int settingsType )
	{
		int policyType = getPolicyTypeBySettingsType( settingsType );
		return isManagedBySomeEdge(policyType);
//		if (!this.isManagedBySomeEdge( policyType ))
//			return false;
//		
//		return isUsingPolicy( policyType );
	}

	//////////////////////////////////////////////////////////////////////////
	
	private int getPolicyTypeBySettingsType( int settingsType )
	{
		switch (settingsType)
		{
		case SettingsTypes.BackupSettings:
		case SettingsTypes.ArchiveSettings:
		case SettingsTypes.Preferences:
			return ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving;
			
		case SettingsTypes.VirtualConversionSettings:
			return ID2DPolicyManagementService.PolicyTypes.VCM;
			
		case SettingsTypes.VMBackupSettings:
			return ID2DPolicyManagementService.PolicyTypes.VMBackup;
			
		default:
			logUtility.writeLog( LogUtility.LogTypes.Error,
				"getPolicyTypeBySettingsType() failed. Unknown settings type. settingsType: %d",
				settingsType );
			return -1;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	private boolean isManagedBySomeEdge( int policyType )
	{
		try
		{
			// For All in one, the app type is CPM
			ApplicationType appType = ApplicationType.CentralManagement; //getAppTypeByPolicyType( policyType );
			D2DEdgeRegistration register = new D2DEdgeRegistration();
			return (register.getRegStatus( "UUID-PLACEHOLDER", appType ) != 0);
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
//	private ApplicationType getAppTypeByPolicyType( int policyType ) throws Exception
//	{
//		switch (policyType)
//		{
//		case ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving:
//			return ApplicationType.CentralManagement;
//			
//		case ID2DPolicyManagementService.PolicyTypes.VCM:
//			return ApplicationType.VirtualConversionManager;
//			
//		case ID2DPolicyManagementService.PolicyTypes.VMBackup:
//			return ApplicationType.vShpereManager;
//			
//		default:
//			throw new Exception();
//		}
//	}

	//////////////////////////////////////////////////////////////////////////

//	private int getPolicyUsageFlag()
//	{
//		try
//		{
//			String markerString =
//				WinRegistry.readString( WinRegistry.HKEY_LOCAL_MACHINE,
//				getD2DRegistryHome(), getPolicyUsageValueName() );
//			
//			logUtility.writeLog( LogUtility.LogTypes.Trace,
//				"markerString: %s", markerString );
//			
//			if (StringUtil.isEmptyOrNull(markerString)){
//				return 0;
//			}else {
//				return Integer.parseInt( markerString );
//			}
//		}
//		catch (Exception e)
//		{
//			// the value may not exist when it had not been set
//			logUtility.writeLog( LogUtility.LogTypes.Warning, e,
//				"getPolicyUsageFlag() failed." );
//			return 0;
//		}
//	}

	//////////////////////////////////////////////////////////////////////////

//	private void setPolicyUsageFlag( int flag )
//	{
//		try
//		{
//			WinRegistry.writeStringValue( WinRegistry.HKEY_LOCAL_MACHINE,
//				getD2DRegistryHome(), getPolicyUsageValueName(),
//				((Integer)flag).toString() );
//		}
//		catch (Exception e)
//		{
//			logUtility.writeLog( LogUtility.LogTypes.Error, e,
//				"setPolicyUsageFlag() failed. flag: %d", flag );
//		}
//	}

	//////////////////////////////////////////////////////////////////////////

//	private int policyTypeToPolicyFlag( int policyType )
//	{
//		switch (policyType)
//		{
//		case ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving:
//			return PolicyFlags.BackupAndAchiving;
//			
//		case ID2DPolicyManagementService.PolicyTypes.VCM:
//			return PolicyFlags.VCM;
//			
//		case ID2DPolicyManagementService.PolicyTypes.RemoteVCM:
//			return PolicyFlags.VCM;
//			
//		case ID2DPolicyManagementService.PolicyTypes.VMBackup:
//			return PolicyFlags.VMBackup;
//			
//		default:
//			logUtility.writeLog( LogUtility.LogTypes.Error,
//				"policyTypeToPolicyFlag() failed. Unknown policy type. policyType: %d",
//				policyType );
//			return 0;
//		}
//	}
}
