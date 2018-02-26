
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
 *         &lt;element name="scenario_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="host_index" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="root_dir_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="obj_label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prop_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="new_value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="check_prompt" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "scenarioData",
    "hostIndex",
    "rootDirId",
    "objLabel",
    "propId",
    "newValue",
    "checkPrompt"
})
@XmlRootElement(name = "update_prop")
public class UpdateProp {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "host_index")
    @XmlSchemaType(name = "unsignedInt")
    protected long hostIndex;
    @XmlElement(name = "root_dir_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long rootDirId;
    @XmlElement(name = "obj_label")
    protected String objLabel;
    @XmlElement(name = "prop_id")
    protected String propId;
    @XmlElement(name = "new_value")
    protected String newValue;
    @XmlElement(name = "check_prompt")
    protected boolean checkPrompt;

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
     * Gets the value of the hostIndex property.
     * 
     */
    public long getHostIndex() {
        return hostIndex;
    }

    /**
     * Sets the value of the hostIndex property.
     * 
     */
    public void setHostIndex(long value) {
        this.hostIndex = value;
    }

    /**
     * Gets the value of the rootDirId property.
     * 
     */
    public long getRootDirId() {
        return rootDirId;
    }

    /**
     * Sets the value of the rootDirId property.
     * 
     */
    public void setRootDirId(long value) {
        this.rootDirId = value;
    }

    /**
     * Gets the value of the objLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjLabel() {
        return objLabel;
    }

    /**
     * Sets the value of the objLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjLabel(String value) {
        this.objLabel = value;
    }

    /**
     * Gets the value of the propId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropId() {
        return propId;
    }

    /**
     * Sets the value of the propId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropId(String value) {
        this.propId = value;
    }

    /**
     * Gets the value of the newValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * Sets the value of the newValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewValue(String value) {
        this.newValue = value;
    }

    /**
     * Gets the value of the checkPrompt property.
     * 
     */
    public boolean isCheckPrompt() {
        return checkPrompt;
    }

    /**
     * Sets the value of the checkPrompt property.
     * 
     */
    public void setCheckPrompt(boolean value) {
        this.checkPrompt = value;
    }

}
