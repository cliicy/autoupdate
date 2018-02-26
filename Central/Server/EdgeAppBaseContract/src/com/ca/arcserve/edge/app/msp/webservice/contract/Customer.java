package com.ca.arcserve.edge.app.msp.webservice.contract;

import java.io.Serializable;

public class Customer implements Serializable {

	private static final long serialVersionUID = 6867585476246007329L;
	
	private int id;
	private String name;
	private String description;
	private String password;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
