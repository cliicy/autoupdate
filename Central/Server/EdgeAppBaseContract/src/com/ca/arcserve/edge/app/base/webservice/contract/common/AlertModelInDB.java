/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.common;


import java.io.Serializable;
import java.util.Date;

public class AlertModelInDB implements Serializable {


	private static final long serialVersionUID = -3695293040171146436L;
	private int id;
	private String sendHost;
	private String nodeName;
	private long jobType;
	private int rawEventType;
	private int jobStatus; 
	private int overAllEventType;
	
	private String alertMessage;
	private Date sendTime;
	private int productType;
	private int isAcknowledge;
	private Date updateTime;
	private String alertSubject;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getRawEventType() {
		return rawEventType;
	}
	public void setRawEventType(int rawEventType) {
		this.rawEventType = rawEventType;
	}
	
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public int getProductType() {
		return productType;
	}
	public void setProductType(int productType) {
		this.productType = productType;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public String getAlertSubject() {
		return alertSubject;
	}
	public void setAlertSubject(String alertSubject) {
		this.alertSubject = alertSubject;
	}
	
	public String getAlertMessage() {
		return alertMessage;
	}
	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}
	
	public int getIsAcknowledge() {
		return isAcknowledge;
	}
	public void setIsAcknowledge(int isAcknowledge) {
		this.isAcknowledge = isAcknowledge;
	}	
	public String getSendHost() {
		return sendHost;
	}
	public void setSendHost(String sendHost) {
		this.sendHost = sendHost;
	}
	public long getJobType() {
		return jobType;
	}
	public void setJobType(long jobType) {
		this.jobType = jobType;
	}
	public int getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(int jobStatus) {
		this.jobStatus = jobStatus;
	}
	public int getOverAllEventType() {
		return overAllEventType;
	}
	public void setOverAllEventType(int overAllEventType) {
		this.overAllEventType = overAllEventType;
	}
}
