package com.ca.arcserve.edge.app.rps.appdaos.model;

import java.sql.Timestamp;

public class EdgeRpsPolicy {
	private int policy_id;
	private String policy_uuid;
	private int node_id;
	private String policy_name;
	private String file_store_path;
	private long storage_size_limit;
	private String external_setting;
	private int status;
	private Timestamp last_updated;
	private int datastore_id;
	private String datastore_name;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getPolicy_id() {
		return policy_id;
	}
	public void setPolicy_id(int policy_id) {
		this.policy_id = policy_id;
	}
	public String getPolicy_uuid() {
		return policy_uuid;
	}
	public void setPolicy_uuid(String policy_uuid) {
		this.policy_uuid = policy_uuid;
	}
	public int getNode_id() {
		return node_id;
	}
	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}
	public String getPolicy_name() {
		return policy_name;
	}
	public void setPolicy_name(String policy_name) {
		this.policy_name = policy_name;
	}
	public String getFile_store_path() {
		return file_store_path;
	}
	public void setFile_store_path(String file_store_path) {
		this.file_store_path = file_store_path;
	}
	public long getStorage_size_limit() {
		return storage_size_limit;
	}
	public void setStorage_size_limit(long storage_size_limit) {
		this.storage_size_limit = storage_size_limit;
	}
	public String getExternal_setting() {
		return external_setting;
	}
	public void setExternal_setting(String external_setting) {
		this.external_setting = external_setting;
	}
	public Timestamp getLast_updated() {
		return last_updated;
	}
	public void setLast_updated(Timestamp last_updated) {
		this.last_updated = last_updated;
	}
	public int getDatastore_id() {
		return datastore_id;
	}
	public void setDatastore_id(int datastore_id) {
		this.datastore_id = datastore_id;
	}
	public String getDatastore_name() {
		return datastore_name;
	}
	public void setDatastore_name(String datastore_name) {
		this.datastore_name = datastore_name;
	}
	
}
