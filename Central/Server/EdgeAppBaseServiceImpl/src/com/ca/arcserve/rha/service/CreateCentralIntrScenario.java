
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
 *         &lt;element name="server_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scenario_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="master_hostname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="replica_hostname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="product_type" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="extra_data_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "serverName",
    "username",
    "password",
    "domain",
    "scenarioName",
    "masterHostname",
    "replicaHostname",
    "productType",
    "extraDataList"
})
@XmlRootElement(name = "create_central_intr_scenario")
public class CreateCentralIntrScenario {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "server_name")
    protected String serverName;
    protected String username;
    protected String password;
    protected String domain;
    @XmlElement(name = "scenario_name")
    protected String scenarioName;
    @XmlElement(name = "master_hostname")
    protected String masterHostname;
    @XmlElement(name = "replica_hostname")
    protected String replicaHostname;
    @XmlElement(name = "product_type")
    @XmlSchemaType(name = "unsignedInt")
    protected long productType;
    @XmlElement(name = "extra_data_list")
    protected String extraDataList;

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
     * Gets the value of the serverName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the value of the serverName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerName(String value) {
        this.serverName = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
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
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the scenarioName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * Sets the value of the scenarioName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioName(String value) {
        this.scenarioName = value;
    }

    /**
     * Gets the value of the masterHostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMasterHostname() {
        return masterHostname;
    }

    /**
     * Sets the value of the masterHostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMasterHostname(String value) {
        this.masterHostname = value;
    }

    /**
     * Gets the value of the replicaHostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplicaHostname() {
        return replicaHostname;
    }

    /**
     * Sets the value of the replicaHostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplicaHostname(String value) {
        this.replicaHostname = value;
    }

    /**
     * Gets the value of the productType property.
     * 
     */
    public long getProductType() {
        return productType;
    }

    /**
     * Sets the value of the productType property.
     * 
     */
    public void setProductType(long value) {
        this.productType = value;
    }

    /**
     * Gets the value of the extraDataList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtraDataList() {
        return extraDataList;
    }

    /**
     * Sets the value of the extraDataList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtraDataList(String value) {
        this.extraDataList = value;
    }

}
