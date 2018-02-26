
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
 *         &lt;element name="get_oracle_instance_listResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ora_inst_list" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="err_messages" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getOracleInstanceListResult",
    "oraInstList",
    "errMessages"
})
@XmlRootElement(name = "get_oracle_instance_listResponse")
public class GetOracleInstanceListResponse {

    @XmlElement(name = "get_oracle_instance_listResult")
    protected boolean getOracleInstanceListResult;
    @XmlElement(name = "ora_inst_list")
    protected String oraInstList;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the getOracleInstanceListResult property.
     * 
     */
    public boolean isGetOracleInstanceListResult() {
        return getOracleInstanceListResult;
    }

    /**
     * Sets the value of the getOracleInstanceListResult property.
     * 
     */
    public void setGetOracleInstanceListResult(boolean value) {
        this.getOracleInstanceListResult = value;
    }

    /**
     * Gets the value of the oraInstList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOraInstList() {
        return oraInstList;
    }

    /**
     * Sets the value of the oraInstList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOraInstList(String value) {
        this.oraInstList = value;
    }

    /**
     * Gets the value of the errMessages property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrMessages() {
        return errMessages;
    }

    /**
     * Sets the value of the errMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrMessages(String value) {
        this.errMessages = value;
    }

}
