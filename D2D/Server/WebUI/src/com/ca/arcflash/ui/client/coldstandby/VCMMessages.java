package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.ui.client.UIContext;

public class VCMMessages {
	public static String productName = UIContext.Constants.virtualStandyNameTranslate();
	public static String productNameLowerCase = UIContext.Constants.virtualStandyNameTranslate();
	
	public static String virtualConversionMonitorTitle(int convertingNum, int totalNum) {
		return UIContext.Messages.virtualConversionMonitorTitle(convertingNum, totalNum, productName);
	}
	
	public static String replicaJobDetailWindowTitle(){
		return UIContext.Messages.replicaJobDetailWindowTitle(productName);
	}
	
	public static String virtualConversionSummary(String server) {
		return UIContext.Messages.virtualConversionSummary(server, productName);
	}
	
	public static String virtualConversionMostRecent() {
		return UIContext.Messages.virtualConversionMostRecent(productName);
	}
	
	public static String coldStandbyTaskEnableAutoOfflineCopy() {
		return UIContext.Messages.coldStandbyTaskEnableAutoOfflineCopy(productName);
	}
	
	public static String coldStandbyTaskDisableAutoOfflineCopy() {
		return UIContext.Messages.coldStandbyTaskDisableAutoOfflineCopy(productName);
	}
	
	public static String coldStandbyTaskSettings() {
		return UIContext.Messages.coldStandbyTaskSettings(productName);
	}
	
	public static String coldStandbyOfflineCopyCommandNowResult() {
		return UIContext.Messages.coldStandbyOfflineCopyCommandNowResult(productNameLowerCase);
	}
	
	public static String coldStandbyenEnableAutoOfflieCopyResult() {
		return UIContext.Messages.coldStandbyenEnableAutoOfflieCopyResult(productNameLowerCase);
	}
	
	public static String coldStandbyDisableAutoOfflieCopyResult() {
		return UIContext.Messages.coldStandbyDisableAutoOfflieCopyResult(productNameLowerCase);
	}
	
	public static String coldStandbySettingEmailAlertReplicationError() {
		return UIContext.Messages.coldStandbySettingEmailAlertReplicationError(productName);
	}
	
	public static String coldStandbySettingEmailAlertConversionSuccess() {
		return UIContext.Messages.coldStandbySettingEmailAlertConversionSuccess(productName);
	}
	
	public static String homepageTaskVirtualConversion() {
		return UIContext.Messages.homepageTaskVirtualConversion(productName);
	}
	
	public static String destinationColdstandbySettingMsg() {
		return UIContext.Messages.destinationColdstandbySettingMsg(productNameLowerCase);
	}
}
