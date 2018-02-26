
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
 *         &lt;element name="set_acl_rightsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "setAclRightsResult"
})
@XmlRootElement(name = "set_acl_rightsResponse")
public class SetAclRightsResponse {

    @XmlElement(name = "set_acl_rightsResult")
    protected boolean setAclRightsResult;

    /**
     * Gets the value of the setAclRightsResult property.
     * 
     */
    public boolean isSetAclRightsResult() {
        return setAclRightsResult;
    }

    /**
     * Sets the value of the setAclRightsResult property.
     * 
     */
    public void setSetAclRightsResult(boolean value) {
        this.setAclRightsResult = value;
    }

}
