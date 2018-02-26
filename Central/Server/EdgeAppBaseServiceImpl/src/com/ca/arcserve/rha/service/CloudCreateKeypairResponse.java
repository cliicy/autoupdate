
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
 *         &lt;element name="cloud_create_keypairResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="keypair_details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "cloudCreateKeypairResult",
    "keypairDetails",
    "errMessages"
})
@XmlRootElement(name = "cloud_create_keypairResponse")
public class CloudCreateKeypairResponse {

    @XmlElement(name = "cloud_create_keypairResult")
    protected boolean cloudCreateKeypairResult;
    @XmlElement(name = "keypair_details")
    protected String keypairDetails;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the cloudCreateKeypairResult property.
     * 
     */
    public boolean isCloudCreateKeypairResult() {
        return cloudCreateKeypairResult;
    }

    /**
     * Sets the value of the cloudCreateKeypairResult property.
     * 
     */
    public void setCloudCreateKeypairResult(boolean value) {
        this.cloudCreateKeypairResult = value;
    }

    /**
     * Gets the value of the keypairDetails property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeypairDetails() {
        return keypairDetails;
    }

    /**
     * Sets the value of the keypairDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeypairDetails(String value) {
        this.keypairDetails = value;
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
