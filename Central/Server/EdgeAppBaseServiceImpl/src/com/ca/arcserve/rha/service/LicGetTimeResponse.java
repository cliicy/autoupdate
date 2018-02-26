
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
 *         &lt;element name="lic_get_timeResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="exp_time" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
 *         &lt;element name="mnt_time" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "licGetTimeResult",
    "expTime",
    "mntTime"
})
@XmlRootElement(name = "lic_get_timeResponse")
public class LicGetTimeResponse {

    @XmlElement(name = "lic_get_timeResult")
    protected boolean licGetTimeResult;
    @XmlElement(name = "exp_time", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger expTime;
    @XmlElement(name = "mnt_time", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger mntTime;

    /**
     * Gets the value of the licGetTimeResult property.
     * 
     */
    public boolean isLicGetTimeResult() {
        return licGetTimeResult;
    }

    /**
     * Sets the value of the licGetTimeResult property.
     * 
     */
    public void setLicGetTimeResult(boolean value) {
        this.licGetTimeResult = value;
    }

    /**
     * Gets the value of the expTime property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExpTime() {
        return expTime;
    }

    /**
     * Sets the value of the expTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExpTime(BigInteger value) {
        this.expTime = value;
    }

    /**
     * Gets the value of the mntTime property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMntTime() {
        return mntTime;
    }

    /**
     * Sets the value of the mntTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMntTime(BigInteger value) {
        this.mntTime = value;
    }

}
