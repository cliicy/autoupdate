//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.07.02 at 05:26:15 PM GMT+08:00 
//


package com.ca.arcserve.edge.app.base.webservice.srm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="Time" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CPUUtil" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Index" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Utilization" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="MemoryUtil">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PhysicalMemUtil" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="PhysicalMemCapacity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="PageFileUtil" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="PageFileCapacity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DiskUtil" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Index" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="Throughput" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="NetworkUtil" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MACAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="LinkSpeed" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                   &lt;element name="Utilization" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="MachineName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Virtualization" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServerPKIInformation")
public class ServerPKIInformation {

    @XmlElement(name = "Time", required = true)
    protected String time;
    @XmlElement(name = "CPUUtil", required = true)
    protected List<ServerPKIInformation.CPUUtil> cpuUtil;
    @XmlElement(name = "MemoryUtil", required = true)
    protected ServerPKIInformation.MemoryUtil memoryUtil;
    @XmlElement(name = "DiskUtil", required = true)
    protected List<ServerPKIInformation.DiskUtil> diskUtil;
    @XmlElement(name = "NetworkUtil")
    protected List<ServerPKIInformation.NetworkUtil> networkUtil;
    @XmlAttribute(name = "MachineName", required = true)
    protected String machineName;
    @XmlAttribute(name = "Virtualization", required = true)
    protected int virtualization;

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTime(String value) {
        this.time = value;
    }

    /**
     * Gets the value of the cpuUtil property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpuUtil property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCPUUtil().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServerPKIInformation.CPUUtil }
     * 
     * 
     */
    public List<ServerPKIInformation.CPUUtil> getCPUUtil() {
        if (cpuUtil == null) {
            cpuUtil = new ArrayList<ServerPKIInformation.CPUUtil>();
        }
        return this.cpuUtil;
    }

    /**
     * Gets the value of the memoryUtil property.
     * 
     * @return
     *     possible object is
     *     {@link ServerPKIInformation.MemoryUtil }
     *     
     */
    public ServerPKIInformation.MemoryUtil getMemoryUtil() {
        return memoryUtil;
    }

    /**
     * Sets the value of the memoryUtil property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServerPKIInformation.MemoryUtil }
     *     
     */
    public void setMemoryUtil(ServerPKIInformation.MemoryUtil value) {
        this.memoryUtil = value;
    }

    /**
     * Gets the value of the diskUtil property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diskUtil property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiskUtil().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServerPKIInformation.DiskUtil }
     * 
     * 
     */
    public List<ServerPKIInformation.DiskUtil> getDiskUtil() {
        if (diskUtil == null) {
            diskUtil = new ArrayList<ServerPKIInformation.DiskUtil>();
        }
        return this.diskUtil;
    }

    /**
     * Gets the value of the networkUtil property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkUtil property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkUtil().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServerPKIInformation.NetworkUtil }
     * 
     * 
     */
    public List<ServerPKIInformation.NetworkUtil> getNetworkUtil() {
        if (networkUtil == null) {
            networkUtil = new ArrayList<ServerPKIInformation.NetworkUtil>();
        }
        return this.networkUtil;
    }

    /**
     * Gets the value of the machineName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * Sets the value of the machineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineName(String value) {
        this.machineName = value;
    }

    /**
     * Gets the value of the virtualization property.
     * 
     */
    public int getVirtualization() {
        return virtualization;
    }

