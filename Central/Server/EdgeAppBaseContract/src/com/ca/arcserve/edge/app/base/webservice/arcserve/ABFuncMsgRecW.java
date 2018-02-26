
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_MsgRecW complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_MsgRecW">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cp_flag" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="hobjbody" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="hobjparentid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="hobjselfid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="hobjsize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lobjbody" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lobjparentid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lobjselfid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lobjsize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="objaux" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="objdate" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="objflags" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="objinfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="objname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="objtype" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="qfachunknum" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="qfachunkoffset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="reserved" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sessid" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_MsgRecW", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "cpFlag",
    "hobjbody",
    "hobjparentid",
    "hobjselfid",
    "hobjsize",
    "lobjbody",
    "lobjparentid",
    "lobjselfid",
    "lobjsize",
    "objaux",
    "objdate",
    "objflags",
    "objinfo",
    "objname",
    "objtype",
    "qfachunknum",
    "qfachunkoffset",
    "reserved",
    "sessid"
})
public class ABFuncMsgRecW {

    @XmlElement(name = "cp_flag")
    @XmlSchemaType(name = "unsignedInt")
    protected Long cpFlag;
    protected Integer hobjbody;
    protected Integer hobjparentid;
    protected Integer hobjselfid;
    protected Integer hobjsize;
    protected Integer lobjbody;
    protected Integer lobjparentid;
    protected Integer lobjselfid;
    protected Integer lobjsize;
    protected Integer objaux;
    protected Integer objdate;
    protected Integer objflags;
    @XmlElementRef(name = "objinfo", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> objinfo;
    @XmlElementRef(name = "objname", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> objname;
    protected Integer objtype;
    protected Integer qfachunknum;
    protected Integer qfachunkoffset;
    protected Integer reserved;
    protected Integer sessid;

    /**
     * Gets the value of the cpFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCpFlag() {
        return cpFlag;
    }

    /**
     * Sets the value of the cpFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCpFlag(Long value) {
        this.cpFlag = value;
    }

    /**
     * Gets the value of the hobjbody property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHobjbody() {
        return hobjbody;
    }

    /**
     * Sets the value of the hobjbody property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHobjbody(Integer value) {
        this.hobjbody = value;
    }

    /**
     * Gets the value of the hobjparentid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHobjparentid() {
        return hobjparentid;
    }

    /**
     * Sets the value of the hobjparentid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHobjparentid(Integer value) {
        this.hobjparentid = value;
    }

    /**
     * Gets the value of the hobjselfid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHobjselfid() {
        return hobjselfid;
    }

    /**
     * Sets the value of the hobjselfid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHobjselfid(Integer value) {
        this.hobjselfid = value;
    }

    /**
     * Gets the value of the hobjsize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHobjsize() {
        return hobjsize;
    }

    /**
     * Sets the value of the hobjsize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHobjsize(Integer value) {
        this.hobjsize = value;
    }

    /**
     * Gets the value of the lobjbody property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLobjbody() {
        return lobjbody;
    }

    /**
     * Sets the value of the lobjbody property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLobjbody(Integer value) {
        this.lobjbody = value;
    }

    /**
     * Gets the value of the lobjparentid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLobjparentid() {
        return lobjparentid;
    }

    /**
     * Sets the value of the lobjparentid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLobjparentid(Integer value) {
        this.lobjparentid = value;
    }

    /**
     * Gets the value of the lobjselfid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLobjselfid() {
        return lobjselfid;
    }

    /**
     * Sets the value of the lobjselfid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLobjselfid(Integer value) {
        this.lobjselfid = value;
    }

    /**
     * Gets the value of the lobjsize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLobjsize() {
        return lobjsize;
    }

    /**
     * Sets the value of the lobjsize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLobjsize(Integer value) {
        this.lobjsize = value;
    }

    /**
     * Gets the value of the objaux property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getObjaux() {
        return objaux;
    }

    /**
     * Sets the value of the objaux property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setObjaux(Integer value) {
        this.objaux = value;
    }

    /**
     * Gets the value of the objdate property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getObjdate() {
        return objdate;
    }

    /**
     * Sets the value of the objdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setObjdate(Integer value) {
        this.objdate = value;
    }

    /**
     * Gets the value of the objflags property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getObjflags() {
        return objflags;
    }

    /**
     * Sets the value of the objflags property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setObjflags(Integer value) {
        this.objflags = value;
    }

    /**
     * Gets the value of the objinfo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getObjinfo() {
        return objinfo;
    }

    /**
     * Sets the value of the objinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setObjinfo(JAXBElement<String> value) {
        this.objinfo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the objname property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getObjname() {
        return objname;
    }

    /**
     * Sets the value of the objname property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setObjname(JAXBElement<String> value) {
        this.objname = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the objtype property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getObjtype() {
        return objtype;
    }

    /**
     * Sets the value of the objtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setObjtype(Integer value) {
        this.objtype = value;
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
     * Gets the value of the reserved property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getReserved() {
        return reserved;
    }

    /**
     * Sets the value of the reserved property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setReserved(Integer value) {
        this.reserved = value;
    }

    /**
     * Gets the value of the sessid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSessid() {
        return sessid;
    }

    /**
     * Sets the value of the sessid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSessid(Integer value) {
        this.sessid = value;
    }

}
