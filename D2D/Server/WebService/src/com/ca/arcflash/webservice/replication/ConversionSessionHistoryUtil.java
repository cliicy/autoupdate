package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.bind.JAXB;

import com.ca.arcflash.common.CommonUtil;

public class ConversionSessionHistoryUtil {
	private String xmlFile;
	private ConversionSessionHistory history;

	public ConversionSessionHistoryUtil(String fileName) {
		this.xmlFile = fileName;
	}
	
	public ConversionSessionHistory getHistory() {
		if (history == null)
			load();
		return history;
	}

	public boolean load() {
		if (xmlFile == null)
			return false;
		File f = new File(xmlFile);
		try {
			history = CommonUtil.unmarshal(f, ConversionSessionHistory.class);
		} catch (Exception e) {
			history = new ConversionSessionHistory();
		}
		return true;
	}

	public boolean save() {
		File f = new File(xmlFile);
		FileOutputStream fos = null;
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			JAXB.marshal(history, fos);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public boolean addItem(ConversionSessionItem item) {
		if (history == null)
			load();
		List<ConversionSessionItem> items = history.getItems();
		items.add(item);
		return save();
	}

	public static String getFilePathFromBackupDestination(String backupDest) {
		return backupDest + "\\VCM-bitmap\\ConversionSessionHistory.xml";
	}
}
