
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
 *         &lt;element name="add_credentials_ex2Result" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "addCredentialsEx2Result"
})
@XmlRootElement(name = "add_credentials_ex2Response")
public class AddCredentialsEx2Response {

    @XmlElement(name = "add_credentials_ex2Result", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger addCredentialsEx2Result;

    /**
     * Gets the value of the addCredentialsEx2Result property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAddCredentialsEx2Result() {
        return addCredentialsEx2Result;
    }

    /**
     * Sets the value of the addCredentialsEx2Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAddCredentialsEx2Result(BigInteger value) {
        this.addCredentialsEx2Result = value;
    }

}
