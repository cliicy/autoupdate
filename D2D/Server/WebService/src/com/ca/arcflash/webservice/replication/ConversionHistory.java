package com.ca.arcflash.webservice.replication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ConversionHistory implements Serializable{
	
	private static final long serialVersionUID = -614494750863561L;
	
	private List<ConversionSources> nodes = new ArrayList<ConversionSources>();
	@XmlElements(@XmlElement(name="Node", type=ConversionSources.class))
	@XmlElementWrapper(name="Nodes")
	public List<ConversionSources> getNodes() {
		return nodes;
	}
	
	public void setNodes(List<ConversionSources> nodes) {
		this.nodes = nodes;
	}
}
