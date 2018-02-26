
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
 *         &lt;element name="retrieve_d2d_configResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="dest_path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "retrieveD2DConfigResult",
    "destPath",
    "errMessages"
})
@XmlRootElement(name = "retrieve_d2d_configResponse")
public class RetrieveD2DConfigResponse {

    @XmlElement(name = "retrieve_d2d_configResult")
    protected boolean retrieveD2DConfigResult;
    @XmlElement(name = "dest_path")
    protected String destPath;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the retrieveD2DConfigResult property.
     * 
     */
    public boolean isRetrieveD2DConfigResult() {
        return retrieveD2DConfigResult;
    }

    /**
     * Sets the value of the retrieveD2DConfigResult property.
     * 
     */
    public void setRetrieveD2DConfigResult(boolean value) {
        this.retrieveD2DConfigResult = value;
    }

    /**
     * Gets the value of the destPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestPath() {
        return destPath;
    }

    /**
     * Sets the value of the destPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestPath(String value) {
        this.destPath = value;
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
