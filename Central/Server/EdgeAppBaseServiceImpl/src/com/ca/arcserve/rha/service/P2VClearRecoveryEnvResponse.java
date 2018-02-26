
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
 *         &lt;element name="p2v_clear_recovery_envResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "p2VClearRecoveryEnvResult",
    "errMessages"
})
@XmlRootElement(name = "p2v_clear_recovery_envResponse")
public class P2VClearRecoveryEnvResponse {

    @XmlElement(name = "p2v_clear_recovery_envResult")
    protected boolean p2VClearRecoveryEnvResult;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the p2VClearRecoveryEnvResult property.
     * 
     */
    public boolean isP2VClearRecoveryEnvResult() {
        return p2VClearRecoveryEnvResult;
    }

    /**
     * Sets the value of the p2VClearRecoveryEnvResult property.
     * 
     */
    public void setP2VClearRecoveryEnvResult(boolean value) {
        this.p2VClearRecoveryEnvResult = value;
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
