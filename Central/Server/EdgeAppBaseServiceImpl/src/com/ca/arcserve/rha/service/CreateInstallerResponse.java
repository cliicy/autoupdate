
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
 *         &lt;element name="create_installerResult" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "createInstallerResult"
})
@XmlRootElement(name = "create_installerResponse")
public class CreateInstallerResponse {

    @XmlElement(name = "create_installerResult")
    @XmlSchemaType(name = "unsignedInt")
    protected long createInstallerResult;

    /**
     * Gets the value of the createInstallerResult property.
     * 
     */
    public long getCreateInstallerResult() {
        return createInstallerResult;
    }

    /**
     * Sets the value of the createInstallerResult property.
     * 
     */
    public void setCreateInstallerResult(long value) {
        this.createInstallerResult = value;
    }

}
