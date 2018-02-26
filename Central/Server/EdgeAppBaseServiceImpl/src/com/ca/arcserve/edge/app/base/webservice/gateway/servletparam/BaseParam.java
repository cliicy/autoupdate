package com.ca.arcserve.edge.app.base.webservice.gateway.servletparam;

import java.util.ArrayList;

public abstract class BaseParam
{
	private static final String DEFAULT_VALUE_SEPERATOR = "|";
	
	private ArrayList<String> valueList = new ArrayList<>();
	private String valueSeperator = DEFAULT_VALUE_SEPERATOR;
	
	public String getValueSeperator()
	{
		return valueSeperator;
	}

	public void setValueSeperator( String valueSeperator )
	{
		this.valueSeperator = valueSeperator;
	}

	public void appendParam( String value )
	{
		this.valueList.add( value );
	}
	
	public void appendParam( int value )
	{
		this.valueList.add( Integer.toString( value ) );
	}
	
	public void setParam( int index, String value )
	{
		this.valueList.set( index, value );
	}
	
	public String getParam( int index )
	{
		return this.valueList.get( index );
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < this.valueList.size(); i ++)
		{
			if (i > 0)
				sb.append( this.valueSeperator );
			
			String value = this.valueList.get( i );
			sb.append( value );
		}
		
		return sb.toString();
	}
}
