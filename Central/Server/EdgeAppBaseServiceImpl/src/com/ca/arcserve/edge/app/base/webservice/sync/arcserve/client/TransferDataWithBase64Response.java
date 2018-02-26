
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
 *         &lt;element name="TransferDataWithBase64Result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "transferDataWithBase64Result",
    "syncFileInfo"
})
@XmlRootElement(name = "TransferDataWithBase64Response")
public class TransferDataWithBase64Response {

    @XmlElement(name = "TransferDataWithBase64Result", nillable = true)
    protected String transferDataWithBase64Result;
    @XmlElement(nillable = true)
    protected SyncFileType syncFileInfo;

    /**
     * Gets the value of the transferDataWithBase64Result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferDataWithBase64Result() {
        return transferDataWithBase64Result;
    }

    /**
     * Sets the value of the transferDataWithBase64Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferDataWithBase64Result(String value) {
        this.transferDataWithBase64Result = value;
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
