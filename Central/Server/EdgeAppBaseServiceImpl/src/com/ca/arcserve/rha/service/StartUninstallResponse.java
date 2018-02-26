
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
 *         &lt;element name="start_uninstallResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "startUninstallResult"
})
@XmlRootElement(name = "start_uninstallResponse")
public class StartUninstallResponse {

    @XmlElement(name = "start_uninstallResult")
    protected boolean startUninstallResult;

    /**
     * Gets the value of the startUninstallResult property.
     * 
     */
    public boolean isStartUninstallResult() {
        return startUninstallResult;
    }

    /**
     * Sets the value of the startUninstallResult property.
     * 
     */
    public void setStartUninstallResult(boolean value) {
        this.startUninstallResult = value;
    }

}
