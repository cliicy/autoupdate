
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="CATLOGDB_QueryFirstExWResult" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
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
    "catlogdbQueryFirstExWResult"
})
@XmlRootElement(name = "CATLOGDB_QueryFirstExWResponse")
public class CATLOGDBQueryFirstExWResponse {

    @XmlElement(name = "CATLOGDB_QueryFirstExWResult")
    @XmlSchemaType(name = "unsignedInt")
    protected Long catlogdbQueryFirstExWResult;

    /**
     * Gets the value of the catlogdbQueryFirstExWResult property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCATLOGDBQueryFirstExWResult() {
        return catlogdbQueryFirstExWResult;
    }

    /**
     * Sets the value of the catlogdbQueryFirstExWResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCATLOGDBQueryFirstExWResult(Long value) {
        this.catlogdbQueryFirstExWResult = value;
    }

}
