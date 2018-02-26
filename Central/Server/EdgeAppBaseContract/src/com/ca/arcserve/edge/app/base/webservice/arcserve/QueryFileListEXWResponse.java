
package com.ca.arcserve.edge.app.base.webservice.arcserve;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element name="QueryFileListEXWResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ArrayOfABFunc_DetailExtRecEXW" minOccurs="0"/>
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
    "queryFileListEXWResult"
})
@XmlRootElement(name = "QueryFileListEXWResponse")
public class QueryFileListEXWResponse {

    @XmlElementRef(name = "QueryFileListEXWResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfABFuncDetailExtRecEXW> queryFileListEXWResult;

    /**
     * Gets the value of the queryFileListEXWResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfABFuncDetailExtRecEXW }{@code >}
     *     
     */
    public JAXBElement<ArrayOfABFuncDetailExtRecEXW> getQueryFileListEXWResult() {
        return queryFileListEXWResult;
    }

    /**
     * Sets the value of the queryFileListEXWResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfABFuncDetailExtRecEXW }{@code >}
     *     
     */
    public void setQueryFileListEXWResult(JAXBElement<ArrayOfABFuncDetailExtRecEXW> value) {
        this.queryFileListEXWResult = ((JAXBElement<ArrayOfABFuncDetailExtRecEXW> ) value);
    }

}
