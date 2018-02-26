package com.ca.arcflash.webservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JRWLong;

public class ApacheServiceUtil {
	
	public static final String X86_PATH = "SOFTWARE\\Apache Software Foundation\\Procrun 2.0\\CASAD2DWebSvc\\Parameters";
	public static final String AMD64_PATH = "SOFTWARE\\Wow6432Node\\Apache Software Foundation\\Procrun 2.0\\CASAD2DWebSvc\\Parameters";
	//public static final String KEY_OPTIONS = "Options"; 
	
	private static final Logger logger = Logger.getLogger(ApacheServiceUtil.class);
	
	private static final ApacheServiceUtil instance = new ApacheServiceUtil();
	
	private ApacheServiceUtil(){}
	
	public static ApacheServiceUtil getInstance(){
		return instance;
	}
	
	public boolean adjustJavaVM(ApplicationType appType, boolean isAMD64){
		logger.info("AppType:"+appType+" isAMD64:"+isAMD64);
		boolean bResult1 = adjustJavaHeapSize(appType, isAMD64);
		//after upgrading to JRE 8, MaxPermSize is not used. So that remove the check for MaxPermSize - by Liang.Shu
//		boolean bResult2 = adjustJavaPermSize(appType, isAMD64);
//		return bResult1 || bResult2;
		return bResult1;
	}
	
	private String getRegKeyPath(boolean isAMD64){
		if(isAMD64)
			return AMD64_PATH;
		else
			return X86_PATH;
	}
	
	private boolean adjustJavaHeapSize(ApplicationType appType,boolean isAMD64){
		boolean isAdjust = false;
		int currentJvmMx = 0;
		int expectJvmMx = 0;
		String keyName = "Java";
		String valueName = "JvmMx";
		
		if(appType == ApplicationType.vShpereManager){
			expectJvmMx = 512;
			isAdjust = true;
		}
		else if(appType == ApplicationType.VirtualConversionManager){
			expectJvmMx = 512;
			isAdjust = true;
		}
		
		if(isAdjust){
			JRWLong jValue = new JRWLong();
			int result = WSJNI.GetRegIntValue(keyName, valueName, getRegKeyPath(isAMD64), jValue);
			if(result !=0){
				logger.error("Failed to get the current java heap size");
				return false;
			}
			
			currentJvmMx = (int)jValue.getValue();
			if((currentJvmMx > 0) && (currentJvmMx < expectJvmMx)){
				result = WSJNI.SetRegIntValue(keyName, valueName,getRegKeyPath(isAMD64), expectJvmMx);
				if(result == 0){
					String msg = StringUtil.enFormat("Change the jvmMx from [%d] to [%d]", currentJvmMx, expectJvmMx);
					logger.info(msg);
					return true;
				}
				else{
					logger.info("failed to adjust the jvmMx size");
				}
			}
			
		}
		return false;
	}
	
	private boolean adjustJavaPermSize(ApplicationType appType,boolean isAMD64){
		String maxPermSizeStr = "";
		int maxPermSize = 0;
		if (appType == ApplicationType.VirtualConversionManager) {
			maxPermSizeStr = "-XX:MaxPermSize=256M";
			maxPermSize = 256;
		}
		else if (appType == ApplicationType.vShpereManager) {
			maxPermSizeStr = "-XX:MaxPermSize=256M";
			maxPermSize = 256;
		}
		else
			return false;
		
		List<String> optionValues = new ArrayList<String>();
		String keyName = "Java";
		String valueName = "Options";
		
		int result = WSJNI.GetRegMultiStringValue(keyName, valueName, getRegKeyPath(isAMD64), optionValues);
		if(result != 0){
			logger.error("Failed to get the java options");
			return false;
		}
		
		int index = -1;
		int currentPermSize = 0;
		for (int i = 0; i< optionValues.size(); i++) {
			String str = optionValues.get(i);
			if(str.trim().startsWith("-XX:MaxPermSize")){
				//-XX:MaxPermSize=128m
				currentPermSize = getCurrentInt(str.trim());
				index = i;
				break;
			}
		}
		if(currentPermSize< maxPermSize){
			if(index == -1){
				optionValues.add(maxPermSizeStr);
			}
			else{
				optionValues.set(index, maxPermSizeStr);
			}
			result = WSJNI.SetRegMultiStringValue(keyName, valueName, getRegKeyPath(isAMD64), optionValues);
			if(result!=0){
				logger.error("Failed to set the java options");
				return false;
			}
			logger.info("set the java options: " + maxPermSizeStr + " with current PermSize: "+currentPermSize);
			return true;
		}
		return false;
	}
	
	private int getCurrentInt(String str){
		logger.info("begin to parse the str:"+str);
		String[] temp = str.split("=");
		if(temp.length<=1)
			return -1;
		
		char[] permChars = temp[1].toCharArray();
		StringBuilder strBuilder = new StringBuilder();
		for (char c : permChars) {
			if((c>='0')&&(c<='9')){
				strBuilder.append(c);
			}
			else{
				break;
			}
		}
		
		if(strBuilder.length()==0)
			return -1;
		else
			return Integer.parseInt(strBuilder.toString());
	}
}
