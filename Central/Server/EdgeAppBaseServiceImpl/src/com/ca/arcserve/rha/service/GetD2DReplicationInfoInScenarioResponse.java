
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
 *         &lt;element name="get_d2d_replication_info_in_scenarioResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="backup_vm_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getD2DReplicationInfoInScenarioResult",
    "backupVmList",
    "errMessages"
})
@XmlRootElement(name = "get_d2d_replication_info_in_scenarioResponse")
public class GetD2DReplicationInfoInScenarioResponse {

    @XmlElement(name = "get_d2d_replication_info_in_scenarioResult")
    protected boolean getD2DReplicationInfoInScenarioResult;
    @XmlElement(name = "backup_vm_list")
    protected String backupVmList;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getD2DReplicationInfoInScenarioResult property.
     * 
     */
    public boolean isGetD2DReplicationInfoInScenarioResult() {
        return getD2DReplicationInfoInScenarioResult;
    }

    /**
     * Sets the value of the getD2DReplicationInfoInScenarioResult property.
     * 
     */
    public void setGetD2DReplicationInfoInScenarioResult(boolean value) {
        this.getD2DReplicationInfoInScenarioResult = value;
    }

    /**
     * Gets the value of the backupVmList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackupVmList() {
        return backupVmList;
    }

    /**
     * Sets the value of the backupVmList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackupVmList(String value) {
        this.backupVmList = value;
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
