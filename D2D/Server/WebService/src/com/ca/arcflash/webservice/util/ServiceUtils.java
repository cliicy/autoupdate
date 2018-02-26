package com.ca.arcflash.webservice.util;

import java.io.Closeable;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.scheduler.CatalogJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.CopyService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VMCopyService;
import com.ca.arcflash.webservice.service.VSPhereCatalogService;

public class ServiceUtils {
	private static DecimalFormat format = new DecimalFormat("#.00");
	private static Logger logger = Logger.getLogger(ServiceUtils.class);
	private static String dateTimeFormat 
		= CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat();
	private static Map<String, IJobDependency> jobDependencies 
				= new HashMap<String, IJobDependency>();
	
	static {
		jobDependencies.put(IJobDependency.CATALOG_JOB, CatalogService.getInstance());
		jobDependencies.put(IJobDependency.COPY_JOB, CopyService.getInstance());
		jobDependencies.put(IJobDependency.FILECOPY_JOB, ArchiveService.getInstance());
		jobDependencies.put(IJobDependency.CONVERSION_JOB, HAService.getInstance());
		jobDependencies.put(IJobDependency.VSPHERE_CATALOG_JOB, VSPhereCatalogService.getInstance());
		jobDependencies.put(IJobDependency.VSPHERE_COPY_JOB, VMCopyService.getInstance());
	}
	
	public static String bytes2String(long bytes){	
		if (bytes <1024)
			return bytes + "bytes";
		else if (bytes<(1024*1024)) {
			String kb = format.format((double)bytes/1024);
			if(kb.startsWith("1024"))
				return "1MB";
			
			return kb + "KB";
		}
		else if (bytes<(1024*1024*1024)) {
			String mb = format.format(((double)bytes)/(1024*1024));
			if(mb.startsWith("1024"))
				return "1GB";
			
			return mb + "MB";
		}
		else
			return format.format(((double)bytes)/(1024*1024*1024)) + "GB";
	}
	
	public static void closeResource(Closeable resource) {
		try {
			if(resource != null)
				resource.close();
		}catch(Throwable t){};
	}
	
	public static String getKey(long jobID, JJobMonitor jJM) {
		if(jJM.getUlJobType() == Constants.JOBTYPE_CATALOG_GRT)
			return jobID + "_" + jJM.getUlJobType();
		else
			return String.valueOf(jJM.getUlJobType());
	} 
	
	public static String getKey(JobMonitor jm) {
		if(jm.getJobType() == Constants.JOBTYPE_CATALOG_GRT)
			return jm.getJobId() + "_" + jm.getJobType();
		else
			return String.valueOf(jm.getJobType());
	}
	
	public static long getServerTimeZoneOffsetByDate(Date date) {
		if(date == null)
			return 0;
		TimeZone tz = Calendar.getInstance().getTimeZone();
		int offset = tz.getOffset(date.getTime());
		return offset;
	}
	
	public static boolean is24Hours()
	{
		String fmt = dateTimeFormat;
		if( fmt.indexOf('H') > -1 || fmt.indexOf('k') > -1 )
			return true;
		return false;
	}
	
	public static int minHour()
	{
		String fmt = dateTimeFormat;
		if( fmt.indexOf('H') > -1 || fmt.indexOf('K') > -1 )
			return 0;
		else
			return 1;
	}
	
	public static int maxHour()
	{
		String fmt = dateTimeFormat;
		if( is24Hours())
		{
			if( fmt.indexOf('H') > -1 )
				return 23;
			else
				return 24;
		}
		else
		{
			if( fmt.indexOf('h') > -1 )
				return 12;
			else
				return 11;
		}
	}
	
	public static boolean isHourPrefix()
	{
		String fmt = dateTimeFormat;
		if( fmt.indexOf("HH") > -1 || fmt.indexOf("hh") > -1 ||
				fmt.indexOf("KK") > -1 || fmt.indexOf("kk") > -1 )
			return true;
		return false;
	}
	
	public static boolean isMinutePrefix()
	{
		String fmt = dateTimeFormat;
		if( fmt.indexOf("mm") > -1 )
			return true;
		return false;
	}
	
	public static String prefixZero( int val, int digit )
	{
		String str = Integer.toString(val);
		int pre = digit - str.length();
		for( int i = 0; i < pre; i++ )
			str = '0' + str;
		return str;
	}
	
	public static String backupType2String(long backupType){
		try{
			if (backupType == BackupType.Full)
				return WebServiceMessages.getResource("BackupTypeFull");			
			else if (backupType == BackupType.Incremental)
				return WebServiceMessages.getResource("BackupTypeIncremental");
			else if (backupType == BackupType.Resync)
				return WebServiceMessages.getResource("BackupTypeResync");
			else
				return WebServiceMessages.getResource("BackupTypeUnknown");
		}
		catch (Exception e)
		{
			return "";
		}
	}

