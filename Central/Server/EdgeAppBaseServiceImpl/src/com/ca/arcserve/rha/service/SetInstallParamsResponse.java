
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
 *         &lt;element name="set_install_paramsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "setInstallParamsResult"
})
@XmlRootElement(name = "set_install_paramsResponse")
public class SetInstallParamsResponse {

    @XmlElement(name = "set_install_paramsResult")
    protected boolean setInstallParamsResult;

    /**
     * Gets the value of the setInstallParamsResult property.
     * 
     */
    public boolean isSetInstallParamsResult() {
        return setInstallParamsResult;
    }

    /**
     * Sets the value of the setInstallParamsResult property.
     * 
     */
    public void setSetInstallParamsResult(boolean value) {
        this.setInstallParamsResult = value;
    }

}
