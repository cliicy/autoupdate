package com.ca.arcflash.webservice.nimsoft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RegConstants;
import com.ca.arcflash.webservice.service.ServiceException;

public class NimsoftService {
	private static final Logger logger = Logger.getLogger(NimsoftService.class);
	private static NimsoftService instance;
	private final static String CONFIGURATION_PATH = "Configuration";
	private final static String NIMSOFT_CONFIGURATION_FILE_NAME = "nimsoft.xml";
	private final static String ALERT_HISTORY_PATH = "AlertHistory";
	private final static String ALERT_HISTORY_FILE_NAME = "alert_history.xml";
	private final static String D2D_PROBE_FILE_NAME = "d2dprobe.jar";
	private final static int MONITOR_INTERVAL = 60*60*1000;
	private volatile static boolean isThreadStarted = false;
	private final static int SUCCESS = 0;
	private final static int FAIL = 1;
	private String installPath;
	
	private NimsoftService(){
		getD2DInstallPath();
		checkAlertHistoryPath();
	}
	
	public static NimsoftService getInstance(){
		if(instance == null){
			synchronized(NimsoftService.class){
				if(instance == null)
					instance = new NimsoftService();
			}
		}
		return instance;
	}
	
	public int registerNimsoft(NimsoftRegisterInfo nimsoftInfo) throws ServiceException{
		int ret = writeRegisterFileToXml(nimsoftInfo);
		startD2DProbeMonitorThread();
		return ret;
	}
	
	public synchronized void startD2DProbeMonitorThread(){
		if(!isThreadStarted){
			Thread thread = new Thread(new D2DProbeMonitorThread());
			thread.setName("D2DProbeMonitorThread");
			thread.setDaemon(true);
			thread.start();
			isThreadStarted = true;
		}
	}
	
	public int unRegisterNimsoft(){
		NimsoftRegisterInfo info = new NimsoftRegisterInfo();
		info.setRegister(false);
		info.setInstallationPath("");
		int ret = writeRegisterFileToXml(info);
		emptyAlertMessages();
		isThreadStarted = false;
		return ret;
	}
	
	public List<Alert> getAlertMessages() throws ServiceException{
		AlertMessages alertMessages = getMessages();
		if(alertMessages == null)
			return null;
		List<Alert> alertList = alertMessages.getAlertList();
		emptyAlertMessages();
		return alertList;
	}
	
	
	public void addAlertMessage(String message,boolean highPriority){
		AlertMessages alertMessages = getMessages();
		Alert alert = new Alert();
		alert.setMessage(message);
		alert.setSeverity(highPriority == true ? AlertSeverity.ERROR:AlertSeverity.INFORMATION);
		alert.setSendTime(new Date().getTime());
		if(alertMessages != null && alertMessages.getAlertList()!=null){
			alertMessages.getAlertList().add(alert);
		}else{
			alertMessages = new AlertMessages();
			List<Alert> alertList = new ArrayList<Alert>();
			alertList.add(alert);
			alertMessages.setAlertList(alertList);
		}
		writeAlertMessagesToXml(alertMessages);
	}
	
	public boolean isRegister(){
		NimsoftRegisterInfo nimsoftInfo = getNimsoftRegisterInfo();
		if(nimsoftInfo == null || !nimsoftInfo.isRegister())
			return false;
		else
			return true;
	}
	
