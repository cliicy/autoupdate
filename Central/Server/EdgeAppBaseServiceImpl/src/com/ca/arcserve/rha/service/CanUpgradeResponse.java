
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
 *         &lt;element name="can_upgradeResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "canUpgradeResult"
})
@XmlRootElement(name = "can_upgradeResponse")
public class CanUpgradeResponse {

    @XmlElement(name = "can_upgradeResult")
    protected boolean canUpgradeResult;

    /**
     * Gets the value of the canUpgradeResult property.
     * 
     */
    public boolean isCanUpgradeResult() {
        return canUpgradeResult;
    }

    /**
     * Sets the value of the canUpgradeResult property.
     * 
     */
    public void setCanUpgradeResult(boolean value) {
        this.canUpgradeResult = value;
    }

}
