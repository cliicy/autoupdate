/**
 * The header file declares all the switches. There are three pages: developer use only, support use only, open for customer.
 * In every page, they are grouped by module name.
 * @author xuvji01
 */

#pragma once

#define SWT_DEFAULT_CONFIG_INI_FILE_NAME						L"Configuration\\Switch.ini"	///<The relative path of config ini file relative to "$D2D_InstallPath"
#define SWT_AFSTOR_CONFIG_INI_FILE_NAME							L"Configuration\\AFStor.ini"	///<The relative path of config ini file relative to "$D2D_InstallPath"

/** @addtogroup Support Support
 *	These switches are used by support.
 * @{
 */
/** @} */

/** @addtogroup Developer Developer
 *	These switches are used by Developer.
 * @{
 */
/** @} */

/** @addtogroup Customer Customer
 *	These switches are used by customer.
 * @{
 */
/** @} */


/** @addtogroup Support Support
 * @{
 */

/** @name MergeMgr
 *-------------------------------------------------------------------------
 * These switches are defined for module "MergeMgrDll.dll" in ini file
 * ------------------------------------------------------------------------
 * Location: "$D2D_InstallPath\Configuration\Switch.ini"
 *
 * Description / Format: 
 *
 *           [MergeMgrDll.DLL]
 *           Common.KeepJS = 1
 *           Common.StartJobWaitSec=0
 *           .....
 */
///@{
#define SWT_MERGEMGRDLL_MODULENAME								L"MergeMgrDll.DLL"				///<The section name for module "MergeMgrDll.dll"
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_NEWCONSOLE				L"Common.NewConsole"			///<If the value is non-0, run merge process in a new console.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_SHOWWND					L"Common.ShowWnd"				///<If the value is non-0, Show merge process window.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_MANUALSTART				L"Common.ManualStart"			///<If the value is non-0, Start merge process manually.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_SCHEDULEJOB				L"Common.ScheduleJob"			///<If the value is non-0, If run next scheduled job after merge process finish.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_PLAINSCRIPT				L"Common.PlainScript"			///<If the value is non-0, job script has been encrypted.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_STARTJOBWAITSEC			L"Common.StartJobWaitSec"		///<Waiting time for starting merge job. (In Seconds)
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_MIDJOBWAITSEC			L"Common.MidJobWaitSec"			///<Waiting time for continuing merge job. (In Seconds)
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_ENDJOBWAITSEC			L"Common.EndJobWaitSec"			///<Waiting time for ending merge job. (In Seconds)
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_ERRORWAITSEC				L"Common.ErrorWaitSec"			///<Waiting time for keeping ENV when some fatal error occurs
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_MERGEDATAWAIT_MS			L"Common.MergeWaitMS"			///<Waiting time for next merge date operation in merge job.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_LOCKRETRYTIMES			L"Common.LockRetryTimes"		///<Retry times when session lock failed.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_LOCKRETRYINTERVAL		L"Common.LockRetryInterval"		///<Time interval between 2 lock retry operation.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_INITBKERRCODE			L"Common.InitBKErrCode"			///<Error code of initialize backup destination for debug.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_MERGEJOBCNT				L"Common.MergeJobCnt"			///<How many merge job can run in parallel.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_MERGEZZ					L"Common.MergeZZ"				///<If the value is non-0, merge job use logic without check point logic.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_DBGPROGRESS				L"Common.DbgProgress"			///<How merge progress shown. Refer to E_PROGRESS_SHOWN;
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_DUMPJOBMON				L"Common.DumpJobMon"			///<Dump job monitor content summary or detail.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_CHKSPACE4MERGE			L"Common.ChkSpace4Merge"		///<If the value is non-0, block merge job when there is no enough free space on destination.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_KEEPJS					L"Common.KeepJS"				///<If the value is non-0, keep job script after merge job.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_DUMPJS_PATH				L"Common.DumpJSPath"		    ///<If the value is non-empty, dump job script used for merge after merge job.
#define SWT_MERGEMGRDLL_KEYNAME_DATA_STORE_2_REPLICATE			L"Common.DS2Replicate"		    ///<If the value is non-empty, we will only merge session has been replicated.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_IGNOREMERGEPLANCHANGE	L"Common.IgnoreMergePlanChange"	///<If the value is non-0, merge with new merge plan or just continue failed merge. 
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_DOIMPERSONATE			L"Common.DoImpersonate"			///<If the value is non-0, this job need do impersonation before launch job.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_JOBID					L"Common.JobID"					///<Job ID assigned to merge job, which is also used to compose share memory name.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_SESSTYPE					L"Common.SessType"				///<Session type to distinguish local D2D session from vsphere session. Refer to E_SESS_TYPE.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_MERGEOPT					L"Common.MergeOpt"				///<Merge option. Refer to E_MERGE_OPT
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_MERGEMETHOD				L"Common.MergeMethod"			///<Which merge logic will be used to merge. Refer to E_MERGE_METHOD.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_RETENTIONCNT				L"Common.RetentionCnt"			///<Retention count. Depend on dwOperateType, this value may indicate session count or backup set count.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_DAILYBACKUPCNT	    	L"Common.DailyCnt"			    ///<Daily count. Valid when variable retention is enabled.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_WEEKLYBACKUPCNT		    L"Common.WeeklyCnt"			    ///<Weekly count. Valid when variable retention is enabled.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_MONTHLYBACKUPCNT  		L"Common.MonthlyCnt"			///<Monthly count. Valid when variable retention is enabled.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_STARTSESS				L"Common.StartSess"				///<Start session number to be merged.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_ENDSESS					L"Common.EndSess"				///<End session number to be merged.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_CRYPTOINFO				L"Common.CryptoInfo"			///<Encryption information. High-16 bit indicate encryption crypto library, which low-16 bit indicate algorithm.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_COMPRESSINFO				L"Common.CompressInfo"			///<Compression information.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_VMGUID					L"Common.VMGUID"				///<For vsphere session we need vm GUID to identity vm host.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_SESSPWD					L"Common.SessPWD"				///<Session password if encrypted.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_BKDEST					L"Common.BKDest"				///<Backup destination information. If it is remote shared folder we need user name and password.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_BKUSR					L"Common.BKUsr"					///<User name of remote backup destination.
#define SWT_MERGEMGRDLL_KEYNAME_COMMON_BKPWD					L"Common.BKPwd"					///<Password of remote backup destination.

///@}
/** @} */


/** @addtogroup Support Support
 * @{
 */

/** @name CatalogMgr
 *-------------------------------------------------------------------------
 * These switches are defined for module "CatalogMgrDll.dll" in ini file
 * ------------------------------------------------------------------------
 * Location: "$D2D_InstallPath\Configuration\Switch.ini"
 *
 * Description / Format: 
 *
 *           [CatalogMgrDll.DLL]
 *           Common.NewConsole = 1
 *           Common.StartJobWaitSec=0
 *           .....
 */
///@{
#define SWT_CATALOGMGRDLL_MODULENAME							L"CatalogMgrDll.DLL"			///<The section name for module "CatalogMgrDll.dll"
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_NEWCONSOLE				L"Common.NewConsole"			///<If the value is non-0, run catalog process in a new console.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_SHOWWND				L"Common.ShowWnd"				///<If the value is non-0, Show catalog process window.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_MANUALSTART			L"Common.ManualStart"			///<If the value is non-0, Start catalog process manually. 
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_SCHEDULEJOB			L"Common.ScheduleJob"			///<If the value is non-0, run next scheduled job after catalog process finish. 
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_STARTBYBACKUP			L"Common.StartByBackup"			///<If the value is non-0, start catalog job after backup finish.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_PLAINSCRIPT			L"Common.PlainScript"			///<If the value is non-0, job script has been encrypted.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_SKIPARCHIVE			L"Common.SkipArchive"			///<If the value is non-0, skip archive job in catalog process.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_KEEPTMPFOLDER			L"Common.KeepTmpFolder"			///<If the value is non-0, delete temp folder for vmimage. 
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_BLOCK2ONLY				L"Common.Block2Only"			///<If the value is non-0, only debug for updating block 2.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_IMPERSONATE			L"Common.Impersonate"			///<If the value is non-0, catalog process need be impersonated. 
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_ISJSKEPT		    	L"Common.IsJSKept"			    ///<If the value is non-0, job script will be kept after job is launched. 
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_READERWAITSEC			L"Common.ReaderWaitSec"			///<Waiting time for debugger to attach. (In Seconds)
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_WRITERWAITSEC			L"Common.WriterWaitSec"			///<Waiting time for debugger to attach. (In Seconds)
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_STARTJOBWAITSEC		L"Common.StartJobWaitSec"		///<Waiting time for starting catalog job. (In Seconds)
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_ERRORWAITSEC			L"Common.ErrorWaitSec"			///<Waiting time for keeping ENV when some fatal error occurs
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_LOCKRETRYTIMES			L"Common.LockRetryTimes"		///<Retry times when session lock failed.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_LOCKRETRYINTERVAL		L"Common.LockRetryInterval"		///<Time interval between 2 lock retry operation.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_B2RETRYTIMES			L"Common.B2RetryTimes"			///<Retry time when failed to update block 2.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_B2RETRYINTERVAL		L"Common.B2RetryInterval"		///<Retry interval when failed to update block 2.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_PARSEERRCODE			L"Common.ParseErrCode"			///<Error code of parsing catalog for debug, both for FS and GRT.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_INITBKERRCODE			L"Common.InitBKErrCode"			///<Error code of initialize backup destination for debug.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_MAXJOBRUNTIMES			L"Common.MaxJobRunTimes"		///<Max retry times for catalog job.
#define SWT_CATALOGMGRDLL_KEYNAME_COMMON_TMPPATH4CATALOG		L"Common.TmpPath4Catalog"		///<Temp folder for catalog file.
#define SWT_CATALOGMGRDLL_KEYNAME_CMDLINE_EXITCONFIRM			L"CmdLine.ExitConfirm"			///<If the value is non-0, the process exits with confirm by command line.
#define SWT_CATALOGMGRDLL_KEYNAME_CMDLINE_JOBID					L"CmdLine.JobID"				///<Job ID for the catalog process. If this variable is zero, the job ID is set by web service.
#define SWT_CATALOGMGRDLL_KEYNAME_CMDLINE_JOBQTYPE				L"CmdLine.JobQType"				///<Job queue type, 1 = regular queue; 2 = ondemand queue. 	
#define SWT_CATALOGMGRDLL_KEYNAME_CMDLINE_CATSCRIPT				L"CmdLine.CatScript"			///<Job script path.
#define SWT_CATALOGMGRDLL_KEYNAME_CMDLINE_IMPERSONATE			L"CmdLine.Impersonate"			///<If the value is non-0, pass 'Impersonate' switch to process by command line.

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name AFStor
 *-------------------------------------------------------------------------
 * These switches are defined for module "AFStor.dll" in ini file
 * ------------------------------------------------------------------------
 * Location: "$D2D_InstallPath\Configuration\AFStor.ini"
 *
 * Description / Format: 
 *
 *           [Configuration]
 *           BufferingForLocalDisk = 1
 *           .....
 */
///@{
#define SWT_AFSTOR_MODULENAME_CONFIGURATION							L"Configuration"				///<The section name for Configuration in AFStor.ini
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_BUFFERINGFORLOCALDISK		L"BufferingForLocalDisk"		///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_BUFFERINGFORUSB			L"BufferingForUSB"				///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_BUFFERINGFORREMOTEFOLDER	L"BufferingForRemoteFolder"		///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_CACHESIZE					L"CacheSizeKB"					///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_PREALLOCSIZE				L"PreAllocate%"					///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_WRITETHROUGHFLAGUSB		L"WriteThroughFlagUSB"			///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_WRITETHROUGHFLAGREMOTE		L"WriteThroughFlagRemote"		///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_WRITETHROUGHFLAGLOCAL		L"WriteThroughFlagLocal"		///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_BUFFERINGFORMERGEREAD		L"BufferingForMergeRead"		///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_BUFFERINGFORSEQUENTIALREAD	L"BufferingForSequentialRead"	///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_MEMALLOCMETHOD				L"MemAllocMethod"				///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_MINIMALFREESPACE			L"MinimalFreeSpace"				///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_MERGEFLUSHPERCENT			L"MergeFlushPercent"			///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_USEHOLE					L"UseHole"						///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_MAXMERGEDSESSIONCOUNT		L"MaxMergedSessionCount"		///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_COMPRESSINGLEVEL			L"CompressingLevel"				///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_COMPRESSINGTHREADSCOUNT	L"CompressingThreadsCount"		///<

