package com.ca.arcflash.webservice.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.merge.BackupSetInfo;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;

public abstract class BaseBackupSetService {
	
	private static final Logger logger = Logger.getLogger(BaseBackupSetService.class);

	protected volatile NativeFacade nativefacade;
	
	protected NativeFacade getNativeFacade() {
		if(nativefacade == null)
			nativefacade = new NativeFacadeImpl();
		return nativefacade;
	}
	
	protected boolean isManuallyBackupSetStart(RetentionPolicy retentionPolicy, String destination,
			String domain, String userName, String password, VirtualMachine vm) {
		if(retentionPolicy == null 
				|| !retentionPolicy.isUseBackupSet())
			return false;
		//if it's first backup, we will convert it to full by default,
		//so don't need to ask user whether need to convert it to full.
		try {
			if(this.isFirstBackup(destination, userName, password))
				return false;
		}catch(ServiceException se) {
			return false;
		}
		
		Calendar currentTime = Calendar.getInstance();		
		if(isBackupSetStartDay(retentionPolicy, currentTime)) {
			//check whether there is backup set start at this day
			return dealWithManualBackupForStartDate(retentionPolicy, destination, 
					 domain, userName, password, vm, currentTime);
		}else {
			return convertBackupForOldDate(retentionPolicy, destination,
					domain, userName, password, vm, currentTime);
		}
	}
	
	protected boolean isCurrentBackupSetStart(RetentionPolicy retentionPolicy,
			int jobType, String jobDetailName, String destination, String domain, String userName, 
			String password, VirtualMachine vm) {
		if(!retentionPolicy.isUseBackupSet())
			return false;		
		
		try {
			if(this.isFirstBackup(destination, userName, password))
				return true;
		}catch(ServiceException se){
			logger.error("Check first backup failed");
		}
		
		
		if(jobType != BackupType.Full && jobDetailName != null 
				&& jobDetailName.endsWith(BaseService.JOB_NAME_BACKUP_NOW_SUFFIX)){
			//We cannot convert the manual job to full if user does not want to do that
			return false;
		}
		
		Calendar currentTime = Calendar.getInstance();
		if(isBackupSetStartDay(retentionPolicy, currentTime)) {
			if(retentionPolicy.isStartWithFirst()) {
				return checkBackupSetForFirstBackup(destination, domain, userName, 
						password, vm, jobType, retentionPolicy, currentTime);
			}else {
				return checkBackupSetForLastBackup(destination, domain, userName, 
						password, vm, jobType, retentionPolicy, currentTime);
			}
		}else {
			return convertBackupForOldDate(retentionPolicy, destination,
					"", userName, password, vm, currentTime);
		}
	}
	
