
package com.ca.arcserve.rha.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="get_detailed_statusResult" type="{http://ca.com/}ArrayOfString" minOccurs="0"/>
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
    "getDetailedStatusResult"
})
@XmlRootElement(name = "get_detailed_statusResponse")
public class GetDetailedStatusResponse {

    @XmlElement(name = "get_detailed_statusResult")
    protected ArrayOfString getDetailedStatusResult;

    /**
     * Gets the value of the getDetailedStatusResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getGetDetailedStatusResult() {
        return getDetailedStatusResult;
    }

    /**
     * Sets the value of the getDetailedStatusResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setGetDetailedStatusResult(ArrayOfString value) {
        this.getDetailedStatusResult = value;
    }

}
