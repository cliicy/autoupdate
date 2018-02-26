package com.ca.arcflash.webservice.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveJobSession;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveSourceItem;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.service.AbstractBackupService.CONN_INFO;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.util.ArchiveToTapeUtils;

public class ArchiveToTapeService {
	private static final Logger logger = Logger.getLogger(ArchiveToTapeService.class);
	private static final ArchiveToTapeService instance = new ArchiveToTapeService();

	public static ArchiveToTapeService getInstance() {
		return instance;
	}

	public long saveArchiveToTapeConfig(ArchiveConfig archiveToTapeConfig) throws ServiceException {
		logger.info("saving archiveToTapeConfig - start, at " + new Date());
		try {
			ArchiveToTapeUtils.saveArchiveToTape(archiveToTapeConfig);
		} catch (Exception e) {
			throw e;
		}
		logger.info("saving archiveToTapeConfig - End");
		return 0;
	}

	public ArchiveConfig getArchiveToTapeConfig() throws ServiceException {
		logger.debug("getArchiveToTapeConfig - start");
		if (ArchiveToTapeUtils.getArchiveToTapeConfig() == null) {
			return ArchiveToTapeUtils.loadArchiveToTape();
		}

		logger.debug("getArchiveToTapeConfig - end");
		return ArchiveToTapeUtils.getArchiveToTapeConfig();
	}
	
	protected List<Long> getArchiveSessions(ArchiveConfig ac, BackupConfiguration configuration, int startSessionNo, int scheduleType) throws ServiceException {
		if(!isScheduleTypeValid(ac)) return null;

		CONN_INFO connInfo = BackupService.getInstance().getCONN_INFO(configuration);

		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(0);
		Date startDate = startCal.getTime();

		java.util.Calendar cal = java.util.Calendar.getInstance();
		Date endDate = cal.getTime();

		List<Long> sessions = new ArrayList<Long>();

		RecoveryPoint[] recoveryPoints = RestoreService.getInstance().getRecoveryPoints(configuration.getDestination(), connInfo.getDomain(),
				connInfo.getUserName(), connInfo.getPwd(), startDate, endDate, false);

		if (recoveryPoints == null) {
			logger.debug("getArchiveSesssions - return null for empty backups during period");
			return null;
		}

		long cfgTime = ac.getSource().getSourceItems().get(0).getConfigTime();
		logger.debug("cfg time:" + new Date(cfgTime));
		
		for (int i = 0; i < recoveryPoints.length; i++) {
			RecoveryPoint rp = recoveryPoints[i];
			if (rp.getBackupStatus() == 1 || rp.getBackupStatus() == 4) {
				if (rp.getPeriodRetentionFlag() == scheduleType) {
					if (rp.getSessionID() > startSessionNo && rp.getTime().getTime() > cfgTime) {
						sessions.add(rp.getSessionID());
					}
				}
			}
		}

		logger.debug("getArchiveSesssions exit -get sessions:" + sessions);
		return sessions;
	}

	protected boolean isScheduleTypeValid(ArchiveConfig ac) {
		return (isDailySet(ac) || isWeeklySet(ac) || isMonthlySet(ac));
	}
	
	protected boolean isDailySet(ArchiveConfig ac) {
		boolean isDailySet = true;
		if (ac.getSource() != null && ac.getSource().getSourceItems() != null && ac.getSource().getSourceItems().size() > 0) {
			ArchiveSourceItem sourceItem = ac.getSource().getSourceItems().get(0);
			isDailySet = sourceItem.getDailyItem() != null && sourceItem.getDailyItem().isEnabled();
		}
		return isDailySet;
	}
	
	protected boolean isWeeklySet(ArchiveConfig ac) {
		boolean isWeeklySet = true;
		if (ac.getSource() != null && ac.getSource().getSourceItems() != null && ac.getSource().getSourceItems().size() > 0) {
			ArchiveSourceItem sourceItem = ac.getSource().getSourceItems().get(0);
			isWeeklySet = sourceItem.getWeeklyItem() != null && sourceItem.getWeeklyItem().isEnabled();
		}
		return isWeeklySet;
	}
	
