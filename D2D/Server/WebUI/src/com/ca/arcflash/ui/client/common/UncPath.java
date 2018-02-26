package com.ca.arcflash.ui.client.common;

public class UncPath
{
	@SuppressWarnings( "serial" )
	public class InvalidUncPathException extends Exception {}
	
	private String uncPath;
	private String computerName;
	private String shareFolder;
	private String path;
	
	public UncPath()
	{
		this.uncPath = "";
		this.computerName = "";
		this.shareFolder = "";
		this.path = "";
	}
	
	public void setUncPath( String uncPath ) throws InvalidUncPathException
	{
		if (!uncPath.startsWith( "\\\\" ))
			throw new InvalidUncPathException();
		
		String temp = uncPath.substring( 2 );
		String[] parts = temp.split( "\\\\", 3 );
		
		String computerName = "";
		String shareFolder = "";
		String path = "";

		if (parts.length > 0)
			computerName = parts[0];
		
		if (computerName.equals( "" ))
			throw new InvalidUncPathException();
		
		if (parts.length > 1)
			shareFolder = parts[1];
		
		if (parts.length > 2)
			path = parts[2];
		
		this.uncPath = uncPath;
		this.computerName = computerName;
		this.shareFolder = shareFolder;
		this.path = path;
	}
	
	public String getUncPath()
	{
		return this.uncPath;
	}
	
	public String getComputerName()
	{
		return this.computerName;
	}
	
	public String getShareFolder()
	{
		return this.shareFolder;
	}
	
	public String getPath()
	{
		return this.path;
	}
}
