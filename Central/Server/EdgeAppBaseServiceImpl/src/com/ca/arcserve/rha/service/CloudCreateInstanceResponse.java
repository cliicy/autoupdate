
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
 *         &lt;element name="cloud_create_instanceResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="instance_details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="async_id" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "cloudCreateInstanceResult",
    "instanceDetails",
    "asyncId",
    "errMessages"
})
@XmlRootElement(name = "cloud_create_instanceResponse")
public class CloudCreateInstanceResponse {

    @XmlElement(name = "cloud_create_instanceResult")
    protected boolean cloudCreateInstanceResult;
    @XmlElement(name = "instance_details")
    protected String instanceDetails;
    @XmlElement(name = "async_id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger asyncId;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the cloudCreateInstanceResult property.
     * 
     */
    public boolean isCloudCreateInstanceResult() {
        return cloudCreateInstanceResult;
    }

    /**
     * Sets the value of the cloudCreateInstanceResult property.
     * 
     */
    public void setCloudCreateInstanceResult(boolean value) {
        this.cloudCreateInstanceResult = value;
    }

    /**
     * Gets the value of the instanceDetails property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceDetails() {
        return instanceDetails;
    }

    /**
     * Sets the value of the instanceDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceDetails(String value) {
        this.instanceDetails = value;
    }

    /**
     * Gets the value of the asyncId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAsyncId() {
        return asyncId;
    }

    /**
     * Sets the value of the asyncId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAsyncId(BigInteger value) {
        this.asyncId = value;
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
