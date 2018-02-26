package com.ca.arcflash.webservice.replication;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConversionSource implements Serializable {

	private static final long serialVersionUID = -614494750863559L;
	private String vmTag;
	private String backupDest;
	private String lastSession;
	
	public String getVmTag() {
		return vmTag;
	}
	
	public void setVmTag(String vmTag) {
		this.vmTag = vmTag;
	}
	
	public String getBackupDest() {
		return backupDest;
	}

	public void setBackupDest(String backupDest) {
		this.backupDest = backupDest;
	}

	public String getLastSession() {
		return lastSession;
	}

	public void setLastSession(String lastSession) {
		this.lastSession = lastSession;
	}

}

