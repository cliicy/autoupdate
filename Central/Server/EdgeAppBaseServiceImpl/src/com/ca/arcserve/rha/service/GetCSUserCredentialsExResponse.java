
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
 *         &lt;element name="get_CS_user_credentialsExResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getCSUserCredentialsExResult"
})
@XmlRootElement(name = "get_CS_user_credentialsExResponse")
public class GetCSUserCredentialsExResponse {

    @XmlElement(name = "get_CS_user_credentialsExResult")
    protected String getCSUserCredentialsExResult;

    /**
     * Gets the value of the getCSUserCredentialsExResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetCSUserCredentialsExResult() {
        return getCSUserCredentialsExResult;
    }

    /**
     * Sets the value of the getCSUserCredentialsExResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetCSUserCredentialsExResult(String value) {
        this.getCSUserCredentialsExResult = value;
    }

}
