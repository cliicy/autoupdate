
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
 *         &lt;element name="group_switchoverResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "groupSwitchoverResult",
    "errMessages"
})
@XmlRootElement(name = "group_switchoverResponse")
public class GroupSwitchoverResponse {

    @XmlElement(name = "group_switchoverResult")
    protected boolean groupSwitchoverResult;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the groupSwitchoverResult property.
     * 
     */
    public boolean isGroupSwitchoverResult() {
        return groupSwitchoverResult;
    }

    /**
     * Sets the value of the groupSwitchoverResult property.
     * 
     */
    public void setGroupSwitchoverResult(boolean value) {
        this.groupSwitchoverResult = value;
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
