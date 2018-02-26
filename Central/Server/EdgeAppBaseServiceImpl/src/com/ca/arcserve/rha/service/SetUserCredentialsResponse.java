
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
 *         &lt;element name="set_user_credentialsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "setUserCredentialsResult"
})
@XmlRootElement(name = "set_user_credentialsResponse")
public class SetUserCredentialsResponse {

    @XmlElement(name = "set_user_credentialsResult")
    protected boolean setUserCredentialsResult;

    /**
     * Gets the value of the setUserCredentialsResult property.
     * 
     */
    public boolean isSetUserCredentialsResult() {
        return setUserCredentialsResult;
    }

    /**
     * Sets the value of the setUserCredentialsResult property.
     * 
     */
    public void setSetUserCredentialsResult(boolean value) {
        this.setUserCredentialsResult = value;
    }

}
