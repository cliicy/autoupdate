package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;
import java.util.List;

public class ASBUMediaGroupInfo implements Serializable {
	private static final long serialVersionUID = -4194238397105906585L;

	private int id;
	private String name;
	private ASBUMediaGroupType type;
	private ASBURegularGroup regularType;
	private int number;
	private List<ASBUMediaInfo> mediaList;

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

	public ASBUMediaGroupType getType() {
		return type;
	}
	
	public ASBURegularGroup getRegularType() {
		return regularType;
	}

	public void setRegularType(ASBURegularGroup regularType) {
		this.regularType = regularType;
	}

	public void setType(ASBUMediaGroupType type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<ASBUMediaInfo> getMediaList() {
		return mediaList;
	}

	public void setMediaList(List<ASBUMediaInfo> mediaList) {
		this.mediaList = mediaList;
	}
}
