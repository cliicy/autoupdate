
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_SessionAndTapeRec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_SessionAndTapeRec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sessRec" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_SessRec" minOccurs="0"/>
 *         &lt;element name="tapeRecW" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_TapeRecW" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_SessionAndTapeRec", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "sessRec",
    "tapeRecW"
})
public class ABFuncSessionAndTapeRec {

    @XmlElementRef(name = "sessRec", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<ABFuncSessRec> sessRec;
    @XmlElementRef(name = "tapeRecW", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<ABFuncTapeRecW> tapeRecW;

    /**
     * Gets the value of the sessRec property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABFuncSessRec }{@code >}
     *     
     */
    public JAXBElement<ABFuncSessRec> getSessRec() {
        return sessRec;
    }

    /**
     * Sets the value of the sessRec property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABFuncSessRec }{@code >}
     *     
     */
    public void setSessRec(JAXBElement<ABFuncSessRec> value) {
        this.sessRec = ((JAXBElement<ABFuncSessRec> ) value);
    }

    /**
     * Gets the value of the tapeRecW property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABFuncTapeRecW }{@code >}
     *     
     */
    public JAXBElement<ABFuncTapeRecW> getTapeRecW() {
        return tapeRecW;
    }

    /**
     * Sets the value of the tapeRecW property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABFuncTapeRecW }{@code >}
     *     
     */
    public void setTapeRecW(JAXBElement<ABFuncTapeRecW> value) {
        this.tapeRecW = ((JAXBElement<ABFuncTapeRecW> ) value);
    }

}
