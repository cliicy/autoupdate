
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
 *         &lt;element name="EnumBranchServerResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ArrayOfBranchSiteInfo" minOccurs="0"/>
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
    "enumBranchServerResult"
})
@XmlRootElement(name = "EnumBranchServerResponse")
public class EnumBranchServerResponse {

    @XmlElement(name = "EnumBranchServerResult", nillable = true)
    protected ArrayOfBranchSiteInfo enumBranchServerResult;

    /**
     * Gets the value of the enumBranchServerResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBranchSiteInfo }
     *     
     */
    public ArrayOfBranchSiteInfo getEnumBranchServerResult() {
        return enumBranchServerResult;
    }

    /**
     * Sets the value of the enumBranchServerResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBranchSiteInfo }
     *     
     */
    public void setEnumBranchServerResult(ArrayOfBranchSiteInfo value) {
        this.enumBranchServerResult = value;
    }

}
