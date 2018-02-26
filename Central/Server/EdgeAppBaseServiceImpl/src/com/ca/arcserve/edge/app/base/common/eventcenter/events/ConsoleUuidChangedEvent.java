package com.ca.arcserve.edge.app.base.common.eventcenter.events;

public class ConsoleUuidChangedEvent extends BaseEdgeEvent
{
	private String newUuid = null;
	
	public ConsoleUuidChangedEvent( String newUuid )
	{
		this.newUuid = newUuid;
	}

	public String getNewUuid()
	{
		return newUuid;
	}
	
}
