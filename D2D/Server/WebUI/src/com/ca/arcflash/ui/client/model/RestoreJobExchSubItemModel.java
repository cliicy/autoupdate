package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreJobExchSubItemModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5876362959064228077L;

	public RestoreJobExchSubItemModel()
	{
		setItemType(0L);
		setItemName("");
		setMailboxName("");
//		setLMailBoxID(0L);
//		setHMailBoxID(0L);
//		setLFolderID(0L);
//		setHFolderID(0L);
//		setLMsgID(0L);
//		setHMsgID(0L);
		setDescription("");
		setExchangeObjectID("");
	}
	
	
	
	public Long getItemType() {
		return (Long) get("ItemType");
	}

	// 1 for MailboxDB, 2 for Folder, 3 for Message
	public void setItemType(Long type) 
	{
		set("ItemType", type);
	}

	public String getItemName() {
		return (String) get("ItemName");
	}

	public void setItemName(String name) {
		set("ItemName", name);
	}
	
	public String getMailboxName() {
		return (String) get("MailboxName");
	}

	public void setMailboxName(String name) {
		set("MailboxName", name);
	}

//	public Long getLMailBoxID() {
//		return (Long) get("LMailBoxID");
//	}
//
//	public void setLMailBoxID(Long var) {
//		set("LMailBoxID", var);
//	}
//
//	public Long getHMailBoxID() {
//		return (Long) get("HMailBoxID");
//	}
//
//	public void setHMailBoxID(Long var) {
//		set("HMailBoxID", var);
//	}
//
//	public Long getLFolderID() {
//		return (Long) get("LFolderID");
//	}
//
//	public void setLFolderID(Long var) {
//		set("LFolderID", var);
//	}
//
//	public Long getHFolderID() {
//		return (Long) get("HFolderID");
//	}
//
//	public void setHFolderID(Long var) {
//		set("HFolderID", var);
//	}
//
//	public Long getLMsgID() {
//		return (Long) get("LMsgID");
//	}
//
//	public void setLMsgID(Long var) {
//		set("LMsgID", var);
//	}
//
//	public Long getHMsgID() {
//		return (Long) get("HMsgID");
//	}
//
//	public void setHMsgID(Long var) {
//		set("HMsgID", var);
//	}

	public String getDescription() {
		return (String) get("Description");
	}

	public void setDescription(String desc) {
		set("Description", desc);
	}

	public String getExchangeObjectID() {	
		return get("ExchangeObjectID");
	}	
	public void setExchangeObjectID(String exchangeObjectID) {
		set("ExchangeObjectID", exchangeObjectID);
	}
}
