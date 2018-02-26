
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
 *         &lt;element name="validate_scenarioResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="validation_result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "validateScenarioResult",
    "validationResult"
})
@XmlRootElement(name = "validate_scenarioResponse")
public class ValidateScenarioResponse {

    @XmlElement(name = "validate_scenarioResult")
    protected boolean validateScenarioResult;
    @XmlElement(name = "validation_result")
    protected String validationResult;

    /**
     * Gets the value of the validateScenarioResult property.
     * 
     */
    public boolean isValidateScenarioResult() {
        return validateScenarioResult;
    }

    /**
     * Sets the value of the validateScenarioResult property.
     * 
     */
    public void setValidateScenarioResult(boolean value) {
        this.validateScenarioResult = value;
    }

    /**
     * Gets the value of the validationResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidationResult() {
        return validationResult;
    }

    /**
     * Sets the value of the validationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidationResult(String value) {
        this.validationResult = value;
    }

}
