package com.ca.arcflash.common;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import com.ca.arcflash.webservice.data.PM.PMRequest;


public class PMRequestFactory {
	public static void CreateRequest(PMRequest pmRequest)
	{
		
		//We need a Document
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document m_PMXMLdoc = docBuilder.newDocument();
	        pmRequest.setM_PMXMLdoc(m_PMXMLdoc);
	        ProcessingInstruction pi = m_PMXMLdoc.createProcessingInstruction("xml", "version = \"1.0\" encoding = \"utf-8\"");
	        m_PMXMLdoc.appendChild(pi);
	        Element m_RootElement = m_PMXMLdoc.createElement("Message");
	        pmRequest.setM_RootElement(m_RootElement);
	        m_PMXMLdoc.appendChild(m_RootElement);

	        CreateHeader(pmRequest);
	        CreateBody(pmRequest);
	        ConvertToXMlStream(pmRequest);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void ConvertToXMlStream(PMRequest pmRequest)
	{
		Document m_PMXMLdoc= pmRequest.getM_PMXMLdoc();
		//set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans;
		try
		{
			trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        trans.setOutputProperty(OutputKeys.INDENT, "yes");

	        StringWriter sw = new StringWriter();
	        StreamResult sXmlResult = new StreamResult(sw);
	        DOMSource source = new DOMSource(m_PMXMLdoc);

	        trans.transform(source, sXmlResult);
	        int iLength = sw.toString().length();
	        String sLength = Integer.toString(iLength);
	        int iTotallength = sLength.length()+ iLength;
	        String sTotalLength = Integer.toString(iTotallength);
	        try {
				NodeList nodes = (NodeList) m_PMXMLdoc.getDocumentElement().getChildNodes();
				System.out.println(nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++)
		        {
		            //System.out.println(nodes.item(i).getNodeName());
		            NodeList nodes2 = (NodeList) nodes.item(i).getChildNodes();

		            for (int j = 0; j < nodes2.getLength(); j++)
			        {
			           // System.out.println(nodes2.item(j).getNodeName());
			            if(nodes2.item(j).getNodeName().equalsIgnoreCase("Length"))
			            {
			            	System.out.println(nodes2.item(j).getNodeName());
			            	nodes2.item(j).setTextContent(sTotalLength);
			            	System.out.println(sTotalLength);
			            }

			        }
		        }
				sw = new StringWriter();
				sXmlResult = new StreamResult(sw);
		        source = new DOMSource(m_PMXMLdoc);
		        trans.transform(source, sXmlResult);

		        OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
		        System.out.println(out.getEncoding());
		        String sPmReq = sw.toString();
		        pmRequest.setM_PMReq(sPmReq);
		        System.out.println("Here's the xml:\n\n" + pmRequest.getM_PMReq());

			}
	        catch (Exception e)
	        {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		catch (TransformerConfigurationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}


	}
	private static void CreateHeader(PMRequest pmRequest)
	{
		Element Header = pmRequest.getM_PMXMLdoc().createElement("Header");
		pmRequest.getM_RootElement().appendChild(Header);
		CreateHeaderChilds(Header,pmRequest);
		return ;
	}
	private static void CreateHeaderChilds(Element in_Header,PMRequest pmRequest)
	{
		Document m_PMXMLdoc= pmRequest.getM_PMXMLdoc();
		try
		{
			String sComputerName= InetAddress.getLocalHost().getHostName();

			Element childHeader = m_PMXMLdoc.createElement("Id");
            String sId = sComputerName + "_1000";
			childHeader.setTextContent(sId);
            in_Header.appendChild(childHeader);

            childHeader = m_PMXMLdoc.createElement("Source");
            childHeader.setTextContent(sComputerName);
            in_Header.appendChild(childHeader);


            childHeader = m_PMXMLdoc.createElement("Type");
            childHeader.setTextContent("UICommand");
            in_Header.appendChild(childHeader);

            childHeader = m_PMXMLdoc.createElement("Length");
            childHeader.setTextContent("1000");
            in_Header.appendChild(childHeader);

            childHeader = m_PMXMLdoc.createElement("Flags");
            childHeader.setTextContent("1");
            in_Header.appendChild(childHeader);

            childHeader = m_PMXMLdoc.createElement("SequenceNumber");
            childHeader.setTextContent("1");
            in_Header.appendChild(childHeader);

            childHeader = m_PMXMLdoc.createElement("Command");
            childHeader.setAttribute("Id", String.valueOf(pmRequest.getRequestType()).toString());
            in_Header.appendChild(childHeader);

		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void CreateBody(PMRequest pmRequest)
	{
		Document m_PMXMLdoc= pmRequest.getM_PMXMLdoc();
		Element m_RootElement = pmRequest.getM_RootElement();
		Element Body = m_PMXMLdoc.createElement("Body");
		m_RootElement.appendChild(Body);
		CreateBodyChilds(Body,pmRequest);
		return ;
	}
	
	protected static void CreateBodyChilds(Element in_Body,PMRequest pmRequest)
	{
		Document m_PMXMLdoc= pmRequest.getM_PMXMLdoc();
		Element childBody = m_PMXMLdoc.createElement("Product");
		childBody.setAttribute("Name", "CA ARCserve D2D");
		in_Body.appendChild(childBody);
		CreateProductChilds(childBody,pmRequest);
		return ;
	}
	
	protected static void CreateProductChilds(Element in_Product,PMRequest pmRequest)
	{
		/*
		Document m_PMXMLdoc= pmRequest.getM_PMXMLdoc();
		Element childProduct = m_PMXMLdoc.createElement("Release");
		String sMajorVersion;
		String sMinorVersion;
		try
		{
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot()+"\\Version");
			sMajorVersion =registry.getValue(handle,PMRequest.REGISTRY_KEY_MAJORVERSION);
			sMinorVersion = registry.getValue(handle,PMRequest.REGISTRY_KEY_MINORVERSION);
			registry.closeKey(handle);
			childProduct.setAttribute("MajorVersion", sMajorVersion);
			childProduct.setAttribute("MinorVersion", sMinorVersion);
			childProduct.setAttribute("ServicePack", "");

			if(pmRequest.getRequestType() == PMRequest.DOWNLOAD)
			{
				Element childBody = m_PMXMLdoc.createElement("Package");
				try {
					childBody.setAttribute("Id", CommonUtil.getPatchInfo().getPackageID());
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				childProduct.appendChild(childBody);
			}

			in_Product.appendChild(childProduct);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return;
		*/
	}
}