#define SWT_AFSTOR_KEYNAME_CONFIGURATION_SLICESIZEINMB				L"SliceSizeInMB"				///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_SLICEMAXDEPENDSESSIONCOUNT	L"SliceMaxDependSessionCount"	///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_ENABLESPARSE				L"EnableSparse"					///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_DISABLEWRITE				L"DisableWrite"					///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_DISABLEPREREAD				L"DisablePreRead"				///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_SYNCREAD					L"SyncRead"						///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_DISABLERESERVESPACE4MERGE	L"DisableReservingSpaceForMerge"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_SLICEMERGETHRESHOLD		L"SliceMergeThresholdInPercentage"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_DISABLERESERVINGSPACE4BACK	L"DisableReservingSpaceForBackup"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_RESERVINGSZ4INDEXINMB		L"ReservingSizeForIndexFileInMB"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_ENABLEDEDUPEDISKLOCK 	    L"EnableDedupeDiskLock"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_RECLAIMREBUILDTHRESHOLD	L"ReclaimRebuildThresholdPercentage"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_RECLAIMALLWITHNEWTHRESHOLD L"ReclaimAllWithNewThresholdPercentage"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_RUNPURGERECLAIMALWAYS		L"RunPurgeReclaimAlways"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_INDEXROLEUPDATEHASHTIMEOUT	L"PurgeTimeoutForHashUpdating"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_INDEXROLEPURGEALERTTIMEUNIT	L"PurgeSuspendedTimeToTriggerWarningInDay"///<
#define SWT_AFSTOR_KEYNAME_CONFIGURATION_INDEXROLE_ACTION_FOR_SUSPEND_PURGE L"ActionForLongPurgeSuspendedTime"


#define SWT_AFSTOR_MODULENAME_MULTIPLETHREADING						L"MultipleThreading"			///<The section name for MultipleThreading in AFStor.ini
#define SWT_AFSTOR_KEYNAME_MULTIPLETHREADING_DISABLE				L"Disable"						///<
#define SWT_AFSTOR_KEYNAME_MULTIPLETHREADING_QUEUESIZE				L"QueueSizeMB"					///<
#define SWT_AFSTOR_KEYNAME_DISABLE_OPTIMIZE_GDD_REPLICATION			L"DontOptimizeGDDReplication"	///<
#define SWT_AFSTOR_KEYNAME_SKIP_NIC_CHECK							L"SkipNICCheck"					///<
#define SWT_AFSTOR_KEYNAME_VERIFY_CHECKSUM					        L"VerifyChecksum"		        ///<
#define SWT_AFSTOR_KEYNAME_DEDUPE_ASYNC_READ_BUF_SIZE_MB		    L"DedupeAsyncReadBufSizeMB"	    ///<
#define SWT_AFSTOR_KEYNAME_CONVERSION_QUEUE_LEN         		    L"ConversionQueueLen"	        ///<
#define SWT_AFSTOR_KEYNAME_LRU_IN_MB								L"LRUBufSizeMB"					///<
///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name VssWrapper
 *-------------------------------------------------------------------------
 * These switches are defined for module "VssWrapperDll.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\VssWrap"
 *
 * Description / Format: 
 *
 *           Key:		VssWrap
 *           Value:          "FullSnapshot"=DWORD:00000001
 *           Value:			 "SnapshotCreatedFlag"=DWORD:00000000
 *           .....
 */
///@{
#define SWT_VSSWRAPPERDLL_MODULENAME							CST_REG_ROOT_L L"\\VssWrap"		///<
#define SWT_VSSWRAPPERDLL_KEYNAME_INCLUDEDWRITERS				L"IncludedWriters"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_EXCLUDEDWRITERS				L"ExcludedWriters"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_MUSTINCLUDEDWRITERS		    L"MustIncludedWriters"	        ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SHOWCONFIG					L"ShowConfig"					///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SKIPLICCHK					L"SkipLicChk"					///<
#define SWT_VSSWRAPPERDLL_KEYNAME_ADAPTSTORAGEAREA				L"AdaptStorageArea"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SAVEBCDWMCTRL					L"SaveBCDWMCtrl"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SNAPSHOTCREATEDFLAG			L"SnapshotCreatedFlag"			///<
#define SWT_VSSWRAPPERDLL_KEYNAME_ENUMNONAPPFILES				L"EnumNonAppFiles"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_FULLSNAPSHOT					L"FullSnapshot"					///<
#define SWT_VSSWRAPPERDLL_KEYNAME_PERSISTENTSNAPSHOT			L"PersistentSnapshot"			///<
#define SWT_VSSWRAPPERDLL_KEYNAME_VSSRETRYINTERVAL				L"VssRetryInterval"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_VSSMAXRETRYTIMES				L"VssMaxRetryTimes"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_VSSASYNCHRETRYINTERVAL		L"VssAsynchRetryInterval"		///<
#define SWT_VSSWRAPPERDLL_KEYNAME_VSSASYNCHMAXRETRYTIMES		L"VssAsynchMaxRetryTimes"		///<
#define SWT_VSSWRAPPERDLL_KEYNAME_VSSLOGLEVEL					L"VssLogLevel"					///<
#define SWT_VSSWRAPPERDLL_KEYNAME_VSSLOGSUPERCTRL				L"VssLogSuperCtrl"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SYSTEMSTATEOPTIMIZATION		L"SystemStateOptimization"		///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SQLRESTORESEQUENCE    		L"SQLRestoreSequence"		    ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_CFGSYSVOLDIFFAREA				L"CfgSysVolDiffArea"			///<
#define SWT_VSSWRAPPERDLL_KEYNAME_CFGSYSVOLDIFFAREA				L"CfgSysVolDiffArea"			///<
#define SWT_VSSWRAPPERDLL_KEYNAME_NOAUTORECOVERY                L"NoAutoRecovery"               ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_TRANSPORTABLESNAP             L"TransportableSnap"            ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_NOTTRANSACTED                 L"NotTransacted"                ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SNAPDIFFERENTIAL              L"SnapDifferential"             ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SNAPBYPLEXORMIRROREDSPLIT     L"SnapMirroredSplit"            ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_ROLLBACKRECOVERY              L"RollbackRecovery"             ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_TXFRECOVERY                   L"TxFRecovery"                  ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SKIPUPDATEBCDWM               L"SkipUpdateBCDWM"              ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_RESTORE_EXCH_CUR_LOG          L"RestoreExchangeCurrentLog"    ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_DISABLE_ADDITIONAL_RESTORE    L"DisableAdditonalRestore"      ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_RUN_COMMAND_IN_THREAD         L"RunCommandInThread"           ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_MOVE_INSTEAD_OF_DELETE        L"MoveInsteadOfDelete"          ///<
#define SWT_VSSWRAPPERDLL_KEYNAME_VSSPROVIDERID					L"VSSProviderID"				///<
#define SWT_VSSWRAPPERDLL_KEYNAME_SNAPSHOT_TIMEOUT				L"SnapshotTimeout"				///<vss take snapshot timeout millisecond, if not exist or zero value means INFINITE timeout.
#define SWT_VSSWRAPPERDLL_KEYNAME_SNAPSHOT_INMAINTHRD			L"SnapshotInMainThread"			///<vss take snapshot in main thread flag, if not exist or zero value means create snapshot in new thread.
#define SWT_VSSWRAPPERDLL_KEYNAME_SNAPSHOTAREA_WARNING			L"SnapshotAreaWarning"			///<none zero value will show snapshot area changed warning message.
///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name TempFolder
*-------------------------------------------------------------------------
* These switches are defined for configuring temp folder in register table
* ------------------------------------------------------------------------
* Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\TempFolder"
*
* Description / Format:
*
*           Key:		TempFolder
*           Value:          "FullSnapshot"=DWORD:00000001
*           Value:			 "SnapshotCreatedFlag"=DWORD:00000000
*           .....
*/
///@{
#define SWT_TEMPFOLDER_KEYNAME                                         CST_REG_ROOT_L L"\\TempFolder"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_BACKUP                       L"AF_JOBTYPE_BACKUP"                 ///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RESTORE					   L"AF_JOBTYPE_RESTORE"				///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_COPY						   L"AF_JOBTYPE_COPY"					///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_BACKUP_VMWARE				   L"AF_JOBTYPE_BACKUP_VMWARE"			///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_BACKUP_HYPERV				   L"AF_JOBTYPE_BACKUP_HYPERV"			///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RESTORE_VMWARE			   L"AF_JOBTYPE_RESTORE_VMWARE"			///<
//#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RESTORE_HYPERV			   L"AF_JOBTYPE_RESTORE_HYPERV"			///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_FILECOPY_BACKUP			   L"AF_JOBTYPE_FILECOPY_BACKUP"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_ARCHIVE					   L"AF_JOBTYPE_ARCHIVE"				///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_ARCHIVE_PURGE				   L"AF_JOBTYPE_ARCHIVE_PURGE"			///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_ARCHIVE_RESTORE			   L"AF_JOBTYPE_ARCHIVE_RESTORE"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_FS_CATALOG_GEN			   L"AF_JOBTYPE_FS_CATALOG_GEN"			///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_APP_CATALOG_GEN			   L"AF_JOBTYPE_APP_CATALOG_GEN"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_EXCH_GRT_CATALOG_GEN		   L"AF_JOBTYPE_EXCH_GRT_CATALOG_GEN"	///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_ARCHIVE_CATALOGRESYNC		   L"AF_JOBTYPE_ARCHIVE_CATALOGRESYNC"	///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_FS_CATALOG_GEN_VM			   L"AF_JOBTYPE_FS_CATALOG_GEN_VM"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_FS_CATALOG_DISABLE		   L"AF_JOBTYPE_FS_CATALOG_DISABLE"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_DIRECT_RESTORE_VM_PROXY	   L"AF_JOBTYPE_DIRECT_RESTORE_VM_PROXY"///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_DIRECT_RESTORE_VM_STUB	   L"AF_JOBTYPE_DIRECT_RESTORE_VM_STUB"	///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_FS_CATALOG_DISABLE_VM		   L"AF_JOBTYPE_FS_CATALOG_DISABLE_VM"	///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_INTER_MERGE				   L"AF_JOBTYPE_INTER_MERGE"			///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RPS_REPLICATION			   L"AF_JOBTYPE_RPS_REPLICATION"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RPS_REPLICATION_IN		   L"AF_JOBTYPE_RPS_REPLICATION_IN"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_BACKUP_VMWARE_APP			   L"AF_JOBTYPE_BACKUP_VMWARE_APP"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RESTORE_VMWARE_APP		   L"AF_JOBTYPE_RESTORE_VMWARE_APP"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_MERGE						   L"AF_JOBTYPE_MERGE"					///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_VM_MERGE					   L"AF_JOBTYPE_VM_MERGE"				///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_MERGE_RPS					   L"AF_JOBTYPE_MERGE_RPS"				///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_CONVERSION				   L"AF_JOBTYPE_CONVERSION"				///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RPS_CONVERSION			   L"AF_JOBTYPE_RPS_CONVERSION"			///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_BMR						   L"AF_JOBTYPE_BMR"					///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RPS_DATA_SEEDING			   L"AF_JOBTYPE_RPS_DATA_SEEDING"		///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RPS_DATA_SEEDING_IN		   L"AF_JOBTYPE_RPS_DATA_SEEDING_IN"	///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_RESTORE_HYPERV			   L"AF_JOBTYPE_RESTORE_HYPERV"			///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_INSTANTVM					   L"AF_JOBTYPE_INSTANTVM"				///<
#define SWT_TEMPFOLDER_VALNAME_AF_JOBTYPE_ASSURED_RECOVERY			   L"AF_JOBTYPE_ASSURED_RECOVERY"		///<

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name AFBackend
 *-------------------------------------------------------------------------
 * These switches are defined for module "AFBackend.exe" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine"
 *
 * Description / Format: 
 *
 *           Value:          "SaveJobScript"=DWORD:00000001
 *           .....
 */
