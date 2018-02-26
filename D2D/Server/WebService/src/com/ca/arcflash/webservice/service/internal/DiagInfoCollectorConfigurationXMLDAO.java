package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;

import com.ca.arcflash.jobqueue.encrypt.Base64;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;

public class DiagInfoCollectorConfigurationXMLDAO {
	
	private static Logger logger = Logger.getLogger(DiagInfoCollectorConfigurationXMLDAO.class);
	JAXBContext jaxbContext;
	
	public DiagInfoCollectorConfigurationXMLDAO() throws JAXBException
	{
		jaxbContext = JAXBContext.newInstance(DiagInfoCollectorConfiguration.class);
	}
			
	synchronized public DiagInfoCollectorConfiguration get(String filePath) throws Exception
	{
		File file = new File(filePath);
		if (!file.exists())
			return null;

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		DiagInfoCollectorConfiguration config = (DiagInfoCollectorConfiguration) jaxbUnmarshaller.unmarshal(file);
		
		return config;
	}

	synchronized public void save(String filePath,
			DiagInfoCollectorConfiguration configuration) throws Exception
	{
		DiagInfoCollectorConfiguration configuration1 = new DiagInfoCollectorConfiguration();
		configuration1.setAdvancedLogCollection(configuration.getAdvancedLogCollection());
		configuration1.setDestinationType(configuration.getDestinationType());
		configuration1.setPassword(configuration.getPassword());
		configuration1.setUploadDestination(configuration.getUploadDestination());
		configuration1.setUserName(configuration.getUserName());
		String originalPwd = configuration1.getPassword();
		if(originalPwd!=null && !originalPwd.isEmpty()){
			configuration1.setPassword(Base64.encode(originalPwd));
		}
		File file = new File(filePath);
		
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		jaxbMarshaller.marshal(configuration1, file);
	}
	
}