    /**
     * Sets the value of the virtualization property.
     * 
     */
    public void setVirtualization(int value) {
        this.virtualization = value;
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
     *         &lt;element name="Index" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Utilization" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CPUUtil {

        @XmlElement(name = "Index", required = true)
        protected String index;
        @XmlElement(name = "Utilization")
        protected int utilization;

        /**
         * Gets the value of the index property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIndex() {
            return index;
        }

        /**
         * Sets the value of the index property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIndex(String value) {
            this.index = value;
        }

        /**
         * Gets the value of the utilization property.
         * 
         */
        public int getUtilization() {
            return utilization;
        }

        /**
         * Sets the value of the utilization property.
         * 
         */
        public void setUtilization(int value) {
            this.utilization = value;
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
     *         &lt;element name="Index" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="Throughput" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DiskUtil {

        @XmlElement(name = "Index")
        protected int index;
        @XmlElement(name = "Throughput")
        protected long throughput;

        /**
         * Gets the value of the index property.
         * 
         */
        public int getIndex() {
            return index;
        }

        /**
         * Sets the value of the index property.
         * 
         */
        public void setIndex(int value) {
            this.index = value;
        }

        /**
         * Gets the value of the throughput property.
         * 
         */
        public long getThroughput() {
            return throughput;
        }

        /**
         * Sets the value of the throughput property.
         * 
         */
        public void setThroughput(long value) {
            this.throughput = value;
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
     *         &lt;element name="PhysicalMemUtil" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="PhysicalMemCapacity" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="PageFileUtil" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="PageFileCapacity" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class MemoryUtil {

        @XmlElement(name = "PhysicalMemUtil")
        protected int physicalMemUtil;
        @XmlElement(name = "PhysicalMemCapacity")
        protected int physicalMemCapacity;
        @XmlElement(name = "PageFileUtil")
        protected int pageFileUtil;
        @XmlElement(name = "PageFileCapacity")
        protected int pageFileCapacity;

        /**
         * Gets the value of the physicalMemUtil property.
         * 
         */
        public int getPhysicalMemUtil() {
            return physicalMemUtil;
        }

        /**
         * Sets the value of the physicalMemUtil property.
         * 
         */
        public void setPhysicalMemUtil(int value) {
            this.physicalMemUtil = value;
        }

        /**
         * Gets the value of the physicalMemCapacity property.
         * 
         */
        public int getPhysicalMemCapacity() {
            return physicalMemCapacity;
        }

        /**
         * Sets the value of the physicalMemCapacity property.
         * 
         */
        public void setPhysicalMemCapacity(int value) {
            this.physicalMemCapacity = value;
        }

        /**
         * Gets the value of the pageFileUtil property.
         * 
         */
        public int getPageFileUtil() {
            return pageFileUtil;
        }

        /**
         * Sets the value of the pageFileUtil property.
         * 
         */
        public void setPageFileUtil(int value) {
            this.pageFileUtil = value;
        }

        /**
         * Gets the value of the pageFileCapacity property.
         * 
         */
        public int getPageFileCapacity() {
            return pageFileCapacity;
        }

        /**
         * Sets the value of the pageFileCapacity property.
         * 
         */
        public void setPageFileCapacity(int value) {
            this.pageFileCapacity = value;
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
     *         &lt;element name="MACAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="LinkSpeed" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *         &lt;element name="Utilization" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class NetworkUtil {

        @XmlElement(name = "MACAddress", required = true)
        protected String macAddress;
        @XmlElement(name = "LinkSpeed")
        protected long linkSpeed;
        @XmlElement(name = "Utilization")
        protected int utilization;

        /**
         * Gets the value of the macAddress property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMACAddress() {
            return macAddress;
        }

        /**
         * Sets the value of the macAddress property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMACAddress(String value) {
            this.macAddress = value;
        }

        /**
         * Gets the value of the linkSpeed property.
         * 
         */
        public long getLinkSpeed() {
            return linkSpeed;
        }

        /**
         * Sets the value of the linkSpeed property.
         * 
         */
        public void setLinkSpeed(long value) {
            this.linkSpeed = value;
        }

        /**
         * Gets the value of the utilization property.
         * 
         */
        public int getUtilization() {
            return utilization;
        }

        /**
         * Sets the value of the utilization property.
         * 
         */
        public void setUtilization(int value) {
            this.utilization = value;
        }

    }

}