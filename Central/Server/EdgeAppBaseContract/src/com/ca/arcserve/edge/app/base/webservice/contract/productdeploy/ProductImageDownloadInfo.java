package com.ca.arcserve.edge.app.base.webservice.contract.productdeploy;

import java.io.Serializable;

public class ProductImageDownloadInfo implements Serializable
{
	private static final long serialVersionUID = -6829784436282927140L;

	private String downloadUrl;
	private String localPath;
	private String md5DownloadUrl;
	private String md5LocalPath;
	private long dataSize;
	
	public String getDownloadUrl()
	{
		return downloadUrl;
	}
	
	public void setDownloadUrl( String downloadUrl )
	{
		this.downloadUrl = downloadUrl;
	}
	
	public String getLocalPath()
	{
		return localPath;
	}
	
	public void setLocalPath( String localPath )
	{
		this.localPath = localPath;
	}

	public String getMd5DownloadUrl()
	{
		return md5DownloadUrl;
	}

	public void setMd5DownloadUrl( String md5DownloadUrl )
	{
		this.md5DownloadUrl = md5DownloadUrl;
	}

	public String getMd5LocalPath()
	{
		return md5LocalPath;
	}

	public void setMd5LocalPath( String md5LocalPath )
	{
		this.md5LocalPath = md5LocalPath;
	}

	public long getDataSize()
	{
		return dataSize;
	}

	public void setDataSize( long dataSize )
	{
		this.dataSize = dataSize;
	}
}
