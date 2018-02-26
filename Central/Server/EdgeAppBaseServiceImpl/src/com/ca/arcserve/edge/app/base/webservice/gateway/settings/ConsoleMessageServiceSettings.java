package com.ca.arcserve.edge.app.base.webservice.gateway.settings;

import java.io.File;
import java.io.StringWriter;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeBrokerKeyStoreUtils;

@XmlRootElement(name = "MessageServiceSetting")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsoleMessageServiceSettings {
	
	public static final String PLACEHOLDER_BROKER_PROTOCOL = "$BROKER_PROTOCOL";
	public static final String PLACEHOLDER_BROKER_HOST = "$BROKER_HOST";
	public static final String PLACEHOLDER_BROKER_PORT = "$BROKER_PORT";
	public static final String PLACEHOLDER_BROKER_PARAM = "$BROKER_PARAM";
	public static final String DEFAULT_BROKER_URL = String.format(
			"%s://0.0.0.0:%s/broker?transport.connectAttemptTimeout=3600000&transport.readCheckTime=30000&maximumConnections=1000&wireFormat.maxFrameSize=104857600", PLACEHOLDER_BROKER_PROTOCOL,
			PLACEHOLDER_BROKER_PORT);
	
	public static final String DEFAULT_CLIENT_BROKER_URL = String.format(
		"failover:(%s://%s:%s/broker?connectAttemptTimeout=3600000&readCheckTime=30000%s)?maxReconnectDelay=10000&timeout=30000", PLACEHOLDER_BROKER_PROTOCOL,
		PLACEHOLDER_BROKER_HOST, PLACEHOLDER_BROKER_PORT, PLACEHOLDER_BROKER_PARAM);
	
	private String brokerURL = DEFAULT_BROKER_URL;
	private String clientBrokerURL = DEFAULT_CLIENT_BROKER_URL;
	private String protocol = EdgeBrokerKeyStoreUtils.getBrokerProtocol();
	private int port = EdgeBrokerKeyStoreUtils.getBrokerPort();
	private boolean useVM = true;
	private String clientVMBrokerURL = "failover:(vm://localhost/broker)?maxReconnectDelay=10000&timeout=30000";
	
	public String getBrokerURL() {
		return brokerURL;
	}

	public void setBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
	}

	public String getClientBrokerURL() {
		return clientBrokerURL;
	}

	public void setClientBrokerURL(String clientBrokerURL) {
		this.clientBrokerURL = clientBrokerURL;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPort()
	{
		return port;
	}
	
	public void setPort( int port )
	{
		this.port = port;
	}
	
	public boolean isUseVM() {
		return useVM;
	}

	public void setUseVM(boolean useVM) {
		this.useVM = useVM;
	}
	
	public String getClientVMBrokerURL() {
		return clientVMBrokerURL;
	}

	public void setClientVMBrokerURL(String clientVMBrokerURL) {
		this.clientVMBrokerURL = clientVMBrokerURL;
	}
	
	@Override
	public String toString() {
		return "MessageServiceSetting{brokerURL=" + clientBrokerURL + "}";
	}
	
	public static String SETTING_FILE_NAME = "ConsoleMessageService.xml";
	
	public static String getMessageServiceSettingFilePath()
	{
		String configFolder = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement);
		return configFolder + SETTING_FILE_NAME;
	}
	
	public static ConsoleMessageServiceSettings load()
	{
		return load( false );
	}
	
	public static ConsoleMessageServiceSettings load( boolean createNew ) {
		String settingFilePath = getMessageServiceSettingFilePath();
		File settingFile = new File(settingFilePath);
		if (!settingFile.exists()) {
			ConsoleMessageServiceSettings settings = new ConsoleMessageServiceSettings();
			if (createNew)
				settings.save();
			return settings;
		}
		
		try {
			return JAXB.unmarshal(settingFile, ConsoleMessageServiceSettings.class);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new ConsoleMessageServiceSettings();
		}
	}
	
	public void save()
	{
		String settingFilePath = getMessageServiceSettingFilePath();
		File settingFile = new File(settingFilePath);
		JAXB.marshal( this, settingFile );
	}
	
	public static void main(String[] args) {
		ConsoleMessageServiceSettings setting = new ConsoleMessageServiceSettings();
		//setting.setServerRole(ServerRole.MSP);
		
		StringWriter writer = new StringWriter();
		JAXB.marshal(setting, writer);
		
		System.out.println(writer.toString());
	}

}
