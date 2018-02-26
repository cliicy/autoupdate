
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
 *         &lt;element name="get_scenario_data_by_typeResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "getScenarioDataByTypeResult",
    "managerData"
})
@XmlRootElement(name = "get_scenario_data_by_typeResponse")
public class GetScenarioDataByTypeResponse {

    @XmlElement(name = "get_scenario_data_by_typeResult")
    protected boolean getScenarioDataByTypeResult;
    @XmlElement(name = "manager_data")
    protected String managerData;

    /**
     * Gets the value of the getScenarioDataByTypeResult property.
     * 
     */
    public boolean isGetScenarioDataByTypeResult() {
        return getScenarioDataByTypeResult;
    }

    /**
     * Sets the value of the getScenarioDataByTypeResult property.
     * 
     */
    public void setGetScenarioDataByTypeResult(boolean value) {
        this.getScenarioDataByTypeResult = value;
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
