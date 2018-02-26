package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;
import java.util.Date;

public class GatewayEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private GatewayId id = GatewayId.INVALID_GATEWAY_ID;
	private String name = "";
	private String description = "";
	private String uuid = "";
	private String hostUuid = "";
	private String hostName = "";
	private String hostVersion = "";
	private boolean isLocal;
	private Date lastContactTime;
	private Date createTime;
	private Date updateTime;
	
	private boolean localGateway;
	
	public String getQueueBaseNameForGateway()
	{
		String name = "TN0." + "GW" + id.getRecordId();
		if (this.isLocal)
			name = name + ".Local";
		name = name + "." + this.hostUuid;
		return name;
	}
	
	public GatewayId getId() {
		return id;
	}
	public void setId(GatewayId id) {
		if (id == null)
			id = GatewayId.INVALID_GATEWAY_ID;
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getHostUuid()
	{
		return hostUuid;
	}

	public void setHostUuid( String hostUuid )
	{
		this.hostUuid = hostUuid;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName( String hostName )
	{
		this.hostName = hostName;
	}

	public String getHostVersion()
	{
		return hostVersion;
	}

	public void setHostVersion( String hostVersion )
	{
		this.hostVersion = hostVersion;
	}

	public boolean isLocal()
	{
		return isLocal;
	}

	public void setLocal( boolean isLocal )
	{
		this.isLocal = isLocal;
	}

	public Date getLastContactTime()
	{
		return lastContactTime;
	}

	public void setLastContactTime( Date lastContactTime )
	{
		this.lastContactTime = lastContactTime;
	}

	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public boolean isLocalGateway() {
		return localGateway;
	}
	public void setLocalGateway(boolean localGateway) {
		this.localGateway = localGateway;
	}
	
	@Override
	public String toString() {
		return id + "-[" + name + "]-" + uuid;
	}
	
	public static GatewayEntity clone(GatewayEntity entity){
		GatewayEntity out = new GatewayEntity();
		out.setId(new GatewayId(entity.getId().getRecordId()));
		out.setCreateTime(entity.getCreateTime());
		out.setDescription(entity.getDescription());
		out.setHostName(entity.getHostName());
		out.setHostUuid(entity.getHostUuid());
		out.setHostVersion(entity.getHostVersion());
		out.setLastContactTime(entity.getLastContactTime());
		out.setLocal(entity.isLocal());
		out.setLocalGateway(entity.isLocalGateway());
		out.setName(entity.getName());
		out.setUpdateTime(entity.getUpdateTime());
		out.setUuid(entity.getUuid());
		return out;
	}

}
