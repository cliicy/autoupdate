
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
 *         &lt;element name="sess_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="group_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="group_type" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "sessId",
    "groupName",
    "groupType"
})
@XmlRootElement(name = "create_scenario_group")
public class CreateScenarioGroup {

    @XmlElement(name = "sess_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessId;
    @XmlElement(name = "group_name")
    protected String groupName;
    @XmlElement(name = "group_type")
    @XmlSchemaType(name = "unsignedInt")
    protected long groupType;

    /**
     * Gets the value of the sessId property.
     * 
     */
    public long getSessId() {
        return sessId;
    }

    /**
     * Sets the value of the sessId property.
     * 
     */
    public void setSessId(long value) {
        this.sessId = value;
    }

    /**
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupName(String value) {
        this.groupName = value;
    }

    /**
     * Gets the value of the groupType property.
     * 
     */
    public long getGroupType() {
        return groupType;
    }

    /**
     * Sets the value of the groupType property.
     * 
     */
    public void setGroupType(long value) {
        this.groupType = value;
    }

}
