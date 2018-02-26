
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfABFunc_TapePropertyRec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfABFunc_TapePropertyRec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ABFunc_TapePropertyRec" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ABFunc_TapePropertyRec" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfABFunc_TapePropertyRec", namespace = "http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject", propOrder = {
    "abFuncTapePropertyRec"
})
public class ArrayOfABFuncTapePropertyRec {

    @XmlElement(name = "ABFunc_TapePropertyRec", nillable = true)
    protected List<ABFuncTapePropertyRec> abFuncTapePropertyRec;

    /**
     * Gets the value of the abFuncTapePropertyRec property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abFuncTapePropertyRec property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getABFuncTapePropertyRec().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ABFuncTapePropertyRec }
     * 
     * 
     */
    public List<ABFuncTapePropertyRec> getABFuncTapePropertyRec() {
        if (abFuncTapePropertyRec == null) {
            abFuncTapePropertyRec = new ArrayList<ABFuncTapePropertyRec>();
        }
        return this.abFuncTapePropertyRec;
    }

}
