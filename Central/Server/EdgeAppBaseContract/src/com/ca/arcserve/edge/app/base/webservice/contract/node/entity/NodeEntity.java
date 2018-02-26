package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ArcserveInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.D2DInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.RemoteDeployInfoSummary;

public class NodeEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private NodeSummary nodeSummary;
	private VmInfoSummary   vmInfoSummary;
	private PlanSummary planSummary;
	private JobSummary  jobSummary;
	private NodeVcloudSummary nodeVcloudSummary;
	private ArcserveInfoSummary arcserveInfoSummary;
	private D2DInfoSummary d2dInfoSummary;
	private RemoteDeployInfoSummary remoteDeployInfoSummary;
	private ConverterSummary converterSummary;
	private GatewaySummary gatewaySummary;
	private VsbSummary vsbSummary;
	private ProxyInfoSummary proxyInfoSummary;
	private LinuxD2DInfoSummary linuxD2DInfoSummary;

	public NodeSummary getNodeSummary() {
		return nodeSummary;
	}
	public void setNodeSummary(NodeSummary nodeSummary) {
		this.nodeSummary = nodeSummary;
	}
	public VmInfoSummary getVmInfoSummary() {
		return vmInfoSummary;
	}
	public void setVmInfoSummary(VmInfoSummary vmInfoSummary) {
		this.vmInfoSummary = vmInfoSummary;
	}
	public PlanSummary getPlanSummary() {
		return planSummary;
	}
	public void setPlanSummary(PlanSummary planSummary) {
		this.planSummary = planSummary;
	}
	public JobSummary getJobSummary() {
		return jobSummary;
	}
	public void setJobSummary(JobSummary jobSummary) {
		this.jobSummary = jobSummary;
	}
	public NodeVcloudSummary getNodeVcloudSummary() {
		return nodeVcloudSummary;
	}
	public void setNodeVcloudSummary(NodeVcloudSummary nodeVcloudSummary) {
		this.nodeVcloudSummary = nodeVcloudSummary;
	}
	public ArcserveInfoSummary getArcserveInfoSummary() {
		return arcserveInfoSummary;
	}
	public void setArcserveInfoSummary(ArcserveInfoSummary arcserveInfoSummary) {
		this.arcserveInfoSummary = arcserveInfoSummary;
	}
	public D2DInfoSummary getD2dInfoSummary() {
		return d2dInfoSummary;
	}
	public void setD2dInfoSummary(D2DInfoSummary d2dInfoSummary) {
		this.d2dInfoSummary = d2dInfoSummary;
	}
	public RemoteDeployInfoSummary getRemoteDeployInfoSummary() {
		return remoteDeployInfoSummary;
	}
	public void setRemoteDeployInfoSummary(
			RemoteDeployInfoSummary remoteDeployInfoSummary) {
		this.remoteDeployInfoSummary = remoteDeployInfoSummary;
	}
	public ConverterSummary getConverterSummary() {
		return converterSummary;
	}
	public void setConverterSummary(ConverterSummary converterSummary) {
		this.converterSummary = converterSummary;
	}
	public GatewaySummary getGatewaySummary() {
		return gatewaySummary;
	}
	public void setGatewaySummary(GatewaySummary gatewaySummary) {
		this.gatewaySummary = gatewaySummary;
	}
	
	public VsbSummary getVsbSummary() {
		return vsbSummary;
	}
	public void setVsbSummary(VsbSummary vsbSummary) {
		this.vsbSummary = vsbSummary;
	}
	public ProxyInfoSummary getProxyInfoSummary() {
		return proxyInfoSummary;
	}
	public void setProxyInfoSummary(ProxyInfoSummary proxyInfoSummary) {
		this.proxyInfoSummary = proxyInfoSummary;
	}
	
	public LinuxD2DInfoSummary getLinuxD2DInfoSummary() {
		return linuxD2DInfoSummary;
	}
	public void setLinuxD2DInfoSummary(LinuxD2DInfoSummary linuxD2DInfoSummary) {
		this.linuxD2DInfoSummary = linuxD2DInfoSummary;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeEntity other = (NodeEntity) obj;

		if(!Utils.simpleObjectEquals(nodeSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(vmInfoSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(planSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(jobSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(nodeVcloudSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(arcserveInfoSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(d2dInfoSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(remoteDeployInfoSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(converterSummary, other.getConverterSummary()))
			return false;
		if(!Utils.simpleObjectEquals(gatewaySummary, other.getGatewaySummary()))
			return false;
		if(!Utils.simpleObjectEquals(vsbSummary, other.getVsbSummary()))
			return false;
		if(!Utils.simpleObjectEquals(proxyInfoSummary, other.getProxyInfoSummary()))
			return false;
		if(!Utils.simpleObjectEquals(linuxD2DInfoSummary, other.getLinuxD2DInfoSummary()))
			return false;
		return true;
	}
	
	public boolean equalsIgnoreVcloudSummary(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeEntity other = (NodeEntity) obj;

		if(!Utils.simpleObjectEquals(nodeSummary, other.getNodeSummary()))
			return false;
		if(!Utils.simpleObjectEquals(vmInfoSummary, other.getVmInfoSummary()))
			return false;
		if(!Utils.simpleObjectEquals(planSummary, other.getPlanSummary()))
			return false;
		if(!Utils.simpleObjectEquals(jobSummary, other.getJobSummary()))
			return false;
		if(!Utils.simpleObjectEquals(arcserveInfoSummary, other.getArcserveInfoSummary()))
			return false;
		if(!Utils.simpleObjectEquals(d2dInfoSummary, other.getD2dInfoSummary()))
			return false;
		if(!Utils.simpleObjectEquals(remoteDeployInfoSummary, other.getRemoteDeployInfoSummary()))
			return false;
		if(!Utils.simpleObjectEquals(converterSummary, other.getConverterSummary()))
			return false;
		if(!Utils.simpleObjectEquals(gatewaySummary, other.getGatewaySummary()))
			return false;
		if(!Utils.simpleObjectEquals(vsbSummary, other.getVsbSummary()))
			return false;
		if(!Utils.simpleObjectEquals(proxyInfoSummary, other.getProxyInfoSummary()))
			return false;
		if(!Utils.simpleObjectEquals(linuxD2DInfoSummary, other.getLinuxD2DInfoSummary()))
			return false;
		return true;
	}
	
	public void updateIgnoreVcloudSummary(NodeEntity other) {
		if(other == null){
			return ;
		}
		if(!Utils.simpleObjectEquals(nodeSummary, other.getNodeSummary())){
			if(nodeSummary == null){
				nodeSummary = other.getNodeSummary();
			}else if(other.getNodeSummary() == null){
				nodeSummary = null;
			}else {
				nodeSummary.update(other.getNodeSummary());
			}
		}
		if(!Utils.simpleObjectEquals(vmInfoSummary, other.getVmInfoSummary())){
			if(vmInfoSummary == null){
				vmInfoSummary = other.getVmInfoSummary();
			}else if(other.getVmInfoSummary() == null){
				vmInfoSummary = null;
			}else {
				vmInfoSummary.update(other.getVmInfoSummary());
			}
		}
		if(!Utils.simpleObjectEquals(planSummary, other.getPlanSummary())){
			if(planSummary == null){
				planSummary = other.getPlanSummary();
			}else if(other.getPlanSummary() == null){
				planSummary = null;
			}else {
				planSummary.update(other.getPlanSummary());
			}
		}
		if(!Utils.simpleObjectEquals(jobSummary, other.getJobSummary())){
			if(jobSummary == null){
				jobSummary = other.getJobSummary();
			}else if(other.getJobSummary() == null){
				jobSummary = null;
			}else {
				jobSummary.update(other.getJobSummary());
			}
		}
		if(!Utils.simpleObjectEquals(arcserveInfoSummary, other.getArcserveInfoSummary())){
			if(arcserveInfoSummary == null){
				arcserveInfoSummary = other.getArcserveInfoSummary();
			}else if(other.getArcserveInfoSummary() == null){
				arcserveInfoSummary = null;
			}else {
				arcserveInfoSummary.update(other.getArcserveInfoSummary());
			}
		}
		if(!Utils.simpleObjectEquals(d2dInfoSummary, other.getD2dInfoSummary())){
			if(d2dInfoSummary == null){
				d2dInfoSummary = other.getD2dInfoSummary();
			}else if(other.getD2dInfoSummary() == null){
				d2dInfoSummary = null;
			}else {
				d2dInfoSummary.update(other.getD2dInfoSummary());
			}
		}
		if(!Utils.simpleObjectEquals(remoteDeployInfoSummary, other.getRemoteDeployInfoSummary())){
			if(remoteDeployInfoSummary == null){
				remoteDeployInfoSummary = other.getRemoteDeployInfoSummary();
			}else if(other.getRemoteDeployInfoSummary() == null){
				remoteDeployInfoSummary = null;
			}else {
				remoteDeployInfoSummary.update(other.getRemoteDeployInfoSummary());
			}
		}
		if(!Utils.simpleObjectEquals(converterSummary,  other.getConverterSummary())){
			if(converterSummary == null){
				converterSummary = other.getConverterSummary();
			}else if (other.getConverterSummary() == null) {
				converterSummary = null;
			}else {
				converterSummary.update(other.getConverterSummary());
			}
		}
		if(!Utils.simpleObjectEquals(gatewaySummary, other.getGatewaySummary())){
			if(gatewaySummary == null){
				gatewaySummary = other.getGatewaySummary();
			}else if (other.getGatewaySummary() == null) {
				gatewaySummary = null;
			}else {
				gatewaySummary.update(other.getGatewaySummary());
			}
		}
		if(!Utils.simpleObjectEquals(vsbSummary, other.getVsbSummary())){
			if(vsbSummary == null){
				vsbSummary = other.getVsbSummary();
			}else if (other.getVsbSummary() == null) {
				vsbSummary = null;
			}else {
				vsbSummary.update(other.getVsbSummary());
			}
		}
		if(!Utils.simpleObjectEquals(proxyInfoSummary, other.getProxyInfoSummary())){
			if(proxyInfoSummary == null){
				proxyInfoSummary = other.getProxyInfoSummary();
			}else if (other.getProxyInfoSummary() == null) {
				proxyInfoSummary = null;
			}else {
				proxyInfoSummary.update(other.getProxyInfoSummary());
			}
		}
		if(!Utils.simpleObjectEquals(linuxD2DInfoSummary, other.getLinuxD2DInfoSummary())){
			if(linuxD2DInfoSummary == null){
				linuxD2DInfoSummary = other.getLinuxD2DInfoSummary();
			}else if (other.getLinuxD2DInfoSummary() == null) {
				linuxD2DInfoSummary = null;
			}else {
				linuxD2DInfoSummary.update(other.getLinuxD2DInfoSummary());
			}
		}
	}
}
