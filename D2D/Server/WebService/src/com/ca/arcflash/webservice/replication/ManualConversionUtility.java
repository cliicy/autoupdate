package com.ca.arcflash.webservice.replication;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.MSPManualConversionConstants;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.BackupInfoServerInfo;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.jobscript.base.JobScript;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacade.RHAScenarioState;

public class ManualConversionUtility
{
	@SuppressWarnings( "serial" )
	public static class SessionNotCompleteException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class CreateVssManagerException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class GetSessionVolumesException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class CreateSnapshotSetException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class VssManagerNotCreatedException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class SnapshotSetNotCreatedException extends Exception {}
	
	@SuppressWarnings( "serial" )
	public static class SessionInSnapshotNotCompleteException extends Exception {}
	
	//////////////////////////////////////////////////////////////////////////
	
	private static Logger logger = Logger.getLogger( ManualConversionUtility.class );
	
	private NativeFacade nativeFacade;
	private long vssManagerHandle;
	private String snapshotSetId;
	
	//////////////////////////////////////////////////////////////////////////
	public enum vm_guest_os_identifier_e
	{	
		asianux3_64Guest,			// Asianux Server 3 (64 bit) 	Since vSphere API 4.0
		asianux3Guest,				// Asianux Server 3	  Since vSphere API 4.0
		asianux4_64Guest,			// Asianux Server 4 (64 bit)	Since vSphere API 4.0
		asianux4Guest,				// Asianux Server 4	  Since vSphere API 4.0
		darwin64Guest,				// Darwin (64-bit)	  	Since vSphere API 4.0
		darwinGuest,				// Darwin 
		debian4_64Guest,			// Debian GNU/Linux 4 (64 bit)		 Since vSphere API 4.0
		debian4Guest,				// Debian GNU/Linux 4			Since vSphere API 4.0
		debian5_64Guest,			// Debian GNU/Linux 5 (64 bit)	 Since vSphere API 4.0
		debian5Guest,				// Debian GNU/Linux 5		Since vSphere API 4.0
		dosGuest,					// MS-DOS. 
		freebsd64Guest,				// FreeBSD x64 
		freebsdGuest,				// FreeBSD 
		mandriva64Guest,			// Mandriva Linux (64 bit)		Since vSphere API 4.0
		mandrivaGuest,				// Mandriva Linux	   Since vSphere API 4.0
		netware4Guest,				// Novell NetWare 4 
		netware5Guest,				// Novell NetWare 5.1 
		netware6Guest,				// Novell NetWare 6.x 
		nld9Guest,					// Novell Linux Desktop 9 
		oesGuest,					// Open Enterprise Server 
		openServer5Guest,			// SCO OpenServer 5		Since vSphere API 4.0
		openServer6Guest,			// SCO OpenServer 6		Since vSphere API 4.0
		os2Guest,					// OS/2 
		other24xLinux64Guest,		// Linux 2.4x Kernel (64 bit) (experimental) 
		other24xLinuxGuest,			// Linux 2.4x Kernel 
		other26xLinux64Guest,		// Linux 2.6x Kernel (64 bit) (experimental) 
		other26xLinuxGuest,			// Linux 2.6x Kernel 
		otherGuest,					// Other Operating System 
		otherGuest64,				// Other Operating System (64 bit) (experimental) 
		otherLinux64Guest,			// Linux (64 bit) (experimental) 
		otherLinuxGuest,			// Other Linux 
		redhatGuest,				// Red Hat Linux 2.1 
		rhel2Guest,					// Red Hat Enterprise Linux 2 
		rhel3_64Guest,				// Red Hat Enterprise Linux 3 (64 bit) 
		rhel3Guest,					// Red Hat Enterprise Linux 3 
		rhel4_64Guest,				// Red Hat Enterprise Linux 4 (64 bit) 
		rhel4Guest,					// Red Hat Enterprise Linux 4 
		rhel5_64Guest,				// Red Hat Enterprise Linux 5 (64 bit) (experimental)   Since VI API 2.5
		rhel5Guest,					// Red Hat Enterprise Linux 5	 Since VI API 2.5
		rhel6_64Guest,				// Red Hat Enterprise Linux 6 (64 bit)	Since vSphere API 4.0
		rhel6Guest,					// Red Hat Enterprise Linux 6	 Since vSphere API 4.0
		sjdsGuest,					// Sun Java Desktop System 
		sles10_64Guest,				// Suse Linux Enterprise Server 10 (64 bit) (experimental)	Since VI API 2.5
		sles10Guest,				// Suse linux Enterprise Server 10		Since VI API 2.5
		sles11_64Guest,				// Suse Linux Enterprise Server 11 (64 bit)	  Since vSphere API 4.0
		sles11Guest,				// Suse linux Enterprise Server 11	   Since vSphere API 4.0
		sles64Guest,				// Suse Linux Enterprise Server 9 (64 bit) 
		slesGuest,					// Suse Linux Enterprise Server 9 
		solaris10_64Guest,			// Solaris 10 (64 bit) (experimental) 
		solaris10Guest,				// Solaris 10 (32 bit) (experimental) 
		solaris6Guest,				// Solaris 6 
		solaris7Guest,				// Solaris 7 
		solaris8Guest,				// Solaris 8 
		solaris9Guest,				// Solaris 9 
		suse64Guest,				// Suse Linux (64 bit) 
		suseGuest,					// Suse Linux 
		turboLinux64Guest,			// Turbolinux (64 bit)   Since vSphere API 4.0
		turboLinuxGuest,			// Turbolinux 
		ubuntu64Guest,				// Ubuntu Linux (64 bit) 
		ubuntuGuest,				// Ubuntu Linux 
		unixWare7Guest,				// SCO UnixWare 7	Since vSphere API 4.0
		win2000AdvServGuest,		// Windows 2000 Advanced Server 
		win2000ProGuest,			// Windows 2000 Professional 
		win2000ServGuest,			// Windows 2000 Server 
		win31Guest,					// Windows 3.1 
		win95Guest,					// Windows 95 
		win98Guest,					// Windows 98 
		windows7_64Guest,			// Windows 7 (64 bit) Since vSphere API 4.0
		windows7Guest,				// Windows 7 Since vSphere API 4.0 
		windows7Server64Guest,		// Windows Server 2008 R2 (64 bit)   Since vSphere API 4.0
		windows8_64Guest,			// Windows 8 (64 bit) 	Since vSphere API 5.0
		windows8Guest,				// Windows 8 Since vSphere API 5.0
		windows8Server64Guest,		// Windows 8 Server (64 bit) Since vSphere API 5.0
		windows9Guest,				// Windows 9/10  Since vSphere API 6.0
		windows9_64Guest,           // Windows 9/10 (64 bit) Since vSphere API 6.0
		windows9Server64Guest,      // Windows 9/10 Server (64 bit) Since vSphere API 6.0
		winLonghorn64Guest,			// Windows Longhorn (64 bit) (experimental)	  Since VI API 2.5
		winLonghornGuest,			// Windows Longhorn (experimental)   Since VI API 2.5
		winMeGuest,					// Windows Millenium Edition 
		winNetBusinessGuest,		// Windows Small Business Server 2003 
		winNetDatacenter64Guest,	// Windows Server 2003, Datacenter Edition (64 bit) (experimental)	 Since VI API 2.5
		winNetDatacenterGuest,		// Windows Server 2003, Datacenter Edition	Since VI API 2.5
		winNetEnterprise64Guest,	// Windows Server 2003, Enterprise Edition (64 bit) 
		winNetEnterpriseGuest,		// Windows Server 2003, Enterprise Edition 
		winNetStandard64Guest,		// Windows Server 2003, Standard Edition (64 bit) 
		winNetStandardGuest,		// Windows Server 2003, Standard Edition 
		winNetWebGuest,				// Windows Server 2003, Web Edition 
		winNTGuest,					// Windows NT 4 
		winVista64Guest,			// Windows Vista (64 bit) 
		winVistaGuest,				// Windows Vista 
		winXPHomeGuest,				// Windows XP Home Edition 
		winXPPro64Guest,			// Windows XP Professional Edition (64 bit) 
		winXPProGuest,				// Windows XP Professional 
		unknown						// Add for unknown os.
	};

