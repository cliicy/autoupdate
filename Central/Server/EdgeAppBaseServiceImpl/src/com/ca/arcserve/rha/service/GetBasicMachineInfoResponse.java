
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
 *         &lt;element name="get_basic_machine_infoResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="basic_machine_info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getBasicMachineInfoResult",
    "basicMachineInfo",
    "errMessages"
})
@XmlRootElement(name = "get_basic_machine_infoResponse")
public class GetBasicMachineInfoResponse {

    @XmlElement(name = "get_basic_machine_infoResult")
    protected boolean getBasicMachineInfoResult;
    @XmlElement(name = "basic_machine_info")
    protected String basicMachineInfo;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getBasicMachineInfoResult property.
     * 
     */
    public boolean isGetBasicMachineInfoResult() {
        return getBasicMachineInfoResult;
    }

    /**
     * Sets the value of the getBasicMachineInfoResult property.
     * 
     */
    public void setGetBasicMachineInfoResult(boolean value) {
        this.getBasicMachineInfoResult = value;
    }

    /**
     * Gets the value of the basicMachineInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBasicMachineInfo() {
        return basicMachineInfo;
    }

    /**
     * Sets the value of the basicMachineInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBasicMachineInfo(String value) {
        this.basicMachineInfo = value;
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
