package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class XMLMarshalUtil {

	private XMLMarshalUtil() {
		throw new UnsupportedOperationException();
	}

	public static Object unmarshal(String contextPath, InputStream xmlStream)
			throws JAXBException {
		
		if(xmlStream == null || contextPath == null)
			return null;
		
		JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
		Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();
		return unMarshaller.unmarshal(xmlStream);
	}
}
