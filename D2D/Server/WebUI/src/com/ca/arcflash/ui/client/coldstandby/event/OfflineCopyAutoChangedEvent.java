package com.ca.arcflash.ui.client.coldstandby.event;

import com.google.gwt.event.shared.GwtEvent;

public class OfflineCopyAutoChangedEvent extends GwtEvent<OfflineCopyAutoChangedEventHandler> {
	
	public static Type<OfflineCopyAutoChangedEventHandler> TYPE = new Type<OfflineCopyAutoChangedEventHandler>();
	
	private int state;
	
	public OfflineCopyAutoChangedEvent(int state) {
		super();
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	protected void dispatch(OfflineCopyAutoChangedEventHandler handler) {
		handler.onOfflineCopyAutoChanged(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<OfflineCopyAutoChangedEventHandler> getAssociatedType() {
		 return TYPE;
	}

}
