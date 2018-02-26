package com.ca.arcserve.edge.app.base.webservice.contract.discovery;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting.SettingType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryStatus;
@XmlRootElement
public class DiscoveryHistory implements Serializable {

	private static final long serialVersionUID = -1194528425683404717L;
	private int id;
	private Date startTime;
	private Date endTime;
	private int result;
	private DiscoveryStatus status;
	private SettingType discoveryType;
	private int existNodeNum;
	
	public int getExistNodeNum() {
		return existNodeNum;
	}
	public void setExistNodeNum(int existNodeNum) {
		this.existNodeNum = existNodeNum;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public DiscoveryStatus getStatus() {
		return status;
	}
	public void setStatus(DiscoveryStatus status) {
		this.status = status;
	}
	public SettingType getDiscoveryType() {
		return discoveryType;
	}
	public void setDiscoveryType(SettingType discoveryType) {
		this.discoveryType = discoveryType;
	}
	
}