	public static String vm_guest_os_identifier_e_map[] = 
	{
		"asianux3_64Guest",	
		"asianux3Guest",		
		"asianux4_64Guest",	
		"asianux4Guest",		
		"darwin64Guest",		
		"darwinGuest",		
		"debian4_64Guest",	
		"debian4Guest",		
		"debian5_64Guest",	
		"debian5Guest",		
		"dosGuest",			
		"freebsd64Guest",		
		"freebsdGuest",		
		"mandriva64Guest",	
		"mandrivaGuest",		
		"netware4Guest",		
		"netware5Guest",		
		"netware6Guest",		
		"nld9Guest",			
		"oesGuest",			
		"openServer5Guest",	
		"openServer6Guest",	
		"os2Guest",			
		"other24xLinux64Guest",
		"other24xLinuxGuest",	
		"other26xLinux64Guest",
		"other26xLinuxGuest",	
		"otherGuest",			
		"otherGuest64",		
		"otherLinux64Guest",	
		"otherLinuxGuest",	
		"redhatGuest",		
		"rhel2Guest",			
		"rhel3_64Guest",		
		"rhel3Guest",			
		"rhel4_64Guest",		
		"rhel4Guest",			
		"rhel5_64Guest",		
		"rhel5Guest",			
		"rhel6_64Guest",		
		"rhel6Guest",			
		"sjdsGuest",			
		"sles10_64Guest",		
		"sles10Guest",		
		"sles11_64Guest",		
		"sles11Guest",		
		"sles64Guest",		
		"slesGuest",			
		"solaris10_64Guest",	
		"solaris10Guest",		
		"solaris6Guest",		
		"solaris7Guest",		
		"solaris8Guest",		
		"solaris9Guest",		
		"suse64Guest",		
		"suseGuest",			
		"turboLinux64Guest",	
		"turboLinuxGuest",	
		"ubuntu64Guest",		
		"ubuntuGuest",		
		"unixWare7Guest",		
		"win2000AdvServGuest",
		"win2000ProGuest",	
		"win2000ServGuest",	
		"win31Guest",			
		"win95Guest",			
		"win98Guest",			
		"windows7_64Guest",	
		"windows7Guest",		
		"windows7Server64Guest",
		"windows8_64Guest",			
		"windows8Guest",				
		"windows8Server64Guest",
		"windows9Guest",				 
		"windows9_64Guest",           
		"windows9Server64Guest",      
		"winLonghorn64Guest",	
		"winLonghornGuest",	
		"winMeGuest",			
		"winNetBusinessGuest",
		"winNetDatacenter64Guest",
		"winNetDatacenterGuest",
		"winNetEnterprise64Guest",
		"winNetEnterpriseGuest",
		"winNetStandard64Guest",
		"winNetStandardGuest",
		"winNetWebGuest",		
		"winNTGuest",			
		"winVista64Guest",	
		"winVistaGuest",		
		"winXPHomeGuest",		
		"winXPPro64Guest",	
		"winXPProGuest",		
	};
	
