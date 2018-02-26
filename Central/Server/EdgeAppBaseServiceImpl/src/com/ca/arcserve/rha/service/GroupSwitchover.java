
package com.ca.arcserve.rha.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="session_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="group_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="switchover_type" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="execute_sync" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="run_reverse" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sessionId",
    "groupId",
    "switchoverType",
    "executeSync",
    "runReverse"
})
@XmlRootElement(name = "group_switchover")
public class GroupSwitchover {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "group_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long groupId;
    @XmlElement(name = "switchover_type")
    @XmlSchemaType(name = "unsignedInt")
    protected long switchoverType;
    @XmlElement(name = "execute_sync")
    protected boolean executeSync;
    @XmlElement(name = "run_reverse")
    protected boolean runReverse;

    /**
     * Gets the value of the sessionId property.
     * 
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     */
    public void setSessionId(long value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the groupId property.
     * 
     */
    public long getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     */
    public void setGroupId(long value) {
        this.groupId = value;
    }

    /**
     * Gets the value of the switchoverType property.
     * 
     */
    public long getSwitchoverType() {
        return switchoverType;
    }

    /**
     * Sets the value of the switchoverType property.
     * 
     */
    public void setSwitchoverType(long value) {
        this.switchoverType = value;
    }

    /**
     * Gets the value of the executeSync property.
     * 
     */
    public boolean isExecuteSync() {
        return executeSync;
    }

    /**
     * Sets the value of the executeSync property.
     * 
     */
    public void setExecuteSync(boolean value) {
        this.executeSync = value;
    }

    /**
     * Gets the value of the runReverse property.
     * 
     */
    public boolean isRunReverse() {
        return runReverse;
    }

    /**
     * Sets the value of the runReverse property.
     * 
     */
    public void setRunReverse(boolean value) {
        this.runReverse = value;
    }

}
