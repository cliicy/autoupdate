
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
 *         &lt;element name="get_sys_snap_listResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="snapshot_info_xml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getSysSnapListResult",
    "snapshotInfoXml",
    "errMessages"
})
@XmlRootElement(name = "get_sys_snap_listResponse")
public class GetSysSnapListResponse {

    @XmlElement(name = "get_sys_snap_listResult")
    protected boolean getSysSnapListResult;
    @XmlElement(name = "snapshot_info_xml")
    protected String snapshotInfoXml;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getSysSnapListResult property.
     * 
     */
    public boolean isGetSysSnapListResult() {
        return getSysSnapListResult;
    }

    /**
     * Sets the value of the getSysSnapListResult property.
     * 
     */
    public void setGetSysSnapListResult(boolean value) {
        this.getSysSnapListResult = value;
    }

    /**
     * Gets the value of the snapshotInfoXml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnapshotInfoXml() {
        return snapshotInfoXml;
    }

    /**
     * Sets the value of the snapshotInfoXml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnapshotInfoXml(String value) {
        this.snapshotInfoXml = value;
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
