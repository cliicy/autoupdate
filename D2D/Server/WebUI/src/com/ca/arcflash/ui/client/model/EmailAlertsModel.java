package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class EmailAlertsModel extends BaseModelData implements IEmailConfigModel{
	private static final long serialVersionUID = -1190005990510526621L;

	
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getMailUser()
	 */
	public String getMailUser() {
		return get("mailUser");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setMailUser(java.lang.String)
	 */
	public void setMailUser(String mailUser) {
		set("mailUser", mailUser);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setEnableTls(java.lang.Boolean)
	 */
	public void setEnableTls(Boolean b){
		set("enableTls", b);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#isEnableTls()
	 */
	public Boolean isEnableTls() {
		return (Boolean) get("enableTls");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setEnableMailAuth(java.lang.Boolean)
	 */
	public void setEnableMailAuth(Boolean b){
		set("enableMailAuth", b);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#isEnableMailAuth()
	 */
	public Boolean isEnableMailAuth() {
		return (Boolean) get("enableMailAuth");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setEnableProxyAuth(java.lang.Boolean)
	 */
	public void setEnableProxyAuth(Boolean b){
		set("enableProxyAuth", b);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#isEnableProxyAuth()
	 */
	public Boolean isEnableProxyAuth() {
		return (Boolean) get("enableProxyAuth");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setEnableSsl(java.lang.Boolean)
	 */
	public void setEnableSsl(Boolean b)
	{
		set("enableSsl", b);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#isEnableSsl()
	 */
	public Boolean isEnableSsl() {
		return (Boolean) get("enableSsl");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setSmtpPort(java.lang.Integer)
	 */
	public void setSmtpPort(Integer port)
	{
		set("smtpPort", port);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getSmtpPort()
	 */
	public Integer getSmtpPort() {
		return (Integer) get("smtpPort");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getMailPwd()
	 */
	public String getMailPwd() {
		return get("MailPassword");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setMailPwd(java.lang.String)
	 */
	public void setMailPwd(String mailPwd) {
		set("MailPassword",mailPwd);
	}
	
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getMailService()
	 */
	public Integer getMailService(){
		return get("MailServiceName");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setMailService(java.lang.String)
	 */
	public void setMailService(Integer mailService){
		set("MailServiceName",mailService);
	}
	
	//Email Notifications
	public Boolean getEnableEmailOnMissedJob() {
		return (Boolean) get("enableEmailOnMissedJob");
	}
	public void setEnableEmailOnMissedJob(Boolean b) {
		set("enableEmailOnMissedJob", b);		
	}
	public Boolean getEnableEmail() {
		return (Boolean) get("enableEmail");
	}
	public void setEnableEmail(Boolean b) {
		set("enableEmail", b);		
	}
	public Boolean getEnableEmailOnSuccess() {
		return (Boolean) get("enableEmailOnSuccess");
	}
	public void setEnableEmailOnSuccess(Boolean b) {
		set("enableEmailOnSuccess", b);		
	}
	
	public Boolean getEnableEmailOnMergeFailure() {
		return (Boolean) get("enableEmailOnMergeFailure");
	}
	public void setEnableEmailOnMergeFailure(Boolean b) {
		set("enableEmailOnMergeFailure", b);		
	}
	
	public Boolean getEnableEmailOnMergeSuccess() {
		return (Boolean) get("enableEmailOnMergeSuccess");
	}
	public void setEnableEmailOnMergeSuccess(Boolean b) {
		set("enableEmailOnMergeSuccess", b);		
	}
	public Boolean getEnableEmailOnRecoveryPointCheckFailure() {
		return(Boolean) get("enableEmailOnCheckRPSFailure");
	}
	
	public void setEnableEmailOnRecoveryPointCheckFailure(Boolean b){
		set("enableEmailOnCheckRPSFailure", b);
	}
	public Boolean getEnableSpaceNotification(){
		return get("enableSpaceNotification");
	}
	public void setEnableSpaceNotification(boolean enableSpaceNotification){
		set("enableSpaceNotification", enableSpaceNotification);
	}
	
	public Boolean getEnableEmailOnNewUpdates() {
		return (Boolean) get("enableEmailOnNewUpdates");
	}
	public void setEnableEmailOnNewUpdates(Boolean b) {
		set("enableEmailOnNewUpdates", b);		
	}
	
	public Double getSpaceMeasureNum(){
		return get("spaceMeasureNum");
	}
	public void setSpaceMeasureNum(double spaceMeasureNum){
		set("spaceMeasureNum", spaceMeasureNum);
	}
	public String getSpaceMeasureUnit(){
		return get("spaceMeasureUnit");
	}
	public void setSpaceMeasureUnit(String spaceMeasureUnit){
		set("spaceMeasureUnit", spaceMeasureUnit);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getEnableHTMLFormat()
	 */
	public Boolean getEnableHTMLFormat(){
		return (Boolean) get("enableHTMLFormat");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setEnableHTMLFormat(java.lang.Boolean)
	 */
	public void setEnableHTMLFormat(Boolean b)
	{
		set("enableHTMLFormat", b);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#isEnableProxy()
	 */
	public Boolean isEnableProxy() {
		return (Boolean) get("enableProxy");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setEnableProxy(java.lang.Boolean)
	 */
	public void setEnableProxy(Boolean b) {
		set("enableProxy", b);
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getProxyAddress()
	 */
	public String getProxyAddress() {
		return get("proxyAddress");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setProxyAddress(java.lang.String)
	 */
	public void setProxyAddress(String proxyAddress) {
		set("proxyAddress", proxyAddress);		
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getProxyPort()
	 */
	public Integer getProxyPort() {
		return (Integer) get("proxyPort");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setProxyPort(java.lang.Integer)
	 */
	public void setProxyPort(Integer port) {
		set("proxyPort", port);			
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getProxyUsername()
	 */
	public String getProxyUsername() {
		return get("proxyUsername");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setProxyUsername(java.lang.String)
	 */
	public void setProxyUsername(String proxyUsername) {
		set("proxyUsername", proxyUsername);		
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getProxyPassword()
	 */
	public String getProxyPassword() {
		return get("proxyPassword");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setProxyPassword(java.lang.String)
	 */
	public void setProxyPassword(String proxyPassword) {
		set("proxyPassword", proxyPassword);		
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getSubject()
	 */
	public String getSubject() {
		return get("subject");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setSubject(java.lang.String)
	 */
	public void setSubject(String subject) {
		set("subject", subject);		
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getContent()
	 */
	public String getContent() {
		return get("content");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setContent(java.lang.String)
	 */
	public void setContent(String content) {
		set("content", content);		
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getFromAddress()
	 */
	public String getFromAddress() {
		return get("fromAddress");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setFromAddress(java.lang.String)
	 */
	public void setFromAddress(String fromAddress) {
		set("fromAddress", fromAddress);		
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#getSMTP()
	 */
	public String getSMTP() {
		return get("SMTP");
	}
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.model.IEmailConfigModel#setSMTP(java.lang.String)
	 */
	public void setSMTP(String smtp) {
		set("SMTP", smtp);
	}	

	// for SRM alert
	public Boolean getEnableSrmPkiAlert() {
		return (Boolean) get("enableSrmPkiAlert");
	}
	public void setEnableSrmPkiAlert(Boolean b) {
		set("enableSrmPkiAlert", b);		
	}
	public void setCpuAlertUtilThreshold(int threshold) {
		set("cpuAlertUtilThreshold", threshold);
	}
	public Integer getCpuAlertUtilThreshold() {
		return get("cpuAlertUtilThreshold");
	}
	public void setMemoryAlertUtilThreshold(int threshold) {
		set("memoryAlertUtilThreshold", threshold);
	}
	public Integer getMemoryAlertUtilThreshold() {
		return get("memoryAlertUtilThreshold");
	}
	public void setDiskAlertUtilThreshold(int threshold) {
		set("diskAlertUtilThreshold", threshold);
	}
	public Integer getDiskAlertUtilThreshold() {
		return get("diskAlertUtilThreshold");
	}
	public void setNetworkAlertUtilThreshold(int threshold) {
		set("networkAlertUtilThreshold", threshold);
	}
	public Integer getNetworkAlertUtilThreshold() {
		return get("networkAlertUtilThreshold");
	}
	
	public Boolean getEnableSettings() {
		return (Boolean)get("enableSettings");
	}
	
	public void setEnableSettings(boolean enableSettings) {
		set("enableSettings", enableSettings);
	}
	
	public ArrayList<String> Recipients;

	@Override
	public ArrayList<String> getRecipients() {
		return Recipients;
	}
	@Override
	public void setRecipients(ArrayList<String> recipients ) {
		this.Recipients = recipients;
	}
	
}
