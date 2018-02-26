
package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_AuthMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ABFunc_AuthMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ARCserve"/>
 *     &lt;enumeration value="Windows"/>
 *     &lt;enumeration value="CurrentProcess"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ABFunc_AuthMode", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject")
@XmlEnum
public enum ABFuncAuthMode {

    @XmlEnumValue("ARCserve")
    AR_CSERVE("ARCserve"),
    @XmlEnumValue("Windows")
    WINDOWS("Windows"),
    @XmlEnumValue("CurrentProcess")
    CURRENT_PROCESS("CurrentProcess");
    private final String value;

    ABFuncAuthMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ABFuncAuthMode fromValue(String v) {
        for (ABFuncAuthMode c: ABFuncAuthMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
