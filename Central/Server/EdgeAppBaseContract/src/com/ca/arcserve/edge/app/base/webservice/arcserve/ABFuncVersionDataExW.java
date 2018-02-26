
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_VersionDataExW complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_VersionDataExW">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataType" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" minOccurs="0"/>
 *         &lt;element name="FileDate" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="FileSize" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="FileSizeHigh" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="Location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LongNameID" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="PathID" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="QFABlockNum" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="QFAChunkNum" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="QFAChunkOffset" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="RandomID" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="Reserved" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Reserved2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SeqNum" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="SesFlags" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="SesMethod" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="SesNum" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="SesNum_Old" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="SesStatus" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" minOccurs="0"/>
 *         &lt;element name="SesType" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" minOccurs="0"/>
 *         &lt;element name="SessionID" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="ShortNameID" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="SrcHostID" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="SrcPathID" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="StartTime" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="StreamNum" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" minOccurs="0"/>
 *         &lt;element name="SubSessNum" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="TapeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TotalFiles" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="TotalKBytes" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="recordPos" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="versiondataFlag" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="versiondata_pad" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_VersionDataExW", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "dataType",
    "fileDate",
    "fileSize",
    "fileSizeHigh",
    "location",
    "longNameID",
    "pathID",
    "qfaBlockNum",
    "qfaChunkNum",
    "qfaChunkOffset",
    "randomID",
    "reserved",
    "reserved2",
    "seqNum",
    "sesFlags",
    "sesMethod",
    "sesNum",
    "sesNumOld",
    "sesStatus",
    "sesType",
    "sessionID",
    "shortNameID",
    "srcHostID",
    "srcPathID",
    "startTime",
    "streamNum",
    "subSessNum",
    "tapeName",
    "totalFiles",
    "totalKBytes",
    "recordPos",
    "versiondataFlag",
    "versiondataPad"
})
public class ABFuncVersionDataExW {

