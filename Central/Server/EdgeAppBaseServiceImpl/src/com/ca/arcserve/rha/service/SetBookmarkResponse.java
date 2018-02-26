
package com.ca.arcserve.rha.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="Set_BookmarkResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "setBookmarkResult"
})
@XmlRootElement(name = "Set_BookmarkResponse")
public class SetBookmarkResponse {

    @XmlElement(name = "Set_BookmarkResult")
    protected int setBookmarkResult;

    /**
     * Gets the value of the setBookmarkResult property.
     * 
     */
    public int getSetBookmarkResult() {
        return setBookmarkResult;
    }

    /**
     * Sets the value of the setBookmarkResult property.
     * 
     */
    public void setSetBookmarkResult(int value) {
        this.setBookmarkResult = value;
    }

}
