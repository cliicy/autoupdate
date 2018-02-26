package com.ca.arcflash.ui.client.coldstandby.event;

import com.google.gwt.event.shared.GwtEvent;

public class ReplicationJobFinishedEvent extends GwtEvent<ReplicationJobFinishedEventHandler> {
	public static Type<ReplicationJobFinishedEventHandler> TYPE = new Type<ReplicationJobFinishedEventHandler>();

	@Override
	protected void dispatch(ReplicationJobFinishedEventHandler handler) {
		handler.onJobFinished(this);
	}

	@Override
	public Type<ReplicationJobFinishedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
