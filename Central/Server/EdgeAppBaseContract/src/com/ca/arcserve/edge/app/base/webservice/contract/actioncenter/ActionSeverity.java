package com.ca.arcserve.edge.app.base.webservice.contract.actioncenter;

public enum ActionSeverity
{
	High	( 1 ),
	Low		( 3 ),
	Medium	( 2 );
	
	private int value;
	
	ActionSeverity( int value )
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
	
	public static ActionSeverity fromValue( int value )
	{
		for (ActionSeverity item : ActionSeverity.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}
}
