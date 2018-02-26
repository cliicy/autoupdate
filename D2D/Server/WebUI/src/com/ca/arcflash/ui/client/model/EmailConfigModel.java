package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

public class EmailConfigModel implements IEmailConfigModel{

	private String content;
	private Boolean enableHTMLFormat;
	private String fromAddress;
	private String mailPwd;
	private Integer mailService;
	private String mailUser;
	private String proxyAddress;
	private String proxyPassword;
	private int    proxyPort;
	private String proxyUsername;
	private ArrayList<String> recipients;
	private String smtp;
	private int	   smtpPort;
	private String subject;
	private Boolean enableMailAuth;
	private Boolean enableProxy;
	private Boolean enableProxyAuth;
	private Boolean enableSSL;
	private Boolean enableTLS;
	
	
	@Override
	public String getContent() {
		return content;
	}

	@Override
	public Boolean getEnableHTMLFormat() {
		return enableHTMLFormat;
	}

	@Override
	public String getFromAddress() {
		return fromAddress;
	}

	@Override
	public String getMailPwd() {
		return mailPwd;
	}

	@Override
	public Integer getMailService() {
		return mailService;
	}

	@Override
	public String getMailUser() {
		return mailUser;
	}

	@Override
	public String getProxyAddress() {
		return proxyAddress;
	}

	@Override
	public String getProxyPassword() {
		return proxyPassword;
	}

	@Override
	public Integer getProxyPort() {
		return proxyPort;
	}

	@Override
	public String getProxyUsername() {
		return proxyUsername;
	}

	@Override
	public ArrayList<String> getRecipients() {
		return recipients;
	}

	@Override
	public String getSMTP() {
		return smtp;
	}

	@Override
	public Integer getSmtpPort() {
		return smtpPort;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public Boolean isEnableMailAuth() {
		return enableMailAuth;
	}

	@Override
	public Boolean isEnableProxy() {
		return enableProxy;
	}

	@Override
	public Boolean isEnableProxyAuth() {
		return enableProxyAuth;
	}

	@Override
	public Boolean isEnableSsl() {
		return enableSSL;
	}

	@Override
	public Boolean isEnableTls() {
		return enableTLS;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
		
	}

	@Override
	public void setEnableHTMLFormat(Boolean b) {
		this.enableHTMLFormat = b;
	}

	@Override
	public void setEnableMailAuth(Boolean b) {
		this.enableMailAuth = b;
	}

	@Override
	public void setEnableProxy(Boolean b) {
		this.enableProxy = b;
	}

	@Override
	public void setEnableProxyAuth(Boolean b) {
		this.enableProxyAuth = b;
	}

	@Override
	public void setEnableSsl(Boolean b) {
		this.enableSSL = b;
	}

	@Override
	public void setEnableTls(Boolean b) {
		this.enableTLS = b;
	}

	@Override
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	@Override
	public void setMailPwd(String mailPwd) {
		this.mailPwd = mailPwd;
	}

	@Override
	public void setMailService(Integer mailService) {
		this.mailService = mailService;
	}

	@Override
	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	@Override
	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	@Override
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	@Override
	public void setProxyPort(Integer port) {
		this.proxyPort = port;
	}

	@Override
	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	@Override
	public void setRecipients(ArrayList<String> recipientsList) {
		if(recipientsList == null){
			return;
		}
		
		if(this.recipients == null) {
			this.recipients = new ArrayList<String>();
		}
		this.recipients.clear();
		this.recipients.addAll(recipientsList);
		
	}

	@Override
	public void setSMTP(String smtp) {
		this.smtp = smtp;
		
	}

	@Override
	public void setSmtpPort(Integer port) {
		this.smtpPort = port;
		
	}

	@Override
	public void setSubject(String subject) {
		this.subject = subject;
	}

}
