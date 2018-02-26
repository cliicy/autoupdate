
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
 *         &lt;element name="get_disk_layout_mappingResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mapping_result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getDiskLayoutMappingResult",
    "mappingResult",
    "errMessages"
})
@XmlRootElement(name = "get_disk_layout_mappingResponse")
public class GetDiskLayoutMappingResponse {

    @XmlElement(name = "get_disk_layout_mappingResult")
    protected boolean getDiskLayoutMappingResult;
    @XmlElement(name = "mapping_result")
    protected String mappingResult;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getDiskLayoutMappingResult property.
     * 
     */
    public boolean isGetDiskLayoutMappingResult() {
        return getDiskLayoutMappingResult;
    }

    /**
     * Sets the value of the getDiskLayoutMappingResult property.
     * 
     */
    public void setGetDiskLayoutMappingResult(boolean value) {
        this.getDiskLayoutMappingResult = value;
    }

    /**
     * Gets the value of the mappingResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMappingResult() {
        return mappingResult;
    }

    /**
     * Sets the value of the mappingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMappingResult(String value) {
        this.mappingResult = value;
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