	protected synchronized void markBackupSetFlag(RetentionPolicy policy, 
			String destination, String domain, String userName, String password,
			VirtualMachine vm) {
		//not using backup set
		if(policy == null || !policy.isUseBackupSet()) {
			return;
		}
		
		Calendar current = Calendar.getInstance();
		Date setStart = getCurrentBackupStartDate(policy, current);
		setStart.setHours(0);
		setStart.setMinutes(0);
		setStart.setSeconds(0);
		current.add(Calendar.MINUTE, 5);
		
		try {
			//get all the recovery point from backup set start day to current time
			RecoveryPoint[] allRps = RestoreService.getInstance().getRecoveryPoints(
					destination, domain, userName, password, setStart, current.getTime(), false);
			if(allRps.length <= 0) return;
			
			List<RecoveryPoint> fullRps = new ArrayList<RecoveryPoint>();
			for(int i = allRps.length - 1; i >= 0;i --) {
				RecoveryPoint rp = allRps[i];
				//only for full backup
				if(rp.getBackupType() == BackupType.Full)
					fullRps.add(rp);
			}
			
			RecoveryPoint flagedOne = null;
			List<RecoveryPoint> deprecatedRecoverySets = new ArrayList<RecoveryPoint>();
			
			for(int i = 0; i < fullRps.size(); i ++ ) {
				RecoveryPoint rp = fullRps.get(i);
				//already find one, just need to clear old ones.
				if(flagedOne != null) {
					if(rp.getBackupSetFlag() > 0)
						unmarkCurrentFlag(destination, domain, userName, password, rp.getSessionID(), vm);
					continue;
				}else{
					if(rp.getBackupSetFlag() > 0){
						deprecatedRecoverySets.add(rp);
					}
				}
				
				RecoveryPoint nextRP = null;
				if((i+1) < fullRps.size()) {
					nextRP = fullRps.get(i+1);
				}
				if(rp.getTime().getMonth() == setStart.getMonth() 
						&& rp.getTime().getDate() == setStart.getDate()) {
					//backup set start day
					if(policy.isStartWithFirst()) {
						//set the flag for first full
						setFlagIfNot(rp, destination, domain, userName, password, vm, deprecatedRecoverySets);
						flagedOne = rp;
					}else{
						if(nextRP == null || nextRP.getTime().getMonth() != setStart.getMonth()
								|| nextRP.getTime().getDate() != setStart.getDate()){
							//set the flag for last full of that day
							setFlagIfNot(rp, destination, domain, userName, password, vm, deprecatedRecoverySets);
							flagedOne = rp;
						}
					}
				}else {
					//for future days, set the flag for first full
					setFlagIfNot(rp, destination, domain, userName, password, vm, deprecatedRecoverySets);
					flagedOne = rp;
				}
			}
			
			if(flagedOne != null)
				logger.info("Find a backup set: id is " + flagedOne.getSessionID());
			
		}catch(ServiceException se) {
			logger.error("Failed to get recovery points, exception is " + se);
		}
	}
	
	protected abstract boolean existScheduledFullJob(int date, VirtualMachine vm);
	
	protected abstract boolean existScheduledBackup(int date, VirtualMachine vm);
	