    @XmlElement(name = "DataType")
    @XmlSchemaType(name = "unsignedByte")
    protected Short dataType;
    @XmlElement(name = "FileDate")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger fileDate;
    @XmlElement(name = "FileSize")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger fileSize;
    @XmlElement(name = "FileSizeHigh")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger fileSizeHigh;
    @XmlElementRef(name = "Location", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> location;
    @XmlElement(name = "LongNameID")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger longNameID;
    @XmlElement(name = "PathID")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger pathID;
    @XmlElement(name = "QFABlockNum")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger qfaBlockNum;
    @XmlElement(name = "QFAChunkNum")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger qfaChunkNum;
    @XmlElement(name = "QFAChunkOffset")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger qfaChunkOffset;
    @XmlElement(name = "RandomID")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer randomID;
    @XmlElementRef(name = "Reserved", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> reserved;
    @XmlElementRef(name = "Reserved2", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> reserved2;
    @XmlElement(name = "SeqNum")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer seqNum;
    @XmlElement(name = "SesFlags")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger sesFlags;
    @XmlElement(name = "SesMethod")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer sesMethod;
    @XmlElement(name = "SesNum")
    protected Long sesNum;
    @XmlElement(name = "SesNum_Old")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer sesNumOld;
    @XmlElement(name = "SesStatus")
    @XmlSchemaType(name = "unsignedByte")
    protected Short sesStatus;
    @XmlElement(name = "SesType")
    @XmlSchemaType(name = "unsignedByte")
    protected Short sesType;
    @XmlElement(name = "SessionID")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger sessionID;
    @XmlElement(name = "ShortNameID")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger shortNameID;
    @XmlElement(name = "SrcHostID")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger srcHostID;
    @XmlElement(name = "SrcPathID")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger srcPathID;
    @XmlElement(name = "StartTime")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger startTime;
    @XmlElement(name = "StreamNum")
    @XmlSchemaType(name = "unsignedByte")
    protected Short streamNum;
    @XmlElement(name = "SubSessNum")
    protected Long subSessNum;
    @XmlElementRef(name = "TapeName", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> tapeName;
    @XmlElement(name = "TotalFiles")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger totalFiles;
    @XmlElement(name = "TotalKBytes")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger totalKBytes;
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger recordPos;
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger versiondataFlag;
    @XmlElement(name = "versiondata_pad")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer versiondataPad;

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
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFileDate() {
        return fileDate;
    }

    /**
     * Sets the value of the fileDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFileDate(BigInteger value) {
        this.fileDate = value;
    }

    /**
     * Gets the value of the fileSize property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFileSize() {
        return fileSize;
    }

    /**
     * Sets the value of the fileSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFileSize(BigInteger value) {
        this.fileSize = value;
    }

    /**
     * Gets the value of the fileSizeHigh property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFileSizeHigh() {
        return fileSizeHigh;
    }

    /**
     * Sets the value of the fileSizeHigh property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFileSizeHigh(BigInteger value) {
        this.fileSizeHigh = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLocation(JAXBElement<String> value) {
        this.location = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the longNameID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLongNameID() {
        return longNameID;
    }

    /**
     * Sets the value of the longNameID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLongNameID(BigInteger value) {
        this.longNameID = value;
    }

    /**
     * Gets the value of the pathID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPathID() {
        return pathID;
    }

    /**
     * Sets the value of the pathID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPathID(BigInteger value) {
        this.pathID = value;
    }

    /**
     * Gets the value of the qfaBlockNum property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQFABlockNum() {
        return qfaBlockNum;
    }

    /**
     * Sets the value of the qfaBlockNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQFABlockNum(BigInteger value) {
        this.qfaBlockNum = value;
    }

    /**
     * Gets the value of the qfaChunkNum property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQFAChunkNum() {
        return qfaChunkNum;
    }

    /**
     * Sets the value of the qfaChunkNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQFAChunkNum(BigInteger value) {
        this.qfaChunkNum = value;
    }

    /**
     * Gets the value of the qfaChunkOffset property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQFAChunkOffset() {
        return qfaChunkOffset;
    }

    /**
     * Sets the value of the qfaChunkOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQFAChunkOffset(BigInteger value) {
        this.qfaChunkOffset = value;
    }

    /**
     * Gets the value of the randomID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRandomID() {
        return randomID;
    }

    /**
     * Sets the value of the randomID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRandomID(Integer value) {
        this.randomID = value;
    }

    /**
     * Gets the value of the reserved property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReserved() {
        return reserved;
    }

    /**
     * Sets the value of the reserved property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReserved(JAXBElement<String> value) {
        this.reserved = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the reserved2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReserved2() {
        return reserved2;
    }

    /**
     * Sets the value of the reserved2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReserved2(JAXBElement<String> value) {
        this.reserved2 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the seqNum property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSeqNum() {
        return seqNum;
    }

    /**
     * Sets the value of the seqNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSeqNum(Integer value) {
        this.seqNum = value;
    }

    /**
     * Gets the value of the sesFlags property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSesFlags() {
        return sesFlags;
    }

    /**
     * Sets the value of the sesFlags property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSesFlags(BigInteger value) {
        this.sesFlags = value;
    }

    /**
     * Gets the value of the sesMethod property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSesMethod() {
        return sesMethod;
    }

    /**
     * Sets the value of the sesMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSesMethod(Integer value) {
        this.sesMethod = value;
    }

    /**
     * Gets the value of the sesNum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSesNum() {
        return sesNum;
    }

    /**
     * Sets the value of the sesNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSesNum(Long value) {
        this.sesNum = value;
    }

    /**
     * Gets the value of the sesNumOld property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSesNumOld() {
        return sesNumOld;
    }

    /**
     * Sets the value of the sesNumOld property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSesNumOld(Integer value) {
        this.sesNumOld = value;
    }

    /**
     * Gets the value of the sesStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSesStatus() {
        return sesStatus;
    }

    /**
     * Sets the value of the sesStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSesStatus(Short value) {
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
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSessionID(BigInteger value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the shortNameID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getShortNameID() {
        return shortNameID;
    }

    /**
     * Sets the value of the shortNameID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setShortNameID(BigInteger value) {
        this.shortNameID = value;
    }

    /**
     * Gets the value of the srcHostID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSrcHostID() {
        return srcHostID;
    }

    /**
     * Sets the value of the srcHostID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSrcHostID(BigInteger value) {
        this.srcHostID = value;
    }

    /**
     * Gets the value of the srcPathID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSrcPathID() {
        return srcPathID;
    }

    /**
     * Sets the value of the srcPathID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSrcPathID(BigInteger value) {
        this.srcPathID = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStartTime(BigInteger value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the streamNum property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getStreamNum() {
        return streamNum;
    }

    /**
     * Sets the value of the streamNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setStreamNum(Short value) {
        this.streamNum = value;
    }

    /**
     * Gets the value of the subSessNum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSubSessNum() {
        return subSessNum;
    }

    /**
     * Sets the value of the subSessNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSubSessNum(Long value) {
        this.subSessNum = value;
    }

    /**
     * Gets the value of the tapeName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTapeName() {
        return tapeName;
    }

    /**
     * Sets the value of the tapeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTapeName(JAXBElement<String> value) {
        this.tapeName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the totalFiles property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalFiles() {
        return totalFiles;
    }

    /**
     * Sets the value of the totalFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalFiles(BigInteger value) {
        this.totalFiles = value;
    }

    /**
     * Gets the value of the totalKBytes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalKBytes() {
        return totalKBytes;
    }

    /**
     * Sets the value of the totalKBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalKBytes(BigInteger value) {
        this.totalKBytes = value;
    }

    /**
     * Gets the value of the recordPos property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRecordPos() {
        return recordPos;
    }

    /**
     * Sets the value of the recordPos property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRecordPos(BigInteger value) {
        this.recordPos = value;
    }

    /**
     * Gets the value of the versiondataFlag property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVersiondataFlag() {
        return versiondataFlag;
    }

    /**
     * Sets the value of the versiondataFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVersiondataFlag(BigInteger value) {
        this.versiondataFlag = value;
    }

    /**
     * Gets the value of the versiondataPad property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVersiondataPad() {
        return versiondataPad;
    }

    /**
     * Sets the value of the versiondataPad property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVersiondataPad(Integer value) {
        this.versiondataPad = value;
    }

}
