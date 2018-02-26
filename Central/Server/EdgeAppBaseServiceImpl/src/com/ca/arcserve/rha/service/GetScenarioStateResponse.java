
package com.ca.arcserve.rha.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="get_scenario_stateResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scen_st" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "getScenarioStateResult",
    "scenSt"
})
@XmlRootElement(name = "get_scenario_stateResponse")
public class GetScenarioStateResponse {

    @XmlElement(name = "get_scenario_stateResult")
    protected boolean getScenarioStateResult;
    @XmlElement(name = "scen_st")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenSt;

    /**
     * Gets the value of the getScenarioStateResult property.
     * 
     */
    public boolean isGetScenarioStateResult() {
        return getScenarioStateResult;
    }

    /**
     * Sets the value of the getScenarioStateResult property.
     * 
     */
    public void setGetScenarioStateResult(boolean value) {
        this.getScenarioStateResult = value;
    }

    /**
     * Gets the value of the scenSt property.
     * 
     */
    public long getScenSt() {
        return scenSt;
    }

    /**
     * Sets the value of the scenSt property.
     * 
     */
    public void setScenSt(long value) {
        this.scenSt = value;
    }

}
