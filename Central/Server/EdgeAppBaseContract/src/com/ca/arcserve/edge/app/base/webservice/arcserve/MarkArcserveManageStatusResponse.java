
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
 *         &lt;element name="MarkArcserveManageStatusResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "markArcserveManageStatusResult"
})
@XmlRootElement(name = "MarkArcserveManageStatusResponse")
public class MarkArcserveManageStatusResponse {

    @XmlElementRef(name = "MarkArcserveManageStatusResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> markArcserveManageStatusResult;

    /**
     * Gets the value of the markArcserveManageStatusResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMarkArcserveManageStatusResult() {
        return markArcserveManageStatusResult;
    }

    /**
     * Sets the value of the markArcserveManageStatusResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMarkArcserveManageStatusResult(JAXBElement<String> value) {
        this.markArcserveManageStatusResult = ((JAXBElement<String> ) value);
    }

}