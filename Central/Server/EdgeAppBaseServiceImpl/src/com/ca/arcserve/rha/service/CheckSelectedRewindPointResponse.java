
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
 *         &lt;element name="check_selected_rewind_pointResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="to_point_id" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
 *         &lt;element name="need_file" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="num_events_to_execute" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "checkSelectedRewindPointResult",
    "toPointId",
    "needFile",
    "numEventsToExecute",
    "whyNotReason"
})
@XmlRootElement(name = "check_selected_rewind_pointResponse")
public class CheckSelectedRewindPointResponse {

    @XmlElement(name = "check_selected_rewind_pointResult")
    protected boolean checkSelectedRewindPointResult;
    @XmlElement(name = "to_point_id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger toPointId;
    @XmlElement(name = "need_file")
    protected boolean needFile;
    @XmlElement(name = "num_events_to_execute")
    protected int numEventsToExecute;
    @XmlElement(name = "why_not_reason")
    protected String whyNotReason;

    /**
     * Gets the value of the checkSelectedRewindPointResult property.
     * 
     */
    public boolean isCheckSelectedRewindPointResult() {
        return checkSelectedRewindPointResult;
    }

    /**
     * Sets the value of the checkSelectedRewindPointResult property.
     * 
     */
    public void setCheckSelectedRewindPointResult(boolean value) {
        this.checkSelectedRewindPointResult = value;
    }

    /**
     * Gets the value of the toPointId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getToPointId() {
        return toPointId;
    }

    /**
     * Sets the value of the toPointId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setToPointId(BigInteger value) {
        this.toPointId = value;
    }

    /**
     * Gets the value of the needFile property.
     * 
     */
    public boolean isNeedFile() {
        return needFile;
    }

    /**
     * Sets the value of the needFile property.
     * 
     */
    public void setNeedFile(boolean value) {
        this.needFile = value;
    }

    /**
     * Gets the value of the numEventsToExecute property.
     * 
     */
    public int getNumEventsToExecute() {
        return numEventsToExecute;
    }

    /**
     * Sets the value of the numEventsToExecute property.
     * 
     */
    public void setNumEventsToExecute(int value) {
        this.numEventsToExecute = value;
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
