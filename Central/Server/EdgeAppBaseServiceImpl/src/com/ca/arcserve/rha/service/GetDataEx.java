
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
 *         &lt;element name="session_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="scenarios_with_statistics" type="{http://ca.com/}ArrayOfUnsignedInt" minOccurs="0"/>
 *         &lt;element name="request_flag" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="last_update_time" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
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
    "sessionId",
    "scenariosWithStatistics",
    "requestFlag",
    "lastUpdateTime"
})
@XmlRootElement(name = "get_data_ex")
public class GetDataEx {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenarios_with_statistics")
    protected ArrayOfUnsignedInt scenariosWithStatistics;
    @XmlElement(name = "request_flag")
    @XmlSchemaType(name = "unsignedInt")
    protected long requestFlag;
    @XmlElement(name = "last_update_time", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger lastUpdateTime;

    /**
     * Gets the value of the sessionId property.
     * 
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     */
    public void setSessionId(long value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the scenariosWithStatistics property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUnsignedInt }
     *     
     */
    public ArrayOfUnsignedInt getScenariosWithStatistics() {
        return scenariosWithStatistics;
    }

    /**
     * Sets the value of the scenariosWithStatistics property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUnsignedInt }
     *     
     */
    public void setScenariosWithStatistics(ArrayOfUnsignedInt value) {
        this.scenariosWithStatistics = value;
    }

    /**
     * Gets the value of the requestFlag property.
     * 
     */
    public long getRequestFlag() {
        return requestFlag;
    }

    /**
     * Sets the value of the requestFlag property.
     * 
     */
    public void setRequestFlag(long value) {
        this.requestFlag = value;
    }

    /**
     * Gets the value of the lastUpdateTime property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Sets the value of the lastUpdateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLastUpdateTime(BigInteger value) {
        this.lastUpdateTime = value;
    }

}
