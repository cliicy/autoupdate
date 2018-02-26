
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
 *         &lt;element name="set_acl_rights_exResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="failed_users" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "setAclRightsExResult",
    "failedUsers"
})
@XmlRootElement(name = "set_acl_rights_exResponse")
public class SetAclRightsExResponse {

    @XmlElement(name = "set_acl_rights_exResult")
    protected boolean setAclRightsExResult;
    @XmlElement(name = "failed_users")
    protected String failedUsers;

    /**
     * Gets the value of the setAclRightsExResult property.
     * 
     */
    public boolean isSetAclRightsExResult() {
        return setAclRightsExResult;
    }

    /**
     * Sets the value of the setAclRightsExResult property.
     * 
     */
    public void setSetAclRightsExResult(boolean value) {
        this.setAclRightsExResult = value;
    }

    /**
     * Gets the value of the failedUsers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFailedUsers() {
        return failedUsers;
    }

    /**
     * Sets the value of the failedUsers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFailedUsers(String value) {
        this.failedUsers = value;
    }

}
