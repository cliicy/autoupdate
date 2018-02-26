
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
 *         &lt;element name="mst_host" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mst_port" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/>
 *         &lt;element name="usr_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "mstHost",
    "mstPort",
    "usrName",
    "password"
})
@XmlRootElement(name = "create_session_bab")
public class CreateSessionBab {

    @XmlElement(name = "mst_host")
    protected String mstHost;
    @XmlElement(name = "mst_port")
    @XmlSchemaType(name = "unsignedShort")
    protected int mstPort;
    @XmlElement(name = "usr_name")
    protected String usrName;
    protected String password;

    /**
     * Gets the value of the mstHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMstHost() {
        return mstHost;
    }

    /**
     * Sets the value of the mstHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMstHost(String value) {
        this.mstHost = value;
    }

    /**
     * Gets the value of the mstPort property.
     * 
     */
    public int getMstPort() {
        return mstPort;
    }

    /**
     * Sets the value of the mstPort property.
     * 
     */
    public void setMstPort(int value) {
        this.mstPort = value;
    }

    /**
     * Gets the value of the usrName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsrName() {
        return usrName;
    }

    /**
     * Sets the value of the usrName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsrName(String value) {
        this.usrName = value;
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

}
