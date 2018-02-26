package com.ca.arcserve.edge.app.base.common;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.ca.arcflash.webservice.jni.WSJNI;


public class XmlEncrypter extends BaseXmlCrypt{
	public final static XmlEncrypter CommonEncrypter=new XmlEncrypter(new String[]{"uuid", ".*password","encryptionkey"});

	public XmlEncrypter(String[] matchTag) {
		super(matchTag);
	}
	protected String doEncrypt(String s){
		return WSJNI.AFEncryptStringEx(s);
	}

	public String encryptXml_NoException(String xml) {
		return super.analyzeXml_NoException(xml);
	}

	public String encryptXml(String xml) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		return super.analyzeXml(xml);
	}
}
