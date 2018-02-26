
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
 *         &lt;element name="get_scenario_usersResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getScenarioUsersResult"
})
@XmlRootElement(name = "get_scenario_usersResponse")
public class GetScenarioUsersResponse {

    @XmlElement(name = "get_scenario_usersResult")
    protected String getScenarioUsersResult;

    /**
     * Gets the value of the getScenarioUsersResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetScenarioUsersResult() {
        return getScenarioUsersResult;
    }

    /**
     * Sets the value of the getScenarioUsersResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetScenarioUsersResult(String value) {
        this.getScenarioUsersResult = value;
    }

}
