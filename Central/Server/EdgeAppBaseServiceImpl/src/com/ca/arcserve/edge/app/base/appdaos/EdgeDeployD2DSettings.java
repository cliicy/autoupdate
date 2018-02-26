package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

public class EdgeDeployD2DSettings
{
	private int port;
	private String installPath;
	private int allowInstallDriver;
	private int rebootType;
	private int protocol;
	private int productType;
	private Date rebootTime;

	public int getPort()
	{
		return port;
	}
	
	public void setPort( int port )
	{
		this.port = port;
	}
	
	public String getInstallPath()
	{
		return installPath;
	}
	
	public void setInstallPath( String installPath )
	{
		this.installPath = installPath;
	}
	
	public int getAllowInstallDriver()
	{
		return allowInstallDriver;
	}
	
	public void setAllowInstallDriver( int allowInstallDriver )
	{
		this.allowInstallDriver = allowInstallDriver;
	}

	public int getRebootType() {
		return rebootType;
	}

	public void setRebootType(int rebootType) {
		this.rebootType = rebootType;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public Date getRebootTime() {
		return rebootTime;
	}

	public void setRebootTime(Date rebootTime) {
		this.rebootTime = rebootTime;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}
	
}
