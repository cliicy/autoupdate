package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

public class PlanTask implements Serializable{
	private static final long serialVersionUID = -8488768794104834954L;
	private String id;
	private String rpsId;
	private String name;
	public PlanTask(){}
	public PlanTask(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRpsId() {
		return rpsId;
	}
	public void setRpsId(String rpsId) {
		this.rpsId = rpsId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
