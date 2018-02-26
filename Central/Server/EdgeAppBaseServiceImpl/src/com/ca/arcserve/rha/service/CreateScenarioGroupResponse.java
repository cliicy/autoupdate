
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
 *         &lt;element name="create_scenario_groupResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_group_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "createScenarioGroupResult",
    "scenarioGroupData"
})
@XmlRootElement(name = "create_scenario_groupResponse")
public class CreateScenarioGroupResponse {

    @XmlElement(name = "create_scenario_groupResult")
    protected boolean createScenarioGroupResult;
    @XmlElement(name = "scenario_group_data")
    protected String scenarioGroupData;

    /**
     * Gets the value of the createScenarioGroupResult property.
     * 
     */
    public boolean isCreateScenarioGroupResult() {
        return createScenarioGroupResult;
    }

    /**
     * Sets the value of the createScenarioGroupResult property.
     * 
     */
    public void setCreateScenarioGroupResult(boolean value) {
        this.createScenarioGroupResult = value;
    }

    /**
     * Gets the value of the scenarioGroupData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioGroupData() {
        return scenarioGroupData;
    }

    /**
     * Sets the value of the scenarioGroupData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioGroupData(String value) {
        this.scenarioGroupData = value;
    }

}
