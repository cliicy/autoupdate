
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;



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
 *         &lt;element name="strUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mode" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_AuthMode" minOccurs="0"/>
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
    "strUser",
    "strPassword",
    "mode"
})
@XmlRootElement(name = "ConnectARCserve")
public class ConnectARCserve {

    @XmlElementRef(name = "strUser", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strUser;
    @XmlElementRef(name = "strPassword", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strPassword;
    protected ABFuncAuthMode mode;

    /**
     * Gets the value of the strUser property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrUser() {
        return strUser;
    }

    /**
     * Sets the value of the strUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrUser(JAXBElement<String> value) {
        this.strUser = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the strPassword property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStrPassword() {
        return strPassword;
    }

    /**
     * Sets the value of the strPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStrPassword(JAXBElement<String> value) {
        this.strPassword = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link ABFuncAuthMode }
     *     
     */
    public ABFuncAuthMode getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ABFuncAuthMode }
     *     
     */
    public void setMode(ABFuncAuthMode value) {
        this.mode = value;
    }

}
