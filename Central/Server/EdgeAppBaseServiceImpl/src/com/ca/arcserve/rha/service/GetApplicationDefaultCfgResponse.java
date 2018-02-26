
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
 *         &lt;element name="get_application_default_cfgResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="application_default_cfg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getApplicationDefaultCfgResult",
    "applicationDefaultCfg",
    "errMessages"
})
@XmlRootElement(name = "get_application_default_cfgResponse")
public class GetApplicationDefaultCfgResponse {

    @XmlElement(name = "get_application_default_cfgResult")
    protected boolean getApplicationDefaultCfgResult;
    @XmlElement(name = "application_default_cfg")
    protected String applicationDefaultCfg;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getApplicationDefaultCfgResult property.
     * 
     */
    public boolean isGetApplicationDefaultCfgResult() {
        return getApplicationDefaultCfgResult;
    }

    /**
     * Sets the value of the getApplicationDefaultCfgResult property.
     * 
     */
    public void setGetApplicationDefaultCfgResult(boolean value) {
        this.getApplicationDefaultCfgResult = value;
    }

    /**
     * Gets the value of the applicationDefaultCfg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationDefaultCfg() {
        return applicationDefaultCfg;
    }

    /**
     * Sets the value of the applicationDefaultCfg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationDefaultCfg(String value) {
        this.applicationDefaultCfg = value;
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
