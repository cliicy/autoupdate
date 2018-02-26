package com.ca.arcflash.webservice.replication;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConversionSessionItem implements Serializable {

	private static final long serialVersionUID = -614494750863570L;
	private String sessionName;
	private String sessionGuid;
	private Date conversionTime;

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSessionGuid() {
		return sessionGuid;
	}

	public void setSessionGuid(String sessionGuid) {
		this.sessionGuid = sessionGuid;
	}

	public Date getConversionTime() {
		return conversionTime;
	}

	public void setConversionTime(Date conversionTime) {
		this.conversionTime = conversionTime;
	}
}
