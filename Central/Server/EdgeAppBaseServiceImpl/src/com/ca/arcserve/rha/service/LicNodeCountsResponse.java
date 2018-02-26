
package com.ca.arcserve.rha.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="lic_node_countsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ar_num" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cdp_num" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "licNodeCountsResult",
    "arNum",
    "cdpNum"
})
@XmlRootElement(name = "lic_node_countsResponse")
public class LicNodeCountsResponse {

    @XmlElement(name = "lic_node_countsResult")
    protected boolean licNodeCountsResult;
    @XmlElement(name = "ar_num")
    protected int arNum;
    @XmlElement(name = "cdp_num")
    protected int cdpNum;

    /**
     * Gets the value of the licNodeCountsResult property.
     * 
     */
    public boolean isLicNodeCountsResult() {
        return licNodeCountsResult;
    }

    /**
     * Sets the value of the licNodeCountsResult property.
     * 
     */
    public void setLicNodeCountsResult(boolean value) {
        this.licNodeCountsResult = value;
    }

    /**
     * Gets the value of the arNum property.
     * 
     */
    public int getArNum() {
        return arNum;
    }

    /**
     * Sets the value of the arNum property.
     * 
     */
    public void setArNum(int value) {
        this.arNum = value;
    }

    /**
     * Gets the value of the cdpNum property.
     * 
     */
    public int getCdpNum() {
        return cdpNum;
    }

    /**
     * Sets the value of the cdpNum property.
     * 
     */
    public void setCdpNum(int value) {
        this.cdpNum = value;
    }

}
