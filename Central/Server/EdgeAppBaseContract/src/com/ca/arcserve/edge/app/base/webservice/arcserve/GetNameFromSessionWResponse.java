
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
 *         &lt;element name="GetNameFromSessionWResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_StringAndFlag" minOccurs="0"/>
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
    "getNameFromSessionWResult"
})
@XmlRootElement(name = "GetNameFromSessionWResponse")
public class GetNameFromSessionWResponse {

    @XmlElementRef(name = "GetNameFromSessionWResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ABFuncStringAndFlag> getNameFromSessionWResult;

    /**
     * Gets the value of the getNameFromSessionWResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABFuncStringAndFlag }{@code >}
     *     
     */
    public JAXBElement<ABFuncStringAndFlag> getGetNameFromSessionWResult() {
        return getNameFromSessionWResult;
    }

    /**
     * Sets the value of the getNameFromSessionWResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABFuncStringAndFlag }{@code >}
     *     
     */
    public void setGetNameFromSessionWResult(JAXBElement<ABFuncStringAndFlag> value) {
        this.getNameFromSessionWResult = ((JAXBElement<ABFuncStringAndFlag> ) value);
    }

}
