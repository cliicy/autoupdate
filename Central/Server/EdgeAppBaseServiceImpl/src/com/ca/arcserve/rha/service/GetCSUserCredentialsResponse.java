
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
 *         &lt;element name="get_CS_user_credentialsResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getCSUserCredentialsResult"
})
@XmlRootElement(name = "get_CS_user_credentialsResponse")
public class GetCSUserCredentialsResponse {

    @XmlElement(name = "get_CS_user_credentialsResult")
    protected String getCSUserCredentialsResult;

    /**
     * Gets the value of the getCSUserCredentialsResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetCSUserCredentialsResult() {
        return getCSUserCredentialsResult;
    }

    /**
     * Sets the value of the getCSUserCredentialsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetCSUserCredentialsResult(String value) {
        this.getCSUserCredentialsResult = value;
    }

}
