package com.ca.arcserve.edge.app.base.webservice.contract.node;


import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;

public class ArcserveConnectInfo implements Serializable{

	private static final long serialVersionUID = 1365446953212892703L;
	private String causer;
	private String capasswd;
	private String uuid;
	private ABFuncAuthMode authmode;
	private int port;
	private Protocol protocol;
	private ABFuncServerType type;
	private String version;
	private int gdb_branchid;
	private NodeManagedStatus managed;
	private GatewayEntity gatewayEntity;
	private boolean isUpdate;
	private boolean isForceRegister;
	
	
	public boolean isForceRegister() {
		return isForceRegister;
	}
	public void setForceRegister(boolean isForceRegister) {
		this.isForceRegister = isForceRegister;
	}
	public boolean isUpdate() {
		return isUpdate;
	}
	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	public ABFuncServerType getType() {
		return type;
	}
	public void setType(ABFuncServerType type) {
		this.type = type;
	}
	public NodeManagedStatus getManaged() {
		return managed;
	}
	public void setManaged(NodeManagedStatus managed) {
		this.managed = managed;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getGdb_branchid() {
		return gdb_branchid;
	}
	public void setGdb_branchid(int gdbBranchid) {
		gdb_branchid = gdbBranchid;
	}
	public String getCauser() {
		return causer;
	}
	public void setCauser(String causer) {
		this.causer = causer;
	}
	public String getCapasswd() {
		return capasswd;
	}
	public void setCapasswd(String capasswd) {
		this.capasswd = capasswd;
	}
	public ABFuncAuthMode getAuthmode() {
		return authmode;
	}
	public void setAuthmode(ABFuncAuthMode authmode) {
		this.authmode = authmode;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Protocol getProtocol() {
		return protocol;
	}
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public GatewayEntity getGatewayEntity() {
		return gatewayEntity;
	}
	public void setGatewayEntity(GatewayEntity gatewayEntity) {
		this.gatewayEntity = gatewayEntity;
	}
}
