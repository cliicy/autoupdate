
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element name="strSessionNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strEdgeServerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strEdgeServerUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strEdgeServerPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strEdgeServerDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strEdgeServiceWsdl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bOverwrite" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="status" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_ManageStatus" minOccurs="0"/>
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
    "strSessionNo",
    "strEdgeServerId",
    "strEdgeServerUserName",
    "strEdgeServerPassword",
    "strEdgeServerDomain",
    "strEdgeServiceWsdl",
    "bOverwrite",
    "status"
})
@XmlRootElement(name = "MarkArcserveManageStatus")
public class MarkArcserveManageStatus {

    @XmlElementRef(name = "strSessionNo", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strSessionNo;
    @XmlElementRef(name = "strEdgeServerId", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strEdgeServerId;
    @XmlElementRef(name = "strEdgeServerUserName", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strEdgeServerUserName;
    @XmlElementRef(name = "strEdgeServerPassword", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strEdgeServerPassword;
    @XmlElementRef(name = "strEdgeServerDomain", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strEdgeServerDomain;
    @XmlElementRef(name = "strEdgeServiceWsdl", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strEdgeServiceWsdl;
    protected Boolean bOverwrite;
    protected ABFuncManageStatus status;

    /**
     * Gets the value of the strSessionNo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrSessionNo() {
        return strSessionNo;
    }

    /**
     * Sets the value of the strSessionNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrSessionNo(JAXBElement<String> value) {
        this.strSessionNo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the strEdgeServerId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrEdgeServerId() {
        return strEdgeServerId;
    }

    /**
     * Sets the value of the strEdgeServerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrEdgeServerId(JAXBElement<String> value) {
        this.strEdgeServerId = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the strEdgeServerUserName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrEdgeServerUserName() {
        return strEdgeServerUserName;
    }

    /**
     * Sets the value of the strEdgeServerUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrEdgeServerUserName(JAXBElement<String> value) {
        this.strEdgeServerUserName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the strEdgeServerPassword property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrEdgeServerPassword() {
        return strEdgeServerPassword;
    }

    /**
     * Sets the value of the strEdgeServerPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrEdgeServerPassword(JAXBElement<String> value) {
        this.strEdgeServerPassword = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the strEdgeServerDomain property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrEdgeServerDomain() {
        return strEdgeServerDomain;
    }

    /**
     * Sets the value of the strEdgeServerDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrEdgeServerDomain(JAXBElement<String> value) {
        this.strEdgeServerDomain = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the strEdgeServiceWsdl property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrEdgeServiceWsdl() {
        return strEdgeServiceWsdl;
    }

    /**
     * Sets the value of the strEdgeServiceWsdl property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrEdgeServiceWsdl(JAXBElement<String> value) {
        this.strEdgeServiceWsdl = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the bOverwrite property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBOverwrite() {
        return bOverwrite;
    }

    /**
     * Sets the value of the bOverwrite property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBOverwrite(Boolean value) {
        this.bOverwrite = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ABFuncManageStatus }
     *     
     */
    public ABFuncManageStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ABFuncManageStatus }
     *     
     */
    public void setStatus(ABFuncManageStatus value) {
        this.status = value;
    }

}
