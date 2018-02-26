
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
 *         &lt;element name="get_host_file_listResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="file_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="why_not_reason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getHostFileListResult",
    "fileList",
    "whyNotReason"
})
@XmlRootElement(name = "get_host_file_listResponse")
public class GetHostFileListResponse {

    @XmlElement(name = "get_host_file_listResult")
    protected boolean getHostFileListResult;
    @XmlElement(name = "file_list")
    protected String fileList;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the getHostFileListResult property.
     * 
     */
    public boolean isGetHostFileListResult() {
        return getHostFileListResult;
    }

    /**
     * Sets the value of the getHostFileListResult property.
     * 
     */
    public void setGetHostFileListResult(boolean value) {
        this.getHostFileListResult = value;
    }

    /**
     * Gets the value of the fileList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileList() {
        return fileList;
    }

    /**
     * Sets the value of the fileList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileList(String value) {
        this.fileList = value;
    }

    /**
     * Gets the value of the whyNotReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhyNotReason() {
        return whyNotReason;
    }

    /**
     * Sets the value of the whyNotReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhyNotReason(String value) {
        this.whyNotReason = value;
    }

}
