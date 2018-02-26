package com.ca.arcserve.edge.app.base.schedulers;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.PurgeSetting;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class PurgeService implements Runnable {
	
	private static Logger logger = Logger.getLogger(PurgeService.class);
	private static PurgeService instance = new PurgeService();
	private static IEdgeJobHistoryDao jobHistoryDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);
	
	private static final String PURGE_FILE_NAME = "PurgeSetting.xml";
	
	private PurgeSetting purgeSetting;
	
	private PurgeService() {
	}
	
	public static PurgeService getInstance() {
		return instance;
	}
	
	public synchronized void initialize(ScheduledExecutorService executor) {
		logger.debug("Purge service - initialize start.");
		
		this.purgeSetting = getPurgeSetting();
		
		long initialDelay = getInitialDelaySeconds(purgeSetting);
		if (logger.isDebugEnabled()) {
			long hours = initialDelay / 3600;
			long minutes = (initialDelay % 3600) / 60;
			long seconds = initialDelay % 60;
			String formatTime = hours + ":" + minutes + ":" + seconds;
			logger.debug("Purge service - wait for " + initialDelay + " seconds (" + formatTime + ")to start the first purge task.");
		}
		long period = 3600;
		
		executor.scheduleAtFixedRate(PurgeService.getInstance(), initialDelay, period, TimeUnit.SECONDS);
		
		logger.debug("Purge service - initialize end.");
	}
	
	private long getInitialDelaySeconds(PurgeSetting purgeSetting) {
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.clear(Calendar.MILLISECOND);
		
		Calendar firstPurgeCalendar = getDateCalendar(currentCalendar);
		int purgeHourOfDay = purgeSetting.getPurgeHourOfDay();
		firstPurgeCalendar.add(Calendar.HOUR_OF_DAY, purgeHourOfDay);
		
		if (firstPurgeCalendar.compareTo(currentCalendar) < 0) {
			firstPurgeCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return (firstPurgeCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis()) / 1000;
	}
	
	private Calendar getDateCalendar(Calendar dateTimeCalendar) {
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.clear();
		dateCalendar.set(dateTimeCalendar.get(Calendar.YEAR), dateTimeCalendar.get(Calendar.MONTH), dateTimeCalendar.get(Calendar.DATE));
		return dateCalendar;
	}

	private PurgeSetting getPurgeSetting() {
		PurgeSetting setting;
		
		String configFolder = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement);
		File purgeFile = new File(configFolder + PURGE_FILE_NAME);
		
		if (!purgeFile.exists()) {
			logger.debug("Purge service - purge setting file does not exist, path = " + purgeFile.getPath());
			setting = new PurgeSetting();
			
			try {
				JAXB.marshal(setting, purgeFile);
			} catch (Exception e) {
				logger.error("Purge service - marshal default purge setting failed.", e);
			}
		} else {
			logger.debug("Purge service - load purge setting from xml file.");
			
			try {
				setting = JAXB.unmarshal(purgeFile, PurgeSetting.class);
			} catch (Exception e) {
				logger.error("Purge service - unmarshal purge setting from xml file failed.", e);
				setting = new PurgeSetting();
			}
		}
		
		if (setting.getPurgeHourOfDay() < 0 || setting.getPurgeHourOfDay() > 23) {
			setting.setPurgeHourOfDay(0);
		}
		
		return setting;
	}

	@Override
	public void run() {
		logger.debug("Purge service - task begin.");
		
		int currentHourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (currentHourOfDay != purgeSetting.getPurgeHourOfDay()) {
			logger.debug("Purge service - not the purge hour, task end.");
			return;
		}
		
		addLog(EdgeCMWebServiceMessages.getMessage("purgeServiceStart"));
		
		Calendar calendar = getDateCalendar(Calendar.getInstance());
		calendar.add(Calendar.DAY_OF_MONTH, -purgeSetting.getRetentionDays());
		
		logger.debug("Purge service - retention date = " + calendar.getTime());
		
		try {
			jobHistoryDao.as_edge_d2dJobHistory_purge(calendar.getTime());
		} catch (Throwable e) {
			logger.error("Purge service - task failed.", e);
		} finally {
			addLog(EdgeCMWebServiceMessages.getMessage("purgeServiceEnd"));
		}
		
		logger.debug("Purge service - task end.");
	}
	
	private void addLog(String message) {
		IActivityLogService logService = new ActivityLogServiceImpl();
		
		try {
			logService.addUnifiedLog(LogAddEntity.create(Severity.Information, message));
		} catch (EdgeServiceFault e) {
			logger.error("Purge service - add log failed, error message = " + e.getMessage(), e);
		}
	}

}
