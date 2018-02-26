
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
 *         &lt;element name="scenario_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="host_index" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="snapshot_file_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execute_sync" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="reboot_after_recovery_systemstate" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="recovery_location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "scenarioId",
    "hostIndex",
    "snapshotFileName",
    "executeSync",
    "rebootAfterRecoverySystemstate",
    "recoveryLocation"
})
@XmlRootElement(name = "restore_system_state")
public class RestoreSystemState {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenario_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenarioId;
    @XmlElement(name = "host_index")
    @XmlSchemaType(name = "unsignedInt")
    protected long hostIndex;
    @XmlElement(name = "snapshot_file_name")
    protected String snapshotFileName;
    @XmlElement(name = "execute_sync")
    protected boolean executeSync;
    @XmlElement(name = "reboot_after_recovery_systemstate")
    protected boolean rebootAfterRecoverySystemstate;
    @XmlElement(name = "recovery_location")
    protected String recoveryLocation;

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
     * Gets the value of the scenarioId property.
     * 
     */
    public long getScenarioId() {
        return scenarioId;
    }

    /**
     * Sets the value of the scenarioId property.
     * 
     */
    public void setScenarioId(long value) {
        this.scenarioId = value;
    }

    /**
     * Gets the value of the hostIndex property.
     * 
     */
    public long getHostIndex() {
        return hostIndex;
    }

    /**
     * Sets the value of the hostIndex property.
     * 
     */
    public void setHostIndex(long value) {
        this.hostIndex = value;
    }

    /**
     * Gets the value of the snapshotFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnapshotFileName() {
        return snapshotFileName;
    }

    /**
     * Sets the value of the snapshotFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnapshotFileName(String value) {
        this.snapshotFileName = value;
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
     * Gets the value of the rebootAfterRecoverySystemstate property.
     * 
     */
    public boolean isRebootAfterRecoverySystemstate() {
        return rebootAfterRecoverySystemstate;
    }

    /**
     * Sets the value of the rebootAfterRecoverySystemstate property.
     * 
     */
    public void setRebootAfterRecoverySystemstate(boolean value) {
        this.rebootAfterRecoverySystemstate = value;
    }

    /**
     * Gets the value of the recoveryLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecoveryLocation() {
        return recoveryLocation;
    }

    /**
     * Sets the value of the recoveryLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecoveryLocation(String value) {
        this.recoveryLocation = value;
    }

}
