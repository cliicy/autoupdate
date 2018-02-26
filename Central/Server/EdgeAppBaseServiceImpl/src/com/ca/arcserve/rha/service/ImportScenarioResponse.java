
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
 *         &lt;element name="import_scenarioResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="scen_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="why_not_reason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "importScenarioResult",
    "scenId",
    "whyNotReason"
})
@XmlRootElement(name = "import_scenarioResponse")
public class ImportScenarioResponse {

    @XmlElement(name = "import_scenarioResult")
    protected boolean importScenarioResult;
    @XmlElement(name = "scen_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenId;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the importScenarioResult property.
     * 
     */
    public boolean isImportScenarioResult() {
        return importScenarioResult;
    }

    /**
     * Sets the value of the importScenarioResult property.
     * 
     */
    public void setImportScenarioResult(boolean value) {
        this.importScenarioResult = value;
    }

    /**
     * Gets the value of the scenId property.
     * 
     */
    public long getScenId() {
        return scenId;
    }

    /**
     * Sets the value of the scenId property.
     * 
     */
    public void setScenId(long value) {
        this.scenId = value;
    }

    /**
     * Gets the value of the whyNotReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhyNotReason() {
        return whyNotReason;
    }

    /**
     * Sets the value of the whyNotReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhyNotReason(String value) {
        this.whyNotReason = value;
    }

}
