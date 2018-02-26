package com.ca.arcflash.webservice.service.internal;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;

public class ArchiveSourceDeleteConfigurationXMLDAO {
	private Logger logger = Logger.getLogger(ArchiveSourceDeleteConfigurationXMLDAO.class);
	
	private Object lock = new Object();
	private JAXBContext jaxbContext;
	private Marshaller archiveDelConfigMarshaller;
	private Unmarshaller archiveDelConfigUnmarshaller;
	
	public ArchiveSourceDeleteConfigurationXMLDAO(){
		try {
			jaxbContext = JAXBContext.newInstance(ArchiveConfiguration.class);
			archiveDelConfigMarshaller = jaxbContext.createMarshaller();
			archiveDelConfigUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			logger.error("Failed to create archiveDelConfig marshaller and unmarshaller\r\n" + e.getMessage());
		}
	}
	
	public void save(ArchiveConfiguration config, String filePath) throws JAXBException{
		String fileName = getFileName(filePath);
		logger.debug("save - file name is " + fileName);
		File file = new File(filePath);
		synchronized(lock){
			archiveDelConfigMarshaller.marshal(config, file);
		}
		if(file.exists())
			logger.info("File saved successfully, file name = " + fileName);
		else{
			logger.info("File saved failed, file name = " + fileName);
		}
	}
	
	public ArchiveConfiguration load(String filePath) throws JAXBException{
		String fileName = getFileName(filePath);
		logger.debug("load - file name is " + fileName);
		ArchiveConfiguration config = null;
		File file = new File(filePath);
		synchronized(lock){
			config = (ArchiveConfiguration) archiveDelConfigUnmarshaller.unmarshal(file);
		}
		return config;
	}
	
	private String getFileName(String filePath){
		int startIndex = filePath.lastIndexOf("\\");
		if(startIndex + 1 < filePath.length())
			return filePath.substring(startIndex + 1);
		else
			return filePath;
	}
}
