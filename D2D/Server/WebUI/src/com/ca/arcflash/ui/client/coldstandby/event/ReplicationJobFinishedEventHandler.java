package com.ca.arcflash.ui.client.coldstandby.event;

import com.google.gwt.event.shared.EventHandler;

public interface ReplicationJobFinishedEventHandler extends EventHandler {
	void onJobFinished(ReplicationJobFinishedEvent event);
}