	/**
	 * Check whether today is the backup set start date.
	 */
	public synchronized boolean isBackupSetStartDay(RetentionPolicy policy,
				final Calendar time) {
		if(policy == null)
			return false;
		if(policy.isUseWeekly()) {
			if(time.get(Calendar.DAY_OF_WEEK) == policy.getDayOfWeek())
				return true;
		}else {			
			if(policy.getDayOfMonth() >= time.getActualMaximum(Calendar.DATE)) {
				Calendar tomorrow = (Calendar) time.clone(); 
				tomorrow.add(Calendar.DAY_OF_MONTH, 1);
				if(tomorrow.get(Calendar.MONTH) != time.get(Calendar.MONTH)) {
					//last day of month
					return true;
				}
			}else {
				if(policy.getDayOfMonth() == time.get(Calendar.DAY_OF_MONTH)) {
					return true;
				}
			}
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	private void setFlagIfNot(RecoveryPoint rp, String destination, 
			String domain, String userName, String password, VirtualMachine vm,
			List<RecoveryPoint> deprecatedRecoverySets) {
		if(rp.getBackupSetFlag() <= 0) {
			rp.setBackupSetFlag(1);
			markBackupSetFlag(destination, domain, userName, password, rp.getSessionID(), vm);
		}
		for(RecoveryPoint rp2 : deprecatedRecoverySets){
			if(rp2.getBackupSetFlag() > 0 && rp2.getSessionID() != rp.getSessionID()){
				rp2.setBackupSetFlag(0);
				unmarkCurrentFlag(destination, domain, userName, password, rp2.getSessionID(), vm);
			}
		}
	}
	/**
	 * If today is backup set start date, for manually backup, we mark it as backup set start 
	 * if the following condition meet in sequently:
	 * 1. there is not already a backup set start.
	 * 2. there is no scheduled full backup after this backup.
	 * 3. if it's set to use last backup, there is no scheduled backup after this one
	 * @param retentionPolicy
	 * @param configuration
	 * @return
	 */
	private boolean dealWithManualBackupForStartDate(RetentionPolicy retentionPolicy,
			String destination, String domain, String userName, String password, VirtualMachine vm, final Calendar currentTime) {
		try {
			//if there is backup set return false
			if(getExistBackupSetToday(destination, "",userName, password, 
					retentionPolicy, currentTime) != null)
				return false;
			else {				
				//check whether there is scheduled full backup
				//if exist, return false;
				if(existScheduledFullJob(currentTime.get(Calendar.DATE), vm)) {
					logger.info("There is scheduled full backup, retrun false");
					return false;
				}else {
					//else if is first backup, mark it, return true
					if(retentionPolicy.isStartWithFirst()){
						JRestorePoint rp = getFirstFullBackup(destination, "", userName, password, currentTime);
						if(rp != null) {
							logger.info("There is already a full backup, return false");
							markBackupSetFlag(destination, "", userName, password, Long.parseLong(rp.getSessionID()), vm);
							return false;
						}else {
							logger.info("Convert this backup to full and set it as backup set flag");
							return true;
						}
					}else {
						JRestorePoint rp = getLastFullBackup(destination, "", userName, password, currentTime);
						if(rp != null) {
							logger.info("There is already a full backup, return false");
							markBackupSetFlag(destination, "", userName, password, Long.parseLong(rp.getSessionID()), vm);
							return false;
						}
						//else if is last backup, check whether there is scheduled backup after this,
						//if yes, return false, else return true
						if(existScheduledBackup(currentTime.get(Calendar.DATE), vm)) {
							logger.info("Wait for the next backup");
							return false;
						}else{
							logger.info("Convert this backup to full and set it as backup set flag");
							return true;
						}
					}
				}					
			}
		}catch (ServiceException se) {
			logger.error(se.getMessage());
			return false;
		}
		
	}
	
	private JRestorePoint getExistBackupSetToday(String destination, String domain,
			String userName, String password, RetentionPolicy policy, final Calendar time) throws ServiceException {
		if(policy == null){
			logger.debug("no retention policy");
			return null;
		}
		try {
			Calendar start = clearTime(time);
				
			JRestorePoint[] rps = getNativeFacade().getRestorePointsForBackupSet(destination, 
					domain, userName, password, 
					start.getTime(), time.getTime());
			for(JRestorePoint rp : rps) {
				if(rp.getBackupSetFlag() > 0) {
					logger.info("Found a backup set start recovery point, session number is " + rp.getSessionID());
					return rp;
				}
			}
		} catch (ServiceException se) {
			throw se;
		}catch(Exception e) {
			logger.error("Failed to get recovery points, exception is ", e);
		}
		return null;
	}
	
	private Calendar clearTime(final Calendar srcTime) {
		Calendar bkSetStart = (Calendar) srcTime.clone();
		bkSetStart.set(srcTime.get(Calendar.YEAR), 
				srcTime.get(Calendar.MONTH), srcTime.get(Calendar.DATE), 0, 0, 0);
		return bkSetStart;
	}
	
	private JRestorePoint getFirstFullBackup(String destination, String domain, 
			String userName, String password, Calendar currentTime) throws ServiceException {
		logger.debug("Enter getFristFullBackup");

		try {
			Calendar time = Calendar.getInstance();
			time.setTime(currentTime.getTime());
			time.add(Calendar.MINUTE, 5);
			Calendar start = clearTime(currentTime);
				
			JRestorePoint[] rps = getNativeFacade().getRestorePointsForBackupSet(destination, 
					domain, userName, password, 
					start.getTime(), time.getTime());
			if(rps.length == 0) return null;
			
			for(int i = rps.length-1; i >= 0; i --) {
				if(BackupConverterUtil.string2BackupType(rps[i].getBackupType()) == BackupType.Full)
					return rps[i];
			}
		}catch (ServiceException se) {
			throw se;
		}catch(Exception e) {
			logger.error("Failed to get recovery points, exception is ", e);
		}
		return null;
	}
	
	private JRestorePoint getLastFullBackup(String destination, String domain, 
			String userName, String password, final Calendar currentTime) throws ServiceException {
		logger.debug("Enter getFristFullBackup");
		Calendar time = Calendar.getInstance();
		time.setTime(currentTime.getTime());
		try {
			time.add(Calendar.MINUTE, 5);
			Calendar start = clearTime(currentTime);
				
			JRestorePoint[] rps = getNativeFacade().getRestorePointsForBackupSet(destination, 
					domain, userName, password, 
					start.getTime(), time.getTime());
			if(rps.length == 0) return null;
			
			for(int i = 0; i < rps.length; i ++) {
				if(BackupConverterUtil.string2BackupType(rps[i].getBackupType()) == BackupType.Full)
					return rps[i];
			}
		}catch(ServiceException se) {
			throw se;
		}catch(Exception e) {
			logger.error("Failed to get recovery points, exception is ", e);
		}
		return null;
	}
	
	protected boolean markBackupSetFlag(String destination, String domain, String userName, String password, 
			long sessionNum, VirtualMachine vm) {		
		String vmInstanceUUID = vm != null ? vm.getVmInstanceUUID() : null;
		logger.info("set recovery set flag for " + sessionNum + " " + vmInstanceUUID);
		return getNativeFacade().markBackupSetFlag(destination, domain, userName, password, sessionNum, vmInstanceUUID);
	}
	
	protected void unmarkCurrentFlag(String destination, String domain, String userName, String password, 
			long sessionNum, VirtualMachine vm) {		
		String vmInstanceUUID = vm != null ? vm.getVmInstanceUUID() : null;
		logger.info("remove recovery set flag for " + sessionNum + " " + vmInstanceUUID);
		getNativeFacade().unmarkBackupSetFlag(destination, domain, userName, password, sessionNum, vmInstanceUUID);
	}
	
	/**
	 * If backup set start is set at Monday, then at Tuesday, we always
	 * check whether there is backup set start at this week or month,
		if no, then set current backup as backup set start no matter it's 
		a manual job or scheduled job.
		
		Both scheduled backup and manual backup need to call this method.		
	 * @param retentionPolicy
	 * @param configuration
	 * @return
	 */
	private boolean convertBackupForOldDate(RetentionPolicy retentionPolicy,
			String destination, String domain, String userName, String password, VirtualMachine vm, final Calendar today) {
		if(retentionPolicy == null){
			logger.debug("No configuration or retention policy, return false");
			return false;
		}
		
		try {
			Date backupSetStart = getCurrentBackupStartDate(retentionPolicy, today);
			JRestorePoint[] recoveryPoints = getNativeFacade().getRestorePointsForBackupSet(destination, 
					domain, userName, password, backupSetStart, today.getTime());
			if(recoveryPoints.length <= 0) return true;
			
			List<JRestorePoint> fullRps = new ArrayList<JRestorePoint>();			
			for(int i = recoveryPoints.length - 1; i >= 0; i --) {
				JRestorePoint rp = recoveryPoints[i];
				if(rp.getBackupSetFlag() > 0){
					//already exist a recovery set start
					return false;
				}else {
					if(BackupConverterUtil.string2BackupType(rp.getBackupType()) == BackupType.Full){
						fullRps.add(rp);
					}
				}
			}
			
			if(fullRps.size() <= 0)
				return true;
			
			Calendar startDate = Calendar.getInstance();
			startDate.setTime(backupSetStart);
			
			for(int i = 0; i < fullRps.size(); i ++) {
				JRestorePoint rp = fullRps.get(i);
				Calendar rpTime = Calendar.getInstance();
				Date time = BackupConverterUtil.string2Date(rp.getDate()+" "+rp.getTime());
				rpTime.setTime(time);
				rpTime = this.clearTime(rpTime);
				if(retentionPolicy.isStartWithFirst()) {
					if(markBackupSetFlag(destination, domain, userName, password, Long.valueOf(rp.getSessionID()), vm)){
						logger.info("No existed recovery set start, mark the first full backup as start");
						return false;
					}
				}else {
					if(i == 0 && rpTime.getTimeInMillis() != startDate.getTimeInMillis()){
						if(markBackupSetFlag(destination, domain, userName, password, Long.valueOf(rp.getSessionID()), vm)){
							logger.info("First full is not in backup set start date, then mark it");
							return false;
						}
					}else {
						if((i+1) >= fullRps.size()){
							if(markBackupSetFlag(destination, domain, userName, password, Long.valueOf(rp.getSessionID()), vm))
								return false;
						}else {
							JRestorePoint nextRp = fullRps.get(i + 1);
							Calendar nextrpTime = Calendar.getInstance();
							Date nexttime = BackupConverterUtil.string2Date(nextRp.getDate()+" "+nextRp.getTime());
							nextrpTime.setTime(nexttime);
							nextrpTime = this.clearTime(nextrpTime);						
							if(nextrpTime.getTimeInMillis() != startDate.getTimeInMillis()){
								//
								logger.info("Next full is not in recovery set start date, mark current one");
								if(markBackupSetFlag(destination, domain, userName, password, Long.valueOf(rp.getSessionID()), vm))
									return false;
							}
						}
					}
				}
			}
		}catch(Exception e) {
			logger.error("Failed to get recovery points", e);
			return false;
		}
		
		return true;
	}
	/**
	 * If we use last backup as the recovery set start, we may need to update the 
	 * recovery set flag if a new full backup finished in the recovery set start date.
	 * @param retentionPolicy
	 * @param destination
	 * @param domain
	 * @param userName
	 * @param password
	 * @param vm
	 * @param backupStartTime
	 */
	protected void fixRecoverySetForNewFullBackup(RetentionPolicy retentionPolicy, String destination,
			String domain, String userName, String password, VirtualMachine vm, 
			long backupStartTime){
		if(retentionPolicy == null || !retentionPolicy.isUseBackupSet()){
			return;			
		}		
		Calendar cal = Calendar.getInstance();
		
		cal.setTimeInMillis(backupStartTime);
		
		if(!this.isBackupSetStartDay(retentionPolicy, cal) 
				|| retentionPolicy.isStartWithFirst()){
			//only fix it for last backup, because the first backup is fixed
			return;
		}
		try {
			JRestorePoint existRP = this.getExistBackupSetToday(destination, domain, 
					userName, password, retentionPolicy, cal);
			if(existRP != null && Long.parseLong(existRP.getSessionID()) == 1){
				//don't change the flag for the first backup
				logger.info("The exist recovery set is the first session");
				return;
			}
			if(this.existScheduledFullJob(cal.get(Calendar.DATE), vm)){
				//exist scheduled full backup, unmark current recovery set flag
				if(existRP != null){				
					logger.info("exist scheduled full backup, unmark current recovery set flag " + existRP.getSessionID());
					this.unmarkCurrentFlag(destination, domain, userName, password, Long.parseLong(existRP.getSessionID()), vm);				
				}	
			}else {
				//mark current full, and unmark exist one
				JRestorePoint newrp = null;
				for(int i = 0; i < 5; i ++){
					//retry 5 times in case there is error to get the recovery point
					newrp = this.getLastFullBackup(destination, domain, userName, password, cal);					
					if(newrp != null){
						if(existRP != null){
							if(Long.parseLong(existRP.getSessionID()) != Long.parseLong(newrp.getSessionID())){
								this.unmarkCurrentFlag(destination, domain, userName, password, Long.parseLong(existRP.getSessionID()), vm);
								logger.info("mark recovery set flag for the new full backup");
								this.markBackupSetFlag(destination, domain, userName, password, Long.parseLong(newrp.getSessionID()), vm);
							}
						}else{
							logger.info("mark recovery set flag for the new full backup");
							this.markBackupSetFlag(destination, domain, userName, password, Long.parseLong(newrp.getSessionID()), vm);
						}
						break;
					}
				}
				if(newrp == null){
					logger.error("Failed to get last full backup with backup starttime " + cal.getTime() + " at " + destination);
				}
			}
		} catch (ServiceException se) {
			logger.error(se.getMessage());
			return;
		}
		
	}
	
	/**
	 * This API is only for the case scrTime is not the backup set start time.
	 * Whether srcTime is backup set start time is checked by API: {@link #isBackupSetStartDay(RetentionPolicy)}
	 * @param policy
	 * @param srcTime
	 * @return
	 */
	protected Date getCurrentBackupStartDate(RetentionPolicy policy, Calendar srcTime) {
		if(isBackupSetStartDay(policy, srcTime))
			return srcTime.getTime();
		Calendar bkSetStart = clearTime(srcTime);
		if(policy.isUseWeekly()) {
			//get this week's recovery points
			int dayOfWeek = policy.getDayOfWeek();
			dayOfWeek = dayOfWeek == 0? 7 : dayOfWeek;//Set Monday as the first week day
			if(srcTime.get(Calendar.DAY_OF_WEEK) > dayOfWeek) {
				bkSetStart.add(Calendar.DAY_OF_MONTH, dayOfWeek - srcTime.get(Calendar.DAY_OF_WEEK));
			}else {
				bkSetStart.add(Calendar.DAY_OF_MONTH, -7 + (dayOfWeek - srcTime.get(Calendar.DAY_OF_WEEK)));
			}
			
		}else {
			//get this month's recovery points
			if(policy.getDayOfMonth() > srcTime.get(Calendar.DAY_OF_MONTH)) {
				bkSetStart.add(Calendar.MONTH, -1);
			}
			int date = policy.getDayOfMonth() <= bkSetStart.getActualMaximum(Calendar.DATE) ?
					policy.getDayOfMonth() : bkSetStart.getActualMaximum(Calendar.DATE);
			bkSetStart.set(Calendar.DAY_OF_MONTH, date);
			
		}
		logger.info("Found backup set start: " + bkSetStart.getTime());
		return bkSetStart.getTime();
	}
	
	/**
	 * Check whether we need to mark this backup as backup set start when it will really run
	 * if it's set as first backup
	 * @return: true means need to convert this job full and set it as backup set flag,
	 * 	or need to set the flag; else false
	 */
	private boolean checkBackupSetForFirstBackup(String destination, String domain, 
			String userName, String password, VirtualMachine vm,
			int jobType, RetentionPolicy retentionPolicy, Calendar currentTime) {
		try {
			//if there is already a backup set today, return false
			if(getExistBackupSetToday(destination, "", userName, password, 
					retentionPolicy, currentTime) != null){
				logger.info("There is already a backcp set");
				return false;
			}else {
				JRestorePoint existFull = getFirstFullBackup(destination, "", 
						userName, password, currentTime );
				if(existFull != null) {
					logger.info("Find a exist full, return false");
					markBackupSetFlag(destination, "", userName, password, Long.parseLong(existFull.getSessionID()), vm);
					return false;
				}
				if(jobType == BackupType.Full) {
					//if current job is full backup, then return true
					logger.info("Set the flag for current full job");
					return true;
				}else {
					if(existScheduledFullJob(currentTime.get(Calendar.DATE), vm)){
						//wait for this scheduled full
						logger.info("wait for the scheduled full job");
						return false;
					}else {
						logger.info("Convert this job to full and set the flag");
						return true;
					}	
				}
			}
		}catch(ServiceException se) {
			logger.error(se.getMessage());
			return false;
		}
	}

	
	private boolean checkBackupSetForLastBackup(String destination, String domain, 
			String userName, String password, VirtualMachine vm,
				int jobType, RetentionPolicy retentionPolicy, Calendar currentTime) {
		try {
			JRestorePoint existRecoverySet = getExistBackupSetToday(destination, "", userName, 
					password, retentionPolicy, currentTime); 
			if(existRecoverySet != null && Long.parseLong(existRecoverySet.getSessionID()) == 1){
				//the first backup should always be recovery set start.
				return false;
			}
			 
			if(existScheduledFullJob(currentTime.get(Calendar.DATE), vm)){
				//if there is scheduled full, wait for this scheduled full			 
				if(existRecoverySet != null) {
					//unmark the old backup set start flag, this if maybe true 
					//after user changes the schedule or retention policy
					unmarkCurrentFlag(destination, "", userName, password, Long.parseLong(existRecoverySet.getSessionID()), vm);
				}
				logger.info("There is scheduled full backup, wait for it, and unmark current one "
						+ (existRecoverySet != null ? existRecoverySet.getSessionID() : null));
				return false;
			}else {
				if(jobType != BackupType.Full) {
					JRestorePoint rp = this.getLastFullBackup(destination, domain, userName, password, currentTime);
					if(rp != null) {
						this.markBackupSetFlag(destination, domain, userName, password, Long.parseLong(rp.getSessionID()), vm);
						if(existRecoverySet != null 
								&& Long.parseLong(existRecoverySet.getSessionID()) != Long.parseLong(rp.getSessionID())) {
							unmarkCurrentFlag(destination, "", userName, password, Long.parseLong(existRecoverySet.getSessionID()), vm);
						}
						return false;
					}else if(existRecoverySet == null){
						if(existScheduledBackup(currentTime.get(Calendar.DATE), vm)){
							logger.info("There is more scheduled backup, wait for it");
							return false;
						}else {
							logger.info("Convert this one to full and mark it");
							return true;
						}
					}else{
						return false;
					}					
				}else{
					return false;
				}
			}
		}catch(ServiceException se) {
			logger.error(se.getMessage());
			return false;
		}
	}
	
	public synchronized static List<BackupSetInfo> getBackupSetInfo(String destination, String domain, String userName, 
			String pwd, Date beginDate, Date endDate) throws ServiceException {
		logger.debug("Get backup set info for " + destination);
		List<BackupSetInfo> setInfos = new ArrayList<BackupSetInfo>();		
		RecoveryPoint[] rps = RestoreService.getInstance().getRecoveryPoints(destination, domain, userName, 
				pwd, beginDate, endDate, false);
		BackupSetInfo currentSet = new BackupSetInfo();
		int count = 0;
		long size = 0;
		for(int i = 0; i < rps.length; i ++) {
			count ++;
			size += rps[i].getDataSize();
			if(rps[i].getBackupSetFlag() == 1){
				currentSet.setStartRecoveryPoint(rps[i]);
				currentSet.setRecoveryPointCount(count);
				currentSet.setTotalSize(size);
				setInfos.add(currentSet);
				currentSet = new BackupSetInfo();
				count = 0;
				size = 0;
			}else if(i > 0 && rps[i - 1].getBackupSetFlag() == 1){
				currentSet.setEndRecoveryPoint(rps[i]);
			}else if(i == 0){
				currentSet.setEndRecoveryPoint(rps[i]);
			}
		}
		logger.debug("Finished get backup set info for " + destination);
		return setInfos;
	}
	
	public synchronized static List<BackupSetInfo> getAllBackupSetInfo(String destination, 
			String domain, String userName, String pwd) throws ServiceException {
		Calendar beginDate = Calendar.getInstance();
		beginDate.set(1970, 0, 1);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2999, 11, 31);
		return getBackupSetInfo(destination, domain, userName, pwd, 
				beginDate.getTime(), endDate.getTime());
	}
	
	private boolean isFirstBackup(String dest, String userName, String password) throws ServiceException {
		Calendar beginDate = Calendar.getInstance();
		beginDate.set(1970, 0, 1);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2999, 11, 31);
		try {
			JRestorePoint[] resp = this.getNativeFacade()
					.getRestorePointsForBackupSet(dest, "", userName, password,
							beginDate.getTime(), endDate.getTime());
			return resp.length == 0;
		}catch(ServiceException se) {
			logger.error("Failed to get recovery point" + se);
			throw se;
		}
	}
	
