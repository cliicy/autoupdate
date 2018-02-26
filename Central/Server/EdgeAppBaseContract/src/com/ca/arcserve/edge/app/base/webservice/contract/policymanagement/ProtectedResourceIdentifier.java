package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;

/**
 * Protected Resource Key contains node ids and kinds of group ids
 * @author zhaji22
 *
 */
public class ProtectedResourceIdentifier implements Serializable{
	private static final long serialVersionUID = 1L;
	private ProtectedResourceType type;
	private int id;
	
	public ProtectedResourceType getType() {
		return type;
	}
	public void setType(ProtectedResourceType type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ProtectedResourceIdentifier){
			ProtectedResourceIdentifier objIdentifier = (ProtectedResourceIdentifier)obj;
			if(this.id == objIdentifier.getId() && this.type == objIdentifier.getType()){
				return true;
			}
		}
		return false;
	}
}
