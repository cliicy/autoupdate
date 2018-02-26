
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
 *         &lt;element name="validate_sessionResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "validateSessionResult"
})
@XmlRootElement(name = "validate_sessionResponse")
public class ValidateSessionResponse {

    @XmlElement(name = "validate_sessionResult")
    protected boolean validateSessionResult;

    /**
     * Gets the value of the validateSessionResult property.
     * 
     */
    public boolean isValidateSessionResult() {
        return validateSessionResult;
    }

    /**
     * Sets the value of the validateSessionResult property.
     * 
     */
    public void setValidateSessionResult(boolean value) {
        this.validateSessionResult = value;
    }

}
