package com.ca.arcserve.edge.app.base.webservice.jobhistory;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.BackupInfo;

public class BackupInfoPaser {
	private static final Logger logger = Logger.getLogger(BackupInfoPaser.class);
	
	public static BackupInfo ParseBackupInfo(String xmlContent){
		BackupInfo bkInfo;
		if (StringUtil.isEmptyOrNull(xmlContent)) {
			return null;
		}
		try {
			// JAXB
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcserve.edge.app.base.webservice.d2ddatasync", BackupInfo.class.getClassLoader());
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			JAXBElement<BackupInfo> bkElement = unmarsh.unmarshal(new StreamSource(new  StringReader(xmlContent)), BackupInfo.class);
			bkInfo = bkElement.getValue();

		} catch (Exception e) {
			logger.error("Parse BackupInfo.xml file failed.", e);
			logger.debug("The xml content is: "+xmlContent);
			return null;
		}
		return bkInfo;
	}
	
	public static String decodeBackupInfoXml(String encryptedXml){
		if(StringUtil.isEmptyOrNull(encryptedXml))
			return "";
		encryptedXml = encryptedXml.replaceAll("[\r\n]{2}", "");
		return com.ca.arcflash.jobqueue.encrypt.Base64.decode( encryptedXml );  
	}
}
