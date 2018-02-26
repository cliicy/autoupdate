
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
 *         &lt;element name="set_host_disk_layoutResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="set_result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "setHostDiskLayoutResult",
    "setResult",
    "asyncId",
    "errMessages"
})
@XmlRootElement(name = "set_host_disk_layoutResponse")
public class SetHostDiskLayoutResponse {

    @XmlElement(name = "set_host_disk_layoutResult")
    protected boolean setHostDiskLayoutResult;
    @XmlElement(name = "set_result")
    protected String setResult;
    @XmlElement(name = "async_id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger asyncId;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the setHostDiskLayoutResult property.
     * 
     */
    public boolean isSetHostDiskLayoutResult() {
        return setHostDiskLayoutResult;
    }

    /**
     * Sets the value of the setHostDiskLayoutResult property.
     * 
     */
    public void setSetHostDiskLayoutResult(boolean value) {
        this.setHostDiskLayoutResult = value;
    }

    /**
     * Gets the value of the setResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSetResult() {
        return setResult;
    }

    /**
     * Sets the value of the setResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSetResult(String value) {
        this.setResult = value;
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
