
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
 *         &lt;element name="group_recover_active_serverResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="err_messages" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "groupRecoverActiveServerResult",
    "errMessages"
})
@XmlRootElement(name = "group_recover_active_serverResponse")
public class GroupRecoverActiveServerResponse {

    @XmlElement(name = "group_recover_active_serverResult")
    protected boolean groupRecoverActiveServerResult;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the groupRecoverActiveServerResult property.
     * 
     */
    public boolean isGroupRecoverActiveServerResult() {
        return groupRecoverActiveServerResult;
    }

    /**
     * Sets the value of the groupRecoverActiveServerResult property.
     * 
     */
    public void setGroupRecoverActiveServerResult(boolean value) {
        this.groupRecoverActiveServerResult = value;
    }

    /**
     * Gets the value of the errMessages property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrMessages() {
        return errMessages;
    }

    /**
     * Sets the value of the errMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrMessages(String value) {
        this.errMessages = value;
    }

}
