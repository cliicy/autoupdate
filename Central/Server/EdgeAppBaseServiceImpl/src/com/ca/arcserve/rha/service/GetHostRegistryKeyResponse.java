
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
 *         &lt;element name="get_host_registry_keyResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="xml_response" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="why_not_reason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getHostRegistryKeyResult",
    "xmlResponse",
    "whyNotReason"
})
@XmlRootElement(name = "get_host_registry_keyResponse")
public class GetHostRegistryKeyResponse {

    @XmlElement(name = "get_host_registry_keyResult")
    protected boolean getHostRegistryKeyResult;
    @XmlElement(name = "xml_response")
    protected String xmlResponse;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the getHostRegistryKeyResult property.
     * 
     */
    public boolean isGetHostRegistryKeyResult() {
        return getHostRegistryKeyResult;
    }

    /**
     * Sets the value of the getHostRegistryKeyResult property.
     * 
     */
    public void setGetHostRegistryKeyResult(boolean value) {
        this.getHostRegistryKeyResult = value;
    }

    /**
     * Gets the value of the xmlResponse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlResponse() {
        return xmlResponse;
    }

    /**
     * Sets the value of the xmlResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlResponse(String value) {
        this.xmlResponse = value;
    }

    /**
     * Gets the value of the whyNotReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhyNotReason() {
        return whyNotReason;
    }

    /**
     * Sets the value of the whyNotReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhyNotReason(String value) {
        this.whyNotReason = value;
    }

}
