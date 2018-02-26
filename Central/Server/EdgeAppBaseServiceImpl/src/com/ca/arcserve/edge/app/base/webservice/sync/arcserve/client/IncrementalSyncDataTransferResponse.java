
package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client;

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
 *         &lt;element name="IncrementalSyncDataTransferResult" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="syncInfo" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}SyncTranInfo" minOccurs="0"/>
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
    "incrementalSyncDataTransferResult",
    "syncInfo"
})
@XmlRootElement(name = "IncrementalSyncDataTransferResponse")
public class IncrementalSyncDataTransferResponse {

    @XmlElement(name = "IncrementalSyncDataTransferResult", nillable = true)
    protected byte[] incrementalSyncDataTransferResult;
    @XmlElement(nillable = true)
    protected SyncTranInfo syncInfo;

    /**
     * Gets the value of the incrementalSyncDataTransferResult property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getIncrementalSyncDataTransferResult() {
        return incrementalSyncDataTransferResult;
    }

    /**
     * Sets the value of the incrementalSyncDataTransferResult property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setIncrementalSyncDataTransferResult(byte[] value) {
        this.incrementalSyncDataTransferResult = ((byte[]) value);
    }

    /**
     * Gets the value of the syncInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SyncTranInfo }
     *     
     */
    public SyncTranInfo getSyncInfo() {
        return syncInfo;
    }

    /**
     * Sets the value of the syncInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SyncTranInfo }
     *     
     */
    public void setSyncInfo(SyncTranInfo value) {
        this.syncInfo = value;
    }

}
