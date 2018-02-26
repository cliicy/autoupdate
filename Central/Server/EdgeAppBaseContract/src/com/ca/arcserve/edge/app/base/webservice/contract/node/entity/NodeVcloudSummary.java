package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class NodeVcloudSummary implements Serializable{
	private static final long serialVersionUID = 1L;
	private int nodeId;
	private String vCenter;
	private String vdc;
	private String organization;
	private String vCloudDirector;
	
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public String getvCenter() {
		return vCenter;
	}
	public void setvCenter(String vCenter) {
		this.vCenter = vCenter;
	}
	public String getVdc() {
		return vdc;
	}
	public void setVdc(String vdc) {
		this.vdc = vdc;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getvCloudDirector() {
		return vCloudDirector;
	}
	public void setvCloudDirector(String vCloudDirector) {
		this.vCloudDirector = vCloudDirector;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeVcloudSummary other = (NodeVcloudSummary) obj;
		
		if(nodeId != other.getNodeId())
			return false;
		if(!StringUtil.isEqual(vCenter, other.getvCenter()))
			return false;
		if(!StringUtil.isEqual(vdc, other.getVdc()))
			return false;
		if(!StringUtil.isEqual(organization, other.getOrganization()))
			return false;
		if(!StringUtil.isEqual(vCloudDirector, other.getvCloudDirector()))
			return false;
		
		return true;
	}
	
	public void update(NodeVcloudSummary other){
		if(other == null){
			return;
		}
		if(nodeId != other.getNodeId())
			nodeId = other.getNodeId();
		if(!StringUtil.isEqual(vCenter, other.getvCenter()))
			vCenter = other.getvCenter();
		if(!StringUtil.isEqual(vdc, other.getVdc()))
			vdc = other.getVdc();
		if(!StringUtil.isEqual(organization, other.getOrganization()))
			organization = other.getOrganization();
		if(!StringUtil.isEqual(vCloudDirector, other.getvCloudDirector()))
			vCloudDirector = other.getvCloudDirector();
	}
}
