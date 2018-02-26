
package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SyncTranInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SyncTranInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BranchID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="IsDBChanged" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="MaxTransactionCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="MaxTransferCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="StartID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="Type" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}SyncTranInfo.TransferType" minOccurs="0"/>
 *         &lt;element name="UTCOffset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SyncTranInfo", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "branchID",
    "isDBChanged",
    "maxTransactionCount",
    "maxTransferCount",
    "startID",
    "type",
    "utcOffset"
})
public class SyncTranInfo {

    @XmlElement(name = "BranchID")
    protected Long branchID;
    @XmlElement(name = "IsDBChanged")
    protected Integer isDBChanged;
    @XmlElement(name = "MaxTransactionCount")
    protected Integer maxTransactionCount;
    @XmlElement(name = "MaxTransferCount")
    protected Integer maxTransferCount;
    @XmlElement(name = "StartID")
    protected Long startID;
    @XmlElement(name = "Type")
    protected SyncTranInfoTransferType type;
    @XmlElement(name = "UTCOffset")
    protected Integer utcOffset;

    /**
     * Gets the value of the branchID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBranchID() {
        return branchID;
    }

    /**
     * Sets the value of the branchID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBranchID(Long value) {
        this.branchID = value;
    }

    /**
     * Gets the value of the isDBChanged property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsDBChanged() {
        return isDBChanged;
    }

    /**
     * Sets the value of the isDBChanged property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsDBChanged(Integer value) {
        this.isDBChanged = value;
    }

    /**
     * Gets the value of the maxTransactionCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxTransactionCount() {
        return maxTransactionCount;
    }

    /**
     * Sets the value of the maxTransactionCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxTransactionCount(Integer value) {
        this.maxTransactionCount = value;
    }

    /**
     * Gets the value of the maxTransferCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxTransferCount() {
        return maxTransferCount;
    }

    /**
     * Sets the value of the maxTransferCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxTransferCount(Integer value) {
        this.maxTransferCount = value;
    }

    /**
     * Gets the value of the startID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStartID() {
        return startID;
    }

    /**
     * Sets the value of the startID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStartID(Long value) {
        this.startID = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link SyncTranInfoTransferType }
     *     
     */
    public SyncTranInfoTransferType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link SyncTranInfoTransferType }
     *     
     */
    public void setType(SyncTranInfoTransferType value) {
        this.type = value;
    }

    /**
     * Gets the value of the utcOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUTCOffset() {
        return utcOffset;
    }

    /**
     * Sets the value of the utcOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUTCOffset(Integer value) {
        this.utcOffset = value;
    }

}
