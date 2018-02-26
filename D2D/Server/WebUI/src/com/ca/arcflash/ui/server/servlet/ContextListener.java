package com.ca.arcflash.ui.server.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;

public class ContextListener implements ServletContextListener {

	public static final String REGISTRY_INSTALLPATH			=	CommonRegistryKey.getD2DRegistryRoot()+"\\InstallPath";
	public static final String REGISTRY_KEY_PATH			=	"Path";
	public static final String WS_PORT_XPATH                =   "/Server/Service/Connector[@protocol='HTTP/1.1']/@port";
	public static String PATH_CONFIGURATION;
	public static String PATH_CUSTOMIZATION;
	public static String ProductNameD2D;
	public static int webServicePort = 8014;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		MessageFormatEx.uninit();  //zxh,do MessageFormatEx uninit
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		configLog4J(sce);
		try {
			MessageFormatEx.init(PATH_CONFIGURATION);	//zxh,do MessageFormatEx init
			//fanda03 fix 158826; must use getDateFormatLocale() not getServerLocale() because of en_GB.
			Locale local = DataFormatUtil.getDateFormatLocale();
			Locale.setDefault(local);
			CommonUtil.prepareTrustAllSSLEnv();
			getCustomizedInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void configLog4J(ServletContextEvent sce) {
		InputStream input = null;
		InputStream inputForServer = null;
	    try {
			String logFilePath = sce.getServletContext().getRealPath("\\WEB-INF\\WebUI.log");
//			String xmlPath = null;
			
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(REGISTRY_INSTALLPATH);
			String homeFolder = registry.getValue(handle, REGISTRY_KEY_PATH);
			PATH_CONFIGURATION = homeFolder +"Configuration\\";
			PATH_CUSTOMIZATION = homeFolder	+ "Customization\\";
			registry.closeKey(handle);
			
			if (!StringUtil.isEmptyOrNull(homeFolder)){
				logFilePath = (homeFolder+"Logs\\WebUI.log").replace('\\', '/');
				/*xmlPath = homeFolder + "TOMCAT\\conf\\server.xml";
				inputForServer = new FileInputStream(new File(xmlPath));
				XPath xpath = XPathFactory.newInstance().newXPath();
				String port = xpath.evaluate(WS_PORT_XPATH,new InputSource(inputForServer));
				if(port != null && !port.isEmpty())
					webServicePort = Integer.parseInt(port);*/
				//cannot depend on server.xml to get port
				String serverURL = CommonUtil.getProductionServerURL();
				String port = CommonUtil.getProductionServerPort(serverURL);
				if(!StringUtil.isEmptyOrNull(port))
					webServicePort = Integer.parseInt(port);
			}
			
			String log4jFile = PATH_CONFIGURATION+"log4j-webui.properties";
			
	    	input = new FileInputStream(log4jFile);
			java.util.Properties props = new java.util.Properties();
			props.load(input);
			
			props.setProperty("log4j.appender.logout.File", logFilePath);
			PropertyConfigurator.configure(props);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(input != null) input.close();
				if(inputForServer != null) inputForServer.close();
			}catch(Throwable t) {}
		}
	}
	
	private void getCustomizedInfo() {
		File cus = new File(PATH_CUSTOMIZATION + "Customized.xml");
		try {
			if(cus.exists()) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document dom = builder.parse(cus);
				XPath xpath = XPathFactory.newInstance().newXPath();
				ProductNameD2D = xpath.evaluate("/Customization/ProductNameD2D/text()", dom);
				if(ProductNameD2D != null) {
					ProductNameD2D = ProductNameD2D.trim();
				}
			}
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}
}
