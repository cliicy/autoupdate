
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
 *         &lt;element name="scen_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="arc_upd" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "scenId",
    "arcUpd"
})
@XmlRootElement(name = "remove_scenario")
public class RemoveScenario {

    @XmlElement(name = "sess_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessId;
    @XmlElement(name = "scen_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenId;
    @XmlElement(name = "arc_upd")
    protected boolean arcUpd;

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
     * Gets the value of the scenId property.
     * 
     */
    public long getScenId() {
        return scenId;
    }

    /**
     * Sets the value of the scenId property.
     * 
     */
    public void setScenId(long value) {
        this.scenId = value;
    }

    /**
     * Gets the value of the arcUpd property.
     * 
     */
    public boolean isArcUpd() {
        return arcUpd;
    }

    /**
     * Sets the value of the arcUpd property.
     * 
     */
    public void setArcUpd(boolean value) {
        this.arcUpd = value;
    }

}
