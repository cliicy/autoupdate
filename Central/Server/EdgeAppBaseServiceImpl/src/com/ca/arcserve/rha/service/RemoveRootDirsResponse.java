
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
 *         &lt;element name="remove_root_dirsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "removeRootDirsResult",
    "scenarioData"
})
@XmlRootElement(name = "remove_root_dirsResponse")
public class RemoveRootDirsResponse {

    @XmlElement(name = "remove_root_dirsResult")
    protected boolean removeRootDirsResult;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;

    /**
     * Gets the value of the removeRootDirsResult property.
     * 
     */
    public boolean isRemoveRootDirsResult() {
        return removeRootDirsResult;
    }

    /**
     * Sets the value of the removeRootDirsResult property.
     * 
     */
    public void setRemoveRootDirsResult(boolean value) {
        this.removeRootDirsResult = value;
    }

    /**
     * Gets the value of the scenarioData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioData() {
        return scenarioData;
    }

    /**
     * Sets the value of the scenarioData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioData(String value) {
        this.scenarioData = value;
    }

}
