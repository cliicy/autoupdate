
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
 *         &lt;element name="execute_actionResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="result_data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="error_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "executeActionResult",
    "resultData",
    "errorMessage"
})
@XmlRootElement(name = "execute_actionResponse")
public class ExecuteActionResponse {

    @XmlElement(name = "execute_actionResult")
    protected boolean executeActionResult;
    @XmlElement(name = "result_data")
    protected String resultData;
    @XmlElement(name = "error_message")
    protected String errorMessage;

    /**
     * Gets the value of the executeActionResult property.
     * 
     */
    public boolean isExecuteActionResult() {
        return executeActionResult;
    }

    /**
     * Sets the value of the executeActionResult property.
     * 
     */
    public void setExecuteActionResult(boolean value) {
        this.executeActionResult = value;
    }

    /**
     * Gets the value of the resultData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultData() {
        return resultData;
    }

    /**
     * Sets the value of the resultData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultData(String value) {
        this.resultData = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

}
