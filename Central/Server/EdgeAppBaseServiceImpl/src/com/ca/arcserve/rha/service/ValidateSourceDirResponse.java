
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
 *         &lt;element name="validate_source_dirResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="install_dir_info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "validateSourceDirResult",
    "installDirInfo",
    "errMessages"
})
@XmlRootElement(name = "validate_source_dirResponse")
public class ValidateSourceDirResponse {

    @XmlElement(name = "validate_source_dirResult")
    protected boolean validateSourceDirResult;
    @XmlElement(name = "install_dir_info")
    protected String installDirInfo;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the validateSourceDirResult property.
     * 
     */
    public boolean isValidateSourceDirResult() {
        return validateSourceDirResult;
    }

    /**
     * Sets the value of the validateSourceDirResult property.
     * 
     */
    public void setValidateSourceDirResult(boolean value) {
        this.validateSourceDirResult = value;
    }

    /**
     * Gets the value of the installDirInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstallDirInfo() {
        return installDirInfo;
    }

    /**
     * Sets the value of the installDirInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstallDirInfo(String value) {
        this.installDirInfo = value;
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
