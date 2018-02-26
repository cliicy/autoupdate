package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;

public class SyncActivityLogMsg {

	public static String getSyncasbuservertypechanged() {
		return EdgeCMWebServiceMessages
				.getResource("EDGE_ARC_SYNC_ARC_SERVER_TYPE_IS_CHANGED");
	}

	public static String getSyncasbuservertypechanged(String nodename,
			ABFuncServerType orType, ABFuncServerType curType) {
		return EdgeCMWebServiceMessages.getResource(
				"EDGE_ARC_SYNC_ARC_SERVER_TYPE_IS_CHANGED", nodename,
				getASBUServerTypeMsg(orType), getASBUServerTypeMsg(curType));
	}

	public static String getSyncasbuabfuncserviceisinvalidate() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_ARC_SERVICE_INVALID");
	}

	public static String getSyncasbuservernotmanagedbycurrentserver() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_ARC_IS_NOT_MANAGED");
	}

	public static String getSyncbranchcannotdofullsyncmsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_BRANCH_CANNT_FULL_SYNC_MSG");
	}

	public static String getSyncfullbcperrormsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FULL_BCP_ERROR_MSG");
	}

	public static String getSyncfailed() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FAILED");
	}

	public static String getDumpdatabasefailedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_DUMP_DB_FAILED");
	}

	public static String getSyncfullsuccessfulMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FULL_SUCCESS");
	}

	public static String getSyncfullstartedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FULL_START");
	}

	public static String getSyncfullendMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FULL_END");
	}

	public static String getSyncfileerrorMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FILE_ERROR");
	}

	public static String getSyncimporterrorMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_IMPORT_ERROR");
	}

	public static String getSyncfulldumpdatabaseretrymsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FULL_DUMP_DB_RETRY_MSG");
	}

	public static String getSyncfullgetfilelistretrymsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FULL_GET_FILE_LIST_RETRY_MSG");
	}

	public static String getSyncfulltransferfileregrymsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_FULL_TRAN_FILE_RETRY_MSG");
	}

	public static String getServiceerrorMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SERVICE_ERROR");
	}

	public static String getSyncincsyncstartMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_INC_SYNC_START");
	}

	public static String getSyncincsyncwarningMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_INC_SYNC_WARNING");
	}

	public static String getSyncincsyncendMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_INC_SYNC_END");
	}

	public static String getSyncincsyncfullnotfinishedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_INC_SYNC_FULL_NOT_FINISHED");
	}

	public static String getSyncincsyncdbischangedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_INC_SYNC_DB_IS_CHANGED");
	}

	public static String getSyncgdbfullstartedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_FULL_STARTED");
	}

	public static String getSyncgdbfullfailedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_FULL_FAILED");
	}

	public static String getSyncgdbfullbranchfailedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_FULL_BRANCH_FAILED");
	}

	public static String getSyncgdbfullsucceedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_FULL_SUCCESS");
	}

	public static String getSyncgdbfullwebserviceerrorMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_FULL_WEB_SERVICE_ERROR");
	}

	public static String getSyncgdbfullgetfilelisterrorMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_FULL_GET_FILE_LIST_ERROR");
	}

	public static String getSyncimportfileintodatabaseMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_IMPORT_FILE_INTO_DB");
	}

	public static String getSyncgdbincstartMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_INC_START");
	}

	public static String getSyncgdbincsucceedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_INC_SUCCESS");
	}

	public static String getSyncgdbincfailedMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_INC_FAILED");
	}

	public static String getSyncgdbincdatatransferstartMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_INC_DATA_TRAN_START");
	}

	public static String getSyncgdbincdatatransferendMsg() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_GDB_INC_DTAT_TRAN_END");
	}
	
	public static String getSyncTransferFileErrorMsg() {
		return EdgeCMWebServiceMessages
				.getResource("EDGE_ARC_SYNC_TRANSFER_FILE_ERROR");
	}

	public static String getASBUServerTypeMsg(ABFuncServerType abType) {
		switch (abType) {
		case ARCSERVE_MEMBER:
			return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_ARC_MEM_TYPE_MSG");
		case STANDALONE_SERVER:
			return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_ARC_STAND_ALONE_TYPE_MSG");
		case NORNAML_SERVER:
			return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_ARC_PRIMARY_TYPE_MSG");
		case BRANCH_PRIMARY:
			return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_ARC_GDB_BRANCH_TYPE_MSG");
		case GDB_PRIMARY_SERVER:
			return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_ARC_GDB_TYPE_MSG");
		default:
			return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_ARC_UNKNOW_TYPE_MSG");
		}
	}

	public static String getSyncservertypeischange() {
		return EdgeCMWebServiceMessages.getResource("EDGE_ARC_SYNC_SYNC_SERVER_TYPE_CHANGE");
	}
}
