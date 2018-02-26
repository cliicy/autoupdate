
package com.ca.arcserve.rha.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="add_host_registry_keysResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data_new" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "addHostRegistryKeysResult",
    "scenarioDataNew"
})
@XmlRootElement(name = "add_host_registry_keysResponse")
public class AddHostRegistryKeysResponse {

    @XmlElement(name = "add_host_registry_keysResult")
    protected boolean addHostRegistryKeysResult;
    @XmlElement(name = "scenario_data_new")
    protected String scenarioDataNew;

    /**
     * Gets the value of the addHostRegistryKeysResult property.
     * 
     */
    public boolean isAddHostRegistryKeysResult() {
        return addHostRegistryKeysResult;
    }

    /**
     * Sets the value of the addHostRegistryKeysResult property.
     * 
     */
    public void setAddHostRegistryKeysResult(boolean value) {
        this.addHostRegistryKeysResult = value;
    }

    /**
     * Gets the value of the scenarioDataNew property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioDataNew() {
        return scenarioDataNew;
    }

    /**
     * Sets the value of the scenarioDataNew property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioDataNew(String value) {
        this.scenarioDataNew = value;
    }

}
