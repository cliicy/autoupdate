
package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * This enum indicates the type of a ARCserve Backup server.
 * 
 * <p>Java class for ABFunc_ServerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ABFunc_ServerType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UnKnown"/>
 *     &lt;enumeration value="GDB_PrimaryServer"/>
 *     &lt;enumeration value="Branch_Primary"/>
 *     &lt;enumeration value="Nornaml_Server"/>
 *     &lt;enumeration value="STANDALONE_Server"/>
 *     &lt;enumeration value="ARCSERVE_Member"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ABFunc_ServerType", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject")
@XmlEnum
public enum ABFuncServerType {

	/**
	 * The server type is unknown.
	 */
    @XmlEnumValue("UnKnown")
    UN_KNOWN("UnKnown"),
    /**
     * The server is a primary server and it's the central server for global
     * dashboard.
     */
    @XmlEnumValue("GDB_PrimaryServer")
    GDB_PRIMARY_SERVER("GDB_PrimaryServer"),
    /**
     * The server is a primary server and it's a branch server for global
     * dashboard.
     */
    @XmlEnumValue("Branch_Primary")
    BRANCH_PRIMARY("Branch_Primary"),
    /**
     * The server is a normal backup server.
     */
    @XmlEnumValue("Nornaml_Server")
    NORNAML_SERVER("Nornaml_Server"),
    /**
     * The server is a stand alone server. Refer to documents of ARCserve
     * Backup for more information.
     */
    @XmlEnumValue("STANDALONE_Server")
    STANDALONE_SERVER("STANDALONE_Server"),
    /**
     * The server is a member server. Refer to documents of ARCserve
     * Backup for more information.
     */
    @XmlEnumValue("ARCSERVE_Member")
    ARCSERVE_MEMBER("ARCSERVE_Member");
    
    private final String value;

    ABFuncServerType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ABFuncServerType fromValue(String v) {
        for (ABFuncServerType c: ABFuncServerType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
