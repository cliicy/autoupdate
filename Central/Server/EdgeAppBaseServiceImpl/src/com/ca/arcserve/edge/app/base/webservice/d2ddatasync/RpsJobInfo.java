package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}unsignedByte"/>
 *         &lt;element name="JobType" type="{http://www.w3.org/2001/XMLSchema}unsignedByte"/>
 *         &lt;element name="JobID" type="{http://www.w3.org/2001/XMLSchema}unsignedByte"/>
 *         &lt;element name="JobName" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="JobGUID" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="ClientNode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PolicyUUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EndTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="JobStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProcessedSize" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="SessionRootPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PolicyName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SourceRPS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TargetRPS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SourceDataStore" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TargetDataStore" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartSession" type="{http://www.w3.org/2001/XMLSchema}unsignedByte"/>
 *         &lt;element name="EndSession" type="{http://www.w3.org/2001/XMLSchema}unsignedByte"/>
 *         &lt;element name="SessionInfo">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Session" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="GUID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GDDInfo">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="RawSize" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
 *                 &lt;attribute name="CompressedSize" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
 *                 &lt;attribute name="CompressionRatio" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
 *                 &lt;attribute name="CompressionPercentage" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "version",
    "jobType",
    "jobID",
    "jobName",
    "jobGUID",
    "clientNode",
    "policyUUID",
    "startTime",
    "endTime",
    "jobStatus",
    "processedSize",
    "sessionRootPath",
    "policyName",
    "sourceRPS",
    "targetRPS",
    "sourceDataStoreUUID",
    "sourceDataStore",
    "targetDataStoreUUID",
    "targetDataStore",
    "startSession",
    "endSession",
    "sessionInfo",
    "gddInfo"
})
public class RpsJobInfo {
	@XmlElement(name = "Version")
    @XmlSchemaType(name = "unsignedByte")
    protected long version;
    @XmlElement(name = "JobType")
    protected String jobType;
    @XmlElement(name = "JobID")
    @XmlSchemaType(name = "unsignedByte")
    protected long jobID;
    @XmlElement(name = "JobName", required = true)
    protected String jobName;
    @XmlElement(name = "JobGUID", required = true)
    protected String jobGUID;
    @XmlElement(name = "ClientNode", required = true)
    protected String clientNode;
    @XmlElement(name = "PolicyUUID", required = true)
    protected String policyUUID;
    @XmlElement(name = "StartTime", required = true)
    protected String startTime;
    @XmlElement(name = "EndTime", required = true)
    protected String endTime;
    @XmlElement(name = "JobStatus", required = true)
    protected String jobStatus;
    @XmlElement(name = "ProcessedSize")
    @XmlSchemaType(name = "unsignedInt")
    protected long processedSize;
    @XmlElement(name = "SessionRootPath", required = true)
    protected String sessionRootPath;
    @XmlElement(name = "PolicyName", required = true)
    protected String policyName;
    @XmlElement(name = "SourceRPS", required = true)
    protected String sourceRPS;
    @XmlElement(name = "TargetRPS", required = true)
    protected String targetRPS;
    @XmlElement(name = "SourceDataStoreUUID", required = true)
    protected String sourceDataStoreUUID;
    @XmlElement(name = "SourceDataStore", required = true)
    protected String sourceDataStore;
    @XmlElement(name = "TargetDataStoreUUID", required = true)
    protected String targetDataStoreUUID;
    @XmlElement(name = "TargetDataStore", required = true)
    protected String targetDataStore;
    @XmlElement(name = "StartSession")
    @XmlSchemaType(name = "unsignedInt")
    protected long startSession;
    @XmlElement(name = "EndSession")
    @XmlSchemaType(name = "unsignedInt")
    protected long endSession;
    @XmlElement(name = "SessionInfo", required = true)
    protected RpsJobInfo.SessionInfo sessionInfo;
    @XmlElement(name = "GDDInfo", required = true)
    protected RpsJobInfo.GDDInfo gddInfo;

