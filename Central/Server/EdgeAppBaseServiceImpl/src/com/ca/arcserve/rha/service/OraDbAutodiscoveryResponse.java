
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
 *         &lt;element name="ora_db_autodiscoveryResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "oraDbAutodiscoveryResult",
    "dbContents"
})
@XmlRootElement(name = "ora_db_autodiscoveryResponse")
public class OraDbAutodiscoveryResponse {

    @XmlElement(name = "ora_db_autodiscoveryResult")
    protected boolean oraDbAutodiscoveryResult;
    @XmlElement(name = "db_contents")
    protected String dbContents;

    /**
     * Gets the value of the oraDbAutodiscoveryResult property.
     * 
     */
    public boolean isOraDbAutodiscoveryResult() {
        return oraDbAutodiscoveryResult;
    }

    /**
     * Sets the value of the oraDbAutodiscoveryResult property.
     * 
     */
    public void setOraDbAutodiscoveryResult(boolean value) {
        this.oraDbAutodiscoveryResult = value;
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
