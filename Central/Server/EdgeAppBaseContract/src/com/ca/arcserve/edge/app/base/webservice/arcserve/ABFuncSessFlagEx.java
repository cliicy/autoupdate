
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_SessFlagEx complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ABFunc_SessFlagEx">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="encralgo" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="flagex" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ABFunc_SessFlagEx", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "encralgo",
    "flagex"
})
public class ABFuncSessFlagEx {

    protected Long encralgo;
    protected Long flagex;

    /**
     * Gets the value of the encralgo property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEncralgo() {
        return encralgo;
    }

    /**
     * Sets the value of the encralgo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEncralgo(Long value) {
        this.encralgo = value;
    }

    /**
     * Gets the value of the flagex property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFlagex() {
        return flagex;
    }

    /**
     * Sets the value of the flagex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFlagex(Long value) {
        this.flagex = value;
    }

}
