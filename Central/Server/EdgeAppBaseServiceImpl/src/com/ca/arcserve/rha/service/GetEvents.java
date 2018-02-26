
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
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="is_sync" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="host_id" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
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
    "id",
    "isSync",
    "hostId"
})
@XmlRootElement(name = "Get_Events")
public class GetEvents {

    @XmlElement(name = "ID")
    @XmlSchemaType(name = "unsignedInt")
    protected long id;
    @XmlElement(name = "is_sync")
    protected boolean isSync;
    @XmlElement(name = "host_id")
    protected Object hostId;

    /**
     * Gets the value of the id property.
     * 
     */
    public long getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setID(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the isSync property.
     * 
     */
    public boolean isIsSync() {
        return isSync;
    }

    /**
     * Sets the value of the isSync property.
     * 
     */
    public void setIsSync(boolean value) {
        this.isSync = value;
    }

    /**
     * Gets the value of the hostId property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getHostId() {
        return hostId;
    }

    /**
     * Sets the value of the hostId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setHostId(Object value) {
        this.hostId = value;
    }

}
