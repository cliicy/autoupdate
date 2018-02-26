package com.ca.arcserve.edge.app.base.webservice.contract.actioncenter;


public enum ActionCategory
{
	SiteManagement	( 1 );
	
	private int value;
	
	ActionCategory( int value )
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
	
	public static ActionCategory fromValue( int value )
	{
		for (ActionCategory item : ActionCategory.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}

}
