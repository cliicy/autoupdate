package com.ca.arcflash.webservice.service.internal;

import java.io.File;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;

public class XMLDAO {
	
	private static final Logger logger = Logger.getLogger(XMLDAO.class);
	private static final long RESERVE_SIZE = (long)1024*1024;//reserve 1MB for xml configuration file
	
	protected void doc2XmlFile(Document document, String filename) throws Exception{
		File file = new File(filename);
		//To avoid no disk space makes xml configuration to 0kb
		if(file.exists()){
			checkFreeSpace(file);
		}else{
			file.createNewFile();
			try{
				checkFreeSpace(file);
			}finally{
				file.delete();
			}
			
		}
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
	}
	
	private void checkFreeSpace(File file) throws Exception{
		if(file.getFreeSpace() < RESERVE_SIZE){
			logger.debug("disk size is less than 1MB");
			throw new ServiceException(FlashServiceErrorCode.Common_Disk_Free_Size_Too_Low, 
					new Object[]{ServiceContext.getInstance().getProductNameD2D()});
		}
	}
}
