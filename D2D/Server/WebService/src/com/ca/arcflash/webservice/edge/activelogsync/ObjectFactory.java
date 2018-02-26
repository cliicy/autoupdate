package com.ca.arcflash.webservice.edge.activelogsync;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public ObjectFactory() {
    	
    }
    
    public ActivityLogTrans createActivityLogTrans() {
        return new ActivityLogTrans();
    }
    
    public LogRec createLogRec() {
        return new LogRec();
    }
}