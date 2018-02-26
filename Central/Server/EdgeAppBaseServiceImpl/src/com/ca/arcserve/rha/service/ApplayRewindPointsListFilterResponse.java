
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
 *         &lt;element name="applay_rewind_points_list_filterResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="total_count" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "applayRewindPointsListFilterResult",
    "totalCount",
    "whyNotReason"
})
@XmlRootElement(name = "applay_rewind_points_list_filterResponse")
public class ApplayRewindPointsListFilterResponse {

    @XmlElement(name = "applay_rewind_points_list_filterResult")
    protected boolean applayRewindPointsListFilterResult;
    @XmlElement(name = "total_count")
    @XmlSchemaType(name = "unsignedInt")
    protected long totalCount;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the applayRewindPointsListFilterResult property.
     * 
     */
    public boolean isApplayRewindPointsListFilterResult() {
        return applayRewindPointsListFilterResult;
    }

    /**
     * Sets the value of the applayRewindPointsListFilterResult property.
     * 
     */
    public void setApplayRewindPointsListFilterResult(boolean value) {
        this.applayRewindPointsListFilterResult = value;
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
