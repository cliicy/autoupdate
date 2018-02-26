package com.ca.arcserve.edge.app.base.common;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.ca.arcflash.webservice.jni.WSJNI;


public class XmlDecrypter extends BaseXmlCrypt{
	public final static XmlDecrypter CommonDecrypter=new XmlDecrypter(new String[]{"uuid", ".*password","encryptionkey"});

	public XmlDecrypter(String[] matchTag) {
		super(matchTag);
	}

	@Override
	protected String doEncrypt(String s) {
		return WSJNI.AFDecryptStringEx(s);
	}
	
	public String decryptXml_NoException(String xml) {
		return super.analyzeXml_NoException(xml);
	}

	public String decryptXml(String xml) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		return super.analyzeXml(xml);
	}
}
