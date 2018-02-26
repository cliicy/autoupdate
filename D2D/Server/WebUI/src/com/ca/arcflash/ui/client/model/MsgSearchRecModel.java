package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MsgSearchRecModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2899934744172975739L;
	private GRTCatalogItemModel msgRec;	
	private long  sessionNumber;
	private long  subSessionNumber;
	private String mailboxOrSameLevelName;
	private String edbFullPath;//edb real restore path
	private long edbType;//Root Public folder (254)or edb(255)
	private String edbDisplayName;// edb display full path name
	private String mailFullDisplayPath;

	public long getSessionNumber() {
		return sessionNumber;
	}
	public void setSessionNumber(long sessionNumber) {
		this.sessionNumber = sessionNumber;
	}
	public long getSubSessionNumber() {
		return subSessionNumber;
	}
	public void setSubSessionNumber(long subSessionNumber) {
		this.subSessionNumber = subSessionNumber;
	}
	public String getMailboxOrSameLevelName() {
		return mailboxOrSameLevelName;
	}
	public void setMailboxOrSameLevelName(String mailboxOrSameLevelName) {
		this.mailboxOrSameLevelName = mailboxOrSameLevelName;
	}
	public String getEdbFullPath() {
		return edbFullPath;
	}
	public void setEdbFullPath(String edbFullPath) {
		this.edbFullPath = edbFullPath;
	}
	public long getEdbType() {
		return edbType;
	}
	public void setEdbType(long edbType) {
		this.edbType = edbType;
	}
	public String getEdbDisplayName() {
		return edbDisplayName;
	}
	public void setEdbDisplayName(String edbDisplayName) {
		this.edbDisplayName = edbDisplayName;
	}
	public String getMailFullDisplayPath() {
		return mailFullDisplayPath;
	}
	public void setMailFullDisplayPath(String mailFullDisplayPath) {
		this.mailFullDisplayPath = mailFullDisplayPath;
	}
	public void setMsgRec(GRTCatalogItemModel msgRec) {
		this.msgRec = msgRec;
	}
	public GRTCatalogItemModel getMsgRec() {
		return msgRec;
	}
}
