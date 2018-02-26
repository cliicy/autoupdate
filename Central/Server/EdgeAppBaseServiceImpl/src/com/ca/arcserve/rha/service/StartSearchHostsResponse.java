
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
 *         &lt;element name="start_search_hostsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "startSearchHostsResult"
})
@XmlRootElement(name = "start_search_hostsResponse")
public class StartSearchHostsResponse {

    @XmlElement(name = "start_search_hostsResult")
    protected boolean startSearchHostsResult;

    /**
     * Gets the value of the startSearchHostsResult property.
     * 
     */
    public boolean isStartSearchHostsResult() {
        return startSearchHostsResult;
    }

    /**
     * Sets the value of the startSearchHostsResult property.
     * 
     */
    public void setStartSearchHostsResult(boolean value) {
        this.startSearchHostsResult = value;
    }

}
