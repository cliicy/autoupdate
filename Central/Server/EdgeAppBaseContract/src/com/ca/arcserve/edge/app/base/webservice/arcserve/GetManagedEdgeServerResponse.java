
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
 *         &lt;element name="GetManagedEdgeServerResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getManagedEdgeServerResult"
})
@XmlRootElement(name = "GetManagedEdgeServerResponse")
public class GetManagedEdgeServerResponse {

    @XmlElementRef(name = "GetManagedEdgeServerResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> getManagedEdgeServerResult;

    /**
     * Gets the value of the getManagedEdgeServerResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGetManagedEdgeServerResult() {
        return getManagedEdgeServerResult;
    }

    /**
     * Sets the value of the getManagedEdgeServerResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGetManagedEdgeServerResult(JAXBElement<String> value) {
        this.getManagedEdgeServerResult = ((JAXBElement<String> ) value);
    }

}