	public synchronized void fixRecoverySetForNewFull(long backupStartTime, String vmInstanceUUID){
		logger.debug("Fix recovery set for new full backup start " + vmInstanceUUID);
		VirtualMachine vm = getVirtualMachine(vmInstanceUUID);
		
		try {
			BackupConfiguration configuration = getBackupConfiguration(vmInstanceUUID);
			RetentionPolicy policy = configuration.getRetentionPolicy();
			
			this.fixRecoverySetForNewFullBackup(policy, getDestinationFromBackupConfiguration(configuration),
					"", getUserFromBackupConfiguration(configuration), 
					getPasswordFromBackupConfiguration(configuration), vm,
					backupStartTime);	
		}catch(Exception e) {
			logger.error("Failed to get backup configuration " + e);
		}
		logger.debug("Fix recovery set for new full backup end. "  + vmInstanceUUID);
	}

	protected String getDestinationFromBackupConfiguration(BackupConfiguration conf) {
		return conf.getDestination();
	}
	
	protected String getUserFromBackupConfiguration(BackupConfiguration conf) {
		return conf.getUserName();
	}
	
	protected String getPasswordFromBackupConfiguration(BackupConfiguration conf) {
		return conf.getPassword();
	}
	
	protected VirtualMachine getVirtualMachine(String vmInstanceUUID) {
		VirtualMachine vm = null;
		if(!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
		}
		return vm;
	}
	
