
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element name="strSessionNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sPwd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "sUser",
    "sPwd",
    "sHost"
})
@XmlRootElement(name = "GetUsernameAndPassword")
public class GetUsernameAndPassword {

    @XmlElementRef(name = "strSessionNo", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strSessionNo;
    @XmlElementRef(name = "sUser", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sUser;
    @XmlElementRef(name = "sPwd", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sPwd;
    @XmlElementRef(name = "sHost", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sHost;

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
     * Gets the value of the sUser property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSUser() {
        return sUser;
    }

    /**
     * Sets the value of the sUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSUser(JAXBElement<String> value) {
        this.sUser = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the sPwd property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSPwd() {
        return sPwd;
    }

    /**
     * Sets the value of the sPwd property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSPwd(JAXBElement<String> value) {
        this.sPwd = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the sHost property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSHost() {
        return sHost;
    }

    /**
     * Sets the value of the sHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSHost(JAXBElement<String> value) {
        this.sHost = ((JAXBElement<String> ) value);
    }

}
