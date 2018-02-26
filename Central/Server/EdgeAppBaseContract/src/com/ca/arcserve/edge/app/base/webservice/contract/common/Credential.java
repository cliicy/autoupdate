package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;

public class Credential implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String username;
	private @NotPrintAttribute String password;
	
	public Credential()
	{
		this( "", "" );
	}
	
	public Credential( String username, String password )
	{
		this.username = username;
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername( String username )
	{
		if (username == null)
			username = "";
		
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		if (password == null)
			password = "";
		
		this.password = password;
	}
	
	@Override
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append( "Credential [" );
		strBuilder.append( "Username: \"" + this.username + "\"" );
		strBuilder.append( "]" );
		return strBuilder.toString();
	}
	
	public boolean isValid()
	{
		return ((this.username != null) && !this.username.trim().isEmpty() &&
			(this.password != null));
	}
}
