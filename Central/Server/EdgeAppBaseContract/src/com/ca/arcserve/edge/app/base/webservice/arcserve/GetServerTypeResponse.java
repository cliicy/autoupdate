
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;



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
 *         &lt;element name="GetServerTypeResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_ServerType" minOccurs="0"/>
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
    "getServerTypeResult"
})
@XmlRootElement(name = "GetServerTypeResponse")
public class GetServerTypeResponse {

    @XmlElement(name = "GetServerTypeResult")
    protected ABFuncServerType getServerTypeResult;

    /**
     * Gets the value of the getServerTypeResult property.
     * 
     * @return
     *     possible object is
     *     {@link ABFuncServerType }
     *     
     */
    public ABFuncServerType getGetServerTypeResult() {
        return getServerTypeResult;
    }

    /**
     * Sets the value of the getServerTypeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ABFuncServerType }
     *     
     */
    public void setGetServerTypeResult(ABFuncServerType value) {
        this.getServerTypeResult = value;
    }

}
