
package com.ca.arcserve.rha.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="sess_id" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="product" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="app" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="is_ass_rec" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="is_cdp" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="si_opt" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
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
    "sessId",
    "product",
    "app",
    "isAssRec",
    "isCdp",
    "siOpt"
})
@XmlRootElement(name = "create_template")
public class CreateTemplate {

    @XmlElement(name = "sess_id")
    @XmlSchemaType(name = "unsignedInt")
    protected long sessId;
    @XmlSchemaType(name = "unsignedInt")
    protected long product;
    @XmlSchemaType(name = "unsignedInt")
    protected long app;
    @XmlElement(name = "is_ass_rec")
    protected boolean isAssRec;
    @XmlElement(name = "is_cdp")
    protected boolean isCdp;
    @XmlElement(name = "si_opt")
    @XmlSchemaType(name = "unsignedInt")
    protected long siOpt;

    /**
     * Gets the value of the sessId property.
     * 
     */
    public long getSessId() {
        return sessId;
    }

    /**
     * Sets the value of the sessId property.
     * 
     */
    public void setSessId(long value) {
        this.sessId = value;
    }

    /**
     * Gets the value of the product property.
     * 
     */
    public long getProduct() {
        return product;
    }

    /**
     * Sets the value of the product property.
     * 
     */
    public void setProduct(long value) {
        this.product = value;
    }

    /**
     * Gets the value of the app property.
     * 
     */
    public long getApp() {
        return app;
    }

    /**
     * Sets the value of the app property.
     * 
     */
    public void setApp(long value) {
        this.app = value;
    }

    /**
     * Gets the value of the isAssRec property.
     * 
     */
    public boolean isIsAssRec() {
        return isAssRec;
    }

    /**
     * Sets the value of the isAssRec property.
     * 
     */
    public void setIsAssRec(boolean value) {
        this.isAssRec = value;
    }

    /**
     * Gets the value of the isCdp property.
     * 
     */
    public boolean isIsCdp() {
        return isCdp;
    }

    /**
     * Sets the value of the isCdp property.
     * 
     */
    public void setIsCdp(boolean value) {
        this.isCdp = value;
    }

    /**
     * Gets the value of the siOpt property.
     * 
     */
    public long getSiOpt() {
        return siOpt;
    }

    /**
     * Sets the value of the siOpt property.
     * 
     */
    public void setSiOpt(long value) {
        this.siOpt = value;
    }

}
