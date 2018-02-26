package com.ca.arcserve.edge.app.msp.webservice.messages;

public interface MspWebServiceMessages {
	
	String importReplicatedRemoteNodeFinished(String nodeName);
	String assignPlanToReplicatedRemoteNodeFinished(String planName, String nodeName);

}
