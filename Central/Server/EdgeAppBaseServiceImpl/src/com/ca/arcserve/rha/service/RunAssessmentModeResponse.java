
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
 *         &lt;element name="run_assessment_modeResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "runAssessmentModeResult",
    "errMessages"
})
@XmlRootElement(name = "run_assessment_modeResponse")
public class RunAssessmentModeResponse {

    @XmlElement(name = "run_assessment_modeResult")
    protected boolean runAssessmentModeResult;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the runAssessmentModeResult property.
     * 
     */
    public boolean isRunAssessmentModeResult() {
        return runAssessmentModeResult;
    }

    /**
     * Sets the value of the runAssessmentModeResult property.
     * 
     */
    public void setRunAssessmentModeResult(boolean value) {
        this.runAssessmentModeResult = value;
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
