
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
 *         &lt;element name="change_su_groupResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "changeSuGroupResult"
})
@XmlRootElement(name = "change_su_groupResponse")
public class ChangeSuGroupResponse {

    @XmlElement(name = "change_su_groupResult")
    protected boolean changeSuGroupResult;

    /**
     * Gets the value of the changeSuGroupResult property.
     * 
     */
    public boolean isChangeSuGroupResult() {
        return changeSuGroupResult;
    }

    /**
     * Sets the value of the changeSuGroupResult property.
     * 
     */
    public void setChangeSuGroupResult(boolean value) {
        this.changeSuGroupResult = value;
    }

}
