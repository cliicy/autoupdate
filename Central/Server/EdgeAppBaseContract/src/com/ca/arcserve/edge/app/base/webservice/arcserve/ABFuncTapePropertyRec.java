
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_TapePropertyRec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_TapePropertyRec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="curkbwritten" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="lastformat" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="lastread" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="lastwrite" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="reserved" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tapeData" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_TapeDataEx2W" minOccurs="0"/>
 *         &lt;element name="tapeflag_ext" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="tapetype" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_TapePropertyRec", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "curkbwritten",
    "lastformat",
    "lastread",
    "lastwrite",
    "reserved",
    "tapeData",
    "tapeflagExt",
    "tapetype"
})
public class ABFuncTapePropertyRec {

    protected Long curkbwritten;
    protected Long lastformat;
    protected Long lastread;
    protected Long lastwrite;
    @XmlElementRef(name = "reserved", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> reserved;
    @XmlElementRef(name = "tapeData", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<ABFuncTapeDataEx2W> tapeData;
    @XmlElement(name = "tapeflag_ext")
    protected Long tapeflagExt;
    protected Short tapetype;

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
     * Gets the value of the tapeData property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABFuncTapeDataEx2W }{@code >}
     *     
     */
    public JAXBElement<ABFuncTapeDataEx2W> getTapeData() {
        return tapeData;
    }

    /**
     * Sets the value of the tapeData property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABFuncTapeDataEx2W }{@code >}
     *     
     */
    public void setTapeData(JAXBElement<ABFuncTapeDataEx2W> value) {
        this.tapeData = ((JAXBElement<ABFuncTapeDataEx2W> ) value);
    }

    /**
     * Gets the value of the tapeflagExt property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTapeflagExt() {
        return tapeflagExt;
    }

    /**
     * Sets the value of the tapeflagExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTapeflagExt(Long value) {
        this.tapeflagExt = value;
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

}
