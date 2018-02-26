
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_TapeRecW complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_TapeRecW">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MBOsavesetno" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="backuptype" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="blocksize" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="curkbwritten" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="curmediaerr" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="curraidtapes" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="cursrerr" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="curswerr" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="curusagetime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="densitycode" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="destroyed" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="expiredate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="firstformat" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="formatcode" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="lastformat" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="lastread" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="lastwrite" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="locatid" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="locatsentdate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="locatstatus" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="mediacode" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="overwritepass" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="poolname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="poolsetstatus" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="randomid" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="seqnum" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="serialnum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tapeflag" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="tapename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tapestatus" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="tapetype" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="ttlkbwritten" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ttlmediaerr" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ttlraidtapes" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="ttlreadpass" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ttlsrerr" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ttlswerr" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ttlusagetime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ttlwritepass" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="udepass" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_TapeRecW", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "mbOsavesetno",
    "backuptype",
    "blocksize",
    "curkbwritten",
    "curmediaerr",
    "curraidtapes",
    "cursrerr",
    "curswerr",
    "curusagetime",
    "densitycode",
    "destroyed",
    "expiredate",
    "firstformat",
    "formatcode",
    "id",
    "lastformat",
    "lastread",
    "lastwrite",
    "locatid",
    "locatsentdate",
    "locatstatus",
    "mediacode",
    "overwritepass",
    "poolname",
    "poolsetstatus",
    "randomid",
    "seqnum",
    "serialnum",
    "tapeflag",
    "tapename",
    "tapestatus",
    "tapetype",
    "ttlkbwritten",
    "ttlmediaerr",
    "ttlraidtapes",
    "ttlreadpass",
    "ttlsrerr",
    "ttlswerr",
    "ttlusagetime",
    "ttlwritepass",
    "udepass"
})
public class ABFuncTapeRecW {

