package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    	
    }
    
    /**
     * Create an instance of {@link JobStatus2Edge }
     * 
     */
    public JobStatus2Edge createEdgeRegInfo() {
        return new JobStatus2Edge();
    }
}