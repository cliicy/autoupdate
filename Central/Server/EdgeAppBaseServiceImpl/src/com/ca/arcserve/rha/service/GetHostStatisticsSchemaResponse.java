
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
 *         &lt;element name="get_host_statistics_schemaResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getHostStatisticsSchemaResult"
})
@XmlRootElement(name = "get_host_statistics_schemaResponse")
public class GetHostStatisticsSchemaResponse {

    @XmlElement(name = "get_host_statistics_schemaResult")
    protected String getHostStatisticsSchemaResult;

    /**
     * Gets the value of the getHostStatisticsSchemaResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetHostStatisticsSchemaResult() {
        return getHostStatisticsSchemaResult;
    }

    /**
     * Sets the value of the getHostStatisticsSchemaResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetHostStatisticsSchemaResult(String value) {
        this.getHostStatisticsSchemaResult = value;
    }

}
