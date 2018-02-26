
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfABFunc_TAPEDATAEX complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfABFunc_TAPEDATAEX">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ABFunc_TAPEDATAEX" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_TAPEDATAEX" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfABFunc_TAPEDATAEX", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "abFuncTAPEDATAEX"
})
public class ArrayOfABFuncTAPEDATAEX {

    @XmlElement(name = "ABFunc_TAPEDATAEX", nillable = true)
    protected List<ABFuncTAPEDATAEX> abFuncTAPEDATAEX;

    /**
     * Gets the value of the abFuncTAPEDATAEX property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abFuncTAPEDATAEX property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getABFuncTAPEDATAEX().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ABFuncTAPEDATAEX }
     * 
     * 
     */
    public List<ABFuncTAPEDATAEX> getABFuncTAPEDATAEX() {
        if (abFuncTAPEDATAEX == null) {
            abFuncTAPEDATAEX = new ArrayList<ABFuncTAPEDATAEX>();
        }
        return this.abFuncTAPEDATAEX;
    }

}