    @XmlElement(name = "MBOsavesetno")
    protected Long mbOsavesetno;
    protected Short backuptype;
    protected Long blocksize;
    protected Long curkbwritten;
    protected Long curmediaerr;
    protected Short curraidtapes;
    protected Long cursrerr;
    protected Long curswerr;
    protected Long curusagetime;
    protected Short densitycode;
    protected Long destroyed;
    protected Long expiredate;
    protected Long firstformat;
    protected Short formatcode;
    protected Long id;
    protected Long lastformat;
    protected Long lastread;
    protected Long lastwrite;
    protected Long locatid;
    protected Long locatsentdate;
    protected Short locatstatus;
    protected Short mediacode;
    protected Long overwritepass;
    @XmlElementRef(name = "poolname", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> poolname;
    protected Short poolsetstatus;
    protected Short randomid;
    protected Short seqnum;
    @XmlElementRef(name = "serialnum", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> serialnum;
    protected Long tapeflag;
    @XmlElementRef(name = "tapename", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> tapename;
    protected Short tapestatus;
    protected Short tapetype;
    protected Long ttlkbwritten;
    protected Long ttlmediaerr;
    protected Short ttlraidtapes;
    protected Long ttlreadpass;
    protected Long ttlsrerr;
    protected Long ttlswerr;
    protected Long ttlusagetime;
    protected Long ttlwritepass;
    protected Long udepass;

    /**
     * Gets the value of the mbOsavesetno property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMBOsavesetno() {
        return mbOsavesetno;
    }

    /**
     * Sets the value of the mbOsavesetno property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMBOsavesetno(Long value) {
        this.mbOsavesetno = value;
    }

    /**
     * Gets the value of the backuptype property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getBackuptype() {
        return backuptype;
    }

    /**
     * Sets the value of the backuptype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setBackuptype(Short value) {
        this.backuptype = value;
    }

    /**
     * Gets the value of the blocksize property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBlocksize() {
        return blocksize;
    }

    /**
     * Sets the value of the blocksize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBlocksize(Long value) {
        this.blocksize = value;
    }

    /**
     * Gets the value of the curkbwritten property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCurkbwritten() {
        return curkbwritten;
    }

    /**
     * Sets the value of the curkbwritten property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCurkbwritten(Long value) {
        this.curkbwritten = value;
    }

    /**
     * Gets the value of the curmediaerr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCurmediaerr() {
        return curmediaerr;
    }

    /**
     * Sets the value of the curmediaerr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCurmediaerr(Long value) {
        this.curmediaerr = value;
    }

    /**
     * Gets the value of the curraidtapes property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getCurraidtapes() {
        return curraidtapes;
    }

    /**
     * Sets the value of the curraidtapes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setCurraidtapes(Short value) {
        this.curraidtapes = value;
    }

    /**
     * Gets the value of the cursrerr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCursrerr() {
        return cursrerr;
    }

    /**
     * Sets the value of the cursrerr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCursrerr(Long value) {
        this.cursrerr = value;
    }

    /**
     * Gets the value of the curswerr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCurswerr() {
        return curswerr;
    }

    /**
     * Sets the value of the curswerr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCurswerr(Long value) {
        this.curswerr = value;
    }

    /**
     * Gets the value of the curusagetime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCurusagetime() {
        return curusagetime;
    }

    /**
     * Sets the value of the curusagetime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCurusagetime(Long value) {
        this.curusagetime = value;
    }

    /**
     * Gets the value of the densitycode property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getDensitycode() {
        return densitycode;
    }

    /**
     * Sets the value of the densitycode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setDensitycode(Short value) {
        this.densitycode = value;
    }

    /**
     * Gets the value of the destroyed property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDestroyed() {
        return destroyed;
    }

    /**
     * Sets the value of the destroyed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDestroyed(Long value) {
        this.destroyed = value;
    }

    /**
     * Gets the value of the expiredate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getExpiredate() {
        return expiredate;
    }

    /**
     * Sets the value of the expiredate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setExpiredate(Long value) {
        this.expiredate = value;
    }

    /**
     * Gets the value of the firstformat property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFirstformat() {
        return firstformat;
    }

    /**
     * Sets the value of the firstformat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFirstformat(Long value) {
        this.firstformat = value;
    }

    /**
     * Gets the value of the formatcode property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getFormatcode() {
        return formatcode;
    }

    /**
     * Sets the value of the formatcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setFormatcode(Short value) {
        this.formatcode = value;
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
     * Gets the value of the lastformat property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLastformat() {
        return lastformat;
    }

    /**
     * Sets the value of the lastformat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLastformat(Long value) {
        this.lastformat = value;
    }

    /**
     * Gets the value of the lastread property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLastread() {
        return lastread;
    }

    /**
     * Sets the value of the lastread property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLastread(Long value) {
        this.lastread = value;
    }

    /**
     * Gets the value of the lastwrite property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLastwrite() {
        return lastwrite;
    }

    /**
     * Sets the value of the lastwrite property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLastwrite(Long value) {
        this.lastwrite = value;
    }

    /**
     * Gets the value of the locatid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLocatid() {
        return locatid;
    }

    /**
     * Sets the value of the locatid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLocatid(Long value) {
        this.locatid = value;
    }

    /**
     * Gets the value of the locatsentdate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLocatsentdate() {
        return locatsentdate;
    }

    /**
     * Sets the value of the locatsentdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLocatsentdate(Long value) {
        this.locatsentdate = value;
    }

    /**
     * Gets the value of the locatstatus property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getLocatstatus() {
        return locatstatus;
    }

    /**
     * Sets the value of the locatstatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setLocatstatus(Short value) {
        this.locatstatus = value;
    }

    /**
     * Gets the value of the mediacode property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getMediacode() {
        return mediacode;
    }

    /**
     * Sets the value of the mediacode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setMediacode(Short value) {
        this.mediacode = value;
    }

    /**
     * Gets the value of the overwritepass property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOverwritepass() {
        return overwritepass;
    }

    /**
     * Sets the value of the overwritepass property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOverwritepass(Long value) {
        this.overwritepass = value;
    }

    /**
     * Gets the value of the poolname property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPoolname() {
        return poolname;
    }

    /**
     * Sets the value of the poolname property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPoolname(JAXBElement<String> value) {
        this.poolname = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the poolsetstatus property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getPoolsetstatus() {
        return poolsetstatus;
    }

    /**
     * Sets the value of the poolsetstatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setPoolsetstatus(Short value) {
        this.poolsetstatus = value;
    }

    /**
     * Gets the value of the randomid property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getRandomid() {
        return randomid;
    }

    /**
     * Sets the value of the randomid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setRandomid(Short value) {
        this.randomid = value;
    }

    /**
     * Gets the value of the seqnum property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSeqnum() {
        return seqnum;
    }

    /**
     * Sets the value of the seqnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSeqnum(Short value) {
        this.seqnum = value;
    }

    /**
     * Gets the value of the serialnum property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSerialnum() {
        return serialnum;
    }

    /**
     * Sets the value of the serialnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSerialnum(JAXBElement<String> value) {
        this.serialnum = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the tapeflag property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTapeflag() {
        return tapeflag;
    }

    /**
     * Sets the value of the tapeflag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTapeflag(Long value) {
        this.tapeflag = value;
    }

    /**
     * Gets the value of the tapename property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTapename() {
        return tapename;
    }

    /**
     * Sets the value of the tapename property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTapename(JAXBElement<String> value) {
        this.tapename = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the tapestatus property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getTapestatus() {
        return tapestatus;
    }

    /**
     * Sets the value of the tapestatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setTapestatus(Short value) {
        this.tapestatus = value;
    }

    /**
     * Gets the value of the tapetype property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getTapetype() {
        return tapetype;
    }

    /**
     * Sets the value of the tapetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setTapetype(Short value) {
        this.tapetype = value;
    }

    /**
     * Gets the value of the ttlkbwritten property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTtlkbwritten() {
        return ttlkbwritten;
    }

    /**
     * Sets the value of the ttlkbwritten property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTtlkbwritten(Long value) {
        this.ttlkbwritten = value;
    }

    /**
     * Gets the value of the ttlmediaerr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTtlmediaerr() {
        return ttlmediaerr;
    }

    /**
     * Sets the value of the ttlmediaerr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTtlmediaerr(Long value) {
        this.ttlmediaerr = value;
    }

    /**
     * Gets the value of the ttlraidtapes property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getTtlraidtapes() {
        return ttlraidtapes;
    }

    /**
     * Sets the value of the ttlraidtapes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setTtlraidtapes(Short value) {
        this.ttlraidtapes = value;
    }

    /**
     * Gets the value of the ttlreadpass property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTtlreadpass() {
        return ttlreadpass;
    }

    /**
     * Sets the value of the ttlreadpass property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTtlreadpass(Long value) {
        this.ttlreadpass = value;
    }

    /**
     * Gets the value of the ttlsrerr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTtlsrerr() {
        return ttlsrerr;
    }

    /**
     * Sets the value of the ttlsrerr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTtlsrerr(Long value) {
        this.ttlsrerr = value;
    }

    /**
     * Gets the value of the ttlswerr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTtlswerr() {
        return ttlswerr;
    }

    /**
     * Sets the value of the ttlswerr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTtlswerr(Long value) {
        this.ttlswerr = value;
    }

    /**
     * Gets the value of the ttlusagetime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTtlusagetime() {
        return ttlusagetime;
    }

    /**
     * Sets the value of the ttlusagetime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTtlusagetime(Long value) {
        this.ttlusagetime = value;
    }

    /**
     * Gets the value of the ttlwritepass property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTtlwritepass() {
        return ttlwritepass;
    }

    /**
     * Sets the value of the ttlwritepass property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTtlwritepass(Long value) {
        this.ttlwritepass = value;
    }

    /**
     * Gets the value of the udepass property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUdepass() {
        return udepass;
    }

    /**
     * Sets the value of the udepass property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUdepass(Long value) {
        this.udepass = value;
    }

}
