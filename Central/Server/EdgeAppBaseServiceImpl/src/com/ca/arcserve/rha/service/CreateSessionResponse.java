
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
 *         &lt;element name="create_sessionResult" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "createSessionResult",
    "errorCode"
})
@XmlRootElement(name = "create_sessionResponse")
public class CreateSessionResponse {

    @XmlElement(name = "create_sessionResult")
    @XmlSchemaType(name = "unsignedInt")
    protected long createSessionResult;
    @XmlElement(name = "error_code")
    @XmlSchemaType(name = "unsignedInt")
    protected long errorCode;

    /**
     * Gets the value of the createSessionResult property.
     * 
     */
    public long getCreateSessionResult() {
        return createSessionResult;
    }

    /**
     * Sets the value of the createSessionResult property.
     * 
     */
    public void setCreateSessionResult(long value) {
        this.createSessionResult = value;
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
