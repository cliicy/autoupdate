
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
 *         &lt;element name="get_host_ha_infoResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="host_ha_info_xml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getHostHaInfoResult",
    "hostHaInfoXml",
    "errMessages"
})
@XmlRootElement(name = "get_host_ha_infoResponse")
public class GetHostHaInfoResponse {

    @XmlElement(name = "get_host_ha_infoResult")
    protected boolean getHostHaInfoResult;
    @XmlElement(name = "host_ha_info_xml")
    protected String hostHaInfoXml;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getHostHaInfoResult property.
     * 
     */
    public boolean isGetHostHaInfoResult() {
        return getHostHaInfoResult;
    }

    /**
     * Sets the value of the getHostHaInfoResult property.
     * 
     */
    public void setGetHostHaInfoResult(boolean value) {
        this.getHostHaInfoResult = value;
    }

    /**
     * Gets the value of the hostHaInfoXml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostHaInfoXml() {
        return hostHaInfoXml;
    }

    /**
     * Sets the value of the hostHaInfoXml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostHaInfoXml(String value) {
        this.hostHaInfoXml = value;
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
