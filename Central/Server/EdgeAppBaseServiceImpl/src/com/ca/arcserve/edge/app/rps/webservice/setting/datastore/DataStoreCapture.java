package com.ca.arcserve.edge.app.rps.webservice.setting.datastore;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfoXML;

public class DataStoreCapture {

	private static final Logger logger = Logger.getLogger(DataStoreCapture.class);
	
	String xmlsetting;

	public String getSetting() {
		return xmlsetting;
	}

	public DataStoreSettingInfoXML getObjectFromXmlString(String strXml) {
		DataStoreSettingInfoXML settingxml = new DataStoreSettingInfoXML();
		settingxml = JAXB.unmarshal(new StringReader(strXml),
				DataStoreSettingInfoXML.class);

		return settingxml;
	}

	public boolean writexml(DataStoreSettingInfoXML settingInfo) {
		// FileOutputStream fos = null;
		try {
			// JAXB
			// fos = new FileOutputStream("dedupesetting.xml");

			JAXBContext jaxbContext = JAXBContext
					.newInstance(DataStoreSettingInfoXML.class);
			Marshaller marsher = jaxbContext.createMarshaller();
			marsher.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(
					true));
			Writer stw = new StringWriter();
			// marsher.marshal(settingInfo, fos);
			marsher.marshal(settingInfo, stw);
			xmlsetting = stw.toString();
			// xmlsetting = fos.toString();

			// fos.close();

			return true;
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);

			// try {
			// fos.close();
			// } catch (IOException e1) {
			// }

			// File file = new File("dedupesetting.xml");
			// if(file.isFile() && file.exists()){
			// file.delete();
			// }

			return false;
		}
	}

}
