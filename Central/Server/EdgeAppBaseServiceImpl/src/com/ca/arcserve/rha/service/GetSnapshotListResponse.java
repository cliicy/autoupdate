
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
 *         &lt;element name="get_snapshot_listResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="snapshot_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getSnapshotListResult",
    "snapshotList",
    "whyNotReason"
})
@XmlRootElement(name = "get_snapshot_listResponse")
public class GetSnapshotListResponse {

    @XmlElement(name = "get_snapshot_listResult")
    protected boolean getSnapshotListResult;
    @XmlElement(name = "snapshot_list")
    protected String snapshotList;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the getSnapshotListResult property.
     * 
     */
    public boolean isGetSnapshotListResult() {
        return getSnapshotListResult;
    }

    /**
     * Sets the value of the getSnapshotListResult property.
     * 
     */
    public void setGetSnapshotListResult(boolean value) {
        this.getSnapshotListResult = value;
    }

    /**
     * Gets the value of the snapshotList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnapshotList() {
        return snapshotList;
    }

    /**
     * Sets the value of the snapshotList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnapshotList(String value) {
        this.snapshotList = value;
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
