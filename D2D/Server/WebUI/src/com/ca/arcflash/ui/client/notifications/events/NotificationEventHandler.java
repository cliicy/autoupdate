package com.ca.arcflash.ui.client.notifications.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent.Type;

public class NotificationEventHandler {
	private static NotificationEventHandler instance = new NotificationEventHandler();
	private HandlerManager eventBus;
	private NotificationEventHandler(){
		eventBus = new HandlerManager(null);
	}
	public static NotificationEventHandler getInstance(){
		return instance;
	}
	
	public <T extends EventHandler> void cleanEvent(GwtEvent.Type<T> type) {
		int count = eventBus.getHandlerCount(type);
		for (int i = 0; i < count; i++) {
			T t = eventBus.getHandler(type, 0);
			eventBus.removeHandler(type, t);
		}
	}
	public <H extends EventHandler> HandlerRegistration registerEventHandler(Type<H> type, H handler) {
		return eventBus.addHandler(type, handler);
	}
	
	/*public <H extends EventHandler> void unregisterEventHandler(Type<H> type, final H handler) {
		eventBus.removeHandler(type, handler);
	}*/
	
	public void fireNotificationRefreshEvent(){
		eventBus.fireEvent(new NotificationRefreshEvent());
	}
}
