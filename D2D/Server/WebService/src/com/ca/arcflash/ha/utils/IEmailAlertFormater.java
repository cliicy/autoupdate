package com.ca.arcflash.ha.utils;

import java.util.Date;

import com.ca.arcflash.jobscript.replication.Protocol;

public interface IEmailAlertFormater {
	
	public void addDestinationThresholdPart(
			StringBuilder alertMessage, Protocol protocol, String hostName, String threshold, Date executeTime);

	public void addDataStoreThresholdPart(StringBuilder alertMessage,
			String storeName, long totalSize, long freeSize);

	public void addClickHerePart(StringBuilder alertMessage,
			String edgeVCMURL);
}