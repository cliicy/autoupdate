package com.ca.arcflash.webservice.edge.d2dreg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo.ListenerType;
import com.ca.arcflash.webservice.service.RegConstants;

public abstract class BaseEdgeRegistration {
	private static final Logger logger = Logger.getLogger(BaseEdgeRegistration.class);
	private static final int ERROR_NOT_ALLOW_MANAGE_SAASD2D = -2;
	private static final String D2D_PRODUCT_TYPE_SAAS = "1";

	protected static Object cmXmlLock = new Object();
	protected static Object vcmXmlLock = new Object();
	protected static Object vspXmlLock = new Object();
	protected static Object reportXmlLock = new Object();
	protected static Object unknownXmlLock = new Object();

	protected String getRegConfigFileName() {
		return null;
	}

	private String getRegConfigFileName(ApplicationType type) {

		try {
			String edgeRegInfoPath = getRegConfigFileName();

			if (edgeRegInfoPath == null) {
				WindowsRegistry registry = new WindowsRegistry();
				int handle = registry
						.openKey(RegConstants.REGISTRY_INSTALLPATH);
				edgeRegInfoPath = registry.getValue(handle,
						RegConstants.REGISTRY_KEY_PATH);
				registry.closeKey(handle);
				edgeRegInfoPath += "Configuration";
			}			

			if (type == ApplicationType.CentralManagement
					|| type == ApplicationType.vShpereManager
					|| type == ApplicationType.VirtualConversionManager)
				edgeRegInfoPath = edgeRegInfoPath + "\\RegConfigPM.xml";
			else if (type == ApplicationType.Report)
				edgeRegInfoPath = edgeRegInfoPath
						+ "\\RegConfigReport.xml";
			else
				edgeRegInfoPath = edgeRegInfoPath
						+ "\\RegConfigUnkown.xml";

			return edgeRegInfoPath;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "";
	}

	public EdgeRegInfo getEdgeRegInfo(ApplicationType appType) {
		File cfgFile = new File(getRegConfigFileName(appType));

		if (cfgFile.exists()) {
			logger.debug("edge config file exist! "
					+ getRegConfigFileName(appType) + "\n");
			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext
						.newInstance("com.ca.arcflash.webservice.edge.d2dreg");
				Unmarshaller unmarsher = jaxbContext.createUnmarshaller();
				EdgeRegInfo regInfo = null;
				synchronized (getLockObj(appType)) {
					regInfo = (EdgeRegInfo) unmarsher
							.unmarshal(new File(getRegConfigFileName(appType)));
				}
				if (regInfo != null && regInfo.getEdgeUUID() != null
						&& regInfo.getEdgeUUID().length() > 0)
					regInfo.setEdgeUUID(decrypt(regInfo
							.getEdgeUUID()));

				return regInfo;
			} catch (JAXBException e) {
				logger.error("Failed to read registry information from " + cfgFile.getName(), e);
				return null;
			}
		} else {
			logger.debug("edge config file doesn't exist! "
					+ getRegConfigFileName(appType) + "\n");

			return null;
		}
	}
	/*
	 * return code: 0 not registered yet 1 registered already with same Edge
	 * host 2 registered with different Edge host
	 */
	public int getRegStatus(String uuid, ApplicationType appType) {
		EdgeRegInfo regInfo = getEdgeRegInfo(appType);

		if (regInfo == null || regInfo.getEdgeUUID() == null
				|| regInfo.getEdgeUUID().compareTo("") == 0) {
			logger.debug("D2D not registered to edge server yet!!\n");
			return 0;
		}

		String existingUUID = regInfo.getEdgeUUID();
		if (existingUUID.compareTo(uuid) != 0) {
			logger.debug("D2D has already registered to another edge server!!\n");
			return 2;
		}

		return 1;
	}

	/*
	 * return code: 0 not registered yet 1 registered already with same Edge
	 * host 2 registered with different Edge host
	 */
	public int getRegStatusByHost(String uuid, String edgeHostName,
			ApplicationType appType) {
		EdgeRegInfo regInfo = getEdgeRegInfo(appType);

		if (regInfo == null || regInfo.getEdgeUUID() == null
				|| regInfo.getEdgeUUID().compareTo("") == 0) {
			logger.debug("D2D not registered to edge server yet!!\n");
			return 0;
		}

		String existingUUID = regInfo.getEdgeUUID();
		String existingEdgeHostName = regInfo.getEdgeHostName();
		if (existingUUID.compareTo(uuid) != 0) {
			if (existingEdgeHostName.compareTo(edgeHostName) != 0) {
				logger.debug("D2D has already registered to another edge server!!\n");
				return 2;
			}
		}

		return 1;
	}

