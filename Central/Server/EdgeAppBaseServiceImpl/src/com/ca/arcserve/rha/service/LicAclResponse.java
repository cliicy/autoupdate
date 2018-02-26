
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
 *         &lt;element name="lic_aclResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="opt_acl" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "licAclResult",
    "optAcl"
})
@XmlRootElement(name = "lic_aclResponse")
public class LicAclResponse {

    @XmlElement(name = "lic_aclResult")
    protected boolean licAclResult;
    @XmlElement(name = "opt_acl")
    protected boolean optAcl;

    /**
     * Gets the value of the licAclResult property.
     * 
     */
    public boolean isLicAclResult() {
        return licAclResult;
    }

    /**
     * Sets the value of the licAclResult property.
     * 
     */
    public void setLicAclResult(boolean value) {
        this.licAclResult = value;
    }

    /**
     * Gets the value of the optAcl property.
     * 
     */
    public boolean isOptAcl() {
        return optAcl;
    }

    /**
     * Sets the value of the optAcl property.
     * 
     */
    public void setOptAcl(boolean value) {
        this.optAcl = value;
    }

}
