package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.io.File;
import java.io.StringReader;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;

@XmlRootElement(name = "AutoDiscoveryConfig")
public class DiscoveryConfiguration {
	private static Logger logger = Logger.getLogger(DiscoveryConfiguration.class); 
	public static String CONFIG_FILE_NAME = "autoDiscoveryConfig.xml";
	public int parellelNumber = 1;
	public int queueMaxLength = 10;
	public int intervalInMinutes = 10;

	public synchronized void saveConfiguration(){
		String filePath = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement)
				+ DiscoveryConfiguration.CONFIG_FILE_NAME;
		
		logger.debug("parellelNumber = " + parellelNumber);
		logger.debug("queueMaxLength = " + queueMaxLength);
		logger.debug("intervalInMinutes = " + intervalInMinutes);
		
		try {
			String marshal = CommonUtil.marshal(this);
			CommonUtil.saveStringToFile(marshal, filePath);
		} catch (JAXBException e) {
			logger.warn("Failed to store the configuration for auto discovery");
		} catch (Exception e) {
			logger.warn("Failed to store the configuration for auto discovery");
		}
	}

	public synchronized static DiscoveryConfiguration getInstance() {
		String filePath = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement)
				+ DiscoveryConfiguration.CONFIG_FILE_NAME;
		DiscoveryConfiguration cfg = null;
		if (cfg == null) {
			File f = new File(filePath);
			if (!f.exists()) {
				cfg = new DiscoveryConfiguration();
				logger.debug("The file [" + filePath + "] does not exist, so default values are used.");
			} else {
				try {
					String readFileAsString = CommonUtil.readFileAsString(filePath);
					if (!StringUtil.isEmptyOrNull(readFileAsString)) {
						cfg = JAXB.unmarshal(new StringReader(readFileAsString), DiscoveryConfiguration.class);
					} else {
						cfg = new DiscoveryConfiguration();
						logger.debug("The content of the file [" + filePath + "] is empty, so default values are used.");
					}
				} catch (Exception e) {
					cfg = new DiscoveryConfiguration();
					logger.debug("Failed to parse the file [" + filePath + "], so default values are used.");
				}
			}
		}
		return cfg;
	}
}
