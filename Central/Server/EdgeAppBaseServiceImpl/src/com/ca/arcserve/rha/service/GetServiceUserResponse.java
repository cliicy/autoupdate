
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
 *         &lt;element name="get_service_userResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getServiceUserResult"
})
@XmlRootElement(name = "get_service_userResponse")
public class GetServiceUserResponse {

    @XmlElement(name = "get_service_userResult")
    protected String getServiceUserResult;

    /**
     * Gets the value of the getServiceUserResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetServiceUserResult() {
        return getServiceUserResult;
    }

    /**
     * Sets the value of the getServiceUserResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetServiceUserResult(String value) {
        this.getServiceUserResult = value;
    }

}
