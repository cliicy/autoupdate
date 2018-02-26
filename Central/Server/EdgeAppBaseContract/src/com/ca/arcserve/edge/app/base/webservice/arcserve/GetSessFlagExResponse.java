
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
 *         &lt;element name="GetSessFlagExResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_SessFlagEx" minOccurs="0"/>
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
    "getSessFlagExResult"
})
@XmlRootElement(name = "GetSessFlagExResponse")
public class GetSessFlagExResponse {

    @XmlElementRef(name = "GetSessFlagExResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ABFuncSessFlagEx> getSessFlagExResult;

    /**
     * Gets the value of the getSessFlagExResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ABFuncSessFlagEx }{@code >}
     *     
     */
    public JAXBElement<ABFuncSessFlagEx> getGetSessFlagExResult() {
        return getSessFlagExResult;
    }

    /**
     * Sets the value of the getSessFlagExResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ABFuncSessFlagEx }{@code >}
     *     
     */
    public void setGetSessFlagExResult(JAXBElement<ABFuncSessFlagEx> value) {
        this.getSessFlagExResult = ((JAXBElement<ABFuncSessFlagEx> ) value);
    }

}
