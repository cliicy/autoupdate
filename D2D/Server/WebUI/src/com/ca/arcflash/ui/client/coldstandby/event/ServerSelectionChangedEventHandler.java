package com.ca.arcflash.ui.client.coldstandby.event;

import com.google.gwt.event.shared.EventHandler;

public interface ServerSelectionChangedEventHandler extends EventHandler {
	void onServerChanged(ServerSelectionChangedEvent event);
}
