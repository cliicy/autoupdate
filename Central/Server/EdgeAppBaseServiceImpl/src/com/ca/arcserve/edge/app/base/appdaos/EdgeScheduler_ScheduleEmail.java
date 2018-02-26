package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeScheduler_ScheduleEmail {
	private int ID;
	private int ScheduleID;
	private String SenderName;
	private String FromAddress;
	private String ToAddresses;
	private String CCAddresses;
	private int Priority;
	private String MailSubject;
	private String MailComment;
	private int Attachment;
	private int branchid;
	private int templateId;

	public int getID() {
		return ID;
	}
	
	public void setID(int iD) {
		ID = iD;
	}
	
	public int getScheduleID() {
		return ScheduleID;
	}

	public void setScheduleID(int scheduleID) {
		ScheduleID = scheduleID;
	}

	public String getSenderName() {
		return SenderName;
	}

	public void setSenderName(String senderName) {
		SenderName = senderName;
	}

	public String getFromAddress() {
		return FromAddress;
	}

	public void setFromAddress(String fromAddress) {
		FromAddress = fromAddress;
	}

	public String getToAddresses() {
		return ToAddresses;
	}

	public void setToAddresses(String toAddresses) {
		ToAddresses = toAddresses;
	}

	public String getCCAddresses() {
		return CCAddresses;
	}

	public void setCCAddresses(String cCAddresses) {
		CCAddresses = cCAddresses;
	}

	public int getPriority() {
		return Priority;
	}

	public void setPriority(int priority) {
		Priority = priority;
	}

	public String getMailSubject() {
		return MailSubject;
	}

	public void setMailSubject(String mailSubject) {
		MailSubject = mailSubject;
	}

	public String getMailComment() {
		return MailComment;
	}

	public void setMailComment(String mailComment) {
		MailComment = mailComment;
	}

	public int getBranchid() {
		return branchid;
	}

	public void setBranchid(int branchid) {
		this.branchid = branchid;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getTemplateId() {
		return templateId;
	}

	public int getAttachment() {
		return Attachment;
	}

	public void setAttachment(int attachment) {
		Attachment = attachment;
	}
	
}
