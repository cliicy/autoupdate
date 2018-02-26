package com.ca.arcflash.ui.client.notifications.events;

import com.google.gwt.event.shared.EventHandler;

public interface NotificationRefreshEventHandler extends EventHandler{
	void refreshNotifications(NotificationRefreshEvent event);
}
