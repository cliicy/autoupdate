
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
 *         &lt;element name="MsgFindFileExWResult" type="{http://schemas.datacontract.org/2004/07/CA.ARCserve.CommunicationFoundation.TransferObject}ArrayOfABFunc_MsgRecW" minOccurs="0"/>
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
    "msgFindFileExWResult"
})
@XmlRootElement(name = "MsgFindFileExWResponse")
public class MsgFindFileExWResponse {

    @XmlElementRef(name = "MsgFindFileExWResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfABFuncMsgRecW> msgFindFileExWResult;

    /**
     * Gets the value of the msgFindFileExWResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfABFuncMsgRecW }{@code >}
     *     
     */
    public JAXBElement<ArrayOfABFuncMsgRecW> getMsgFindFileExWResult() {
        return msgFindFileExWResult;
    }

    /**
     * Sets the value of the msgFindFileExWResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfABFuncMsgRecW }{@code >}
     *     
     */
    public void setMsgFindFileExWResult(JAXBElement<ArrayOfABFuncMsgRecW> value) {
        this.msgFindFileExWResult = ((JAXBElement<ArrayOfABFuncMsgRecW> ) value);
    }

}
