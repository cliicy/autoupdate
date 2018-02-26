package com.ca.arcflash.webservice.service.internal;

import org.w3c.dom.Document;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.util.ScheduledExportConfigXMLParser;

public class ScheduledExportConfigurationXMLDAO extends XMLDAO {
	private ScheduledExportConfigXMLParser scheduledExportConfigXMLParser = new ScheduledExportConfigXMLParser();

	synchronized public void save(String filePath,
			ScheduledExportConfiguration configuration) throws Exception {
		String plainDestPassword = null;
		String plainEncryptionKey = null;
		
		NativeFacade nativeFacade = CommonService.getInstance().getNativeFacade();
		if(!StringUtil.isEmptyOrNull(configuration.getDestPassword())) {
			plainDestPassword = configuration.getDestPassword();
			configuration.setDestPassword(nativeFacade.encrypt(configuration.getDestPassword()));
		}
		
		if(!StringUtil.isEmptyOrNull(configuration.getEncryptionKey())) {
			plainEncryptionKey = configuration.getEncryptionKey();
			configuration.setEncryptionKey(nativeFacade.encrypt(configuration.getEncryptionKey()));
		}
		Document xmlDocument = scheduledExportConfigXMLParser
				.saveXML(configuration);
		
		if(plainDestPassword != null) {
			configuration.setDestPassword(plainDestPassword);
		}
		
		if(plainEncryptionKey != null) {
			configuration.setEncryptionKey(plainEncryptionKey);
		}
		
		doc2XmlFile(xmlDocument, filePath);
	}
	
	synchronized public ScheduledExportConfiguration get(String filePath) throws Exception {

		ScheduledExportConfiguration scheduledExportConfiguration = scheduledExportConfigXMLParser.loadXML(filePath);
		NativeFacade nativeFacade = CommonService.getInstance().getNativeFacade();
		if(!StringUtil.isEmptyOrNull(scheduledExportConfiguration.getDestPassword())) {
			scheduledExportConfiguration.setDestPassword(nativeFacade.decrypt(scheduledExportConfiguration.getDestPassword()));
		}
		
		if(!StringUtil.isEmptyOrNull(scheduledExportConfiguration.getEncryptionKey())) {
			scheduledExportConfiguration.setEncryptionKey(nativeFacade.decrypt(scheduledExportConfiguration.getEncryptionKey()));
		}
		
		return scheduledExportConfiguration;
	}

}
