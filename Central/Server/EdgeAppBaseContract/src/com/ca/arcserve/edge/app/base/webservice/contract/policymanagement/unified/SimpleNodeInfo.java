package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;


public class SimpleNodeInfo implements Serializable{
	private int id;
	private String name;
	
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
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SimpleNodeInfo))
			return false;
		return id==((SimpleNodeInfo)obj).getId();
	}
	
	@Override
	public String toString() {
		return name;
	}
}
