package com.ca.arcserve.edge.app.base.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogJobType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
/**
 * 
 * @author fanda03
 * the message fetch method used in this class is not quite right and tend to issues!!  after discuss with Eric, we use this temp solution. 
 * unless we can move the message file from UI project to Contract project and refer same Message class using Dynamic Proxy, we have no other better solution,
 *
 */
public class ActivityLogMsgUtil {
	private static String dayFormatLastString = "%s %d %s";
	private static String dayFormatSinceString = "%s %s";
	/** 
	 * 
	 * @param severity
	 * @param usage=0 means used in filter; = 1 means used in data grid
	 * @return
	 */
	public static String getSeverityString( Severity severity, int usage ){
		String msg ="";
		switch ( severity ) { 
		case All:
			msg = MessageReader.getCMUILogConst("all");
			break;
		case Information:
			msg = MessageReader.getCMUILogConst("information");
			break;
		case ErrorAndWarning:
			msg = MessageReader.getCMUILogConst("errorsAndWarnings");
			break;
		case Warning:
			msg = MessageReader.getCMUILogConst("warning");
			break;
		case Error:
			msg = MessageReader.getCMUILogConst("error");
			break;
		default:
		}
		return msg;
	}
	private static final Map<LogJobType, String> jobTypeStringMap = new HashMap<LogJobType, String>();
	static {
		jobTypeStringMap.put(LogJobType.All, 	MessageReader.getCMUILogConst( "all" ) );
		jobTypeStringMap.put(LogJobType.Backup, MessageReader.getCMUILogConst( "backupType") );
		jobTypeStringMap.put(LogJobType.Replication, MessageReader.getCMUILogConst( "replicationType"));
		jobTypeStringMap.put(LogJobType.Restore, MessageReader.getCMUILogConst( "restoreType" ) );
		jobTypeStringMap.put(LogJobType.Merge, MessageReader.getCMUILogConst("mergeType" ));
		jobTypeStringMap.put(LogJobType.VSB, MessageReader.getFlashUIConst("virtualStandyNameTranslate") );
		jobTypeStringMap.put(LogJobType.Catalog, MessageReader.getCMUILogConst( "catalogType") );
		jobTypeStringMap.put( LogJobType.CopyRecoveryPoint, MessageReader.getCMUILogConst("copyRecoveryPointType") );
		jobTypeStringMap.put(LogJobType.FileArchive, MessageReader.getCMUILogConst("filearchiveType") );
		jobTypeStringMap.put(LogJobType.FileCopy, MessageReader.getCMUILogConst("filecopyType") );
		jobTypeStringMap.put(LogJobType.Other, MessageReader.getCMUILogConst( "otherType" )); 
	}
	
	public static String getJobTypeFilterString( LogJobType jobTypeInFilter  ) {
		return jobTypeStringMap.get(jobTypeInFilter);
	}
	public static String getJobTypeString( int jobType ) {

		String jobTypeString = null;
		
		if (jobType==JobType.JOBTYPE_CATALOG_FS 
				|| jobType == JobType.JOBTYPE_CATALOG_FS_ONDEMAND
				|| jobType == JobType.JOBTYPE_VM_CATALOG_FS
				|| jobType == JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND){
			jobTypeString = MessageReader.getFlashUIConst( "jobMonitorTypeCatalogFS" );
		}
		else if(jobType==JobType.JOBTYPE_CATALOG_GRT){
			jobTypeString =  MessageReader.getFlashUIConst( "jobMonitorTypeGRTCatalog" );
		}
		else if (jobType == JobType.JOBTYPE_BACKUP
				|| jobType == JobType.JOBTYPE_VM_BACKUP){
				jobTypeString = MessageReader.getFlashUIConst( "backup");
		}
		else if (jobType == JobType.JOBTYPE_VM_RECOVERY)
		{
			jobTypeString = MessageReader.getFlashUIConst("recoveryVMJob");
		}
		else if (jobType == JobType.JOBTYPE_RESTORE)
		{
			jobTypeString =  MessageReader.getFlashUIConst("jobMonitorTypeRestore");
		}
		else if (jobType == JobType.JOBTYPE_COPY)
		{
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorTypeCopy");
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_BACKUP)
		{
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorArchive");
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_SOURCEDELETE)
		{
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorFileArchive");
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_RESTORE)
		{
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorArchiveRestore");
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_PURGE)
		{
			jobTypeString = MessageReader.getFlashUIConst("archivePurgeJob");
		}
		else if (jobType == JobType.JOBTYPE_FILECOPY_CATALOGSYNC)
		{
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorTypeArchiveCatalogSync");
		} else if (jobType == JobType.JOBTYPE_RPS_MERGE || jobType == JobType.JOBTYPE_MERGE || jobType == JobType.JOBTYPE_VM_MERGE) {
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorArchiveMerge");
		} else if (jobType == JobType.JOBTYPE_RPS_REPLICATE) {
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorArchiveReplicate");
		} else if (jobType == JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND) {
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorArchiveInReplicate");
		} else if (jobType == JobType.JOBTYPE_RPS_DATA_SEEDING) {
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorArchiveDataSeeding");
		} else if (jobType == JobType.JOBTYPE_RPS_DATA_SEEDING_IN) {
			jobTypeString = MessageReader.getFlashUIConst("jobMonitorArchiveDataSeedingIn");
		} else if (jobType == JobType.JOBTYPE_RPS_CONVERSION) {
			jobTypeString = MessageReader.getFlashUIConst("virtualStandyNameTranslate");
		} else if (jobType == JobType.JOBTYPE_BMR){
			jobTypeString = MessageReader.getFlashUIConst("BMRJob");
		} else if (jobType == JobType.JOBTYPE_CONVERSION || jobType == JobType.JOBTYPE_RPS_CONVERSION) {
			jobTypeString = MessageReader.getFlashUIConst("virtualStandyNameTranslate");
		}
		return jobTypeString;
	}
	public static String getTimeFilterValue( BaseFilter filter,  SimpleDateFormat timeFormatter  ){
		if (filter.getType() == 1) {
			return MessageReader.getCMUIDashboardConst("mostRecentRun");
		} else if (filter.getType() == 4) {
			return MessageReader.getCMUILogConst("all");
		} else {
			return getMinuteHourDayName(filter, timeFormatter);
		}
	}

	private static String getMinuteHourDayName( BaseFilter filter, SimpleDateFormat timeFormatter ) {
		String mhdName ="";
		if (filter.getType() == 2) {
			mhdName = String.format( dayFormatLastString, MessageReader.getCMUIDashboardConst("last"),  filter.getAmount(), getMinuteHourDayName(filter.getUnit())   );

		} else {
			mhdName = String.format( dayFormatSinceString, MessageReader.getCMUIDashboardConst("since"), timeFormatter.format( filter.getTimeStamp() ) );
		}
		return mhdName;
	}
	
	private static String getMinuteHourDayName(int unit) {
		if (unit == 1) {
			return MessageReader.getCMUIDashboardConst("minutes");
		} else if(unit == 2){
			return MessageReader.getCMUIDashboardConst("hours");
		} else {
			return MessageReader.getCMUIDashboardConst("days");
		}
	}
	
	
	
	
}
