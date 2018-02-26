
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
 *         &lt;element name="lic_check_keyResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "licCheckKeyResult"
})
@XmlRootElement(name = "lic_check_keyResponse")
public class LicCheckKeyResponse {

    @XmlElement(name = "lic_check_keyResult")
    protected boolean licCheckKeyResult;

    /**
     * Gets the value of the licCheckKeyResult property.
     * 
     */
    public boolean isLicCheckKeyResult() {
        return licCheckKeyResult;
    }

    /**
     * Sets the value of the licCheckKeyResult property.
     * 
     */
    public void setLicCheckKeyResult(boolean value) {
        this.licCheckKeyResult = value;
    }

}
