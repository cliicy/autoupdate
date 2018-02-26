package com.ca.arcflash.ui.client.notifications.events;

import com.google.gwt.event.shared.GwtEvent;

public class NotificationRefreshEvent extends GwtEvent<NotificationRefreshEventHandler>{
	public static Type<NotificationRefreshEventHandler> TYPE = new Type<NotificationRefreshEventHandler>();
	
	public NotificationRefreshEvent() {
		super();
	}
	
	@Override
	public GwtEvent.Type<NotificationRefreshEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NotificationRefreshEventHandler handler) {
		handler.refreshNotifications(this);
	}
	
}