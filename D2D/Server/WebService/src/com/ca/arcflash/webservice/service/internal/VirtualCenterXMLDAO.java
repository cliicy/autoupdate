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
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.util.VirtualCenterXMLParser;

public class VirtualCenterXMLDAO extends XMLDAO {

	private VirtualCenterXMLParser virtualCenterXMLParser = new VirtualCenterXMLParser();

	public VirtualCenterXMLDAO() {

	}

	synchronized public VirtualCenter[] getVirtualCenters(String filePath)
			throws Exception {
		return getVirtualCenters(filePath, true);
	}

	synchronized public VirtualCenter[] getVirtualCenters(String filePath,
			boolean decrypted) throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			return new VirtualCenter[0];

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		List<VirtualCenter> vcList = virtualCenterXMLParser.loadXML(doc);
		if (decrypted) {
			for (VirtualCenter vc : vcList) {
				vc.setPassword(CommonService.getInstance().getNativeFacade()
						.decrypt(vc.getPassword()));
			}
		}

		return vcList.toArray(new VirtualCenter[0]);
	}

	synchronized public void addVirtualCenter(String filePath, VirtualCenter vc)
			throws Exception {
		VirtualCenter[] existVcs = getVirtualCenters(filePath, false);
		List<VirtualCenter> vcList;

		if (existVcs != null && existVcs.length > 0) {
			vcList = new ArrayList<VirtualCenter>(Arrays.asList(existVcs));
			boolean find = false;
			for (VirtualCenter item : vcList) {
				if (item.getVcName().equals(vc.getVcName())) {
					item.setPort(vc.getPort());
					item.setUsername(vc.getUsername());
					item.setPassword(CommonService.getInstance()
							.getNativeFacade().encrypt(vc.getPassword()));
					item.setProtocol(vc.getProtocol());
					find = true;
				}
			}

			if (!find) {
				vc.setPassword(CommonService.getInstance().getNativeFacade()
						.encrypt(vc.getPassword()));
				vcList.add(vc);
			}
		} else {
			vcList = new ArrayList<VirtualCenter>(1);
			vc.setPassword(CommonService.getInstance().getNativeFacade()
					.encrypt(vc.getPassword()));
			vcList.add(vc);
		}

		Document xmlDocument = virtualCenterXMLParser.saveXML(vcList
				.toArray(new VirtualCenter[0]));
		doc2XmlFile(xmlDocument, filePath);
	}

	synchronized public void remove(String filePath, VirtualCenter host,
			NativeFacade nativeFacade) throws Exception {
		VirtualCenter[] existVcs = getVirtualCenters(filePath, false);
		List<VirtualCenter> vcList = new LinkedList<VirtualCenter>();

		if (existVcs != null && existVcs.length > 0) {
			for (VirtualCenter item : existVcs) {
				if (!item.getVcName().equals(host.getVcName())) {
					vcList.add(item);
				}
			}
		}

		Document xmlDocument = virtualCenterXMLParser.saveXML(vcList
				.toArray(new VirtualCenter[0]));
		doc2XmlFile(xmlDocument, filePath);
	}

}
