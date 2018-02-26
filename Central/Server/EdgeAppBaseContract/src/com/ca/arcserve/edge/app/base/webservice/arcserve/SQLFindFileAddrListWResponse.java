
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
 *         &lt;element name="SQLFindFileAddrListWResult" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfunsignedInt" minOccurs="0"/>
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
    "sqlFindFileAddrListWResult"
})
@XmlRootElement(name = "SQLFindFileAddrListWResponse")
public class SQLFindFileAddrListWResponse {

    @XmlElementRef(name = "SQLFindFileAddrListWResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfunsignedInt> sqlFindFileAddrListWResult;

    /**
     * Gets the value of the sqlFindFileAddrListWResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}
     *     
     */
    public JAXBElement<ArrayOfunsignedInt> getSQLFindFileAddrListWResult() {
        return sqlFindFileAddrListWResult;
    }

    /**
     * Sets the value of the sqlFindFileAddrListWResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}
     *     
     */
    public void setSQLFindFileAddrListWResult(JAXBElement<ArrayOfunsignedInt> value) {
        this.sqlFindFileAddrListWResult = ((JAXBElement<ArrayOfunsignedInt> ) value);
    }

}
