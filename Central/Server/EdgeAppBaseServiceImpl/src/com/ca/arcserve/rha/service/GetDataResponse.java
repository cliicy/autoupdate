
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
 *         &lt;element name="get_dataResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="manager_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="last_update_time" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "getDataResult",
    "managerData",
    "lastUpdateTime"
})
@XmlRootElement(name = "get_dataResponse")
public class GetDataResponse {

    @XmlElement(name = "get_dataResult")
    protected boolean getDataResult;
    @XmlElement(name = "manager_data")
    protected String managerData;
    @XmlElement(name = "last_update_time", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger lastUpdateTime;

    /**
     * Gets the value of the getDataResult property.
     * 
     */
    public boolean isGetDataResult() {
        return getDataResult;
    }

    /**
     * Sets the value of the getDataResult property.
     * 
     */
    public void setGetDataResult(boolean value) {
        this.getDataResult = value;
    }

    /**
     * Gets the value of the managerData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagerData() {
        return managerData;
    }

    /**
     * Sets the value of the managerData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagerData(String value) {
        this.managerData = value;
    }

    /**
     * Gets the value of the lastUpdateTime property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Sets the value of the lastUpdateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLastUpdateTime(BigInteger value) {
        this.lastUpdateTime = value;
    }

}