	public static vm_guest_os_identifier_e convert_os_version_to_os_identifier(String ostype, boolean is_x86)
	{
	
		vm_guest_os_identifier_e id = vm_guest_os_identifier_e.unknown;
					
		if (is_x86)
		{
			if (ostype.contains("Windows 10".toUpperCase()))
				id = vm_guest_os_identifier_e.windows9Guest;
			else if(ostype.contains("Windows 8".toUpperCase()))
				id = vm_guest_os_identifier_e.windows8Guest;
			else if(ostype.contains("Windows 7".toUpperCase()))
				id = vm_guest_os_identifier_e.windows7Guest;
			else if(ostype.contains("Windows Server 2008 R2".toUpperCase()))
				id = vm_guest_os_identifier_e.winLonghornGuest;
			else if(ostype.contains("Windows Server 2008".toUpperCase()))
				id = vm_guest_os_identifier_e.winLonghornGuest;
			else if(ostype.contains("Windows Server (R) 2008".toUpperCase()))
				id = vm_guest_os_identifier_e.winLonghornGuest;			
			else if(ostype.contains("Windows Vista".toUpperCase()))
				id = vm_guest_os_identifier_e.winVistaGuest;
			else if(ostype.contains("Microsoft Windows Server 2003 R2".toUpperCase()))
				id = vm_guest_os_identifier_e.winNetEnterpriseGuest;
			else if(ostype.contains("Microsoft Windows Server 2003".toUpperCase()))
				id = vm_guest_os_identifier_e.winNetEnterpriseGuest;
			else if(ostype.contains("Microsoft Windows XP Professional x64 Edition".toUpperCase()))
				id = vm_guest_os_identifier_e.winXPProGuest;
			else if(ostype.contains("Windows Server Longhorn".toUpperCase()))
				id = vm_guest_os_identifier_e.windows8Guest;
		}
		else
		{
			if (ostype.contains("Windows Server 2016".toUpperCase()))
				id = vm_guest_os_identifier_e.windows9Server64Guest;
			else if (ostype.contains("Windows 10".toUpperCase()))
				id = vm_guest_os_identifier_e.windows9_64Guest;
			else if(ostype.contains("Windows 8".toUpperCase()))
				id = vm_guest_os_identifier_e.windows8_64Guest;
			else if(ostype.contains("Windows 7".toUpperCase()))
				id = vm_guest_os_identifier_e.windows7_64Guest;
			else if(ostype.contains("Windows Server 2012".toUpperCase()))
				id = vm_guest_os_identifier_e.windows8Server64Guest;
			else if(ostype.contains("Windows Server 2008 R2".toUpperCase()))
				id = vm_guest_os_identifier_e.windows7Server64Guest;
			else if(ostype.contains("Windows Server 2008".toUpperCase()))
				id = vm_guest_os_identifier_e.winLonghorn64Guest;
			else if(ostype.contains("Windows Server (R) 2008".toUpperCase()))
				id = vm_guest_os_identifier_e.winLonghorn64Guest;			
			else if(ostype.contains("Windows Vista".toUpperCase()))
				id = vm_guest_os_identifier_e.winVista64Guest;
			else if(ostype.contains("Microsoft Windows Server 2003 R2".toUpperCase()))
				id = vm_guest_os_identifier_e.winNetEnterprise64Guest;
			else if(ostype.contains("Microsoft Windows Server 2003".toUpperCase()))
				id = vm_guest_os_identifier_e.winNetEnterprise64Guest;
			else if(ostype.contains("Microsoft Windows XP Professional x64 Edition".toUpperCase()))
				id = vm_guest_os_identifier_e.winXPPro64Guest;
			else if(ostype.contains("Windows Server Longhorn".toUpperCase()))
				id = vm_guest_os_identifier_e.winLonghorn64Guest;
		}
		return id;
	}
	