///@{
#define SWT_AFBACKEND_KEYNAME_SAVEJOBSCRIPT						L"SaveJobScript"				///< Save Job script to "C:\AFTemp" for debugging
#define SWT_AFBACKEND_KEYNAME_DESTTHRESHOLDFACTORFORMERGE		L"DestThresholdFactorForMerge"	///< If the value is non-0,Check destination threshold before merge and do merge only when destination free space is N times larger than of the olddest incremental session size, else skip the merge.If the value is 0, Don't check destination threshold for merge.
#define SWT_AFBACKEND_KEYNAME_SHOWVOLGUIDPATH					L"ShowVolGuidPath"				///< If the value is non-0, show vol guid path.
#define SWT_AFBACKEND_KEYNAME_ISCLUSTERMAPINMEMORY				L"IsClusterMapInMemory"			///< If the value is non-0, keep entire cluster map in memory which will improve the catalog generation time
#define SWT_AFBACKEND_KEYNAME_CATALOGMEMORYUSAGEINMB			L"CatalogMemoryUsageInMB"		///< Usually 1000000 cluster map entries are kept in memory before dumping them to on disk cluster map. i.e Catalog generation uses around 30 MB of memory by default. To increase this "CatalogMemoryUsageInMB" value should be set to the amount of memory user wants the catalog generation to use up in MB.
#define SWT_AFBACKEND_KEYNAME_DOLISTSORT						L"DoListSort"					///< if the volume has more that 30 million files, catalog generation can fail on 32 bit machine. In such scneario this value needs to be set to 1 an d /3GB switch needs to be enabled in boot.ini file and machine rebooted so that cataloggeneration process can use 3GB memory instead of default2GB
#define SWT_AFBACKEND_KEYNAME_VDDKENFORCENBD					L"VDDKEnforceNBD"			    ///< If the value is non-0, for Virtual Standby job, the conversion to VMware VC/Esx servers will not try use advanced transport mode, but use NBD mode directly
#define SWT_AFBACKEND_KEYNAME_D2DGUID							L"GUID"
#define SWT_AFBACKEND_KEYNAME_KEEPHBBUSNAPSHOT					L"KeepHBBUSnapshot"				///< Set it to 1 to prevent the snapshot from being deleted after a HBBU backup
#define SWT_AFBACKEND_KEYNAME_KEEPHBBUSNAPSHOT_BY_NAME			L"KeepHBBUSnapshotByName"		///< Set it to 1 to prevent the snapshot left by the previous crashed jobs from being deleted at the beginning of a HBBU VMware VM backup
#define SWT_AFBACKEND_KEYNAME_SAVEBROWSEINFO					L"SaveBrowseInfo"
#define SWT_AFBACKEND_KEYNAME_MAXTHROUGHPUT						L"MaxThroughput"
#define SWT_AFBACKEND_KEYNAME_LOGONTYPE							L"LogonType"					///< allow user adjust logon type between 2 - LOGON32_LOGON_INTERACTIVE , 3 - LOGON32_LOGON_NETWORK and 4 - LOGON32_LOGON_BATCH
#define SWT_AFBACKEND_KEYNAME_DISABLE_KEYMGMT                   L"DisableKeyMgmt"               ///< Disable key management so that session password will not be saved and retrieced. 
#define SWT_AFBACKEND_KEYNAME_VMIMAGE_OPTION                    L"VmimageOption"                ///< Option to control vmimage logic.
#define SWT_AFBACKEND_KEYNAME_VMIMAGE_FILE_RECORD_TO_PARSE      L"VmimageFileRecToParse"        ///< REG_SZ: File record numbers, separated by comma, to parse. If value is omitted or equal to zero all file record will be parse. If number is HEX, a '0x' prefix is required.
#define SWT_AFBACKEND_KEYNAME_VMIMAGE_READ_CACHE_SIZE           L"VmimageReadCacheSize"         ///< Read cache size (in bytes) for data read when traverse MFT.
#define SWT_AFBACKEND_KEYNAME_VMIMAGE_MAX_DATA_RUN_CNT_4_ATTR   L"MaxDataRunCnt4Attr"           ///< REG_DWORD: Maximal data run count support for an attribute list attribute. By default it is 512.
#define SWT_AFBACKEND_KEYNAME_DISABLERESCANFORVC_INCR			L"DisableAllESXNodeRescan"		///< Set it to 1 to prevent the rescan operation of all ESX nodes in the VC inventory during an incremental backup

#define SWT_AFBACKEND_KEYNAME_REPSAVECOMMTICKET					L"RepSaveCommTicket"			///< If save the rep_comm_lib ticket xml
#define SWT_AFBACKEND_KEYNAME_REPCOMMDUMPDIR					L"RepCommDumpDir"				///< The dir to save the xml, default is C:\RepTemp
#define SWT_AFBACKEND_KEYNAME_LOCKWAITTIME						L"LockWaitTime"					///< the lock wait time for session
#define SWT_AFBACKEND_KEYNAME_LOCKMAXRETRY						L"LockMaxRetry"					///< the max retry times
#define SWT_AFBACKEND_KEYNAME_LOCKWAITTIMEMERGE					L"LockWaitTime_Merge"			///< the lock wait time for merge job
#define SWT_AFBACKEND_KEYNAME_LOCKMAXRETRYMERGE					L"LockMaxRetry_Merge"			///< the max retry times for merge job
#define SWT_AFBACKEND_KEYNAME_REPDBGJOBSTATUS					L"RepDbgJobStatus"				///< the replication rep status, such as AF_JOB_STATUS_FAILED_STRING
#define SWT_AFBACKEND_KEYNAME_REPDBGRUNNINGTIME					L"RepDbgRunningTime"			///< the replication running time, in MS
#define SWT_AFBACKEND_KEYNAME_SPACEREQUIREDOFD2D                L"SpaceRequiredOfD2D"           ///< the minimum space required for D2D home drive 
#define SWT_AFBACKEND_KEYNAME_JREDIRECTORY						L"JreDirectory"					///< user can specify JRE directory manually so as not to use the JRE included in installation package
#define SWT_AFBACKEND_KEYNAME_HISTORYTORESERVEINDAY				L"BackupHistoryToReserveInDay"  ///< define how many backup histories to reserve in days. By default keep the last 30 days backup histories only.
#define SWT_AFBACKEND_KEYNAME_CHKDISK_THREADSNUMBER             L"ChkDskThreadsNumber"			///< define how many concurrent threads to check the states of mounted volumes. The default value is 2 and the maximium value is 10 and the minimum value is 1.
#define SWT_AFBACKEND_KEYNAME_CHKDISK_TIMEOUT					L"ChkDskProcessTimeout"			///< define the timeout value to wait for the exit of the process chkdsk.exe. The default value is 24 * 60 minutes. The unit is minute.
#define SWT_AFBACKEND_KEYNAME_CHECK_GUEST_VSS_DEPLOY_SIGNATURE	L"CheckGuestVssDeploySignature"	///< Set it to 1 to Check Guest VSS Deploy Signature file to ensure whether snapshot method has been deployed or undeployed from inside guest OS
#define SWT_AFBACKEND_KEYNAME_CHKDISK_RWLOCK_RETRYCOUNT			L"ChkDskRWLockCount"			///< this read/write lock is used for the issue that it takes long time to lock volume, while another mounting fails to wait for the volume mounting event. By default this value is 600 count, which equals 30 minutes. 
#define SWT_AFBACKEND_KEYNAME_CHKDISK_CONTROL_THRESHOLD			L"ChkDskControlThreshold"		///< Specify under which codition the throttling is enabled. For example, if this value is 30, if the memory usage exceeds 70%, the throttling is enabled
#define SWT_AFBACKEND_KEYNAME_CHKDISK_CONTROL_INTERVAL			L"ChkDskControlInterval"		///< Specify how much time, in seconds, it waits during the throttling
#define SWT_AFBACKEND_KEYNAME_CHKDISK_MINIMUM_WORKING_SET		L"ChkDskWorkingSetMinimum"		///< Specify the minimum working set, in MB, assigned to each check
#define SWT_AFBACKEND_KEYNAME_CHKDISK_DISABLE_THROTTLING		L"ChkDskDisableThrottling"		///< Specify whether to disable throttling
#define SWT_AFBACKEND_KEYNAME_CHKDISK_OPTION					L"ChkDskOption"					///< If the value is not 0, use chkdskEx
#define SWT_AFBACKEND_KEYNAME_WIN_IVM_MOUNT_DISK				L"WinIVMMountDisk"              ///< whether the Instant-VM job mount the disk to employ the driver injection. By default the value is 1.


///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name AFStorHBAMgmt
 *-------------------------------------------------------------------------
 * These switches are defined for module "AFStorHBAMgmt.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\AFStorHBAMgmt"
 *
 * Description / Format: 
 *
 *           Key:		AFStorHBAMgmt
 *		     Value:          "CacheFilePath"=string:
 *           .....
 */
///@{
#define SWT_AFSTORHBAMGMT_MODULENAME							CST_REG_ROOT_L L"\\AFStorHBAMgmt"
#define SWT_AFSTORHBAMGMT_KEYNAME_CACHEFILEPATH					L"CacheFilePath"				///<Specify the mounting driver cache file path
#define SWT_AFSTORHBAMGMT_KEYNAME_NOUSEUMDF						L"NoUseUmdf"
#define SWT_AFSTORHBAMGMT_KEYNAME_NODELVDISK					L"NoDelVDisk"
#define SWT_AFSTORHBAMGMT_KEYNAME_FORCEWRITABLE					L"ForceWritable"
#define SWT_AFSTORHBAMGMT_KEYNAME_FORCEREADONLY					L"ForceReadonly"
#define SWT_AFSTORHBAMGMT_KEYNAME_MOUNTRETRY					L"MountRetry"
#define SWT_AFSTORHBAMGMT_KEYNAME_SETMOUNTPATHRETRY				L"SetMountPathRetry"
#define SWT_AFSTORHBAMGMT_KEYNAME_ASYNCREAD						L"AsyncRead" ///<UMDF driver will read data from session synchronously, default is 1
#define SWT_AFSTORHBAMGMT_KEYNAME_TIMEOUT						L"TimeOut"   ///<second as unit, the max time to wait the read opertion finished when call AFStor in UMDF driver, default is 180
#define SWT_AFSTORHBAMGMT_KEYNAME_MEDIABUSIZE					L"MediaBufferSize"///< KB as unit, it is used to cache the data bwtween AFStor and requestor of UMDF, defaul is 64  
#define SWT_AFSTORHBAMGMT_KEYNAME_PHYSICALSECTORSIZE            L"PhysicalSectorSize" ///<the default volume is 0, it should be 512, 4096
#define SWT_AFSTORHBAMGMT_KEYNAME_FORCEPHYSICALCUSTOMIZEVALUE   L"UseCustomizePhysicalSize"///<force to use the PhysicalSectorSize assigned in registry
#define SWT_AFSTORHBAMGMT_KEYNAME_HOLDWRITE_TIMEOU              L"HoldWriteIoCtlTimeOut"///<millisecond, the timeout when send IOCtl to mounting driver for hold the write IO, it will wait all doing write operation finished
#define SET_AFSTORHBAMGMT_KEYNAME_WAITDEVICEREADYTIMEOUT		L"WaitDeviceReadyTimeoutS"///<wait timeout for volume device ready. unit: second
#define SWT_AFSTORHBAMGMT_KEYNAME_USEBLOCK2			            L"MountUseBlock2" ///< use block0000000002.ctf instead of AdrConfigure.xml
///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name OfflineCopy
 *-------------------------------------------------------------------------
 * These switches are defined for "OfflineCopy" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\OfflineCopy"
 *
 * Description / Format: 
 *
 *           Key:		OfflineCopy
 *			 Value:          "DebugSaveJobScript"=DWORD:00000001
 *           .....
 */
///@{
#define SWT_OFFLINECOPY_MODULENAME									CST_REG_ROOT_L L"\\OfflineCopy"
#define SWT_OFFLINECOPY_KEYNAME_DEBUGSAVEJOBSCRIPT					L"DebugSaveJobScript"			///< Debug save VCM job script for HyperV and ESX-proxy mode in C:\HATemp
//#define SWT_OFFLINECOPY_KEYNAME_DRDEBUGGETVMNAME					L"DRDebugGetVMName"				///< Debug get VM name and port by guid for DR
#define SWT_OFFLINECOPY_KEYNAME_SOCKETRECONNTIMEINTEVINS			L"SocketReconnTimeIntevInS"		///< Socket reconnect time interval, 60 seconds by default
#define SWT_OFFLINECOPY_KEYNAME_SOCKETRECONNTIMEOUTINS				L"SocketReconnTimeOutInS"		///< Socket reconnect timeout in seconds, 15 seconds by default
#define SWT_OFFLINECOPY_KEYNAME_SOCKETRECVTIMEOUTINS				L"SocketRecvTimeOutInS"			///< Socket recv timeout in seconds, 15 minutes by default
#define SWT_OFFLINECOPY_KEYNAME_SOCKETSENDTIMEOUTINS				L"SocketSendTimeOutInS"			///< Socket send timeout in seconds, 15 minutes by default
#define SWT_OFFLINECOPY_KEYNAME_FLUSHSIZEINMB						L"FlushSizeInMB"				///< 0 means donot flush when replica
#define SWT_OFFLINECOPY_KEYNAME_HATRANSSERVERPORT					L"HATransPort"					///< The socket port of the process HATransServer.exe listening, the default value is 4090
#define SWT_OFFLINECOPY_KEYNAME_STATUSSYNCINTERVAL					L"VSBStatusSyncInterval"		///< the time interval of Virtual Standby status sync to CPM, the default value is 60 (seconds)
#define SWT_OFFLINECOPY_KEYNAME_VSBTHREADCOUNT						L"VSBThreadCount"				///< the max thread count that Virtual Standby jobs can be used, the default value is 10

