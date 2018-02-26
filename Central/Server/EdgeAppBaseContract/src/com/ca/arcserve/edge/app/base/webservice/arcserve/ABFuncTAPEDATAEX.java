
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_TAPEDATAEX complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_TAPEDATAEX">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataType" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" minOccurs="0"/>
 *         &lt;element name="FileDate" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="FileSize" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="FileSizeHigh" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="LongNameID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="PathID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="QFAChunkNum" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="QFAChunkOffset" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="SesStatus" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="SesType" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" minOccurs="0"/>
 *         &lt;element name="SessionID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="ShortNameID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="recordPos" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="tapedataFlag" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_TAPEDATAEX", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "dataType",
    "fileDate",
    "fileSize",
    "fileSizeHigh",
    "longNameID",
    "pathID",
    "qfaChunkNum",
    "qfaChunkOffset",
    "sesStatus",
    "sesType",
    "sessionID",
    "shortNameID",
    "recordPos",
    "tapedataFlag"
})
@XmlSeeAlso({
    ABFuncTapeDataEx2W.class
})
public class ABFuncTAPEDATAEX {

    @XmlElement(name = "DataType")
    @XmlSchemaType(name = "unsignedByte")
    protected Short dataType;
    @XmlElement(name = "FileDate")
    @XmlSchemaType(name = "unsignedInt")
    protected Long fileDate;
    @XmlElement(name = "FileSize")
    @XmlSchemaType(name = "unsignedInt")
    protected Long fileSize;
    @XmlElement(name = "FileSizeHigh")
    @XmlSchemaType(name = "unsignedInt")
    protected Long fileSizeHigh;
    @XmlElement(name = "LongNameID")
    @XmlSchemaType(name = "unsignedInt")
    protected Long longNameID;
    @XmlElement(name = "PathID")
    @XmlSchemaType(name = "unsignedInt")
    protected Long pathID;
    @XmlElement(name = "QFAChunkNum")
    @XmlSchemaType(name = "unsignedInt")
    protected Long qfaChunkNum;
    @XmlElement(name = "QFAChunkOffset")
    @XmlSchemaType(name = "unsignedInt")
    protected Long qfaChunkOffset;
    @XmlElement(name = "SesStatus")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer sesStatus;
    @XmlElement(name = "SesType")
    @XmlSchemaType(name = "unsignedByte")
    protected Short sesType;
    @XmlElement(name = "SessionID")
    @XmlSchemaType(name = "unsignedInt")
    protected Long sessionID;
    @XmlElement(name = "ShortNameID")
    @XmlSchemaType(name = "unsignedInt")
    protected Long shortNameID;
    @XmlSchemaType(name = "unsignedInt")
    protected Long recordPos;
    @XmlSchemaType(name = "unsignedInt")
    protected Long tapedataFlag;

    /**
     * Gets the value of the dataType property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setDataType(Short value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the fileDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFileDate() {
        return fileDate;
    }

    /**
     * Sets the value of the fileDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFileDate(Long value) {
        this.fileDate = value;
    }

    /**
     * Gets the value of the fileSize property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the value of the fileSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFileSize(Long value) {
        this.fileSize = value;
    }

    /**
     * Gets the value of the fileSizeHigh property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFileSizeHigh() {
        return fileSizeHigh;
    }

    /**
     * Sets the value of the fileSizeHigh property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFileSizeHigh(Long value) {
        this.fileSizeHigh = value;
    }

    /**
     * Gets the value of the longNameID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLongNameID() {
        return longNameID;
    }

    /**
     * Sets the value of the longNameID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLongNameID(Long value) {
        this.longNameID = value;
    }

    /**
     * Gets the value of the pathID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPathID() {
        return pathID;
    }

    /**
     * Sets the value of the pathID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPathID(Long value) {
        this.pathID = value;
    }

    /**
     * Gets the value of the qfaChunkNum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getQFAChunkNum() {
        return qfaChunkNum;
    }

    /**
     * Sets the value of the qfaChunkNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setQFAChunkNum(Long value) {
        this.qfaChunkNum = value;
    }

    /**
     * Gets the value of the qfaChunkOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getQFAChunkOffset() {
        return qfaChunkOffset;
    }

    /**
     * Sets the value of the qfaChunkOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setQFAChunkOffset(Long value) {
        this.qfaChunkOffset = value;
    }

    /**
     * Gets the value of the sesStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSesStatus() {
        return sesStatus;
    }

    /**
     * Sets the value of the sesStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSesStatus(Integer value) {
        this.sesStatus = value;
    }

    /**
     * Gets the value of the sesType property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSesType() {
        return sesType;
    }

    /**
     * Sets the value of the sesType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSesType(Short value) {
        this.sesType = value;
    }

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSessionID(Long value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the shortNameID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getShortNameID() {
        return shortNameID;
    }

    /**
     * Sets the value of the shortNameID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setShortNameID(Long value) {
        this.shortNameID = value;
    }

    /**
     * Gets the value of the recordPos property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRecordPos() {
        return recordPos;
    }

    /**
     * Sets the value of the recordPos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRecordPos(Long value) {
        this.recordPos = value;
    }

    /**
     * Gets the value of the tapedataFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTapedataFlag() {
        return tapedataFlag;
    }

    /**
     * Sets the value of the tapedataFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTapedataFlag(Long value) {
        this.tapedataFlag = value;
    }

}
