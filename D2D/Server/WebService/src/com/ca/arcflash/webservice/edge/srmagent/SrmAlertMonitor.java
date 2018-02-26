package com.ca.arcflash.webservice.edge.srmagent;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RegConstants;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class SrmAlertMonitor {

	private static Timer timer;
	private static final Logger logger = Logger.getLogger(SrmAlertMonitor.class);
	
	public static final int ALERT_TYPE_CPU = 1;
	public static final int ALERT_TYPE_PHY_MEMORY = 2;
	public static final int ALERT_TYPE_DISK = 3;
	public static final int ALERT_TYPE_NETWORK = 4;
	public static final int ALERT_TYPE_PAGE_FILE = 5;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat dateFormat1 = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat());
	
	private static int GetPeriod() {
		return 20;//20 seconds, in future, this value should be configured in file/registry
	}
	
	public synchronized static void startMonitor() {
		try {
			if (timer!=null)
				timer.cancel();
			timer = new Timer();
			timer.schedule(new alertTask(), 10000, GetPeriod() * 1000);
			logger.debug("SRM timer has been scheduled");
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public synchronized static void stopMonitor() {
		try {
			if (timer != null)
				timer.cancel();
			logger.debug("SRM timer has been cancelled");
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public static List<CommonEmailInformation> GetAlertInfo() {
		AlertBasicInfo basicInfo = new AlertBasicInfo(alertTask.ALERT_RECORDS_COUNT);
		
		int realCount = SrmJniCaller.getAlertRecords(basicInfo.alertTypes, basicInfo.alertHeaders,
				basicInfo.thresholds, basicInfo.curUtils, basicInfo.GetSize());
		
		if ( realCount <=  0 ) {
			return null;
		}

		List<CommonEmailInformation> infoList = new ArrayList<CommonEmailInformation>();
		
		while (realCount >= basicInfo.GetSize()) {
			
			processAlertInfo(basicInfo, infoList);
			realCount = SrmJniCaller.getAlertRecords(basicInfo.alertTypes, basicInfo.alertHeaders,
					basicInfo.thresholds, basicInfo.curUtils, basicInfo.GetSize());
		}
		
		basicInfo.SetSize( realCount );
		processAlertInfo(basicInfo, infoList);
		
		return infoList;
	}
	
	private static boolean processAlertInfo(AlertBasicInfo basicInfo, List<CommonEmailInformation> alertInfo) {
		if ( basicInfo == null || alertInfo == null ) {
			return false;
		}
		
		String subject = getAlertSubject();
		
		for ( int i = 0; i < basicInfo.GetSize(); ++i ){
			CommonEmailInformation info = new CommonEmailInformation();
			info.setContent(getAlertMessage(basicInfo.alertTypes[i], basicInfo.alertHeaders[i],
					basicInfo.thresholds[i], basicInfo.curUtils[i]));
			info.setSubject(subject);

			alertInfo.add(info);
		}
		
		return true;
	}
	
	public static BackupEmail getLocalEmailSetting() {
		Date time = new Date();
		
		try {
			PreferencesConfiguration configuration = CommonService.getInstance().getPreferences();
			if (configuration == null) {
				time.setTime(System.currentTimeMillis());
				logger.debug(time.toString() + "Not set backup configuration!");
				return null;
			}
			
			BackupEmail email = configuration.getEmailAlerts();
			return email;
		} catch (Throwable e) {
			logger.error(time.toString() + "Exception happened when getting local email settings: " + e.getMessage());
		}
		
		return null;
	}
	
	public static int SendAlert(String subject, String content,int nAlertType) {
		Date time = new Date();
		logger.debug(time.toString() + "Send Alert Start");

		if ( content == null || content.length() == 0 ) {
			return -1;
		}
		
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		
		try {
			BackupEmail email = getLocalEmailSetting();
			if (email == null || !email.isEnableSettings()) {
				time.setTime(System.currentTimeMillis());
				logger.debug(time.toString() + "Not set email configuration!");
				//nativeFacade.addLogActivity(BaseJob.AFRES_AFALOG_INFO,BaseJob.AFRES_AFJWBS_GENERAL,new String[]{WebServiceMessages.getResource("SrmAlertNotSend", content),"","","",""});
				return 0;
			}
			
			if ( !email.isEnableSrmPkiAlert() ) {
				time.setTime(System.currentTimeMillis());
				logger.debug(time.toString() + "Server utilization alert is disabled!");
				//nativeFacade.addLogActivity(BaseJob.AFRES_AFALOG_INFO,BaseJob.AFRES_AFJWBS_GENERAL,new String[]{WebServiceMessages.getResource("SrmAlertNotSend", content),"","","",""});
				return 0;
			}

			EmailSender emailSender = new EmailSender();
			
			String emailSubject = subject;
			if ( subject == null ) {
				String alertType = getAlertTypeString(nAlertType);
				emailSubject = email.getSubject() + " - " +alertType + " - " + ServiceContext.getInstance().getLocalMachineName();
			}
			
			emailSender.setJobStatus(CommonEmailInformation.EVENT_TYPE.SRM_ALERT.getValue()
					| CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue());
			emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue());
			
			emailSender.setSubject(emailSubject);
			emailSender.setContent(content);
			emailSender.setUseSsl(email.isEnableSsl());
			emailSender.setSmptPort(email.getSmtpPort());
			emailSender.setMailPassword(email.getMailPassword());
			emailSender.setMailUser(email.getMailUser());
			emailSender.setUseTls(email.isEnableTls());
			emailSender.setProxyAuth(email.isProxyAuth());
			emailSender.setMailAuth(email.isMailAuth());

			emailSender.setFromAddress(email.getFromAddress());
			emailSender.setRecipients(email.getRecipientsAsArray());
			emailSender.setSMTP(email.getSmtp());
			emailSender.setEnableProxy(email.isEnableProxy());
			emailSender.setProxyAddress(email.getProxyAddress());
			emailSender.setProxyPort(email.getProxyPort());
			emailSender.setProxyUsername(email.getProxyUsername());
			emailSender.setProxyPassword(email.getProxyPassword());

			emailSender.sendEmail(email.isEnableHTMLFormat());
			
			time.setTime(System.currentTimeMillis());
			logger.debug(time.toString() + "Send Alert End");
			return 0;
		} catch (Exception e) {
			time.setTime(System.currentTimeMillis());
			logger.debug(time.toString() + "Exception happens when send Alert <" + e.getMessage() + ">");
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,new String[]{WebServiceMessages.getResource("SrmAlertFailedToSend", content),"","","",""});
			return -1;
		}
	}
	
	private static String getAlertTypeString(int nAlertType){
		String alertType = "";
		switch (nAlertType) {
		case SrmAlertMonitor.ALERT_TYPE_CPU:
			alertType = WebServiceMessages.getResource("CPUAlert");
			break;
		case SrmAlertMonitor.ALERT_TYPE_PHY_MEMORY:
			alertType = WebServiceMessages.getResource("MemoryAlert");
			
			break;
		case SrmAlertMonitor.ALERT_TYPE_DISK:
			alertType = WebServiceMessages.getResource("DiskAlert");
			
			break;
		case SrmAlertMonitor.ALERT_TYPE_NETWORK:
			alertType = WebServiceMessages.getResource("NetworkAlert");
			
			break;
		case SrmAlertMonitor.ALERT_TYPE_PAGE_FILE:
			alertType = WebServiceMessages.getResource("MemoryAlert");
			break;
		default:
			logger.error("Wrong Alert type (" + nAlertType + ")");
			return null;
		}
		return alertType;
	}
	
	public static String getAlertMessage(int nAlertType, String msgHeader, int nThreshold, int ncurUtil, boolean isHTML) {
		String alertMsg = "";
		if(isHTML) {
			alertMsg = EmailContentTemplate.getPKIHtmlContent(nAlertType, msgHeader, nThreshold, ncurUtil);
		} else {
			alertMsg = getAlertMessage(nAlertType, msgHeader, nThreshold, ncurUtil);
		}
		return alertMsg;
	}
	
	public static String getAlertMessage(int nAlertType, String msgHeader, int nThreshold, int ncurUtil) {
		if ( msgHeader == null ) {
			logger.error("No alert header!");
			return null;
		}
		
		String alertMsgFormat = null;
		switch (nAlertType) {
		case ALERT_TYPE_CPU:
			alertMsgFormat = WebServiceMessages.getResource("SrmAlertCPU");
			break;
			
		case ALERT_TYPE_PHY_MEMORY:
			alertMsgFormat = WebServiceMessages.getResource("SrmAlertPhysicalMemory");
			break;
			
		case ALERT_TYPE_DISK:
			alertMsgFormat = WebServiceMessages.getResource("SrmAlertDiskTextContent");
			break;
			
		case ALERT_TYPE_NETWORK:
			alertMsgFormat = WebServiceMessages.getResource("SrmAlertNetwork");
			break;
			
		case ALERT_TYPE_PAGE_FILE:
			alertMsgFormat = WebServiceMessages.getResource("SrmAlertPageFileMemory");
			break;
		}
		
		if ( alertMsgFormat == null || alertMsgFormat.length() == 0 ) {
			logger.error("Wrong Alert type (" + nAlertType + ")");
			return null;
		}
		if(!StringUtil.isEmptyOrNull(msgHeader) && nAlertType == SrmAlertMonitor.ALERT_TYPE_DISK ) {
			String[] msg = msgHeader.split(",");
			
			if(msg.length == 3){
				StringBuilder diskVolume = new StringBuilder();
				String alertTime = msg[0];
				try {
					Date dt = dateFormat.parse(msg[0]);
					alertTime = dateFormat1.format(dt);
				} catch (ParseException e) {
					logger.debug(e.getMessage());
				}
				
				diskVolume.append(alertTime)//time
						.append(", ")
						.append(msg[1]) //computer name
						.append(", ");
				
				String[] alertMsgArray = msg[2].trim().split(" ");
				if(alertMsgArray.length > 0) {
					diskVolume.append(WebServiceMessages.getResource("SrmAlertDisk"))
								.append(" ")
								.append(alertMsgArray[0])
								.append(", ")
								.append(WebServiceMessages.getResource("SrmAlertVolume"))
								.append(" "); 
					for(int i = 1; i< alertMsgArray.length; i++){
						diskVolume.append(alertMsgArray[i]);
					}
				}
				
				msgHeader = diskVolume.toString();
			}
			
		} else {
			try {
				StringBuilder messageHead = new StringBuilder();
				String msg[] = msgHeader.split(",");
				String alertTime = msg[0];
				Date dt = dateFormat.parse(msg[0]);
				alertTime = dateFormat1.format(dt);
				messageHead.append(alertTime).append(", ").append(msg[1]);
				msgHeader = messageHead.toString();
			} catch (ParseException e) {
				logger.debug(e.getMessage());
			}
			
		}
		
		String alertMsg = String.format(alertMsgFormat, msgHeader, nThreshold, ncurUtil);
		return alertMsg;
	}
	
	public static String getAlertSubject() {
		BackupEmail email = getLocalEmailSetting();
		String hostname = ServiceContext.getInstance().getLocalMachineName();
		if ( email != null && email.isEnableSrmPkiAlert() ) {
			return (email.getSubject() + " - " + hostname);
		}
		
		return (WebServiceMessages.getResource("SrmAlertDefaultSubject") + " - " + hostname);
	}
	
	public static boolean isManagedByEdge() {
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		String edgeWSDL = edgeReg.GetEdgeWSDL();
		if ( edgeWSDL != null && edgeWSDL.length() > 0 ) {
			return true;
		}

		return false;
	}
	
}

class AlertBasicInfo {
	private int size;
	int[] alertTypes;
	String[] alertHeaders;
	int[] thresholds;
	int[] curUtils;
	
	AlertBasicInfo(int size) {
		this.size = size;
		alertTypes = new int[size];
		alertHeaders = new String[size];
		thresholds = new int[size];
		curUtils = new int[size];
	}
	
	void SetSize(int size) {
		if ( size < this.size && size >= 0 ) {
			this.size = size;
		}
	}
	
	int GetSize() {
		return size;
	}
}

class alertTask extends TimerTask {

	public static final String ALERT_LOCAL_FILE_PATH = "BIN\\LocalAlt.dat";
	public static final int ALERT_RECORDS_COUNT = 16;
	private static final Logger logger = Logger.getLogger(TimerTask.class);
		
	private static Object lock = new Object();

	@Override
	public void run() {
		synchronized(lock) {
			if ( !hasAlertRecords() ) {
				return;
			}
			
/*			if ( SrmAlertMonitor.isManagedByEdge() ) {
				//Notify Edge Webservice to send alert
				try {
					D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
					String edgeWSDL = edgeReg.GetEdgeWSDL();
					if ( edgeWSDL != null && edgeWSDL.length() > 0 ) {
						IEdgeD2DService proxy = WebServiceFactory.getFlassService("http", "", 8015, edgeWSDL);
						if(proxy == null)
						{
							logger.debug("alertTask - Failed to get proxy handle!!\n");
							return;
						}

						List<CommonEmailInformation> alertList = SrmAlertMonitor.GetAlertInfo();
						if ( alertList == null || alertList.size() == 0 ) {
							return;
						}
						
						proxy.SendEmail(alertList);
					}
					else {
						logger.error("alertTask - Edge WSDL is empty.");
					}
				} catch (Throwable e) {
					e.printStackTrace();
					logger.error("alertTask - Exception happends when sending server utilization alert via Edge: " + e.getMessage());
				}
			} else {*/
			
				
				
				AlertBasicInfo basicInfo = new AlertBasicInfo(ALERT_RECORDS_COUNT);
				
				int realCount = SrmJniCaller.getAlertRecords(basicInfo.alertTypes, basicInfo.alertHeaders,
						basicInfo.thresholds, basicInfo.curUtils, basicInfo.GetSize());
				
				if ( realCount <=  0 ) {
					return;
				}
		
				while (realCount >= basicInfo.GetSize()) {
					
					
					
					processAlertRecord(basicInfo);
					realCount = SrmJniCaller.getAlertRecords(basicInfo.alertTypes, basicInfo.alertHeaders,
							basicInfo.thresholds, basicInfo.curUtils, basicInfo.GetSize());
				}
		
				basicInfo.SetSize( realCount );
				processAlertRecord(basicInfo);
			//}
		}
	}

	private boolean hasAlertRecords() {
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(RegConstants.REGISTRY_INSTALLPATH);
			String homeFolder = registry.getValue(handle, RegConstants.REGISTRY_KEY_PATH);
			registry.closeKey(handle);
			
			String alertFile = homeFolder + ALERT_LOCAL_FILE_PATH;
			File file = new File(alertFile);
			return file.exists() && file.length() > 0;
		} catch (Exception e) {
			logger.error("alertTask - Exceptions in alert monitor: "+e.getMessage());
		}
		
		return false;
	}
	
	private void processAlertRecord(AlertBasicInfo basicInfo) {
		if ( basicInfo == null ) {
			return;
		}
		
		BackupEmail email = SrmAlertMonitor.getLocalEmailSetting();
		boolean isHTML = email.isEnableHTMLFormat();
		
		for (int i = 0; i < basicInfo.GetSize(); ++i) {
			String alertMsg = SrmAlertMonitor.getAlertMessage(
					basicInfo.alertTypes[i], 
					basicInfo.alertHeaders[i],
					basicInfo.thresholds[i], 
					basicInfo.curUtils[i], isHTML);
			
			SrmAlertMonitor.SendAlert(null, alertMsg,basicInfo.alertTypes[i]);
		}
	}
}