	protected boolean isMonthlySet(ArchiveConfig ac) {
		boolean isMonthlySet = true;
		if (ac.getSource() != null && ac.getSource().getSourceItems() != null && ac.getSource().getSourceItems().size() > 0) {
			ArchiveSourceItem sourceItem = ac.getSource().getSourceItems().get(0);
			isMonthlySet = sourceItem.getMonthlyItem() != null && sourceItem.getMonthlyItem().isEnabled();
		}
		return isMonthlySet;
	}
	/**
	 * 
	 * @param startSessionNo
	 *            the last archive to tape session
	 * @param scheduleType
	 *            allowed value 1,2,4 meaning daily, weekly and monthly
	 *            respectively
	 * @see {@link com.ca.arcflash.service.data.PeriodRetentionValue}
	 * @return the session id list
	 * @throws ServiceException
	 */

	public List<Long> getArchiveSesssions(int startSessionNo, int scheduleType) throws ServiceException {
		BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();

		ArchiveConfig ac = getArchiveToTapeConfig();

		if (configuration == null || ac == null) {
			logger.debug("getArchiveSesssions - return null for null configuration, configuration=" + configuration + " ,ArchiveConfig:" + ac);
			return null;
		}

		List<Long> sessions = getArchiveSessions(ac, configuration, startSessionNo, scheduleType);

		return sessions;
	}
	
