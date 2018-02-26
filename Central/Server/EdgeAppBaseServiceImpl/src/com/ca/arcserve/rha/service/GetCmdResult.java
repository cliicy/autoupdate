
package com.ca.arcserve.rha.service;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="sess_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="async_id" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "sessId",
    "asyncId"
})
@XmlRootElement(name = "get_cmd_result")
public class GetCmdResult {

    @XmlElement(name = "sess_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessId;
    @XmlElement(name = "async_id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger asyncId;

    /**
     * Gets the value of the sessId property.
     * 
     */
    public long getSessId() {
        return sessId;
    }

    /**
     * Sets the value of the sessId property.
     * 
     */
    public void setSessId(long value) {
        this.sessId = value;
    }

    /**
     * Gets the value of the asyncId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAsyncId() {
        return asyncId;
    }

    /**
     * Sets the value of the asyncId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAsyncId(BigInteger value) {
        this.asyncId = value;
    }

}
