
package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SyncFileType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SyncFileType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MaxSendSize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ReadSize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="StartOffset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="StrFileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SyncFileType", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "maxSendSize",
    "readSize",
    "startOffset",
    "strFileName"
})
public class SyncFileType {

    @XmlElement(name = "MaxSendSize")
    protected Integer maxSendSize;
    @XmlElement(name = "ReadSize")
    protected Integer readSize;
    @XmlElement(name = "StartOffset")
    protected Integer startOffset;
    @XmlElement(name = "StrFileName", nillable = true)
    protected String strFileName;

    /**
     * Gets the value of the maxSendSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxSendSize() {
        return maxSendSize;
    }

    /**
     * Sets the value of the maxSendSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxSendSize(Integer value) {
        this.maxSendSize = value;
    }

    /**
     * Gets the value of the readSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getReadSize() {
        return readSize;
    }

    /**
     * Sets the value of the readSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setReadSize(Integer value) {
        this.readSize = value;
    }

    /**
     * Gets the value of the startOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStartOffset() {
        return startOffset;
    }

    /**
     * Sets the value of the startOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStartOffset(Integer value) {
        this.startOffset = value;
    }

    /**
     * Gets the value of the strFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrFileName() {
        return strFileName;
    }

    /**
     * Sets the value of the strFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrFileName(String value) {
        this.strFileName = value;
    }

}
