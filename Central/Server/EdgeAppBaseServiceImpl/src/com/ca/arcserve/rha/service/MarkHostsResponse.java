
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
 *         &lt;element name="mark_hostsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "markHostsResult"
})
@XmlRootElement(name = "mark_hostsResponse")
public class MarkHostsResponse {

    @XmlElement(name = "mark_hostsResult")
    protected boolean markHostsResult;

    /**
     * Gets the value of the markHostsResult property.
     * 
     */
    public boolean isMarkHostsResult() {
        return markHostsResult;
    }

    /**
     * Sets the value of the markHostsResult property.
     * 
     */
    public void setMarkHostsResult(boolean value) {
        this.markHostsResult = value;
    }

}
