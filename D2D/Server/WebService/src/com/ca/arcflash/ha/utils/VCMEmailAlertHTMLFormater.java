package com.ca.arcflash.ha.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.webservice.replication.ReplicationMessage;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class VCMEmailAlertHTMLFormater implements IEmailAlertFormater {
	Protocol destProtocol = null;
	Date executeTime = null;
	List<DataStore> dataStoreList = new ArrayList<DataStore>();
	
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ha.utils.IEmailAlertUtil#addDestinationThresholdPart(java.lang.StringBuilder, java.lang.String)
	 */
	public void addDestinationThresholdPart(StringBuilder emailContent,
			Protocol protocol, String hostName, String threshold, Date executeTime) {
		this.destProtocol = protocol;
		this.executeTime = executeTime;
		
		String serverTypeName = "";
		if(protocol ==  Protocol.HeartBeatMonitor)
			serverTypeName = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_TYPE_HYPERV);
		else 
			serverTypeName = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_TYPE_VMWARE_ESX);
			
		emailContent.append("<HTML>")
			.append(EmailContentTemplate.getHTMLHeaderSection()).append("\n")
			.append("<BODY>")
			.append("<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">").append("\n")
			
			.append("<TR>")
			.append("<TD BGCOLOR=#DDDDDD><B>")
			.append(serverTypeName)
			.append("</B></TD>")
			
			.append("<TD>")
			.append(hostName)
			.append("</TD>")
			
			.append("</TR>\n")
			
			.append("<TR>")
			.append("<TD BGCOLOR=#DDDDDD><B>")
			.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DESTINATION_TRESHOLD))
			.append("</B></TD>")
			
			.append("<TD>")
			.append(threshold)
			.append("</TD>")
			
			.append("</TR>\n");
			
			
			
	}
	
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ha.utils.IEmailAlertUtil#addDataStoreThresholdPart(java.lang.StringBuilder, java.lang.String, long, long)
	 */
	public void addDataStoreThresholdPart(StringBuilder emailContent,
			String storeName, long totalSize, long freeSize) {
		
		if(StringUtil.isEmptyOrNull(storeName) && totalSize == 0 && freeSize == 0)
			return; 
		
		DataStore store = new DataStore();
		store.storeName = storeName;
		store.totalSize = totalSize;
		store.freeSize = freeSize;
		
		dataStoreList.add(store);
	}
	
	
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ha.utils.IEmailAlertUtil#addClickHerePart(java.lang.StringBuilder, java.lang.String)
	 */
	public void addClickHerePart(StringBuilder emailContent,
			String edgeVCMURL) {
		
		if(dataStoreList.size() > 1) {
			if(executeTime != null) {
				addExecuteTime(emailContent);
			}
			
			String destResourceId = getDestNameResouceID();
			
			String destNoteResourceId = "COLDSTANDBY_ALERT_FREE_THRESHOLD_DATA_STORES";
			if(destProtocol == Protocol.HeartBeatMonitor)
				destNoteResourceId = "COLDSTANDBY_ALERT_FREE_THRESHOLD_VOLUMES";
			
			emailContent.append("</TABLE><P/><P/>\n");
			
			emailContent.append(WebServiceMessages.getResource(destNoteResourceId))
				.append("<BR>\n");
			
			emailContent.append("<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">").append("\n")
			.append("<TR>")
			
			.append("<TD BGCOLOR=#DDDDDD><B>")
			.append(ReplicationMessage.getResource(destResourceId))
			.append("</B></TD>")
			
			.append("<TD BGCOLOR=#DDDDDD><B>")
			.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_TOTAL_SPACE))
			.append("</B></TD>")
			
			.append("<TD BGCOLOR=#DDDDDD><B>")
			.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_FREE_SPACE))
			.append("</B></TD>")
			
			.append("</TR>")
			.append("\n");
			
			for(DataStore store : dataStoreList) {
				String storeName = store.storeName;
				long totalSize = store.totalSize;
				long freeSize = store.freeSize;
				
				emailContent.append("<TR>")
				.append("<TD>").append(storeName).append("</TD>")
				.append("<TD>").append(VirtualConversionEmailAlertUtil.bytes2String(totalSize)).append("</TD>")
				.append("<TD>").append(VirtualConversionEmailAlertUtil.bytes2String(freeSize)).append("</TD>")
				.append("</TR>")
				.append("\n");
			}
			
		}
		else {
			if(dataStoreList.size() == 1) {
				DataStore store = dataStoreList.get(0);
				String storeName = store.storeName;
				long totalSize = store.totalSize;
				long freeSize = store.freeSize;
				
				String destResourceId = getDestNameResouceID();
				
				emailContent.append("<TR>")
					.append("<TD BGCOLOR=#DDDDDD><B>")
					.append(ReplicationMessage.getResource(destResourceId))
					.append("</B></TD>")
					
					.append("<TD>")
					.append(storeName)
					.append("</TD>")
					
					.append("</TR>\n")
					
					.append("<TR>")
					.append("<TD BGCOLOR=#DDDDDD><B>")
					.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_TOTAL_SPACE))
					.append("</B></TD>")
					
					.append("<TD>")
					.append(VirtualConversionEmailAlertUtil.bytes2String(totalSize))
					.append("</TD>")
					
					.append("</TR>\n")
				
					.append("<TR>")
					.append("<TD BGCOLOR=#DDDDDD><B>")
					.append(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_FREE_SPACE))
					.append("</B></TD>")
					
					.append("<TD>")
					.append(VirtualConversionEmailAlertUtil.bytes2String(freeSize))
					.append("</TD>")
					
					.append("</TR>\n");
				
			}
			if(executeTime != null)
				addExecuteTime(emailContent);
		}
		
		emailContent.append("</TABLE><P/><P/>\n");
		
		if(edgeVCMURL != null) {
			String clickHereToEdge = VirtualConversionEmailAlertUtil.getHtmlClickHere(edgeVCMURL);
			emailContent.append("\n").append(clickHereToEdge);
		}
		
		emailContent.append("</BODY>\n");
		emailContent.append("</HTML>");
	}

	private String getDestNameResouceID() {
		String destResourceId = ReplicationMessage.REPLICATION_DESTINATION;
		if(destProtocol == Protocol.HeartBeatMonitor)
			destResourceId = ReplicationMessage.REPLICATION_DESTINATION_VOLUME;
		return destResourceId;
	}

	private void addExecuteTime(StringBuilder emailContent) {
		emailContent.append("<TR>")
			.append("<TD BGCOLOR=#DDDDDD><B>")
			.append(WebServiceMessages.getResource("ThreshHold_Email_Execution_Time"))
			.append("</B></TD>")
			
			.append("<TD>")
			.append(BackupConverterUtil.dateToString(executeTime))
			.append("</TD>")
			
			.append("</TR>\n");
		
	}
	
	class DataStore{
		String storeName;
		long totalSize;
		long freeSize;
	}
}
