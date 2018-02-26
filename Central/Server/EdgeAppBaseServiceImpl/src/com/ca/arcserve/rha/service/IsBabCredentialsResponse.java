
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
 *         &lt;element name="is_bab_credentialsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "isBabCredentialsResult"
})
@XmlRootElement(name = "is_bab_credentialsResponse")
public class IsBabCredentialsResponse {

    @XmlElement(name = "is_bab_credentialsResult")
    protected boolean isBabCredentialsResult;

    /**
     * Gets the value of the isBabCredentialsResult property.
     * 
     */
    public boolean isIsBabCredentialsResult() {
        return isBabCredentialsResult;
    }

    /**
     * Sets the value of the isBabCredentialsResult property.
     * 
     */
    public void setIsBabCredentialsResult(boolean value) {
        this.isBabCredentialsResult = value;
    }

}
