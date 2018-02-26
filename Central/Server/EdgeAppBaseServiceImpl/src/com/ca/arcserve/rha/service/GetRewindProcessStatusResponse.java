
package com.ca.arcserve.rha.service;

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
 *         &lt;element name="get_rewind_process_statusResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="estimate_remaining_time" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="percent" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "getRewindProcessStatusResult",
    "estimateRemainingTime",
    "percent",
    "whyNotReason"
})
@XmlRootElement(name = "get_rewind_process_statusResponse")
public class GetRewindProcessStatusResponse {

    @XmlElement(name = "get_rewind_process_statusResult")
    protected boolean getRewindProcessStatusResult;
    @XmlElement(name = "estimate_remaining_time")
    @XmlSchemaType(name = "unsignedInt")
    protected long estimateRemainingTime;
    @XmlSchemaType(name = "unsignedInt")
    protected long percent;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the getRewindProcessStatusResult property.
     * 
     */
    public boolean isGetRewindProcessStatusResult() {
        return getRewindProcessStatusResult;
    }

    /**
     * Sets the value of the getRewindProcessStatusResult property.
     * 
     */
    public void setGetRewindProcessStatusResult(boolean value) {
        this.getRewindProcessStatusResult = value;
    }

    /**
     * Gets the value of the estimateRemainingTime property.
     * 
     */
    public long getEstimateRemainingTime() {
        return estimateRemainingTime;
    }

    /**
     * Sets the value of the estimateRemainingTime property.
     * 
     */
    public void setEstimateRemainingTime(long value) {
        this.estimateRemainingTime = value;
    }

    /**
     * Gets the value of the percent property.
     * 
     */
    public long getPercent() {
        return percent;
    }

    /**
     * Sets the value of the percent property.
     * 
     */
    public void setPercent(long value) {
        this.percent = value;
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
