
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
 *         &lt;element name="get_vm_infoResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="vm_info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getVmInfoResult",
    "vmInfo",
    "errMessages"
})
@XmlRootElement(name = "get_vm_infoResponse")
public class GetVmInfoResponse {

    @XmlElement(name = "get_vm_infoResult")
    protected boolean getVmInfoResult;
    @XmlElement(name = "vm_info")
    protected String vmInfo;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getVmInfoResult property.
     * 
     */
    public boolean isGetVmInfoResult() {
        return getVmInfoResult;
    }

    /**
     * Sets the value of the getVmInfoResult property.
     * 
     */
    public void setGetVmInfoResult(boolean value) {
        this.getVmInfoResult = value;
    }

    /**
     * Gets the value of the vmInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVmInfo() {
        return vmInfo;
    }

    /**
     * Sets the value of the vmInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVmInfo(String value) {
        this.vmInfo = value;
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
