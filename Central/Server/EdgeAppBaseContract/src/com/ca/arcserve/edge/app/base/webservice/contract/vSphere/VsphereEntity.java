package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
/**
 * The vsphere entity contains: vCloud director, Organization, VDC, vApp, VM
 * @author zhaji22
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class VsphereEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private VsphereEntityType entityType;
	private int id;
	@XmlID
	private String entityId;
	private String name;
	private String uuid;
	private String href;
	private String userName;
	private @NotPrintAttribute String password;
	private Protocol protocol;
	private int port;
	private String fullName; //organization
	private String description;//organization,vapp
	private String version;//vCenter
	private int status;//vapp
	private String moRef;//vm which is use to get vminfo from vcenter , like hostname instanceuuid and so on.	
	private String url;// vcenter , which is used to analysis the protocol and port

	@XmlIDREF
	private VsphereEntity parent;
	private List<VsphereEntity> children = new ArrayList<VsphereEntity>();
	private List<VsphereEntity> vcenters = new ArrayList<VsphereEntity>();
	
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	public VsphereEntityType getEntityType() {
		return entityType;
	}
	public void setEntityType(VsphereEntityType entityType) {
		this.entityType = entityType;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public List<VsphereEntity> getChildren() {
		return children;
	}

	public void setChildren(List<VsphereEntity> children) {
		this.children = children;
	}
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public List<VsphereEntity> getVcenters() {
		return vcenters;
	}
	public void setVcenters(List<VsphereEntity> vcenters) {
		this.vcenters = vcenters;
	}

	public VsphereEntity getParent() {
		return parent;
	}

	public void setParent(VsphereEntity parent) {
		this.parent = parent;
	}
	
	public String getMoRef() {
		return moRef;
	}
	public void setMoRef(String moRef) {
		this.moRef = moRef;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public GatewayId getGatewayId()
	{
		return gatewayId;
	}
	public void setGatewayId( GatewayId gatewayId )
	{
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}
}
