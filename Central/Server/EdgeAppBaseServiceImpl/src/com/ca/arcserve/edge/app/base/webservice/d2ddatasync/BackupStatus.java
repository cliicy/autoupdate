package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackupStatus")
public class BackupStatus {
    @XmlAttribute(name = "Status")
    protected String status;
    
    public String getStatus() {
        return status;
    }


    public void setStatus(String value) {
        this.status = value;
    }
}
