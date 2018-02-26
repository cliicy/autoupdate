
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
 *         &lt;element name="db_autodiscoveryResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="db_contents" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "dbAutodiscoveryResult",
    "dbContents"
})
@XmlRootElement(name = "db_autodiscoveryResponse")
public class DbAutodiscoveryResponse {

    @XmlElement(name = "db_autodiscoveryResult")
    protected boolean dbAutodiscoveryResult;
    @XmlElement(name = "db_contents")
    protected String dbContents;

    /**
     * Gets the value of the dbAutodiscoveryResult property.
     * 
     */
    public boolean isDbAutodiscoveryResult() {
        return dbAutodiscoveryResult;
    }

    /**
     * Sets the value of the dbAutodiscoveryResult property.
     * 
     */
    public void setDbAutodiscoveryResult(boolean value) {
        this.dbAutodiscoveryResult = value;
    }

    /**
     * Gets the value of the dbContents property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDbContents() {
        return dbContents;
    }

    /**
     * Sets the value of the dbContents property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDbContents(String value) {
        this.dbContents = value;
    }

}
