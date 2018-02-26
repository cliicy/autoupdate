
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
 *         &lt;element name="cloud_get_instance_paramsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="instance_params" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="async_res_id" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "cloudGetInstanceParamsResult",
    "instanceParams",
    "asyncResId",
    "errMessages"
})
@XmlRootElement(name = "cloud_get_instance_paramsResponse")
public class CloudGetInstanceParamsResponse {

    @XmlElement(name = "cloud_get_instance_paramsResult")
    protected boolean cloudGetInstanceParamsResult;
    @XmlElement(name = "instance_params")
    protected String instanceParams;
    @XmlElement(name = "async_res_id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger asyncResId;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the cloudGetInstanceParamsResult property.
     * 
     */
    public boolean isCloudGetInstanceParamsResult() {
        return cloudGetInstanceParamsResult;
    }

    /**
     * Sets the value of the cloudGetInstanceParamsResult property.
     * 
     */
    public void setCloudGetInstanceParamsResult(boolean value) {
        this.cloudGetInstanceParamsResult = value;
    }

    /**
     * Gets the value of the instanceParams property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceParams() {
        return instanceParams;
    }

    /**
     * Sets the value of the instanceParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceParams(String value) {
        this.instanceParams = value;
    }

    /**
     * Gets the value of the asyncResId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAsyncResId() {
        return asyncResId;
    }

    /**
     * Sets the value of the asyncResId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAsyncResId(BigInteger value) {
        this.asyncResId = value;
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
