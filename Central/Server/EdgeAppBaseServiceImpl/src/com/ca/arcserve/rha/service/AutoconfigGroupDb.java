
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
 *         &lt;element name="group_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="session_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="replica_host_index" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="scenario_data_set" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="relations_map" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "groupId",
    "sessionId",
    "replicaHostIndex",
    "scenarioDataSet",
    "relationsMap"
})
@XmlRootElement(name = "autoconfig_group_db")
public class AutoconfigGroupDb {

    @XmlElement(name = "group_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long groupId;
    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "replica_host_index")
    @XmlSchemaType(name = "unsignedInt")
    protected long replicaHostIndex;
    @XmlElement(name = "scenario_data_set")
    protected String scenarioDataSet;
    @XmlElement(name = "relations_map")
    protected String relationsMap;

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
     * Gets the value of the replicaHostIndex property.
     * 
     */
    public long getReplicaHostIndex() {
        return replicaHostIndex;
    }

    /**
     * Sets the value of the replicaHostIndex property.
     * 
     */
    public void setReplicaHostIndex(long value) {
        this.replicaHostIndex = value;
    }

    /**
     * Gets the value of the scenarioDataSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioDataSet() {
        return scenarioDataSet;
    }

    /**
     * Sets the value of the scenarioDataSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioDataSet(String value) {
        this.scenarioDataSet = value;
    }

    /**
     * Gets the value of the relationsMap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationsMap() {
        return relationsMap;
    }

    /**
     * Sets the value of the relationsMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationsMap(String value) {
        this.relationsMap = value;
    }

}