    /**
     * Gets the value of the version property.
     * 
     */
    public long getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     */
    public void setVersion(long value) {
        this.version = value;
    }

    /**
     * Gets the value of the jobType property.
     * 
     */
    public String getJobType() {
        return jobType;
    }

    /**
     * Sets the value of the jobType property.
     * 
     */
    public void setJobType(String value) {
        this.jobType = value;
    }

    /**
     * Gets the value of the jobID property.
     * 
     */
    public long getJobID() {
        return jobID;
    }

    /**
     * Sets the value of the jobID property.
     * 
     */
    public void setJobID(long value) {
        this.jobID = value;
    }

    /**
     * Gets the value of the jobName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets the value of the jobName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobName(String value) {
        this.jobName = value;
    }

    /**
     * Gets the value of the jobGUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobGUID() {
        return jobGUID;
    }

    /**
     * Sets the value of the jobGUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobGUID(String value) {
        this.jobGUID = value;
    }

    /**
     * Gets the value of the clientNode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientNode() {
        return clientNode;
    }

    /**
     * Sets the value of the clientNode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientNode(String value) {
        this.clientNode = value;
    }

    /**
     * Gets the value of the policyUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyUUID() {
        return policyUUID;
    }

    /**
     * Sets the value of the policyUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyUUID(String value) {
        this.policyUUID = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartTime(String value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndTime(String value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the jobStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobStatus() {
        return jobStatus;
    }

    /**
     * Sets the value of the jobStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobStatus(String value) {
        this.jobStatus = value;
    }

    /**
     * Gets the value of the processedSize property.
     * 
     */
    public long getProcessedSize() {
        return processedSize;
    }

    /**
     * Sets the value of the processedSize property.
     * 
     */
    public void setProcessedSize(long value) {
        this.processedSize = value;
    }

    /**
     * Gets the value of the sessionRootPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionRootPath() {
        return sessionRootPath;
    }

    /**
     * Sets the value of the sessionRootPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionRootPath(String value) {
        this.sessionRootPath = value;
    }

    /**
     * Gets the value of the policyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * Sets the value of the policyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyName(String value) {
        this.policyName = value;
    }

    /**
     * Gets the value of the sourceRPS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceRPS() {
        return sourceRPS;
    }

    /**
     * Sets the value of the sourceRPS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceRPS(String value) {
        this.sourceRPS = value;
    }

    /**
     * Gets the value of the targetRPS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetRPS() {
        return targetRPS;
    }

    /**
     * Sets the value of the targetRPS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetRPS(String value) {
        this.targetRPS = value;
    }

    /**
     * Gets the value of the sourceDataStoreUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceDataStoreUUID() {
        return sourceDataStoreUUID;
    }

    /**
     * Sets the value of the sourceDataStoreUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceDataStoreUUID(String value) {
        this.sourceDataStoreUUID = value;
    }
    
    /**
     * Gets the value of the sourceDataStore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceDataStore() {
        return sourceDataStore;
    }

    /**
     * Sets the value of the sourceDataStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceDataStore(String value) {
        this.sourceDataStore = value;
    }

    /**
     * Gets the value of the targetDataStoreUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetDataStoreUUID() {
        return targetDataStoreUUID;
    }

    /**
     * Sets the value of the targetDataStoreUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetDataStoreUUID(String value) {
        this.targetDataStoreUUID = value;
    }
    
    /**
     * Gets the value of the targetDataStore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetDataStore() {
        return targetDataStore;
    }

    /**
     * Sets the value of the targetDataStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetDataStore(String value) {
        this.targetDataStore = value;
    }

    /**
     * Gets the value of the startSession property.
     * 
     */
    public long getStartSession() {
        return startSession;
    }

    /**
     * Sets the value of the startSession property.
     * 
     */
    public void setStartSession(long value) {
        this.startSession = value;
    }

