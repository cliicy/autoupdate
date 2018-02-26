
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_DetailRec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_DetailRec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datatype" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="detailflag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="fileattr" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="filedate" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="hsize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="longnameid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lsize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="namesp" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="pathid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="qfachunknum" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="qfachunkoffset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sesid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="shortnameid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="streamnum" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_DetailRec", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "datatype",
    "detailflag",
    "fileattr",
    "filedate",
    "hsize",
    "longnameid",
    "lsize",
    "namesp",
    "pathid",
    "qfachunknum",
    "qfachunkoffset",
    "sesid",
    "shortnameid",
    "streamnum"
})
public class ABFuncDetailRec {

    protected Short datatype;
    protected Integer detailflag;
    protected Integer fileattr;
    protected Integer filedate;
    protected Integer hsize;
    protected Integer longnameid;
    protected Integer lsize;
    protected Short namesp;
    protected Integer pathid;
    protected Integer qfachunknum;
    protected Integer qfachunkoffset;
    protected Integer sesid;
    protected Integer shortnameid;
    protected Integer streamnum;

    /**
     * Gets the value of the datatype property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getDatatype() {
        return datatype;
    }

    /**
     * Sets the value of the datatype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setDatatype(Short value) {
        this.datatype = value;
    }

    /**
     * Gets the value of the detailflag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDetailflag() {
        return detailflag;
    }

    /**
     * Sets the value of the detailflag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDetailflag(Integer value) {
        this.detailflag = value;
    }

    /**
     * Gets the value of the fileattr property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFileattr() {
        return fileattr;
    }

    /**
     * Sets the value of the fileattr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFileattr(Integer value) {
        this.fileattr = value;
    }

    /**
     * Gets the value of the filedate property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFiledate() {
        return filedate;
    }

    /**
     * Sets the value of the filedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFiledate(Integer value) {
        this.filedate = value;
    }

    /**
     * Gets the value of the hsize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHsize() {
        return hsize;
    }

    /**
     * Sets the value of the hsize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHsize(Integer value) {
        this.hsize = value;
    }

    /**
     * Gets the value of the longnameid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLongnameid() {
        return longnameid;
    }

    /**
     * Sets the value of the longnameid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLongnameid(Integer value) {
        this.longnameid = value;
    }

    /**
     * Gets the value of the lsize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLsize() {
        return lsize;
    }

    /**
     * Sets the value of the lsize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLsize(Integer value) {
        this.lsize = value;
    }

    /**
     * Gets the value of the namesp property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getNamesp() {
        return namesp;
    }

    /**
     * Sets the value of the namesp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setNamesp(Short value) {
        this.namesp = value;
    }

    /**
     * Gets the value of the pathid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPathid() {
        return pathid;
    }

    /**
     * Sets the value of the pathid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPathid(Integer value) {
        this.pathid = value;
    }

    /**
     * Gets the value of the qfachunknum property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQfachunknum() {
        return qfachunknum;
    }

    /**
     * Sets the value of the qfachunknum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQfachunknum(Integer value) {
        this.qfachunknum = value;
    }

    /**
     * Gets the value of the qfachunkoffset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQfachunkoffset() {
        return qfachunkoffset;
    }

    /**
     * Sets the value of the qfachunkoffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQfachunkoffset(Integer value) {
        this.qfachunkoffset = value;
    }

    /**
     * Gets the value of the sesid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSesid() {
        return sesid;
    }

    /**
     * Sets the value of the sesid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSesid(Integer value) {
        this.sesid = value;
    }

    /**
     * Gets the value of the shortnameid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getShortnameid() {
        return shortnameid;
    }

    /**
     * Sets the value of the shortnameid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setShortnameid(Integer value) {
        this.shortnameid = value;
    }

    /**
     * Gets the value of the streamnum property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStreamnum() {
        return streamnum;
    }

    /**
     * Sets the value of the streamnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStreamnum(Integer value) {
        this.streamnum = value;
    }

}
