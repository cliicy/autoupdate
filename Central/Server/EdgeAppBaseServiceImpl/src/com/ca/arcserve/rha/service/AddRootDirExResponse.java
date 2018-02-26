
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
 *         &lt;element name="add_root_dir_exResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="new_root_dir_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "addRootDirExResult",
    "scenarioData",
    "newRootDirId"
})
@XmlRootElement(name = "add_root_dir_exResponse")
public class AddRootDirExResponse {

    @XmlElement(name = "add_root_dir_exResult")
    protected boolean addRootDirExResult;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "new_root_dir_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long newRootDirId;

    /**
     * Gets the value of the addRootDirExResult property.
     * 
     */
    public boolean isAddRootDirExResult() {
        return addRootDirExResult;
    }

    /**
     * Sets the value of the addRootDirExResult property.
     * 
     */
    public void setAddRootDirExResult(boolean value) {
        this.addRootDirExResult = value;
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
     * Gets the value of the newRootDirId property.
     * 
     */
    public long getNewRootDirId() {
        return newRootDirId;
    }

    /**
     * Sets the value of the newRootDirId property.
     * 
     */
    public void setNewRootDirId(long value) {
        this.newRootDirId = value;
    }

}
