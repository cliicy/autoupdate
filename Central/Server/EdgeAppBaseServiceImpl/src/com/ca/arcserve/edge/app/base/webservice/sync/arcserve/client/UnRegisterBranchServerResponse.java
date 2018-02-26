
package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client;

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
 *         &lt;element name="UnRegisterBranchServerResult" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "unRegisterBranchServerResult"
})
@XmlRootElement(name = "UnRegisterBranchServerResponse")
public class UnRegisterBranchServerResponse {

    @XmlElement(name = "UnRegisterBranchServerResult")
    protected Boolean unRegisterBranchServerResult;

    /**
     * Gets the value of the unRegisterBranchServerResult property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUnRegisterBranchServerResult() {
        return unRegisterBranchServerResult;
    }

    /**
     * Sets the value of the unRegisterBranchServerResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnRegisterBranchServerResult(Boolean value) {
        this.unRegisterBranchServerResult = value;
    }

}
