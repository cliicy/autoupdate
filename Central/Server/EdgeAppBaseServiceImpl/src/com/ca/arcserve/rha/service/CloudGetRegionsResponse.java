
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
 *         &lt;element name="cloud_get_regionsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="region_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "cloudGetRegionsResult",
    "regionList",
    "errMessages"
})
@XmlRootElement(name = "cloud_get_regionsResponse")
public class CloudGetRegionsResponse {

    @XmlElement(name = "cloud_get_regionsResult")
    protected boolean cloudGetRegionsResult;
    @XmlElement(name = "region_list")
    protected String regionList;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the cloudGetRegionsResult property.
     * 
     */
    public boolean isCloudGetRegionsResult() {
        return cloudGetRegionsResult;
    }

    /**
     * Sets the value of the cloudGetRegionsResult property.
     * 
     */
    public void setCloudGetRegionsResult(boolean value) {
        this.cloudGetRegionsResult = value;
    }

    /**
     * Gets the value of the regionList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionList() {
        return regionList;
    }

    /**
     * Sets the value of the regionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionList(String value) {
        this.regionList = value;
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
