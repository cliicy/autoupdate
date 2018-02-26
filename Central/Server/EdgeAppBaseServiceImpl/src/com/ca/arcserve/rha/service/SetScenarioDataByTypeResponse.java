
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
 *         &lt;element name="set_scenario_data_by_typeResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="manager_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "setScenarioDataByTypeResult",
    "managerData"
})
@XmlRootElement(name = "set_scenario_data_by_typeResponse")
public class SetScenarioDataByTypeResponse {

    @XmlElement(name = "set_scenario_data_by_typeResult")
    protected boolean setScenarioDataByTypeResult;
    @XmlElement(name = "manager_data")
    protected String managerData;

    /**
     * Gets the value of the setScenarioDataByTypeResult property.
     * 
     */
    public boolean isSetScenarioDataByTypeResult() {
        return setScenarioDataByTypeResult;
    }

    /**
     * Sets the value of the setScenarioDataByTypeResult property.
     * 
     */
    public void setSetScenarioDataByTypeResult(boolean value) {
        this.setScenarioDataByTypeResult = value;
    }

    /**
     * Gets the value of the managerData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagerData() {
        return managerData;
    }

    /**
     * Sets the value of the managerData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagerData(String value) {
        this.managerData = value;
    }

}
