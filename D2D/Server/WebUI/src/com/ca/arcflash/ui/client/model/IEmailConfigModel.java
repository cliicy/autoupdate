package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

public interface IEmailConfigModel {

	/**alert email */
	public abstract String getMailUser();

	public abstract void setMailUser(String mailUser);

	public abstract void setEnableTls(Boolean b);

	public abstract Boolean isEnableTls();

	public abstract void setEnableMailAuth(Boolean b);

	public abstract Boolean isEnableMailAuth();

	public abstract void setEnableProxyAuth(Boolean b);

	public abstract Boolean isEnableProxyAuth();

	public abstract void setEnableSsl(Boolean b);

	public abstract Boolean isEnableSsl();

	public abstract void setSmtpPort(Integer port);

	public abstract Integer getSmtpPort();

	public abstract String getMailPwd();

	public abstract void setMailPwd(String mailPwd);

	public abstract Integer getMailService();

	public abstract void setMailService(Integer mailService);

	public abstract Boolean getEnableHTMLFormat();

	public abstract void setEnableHTMLFormat(Boolean b);

	public abstract Boolean isEnableProxy();

	public abstract void setEnableProxy(Boolean b);

	public abstract String getProxyAddress();

	public abstract void setProxyAddress(String proxyAddress);

	public abstract Integer getProxyPort();

	public abstract void setProxyPort(Integer port);

	public abstract String getProxyUsername();

	public abstract void setProxyUsername(String proxyUsername);

	public abstract String getProxyPassword();

	public abstract void setProxyPassword(String proxyPassword);

	public abstract String getSubject();

	public abstract void setSubject(String subject);

	public abstract String getContent();

	public abstract void setContent(String content);

	public abstract String getFromAddress();

	public abstract void setFromAddress(String fromAddress);

	public abstract String getSMTP();

	public abstract void setSMTP(String smtp);
	
	public abstract ArrayList<String> getRecipients();
	
	public abstract void setRecipients(ArrayList<String> recipients);
	
	/*public abstract Boolean getEnableEmail();
	
	public abstract Boolean getEnableEmailOnSuccess();
	
	public abstract Boolean getEnableSpaceNotification();*/

}