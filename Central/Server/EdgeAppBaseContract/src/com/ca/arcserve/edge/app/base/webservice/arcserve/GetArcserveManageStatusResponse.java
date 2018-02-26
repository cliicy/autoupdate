
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;



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
 *         &lt;element name="GetArcserveManageStatusResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_ManageStatus" minOccurs="0"/>
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
    "getArcserveManageStatusResult"
})
@XmlRootElement(name = "GetArcserveManageStatusResponse")
public class GetArcserveManageStatusResponse {

    @XmlElement(name = "GetArcserveManageStatusResult")
    protected ABFuncManageStatus getArcserveManageStatusResult;

    /**
     * Gets the value of the getArcserveManageStatusResult property.
     * 
     * @return
     *     possible object is
     *     {@link ABFuncManageStatus }
     *     
     */
    public ABFuncManageStatus getGetArcserveManageStatusResult() {
        return getArcserveManageStatusResult;
    }

    /**
     * Sets the value of the getArcserveManageStatusResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ABFuncManageStatus }
     *     
     */
    public void setGetArcserveManageStatusResult(ABFuncManageStatus value) {
        this.getArcserveManageStatusResult = value;
    }

}
