
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
 *         &lt;element name="set_host_credentialsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "setHostCredentialsResult"
})
@XmlRootElement(name = "set_host_credentialsResponse")
public class SetHostCredentialsResponse {

    @XmlElement(name = "set_host_credentialsResult")
    protected boolean setHostCredentialsResult;

    /**
     * Gets the value of the setHostCredentialsResult property.
     * 
     */
    public boolean isSetHostCredentialsResult() {
        return setHostCredentialsResult;
    }

    /**
     * Sets the value of the setHostCredentialsResult property.
     * 
     */
    public void setSetHostCredentialsResult(boolean value) {
        this.setHostCredentialsResult = value;
    }

}