#define SWT_OFFLINECOPY_MODULENAME_CLIENT							SWT_OFFLINECOPY_MODULENAME L"\\Client"
#define SWT_OFFLINECOPY_KEYNAME_SOCKETSENDBUFSIZE					L"SocketSendBufSize"			///< Client socket send buffer size in bytes, 64K by default
#define SWT_OFFLINECOPY_KEYNAME_CACHEBLOCKSIZEINKB					L"CacheBlockSizeInKB"			///< The cache block size for client socket to send, 2M by default
#define SWT_OFFLINECOPY_KEYNAME_CACHEBLOCKCOUNT						L"CacheBlockCount"				///< The size of cache block list for client socket to send, 32 by default

#define SWT_OFFLINECOPY_MODULENAME_SERVER							SWT_OFFLINECOPY_MODULENAME L"\\Server"
#define SWT_OFFLINECOPY_KEYNAME_SOCKETRECEIVEBUFSIZE				L"SocketReceiveBufSize"			///< Server socket recv buffer size in bytes, 64K by default
//#define SWT_OFFLINECOPY_KEYNAME_DRVMHOSTNAME						L"DRVMHostName"					///< If DRDebugGetVMName is non-0, debug get VM name by guid for DR, 
//#define SWT_OFFLINECOPY_KEYNAME_DRVMPORT							L"DRVMPort"						///< If DRDebugGetVMName is non-0, debug get port by guid for DR, 

/** @addtogroup Support Support
* @{
*/

/** @name ASBUGetRecoveryPoint
*-------------------------------------------------------------------------
* These switches are defined for "ASBUGetRecoveryPoint" in register table
* ------------------------------------------------------------------------
* Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine"
*
* Description / Format:
*
*           Key:	   Engine
*		    Value:     "GetRecoveryPoint"=string:1
*			ASBUGetRecoveryPoint JNI,this JNI base on JNI GetRecoveryPoint but optimize the file read times.when "GetRecoveryPoint"=string:1,Agent webService will use old mode(GetRecoveryPoint).
*           .....
*/
///@{
#define SWT_USEGETRECOVERYPOINT_PATH							CST_REG_ROOT_L
#define SWT_USEGETRECOVERYPOINT_FLAG							L"UseGetRecoveryPoint"			
///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name AFBackupDll
 *-------------------------------------------------------------------------
 * These switches are defined for module "AFBackupDll.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\AFBackupDll"
 *
 * Description / Format: 
 *
 *           Key:		AFBackupDll
 *           Value:          "DeleteTempMetadataFolder"=DWORD:00000000
 *           Value:			 "PurgeFailedSession"=DWORD:00000000
 *           .....
 */
///@{
#define SWT_AFBACKUPDLL_MODULENAME									CST_REG_ROOT_L L"\\AFBackupDll"		///<
#define SWT_AFBACKUPDLL_KEYNAME_DELETETEMPMETADATAFOLDER			L"DeleteTempMetadataFolder"			///< If the value is 0, do not delete temporary meta data files generated by VMImage or Applications SQL / Exchange
#define SWT_AFBACKUPDLL_KEYNAME_PURGEFAILEDSESSION					L"PurgeFailedSession"				///< If the value is 0, do not purge failed session in order to check VHD integrity
//#define SWT_AFBACKUPDLL_KEYNAME_DUMPSKIPPEDJUNKFILE				L"DumpSkippedJunkFile"				///< If the value is non-0, to dump skipped file.
//#define SWT_AFBACKUPDLL_KEYNAME_DUMPSKIPPEDJUNKFILEFOLDER			L"DumpSkippedJunkFileFolder"		///< Where to save the dumped skipped file.
#define SWT_AFBACKUPDLL_KEYNAME_KEEPSNAPSHOT						L"KeepSnapshot"						///< If the value is non-0, persistent snapshot
#define SWT_AFBACKUPDLL_KEYNAME_KEEPVOLUMEBITMAP					L"KeepVolumeBitmap"					///< If the value is non-0, save driver’s bitmap as local files
#define SWT_AFBACKUPDLL_KEYNAME_LOGALLWRITEOPERATION				L"LogAllWriteOperation"				///< If the value is non-0, log all write operations to VHD
#define SWT_AFBACKUPDLL_KEYNAME_HANGONVMIMAGEERROR					L"HangOnVMImageError"				///< If the value is non-0, enter sleep forever loop if VMIMage encounter error
#define SWT_AFBACKUPDLL_KEYNAME_COMPARESNAPSHOTTOVHD				L"CompareSnapshotToVHD"				///<
#define SWT_AFBACKUPDLL_KEYNAME_COMPARESNAPSHOTTOVHD_VMIMAGE		L"CompareSnapshotToVHD_VMImage"		///<
#define SWT_AFBACKUPDLL_KEYNAME_SKIPJUNKFILE						L"SkipJunkFile"						///<
#define SWT_AFBACKUPDLL_KEYNAME_SKIPFREECLUSTERSFORINCRBACKUP		L"SkipFreeClustersForIncrBackup"	///<
#define SWT_AFBACKUPDLL_KEYNAME_FAKEREADSHADOWCOPY					L"FakeReadShadowCopy"				///<Fake read shadow copy for perf test
#define SWT_AFBACKUPDLL_KEYNAME_FAKEWRITEVDISK						L"FakeWriteVDisk"					///<Fake Write VDisk for perf test
#define SWT_AFBACKUPDLL_KEYNAME_SEQUENTIALSCANVOLUME				L"SequentialScanVolume"				///<
#define SWT_AFBACKUPDLL_KEYNAME_CREATESUBSESSIONMETADATA			L"CreateSubSessionMetadata"			///<
#define SWT_AFBACKUPDLL_KEYNAME_ENABLEASYNCIO						L"EnableAsyncIO"					///<
#define SWT_AFBACKUPDLL_KEYNAME_VERIFYVDISK							L"VerifyVDisk"						///<
#define SWT_AFBACKUPDLL_KEYNAME_WAITASYNCIO							L"WaitAsyncIO"						///<
#define SWT_AFBACKUPDLL_KEYNAME_BACKUPSPECIALPARTITIONSINCR			L"BackupSpecialPartitionsIncr"		///<to backup special partitions for full, verify, incr jobs according to reg settings
#define SWT_AFBACKUPDLL_KEYNAME_BACKUPSPECIALPARTITIONSVERIFY		L"BackupSpecialPartitionsVerify"	///<
#define SWT_AFBACKUPDLL_KEYNAME_ENFORCETRANSPORT					L"EnforceTransport"					///< This value is under the subkey "\\AFBackupDll\\$vmInstUUID", enforce transport mode for a specific VM for backup
#define SWT_AFBACKUPDLL_KEYNAME_DISABLEVMOTIONCHECK					L"DisableVMotionCheck"				///< To disabled VMotion Check, do not call PrepareforAccess to avoid hang
#define SWT_AFBACKUPDLL_KEYNAME_SAMPLENUFFERFILE					L"SampleBufferFile"
#define SWT_AFBACKUPDLL_KEYNAME_MAXSLEEPTIMEFORFILESIZE				L"MaxSleepTimeForFileSize"			///< the max sleep time for get compress file size (the file compressed by File System)
#define SWT_AFBACKUPDLL_KEYNAME_ENABLESKIPVSSFILES					L"EnableSkipVSSFiles"				///<
#define SWT_AFBACKUPDLL_KEYNAME_FILESNOTTOBACKUP					L"FilesNotToBackup"
#define SWT_AFBACKUPDLL_KEYNAME_PRECHECKDISKSPACE					L"PreCheckDiskSpace"				///<Registry setting for enable/disable pre-check for disk space
#define SWT_AFBACKUPDLL_KEYNAME_COMPRESSIONSAVINGSPERCENT			L"CompressionSavingsPercent"		///<Registry setting for compression savings
#define SWT_AFBACKUPDLL_KEYNAME_VMDKIOLOGTRUNC						L"VMDKIOLogTrunc"					///< max VDDK log size, it is for r16.5. since R17 VDDK logs are moved to individual debug log of jobs
#define SWT_AFBACKUPDLL_KEYNAME_VMOFFCAT							L"VMOffCat"							///< it is deprecated. always off line catalog for VM backups
#define SWT_AFBACKUPDLL_KEYNAME_GETAPPDETAILSTIMEOUT				L"GetAppDetailsTimeout"				///< VIX get applications details timeout
#define SWT_AFBACKUPDLL_KEYNAME_USERDEFINEDSCRIPT					L"UserDefinedScript"				///<user defined pre-job cmd script
#define SWT_AFBACKUPDLL_KEYNAME_SESSION_PASSWORD					L"SessionPassword"			    	///<Session password for backup.
#define SWT_AFBACKUPDLL_KEYNAME_DATA_PASSWORD					    L"DataPassword"			    	    ///<Data password for encrypting data.
#define SWT_AFBACKUPDLL_KEYNAME_CRYPTO_INFO  					    L"CryptoInfo"			    	    ///<Encryption information for encrypting data.
#define SWT_AFBACKUPDLL_KEYNAME_ENCRYPT_BY_SESSPWD				    L"EncBySessPwd"			    	    ///<Encrypt data by session password and ignore data password.
#define SWT_AFBACKUPDLL_KEYNAME_DISABLE_VMBACKUP_WARNLOG		    L"DisableVMBackupWarnLog"			///<Enable output HBBU warn activity log, non-0 is not output, other or no the value is output log, this is by default.
#define SWT_AFBACKUPDLL_KEYNAME_NO_ALIGN_WRITE                      L"NoAlignWrite"                     ///<if the key value is non-0, use legacy approach to send data, if it is 0, align the data offset.Default value is 0
#define SWT_AFBACKUPDLL_KEYNAME_ALIGN_SIZE_COPYONWRITE              L"AlignSize"                        ///<the align size of data block written in session, for example, for compression format is 64k;for non-compression format is 2M
#define SWT_AFBACKUPDLL_KEYNAME_ENABLE_COPY_ON_WRITE_HBBU           L"EnableCopyOnWriteForHBBU"         ///<if the key value is non-0, will enable copy on write for HBBU backup job
#define SWT_AFBACKUPDLL_KEYNAME_DUMP_VMDK							L"DumpVMDK"         ///<if the key value is non-0, will dump VMDK data blocks read from VM snapshot, the value is under VM instance UUID, e.g. HKEY_LOCAL_MACHINE\SOFTWARE\CA\CA ARCserve D2D\AFBackupDll\502d9e1a-ab79-e051-a382-ef505e5828cb
#define SWT_AFBACKUPDLL_KEYNAME_DUMP_CBT_BITMAP  					L"DumpCBTBitmap"                    ///<if the key value is non-0, Hyper-V VM backup job will dump CBT bitmap files to session folder
#define SWT_AFBACKUPDLL_KEYNAME_FIX_HIVE_INTEGRITY  				L"FixHiveIntegrity"                 ///<if the key value is non-0, Hyper-V VM backup job will fix the hive integrity problem for Windows 8.1/2012R2 VM
#define SWT_AFBACKUPDLL_KEYNAME_DUMP_VIRTUAL_DISK_DATA				L"DumpVirtualDiskData"				///<if the key value is non-0, Hyper-V VM backup job will dump the virtual disk file debug data
#define SWT_AFBACKUPDLL_KEYNAME_DUMP_VOLUME_BITMAP                  L"DumpVolumeBitmap"
#define SWT_AFBACKUPDLL_KEYNAME_WAIT_HOLD_MNTWRTIE                  L"WaitMntWrite"                     ///< second as unit. default value is 0, no wait
#define SWT_AFBACKUPDLL_KEYNAME_DONOT_RECONFIG_DISK_UUID			L"DoNotReconfigDiskUUID"			///< do not reconfigure disk.enableUUID to avoid backup failures (but will result in non-application consistent backup. NOT recommended) 
#define SWT_AFBACKUPDLL_KEYNAME_KEEP_HIVE_FILE						L"KeepHiveFile"						///<If the key is none zero, the hive file parsed from the virtual disk will be saved into BIN\temp
#define	SWT_AFBACKUPDLL_KEYNAME_DISK_REOPEN_INTERVAL				L"HBBUDiskReopenInterval"			///<The time interval to reopen the virtual disk file if file operation fails for Hyper-V HBBU
#define	SWT_AFBACKUPDLL_KEYNAME_HANG_DETECTION_TIMEOUT				L"HangDetectionTimeout"				///<The timeout (in second) to detect whether 3rd party API hang or not (for now it is used by HBBU VMware Backup)
#define	SWT_AFBACKUPDLL_KEYNAME_HOTADDVMDKLIST						L"HotaddVmdkList"					///<hot added VMDK list onto proxy machine (virtual machine) to be removed on next backup
#define SWT_AFBACKUPDLL_KEYNAME_RESERVE_VMEXCH_BINARY				L"ReserveVMExchBinary"				///if it is 1 or higher than 1, Exchange binaries from VM is not delete after the job complete. 0 or not exist is default, delete them after backup.
#define SWT_AFBACKUPDLL_KEYNAME_CACHE_BITMAP_INFILE					L"CacheBitmapInFile"				///< Cache bitmap in file instead of memory. It is used when backing up very large volume on 32bit system.
#define SWT_AFBACKUPDLL_KEYNAME_CACHE_BITMAP_FOLDER					L"CacheBitmapFolder"				///< The folder used to cache bitmap.
#define	SWT_AFBACKUPDLL_KEYNAME_SupplementDataChange    			L"SupplementDataChange"				///<default value is non-0, if it is 0, will not OR the bitmap from previous session for incremetnal backup
#define	SWT_AFBACKUPDLL_KEYNAME_vAppChildUseJobQueue    			L"vAppChildUseJobQueue"				///if it is 1, vApp child backups will be put into job queue; if 0, submmitted by backend directly. default is 1 (to use job queue)
#define	SWT_AFBACKUPDLL_KEYNAME_ResetCBTOnDiskSizeChange    		L"ResetCBTOnDiskSizeChange"			///if it is 1, it will ResetCBT On Disk Size Change; if 0, will not. default is 1 (to use job queue)
#define SWT_AFBACKUPDLL_KEYNAME_VERIFY_AFTER_FAILOLVER				L"DoVerifyAfterFailover"		    ///if it is 1, will convert incremental to verify after failover.
#define SWT_AFBACKUPDLL_KEYNAME_CUSTOMIZE_GUEST_VSS					L"CustomizeGuestVss"				///if it is 1, HBBU for VMware VM will use customized guest VSS; if 0, it use VMware VSS snapshot inside geust OS. Default value is 0
#define	SWT_AFBACKUPDLL_KEYNAME_VM_BACKUP_EXPECTATION    			L"VMBackupExpectation"				///enum VMBackupExpectation{ VM_BACKUP_EXPECTATION_CONSISTENT_ONLINE = 1, VM_BACKUP_EXPECTATION_ONLINE, VM_BACKUP_EXPECTATION_CONSISTENT };
#define	SWT_AFBACKUPDLL_KEYNAME_USE_DEDICATED_STUB					L"DedicatedStub"					///if it is 1, Hyper-V will use a dedicated backup stub for the specified VM
#define	SWT_AFBACKUPDLL_KEYNAME_VM_BACKUP_FAIL_IF_VSS_ERROR			L"VSSErrorFailJob"					///if it is 1, Hyper-V HBBU will fail the job if VSS writer failed to process the VM
#define SWT_AFBACKUPDLL_KEYNAME_DUMP_VSS_METADATA					L"DumpVSSMetaData"					///if it is 1, Hyper-V will save the VSS metadata
#define	SWT_AFBACKUPDLL_KEYNAME_CHECK_RECOVERYPOINT_DONT_FAIL_JOB   L"CheckRecoveryPointIgnoreError"	///if it is 1, the HBBU backup job won't do verify backup for the next backup job. default 0
#define	SWT_AFBACKUPDLL_KEYNAME_SNAPSHOT_METHOD_DONT_FAIL_JOB		L"SnapshotMethodDontFailJob"		///if it is 1, the HBBU backup job won't fail even if current snapshot method does not match the one in configuration. Default 0
#define	SWT_AFBACKUPDLL_KEYNAME_ENABLE_VSS_APP_SUPPORT				L"EnableVssAppSupport"				///if it is 1, the HBBU backup job support backup of applications when MS VSS snapshot method is used. default is 1
#define	SWT_AFBACKUPDLL_KEYNAME_DETECT_SNAPSHOT_CHANGE				L"DetectSnapshotChange"				///if it is 1, the HBBU backup job will detect VM snapshot changes since the last backup, if snapshot changes are detected, incremental backups will be converted to full backup. default is 1
#define	SWT_AFBACKUPDLL_KEYNAME_CANCEL_BACKUP_IF_ALL_DISKS_EXCLUDED				L"CancelBackupIfAllDisksExcluded"				///if it is 1, the HBBU backup job will be canceled if all disks are excluded from backup. default is 1
#define SWT_AFBACKUPDLL_KEYNAME_CREATE_SNAPSHOT_SEQUENTIALLY        L"CreateSnapshotSequentially"  // if it is NOT 0, which means create snapshot of VM on ESX server sequentially
#define SWT_AFBACKUPDLL_KEYNAME_CREATE_SNAPSHOT_TIMEOUT L"CreateSnapshotTimeout" //waiting time out for creating snapshot on ESX server sequentially, in second
#define	SWT_AFBACKUPDLL_KEYNAME_BACKUPENTIREDISKONCBTBITMAPFAILURE  L"BackupEntireDiskOnCBTBitmapFailure" ///if it is non-0, hbbu backup will backup the entire disk if it failed to require bitmap for changed/used blocks.
#define SWT_AFBACKUPDLL_KEYNAME_DISABLEQUIESCENCESNAPSHOT           L"DisableTakeQuiescenceSnapshot"      ///Disable taking VM tool Quiescence Snapshot
#define SWT_AFBACKUPDLL_KEYNAME_BACKUP_SQL_LOG_4_PURGE              L"BackupSQLLog4Purge"                 ///< Purge SQL log by calling BACKUP LOG command to deal with unable to shrink file for FULL mode database after full backup
#define SWT_AFBACKUPDLL_KEYNAME_FORCE_SHRINK_SQL_LOG                L"ForceShrinkSQLLog"                  ///< Purge SQL log by calling BACKUP LOG command and force move page in logs.
#define SWT_AFBACKUPDLL_KEYNAME_KEEP_PURGE_SQL_LOG_INFO             L"KeepPurgeSQLLogInfo"                ///< Keep purge SQL log information for purgesqlogs.exe
#define	SWT_AFBACKUPDLL_KEYNAME_VM_BACKUP_FALLBACK_TO_SW_SNAPSHOT   L"FallbackToSWSnapshot"               ///if it is 0, hbbu backup will not fall back to software snapshot from hardware snapshot
#define SWT_AFBACKUPDLL_KEYNAME_ENABLE_SHARED_JVM                   L"EnableSharedJVM"                      ///0 means didn't use RPC method, non zero represents to use RPC method, wanxi18 2015-06-16
#define SWT_AFBACKUPDLL_KEYNAME_CONTINUE_INREMENTAL_BY_CHANGE_ID	L"ContinueInrementalByChangeId"			// if 1, continue incremental backups by changeId; else by VMDK name
#define SWT_AFBACKUPDLL_KEYNAME_BACKUP_ZERO_BLOCK					L"BackupZeroBlock"						// if 1, full backup job will back up zero blocks. default 0
#define SWT_AFBACKUPDLL_KEYNAME_COUNT_NTFS_VOLUME_SIZE				L"CountNtfsVolumeSize"					// if 1, the protected size (managed capacity report) will count NTFS volumes only; else will count written size to D2D. default 0
#define SWT_AFBACKUPDLL_KEYNAME_REPORT_ZERO_IF_HAVING_NON_NTFS_VOLUME		L"ReportZeroIfHavingNonNtfsVolume"		// if 1, the protected size (managed capacity report) will be zero if having non-NTFs volumes or storage space; else will the sum of the sizes of NTFS volumes. default 0
#define SWT_AFBACKUPDLL_KEYNAME_CHECK_RECOVERY_POINT_DONT_FAIL_JOB_2		L"CheckRecoveryPointDontFailJob"		// if it is 1, the HBBU backup job won't fail on the failure of checking recovery point. default 1
#define SWT_AFBACKUPDLL_KEYNAME_PREPOST_CMD_TIMEOUT_IN_MINUTE				L"PrePostCMDTimeoutInMinute"			// The pre-post command timeout (in minute) for HBBU VMware and Hyper-V  VM backup job. default is 3 (minutes)
#define SWT_AFBACKUPDLL_KEYNAME_ENABLE_BDI							L"EnableBDI"						///<default value is 0. It it is non-zero, means will enable BDI to find destination
#define SWT_AFBACKUPDLL_KEYNAME_DISABLE_SPECIFIC_VSS_WRITERS				L"DisableSpecificVSSwriters"			// The list of writers to be disabled inside guest for VMware VM backup (it is a multiple-string value)

