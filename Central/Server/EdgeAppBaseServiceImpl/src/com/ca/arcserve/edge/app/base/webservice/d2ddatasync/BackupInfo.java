//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.07.08 at 03:04:03 ���� CST 
//


package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BackupInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BackupInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BackupName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServerInfo" type="{}ServerInfo"/>
 *         &lt;element name="TimeStamp" type="{}TimeStamp"/>
 *         &lt;element name="BackupDetail" type="{}BackupDetail"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackupInfo", propOrder = {
    "backupName",
    "serverInfo",
    "timeStamp",
    "backupStatus",
    "backupDetail"
})
public class BackupInfo {

    @XmlElement(name = "BackupName", required = true)
    protected String backupName;
    @XmlElement(name = "ServerInfo", required = true)
    protected ServerInfo serverInfo;
    @XmlElement(name = "TimeStamp", required = true)
    protected TimeStamp timeStamp;
    @XmlElement(name = "BackupStatus", required = true)
    protected BackupStatus backupStatus;
    @XmlElement(name = "BackupDetail", required = true)
    protected BackupDetail backupDetail;

    /**
     * Gets the value of the backupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackupName() {
        return backupName;
    }

    /**
     * Sets the value of the backupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackupName(String value) {
        this.backupName = value;
    }

    public BackupStatus getBackupStatus(){
    	return backupStatus;
    }
    
    public void setBackupStatus(BackupStatus value) {
    	this.backupStatus = value;
    }
    /**
     * Gets the value of the serverInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ServerInfo }
     *     
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    /**
     * Sets the value of the serverInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServerInfo }
     *     
     */
    public void setServerInfo(ServerInfo value) {
        this.serverInfo = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link TimeStamp }
     *     
     */
    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeStamp }
     *     
     */
    public void setTimeStamp(TimeStamp value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the backupDetail property.
     * 
     * @return
     *     possible object is
     *     {@link BackupDetail }
     *     
     */
    public BackupDetail getBackupDetail() {
        return backupDetail;
    }

    /**
     * Sets the value of the backupDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link BackupDetail }
     *     
     */
    public void setBackupDetail(BackupDetail value) {
        this.backupDetail = value;
    }

}
