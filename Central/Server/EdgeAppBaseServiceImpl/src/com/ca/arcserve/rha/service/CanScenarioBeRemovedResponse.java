
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
 *         &lt;element name="can_scenario_be_removedResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "canScenarioBeRemovedResult",
    "whyNotReason"
})
@XmlRootElement(name = "can_scenario_be_removedResponse")
public class CanScenarioBeRemovedResponse {

    @XmlElement(name = "can_scenario_be_removedResult")
    protected boolean canScenarioBeRemovedResult;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the canScenarioBeRemovedResult property.
     * 
     */
    public boolean isCanScenarioBeRemovedResult() {
        return canScenarioBeRemovedResult;
    }

    /**
     * Sets the value of the canScenarioBeRemovedResult property.
     * 
     */
    public void setCanScenarioBeRemovedResult(boolean value) {
        this.canScenarioBeRemovedResult = value;
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
