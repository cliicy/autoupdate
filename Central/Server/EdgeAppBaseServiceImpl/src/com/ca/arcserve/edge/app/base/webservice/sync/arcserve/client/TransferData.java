
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
 *         &lt;element name="strSessionNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "strSessionNo",
    "syncFileInfo"
})
@XmlRootElement(name = "TransferData")
public class TransferData {

    @XmlElement(nillable = true)
    protected String strSessionNo;
    @XmlElement(nillable = true)
    protected SyncFileType syncFileInfo;

    /**
     * Gets the value of the strSessionNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrSessionNo() {
        return strSessionNo;
    }

    /**
     * Sets the value of the strSessionNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrSessionNo(String value) {
        this.strSessionNo = value;
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
