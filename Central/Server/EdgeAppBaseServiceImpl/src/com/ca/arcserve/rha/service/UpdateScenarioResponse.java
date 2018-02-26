
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
 *         &lt;element name="update_scenarioResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data_str" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="why_not_reason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "updateScenarioResult",
    "scenarioDataStr",
    "whyNotReason"
})
@XmlRootElement(name = "update_scenarioResponse")
public class UpdateScenarioResponse {

    @XmlElement(name = "update_scenarioResult")
    protected boolean updateScenarioResult;
    @XmlElement(name = "scenario_data_str")
    protected String scenarioDataStr;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the updateScenarioResult property.
     * 
     */
    public boolean isUpdateScenarioResult() {
        return updateScenarioResult;
    }

    /**
     * Sets the value of the updateScenarioResult property.
     * 
     */
    public void setUpdateScenarioResult(boolean value) {
        this.updateScenarioResult = value;
    }

    /**
     * Gets the value of the scenarioDataStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioDataStr() {
        return scenarioDataStr;
    }

    /**
     * Sets the value of the scenarioDataStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioDataStr(String value) {
        this.scenarioDataStr = value;
    }

    /**
     * Gets the value of the whyNotReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhyNotReason() {
        return whyNotReason;
    }

    /**
     * Sets the value of the whyNotReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhyNotReason(String value) {
        this.whyNotReason = value;
    }

}
