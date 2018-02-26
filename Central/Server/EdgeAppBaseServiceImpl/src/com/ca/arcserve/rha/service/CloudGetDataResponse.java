
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
 *         &lt;element name="cloud_get_dataResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="cloud_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "cloudGetDataResult",
    "cloudData",
    "errMessages"
})
@XmlRootElement(name = "cloud_get_dataResponse")
public class CloudGetDataResponse {

    @XmlElement(name = "cloud_get_dataResult")
    protected boolean cloudGetDataResult;
    @XmlElement(name = "cloud_data")
    protected String cloudData;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the cloudGetDataResult property.
     * 
     */
    public boolean isCloudGetDataResult() {
        return cloudGetDataResult;
    }

    /**
     * Sets the value of the cloudGetDataResult property.
     * 
     */
    public void setCloudGetDataResult(boolean value) {
        this.cloudGetDataResult = value;
    }

    /**
     * Gets the value of the cloudData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloudData() {
        return cloudData;
    }

    /**
     * Sets the value of the cloudData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloudData(String value) {
        this.cloudData = value;
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
