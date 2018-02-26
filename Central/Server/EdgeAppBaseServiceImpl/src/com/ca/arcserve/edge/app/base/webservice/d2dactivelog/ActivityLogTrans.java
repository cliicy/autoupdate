package com.ca.arcserve.edge.app.base.webservice.d2dactivelog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.BackupRecord;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivityLogTrans", propOrder = {
    "logRec"
})

@XmlRootElement(name = "ActivityLogTrans")
public class ActivityLogTrans {
    @XmlElement(name = "LogRec")
    protected List<LogRec> logRec;
    
    public List<LogRec> getLogRec() {
        if (logRec == null) {
        	logRec = new ArrayList<LogRec>();
        }
        return this.logRec;
    }
}
