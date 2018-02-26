package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MailServiceInfo extends BaseModelData {
	
	private static final long serialVersionUID = 8832291240524409486L;
	public static final String NAMEFIELD = "Name";
	public static final String MAILSERVER = "MailServer";
	public static final String SSL = "ssl";
	public static final String TLS = "tls";
	public static final String SMTPPort = "smtpport";
	public static final String MAILSERVERVALUE = "mailServerValue";
	/**
	 * Google, Live, Yahoo, other Yahoo Outgoing Mail Server (SMTP) - smtp.mail.yahoo.com (port 25)
	 * Hotmail Outgoing Mail Server (SMTP) - smtp.live.com (SSL disabled, Tls enable, port 25)
	 *  smtp.gmail.com (SSL enabled, port 465)
		http://www.emailaddressmanager.com/tips/mail-settings.html
	 * @return
	 */
	public String getName() {
		return get(NAMEFIELD);
	}
	public void setName(String name) {
		set(NAMEFIELD, name);
	}
	
	public String getMailServer()
	{
		return get(MAILSERVER);
	}
	public void setMailServer(String mailServer)
	{
		set(MAILSERVER, mailServer);
	}
	public Boolean isUseSSL()
	{
		return get(SSL);
	}
	public void setUseSSL(Boolean useSSL)
	{
		set(SSL, useSSL);
	}
	public Boolean isUseTLS()
	{
		return get(TLS);
	}
	public void setUseTLS(Boolean useTLS)
	{
		set(TLS, useTLS);
	}
	public Integer getSmtpPort(){
		return get(SMTPPort);
	}
	public void setSmtpPort(Integer port){
		 set(SMTPPort,port);
	}
	public Integer getMailServerValue(){
		return get(MAILSERVERVALUE);
	}
	
	public void setMailServerValue(Integer mailServerValue){
		set(MAILSERVERVALUE,mailServerValue);
	}

}
