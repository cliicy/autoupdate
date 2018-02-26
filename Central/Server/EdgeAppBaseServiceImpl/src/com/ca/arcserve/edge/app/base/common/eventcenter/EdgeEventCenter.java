package com.ca.arcserve.edge.app.base.common.eventcenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class EdgeEventCenter implements IEdgeEventCenter
{
	private static class SubscriberEntry
	{
		private IEdgeEventSubscriber subscriber;
		private Set<Class<?>> interestedEvents = new HashSet<>();

		public IEdgeEventSubscriber getSubscriber()
		{
			return subscriber;
		}

		public void setSubscriber( IEdgeEventSubscriber subscriber )
		{
			this.subscriber = subscriber;
		}

		public Set<Class<?>> getInterestedEvents()
		{
			return interestedEvents;
		}
	}
	
	private static Logger logger = Logger.getLogger( EdgeEventCenter.class );
	private static EdgeEventCenter instance = new EdgeEventCenter();
	
	private List<SubscriberEntry> subscribes = new ArrayList<>();
	
	private EdgeEventCenter()
	{
	}
	
	public static IEdgeEventCenter getInstance()
	{
		return instance;
	}

	@Override
	public void registerSubscriber( IEdgeEventSubscriber subscriber, Class<?>[] interestedEvents )
	{
		SubscriberEntry entry = this.findSubsriberEntry( subscriber );
		if (entry == null)
		{
			entry = new SubscriberEntry();
			entry.setSubscriber( subscriber );
			this.subscribes.add( entry );
		}
		
		for (int i = 0; i < interestedEvents.length; i ++)
			entry.getInterestedEvents().add( interestedEvents[i] );
	}

	@Override
	public void unregisterSubscriber( IEdgeEventSubscriber subscriber, Class<?>[] interestedEvents )
	{
		SubscriberEntry entry = this.findSubsriberEntry( subscriber );
		if (entry == null)
			return;
		
		for (int i = 0; i < interestedEvents.length; i ++)
			entry.getInterestedEvents().remove( interestedEvents[i] );
		
		if (entry.getInterestedEvents().size() == 0)
			this.subscribes.remove( entry );
	}
	
	private SubscriberEntry findSubsriberEntry( IEdgeEventSubscriber subscriber )
	{
		for (SubscriberEntry entry : this.subscribes)
		{
			if (entry.getSubscriber() == subscriber)
				return entry;
		}
		
		return null;
	}

	@Override
	public void publishEvent( IEdgeEvent event, Object publisher )
	{
		for (SubscriberEntry entry : this.subscribes)
		{
			if (entry.getInterestedEvents().contains( event.getClass() ))
			{
				try
				{
					entry.getSubscriber().onEdgeEvent( event, publisher );
				}
				catch (Throwable t)
				{
					logger.error( this.getClass().getSimpleName() + 
						".publishEvent(): The event subscriber error handling event. Subscriber: " + entry.getSubscriber() +
						", Event: " + event + ", Publisher: " + publisher, t );
				}
			}
		}
	}
}
