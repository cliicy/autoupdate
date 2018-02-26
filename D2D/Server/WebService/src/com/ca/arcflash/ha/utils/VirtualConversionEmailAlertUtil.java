package com.ca.arcflash.ha.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.model.DiskInfo;
import com.ca.arcflash.job.AFJob;
import com.ca.arcflash.job.alert.AlertJob;
import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.alert.EmailModel;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.replication.ReplicationMessage;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class VirtualConversionEmailAlertUtil {
	private static final Logger logger = Logger.getLogger(VirtualConversionEmailAlertUtil.class);
	private static final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(DataFormatUtil.getDateFormatLocale());
	public static final DecimalFormat number = new DecimalFormat("0.00", formatSymbols);
	//Alert string 
	public static final String COLDSTANDBY_ALERT_AUTO_FAILOVER = "COLDSTANDBY_ALERT_AUTO_FAILOVER";
	public static final String COLDSTANDBY_ALERT_MANUAL_FAILOVER = "COLDSTANDBY_ALERT_MANUAL_FAILOVER";
	public static final String COLDSTANDBY_ALERT_FREE_THRESHOLD_HYPERV = "COLDSTANDBY_ALERT_FREE_THRESHOLD_HYPERV";
	public static final String COLDSTANDBY_ALERT_FREE_THRESHOLD_VMWARE = "COLDSTANDBY_ALERT_FREE_THRESHOLD_VMWARE";
	public static final String COLDSTANDBY_ALERT_FREE_SPACE = "COLDSTANDBY_ALERT_FREE_SPACE";
	public static final String COLDSTANDBY_ALERT_FREE_SPACE_VOLUME = "COLDSTANDBY_ALERT_FREE_SPACE_VOLUME";
	public static final String COLDSTANDBY_ALERT_FREE_SPACE_DATASTORE = "COLDSTANDBY_ALERT_FREE_SPACE_DATASTORE";
	public static final String COLDSTANDBY_ALERT_CONVERSION_ERROR = "COLDSTANDBY_ALERT_CONVERSION_ERROR";
	public static final String COLDSTANDBY_ALERT_LICENSE_FIAL = "COLDSTANDBY_ALERT_LICENSE_FIAL";
	public static final String COLDSTANDBY_ALERT_FAILED_CONNECT_VIRTUALIZATION_SERVER = "COLDSTANDBY_ALERT_FAILED_CONNECT_VIRTUALIZATION_SERVER";
	public static final String COLDSTANDBY_ALERT_MISS_HEATBEAT = "COLDSTANDBY_ALERT_MISS_HEATBEAT";
	public static final String COLDSTANDBY_ALERT_CLICK_HERE = "COLDSTANDBY_ALERT_CLICK_HERE";
	public static final String COLDSTANDBY_ALERT_CONVERSION_CLICK_HERE_TEXT = "COLDSTANDBY_ALERT_CONVERSION_CLICK_HERE_TEXT";
	//end
	
	public static int sendAlertForWarningSpace(Set<String> usedDataStore, 
			HashMap<String,DiskInfo> freeSizeGroup, String afguid, long jobID) {
		try {
			AlertJobScript alertJobScript = HAService.getInstance().getAlertJobScript(afguid);
			if(alertJobScript == null) {
				return 1;
			}
			if(!alertJobScript.isReplicationSpaceWarning()) {
				return 1;
			}
			
			IEmailAlertFormater formater = getEmailAlertFormator(alertJobScript);
			
			StringBuilder alertMessage = new StringBuilder();
			float measureNumber = alertJobScript.getSpaceMeasureNumber();
			String measureUnit = alertJobScript.getSpaceMeasureUnit();
			
			boolean thresholdReached = addDiskDetails(alertMessage, usedDataStore,
														freeSizeGroup, measureNumber, measureUnit, afguid, formater);
			
			StringBuilder logMessage = new StringBuilder();
			if(formater instanceof VCMEmailAlertTextFormater) {
				logMessage = alertMessage;
			}
			else {
				addDiskDetails(logMessage, usedDataStore,
						freeSizeGroup, measureNumber, measureUnit, afguid, new VCMEmailAlertTextFormater());
			}
			
			String activityLogMsg = logMessage.toString().replaceAll("\n", " ");
			
			if(thresholdReached) {
				addClickHereStr(alertMessage, formater);
				
				sendAlertMail(AlertType.ReplicationSpaceWarning, alertMessage.toString(), activityLogMsg, afguid, jobID, null);
				return 0;
			}
		
		}catch(Exception e) {
			logger.error("Error occurs while sending email alert.", e);
		}
		
		return 1;
	}
	
	public static String bytes2String(long bytes){	
		if (bytes <1024)
			return WebServiceMessages.getResource("bytesString", bytes+"");
		else if (bytes<(1024*1024)) {
			String kb = number.format(((double)bytes)/1024);
			if(kb.startsWith("1024"))
				return WebServiceMessages.getResource("MBString", "1");
			
			return WebServiceMessages.getResource("KBString", kb);
		}
		else if (bytes<(1024*1024*1024)) {
			String mb = number.format(((double)bytes)/(1024*1024));
			if(mb.startsWith("1024"))
				return WebServiceMessages.getResource("GBString", "1");
			
			return WebServiceMessages.getResource("MBString", mb);
		}
		else
			return WebServiceMessages.getResource("GBString", number.format(((double)bytes)/(1024*1024*1024)));
	}
	
//	public static void main(String... args) {
//		System.out.println(bytes2String(121));
//		System.out.println(bytes2String(1223321));
//		System.out.println(bytes2String(12232423421l));
//		System.out.println(bytes2String(12123423412432432l));
//	}
	/**
	 * 
	 * @param alertType
	 * @param alertMessage
	 * @param activityLogMsg if this param is null, <code>alertMessage</code> is used.
	 * @param afguid
	 * @return
	 */
	public static int sendAlertMail(AlertType alertType,String alertMessage, String activityLogMsg, String afguid, long jobID, Date executeTime) {
		//String uuid = evaluteAFGuid(afguid);
		
		if(StringUtil.isEmptyOrNull(activityLogMsg))
			activityLogMsg = alertMessage;
		//if more alerts need adding activity log, just add more or(||) condition in the following if condition.
		if(alertType == AlertType.MissHeatBeat || alertType == AlertType.AutoFaiover || alertType == AlertType.MaualFaiover
				|| alertType == AlertType.ReplicationSpaceWarning) {
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID , Constants.AFRES_AFJWBS_GENERAL, 
					new String[] {activityLogMsg, "", "", "", "" }, afguid);
		}
		
		AFJob afJob = HAService.getInstance().getAlertJob(afguid);
		if(afJob == null) {
			return 1;
		}
		
		AlertJob job = (AlertJob)afJob;
		if(job !=null) {
			
			if(isSendAlert(alertType, job.getJobScript())) {
				String normalizedAlertMessage = getQualifiedAlertMsg(alertType, alertMessage, afguid, executeTime);
				job.scheduleNow( alertType, normalizedAlertMessage );
			}
			
		}
		
		return 0;
	}
	
	private static String getConversionJobStatus(AlertType alertType){
		String conversionJobStatus = "";
		if(alertType == AlertType.ConversionFailed)
			conversionJobStatus = ReplicationMessage.getResource("REPLICATION_JOB_STATUS_FAIL");
		else if(alertType == AlertType.CancelConversionJob)
			conversionJobStatus = ReplicationMessage.getResource("REPLICATION_JOB_STATUS_CANCEL");
		else if(alertType == AlertType.ConversionSuccess)
			conversionJobStatus = ReplicationMessage.getResource("REPLICATION_JOB_STATUS_SUCCESS");
		return conversionJobStatus;
	}
	
	public static String getQualifiedAlertMsg(AlertType alertType,
			String alertMessage, String afguid, Date executeTime) {
		
		// this type of alert message has processed outside.
		if(alertType == AlertType.ReplicationSpaceWarning)
			return alertMessage;
		
		FailoverJobScript failoverScript = HAService.getInstance().getFailoverJobScript(afguid);
		String sourceName = null; 
		if(failoverScript != null)
			sourceName = failoverScript.getProductionServerName();
		
		HeartBeatJobScript heartbeatScript = HAService.getInstance().getHeartBeatJobScript(afguid);
		String monitorName = null;
		if(heartbeatScript != null)
			monitorName = heartbeatScript.getHeartBeatMonitorHostName();
		
		//if this runs on monitor for AlertType.MissHeatBeat, AlertType.AutoFaiover 
		//and AlertType.MaualFaiover alert
		if(sourceName != null && monitorName == null 
				&& (alertType == AlertType.MissHeatBeat || alertType == AlertType.MaualFaiover 
						|| alertType == AlertType.AutoFaiover || alertType == AlertType.FailoverFailure)){
			
			monitorName = ServiceContext.getInstance().getLocalMachineName();
			
		}
			
		ReplicationJobScript repScript = HAService.getInstance().getReplicationJobScript(afguid);
		String hpvType = null;
		String hypervisor = null;
		if(repScript != null) {
			ReplicationDestination replicationDestinaiton = HAService.getInstance().getReplicationDestinaiton(repScript);
			if(replicationDestinaiton != null) {
				Protocol destProtocol = replicationDestinaiton.getDestProtocol();
				hpvType = ReplicationMessage.getReplicationDestType(destProtocol);
			
				if(replicationDestinaiton instanceof ARCFlashStorage){
					ARCFlashStorage tmp = (ARCFlashStorage)replicationDestinaiton;
					hypervisor = tmp.getHostName();
					
				}else if(replicationDestinaiton instanceof VMwareESXStorage){
					VMwareESXStorage tmp = (VMwareESXStorage)replicationDestinaiton;
					hypervisor = tmp.getESXHostName();
					
				}else if(replicationDestinaiton instanceof VMwareVirtualCenterStorage){
					VMwareVirtualCenterStorage tmp = (VMwareVirtualCenterStorage)replicationDestinaiton;
					hypervisor = tmp.getEsxName() == null ? tmp.getESXHostName() : tmp.getEsxName();
				}
			
			}
		}
		
		long jobID = -1;
		switch (alertType) {
			case ConversionFailed:
			case VMHostNotReachable:
			case LicenseFailed:
			case ConversionSuccess:
			case CancelConversionJob:
			
				long startTimeLong = -1;
				RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid);
				synchronized (jobMonitor) {
					if(executeTime == null) {
						startTimeLong = jobMonitor.getRepStartTime();
						startTimeLong = startTimeLong < jobMonitor.getRepJobStartTime() ? jobMonitor.getRepJobStartTime(): startTimeLong;
					}
				
					if(alertType == AlertType.ConversionFailed || alertType == AlertType.ConversionSuccess
							|| alertType == AlertType.CancelConversionJob) {
						jobID = jobMonitor.getId();
					}
				}
			
				if(startTimeLong > 0) {
					executeTime = new Date(startTimeLong);
				}
		}
		
		StringBuilder builder = new StringBuilder();
		AlertJobScript alertScript = HAService.getInstance().getAlertJobScript(afguid);
		if(alertScript != null && alertScript.getEmailModel() != null 
				&& alertScript.getEmailModel().isHtmlFormat()) {

			builder.append("<HTML>")
				.append(EmailContentTemplate.getHTMLHeaderSection())
				.append("<BODY>")
				.append("<P/><P/>\n")
				.append("<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">").append("\n");
				
			if(!StringUtil.isEmptyOrNull(sourceName)) {
				builder.append("<TR>")
				
				.append("<TD BGCOLOR=#DDDDDD><B>")
				.append(ReplicationMessage.getResource("REPLICATION_SOURCE_MACHINE"))
				.append("</B></TD>")
				
				.append("<TD>")
				.append(sourceName)
				.append("</TD>")
				
				.append("</TR>")
				.append("\n");
			}
			
			if(!StringUtil.isEmptyOrNull(hpvType)) {
				builder.append("<TR>")
				
				.append("<TD BGCOLOR=#DDDDDD><B>")
				.append(ReplicationMessage.getResource("REPLICATION_VIRTUL_TYPE"))
				.append("</B></TD>")
				
				.append("<TD>")
				.append(hpvType)
				.append("</TD>")
				
				.append("</TR>")
				.append("\n");
			}
			
			if(!StringUtil.isEmptyOrNull(hypervisor)) {
				builder.append("<TR>")
				
				.append("<TD BGCOLOR=#DDDDDD><B>")
				.append(ReplicationMessage.getResource("REPLICATION_HYPERVISOR_NAME"))
				.append("</B></TD>")
				
				.append("<TD>")
				.append(hypervisor)
				.append("</TD>")
				
				.append("</TR>")
				.append("\n");
			}
			
			if(!StringUtil.isEmptyOrNull(monitorName)) {
				builder.append("<TR>")
				
				.append("<TD BGCOLOR=#DDDDDD><B>")
				.append(ReplicationMessage.getResource("REPLICATION_MONITOR_NAME"))
				.append("</B></TD>")
				
				.append("<TD>")
				.append(monitorName)
				.append("</TD>")
				
				.append("</TR>")
				.append("\n");
			}
			
			if(alertType == AlertType.ConversionFailed || alertType == AlertType.ConversionSuccess 
					|| alertType == AlertType.CancelConversionJob){
				
				builder.append("<TR>")
				
				.append("<TD BGCOLOR=#DDDDDD><B>")
				.append(ReplicationMessage.getResource("REPLICATION_JOB_STATUS_TITLE"))
				.append("</B></TD>")
				
				.append("<TD>")
				.append(getConversionJobStatus(alertType))
				.append("</TD>")
				
				.append("</TR>")
				.append("\n");
			}
			
			if(failoverScript != null 
					&& (alertType == AlertType.MissHeatBeat || alertType == AlertType.AutoFaiover)) {
				int timeoutInsec = failoverScript.getHeartBeatFailoverTimeoutInSecond();
				long frequencyInsec = failoverScript.getHeartBeatFrequencyInSeconds();
				
				builder.append("<TR>")
				
				.append("<TD BGCOLOR=#DDDDDD><B>")
				.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_HAERT_BEAT_TIMEOUT_NAME))
				.append("</B></TD>")
				
				.append("<TD>")
				.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_HAERT_BEAT_TIMEOUT_SECONDS, timeoutInsec + ""))
				.append("</TD>")
				
				.append("</TR>")
				.append("\n");
				
				if(alertType == AlertType.AutoFaiover && frequencyInsec > 0) {
					builder.append("<TR>")
				
					.append("<TD BGCOLOR=#DDDDDD><B>")
					.append(ReplicationMessage.getResource(ReplicationMessage.HEART_BEAT_FREQUENCY))
					.append("</B></TD>")
					
					.append("<TD>")
					.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_HAERT_BEAT_TIMEOUT_SECONDS, frequencyInsec+""))
					.append("</TD>")
					
					.append("</TR>")
					.append("\n");
				}
			}
			
			if(!StringUtil.isEmptyOrNull(alertMessage)) {
				builder.append("<TR>")
				
				.append("<TD BGCOLOR=#DDDDDD><B>")
				.append(ReplicationMessage.getResource("REPLICATION_ALERT_CAUSE"))
				.append("</B></TD>")
				
				.append("<TD>")
				.append(alertMessage)
				.append("</TD>")
				
				.append("</TR>")
				.append("\n");
			}
			
			if(executeTime != null) {

				builder.append("<TR>")
				
				.append("<TD BGCOLOR=#DDDDDD><B>")
				.append(WebServiceMessages.getResource("ThreshHold_Email_Execution_Time"))
				.append("</B></TD>")
				
				.append("<TD>")
				.append(BackupConverterUtil.dateToString(executeTime))
				.append("</TD>")
				
				.append("</TR>")
				.append("\n");
			}
			
			builder.append("</TABLE><P/><P/>\n\n");
			
			if(jobID > 0) {
				ActivityLogResult activityLog = getActivityLog(jobID, afguid);
				
				if(activityLog != null && activityLog.getTotalCount() > 0) {
					String activityLogHeader = WebServiceMessages.getResource("ActivityLog");
					String content = EmailContentTemplate.convertLogToHtml(activityLog, jobID);
					builder.append("<h1>")
							.append(activityLogHeader)
							.append("</h1>")
							.append(content)
							.append("<P/><P/>\n");
				}
			}
			
			String edgeVCMURL = getEdgeVCMURL();		
			if(!StringUtil.isEmptyOrNull(edgeVCMURL)) {
				builder.append(getHtmlClickHere(edgeVCMURL));
			}
			
			builder.append("</BODY>");
			builder.append("</HTML>");
			
		}
		else {
			if(!StringUtil.isEmptyOrNull(sourceName)) {
				builder.append(ReplicationMessage.getResource("REPLICATION_SOURCE_MACHINE"))
					.append(": ")
					.append(sourceName)
					.append("\n");
			}
			
			if(!StringUtil.isEmptyOrNull(hpvType)) {
				builder.append(ReplicationMessage.getResource("REPLICATION_VIRTUL_TYPE"))
				.append(": ")
				.append(hpvType)
				.append("\n");
			}
			
			if(!StringUtil.isEmptyOrNull(hypervisor)) {
				builder.append(ReplicationMessage.getResource("REPLICATION_HYPERVISOR_NAME"))
				.append(": ")
				.append(hypervisor)
				.append("\n");
			}
			
			if(!StringUtil.isEmptyOrNull(monitorName)) {
				builder.append(ReplicationMessage.getResource("REPLICATION_MONITOR_NAME"))
				.append(": ")
				.append(monitorName)
				.append("\n");
			}
			
			if(alertType == AlertType.ConversionFailed || alertType == AlertType.ConversionSuccess 
					|| alertType == AlertType.CancelConversionJob){
				builder.append(ReplicationMessage.getResource("REPLICATION_JOB_STATUS_TITLE"))
				.append(": ")
				.append(getConversionJobStatus(alertType))
				.append("\n");
			}
			
			if(failoverScript != null 
					&& (alertType == AlertType.MissHeatBeat || alertType == AlertType.AutoFaiover)) {
				int timeoutInsec = failoverScript.getHeartBeatFailoverTimeoutInSecond();
				long frequencyInsec = failoverScript.getHeartBeatFrequencyInSeconds();
				
				builder.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_HAERT_BEAT_TIMEOUT_NAME))
				.append(": ")
				.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_HAERT_BEAT_TIMEOUT_SECONDS, timeoutInsec + ""))
				.append("\n");
				
				if(alertType == AlertType.AutoFaiover && frequencyInsec > 0) {
					builder.append(ReplicationMessage.getResource(ReplicationMessage.HEART_BEAT_FREQUENCY))
					.append(": ")
					.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_HAERT_BEAT_TIMEOUT_SECONDS, frequencyInsec + ""))
					.append("\n");
				}
			
			}
			
			if(!StringUtil.isEmptyOrNull(alertMessage)) {
				builder.append(ReplicationMessage.getResource("REPLICATION_ALERT_CAUSE"))
				.append(": ")
				.append(alertMessage)
				.append("\n");
			}
			
			if(executeTime != null) {
				builder.append(WebServiceMessages.getResource("ThreshHold_Email_Execution_Time"))
				.append(": ")
				.append(BackupConverterUtil.dateToString(executeTime))
				.append("\n\n");
			}
			
			if(jobID > 0) {
				ActivityLogResult activityLog = getActivityLog(jobID, afguid);
				
				if(activityLog != null && activityLog.getTotalCount() > 0) {
					String activityLogHeader = WebServiceMessages.getResource("ActivityLog");
					String content = EmailContentTemplate.convertLogToPlainText(activityLog, jobID);
					builder	.append(activityLogHeader)
							.append("\n")
							.append(content)
							.append("\n\n");
				}
			}
			
			String edgeVCMURL = getEdgeVCMURL();		
			if(!StringUtil.isEmptyOrNull(edgeVCMURL)) {
				builder.append(getTextClickHere(edgeVCMURL));
			}
		}
			
		return builder.toString();
	}

	private static ActivityLogResult getActivityLog(long jobId, String afguid) {
		ActivityLogResult log = new ActivityLogResult();
		try {
			if(StringUtil.isEmptyOrNull(afguid) || HAService.getInstance().retrieveCurrentNodeID().equals(afguid))
				log = CommonService.getInstance().getJobActivityLogs(jobId, 0, 512);
			else {
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(afguid);
				log = VSphereService.getInstance().GetJobLogActivityForVM(jobId, 0, 512, vm);
			}
			
			return log;
		} catch (Exception e) {
			logger.error("Fails to get activity log jobId:" + jobId, e);
		}
		return null;
	}
	
	public static String getHtmlClickHere(String edgeVCMURL) {
		return String.format(
				WebServiceMessages.getResource(VirtualConversionEmailAlertUtil.COLDSTANDBY_ALERT_CLICK_HERE)
				, edgeVCMURL);
	}
	
	public static String getTextClickHere(String edgeVCMURL) {
		return String.format(
				WebServiceMessages.getResource(VirtualConversionEmailAlertUtil.COLDSTANDBY_ALERT_CONVERSION_CLICK_HERE_TEXT), edgeVCMURL);
	}
	
	public static boolean isSendAlert(AlertType alertType, AlertJobScript alertJobScript) {
		boolean result = false;
		if((alertJobScript == null) ||(alertType == AlertType.Unknown)) {
			return result;
		}
		
		if(alertType == AlertType.AutoFaiover) {
			result = alertJobScript.isAutoFaiover();
		}
		else if(alertType == AlertType.MaualFaiover) {
			result = alertJobScript.isManualFailover();
		}
		else if(alertType == AlertType.ConversionFailed || alertType == AlertType.CancelConversionJob) {
			result = alertJobScript.isReplicationError();
		}
		else if(alertType == AlertType.LicenseFailed) {
			result = alertJobScript.isReplicationError();// alertJobScript.isLicense();
		}
		else if(alertType == AlertType.VMHostNotReachable) {
			result = alertJobScript.isNotreachable();
		}
		else if(alertType == AlertType.ReplicationSpaceWarning) {
			result = alertJobScript.isReplicationSpaceWarning();
		}
		else if(alertType == AlertType.MissHeatBeat) {
			result = alertJobScript.isMissHeatbeat();
		}
		else if(alertType == AlertType.ConversionSuccess){
			result = alertJobScript.isConversionSuccess();
		}
		else if(alertType == AlertType.FailoverFailure){
			result = alertJobScript.isFailoverFailure();
		}
		
		return result;
	}
	
	public static IEmailAlertFormater getEmailAlertFormator(
			AlertJobScript alertJobScript) {
		EmailModel email = alertJobScript.getEmailModel();
		IEmailAlertFormater formater = null;
		if(email != null && !email.isHtmlFormat()) {
			formater = new VCMEmailAlertTextFormater();
		}
		else {
			formater = new VCMEmailAlertHTMLFormater();
		}
		return formater;
	}

	private static boolean addDiskDetails(StringBuilder alertMessage,
			Set<String> usedDataStore, HashMap<String, DiskInfo> freeSizeGroup,
			float measureNumber, String measureUnit, String afguid, IEmailAlertFormater formater) {
		
		boolean isThresholdReached = false;
		
		boolean destAndThreshHold = false; 
		boolean isPercent = false;
		if(measureUnit.equals("%")) {
			isPercent = true;
		}
		
		for (String storeName : freeSizeGroup.keySet()) {
			if(!usedDataStore.contains(storeName)) 
				continue;
			
			DiskInfo diskInfo = freeSizeGroup.get(storeName);
			long freeSizeBytes = diskInfo.getFreeSize();
			long totalSizeBytes = diskInfo.getTotalSize();
			long GB = 1024*1024;
			long freeSize = freeSizeBytes/GB;
			boolean reached = false;
			if(isPercent) {
				float currentPercent = (float)freeSizeBytes/totalSizeBytes;
				float alertPrecent = (float)measureNumber/100;
				if(currentPercent<alertPrecent) {
					isThresholdReached = true;
					reached = true;
				}
			}
			else {
				long freeSizeMB = freeSize;
				if(freeSizeMB<measureNumber) {
					isThresholdReached = true;
					reached = true;
				}
			}
			
			if(reached) {
				if(!destAndThreshHold) {
					addDestinationThreshold(alertMessage, afguid,
							measureNumber, measureUnit, formater);
					destAndThreshHold = true;
				}
				formater.addDataStoreThresholdPart(alertMessage, storeName, totalSizeBytes, freeSizeBytes);
			}
		}
		
		return isThresholdReached;
	}

	private static void addClickHereStr(StringBuilder alertMessage, IEmailAlertFormater formater) {
		
		String edgeVCMURL = getEdgeVCMURL();
		
		formater.addClickHerePart(alertMessage, edgeVCMURL);
	}

	private static String getEdgeVCMURL() {
		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();

		String edgeVCMURL = null;
		EdgeRegInfo info = edgeRegInfo.getEdgeRegInfo(ApplicationType.VirtualConversionManager);
		if(info != null && info.getEdgeAppType() == ApplicationType.VirtualConversionManager) {
			edgeVCMURL = info.getConsoleUrl();
//			String edgeWSDL = info.getEdgeWSDL();
//			if(edgeWSDL!=null){
//				edgeVCMURL = edgeWSDL.split("services")[0];
//			}
		}
		return edgeVCMURL;
	}

	private static void addDestinationThreshold(StringBuilder alertMessage, String afguid,
			float measureNumber, String measureUnit, IEmailAlertFormater formater) {
		
		Date executeTime = null;
		long startTimeLong = -1;
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid);
		synchronized (jobMonitor) {
			startTimeLong = jobMonitor.getRepStartTime();
			startTimeLong = jobMonitor.getRepJobStartTime() > startTimeLong ? jobMonitor.getRepJobStartTime() : startTimeLong;
		}
		if(startTimeLong > 0) {
			executeTime = new Date(startTimeLong);
		}
		
		Protocol protocol = null;
		String hostName = "";
		String threshold = "";
