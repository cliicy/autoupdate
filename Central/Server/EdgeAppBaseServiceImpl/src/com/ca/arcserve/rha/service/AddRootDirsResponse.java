
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
 *         &lt;element name="add_root_dirsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="new_root_dir_ids" type="{http://ca.com/}ArrayOfUnsignedInt" minOccurs="0"/>
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
    "addRootDirsResult",
    "scenarioData",
    "newRootDirIds"
})
@XmlRootElement(name = "add_root_dirsResponse")
public class AddRootDirsResponse {

    @XmlElement(name = "add_root_dirsResult")
    protected boolean addRootDirsResult;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "new_root_dir_ids")
    protected ArrayOfUnsignedInt newRootDirIds;

    /**
     * Gets the value of the addRootDirsResult property.
     * 
     */
    public boolean isAddRootDirsResult() {
        return addRootDirsResult;
    }

    /**
     * Sets the value of the addRootDirsResult property.
     * 
     */
    public void setAddRootDirsResult(boolean value) {
        this.addRootDirsResult = value;
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
     * Gets the value of the newRootDirIds property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUnsignedInt }
     *     
     */
    public ArrayOfUnsignedInt getNewRootDirIds() {
        return newRootDirIds;
    }

    /**
     * Sets the value of the newRootDirIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUnsignedInt }
     *     
     */
    public void setNewRootDirIds(ArrayOfUnsignedInt value) {
        this.newRootDirIds = value;
    }

}
