
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
 *         &lt;element name="get_rewind_points_list_statusResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="to_be_cont" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="estimate_remaining_time" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="current_count" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "getRewindPointsListStatusResult",
    "toBeCont",
    "estimateRemainingTime",
    "currentCount",
    "percent",
    "whyNotReason"
})
@XmlRootElement(name = "get_rewind_points_list_statusResponse")
public class GetRewindPointsListStatusResponse {

    @XmlElement(name = "get_rewind_points_list_statusResult")
    protected boolean getRewindPointsListStatusResult;
    @XmlElement(name = "to_be_cont")
    protected boolean toBeCont;
    @XmlElement(name = "estimate_remaining_time")
    @XmlSchemaType(name = "unsignedInt")
    protected long estimateRemainingTime;
    @XmlElement(name = "current_count")
    @XmlSchemaType(name = "unsignedInt")
    protected long currentCount;
    @XmlSchemaType(name = "unsignedInt")
    protected long percent;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the getRewindPointsListStatusResult property.
     * 
     */
    public boolean isGetRewindPointsListStatusResult() {
        return getRewindPointsListStatusResult;
    }

    /**
     * Sets the value of the getRewindPointsListStatusResult property.
     * 
     */
    public void setGetRewindPointsListStatusResult(boolean value) {
        this.getRewindPointsListStatusResult = value;
    }

    /**
     * Gets the value of the toBeCont property.
     * 
     */
    public boolean isToBeCont() {
        return toBeCont;
    }

    /**
     * Sets the value of the toBeCont property.
     * 
     */
    public void setToBeCont(boolean value) {
        this.toBeCont = value;
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
     * Gets the value of the currentCount property.
     * 
     */
    public long getCurrentCount() {
        return currentCount;
    }

    /**
     * Sets the value of the currentCount property.
     * 
     */
    public void setCurrentCount(long value) {
        this.currentCount = value;
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
