package com.ca.arcflash.webservice.edge.activelogsync;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
    
    public void SetLogRec(List<LogRec> logRecLst) {
    	logRec = logRecLst;
    }
}
