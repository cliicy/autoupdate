package com.ca.arcserve.edge.app.base.webservice.contract.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;

public class ActionTaskParameter<T extends Serializable> implements Serializable{
	private static final long serialVersionUID = 6617810920591343235L;
	private Module module;
	private List<T> entityIds = new ArrayList<T>();
	public Module getModule() {
		return module;
	}
	public void setModule(Module module) {
		this.module = module;
	}
	public List<T> getEntityIds() {
		return entityIds;
	}
	public void setEntityIds(List<T> entityIds) {
		this.entityIds = entityIds;
	}
}
