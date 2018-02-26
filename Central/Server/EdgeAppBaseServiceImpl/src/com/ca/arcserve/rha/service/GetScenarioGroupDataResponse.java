
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
 *         &lt;element name="get_scenario_group_dataResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getScenarioGroupDataResult"
})
@XmlRootElement(name = "get_scenario_group_dataResponse")
public class GetScenarioGroupDataResponse {

    @XmlElement(name = "get_scenario_group_dataResult")
    protected String getScenarioGroupDataResult;

    /**
     * Gets the value of the getScenarioGroupDataResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetScenarioGroupDataResult() {
        return getScenarioGroupDataResult;
    }

    /**
     * Sets the value of the getScenarioGroupDataResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetScenarioGroupDataResult(String value) {
        this.getScenarioGroupDataResult = value;
    }

}
