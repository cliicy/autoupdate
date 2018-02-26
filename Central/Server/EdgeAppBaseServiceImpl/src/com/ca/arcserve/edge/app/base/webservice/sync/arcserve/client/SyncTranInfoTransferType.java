
package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SyncTranInfo.TransferType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SyncTranInfo.TransferType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RegularSync"/>
 *     &lt;enumeration value="GDBSync"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SyncTranInfo.TransferType", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject")
@XmlEnum
public enum SyncTranInfoTransferType {

    @XmlEnumValue("RegularSync")
    REGULAR_SYNC("RegularSync"),
    @XmlEnumValue("GDBSync")
    GDB_SYNC("GDBSync");
    private final String value;

    SyncTranInfoTransferType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SyncTranInfoTransferType fromValue(String v) {
        for (SyncTranInfoTransferType c: SyncTranInfoTransferType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
