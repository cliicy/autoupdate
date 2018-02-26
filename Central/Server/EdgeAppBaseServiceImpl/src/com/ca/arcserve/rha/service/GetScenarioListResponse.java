
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
 *         &lt;element name="get_scenario_listResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getScenarioListResult"
})
@XmlRootElement(name = "get_scenario_listResponse")
public class GetScenarioListResponse {

    @XmlElement(name = "get_scenario_listResult")
    protected String getScenarioListResult;

    /**
     * Gets the value of the getScenarioListResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetScenarioListResult() {
        return getScenarioListResult;
    }

    /**
     * Sets the value of the getScenarioListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetScenarioListResult(String value) {
        this.getScenarioListResult = value;
    }

}
