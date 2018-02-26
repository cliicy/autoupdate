package com.ca.arcflash.webservice.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.ca.arcflash.webservice.scheduler.BaseJob;
import com.ca.arcflash.webservice.service.ServiceContext;


public class RSSItemXMLParser {
	private static final Logger logger = Logger.getLogger(RSSItemXMLParser.class);
	
	
	public Document saveXML(RSSItem[] items) throws Exception{
		logger.debug("saveXML called");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element rootElement = doc.createElement("rss");
		rootElement.setAttribute("version", "2.0"); //version="2.0"
		doc.appendChild(rootElement);
		
		logger.debug("saveXML after rss");
		
		Element channelElement = doc.createElement("channel");
		rootElement.appendChild(channelElement);
		
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		//Channel title
		Element channelTitleElement = doc.createElement("title");
		channelTitleElement.appendChild(doc.createTextNode(serverName + ": " + WebServiceMessages.getResource("FailedJobRSSTitle",ServiceContext.getInstance().getProductNameD2D())));
		channelElement.appendChild(channelTitleElement);
		//Channel link
		Element channelLinkElement = doc.createElement("link");
		channelLinkElement.appendChild(doc.createTextNode(BaseJob.getServerURL()));
		channelElement.appendChild(channelLinkElement);
		//Channel description
		Element channelDescElement = doc.createElement("description");
		channelDescElement.appendChild(doc.createTextNode(WebServiceMessages.getResource("FailedJobRSSDescription",ServiceContext.getInstance().getProductNameD2D())));		
		channelElement.appendChild(channelDescElement);
		
		logger.debug("saveXML after channel");
		
		for (RSSItem item : items)
		{
			Element itemElement = doc.createElement("item");
			
			Element titleElement = doc.createElement("title");
			titleElement.appendChild(doc.createTextNode(item.getTitle()));
			
			Element linkElement = doc.createElement("link");
			linkElement.appendChild(doc.createTextNode(item.getLink()));
			
			//Element descriptionElement = doc.createElement("description");
			//descriptionElement.appendChild(doc.createTextNode(item.getDescription()));
			
			itemElement.appendChild(titleElement);
			itemElement.appendChild(linkElement);
			//itemElement.appendChild(descriptionElement);
			
			channelElement.appendChild(itemElement);
		}
				
		return doc;
	}
	
	public List<RSSItem> loadXML(Document doc) throws Exception{
		logger.debug("loadXML called");
		
		List<RSSItem> list = new ArrayList<RSSItem>();
		NodeList nodeList = doc.getElementsByTagName("item");
		
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			RSSItem rssItem = new RSSItem();
			
			Element item = (Element)nodeList.item(i);
            Element titleTag = (Element)item.getElementsByTagName("title").item(0);
            if (titleTag == null) continue;
            String title =((Text)titleTag.getFirstChild()).getData().trim();
            rssItem.setTitle(title);            
            
            Element linkTag = (Element)item.getElementsByTagName("link").item(0);
            if (linkTag == null) continue;
            String link =((Text)linkTag.getFirstChild()).getData().trim();
            rssItem.setLink(link);            
            
            //Element descTag = (Element)item.getElementsByTagName("description").item(0);
            //if (descTag == null) continue;
            //String description =((Text)descTag.getFirstChild()).getData().trim();
            //rssItem.setDescription(description);
            //logger.info("loadXML get Description");
                   
            list.add(rssItem);
		}
				
		return list;
	}
}
