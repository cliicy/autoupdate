package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class DiscoveryESXOption implements Serializable {

	private static final long serialVersionUID = 7466623627636145570L;
	
	private int id;
	private String esxServerName;
	private String esxUserName;
	private @NotPrintAttribute String esxPassword;
	private Protocol protocol = Protocol.Https;
	private boolean ignoreCertificate = true;
	private int port = 0;
	private String esxHost;
	private int jobType	= 0;  // 0:manual job  1:schedule job
	private boolean addEsxToADList;
	private int taskId;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;	
	private int morType = 0;

	
	public GatewayId getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(GatewayId gatewayId) {
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEsxServerName() {
		return esxServerName;
	}
	public void setEsxServerName(String esxServerName) {
		this.esxServerName = esxServerName;
	}
	public String getEsxUserName() {
		return esxUserName;
	}
	public void setEsxUserName(String esxUserName) {
		this.esxUserName = esxUserName;
	}
	public String getEsxPassword() {
		return esxPassword;
	}
	public void setEsxPassword(String esxPassword) {
		this.esxPassword = esxPassword;
	}
	public Protocol getProtocol() {
		return protocol;
	}
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	public boolean isIgnoreCertificate() {
		return ignoreCertificate;
	}
	public void setIgnoreCertificate(boolean ignoreCertificate) {
		this.ignoreCertificate = ignoreCertificate;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getEsxHost() {
		return esxHost;
	}
	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
	}
	public void setJobType(int jobType) {
		this.jobType = jobType;
	}
	public int getJobType() {
		return jobType;
	}
	public boolean isAddEsxToADList() {
		return addEsxToADList;
	}
	public void setAddEsxToADList(boolean addEsxToADList) {
		this.addEsxToADList = addEsxToADList;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getMorType() {
		return morType;
	}
	public void setMorType(int morType) {
		this.morType = morType;
	}
}
