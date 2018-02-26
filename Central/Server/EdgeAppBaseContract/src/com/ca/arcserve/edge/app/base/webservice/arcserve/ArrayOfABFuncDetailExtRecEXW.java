
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfABFunc_DetailExtRecEXW complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfABFunc_DetailExtRecEXW">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ABFunc_DetailExtRecEXW" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_DetailExtRecEXW" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfABFunc_DetailExtRecEXW", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "abFuncDetailExtRecEXW"
})
public class ArrayOfABFuncDetailExtRecEXW {

    @XmlElement(name = "ABFunc_DetailExtRecEXW", nillable = true)
    protected List<ABFuncDetailExtRecEXW> abFuncDetailExtRecEXW;

    /**
     * Gets the value of the abFuncDetailExtRecEXW property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abFuncDetailExtRecEXW property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getABFuncDetailExtRecEXW().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ABFuncDetailExtRecEXW }
     * 
     * 
     */
    public List<ABFuncDetailExtRecEXW> getABFuncDetailExtRecEXW() {
        if (abFuncDetailExtRecEXW == null) {
            abFuncDetailExtRecEXW = new ArrayList<ABFuncDetailExtRecEXW>();
        }
        return this.abFuncDetailExtRecEXW;
    }

}
