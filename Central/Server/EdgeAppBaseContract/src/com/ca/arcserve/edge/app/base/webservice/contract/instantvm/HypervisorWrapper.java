package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.instantvm.HypervisorType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;


/**
 * why we define this class; because we reuse many other component and they define different data structure to describe vc and esx data;
 * it's very hard to use these data without conflict; so we wrapper it; every time InstantVm use the detailed information about the esx and vc,
 * it should retrieve this class and change to the data you need
 * @author fanda03
 *The esxhost property store the esx host detail information including data-center information;
 *the hypervisorSupportInfo property store detailed information about for both esx and hyperv
 */
public class HypervisorWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	private Hypervisor hypervisor;
	// /this property( support Info) now only used in UI!
	private EsxHostInformation hypervisorSupportInfo;
	// liuyu07 this property only used in UI
	private DiscoveryVmwareEntityInfo vmWareEntity;
	private HypervisorType hypervisorType;
	private VMWareInfoForIVM vmWareInfo;
	private String vmLocation;
	private NodeDetail nodeDetail;
	
	public Hypervisor getHyperVisor() {
		return hypervisor;
	}
	public void setHyperVisor(Hypervisor currentHyperVisor) {
		this.hypervisor = currentHyperVisor;
	}
	public EsxHostInformation getHypervisorSupportInfo() {
		return hypervisorSupportInfo;
	}
	public void setHypervisorSupportInfo(EsxHostInformation hypervisorSupportInfo) {
		this.hypervisorSupportInfo = hypervisorSupportInfo;
	}
	public HypervisorType getHypervisorType() {
		return hypervisorType;
	}
	public void setHypervisorType(HypervisorType hypervisorType) {
		this.hypervisorType = hypervisorType;
	}
	public DiscoveryVmwareEntityInfo getVmWareEntity() {
		return vmWareEntity;
	}
	public void setVmWareEntity(DiscoveryVmwareEntityInfo vmWareEntity) {
		this.vmWareEntity = vmWareEntity;
	}
	public VMWareInfoForIVM getVmWareInfo() {
		return vmWareInfo;
	}
	public void setVmWareInfo(VMWareInfoForIVM vmWareInfo) {
		this.vmWareInfo = vmWareInfo;
	}
	public String getVmLocation() {
		return vmLocation;
	}
	public void setVmLocation(String vmLocation) {
		this.vmLocation = vmLocation;
	}
	public NodeDetail getNodeDetail() {
		return nodeDetail;
	}
	public void setNodeDetail(NodeDetail nodeDetail) {
		this.nodeDetail = nodeDetail;
	}
	
}
