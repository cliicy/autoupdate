package com.ca.arcflash.ui.client.coldstandby.event;

import com.google.gwt.event.shared.GwtEvent;

public class HeartBeatStateChangedEvent extends GwtEvent<HeartBeatStateChangedEventHandler> {
	
	public static Type<HeartBeatStateChangedEventHandler> TYPE = new Type<HeartBeatStateChangedEventHandler>();
	
	private int state;
	
	public HeartBeatStateChangedEvent(int state) {
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
	protected void dispatch(HeartBeatStateChangedEventHandler handler) {
		handler.onHeartBeatStateChanged(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<HeartBeatStateChangedEventHandler> getAssociatedType() {
		 return TYPE;
	}

}
