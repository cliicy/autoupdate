package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeIntegerValue
{
	private int value;

	public int getValue()
	{
		return value;
	}

	public void setValue( int value )
	{
		this.value = value;
	}
	
	public String toString() {
		return Integer.toString(value);
	}
}
