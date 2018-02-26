
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
 *         &lt;element name="session_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="scenario_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="sync_method" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="ignore_same_files" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arc_upd" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="verification_and_run" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "scenarioId",
    "syncMethod",
    "ignoreSameFiles",
    "arcUpd",
    "verificationAndRun"
})
@XmlRootElement(name = "run")
public class Run {

    @XmlElement(name = "session_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessionId;
    @XmlElement(name = "scenario_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long scenarioId;
    @XmlElement(name = "sync_method")
    @XmlSchemaType(name = "unsignedInt")
    protected long syncMethod;
    @XmlElement(name = "ignore_same_files")
    protected boolean ignoreSameFiles;
    @XmlElement(name = "arc_upd")
    protected boolean arcUpd;
    @XmlElement(name = "verification_and_run")
    protected int verificationAndRun;

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
     * Gets the value of the scenarioId property.
     * 
     */
    public long getScenarioId() {
        return scenarioId;
    }

    /**
     * Sets the value of the scenarioId property.
     * 
     */
    public void setScenarioId(long value) {
        this.scenarioId = value;
    }

    /**
     * Gets the value of the syncMethod property.
     * 
     */
    public long getSyncMethod() {
        return syncMethod;
    }

    /**
     * Sets the value of the syncMethod property.
     * 
     */
    public void setSyncMethod(long value) {
        this.syncMethod = value;
    }

    /**
     * Gets the value of the ignoreSameFiles property.
     * 
     */
    public boolean isIgnoreSameFiles() {
        return ignoreSameFiles;
    }

    /**
     * Sets the value of the ignoreSameFiles property.
     * 
     */
    public void setIgnoreSameFiles(boolean value) {
        this.ignoreSameFiles = value;
    }

    /**
     * Gets the value of the arcUpd property.
     * 
     */
    public boolean isArcUpd() {
        return arcUpd;
    }

    /**
     * Sets the value of the arcUpd property.
     * 
     */
    public void setArcUpd(boolean value) {
        this.arcUpd = value;
    }

    /**
     * Gets the value of the verificationAndRun property.
     * 
     */
    public int getVerificationAndRun() {
        return verificationAndRun;
    }

    /**
     * Sets the value of the verificationAndRun property.
     * 
     */
    public void setVerificationAndRun(int value) {
        this.verificationAndRun = value;
    }

}
