
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
 *         &lt;element name="get_rewind_points_next_chunkResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="to_be_cont" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="total_count" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "getRewindPointsNextChunkResult",
    "toBeCont",
    "totalCount",
    "currentCount",
    "percent",
    "whyNotReason"
})
@XmlRootElement(name = "get_rewind_points_next_chunkResponse")
public class GetRewindPointsNextChunkResponse {

    @XmlElement(name = "get_rewind_points_next_chunkResult")
    protected boolean getRewindPointsNextChunkResult;
    @XmlElement(name = "to_be_cont")
    protected boolean toBeCont;
    @XmlElement(name = "total_count")
    @XmlSchemaType(name = "unsignedInt")
    protected long totalCount;
    @XmlElement(name = "current_count")
    @XmlSchemaType(name = "unsignedInt")
    protected long currentCount;
    @XmlSchemaType(name = "unsignedInt")
    protected long percent;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the getRewindPointsNextChunkResult property.
     * 
     */
    public boolean isGetRewindPointsNextChunkResult() {
        return getRewindPointsNextChunkResult;
    }

    /**
     * Sets the value of the getRewindPointsNextChunkResult property.
     * 
     */
    public void setGetRewindPointsNextChunkResult(boolean value) {
        this.getRewindPointsNextChunkResult = value;
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
     * Gets the value of the totalCount property.
     * 
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the value of the totalCount property.
     * 
     */
    public void setTotalCount(long value) {
        this.totalCount = value;
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