	private AlertMessages getMessages(){
		String fileName = getAlertMessagesFile();
		File file  = new File(fileName);
		if(!file.exists())
			return null;
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(AlertMessages.class);
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			AlertMessages alertMessages = (AlertMessages) unmarsh.unmarshal(file);
			return alertMessages;
		}catch(Exception e){
			logger.info("Error in getMessages:",e);
		}
		return null;
	}
	
	private synchronized void writeAlertMessagesToXml(AlertMessages alertMessages){
		FileOutputStream fos = null;
		try{
			File file = new File(getAlertMessagesFile());
			if(!file.exists())
				file.createNewFile();
			fos = new FileOutputStream(file);
			JAXBContext jaxbContext = JAXBContext.newInstance(AlertMessages.class);
			Marshaller marsh = jaxbContext.createMarshaller();
			marsh.marshal(alertMessages, fos);
		}catch(Exception e){
			logger.info("Error in writeAlertMessagesToXml:",e);
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void emptyAlertMessages(){
		AlertMessages alertMessages = new AlertMessages();
		writeAlertMessagesToXml(alertMessages);
	}
	
	private void getD2DInstallPath(){
		try{
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(RegConstants.REGISTRY_INSTALLPATH);
			installPath = registry.getValue(handle, RegConstants.REGISTRY_KEY_PATH);
			registry.closeKey(handle);
		}catch(Exception e){
			logger.info("getD2DInstallPath failed",e);
		}
		
	}
	
	private void checkAlertHistoryPath(){
		File file = new File(installPath +ALERT_HISTORY_PATH);
		if(!file.exists())
			file.mkdir();
	}
	
	private String getAlertMessagesFile(){
		return installPath +ALERT_HISTORY_PATH +"\\" + ALERT_HISTORY_FILE_NAME;
	}
	
	private String getRegisterFile(){
		return installPath +CONFIGURATION_PATH +"\\" + NIMSOFT_CONFIGURATION_FILE_NAME;
	}
	
	private synchronized int writeRegisterFileToXml(NimsoftRegisterInfo nimsoftRegisterInfo){
		FileOutputStream fos = null;
		try{
			File file = new File(getRegisterFile());
			if(!file.exists())
				file.createNewFile();
			fos = new FileOutputStream(file);
			JAXBContext jaxbContext = JAXBContext.newInstance(NimsoftRegisterInfo.class);
			Marshaller marsh = jaxbContext.createMarshaller();
			marsh.marshal(nimsoftRegisterInfo, fos);
			return SUCCESS;
		}catch(Exception e){
			logger.info("Error in writeRegisterFileToXml:",e);
			return FAIL;
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public NimsoftRegisterInfo getNimsoftRegisterInfo(){
		String fileName = getRegisterFile();
		File file  = new File(fileName);
		if(!file.exists())
			return null;
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(NimsoftRegisterInfo.class);
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			NimsoftRegisterInfo info = (NimsoftRegisterInfo) unmarsh.unmarshal(file);
			return info;
		}catch(Exception e){
			logger.info("Error in getNimsoftRegisterInfo:",e);
		}
		return null;
	}
	/**
	 * This thread is used for checking if Nimsoft D2D probe exists
	 * @author lijxi03
	 *
	 */
	public class D2DProbeMonitorThread implements Runnable{
		
		private NimsoftRegisterInfo nimsoftInfo;

		@Override
		public void run() {
			while(true){
				nimsoftInfo = NimsoftService.getInstance().getNimsoftRegisterInfo();
				if(nimsoftInfo == null || !nimsoftInfo.isRegister())
					break;
				if(!isD2DProbeExist()){
					unRegisterNimsoft();
					break;
				}
				try {
					Thread.sleep(MONITOR_INTERVAL);
				} catch (InterruptedException e) {
					logger.info(e);
					e.printStackTrace();
				}
			}
				
		}
		
		private boolean isD2DProbeExist(){
			String installationPath = nimsoftInfo.getInstallationPath();
			File file = new File(installationPath + "\\" + D2D_PROBE_FILE_NAME);
			if(file.exists())
				return true;
			else
				return false;
		}
		
	}
	
	public static void main(String[] args){
		/*List<Alert> alertList = NimsoftService.getInstance().getAlertMessages();
		for(Alert alert: alertList){
			System.out.println(alert.getMessage());
		}*/
		NimsoftService.getInstance().addAlertMessage("test",true);
		/*NimsoftRegisterInfo info = new NimsoftRegisterInfo();
		info.setInstallationPath("c:\\");
		info.setRegister(true);
		try {
			int ret = NimsoftService.getInstance().registerNimsoft(info);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
