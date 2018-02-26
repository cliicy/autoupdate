
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="sComputerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sDir" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bCaseSensitive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="bIncludeSubDir" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="NameID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="bFirst" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="nRequest" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
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
    "sComputerName",
    "sDir",
    "bCaseSensitive",
    "bIncludeSubDir",
    "nameID",
    "bFirst",
    "nRequest"
})
@XmlRootElement(name = "QueryFileListEXW")
public class QueryFileListEXW {

    @XmlElementRef(name = "strSessionNo", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> strSessionNo;
    @XmlElementRef(name = "sComputerName", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sComputerName;
    @XmlElementRef(name = "sDir", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> sDir;
    protected Boolean bCaseSensitive;
    protected Boolean bIncludeSubDir;
    @XmlElement(name = "NameID")
    @XmlSchemaType(name = "unsignedInt")
    protected Long nameID;
    protected Boolean bFirst;
    @XmlSchemaType(name = "unsignedInt")
    protected Long nRequest;

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
     * Gets the value of the sComputerName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSComputerName() {
        return sComputerName;
    }

    /**
     * Sets the value of the sComputerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSComputerName(JAXBElement<String> value) {
        this.sComputerName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the sDir property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSDir() {
        return sDir;
    }

    /**
     * Sets the value of the sDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSDir(JAXBElement<String> value) {
        this.sDir = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the bCaseSensitive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBCaseSensitive() {
        return bCaseSensitive;
    }

    /**
     * Sets the value of the bCaseSensitive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBCaseSensitive(Boolean value) {
        this.bCaseSensitive = value;
    }

    /**
     * Gets the value of the bIncludeSubDir property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBIncludeSubDir() {
        return bIncludeSubDir;
    }

    /**
     * Sets the value of the bIncludeSubDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBIncludeSubDir(Boolean value) {
        this.bIncludeSubDir = value;
    }

    /**
     * Gets the value of the nameID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNameID() {
        return nameID;
    }

    /**
     * Sets the value of the nameID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNameID(Long value) {
        this.nameID = value;
    }

    /**
     * Gets the value of the bFirst property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBFirst() {
        return bFirst;
    }

    /**
     * Sets the value of the bFirst property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBFirst(Boolean value) {
        this.bFirst = value;
    }

    /**
     * Gets the value of the nRequest property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNRequest() {
        return nRequest;
    }

    /**
     * Sets the value of the nRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNRequest(Long value) {
        this.nRequest = value;
    }

}
