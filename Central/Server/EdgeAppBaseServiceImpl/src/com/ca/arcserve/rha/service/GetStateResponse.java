
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
 *         &lt;element name="get_stateResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getStateResult"
})
@XmlRootElement(name = "get_stateResponse")
public class GetStateResponse {

    @XmlElement(name = "get_stateResult")
    protected int getStateResult;

    /**
     * Gets the value of the getStateResult property.
     * 
     */
    public int getGetStateResult() {
        return getStateResult;
    }

    /**
     * Sets the value of the getStateResult property.
     * 
     */
    public void setGetStateResult(int value) {
        this.getStateResult = value;
    }

}