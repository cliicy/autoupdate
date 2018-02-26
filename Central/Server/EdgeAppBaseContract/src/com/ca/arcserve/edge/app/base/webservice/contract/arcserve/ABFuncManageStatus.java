
package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ABFunc_ManageStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ABFunc_ManageStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UnKnown"/>
 *     &lt;enumeration value="Managed"/>
 *     &lt;enumeration value="UnManaged"/>
 *     &lt;enumeration value="ManagedByOtherServer"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ABFunc_ManageStatus", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject")
@XmlEnum
public enum ABFuncManageStatus {

    @XmlEnumValue("UnKnown")
    UN_KNOWN("UnKnown"),
    @XmlEnumValue("Managed")
    MANAGED("Managed"),
    @XmlEnumValue("UnManaged")
    UN_MANAGED("UnManaged"),
    @XmlEnumValue("ManagedByOtherServer")
    MANAGED_BY_OTHER_SERVER("ManagedByOtherServer");
    private final String value;

    ABFuncManageStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ABFuncManageStatus fromValue(String v) {
        for (ABFuncManageStatus c: ABFuncManageStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
