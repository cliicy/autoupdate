package com.ca.arcflash.webservice.scheduler;

import javax.xml.bind.annotation.XmlElement;

public class MakeupItem {
	@XmlElement
	public long time;
	@XmlElement
	public ConfilctData data;

	private MakeupItem() {
	}

	public MakeupItem(Long key, ConfilctData value) {
		this.time = key;
		this.data = value;
	}
}
