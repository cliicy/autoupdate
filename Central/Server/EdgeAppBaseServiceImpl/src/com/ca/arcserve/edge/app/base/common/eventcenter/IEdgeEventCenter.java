package com.ca.arcserve.edge.app.base.common.eventcenter;

public interface IEdgeEventCenter
{
	public interface IEdgeEvent
	{
	}
	
	public interface IEdgeEventSubscriber
	{
		void onEdgeEvent( IEdgeEvent event, Object publisher );
	}
	
	void registerSubscriber( IEdgeEventSubscriber subscriber, Class<?>[] interestedEvents );
	void unregisterSubscriber( IEdgeEventSubscriber subscriber, Class<?>[] interestedEvents );
	void publishEvent( IEdgeEvent event, Object publisher );
}
