package com.ca.arcflash.webservice.jni.model;

public class JJobScriptExchSubItem {
	private long	ulItemType;		// 1 for MailboxDB, 2 for Folder, 3 for Message
	private String 	pwszItemName;
	private String  pwszMailboxName;
	private String  pwszExchangeObjectIDs;
//	private long	ul_lMailboxID;
//	private long 	ul_lFolderID;
//	private long	ul_lMsgID;
//	private long	ul_hMailboxID;
//	private long 	ul_hFolderID;
//	private long	ul_hMsgID;	
	private String	pwszDescription;
	
	public long getUlItemType() {
		return ulItemType;
	}
	public void setUlItemType(long ulItemType) {
		this.ulItemType = ulItemType;
	}
	public String getPwszItemName() {
		return pwszItemName;
	}
	public void setPwszItemName(String pwszItemName) {
		this.pwszItemName = pwszItemName;
	}

	public String getPwszMailboxName() {
		return pwszMailboxName;
	}
	public void setPwszMailboxName(String pwszMailboxName) {
		this.pwszMailboxName = pwszMailboxName;
	}
	
	public String getPwszDescription() {
		return pwszDescription;
	}
	public void setPwszDescription(String pwszDescription) {
		this.pwszDescription = pwszDescription;
	}
	public String getPwszExchangeObjectIDs() {
		return pwszExchangeObjectIDs;
	}
	public void setPwszExchangeObjectIDs(String pwszExchangeObjectIDs) {
		this.pwszExchangeObjectIDs = pwszExchangeObjectIDs;
	}		
}
