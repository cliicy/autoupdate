
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
 *         &lt;element name="clone_scenarioResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="group_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "cloneScenarioResult",
    "scenarioData",
    "groupData"
})
@XmlRootElement(name = "clone_scenarioResponse")
public class CloneScenarioResponse {

    @XmlElement(name = "clone_scenarioResult")
    protected boolean cloneScenarioResult;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "group_data")
    protected String groupData;

    /**
     * Gets the value of the cloneScenarioResult property.
     * 
     */
    public boolean isCloneScenarioResult() {
        return cloneScenarioResult;
    }

    /**
     * Sets the value of the cloneScenarioResult property.
     * 
     */
    public void setCloneScenarioResult(boolean value) {
        this.cloneScenarioResult = value;
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

    /**
     * Gets the value of the groupData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupData() {
        return groupData;
    }

    /**
     * Sets the value of the groupData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupData(String value) {
        this.groupData = value;
    }

}
