
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
 *         &lt;element name="host_indexes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="to_remove_scenario" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "hostIndexes",
    "toRemoveScenario"
})
@XmlRootElement(name = "cleanup_vm_resource")
public class CleanupVmResource {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenario_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenarioId;
    @XmlElement(name = "host_indexes")
    protected String hostIndexes;
    @XmlElement(name = "to_remove_scenario")
    protected boolean toRemoveScenario;

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
     * Gets the value of the hostIndexes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostIndexes() {
        return hostIndexes;
    }

    /**
     * Sets the value of the hostIndexes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostIndexes(String value) {
        this.hostIndexes = value;
    }

    /**
     * Gets the value of the toRemoveScenario property.
     * 
     */
    public boolean isToRemoveScenario() {
        return toRemoveScenario;
    }

    /**
     * Sets the value of the toRemoveScenario property.
     * 
     */
    public void setToRemoveScenario(boolean value) {
        this.toRemoveScenario = value;
    }

}
