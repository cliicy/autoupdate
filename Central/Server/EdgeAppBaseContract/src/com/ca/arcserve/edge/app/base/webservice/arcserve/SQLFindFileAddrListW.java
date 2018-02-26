
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element name="sPattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bCaseSensitive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="DefStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "sPattern",
    "sHost",
    "bCaseSensitive",
    "defStr",
    "bFirst"
})
@XmlRootElement(name = "SQLFindFileAddrListW")
public class SQLFindFileAddrListW {

    @XmlElementRef(name = "strSessionNo", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strSessionNo;
    @XmlElementRef(name = "sPattern", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sPattern;
    @XmlElementRef(name = "sHost", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sHost;
    protected Boolean bCaseSensitive;
    @XmlElementRef(name = "DefStr", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> defStr;
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
     * Gets the value of the bCaseSensitive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBCaseSensitive() {
        return bCaseSensitive;
    }

    /**
     * Sets the value of the bCaseSensitive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBCaseSensitive(Boolean value) {
        this.bCaseSensitive = value;
    }

    /**
     * Gets the value of the defStr property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDefStr() {
        return defStr;
    }

    /**
     * Sets the value of the defStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDefStr(JAXBElement<String> value) {
        this.defStr = ((JAXBElement<String> ) value);
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
