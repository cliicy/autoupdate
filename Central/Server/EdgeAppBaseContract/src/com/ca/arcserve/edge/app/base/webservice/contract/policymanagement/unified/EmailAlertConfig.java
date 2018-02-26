package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

import com.ca.arcflash.rps.webservice.data.policy.RpsEmailAlertSettings;

public class EmailAlertConfig implements Serializable {
	private static final long serialVersionUID = 7741362742913097628L;
	private boolean enablEmailAlert;
	private boolean alertJobComplete;
	private boolean alertJobInComplete;
	private boolean alertCanceledByUser;
	private boolean alertJobFailed;
	private boolean alertCrashed;
	private boolean alertVirusDetected;
	private boolean alertMediaInAvailable;
	private boolean alertFormatBlankTape;
	private RpsEmailAlertSettings emailsetting;
	public boolean isAlertCrashed() {
		return alertCrashed;
	}

	public void setAlertCrashed(boolean alertCrashed) {
		this.alertCrashed = alertCrashed;
	}
	public boolean isEnablEmailAlert() {
		return enablEmailAlert;
	}

	public void setEnablEmailAlert(boolean enablEmailAlert) {
		this.enablEmailAlert = enablEmailAlert;
	}

	public boolean isAlertJobComplete() {
		return alertJobComplete;
	}

	public void setAlertJobComplete(boolean alertJobComplete) {
		this.alertJobComplete = alertJobComplete;
	}

	public boolean isAlertJobInComplete() {
		return alertJobInComplete;
	}

	public void setAlertJobInComplete(boolean alertJobInComplete) {
		this.alertJobInComplete = alertJobInComplete;
	}

	public boolean isAlertCanceledByUser() {
		return alertCanceledByUser;
	}

	public void setAlertCanceledByUser(boolean alertCanceledByUser) {
		this.alertCanceledByUser = alertCanceledByUser;
	}

	public boolean isAlertJobFailed() {
		return alertJobFailed;
	}

	public void setAlertJobFailed(boolean alertJobFailed) {
		this.alertJobFailed = alertJobFailed;
	}

	public boolean isAlertVirusDetected() {
		return alertVirusDetected;
	}

	public void setAlertVirusDetected(boolean alertVirusDetected) {
		this.alertVirusDetected = alertVirusDetected;
	}

	public boolean isAlertMediaInAvailable() {
		return alertMediaInAvailable;
	}

	public void setAlertMediaInAvailable(boolean alertMediaInAvailable) {
		this.alertMediaInAvailable = alertMediaInAvailable;
	}

	public boolean isAlertFormatBlankTape() {
		return alertFormatBlankTape;
	}

	public void setAlertFormatBlankTape(boolean alertFormatBlankTape) {
		this.alertFormatBlankTape = alertFormatBlankTape;
	}

	public RpsEmailAlertSettings getEmailsetting() {
		return emailsetting;
	}

	public void setEmailsetting(RpsEmailAlertSettings emailsetting) {
		this.emailsetting = emailsetting;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
