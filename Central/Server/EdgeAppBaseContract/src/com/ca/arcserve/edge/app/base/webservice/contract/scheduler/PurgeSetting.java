package com.ca.arcserve.edge.app.base.webservice.contract.scheduler;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PurgeSetting implements Serializable {

	private static final long serialVersionUID = 943931327475760694L;
	
	private int retentionDays = 90;
	private int purgeHourOfDay;
	
	public int getRetentionDays() {
		return retentionDays;
	}
	public void setRetentionDays(int retentionDays) {
		this.retentionDays = retentionDays;
	}
	public int getPurgeHourOfDay() {
		return purgeHourOfDay;
	}
	public void setPurgeHourOfDay(int purgeHourOfDay) {
		this.purgeHourOfDay = purgeHourOfDay;
	}
	
}
