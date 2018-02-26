package com.ca.arcflash.webservice.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ca.arcflash.common.xml.XMLXPathReader;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.CommonService;

public class SubscriptionConfigXMLParser {

	private static final Logger logger = Logger
			.getLogger(SubscriptionConfigXMLParser.class);
	private Document m_subscriptionXMLdoc;

	public SubscriptionConfigXMLParser() {
		m_subscriptionXMLdoc = null;
	}

	public Document saveXML(SubscriptionConfiguration config) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		m_subscriptionXMLdoc = db.newDocument();

		if (m_subscriptionXMLdoc == null)
			return null;

		Element RootElement = m_subscriptionXMLdoc
				.createElement("SubscriptionConfig");
		m_subscriptionXMLdoc.appendChild(RootElement);

		Element SubscriptionElement = m_subscriptionXMLdoc
				.createElement("Subscription");
		Element cloudVendorType = m_subscriptionXMLdoc
				.createElement("VendorType");
		cloudVendorType.setTextContent(Long.toString(config
				.getCloudVendorType()));
		SubscriptionElement.appendChild(cloudVendorType);
		Element cloudVendorURL = m_subscriptionXMLdoc
				.createElement("VendorURL");
		cloudVendorURL.setTextContent(config.getCloudVendorURL());
		SubscriptionElement.appendChild(cloudVendorURL);
		Element cloudVendorUsername = m_subscriptionXMLdoc
				.createElement("VendorUserName");
		cloudVendorUsername.setTextContent(config.getUserName());
		SubscriptionElement.appendChild(cloudVendorUsername);
		Element cloudVendorPassword = m_subscriptionXMLdoc
				.createElement("VendorPassword");
		cloudVendorPassword.setTextContent(CommonService.getInstance()
				.getNativeFacade().encrypt(config.getPassword()));
		SubscriptionElement.appendChild(cloudVendorPassword);
		Element cloudVendorRegion = m_subscriptionXMLdoc
				.createElement("VendorRegion");
		cloudVendorRegion.setTextContent(config.getRegion());
		SubscriptionElement.appendChild(cloudVendorRegion);
		Element cloudStorageKey = m_subscriptionXMLdoc
				.createElement("StorageKey");
		cloudStorageKey.setTextContent(CommonService.getInstance()
				.getNativeFacade().encrypt(config.getStorageKey()));
		SubscriptionElement.appendChild(cloudStorageKey);

		// integrity,to verify the integrity of config xml
		Element integrity = m_subscriptionXMLdoc.createElement("Integrity");
		integrity.setTextContent(generateLocalMachineIntegrity(config
				.getStorageKey()));
		SubscriptionElement.appendChild(integrity);

		Element serverName = m_subscriptionXMLdoc.createElement("ServerName");
		serverName.setTextContent(config.getServerName());
		SubscriptionElement.appendChild(serverName);

		Element deviceIP = m_subscriptionXMLdoc.createElement("DeviceIP");
		deviceIP.setTextContent(config.getDeviceIP());
		SubscriptionElement.appendChild(deviceIP);

		Element deviceType = m_subscriptionXMLdoc.createElement("DeviceType");
		deviceType.setTextContent("" + config.getDeviceType());
		SubscriptionElement.appendChild(deviceType);

		// Proxy Details
		Element ProxyDetail = m_subscriptionXMLdoc.createElement("Proxy");
		ProxyDetail.setAttribute("Enabled",
				Boolean.toString(config.isCloudUseProxy()));
		ProxyDetail.setAttribute("RequiresAuth",
				Boolean.toString(config.isCloudProxyRequireAuth()));
		if (config.isCloudUseProxy()) {
			Element ProxyServerName = m_subscriptionXMLdoc
					.createElement("ServerName");
			ProxyServerName.setTextContent(config.getCloudProxyServerName());
			ProxyDetail.appendChild(ProxyServerName);

			Element ProxyPort = m_subscriptionXMLdoc.createElement("Port");
			ProxyPort.setTextContent(Long.toString(config.getCloudProxyPort()));
			ProxyDetail.appendChild(ProxyPort);

			if (config.isCloudProxyRequireAuth()) {
				Element ProxyUserName = m_subscriptionXMLdoc
						.createElement("UserName");
				ProxyUserName.setTextContent(config.getCloudProxyUserName());
				ProxyDetail.appendChild(ProxyUserName);

				Element ProxyPassword = m_subscriptionXMLdoc
						.createElement("Password");
				ProxyPassword.setTextContent(CommonService.getInstance()
						.getNativeFacade()
						.encrypt(config.getCloudProxyPassword()));
				ProxyDetail.appendChild(ProxyPassword);
			}
		}
		SubscriptionElement.appendChild(ProxyDetail);
		RootElement.appendChild(SubscriptionElement);

