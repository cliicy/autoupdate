package com.ca.arcflash.webservice.service.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.common.xml.XMLXPathReader;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.ca.arcflash.webservice.data.PM.PMResponse;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.PM.ProxySettings;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RegConstants;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class PatchManager {
	
	public static final int UPDATE_SERVER_TYPE_CA		= 0;
	public static final int UPDATE_SERVER_TYPE_STAGE	= 1;
	
	public static final int UPDATE_STATUS_NONE 	=0;
	public static final int UPDATE_STATUS_START =1;
	public static final int UPDATE_STATUS_END 	=2;
	
	public static final long UPDATE_TIMEOUT_INTERVAL_DEFULT = 30*60*1000;
	
	private static Logger logger = Logger.getLogger(PatchManager.class);	
	private static PatchManager instance = new PatchManager();
//	private static Thread mailAlertThead = null;
	private PMResponse pmResponse = new PMResponse();;
	private int check_update_status = UPDATE_STATUS_NONE;
	private long check_update_timestamp;
	private long check_update_timeout = UPDATE_TIMEOUT_INTERVAL_DEFULT;
	private Thread thread;
	
	
	private PatchManager() {
		initUpdateTimeout();
	}
	
	private void initUpdateTimeout() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(RegConstants.REGISTRY_WEBSERVICE);
			logger.debug("Handle of REGISTRY_CHECK_UPDATE_TIMEOUT is: " + handle);
			if (handle != 0) {
				String value = registry.getValue(handle, RegConstants.REGISTRY_CHECK_UPDATE_TIMEOUT);
				logger.debug("REGISTRY_CHECK_UPDATE_TIMEOUT is: " + value);
				if(StringUtil.isEmptyOrNull(value)){
					return;
				}
				int timeout = Integer.parseInt(value);
				if(timeout>0){
					check_update_timeout = timeout*60*1000;
				}
			}
		} catch (Exception e) {
			logger.error("Read registry error", e);
		} finally {
			if (handle != 0)
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
		}
	}
	
	public static PatchManager getInstance() {
		return instance;
	}
	
	public PMResponse checkUpdate() {
		try {
			//
			// if udp agent is managed by cpm, popup message to ask user deploy message from cpm
			//
			logger.debug("PatchManager.java oooo ServiceImple checkUpdate");
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
			if( edgeRegInfo != null && !StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeHostName()) ){
				pmResponse.setM_iResponseError(-1);
				pmResponse.setM_ErrorMessage( 
						String.format( WebServiceMessages.getResource("PM_Warning_ManagedByCPM"), edgeRegInfo.getEdgeHostName() )
						);
				return pmResponse;
			}
			
			if(check_update_status == UPDATE_STATUS_NONE){
				check_update_timestamp = System.currentTimeMillis();
				check_update_status = UPDATE_STATUS_START;
				startUpdateByNewThread();
				pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_Update_Downloading);
				pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_Update_Downloading") );
			}else if(check_update_status == UPDATE_STATUS_START){
				if(System.currentTimeMillis()-check_update_timestamp > check_update_timeout){
					pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_Update_Timeout);
					pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_Update_Timeout") );
					thread=null;
					check_update_status = UPDATE_STATUS_NONE;
				}else{
					pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_Update_Downloading);
					pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_Update_Downloading") );
				}
			}else if(check_update_status == UPDATE_STATUS_END){
				check_update_status = UPDATE_STATUS_NONE;
			}
			
