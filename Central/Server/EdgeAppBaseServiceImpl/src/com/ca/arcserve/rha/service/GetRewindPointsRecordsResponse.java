
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
 *         &lt;element name="get_rewind_points_recordsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="points_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getRewindPointsRecordsResult",
    "pointsList",
    "whyNotReason"
})
@XmlRootElement(name = "get_rewind_points_recordsResponse")
public class GetRewindPointsRecordsResponse {

    @XmlElement(name = "get_rewind_points_recordsResult")
    protected boolean getRewindPointsRecordsResult;
    @XmlElement(name = "points_list")
    protected String pointsList;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the getRewindPointsRecordsResult property.
     * 
     */
    public boolean isGetRewindPointsRecordsResult() {
        return getRewindPointsRecordsResult;
    }

    /**
     * Sets the value of the getRewindPointsRecordsResult property.
     * 
     */
    public void setGetRewindPointsRecordsResult(boolean value) {
        this.getRewindPointsRecordsResult = value;
    }

    /**
     * Gets the value of the pointsList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPointsList() {
        return pointsList;
    }

    /**
     * Sets the value of the pointsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPointsList(String value) {
        this.pointsList = value;
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
