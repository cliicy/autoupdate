
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
 *         &lt;element name="TransferDataResult" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="syncFileInfo" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}SyncFileType" minOccurs="0"/>
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
    "transferDataResult",
    "syncFileInfo"
})
@XmlRootElement(name = "TransferDataResponse")
public class TransferDataResponse {

    @XmlElement(name = "TransferDataResult", nillable = true)
    protected byte[] transferDataResult;
    @XmlElement(nillable = true)
    protected SyncFileType syncFileInfo;

    /**
     * Gets the value of the transferDataResult property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getTransferDataResult() {
        return transferDataResult;
    }

    /**
     * Sets the value of the transferDataResult property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setTransferDataResult(byte[] value) {
        this.transferDataResult = ((byte[]) value);
    }

    /**
     * Gets the value of the syncFileInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SyncFileType }
     *     
     */
    public SyncFileType getSyncFileInfo() {
        return syncFileInfo;
    }

    /**
     * Sets the value of the syncFileInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SyncFileType }
     *     
     */
    public void setSyncFileInfo(SyncFileType value) {
        this.syncFileInfo = value;
    }

}
