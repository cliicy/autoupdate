
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
 *         &lt;element name="is_appl_installedResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="res_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "isApplInstalledResult",
    "resMessage"
})
@XmlRootElement(name = "is_appl_installedResponse")
public class IsApplInstalledResponse {

    @XmlElement(name = "is_appl_installedResult")
    protected boolean isApplInstalledResult;
    @XmlElement(name = "res_message")
    protected String resMessage;

    /**
     * Gets the value of the isApplInstalledResult property.
     * 
     */
    public boolean isIsApplInstalledResult() {
        return isApplInstalledResult;
    }

    /**
     * Sets the value of the isApplInstalledResult property.
     * 
     */
    public void setIsApplInstalledResult(boolean value) {
        this.isApplInstalledResult = value;
    }

    /**
     * Gets the value of the resMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResMessage() {
        return resMessage;
    }

    /**
     * Sets the value of the resMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResMessage(String value) {
        this.resMessage = value;
    }

}
