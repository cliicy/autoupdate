
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
 *         &lt;element name="discover_topology" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="group_or_scen_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sce_to_discover_array" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="db_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="user_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="db_type" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "discoverTopology",
    "groupOrScenData",
    "sceToDiscoverArray",
    "dbName",
    "userName",
    "password",
    "dbType"
})
@XmlRootElement(name = "group_db_autodiscovery")
public class GroupDbAutodiscovery {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "discover_topology")
    protected boolean discoverTopology;
    @XmlElement(name = "group_or_scen_data")
    protected String groupOrScenData;
    @XmlElement(name = "sce_to_discover_array")
    protected String sceToDiscoverArray;
    @XmlElement(name = "db_name")
    protected String dbName;
    @XmlElement(name = "user_name")
    protected String userName;
    protected String password;
    @XmlElement(name = "db_type")
    @XmlSchemaType(name = "unsignedInt")
    protected long dbType;

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
     * Gets the value of the discoverTopology property.
     * 
     */
    public boolean isDiscoverTopology() {
        return discoverTopology;
    }

    /**
     * Sets the value of the discoverTopology property.
     * 
     */
    public void setDiscoverTopology(boolean value) {
        this.discoverTopology = value;
    }

    /**
     * Gets the value of the groupOrScenData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupOrScenData() {
        return groupOrScenData;
    }

    /**
     * Sets the value of the groupOrScenData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupOrScenData(String value) {
        this.groupOrScenData = value;
    }

    /**
     * Gets the value of the sceToDiscoverArray property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSceToDiscoverArray() {
        return sceToDiscoverArray;
    }

    /**
     * Sets the value of the sceToDiscoverArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSceToDiscoverArray(String value) {
        this.sceToDiscoverArray = value;
    }

    /**
     * Gets the value of the dbName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Sets the value of the dbName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDbName(String value) {
        this.dbName = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
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

}
