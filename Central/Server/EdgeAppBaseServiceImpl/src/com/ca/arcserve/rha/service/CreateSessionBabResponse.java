
package com.ca.arcserve.rha.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="create_session_babResult" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "createSessionBabResult"
})
@XmlRootElement(name = "create_session_babResponse")
public class CreateSessionBabResponse {

    @XmlElement(name = "create_session_babResult")
    @XmlSchemaType(name = "unsignedInt")
    protected long createSessionBabResult;

    /**
     * Gets the value of the createSessionBabResult property.
     * 
     */
    public long getCreateSessionBabResult() {
        return createSessionBabResult;
    }

    /**
     * Sets the value of the createSessionBabResult property.
     * 
     */
    public void setCreateSessionBabResult(long value) {
        this.createSessionBabResult = value;
    }

}
