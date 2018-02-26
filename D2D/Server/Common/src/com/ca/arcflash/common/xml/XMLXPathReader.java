package com.ca.arcflash.common.xml;

import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLXPathReader 
{
    private String m_XmlFile = null;
    private Document m_XmlDocument = null;
    private XPath m_XPath = null;
    private InputSource m_InputSource = null;
    
    public XMLXPathReader(String in_XmlFile) 
    {
        this.m_XmlFile = in_XmlFile;
        m_XmlDocument = null;
        m_XPath = null;
        m_InputSource = null;
    }
    public XMLXPathReader(InputSource inputSource) 
    {
    	this.m_XmlFile = null;
    	m_InputSource = inputSource;
    	m_XmlDocument = null;
        m_XPath = null;
       // m_InputSource = null;
	}
    public XMLXPathReader(Document xmlDocument)
    {
    	this.m_XmlFile = null;
        m_XmlDocument = xmlDocument;
        m_XPath = null;
        m_InputSource = null;
    }
	public boolean Initialise()
    {        
        try 
        {
        	if (this.m_XmlDocument == null)
        	{
	        	if(this.m_XmlFile != null)
	        		m_XmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.m_XmlFile);
	        	if(this.m_InputSource != null)
	        		m_XmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(m_InputSource);
        	}
        	m_XPath =  XPathFactory.newInstance().
			newXPath();
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
            return false;
        } 
        catch (SAXException ex) 
        {
            ex.printStackTrace();
            return false;
        } 
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            return false;
        }  
        return true;
    }
    
    public Object readXPath(String in_expression,QName returnType)
    {
        try 
        {
            XPathExpression xPathExpression = m_XPath.compile(in_expression);
            return xPathExpression.evaluate(m_XmlDocument, returnType);
        } 
        catch (XPathExpressionException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
