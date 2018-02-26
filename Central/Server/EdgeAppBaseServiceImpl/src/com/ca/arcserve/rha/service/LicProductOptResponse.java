
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
 *         &lt;element name="lic_product_optResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="opt_ha" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="opt_dr" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="opt_cd" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "licProductOptResult",
    "optHa",
    "optDr",
    "optCd"
})
@XmlRootElement(name = "lic_product_optResponse")
public class LicProductOptResponse {

    @XmlElement(name = "lic_product_optResult")
    protected boolean licProductOptResult;
    @XmlElement(name = "opt_ha")
    protected boolean optHa;
    @XmlElement(name = "opt_dr")
    protected boolean optDr;
    @XmlElement(name = "opt_cd")
    protected boolean optCd;

    /**
     * Gets the value of the licProductOptResult property.
     * 
     */
    public boolean isLicProductOptResult() {
        return licProductOptResult;
    }

    /**
     * Sets the value of the licProductOptResult property.
     * 
     */
    public void setLicProductOptResult(boolean value) {
        this.licProductOptResult = value;
    }

    /**
     * Gets the value of the optHa property.
     * 
     */
    public boolean isOptHa() {
        return optHa;
    }

    /**
     * Sets the value of the optHa property.
     * 
     */
    public void setOptHa(boolean value) {
        this.optHa = value;
    }

    /**
     * Gets the value of the optDr property.
     * 
     */
    public boolean isOptDr() {
        return optDr;
    }

    /**
     * Sets the value of the optDr property.
     * 
     */
    public void setOptDr(boolean value) {
        this.optDr = value;
    }

    /**
     * Gets the value of the optCd property.
     * 
     */
    public boolean isOptCd() {
        return optCd;
    }

    /**
     * Sets the value of the optCd property.
     * 
     */
    public void setOptCd(boolean value) {
        this.optCd = value;
    }

}