	public ManualConversionUtility( NativeFacade nativeFacade )
	{
		this.nativeFacade = nativeFacade;
		this.vssManagerHandle = 0;
		this.snapshotSetId = null;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public void prepareForManualConversion( String guid, BackupDestinationInfo backupDestinationInfo ) throws
		SessionNotCompleteException,
		CreateVssManagerException,
		GetSessionVolumesException,
		CreateSnapshotSetException,
		SessionInSnapshotNotCompleteException
	{
		if (!isSessionComplete())
			throw new SessionNotCompleteException();
		
		long vssManagerHandle = getVssManager();
		List<String> volumeList = getBackupDestinationVolumes( guid, backupDestinationInfo );
		snapshotSetId = this.nativeFacade.vssManager_CreateSnapshotSet( vssManagerHandle, volumeList );
		if (snapshotSetId == null)
		{
			logger.error( "Error creating snapshot set." );
			throw new CreateSnapshotSetException();
		}
		
		if (!isSessionInSnapshotComplete())
			throw new SessionInSnapshotNotCompleteException();
		
		logger.info( "Preparing for manual conversion finished. (Guid: " + guid + ")" );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public String getSnapshotDeviceName( String originalVolumeName ) throws
		VssManagerNotCreatedException,
		SnapshotSetNotCreatedException
	{
		if (this.vssManagerHandle == 0)
			throw new VssManagerNotCreatedException();
		
		if (this.snapshotSetId == null)
			throw new SnapshotSetNotCreatedException();
		
		long snapshotSetHandle = this.nativeFacade.vssManager_GetSnapshotSetByGuid( vssManagerHandle, snapshotSetId );
		String snapshotDeviceName = this.nativeFacade.vcmSnapshotSet_QuerySnapshotDeviceName( snapshotSetHandle, originalVolumeName );
		this.nativeFacade.vcmSnapshotSet_Release( snapshotSetHandle );
		return snapshotDeviceName;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public String translateToSnapshotPath( String originalPath ) throws
		VssManagerNotCreatedException,
		SnapshotSetNotCreatedException
	{
		String originalVolumeName = originalPath.substring( 0, 2 );
		String snapshotVolume = getSnapshotDeviceName( originalVolumeName );
		String newPath = snapshotVolume + originalPath.substring( 2, originalPath.length() - 1 );
		return newPath;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public void cleanUpForManualConversion()
	{
		if (this.vssManagerHandle == 0)
			return;
		
		if (this.snapshotSetId != null)
			this.nativeFacade.vssManager_DeleteSnapshotSet( vssManagerHandle, snapshotSetId );
		
		this.nativeFacade.vssManager_Release( vssManagerHandle );
		
		this.vssManagerHandle = 0;
		this.snapshotSetId = null;
		
		logger.info( "Cleaning up for manual conversion finished." );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private List<String> getBackupDestinationVolumes( String guid, BackupDestinationInfo backupDestinationInfo ) throws
		GetSessionVolumesException
	{
		try
		{
			List<String> volumeList = new ArrayList<String>();
			
			String destPath = backupDestinationInfo.getBackupDestination();
			if (StringUtil.isEmptyOrNull( destPath ))
				throw new Exception();
			
			String volume = destPath.substring( 0, 2 );
			volumeList.add( volume );
			
			return volumeList;
		}
		catch (Exception e)
		{
			logger.error( "Error getting destination volumes.", e );
			throw new GetSessionVolumesException();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private long getVssManager() throws CreateVssManagerException
	{
		if (this.vssManagerHandle == 0)
		{
			long vssManagerHandle = this.nativeFacade.createVssManager();
			if (vssManagerHandle == 0)
				throw new CreateVssManagerException();
				
			if (this.nativeFacade.vssManager_Init( vssManagerHandle, 0, false ) != 0)
			{
				this.nativeFacade.vssManager_Release( vssManagerHandle );
				throw new CreateVssManagerException();
			}
			
			this.vssManagerHandle = vssManagerHandle;
		}
		
		return this.vssManagerHandle;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private boolean isSessionComplete()
	{
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private boolean isSessionInSnapshotComplete()
	{
		return true;
	}
	
	public static String getGuestOSID(BackupInfo backupInfo) {
		if (backupInfo == null || backupInfo.getServerInfo() == null) {
			logger.error("The backupInfo or serverInfo is empty.");
			return null;
		}
		BackupInfoServerInfo serverInfo = backupInfo.getServerInfo();
		String os = serverInfo.getOs();
		String cpu = serverInfo.getCup();
		logger.info("The guest os indentity is OS:" + os + " CPU:" + cpu);
		
		boolean is_x86 = !cpu.toUpperCase().contentEquals("AMD64");
		vm_guest_os_identifier_e id = convert_os_version_to_os_identifier(os.toUpperCase(), is_x86);
		
		if(id.ordinal() >= vm_guest_os_identifier_e_map.length) // The last one is unknown
		{
			logger.error("The OS type cannot match VMware OS type, the guest OS type convert to unknow.");
			return null;
		}
					
		String guestOSID = vm_guest_os_identifier_e_map[id.ordinal()];
			
		if (guestOSID == null) {
			logger.warn("Failed to get the guest os id for os:" + os + " cpu:" + cpu);
		} else if (logger.isInfoEnabled()) {
			logger.info("The guest os indentity is:" + guestOSID + " for OS:" + os + " CPU:" + cpu);
		}
		return guestOSID;
	}
	
	public static boolean getBootFirmwareUEFIFlag(BackupInfo backupInfo) {
		String bootFirmware = getBootFirmware(backupInfo);
		
		return bootFirmware.equalsIgnoreCase("UEFI");
	}

	private static String getBootFirmware(BackupInfo backupInfo) {
		if (backupInfo == null || backupInfo.getServerInfo() == null) {
			logger.error("The backupInfo or serverInfo is empty.");
			return MSPManualConversionConstants.DEFAULT_BOOT_FIRMWARE;
		}
		String bootFirmware = backupInfo.getServerInfo().getBootFirmware();
		if (bootFirmware == null || bootFirmware.trim().length() == 0) {
			return MSPManualConversionConstants.DEFAULT_BOOT_FIRMWARE;
		}
		return bootFirmware;
	}
	
	public static boolean isVSBWithoutHASupport(JobScript jobScript) {
		if (jobScript == null) {
			return false;
		}
		return jobScript.getGenerateType() == GenerateType.MSPManualConversion || jobScript.getGenerateType() == GenerateType.NoHASupport;
	}
	
	public static boolean isVSBWithoutHASupport(VMBackupConfiguration conf) {
		if (conf == null) {
			return false;
		}
		return conf.getGenerateType() == GenerateType.MSPManualConversion || conf.getGenerateType() == GenerateType.NoHASupport;
	}
	
	public static boolean isVSBOnMSP(JobScript jobScript) {
		if (jobScript == null) {
			return false;
		}
		return jobScript.getGenerateType() == GenerateType.MSPManualConversion;
	}
	
	public static boolean isCrossSite(JobScript jobScript) {
		if (jobScript == null) {
			return false;
		}
		return jobScript.getGenerateType() == GenerateType.NoHASupport;
	}
	
	public static String getSessionFolderPathOfNode( String uuid )
	{
		BackupConfiguration bkConfig = null;
		
		try
		{
			bkConfig = HACommon.getBackupConfigurationViaAFGuid( uuid );
		}
		catch (Exception e)
		{
			logger.error( "Error getting backup configuration. UUID: " + uuid, e );
		}
		
		if (bkConfig == null)
			return null;
		
		return bkConfig.getDestination();
	}
	
	public static String getRHAScenarioRootPathFromSessionFolderPath( String sessionFolderPath )
	{
//		String pathSeperator = "\\";
//		int lastSlash = sessionFolderPath.lastIndexOf( pathSeperator );
//		if (lastSlash == sessionFolderPath.length() - 1)
//			lastSlash = sessionFolderPath.lastIndexOf( pathSeperator, lastSlash );
//		String rootPath = sessionFolderPath.substring( 0, lastSlash );
//		return rootPath;
		
		return sessionFolderPath;
	}
	
	public RHAScenarioState getRHAScenarioStateByNode( String uuid )
	{
		try
		{
			String sessionFolderPath = getSessionFolderPathOfNode( uuid );
			if (sessionFolderPath == null)
			{
				logger.error( "Error getting session folder path. UUID: " + uuid );
				return RHAScenarioState.Unknown;
			}
			
			String scenarioRootPath = getRHAScenarioRootPathFromSessionFolderPath( sessionFolderPath );
			return this.nativeFacade.getRHAScenarioState( scenarioRootPath );
		}
		catch (Exception e)
		{
			logger.error( "Error getting RHA scenario state. UUID: " + uuid, e );
			return RHAScenarioState.Unknown;
		}
	}
}
