package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;

public class ASBUMediaPool implements Serializable{
	private static final long serialVersionUID = -1623846992537612325L;
	private String name;
	private int value;
	private boolean isSelected;
	private MediaPoolType type;
	private int mediaUsageMode;
	private boolean bMuxEnable;
    private int nMuxStream;

	public int getMediaUsageMode() {
		return mediaUsageMode;
	}

	public void setMediaUsageMode(int mediaUsageMode) {
		this.mediaUsageMode = mediaUsageMode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public MediaPoolType getType() {
		return type;
	}

	public void setType(MediaPoolType type) {
		this.type = type;
	}

	public boolean isbMuxEnable() {
		return bMuxEnable;
	}

	public void setbMuxEnable(boolean bMuxEnable) {
		this.bMuxEnable = bMuxEnable;
	}

	public int getnMuxStream() {
		return nMuxStream;
	}

	public void setnMuxStream(int nMuxStream) {
		this.nMuxStream = nMuxStream;
	}
	
	
}
