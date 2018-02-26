package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="DiscoveryOption")
public class DiscoveryOption implements Serializable {

	private static final long serialVersionUID = 7833411676430414644L;

	private int id;
	private String 							targetComputerName;
	private String 							userName;
	@NotPrintAttribute
	private String 							password;
	private DiscoveryOperatingSystem	 	nodeOperatingSystem = DiscoveryOperatingSystem.EDGE_DISCOVERY_NODE_OS_ALL;
	private Set<DiscoveryApplication>	 	applicationFilter = null;
	private String							computerNameFilter = "*";
	private int udpTimeout	= 30;
	private int udpPort = 7777;
	private int jobType	= 0;  // 0:manual job  1:schedule job 
	private int taskId = 0;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	public GatewayId getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(GatewayId gatewayId) {
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setTargetComputerName(String targetComputerName) {
		this.targetComputerName = targetComputerName;
	}

	public String getTargetComputerName() {
		return targetComputerName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		}

	public String getUserName() {
		return userName;
	}

	public void setPassword(String password) {
		this.password = password;
		}
	@EncryptSave
	public String getPassword() {
		return password;
	}

	public void setNodeOperatingSystem(DiscoveryOperatingSystem nodeOperatingSystem) {
		this.nodeOperatingSystem = nodeOperatingSystem;
	}

	public DiscoveryOperatingSystem getNodeOperatingSystem() {
		return nodeOperatingSystem;
	}

	public void setApplicationFilter(Set<DiscoveryApplication> applicationFilter) {
		this.applicationFilter = applicationFilter;
	}

	public Set<DiscoveryApplication> getApplicationFilter() {
		return applicationFilter;
	}

	public void setComputerNameFilter(String computerNameFilter) {
		this.computerNameFilter = computerNameFilter;
	}

	public String getComputerNameFilter() {
		return computerNameFilter;
	}
	public int getUdpTimeout() {
		return udpTimeout;
	}

	public void setUdpTimeout(int udpTimeout) {
		this.udpTimeout = udpTimeout;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

	public int getJobType() {
		return jobType;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
}
