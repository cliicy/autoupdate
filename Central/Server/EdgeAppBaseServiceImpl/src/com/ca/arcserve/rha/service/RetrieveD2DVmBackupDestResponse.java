
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
 *         &lt;element name="retrieve_d2d_vm_backup_destResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="vm_backup_dest_path_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "retrieveD2DVmBackupDestResult",
    "vmBackupDestPathList",
    "errMessages"
})
@XmlRootElement(name = "retrieve_d2d_vm_backup_destResponse")
public class RetrieveD2DVmBackupDestResponse {

    @XmlElement(name = "retrieve_d2d_vm_backup_destResult")
    protected boolean retrieveD2DVmBackupDestResult;
    @XmlElement(name = "vm_backup_dest_path_list")
    protected String vmBackupDestPathList;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the retrieveD2DVmBackupDestResult property.
     * 
     */
    public boolean isRetrieveD2DVmBackupDestResult() {
        return retrieveD2DVmBackupDestResult;
    }

    /**
     * Sets the value of the retrieveD2DVmBackupDestResult property.
     * 
     */
    public void setRetrieveD2DVmBackupDestResult(boolean value) {
        this.retrieveD2DVmBackupDestResult = value;
    }

    /**
     * Gets the value of the vmBackupDestPathList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVmBackupDestPathList() {
        return vmBackupDestPathList;
    }

    /**
     * Sets the value of the vmBackupDestPathList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVmBackupDestPathList(String value) {
        this.vmBackupDestPathList = value;
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
