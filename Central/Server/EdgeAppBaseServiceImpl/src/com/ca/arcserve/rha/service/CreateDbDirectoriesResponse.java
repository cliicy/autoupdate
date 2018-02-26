
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
 *         &lt;element name="create_db_directoriesResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="new_db_dir_ids" type="{http://ca.com/}ArrayOfUnsignedInt" minOccurs="0"/>
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
    "createDbDirectoriesResult",
    "scenarioData",
    "newDbDirIds"
})
@XmlRootElement(name = "create_db_directoriesResponse")
public class CreateDbDirectoriesResponse {

    @XmlElement(name = "create_db_directoriesResult")
    protected boolean createDbDirectoriesResult;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "new_db_dir_ids")
    protected ArrayOfUnsignedInt newDbDirIds;

    /**
     * Gets the value of the createDbDirectoriesResult property.
     * 
     */
    public boolean isCreateDbDirectoriesResult() {
        return createDbDirectoriesResult;
    }

    /**
     * Sets the value of the createDbDirectoriesResult property.
     * 
     */
    public void setCreateDbDirectoriesResult(boolean value) {
        this.createDbDirectoriesResult = value;
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
     * Gets the value of the newDbDirIds property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUnsignedInt }
     *     
     */
    public ArrayOfUnsignedInt getNewDbDirIds() {
        return newDbDirIds;
    }

    /**
     * Sets the value of the newDbDirIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUnsignedInt }
     *     
     */
    public void setNewDbDirIds(ArrayOfUnsignedInt value) {
        this.newDbDirIds = value;
    }

}
