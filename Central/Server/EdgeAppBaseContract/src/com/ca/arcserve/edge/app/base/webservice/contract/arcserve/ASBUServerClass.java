package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public enum ASBUServerClass
{
	Member		( 1 ),
	Secondary	( 2 ),
	DataMover	( 3 ),
	Primary		( 4 );
	
	private int value;
	
	ASBUServerClass( int value )
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
	
	public static ASBUServerClass fromValue( int value )
	{
		for (ASBUServerClass item : ASBUServerClass.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}
}
