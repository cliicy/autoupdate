
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
 *         &lt;element name="net_mapping_id" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="snapshot_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "netMappingId",
    "snapshotId"
})
@XmlRootElement(name = "start_vm_with_snapshort")
public class StartVmWithSnapshort {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenario_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenarioId;
    @XmlElement(name = "host_index")
    @XmlSchemaType(name = "unsignedInt")
    protected long hostIndex;
    @XmlElement(name = "net_mapping_id")
    protected short netMappingId;
    @XmlElement(name = "snapshot_id")
    protected String snapshotId;

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
     * Gets the value of the netMappingId property.
     * 
     */
    public short getNetMappingId() {
        return netMappingId;
    }

    /**
     * Sets the value of the netMappingId property.
     * 
     */
    public void setNetMappingId(short value) {
        this.netMappingId = value;
    }

    /**
     * Gets the value of the snapshotId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    /**
     * Sets the value of the snapshotId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnapshotId(String value) {
        this.snapshotId = value;
    }

}
