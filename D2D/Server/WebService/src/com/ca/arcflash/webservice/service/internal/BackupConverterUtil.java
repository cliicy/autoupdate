package com.ca.arcflash.webservice.service.internal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.webservice.data.backup.BackupStatus;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class BackupConverterUtil {

	private static final String dateStandardFormatStr = "yyyy-MM-dd HH:mm:ss";
	private static final String dateLocaleFormatStr = CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat();
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(BackupConverterUtil.class);
	
	public static int string2BackupStatus(String source){
		if (StringUtil.isEmptyOrNull(source))
			return BackupStatus.Unknown;
		
		if (source.toLowerCase().equals("finished"))
			return BackupStatus.Finished;
		else if (source.toLowerCase().equals("failed"))
			return BackupStatus.Failed;
		else if (source.toLowerCase().equals("active"))
			return BackupStatus.Active;
		else if (source.toLowerCase().equals("canceled"))
			return BackupStatus.Canceled;
		else if (source.toLowerCase().equals("crashed"))
			return BackupStatus.Crashed;
		else if (source.toLowerCase().equals("missed"))
			return BackupStatus.Missed;
		else
			return BackupStatus.Unknown;
	}
	
	public static int string2BackupType(String source){
		if (StringUtil.isEmptyOrNull(source))
			return BackupType.Unknown;
		
		if (source.toLowerCase().equals("full"))
			return BackupType.Full;
		else if (source.toLowerCase().equals("incremental"))
			return BackupType.Incremental;
		else if (source.toLowerCase().equals("resync"))
			return BackupType.Resync;
		else
			return BackupType.Unknown;
	}
	
	public static long dosTime2UTC(long dosTime) {
		if(dosTime == 0)
			return 0;
		
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar cal = Calendar.getInstance(timeZone);
		int date = (int)(dosTime >>> 16);
		int time = (int)(dosTime & 0xffff);
		
		int year = 1980 + (date >>> 9);
		int month = ((date >>> 5) & 0xf) - 1;
		int day = date & 0x1f;
		int hourOfDay = time >>> 11;
		int minute = (time >>> 5) & 0x3f;
		int second = 2*(time & 0x1f);
		
		cal.set(year, month, day, hourOfDay, minute, second);
		
		return cal.getTime().getTime();
	}
	
	public static boolean validateDataFormat(String stringDateTime) {
		if(stringDateTime == null || stringDateTime.isEmpty()) {
			return false;
		}else {
			StringTokenizer token = new StringTokenizer(stringDateTime, "/-: ");
			if(token!=null && token.hasMoreTokens())
				return true;
			else 
				return false;
		}
	}
	
	public static Date string2Date(String source) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		StringTokenizer token = new StringTokenizer(source, "/-: ");
		Calendar cal = Calendar.getInstance(timeZone);
		
		if(token!=null && token.hasMoreTokens())
		{
			try {
				int year = Integer.parseInt(token.nextToken());
				int month = Integer.parseInt(token.nextToken()) - 1;
				int date = Integer.parseInt(token.nextToken());
				int hourOfDay = Integer.parseInt(token.nextToken());
				int minute = Integer.parseInt(token.nextToken());
				int second = Integer.parseInt(token.nextToken());
				cal.set(year, month, date, hourOfDay, minute, second);
				cal.set( Calendar.MILLISECOND, 0 );
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new IllegalArgumentException("Wrong date time format: " + source + ", " + e.getMessage());
			}
		}
		return cal.getTime();
	}
	
	public static String dateToUTCString(Date date)	{
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateStandardFormatStr, new Locale("en", "US"));
		dateFormat.setCalendar(calendar);
		return dateFormat.format(date);		
	}
	
	public static String dateToString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateLocaleFormatStr, DataFormatUtil.getDateFormatLocale());
		return dateFormat.format(date);
	}
	
	public static JNetConnInfo convertToConnInfoObj(String path, String domain, String usr, String pwd) {
		if (path == null)
			path = "";

		if (usr == null)
			usr = "";
		if (pwd == null)
			pwd = "";

		if (StringUtil.isEmptyOrNull(domain)) {
			int indx = usr.indexOf('\\');
			if (indx > 0) {
				domain = usr.substring(0, indx);
				usr = usr.substring(indx + 1);
			}
		}
		
		JNetConnInfo connInfo = new JNetConnInfo();
		connInfo.setSzDir(path);
		connInfo.setSzDomain(domain);
		connInfo.setSzUsr(usr);
		connInfo.setSzPwd(pwd);
		
		return connInfo;
	}
	
	public static String backupIndicatorToName(String backupIndicator) {
		if(backupIndicator == null)
			return null;
		if(BaseService.JOB_NAME_BACKUP_FULL.equals(backupIndicator)
				|| backupIndicator.startsWith(BaseService.JOB_NAME_BACKUP_FULL))
			return WebServiceMessages.getResource("BackupTypeFull");
		else if(BaseService.JOB_NAME_BACKUP_INCREMENTAL.equals(backupIndicator)
				|| backupIndicator.startsWith(BaseService.JOB_NAME_BACKUP_INCREMENTAL))
			return WebServiceMessages.getResource("BackupTypeIncremental");
		else if(BaseService.JOB_NAME_BACKUP_RESYNC.equals(backupIndicator)
				|| backupIndicator.startsWith(BaseService.JOB_NAME_BACKUP_RESYNC))
			return WebServiceMessages.getResource("BackupTypeResync");
		
		return backupIndicator;
	}
	
	public static String backupIndicatorToName(String backupIndicator,String vmInstanceUUID) {
		if(backupIndicator == null)
			return null;
		if((BaseService.JOB_NAME_BACKUP_FULL+vmInstanceUUID).equals(backupIndicator)
				|| backupIndicator.startsWith(BaseService.JOB_NAME_BACKUP_FULL))
			return WebServiceMessages.getResource("BackupTypeFull");
		else if((BaseService.JOB_NAME_BACKUP_INCREMENTAL+vmInstanceUUID).equals(backupIndicator)
				|| backupIndicator.startsWith(BaseService.JOB_NAME_BACKUP_INCREMENTAL))
			return WebServiceMessages.getResource("BackupTypeIncremental");
		else if((BaseService.JOB_NAME_BACKUP_RESYNC+vmInstanceUUID).equals(backupIndicator)
				|| backupIndicator.startsWith(BaseService.JOB_NAME_BACKUP_RESYNC))
			return WebServiceMessages.getResource("BackupTypeResync");
		
		return backupIndicator;
	}
}
