package com.ca.arcflash.webservice.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveJobSession;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.util.VSphereArchiveToTapeUtils;

public class VSphereArchiveToTapeService extends ArchiveToTapeService {
	private static final Logger logger = Logger.getLogger(VSphereArchiveToTapeService.class);
	private static final VSphereArchiveToTapeService instance = new VSphereArchiveToTapeService();

	public static VSphereArchiveToTapeService getInstance() {
		return instance;
	}

	public ArchiveConfig getArchiveToTapeConfig(String vmInstanceUUID) throws ServiceException {
		logger.debug("getArchiveToTapeConfig - start");
		return VSphereArchiveToTapeUtils.getArchiveToTapeConfig(vmInstanceUUID);
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

	public List<Long> getArchiveSesssions(String vmInstanceUUID, int startSessionNo, int scheduleType) throws ServiceException {
		BackupConfiguration configuration =  VSphereService.getInstance().getBackupConfiguration(vmInstanceUUID);

		ArchiveConfig ac = getArchiveToTapeConfig(vmInstanceUUID);

		if (configuration == null || ac == null) {
			logger.debug("getArchiveSesssions - return null for null configuration, configuration=" + configuration + " ,ArchiveConfig:" + ac);
			return null;
		}

		List<Long> sessions = getArchiveSessions(ac, configuration, startSessionNo, scheduleType);

		logger.debug("getArchiveSesssions exit -get sessions:" + sessions);

		return sessions;
	}
	
	public List<ArchiveJobSession> getArchiveSessionsMore(String vmInstanceUUID, int scheduleType) throws ServiceException{
		BackupConfiguration configuration = VSphereService.getInstance().getBackupConfiguration(vmInstanceUUID);

		ArchiveConfig ac = getArchiveToTapeConfig(vmInstanceUUID);

		if (configuration == null || ac == null) {
			logger.debug("getArchiveSesssions - return null for null configuration, configuration=" + configuration + " ,ArchiveConfig:" + ac);
			return null;
		}
		List<ArchiveJobSession> sessions = getArchiveSessionsMore(ac, configuration, scheduleType);
		logger.debug("getArchiveSesssions exit -get sessions:" + sessions);
		return sessions;
	}
	
	public void deleteArchive2TapeConfigurationFile(String vmInstanceUUID) {
		VSphereArchiveToTapeUtils.removeArchiveToTape(vmInstanceUUID);		
	}
}
