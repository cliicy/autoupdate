
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
 *         &lt;element name="add_replicaResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="new_replica_index" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "addReplicaResult",
    "scenarioData",
    "newReplicaIndex"
})
@XmlRootElement(name = "add_replicaResponse")
public class AddReplicaResponse {

    @XmlElement(name = "add_replicaResult")
    protected boolean addReplicaResult;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "new_replica_index")
    @XmlSchemaType(name = "unsignedInt")
    protected long newReplicaIndex;

    /**
     * Gets the value of the addReplicaResult property.
     * 
     */
    public boolean isAddReplicaResult() {
        return addReplicaResult;
    }

    /**
     * Sets the value of the addReplicaResult property.
     * 
     */
    public void setAddReplicaResult(boolean value) {
        this.addReplicaResult = value;
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
     * Gets the value of the newReplicaIndex property.
     * 
     */
    public long getNewReplicaIndex() {
        return newReplicaIndex;
    }

    /**
     * Sets the value of the newReplicaIndex property.
     * 
     */
    public void setNewReplicaIndex(long value) {
        this.newReplicaIndex = value;
    }

}
