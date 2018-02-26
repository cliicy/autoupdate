
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
 *         &lt;element name="add_replica_exResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="host_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "addReplicaExResult",
    "scenarioData",
    "hostList"
})
@XmlRootElement(name = "add_replica_exResponse")
public class AddReplicaExResponse {

    @XmlElement(name = "add_replica_exResult")
    protected boolean addReplicaExResult;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "host_list")
    protected String hostList;

    /**
     * Gets the value of the addReplicaExResult property.
     * 
     */
    public boolean isAddReplicaExResult() {
        return addReplicaExResult;
    }

    /**
     * Sets the value of the addReplicaExResult property.
     * 
     */
    public void setAddReplicaExResult(boolean value) {
        this.addReplicaExResult = value;
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
     * Gets the value of the hostList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostList() {
        return hostList;
    }

    /**
     * Sets the value of the hostList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostList(String value) {
        this.hostList = value;
    }

}
