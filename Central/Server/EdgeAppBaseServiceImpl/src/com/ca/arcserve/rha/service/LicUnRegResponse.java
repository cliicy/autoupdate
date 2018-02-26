
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
 *         &lt;element name="lic_un_regResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "licUnRegResult"
})
@XmlRootElement(name = "lic_un_regResponse")
public class LicUnRegResponse {

    @XmlElement(name = "lic_un_regResult")
    protected boolean licUnRegResult;

    /**
     * Gets the value of the licUnRegResult property.
     * 
     */
    public boolean isLicUnRegResult() {
        return licUnRegResult;
    }

    /**
     * Sets the value of the licUnRegResult property.
     * 
     */
    public void setLicUnRegResult(boolean value) {
        this.licUnRegResult = value;
    }

}
