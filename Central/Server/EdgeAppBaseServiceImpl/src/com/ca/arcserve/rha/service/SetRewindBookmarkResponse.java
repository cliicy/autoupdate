
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
 *         &lt;element name="set_rewind_bookmarkResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="why_not" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "setRewindBookmarkResult",
    "whyNot"
})
@XmlRootElement(name = "set_rewind_bookmarkResponse")
public class SetRewindBookmarkResponse {

    @XmlElement(name = "set_rewind_bookmarkResult")
    protected boolean setRewindBookmarkResult;
    @XmlElement(name = "why_not")
    protected String whyNot;

    /**
     * Gets the value of the setRewindBookmarkResult property.
     * 
     */
    public boolean isSetRewindBookmarkResult() {
        return setRewindBookmarkResult;
    }

    /**
     * Sets the value of the setRewindBookmarkResult property.
     * 
     */
    public void setSetRewindBookmarkResult(boolean value) {
        this.setRewindBookmarkResult = value;
    }

    /**
     * Gets the value of the whyNot property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhyNot() {
        return whyNot;
    }

    /**
     * Sets the value of the whyNot property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhyNot(String value) {
        this.whyNot = value;
    }

}
