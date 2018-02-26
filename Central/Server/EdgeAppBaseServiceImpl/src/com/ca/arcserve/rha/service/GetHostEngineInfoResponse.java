
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
 *         &lt;element name="get_host_engine_infoResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="host_OS_64bit" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="engine_ver" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hos_OS_type" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "getHostEngineInfoResult",
    "hostOS64Bit",
    "engineVer",
    "hosOSType"
})
@XmlRootElement(name = "get_host_engine_infoResponse")
public class GetHostEngineInfoResponse {

    @XmlElement(name = "get_host_engine_infoResult")
    protected boolean getHostEngineInfoResult;
    @XmlElement(name = "host_OS_64bit")
    protected boolean hostOS64Bit;
    @XmlElement(name = "engine_ver")
    protected String engineVer;
    @XmlElement(name = "hos_OS_type")
    @XmlSchemaType(name = "unsignedInt")
    protected long hosOSType;

    /**
     * Gets the value of the getHostEngineInfoResult property.
     * 
     */
    public boolean isGetHostEngineInfoResult() {
        return getHostEngineInfoResult;
    }

    /**
     * Sets the value of the getHostEngineInfoResult property.
     * 
     */
    public void setGetHostEngineInfoResult(boolean value) {
        this.getHostEngineInfoResult = value;
    }

    /**
     * Gets the value of the hostOS64Bit property.
     * 
     */
    public boolean isHostOS64Bit() {
        return hostOS64Bit;
    }

    /**
     * Sets the value of the hostOS64Bit property.
     * 
     */
    public void setHostOS64Bit(boolean value) {
        this.hostOS64Bit = value;
    }

    /**
     * Gets the value of the engineVer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEngineVer() {
        return engineVer;
    }

    /**
     * Sets the value of the engineVer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEngineVer(String value) {
        this.engineVer = value;
    }

    /**
     * Gets the value of the hosOSType property.
     * 
     */
    public long getHosOSType() {
        return hosOSType;
    }

    /**
     * Sets the value of the hosOSType property.
     * 
     */
    public void setHosOSType(long value) {
        this.hosOSType = value;
    }

}
