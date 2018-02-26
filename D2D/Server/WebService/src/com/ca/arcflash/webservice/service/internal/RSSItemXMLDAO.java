package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ca.arcflash.webservice.util.RSSItem;
import com.ca.arcflash.webservice.util.RSSItemXMLParser;

public class RSSItemXMLDAO extends XMLDAO {
	
	private static final Logger logger = Logger.getLogger(RSSItemXMLDAO.class);
	private RSSItemXMLParser rssItemXMLParser = new RSSItemXMLParser();
	
	public RSSItemXMLDAO ()	{
		
	}
	
	synchronized public RSSItem[] getRSSItems(String filePath) throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			return new RSSItem[0];

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		List<RSSItem> l = rssItemXMLParser.loadXML(doc);
		
		return l.toArray(new RSSItem[0]);
	}
	
	synchronized public void addRSSItem(String filePath, RSSItem rssItem)
	throws Exception {		
		logger.info("addRSSItem called");		
		RSSItem[] items = getRSSItems(filePath);
		List<RSSItem> itemList;
		
		if (items != null && items.length > 0) {
			itemList = new ArrayList<RSSItem>(Arrays.asList(items));
			
			itemList.add(rssItem);
		}
		else
		{
			//empty list
			itemList = new ArrayList<RSSItem>(1);
			itemList.add(rssItem);
		}		
		Document xmlDocument = rssItemXMLParser.saveXML(itemList.toArray(new RSSItem[0]));		
		doc2XmlFile(xmlDocument, filePath);
		logger.info("addRSSItem ending");
	}

	synchronized public void removeItem(String rssXML, String name) throws Exception {
		RSSItem[] items = getRSSItems(rssXML);
		List<RSSItem> itemList;
		
		if (items != null && items.length > 0) {
			itemList = new ArrayList<RSSItem>(Arrays.asList(items));			
			RSSItem temp = null;
			for (RSSItem item : itemList) {
				if (item.getLink().contains(name))
				{
					temp = item;
				}
			}
			itemList.remove(temp);
			Document xmlDocument = rssItemXMLParser.saveXML(itemList.toArray(new RSSItem[0]));		
			doc2XmlFile(xmlDocument, rssXML);	
		}			
	}
}
