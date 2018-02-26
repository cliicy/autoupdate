
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_StringAndFlag complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_StringAndFlag">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cp_flag" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="strValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_StringAndFlag", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "cpFlag",
    "strValue"
})
public class ABFuncStringAndFlag {

    @XmlElement(name = "cp_flag")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger cpFlag;
    @XmlElementRef(name = "strValue", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", type = JAXBElement.class)
    protected JAXBElement<String> strValue;

    /**
     * Gets the value of the cpFlag property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCpFlag() {
        return cpFlag;
    }

    /**
     * Sets the value of the cpFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCpFlag(BigInteger value) {
        this.cpFlag = value;
    }

    /**
     * Gets the value of the strValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrValue() {
        return strValue;
    }

    /**
     * Sets the value of the strValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrValue(JAXBElement<String> value) {
        this.strValue = ((JAXBElement<String> ) value);
    }

}
