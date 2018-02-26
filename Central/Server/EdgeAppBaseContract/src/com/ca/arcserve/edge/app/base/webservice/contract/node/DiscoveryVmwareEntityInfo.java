package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType( XmlAccessType.FIELD )
public class DiscoveryVmwareEntityInfo implements Serializable {
	private static final long serialVersionUID = 6470795846287533916L;
	private String name;
	private String entityType;
	private String refId;
	
	//for instantvm getNetworkInfo. only in root node.
	private boolean vCenter;
	
	public boolean isvCenter() {
		return vCenter;
	}
	public void setvCenter(boolean vCenter) {
		this.vCenter = vCenter;
	}
	
	/**
	 * used to identify object when do jaxb serialization in web service
	 */
	@XmlID
	private String globalIdForwebService; 
	private DiscoveryVirtualMachineInfo discoveryVMInfo;
	@XmlIDREF 
	private DiscoveryVmwareEntityInfo parent;
	private ArrayList<DiscoveryVmwareEntityInfo> children = new ArrayList<DiscoveryVmwareEntityInfo>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		entityType = type;
	}
	
	public String getType() {
		return entityType;
	}
	
	public DiscoveryVirtualMachineInfo getVMInfo() {
		return discoveryVMInfo;
	}
	
	public void setVMInfo(DiscoveryVirtualMachineInfo vmInfo) {
		if(this.discoveryVMInfo == vmInfo)
			return;
		if(this.discoveryVMInfo == null) 
			discoveryVMInfo = new DiscoveryVirtualMachineInfo();
		discoveryVMInfo.setbRunning(vmInfo.isbRunning());
		discoveryVMInfo.setVmEsxHost(vmInfo.getVmEsxHost());
		discoveryVMInfo.setVmGuestOS(vmInfo.getVmGuestOS());
		discoveryVMInfo.setVmHostName(vmInfo.getVmHostName());
		discoveryVMInfo.setVmInstanceUuid(vmInfo.getVmInstanceUuid());
		discoveryVMInfo.setVmIP(vmInfo.getVmIP());
		discoveryVMInfo.setVmName(vmInfo.getVmName());
		discoveryVMInfo.setVmServerType(vmInfo.getVmServerType());
		discoveryVMInfo.setVmUuid(vmInfo.getVmUuid());
		discoveryVMInfo.setVmXPath(vmInfo.getVmXPath());
		discoveryVMInfo.setWindowsOS(vmInfo.isWindowsOS());
		discoveryVMInfo.setVmConnectionState(vmInfo.getVmConnectionState());
		discoveryVMInfo.setVmEsxSocketCount(vmInfo.getVmEsxSocketCount());
		discoveryVMInfo.setVmEsxEssential(vmInfo.isVmEsxEssential());
		discoveryVMInfo.setManagedByVCloud(vmInfo.isManagedByVCloud());
	}
	public ArrayList<DiscoveryVmwareEntityInfo> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<DiscoveryVmwareEntityInfo> children) {
		this.children = children;
	}
	
	public void addChild(DiscoveryVmwareEntityInfo child) {
		children.add(child);
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public DiscoveryVmwareEntityInfo getParent() {
		return parent;
	}
	public void setParent(DiscoveryVmwareEntityInfo parent) {
		this.parent = parent;
	}
	public String getGlobalIdForwebService() {
		return globalIdForwebService;
	}
	public void setGlobalIdForwebService(String globalIdForwebService) {
		this.globalIdForwebService = globalIdForwebService;
	}

}