	protected List<ArchiveJobSession> getArchiveSessionsMore(ArchiveConfig ac, BackupConfiguration configuration, int scheduleType) throws ServiceException {
		JNetConnInfo jnetConnInfo = new JNetConnInfo();
		Lock lock = null;		
		boolean bNeedCutConnection = true;
		try {
			if(!isScheduleTypeValid(ac)) return null;

			CONN_INFO connInfo = BackupService.getInstance().getCONN_INFO(configuration);

			Calendar startCal = Calendar.getInstance();
			startCal.setTimeInMillis(0);
			Date startDate = startCal.getTime();

			java.util.Calendar cal = java.util.Calendar.getInstance();
			Date endDate = cal.getTime();

			List<ArchiveJobSession> sessions = new ArrayList<ArchiveJobSession>();
			
			//zxh,create connection
			if (logger.isDebugEnabled()) {			
				logger.debug("connect destination.dest=" + configuration.getDestination() + ", domain=" + connInfo.getDomain() + ", username=" + connInfo.getUserName()
						+ ", pwdLen=" + connInfo.getPwd().length());
			}
			
			jnetConnInfo.setSzDir(configuration.getDestination());
			jnetConnInfo.setSzDomain(connInfo.getDomain());
			jnetConnInfo.setSzUsr(connInfo.getUserName());
			jnetConnInfo.setSzPwd(connInfo.getPwd());
			

			lock = RemoteFolderConnCache.getInstance().getLockByPath(configuration.getDestination());
			if(lock != null)
				lock.lock();
			
			long lRtVal = WSJNI.AFCreateConnection(jnetConnInfo);
			if (0 != lRtVal) {
				logger.error("AFCreateConnection error: return val is:" + lRtVal);
				bNeedCutConnection = false;
				return null;
			}

			//zxh,mark:ASBUGetRecoveryPoint
			RecoveryPoint[] recoveryPoints = RestoreService.getInstance().getRecoveryPoints4ASBU(configuration.getDestination(), connInfo.getDomain(),
					connInfo.getUserName(), connInfo.getPwd(), startDate, endDate, false);

			if (recoveryPoints == null) {
				logger.debug("getArchiveSesssions - return null for empty backups during period");
				return null;
			}

			long cfgTime = ac.getSource().getSourceItems().get(0).getConfigTime();
			logger.debug("cfg time:" + new Date(cfgTime));
			
			String userName = connInfo.getUserName();
			if(connInfo.getDomain() !=null && connInfo.getDomain().trim().length() >0){
				userName =connInfo.getDomain()+"\\" + userName;
			}
			
			int[] lastSessions = BackupService.getInstance().getNativeFacade()
					.getLastArchiveToTapeSession(configuration.getDestination(), userName, connInfo.getPwd());
			
			if(null == lastSessions){
				logger.error("getLastArchiveToTapeSession - return null");
				return null;
			}
			
			long dailyStartSessionNo = Long.MAX_VALUE;
			long weeklyStartSessionNo = Long.MAX_VALUE;
			long monthlyStartSessionNo = Long.MAX_VALUE;		
			
			int dailyFlag = PeriodRetentionValue.QJDTO_B_Backup_Daily;
			int weeklyFlag = PeriodRetentionValue.QJDTO_B_Backup_Weekly;
			int monthlyFlag = PeriodRetentionValue.QJDTO_B_Backup_Monthly;
			
			if(isDailySet(ac) && (scheduleType & dailyFlag) >0){
				dailyStartSessionNo = lastSessions[0];
				logger.debug("dailyStartSessionNo:" + dailyStartSessionNo);
			}
			
			if(isWeeklySet(ac) && (scheduleType & weeklyFlag) >0){
				weeklyStartSessionNo = lastSessions[1];
				logger.debug("weeklyStartSessionNo:" + weeklyStartSessionNo);
			}
			if(isMonthlySet(ac) && (scheduleType & monthlyFlag) >0){
				monthlyStartSessionNo = lastSessions[2];
				logger.debug("monthlyStartSessionNo:" + monthlyStartSessionNo);
			}		
			
			for (int i = 0; i < recoveryPoints.length; i++) {
				RecoveryPoint rp = recoveryPoints[i];
				
				if (rp.getTime().getTime() <= cfgTime){
					if (logger.isDebugEnabled()) {
						logger.debug("session id:" + rp.getSessionID() + ":backup time is:" + new Date(rp.getTime().getTime()));	//zxh, add logs
					}
					continue;
				}
				
				if(rp.getBackupStatus() != 1 && rp.getBackupStatus() != 4){
					if (logger.isDebugEnabled()) {
						logger.debug("session id:" + rp.getSessionID() + " is not the status 'finished' or 'finished incompletely',ignore it.");	//zxh, add logs
					}
					continue;
				}
				
				if ( ((rp.getPeriodRetentionFlag() & dailyFlag) > 0 && (-1 == dailyStartSessionNo))
					|| ((rp.getPeriodRetentionFlag() & weeklyFlag) > 0 && (-1 == weeklyStartSessionNo))
					|| ((rp.getPeriodRetentionFlag() & monthlyFlag) > 0 && (-1 == monthlyStartSessionNo))){	
					if (logger.isDebugEnabled()) {
						logger.debug("session id:" + rp.getSessionID() + ":JNI api 'getLastArchiveToTapeSession' return values is -1(ASBU c++ backend have error)");	//zxh, add logs
					}
					continue;
				}
				
				if ( ((rp.getPeriodRetentionFlag() & dailyFlag) > 0 && (-2 == dailyStartSessionNo))
						|| ((rp.getPeriodRetentionFlag() & weeklyFlag) > 0 && (-2 == weeklyStartSessionNo))
						|| ((rp.getPeriodRetentionFlag() & monthlyFlag) > 0 && (-2 == monthlyStartSessionNo))){	
					if (logger.isDebugEnabled()) {
						logger.debug("session id:" + rp.getSessionID() + ":JNI api 'getLastArchiveToTapeSession' return values is -2(Backup is running)");	//zxh, add logs
					}
					continue;
				}
				
				if (((rp.getPeriodRetentionFlag() & dailyFlag) > 0 && rp.getSessionID() > dailyStartSessionNo)
						|| ((rp.getPeriodRetentionFlag() & weeklyFlag) > 0 && rp.getSessionID() > weeklyStartSessionNo)
						|| ((rp.getPeriodRetentionFlag() & monthlyFlag) > 0 && rp.getSessionID() > monthlyStartSessionNo)) {
					ArchiveJobSession session = new ArchiveJobSession();
					session.setSessionId(rp.getSessionID());
					session.setSessionTime(rp.getTime().getTime());
					session.setSchedueType(rp.getPeriodRetentionFlag());
					sessions.add(session);
				}
			}

			logger.debug("getArchiveSesssions exit -get sessions:" + sessions);

			return sessions;
		} finally{
			//zxh,cut connection
			if (bNeedCutConnection) {				
				long lCutVal = WSJNI.AFCutConnection(jnetConnInfo, false);
				if (0 != lCutVal) {
					logger.error("WSJNI.AFCutConnection failed,return value is " + lCutVal);
				}
			}
			if (null != lock) {
				lock.unlock();
			}
		}
	}
	
	public List<ArchiveJobSession> getArchiveSessionsMore(int scheduleType) throws ServiceException{
		BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();

		ArchiveConfig ac = getArchiveToTapeConfig();

		if (configuration == null || ac == null) {
			logger.debug("getArchiveSesssions - return null for null configuration, configuration=" + configuration + " ,ArchiveConfig:" + ac);
			return null;
		}
		List<ArchiveJobSession> sessions = getArchiveSessionsMore(ac, configuration, scheduleType);
		logger.debug("getArchiveSesssions exit -get sessions:" + sessions);
		return sessions;
		
	}
}
