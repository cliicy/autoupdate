package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeFilter {
	private int id;
	private String name;
	private String filterXml;
	private int type;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFilterXml() {
		return filterXml;
	}
	public void setFilterXml(String filterXml) {
		this.filterXml = filterXml;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
