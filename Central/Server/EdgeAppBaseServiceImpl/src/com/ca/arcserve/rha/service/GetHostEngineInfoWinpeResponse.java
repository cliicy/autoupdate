
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
 *         &lt;element name="get_host_engine_info_winpeResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="host_OS_64bit" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="engine_ver" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hos_OS_type" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="is_winpe" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="winpe_ver" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="system_start_time" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="system_rest_time" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "getHostEngineInfoWinpeResult",
    "hostOS64Bit",
    "engineVer",
    "hosOSType",
    "isWinpe",
    "winpeVer",
    "systemStartTime",
    "systemRestTime"
})
@XmlRootElement(name = "get_host_engine_info_winpeResponse")
public class GetHostEngineInfoWinpeResponse {

    @XmlElement(name = "get_host_engine_info_winpeResult")
    protected boolean getHostEngineInfoWinpeResult;
    @XmlElement(name = "host_OS_64bit")
    protected boolean hostOS64Bit;
    @XmlElement(name = "engine_ver")
    protected String engineVer;
    @XmlElement(name = "hos_OS_type")
    @XmlSchemaType(name = "unsignedInt")
    protected long hosOSType;
    @XmlElement(name = "is_winpe")
    protected boolean isWinpe;
    @XmlElement(name = "winpe_ver")
    protected String winpeVer;
    @XmlElement(name = "system_start_time")
    @XmlSchemaType(name = "unsignedInt")
    protected long systemStartTime;
    @XmlElement(name = "system_rest_time")
    @XmlSchemaType(name = "unsignedInt")
    protected long systemRestTime;

    /**
     * Gets the value of the getHostEngineInfoWinpeResult property.
     * 
     */
    public boolean isGetHostEngineInfoWinpeResult() {
        return getHostEngineInfoWinpeResult;
    }

    /**
     * Sets the value of the getHostEngineInfoWinpeResult property.
     * 
     */
    public void setGetHostEngineInfoWinpeResult(boolean value) {
        this.getHostEngineInfoWinpeResult = value;
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

    /**
     * Gets the value of the isWinpe property.
     * 
     */
    public boolean isIsWinpe() {
        return isWinpe;
    }

    /**
     * Sets the value of the isWinpe property.
     * 
     */
    public void setIsWinpe(boolean value) {
        this.isWinpe = value;
    }

    /**
     * Gets the value of the winpeVer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWinpeVer() {
        return winpeVer;
    }

    /**
     * Sets the value of the winpeVer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWinpeVer(String value) {
        this.winpeVer = value;
    }

    /**
     * Gets the value of the systemStartTime property.
     * 
     */
    public long getSystemStartTime() {
        return systemStartTime;
    }

    /**
     * Sets the value of the systemStartTime property.
     * 
     */
    public void setSystemStartTime(long value) {
        this.systemStartTime = value;
    }

    /**
     * Gets the value of the systemRestTime property.
     * 
     */
    public long getSystemRestTime() {
        return systemRestTime;
    }

    /**
     * Sets the value of the systemRestTime property.
     * 
     */
    public void setSystemRestTime(long value) {
        this.systemRestTime = value;
    }

}
