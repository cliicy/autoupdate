package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SessionInfo", propOrder = {
    "backupDetail"
})
public class SessionInfo {
    @XmlElement(name = "BackupDetail", required = true)
    protected BackupDetail backupDetail;

	public BackupDetail getBackupDetail() {
		return backupDetail;
	}

	public void setBackupDetail(BackupDetail backupDetail) {
		this.backupDetail = backupDetail;
	}
}