//[HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\AFBackupDll\SkipErrCode]
//	"23"=dword:00000017
//	"1117"=dword:0000045d
//  ...
#define SWT_AFBACKUPDLL_SUBKEYNAME_SKIP_ERR_CODE					L"SkipIoErrCode"					// For special IO error code, we skip it.
#define SWT_AFBACKUPDLL_KEYNAME_COLLECTDRINFO_TIMEOUT				L"CollectDrInfoTimeout"				///<collect dr info timeout millisecond.
///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name AFRestoreDll
 *-------------------------------------------------------------------------
 * These switches are defined for module "AFRestoreDll.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\AFRestoreDll"
 *
 * Description / Format: 
 *
 *           Key:		AFRestoreDll
 *           Value:          "EnforceTransportForRecovery"=string:
 *           .....
 */
///@{
#define SWT_AFRESTOREDLL_MODULENAME									CST_REG_ROOT_L L"\\AFRestoreDll"
#define SWT_AFRESTOREDLL_KEYNAME_ENFORCETRANSPORTFORRECOVERY		L"EnforceTransportForRecovery"		///<enforced transport mode
#define SWT_AFRESTOREDLL_KEYNAME_SESSION_PASSWORD					L"SessionPassword"		            ///<Session password to restore.
#define SWT_AFRESTOREDLL_KEYNAME_CRYPTO_INFO  					    L"CryptoInfo"			    	    ///<Decryption information for decrypting data.
#define SWT_AFRESTOREDLL_KEYNAME_VAPP_NUMBEROFCONCURRENTRUNNINGJOB  L"NumberOfConcurrentRunningJob"     ///<setting of concurrent job number for vApp child VM recovery
#define SWT_AFRESTOREDLL_KEYNAME_RESTORE_CONTROL_GLOBAL             L"RestoreCtrl"                      ///<Control option for restore.
#define SWT_AFRESTOREDLL_KEYNAME_RESTORE_CONTROL_SESSION            L"RestoreCtrlSess"                  ///<Control option for restore specified session, 0xFFFFFFFF or 0 means all session.
#define SWT_AFRESTOREDLL_KEYNAME_RESTORE_CONTROL_SUB_SESSION        L"RestoreCtrlSubSess"               ///<Control option for restore specified sub session, 0xFFFFFFFF or 0 means all sub session.
#define SWT_AFRESTOREDLL_KEYNAME_METAFILE_PATH                      L"MetaFilePath"                     ///<Configure temp meta file path.
#define SWT_AFRESTOREDLL_KEYNAME_KEEP_TEMP_FOLDER                   L"KeepTmpFolder"                    ///<Configure if meta files should be kept after restore.
#define SWT_AFRESTOREDLL_KEYNAME_GENERATENEWVMINSTANCEUUID          L"GenerateNewVMInstanceUUID"        ///<setting of if generate new instance uuid for VM recovery
#define SWT_AFRESTOREDLL_KEYNAME_VDISK_CACHE_MIN_BUFF_SIZE_MB          L"VDiskMinimumCacheBufferSizeInMB"   ///<DWORD, Specify the miniumun cache buffer size in MB for virtual disk recovery, can be 2, 4, 8MB... For Hyper-V VM recovery, configure it on the hyper-v host.
#define SWT_AFRESTOREDLL_KEYNAME_ENABLE_SHARED_JVM                   L"EnableSharedJVM"                      ///0 means didn't use RPC method, non zero represents to use RPC method

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name AFArchiveDLL
 *-------------------------------------------------------------------------
 * These switches are defined for module "AFArchiveDLL.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\AFArchiveDLL"
 *
 * Description / Format: 
 *
 *           Key:		AFArchiveDLL
 *           Value:          "ArchMultChunkIO"=DWORD:00000000
 *           .....
 *           .....
 */
