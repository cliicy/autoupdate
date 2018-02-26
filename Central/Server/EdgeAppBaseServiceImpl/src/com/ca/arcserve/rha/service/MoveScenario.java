
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
 *         &lt;element name="session_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="scenario_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="group_id_from" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="group_id_to" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "sessionId",
    "scenarioId",
    "groupIdFrom",
    "groupIdTo"
})
@XmlRootElement(name = "move_scenario")
public class MoveScenario {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenario_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenarioId;
    @XmlElement(name = "group_id_from")
    @XmlSchemaType(name = "unsignedInt")
    protected long groupIdFrom;
    @XmlElement(name = "group_id_to")
    @XmlSchemaType(name = "unsignedInt")
    protected long groupIdTo;

    /**
     * Gets the value of the sessionId property.
     * 
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     */
    public void setSessionId(long value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the scenarioId property.
     * 
     */
    public long getScenarioId() {
        return scenarioId;
    }

    /**
     * Sets the value of the scenarioId property.
     * 
     */
    public void setScenarioId(long value) {
        this.scenarioId = value;
    }

    /**
     * Gets the value of the groupIdFrom property.
     * 
     */
    public long getGroupIdFrom() {
        return groupIdFrom;
    }

    /**
     * Sets the value of the groupIdFrom property.
     * 
     */
    public void setGroupIdFrom(long value) {
        this.groupIdFrom = value;
    }

    /**
     * Gets the value of the groupIdTo property.
     * 
     */
    public long getGroupIdTo() {
        return groupIdTo;
    }

    /**
     * Sets the value of the groupIdTo property.
     * 
     */
    public void setGroupIdTo(long value) {
        this.groupIdTo = value;
    }

}
