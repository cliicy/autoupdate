
package com.ca.arcserve.rha.service;

import java.math.BigInteger;
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
 *         &lt;element name="is_host_already_authenticatedResult" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "isHostAlreadyAuthenticatedResult"
})
@XmlRootElement(name = "is_host_already_authenticatedResponse")
public class IsHostAlreadyAuthenticatedResponse {

    @XmlElement(name = "is_host_already_authenticatedResult", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger isHostAlreadyAuthenticatedResult;

    /**
     * Gets the value of the isHostAlreadyAuthenticatedResult property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIsHostAlreadyAuthenticatedResult() {
        return isHostAlreadyAuthenticatedResult;
    }

    /**
     * Sets the value of the isHostAlreadyAuthenticatedResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIsHostAlreadyAuthenticatedResult(BigInteger value) {
        this.isHostAlreadyAuthenticatedResult = value;
    }

}
