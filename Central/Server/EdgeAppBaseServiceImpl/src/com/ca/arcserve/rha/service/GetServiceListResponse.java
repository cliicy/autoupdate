
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
 *         &lt;element name="get_service_listResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="service_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="err_messages" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getServiceListResult",
    "serviceList",
    "errMessages"
})
@XmlRootElement(name = "get_service_listResponse")
public class GetServiceListResponse {

    @XmlElement(name = "get_service_listResult")
    protected boolean getServiceListResult;
    @XmlElement(name = "service_list")
    protected String serviceList;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getServiceListResult property.
     * 
     */
    public boolean isGetServiceListResult() {
        return getServiceListResult;
    }

    /**
     * Sets the value of the getServiceListResult property.
     * 
     */
    public void setGetServiceListResult(boolean value) {
        this.getServiceListResult = value;
    }

    /**
     * Gets the value of the serviceList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceList() {
        return serviceList;
    }

    /**
     * Sets the value of the serviceList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceList(String value) {
        this.serviceList = value;
    }

    /**
     * Gets the value of the errMessages property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrMessages() {
        return errMessages;
    }

    /**
     * Sets the value of the errMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrMessages(String value) {
        this.errMessages = value;
    }

}
