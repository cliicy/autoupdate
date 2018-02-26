
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
 *         &lt;element name="compare_db_configsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "compareDbConfigsResult",
    "asyncResId",
    "errMessages"
})
@XmlRootElement(name = "compare_db_configsResponse")
public class CompareDbConfigsResponse {

    @XmlElement(name = "compare_db_configsResult")
    protected boolean compareDbConfigsResult;
    @XmlElement(name = "async_res_id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger asyncResId;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the compareDbConfigsResult property.
     * 
     */
    public boolean isCompareDbConfigsResult() {
        return compareDbConfigsResult;
    }

    /**
     * Sets the value of the compareDbConfigsResult property.
     * 
     */
    public void setCompareDbConfigsResult(boolean value) {
        this.compareDbConfigsResult = value;
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