//		String destinationStr = null;
		ReplicationJobScript repScript = HAService.getInstance().getReplicationJobScript(afguid);
		if(repScript != null && 
				repScript.getReplicationDestination() != null && repScript.getReplicationDestination().size() > 0) {
			threshold = measureNumber+measureUnit;
			ReplicationDestination dest = repScript.getReplicationDestination().get(0);
			if(dest instanceof ARCFlashStorage){
				ARCFlashStorage tmp = (ARCFlashStorage)dest;
				protocol = Protocol.HeartBeatMonitor;
				hostName = tmp.getHostName();
//				destinationStr = String.format(WebServiceMessages.getResource(COLDSTANDBY_ALERT_FREE_THRESHOLD_HYPERV),
//						hostName, threshold);
				
			}else if(dest instanceof VMwareESXStorage){
				VMwareESXStorage tmp = (VMwareESXStorage)dest;
				protocol = Protocol.VMwareESX;
				hostName = tmp.getESXHostName();
					
//				destinationStr = String.format(WebServiceMessages.getResource(COLDSTANDBY_ALERT_FREE_THRESHOLD_VMWARE),
//						hostName, threshold);
				
			}else if(dest instanceof VMwareVirtualCenterStorage){
				VMwareVirtualCenterStorage tmp = (VMwareVirtualCenterStorage)dest;
				protocol = Protocol.VMwareVCenter;
				hostName = tmp.getEsxName() == null ? tmp.getESXHostName() : tmp.getEsxName();
//				destinationStr = String.format(WebServiceMessages.getResource(COLDSTANDBY_ALERT_FREE_THRESHOLD_VMWARE),
//						hostName, threshold);
			}
		}
		
		if(protocol != null) {
			formater.addDestinationThresholdPart(alertMessage, protocol, hostName, threshold, executeTime);
		}
	}

	public static String getAlertMessage(AlertType alertType) {
		String content = "";
		if(alertType == AlertType.Unknown) {
			return "";
		}
		
		if(alertType == AlertType.AutoFaiover) {
			content =  WebServiceMessages.getResource(COLDSTANDBY_ALERT_AUTO_FAILOVER);
		}
		else if(alertType == AlertType.MaualFaiover) {
			content = WebServiceMessages.getResource(COLDSTANDBY_ALERT_MANUAL_FAILOVER);
		}
		else if(alertType == AlertType.ConversionFailed) {
			content = WebServiceMessages.getResource(COLDSTANDBY_ALERT_CONVERSION_ERROR);
		}
		else if(alertType == AlertType.LicenseFailed) {
			content = WebServiceMessages.getResource(COLDSTANDBY_ALERT_LICENSE_FIAL);
		}
		else if(alertType == AlertType.VMHostNotReachable) {
			content = WebServiceMessages.getResource(COLDSTANDBY_ALERT_FAILED_CONNECT_VIRTUALIZATION_SERVER);
		}
		else if(alertType == AlertType.ReplicationSpaceWarning) {
			content = WebServiceMessages.getResource(COLDSTANDBY_ALERT_FREE_SPACE);
		}
		else if(alertType == AlertType.MissHeatBeat) {
			content = WebServiceMessages.getResource(COLDSTANDBY_ALERT_MISS_HEATBEAT);
		}
		
		return content;
	}
	
	private static void test(AlertType alertType){
		
		switch (alertType) {
		case ConversionFailed:
		case VMHostNotReachable:
		case LicenseFailed:
			
			System.out.println(111111);
			
		}
	}
	
	public static void main(String[] args) {
		
		test(AlertType.ConversionFailed);
		test(AlertType.VMHostNotReachable);
		test(AlertType.LicenseFailed);
		
	}
	
	
	

}