	/**
	 * Check whether need to convert current manual job to full and set it as backup set start.
	 * @return
	 */
	public synchronized boolean isManuallyBackupSetStart(String vmInstanceUUID) {
		logger.debug("Check backup set start for manually backup");
		
		try {
			BackupConfiguration configuration = getBackupConfiguration(vmInstanceUUID);
			RetentionPolicy policy = configuration.getRetentionPolicy();
			return isManuallyBackupSetStart(policy, getDestinationFromBackupConfiguration(configuration),
					"", getUserFromBackupConfiguration(configuration), 
					getPasswordFromBackupConfiguration(configuration), 
					getVirtualMachine(vmInstanceUUID));	
		}catch(Exception e) {
			logger.error("Failed to get backup configuration " + e);
		}
		
		return false;	
	}
	
	/**
	 * Check whether current backup need to convert to full and set as backup set start
	 * when the job will run.
	 * @param configuration
	 * @param jobDetail
	 * @return
	 */
	public synchronized boolean isCurrentBackupSetStart(BackupConfiguration configuration,
				int jobType,
				String jobDetailName,
				String vmInstanceUUID) {
		if(configuration == null  
				|| configuration.getRetentionPolicy() == null) {
			return false;
		}
		
		RetentionPolicy retentionPolicy = configuration.getRetentionPolicy();
		if(!retentionPolicy.isUseBackupSet())
			return false;	
		
		return isCurrentBackupSetStart(retentionPolicy, jobType, jobDetailName, 
				getDestinationFromBackupConfiguration(configuration),
				"", configuration.getUserName(), 
				configuration.getPassword(), 
				getVirtualMachine(vmInstanceUUID));
	}
	
	public synchronized List<BackupSetInfo> getBackupSetInfo(String vmInstanceUUID) throws ServiceException {
		BackupConfiguration configuration = getBackupConfiguration(vmInstanceUUID);
		if(configuration == null) 
			return null;
		return getAllBackupSetInfo(getDestinationFromBackupConfiguration(configuration), 
				"", 
				getUserFromBackupConfiguration(configuration), 
				getPasswordFromBackupConfiguration(configuration));
	}
	
	protected BackupConfiguration getBackupConfiguration(String vmInstanceUUID) {
		try {
			return BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
