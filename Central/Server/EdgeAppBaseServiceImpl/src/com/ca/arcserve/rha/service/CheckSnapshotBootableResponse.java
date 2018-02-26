
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
 *         &lt;element name="check_snapshot_bootableResult" type="{http://www.w3.org/2001/XMLSchema}short"/>
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
    "checkSnapshotBootableResult",
    "snapshotList",
    "whyNotReason"
})
@XmlRootElement(name = "check_snapshot_bootableResponse")
public class CheckSnapshotBootableResponse {

    @XmlElement(name = "check_snapshot_bootableResult")
    protected short checkSnapshotBootableResult;
    @XmlElement(name = "snapshot_list")
    protected String snapshotList;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the checkSnapshotBootableResult property.
     * 
     */
    public short getCheckSnapshotBootableResult() {
        return checkSnapshotBootableResult;
    }

    /**
     * Sets the value of the checkSnapshotBootableResult property.
     * 
     */
    public void setCheckSnapshotBootableResult(short value) {
        this.checkSnapshotBootableResult = value;
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
