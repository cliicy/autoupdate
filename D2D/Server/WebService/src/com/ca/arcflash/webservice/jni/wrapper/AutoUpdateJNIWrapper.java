package com.ca.arcflash.webservice.jni.wrapper;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.service.CommonService;

public class AutoUpdateJNIWrapper {
	//public static File f =new File("C:\\AutoUpdatelog.txt");
	static String g_localHostName = "";
	static WebServiceClientProxy g_proxy = null;
	static String g_protocol = "http:";
	static int g_localPort = 0;
	static String g_uuid="";
	public static final String REGISTRY_INSTALLPATH			=	CommonRegistryKey.getD2DRegistryRoot()+"\\InstallPath";
	public static final String REGISTRY_KEY_PATH			=	"Path";
	
	private static Logger logger = Logger.getLogger(AutoUpdateJNIWrapper.class);
	
	static{
		try {
			CommonUtil.prepareTrustAllSSLEnv();
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());;
		}
	}
	
	static void GetServiceProxyHandle()
	 {
		   g_localHostName = "localhost";
/*		   String str;
		   str = "GetPortNumber_Protocol_FromXML -start\n";*/
		   //writeTofile(str);
		   GetPortNumber_Protocol_FromXML();
		   //str = "GetPortNumber_Protocol_FromXML -end\n";
		   //writeTofile(str);
	  	 try {
	  		 if(AutoUpdateJNIWrapper.g_proxy==null)
	  		 {
	  			//str = "getFlashServiceV2 -start\n";
	  			//writeTofile(str); 
	  			AutoUpdateJNIWrapper.g_proxy = WebServiceFactory.getFlashServiceV2(g_protocol,g_localHostName,g_localPort);
	  			//str = "getFlashServiceV2 -end\n";
	  			//writeTofile(str); 
	  		 }
	  		 
	  		AutoUpdateJNIWrapper.g_proxy.getServiceV2().validateUserByUUID(retrieveCurrentUUID());

		 } catch (WebServiceException e) {
			 logger.error(e.getMessage() == null ? e : e.getMessage());
		 }

		 return ;
	 }
	static void GetPortNumber_Protocol_FromXML ()
	 {
		  boolean bFoundPort = false;
		  try {
			  WindowsRegistry registry = new WindowsRegistry();
			  String sbARCFLASHHomeDir = null;
			  int handle = 0;
			  try {
				handle = registry.openKey(REGISTRY_INSTALLPATH);
				sbARCFLASHHomeDir = registry
							.getValue(handle, REGISTRY_KEY_PATH);
			  } finally {
				if(handle != 0)
					registry.closeKey(handle);
			  }
			  String xmlFilePath = sbARCFLASHHomeDir + "\\TOMCAT\\conf\\server.xml";
			  // example)  C:\\Program Files\\CA\\ARCFlash\\TOMCAT\\conf\\server.xml
			  File file = new File(xmlFilePath);
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(file);

			  doc.getDocumentElement().normalize();
			  //System.out.println("Root element " + doc.getDocumentElement().getAttribute("port"));
			  NodeList nodeLst = doc.getElementsByTagName("Connector");

			  int count = nodeLst.getLength();
			  for (int s = 0; s < count; s++) {

			     Node fstNode = nodeLst.item(s);

			     if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			        Element fstElmnt = (Element) fstNode;
			        String str_connector_port = fstElmnt.getAttribute("port");
			        String str_protocol = fstElmnt.getAttribute("protocol");
			        String str_SSLEnabled = fstElmnt.getAttribute("SSLEnabled");

			        try{
   		    		   if(str_connector_port !=null)
		    			   g_localPort = Integer.parseInt(str_connector_port);
			        }catch (Exception e) {
			        	g_localPort = 80;
					  }

			        if (str_protocol!=null && str_protocol.compareTo("HTTP/1.1") == 0)
			        {
			    	    if (str_SSLEnabled!=null && str_SSLEnabled.compareTo("true") == 0)
			    	    {
			    	    	g_protocol = "https:";
			    	    }else {
			    	    	g_protocol = "http:";
			    	    }
			    	    bFoundPort = true;
			    	    break;
			        }
			     }
			  }
		  } catch (Exception e) {
			  logger.error(e.getMessage() == null ? e : e.getMessage());
		  }
		  return ;

	 }

	static private String retrieveCurrentUUID() {
		if (StringUtil.isEmptyOrNull(g_uuid)) {
			g_uuid = CommonService.getInstance().getNativeFacade()
					.decrypt(CommonService.getInstance().getLoginUUID());
		}
		return g_uuid;
	}
	static public void PMEmailStartOnSuccess()
	{

		try
		{
			//String str;
			//str = "PMEmailStartOnSuccess -start\n";
			//writeTofile(str);
			//str = "GetServiceProxyHandle -start\n";
			//writeTofile(str);
			AutoUpdateJNIWrapper.GetServiceProxyHandle();
			//str = "PMSendMail -start\n";
			//writeTofile(str);		
			AutoUpdateJNIWrapper.g_proxy.getServiceV2().PMSendMail();
		} catch(Exception ex)
		{
			//fop.write(ex.getMessage().getBytes());
			return;
		}
		return;
	}
	static public void PMEmailStartOnFailure(String strErrorMessage)
	{
		
		AutoUpdateJNIWrapper.GetServiceProxyHandle();
		boolean bRet = AutoUpdateJNIWrapper.g_proxy.getServiceV2().PMSendMailForFailures(strErrorMessage);
		return;
	}
	/*static void writeTofile(String str)
	{
		try
		{
			FileOutputStream fop=new FileOutputStream(f,true);
			fop.write(str.getBytes());
		}
		catch(Exception ex)
		{
			return;
		}
		return;
	}*/
	

}
