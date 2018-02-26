package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;
import java.util.List;

public class ASBUMediaPoolSet implements Serializable{
	private static final long serialVersionUID = -13991048129083020L;
	private String name;
	private List<ASBUMediaPool> mediaPools;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ASBUMediaPool> getMediaPools() {
		return mediaPools;
	}
	public void setMediaPools(List<ASBUMediaPool> mediaPools) {
		this.mediaPools = mediaPools;
	}
}
