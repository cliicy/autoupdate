
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
 *         &lt;element name="session_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="lic_key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "sessionId",
    "licKey"
})
@XmlRootElement(name = "lic_check_key")
public class LicCheckKey {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "lic_key")
    protected String licKey;

    /**
     * Gets the value of the sessionId property.
     * 
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     */
    public void setSessionId(long value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the licKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicKey() {
        return licKey;
    }

    /**
     * Sets the value of the licKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicKey(String value) {
        this.licKey = value;
    }

}
