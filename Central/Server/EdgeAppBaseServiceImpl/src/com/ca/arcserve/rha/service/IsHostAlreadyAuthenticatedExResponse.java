
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
 *         &lt;element name="is_host_already_authenticated_exResult" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "isHostAlreadyAuthenticatedExResult"
})
@XmlRootElement(name = "is_host_already_authenticated_exResponse")
public class IsHostAlreadyAuthenticatedExResponse {

    @XmlElement(name = "is_host_already_authenticated_exResult", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger isHostAlreadyAuthenticatedExResult;

    /**
     * Gets the value of the isHostAlreadyAuthenticatedExResult property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIsHostAlreadyAuthenticatedExResult() {
        return isHostAlreadyAuthenticatedExResult;
    }

    /**
     * Sets the value of the isHostAlreadyAuthenticatedExResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIsHostAlreadyAuthenticatedExResult(BigInteger value) {
        this.isHostAlreadyAuthenticatedExResult = value;
    }

}
