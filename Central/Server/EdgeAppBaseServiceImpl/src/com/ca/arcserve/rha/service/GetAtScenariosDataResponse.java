
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
 *         &lt;element name="get_at_scenarios_dataResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scens_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getAtScenariosDataResult",
    "scensData"
})
@XmlRootElement(name = "get_at_scenarios_dataResponse")
public class GetAtScenariosDataResponse {

    @XmlElement(name = "get_at_scenarios_dataResult")
    protected boolean getAtScenariosDataResult;
    @XmlElement(name = "scens_data")
    protected String scensData;

    /**
     * Gets the value of the getAtScenariosDataResult property.
     * 
     */
    public boolean isGetAtScenariosDataResult() {
        return getAtScenariosDataResult;
    }

    /**
     * Sets the value of the getAtScenariosDataResult property.
     * 
     */
    public void setGetAtScenariosDataResult(boolean value) {
        this.getAtScenariosDataResult = value;
    }

    /**
     * Gets the value of the scensData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScensData() {
        return scensData;
    }

    /**
     * Sets the value of the scensData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScensData(String value) {
        this.scensData = value;
    }

}
