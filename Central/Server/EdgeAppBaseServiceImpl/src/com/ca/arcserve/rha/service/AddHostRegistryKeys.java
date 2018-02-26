
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
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="host_index" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="is_x64_keys" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="reg_keys" type="{http://ca.com/}ArrayOfString" minOccurs="0"/>
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
    "scenarioData",
    "hostIndex",
    "isX64Keys",
    "regKeys"
})
@XmlRootElement(name = "add_host_registry_keys")
public class AddHostRegistryKeys {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "host_index")
    @XmlSchemaType(name = "unsignedInt")
    protected long hostIndex;
    @XmlElement(name = "is_x64_keys")
    protected boolean isX64Keys;
    @XmlElement(name = "reg_keys")
    protected ArrayOfString regKeys;

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
     * Gets the value of the scenarioData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioData() {
        return scenarioData;
    }

    /**
     * Sets the value of the scenarioData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioData(String value) {
        this.scenarioData = value;
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
     * Gets the value of the isX64Keys property.
     * 
     */
    public boolean isIsX64Keys() {
        return isX64Keys;
    }

    /**
     * Sets the value of the isX64Keys property.
     * 
     */
    public void setIsX64Keys(boolean value) {
        this.isX64Keys = value;
    }

    /**
     * Gets the value of the regKeys property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getRegKeys() {
        return regKeys;
    }

    /**
     * Sets the value of the regKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setRegKeys(ArrayOfString value) {
        this.regKeys = value;
    }

}
