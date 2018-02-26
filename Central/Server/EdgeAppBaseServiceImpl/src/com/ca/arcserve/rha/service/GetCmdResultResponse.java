
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
 *         &lt;element name="get_cmd_resultResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mgr_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getCmdResultResult",
    "mgrData"
})
@XmlRootElement(name = "get_cmd_resultResponse")
public class GetCmdResultResponse {

    @XmlElement(name = "get_cmd_resultResult")
    protected boolean getCmdResultResult;
    @XmlElement(name = "mgr_data")
    protected String mgrData;

    /**
     * Gets the value of the getCmdResultResult property.
     * 
     */
    public boolean isGetCmdResultResult() {
        return getCmdResultResult;
    }

    /**
     * Sets the value of the getCmdResultResult property.
     * 
     */
    public void setGetCmdResultResult(boolean value) {
        this.getCmdResultResult = value;
    }

    /**
     * Gets the value of the mgrData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMgrData() {
        return mgrData;
    }

    /**
     * Sets the value of the mgrData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMgrData(String value) {
        this.mgrData = value;
    }

}
