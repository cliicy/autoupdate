package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

public class UpdateDownloadFile
{
	private String downloadUrl;
	private String localPath;
	private long fileSize;
	private String MD5FileUrl;
	private int md5FileSize;

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

	public long getFileSize()
	{
		return fileSize;
	}

	public void setFileSize( long fileSize )
	{
		this.fileSize = fileSize;
	}

	public String getMD5FileUrl()
	{
		return MD5FileUrl;
	}

	public void setMD5FileUrl( String mD5FileUrl )
	{
		MD5FileUrl = mD5FileUrl;
	}

	public int getMd5FileSize()
	{
		return md5FileSize;
	}

	public void setMd5FileSize( int md5FileSize )
	{
		this.md5FileSize = md5FileSize;
	}

}
