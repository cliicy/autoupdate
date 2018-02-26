
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
 *         &lt;element name="check_p2v_scenario_stateResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="is_run_reverse" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="vm_starting_reason" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="async_res_id" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "checkP2VScenarioStateResult",
    "isRunReverse",
    "vmStartingReason",
    "asyncResId",
    "errMessages"
})
@XmlRootElement(name = "check_p2v_scenario_stateResponse")
public class CheckP2VScenarioStateResponse {

    @XmlElement(name = "check_p2v_scenario_stateResult")
    protected boolean checkP2VScenarioStateResult;
    @XmlElement(name = "is_run_reverse")
    protected boolean isRunReverse;
    @XmlElement(name = "vm_starting_reason")
    protected short vmStartingReason;
    @XmlElement(name = "async_res_id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger asyncResId;
    @XmlElement(name = "err_messages")
    protected String errMessages;

    /**
     * Gets the value of the checkP2VScenarioStateResult property.
     * 
     */
    public boolean isCheckP2VScenarioStateResult() {
        return checkP2VScenarioStateResult;
    }

    /**
     * Sets the value of the checkP2VScenarioStateResult property.
     * 
     */
    public void setCheckP2VScenarioStateResult(boolean value) {
        this.checkP2VScenarioStateResult = value;
    }

    /**
     * Gets the value of the isRunReverse property.
     * 
     */
    public boolean isIsRunReverse() {
        return isRunReverse;
    }

    /**
     * Sets the value of the isRunReverse property.
     * 
     */
    public void setIsRunReverse(boolean value) {
        this.isRunReverse = value;
    }

    /**
     * Gets the value of the vmStartingReason property.
     * 
     */
    public short getVmStartingReason() {
        return vmStartingReason;
    }

    /**
     * Sets the value of the vmStartingReason property.
     * 
     */
    public void setVmStartingReason(short value) {
        this.vmStartingReason = value;
    }

    /**
     * Gets the value of the asyncResId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAsyncResId() {
        return asyncResId;
    }

    /**
     * Sets the value of the asyncResId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAsyncResId(BigInteger value) {
        this.asyncResId = value;
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
