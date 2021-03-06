
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
 *         &lt;element name="account_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execute_sync" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="stats_config" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "accountId",
    "executeSync",
    "statsConfig"
})
@XmlRootElement(name = "cloud_get_statistics")
public class CloudGetStatistics {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "account_id")
    protected String accountId;
    @XmlElement(name = "execute_sync")
    protected boolean executeSync;
    @XmlElement(name = "stats_config")
    protected String statsConfig;

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
     * Gets the value of the accountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountId(String value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the executeSync property.
     * 
     */
    public boolean isExecuteSync() {
        return executeSync;
    }

    /**
     * Sets the value of the executeSync property.
     * 
     */
    public void setExecuteSync(boolean value) {
        this.executeSync = value;
    }

    /**
     * Gets the value of the statsConfig property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatsConfig() {
        return statsConfig;
    }

    /**
     * Sets the value of the statsConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatsConfig(String value) {
        this.statsConfig = value;
    }

}
