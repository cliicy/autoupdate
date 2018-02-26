
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
 *         &lt;element name="lic_get_keyResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="lic_key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "licGetKeyResult",
    "licKey"
})
@XmlRootElement(name = "lic_get_keyResponse")
public class LicGetKeyResponse {

    @XmlElement(name = "lic_get_keyResult")
    protected boolean licGetKeyResult;
    @XmlElement(name = "lic_key")
    protected String licKey;

    /**
     * Gets the value of the licGetKeyResult property.
     * 
     */
    public boolean isLicGetKeyResult() {
        return licGetKeyResult;
    }

    /**
     * Sets the value of the licGetKeyResult property.
     * 
     */
    public void setLicGetKeyResult(boolean value) {
        this.licGetKeyResult = value;
    }

    /**
     * Gets the value of the licKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicKey() {
        return licKey;
    }

    /**
     * Sets the value of the licKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicKey(String value) {
        this.licKey = value;
    }

}
