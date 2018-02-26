
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
 *         &lt;element name="get_superuser_nameResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="superuser_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getSuperuserNameResult",
    "superuserName"
})
@XmlRootElement(name = "get_superuser_nameResponse")
public class GetSuperuserNameResponse {

    @XmlElement(name = "get_superuser_nameResult")
    protected boolean getSuperuserNameResult;
    @XmlElement(name = "superuser_name")
    protected String superuserName;

    /**
     * Gets the value of the getSuperuserNameResult property.
     * 
     */
    public boolean isGetSuperuserNameResult() {
        return getSuperuserNameResult;
    }

    /**
     * Sets the value of the getSuperuserNameResult property.
     * 
     */
    public void setGetSuperuserNameResult(boolean value) {
        this.getSuperuserNameResult = value;
    }

    /**
     * Gets the value of the superuserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuperuserName() {
        return superuserName;
    }

    /**
     * Sets the value of the superuserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuperuserName(String value) {
        this.superuserName = value;
    }

}
