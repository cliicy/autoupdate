package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import com.ca.arcflash.common.CommonUtil;

public class ConversionHistoryUtil {

	private String xmlFile;
	private ConversionHistory history;

	public ConversionHistoryUtil(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public boolean load() {
		if (xmlFile == null)
			return false;
		File f = new File(xmlFile);
		try {
			history = CommonUtil.unmarshal(f, ConversionHistory.class);
		} catch (Exception e) {
			history = new ConversionHistory();
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

	public synchronized void updateLastConversion(String afGuid, String vmTag,
			String backupDest, String lastSession) {
		List<ConversionSources> all = this.history.getNodes();
		ConversionSources sources = null;
		ConversionSource source = null;
		for (ConversionSources i : all) {
			if (i.getAfGuid().equalsIgnoreCase(afGuid)) {
				sources = i;
				for (ConversionSource j : i.getSources()) {
					if (j.getBackupDest().equalsIgnoreCase(backupDest)
							&& j.getVmTag().equalsIgnoreCase(vmTag)) {
						source = j;
						j.setLastSession(lastSession);
						this.save();
						return;
					}
				}
			}
		}
		if (source == null) {
			source = new ConversionSource();
			source.setBackupDest(backupDest);
			source.setLastSession(lastSession);
			source.setVmTag(vmTag);
		}
		if (sources != null) {
			sources.getSources().add(source);
		} else {
			sources = new ConversionSources();
			sources.getSources().add(source);
			sources.setAfGuid(afGuid);
			this.history.getNodes().add(sources);
		}
		this.save();
	}

	public synchronized String getLastConvertedSession(String afGuid,
			String vmTag, String backupDest) {
		List<ConversionSources> all = this.history.getNodes();
		for (ConversionSources i : all) {
			if (i.getAfGuid().equalsIgnoreCase(afGuid)) {
				for (ConversionSource j : i.getSources()) {
					if (j.getBackupDest().equalsIgnoreCase(backupDest)
							&& j.getVmTag().equalsIgnoreCase(vmTag)) {
						return j.getLastSession();
					}
				}
			}
		}
		return null;
	}

	public static ConversionHistoryUtil getDefaultConversionHistory() {
		ConversionHistoryUtil history = new ConversionHistoryUtil(
				CommonUtil.D2DInstallPath
						+ "Configuration\\ConversionHistory.xml");
		if (!history.load())
			return null;
		return history;
	}
}
