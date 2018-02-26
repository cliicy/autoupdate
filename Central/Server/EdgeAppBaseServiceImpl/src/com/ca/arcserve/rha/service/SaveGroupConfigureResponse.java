
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
 *         &lt;element name="save_group_configureResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="group_data_str" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signature" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "saveGroupConfigureResult",
    "groupDataStr",
    "signature"
})
@XmlRootElement(name = "save_group_configureResponse")
public class SaveGroupConfigureResponse {

    @XmlElement(name = "save_group_configureResult")
    protected boolean saveGroupConfigureResult;
    @XmlElement(name = "group_data_str")
    protected String groupDataStr;
    @XmlElement(required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger signature;

    /**
     * Gets the value of the saveGroupConfigureResult property.
     * 
     */
    public boolean isSaveGroupConfigureResult() {
        return saveGroupConfigureResult;
    }

    /**
     * Sets the value of the saveGroupConfigureResult property.
     * 
     */
    public void setSaveGroupConfigureResult(boolean value) {
        this.saveGroupConfigureResult = value;
    }

    /**
     * Gets the value of the groupDataStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupDataStr() {
        return groupDataStr;
    }

    /**
     * Sets the value of the groupDataStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupDataStr(String value) {
        this.groupDataStr = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSignature(BigInteger value) {
        this.signature = value;
    }

}
