package com.ca.arcflash.webservice.edge.policymanagement;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class PolicyXmlObject
{
	//////////////////////////////////////////////////////////////////////////

	public class PolicyXmlSectionNames
	{
		public static final String BackupSettings			= "BackupSettings";
		public static final String ArchivingSettings		= "ArchivingSettings";
		public static final String ScheduledExportSettings	= "ScheduledExportSettings";
		public static final String VCMSettings				= "VirtualConversionSettings";
		public static final String VMBackupSettings			= "VMBackupSettings";
		public static final String PreferencesSettings		= "PreferencesSettings";
	}
	
	//////////////////////////////////////////////////////////////////////////

	public class InvalidParameterException extends Exception
	{
		private static final long serialVersionUID = 1L;
		private String parameterName;
		
		public InvalidParameterException( String parameterName )
		{
			this.parameterName = parameterName;
		}

		public String getParameterName()
		{
			return parameterName;
		}
	}
	
	public class InvalidXmlException extends Exception
	{
		private static final long serialVersionUID = 1L;
	}

	//////////////////////////////////////////////////////////////////////////

	private String policyXmlString;
	private Document policyXmlDocument;
	
	//////////////////////////////////////////////////////////////////////////

	public PolicyXmlObject()
	{
		this.policyXmlString = null;
	}

	//////////////////////////////////////////////////////////////////////////

	public String getPolicyXmlString()
	{
		return policyXmlString;
	}

	//////////////////////////////////////////////////////////////////////////

	public void setPolicyXmlString( String policyXmlString ) throws
		InvalidParameterException,
		InvalidXmlException
	{
		if (policyXmlString == null)
			throw new InvalidParameterException( "policyXml" );
		
		this.policyXmlDocument = xmlStringToDocument( policyXmlString );
		this.policyXmlString = policyXmlString;
	}
	
	//////////////////////////////////////////////////////////////////////////

	public Document getSettingsSection( String sectionName ) throws
		InvalidParameterException
	{
		if (sectionName == null)
			throw new InvalidParameterException( "sectionName" );
		
		return getSectionDocument( this.policyXmlDocument, sectionName );
	}
	
	//////////////////////////////////////////////////////////////////////////

	private Document xmlStringToDocument( String string ) throws
		InvalidXmlException
	{
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			Document xmlDocument = docBuilder.parse( new InputSource( new StringReader( string ) ) );

			return xmlDocument;
		}
		catch (Exception e)
		{
			throw new InvalidXmlException();
		}
	}

	//////////////////////////////////////////////////////////////////////////

	private Document getSectionDocument( Document xmlDoc, String sectionName )
	{
		try
		{
			// find the root node of the section

			XPathFactory pathFactory = XPathFactory.newInstance();
			XPath path = pathFactory.newXPath();

			NodeList nodeList = (NodeList)
				path.evaluate( "/BackupPolicy/" + sectionName, xmlDoc, XPathConstants.NODESET );

			NodeList childNodeList = nodeList.item( 0 ).getChildNodes();
			Node rootNode = null;
			for (int i = 0; i < childNodeList.getLength(); i ++)
			{
				Node node = childNodeList.item( i );
				if (node instanceof Element)
				{
					rootNode = node;
					break;
				}
			}

			if (rootNode == null)
				return null;

			// create a new document and import the section

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			Document xmlDocument = docBuilder.newDocument();
			xmlDocument.appendChild( xmlDocument.importNode( rootNode, true ) );

			return xmlDocument;
		}
		catch (Exception e)
		{
			return null;
		}
	}

}
