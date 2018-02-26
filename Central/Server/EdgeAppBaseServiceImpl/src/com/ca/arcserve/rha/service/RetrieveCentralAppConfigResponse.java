
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
 *         &lt;element name="retrieve_central_app_configResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="central_app_config" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "retrieveCentralAppConfigResult",
    "centralAppConfig",
    "errMessages"
})
@XmlRootElement(name = "retrieve_central_app_configResponse")
public class RetrieveCentralAppConfigResponse {

    @XmlElement(name = "retrieve_central_app_configResult")
    protected boolean retrieveCentralAppConfigResult;
    @XmlElement(name = "central_app_config")
    protected String centralAppConfig;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the retrieveCentralAppConfigResult property.
     * 
     */
    public boolean isRetrieveCentralAppConfigResult() {
        return retrieveCentralAppConfigResult;
    }

    /**
     * Sets the value of the retrieveCentralAppConfigResult property.
     * 
     */
    public void setRetrieveCentralAppConfigResult(boolean value) {
        this.retrieveCentralAppConfigResult = value;
    }

    /**
     * Gets the value of the centralAppConfig property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCentralAppConfig() {
        return centralAppConfig;
    }

    /**
     * Sets the value of the centralAppConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCentralAppConfig(String value) {
        this.centralAppConfig = value;
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
