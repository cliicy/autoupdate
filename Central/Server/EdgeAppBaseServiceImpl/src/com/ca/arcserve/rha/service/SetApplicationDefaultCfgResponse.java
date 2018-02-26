
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
 *         &lt;element name="set_application_default_cfgResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="application_comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="err_messages" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "setApplicationDefaultCfgResult",
    "applicationComments",
    "errMessages"
})
@XmlRootElement(name = "set_application_default_cfgResponse")
public class SetApplicationDefaultCfgResponse {

    @XmlElement(name = "set_application_default_cfgResult")
    protected boolean setApplicationDefaultCfgResult;
    @XmlElement(name = "application_comments")
    protected String applicationComments;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the setApplicationDefaultCfgResult property.
     * 
     */
    public boolean isSetApplicationDefaultCfgResult() {
        return setApplicationDefaultCfgResult;
    }

    /**
     * Sets the value of the setApplicationDefaultCfgResult property.
     * 
     */
    public void setSetApplicationDefaultCfgResult(boolean value) {
        this.setApplicationDefaultCfgResult = value;
    }

    /**
     * Gets the value of the applicationComments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationComments() {
        return applicationComments;
    }

    /**
     * Sets the value of the applicationComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationComments(String value) {
        this.applicationComments = value;
    }

    /**
     * Gets the value of the errMessages property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrMessages() {
        return errMessages;
    }

    /**
     * Sets the value of the errMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrMessages(String value) {
        this.errMessages = value;
    }

}
