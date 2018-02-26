
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
 *         &lt;element name="cloud_get_instance_listResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="instance_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "cloudGetInstanceListResult",
    "instanceList",
    "errMessages"
})
@XmlRootElement(name = "cloud_get_instance_listResponse")
public class CloudGetInstanceListResponse {

    @XmlElement(name = "cloud_get_instance_listResult")
    protected boolean cloudGetInstanceListResult;
    @XmlElement(name = "instance_list")
    protected String instanceList;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the cloudGetInstanceListResult property.
     * 
     */
    public boolean isCloudGetInstanceListResult() {
        return cloudGetInstanceListResult;
    }

    /**
     * Sets the value of the cloudGetInstanceListResult property.
     * 
     */
    public void setCloudGetInstanceListResult(boolean value) {
        this.cloudGetInstanceListResult = value;
    }

    /**
     * Gets the value of the instanceList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceList() {
        return instanceList;
    }

    /**
     * Sets the value of the instanceList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceList(String value) {
        this.instanceList = value;
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
