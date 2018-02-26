package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class EmailServerSetting implements Serializable {

	private static final long serialVersionUID = -2471610965037004136L;

	public enum EmailService {
		GoogleMail,
		YahooMail,
		LiveMail,
		Other
	}
	
	private EmailTemplateSetting templateSetting = new EmailTemplateSetting();
	private EmailService mail_server = EmailService.Other;
	private String smtp = "";
	private int port = 25;
	private short auth_flag = 0;
	private String user_name = "";
	private @NotPrintAttribute String user_password = "";
	private short ssl_flag = 0;
	private short tls_flag = 0;
	private short proxy_flag = 0;
	private String proxy_server = "";
	private int proxy_port = 1080;
	private short proxy_auth_flag = 0;
	private String proxy_user_name = "";
	private @NotPrintAttribute String proxy_user_password = "";
	private short auto_discovery_flag = 0;
	
	//added by tony, used to mark whether the enable email alerts checkbox
	private boolean isEnableEmailAlerts;
		
	public EmailService getMail_server() {
		return mail_server;
	}

	public void setMail_server(EmailService s) {
		mail_server = s;
	}
	
	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int p) {
		port = p;
	}

	public short getAuth_flag() {
		return auth_flag;
	}

	public void setAuth_flag(short f) {
		auth_flag = f;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String s) {
		user_name = s;
	}

	public String getUser_password() {
		return user_password;
	}

	public void setUser_password(String s) {
		user_password = s;
	}
	
	public short getSsl_flag() {
		return ssl_flag;
	}

	public void setSsl_flag(short f) {
		ssl_flag = f;
	}

	public short getTls_flag() {
		return tls_flag;
	}

	public void setTls_flag(short tlsFlag) {
		tls_flag = tlsFlag;
	}
	
	public short getProxy_flag() {
		return proxy_flag;
	}

	public void setProxy_flag(short f) {
		proxy_flag = f;
	}

	public String getProxy_server() {
		return proxy_server;
	}

	public void setProxy_server(String s) {
		proxy_server = s;
	}
	
	public short getAuto_discovery_flag() {
		return auto_discovery_flag;
	}

	public void setAuto_discovery_flag(short f) {
		auto_discovery_flag = f;
	}

	public int getProxy_port() {
		return proxy_port;
	}

	public void setProxy_port(int p) {
		proxy_port = p;
	}

	public short getProxy_auth_flag() {
		return proxy_auth_flag;
	}

	public void setProxy_auth_flag(short f) {
		proxy_auth_flag = f;
	}

	public String getProxy_user_name() {
		return proxy_user_name;
	}

	public void setProxy_user_name(String s) {
		proxy_user_name = s;
	}

	public String getProxy_user_password() {
		return proxy_user_password;
	}

	public void setProxy_user_password(String s) {
		proxy_user_password = s;
	}
	
	public EmailTemplateSetting getTemplateSetting() {
		return templateSetting;
	}

	public void setTemplateSetting(EmailTemplateSetting templateSetting) {
		this.templateSetting = templateSetting;
	}
	
	public boolean isEnableEmailAlerts() {
		return isEnableEmailAlerts;
	}

	public void setEnableEmailAlerts(boolean isEnableEmailAlerts) {
		this.isEnableEmailAlerts = isEnableEmailAlerts;
	}

	@Override
	public boolean equals(Object obj) {
		if( null == obj) {
			return false;
		}
		if( this == obj ) {
			return true;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		EmailServerSetting other = (EmailServerSetting)obj;
		if( this.auth_flag == other.getAuth_flag() &&
				this.mail_server == other.getMail_server() &&
				isMailSettingEqual(other) &&
				this.port == other.getPort() &&
				this.proxy_auth_flag == other.getProxy_auth_flag() &&
				this.proxy_flag == other.getProxy_flag() &&
				this.proxy_port == other.getProxy_port() &&
				StringUtil.isEqual(this.proxy_server, other.getProxy_server()) &&
				StringUtil.isEqual(this.proxy_user_name, other.getProxy_user_name()) && 
				StringUtil.isEqual(this.proxy_user_password, other.getProxy_user_password()) &&
				StringUtil.isEqual(this.smtp, other.getSmtp()) &&
				this.ssl_flag == getSsl_flag() &&
				this.tls_flag == getTls_flag() &&
				StringUtil.isEqual(this.user_name, other.getUser_name()) &&
				StringUtil.isEqual(this.user_password, other.getUser_password()) &&
				this.auto_discovery_flag == other.getAuto_discovery_flag()){
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isMailSettingEqual(EmailServerSetting other) {
		if (other.getTemplateSetting() == null && this.getTemplateSetting() == null) {
			return true;
		}
		if (other.getTemplateSetting() != null && this.getTemplateSetting() != null) {
			return this.getTemplateSetting().equals(other.getTemplateSetting());
		}
		return false;
	}

	public EmailServerSetting clone() {
		EmailServerSetting newSetting = new EmailServerSetting();
		newSetting.auth_flag = this.auth_flag;
		newSetting.auto_discovery_flag = this.auth_flag;
		newSetting.port = this.port;
		newSetting.proxy_auth_flag = this.proxy_auth_flag;
		newSetting.proxy_flag = this.proxy_flag;
		newSetting.proxy_port = this.proxy_port;
		newSetting.proxy_server = this.proxy_server;
		newSetting.proxy_user_name = this.proxy_user_name;
		newSetting.proxy_user_password = this.proxy_user_password;
		newSetting.smtp = this.smtp;
		newSetting.ssl_flag = this.ssl_flag;
		newSetting.mail_server = this.mail_server;
		newSetting.tls_flag = this.tls_flag;
		newSetting.user_name = this.user_name;
		newSetting.user_password = this.user_password;
		newSetting.templateSetting = this.templateSetting.clone();
		return newSetting;
		
	}
}
