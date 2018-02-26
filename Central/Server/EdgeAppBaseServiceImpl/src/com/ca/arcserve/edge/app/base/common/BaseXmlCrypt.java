package com.ca.arcserve.edge.app.base.common;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class BaseXmlCrypt {
	private String[] matchTag;
	private static final Logger logger = Logger.getLogger(BaseXmlCrypt.class);
	public BaseXmlCrypt(String[] matchTag) {
		this.matchTag = matchTag;
	}

	protected String analyzeXml_NoException(String xml) {
		try {
			return analyzeXml(xml);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return xml;
		}
	}

	protected String analyzeXml(String xml) throws ParserConfigurationException,
			SAXException, IOException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.parse(new InputSource(new StringReader(xml)));
		analyzeNode(dom);
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		DOMSource source = new DOMSource(dom);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		return writer.toString();
	}

	private void analyzeNode(Node node) {
		if (node instanceof Element) {
			analyzeAttributes(node.getAttributes());
			if (isSecretTag(node.getNodeName())) {
				encryptText(node);
			}
		}
		if (node instanceof org.w3c.dom.CharacterData)
			return;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			analyzeNode(n);
		}
	}

	private void encryptText(Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node c = children.item(i);
			if (c instanceof Text) {
				String content = ((Text) c).getData();
				if (isContentNeedEncrypt(content)) {
					((Text) c).setData(doEncrypt(content));
				}
			}
		}
	}

	private void analyzeAttributes(NamedNodeMap attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr att = (Attr) attributes.item(i);
			String n = att.getNodeName();
			if (isSecretTag(n)) {
				String content = att.getValue();
				if (isContentNeedEncrypt(content)) {
					att.setValue(doEncrypt(content));
				}
			}
		}
	}

	protected abstract String doEncrypt(String s);

	private boolean isContentNeedEncrypt(String content) {
		if (content != null) {
//			content = content.trim();	// defect 95684
			if (!content.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private boolean isSecretTag(String name) {
		if (name == null || name.isEmpty())
			return false;
		if (name.indexOf(":") > 0)
			name = name.substring(name.indexOf(":") + 1);
		for (String t : matchTag) {
			if (Pattern.compile(t, Pattern.CASE_INSENSITIVE).matcher(name)
					.matches())
				return true;
		}
		return false;
	}

}
