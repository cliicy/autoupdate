package com.ca.arcserve.edge.app.rps.appdaos.model;

import java.sql.Timestamp;

public class EdgeRpsDataStore {
	private int datastore_id;
	private int node_id;
	private String datastore_name;
	private String datastore_setting;
	private int status;
	private Timestamp last_updated;
	private String datastore_uuid;
	public int getDatastore_id() {
		return datastore_id;
	}
	public void setDatastore_id(int datastore_id) {
		this.datastore_id = datastore_id;
	}
	public int getNode_id() {
		return node_id;
	}
	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}
	public String getDatastore_name() {
		return datastore_name;
	}
	public void setDatastore_name(String datastore_name) {
		this.datastore_name = datastore_name;
	}
	public String getDatastore_setting() {
		return datastore_setting;
	}
	public void setDatastore_setting(String datastore_setting) {
		this.datastore_setting = datastore_setting;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Timestamp getLast_updated() {
		return last_updated;
	}
	public void setLast_updated(Timestamp last_updated) {
		this.last_updated = last_updated;
	}
	public String getDatastore_uuid() {
		return datastore_uuid;
	}
	public void setDatastore_uuid(String datastore_uuid) {
		this.datastore_uuid = datastore_uuid;
	}
	
}
