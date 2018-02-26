
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
 *         &lt;element name="get_host_listResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="host_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getHostListResult",
    "hostList",
    "whyNotReason"
})
@XmlRootElement(name = "get_host_listResponse")
public class GetHostListResponse {

    @XmlElement(name = "get_host_listResult")
    protected boolean getHostListResult;
    @XmlElement(name = "host_list")
    protected String hostList;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the getHostListResult property.
     * 
     */
    public boolean isGetHostListResult() {
        return getHostListResult;
    }

    /**
     * Sets the value of the getHostListResult property.
     * 
     */
    public void setGetHostListResult(boolean value) {
        this.getHostListResult = value;
    }

    /**
     * Gets the value of the hostList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostList() {
        return hostList;
    }

    /**
     * Sets the value of the hostList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostList(String value) {
        this.hostList = value;
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
