package com.ca.arcflash.webservice.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveSourceItem;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveUtils;
import com.ca.arcflash.webservice.service.ServiceContext;

public class ArchiveToTapeUtils {
	private static final Logger logger = Logger.getLogger(ArchiveToTapeUtils.class);
	private static ArchiveConfig archiveToTapeConfig = null;

	public synchronized static void saveArchiveToTape(ArchiveConfig pArchiveToTapeConfig) {
		if (pArchiveToTapeConfig == null)
			return;

		File cfgFile = new File(ServiceContext.getInstance().getArchiveToTapeFilePath());
		if (cfgFile.exists()){
			if (archiveToTapeConfig == null){
				loadArchiveToTape();
			}
		} else{
			try {
				cfgFile.createNewFile();
			} catch (IOException e) {
				logger.info("failed to create file:" + cfgFile.getAbsolutePath() + "," + e.getMessage());
			}
		}
		
		ArchiveSourceItem original = null;	
		
		if (archiveToTapeConfig != null) {
			if (archiveToTapeConfig.getSource() != null && archiveToTapeConfig.getSource().getSourceItems() != null
					&& archiveToTapeConfig.getSource().getSourceItems().size() > 0) {
				original = archiveToTapeConfig.getSource().getSourceItems().get(0);
			}
		}

		ArchiveSourceItem newOne = null;

		if (pArchiveToTapeConfig.getSource() != null && pArchiveToTapeConfig.getSource().getSourceItems() != null
				&& pArchiveToTapeConfig.getSource().getSourceItems().size() > 0) {
			newOne = pArchiveToTapeConfig.getSource().getSourceItems().get(0);
		}

		if (newOne != null) {
			if (ArchiveUtils.isArchiveSourceChanged(original, newOne) || original == null || original.getConfigTime() == 0) {
				newOne.setConfigTime(System.currentTimeMillis());
			} else {
				newOne.setConfigTime(original.getConfigTime());
			}
		}
		
		archiveToTapeConfig = pArchiveToTapeConfig;
		JAXB.marshal(archiveToTapeConfig, cfgFile);		
	}

	public synchronized static ArchiveConfig loadArchiveToTape() {
		ArchiveConfig config = null;
		File f = new File(ServiceContext.getInstance().getArchiveToTapeFilePath());
		if (f.exists()) {
			config = JAXB.<ArchiveConfig> unmarshal(f, ArchiveConfig.class);
			setArchiveToTapeConfig(config);
		}
		return config;
	}

	public synchronized static void removeArchiveToTape() {
		File f = new File(ServiceContext.getInstance().getArchiveToTapeFilePath());
		if (f.exists()) {
			f.delete();
		}

		setArchiveToTapeConfig(null);
	}

	public static ArchiveConfig getArchiveToTapeConfig() {
		return archiveToTapeConfig;
	}

	private static void setArchiveToTapeConfig(ArchiveConfig archiveToTapeConfig) {
		ArchiveToTapeUtils.archiveToTapeConfig = archiveToTapeConfig;
	}
}
