package com.ca.arcflash.ha.utils;

import java.util.Date;

import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class VCMEmailAlertTextFormater implements IEmailAlertFormater{
	Protocol destProtocol = null;
	
	@Override
	public void addDestinationThresholdPart(StringBuilder alertMessage,
			Protocol protocol, String hostName, String threshold, Date executeTime) {
		
		this.destProtocol = protocol;
		String resouceId = null;
		if(protocol ==  Protocol.HeartBeatMonitor) {
			resouceId = VirtualConversionEmailAlertUtil.COLDSTANDBY_ALERT_FREE_THRESHOLD_HYPERV;
		}
		else if(protocol == Protocol.VMwareESX || protocol == Protocol.VMwareVCenter){
			resouceId = VirtualConversionEmailAlertUtil.COLDSTANDBY_ALERT_FREE_THRESHOLD_VMWARE;
		}
		
		if(resouceId != null) { 
			String destinationStr = String.format(WebServiceMessages.getResource(resouceId),
					hostName, threshold);
			alertMessage.append(destinationStr).append("\n");
		}
	}

	@Override
	public void addDataStoreThresholdPart(StringBuilder alertMessage,
			String storeName, long totalSize, long freeSize) {
		
		String messageFormat = VirtualConversionEmailAlertUtil.getAlertMessage(AlertType.ReplicationSpaceWarning);
		if(destProtocol == Protocol.HeartBeatMonitor)
			messageFormat = WebServiceMessages.getResource(VirtualConversionEmailAlertUtil.COLDSTANDBY_ALERT_FREE_SPACE_VOLUME);
		else if(destProtocol == Protocol.VMwareESX || destProtocol == Protocol.VMwareVCenter)
			messageFormat = WebServiceMessages.getResource(VirtualConversionEmailAlertUtil.COLDSTANDBY_ALERT_FREE_SPACE_DATASTORE);
		
		alertMessage.append(String.format(messageFormat, storeName, 
				VirtualConversionEmailAlertUtil.bytes2String(totalSize), 
				VirtualConversionEmailAlertUtil.bytes2String(freeSize))).append("\n");
	}

	@Override
	public void addClickHerePart(StringBuilder alertMessage, String edgeVCMURL) {
		
		if(edgeVCMURL != null) {
			String clickHereToEdge = VirtualConversionEmailAlertUtil.getTextClickHere(edgeVCMURL);
			alertMessage.append("\n").append(clickHereToEdge);
		}
	}
}