    /**
     * Gets the value of the endSession property.
     * 
     */
    public long getEndSession() {
        return endSession;
    }

    /**
     * Sets the value of the endSession property.
     * 
     */
    public void setEndSession(long value) {
        this.endSession = value;
    }

    /**
     * Gets the value of the sessionInfo property.
     * 
     * @return
     *     possible object is
     *     {@link RpsJobInfo.SessionInfo }
     *     
     */
    public RpsJobInfo.SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    /**
     * Sets the value of the sessionInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link RpsJobInfo.SessionInfo }
     *     
     */
    public void setSessionInfo(RpsJobInfo.SessionInfo value) {
        this.sessionInfo = value;
    }

    /**
     * Gets the value of the gddInfo property.
     * 
     * @return
     *     possible object is
     *     {@link RpsJobInfo.GDDInfo }
     *     
     */
    public RpsJobInfo.GDDInfo getGDDInfo() {
        return gddInfo;
    }

    /**
     * Sets the value of the gddInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link RpsJobInfo.GDDInfo }
     *     
     */
    public void setGDDInfo(RpsJobInfo.GDDInfo value) {
        this.gddInfo = value;
    }
    
    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="RawSize" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
     *       &lt;attribute name="CompressedSize" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
     *       &lt;attribute name="CompressionRatio" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
     *       &lt;attribute name="CompressionPercentage" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GDDInfo {

        @XmlAttribute(name = "RawSize", required = true)
        @XmlSchemaType(name = "unsignedByte")
        protected long rawSize;
        @XmlAttribute(name = "CompressedSize", required = true)
        @XmlSchemaType(name = "unsignedInt")
        protected long compressedSize;
        @XmlAttribute(name = "CompressionRatio", required = true)
        @XmlSchemaType(name = "unsignedInt")
        protected long compressionRatio;
        @XmlAttribute(name = "CompressionPercentage", required = true)
        @XmlSchemaType(name = "unsignedInt")
        protected short compressionPercentage;

        /**
         * Gets the value of the rawSize property.
         * 
         */
        public long getRawSize() {
            return rawSize;
        }

        /**
         * Sets the value of the rawSize property.
         * 
         */
        public void setRawSize(long value) {
            this.rawSize = value;
        }

        /**
         * Gets the value of the compressedSize property.
         * 
         */
        public long getCompressedSize() {
            return compressedSize;
        }

        /**
         * Sets the value of the compressedSize property.
         * 
         */
        public void setCompressedSize(long value) {
            this.compressedSize = value;
        }

        /**
         * Gets the value of the compressionRatio property.
         * 
         */
        public long getCompressionRatio() {
            return compressionRatio;
        }

        /**
         * Sets the value of the compressionRatio property.
         * 
         */
        public void setCompressionRatio(long value) {
            this.compressionRatio = value;
        }

        /**
         * Gets the value of the compressionPercentage property.
         * 
         */
        public short getCompressionPercentage() {
            return compressionPercentage;
        }

        /**
         * Sets the value of the compressionPercentage property.
         * 
         */
        public void setCompressionPercentage(short value) {
            this.compressionPercentage = value;
        }

    }


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
     *         &lt;element name="Session" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="GUID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "session"
    })
    public static class SessionInfo {

        @XmlElement(name = "Session", required = true)
        protected List<RpsJobInfo.SessionInfo.Session> session;

        /**
         * Gets the value of the session property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the session property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSession().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link RpsJobInfo.SessionInfo.Session}
         * 
         * 
         */
        public List<RpsJobInfo.SessionInfo.Session> getSession() {
            if (session == null) {
                session = new ArrayList<RpsJobInfo.SessionInfo.Session>();
            }
            return this.session;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="GUID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Session {

            @XmlAttribute(name = "GUID", required = true)
            protected String guid;

            /**
             * Gets the value of the guid property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getGUID() {
                return guid;
            }

            /**
             * Sets the value of the guid property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setGUID(String value) {
                this.guid = value;
            }

        }

    }

}
