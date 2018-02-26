package com.ca.arcserve.edge.app.base.webservice.contract.networkmapping;

import java.io.Serializable;

public class IPv4Address implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int part1;
	private int part2;
	private int part3;
	private int part4;
	
	public IPv4Address()
	{
		this.part1 = 0;
		this.part2 = 0;
		this.part3 = 0;
		this.part4 = 0;
	}
	
	public IPv4Address( int part1, int part2, int part3, int part4 )
	{
		this.part1 = part1;
		this.part2 = part2;
		this.part3 = part3;
		this.part4 = part4;
	}
	
	public IPv4Address( String ipString )
	{
		setByString( ipString );
	}
	
	public boolean setByString( String ipString )
	{
		this.part1 = 0;
		this.part2 = 0;
		this.part3 = 0;
		this.part4 = 0;
		
		if (ipString == null)
			return false;
		
		ipString = ipString.trim();
		
		String[] parts = ipString.split( "\\." );
		if (parts.length < 4)
			return false;
		
		int part1, part2, part3, part4;
		
		try
		{
			part1 = Integer.parseInt( parts[0] );
			part2 = Integer.parseInt( parts[1] );
			part3 = Integer.parseInt( parts[2] );
			part4 = Integer.parseInt( parts[3] );
		}
		catch (Exception e)
		{
			return false;
		}
		
		if (!isValidIPPart( part1 ) ||
			!isValidIPPart( part2 ) ||
			!isValidIPPart( part3 ) ||
			!isValidIPPart( part4 ))
			return false;
		
		this.part1 = part1;
		this.part2 = part2;
		this.part3 = part3;
		this.part4 = part4;
		
		return true;
	}
	
	private boolean isValidIPPart( int part )
	{
		if ((part < 0) || (part > 255))
			return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( this.part1 );
		stringBuilder.append( "." );
		stringBuilder.append( this.part2 );
		stringBuilder.append( "." );
		stringBuilder.append( this.part3 );
		stringBuilder.append( "." );
		stringBuilder.append( this.part4 );
		
		return stringBuilder.toString();
	}
}
