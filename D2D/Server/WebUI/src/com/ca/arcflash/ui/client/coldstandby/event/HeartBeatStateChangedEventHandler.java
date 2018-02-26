package com.ca.arcflash.ui.client.coldstandby.event;

import com.google.gwt.event.shared.EventHandler;

public interface HeartBeatStateChangedEventHandler extends EventHandler {
	void onHeartBeatStateChanged(HeartBeatStateChangedEvent event);
}
