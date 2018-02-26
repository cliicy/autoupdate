package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

public class ArchiveToTapeAdvance implements Serializable{
	private static final long serialVersionUID = -1463396303187381111L;
	private EncryptionCompression encryptionCompression = new EncryptionCompression();
	private boolean disableFileEstimate;
	private EjectMediaOption ejectMediaOption;
	private CommandConfig commandConfig = new CommandConfig();
	private JobLogOption jobLogOption;
	private EmailAlertConfig emailAlertConfig = new EmailAlertConfig();
	private SaveJobOption saveJobOption = new SaveJobOption();
	private JobVerifiicationOption jobVerificationOption;
	public EncryptionCompression getEncryptionCompression() {
		return encryptionCompression;
	}
	public void setEncryptionCompression(EncryptionCompression encryptionCompression) {
		this.encryptionCompression = encryptionCompression;
	}
	public boolean isDisableFileEstimate() {
		return disableFileEstimate;
	}
	public void setDisableFileEstimate(boolean disableFileEstimate) {
		this.disableFileEstimate = disableFileEstimate;
	}
	public EjectMediaOption getEjectMediaOption() {
		return ejectMediaOption;
	}
	public void setEjectMediaOption(EjectMediaOption ejectMediaOption) {
		this.ejectMediaOption = ejectMediaOption;
	}
	public CommandConfig getCommandConfig() {
		return commandConfig;
	}
	public void setCommandConfig(CommandConfig commandConfig) {
		this.commandConfig = commandConfig;
	}
	public JobLogOption getJobLogOption() {
		return jobLogOption;
	}
	public void setJobLogOption(JobLogOption jobLogOption) {
		this.jobLogOption = jobLogOption;
	}
	public EmailAlertConfig getEmailAlertConfig() {
		return emailAlertConfig;
	}
	public void setEmailAlertConfig(EmailAlertConfig emailAlertConfig) {
		this.emailAlertConfig = emailAlertConfig;
	}
	public SaveJobOption getSaveJobOption() {
		return saveJobOption;
	}
	public void setSaveJobOption(SaveJobOption saveJobOption) {
		this.saveJobOption = saveJobOption;
	}
	public JobVerifiicationOption getJobVerificationOption() {
		return jobVerificationOption;
	}
	public void setJobVerificationOption(
			JobVerifiicationOption jobVerificationOption) {
		this.jobVerificationOption = jobVerificationOption;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