	public static String jobType2String(long jobType, long backupType) {
		String jobTypeString = null;

		if (jobType == Constants.JOBTYPE_CATALOG_FS	|| jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND) {
			jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
		} else if (jobType == Constants.JOBTYPE_CATALOG_GRT) {
			jobTypeString = WebServiceMessages.getResource("GRTCatalogJob");
		} else if (jobType == Constants.AF_JOBTYPE_BACKUP) {
			jobTypeString = backupType2String(backupType);
		} else if (jobType == Constants.AF_JOBTYPE_VM_BACKUP) {
			jobTypeString = backupType2String(backupType);
		} else if (jobType == Constants.AF_JOBTYPE_VM_RECOVERY) {
			jobTypeString = WebServiceMessages.getResource("RecoveryVMJob");
		} else if (jobType == Constants.AF_JOBTYPE_RESTORE) {
			jobTypeString = WebServiceMessages.getResource("RestoreJob");
		} else if (jobType == Constants.AF_JOBTYPE_COPY) {
			jobTypeString = WebServiceMessages.getResource("CopyJob");
		} else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP) {
			jobTypeString = WebServiceMessages.getResource("FileCopyJob");
		} else if(jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE) {
			jobTypeString = WebServiceMessages.getResource("FileArchiveJob");
		} else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE) {
			jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob");
		} else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE) {
			jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob");
		} else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC) {
			jobTypeString = WebServiceMessages.getResource("ArchiveCatalogReSyncJob") + " ";
		}
		return jobTypeString;
	}
	
	public static String jobType2String(long jobType) {
		String jobTypeString = null;

		if (jobType == Constants.JOBTYPE_CATALOG_FS
				|| jobType == Constants.JOBTYPE_CATALOG_FS_ONDEMAND
				|| jobType == JobType.JOBTYPE_VM_CATALOG_FS
				|| jobType == JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND) {
			jobTypeString = WebServiceMessages.getResource("FSCatalogJob");
		} else if (jobType == Constants.JOBTYPE_CATALOG_GRT) {
			jobTypeString = WebServiceMessages.getResource("GRTCatalogJob");
		} else if (jobType == Constants.AF_JOBTYPE_BACKUP
				|| jobType == Constants.AF_JOBTYPE_VM_BACKUP) {
			jobTypeString = WebServiceMessages.getResource("BackupJob");
		} else if (jobType == Constants.AF_JOBTYPE_VM_RECOVERY) {
			jobTypeString = WebServiceMessages.getResource("RecoveryVMJob");
		} else if (jobType == Constants.AF_JOBTYPE_RESTORE) {
			jobTypeString = WebServiceMessages.getResource("RestoreJob");
		} else if (jobType == Constants.AF_JOBTYPE_COPY) {
			jobTypeString = WebServiceMessages.getResource("CopyJob");
		} else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP) {
			jobTypeString = WebServiceMessages.getResource("FileCopyJob");
		} else if(jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE) {
			jobTypeString = WebServiceMessages.getResource("FileArchiveJob");
		} else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE) {
			jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob");
		} else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE) {
			jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob");
		} else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC) {
			jobTypeString = WebServiceMessages.getResource("ArchiveCatalogReSyncJob") + " ";
		} else if (jobType == CatalogJob.JOB_TYPE_CATALOG) {
			jobTypeString = WebServiceMessages.getResource("CatalogJob");
		}
		return jobTypeString;
	}
	
	public static boolean isTimeBeforeOrEqual(int hourFromC, int minuteFromC,
			int hourToC, int minutesToC) {
		if(hourFromC < hourToC) {
			return true;
		}else if(hourFromC == hourToC) {
			if(minuteFromC <= minutesToC) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isTimeBefore(int hourFromC, int minuteFromC,
			int hourToC, int minutesToC){
		if(hourFromC < hourToC) {
			return true;
		}else if(hourFromC == hourToC) {
			if(minuteFromC < minutesToC) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Only to process Rps related webservice error.
	 * @param exception
	 * @throws ServiceException
	 */
	public static void processWebServiceException(WebServiceException exception, Object... args) throws ServiceException {
		if(exception.getCause() instanceof UnknownHostException){
			throw new ServiceException(
					WebServiceMessages.getResource("cannotConnectServer", args),
							FlashServiceErrorCode.Common_CannotConnectRPSServer); 
		}
		if(exception.getCause() instanceof ConnectException 
				|| exception.getCause() instanceof SSLHandshakeException 
				|| exception.getCause() instanceof SocketException
				|| exception.getCause() instanceof SSLException 
				|| exception.getMessage().startsWith("XML reader error") ){
			throw new ServiceException(
					WebServiceMessages.getResource("cannotConnectService", args),
					FlashServiceErrorCode.Common_CannotConnectRPSService); 
		} 
		if(exception instanceof SOAPFaultException){
			SOAPFaultException fault = (SOAPFaultException)exception;
			String m = fault.getFault().getFaultString();
			logger.debug(m);
			String mesg = m.replace("Client received SOAP Fault from server: ", "");
			String showMesg = mesg.replace(" Please see the server log to find more detail regarding exact cause of the failure.", "");
			throw new ServiceException(showMesg, FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);			
		}
	}
	
	public static IJobDependency getJobDependency(String dependencyName) {
		return jobDependencies.get(dependencyName); 
	}
}
