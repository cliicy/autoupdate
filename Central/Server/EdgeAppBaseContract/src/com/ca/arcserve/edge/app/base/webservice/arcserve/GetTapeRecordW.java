
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
 *         &lt;element name="strTapeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="randomID" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="seqnum" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
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
    "strTapeName",
    "randomID",
    "seqnum"
})
@XmlRootElement(name = "GetTapeRecordW")
public class GetTapeRecordW {

    @XmlElementRef(name = "strSessionNo", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strSessionNo;
    @XmlElementRef(name = "strTapeName", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strTapeName;
    protected Short randomID;
    protected Short seqnum;

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
     * Gets the value of the strTapeName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrTapeName() {
        return strTapeName;
    }

    /**
     * Sets the value of the strTapeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrTapeName(JAXBElement<String> value) {
        this.strTapeName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the randomID property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getRandomID() {
        return randomID;
    }

    /**
     * Sets the value of the randomID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setRandomID(Short value) {
        this.randomID = value;
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

}
