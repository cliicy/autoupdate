package com.ca.arcflash.webservice.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.service.exception.ServiceException;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveSourceItem;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveUtils;
import com.ca.arcflash.webservice.service.ServiceContext;

public class VSphereArchiveToTapeUtils {
	private static final Logger logger = Logger.getLogger(VSphereArchiveToTapeUtils.class);
	
	private static String getArchiveToTapeFilePath(String vmInstanceUUID) throws ServiceException {
		String archive2TapeFile = "";
		String dataFolder = ServiceContext.getInstance().getDataFolderPath();
		archive2TapeFile = dataFolder + "/a2t_" + vmInstanceUUID + ".xml";		
		return archive2TapeFile;
	}
	
	public static void saveArchiveToTape(String vmInstanceUUID, ArchiveConfig archiveToTapeConfig) {
		if (archiveToTapeConfig == null)
			return;
		try {
			File cfgFile = new File(getArchiveToTapeFilePath(vmInstanceUUID));
			try {
				if (!cfgFile.exists()) {
					cfgFile.createNewFile();
				}
			} catch (IOException e) {
				logger.info("failed to create file:" + cfgFile.getAbsolutePath()
						+ "," + e.getMessage());
			}
	
			ArchiveSourceItem original = null;	
			
			if (archiveToTapeConfig.getSource() != null && archiveToTapeConfig.getSource().getSourceItems() != null
					&& archiveToTapeConfig.getSource().getSourceItems().size() > 0) {
				original = archiveToTapeConfig.getSource().getSourceItems().get(0);
			}
	
			ArchiveSourceItem newOne = null;
	
			if (archiveToTapeConfig.getSource() != null && archiveToTapeConfig.getSource().getSourceItems() != null
					&& archiveToTapeConfig.getSource().getSourceItems().size() > 0) {
				newOne = archiveToTapeConfig.getSource().getSourceItems().get(0);
			}
	
			if (newOne != null) {
				if (ArchiveUtils.isArchiveSourceChanged(original, newOne) || original == null || original.getConfigTime() == 0) {
					newOne.setConfigTime(System.currentTimeMillis());
				} else {
					newOne.setConfigTime(original.getConfigTime());
				}
			}
			JAXB.marshal(archiveToTapeConfig, cfgFile);		
		} catch(ServiceException e) {
			
		}
	}

	public static ArchiveConfig loadArchiveToTape(String vmInstanceUUID) {
		ArchiveConfig config = null;
		try {
			File f = new File(getArchiveToTapeFilePath(vmInstanceUUID));
			if (f.exists()) {
				config = JAXB.<ArchiveConfig> unmarshal(f, ArchiveConfig.class);
			}
		} catch(ServiceException e) {}
		return config;
	}

	public static void removeArchiveToTape(String vmInstanceUUID) {
		try {
			File f = new File(getArchiveToTapeFilePath(vmInstanceUUID));
			if (f.exists()) {
				f.delete();
			}
		} catch(ServiceException e) {}
	}

	public static ArchiveConfig getArchiveToTapeConfig(String vmInstanceUUID) {
		return loadArchiveToTape(vmInstanceUUID);
	}
}
