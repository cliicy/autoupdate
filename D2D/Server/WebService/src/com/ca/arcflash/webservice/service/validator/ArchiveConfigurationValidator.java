package com.ca.arcflash.webservice.service.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.data.restore.RestoreJobType;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.ArchiveJobConverter;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class ArchiveConfigurationValidator {

	private static final Logger logger = Logger.getLogger(ArchiveConfigurationValidator.class);
	private static final int MAX_PORT = 65535;
	private static final int MIN_PORT = 0;
	public static final int WINDOWS_HOST_NAME_MAX_LENGTH = 16;  
	protected ArchiveJobConverter jobConverter = new ArchiveJobConverter();
	
	public int Validate(ArchiveConfiguration archiveConfig)  throws ServiceException{
		if (archiveConfig == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);
		
		//if (Integer.parseInt(archiveConfig.getRetentiontime()) <0)
		//	throw new ServiceException(FlashServiceErrorCode.BackupConfig_InvalidRetentionCount);
		
//		if (!StringUtil.isExistingPath(backupConfiguration.getDestination()))
//			throw new ServiceException(
//					FlashServiceErrorCode.BackupConfig_IvalidDestinationPath);
		
		int pathMaxWithoutHostName = 0;
		if(archiveConfig.getStrArchiveToDrivePath()!=null)
		{
			//validating whether same destination is used for archive
			if(archiveConfig.getStrArchiveToDrivePath().equalsIgnoreCase(archiveConfig.getbackupDestination())){
				logger.error("The File Copy destination should not be same as Backup destination path.");
				throw new ServiceException(FlashServiceErrorCode.ArchiveConfig_ERR_DEST_SAME_BACKUPDEST);
			}
			pathMaxWithoutHostName = validateDestPath(archiveConfig);

			if ((!isRemote(archiveConfig.getStrArchiveToDrivePath()))
					&& (BrowserService.getInstance().getVolumes(false, null, null, null).length == 1)) {
				throw new ServiceException(
						FlashServiceErrorCode.ArchiveConfig_SingleVolumeLocalDestination);
			}
		}
		return pathMaxWithoutHostName;
	}

	private boolean isRemote(String inputFolder) {
		return inputFolder != null && inputFolder.startsWith("\\\\");
	}
	
	public int validateDestPath(ArchiveConfiguration archiveConfiguration)
	throws ServiceException {

	logger.debug("validateDestPath - start");
	String path = archiveConfiguration.getStrArchiveToDrivePath();
	
	String username = archiveConfiguration.getStrArchiveDestinationUserName();
	String password = archiveConfiguration.getStrArchiveDestinationPassword();
	if (path == null)
		path = "";
	
	if (username == null)
		username = "";
	if (password == null)
		password = "";
	
	logger.debug("path" + path);
	logger.debug("username" + username);
	String domain = "";
	int indx = username.indexOf('\\');
	if (indx > 0) {
		domain = username.substring(0, indx);
		username = username.substring(indx + 1);
	}
	
	long pathMaxWithoutHostName = BackupService.getInstance().getPathMaxLength();
	//To make sure the path without appending host name does not exceed the maximum length,
	//the validation is necessary because getNativeFacade().checkFolderAccess in the following  
	//may lead to the web service breakdown.
	if(path.length() > pathMaxWithoutHostName+1) {
		generatePathExeedLimitException(pathMaxWithoutHostName);
	}
	
	 BrowserService.getInstance().getNativeFacade()
			.validateDestUser(path, domain, username, password);	
	
	logger.debug("validateDestPath - end");
	return (int)pathMaxWithoutHostName;
	}

	public void generatePathExeedLimitException(long pathMaxWithoutHostName)
		throws ServiceException {
		long pathMaxLength = pathMaxWithoutHostName - ServiceContext.getInstance().getLocalMachineName().length();
		throw new ServiceException("" + pathMaxLength, 
				        FlashServiceErrorCode.ArchiveConfig_ERR_FileNameTooLong);
	}
	
	public void validateArchiveJob(RestoreArchiveJob job) throws ServiceException{
		if (job == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);			

		
		String destinationPath = job.getarchiveRestoreDestinationPath();

		if (!StringUtil.isEmptyOrNull(destinationPath)) {
			String destUser = job.getarchiveRestoreUserName() == null ? "" : job.getarchiveRestoreUserName();
			String destpass = job.getarchiveRestoreUserName() == null ? "" : job.getarchiveRestorePassword();					
			String destDomain = "";
			int destindx = destUser.indexOf('\\');
			if (destindx > 0) {
				destDomain = destUser.substring(0, destindx);
				destUser = destUser.substring(destindx + 1);
			}

			checkDestination(destinationPath, destUser, destpass, destDomain);
		}
		
		if (job.getJobType() == RestoreJobType.FileSystem && job.getFileSystemOption()==null)
			throw new ServiceException(FlashServiceErrorCode.RestoreJob_NoFileSystemOption);	
		
	}
	//madra04 
	public void validateRestoreEncryptionDetails(RestoreArchiveJob job) throws ServiceException{
		
		String NodeName = job.getArchiveNodes()[0].getNodeName();
		if(NodeName == null || NodeName.length() == 0)
		{
			NodeName = ServiceContext.getInstance().getLocalMachineName();
		}		
		ArchiveJobScript targetJob = generateArchiveRestoreJobScript(0,job, NodeName);
		 ArrayList errorList;
			try {
				errorList = BrowserService.getInstance().getNativeFacade().validateEncryptionSettings(targetJob);
			} catch (Exception e) {
				throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_ProcessingError);
			}
		    long Errorcode = Long.parseLong(errorList.get(0).toString());	 
		    long CCIErrorCode = Long.parseLong(errorList.get(1).toString());
		    
		    if(Errorcode == 0)
			{
				if(CCIErrorCode != 0 )
				{
					if(CCIErrorCode == 424) //CCI internal error code
					 throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_PasswordDoesnotMatch);
					else
					 throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_CCIInternalError);
					
				}
			}
		    else if(Errorcode == -1)
			{
				throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_ProcessingError);
			}
			else if(Errorcode == 1)
			{
				if(CCIErrorCode == 0)
				{
					throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_EncryptedBeforeInRestore);
				}
				else if(CCIErrorCode == 1)
				{
					throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_NotEncryptedBeforeInRestore);
				}
					
			}
		
	}
	
	public boolean validateRestoreDetails(RestoreArchiveJob job) throws ServiceException{
		
		boolean isEncrypted = false;
		String NodeName = job.getArchiveNodes()[0].getNodeName();
		if(NodeName == null || NodeName.length() == 0)
		{
			NodeName = ServiceContext.getInstance().getLocalMachineName();
		}		
		ArchiveJobScript targetJob = generateArchiveRestoreJobScript(0,job, NodeName);
		 ArrayList errorList;
			try {
				errorList = BrowserService.getInstance().getNativeFacade().validateEncryptionSettings(targetJob);
			} catch (Exception e) {
				throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_ProcessingError);
			}
		    long Errorcode = Long.parseLong(errorList.get(0).toString());	 
		    long CCIErrorCode = Long.parseLong(errorList.get(1).toString());
		    
		    if(Errorcode == 0)
			{
				if(CCIErrorCode != 0 )
				{
					if(CCIErrorCode == 424) //CCI internal error code
					 throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_PasswordDoesnotMatch);
					else
					 throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_CCIInternalError);
					
				}
			}
		    else if(Errorcode == -1)
			{
				throw new ServiceException(FlashServiceErrorCode.ArchiveSettings_ProcessingError);
			}
			else if(Errorcode == 1)
			{
				if(CCIErrorCode == 0)
				{
					isEncrypted = true;
				}
				else if(CCIErrorCode == 1)
				{
					isEncrypted = false;
				}
					
			}
		    
		    return isEncrypted;
		
	}

	
	
	private ArchiveJobScript generateArchiveRestoreJobScript(long shrmemid,RestoreArchiveJob job,
			String localMachineName) {
		
		ArchiveJobScript archiveJob = null;
		archiveJob = jobConverter.convertArchiveRestoreJobToScript(shrmemid, job, localMachineName);		
		return archiveJob;
	}
	
	
	
	private void checkDestination(String destinationPath, String destUser,
			String destpass, String destDomain) throws ServiceException {
		if (StringUtil.isEmptyOrNull(destinationPath))
			throw new ServiceException(
					FlashServiceErrorCode.RestoreJob_InvalidDestinationPath);
		BrowserService.getInstance().getNativeFacade()
			.validateDestUser(destinationPath, destDomain, destUser, destpass);
	
	}
	
	public void validateAdvanceSchedule(AdvanceSchedule advanceSchedule, boolean hasEndTime) throws ServiceException{
		logger.debug("validate advanced schedule - start with end time = " + hasEndTime);
		List<DailyScheduleDetailItem> dailyLists = advanceSchedule.getDailyScheduleDetailItems();
		if (dailyLists != null) {
			for (int i = dailyLists.size() - 1; i >= 0; i--) {
				DailyScheduleDetailItem daylyItem = dailyLists.get(i);
				int dayOfWeek = daylyItem.getDayofWeek();
				if (dayOfWeek < 1 || dayOfWeek > 7) {
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_ScheduleDayofWeekOutOfRange);
				}
			}
			List<DailyScheduleDetailItem> newDaylyLists = removeDuplicateDayOfWeek(dailyLists);
			Collections.sort(newDaylyLists);
			advanceSchedule.setDailyScheduleDetailItems(newDaylyLists);
			for (DailyScheduleDetailItem daylyItem : advanceSchedule.getDailyScheduleDetailItems()) {
				ArrayList<ScheduleDetailItem> scheduleDetailLists = daylyItem.getScheduleDetailItems();
				if ((scheduleDetailLists != null)
						&& (scheduleDetailLists.size() > Integer.parseInt(WebServiceMessages
								.getResource("scheduleMaxItemCount")))) {
					throw new ServiceException(WebServiceMessages.getResource("scheduleItemIsMax"),
							FlashServiceErrorCode.AdvanceSchedule_ScheduleMaxItemCount);
				}
				
				if(scheduleDetailLists != null){
					for (ScheduleDetailItem scheduleItem : scheduleDetailLists) {
						validateDayTime(scheduleItem.getStartTime(), scheduleItem.getEndTime(), hasEndTime);
					}
				}
			}
		}
		
		PeriodSchedule periodSchedule = advanceSchedule.getPeriodSchedule();
		if(periodSchedule != null){
			EveryMonthSchedule monthSchedule = periodSchedule.getMonthSchedule();
			if(monthSchedule.isEnabled()){
				validateDayTime(monthSchedule.getDayTime(), monthSchedule.getEndTime(), hasEndTime);
			}
			EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();
			if(daySchedule.isEnabled()){
				validateDayTime(daySchedule.getDayTime(), null, hasEndTime);
			}
		}
		logger.debug("validate advanced schedule - end");
	}
	
	private List<DailyScheduleDetailItem> removeDuplicateDayOfWeek(List<DailyScheduleDetailItem> daylyLists)
			throws ServiceException {
		ArrayList<DailyScheduleDetailItem> newDaylyLists = new ArrayList<DailyScheduleDetailItem>();
		// The later duplicate one will override the previous one if same
		// dayofweek
		for (int i = daylyLists.size() - 1; i >= 0; i--) {
			DailyScheduleDetailItem dailyItem = daylyLists.get(i);
			int dayOfWeek = dailyItem.getDayofWeek();
			boolean isAdd = true;
			for (DailyScheduleDetailItem item : newDaylyLists) {
				if (item.getDayofWeek() == dayOfWeek) {
					isAdd = false;
					logger.info("Duplicate DayOfweek set, the later will override previous one:" + dayOfWeek);
					break;
				}
			}
			if (isAdd) {
				newDaylyLists.add(dailyItem);
			}

		}
		return newDaylyLists;
	}
	
	private boolean isValidTime(DayTime dayTime){
		return dayTime.getHour()>=0 && dayTime.getHour()<=23 && dayTime.getMinute()>=0 && dayTime.getMinute()<=59;
	}
	
	private void validateDayTime(DayTime startTime, DayTime endTime, boolean hasEndTime) throws ServiceException{
		if (!isValidTime(startTime)) {
			throw new ServiceException(
					WebServiceMessages.getResource("scheduleItemStartTimeIsInvalid"),
					FlashServiceErrorCode.AdvanceSchedule_ScheduleItemStartTimeIsInvalid);
		}
		if(hasEndTime){
			int startMinutes = startTime.getHour() * 60 + startTime.getMinute();
			
			if (!isValidTime(endTime)){
				throw new ServiceException(
						WebServiceMessages.getResource("scheduleItemEndTimeIsInvalid"),
						FlashServiceErrorCode.AdvanceSchedule_ScheduleItemEndTimeIsInvalid);
			}
			int endMinutes = endTime.getHour() * 60	+ endTime.getMinute();
			
			int diffMinutes = endMinutes - startMinutes;
			if (diffMinutes < 15){
				throw new ServiceException(
						WebServiceMessages.getResource("scheduleDifferenceTimeNoLessThan"),
						FlashServiceErrorCode.AdvanceSchedule_ScheduleDifferenceTimeNoLessThan);						
			}
		}
	}
	
}
