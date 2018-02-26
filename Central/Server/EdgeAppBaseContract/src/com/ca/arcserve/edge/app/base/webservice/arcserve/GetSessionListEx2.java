
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="tapeDataEx" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_TAPEDATAEX" minOccurs="0"/>
 *         &lt;element name="begin_sesstime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="end_sesstime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lFlag" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
    "tapeDataEx",
    "beginSesstime",
    "endSesstime",
    "lFlag"
})
@XmlRootElement(name = "GetSessionListEx2")
public class GetSessionListEx2 {

    @XmlElementRef(name = "strSessionNo", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strSessionNo;
    @XmlElementRef(name = "tapeDataEx", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ABFuncTAPEDATAEX> tapeDataEx;
    @XmlElement(name = "begin_sesstime")
    protected Integer beginSesstime;
    @XmlElement(name = "end_sesstime")
    protected Integer endSesstime;
    protected Integer lFlag;

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
     * Gets the value of the tapeDataEx property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABFuncTAPEDATAEX }{@code >}
     *     
     */
    public JAXBElement<ABFuncTAPEDATAEX> getTapeDataEx() {
        return tapeDataEx;
    }

    /**
     * Sets the value of the tapeDataEx property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABFuncTAPEDATAEX }{@code >}
     *     
     */
    public void setTapeDataEx(JAXBElement<ABFuncTAPEDATAEX> value) {
        this.tapeDataEx = ((JAXBElement<ABFuncTAPEDATAEX> ) value);
    }

    /**
     * Gets the value of the beginSesstime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBeginSesstime() {
        return beginSesstime;
    }

    /**
     * Sets the value of the beginSesstime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBeginSesstime(Integer value) {
        this.beginSesstime = value;
    }

    /**
     * Gets the value of the endSesstime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEndSesstime() {
        return endSesstime;
    }

    /**
     * Sets the value of the endSesstime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEndSesstime(Integer value) {
        this.endSesstime = value;
    }

    /**
     * Gets the value of the lFlag property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLFlag() {
        return lFlag;
    }

    /**
     * Sets the value of the lFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLFlag(Integer value) {
        this.lFlag = value;
    }

}
