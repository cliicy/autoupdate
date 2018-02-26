package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.srm.SrmAshostInfo;

public interface IEdgeSrmDao {
	
	
	@StoredProcedure( name="as_edge_backup_size_merge_byday" )
	int backupSizeMergeForTimeZoneChange( int timeOffSet );
	/**
	 * as_edge_backup_size_of_arcserve_collect 
	 */
	@StoredProcedure(name="as_edge_backup_size_of_arcserve_collect")
	int spsrmedgeCollectARCserveBackupSize( int timeOffSet );
	/**
	 * as_edge_backup_size_of_d2d_collect 
	 */
	@StoredProcedure(name="as_edge_backup_size_of_d2d_collect")
	int spsrmedgeCollectD2DBackupSize( int timeOffSet );
	/**
	 * as_edge_srm_archiveBackupSizeTrendD2W
	 */
	@StoredProcedure(name="as_edge_srm_archiveBackupSizeTrendD2W")
	int spsrmedgearchiveBackupSizeTrendD2W(int keepDay, Date curDateTime);
	/**
	 * as_edge_srm_archiveBackupSizeTrendW2M
	 */
	@StoredProcedure(name="as_edge_srm_archiveBackupSizeTrendW2M")
	int spsrmedgearchiveBackupSizeTrendW2M(int keepMonths, Date curDateTime);
	/**
	 * as_edge_srm_cleanupBackupSizeTrending
	 */
	@StoredProcedure(name="as_edge_srm_cleanupBackupSizeTrending")
	int spsrmedgecleanupBackupSizeTrending(int keepYear, Date curDateTime);
	/**
	 * as_edge_srm_getRhostID
	 */
	@StoredProcedure(name="as_edge_srm_getRhostID")
	int spsrmedgegetRhostID(String rHostName, @ResultSet List<SrmAshostInfo> rhostIDs);
	
