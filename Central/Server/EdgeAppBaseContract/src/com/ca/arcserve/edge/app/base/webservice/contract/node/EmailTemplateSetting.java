package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;


public class EmailTemplateSetting implements Serializable{

	private static final long serialVersionUID = 1811104077978667064L;
	
	private int feature_Id = 0;
	private String subject = "";
	private String from_addrs = "";
	private String recipients = "";
	private short html_flag = 0;
	private String content = "";
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFrom_addrs() {
		return from_addrs;
	}
	public void setFrom_addrs(String from) {
		this.from_addrs = from;
	}
	public String getRecipients() {
		return recipients;
	}
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	public short getHtml_flag() {
		return html_flag;
	}
	public void setHtml_flag(short htmlFlag) {
		html_flag = htmlFlag;
	}
	public void setFeature_Id(int featureId) {
		this.feature_Id = featureId;
	}
	public int getFeature_Id() {
		return feature_Id;
	}		
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public boolean equals(Object obj) {
		if( null == obj){
			return false;
		}
		if( this == obj ){
			return true;
		}
		if( getClass() != obj.getClass() ){
			return false;
		}
		EmailTemplateSetting other = (EmailTemplateSetting)obj;
		if( feature_Id == other.feature_Id && 
				StringUtil.isEqual(subject, other.subject) &&
				StringUtil.isEqual(from_addrs, other.from_addrs) &&
				StringUtil.isEqual(recipients, other.recipients) &&
				html_flag == other.html_flag ){
			return true;
		}
		else{
			return false;
		}
	}
	
	public EmailTemplateSetting clone() {
		EmailTemplateSetting newSetting = new EmailTemplateSetting();
		newSetting.content = this.content;
		newSetting.feature_Id = this.feature_Id;
		newSetting.from_addrs = this.from_addrs;
		newSetting.html_flag = this.html_flag;
		newSetting.recipients = this.recipients;
		newSetting.subject = this.subject;
		return newSetting;
	}
}
