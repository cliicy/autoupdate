package com.ca.arcflash.webservice.util;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.BackupService;

public class OnlineDiskThread extends Thread{
	private static final Logger logger = Logger.getLogger(OnlineDiskThread.class);

	@Override
	public void run() {
		try {
			int result = 0;
			NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
			result = nativeFacade.OnlineDisks();
			logger.info("online disk return:"+result);
			if(result == 0)
				CommonUtil.setFailoverOnlineDiskFlag("1");
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
		}
		
	}
	
	
}
