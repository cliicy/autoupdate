
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
    "scenId"
})
@XmlRootElement(name = "is_bab_credentials")
public class IsBabCredentials {

    @XmlElement(name = "sess_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessId;
    @XmlElement(name = "scen_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenId;

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

}
