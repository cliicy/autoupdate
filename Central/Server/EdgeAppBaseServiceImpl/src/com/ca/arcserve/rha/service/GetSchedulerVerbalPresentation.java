
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
 *         &lt;element name="scheduler_xml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "schedulerXml"
})
@XmlRootElement(name = "get_scheduler_verbal_presentation")
public class GetSchedulerVerbalPresentation {

    @XmlElement(name = "scheduler_xml")
    protected String schedulerXml;

    /**
     * Gets the value of the schedulerXml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchedulerXml() {
        return schedulerXml;
    }

    /**
     * Sets the value of the schedulerXml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchedulerXml(String value) {
        this.schedulerXml = value;
    }

}