///@{
#define SWT_AFARCHIVEDLL_MODULENAME									CST_REG_ROOT_L L"\\AFArchiveDLL"		///<
#define SWT_AFARCHIVEDLL_KEYNAME_ARCHMULTCHUNKIO					L"ArchMultChunkIO"
#define SWT_AFARCHIVEDLL_KEYNAME_ARCHLOGLEVEL						L"ArchLogLevel"
#define SWT_AFARCHIVEDLL_KEYNAME_ARCHMAXLINESPERFILE				L"ArchMaxLinesPerFile"
#define SWT_AFARCHIVEDLL_KEYNAME_CREATESTUBFILE						L"CreateStubFile"
#define SWT_AFARCHIVEDLL_KEYNAME_SKIPWINDOWSFOLDERS					L"SkipWindowsFolders"
#define SWT_AFARCHIVEDLL_KEYNAME_ARCHCHUNKSIZEINMB						L"ArchChunkSizeInMB" 

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name LicenseMsg
 *-------------------------------------------------------------------------
 * These switches are defined for "LicenseMsg" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\LicenseMsg"
 *
 * Description / Format: 
 *
 *           Key:		LicenseMsg
 *           Value:          "TurnoffLogSqlLicErr"=DWORD:00000000
 *           .....
 *           .....
 */
///@{
#define SWT_LICENSEMSG_MODULENAME									CST_REG_ROOT_L L"\\LicenseMsg"			///<wanxi08 for issue #19520378-1 (3DSP LICENSE ERROR HAPPEN)
#define SWT_LICENSEMSG_KEYNAME_TURNOFFLOGSQLLICERR					L"TurnoffLogSqlLicErr"
#define SWT_LICENSEMSG_KEYNAME_TURNOFFLOGEXCHLICERR					L"TurnoffLogExchLicErr"
#define SWT_LICENSEMSG_KEYNAME_TURNOFFBLILICERR						L"TurnoffBLILicErr"
#define SWT_LICENSEMSG_KEYNAME_TURNOFFBMRLICERR						L"TurnoffBMRLicErr"
#define SWT_LICENSEMSG_KEYNAME_TURNOFFBMRALTLICERR					L"TurnoffBMRAltLicErr"
#define SWT_LICENSEMSG_KEYNAME_TURNOFFLOGHYPERVLICERR				L"TurnoffLogHyperVLicErr"

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name WebService
 *-------------------------------------------------------------------------
 * These switches are defined for "WebService" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\WebService"
 *
 * Description / Format: 
 *
 *           Key:		WebService
 *           Value:          "LogRetentionDays"=DWORD:00000000
 *			 Value:			 "EnableNodeID"    =REG_SZ
 *           .....
 *           .....
 */
///@{
#define SWT_WEBSERVICE_MODULENAME									CST_REG_ROOT_L L"\\WebService"
#define SWT_WEBSERVICE_KEYNAME_LOGRETENTIONDAYS						L"LogRetentionDays"
#define SWT_WEBSERVICE_KEYNAME_ENABLENODEID							L"EnableNodeID"
#define SWT_WEBSERVICE_KEYNAME_CHECKUPDATETIMEOUT					L"CheckUpdateTimeout" ///<Update downloading timeout value, default value is 30 mintues. If time is up we will continue download update but UI will see an error message.
#define SWT_WEBSERVICE_KEYNAME_DISABLECLUSTERMONITOR				L"DisableClusterMonitor" ///<Disable the cluster monitor thread

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name RPSReplication
 *-------------------------------------------------------------------------
 * These switches are defined for "RPSReplication" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\RPSReplication"
 *
 * Description / Format: 
 *
 *           Key:		RPSReplication
 *           Value:          "D2DLocalTempLocation"=string:
 *           .....
 *           .....
 */
///@{
#define SWT_RPSREPLICATION_MODULENAME								CST_REG_ROOT_L L"\\RPSReplication"		///<rps replication job
#define SWT_RPSREPLICATION_KEYNAME_D2DLOCALTEMPLOCATION				L"D2DLocalTempLocation" ///<Temp path on the source side for caching the *.D2D file when replicating the disk data to dedup data store
#define SWT_RPSREPLICATION_KEYNAME_FLUSH_BLOCK_SIZE_MB              L"RPSFlushBlockSizeMB" ///<Replication use this size as a unit to flush data to the disk on the destination side

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name Network
 *-------------------------------------------------------------------------
 * These switches are defined for "Transport.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\Network"
 *
 * Description / Format: 
 *
 *           Key:		Network
 *           Value:          "NET_ChunkLength"=dword:
 *           .....
 *           .....
 */
///@{
#define SWT_NETWORK_MODULENAME									CST_REG_ROOT_L L"\\Network"
#define SWT_NETWORK_CHUNKLENGTH									L"NET_ChunkLength"					///<data chunk size that is transferred on LAN. Unit:byte,range:512B-128MB,default:64kB.
#define SWT_NETWORK_IOREADLENGTH								L"NET_IOReadLength"					///<read/write data chunk size from/to disk. Unit:byte,range:512B-128MB,default:64kB.
//#define SWT_NETWORK_GETHAINFOTIMEOUT							L"NET_GetHAInfoTimeout"				///<Get peer software information. Unit:second,default:10. Not used in UDP.
//#define SWT_NETWORK_HASCRIPTEXECUTIONTIMEOUT					L"NET_HAScriptExecutionTimeout"		///<Unit:second,default:180. Not used in UDP.
//#define SWT_NETWORK_MOVEDIPASPRIMARYIP						L"NET_MovedIPAsPrimaryIP"			///<Not used in UDP.
//#define SWT_NETWORK_NET_ADDITIONALIPPINGTEST					L"NET_AdditionalIPPingTest"			///<Not used in UDP.
#define SWT_NETWORK_NODELAY										L"NET_NoDelay"						///<Disables the Nagle algorithm for send coalescing. non-zero: disable.
#define SWT_NETWORK_RECEIVECONNECTTIMEOUT						L"NET_ReceiveConnectTimeout"		///<receive timeout. Unit:second,range:20-7200,default:60.
#define SWT_NETWORK_SENDTIMEOUT									L"NET_SendTimeout"					///<send timeout. Unit:second,range:20-7200,default:60.
#define SWT_NETWORK_RECVTIMEOUT									L"NET_RecvTimeout"					///<heartbeat timeout. Unit:second,range:20-7200,default:20.
#define SWT_NETWORK_SENDRECVCTRL								L"NET_SendRecvCtrl"					///<whether set socket send/recv buffer size. non-zeor: set, NET_SendRecvBufferSize will be loaded.
#define SWT_NETWORK_SENDRECVBUFFERSIZE							L"NET_SendRecvBufferSize"			///<socket low layer send/recv buffer size when NET_SendRecvCtrl is non-zero.Unit:byte,range:4kB-32MB,default:256kB.
#define SWT_NETWORK_ENABLEHEARTBEAT								L"NET_EnableHeartbeat"				///<whether enable heartbeat. 0(zero): disable, non-zero: enabled, default:1(enable).
#define SWT_NETWORK_HEARTBEATINTERVAL							L"NET_HeartbeatInterval"			///<send a heartbeat with this interval.Unit:second,range:20-7200,default:30.
#define SWT_NETWORK_HEARTBEATTIMEOUT							L"NET_HeartbeatTimeout"				///<heartbeat timeout.Heartbeat can't be sent in this period, the connection is destroied.Unit:second,range:60-7200,default:180.
#define SWT_NETWORK_RECVCHUNKDISORDEREDTIMEOUT					L"NET_RecvChunkDisorderedTimeout"	///<for multi-stream. timeout for chunk data.
#define SWT_NETWORK_BACKLOG										L"NET_Backlog"						///<The maximum length of the queue of pending connections. Range:50-2147483647(2G-1). Default:250.
#define SWT_NETWORK_FILEACKTIMEOUT								L"NET_FileAckTimeout"				///<ACK timeout for file data block.Unit:second,range:20-7200,default:60.
#define SWT_NETWORK_COMMANDTIMEOUT								L"NET_CommandTimeout"				///<ACK timeout for command.Unit:second,range:20-7200,default:30.
#define SWT_NETWORK_TIMESTAMPREALTIMESYNC						L"NET_TimestampRealtimeSync"		///<set file time with original one. non-zero, set. default:1.
#define SWT_NETWORK_MAXRECEIVETHREADS							L"NET_MaxReceiveThreads"			///<max. thread number.Range:100-1000,default:300. 
#define SWT_NETWORK_MINRECEIVETHREADS							L"NET_MinReceiveThreads"			///<min. thread number.Range:10-100,default:10. 
#define SWT_NETWORK_CLIENTMAXTHREAD								L"NET_ClientMaxThread"				///<max. client thread in transport.dll's thread pool.Range:1-1000,default:10.
#define SWT_NETWORK_SERVERMAXTHREAD								L"NET_ServerMaxThread"				///<max. server thread in sync_utl.dll's thread pool. Range:1-1000,default:10.
#define SWT_NETWORK_RECONNCTRL									L"NET_ReconnCtrl"					///<whether control reconnection behavior by registry. Non-zero:controlled by registry. Default:0.
#define SWT_NETWORK_RECONNTIMEOUT								L"NET_ReconnTimeout"				///<reconnection timeout.Unit:second,range:20-7200,default:60.
#define SWT_NETWORK_RECONNINTERVAL								L"NET_ReconnInterval"				///<reconnection interval.Unit:second,range:5-7200,default:10.
#define SWT_NETWORK_INHERITPORTSHARING							L"NET_InheritPortSharing"			///<apply port sharing for all connections.non-zero: inherite. default:2.
#define SWT_NETWORK_MULTISTREAMSMAXCACHESIZE					L"WAN_MultiStreamsMaxCacheSize"		///<cache size for multi-stream feature.Unit:byte,range:1MB-128MB,default:16MB.
#define SWT_NETWORK_ENABLEAUTOTUNNING							L"WAN_EnableAutoTunning"			///<whether detect network status to enable/disable multi-stream feature.Non-zero:eanble.Default:1.
#define SWT_NETWORK_NUMBEROFSTREAMS								L"WAN_NumberofStreams"				///<How many streams will be created when multi-stream is enabled. Range:2-10,default:5.
#define SWT_NETWORK_RTT_THRESHOLD								L"WAN_RTT_Threshold"				///<How slow that multi-stream should be enabled.Unit:ms,range:20-600,default:50.
#define SWT_NETWORK_CHUNKSIZEBYTE								L"WAN_ChunkSizeByte"				///<chunk size on WAN when multi-stream is enabled by application.
#define SWT_NETWORK_SENDCOMMANDFRAGDATAMERGED					L"WAN_SendCommandFragDataMerged"	///<merge small command to a large one. non-zero:enable.default:0.
#define SWT_NETWORK_SSLSELFSIGNEDCERTIFICATE					L"SSLSelfSignedCertificate"			///<self-signed certificate for SSL. path name.
#define SWT_NETWORK_SSLRSAPRIVATEKEY							L"SSLRSAPrivateKey"					///<private key for SSL. path name.

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name NetworkThrottling
 *-------------------------------------------------------------------------
 * These switches are defined for "TransportThrottling.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\NetworkThrottling"
 *
 * Description / Format: 
 *
 *           Key:		Network
 *           Value:          "ExtendToMidnight"=dword:00000001
 *           .....
 *           .....
 */
///@{
#define SWT_NETWORKTHROTTLING_MODULENAME									CST_REG_ROOT_L L"\\NetworkThrottling"
#define SWT_NETWORKTHROTTLING_EXTENDTOMIDNIGHT								L"ExtendToMidnight"		///<whether extend 23:45 to 24:00 for end time of network throttling schedule. non-zero:extend,default:1.

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name Database
 *-------------------------------------------------------------------------
 * These switches are defined for "Database" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\Database"
 *
 * Description / Format: 
 *
 *           Key:		Database
 *           Value:          "DisableSync"=DWORD:00000000
 *           .....
 *           .....
 */
