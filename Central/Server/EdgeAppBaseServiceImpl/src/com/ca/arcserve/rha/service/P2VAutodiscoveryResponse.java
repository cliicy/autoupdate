
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
 *         &lt;element name="p2v_autodiscoveryResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="info_contents" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "p2VAutodiscoveryResult",
    "infoContents"
})
@XmlRootElement(name = "p2v_autodiscoveryResponse")
public class P2VAutodiscoveryResponse {

    @XmlElement(name = "p2v_autodiscoveryResult")
    protected boolean p2VAutodiscoveryResult;
    @XmlElement(name = "info_contents")
    protected String infoContents;

    /**
     * Gets the value of the p2VAutodiscoveryResult property.
     * 
     */
    public boolean isP2VAutodiscoveryResult() {
        return p2VAutodiscoveryResult;
    }

    /**
     * Sets the value of the p2VAutodiscoveryResult property.
     * 
     */
    public void setP2VAutodiscoveryResult(boolean value) {
        this.p2VAutodiscoveryResult = value;
    }

    /**
     * Gets the value of the infoContents property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoContents() {
        return infoContents;
    }

    /**
     * Sets the value of the infoContents property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoContents(String value) {
        this.infoContents = value;
    }

}
