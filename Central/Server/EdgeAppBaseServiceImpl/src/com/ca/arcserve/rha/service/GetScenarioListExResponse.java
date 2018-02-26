
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
 *         &lt;element name="get_scenario_list_exResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_list_str" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getScenarioListExResult",
    "scenarioListStr"
})
@XmlRootElement(name = "get_scenario_list_exResponse")
public class GetScenarioListExResponse {

    @XmlElement(name = "get_scenario_list_exResult")
    protected boolean getScenarioListExResult;
    @XmlElement(name = "scenario_list_str")
    protected String scenarioListStr;

    /**
     * Gets the value of the getScenarioListExResult property.
     * 
     */
    public boolean isGetScenarioListExResult() {
        return getScenarioListExResult;
    }

    /**
     * Sets the value of the getScenarioListExResult property.
     * 
     */
    public void setGetScenarioListExResult(boolean value) {
        this.getScenarioListExResult = value;
    }

    /**
     * Gets the value of the scenarioListStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioListStr() {
        return scenarioListStr;
    }

    /**
     * Sets the value of the scenarioListStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioListStr(String value) {
        this.scenarioListStr = value;
    }

}
