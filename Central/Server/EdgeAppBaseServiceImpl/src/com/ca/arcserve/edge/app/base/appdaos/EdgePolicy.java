package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;

public class EdgePolicy {
	private int id;
	private String name;
	private String policyxml;
	private int type;// 1:D2D or 2:ARCserve.
	private int contentflag;
	private String version;// edge version?
	private Date creationtime;
	private Date modifiedtime;
	private String uuid;
	private int producttype;
	private PlanEnableStatus enablestatus;
	private PlanStatus status;
	
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
	public String getPolicyxml() {
		return policyxml;
	}
	public void setPolicyxml(String policyxml) {
		this.policyxml = policyxml;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getContentflag() {
		return contentflag;
	}
	public void setContentflag(int contentflag) {
		this.contentflag = contentflag;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getCreationtime() {
		return creationtime;
	}
	public void setCreationtime(Date creationtime) {
		this.creationtime = creationtime;
	}
	public Date getModifiedtime() {
		return modifiedtime;
	}
	public void setModifiedtime(Date modifiedtime) {
		this.modifiedtime = modifiedtime;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getProducttype() {
		return producttype;
	}
	public void setProducttype(int producttype) {
		this.producttype = producttype;
	}
	public PlanEnableStatus getEnablestatus() {
		return enablestatus;
	}
	public void setEnablestatus(PlanEnableStatus enablestatus) {
		this.enablestatus = enablestatus;
	}
	public PlanStatus getStatus() {
		return status;
	}
	public void setStatus(PlanStatus status) {
		this.status = status;
	}
	
}
