package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ca.arcflash.webservice.service.CommonService;

//
// A B O U T   T H I S   C L A S S
//
// This class is intent to consolidate all common helper methods for policy
// XML. Some methods were already put in other utility classes, and we'll
// not move them here immediately. To reduce the risk the refactoring will
// introduce, we'll move them later step by step.
//
// Pang, Bo (panbo01)
// 2011-04-02
//

public class PolicyXmlUtilities
{
	private static PolicyXmlUtilities instance = null;
	private static final Logger logger = Logger.getLogger( PolicyXmlUtilities.class );
	
	//////////////////////////////////////////////////////////////////////////
	
	private PolicyXmlUtilities()
	{
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static synchronized PolicyXmlUtilities getInstance()
	{
		if (instance == null)
			instance = new PolicyXmlUtilities();
		
		return instance;
	}

	//////////////////////////////////////////////////////////////////////////
	
	private static final String VCMSETTINGS_ROOTELEMENT			= "VCMSettings";
	private static final String VCMSETTINGS_CONTENTATTRIBUTE	= "Content";
	
	//////////////////////////////////////////////////////////////////////////
	
	public String getVCMSettingsFromXmlDocument( Document xmlDocument )
	{
		try
		{
			// get the settings content from the XML
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			String path = String.format( "/%s/@%s",
				VCMSETTINGS_ROOTELEMENT, VCMSETTINGS_CONTENTATTRIBUTE );
			String encrypted = xpath.evaluate( path, xmlDocument );
			
			// decrypt the settings content
			
			String settingsContent =
				CommonService.getInstance().getNativeFacade().decrypt( encrypted );
			
			return settingsContent;
		}
		catch (Exception e)
		{
			logger.error( "getVCMSettingsFromSubDocument() failed.", e );
			return null;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public Document generateVCMSettingsXmlDocument( String jobScriptComboStr )
	{
		try
		{
			// encrypt the settings content
			
			String encrypted =
				CommonService.getInstance().getNativeFacade().encrypt( jobScriptComboStr );
			
			// create settings XML
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			Document xmlDocument = docBuilder.newDocument();
			
			Element rootElement = xmlDocument.createElement( VCMSETTINGS_ROOTELEMENT );
			xmlDocument.appendChild( rootElement );
			rootElement.setAttribute( VCMSETTINGS_CONTENTATTRIBUTE, encrypted );
			
			return xmlDocument;
		}
		catch (Exception e)
		{
			logger.error( "generateVCMSettingsSubDocument() failed.", e );
			return null;
		}
	}
	
}