		return m_subscriptionXMLdoc;
	}

	public SubscriptionConfiguration loadXML(Document xmlDocument) {
		XMLXPathReader subscriptionXMLXPathReader = new XMLXPathReader(
				xmlDocument);
		subscriptionXMLXPathReader.Initialise();

		return loadXML(subscriptionXMLXPathReader);
	}

	public SubscriptionConfiguration loadXML(
			String in_archiveConfigurationFilePath) {
		XMLXPathReader subscriptionXMLXPathReader = new XMLXPathReader(
				in_archiveConfigurationFilePath);
		subscriptionXMLXPathReader.Initialise();

		return loadXML(subscriptionXMLXPathReader);
	}

	private SubscriptionConfiguration loadXML(XMLXPathReader xpathReader) {
		SubscriptionConfiguration config = null;

		// ArchiveSourceInfoConfiguration[] FileCopySources = new
		// ArchiveSourceInfoConfiguration[iFileCopyPolicies];
		// int iFileCopyIndex = iPolicyIndex;

		String sXRootPath = "/SubscriptionConfig/Subscription";
		Object SubscriptionElement = xpathReader.readXPath(sXRootPath,
				XPathConstants.STRING);

		if (SubscriptionElement != null) {
			config = new SubscriptionConfiguration();

			String sXPathCloudVendorType = sXRootPath + "/VendorType";
			Object ObjCloudVendorType = xpathReader.readXPath(
					sXPathCloudVendorType, XPathConstants.STRING);
			if (ObjCloudVendorType != null) {
				config.setCloudVendorType(Integer.parseInt(ObjCloudVendorType
						.toString()));
			}

			String sXPathCloudVendorURL = sXRootPath + "/VendorURL";
			Object ObjDestinationVendorURL = xpathReader.readXPath(
					sXPathCloudVendorURL, XPathConstants.STRING);
			if (ObjDestinationVendorURL != null) {
				config.setCloudVendorURL(ObjDestinationVendorURL.toString());
			}

			String sXPathCloudVendorUsername = sXRootPath + "/VendorUserName";
			Object ObjCloudVendorUsername = xpathReader.readXPath(
					sXPathCloudVendorUsername, XPathConstants.STRING);
			if (ObjCloudVendorUsername != null) {
				config.setUserName(ObjCloudVendorUsername.toString());
			}

			String sXPathCloudVendorPassword = sXRootPath + "/VendorPassword";
			Object ObjCloudVendorPassword = xpathReader.readXPath(
					sXPathCloudVendorPassword, XPathConstants.STRING);
			if (ObjCloudVendorPassword != null) {
				config.setPassword(CommonService.getInstance()
						.getNativeFacade()
						.decrypt(ObjCloudVendorPassword.toString()));
			}

			String sXPathCloudVendorRegion = sXRootPath + "/VendorRegion";
			Object ObjCloudVendorRegion = xpathReader.readXPath(
					sXPathCloudVendorRegion, XPathConstants.STRING);
			if (ObjCloudVendorRegion != null) {
				config.setRegion(ObjCloudVendorRegion.toString());
			}

			String sXPathCloudStorageKey = sXRootPath + "/StorageKey";
			Object ObjCloudStorageKey = xpathReader.readXPath(
					sXPathCloudStorageKey, XPathConstants.STRING);
			if (ObjCloudStorageKey != null) {
				config.setStorageKey(CommonService.getInstance()
						.getNativeFacade()
						.decrypt(ObjCloudStorageKey.toString()));
			}

			String sXPathIntegrity = sXRootPath + "/Integrity";
			Object ObjIntegrity = xpathReader.readXPath(sXPathIntegrity,
					XPathConstants.STRING);
			String integrity = generateLocalMachineIntegrity(config
					.getStorageKey());
			logger.debug("config integrity: " + ObjIntegrity.toString());
			logger.debug("current integrity: " + integrity);
			if (ObjIntegrity == null
					|| !integrity.equals(ObjIntegrity.toString())) {
				logger.error("[Integrity]Can't pass the verification.The 'subscriptionConfiguration.xml' must be generated by localhost!");
				config.setStorageKey("");
			}

			/*
			 * String sXPathServerName = sXRootPath+"/ServerName"; Object
			 * ObjServerName = xpathReader .readXPath(sXPathServerName,
			 * XPathConstants.STRING); if (ObjServerName != null) {
			 * subscriptionConfig.setServerName(ObjServerName .toString()); }
			 */
			/*
			 * In CA Cloud,default value comes from current host, not the value
			 * saved in SubscriptionConfiguration.xml
			 */
			config.setServerName(ArchiveService.getInstance()
					.GetArchiveDNSHostName());
			String sXPathDeviceIP = sXRootPath + "/DeviceIP";
			Object ObjDeviceIP = xpathReader.readXPath(sXPathDeviceIP,
					XPathConstants.STRING);
			if (ObjDeviceIP != null) {
				config.setDeviceIP(ObjDeviceIP.toString());
			}
			try {
				config.setDeviceIP(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				logger.error("can't get host ip.", e);
			}

			String sXPathDeviceType = sXRootPath + "/DeviceType";
			Object ObjDeviceType = xpathReader.readXPath(sXPathDeviceType,
					XPathConstants.STRING);
			if (ObjDeviceType != null) {
				config.setDeviceType(Integer.parseInt(ObjDeviceType.toString()));
			}

			String sXPathCloudProxyDetail = sXRootPath + "/Proxy";
			Node ObjCloudProxy = (Node) xpathReader.readXPath(
					sXPathCloudProxyDetail, XPathConstants.NODE);
			if (ObjCloudProxy != null) {
				String ProxyEnabled = ObjCloudProxy.getAttributes()
						.getNamedItem("Enabled").getTextContent();
				config.setCloudUseProxy(ProxyEnabled
						.compareToIgnoreCase("true") == 0 ? true : false);

				String ProxyRequiresAuth = ObjCloudProxy.getAttributes()
						.getNamedItem("RequiresAuth").getTextContent();
				config.setCloudProxyRequireAuth(ProxyRequiresAuth
						.compareToIgnoreCase("true") == 0 ? true : false);
			}

			if (config.isCloudUseProxy()) {
				String sXPathProxyServer = sXRootPath + "/Proxy/ServerName";
				Object ObjProxyServerName = xpathReader.readXPath(
						sXPathProxyServer, XPathConstants.STRING);
				if (ObjProxyServerName != null) {
					config.setCloudProxyServerName(ObjProxyServerName
							.toString());
				}

				String sXPathProxyPort = sXRootPath + "/Proxy/Port";
				Object ObjProxyPort = xpathReader.readXPath(sXPathProxyPort,
						XPathConstants.STRING);
				if (ObjProxyPort != null) {
					config.setCloudProxyPort(Long.parseLong(ObjProxyPort
							.toString()));
				}

				if (config.isCloudProxyRequireAuth()) {
					String sXPathProxyUserName = sXRootPath + "/Proxy/UserName";
					Object ObjProxyUserName = xpathReader.readXPath(
							sXPathProxyUserName, XPathConstants.STRING);
					if (ObjProxyUserName != null) {
						config.setCloudProxyUserName(ObjProxyUserName
								.toString());
					}

					String sXPathProxyPassword = sXRootPath + "/Proxy/Password";
					Object ObjProxyPassword = xpathReader.readXPath(
							sXPathProxyPassword, XPathConstants.STRING);
					if (ObjProxyPassword != null) {
						config.setCloudProxyPassword(CommonService
								.getInstance().getNativeFacade()
								.decrypt(ObjProxyPassword.toString()));
					}
				}
			}
		}

		return config;
	}

	private String generateLocalMachineIntegrity(String key) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String input = key
					+ ArchiveService.getInstance().GetArchiveDNSHostName()
					+ ArchiveService.getInstance().GetArchiveDNSHostSID();
			// String
			// input=key+ArchiveService.getInstance().GetArchiveDNSHostSID();
			md.update(input.getBytes());
			byte[] digest = md.digest();
			BigInteger code = new BigInteger(1, digest);
			String result = code.toString(16);
			return result;
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
			return "";
		}

	}

}
