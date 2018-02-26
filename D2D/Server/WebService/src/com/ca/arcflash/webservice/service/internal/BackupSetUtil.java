package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class BackupSetUtil {
	private static Logger logger = Logger.getLogger(BackupSetUtil.class);
	
	/**
	 * This method is only for test use, called by NativeFacade. Only support for local destination.
	 * @param destination
	 * @param sessionNumber
	 * @param flag
	 */
	public static void setBackupSetFlag(String destination, int sessionNumber, int flag) {
		
		File file = new File(destination + "/VStore/" + getRPPathBySessionID(destination, sessionNumber) + "\\BackupInfo.XML");
		if(!file.exists()) return;
		
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(file);
			Element rootElement = (Element) doc.getElementsByTagName("BackupInfo").item(0);
			Element backupDetail = (Element) rootElement.getElementsByTagName("BackupDetail").item(0);
			Attr attr = backupDetail.getAttributeNode("BackupSetFlag");
			if(attr == null) attr = doc.createAttribute("BackupSetFlag");
			attr.setTextContent(String.valueOf(flag));
			backupDetail.setAttributeNode(attr);
			
			TransformerFactory tsf = TransformerFactory.newInstance();
			Transformer ts = tsf.newTransformer();
			FileOutputStream fos = new FileOutputStream(file);
			ts.transform(new DOMSource(doc), new StreamResult(fos));
			fos.close();
		}catch(Exception e) {
			logger.error("Failed to set backup set flag");
		}
	}
	
	public static int getBackupSetFlag(String destination, int sessionNumber) {
		File file = new File(destination + "/VStore/" 
				+ getRPPathBySessionID(destination,sessionNumber)  + "\\BackupInfo.XML");
		if(!file.exists()) return 0;
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			FileInputStream fis = new FileInputStream(file);
			String value = xpath.evaluate("/BackupInfo/BackupDetail/@BackupSetFlag", new InputSource(fis));
			if(value == null || value.isEmpty()) return 0;
			return Integer.valueOf(value);
		}catch(Exception e) {
			logger.error("Failed to get backup set flag for " + sessionNumber);
		}
		return 0;
	}
	
	private static String getRPPathBySessionID(String destination, int sessionNumber) {
		StringBuilder strNumber = new StringBuilder(String.valueOf(sessionNumber));
		for(int i = strNumber.length(); i < 10; i ++) {
			strNumber.insert(0, '0');
		}
		strNumber.insert(0, "S");
		return strNumber.toString();
	}
	
	/**
	 * This is a complex code to change the flag for all old reocvery points.
	 * 
	 * Map<String, List<RecoveryPoint>> rpByDate = new HashMap<String, List<RecoveryPoint>>();
			for(RecoveryPoint rp : allRps) {
				if(rp.getBackupType() == BackupType.Full) {
					Date time = rp.getTime();
					String strTime = genBackupSetFlagKey(time);
					List<RecoveryPoint> listRp = rpByDate.get(strTime);
					if(listRp == null){
						listRp = new ArrayList<RecoveryPoint>();
						rpByDate.put(strTime, listRp);
					}
					listRp.add(rp);
					
				}
			}
			Map<String, RecoveryPoint> flags = new HashMap<String, RecoveryPoint>();
			if(policy.isStartWithFirst()) {
				for(List<RecoveryPoint> listRP : rpByDate.values()) {
					RecoveryPoint rp = listRP.get(0);
					Calendar cal = Calendar.getInstance();
					cal.setTime(rp.getTime());
					if(!isBackupSetStartDay(policy, cal)) {
						//mark backup set flag is there is no for its backup set and unmark unnecessary ones.
						Date startDate = getCurrentBackupStartDate(policy, cal);
						String key = genBackupSetFlagKey(startDate);
						if(flags.containsKey(key)) {
							if(rp.getBackupSetFlag() > 0) {
								unmarkCurrentFlag(configuration, rp);
							}
						}else {
							flags.put(genBackupSetFlagKey(rp.getTime()), rp);
							if(listRP.size() > 1)
								changeFlag(configuration, rp, listRP.subList(0, listRP.size() - 1));
						}
					}else {
						//mark backup set flag if needed.
						flags.put(genBackupSetFlagKey(rp.getTime()), rp);
						if(listRP.size() > 1) {
							changeFlag(configuration, rp, listRP.subList(1, listRP.size()));
						}
					}
				}
			}else {
				for(List<RecoveryPoint> listRP : rpByDate.values()) {
					RecoveryPoint rp = listRP.get(0);
					Calendar cal = Calendar.getInstance();
					cal.setTime(rp.getTime());
					if(!isBackupSetStartDay(policy, cal)) {
						//mark backup set flag is there is no for its backup set and unmark unnecessary ones.
						Date startDate = getCurrentBackupStartDate(policy, cal);
						String key = genBackupSetFlagKey(startDate);
						if(flags.containsKey(key)) {
							if(rp.getBackupSetFlag() > 0) {
								unmarkCurrentFlag(configuration, rp);
							}
						}else {
							flags.put(genBackupSetFlagKey(rp.getTime()), rp);
							changeFlag(configuration, rp, listRP);
						}
					}else {
						//mark backup set flag if needed.
						RecoveryPoint lastOne = listRP.get(listRP.size() - 1);
						flags.put(genBackupSetFlagKey(lastOne.getTime()), lastOne);
						List<RecoveryPoint> toUnmark = listRP.subList(0, listRP.size() - 1);
						changeFlag(configuration, rp, toUnmark);
					}
				}
			}
			
			
	private static void changeFlag(BackupConfiguration configuration, 
			RecoveryPoint rpToMark, List<RecoveryPoint> listToUnmark) {
		if(rpToMark.getBackupSetFlag() <= 0) {
			rpToMark.setBackupSetFlag(1);
			markBackupSetFlag(configuration, rpToMark);
			
		}
		
		for(RecoveryPoint rp : listToUnmark) {
			if(rp.getBackupSetFlag() > 0) {
				unmarkCurrentFlag(configuration, rp);
			}
		}
	}
	
	private static String genBackupSetFlagKey(Date date) {
		StringBuilder sb = new StringBuilder(date.getYear());
		sb.append("-");
		sb.append(date.getMonth());
		sb.append("-");
		sb.append(date.getDate());
		return sb.toString();
	}
	 */
}
