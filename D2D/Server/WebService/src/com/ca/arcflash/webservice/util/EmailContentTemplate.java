package com.ca.arcflash.webservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JJobHistoryFilterCol;
import com.ca.arcflash.jni.common.JJobHistoryResult;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.activitylog.ActivityLog;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.callback.MergeFailureInfo;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.edge.srmagent.SrmAlertMonitor;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.BaseVSphereJob;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;


public class EmailContentTemplate {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
	private static SimpleDateFormat dateFormat1 = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat(), DataFormatUtil.getDateFormatLocale());
	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getDateFormat(), DataFormatUtil.getDateFormatLocale());
	private static final Logger logger = Logger.getLogger(EmailContentTemplate.class);
	
	public final static int Unknown			= -1;
	public final static int Full			= 0;
	public final static int Incremental		= 1;
	public final static int Resync			= 2;
	
	public final static int SCHEDULE_NOW= -1;
	public final static int SCHEDULE_REGULAR= 0;
	public final static int SCHEDULE_DAILY	= 1;
	public final static int SCHEDULE_WEEKLY	= 2;
	public final static int SCHEDULE_MONTHLY= 4;

	
	public static final int Log_Unknown				= -1;
	public static final int Log_Information			= 0;
	public static final int Log_Warning				= 1;
	public static final int Log_Error				= 2;
	
	private static final String Row_Color_Red = "#FF8080";
	private static final String Row_Color_Orange = "#FFC488";
	private static final String Row_Color_Alternate = "#F2F8FF";
	private static final String Row_Color_White = "#FFFFFF";
	private static final String Row_Color_Grey = "#CCCCCC";
	
	

	
	
	/**
	 * 
	 * @param jobStatus
	 * @param jobType
	 * @param jobMethod
	 * @param jobID
	 * @param executionTime
	 * @param destination
	 * @param result
	 * @param URL is for To log on the server to make changes or fix job settings - click here. You can also submit a backup now
	 * @return
	 */
	public static String getHtmlContent(long jobStatus,	long jobType,long jobMethod,long jobID,
			String executionTime,String source,	String destination,	ActivityLogResult result, String URL){
		return getHtmlContent(jobStatus,jobType,jobMethod,SCHEDULE_NOW,jobID,executionTime,source,destination,result,URL);
	}
	
	
	
	/**
	 * 
	 * @param jobStatus
	 * @param jobType
	 * @param jobMethod
	 * @param jobID
	 * @param executionTime
	 * @param destination
	 * @param result
	 * @param URL is for To log on the server to make changes or fix job settings - click here. You can also submit a backup now
	 * @return
	 */
	public static String getHtmlContent(long jobStatus,
			long jobType,
			long jobMethod,
			int scheduleType,
			long jobID,
			String executionTime,
			String source,
			String destination,
			ActivityLogResult result, String URL)
	{
		String template = "";
		try
		{
			logger.debug("getHtmlContent - start");
			
			//HTML format
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType);
			String jobTypeString = null;
			String sourceHead = null;
			String destinationHead = null;
			String scheduleTypeString=EmailContentTemplate.scheduleType2String(scheduleType);
			//wanqi06:deal with catalog on demand when catalog
			if (jobType==Constants.JOBTYPE_CATALOG_FS || jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if(jobType==Constants.JOBTYPE_CATALOG_GRT){
				jobTypeString = WebServiceMessages.getResource("GRTCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_BACKUP){
				jobTypeString = EmailContentTemplate.backupType2String(jobMethod);
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if(jobType == Constants.AF_JOBTYPE_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP)
			{
				jobTypeString = WebServiceMessages.getResource("VSphereBackupJob") + "-" +EmailContentTemplate.backupType2String(jobMethod);	
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_RECOVERY)
			{
				jobTypeString = WebServiceMessages.getResource("RecoveryVMJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_RESTORE)
			{
				jobTypeString = WebServiceMessages.getResource("RestoreJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_COPY)
			{
				jobTypeString = WebServiceMessages.getResource("CopyJob");	
				sourceHead = WebServiceMessages.getResource("EmailCopySrcLocation");
				destinationHead = WebServiceMessages.getResource("EmailCopyDestLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP)
			{
				jobTypeString = WebServiceMessages.getResource("FileCopyJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE)
			{
				jobTypeString = WebServiceMessages.getResource("FileArchiveJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE)
			{
				jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)
			{
				jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC)
			{
				jobTypeString = WebServiceMessages.getResource("ArchiveCatalogReSyncJob")+ " ";
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
				
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_CATALOG_FS || jobType == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
			{
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer htmlTemplate = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(getHTMLHeaderSection());
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<h1>%s</h1>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			
			//(jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP ||
			//jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE || 
			//jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE) && 
			if(jobType == Constants.AF_JOBTYPE_BACKUP){
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			}else
			if(jobType == Constants.AF_JOBTYPE_COPY)
			{
				if(!isRemote(destination)){
					htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");				
				}else{
					htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=\"file://%s\" target=\"_blank\">%s</a></TD></TR>");
				}
			}
			
			if(!isRemote(destination)){
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");				
			}else{
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=\"file://%s\" target=\"_blank\">%s</a></TD></TR>");
			}
			
			htmlTemplate.append("	</TABLE>");
			
			//Activity Log
			htmlTemplate.append("<P/><P/><h1>%s</h1>%s");
			//Alert Email PR
			htmlTemplate.append("<P/><P/>%s");
			htmlTemplate.append("</BODY>");
			htmlTemplate.append("</HTML>");
			
			String activityLogHeader = "";
			String activityLog = "";
			if (jobStatus != Constants.JOBSTATUS_FINISHED/* && jobStatus != Constants.JOBSTATUS_MISSED*/
					&& jobStatus != Constants.BackupJob_PROC_EXIT)
			{
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if (result != null)
					activityLog = convertLogToHtml(result, jobID);
				else 
					logger.debug("getHtmlContent -result was null");
			}
			//Alert Email PR
			
			String clickHere = null;
			if(jobType == Constants.AF_JOBTYPE_COPY)
			{
				if(!isRemote(destination)){
					clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
							WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							sourceHead, source,
							destinationHead, destination,
							activityLogHeader, activityLog,clickHere);
				}else{//network path
					clickHere = WebServiceMessages.getResource("clickhere",URL);
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
							WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							sourceHead, source,source,
							destinationHead, destination,destination,
							activityLogHeader, activityLog,clickHere);
				}
			}
			else if(jobType == Constants.AF_JOBTYPE_BACKUP){
					if(!isRemote(destination)){
						clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
						template = StringUtil.format(htmlTemplate.toString(),
								WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
								WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
								WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
								WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
								WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString, 
								WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
								destinationHead, destination,
								activityLogHeader, activityLog,clickHere);
					}else{//network path
						clickHere = WebServiceMessages.getResource("clickhere",URL);
						template = StringUtil.format(htmlTemplate.toString(),
								WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
								WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
								WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
								WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
								WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString, 
								WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
								destinationHead, destination,destination,
								activityLogHeader, activityLog,clickHere);
					}
			}
			else 		
			{
				if(!isRemote(destination)){
					clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
							WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							destinationHead, destination,
							activityLogHeader, activityLog,clickHere);
				}else{//network path
					clickHere = WebServiceMessages.getResource("clickhere",URL);
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
							WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							destinationHead, destination,destination,
							activityLogHeader, activityLog,clickHere);
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug(template);
		logger.debug("getHtmlContent - end");
		return template;
	}
	
	
	
	/**
	 * 
	 * @param jobStatus
	 * @param jobType
	 * @param jobMethod
	 * @param scheduleType
	 * @param jobID
	 * @param executionTime
	 * @param destination
	 * @param result
	 * @param URL is for To log on the server to make changes or fix job settings - click here. You can also submit a backup now
	 * @return
	 */
	public static String getBackUpHtmlContent(long jobStatus,long jobType,long jobMethod,int scheduleType,long jobID,
			String executionTime,String source,	String destination,	ActivityLogResult result, String URL)
	{
		return getHtmlContent(jobStatus,jobType,jobMethod,scheduleType,jobID,executionTime,source,destination,result,URL);
	}
	
	
	private static String scheduleType2String(int scheduleType) {
		logger.debug("scheduleType2String - start");
		try{
			switch(scheduleType){
			case SCHEDULE_MONTHLY:return WebServiceMessages.getResource("ScheduleTypeMonthly");	
			case SCHEDULE_WEEKLY:return WebServiceMessages.getResource("ScheduleTypeWeekly");
			case SCHEDULE_DAILY:return WebServiceMessages.getResource("ScheduleTypeDaily");
			case SCHEDULE_NOW:return WebServiceMessages.getResource("ScheduleTypeImmediately");
			case SCHEDULE_REGULAR:return WebServiceMessages.getResource("ScheduleTypeRegular");
			default:return WebServiceMessages.getResource("ScheduleTypeRegular");
			}
		}
		catch (Exception e)
		{
			logger.debug("backupType2String - exception");
			logger.debug(e.toString());
			return "";
		}
	}

	public static String getHtmlContentCausedByRPS(long jobStatus,
			long jobType,
			long jobMethod,
			int scheduleType,
			long jobID,
			String executionTime,
			ActivityLogResult result, String URL, String rpsName, String dataStoreName) {
		String template = "";
		try {
			logger.debug("getHtmlContent - start");
			
			//HTML format
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType);
			String jobTypeString = EmailContentTemplate.backupType2String(jobMethod);
			String destinationHead = WebServiceMessages.getResource("EmailDestination");
			String scheduleTypeString = EmailContentTemplate.scheduleType2String(scheduleType);
			
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer htmlTemplate = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(getHTMLHeaderSection());
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<h1>%s</h1>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD colspan=\"2\">%s</TD></TR>");//serverName
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD colspan=\"2\">%s</TD></TR>");//jobStatusString
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD colspan=\"2\">%s</TD></TR>");//jobTypeString
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD colspan=\"2\">%s</TD></TR>");//scheduleTypeString
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD colspan=\"2\">%s</TD></TR>");//executionTime
			htmlTemplate.append("		<TR><TD rowspan=\"2\" BGCOLOR=#DDDDDD><B>%s</B></TD>");	
			htmlTemplate.append("<TD>%s</TD><TD>%s</TD></TR><TR><TD>%s</TD><TD>%s</TD></TR>");		
			
			htmlTemplate.append("	</TABLE>");
			
			//Activity Log
			htmlTemplate.append("<P/><P/><h1>%s</h1>%s");
			//Alert Email PR
			htmlTemplate.append("<P/><P/>%s");
			htmlTemplate.append("</BODY>");
			htmlTemplate.append("</HTML>");
			
			String activityLogHeader = "";
			String activityLog = "";
			if (jobStatus != Constants.JOBSTATUS_FINISHED && jobStatus != Constants.BackupJob_PROC_EXIT) {
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if (result != null)
					activityLog = convertLogToHtml(result, jobID);
				else 
					logger.debug("getHtmlContent -result was null");
			}
			//Alert Email PR
			
			String clickHere = null;
			clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
					
			template = StringUtil.format(htmlTemplate.toString(),
					WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
					WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
					WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
					WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
					WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString, 
					WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
					destinationHead, WebServiceMessages.getResource("EmailRPS"), rpsName,
					WebServiceMessages.getResource("EmailDataStore"), dataStoreName,
					activityLogHeader, activityLog,clickHere);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug(template);
		logger.debug("getHtmlContent - end");
		return template;
	}
	

	/**
	 * 
	 * @param jobStatus
	 * @param jobType
	 * @param jobMethod
	 * @param jobID
	 * @param executionTime
	 * @param destination
	 * @param result
	 * @param URL is for To log on the server to make changes or fix job settings - click here. You can also submit a backup now
	 * @return
	 */
	public static String getVSphereHtmlContent(long jobStatus,
			long jobType,
			long jobMethod,
			int scheduleType,
			long jobID,
			String destination,
			String executionTime,
			VMBackupConfiguration configuration,
			ActivityLogResult result, String URL, 
			long jobSubstatus)
	{
		String template = "";
		logger.info("destination is: " + destination);
		if(destination==null || destination.isEmpty())
			destination = configuration.getBackupVM().getDestination();
		//String destination = configuration.getBackupVM().getDestination();
		String scheduleTypeString=EmailContentTemplate.scheduleType2String(scheduleType);
		BackupEmail email = configuration.getEmail();
		try
		{
			logger.debug("getHtmlContent - start");
			
			//HTML format
			if (!email.isEnableEmailOnRecoveryPointCheckFailure())
				jobSubstatus = 0;
			
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType, jobSubstatus);
			String jobTypeString = null;
			String destinationHead = null;
			
			if (jobType==Constants.JOBTYPE_CATALOG_FS || jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_BACKUP)
			{
				jobTypeString = EmailContentTemplate.backupType2String(jobMethod);
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if(jobType == Constants.AF_JOBTYPE_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP){
				jobTypeString = WebServiceMessages.getResource("VSphereBackupJob") + "-" +EmailContentTemplate.backupType2String(jobMethod);	
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_RECOVERY ||
					 jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_RECOVERY ||
					 jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_RECOVERY)
			{
				jobTypeString = WebServiceMessages.getResource("RecoveryVMJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_RESTORE)
			{
				jobTypeString = WebServiceMessages.getResource("RestoreJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_COPY)
			{
				jobTypeString = WebServiceMessages.getResource("CopyJob");	
				//destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
				destinationHead = WebServiceMessages.getResource("EmailCopyDestLocation");
				
				
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_CATALOG_FS || jobType == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
			{
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer htmlTemplate = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(getHTMLHeaderSection());
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<h1>%s</h1>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");			
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			
			if(jobType == Constants.AF_JOBTYPE_VM_BACKUP ||
						jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
						jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
						jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP){
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			}
			
			if(jobStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND){
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			}
			//(jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP ||
			//jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE || 
			//jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE) && 
			if(!configuration.isD2dOrRPSDestType() && jobType == Constants.AF_JOBTYPE_VM_BACKUP){
				//htmlTemplate.append("<TR><TD rowspan=\"2\" BGCOLOR=#DDDDDD><B>");
				htmlTemplate.append("<TR><TD BGCOLOR=#DDDDDD><B>");
				htmlTemplate.append(destinationHead);
				htmlTemplate.append("</B></TD>");
				htmlTemplate.append("<TD>");
				
				htmlTemplate.append("<TABLE border=\"1\"  class=\"data_table\" CELLPADDING=\"3\" CELLSPACING=\"0\">");
				htmlTemplate.append("<TR><TD>");				
				htmlTemplate.append(WebServiceMessages.getResource("EmailRPS"));
				htmlTemplate.append("</TD><TD>");
				htmlTemplate.append(configuration.getBackupRpsDestSetting().getRpsHost().getRhostname());
				htmlTemplate.append("</TD></TR><TR><TD>");
				htmlTemplate.append(WebServiceMessages.getResource("EmailDataStore"));
				htmlTemplate.append("</TD><TD>");
				htmlTemplate.append(configuration.getBackupRpsDestSetting().getRPSDataStoreDisplayName());
				htmlTemplate.append("</TD></TR>");
				htmlTemplate.append("	</TABLE>");
			}
			else if(!isRemote(destination)){
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");				
			}else{
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=\"file://%s\" target=\"_blank\">%s</a></TD></TR>");
			}
			
			htmlTemplate.append("	</TABLE>");
			
			//Activity Log
			htmlTemplate.append("<P/><P/><h1>%s</h1>%s");
			//Alert Email PR
			htmlTemplate.append("<P/><P/>%s");
			htmlTemplate.append("</BODY>");
			htmlTemplate.append("</HTML>");
			
			String activityLogHeader = "";
			String activityLog = "";
			if ((  (jobStatus != Constants.JOBSTATUS_FINISHED && 
						    jobStatus != Constants.JOBSTATUS_MISSED &&
					        jobStatus != Constants.BackupJob_PROC_EXIT) || 
					       (jobSubstatus == Constants.JOB_SUB_STATUS_SJS_CHECK_RP_FAILED &&
					        jobStatus == Constants.BackupJob_PROC_EXIT)  ) &&
				result !=null)
			{
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if (result != null)
					activityLog = convertLogToHtml(result, jobID);
				else 
					logger.debug("getHtmlContent -result was null");
			}
			//Alert Email PR
			
			String clickHere = null;
			String nodeName = configuration.getBackupVM().getVmHostName();
			if(nodeName == null || nodeName.isEmpty()){
				nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
			}
			
			if(jobType == Constants.AF_JOBTYPE_VM_BACKUP || jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP){
				if(!configuration.isD2dOrRPSDestType()){
					clickHere = WebServiceMessages.getResource("clickhere",URL);
					if(jobStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND){
						template = StringUtil.format(htmlTemplate.toString(),
								WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
								WebServiceMessages.getResource("EmailEsxServerName"), configuration.getBackupVM().getEsxServerName(),
								WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
								WebServiceMessages.getResource("EmailNodeName"), nodeName, 
								WebServiceMessages.getResource("VSphereEmailServerName"), serverName,							
								WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
								WebServiceMessages.getResource("EmailJobType"), jobTypeString,
								WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString,
								WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
								activityLogHeader, activityLog,clickHere);
					}else{
						template = StringUtil.format(htmlTemplate.toString(),
								WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
								WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
								WebServiceMessages.getResource("EmailNodeName"), nodeName, 
								WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
								WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
								WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
								WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString,
								WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
								activityLogHeader, activityLog,clickHere);
					}
					
				}
				else if(!isRemote(destination)){
					clickHere = WebServiceMessages.getResource("clickhere",URL);
					if(jobStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND){
						template = StringUtil.format(htmlTemplate.toString(),
								WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
								WebServiceMessages.getResource("EmailEsxServerName"), configuration.getBackupVM().getEsxServerName(),
								WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
								WebServiceMessages.getResource("EmailNodeName"), nodeName, 
								WebServiceMessages.getResource("VSphereEmailServerName"), serverName,							
								WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
								WebServiceMessages.getResource("EmailJobType"), jobTypeString,
								WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString,
								WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
								destinationHead, destination,
								activityLogHeader, activityLog,clickHere);
					}else{
						template = StringUtil.format(htmlTemplate.toString(),
								WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
								WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
								WebServiceMessages.getResource("EmailNodeName"), nodeName, 
								WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
								WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
								WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
								WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString,
								WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
								destinationHead, destination,
								activityLogHeader, activityLog,clickHere);
					}
					
				}else{//network path
					clickHere = WebServiceMessages.getResource("clickhere",URL);
					if(jobStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND){
						template = StringUtil.format(htmlTemplate.toString(),
								WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
								WebServiceMessages.getResource("EmailEsxServerName"), configuration.getBackupVM().getEsxServerName(),
								WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
								WebServiceMessages.getResource("EmailNodeName"), nodeName,		
								WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
								WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
								WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
								WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString,
								WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
								destinationHead, destination,destination,
								activityLogHeader, activityLog,clickHere);
					}else{
						template = StringUtil.format(htmlTemplate.toString(),
								WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
								WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
								WebServiceMessages.getResource("EmailNodeName"), nodeName,		
								WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
								WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
								WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
								WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString,
								WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
								destinationHead, destination,destination,
								activityLogHeader, activityLog,clickHere);
					}
					
				}
			}
			/*else if(!configuration.isD2dOrRPSDestType()){
				clickHere = WebServiceMessages.getResource("clickhere",URL);
				if(jobStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND){
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
							WebServiceMessages.getResource("EmailEsxServerName"), configuration.getBackupVM().getEsxServerName(),
							WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
							WebServiceMessages.getResource("EmailNodeName"), nodeName, 
							WebServiceMessages.getResource("VSphereEmailServerName"), serverName,							
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString,
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							activityLogHeader, activityLog,clickHere);
				}else{
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
							WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
							WebServiceMessages.getResource("EmailNodeName"), nodeName, 
							WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							activityLogHeader, activityLog,clickHere);
				}
				
			}*/
			else if(!isRemote(destination)){
				clickHere = WebServiceMessages.getResource("clickhere",URL);
				if(jobStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND){
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
							WebServiceMessages.getResource("EmailEsxServerName"), configuration.getBackupVM().getEsxServerName(),
							WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
							WebServiceMessages.getResource("EmailNodeName"), nodeName, 
							WebServiceMessages.getResource("VSphereEmailServerName"), serverName,							
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString,
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							destinationHead, destination,
							activityLogHeader, activityLog,clickHere);
				}else{
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
							WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
							WebServiceMessages.getResource("EmailNodeName"), nodeName, 
							WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							destinationHead, destination,
							activityLogHeader, activityLog,clickHere);
				}
				
			}else{//network path
				clickHere = WebServiceMessages.getResource("clickhere",URL);
				if(jobStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND){
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
							WebServiceMessages.getResource("EmailEsxServerName"), configuration.getBackupVM().getEsxServerName(),
							WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
							WebServiceMessages.getResource("EmailNodeName"), nodeName,		
							WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							destinationHead, destination,destination,
							activityLogHeader, activityLog,clickHere);
				}else{
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
							WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(),
							WebServiceMessages.getResource("EmailNodeName"), nodeName,		
							WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 
							destinationHead, destination,destination,
							activityLogHeader, activityLog,clickHere);
				}
				
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug(template);
		logger.debug("getHtmlContent - end");
		return template;
	}
	
	
	public static String getHtmlContent(JJobMonitor jJobMoniter,long jobID,
			String executionTime,
			String destination,
			ActivityLogResult result, String URL)
	{
		long jobStatus = jJobMoniter.getUlJobStatus();
		long jobType = jJobMoniter.getUlJobType();
		long jobMethod = jJobMoniter.getUlJobMethod();		
		String template = "";
		try
		{
			String archivesize = null;
			long archiveSizeinBytes = jJobMoniter.getUlXferBytesJob();
			archivesize = ServiceUtils.bytes2String(Long.parseLong(archiveSizeinBytes+""));
			logger.debug("getHtmlContent - start");
			
			//HTML format
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType);
			String jobTypeString = null;
			String destinationHead = null;
			
			if (jobType==Constants.JOBTYPE_CATALOG_FS || jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_BACKUP)
			{
				jobTypeString = EmailContentTemplate.backupType2String(jobMethod);
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if(jobType == Constants.AF_JOBTYPE_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP){
				jobTypeString = WebServiceMessages.getResource("VSphereBackupJob") + "-" +EmailContentTemplate.backupType2String(jobMethod);	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_RESTORE)
			{
				jobTypeString = WebServiceMessages.getResource("RestoreJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_COPY)
			{
				jobTypeString = WebServiceMessages.getResource("CopyJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP)
			{
				jobTypeString = WebServiceMessages.getResource("FileCopyJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE)
			{
				jobTypeString = WebServiceMessages.getResource("FileArchiveJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE)
			{
				jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)
			{
				jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC)
			{
				jobTypeString = WebServiceMessages.getResource("ArchiveCatalogReSyncJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
				
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_CATALOG_FS || jobType == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
			{
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer htmlTemplate = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(getHTMLHeaderSection());
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<h1>%s</h1>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			if(jobType != Constants.AF_JOBTYPE_ARCHIVE_PURGE)
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			
			//(jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP ||
			//jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE || 
			//jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE) && 
			if(!isRemote(destination)){
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");				
			}else{
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=\"file://%s\" target=\"_blank\">%s</a></TD></TR>");
			}
			
			htmlTemplate.append("	</TABLE>");
			
			//Activity Log
			htmlTemplate.append("<P/><P/><h1>%s</h1>%s");
			//Alert Email PR
			htmlTemplate.append("<P/><P/>%s");
			htmlTemplate.append("</BODY>");
			htmlTemplate.append("</HTML>");
			
			String activityLogHeader = "";
			String activityLog = "";
			if (jobStatus != Constants.JOBSTATUS_FINISHED && jobStatus != Constants.JOBSTATUS_MISSED 
					&& jobStatus != Constants.BackupJob_PROC_EXIT)
			{
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if (result != null)
					activityLog = convertLogToHtml(result, jobID);
				else 
					logger.debug("getHtmlContent -result was null");
			}
			//Alert Email PR
			
			String clickHere = null;
			if(!isRemote(destination)){
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				if(jobType != Constants.AF_JOBTYPE_ARCHIVE_PURGE)
				{
				template = StringUtil.format(htmlTemplate.toString(),
						WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
						WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
						WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
						WebServiceMessages.getResource("EmailJobType"), jobTypeString, 						
						WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 	
						WebServiceMessages.getResource("EmailBackupSize", jobTypeString), archivesize,
						destinationHead, destination,
						activityLogHeader, activityLog,clickHere);
				}
				else if(jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)
				{
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
							WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 						
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 	
							destinationHead, destination,
							activityLogHeader, activityLog,clickHere);
				}

			}else{//network path
				clickHere = WebServiceMessages.getResource("clickhere",URL);
				if(jobType != Constants.AF_JOBTYPE_ARCHIVE_PURGE)
				{
				template = StringUtil.format(htmlTemplate.toString(),
						WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
						WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
						WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
						WebServiceMessages.getResource("EmailJobType"), jobTypeString, 						
						WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 	
						WebServiceMessages.getResource("EmailBackupSize", jobTypeString), archivesize,
						destinationHead, destination,destination,
						activityLogHeader, activityLog,clickHere);
				}
				else if(jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)
				{
					template = StringUtil.format(htmlTemplate.toString(),
							WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
							WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
							WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
							WebServiceMessages.getResource("EmailJobType"), jobTypeString, 						
							WebServiceMessages.getResource("EmailExecutionTime"), executionTime, 	
							destinationHead, destination,destination,
							activityLogHeader, activityLog,clickHere);
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug(template);
		logger.debug("getHtmlContent - end");
		return template;
	}
	
	protected static Date PublishedDateString2PublishedDate(String source) {
		StringTokenizer token = new StringTokenizer(source, "/");
		Calendar cal = Calendar.getInstance();
		try {
			int month = Integer.parseInt(token.nextToken()) - 1;
			logger.error("month=");
			logger.error(month);
			int date = Integer.parseInt(token.nextToken());
			logger.error("date=");
			logger.error(date);
			int year = Integer.parseInt(token.nextToken());
			logger.error("year=");
			logger.error(year);
			
			/*int hourOfDay = Integer.parseInt(token.nextToken());
			int minute = Integer.parseInt(token.nextToken());
			int second = Integer.parseInt(token.nextToken());*/
			
			cal.set(year, month, date,0,0,0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException("Wrong date time format: "
					+ source + ", " + e.getMessage());
		}
		return cal.getTime();
	}
	
	public static String getPMHtmlContent(String PatchName,String PatchURL,String PatchPublishedDate,String PatchDescription)
	{
		String template = "";
		
		StringBuffer htmlTemplate = new StringBuffer();
		htmlTemplate.append("<HTML>");
		htmlTemplate.append(getHTMLHeaderSection());
		htmlTemplate.append("	<BODY>");
		htmlTemplate.append("	<h1>%s</h1>");
		htmlTemplate.append("   <p/><p/>");
		htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href='%s'>%s</a></TD></TR>");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		//htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate.append("	</TABLE>");
		
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		Date PatchPublishedDate1 = PublishedDateString2PublishedDate(PatchPublishedDate);
		String publishedDateString = dateFormat2.format(PatchPublishedDate1);
		template = StringUtil.format(htmlTemplate.toString(),
				WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()),
				WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
				WebServiceMessages.getResource("PM_Patch"), PatchURL,PatchName,
				WebServiceMessages.getResource("PM_PublishedDate"), publishedDateString, 
				WebServiceMessages.getResource("PM_Description"), PatchDescription 
				);
		return template;
	}
	public static String getPMFailureHtmlContent(String JobName,String JobStatus,String strErrorMessage)
	{
		String template = "";
		
		StringBuffer htmlTemplate = new StringBuffer();
		htmlTemplate.append("<HTML>");
		htmlTemplate.append(getHTMLHeaderSection());
		htmlTemplate.append("	<BODY>");
		htmlTemplate.append("	<h1>%s</h1>");
		htmlTemplate.append("   <p/><p/>");
		htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate.append("	</TABLE>");
		
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		template = StringUtil.format(htmlTemplate.toString(),
				WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()),
				WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()), serverName,
				WebServiceMessages.getResource("PM_JobName"), JobName,
				WebServiceMessages.getResource("PM_JobStatus"), JobStatus, 
				WebServiceMessages.getResource("PM_FailureMessage"), strErrorMessage 
				);
		return template;
	}
	public static String getPMPlainTextContent(String PatchName,String PatchPublishedDate,String PatchDescription)
	{
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		
		StringBuffer plainTemplate = new StringBuffer();
		
		plainTemplate.append(WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()));
		plainTemplate.append(serverName);
		plainTemplate.append("   |   ");
		
		plainTemplate.append(WebServiceMessages.getResource("PM_Patch"));
		plainTemplate.append(PatchName);
		plainTemplate.append("   |   ");
		
		plainTemplate.append(WebServiceMessages.getResource("PM_PublishedDate"));
		Date PatchPublishedDate1 = PublishedDateString2PublishedDate(PatchPublishedDate);
		String publishedDateString = dateFormat2.format(PatchPublishedDate1);
		plainTemplate.append(publishedDateString);
		plainTemplate.append("   |   ");
		
		plainTemplate.append(WebServiceMessages.getResource("PM_Description"));
		plainTemplate.append(PatchDescription);
		plainTemplate.append("   |   ");
		
		return plainTemplate.toString();
	}
	public static String getPMFailurePlainTextContent(String JobName,String JobStatus,String strErrorMessage)
	{
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		
		StringBuffer plainTemplate = new StringBuffer();
		
		plainTemplate.append(WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()));
		plainTemplate.append(serverName);
		plainTemplate.append("   |   ");
		
		plainTemplate.append(WebServiceMessages.getResource("PM_JobName"));
		plainTemplate.append(JobName);
		plainTemplate.append("   |   ");
		
		plainTemplate.append(WebServiceMessages.getResource("PM_JobStatus"));
		plainTemplate.append(JobStatus);
		plainTemplate.append("   |   ");
		
		plainTemplate.append(WebServiceMessages.getResource("PM_FailureMessage"));
		plainTemplate.append(strErrorMessage);
		plainTemplate.append("   |   ");
		
		return plainTemplate.toString();
	}
	public static String convertLogToHtml(ActivityLogResult result, long jobID) {
		logger.debug("convertLogToHtml - start");
		if (result == null || result.getLogs() == null)
		{
			logger.debug("convertLogToHtml - result == null || result.getLogs() == null");
			return "";
		}
		
		logger.debug("result.getLogs() size is " + result.getLogs().length);
		
		StringBuffer html = new StringBuffer();
		html.append("<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\" >");
		html.append("<TR BGCOLOR=#CCCCCC><TD style=\"white-space: nowrap;\"><B>");
		html.append(WebServiceMessages.getResource("Header_Type"));
		html.append("</B></TD><TD style=\"white-space: nowrap;\"><B>");
		html.append(WebServiceMessages.getResource("Header_JobID"));
		html.append("</B></TD><TD><B>");
		html.append(WebServiceMessages.getResource("Header_Time"));
		html.append("</B></TD><TD><B>");
		html.append(WebServiceMessages.getResource("Header_Message"));
		html.append("</B></TD></TR>");
		
		int row = 0;
		for (ActivityLog log : result.getLogs())
		{
			if (log != null)
			{
				try{
					if(log.getJobID() != jobID)
						continue;
					String type = LogEntryTypeToString(log.getType());
					String time = EmailContentTemplate.formatDate(log.getTime());				
					String message = log.getMessage();
					String bgColor = Row_Color_White;
					
					//Alternate row color and highlight error rows
					if (log.getType() == Log_Error)
						bgColor = Row_Color_Red;
					else if (log.getType() == Log_Warning)
						bgColor = Row_Color_Orange;
					else if ((row % 2) == 1)
						bgColor = Row_Color_Alternate;
					
					html.append("<TR BGCOLOR=");
					html.append(bgColor);
					html.append("><TD style=\"white-space: nowrap;\">");
					html.append(type);
					html.append("</TD><TD>");
					if(jobID > 0)
						html.append(jobID);
					else
						html.append("");
					html.append("</TD><TD>");
					html.append(time);
					html.append("</TD><TD>");
					html.append(message);
					html.append("</TD></TR>");
				}
				catch (Exception e)
				{
					logger.debug(e);
				}
				row++;
			}
		}
		html.append("</TABLE>");
		return html.toString();
	}

	public static String getHTMLHeaderSection()
	{
		StringBuffer headerSection = new StringBuffer();
		headerSection.append("<head>");
		headerSection.append("<title></title>");
		headerSection.append("<style type=\"text/css\">");
		
		headerSection.append("body, p, th, td, h1 {	font-family: Verdana, Arial; font-size: 8.5pt; }");
		headerSection.append("h1 { font-size: 14pt;	}");
		headerSection.append(".data_table { border-width: 1px; border-style: solid; border-color: #000000; border-collapse: collapse; }");
		
		headerSection.append("</style>");
		headerSection.append("</head>");
		
		return headerSection.toString();
	}
	
	public static String backupType2String(long backupType){
		logger.debug("backupType2String - start");
		try{
			if (backupType == Full)
				return WebServiceMessages.getResource("BackupTypeFull");			
			else if (backupType == Incremental)
				return WebServiceMessages.getResource("BackupTypeIncremental");
			else if (backupType == Resync)
				return WebServiceMessages.getResource("BackupTypeResync");
			else
				return WebServiceMessages.getResource("BackupTypeUnknown");
		}
		catch (Exception e)
		{
			logger.debug("backupType2String - exception");
			logger.debug(e.toString());
			return "";
		}
	}
	
	public static String jobStatus2String(long backupStatus)
	{
		logger.debug("jobStatus2String - start");		
		
		try{ ///add skipped status fanda03 2012.10.17
			if (backupStatus == Constants.JOBSTATUS_MISSED || backupStatus == Constants.JOBSTATUS_SKIPPED)
				return WebServiceMessages.getResource("BackupStatusMissed");
			else if (backupStatus == Constants.JOBSTATUS_FAILED)
				return WebServiceMessages.getResource("BackupStatusFailed");
			else if (backupStatus == Constants.JOBSTATUS_CANCELLED)
				return WebServiceMessages.getResource("BackupStatusCanceled");
			else if (backupStatus == Constants.JOBSTATUS_CRASH)
				return WebServiceMessages.getResource("BackupStatusCrashed");
			else if (backupStatus == Constants.JOBSTATUS_FINISHED || backupStatus == Constants.BackupJob_PROC_EXIT)
				return WebServiceMessages.getResource("BackupStatusFinished");
			else if(backupStatus == Constants.JOBSTATUS_LICENSE_FAILED || backupStatus == BaseVSphereJob.JOBSTATUS_VSPHERE_LICENSE_FAIL)
				return WebServiceMessages.getResource("BackupStatusLicenseFailed");
			else if(backupStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND)
				return WebServiceMessages.getResource("BackupStatusHostNotFound");
			else
				return WebServiceMessages.getResource("BackupStatusUnknown");
		}
		catch (Exception e)
		{
			logger.error("jobStatus2String",e);
			return "";
		}
	}
	
	public static String jobStatus2StringWithCRP(long backupStatus, long jobSubStatus)
	{
		logger.debug("jobStatus2String - start");		
		
		try{ ///add skipped status fanda03 2012.10.17
			if (backupStatus == Constants.JOBSTATUS_MISSED || backupStatus == Constants.JOBSTATUS_SKIPPED)
				return WebServiceMessages.getResource("BackupStatusMissed");
			else if (backupStatus == Constants.JOBSTATUS_FAILED)
				return WebServiceMessages.getResource("BackupStatusFailed");
			else if (backupStatus == Constants.JOBSTATUS_CANCELLED)
				return WebServiceMessages.getResource("BackupStatusCanceled");
			else if (backupStatus == Constants.JOBSTATUS_CRASH)
				return WebServiceMessages.getResource("BackupStatusCrashed");
			else if (backupStatus == Constants.JOBSTATUS_FINISHED || backupStatus == Constants.BackupJob_PROC_EXIT)
			{
				if (jobSubStatus == 1)
					return WebServiceMessages.getResource("BackupStatusFinishedWithFailedCRP");
				else 					
				    return WebServiceMessages.getResource("BackupStatusFinished");
			}
			else if(backupStatus == Constants.JOBSTATUS_LICENSE_FAILED || backupStatus == BaseVSphereJob.JOBSTATUS_VSPHERE_LICENSE_FAIL)
				return WebServiceMessages.getResource("BackupStatusLicenseFailed");
			else if(backupStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND)
				return WebServiceMessages.getResource("BackupStatusHostNotFound");
			else
				return WebServiceMessages.getResource("BackupStatusUnknown");
		}
		catch (Exception e)
		{
			logger.error("jobStatus2String",e);
			return "";
		}
	}
	
	public static String jobStatus2String(long backupStatus, long jobID, long jobType)
	{		
		try { 
			if (backupStatus != Constants.BackupJob_PROC_EXIT || jobType != JobType.JOBTYPE_RESTORE)
				return jobStatus2String(backupStatus);
			else {
				JJobHistoryFilterCol filter = new JJobHistoryFilterCol();
				filter.setJobID(jobID);
				JJobHistoryResult result = CommonNativeInstance.getICommonNative().getJobHistory(0, 1,
						filter);
				int jobStatus =  result.getJobHistory().get(0).getJobStatus();
				if (jobStatus == Constants.JOBSTATUS_INCOMPLETE) 
					return WebServiceMessages.getResource("BackupStatusIncomplete");
					
				return WebServiceMessages.getResource("BackupStatusFinished");
			}
		}
		catch (Exception e)
		{
			logger.error("jobStatus2String",e);
			return "";
		}
	}
	
	public static String jobStatus2String(long backupStatus, long jobID, long jobType, long jobSubStatus)
	{		
		try { 
			if (backupStatus != Constants.BackupJob_PROC_EXIT || jobType != JobType.JOBTYPE_RESTORE)
				return jobStatus2String(backupStatus);
			else {
				JJobHistoryFilterCol filter = new JJobHistoryFilterCol();
				filter.setJobID(jobID);
				JJobHistoryResult result = CommonNativeInstance.getICommonNative().getJobHistory(0, 1,
						filter);
				int jobStatus =  result.getJobHistory().get(0).getJobStatus();
				if (jobStatus == Constants.JOBSTATUS_INCOMPLETE) 
					return WebServiceMessages.getResource("BackupStatusIncomplete");
				
				if (jobSubStatus == Constants.JOB_SUB_STATUS_SJS_CHECK_RP_FAILED)
				{
					String s = WebServiceMessages.getResource("BackupStatusFinished");
					s += "(" + WebServiceMessages.getResource("CheckRPFailed") + ")";
					return s;					
				}
				else
				{
					return WebServiceMessages.getResource("BackupStatusFinished");
				}
			}
		}
		catch (Exception e)
		{
			logger.error("jobStatus2String",e);
			return "";
		}
	}

	public static String jobType2String(long ulJobType, long ulJobMethod){
		
		String strJobType = "";
		if(ulJobType == Constants.AF_JOBTYPE_BACKUP)
			strJobType = WebServiceMessages.getResource("BackupJob") + " ";
		else if(ulJobType == Constants.AF_JOBTYPE_COPY)
			strJobType = WebServiceMessages.getResource("CopyJob") + " ";
		else if (ulJobType == Constants.AF_JOBTYPE_VM_RECOVERY)
			strJobType = WebServiceMessages.getResource("RecoveryVMJob") +" ";
		else if(ulJobType == Constants.AF_JOBTYPE_RESTORE)
			strJobType = WebServiceMessages.getResource("RestoreJob") + " ";
		else if(ulJobType == Constants.AF_JOBTYPE_VM_BACKUP ||
				ulJobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
				ulJobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP ||
				ulJobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP)
			strJobType = WebServiceMessages.getResource("VSphereBackupJob") + "-" + backupType2String(ulJobMethod)+ " ";				
		else if(ulJobType == Constants.JOBTYPE_CATALOG_FS || ulJobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND || ulJobType == Constants.AF_JOBTYPE_VM_CATALOG_FS || ulJobType == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
			strJobType = WebServiceMessages.getResource("FSCatalogJob") + " ";
		else if(ulJobType == Constants.JOBTYPE_CATALOG_GRT)
			strJobType = WebServiceMessages.getResource("GRTCatalogJob") + " ";
		
		return strJobType;
	}

	/**
	 * 
	 * @param jobStatus
	 * @param jobType
	 * @param jobMethod
	 * @param jobID
	 * @param executionTime
	 * @param destination
	 * @param result
	 * @param URL is for To log on the server to make changes or fix job settings - click here. You can also submit a backup now
	 * @return
	 */
	public static String getPlainTextContent(JJobMonitor jJobMoniter,long jobID,
			String executionTime,
			String destination,
			ActivityLogResult result, String URL)
	{
		logger.debug("getPlainTextContent - start");
		long jobStatus = jJobMoniter.getUlJobStatus();
		long jobType = jJobMoniter.getUlJobType();
		long jobMethod = jJobMoniter.getUlJobMethod();		
		String template = "";
		try {
			String archivesize = null;
			long archiveSizeinBytes = jJobMoniter.getUlXferBytesJob();
			archivesize = ServiceUtils.bytes2String(Long.parseLong(archiveSizeinBytes+""));
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType);
			String jobTypeString = null;
			String clickHere = null;
			String destinationHead = null;
			
			if (jobType==Constants.JOBTYPE_CATALOG_FS || jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_BACKUP)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = EmailContentTemplate.backupType2String(jobMethod);
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_RESTORE)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("RestoreJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_COPY)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("CopyJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP)
			{
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("FileCopyJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE)
			{
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("FileArchiveJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE)
			{
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob");
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)
			{
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC)
			{
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("ArchiveCatalogReSyncJob");
				destinationHead = WebServiceMessages.getResource("EmailArchiveLocation");
				
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_CATALOG_FS || jobType == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer plainTemplate = new StringBuffer();
			
			plainTemplate.append(WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()));
			plainTemplate.append(serverName);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobStatus"));
			plainTemplate.append(jobStatusString);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobType"));
			plainTemplate.append(jobTypeString);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailExecutionTime"));
			plainTemplate.append(executionTime);			
			
			if(archivesize != null && (!(jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)) && (!(jobType == Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC)))
			{
			plainTemplate.append("   |   ");
		    plainTemplate.append(WebServiceMessages.getResource("EmailBackupSize", jobTypeString));
			plainTemplate.append(archivesize);			
			}
			
			if(!(jobType == Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC))
			{
			plainTemplate.append("   |   ");
			plainTemplate.append(destinationHead);
			plainTemplate.append(destination);
			}
			
			String activityLogHeader = "";
			String activityLog = "";
			if (jobStatus != Constants.JOBSTATUS_FINISHED && jobStatus != Constants.JOBSTATUS_MISSED
					&& jobStatus != Constants.BackupJob_PROC_EXIT)
			{
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if(result != null)
					activityLog = convertLogToPlainText(result, jobID);
			}
			plainTemplate.append("\n\n");
			plainTemplate.append(activityLogHeader);
			plainTemplate.append("\n");
			plainTemplate.append(activityLog);
			//Alert Email PR
			plainTemplate.append("\n");
			
			plainTemplate.append(clickHere);
			
			template = plainTemplate.toString();
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		logger.debug(template);
		logger.debug("getPlainTextContent - end");
		return template;
	}
	
	public static String getPlainTextContent(long jobStatus, long jobType,
			long jobMethod,
			long jobID,
			String executionTime,
			String source,
			String destination,
			ActivityLogResult result,String URL)
	{
		
		return getPlainTextContent(jobStatus,jobType,jobMethod,SCHEDULE_NOW,jobID,executionTime,source,destination,result,URL);
	}
	
	
	public static String getPlainTextContent(long jobStatus, long jobType,
			long jobMethod,
			int scheduleType,
			long jobID,
			String executionTime,
			String source,
			String destination,
			ActivityLogResult result,String URL)
	{
		logger.debug("getPlainTextContent - start");
		String template = "";
		try {
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType);
			String jobTypeString = null;
			String clickHere = null;
			String sourceHead = null;
			String destinationHead = null;
			String scheduleTypeString=null;
			if (jobType==Constants.JOBTYPE_CATALOG_FS || jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if(jobType==Constants.JOBTYPE_CATALOG_GRT){
				jobTypeString = WebServiceMessages.getResource("GRTCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_BACKUP){
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = EmailContentTemplate.backupType2String(jobMethod);
				destinationHead = WebServiceMessages.getResource("EmailDestination");
				scheduleTypeString=EmailContentTemplate.scheduleType2String(scheduleType);
			}else if(jobType == Constants.AF_JOBTYPE_VM_BACKUP){
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("VSphereBackupJob") + "-" +EmailContentTemplate.backupType2String(jobMethod);	
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_RECOVERY)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("RecoveryVMJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_RESTORE)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("RestoreJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_COPY)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("CopyJob");	
				sourceHead = WebServiceMessages.getResource("EmailCopySrcLocation");
				destinationHead = WebServiceMessages.getResource("EmailCopyDestLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP)
			{
				//clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",URL);
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("FileCopyJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE)
			{
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("FileArchiveJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE)
			{
				//clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",URL);
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob");
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)
			{
				//clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",URL);
				clickHere = WebServiceMessages.getResource("clickhere4ArchiveSettings",ServiceContext.getInstance().getProductNameD2D(),URL);
				jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_CATALOG_FS || jobType == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
			{
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer plainTemplate = new StringBuffer();
			
			plainTemplate.append(WebServiceMessages.getResource("EmailServerName",ServiceContext.getInstance().getProductNameD2D()));
			plainTemplate.append(serverName);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobStatus"));
			plainTemplate.append(jobStatusString);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobType"));
			plainTemplate.append(jobTypeString);
			plainTemplate.append("   |   ");
			
			if(jobType == Constants.AF_JOBTYPE_BACKUP){
			plainTemplate.append(WebServiceMessages.getResource("EmailScheduleType"));
			plainTemplate.append(scheduleTypeString);
			plainTemplate.append("   |   ");
			}
			plainTemplate.append(WebServiceMessages.getResource("EmailExecutionTime"));
			plainTemplate.append(executionTime);
			plainTemplate.append("   |   ");
			
			if(jobType == Constants.AF_JOBTYPE_COPY)
			{
				plainTemplate.append(sourceHead);
				plainTemplate.append(source);
				plainTemplate.append("   |   ");
			}
			
			plainTemplate.append(destinationHead);
			plainTemplate.append(destination);
			
			String activityLogHeader = "";
			String activityLog = "";
			if (jobStatus != Constants.JOBSTATUS_FINISHED && jobStatus != Constants.JOBSTATUS_MISSED
					&& jobStatus != Constants.BackupJob_PROC_EXIT)
			{
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if(result != null)
					activityLog = convertLogToPlainText(result, jobID);
			}
			plainTemplate.append("\n\n");
			plainTemplate.append(activityLogHeader);
			plainTemplate.append("\n");
			plainTemplate.append(activityLog);
			//Alert Email PR
			plainTemplate.append("\n");
			
			plainTemplate.append(clickHere);
			
			template = plainTemplate.toString();
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		logger.debug(template);
		logger.debug("getPlainTextContent - end");
		return template;
	}
	
	public static String getBackUpPlainTextContent(long jobStatus, long jobType,long jobMethod,	int scheduleType,long jobID,
			String executionTime,String source,	String destination,	ActivityLogResult result,String URL)
	{
		return getPlainTextContent(jobStatus,jobType,jobMethod,scheduleType,jobID,executionTime,source,destination,result,URL);
	}
	
	
	public static String getVSpherePlainTextContent(long jobStatus, long jobType,
			long jobMethod,
			int scheduleType,
			long jobID,
			String destination,
			String executionTime,
			VMBackupConfiguration configuration,
			ActivityLogResult result,String URL, 
			long jobSubStatus)
	{
		logger.debug("getPlainTextContent - start");
		String template = "";
		try {
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType, jobSubStatus);
			String jobTypeString = null;
			String clickHere = null;
			String destinationHead = null;
			String scheduleTypeString=null;
			
			if (jobType==Constants.JOBTYPE_CATALOG_FS || jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if(jobType == Constants.AF_JOBTYPE_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
					jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP){
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("VSphereBackupJob") + "-" +EmailContentTemplate.backupType2String(jobMethod);	
				destinationHead = WebServiceMessages.getResource("EmailDestination");
				scheduleTypeString=EmailContentTemplate.scheduleType2String(scheduleType);
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_RECOVERY ||
					 jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_RECOVERY ||
					 jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_RECOVERY)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("RecoveryVMJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_RESTORE)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("RestoreJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_COPY)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("CopyJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else if (jobType == Constants.AF_JOBTYPE_VM_CATALOG_FS || jobType == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
			{
				clickHere = WebServiceMessages.getResource("clickhere_text",URL);
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			String nodeName = configuration.getBackupVM().getVmHostName();
			if(nodeName == null || nodeName.isEmpty()){
				nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
			}
			StringBuffer plainTemplate = new StringBuffer();
			
			if(jobStatus == BaseVSphereJob.JOBSTATUS_HOST_NOT_FOUND){
				plainTemplate.append(WebServiceMessages.getResource("EmailEsxServerName"));
				plainTemplate.append(configuration.getBackupVM().getEsxServerName());
				plainTemplate.append("   |   ");
			}
			
			plainTemplate.append(WebServiceMessages.getResource("EmailVMName"));
			plainTemplate.append(configuration.getBackupVM().getVmName());
			plainTemplate.append("   |   ");

			plainTemplate.append(WebServiceMessages.getResource("EmailNodeName"));
			plainTemplate.append(nodeName);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("VSphereEmailServerName"));
			plainTemplate.append(serverName);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobStatus"));
			plainTemplate.append(jobStatusString);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobType"));
			plainTemplate.append(jobTypeString);
			plainTemplate.append("   |   ");
			
			if(jobType == Constants.AF_JOBTYPE_VM_BACKUP ||
						jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
						jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
						jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP){
				plainTemplate.append(WebServiceMessages.getResource("EmailScheduleType"));
				plainTemplate.append(scheduleTypeString);
				plainTemplate.append("   |   ");
			}

			plainTemplate.append(WebServiceMessages.getResource("EmailExecutionTime"));
			plainTemplate.append(executionTime);
			plainTemplate.append("   |   ");
						
			plainTemplate.append(destinationHead);
			plainTemplate.append(destination);
			//plainTemplate.append(configuration.getBackupVM().getDestination());
			
			String activityLogHeader = "";
			String activityLog = "";
			if (jobStatus != Constants.JOBSTATUS_FINISHED && jobStatus != Constants.JOBSTATUS_MISSED && result!=null
					&& jobStatus != Constants.BackupJob_PROC_EXIT)
			{
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if(result != null)
					activityLog = convertLogToPlainText(result, jobID);
			}
			plainTemplate.append("\n\n");
			plainTemplate.append(activityLogHeader);
			plainTemplate.append("\n");
			plainTemplate.append(activityLog);
			//Alert Email PR
			plainTemplate.append("\n");
			
			plainTemplate.append(clickHere);
			template = plainTemplate.toString();
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		logger.debug(template);
		logger.debug("getPlainTextContent - end");
		return template;
	}

	
	public static String convertLogToPlainText(ActivityLogResult result,
			long jobID) {
		logger.debug("convertLogToPlainText - start");
		StringBuffer plain = new StringBuffer();
		for (ActivityLog log : result.getLogs())
		{
			if (log != null)
			{
				try{
					if(log.getJobID() != jobID)
						continue;
					String type = LogEntryTypeToString(log.getType());
					String time = EmailContentTemplate.formatDate(log.getTime());				
					String message = log.getMessage();
					plain.append(type);
					plain.append(" | ");
					plain.append(jobID);
					plain.append(" | ");
					plain.append(time);
					plain.append(" | ");
					plain.append(message);
					
				}
				catch (Exception e)
				{
					logger.debug(e);
				}
				plain.append("\n");
			}
		}
		return plain.toString();
	}

	public static String formatDate(Date date){
		return BackupConverterUtil.dateToString(date);
	}
	public static String LogEntryTypeToString(int type)
	{
		if (type == Log_Information)
			return WebServiceMessages.getResource("Log_Information");
		else if (type == Log_Error)
			return WebServiceMessages.getResource("Log_Error");
		else if (type == Log_Warning)
			return WebServiceMessages.getResource("Log_Warning");
		else 
			return "";
	}
	
	public static String getHtmlContent(long jobStatus,
			long jobType,
			long jobMethod,
			long jobID,
			String executionTime,
			String source,
			String destination,
			ActivityLogResult result, String URL,
			String backupSize)
	{
		String template = "";
		try
		{
			logger.debug("getHtmlContent - start");
			
			//HTML format
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType);
			String jobTypeString = null;
			String sourceHead = null;
			String destinationHead = null;
			//wanqi06:deal with catalog on demand
			if (jobType==Constants.JOBTYPE_CATALOG_FS || jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if(jobType==Constants.JOBTYPE_CATALOG_GRT){
				jobTypeString = WebServiceMessages.getResource("GRTCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_BACKUP)
			{
				jobTypeString = EmailContentTemplate.backupType2String(jobMethod);
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_RESTORE)
			{
				jobTypeString = WebServiceMessages.getResource("RestoreJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else 
			{
				jobTypeString = WebServiceMessages.getResource("CopyJob");	
				sourceHead = WebServiceMessages.getResource("EmailCopySrcLocation");
				destinationHead = WebServiceMessages.getResource("EmailCopyDestLocation");
			}
			
			
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer htmlTemplate = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(getHTMLHeaderSection());
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<h1>%s</h1>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			if (jobType == Constants.AF_JOBTYPE_COPY)
			{
				htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=\"file://%s\" target=\"_blank\">%s</a></TD></TR>");
			}
			htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=\"file://%s\" target=\"_blank\">%s</a></TD></TR>");
			htmlTemplate.append("	</TABLE>");
			
			//Activity Log
			htmlTemplate.append("<P/><P/><h1>%s</h1>%s");
			//Alert Email PR
			htmlTemplate.append("<P/><P/>%s");
			htmlTemplate.append("</BODY>");
			htmlTemplate.append("</HTML>");
			
			String activityLogHeader = "";
			String activityLog = "";
			if (jobStatus != Constants.JOBSTATUS_FINISHED && jobStatus != Constants.JOBSTATUS_MISSED
					&& jobStatus != Constants.BackupJob_PROC_EXIT)
			{
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if (result != null)
					activityLog = convertLogToHtml(result, jobID);
				else 
					logger.debug("getHtmlContent -result was null");
			}
			//Alert Email PR
			String clickHere = WebServiceMessages.getResource("clickhere",URL);
			
			if (jobType == Constants.AF_JOBTYPE_COPY)
			{
				template = StringUtil.format(htmlTemplate.toString(),
						WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
						WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()), serverName,
						WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
						WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
						WebServiceMessages.getResource("EmailExecutionTime"), executionTime,
						WebServiceMessages.getResource("EmailBackupSize", jobTypeString), backupSize,
						sourceHead, source, source,
						destinationHead, destination, destination,
						activityLogHeader, activityLog,clickHere);
			}
			else
			{
				template = StringUtil.format(htmlTemplate.toString(),
						WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
						WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()), serverName,
						WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, 
						WebServiceMessages.getResource("EmailJobType"), jobTypeString, 
						WebServiceMessages.getResource("EmailExecutionTime"), executionTime,
						WebServiceMessages.getResource("EmailBackupSize", jobTypeString), backupSize,
						destinationHead, destination, destination,
						activityLogHeader, activityLog,clickHere);
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		logger.debug(template);
		logger.debug("getHtmlContent - end");
		return template;
	}
	
	public static String getPlainTextContent(long jobStatus, long jobType,
			long jobMethod,
			long jobID,
			String executionTime,
			String source,
			String destination,
			ActivityLogResult result,String URL,
			String backupSize)
	{
		logger.debug("getPlainTextContent - start");
		String template = "";
		try {
			String jobStatusString = EmailContentTemplate.jobStatus2String(jobStatus, jobID, jobType);
			String jobTypeString = null;
			String sourceHead = null;
			String destinationHead = null;
			//wanqi06:deal with catalog on demand when catalog
			if (jobType==Constants.JOBTYPE_CATALOG_FS || jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if(jobType==Constants.JOBTYPE_CATALOG_GRT){
				jobTypeString = WebServiceMessages.getResource("GRTCatalogJob");
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_BACKUP)
			{
				jobTypeString = EmailContentTemplate.backupType2String(jobMethod);
				destinationHead = WebServiceMessages.getResource("EmailDestination");
			}
			else if (jobType == Constants.AF_JOBTYPE_RESTORE)
			{
				jobTypeString = WebServiceMessages.getResource("RestoreJob");	
				destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
			}
			else 
			{
				jobTypeString = WebServiceMessages.getResource("CopyJob");
				sourceHead = WebServiceMessages.getResource("EmailCopySrcLocation");
				destinationHead = WebServiceMessages.getResource("EmailCopyDestLocation");
			}
			String serverName = ServiceContext.getInstance().getLocalMachineName();
			
			StringBuffer plainTemplate = new StringBuffer();
			
			plainTemplate.append(WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()));
			plainTemplate.append(serverName);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobStatus"));
			plainTemplate.append(jobStatusString);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailJobType"));
			plainTemplate.append(jobTypeString);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailExecutionTime"));
			plainTemplate.append(executionTime);
			plainTemplate.append("   |   ");
			
			plainTemplate.append(WebServiceMessages.getResource("EmailBackupSize", jobTypeString));
			plainTemplate.append(backupSize);
			plainTemplate.append("   |   ");

			if(jobType == Constants.AF_JOBTYPE_COPY)
			{
				plainTemplate.append(sourceHead);
				plainTemplate.append(source);
				plainTemplate.append("   |   ");
			}
			
			plainTemplate.append(destinationHead);
			plainTemplate.append(destination);
			
			String activityLogHeader = "";
			String activityLog = "";
			if (jobStatus != Constants.JOBSTATUS_FINISHED && jobStatus != Constants.JOBSTATUS_MISSED
					&& jobStatus != Constants.BackupJob_PROC_EXIT)
			{
				activityLogHeader = WebServiceMessages.getResource("ActivityLog");
				if(result != null)
					activityLog = convertLogToPlainText(result, jobID);
			}
			plainTemplate.append("\n\n");
			plainTemplate.append(activityLogHeader);
			plainTemplate.append("\n");
			plainTemplate.append(activityLog);
			//Alert Email PR
			plainTemplate.append("\n");
			String clickHere = WebServiceMessages.getResource("clickhere_text",URL);
			plainTemplate.append(clickHere);
			
			template = plainTemplate.toString();
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
		logger.debug(template);
		logger.debug("getPlainTextContent - end");
		return template;
	}
	
	//archive
	public static boolean isRemote(String inputFolder) {
		return inputFolder != null && inputFolder.startsWith("\\\\");
	}
	
	public static String getPKIHtmlContent(int nAlertType, String msgHeader, int nThreshold, int ncurUtil) {
		String template = "";
		logger.debug("getPKIHtmlContent - start");

		// HTML format
		String serverName = ServiceContext.getInstance().getLocalMachineName();

		StringBuffer htmlTemplate = new StringBuffer();
		htmlTemplate.append("<HTML>");
		htmlTemplate.append(getHTMLHeaderSection());
		htmlTemplate.append("	<BODY>");
		htmlTemplate.append("	<h1>%s</h1>");
		htmlTemplate.append("   <p/><p/>");
		htmlTemplate
				.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		if(nAlertType == SrmAlertMonitor.ALERT_TYPE_DISK){
			htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
			htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		}
		if(nAlertType == SrmAlertMonitor.ALERT_TYPE_NETWORK){
			htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		}
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");

		htmlTemplate.append("	</TABLE>");

		htmlTemplate.append("</BODY>");
		htmlTemplate.append("</HTML>");
		
		String alertType = "";
		String alertThreshold = "";
		String alertUtil = "";
		String alertUtilType = "";
		
		switch (nAlertType) {
		case SrmAlertMonitor.ALERT_TYPE_CPU:
			alertType = WebServiceMessages.getResource("CPUAlert");
			alertThreshold = "" + nThreshold + "%";
			alertUtilType = WebServiceMessages.getResource("CPUUtil");
			alertUtil = "" + ncurUtil + "%";
			break;
		case SrmAlertMonitor.ALERT_TYPE_PHY_MEMORY:
			alertType = WebServiceMessages.getResource("MemoryAlert");
			alertThreshold = "" + nThreshold + "%";
			alertUtilType = WebServiceMessages.getResource("MemoryUtil");
			alertUtil = "" + ncurUtil + "%";
			break;
		case SrmAlertMonitor.ALERT_TYPE_DISK:
			alertType = WebServiceMessages.getResource("DiskAlert");
			alertThreshold = "" + nThreshold + WebServiceMessages.getResource("DiskUnit");
			alertUtilType = WebServiceMessages.getResource("DiskUtil");
			alertUtil = "" + ncurUtil + WebServiceMessages.getResource("DiskUnit");
			break;
		case SrmAlertMonitor.ALERT_TYPE_NETWORK:
			alertType = WebServiceMessages.getResource("NetworkAlert");
			alertThreshold = "" + nThreshold + "%";
			alertUtilType = WebServiceMessages.getResource("NetworkUtil");
			alertUtil = "" + ncurUtil + "%";
			break;
		case SrmAlertMonitor.ALERT_TYPE_PAGE_FILE:
			alertType = WebServiceMessages.getResource("MemoryAlert");
			alertThreshold = "" + nThreshold + "%";
			alertUtilType = WebServiceMessages.getResource("PFMemoryUtil");
			alertUtil = "" + ncurUtil + "%";
			break;
		default:
			logger.error("Wrong Alert type (" + nAlertType + ")");
			return null;
		}
		
		String alertTime = "";
		String alertMsg = "";
		if(msgHeader !=null && !msgHeader.equals("")){
			String[] msg = msgHeader.split(",");
			try {
					Date dt = dateFormat.parse(msg[0]);
					alertTime = dateFormat1.format(dt);
			} catch (ParseException e) {
					logger.debug(e.getMessage());
			}

			if(nAlertType == SrmAlertMonitor.ALERT_TYPE_DISK ){
				if(msg.length == 3){
					alertMsg = msg[2];
					String[] alertMsgArray = alertMsg.trim().split(" ");
					String diskName = WebServiceMessages.getResource("SrmAlertDisk")+" "+alertMsgArray[0];
					String volumeName = "";
					for(int i =1;i< alertMsgArray.length ; i++){
						volumeName += alertMsgArray[i];
					}
					template = StringUtil.format(htmlTemplate.toString(), WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D())+ " - " + alertType,
							alertType+" "+WebServiceMessages.getResource("SrmAlertTime"), alertTime,WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()),serverName,WebServiceMessages.getResource("SrmAlertDiskName"),diskName,WebServiceMessages.getResource("SrmAlertVolume"),volumeName, WebServiceMessages.getResource("Threshold"), alertThreshold, alertUtilType, alertUtil);
				}
			}
			else if(nAlertType == SrmAlertMonitor.ALERT_TYPE_NETWORK){
				if(msg.length == 3){
					alertMsg = msg[2];
					template = StringUtil.format(htmlTemplate.toString(), WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D())+ " - " + alertType,
							alertType+" "+WebServiceMessages.getResource("SrmAlertTime"), alertTime,WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()),serverName,WebServiceMessages.getResource("SrmAlertNetworkCardName"),alertMsg, WebServiceMessages.getResource("Threshold"), alertThreshold, alertUtilType, alertUtil);
				}
			}else{
				template = StringUtil.format(htmlTemplate.toString(), WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D())+ " - " + alertType,
						alertType+" "+WebServiceMessages.getResource("SrmAlertTime"), alertTime,WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()),serverName, WebServiceMessages.getResource("Threshold"), alertThreshold, alertUtilType, alertUtil);
			}
		}else{
			template = StringUtil.format(htmlTemplate.toString(), WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D())+ " - " + alertType,
					alertType+" "+WebServiceMessages.getResource("SrmAlertTime"), alertTime,WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()),serverName, WebServiceMessages.getResource("Threshold"), alertThreshold, alertUtilType, alertUtil);

		}
		
		logger.debug(template);
		logger.debug("getPKIHtmlContent - end");
		return template;
	}
	
	public static class Content {
		String label;
		String value;
		boolean isLink;
		String rpsName;
		public Content(String label, String value, boolean isLink) {
			this.label = label;
			this.value = value;
			this.isLink = isLink;
		}
		public Content(String label, String value, boolean isLink, String rpsName) {
			this.label = label;
			this.value = value;
			this.isLink = isLink;
			this.rpsName = rpsName;
		}
	}
	
	public static String getContent(EmailContentContext context) {
		List<Content> contents = new ArrayList<Content>();
		
		String jobStatusString = EmailContentTemplate.jobStatus2String(context.jobStatus, context.jobID, context.jobType);
		String jobTypeString = null;
		String sourceHead = null;
		String destinationHead = null;
		String scheduleTypeString=EmailContentTemplate.scheduleType2String(context.jobScheduleType);
		//wanqi06:deal with catalog on demand when catalog
		if (context.jobType==Constants.JOBTYPE_CATALOG_FS || context.jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND){
			jobTypeString=WebServiceMessages.getResource("FSCatalogJob");
			destinationHead = WebServiceMessages.getResource("EmailDestination");
		}else if (context.jobType==Constants.JOBTYPE_CATALOG_GRT){
			jobTypeString=WebServiceMessages.getResource("GRTCatalogJob");
			destinationHead = WebServiceMessages.getResource("EmailDestination");
		}
		else if (context.jobType == Constants.AF_JOBTYPE_BACKUP)
		{
			jobTypeString = EmailContentTemplate.backupType2String(context.jobMethod);
			destinationHead = WebServiceMessages.getResource("EmailDestination");
		}
		else if (context.jobType == Constants.AF_JOBTYPE_RESTORE)
		{
			jobTypeString = WebServiceMessages.getResource("RestoreJob");	
			destinationHead = WebServiceMessages.getResource("EmailBackupLocation");
		}
		else 
		{
			jobTypeString = WebServiceMessages.getResource("CopyJob");	
			sourceHead = WebServiceMessages.getResource("EmailCopySrcLocation");
			destinationHead = WebServiceMessages.getResource("EmailCopyDestLocation");
		}
		
		String serverNameLabel = WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D());
		contents.add(new Content(serverNameLabel, ServiceContext.getInstance().getLocalMachineName(), false));
		contents.add(new Content(WebServiceMessages.getResource("EmailJobStatus"), jobStatusString, false));
		contents.add(new Content(WebServiceMessages.getResource("EmailJobType"), jobTypeString, false));
		if (context.jobType == Constants.AF_JOBTYPE_BACKUP)
			contents.add(new Content(WebServiceMessages.getResource("EmailScheduleType"), scheduleTypeString, false));//ADD
		contents.add(new Content(WebServiceMessages.getResource("EmailExecutionTime"), context.executionTime, false));
		//wanqi06:deal with catalog on demand when catalog
		if(context.jobType == Constants.JOBTYPE_CATALOG_FS || context.jobType == Constants.JOBTYPE_CATALOG_GRT || context.jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND) {
			Date time = BackupConverterUtil.string2Date(context.backupStartTime);
			contents.add(new Content(WebServiceMessages.getResource("BackupStartTime"), BackupConverterUtil.dateToString(time), false));
		}
		
		if(context.backupSize != null) {
			contents.add(new Content(WebServiceMessages.getResource("EmailBackupSize", jobTypeString), context.backupSize, false));
		}
		
		if(context.jobType == Constants.AF_JOBTYPE_COPY) {
			boolean link;
			if (context.rpsName == null || context.rpsName.equals(""))
				link = true;
			else
				link = false;
			contents.add(new Content(sourceHead, context.source, link, context.rpsName));
		}
		
		contents.add(new Content(destinationHead, context.destination, context.isLink, context.rpsName));
		String activityLogHeader = "";
		String activityLog = "";
		if (context.jobStatus != Constants.JOBSTATUS_FINISHED && context.jobStatus != Constants.JOBSTATUS_MISSED
				&& context.jobStatus != Constants.BackupJob_PROC_EXIT)
		{
			activityLogHeader = WebServiceMessages.getResource("ActivityLog");
			if(context.result != null)
				if(context.enableHtml)
					activityLog = convertLogToHtml(context.result, context.jobID);
				else
					activityLog = convertLogToPlainText(context.result, context.jobID);
		}
		
		return formatContent(contents, activityLog, activityLogHeader, context.URL, context.enableHtml);
	}
	
	public static String formatContent(List<Content> contents, String activityLog, String activityLogHeader, String URL, boolean enableHtml) {
		StringBuffer template = new StringBuffer();
		if(enableHtml) {
			template.append("<HTML>");
			template.append(getHTMLHeaderSection());
			template.append("	<BODY>");
			template.append("	<h1>");
			template.append(WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()));
			template.append("</h1>");
			template.append("   <p/><p/>");
			template.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");

			for(Content content : contents) {
				if ((content.label.equals(WebServiceMessages.getResource("EmailDestination")) && !content.isLink)
						|| (content.label.equals(WebServiceMessages.getResource("EmailCopySrcLocation")) && !content.isLink))
				    template.append("<TR><TD rowspan=\"2\" BGCOLOR=#DDDDDD><B>");
				else
					template.append("<TR><TD BGCOLOR=#DDDDDD><B>");
				template.append(content.label);
				template.append("</B></TD>");
				
				if (content.label.equals(WebServiceMessages.getResource("EmailDestination"))
						|| content.label.equals(WebServiceMessages.getResource("EmailCopySrcLocation"))) {
					if (content.isLink) {
						template.append("<TD colspan=\"2\">");
						template.append("<a href=\"file://");
						template.append(content.value);
						template.append("\" target=\"_blank\">");
						template.append(content.value);
						template.append("</a></TD></TR>");
					} else {
						template.append("<TD>");
						template.append(WebServiceMessages.getResource("EmailRPS"));
						template.append("</TD><TD>");
						template.append(content.rpsName);
						template.append("</TD></TR><TR><TD>");
						template.append(WebServiceMessages.getResource("EmailDataStore"));
						template.append("</TD><TD>");
						template.append(content.value);
						template.append("</TD></TR>");
					}
				} else {
					template.append("<TD colspan=\"2\">");
					template.append(content.value);
					template.append("</TD></TR>");
				}
			}
			template.append("</TABLE>");
			String clickHere = WebServiceMessages.getResource("clickhere_text",URL);
			//Activity Log
			template.append("<P/><P/><h1>");
			template.append(activityLogHeader);
			template.append("</h1>");
			template.append(activityLog);
			//Alert Email PR
			template.append("<P/><P/>");
			template.append(clickHere);
			template.append("</BODY>");
			template.append("</HTML>");
		}else {
			for(Content content : contents) {
				if(!template.toString().isEmpty()) {
					template.append("   |   ");
				}
				if ((content.label.equals(WebServiceMessages.getResource("EmailDestination")) && !content.isLink)
						|| (content.label.equals(WebServiceMessages.getResource("EmailCopySrcLocation")) && !content.isLink)) {
					template.append(content.label);
					template.append(WebServiceMessages.getResource("EmailRPS"));
					template.append(content.rpsName);
					template.append(";");
					template.append(WebServiceMessages.getResource("EmailDataStore"));
					template.append(content.value);
				} else {
				    template.append(content.label);
				    template.append(content.value);
				}
			}
			template.append("\n\n");
			template.append(activityLogHeader);
			template.append("\n");
			template.append(activityLog);
			//Alert Email PR
			template.append("\n");
			String clickHere = WebServiceMessages.getResource("clickhere_text",URL);
			template.append(clickHere);
		}
		
		return template.toString();
	}
	
	private static String getMergeFailureJobTypeString(MergeFailureInfo info)
	{
		String jobTypeString = null;
		
		if (info != null)
		{
			switch ((int) info.getMergeSource())
			{
			case MergeFailureInfo.MERGE_SOURCE_BACKUP:
				jobTypeString = WebServiceMessages.getResource("BackupJob");
				break;
			case MergeFailureInfo.MERGE_SOURCE_CATALOG:
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				break;
			case MergeFailureInfo.MERGE_SOURCE_BACKUP_VM:
				jobTypeString = WebServiceMessages.getResource("VSphereBackupJob");
				break;
			case MergeFailureInfo.MERGE_SOURCE_CATALOG_VM:
				jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
				break;
			default:
				jobTypeString = WebServiceMessages.getResource("BackupTypeUnknown");
				break;
			}
		}
		
		return jobTypeString;
	}
	public static String getContentOfMergeFailure(MergeFailureInfo info, String destination, String executionTime, ActivityLogResult logs, String url, boolean isEnableHTML) {
		List<Content> contents = new ArrayList<Content>();
		
		// server name
		String serverNameLabel = WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D());		
		contents.add(new Content(serverNameLabel, ServiceContext.getInstance().getLocalMachineName(), false));
		
		// job type
		contents.add(new Content(WebServiceMessages.getResource("EmailJobType"), getMergeFailureJobTypeString(info), false));
		
		// merge status
		contents.add(new Content(WebServiceMessages.getResource("mergeFailureStatus"), WebServiceMessages.getResource("mergeFailureStatusDescription"), false));
		
		// sessions to merge
		contents.add(new Content(WebServiceMessages.getResource("mergeFailureSessionsToMerge"), 
				info.getFailedStartSession() + ", " + info.getFailedEndSession(), false));
		
		if (executionTime != null && !executionTime.isEmpty())
		{
			contents.add(new Content(WebServiceMessages.getResource("EmailExecutionTime"), executionTime, false));
		}
				
		// trouble shooting suggestions
		contents.add(new Content(WebServiceMessages.getResource("mergeFailureTroubleShooting"), WebServiceMessages.getResource("mergeFailureTroubleShootingDescription"), false));
				
		// destination
		if (destination != null && !destination.isEmpty())
		{
			String destinationHead = WebServiceMessages.getResource("EmailDestination");
			contents.add(new Content(destinationHead, destination, true));
		}
		
		
		String activityLogHeader = WebServiceMessages.getResource("ActivityLog");
		String activityLog = "";

		if(logs != null)
		{
			if(isEnableHTML)
				activityLog = convertLogToHtml(logs, info.getJobID());
			else
				activityLog = convertLogToPlainText(logs, info.getJobID());
		}

		return formatContentOfMergeFailure(contents, activityLog, activityLogHeader, url, isEnableHTML);
	}
	
	private static String formatContentOfMergeFailure(List<Content> contents, String activityLog, String activityLogHeader, String URL, boolean enableHtml) {
		StringBuffer template = new StringBuffer();
		if(enableHtml) {
			template.append("<HTML>");
			template.append(getHTMLHeaderSection());
			template.append("	<BODY>");
			template.append("	<h1>");
			template.append(WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()));
			template.append("</h1>");
			template.append("   <p/><p/>");
			template.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");

			for(Content content : contents) {
				template.append("<TR><TD BGCOLOR=#DDDDDD><B>");
				template.append(content.label);
				template.append("</B></TD><TD>");				
				if(content.isLink) {
					template.append("<a href=\"file://");
					template.append(content.value);
					template.append("\" target=\"_blank\">");
					template.append(content.value);
					template.append("</a></TD></TR>");
				}else {
					template.append(content.value);
					template.append("</TD></TR>");
				}
			}
			template.append("</TABLE>");
			String clickHere = WebServiceMessages.getResource("mergeFailureClickhere_text",URL);
			//Activity Log
			template.append("<P/><P/><h1>");
			template.append(activityLogHeader);
			template.append("</h1>");
			template.append(activityLog);
			//Alert Email PR
			template.append("<P/><P/>");
			template.append(clickHere);
			template.append("</BODY>");
			template.append("</HTML>");
		}else {
			for(Content content : contents) {
				if(!template.toString().isEmpty()) {
					template.append("   |   ");
				}
				template.append(content.label);
				template.append(content.value);
			}
			template.append("\n\n");
			template.append(activityLogHeader);
			template.append("\n");
			template.append(activityLog);
			//Alert Email PR
			template.append("\n");
			String clickHere = WebServiceMessages.getResource("mergeFailureClickhere_text",URL);
			template.append(clickHere);
		}
		
		return template.toString();
	}
	
	public static String getContentOfMergeFailureVSphere(MergeFailureInfo info, 
			VMBackupConfiguration configuration, ActivityLogResult logs, String url, boolean isEnableHTML) {
		
		List<Content> contents = new ArrayList<Content>();
		
		// vm name
		contents.add(new Content(WebServiceMessages.getResource("EmailVMName"), configuration.getBackupVM().getVmName(), false));
		
		// node name
		String nodeName = configuration.getBackupVM().getVmHostName();
		if (nodeName == null || nodeName.isEmpty())
		{
			nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
		}
		contents.add(new Content(WebServiceMessages.getResource("EmailVMName"), nodeName, false));
		
		// server name
		contents.add(new Content(WebServiceMessages.getResource("VSphereEmailServerName"), ServiceContext.getInstance().getLocalMachineName(), false));
		
		// job type
		contents.add(new Content(WebServiceMessages.getResource("EmailJobType"), getMergeFailureJobTypeString(info), false));
		
		// merge status
		contents.add(new Content(WebServiceMessages.getResource("mergeFailureStatus"), WebServiceMessages.getResource("mergeFailureStatusDescription"), false));
		
		// sessions to merge
		contents.add(new Content(WebServiceMessages.getResource("mergeFailureSessionsToMerge"), 
				info.getFailedStartSession() + ", " + info.getFailedEndSession(), false));
		
		
		// trouble shooting suggestions
		contents.add(new Content(WebServiceMessages.getResource("mergeFailureTroubleShooting"), WebServiceMessages.getResource("mergeFailureTroubleShootingDescription"), false));
				
		// destination
		contents.add(new Content(WebServiceMessages.getResource("EmailDestination"), configuration.getBackupVM().getDestination(), true));		
		
		String activityLogHeader = WebServiceMessages.getResource("ActivityLog");
		String activityLog = "";
		
		if(logs != null)
		{
			if(isEnableHTML)
				activityLog = convertLogToHtml(logs, info.getJobID());
			else
				activityLog = convertLogToPlainText(logs, info.getJobID());
		}

		return formatContentOfMergeFailureVSphere(contents, activityLog, activityLogHeader, url, isEnableHTML);
	}
	
	private static String formatContentOfMergeFailureVSphere(List<Content> contents, String activityLog, String activityLogHeader, String URL, boolean enableHtml) {
		StringBuffer template = new StringBuffer();
		if(enableHtml) {
			template.append("<HTML>");
			template.append(getHTMLHeaderSection());
			template.append("	<BODY>");
			template.append("	<h1>");
			template.append(WebServiceMessages.getResource("VSphereAlertEmailTitle"));
			template.append("</h1>");
			template.append("   <p/><p/>");
			template.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");

			for(Content content : contents) {
				template.append("<TR><TD BGCOLOR=#DDDDDD><B>");
				template.append(content.label);
				template.append("</B></TD><TD>");				
				if(content.isLink) {
					template.append("<a href=\"file://");
					template.append(content.value);
					template.append("\" target=\"_blank\">");
					template.append(content.value);
					template.append("</a></TD></TR>");
				}else {
					template.append(content.value);
					template.append("</TD></TR>");
				}
			}
			template.append("</TABLE>");
			String clickHere = WebServiceMessages.getResource("mergeFailureClickhere_text",URL);
			//Activity Log
			template.append("<P/><P/><h1>");
			template.append(activityLogHeader);
			template.append("</h1>");
			template.append(activityLog);
			//Alert Email PR
			template.append("<P/><P/>");
			template.append(clickHere);
			template.append("</BODY>");
			template.append("</HTML>");
		}else {
			for(Content content : contents) {
				if(!template.toString().isEmpty()) {
					template.append("   |   ");
				}
				template.append(content.label);
				template.append(content.value);
			}
			template.append("\n\n");
			template.append(activityLogHeader);
			template.append("\n");
			template.append(activityLog);
			//Alert Email PR
			template.append("\n");
			String clickHere = WebServiceMessages.getResource("mergeFailureClickhere_text",URL);
			template.append(clickHere);
		}
		
		return template.toString();
	}
}
