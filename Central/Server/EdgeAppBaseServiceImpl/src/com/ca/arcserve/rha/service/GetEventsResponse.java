
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
 *         &lt;element name="Get_EventsResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getEventsResult"
})
@XmlRootElement(name = "Get_EventsResponse")
public class GetEventsResponse {

    @XmlElement(name = "Get_EventsResult")
    protected int getEventsResult;

    /**
     * Gets the value of the getEventsResult property.
     * 
     */
    public int getGetEventsResult() {
        return getEventsResult;
    }

    /**
     * Sets the value of the getEventsResult property.
     * 
     */
    public void setGetEventsResult(int value) {
        this.getEventsResult = value;
    }

}
