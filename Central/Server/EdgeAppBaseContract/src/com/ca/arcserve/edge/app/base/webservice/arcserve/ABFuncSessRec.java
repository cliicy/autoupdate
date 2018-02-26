
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_SessRec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_SessRec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="endtime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="fsname_length" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="jobid" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ownerid" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="qfablocknum" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="reserved2" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="sesflags" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="sesmethod" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="sesnum" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="sesnum_old" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="sestype" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="srchostid" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="srcpathid" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="starttime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="streamnum" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="tapeid" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="tapeseq_end" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="totalfiles" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="totalkb" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="totalmissed" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_SessRec", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "endtime",
    "fsnameLength",
    "id",
    "jobid",
    "ownerid",
    "qfablocknum",
    "reserved2",
    "sesflags",
    "sesmethod",
    "sesnum",
    "sesnumOld",
    "sestype",
    "srchostid",
    "srcpathid",
    "starttime",
    "status",
    "streamnum",
    "tapeid",
    "tapeseqEnd",
    "totalfiles",
    "totalkb",
    "totalmissed"
})
public class ABFuncSessRec {

    protected Long endtime;
    @XmlElement(name = "fsname_length")
    protected Short fsnameLength;
    protected Long id;
    protected Long jobid;
    protected Long ownerid;
    protected Long qfablocknum;
    protected Short reserved2;
    protected Long sesflags;
    protected Short sesmethod;
    protected Long sesnum;
    @XmlElement(name = "sesnum_old")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer sesnumOld;
    protected Short sestype;
    protected Long srchostid;
    protected Long srcpathid;
    protected Long starttime;
    protected Short status;
    protected Short streamnum;
    protected Long tapeid;
    @XmlElement(name = "tapeseq_end")
    protected Short tapeseqEnd;
    protected Long totalfiles;
    protected Long totalkb;
    protected Long totalmissed;

    /**
     * Gets the value of the endtime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEndtime() {
        return endtime;
    }

    /**
     * Sets the value of the endtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEndtime(Long value) {
        this.endtime = value;
    }

    /**
     * Gets the value of the fsnameLength property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getFsnameLength() {
        return fsnameLength;
    }

    /**
     * Sets the value of the fsnameLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setFsnameLength(Short value) {
        this.fsnameLength = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the jobid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getJobid() {
        return jobid;
    }

    /**
     * Sets the value of the jobid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setJobid(Long value) {
        this.jobid = value;
    }

    /**
     * Gets the value of the ownerid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOwnerid() {
        return ownerid;
    }

    /**
     * Sets the value of the ownerid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOwnerid(Long value) {
        this.ownerid = value;
    }

    /**
     * Gets the value of the qfablocknum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getQfablocknum() {
        return qfablocknum;
    }

    /**
     * Sets the value of the qfablocknum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setQfablocknum(Long value) {
        this.qfablocknum = value;
    }

    /**
     * Gets the value of the reserved2 property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getReserved2() {
        return reserved2;
    }

    /**
     * Sets the value of the reserved2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setReserved2(Short value) {
        this.reserved2 = value;
    }

    /**
     * Gets the value of the sesflags property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSesflags() {
        return sesflags;
    }

    /**
     * Sets the value of the sesflags property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSesflags(Long value) {
        this.sesflags = value;
    }

    /**
     * Gets the value of the sesmethod property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSesmethod() {
        return sesmethod;
    }

    /**
     * Sets the value of the sesmethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSesmethod(Short value) {
        this.sesmethod = value;
    }

    /**
     * Gets the value of the sesnum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSesnum() {
        return sesnum;
    }

    /**
     * Sets the value of the sesnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSesnum(Long value) {
        this.sesnum = value;
    }

    /**
     * Gets the value of the sesnumOld property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSesnumOld() {
        return sesnumOld;
    }

    /**
     * Sets the value of the sesnumOld property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSesnumOld(Integer value) {
        this.sesnumOld = value;
    }

    /**
     * Gets the value of the sestype property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSestype() {
        return sestype;
    }

    /**
     * Sets the value of the sestype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSestype(Short value) {
        this.sestype = value;
    }

    /**
     * Gets the value of the srchostid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSrchostid() {
        return srchostid;
    }

    /**
     * Sets the value of the srchostid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSrchostid(Long value) {
        this.srchostid = value;
    }

    /**
     * Gets the value of the srcpathid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSrcpathid() {
        return srcpathid;
    }

    /**
     * Sets the value of the srcpathid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSrcpathid(Long value) {
        this.srcpathid = value;
    }

    /**
     * Gets the value of the starttime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStarttime() {
        return starttime;
    }

    /**
     * Sets the value of the starttime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStarttime(Long value) {
        this.starttime = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setStatus(Short value) {
        this.status = value;
    }

    /**
     * Gets the value of the streamnum property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getStreamnum() {
        return streamnum;
    }

    /**
     * Sets the value of the streamnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setStreamnum(Short value) {
        this.streamnum = value;
    }

    /**
     * Gets the value of the tapeid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTapeid() {
        return tapeid;
    }

    /**
     * Sets the value of the tapeid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTapeid(Long value) {
        this.tapeid = value;
    }

    /**
     * Gets the value of the tapeseqEnd property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getTapeseqEnd() {
        return tapeseqEnd;
    }

    /**
     * Sets the value of the tapeseqEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setTapeseqEnd(Short value) {
        this.tapeseqEnd = value;
    }

    /**
     * Gets the value of the totalfiles property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalfiles() {
        return totalfiles;
    }

    /**
     * Sets the value of the totalfiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalfiles(Long value) {
        this.totalfiles = value;
    }

    /**
     * Gets the value of the totalkb property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalkb() {
        return totalkb;
    }

    /**
     * Sets the value of the totalkb property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalkb(Long value) {
        this.totalkb = value;
    }

    /**
     * Gets the value of the totalmissed property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalmissed() {
        return totalmissed;
    }

    /**
     * Sets the value of the totalmissed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalmissed(Long value) {
        this.totalmissed = value;
    }

}