	public String GetEdgeWSDL() {
		EdgeRegInfo regInfo = getEdgeRegInfo(ApplicationType.CentralManagement);
		if (regInfo == null) {
			logger.debug("GetEdgeWSDL(): cannot get edge wsdl from config file!\n");
			return null;
		}

		return regInfo.getEdgeWSDL();
	}

	public String GetEdgeUUID() {
		EdgeRegInfo regInfo = getEdgeRegInfo(ApplicationType.CentralManagement);
		if (regInfo == null) {
			logger.debug("GetEdgeWSDL(): cannot get edge wsdl from config file!\n");
			return null;
		}

		return regInfo.getEdgeUUID();
	}
	
	public String getConsoleUrl()
	{
		EdgeRegInfo regInfo = getEdgeRegInfo(ApplicationType.CentralManagement);
		if (regInfo == null) {
			logger.debug("getConsoleUrl(): cannot get edge wsdl from config file!\n");
			return null;
		}

		return regInfo.getConsoleUrl();
	}

	private ListenerType getListenerType(ApplicationType appType) {
		switch (appType) {
		case CentralManagement:
			return ListenerType.CPM;
		case VirtualConversionManager:
			return ListenerType.VCM;
		case vShpereManager:
			return ListenerType.HBBU;
		default:
			logger.error("Invalid app type: " + appType
					+ ", convert it to CentralManagement type by default.");
			return ListenerType.CPM;
		}
	}

	protected abstract void removeConfig();

	public int removeEdge(String uuid, ApplicationType appType,
			String edgeHostName, boolean forceUnRegFlag) {
		synchronized (getLockObj(appType)) {
			int status = getRegStatus(uuid, appType);

			if (status == 0) {
				logger.debug("The D2D node is not managed under a Edge server yet\n");
				return 0;
			} else if (status == 2) {
				if (forceUnRegFlag == false) {
					logger.debug("The D2D node is managed under another Edge server. Cannot remove it!!\n");
					return 2;
				} else {
					logger.debug("The D2D node is managed under another Edge server. Forcely remove it!!\n");
					status = 1;
				}
			}

			removeConfig();

			String regConfigFilePath = getRegConfigFileName(appType);
			File regConfigFile = new File(regConfigFilePath);

			if (!regConfigFile.delete()) {
				return -1;
			}

			FlashListenerInfo listener = FlashListenerInfo.createListenerInfo(
					getListenerType(appType), null, uuid);
			ListenerManager.getInstance().removeFlashListener(listener);

			return status;
		}
	}

