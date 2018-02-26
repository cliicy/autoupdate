package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.HashMap;
import java.util.Map;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;

public class D2DSyncMessage {
	private static Map<Integer, String> MessageMapList = new HashMap<Integer, String>();
	
	public final static int EDGE_D2D_SYNC_RESYNC_SUCCEEDED = 1;
	public final static int EDGE_D2D_SYNC_SUCCEEDED = 2;
	public final static int EDGE_D2D_SYNC_RESYNC_FAILED = 3;
	public final static int EDGE_D2D_SYNC_FAILED = 4;
	public final static int EDGE_D2D_SYNC_RESYNC_START = 5;
	public final static int EDGE_D2D_SYNC_START = 6;
	public final static int EDGE_D2D_SYNC_ACTIVE_LOG_SUCCEEDED = 7;
	public final static int EDGE_D2D_SYNC_ACTIVE_LOG_FAILED = 8;
	public final static int EDGE_D2D_SYNC_ARCHIVE_JOB_SUCCEEDED = 9;
	public final static int EDGE_D2D_SYNC_ARCHIVE_JOB_FAILED = 10;
	public final static int EDGE_D2D_SYNC_BACKUP_JOB_SUCCEEDED = 11;
	public final static int EDGE_D2D_SYNC_BACKUP_JOB_FAILED = 12;
	public final static int EDGE_D2D_SYNC_VCM_EVENT_SUCCEEDED = 13;
	public final static int EDGE_D2D_SYNC_VCM_EVENT_FAILED = 14;
	public final static int EDGE_D2D_SYNC_VM_INFO_SUCCEEDED = 15;
	public final static int EDGE_D2D_SYNC_VM_INFO_FAILED = 16;
	public final static int EDGE_D2D_SYNC_RESYNC_MANAGED_BY_OTHER = 17;
	public final static int EDGE_D2D_SYNC_RESYNC_NOT_MANAGED = 18;
	public final static int EDGE_D2D_SYNC_RESYNC_LOGIN_FAILED = 19;
	public final static int EDGE_D2D_SYNC_RESYNC_CONNECTION_FAILED = 20;
	public final static int EDGE_D2D_SYNC_RESYNC_MANAGED_BY_OTHER_DETAIL = 21;
	//public final static int EDGE_D2D_SYNC_CANNOT_CONNECT_EDGE = 22; //message contains console, should change to machine name. but this message is no use until now
	
	public final static int D2DSync_ArchiveDestType_AMAZON_S3 = 23;
	public final static int D2DSync_ArchiveDestType_WINDOWS_AZURE_BLOB = 24;
	public final static int D2DSync_ArchiveDestType_EUCALYPTUS = 25;
	
	public final static int EDGE_D2D_SYNC_ANOTHER_FULL_SYNC_THREAD_RUNNING = 26;
	public final static int EDGE_D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE = 27;
	public final static int EDGE_D2D_SYNC_RESYNC_UNKNOWN_FAILURE = 28;
	
	static {
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_SUCCEEDED, "EDGE_D2D_SYNC_RESYNC_SUCCEEDED");
		MessageMapList.put(EDGE_D2D_SYNC_SUCCEEDED, "EDGE_D2D_SYNC_SUCCEEDED");
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_FAILED, "EDGE_D2D_SYNC_RESYNC_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_FAILED, "EDGE_D2D_SYNC_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_START, "EDGE_D2D_SYNC_RESYNC_START");
		MessageMapList.put(EDGE_D2D_SYNC_START, "EDGE_D2D_SYNC_START");
		MessageMapList.put(EDGE_D2D_SYNC_ACTIVE_LOG_SUCCEEDED, "EDGE_D2D_SYNC_ACTIVE_LOG_SUCCEEDED");
		MessageMapList.put(EDGE_D2D_SYNC_ACTIVE_LOG_FAILED, "EDGE_D2D_SYNC_ACTIVE_LOG_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_ARCHIVE_JOB_SUCCEEDED, "EDGE_D2D_SYNC_ARCHIVE_JOB_SUCCEEDED");
		MessageMapList.put(EDGE_D2D_SYNC_ARCHIVE_JOB_FAILED, "EDGE_D2D_SYNC_ARCHIVE_JOB_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_BACKUP_JOB_SUCCEEDED, "EDGE_D2D_SYNC_BACKUP_JOB_SUCCEEDED");
		MessageMapList.put(EDGE_D2D_SYNC_BACKUP_JOB_FAILED, "EDGE_D2D_SYNC_BACKUP_JOB_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_VCM_EVENT_SUCCEEDED, "EDGE_D2D_SYNC_VCM_EVENT_SUCCEEDED");
		MessageMapList.put(EDGE_D2D_SYNC_VCM_EVENT_FAILED, "EDGE_D2D_SYNC_VCM_EVENT_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_VM_INFO_SUCCEEDED, "EDGE_D2D_SYNC_VM_INFO_SUCCEEDED");
		MessageMapList.put(EDGE_D2D_SYNC_VM_INFO_FAILED, "EDGE_D2D_SYNC_VM_INFO_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_MANAGED_BY_OTHER, "EDGE_D2D_SYNC_RESYNC_MANAGED_BY_OTHER");
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_NOT_MANAGED, "EDGE_D2D_SYNC_RESYNC_NOT_MANAGED");
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_LOGIN_FAILED, "EDGE_D2D_SYNC_RESYNC_LOGIN_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_CONNECTION_FAILED, "EDGE_D2D_SYNC_RESYNC_CONNECTION_FAILED");
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_MANAGED_BY_OTHER_DETAIL, "EDGE_D2D_SYNC_RESYNC_MANAGED_BY_OTHER_DETAIL");
		//MessageMapList.put(EDGE_D2D_SYNC_CANNOT_CONNECT_EDGE, "EDGE_D2D_SYNC_CANNOT_CONNECT_EDGE");
	
		MessageMapList.put(D2DSync_ArchiveDestType_AMAZON_S3, "D2DSync_ArchiveDestType_AMAZON_S3");
		MessageMapList.put(D2DSync_ArchiveDestType_WINDOWS_AZURE_BLOB, "D2DSync_ArchiveDestType_WINDOWS_AZURE_BLOB");
		MessageMapList.put(D2DSync_ArchiveDestType_EUCALYPTUS, "D2DSync_ArchiveDestType_EUCALYPTUS");
		MessageMapList.put(EDGE_D2D_SYNC_ANOTHER_FULL_SYNC_THREAD_RUNNING, "EDGE_D2D_SYNC_ANOTHER_FULL_SYNC_THREAD_RUNNING");
		MessageMapList.put(EDGE_D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE,"EDGE_D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE");
		MessageMapList.put(EDGE_D2D_SYNC_RESYNC_UNKNOWN_FAILURE,"EDGE_D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE");
	}
	
	public static String GetMessage(int messageId) {
		String messageKey = MessageMapList.get(messageId);
		if(messageKey == null)
			return null;
		
		String message = EdgeCMWebServiceMessages.getResource(messageKey);
		
		return message;
	}
}
