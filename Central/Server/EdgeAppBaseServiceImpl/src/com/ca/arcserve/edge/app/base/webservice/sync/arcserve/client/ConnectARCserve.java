
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

    @XmlElement(nillable = true)
    protected String strUser;
    @XmlElement(nillable = true)
    protected String strPassword;
    protected ABFuncAuthMode mode;

    /**
     * Gets the value of the strUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrUser() {
        return strUser;
    }

    /**
     * Sets the value of the strUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrUser(String value) {
        this.strUser = value;
    }

    /**
     * Gets the value of the strPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrPassword() {
        return strPassword;
    }

    /**
     * Sets the value of the strPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrPassword(String value) {
        this.strPassword = value;
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
