
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
 *         &lt;element name="replica_host_index" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="check_purpose" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="execute_sync" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="sce_to_discover_array" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "replicaHostIndex",
    "checkPurpose",
    "executeSync",
    "sceToDiscoverArray"
})
@XmlRootElement(name = "verify_group_state")
public class VerifyGroupState {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "group_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long groupId;
    @XmlElement(name = "replica_host_index")
    @XmlSchemaType(name = "unsignedInt")
    protected long replicaHostIndex;
    @XmlElement(name = "check_purpose")
    @XmlSchemaType(name = "unsignedInt")
    protected long checkPurpose;
    @XmlElement(name = "execute_sync")
    protected boolean executeSync;
    @XmlElement(name = "sce_to_discover_array")
    protected String sceToDiscoverArray;

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
     * Gets the value of the replicaHostIndex property.
     * 
     */
    public long getReplicaHostIndex() {
        return replicaHostIndex;
    }

    /**
     * Sets the value of the replicaHostIndex property.
     * 
     */
    public void setReplicaHostIndex(long value) {
        this.replicaHostIndex = value;
    }

    /**
     * Gets the value of the checkPurpose property.
     * 
     */
    public long getCheckPurpose() {
        return checkPurpose;
    }

    /**
     * Sets the value of the checkPurpose property.
     * 
     */
    public void setCheckPurpose(long value) {
        this.checkPurpose = value;
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
     * Gets the value of the sceToDiscoverArray property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSceToDiscoverArray() {
        return sceToDiscoverArray;
    }

    /**
     * Sets the value of the sceToDiscoverArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSceToDiscoverArray(String value) {
        this.sceToDiscoverArray = value;
    }

}
