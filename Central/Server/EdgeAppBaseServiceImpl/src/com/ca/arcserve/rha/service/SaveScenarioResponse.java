
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
 *         &lt;element name="save_scenarioResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scenario_data_str" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "saveScenarioResult",
    "scenarioDataStr",
    "signature"
})
@XmlRootElement(name = "save_scenarioResponse")
public class SaveScenarioResponse {

    @XmlElement(name = "save_scenarioResult")
    protected boolean saveScenarioResult;
    @XmlElement(name = "scenario_data_str")
    protected String scenarioDataStr;
    @XmlElement(required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger signature;

    /**
     * Gets the value of the saveScenarioResult property.
     * 
     */
    public boolean isSaveScenarioResult() {
        return saveScenarioResult;
    }

    /**
     * Sets the value of the saveScenarioResult property.
     * 
     */
    public void setSaveScenarioResult(boolean value) {
        this.saveScenarioResult = value;
    }

    /**
     * Gets the value of the scenarioDataStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScenarioDataStr() {
        return scenarioDataStr;
    }

    /**
     * Sets the value of the scenarioDataStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScenarioDataStr(String value) {
        this.scenarioDataStr = value;
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
