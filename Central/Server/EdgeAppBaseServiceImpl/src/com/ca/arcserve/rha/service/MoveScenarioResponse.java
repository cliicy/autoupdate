
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
 *         &lt;element name="move_scenarioResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "moveScenarioResult"
})
@XmlRootElement(name = "move_scenarioResponse")
public class MoveScenarioResponse {

    @XmlElement(name = "move_scenarioResult")
    protected boolean moveScenarioResult;

    /**
     * Gets the value of the moveScenarioResult property.
     * 
     */
    public boolean isMoveScenarioResult() {
        return moveScenarioResult;
    }

    /**
     * Sets the value of the moveScenarioResult property.
     * 
     */
    public void setMoveScenarioResult(boolean value) {
        this.moveScenarioResult = value;
    }

}
