
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
 *         &lt;element name="group_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "sessionId",
    "groupId",
    "groupData"
})
@XmlRootElement(name = "remove_scenario_group")
public class RemoveScenarioGroup {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "group_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long groupId;
    @XmlElement(name = "group_data")
    protected String groupData;

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
     * Gets the value of the groupId property.
     * 
     */
    public long getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     */
    public void setGroupId(long value) {
        this.groupId = value;
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
