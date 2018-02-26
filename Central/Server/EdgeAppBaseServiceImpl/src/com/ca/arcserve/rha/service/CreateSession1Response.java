
package com.ca.arcserve.rha.service;

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
 *         &lt;element name="create_session1Result" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="error_code" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "createSession1Result",
    "errorCode"
})
@XmlRootElement(name = "create_session1Response")
public class CreateSession1Response {

    @XmlElement(name = "create_session1Result")
    @XmlSchemaType(name = "unsignedInt")
    protected long createSession1Result;
    @XmlElement(name = "error_code")
    @XmlSchemaType(name = "unsignedInt")
    protected long errorCode;

    /**
     * Gets the value of the createSession1Result property.
     * 
     */
    public long getCreateSession1Result() {
        return createSession1Result;
    }

    /**
     * Sets the value of the createSession1Result property.
     * 
     */
    public void setCreateSession1Result(long value) {
        this.createSession1Result = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     */
    public long getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     */
    public void setErrorCode(long value) {
        this.errorCode = value;
    }

}