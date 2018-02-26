
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
 *         &lt;element name="CATLOGDB_QueryNextExWResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ArrayOfABFunc_DetailExtRecEXW" minOccurs="0"/>
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
    "catlogdbQueryNextExWResult"
})
@XmlRootElement(name = "CATLOGDB_QueryNextExWResponse")
public class CATLOGDBQueryNextExWResponse {

    @XmlElementRef(name = "CATLOGDB_QueryNextExWResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfABFuncDetailExtRecEXW> catlogdbQueryNextExWResult;

    /**
     * Gets the value of the catlogdbQueryNextExWResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfABFuncDetailExtRecEXW }{@code >}
     *     
     */
    public JAXBElement<ArrayOfABFuncDetailExtRecEXW> getCATLOGDBQueryNextExWResult() {
        return catlogdbQueryNextExWResult;
    }

    /**
     * Sets the value of the catlogdbQueryNextExWResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfABFuncDetailExtRecEXW }{@code >}
     *     
     */
    public void setCATLOGDBQueryNextExWResult(JAXBElement<ArrayOfABFuncDetailExtRecEXW> value) {
        this.catlogdbQueryNextExWResult = ((JAXBElement<ArrayOfABFuncDetailExtRecEXW> ) value);
    }

}
