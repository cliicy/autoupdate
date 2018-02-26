package com.ca.arcserve.edge.app.base.webservice.contract.productdeploy;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;

public class ProductImagesInfo implements Serializable
{
	private static final long serialVersionUID = 7815946214758422753L;

	private EdgeVersionInfo consoleVersionInfo;
	private int udpateVersionNumber;
	private ProductImageDownloadInfo gmImageDownloadInfo;
	private ProductImageDownloadInfo updateImageDownloadInfo;
	
	public EdgeVersionInfo getConsoleVersionInfo()
	{
		return consoleVersionInfo;
	}

	public void setConsoleVersionInfo( EdgeVersionInfo consoleVersionInfo )
	{
		this.consoleVersionInfo = consoleVersionInfo;
	}

	public int getUdpateVersionNumber()
	{
		return udpateVersionNumber;
	}
	
	public void setUdpateVersionNumber( int udpateVersionNumber )
	{
		this.udpateVersionNumber = udpateVersionNumber;
	}
	
	public ProductImageDownloadInfo getGmImageDownloadInfo()
	{
		return gmImageDownloadInfo;
	}
	
	public void setGmImageDownloadInfo( ProductImageDownloadInfo gmImageDownloadInfo )
	{
		this.gmImageDownloadInfo = gmImageDownloadInfo;
	}
	
	public ProductImageDownloadInfo getUpdateImageDownloadInfo()
	{
		return updateImageDownloadInfo;
	}
	
	public void setUpdateImageDownloadInfo(
		ProductImageDownloadInfo updateImageDownloadInfo )
	{
		this.updateImageDownloadInfo = updateImageDownloadInfo;
	}
}
