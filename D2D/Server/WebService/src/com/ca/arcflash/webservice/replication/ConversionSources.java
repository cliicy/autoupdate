package com.ca.arcflash.webservice.replication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConversionSources implements Serializable {
	private static final long serialVersionUID = -614494750863560L;
	
	private String afGuid;
	public String getAfGuid() {
		return afGuid;
	}
	
	public void setAfGuid(String afGuid) {
		this.afGuid = afGuid;
	}
	
	private List<ConversionSource> sources = new ArrayList<ConversionSource>();
	@XmlElements(@XmlElement(name="Source", type=ConversionSource.class))
	@XmlElementWrapper(name="Sources")
	public List<ConversionSource> getSources() {
		return sources;
	}
	
	public void setSources(List<ConversionSource> sources) {
		this.sources = sources;
	}
	
}
