
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
 *         &lt;element name="Send_Notification_MessageResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "sendNotificationMessageResult"
})
@XmlRootElement(name = "Send_Notification_MessageResponse")
public class SendNotificationMessageResponse {

    @XmlElement(name = "Send_Notification_MessageResult")
    protected int sendNotificationMessageResult;

    /**
     * Gets the value of the sendNotificationMessageResult property.
     * 
     */
    public int getSendNotificationMessageResult() {
        return sendNotificationMessageResult;
    }

    /**
     * Sets the value of the sendNotificationMessageResult property.
     * 
     */
    public void setSendNotificationMessageResult(int value) {
        this.sendNotificationMessageResult = value;
    }

}
