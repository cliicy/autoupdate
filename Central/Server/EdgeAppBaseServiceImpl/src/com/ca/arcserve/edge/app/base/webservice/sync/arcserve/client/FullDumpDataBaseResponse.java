
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
 *         &lt;element name="FullDumpDataBaseResult" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="UTCOffset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
    "fullDumpDataBaseResult",
    "utcOffset"
})
@XmlRootElement(name = "FullDumpDataBaseResponse")
public class FullDumpDataBaseResponse {

    @XmlElement(name = "FullDumpDataBaseResult")
    protected Integer fullDumpDataBaseResult;
    @XmlElement(name = "UTCOffset")
    protected Integer utcOffset;

    /**
     * Gets the value of the fullDumpDataBaseResult property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFullDumpDataBaseResult() {
        return fullDumpDataBaseResult;
    }

    /**
     * Sets the value of the fullDumpDataBaseResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFullDumpDataBaseResult(Integer value) {
        this.fullDumpDataBaseResult = value;
    }

    /**
     * Gets the value of the utcOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUTCOffset() {
        return utcOffset;
    }

    /**
     * Sets the value of the utcOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUTCOffset(Integer value) {
        this.utcOffset = value;
    }

}