	private boolean writexml(ApplicationType appType, EdgeRegInfo regInfo) {
		FileOutputStream fos = null;
		try {
			// JAXB
			File file = new File(getRegConfigFileName(appType));
			if(!file.exists()){
				file.createNewFile();
			}
			fos = new FileOutputStream(file);

			JAXBContext jaxbContext;
			jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.edge.d2dreg");
			Marshaller marsher = jaxbContext.createMarshaller();

			marsher.marshal(regInfo, fos);

			fos.close();

			return true;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			return false;
		} catch (JAXBException e) {
			logger.error(e.getMessage());

			try {
				fos.close();
			} catch (IOException e1) {
				logger.error(e.getMessage());
			}

			File file = new File(getRegConfigFileName(appType));
			if (file.isFile() && file.exists()) {
				file.delete();
			}

			return false;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public void SavePolicyUuid2Xml(ApplicationType appType,
			Map<String, String> policyUuids) {
		synchronized (getLockObj(appType)) {
			if (policyUuids == null || policyUuids.size() == 0)
				return;
			EdgeRegInfo regInfo = getEdgeRegInfo(appType);
			regInfo.setEdgeUUID(encrypt(regInfo.getEdgeUUID()));

			for (Iterator<String> vms = policyUuids.keySet().iterator(); vms
					.hasNext();) {
				String vm = (String) vms.next();

				String key_vm = "";
				if (appType == ApplicationType.VirtualConversionManager)
					key_vm = "vcm_" + vm;
				else
					key_vm = vm;
				regInfo.getPolicyUuids().put(key_vm, policyUuids.get(vm));
			}

			writexml(appType, regInfo);
			logger.info(String.format(
					"save policy uuid to xml for %s with %d nodes",
					appType.name(), policyUuids.size()));
		}
	}

	public void RemovePolicyUuidFromXml(ApplicationType appType,
			List<String> hostuuids) {
		synchronized (getLockObj(appType)) {
			if (hostuuids == null || hostuuids.size() == 0)
				return;
			EdgeRegInfo regInfo = getEdgeRegInfo(appType);
			regInfo.setEdgeUUID(encrypt(regInfo.getEdgeUUID()));

			for (String h : hostuuids) {
				String key_vm = "";
				if (appType == ApplicationType.VirtualConversionManager)
					key_vm = "vcm_" + h;
				else
					key_vm = h;
				regInfo.getPolicyUuids().remove(key_vm);
			}

			writexml(appType, regInfo);
			logger.info(String.format(
					"remove policy uuid from xml for %s with %d nodes",
					appType.name(), hostuuids.size()));
		}
	}

	public int saveEdgeWSDL2XML(String uuid, ApplicationType appType,
			String edgeHostName, String edgeWSDL, String edgeLocale,
			boolean forceRegFlag, String regHostName, String consoleUrl,
			List<String> nameList) {
		int status = 0;
		synchronized (getLockObj(appType)) {
			if (isSaaSD2D()) {
				return ERROR_NOT_ALLOW_MANAGE_SAASD2D;
			}

			if (forceRegFlag == false) {
				status = getRegStatusByHost(uuid, edgeHostName, appType);
				if (status == 2) {
					logger.debug("Cannot register again!!\n");
					return 2;
				}
			} else
				logger.info("Forcely register!!");

			EdgeRegInfo regInfo = getEdgeRegInfo(appType);
			if (regInfo == null)
				regInfo = new EdgeRegInfo();
			regInfo.setEdgeHostName(edgeHostName);
			regInfo.setEdgeWSDL(edgeWSDL);
			regInfo.setEdgeUUID(encrypt(uuid));
			regInfo.setEdgeAppType(appType);
			regInfo.setEdgeLocale(edgeLocale);
			regInfo.setRegHostName(regHostName);
			regInfo.setConsoleUrl( consoleUrl );
			regInfo.setEdgeConnectNameList(nameList);

			if (!writexml(appType, regInfo)) {
				return -1;
			}
		}
		FlashListenerInfo listener = FlashListenerInfo.createListenerInfo(
				getListenerType(appType), edgeWSDL, uuid);
		ListenerManager.getInstance().addFlashListener(listener);
		return status;
		
	}

	/**
	 * Change the protocol for managed app's wsdl
	 * 
	 * @param uuid
	 * @param appType
	 * @param protocl
	 *            1 for http, 2 for https
	 * @return 0 for not managed by this uuid, 1 for success
	 */
	public int changeProtocol(String uuid, ApplicationType appType, int protocol) {
		EdgeRegInfo regInfo = getEdgeRegInfo(appType);

		if (regInfo == null || regInfo.getEdgeUUID() == null
				|| regInfo.getEdgeUUID().compareTo("") == 0) {
			logger.debug("D2D not registered to edge server yet!!\n");
			return 0;
		}
		String existingUUID = regInfo.getEdgeUUID();
		if (existingUUID.compareTo(uuid) != 0) {
			logger.debug("D2D is registered to another edge server!\n");
			return 0;
		}
		String edgeWSDL = regInfo.getEdgeWSDL();

		if (edgeWSDL == null || edgeWSDL.trim().isEmpty()) {
			logger.debug("Invalid WSDL!\n");
			return 0;
		}
		int indexOf = edgeWSDL.indexOf("://");
		if (indexOf == -1) {
			logger.debug("Invalid WSDL!\n");
			return 0;
		}
		String targetProtocol = (protocol == 1 ? "http" : "https");
		String sourceProtocol = edgeWSDL.substring(0, indexOf);
		if (targetProtocol.equalsIgnoreCase(sourceProtocol)) {
			return 1;
		}
		edgeWSDL = edgeWSDL.replaceFirst(sourceProtocol, targetProtocol);
		regInfo.setEdgeWSDL(edgeWSDL);

		int i = saveEdgeWSDL2XML(regInfo.getEdgeUUID(),
				regInfo.getEdgeAppType(), regInfo.getEdgeHostName(),
				regInfo.getEdgeWSDL(), regInfo.getEdgeLocale(), true,
				regInfo.getRegHostName(), regInfo.getConsoleUrl(), regInfo.getEdgeConnectNameList());
		if (i == 0)
			return 1;
		else
			return 0;

	}
	
	private String getProductType(){
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(RegConstants.REGISTRY_VERSION_ROOTKEY);
			String pt = registry.getValue(handle, RegConstants.REGISTRY_KEY_PRODUCTTYPE);
			registry.closeKey(handle);
			return pt;
		} catch (Exception e) {
			logger.error("Read registry error", e);
		}
		return null;
	}

	private boolean isSaaSD2D() {
		String pType = getProductType();
		logger.debug("Product Type:" + pType);
		if (pType != null
				&& D2D_PRODUCT_TYPE_SAAS.equals(pType))
			return true;

		return false;
	}

	private Object getLockObj(ApplicationType type) {
		if (type == ApplicationType.CentralManagement
				|| type == ApplicationType.vShpereManager
				|| type == ApplicationType.VirtualConversionManager)
			return cmXmlLock;
		else if (type == ApplicationType.Report)
			return reportXmlLock;
		else
			return unknownXmlLock;
	}
	
	protected abstract String encrypt(String toEnc);
	
	protected abstract String decrypt(String toDec);
}
