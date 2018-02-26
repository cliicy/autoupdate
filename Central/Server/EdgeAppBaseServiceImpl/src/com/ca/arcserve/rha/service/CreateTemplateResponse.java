
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
 *         &lt;element name="create_templateResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "createTemplateResult",
    "scenarioData",
    "groupData"
})
@XmlRootElement(name = "create_templateResponse")
public class CreateTemplateResponse {

    @XmlElement(name = "create_templateResult")
    protected boolean createTemplateResult;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "group_data")
    protected String groupData;

    /**
     * Gets the value of the createTemplateResult property.
     * 
     */
    public boolean isCreateTemplateResult() {
        return createTemplateResult;
    }

    /**
     * Sets the value of the createTemplateResult property.
     * 
     */
    public void setCreateTemplateResult(boolean value) {
        this.createTemplateResult = value;
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