///@{
#define SWT_FLASHDB_MODULENAME									CST_REG_ROOT_L L"\\Database"
#define SWT_FLASHDB_KEYNAME_DISABLESYNC							L"DisableSync"
#define SWT_FLASHDB_KEYNAME_COPYPAGES							L"CopyPages"
#define SWT_FLASHDB_KEYNAME_COPYINTERVAL						L"CopyInterval"

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name CBImage
 *-------------------------------------------------------------------------
 * These switches are defined for "CBImage" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\CBImage"
 *
 * Description / Format: 
 *
 *           Key:		CBImage
 *           Value:          "CommandTimeout"=DWORD:00000000
 *           .....
 *           .....
 */
///@{
#define SWT_CBIMAGE_MODULENAME									CST_REG_ROOT_L L"\\CBImage"
#define SWT_CBIMAGE_KEYNAME_COMMANDTIMEOUT						L"CommandTimeout"			///<the command execute timeout, unit is min, smaller than 100 mins
#define SWT_CBIMAGE_KEYNAME_WINPE_DEBUG_ON						L"DebugOn"					///<set debug on of bmr iso or usb stick, non 0-debug on							
#define SWT_CBIMAGE_KEYNAME_TEMP_PATH							L"TempPath"					///<the temp path for WADK or WAIK
///@}
/** @} */

/** @addtogroup Developer Developer
 * @{
 */

/** @name SyncOptions
 *-------------------------------------------------------------------------
 * These switches are defined for "SyncOptions" in 'Switch.ini'
 * ------------------------------------------------------------------------
 * Location: "$Home$\Configuration\Switch.ini"
 *
 * Description / Format: 
 *
 *           [SyncOptions]
 *           .....
 *           .....
 */
///@{
#define SWT_SYNC_OPTIONS										L"SyncOptions"					///<the kep is to control syncup options
#define SWT_DISABLE_SYNC_ACTIVITY_LOG							L"DisableSync_ActLog"			///<If the key is none zero, don't sync activity logs
#define SWT_DISABLE_SYNC_JOB_HISTORY							L"DisableSync_JobHistory"		///<If the key is none zero, don't sync job histories
#define SWT_DISABLE_SYNC_FULL_ACTIVITY_LOG						L"DisableSync_FullActLog"		///<If the key is none zero, don't sync full activity logs
#define SWT_DISABLE_SYNC_FULL_JOB_HISTORY						L"DisableSync_FullJobHistory"	///<If the key is none zero, don't sync full job histories
#define SWT_DISABLE_SYNC_REPLICATION							L"DisableSync_Replication"		///<If the key is none zero, don't sync replication staff
#define SWT_DISABLE_SYNC_UPDATE_LOG								L"DisableSync_UpdateLog"		///<If the key is none zero, don't sync update activity logs
#define SWT_DISABLE_SYNC_RECOVERYPOINT_INFO						L"DisableSync_RecoveryPointInfo"///<If the key is none zero, don't sync recovery point info

#define SWT_KEEP_FILES_ACTIVITY_LOG								L"KeepFile_ActLog"				///<If the key is none zero, keep a copy of synced activity logs
#define SWT_KEEP_FILES_JOB_HISTORY								L"KeepFile_JobHistory"			///<If the key is none zero, keep a copy of synced job histories
#define SWT_KEEP_FILES_FULL_ACTIVITY_LOG						L"KeepFile_FullActLog"			///<If the key is none zero, keep a copy of synced full activity logs
#define SWT_KEEP_FILES_FULL_JOB_HISTORY							L"KeepFile_FullJobHistory"		///<If the key is none zero, keep a copy of synced full job histories
#define SWT_KEEP_FILES_REPLICATION								L"KeepFile_Replication"			///<If the key is none zero, keep a copy of synced replication staff
#define SWT_KEEP_FILES_UPDATE_LOG								L"KeepFile_UpdateLog"			///<If the key is none zero, keep a copy of synced update activity logs
#define SWT_KEEP_FILES_RECOVERYPOINT_INFO						L"KeepFile_RecoveryPointInfo"	///<If the key is none zero, keep a copy of synced recovery point info

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name Hyper-V HBBU Backup Stub
 *-------------------------------------------------------------------------
 * These switches are defined for "Hyper-V HBBU Backup Stub" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\Hyper-v HBBU CBT"
 * Target:	 Hyper-V host
 *
 * Description / Format: 
 *
 *           Key:		Hyper-v HBBU CBT
 *           Value:     "EnableAutoMount"=DWORD:00000000
 *           .....
 *           .....
 */
///@{
#define	SWT_HBBU_BACKUP_STUB_MODULE								CST_REG_ROOT_L L"\\Hyper-v HBBU CBT"
#define SWT_HBBU_BACKUP_STUB_ENABLE_AUTO_MOUNT					L"EnableAutoMount"
#define SWT_HBBU_BACKUP_STUB_DEBUG_DUMP							L"DebugDump"							///<If the key is none zero, Hyper-V Backup Stub will keep the VSS snpashot it creates
#define SWT_HBBU_BACKUP_STUB_DEBUG_DUMP_MAX_SNAPSHOT			L"DebugMaxSnapshot"						///<Specify the maximum number of VSS snapshots Hyper-V Backup Stub keeps
#define	SWT_HBBU_BACKUP_STUB_CBT_VERSION_FROM_REG				L"VersionFromReg"						///<Specify wheather Backup Stub gets the CBT service/driver version from registry or binary file version
#define SWT_HBBU_BACKUP_STUB_DUMP_BCD							L"DumpBCD"								///<If the key is 1, Hyper-V Backup Stub will dump the VSS BCD
#define SWT_HBBU_BACKUP_STUB_REMOTE_SNAPSHOT_DETECTION_COUNT	L"RemoteSnapshotDetectionCount"			///<The key allows users to customize the remote vss snapshot detection count, should be bigger than 20

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name GRTMntBrowser
 *-------------------------------------------------------------------------
 * These switches are defined for module "GRTMntBrowser.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\GRTMntBrowser"
 *
 * Description / Format: 
 *
 *           Key:		GRTMntBrowser
 *           Value:          "CompareWithSession"=DWORD:00000000
 *           .....
 *           .....
 */
///@{
#define SWT_GRTMNTBROWSER_MODULENAME									CST_REG_ROOT_L L"\\GRTMntBrowser"		///<
#define SWT_GRTMNTBROWSER_KEYNAME_COMPARE2SESSION					L"CompareWithSession" ///<If the key is none zero, will compare the restored file with the file which is on session
#define SWT_GRTMNTBROWSER_KEYNAME_COMPARE2SOURCE					L"CompareWithSource" ///<If the key is none zero, will compare the restored file with the source file. the folder of source file is assigned by SWT_GRTMNTBROWSER_KEYNAME_COMPARE2SOURCE_PATH
#define SWT_GRTMNTBROWSER_KEYNAME_COMPARE2SOURCE_PATH               L"CompareWithSourcePath" ///<the path for the folder where the source files locate
#define SWT_GRTMNTBROWSER_KEYNAME_COMPARE_HANG_DIFF                 L"CompareHangDiff"///<If the key is none zero, the resore process will hang if the content is different. default value is 1

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name RPS Jumpstart
 *-------------------------------------------------------------------------
 * These switches are defined for "RPS Jumstart" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\RPS JumpStart"
 *
 * Description / Format: 
 *
 *           Key:		RPS JumpStart
 *           Value:     "JumpStartConcurrencyCount"=DWORD:00000000
 *           .....
 *           .....
 */
///@{
#define	SWT_RPS_JUMP_START_MODULE								CST_REG_ROOT_L L"\\RPS JumpStart"
#define SWT_RPS_JUMP_START_MODULE_CONCURRENCY_COUNT				L"JumpStartConcurrencyCount"

///@}
/** @} */

/** @addtogroup Support Support
 * @{
 */

/** @name AFCoreInterface
 *-------------------------------------------------------------------------
 * These switches are defined for module "AFCoreInterface.dll" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\AFCoreInterface"
 *
 * Description / Format: 
 *
 *           Key:		AFCoreInterface
 *           Value:          "CompareWithSession"=DWORD:00000000
 *           .....
 *           .....
 */
///@{
#define SWT_AFCOREINTERFACE_MODULENAME									CST_REG_ROOT_L L"\\AFCoreInterface"		///<
#define SWT_AFCOREINTERFACE_KEYNAME_LOGHISTORY					L"LogHistory" ///<the retention count of the debug log will be keept. the default value is 3, means will keep the last 3 month of the debug logs

///@}
/** @} */



/** @name DataStoreInstService
 *-------------------------------------------------------------------------
 * These switches are defined for "DataStoreInstService" in register table
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore"
 *
 * Description / Format: 
 *
 *           Key:		DataStore
 *           Value:     "MaxDedupeDSAmount"=DWORD:00000000
 *           .....
 *           .....
 */
#define SWT_DATASTOREINSTSERVICE_MODULENAME						CST_REG_ROOT_L L"\\DataStore"

/** @addtogroup Developer Developer
 * @{
 */
#define SWT_DATASTORE_KEYNAME_PORTRANGEFORGDD					L"PortRangeForGDD"
/** @} */
/** @addtogroup Support Support
 * @{
 */
#define SWT_DATASTORE_KEYNAME_MAXDEDUPEDSAMOUNT					L"MaxDedupeDSAmount"			///<maximum dedupe data store amount, 30 by default.
#define SWT_DATASTORE_KEYNAME_MONSTATUSINTERVAL					L"MonStatusInterval"			///<monitor data store status interval forwardly, 300 seconds by default.
/** @} */ 



/** @name <DataStoreName>
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore\<DataStoreName>
 *
 * Description / Format: 
 *
 *           Key:		<DataStoreName>
 *           Value:     "CompressType"=REG_DWORD:1
 *						...
 */
/** @addtogroup Developer Developer
 * @{
 */
#define SWT_DATASTORE_KEYNAME_COMPRESSTYPE						L"CompressType"
#define SWT_DATASTORE_KEYNAME_DSPASSWORD						L"DSPassword"
#define SWT_DATASTORE_KEYNAME_DSPASSWORDHASH					L"DSPasswordHash"
#define SWT_DATASTORE_KEYNAME_DSVERSION							L"DSVersion"
#define SWT_DATASTORE_KEYNAME_ENABLECOMPRESS					L"EnableCompress"
#define SWT_DATASTORE_KEYNAME_ENABLEENCTYPT						L"EnableEnctypt"
#define SWT_DATASTORE_KEYNAME_ENABLEGDD							L"EnableGDD"
#define SWT_DATASTORE_KEYNAME_ENCRYPTINFO						L"EncryptInfo"
#define SWT_DATASTORE_KEYNAME_ENCRYPTTYPE						L"EncryptType"
#define SWT_DATASTORE_KEYNAME_HASHVALUE							L"HashValue"
#define SWT_DATASTORE_KEYNAME_INSTGUID							L"InstGUID"
#define SWT_DATASTORE_KEYNAME_MANUALSTOP						L"ManualStop"
#define SWT_DATASTORE_KEYNAME_MAXNODECOUNT						L"MaxNodeCount"
#define SWT_DATASTORE_KEYNAME_SHAREDNAME						L"SharedName"
#define SWT_DATASTORE_KEYNAME_WATERMARK							L"WaterMark"
/** @} */



/** @name CommStore
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore\<DataStoreName>\CommStore"
 *
 * Description / Format: 
 *
 *           Key:		CommStore
 *			 Comment:   For "WarnPathThreshold" and "ErrorPathThreshold", when the value is smaller than 1, means percentage of total space of the valume;
 *						When the value is equal to or larger than 1, means actual size, in MB.
 *           Value:     "WarnPathThreshold"=REG_SZ:"0.03"
 *					    "ErrorPathThreshold"=REG_SZ:"100"
 *						...
 */
/** @addtogroup Developer Developer
 * @{
 */
#define SWT_DATASTORE_KEYNAME_COMMONPATH_STOREPATH					L"StorePath"
#define SWT_DATASTORE_KEYNAME_COMMONPATH_USER						L"User"
#define SWT_DATASTORE_KEYNAME_COMMONPATH_PWD						L"Pwd"
/** @} */
/** @addtogroup Support Support
 * @{
 */
#define SWT_DATASTORE_KEYNAME_COMMONPATH_THRESHOLD_WARN				L"WarnPathThreshold"			///<Warning threshold of backup destination path free space, 0.03 by default. 
#define SWT_DATASTORE_KEYNAME_COMMONPATH_THRESHOLD_ERR				L"ErrorPathThreshold"			///<Error threshold of backup destination path free space, 100 by default.
/** @} */ 




/** @name GDD
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore\<DataStoreName>\GDD"
 *
 * Description / Format: 
 *
 *           Key:		GDD
 *           Value:     "WarnPathThreshold"=REG_DWORD:4
 *						...
 */
