
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
 *         &lt;element name="Get_BookmarksResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getBookmarksResult"
})
@XmlRootElement(name = "Get_BookmarksResponse")
public class GetBookmarksResponse {

    @XmlElement(name = "Get_BookmarksResult")
    protected int getBookmarksResult;

    /**
     * Gets the value of the getBookmarksResult property.
     * 
     */
    public int getGetBookmarksResult() {
        return getBookmarksResult;
    }

    /**
     * Sets the value of the getBookmarksResult property.
     * 
     */
    public void setGetBookmarksResult(int value) {
        this.getBookmarksResult = value;
    }

}
