
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
 *         &lt;element name="RecoveryResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "recoveryResult"
})
@XmlRootElement(name = "RecoveryResponse")
public class RecoveryResponse {

    @XmlElement(name = "RecoveryResult")
    protected int recoveryResult;

    /**
     * Gets the value of the recoveryResult property.
     * 
     */
    public int getRecoveryResult() {
        return recoveryResult;
    }

    /**
     * Sets the value of the recoveryResult property.
     * 
     */
    public void setRecoveryResult(int value) {
        this.recoveryResult = value;
    }

}
