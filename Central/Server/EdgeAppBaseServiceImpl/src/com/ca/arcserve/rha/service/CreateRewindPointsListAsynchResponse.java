
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
 *         &lt;element name="create_rewind_points_list_asynchResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="async_res_id" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "createRewindPointsListAsynchResult",
    "asyncResId",
    "whyNotReason"
})
@XmlRootElement(name = "create_rewind_points_list_asynchResponse")
public class CreateRewindPointsListAsynchResponse {

    @XmlElement(name = "create_rewind_points_list_asynchResult")
    protected boolean createRewindPointsListAsynchResult;
    @XmlElement(name = "async_res_id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger asyncResId;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the createRewindPointsListAsynchResult property.
     * 
     */
    public boolean isCreateRewindPointsListAsynchResult() {
        return createRewindPointsListAsynchResult;
    }

    /**
     * Sets the value of the createRewindPointsListAsynchResult property.
     * 
     */
    public void setCreateRewindPointsListAsynchResult(boolean value) {
        this.createRewindPointsListAsynchResult = value;
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
