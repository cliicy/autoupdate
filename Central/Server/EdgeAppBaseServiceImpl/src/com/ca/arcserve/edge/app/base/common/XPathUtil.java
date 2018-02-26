/**
 * 
 */
package com.ca.arcserve.edge.app.base.common;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author lijwe02
 * 
 */
public class XPathUtil {
	private static XPath xPath = XPathFactory.newInstance().newXPath();
	private static final Logger logger = Logger.getLogger(XPathUtil.class);
	public static String getNodeValue(Object item, String path) {
		Node node = getNode(item, path);
		if (node != null) {
			return node.getNodeValue();
		}
		return null;
	}

	public static Node getNode(Object item, String path) {
		try {
			XPathExpression pathExpression = xPath.compile(path);
			Node node = (Node) pathExpression.evaluate(item, XPathConstants.NODE);
			return node;
		} catch (XPathExpressionException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static NodeList getNodeList(Object item, String path) {
		try {
			XPathExpression pathExpression = xPath.compile(path);
			NodeList node = (NodeList) pathExpression.evaluate(item, XPathConstants.NODESET);
			return node;
		} catch (XPathExpressionException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
