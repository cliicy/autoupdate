
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
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="is_sync" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="bookmark_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bookmark_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "id",
    "isSync",
    "bookmarkType",
    "bookmarkName"
})
@XmlRootElement(name = "Set_Bookmark")
public class SetBookmark {

    @XmlElement(name = "ID")
    @XmlSchemaType(name = "unsignedInt")
    protected long id;
    @XmlElement(name = "is_sync")
    protected boolean isSync;
    @XmlElement(name = "bookmark_type")
    protected String bookmarkType;
    @XmlElement(name = "bookmark_name")
    protected String bookmarkName;

    /**
     * Gets the value of the id property.
     * 
     */
    public long getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setID(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the isSync property.
     * 
     */
    public boolean isIsSync() {
        return isSync;
    }

    /**
     * Sets the value of the isSync property.
     * 
     */
    public void setIsSync(boolean value) {
        this.isSync = value;
    }

    /**
     * Gets the value of the bookmarkType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookmarkType() {
        return bookmarkType;
    }

    /**
     * Sets the value of the bookmarkType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookmarkType(String value) {
        this.bookmarkType = value;
    }

    /**
     * Gets the value of the bookmarkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookmarkName() {
        return bookmarkName;
    }

    /**
     * Sets the value of the bookmarkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookmarkName(String value) {
        this.bookmarkName = value;
    }

}