/** @addtogroup Developer Developer
 * @{
 */
#define SWT_DATASTORE_KEYNAME_GDD_DEDUPEBLOCKSIZEINKB				L"DedupeBlockSizeInKB"
/** @} */



/** @name NGDD
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore\<DataStoreName>\NGDD"
 *
 * Description / Format: 
 *
 *           Key:		NGDD
 *           Value:     "DisablePreRead"=REG_DWORD:0
 *						...
 */
/** @addtogroup Support Support
 * @{
 */
#define SWT_DATASTORE_KEYNAME_NGDD_DISABLEPREREAD					L"DisablePreRead"
#define SWT_DATASTORE_KEYNAME_NGDD_SLICEMERGETHRESHOLDINPERCENTAGE	L"SliceMergeThresholdInPercentage"
#define SWT_DATASTORE_KEYNAME_NGDD_SLICESIZEINMB					L"SliceSizeInMB"
#define SWT_DATASTORE_KEYNAME_NGDD_ENABLESPARSE						L"EnableSparse"
#define SWT_DATASTORE_KEYNAME_NGDD_DISABLERESERVINGSPACEFORMERGE	L"DisableReservingSpaceForMerge"
/** @} */



/** @name IndexRole
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore\<DataStoreName>\GDD\IndexRole"
 *
 * Description / Format: 
 *
 *           Key:		IndexRole
 *			 Comment:   For "WarnPathThreshold" and "ErrorPathThreshold", when the value is smaller than 1, means percentage of total space of the valume;
 *						When the value is equal to or larger than 1, means actual size, in MB.
 *           Value:     "WarnPathThreshold"=REG_SZ:"0.03"
 *					    "ErrorPathThreshold"=REG_SZ:"100"
 *						...
 */
/** @addtogroup Developer Developer
 * @{
 */
#define SWT_DATASTORE_KEYNAME_GDD_INDEX_IP						L"IP"
#define SWT_DATASTORE_KEYNAME_GDD_INDEX_PATH					L"Path"
#define SWT_DATASTORE_KEYNAME_GDD_INDEX_PORT					L"Port"
#define SWT_DATASTORE_KEYNAME_GDD_INDEX_USER					L"User"
#define SWT_DATASTORE_KEYNAME_GDD_INDEX_PWD						L"Pwd"
/** @} */
/** @addtogroup Support Support
 * @{
 */
#define SWT_DATASTORE_KEYNAME_GDD_INDEX_THRESHOLD_WARN			L"WarnPathThreshold"			///<Warning threshold of index role path free space, 0.03 by default. 
#define SWT_DATASTORE_KEYNAME_GDD_INDEX_THRESHOLD_ERR			L"ErrorPathThreshold"			///<Error threshold of index role path free space, 100 by default.
/** @} */ 





/** @name DataRole
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore\<DataStoreName>\GDD\DataRole"
 *
 * Description / Format: 
 *
 *           Key:		DataRole
 *			 Comment:   For "WarnPathThreshold" and "ErrorPathThreshold", when the value is smaller than 1, means percentage of total space of the valume;
 *						When the value is equal to or larger than 1, means actual size, in MB.
 *           Value:     "WarnPathThreshold"=REG_SZ:"0.03"
 *					    "ErrorPathThreshold"=REG_SZ:"100"
 */
/** @addtogroup Developer Developer
 * @{
 */
#define SWT_DATASTORE_KEYNAME_GDD_DATA_IP						L"IP"
#define SWT_DATASTORE_KEYNAME_GDD_DATA_PATH						L"Path"
#define SWT_DATASTORE_KEYNAME_GDD_DATA_PORT						L"Port"
#define SWT_DATASTORE_KEYNAME_GDD_DATA_USER						L"User"
#define SWT_DATASTORE_KEYNAME_GDD_DATA_PWD						L"Pwd"
/** @} */
/** @addtogroup Support Support
 * @{
 */
#define SWT_DATASTORE_KEYNAME_GDD_DATA_THRESHOLD_WARN			L"WarnPathThreshold"			///<Warning threshold of data role path free space, 0.03 by default. 
#define SWT_DATASTORE_KEYNAME_GDD_DATA_THRESHOLD_ERR			L"ErrorPathThreshold"			///<Error threshold of data role path free space, 100 by default.
/** @} */ 



/** @name HashRole
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore\<DataStoreName>\GDD\HashRole"
 *
 * Description / Format: 
 *
 *           Key:		HashRole
 *			 Comment:   For "WarnPathThreshold", "ErrorPathThreshold", "WarnMemThreshold" and "ErrorMemThreshold", 
 *						when the value is smaller than 1, means percentage of total space of the valume;
 *						When the value is equal to or larger than 1, means actual size, in MB.
 *           Value:     "WarnPathThreshold"=REG_SZ:"0.03"
 *					    "ErrorPathThreshold"=REG_SZ:"100"
 *						...
 */
/** @addtogroup Developer Developer
 * @{
 */
#define SWT_DATASTORE_KEYNAME_GDD_HASH_HASHROLEMODE				L"HashRoleMode"
#define SWT_DATASTORE_KEYNAME_GDD_HASH_MAXMEMINMB				L"MaxMemInMB"
#define SWT_DATASTORE_KEYNAME_GDD_HASH_SSDSizeInGB				L"SSDSizeInGB"
#define SWT_DATASTORE_KEYNAME_GDD_HASH_IP						L"IP"
#define SWT_DATASTORE_KEYNAME_GDD_HASH_PATH						L"Path"
#define SWT_DATASTORE_KEYNAME_GDD_HASH_PORT						L"Port"
#define SWT_DATASTORE_KEYNAME_GDD_HASH_USER						L"User"
#define SWT_DATASTORE_KEYNAME_GDD_HASH_PWD						L"Pwd"
/** @} */
/** @addtogroup Support Support
 * @{
 */
#define SWT_DATASTORE_KEYNAME_GDD_HASH_PATH_THRESHOLD_WARN		L"WarnPathThreshold"			///<Warning threshold of data role path free space, 0.03 by default. 
#define SWT_DATASTORE_KEYNAME_GDD_HASH_PATH_THRESHOLD_ERR		L"ErrorPathThreshold"			///<Error threshold of data role path free space, 100  by default.
#define SWT_DATASTORE_KEYNAME_GDD_HASH_MEM_THRESHOLD_WARN		L"WarnMemThreshold"				///<Warning threshold of available physical memory, 0.03 by default. 
#define SWT_DATASTORE_KEYNAME_GDD_HASH_MEM_THRESHOLD_ERR		L"ErrorMemThreshold"			///<Error threshold of available physical memory, 10 by default.
/** @} */ 




/** @name Debug
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\DataStore\<DataStoreName>\GDD\Debug"
 *
 * Description / Format: 
 *
 *           Key:		Debug
 *           Value:     "NetworkTimeoutInSec"=REG_DWORD:300
 *						...
 */
/** @addtogroup Support Support
 * @{
 */
#define SWT_DATASTORE_KEYNAME_GDD_DEBUG_NETWORKTIMEOUTINSEC		L"NetworkTimeoutInSec"
#define SWT_DATASTORE_KEYNAME_GDD_DEBUG_HASHKEYCALCTYPE			L"HashKeyCalcType"
#define SWT_DATASTORE_KEYNAME_GDD_DEBUG_COMMLIBTYPE				L"CommlibType"
#define SWT_DATASTORE_KEYNAME_GDD_DEBUG_DATASLICEMAXSIZEINMB	L"DataSliceMaxSizeInMB"
#define SWT_DATASTORE_KEYNAME_GDD_DEBUG_USEMIXEDRECLAIM			L"UseMixedReclaim"
/** @} */ 


/** @name InstantVM
*-------------------------------------------------------------------------
* These switches are defined for "InstantVM" in register table
* ------------------------------------------------------------------------
* Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\InstantVM"
*
* Description / Format:
*
*           Key:		InstantVM
*			Value:      "DebugNoCleanupIfJobFailed"=DWORD:00000001
*           .....
*/
///@{
#define SWT_INSTANTVM_MODULENAME								CST_REG_ROOT_L L"\\InstantVM"
#define SWT_INSTANTVM_KEYNAME_NOCLEANUPIFJOBFAILED				L"DebugNoCleanupIfJobFailed"			///< Debug not to clean up resources if Instant VM job failed until stopping the job manually.
#define SWT_INSTANTVM_ALIVE_TIMEOUT_SECONDS						L"AliveCheckTimeoutSeconds"				///<This value is to set the timeout value of Instant VM Heartbeat check.
///@}
/** @} */


/** @name PurgeNodeDataDll
 *-------------------------------------------------------------------------
 * These switches are defined for module "PurgeNodeDataDll.dll" in register table, only for developer debug usage
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\PurgeNodeDataDll"
 *
 * Description / Format: 
 *
 *           Key:			PurgeNodeDataDll
 *           Value:          "SlowDownPurgeNodeInSecs"=DWORD:00000000 
 */
///@{
#define SWT_PURGENODEDATADLL_MODULENAME									CST_REG_ROOT_L L"\\PurgeNodeDataDll"
#define SWT_PURGENODEDATADLL_KEYNAME_SLOWDOWN_SPEED						L"SlowDownPurgeNodeInSecs"		//The interval time value in secs between two to be purged recovery points.
///@}
/** @} */


///@}
/** @} */

/** @name GDDClient.dll
*-------------------------------------------------------------------------
* These switches are defined for module "GDDClient.dll" in register table, only for developer debug usage
* ------------------------------------------------------------------------
* Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\GDDClient"
*
* Description / Format:
*
*           Key:			GDDClient.dll
*           Value:          "ClientandServerTimeoutInSeconds"=DWORD:5*60*1000
*/
///@{
#define SWT_GDDCLIENT_MODULENAME									CST_REG_ROOT_L L"\\GDDClient"
#define SWT_GDDCLIENT_KEYNAME_TIMEOUT								L"ClientandServerTimeoutInSeconds"			//default 5mins
///@}
/** @} */
/** @addtogroup Support Support
* @{
*/


/** @name AFCopySession
*-------------------------------------------------------------------------
* These switches are defined for module "AFCopySession.dll" in register table
* ------------------------------------------------------------------------
* Location: "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\AFCopySession"
*
* Description / Format:
*
*           Key:		AFCopySess
*           Value:          "CompareWithSession"=DWORD:00000000
*           .....
*           .....
*/
///@{
#define SWT_AFCOPYSESSION_MODULENAME									CST_REG_ROOT_L L"\\AFCopySession"		///<
#define SWT_AFCOPYSESSION_KEYNAME_FAKEREAD           					L"FakeRead" ///<the default value is 0, if the value is non-0, will not read data from the source session
#define SWT_AFCOPYSESSION_KEYNAME_FAKEWRITE           					L"FakeWrite" ///<the default value is 0, if the value is non-0, will not write data into the destination session
#define SWT_AFCOPYSESSION_KEYNAME_CRYPTO_INFO  					        L"CryptoInfo"			    	    ///<Encryption information for encrypting data.
#define SWT_AFCOPYSESSION_KEYNAME_ENCRYPT_BY_SESSPWD				    L"EncBySessPwd"			    	    ///<Encrypt data by session password and ignore data password.
#define SWT_AFCOPYSESSION_KEYNAME_SESSION_PASSWORD					    L"SessionPassword"			    	///<Session password for backup.
#define SWT_AFCOPYSESSION_KEYNAME_DATA_PASSWORD					        L"DataPassword"			    	    ///<Data password for encrypting data.
#define SWT_AFCOPYSESSION_KEYNAME_ISCRPJOBCRASHED                       L"IsCRPJobCrashed"                  ///<mark if job crashed.
#define SWT_AFCOPYSESSION_KEYNAME_COPY_BY_DISK					        L"CopyByDisk"
///@}
/** @} */

/** @name RPSMerge
 *-------------------------------------------------------------------------
 * These switches are defined for "RPSMerge" in register table
 * Which is used by JAVA module defines in FlashSwitchDefine
 * ------------------------------------------------------------------------
 * Location: "HKEY_LOCAL_MACHINE\SOFTWARE\CA\ARCserve Unified Data Protection\Engine\RPSMerge"
 *
 * Description / Format: 
 *
 *           Key:		RPSMerge
 *           Value:     "ASBULockTimeOut"=DWORD:00000001
 *           .....
 *           .....
 */
///@{
#define	SWT_RPS_MERGE_MODULE									CST_REG_ROOT_L L"\\RPSMerge"
#define SWT_RPS_MERGE_MODULE_PAUSE_TIMEOUT						L"ASBULockTimeOut"
///@}
/** @} */