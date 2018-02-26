
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
 *         &lt;element name="host_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ip_string" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="host_port" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/>
 *         &lt;element name="host_key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reboot_host" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reboot_ip_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "hostName",
    "ipString",
    "hostPort",
    "hostKey",
    "rebootHost",
    "rebootIpList"
})
@XmlRootElement(name = "prepare_for_reboot")
public class PrepareForReboot {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "host_name")
    protected String hostName;
    @XmlElement(name = "ip_string")
    protected String ipString;
    @XmlElement(name = "host_port")
    @XmlSchemaType(name = "unsignedShort")
    protected int hostPort;
    @XmlElement(name = "host_key")
    protected String hostKey;
    @XmlElement(name = "reboot_host")
    protected String rebootHost;
    @XmlElement(name = "reboot_ip_list")
    protected String rebootIpList;

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
     * Gets the value of the hostName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the value of the hostName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostName(String value) {
        this.hostName = value;
    }

    /**
     * Gets the value of the ipString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpString() {
        return ipString;
    }

    /**
     * Sets the value of the ipString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpString(String value) {
        this.ipString = value;
    }

    /**
     * Gets the value of the hostPort property.
     * 
     */
    public int getHostPort() {
        return hostPort;
    }

    /**
     * Sets the value of the hostPort property.
     * 
     */
    public void setHostPort(int value) {
        this.hostPort = value;
    }

    /**
     * Gets the value of the hostKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostKey() {
        return hostKey;
    }

    /**
     * Sets the value of the hostKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostKey(String value) {
        this.hostKey = value;
    }

    /**
     * Gets the value of the rebootHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRebootHost() {
        return rebootHost;
    }

    /**
     * Sets the value of the rebootHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRebootHost(String value) {
        this.rebootHost = value;
    }

    /**
     * Gets the value of the rebootIpList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRebootIpList() {
        return rebootIpList;
    }

    /**
     * Sets the value of the rebootIpList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRebootIpList(String value) {
        this.rebootIpList = value;
    }

}