	/**
	 * as_edge_srm_updateNICInsert
	 */
	@StoredProcedure(name="as_edge_srm_updateNICInsert")
	int spsrmedgeupdateNICInsert(String AdapterType, String MACAddress, String Manufacturer, String Name, long Speed, int Mtu, String RawData, int nodeID);
	/**
	 * as_edge_srm_updatePartitionBegin
	 */
	@StoredProcedure(name="as_edge_srm_updatePartitionBegin")
	int spsrmedgeupdatePartitionBegin();
	/**
	 * as_edge_srm_updatePartitionEnd
	 */
	@StoredProcedure(name="as_edge_srm_updatePartitionEnd")
	int spsrmedgeupdatePartitionEnd(int nodeID);
	/**
	 * as_edge_srm_updateVolumeBegin
	 */
	@StoredProcedure(name="as_edge_srm_updateVolumeBegin")
	int spsrmedgeupdateVolumeBegin();
	/**
	 * as_edge_srm_updateVolumeEnd
	 */
	@StoredProcedure(name="as_edge_srm_updateVolumeEnd")
	int spsrmedgeupdateVolumeEnd(int nodeID);
	/**
	 * as_edge_srm_updateVolumeInsert
	 */
	@StoredProcedure(name="as_edge_srm_updateVolumeInsert")
	int spsrmedgeupdateVolumeInsert(long BlockSize, String Caption, String Name, long Capacity, boolean Compressed, String DriveLetter, int DriveType, String FileSystem, long FreeSpace, int FreeSpacePercent, String DeviceID, int VolumeType, String RawData, int nodeID);
	/**
	 * as_edge_srm_updateFibercardBegin
	 */
	@StoredProcedure(name="as_edge_srm_updateFibercardBegin")
	int spsrmedgeupdateFibercardBegin();
	/**
	 * as_edge_srm_updateFibercardEnd
	 */
	@StoredProcedure(name="as_edge_srm_updateFibercardEnd")
	int spsrmedgeupdateFibercardEnd(int nodeID);
	/**
	 * as_edge_srm_updateFibercardInsert
	 */
	@StoredProcedure(name="as_edge_srm_updateFibercardInsert")
	int spsrmedgeupdateFibercardInsert(int Availability, String Caption, String Description, String DriverName, String Name, String Manufacturer, int ProtocolSupported, String HardwareVersion, String DriverVersion, int MaxDataWidth, int MaxNumberControlled, long MaxTransferRate, String RawData, int nodeID);
	/**
	 * as_edge_srm_updateCPUBegin
	 */
	@StoredProcedure(name="as_edge_srm_updateCPUBegin")
	int spsrmedgeupdateCPUBegin();
	/**
	 * as_edge_srm_updateCPUEnd
	 */
	@StoredProcedure(name="as_edge_srm_updateCPUEnd")
	int spsrmedgeupdateCPUEnd(int nodeID);
	/**
	 * as_edge_srm_updateCPUInsert
	 */
	@StoredProcedure(name="as_edge_srm_updateCPUInsert")
	int spsrmedgeupdateCPUInsert(int AddressWidth, int Architecture, int Availability, int DataWidth, String DeviceID, int Family, int L2CacheSize, String Manufacturer, int MaxClockSpeed, String Name, String ProcessorId, int ProcessorType, String RawData, int nodeID);
	/**
	 * as_edge_srm_updateDiskBegin
	 */
	@StoredProcedure(name="as_edge_srm_updateDiskBegin")
	int spsrmedgeupdateDiskBegin();
	/**
	 * as_edge_srm_updateDiskEnd
	 */
	@StoredProcedure(name="as_edge_srm_updateDiskEnd")
	int spsrmedgeupdateDiskEnd(int nodeID);
	/**
	 * as_edge_srm_updateDiskInsert
	 */
	@StoredProcedure(name="as_edge_srm_updateDiskInsert")
	int spsrmedgeupdateDiskInsert(String Caption, String Description, String DeviceID, long DiskUsedSpace, int DiskThroughput, String InterfaceType, String Manufacturer, String MediaType, String Model, String Name, short Partitions, long Size, long TotalCylinders, long TotalHeads, long TotalSectors, long TotalTracks, long TracksPerCylinder, int SCSIBus, int SCSILogicalUnit, int SCSIPort, int SCSITargetId, String Signature, int Index, int DiskType, String RawData, int nodeID);
	/**
	 * as_edge_srm_updateLogicalDiskBegin
	 */
	@StoredProcedure(name="as_edge_srm_updateLogicalDiskBegin")
	int spsrmedgeupdateLogicalDiskBegin();
	/**
	 * as_edge_srm_updateLogicalDiskEnd
	 */
	@StoredProcedure(name="as_edge_srm_updateLogicalDiskEnd")
	int spsrmedgeupdateLogicalDiskEnd(int nodeID);
	/**
	 * as_edge_srm_updateMapBegin
	 */
	@StoredProcedure(name="as_edge_srm_updateMapBegin")
	int spsrmedgeupdateMapBegin();
	/**
	 * as_edge_srm_updateMapEnd
	 */
	@StoredProcedure(name="as_edge_srm_updateMapEnd")
	int spsrmedgeupdateMapEnd(int nodeID);
	/**
	 * as_edge_srm_updateMemoryBegin
	 */
	@StoredProcedure(name="as_edge_srm_updateMemoryBegin")
	int spsrmedgeupdateMemoryBegin();
	/**
	 * as_edge_srm_updateMemoryEnd
	 */
	@StoredProcedure(name="as_edge_srm_updateMemoryEnd")
	int spsrmedgeupdateMemoryEnd(int nodeID);
	/**
	 * as_edge_srm_updateMemoryInsert
	 */
	@StoredProcedure(name="as_edge_srm_updateMemoryInsert")
	int spsrmedgeupdateMemoryInsert(long Capacity, int DataWidth, String DeviceLocator, int FormFactor, String Manufacturer, int MemoryType, int Speed, String Tag, int TotalWidth, int TypeDetail, String Name, String RawData, int nodeID);
	/**
	 * as_edge_srm_updateNICBegin
	 */
	@StoredProcedure(name="as_edge_srm_updateNICBegin")
	int spsrmedgeupdateNICBegin();
	/**
	 * as_edge_srm_updateNICEnd
	 */
	@StoredProcedure(name="as_edge_srm_updateNICEnd")
	int spsrmedgeupdateNICEnd(int nodeID);
	/**
	 * as_edge_srm_collectVolDailyData
	 */
	@StoredProcedure(name="as_edge_srm_collectVolDailyData")
	int spsrmedgecollectVolDailyData(int NodeId, Date curDateTime);
	/**
	 * as_edge_srm_archiveVolTrendD2W
	 */
	@StoredProcedure(name="as_edge_srm_archiveVolTrendD2W")
	int spsrmedgearchiveVolTrendD2W(int keepDay, Date curDateTime);
	/**
	 * as_edge_srm_archiveVolTrendW2M
	 */
	@StoredProcedure(name="as_edge_srm_archiveVolTrendW2M")
	int spsrmedgearchiveVolTrendW2M(int keepMonths, Date curDateTime);
	/**
	 * as_edge_srm_cleanupVolTrending
	 */
	@StoredProcedure(name="as_edge_srm_cleanupVolTrending")
	int spsrmedgecleanupVolTrending(int keepYear, Date curDateTime);
	/**
	 * as_edge_srm_archivePKITrending
	 */
	@StoredProcedure(name="as_edge_srm_archivePKITrending")
	int spsrmedgearchivePKITrending(int keepDays, Date curDateTime);
	/**
	 * as_edge_srm_addSoftwareInfo
	 */
	@StoredProcedure(name="as_edge_srm_addSoftwareInfo")
	int spsrmedgeaddSoftwareInfo(@Out String[] idString, int nodeID, int categoryID, int majorVersion, int minorVersion, String displayName, String version, String installDate, int language, int spLvl, String edition);
	/**
	 * as_edge_srm_cleanupSoftwareInfo
	 */
	@StoredProcedure(name="as_edge_srm_cleanupSoftwareInfo")
	int spsrmedgecleanupSoftwareInfo(int years);
	/**
	 * as_edge_srm_addSoftwarePatchInfo
	 */
	@StoredProcedure(name="as_edge_srm_addSoftwarePatchInfo")
	int spsrmedgeaddSoftwarePatchInfo(String idArray, String name, String version, String installDate);
	/**
	 * as_edge_srm_archiveAppTrendD2W
	 */
	@StoredProcedure(name="as_edge_srm_archiveAppTrendD2W")
	int spsrmedgearchiveAppTrendD2W(int keepDay, Date curDateTime);
	/**
	 * as_edge_srm_archiveAppTrendW2M
	 */
	@StoredProcedure(name="as_edge_srm_archiveAppTrendW2M")
	int spsrmedgearchiveAppTrendW2M(int keepMonths, Date curDateTime);
	/**
	 * as_edge_srm_cleanupAppTrending
	 */
	@StoredProcedure(name="as_edge_srm_cleanupAppTrending")
	int spsrmedgecleanupAppTrending(int keepYear, Date curDateTime);
	/**
	 * as_edge_srm_updateLogicalDiskUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updateLogicalDiskUpdate")
	int spsrmedgeupdateLogicalDiskUpdate(int nodeID);
	/**
	 * as_edge_srm_updatenode
	 */
	@StoredProcedure(name="as_edge_srm_updatenode")
	int spsrmedgeupdatenode(String Name, int Virtualization, int RhostID, @Out int[] NodeID);
	/**
	 * as_edge_srm_cleanupNode2Host
	 */
	@StoredProcedure(name="as_edge_srm_cleanupNode2Host")
	int spsrmedgecleanupNode2Host();
	/**
	 * as_edge_srm_copyNode2HostFromTempTable
	 */
	@StoredProcedure(name="as_edge_srm_copyNode2HostFromTempTable")
	int spsrmedgecopyNode2HostFromTempTable();
	/**
	 * as_edge_srm_updateFragmentEvent
	 */
	@StoredProcedure(name="as_edge_srm_updateFragmentEvent")
	int spsrmedgeupdateFragmentEvent(long AverageFileSize, double AverageFragmentsPerFile, long ClusterSize, long ExcessFolderFragments, int FilePercentFragmentation, long FragmentedFolders, long FreeSpace, int FreeSpacePercent, int FreeSpacePercentFragmentation, int MFTPercentInUse, long MFTRecordCount, long PageFileSize, long TotalExcessFragments, long TotalFiles, long TotalFolders, long TotalFragmentedFiles, long TotalMFTFragments, long TotalMFTSize, long TotalPageFileFragments, int TotalPercentFragmentation, long UsedSpace, long VolumeSize, String VolumeDeviceID, int nodeID);
	/**
	 * as_edge_srm_GetLastDateTime
	 */
	@StoredProcedure(name="as_edge_srm_GetLastDateTime")
	int spsrmedgeGetLastDateTime(int nodeID);
	/**
	 * as_edge_srm_updateevent
	 */
	@StoredProcedure(name="as_edge_srm_updateevent")
	int spsrmedgeupdateevent(String Action, String Source, String Target, int nodeID);
	/**
	 * as_edge_srm_updateMapInsert
	 */
	@StoredProcedure(name="as_edge_srm_updateMapInsert")
	int spsrmedgeupdateMapInsert(int nodeID, String VolumeDeviceID, int diskIndex);
	/**
	 * as_edge_srm_updatePartitionInsert
	 */
	@StoredProcedure(name="as_edge_srm_updatePartitionInsert")
	int spsrmedgeupdatePartitionInsert(long BlockSize, boolean BootPartition, String Name, long NumberOfBlocks, boolean PrimaryPartition, long Size, String Type, String RawData, int nodeID, int diskIndex);
	/**
	 * as_edge_srm_updateMapUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updateMapUpdate")
	int spsrmedgeupdateMapUpdate(int nodeID);
	/**
	 * as_edge_srm_updateLogicalDiskInsert
	 */
	@StoredProcedure(name="as_edge_srm_updateLogicalDiskInsert")
	int spsrmedgeupdateLogicalDiskInsert(String Caption, boolean Compressed, String Description, String DeviceID, int DriveType, String FileSystem, long FreeSpace, int MaximumComponentLength, int MediaType, String Name, boolean QuotasDisabled, boolean QuotasIncomplete, boolean QuotasRebuilding, long Size, boolean SupportsDiskQuotas, boolean SupportsFileBasedCompression, String VolumeName, String RawData, String PartitionName, String VolumeDeviceID, int nodeID);
	/**
	 * as_edge_srm_updateComponentLicenseInfo
	 */
	@StoredProcedure(name="as_edge_srm_updateComponentLicenseInfo")
	int spsrmedgeupdateComponentLicenseInfo(int componentId, int majorVersion, int minorVersion, int totalLicenses, int usedLicenses, int topologyId, int unusedLicenses, int minLicensesNeed);
	/**
	 * as_edge_srm_cleanupComponentLicenseInfo
	 */
	@StoredProcedure(name="as_edge_srm_cleanupComponentLicenseInfo")
	int spsrmedgecleanupComponentLicenseInfo();
	/**
	 * as_edge_srm_updatePhyDskTrending
	 */
	@StoredProcedure(name="as_edge_srm_updatePhyDskTrending")
	int spsrmedgeupdatePhyDskTrending(String nodeName, int diskIndex, long throughput, Date curDateTime);
	/**
	 * as_edge_srm_updateNicTrending
	 */
	@StoredProcedure(name="as_edge_srm_updateNicTrending")
	int spsrmedgeupdateNicTrending(String nodeName, int usage, long linkSpeed, String macAddr, Date curDateTime);
	/**
	 * as_edge_srm_updateMemTrending
	 */
	@StoredProcedure(name="as_edge_srm_updateMemTrending")
	int spsrmedgeupdateMemTrending(String nodeName, int usage, int capacity, int PFusage, int PFcapacity, Date curDateTime);
	/**
	 * as_edge_srm_updateCPUTrending
	 */
	@StoredProcedure(name="as_edge_srm_updateCPUTrending")
	int spsrmedgeupdateCPUTrending(String nodeName, int usage, String coreIndex, Date curDateTime);
	/**
	 * as_edge_srm_updateCPUUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updateCPUUpdate")
	int spsrmedgeupdateCPUUpdate(int nodeID);
	/**
	 * as_edge_srm_updateIP
	 */
	@StoredProcedure(name="as_edge_srm_updateIP")
	int spsrmedgeupdateIP(int NodeID, String MACAddress, String IP, String Subnet, String SubnetMask, String DefGateway, String DNSDomain, String DNSHostName, String DHCPServer, int PrefixLen, String RawData);
	/**
	 * as_edge_srm_updatePartitionUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updatePartitionUpdate")
	int spsrmedgeupdatePartitionUpdate(int nodeID);
	/**
	 * as_edge_srm_updateos
	 */
	@StoredProcedure(name="as_edge_srm_updateos")
	int spsrmedgeupdateos(int BuildNumber, String BuildType, String Caption, String CountryCode, String CSDVersion, String CSName, String Manufacturer, String OEMInfo, int OSLanguage, int ProductType, String SerialNumber, int ServicePackMajorVersion, int ServicePackMinorVersion, String SystemDevice, String SystemDirectory, String SystemDrive, String Version, String WindowsDirectory, String RawData, int nodeID);
	/**
	 * as_edge_srm_updateDiskUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updateDiskUpdate")
	int spsrmedgeupdateDiskUpdate(int nodeID);
	/**
	 * as_edge_srm_updateFibercardUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updateFibercardUpdate")
	int spsrmedgeupdateFibercardUpdate(int nodeID);
	/**
	 * as_edge_srm_updateVolumeUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updateVolumeUpdate")
	int spsrmedgeupdateVolumeUpdate(int nodeID);
	/**
	 * as_edge_srm_updateNICUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updateNICUpdate")
	int spsrmedgeupdateNICUpdate(int nodeID);
	/**
	 * as_edge_srm_updateMemoryUpdate
	 */
	@StoredProcedure(name="as_edge_srm_updateMemoryUpdate")
	int spsrmedgeupdateMemoryUpdate(int nodeID);
	/**
	 * as_edge_srm_addAppDailyData
	 */
	@StoredProcedure(name="as_edge_srm_addAppDailyData")
	int spsrmedgeaddAppDailyData(int nodeID, int categoryID, int majorVersion, int minorVersion, String instanceName, long size, Date curDateTime);
	/**
	 * as_edge_alert_message_insert
	 */
	@StoredProcedure(name="as_edge_alert_message_insert")
	int spedgeAlertMessageInsert(
			 @In(jdbcType = Types.VARCHAR) String send_host,
			 @In(jdbcType = Types.VARCHAR) String protectedNode,
			 long raw_event_type, long job_type, int over_all_event_type, int job_status,
			 @In(jdbcType = Types.VARCHAR) String alert_subject, 
			 @In(jdbcType = Types.VARCHAR)  String alert_message, 
			 @In(jdbcType = Types.TIMESTAMP) Date send_time, long product_type, @Out int[] alert_id);
	/**
	 * as_edge_alert_message_delete
	 */
	@StoredProcedure(name="as_edge_alert_message_delete")
	int spsrmedgeAlertMessageDelete(String condition);
}
