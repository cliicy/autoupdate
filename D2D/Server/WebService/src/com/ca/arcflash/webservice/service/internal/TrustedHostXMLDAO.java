package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.ca.arcflash.webservice.data.TrustedHost;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.util.TrustedHostXMLParser;

public class TrustedHostXMLDAO extends XMLDAO {

	private TrustedHostXMLParser trustedHostXMLParser = new TrustedHostXMLParser();

	public TrustedHostXMLDAO() {
	}

	synchronized public TrustedHost[] getTrustedHosts(String filePath)
			throws Exception {
		return getTrustedHosts(filePath, true);
	}

	synchronized public TrustedHost[] getTrustedHosts(String filePath,
			boolean decrypted) throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			return new TrustedHost[0];

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		List<TrustedHost> hostList = trustedHostXMLParser.loadXML(doc);
		List<TrustedHost> validList = new ArrayList<TrustedHost>();
		
		
		for (TrustedHost host : hostList) {
			if (decrypted) {
				host.setUuid(CommonService.getInstance().getNativeFacade()
						.decrypt(host.getUuid()));
				host.setPassword(CommonService.getInstance().getNativeFacade()
						.decrypt(host.getPassword()));
			}
			
			if(isValidHost(host)){
				validList.add(host);
			}
		}

		return validList.toArray(new TrustedHost[0]);
	}

	synchronized public void addTrustedHost(String filePath, TrustedHost host)
			throws Exception {
		TrustedHost[] existHosts = getTrustedHosts(filePath, false);
		List<TrustedHost> hostList;

		if (existHosts != null && existHosts.length > 0) {
			hostList = new ArrayList<TrustedHost>(Arrays.asList(existHosts));
			boolean find = false;
			for (TrustedHost item : hostList) {
				if (item.getName().equals(host.getName())) {
					item.setPort(host.getPort());
					item.setUuid(CommonService.getInstance().getNativeFacade()
							.encrypt(host.getUuid()));
					item.setPassword(CommonService.getInstance()
							.getNativeFacade().encrypt(host.getPassword()));
					item.setProtocol(host.getProtocol());
					item.setD2dVersion(host.getD2dVersion());
					find = true;
				}
			}

			if (!find) {
				host.setUuid(CommonService.getInstance().getNativeFacade()
						.encrypt(host.getUuid()));
				host.setPassword(CommonService.getInstance().getNativeFacade()
						.encrypt(host.getPassword()));
				hostList.add(host);
			}
		} else {
			hostList = new ArrayList<TrustedHost>(1);
			host.setUuid(CommonService.getInstance().getNativeFacade().encrypt(
					host.getUuid()));
			host.setPassword(CommonService.getInstance().getNativeFacade()
					.encrypt(host.getPassword()));
			hostList.add(host);
		}

		Document xmlDocument = trustedHostXMLParser.saveXML(hostList
				.toArray(new TrustedHost[0]));
		doc2XmlFile(xmlDocument, filePath);
	}

	synchronized public void remove(String filePath, TrustedHost host,
			NativeFacade nativeFacade) throws Exception {
		TrustedHost[] existHosts = getTrustedHosts(filePath, false);
		List<TrustedHost> hostList = new LinkedList<TrustedHost>();

		if (existHosts != null && existHosts.length > 0) {
			for (TrustedHost item : existHosts) {
				if (!item.getName().equals(host.getName())) {
					hostList.add(item);
				}
			}
		}

		Document xmlDocument = trustedHostXMLParser.saveXML(hostList
				.toArray(new TrustedHost[0]));
		doc2XmlFile(xmlDocument, filePath);
	}
	
	private boolean isValidHost(TrustedHost host) {
		if(host.getD2dVersion() <= 0)
			return false;
		
		if(host.getName() == null || host.getName().isEmpty())
			return false;
		
		if(host.getPassword() == null || host.getPassword().isEmpty())
			return false;
		
		if(host.getPort() <= 0)
			return false;
		
		if(host.getProtocol() == null || host.getProtocol().isEmpty())
			return false;
		
		if(host.getType() <= 0)
			return false;
		
		if(host.getUserName() == null || host.getUserName().isEmpty())
			return false;
		
		if(host.getUuid() == null || host.getUuid().isEmpty())
			return false;
		
		return true;
	}
}
