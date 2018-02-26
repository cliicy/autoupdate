
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="ulFlags" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sPath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sPattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="begin_sesstime" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="end_sesstime" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="bFirst" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "ulFlags",
    "sHost",
    "sPath",
    "sPattern",
    "beginSesstime",
    "endSesstime",
    "bFirst"
})
@XmlRootElement(name = "MsgFindFileExW")
public class MsgFindFileExW {

    @XmlElementRef(name = "strSessionNo", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strSessionNo;
    protected Integer ulFlags;
    @XmlElementRef(name = "sHost", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sHost;
    @XmlElementRef(name = "sPath", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sPath;
    @XmlElementRef(name = "sPattern", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sPattern;
    @XmlElement(name = "begin_sesstime")
    @XmlSchemaType(name = "unsignedInt")
    protected Long beginSesstime;
    @XmlElement(name = "end_sesstime")
    @XmlSchemaType(name = "unsignedInt")
    protected Long endSesstime;
    protected Boolean bFirst;

    /**
     * Gets the value of the strSessionNo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrSessionNo() {
        return strSessionNo;
    }

    /**
     * Sets the value of the strSessionNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrSessionNo(JAXBElement<String> value) {
        this.strSessionNo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the ulFlags property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUlFlags() {
        return ulFlags;
    }

    /**
     * Sets the value of the ulFlags property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUlFlags(Integer value) {
        this.ulFlags = value;
    }

    /**
     * Gets the value of the sHost property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSHost() {
        return sHost;
    }

    /**
     * Sets the value of the sHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSHost(JAXBElement<String> value) {
        this.sHost = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the sPath property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSPath() {
        return sPath;
    }

    /**
     * Sets the value of the sPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSPath(JAXBElement<String> value) {
        this.sPath = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the sPattern property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSPattern() {
        return sPattern;
    }

    /**
     * Sets the value of the sPattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSPattern(JAXBElement<String> value) {
        this.sPattern = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the beginSesstime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBeginSesstime() {
        return beginSesstime;
    }

    /**
     * Sets the value of the beginSesstime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBeginSesstime(Long value) {
        this.beginSesstime = value;
    }

    /**
     * Gets the value of the endSesstime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEndSesstime() {
        return endSesstime;
    }

    /**
     * Sets the value of the endSesstime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEndSesstime(Long value) {
        this.endSesstime = value;
    }

    /**
     * Gets the value of the bFirst property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBFirst() {
        return bFirst;
    }

    /**
     * Sets the value of the bFirst property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBFirst(Boolean value) {
        this.bFirst = value;
    }

}
