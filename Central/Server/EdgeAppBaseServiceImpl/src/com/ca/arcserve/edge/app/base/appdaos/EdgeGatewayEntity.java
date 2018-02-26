package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

public class EdgeGatewayEntity
{
	private int id;
	private String name = "";
	private String description = "";
	private String uuid = "";
	private String hostUuid = "";
	private String hostName = "";
	private String hostVersion = "";
	private int isLocal;
	private Date lastContactTime;
	private Date createTime;
	private Date updateTime;
	private int heartbeatInterval;
	
	private boolean localGateway;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
	
	public int getIsLocal()
	{
		return isLocal;
	}
	
	public void setIsLocal( int isLocal )
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
	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}
	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}
	
}
