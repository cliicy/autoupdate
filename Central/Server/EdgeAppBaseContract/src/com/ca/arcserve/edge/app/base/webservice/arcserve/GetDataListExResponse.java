
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
 *         &lt;element name="GetDataListExResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ArrayOfABFunc_TAPEDATAEX" minOccurs="0"/>
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
    "getDataListExResult"
})
@XmlRootElement(name = "GetDataListExResponse")
public class GetDataListExResponse {

    @XmlElementRef(name = "GetDataListExResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfABFuncTAPEDATAEX> getDataListExResult;

    /**
     * Gets the value of the getDataListExResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfABFuncTAPEDATAEX }{@code >}
     *     
     */
    public JAXBElement<ArrayOfABFuncTAPEDATAEX> getGetDataListExResult() {
        return getDataListExResult;
    }

    /**
     * Sets the value of the getDataListExResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfABFuncTAPEDATAEX }{@code >}
     *     
     */
    public void setGetDataListExResult(JAXBElement<ArrayOfABFuncTAPEDATAEX> value) {
        this.getDataListExResult = ((JAXBElement<ArrayOfABFuncTAPEDATAEX> ) value);
    }

}