//			int iRet = (int) BrowserService.getInstance().getNativeFacade().checkUpdate();
//			logger.info("Check update returned: " + iRet );
//			objPMResponse.setM_iResponseError(iRet);
//			switch(iRet){
//			case 0:
//				objPMResponse.setM_ErrorMessage("");
//				break;
//			case 1: // update to date
//				objPMResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_ProductUpToDate") );
//				break;
//			case 2: // no update available.
//				objPMResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_NoNewUpdate") );
//				break;
//			default: // errors.
//				objPMResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_FailedToCheckUpdate") );
//				break;
//			}
			logger.debug("checkUpdate return: "+pmResponse.getM_iResponseError() + " "+pmResponse.getM_PMResponse());
			logger.debug("current checkUpdate job status is: "+check_update_status);
			return pmResponse;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault( "Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	private void startUpdateByNewThread() {
		logger.info("launch a new thread to start Update.");
		thread =new Thread(){
			
			public void run(){
				try {
					int iRet = (int) BrowserService.getInstance().getNativeFacade().checkUpdate();
					check_update_status = UPDATE_STATUS_END;
					logger.info("Check update returned: " + iRet );
					logger.debug("current checkUpdate job status is: "+check_update_status);
					switch(iRet){
					case 0:
						pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_Update_Success);
						pmResponse.setM_ErrorMessage("");
						break;
					case 1: // update to date
						pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_ProductUpToDate);
						pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_ProductUpToDate") );
						break;
					case 2: // no update available.
						pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_NoNewUpdate);
						pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_NoNewUpdate") );
						break;
					default: // errors.
						pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_FailedToCheckUpdate);
						pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_FailedToCheckUpdate") );
						break;
					};
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
					try {
						throw AxisFault.fromAxisFault( "Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
					} catch (Throwable e1) {
						logger.error(e1.getMessage() == null ? e1 : e1.getMessage());
						pmResponse.setM_PMResponse("");
					}
				}
			}
		};
		thread.start();
	}
	
	public int installUpdate(){
		logger.info("Start to install update...");
		try{
			int iRet = (int)BrowserService.getInstance().getNativeFacade().installUpdate();
			logger.info("Install update returned: " + iRet);
			return iRet;
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			try {
				throw AxisFault.fromAxisFault( "Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
			} catch (Throwable e1) {
				logger.error(e1.getMessage() == null ? e1 : e1.getMessage());
				return 1; // non zero means install failed
			}
		}
	}
	
	//added by cliicy.luo
	public PMResponse checkBIUpdate() {
		try {
			//
			// if udp agent is managed by cpm, popup message to ask user deploy message from cpm
			//
			logger.debug("PatchManager.java oooo ServiceImple checkBIUpdate");
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
			if( edgeRegInfo != null && !StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeHostName()) ){
				pmResponse.setM_iResponseError(-1);
				pmResponse.setM_ErrorMessage( 
						String.format( WebServiceMessages.getResource("PM_Warning_ManagedByCPM"), edgeRegInfo.getEdgeHostName() )
						);
				return pmResponse;
			}
			
			if(check_update_status == UPDATE_STATUS_NONE){
				check_update_timestamp = System.currentTimeMillis();
				check_update_status = UPDATE_STATUS_START;
				startUpdateBIByNewThread();
				pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_Update_Downloading);
				pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_Update_Downloading") );
			}else if(check_update_status == UPDATE_STATUS_START){
				if(System.currentTimeMillis()-check_update_timestamp > check_update_timeout){
					pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_Update_Timeout);
					pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_Update_Timeout") );
					thread=null;
					check_update_status = UPDATE_STATUS_NONE;
				}else{
					pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_Update_Downloading);
					pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_Update_Downloading") );
				}
			}else if(check_update_status == UPDATE_STATUS_END){
				check_update_status = UPDATE_STATUS_NONE;
			}
			

			logger.debug("checkUpdate return: "+pmResponse.getM_iResponseError() + " "+pmResponse.getM_PMResponse());
			logger.debug("current checkUpdate job status is: "+check_update_status);
			return pmResponse;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault( "Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	private void startUpdateBIByNewThread() {
		logger.info("launch a new thread to start hotfix Update.");
		thread =new Thread(){
			
			public void run(){
				try {
					int iRet = (int) BrowserService.getInstance().getNativeFacade().checkBIUpdate();
					check_update_status = UPDATE_STATUS_END;
					logger.info("Check hotfix update returned: " + iRet );
					logger.debug("current hotfix checkUpdate job status is: "+check_update_status);
					switch(iRet){
					case 0:
						pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_Update_Success);
						pmResponse.setM_ErrorMessage("");
						break;
					case 1: // update to date
						pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_ProductUpToDate);
						pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_ProductUpToDate") );
						break;
					case 2: // no update available.
						pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_NoNewUpdate);
						pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_NoNewUpdate") );
						break;
					default: // errors.
						pmResponse.setM_iResponseError(PMResponse.RESPONSE_ERROR_FailedToCheckUpdate);
						pmResponse.setM_ErrorMessage( WebServiceMessages.getResource("PM_FailedToCheckUpdate") );
						break;
					};
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
					try {
						throw AxisFault.fromAxisFault( "Unhandled exception in web service when checking BI updates", FlashServiceErrorCode.Common_ErrorOccursInService);
					} catch (Throwable e1) {
						logger.error(e1.getMessage() == null ? e1 : e1.getMessage());
						pmResponse.setM_PMResponse("");
					}
				}
			}
		};
		thread.start();
	}
	
	public int installBIUpdate(){
		logger.info("Start to install Binaries update...");
		try{
			int iRet = (int)BrowserService.getInstance().getNativeFacade().installBIUpdate();
			logger.info("Install update Binaries returned: " + iRet);
			return iRet;
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			try {
				throw AxisFault.fromAxisFault( "Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
			} catch (Throwable e1) {
				logger.error(e1.getMessage() == null ? e1 : e1.getMessage());
				return 1; // non zero means install failed
			}
		}
	}
	//added by cliicy.luo
	
	public boolean isPatchmangerBusy(){
		logger.debug("Start to check if patch manager busy");
		try{
			boolean bRet = BrowserService.getInstance().getNativeFacade().IsPatchManagerBusy("");
			logger.info("Install update returned: " + bRet);
			return bRet;
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			try {
				throw AxisFault.fromAxisFault( "Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
			} catch (Throwable e1) {
				logger.error(e1.getMessage() == null ? e1 : e1.getMessage());
				return true; // non zero means install failed
			}
		}
	}
	
	public AutoUpdateSettings testDownloadServerConnnections(AutoUpdateSettings in_updateSettings) {
		logger.info("HHHH PM testDownloadServerConnnections " );
		boolean bManagedByCPM = false;
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
		if (edgeRegInfo != null && !StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeHostName()))
			bManagedByCPM = true;
		
		if( bManagedByCPM )
			logger.info("Agent is managed by console, skip to test download server connection. " );
		
		switch (in_updateSettings.getServerType()) {
		case UPDATE_SERVER_TYPE_CA: {
			ProxySettings proxyConfig = in_updateSettings.getproxySettings();
			boolean bUseProxy = proxyConfig != null ? proxyConfig.isUseProxy() : false;
			String strProxyServerName = "";
			String strProxyPort = "";
			String strProxyUserName = "";
			String strProxyPassword = "";
			if (bUseProxy) {
				strProxyServerName = proxyConfig.getProxyServerName();
				strProxyPort = Integer.toString(proxyConfig.getProxyServerPort());
				if (proxyConfig.isProxyRequiresAuth()) {
					strProxyUserName = proxyConfig.getProxyUserName();
					strProxyPassword = proxyConfig.getProxyPassword();
				}
			}
			
			int nRet = 0;
			if( bManagedByCPM ){
				nRet = -1;
			}
			else{
				nRet = (int)BrowserService.getInstance().getNativeFacade().testUpdateServerConnection( 
					UPDATE_SERVER_TYPE_CA, "", "", strProxyServerName, strProxyPort, strProxyUserName, strProxyPassword);
			}

			if (nRet == 0) {
				in_updateSettings.setiCAServerStatus(1);
			} else {
				in_updateSettings.setiCAServerStatus(0);
			}

			break;
		}
		case 1: {
			StagingServerSettings[] stagingServers = in_updateSettings.getStagingServers();
			int iStagingServer = 0;
			for (StagingServerSettings stagingServer : stagingServers) {
				String strServer = stagingServer.getStagingServer();
				String strPort = Integer.toString(stagingServer.getStagingServerPort());
				
				int nRet = 0;
				if( bManagedByCPM ){
					nRet = -1;
				}
				else{
					nRet = (int)BrowserService.getInstance().getNativeFacade().testUpdateServerConnection(
						UPDATE_SERVER_TYPE_STAGE, strServer, strPort, "", "", "", "");
				}
				
				if (nRet == 0) {
					in_updateSettings.getStagingServers()[iStagingServer++].setStagingServerStatus(1);
				} else {
					in_updateSettings.getStagingServers()[iStagingServer++].setStagingServerStatus(0);
				}
			}
			break;
		}
		}

		return in_updateSettings;
	}
	
	//added by cliicy.luo
	public AutoUpdateSettings testBIDownloadServerConnnections(AutoUpdateSettings in_updateSettings) {
		logger.info("ooo PM testBIDownloadServerConnnections " );
		boolean bManagedByCPM = false;
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
		if (edgeRegInfo != null && !StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeHostName()))
			bManagedByCPM = true;
		
		if( bManagedByCPM )
			logger.info("Agent is managed by console, skip to test download server connection. " );
		
		switch (in_updateSettings.getServerType()) {
		case UPDATE_SERVER_TYPE_CA: {
			ProxySettings proxyConfig = in_updateSettings.getproxySettings();
			boolean bUseProxy = proxyConfig != null ? proxyConfig.isUseProxy() : false;
			String strProxyServerName = "";
			String strProxyPort = "";
			String strProxyUserName = "";
			String strProxyPassword = "";
			if (bUseProxy) {
				strProxyServerName = proxyConfig.getProxyServerName();
				strProxyPort = Integer.toString(proxyConfig.getProxyServerPort());
				if (proxyConfig.isProxyRequiresAuth()) {
					strProxyUserName = proxyConfig.getProxyUserName();
					strProxyPassword = proxyConfig.getProxyPassword();
				}
			}
			
			int nRet = 0;
			if( bManagedByCPM ){
				nRet = -1;
			}
			else{
				nRet = (int)BrowserService.getInstance().getNativeFacade().testBIUpdateServerConnection( 
					UPDATE_SERVER_TYPE_CA, "", "", strProxyServerName, strProxyPort, strProxyUserName, strProxyPassword);
			}

			if (nRet == 0) {
				in_updateSettings.setiCAServerStatus(1);
			} else {
				in_updateSettings.setiCAServerStatus(0);
			}

			break;
		}
		case 1: {
			StagingServerSettings[] stagingServers = in_updateSettings.getStagingServers();
			int iStagingServer = 0;
			for (StagingServerSettings stagingServer : stagingServers) {
				String strServer = stagingServer.getStagingServer();
				String strPort = Integer.toString(stagingServer.getStagingServerPort());
				
				int nRet = 0;
				if( bManagedByCPM ){
					nRet = -1;
				}
				else{
					nRet = (int)BrowserService.getInstance().getNativeFacade().testBIUpdateServerConnection(
						UPDATE_SERVER_TYPE_STAGE, strServer, strPort, "", "", "", "");
				}
				
				if (nRet == 0) {
					in_updateSettings.getStagingServers()[iStagingServer++].setStagingServerStatus(1);
				} else {
					in_updateSettings.getStagingServers()[iStagingServer++].setStagingServerStatus(0);
				}
			}
			break;
		}
		}

		return in_updateSettings;
	}
	
	public String getBIUpdateStatusFile(){
		String strFile = "";
		try{
			// if udp agent is managed by CPM, don't ask user to install update
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
			if( edgeRegInfo != null && !edgeRegInfo.getEdgeHostName().isEmpty() ){
				return strFile;
			}
			
			strFile = BrowserService.getInstance().getNativeFacade().getBIUpdateStatusFile();
			return strFile;
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			try {
				throw AxisFault.fromAxisFault( "Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
			} catch (Throwable e1) {
				return strFile;
			}
		}
	}
	
	public  BIPatchInfo getPMBIPatchInfo (){
		logger.info("ooo PM getPMBIPatchInfo " );
		try{
			String sStatusXmlFile = getBIUpdateStatusFile();
			logger.info("ooo PM will call  CommonUtil.getBIPatchInfo" );
			BIPatchInfo oBIPatchInfo = CommonUtil.getBIPatchInfo( sStatusXmlFile );			
			return oBIPatchInfo;
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("oooo Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	//added by cliicy.luo
	
	public String getUpdateStatusFile(){
		String strFile = "";
		try{
			// if udp agent is managed by CPM, don't ask user to install update
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
			if( edgeRegInfo != null && !edgeRegInfo.getEdgeHostName().isEmpty() ){
				return strFile;
			}
			
			strFile = BrowserService.getInstance().getNativeFacade().getUpdateStatusFile();
			logger.info("ooo begin getUpdateStatusFile" );
			//logger.info(strFile );
			logger.info("ooo end getUpdateStatusFile" );
			return strFile;
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			try {
				throw AxisFault.fromAxisFault( "Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
			} catch (Throwable e1) {
				return strFile;
			}
		}
	}
	
	public  PatchInfo getPatchInfo (){
		logger.info("ooo PM getPatchInfo " );
		try{
			String sStatusXmlFile = getUpdateStatusFile();
			PatchInfo objPatchInfo = CommonUtil.getPatchInfo( sStatusXmlFile );			
			return objPatchInfo;
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	/**
	 *  to send a mail, the backend process just need place a file under \configration\AlertMails,
	 *  the mail thread will send it and delete the files.
	 */	
	private void sendMailByFile(String sFilePath, BackupEmail mailSettings) {
		try {
			String strAlertType = "";
			String strMailSubject = mailSettings.getSubject();
			String strMailBodyHtml = "";
			String strMailBodyText = "";

			XMLXPathReader xmlReader = new XMLXPathReader(sFilePath);
			xmlReader.Initialise();
			Object obj = xmlReader.readXPath("/AlertMail/Type", XPathConstants.STRING);
			if (obj != null) {
				strAlertType = obj.toString();
			}
			obj = xmlReader.readXPath("/AlertMail/Subject", XPathConstants.STRING);
			if (obj != null) {
				String strSubject = obj.toString(); 
				strMailSubject = strMailSubject + ": " + strSubject;
			}
			obj = xmlReader.readXPath("/AlertMail/BodyHtml", XPathConstants.STRING);
			if (obj != null) {
				strMailBodyHtml = obj.toString();
			}
			obj = xmlReader.readXPath("/AlertMail/BodyPlain", XPathConstants.STRING);
			if (obj != null) {
				strMailBodyText = obj.toString();
			}

			if (0 != strAlertType.compareToIgnoreCase("update") || !mailSettings.isNotifyOnNewUpdates() ) {
				// delete the files
				File fObj = new File(sFilePath);
				fObj.delete();
			} 
			else {
				EmailSender emailSender = new EmailSender();
				emailSender.setSubject(strMailSubject);
				if (mailSettings.isEnableHTMLFormat()) {
					emailSender.setContent(strMailBodyHtml);
				} else {
					emailSender.setContent(strMailBodyText);
				}
				emailSender.setUseSsl(mailSettings.isEnableSsl());
				emailSender.setSmptPort(mailSettings.getSmtpPort());
				emailSender.setMailPassword(mailSettings.getMailPassword());
				emailSender.setMailUser(mailSettings.getMailUser());
				emailSender.setUseTls(mailSettings.isEnableTls());
				emailSender.setProxyAuth(mailSettings.isProxyAuth());
				emailSender.setMailAuth(mailSettings.isMailAuth());

				emailSender.setFromAddress(mailSettings.getFromAddress());
				emailSender.setRecipients(mailSettings.getRecipientsAsArray());
				emailSender.setSMTP(mailSettings.getSmtp());
				emailSender.setEnableProxy(mailSettings.isEnableProxy());
				emailSender.setProxyAddress(mailSettings.getProxyAddress());
				emailSender.setProxyPort(mailSettings.getProxyPort());
				emailSender.setProxyUsername(mailSettings.getProxyUsername());
				emailSender.setProxyPassword(mailSettings.getProxyPassword());
				emailSender.setJobStatus( CommonEmailInformation.EVENT_TYPE.AGENT_RPS_UPDTE_AVAILABLE.getValue() );//event type;
				emailSender.setProductType( CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue() );
				
				emailSender.sendEmail(mailSettings.isEnableHTMLFormat());
				
				File fObj = new File(sFilePath);
				fObj.delete();
			}
		} catch (Exception e) {
			logger.error("Failed to send mail by file: " + sFilePath);
			logger.error("Exception in sending mail....", e);
		}
	}
	
	private void doSendMail(){
		PreferencesConfiguration preference = null;
		BackupEmail mailSettings = null;
		ArrayList<String> mailFileList = new ArrayList<String>();
		try{
				BrowserService.getInstance().getNativeFacade().getMailAlertFiles( (ArrayList<String>)mailFileList);
				preference = CommonService.getInstance().getPreferences();
				if(preference!=null)
					mailSettings = preference.getEmailAlerts();
				// if mail settings is not enabled, delete all files.
				if( mailSettings==null || !mailSettings.isEnableSettings() ){
					for(String fPath : mailFileList) {
						File fObj = new File(fPath);
						if(fObj.exists())
							fObj.delete();
					}
					return;
				}
		}catch(Exception e){
			logger.error("Error in sending email...", e);
			return;
		}
		
		for (String f : mailFileList) {
			sendMailByFile(f, mailSettings);
		}
	}
	
	public void startMailAlterThread() {
		Runnable mailAlertThead;
		try {
				logger.info("Starting a thread to send mail alter....");
				mailAlertThead = new Runnable() 
				{
					@Override
					public void run() {
						while (true) {
							try {
								doSendMail();	
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								logger.debug("Exception in sleep:", e);
								break;
							}
						}
					}
				};
//				mailAlertThead.setDaemon(true);
//				mailAlertThead.start();
				CommonService.getInstance().getUtilTheadPool().submit(mailAlertThead);
				
		} catch (Exception e) {
			logger.error("Failed to start a mail alter thread:" + e.getMessage(), e);
			mailAlertThead = null;
		}
	}

}
