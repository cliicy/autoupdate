
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
 *         &lt;element name="get_scheduler_verbal_presentationResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="verbal_presentation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getSchedulerVerbalPresentationResult",
    "verbalPresentation"
})
@XmlRootElement(name = "get_scheduler_verbal_presentationResponse")
public class GetSchedulerVerbalPresentationResponse {

    @XmlElement(name = "get_scheduler_verbal_presentationResult")
    protected boolean getSchedulerVerbalPresentationResult;
    @XmlElement(name = "verbal_presentation")
    protected String verbalPresentation;

    /**
     * Gets the value of the getSchedulerVerbalPresentationResult property.
     * 
     */
    public boolean isGetSchedulerVerbalPresentationResult() {
        return getSchedulerVerbalPresentationResult;
    }

    /**
     * Sets the value of the getSchedulerVerbalPresentationResult property.
     * 
     */
    public void setGetSchedulerVerbalPresentationResult(boolean value) {
        this.getSchedulerVerbalPresentationResult = value;
    }

    /**
     * Gets the value of the verbalPresentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerbalPresentation() {
        return verbalPresentation;
    }

    /**
     * Sets the value of the verbalPresentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerbalPresentation(String value) {
        this.verbalPresentation = value;
    }

}
