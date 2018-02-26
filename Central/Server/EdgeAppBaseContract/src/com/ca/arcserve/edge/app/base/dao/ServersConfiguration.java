package com.ca.arcserve.edge.app.base.dao;

import javax.xml.bind.annotation.XmlElement;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "name", "status" })
public class ServersConfiguration
{

	private String name;
	private String status;
	

	@XmlElement
	public String getName() {
		return name;
	}


	public void setName(String Name) {
		this.name = Name;
	}

	@XmlElement
	public String getStatus() {
		return status;
	}


	public void setStatus(String Status) {
		this.status = Status;
	}

	


	}
