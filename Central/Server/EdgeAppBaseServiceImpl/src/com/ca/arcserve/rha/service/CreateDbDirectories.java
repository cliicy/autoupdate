
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
 *         &lt;element name="db_type" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="db_autodiscovery" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "dbType",
    "dbAutodiscovery"
})
@XmlRootElement(name = "create_db_directories")
public class CreateDbDirectories {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenario_data")
    protected String scenarioData;
    @XmlElement(name = "db_type")
    @XmlSchemaType(name = "unsignedInt")
    protected long dbType;
    @XmlElement(name = "db_autodiscovery")
    protected String dbAutodiscovery;

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
     * Gets the value of the dbType property.
     * 
     */
    public long getDbType() {
        return dbType;
    }

    /**
     * Sets the value of the dbType property.
     * 
     */
    public void setDbType(long value) {
        this.dbType = value;
    }

    /**
     * Gets the value of the dbAutodiscovery property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDbAutodiscovery() {
        return dbAutodiscovery;
    }

    /**
     * Sets the value of the dbAutodiscovery property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDbAutodiscovery(String value) {
        this.dbAutodiscovery = value;
    }

}
