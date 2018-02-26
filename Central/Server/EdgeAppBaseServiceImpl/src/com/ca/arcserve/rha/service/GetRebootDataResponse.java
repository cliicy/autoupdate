
package com.ca.arcserve.rha.service;

import java.math.BigInteger;
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
 *         &lt;element name="get_reboot_dataResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="reboot_manager_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="last_update_ticks" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "getRebootDataResult",
    "rebootManagerData",
    "lastUpdateTicks"
})
@XmlRootElement(name = "get_reboot_dataResponse")
public class GetRebootDataResponse {

    @XmlElement(name = "get_reboot_dataResult")
    protected boolean getRebootDataResult;
    @XmlElement(name = "reboot_manager_data")
    protected String rebootManagerData;
    @XmlElement(name = "last_update_ticks", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger lastUpdateTicks;

    /**
     * Gets the value of the getRebootDataResult property.
     * 
     */
    public boolean isGetRebootDataResult() {
        return getRebootDataResult;
    }

    /**
     * Sets the value of the getRebootDataResult property.
     * 
     */
    public void setGetRebootDataResult(boolean value) {
        this.getRebootDataResult = value;
    }

    /**
     * Gets the value of the rebootManagerData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRebootManagerData() {
        return rebootManagerData;
    }

    /**
     * Sets the value of the rebootManagerData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRebootManagerData(String value) {
        this.rebootManagerData = value;
    }

    /**
     * Gets the value of the lastUpdateTicks property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLastUpdateTicks() {
        return lastUpdateTicks;
    }

    /**
     * Sets the value of the lastUpdateTicks property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLastUpdateTicks(BigInteger value) {
        this.lastUpdateTicks = value;
    }

}
