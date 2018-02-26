package com.ca.arcflash.ui.client.model.rps;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RpsPolicy4D2D extends BaseModelData {
	private static final long serialVersionUID = -3332939135367576363L;
	
	private int policyid;
	private String id  ;       // UUID to identify an RPS policy

	private String name;       // policy name
	
	private int dataStoreId;
	private String dataStoreName;
	private String dataStoreDisplayName;
	private boolean enableGDD;

	public int getPolicyid() {
		return policyid;
	}

	public void setPolicyid(int policyid) {
		this.policyid = policyid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDataStoreId() {
		return dataStoreId;
	}

	public void setDataStoreId(int dataStoreId) {
		this.dataStoreId = dataStoreId;
	}

	public String getDataStoreName() {
		return dataStoreName;
	}

	public void setDataStoreName(String dataStoreName) {
		this.dataStoreName = dataStoreName;
	}

	public String getDataStoreDisplayName() {
		return dataStoreDisplayName;
	}

	public void setDataStoreDisplayName(String dataStoreDisplayName) {
		this.dataStoreDisplayName = dataStoreDisplayName;
	}

	public boolean isEnableGDD() {
		return enableGDD;
	}

	public void setEnableGDD(boolean enableGDD) {
		this.enableGDD = enableGDD;
	}

}
