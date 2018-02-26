package com.ca.arcflash.webservice.replication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

public class ConversionSessionHistory implements Serializable {
	private static final long serialVersionUID = -614494750863571L;

	private List<ConversionSessionItem> items = new ArrayList<ConversionSessionItem>();

	@XmlElements(@XmlElement(name = "Item", type = ConversionSessionItem.class))
	@XmlElementWrapper(name = "History")
	public List<ConversionSessionItem> getItems() {
		return items;
	}

	public void setItems(List<ConversionSessionItem> items) {
		this.items = items;
	}

}
