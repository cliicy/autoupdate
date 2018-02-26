
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
 *         &lt;element name="lic_get_descResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="lic_desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "licGetDescResult",
    "licDesc"
})
@XmlRootElement(name = "lic_get_descResponse")
public class LicGetDescResponse {

    @XmlElement(name = "lic_get_descResult")
    protected boolean licGetDescResult;
    @XmlElement(name = "lic_desc")
    protected String licDesc;

    /**
     * Gets the value of the licGetDescResult property.
     * 
     */
    public boolean isLicGetDescResult() {
        return licGetDescResult;
    }

    /**
     * Sets the value of the licGetDescResult property.
     * 
     */
    public void setLicGetDescResult(boolean value) {
        this.licGetDescResult = value;
    }

    /**
     * Gets the value of the licDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicDesc() {
        return licDesc;
    }

    /**
     * Sets the value of the licDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicDesc(String value) {
        this.licDesc = value;
    }

}
