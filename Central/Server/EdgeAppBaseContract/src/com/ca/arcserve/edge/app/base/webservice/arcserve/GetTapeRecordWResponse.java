
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
 *         &lt;element name="GetTapeRecordWResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_TapeRecW" minOccurs="0"/>
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
    "getTapeRecordWResult"
})
@XmlRootElement(name = "GetTapeRecordWResponse")
public class GetTapeRecordWResponse {

    @XmlElementRef(name = "GetTapeRecordWResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ABFuncTapeRecW> getTapeRecordWResult;

    /**
     * Gets the value of the getTapeRecordWResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABFuncTapeRecW }{@code >}
     *     
     */
    public JAXBElement<ABFuncTapeRecW> getGetTapeRecordWResult() {
        return getTapeRecordWResult;
    }

    /**
     * Sets the value of the getTapeRecordWResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABFuncTapeRecW }{@code >}
     *     
     */
    public void setGetTapeRecordWResult(JAXBElement<ABFuncTapeRecW> value) {
        this.getTapeRecordWResult = ((JAXBElement<ABFuncTapeRecW> ) value);
    }

}
