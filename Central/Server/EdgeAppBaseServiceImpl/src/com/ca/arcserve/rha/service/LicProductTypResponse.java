
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
 *         &lt;element name="lic_product_typResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="typ_fs" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="typ_db" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "licProductTypResult",
    "typFs",
    "typDb"
})
@XmlRootElement(name = "lic_product_typResponse")
public class LicProductTypResponse {

    @XmlElement(name = "lic_product_typResult")
    protected boolean licProductTypResult;
    @XmlElement(name = "typ_fs")
    protected boolean typFs;
    @XmlElement(name = "typ_db")
    protected boolean typDb;

    /**
     * Gets the value of the licProductTypResult property.
     * 
     */
    public boolean isLicProductTypResult() {
        return licProductTypResult;
    }

    /**
     * Sets the value of the licProductTypResult property.
     * 
     */
    public void setLicProductTypResult(boolean value) {
        this.licProductTypResult = value;
    }

    /**
     * Gets the value of the typFs property.
     * 
     */
    public boolean isTypFs() {
        return typFs;
    }

    /**
     * Sets the value of the typFs property.
     * 
     */
    public void setTypFs(boolean value) {
        this.typFs = value;
    }

    /**
     * Gets the value of the typDb property.
     * 
     */
    public boolean isTypDb() {
        return typDb;
    }

    /**
     * Sets the value of the typDb property.
     * 
     */
    public void setTypDb(boolean value) {
        this.typDb = value;
    }

}
