package com.ca.arcflash.webservice.jni.model;

import java.io.Serializable;
import java.util.List;


public class JHypervVMInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String vmName;
	private String vmUuid;
	private String vmHostName;
	private String vmGuestOS;
	private int vmPowerStatus;
	private int vmInteServiceSatus;
	private List<String> ipList;
	private int vmType; //0: unknown; 1: stand alone VM; 2: stand alone VM in CSV or cluster resource VM
	private String hypervisor; 
	private String clusterName;
	private int  vmCpuNum;
	private int vmMemoryMB;

	public List<String> getIpList() {
		return ipList;
	}
	public void setIpList(List<String> ipList) {
		this.ipList = ipList;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getVmUuid() {
		return vmUuid;
	}
	public void setVmUuid(String vmUuid) {
		this.vmUuid = vmUuid;
	}
	public String getVmHostName() {
		return vmHostName;
	}
	public void setVmHostName(String vmHostName) {
		this.vmHostName = vmHostName;
	}
	
	public String getVmGuestOS() {
		return vmGuestOS;
	}
	public void setVmGuestOS(String vmGuestOS) {
		this.vmGuestOS = vmGuestOS;
	}
	public int getVmPowerStatus() {
		return vmPowerStatus;
	}
	public int getVmInteServiceSatus() {
		return vmInteServiceSatus;
	}
	public void setVmPowerStatus(int vmPowerStatus) {
		this.vmPowerStatus = vmPowerStatus;
	}
	public void setVmInteServiceSatus(int vmInteServiceSatus) {
		this.vmInteServiceSatus = vmInteServiceSatus;
	}
	public int getVmType() {
		return vmType;
	}
	public void setVmType(int vmType) {
		this.vmType = vmType;
	}
	public String getHypervisor() {
		return hypervisor;
	}
	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public int getVmCpuNum() {
		return vmCpuNum;
	}
	public void setVmCpuNum(int vmCpuNum) {
		this.vmCpuNum = vmCpuNum;
	}
	public int getVmMemoryMB() {
		return vmMemoryMB;
	}
	public void setVmMemoryMB(int vmMemoryMB) {
		this.vmMemoryMB = vmMemoryMB;
	}
}
