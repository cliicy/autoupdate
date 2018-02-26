package com.ca.arcflash.ui.client.coldstandby.event;

import com.ca.arcflash.ha.model.JobScriptCombo;
import com.google.gwt.event.shared.GwtEvent;

public class SettingChangedEvent extends GwtEvent<SettingChangedEventHandler> {

	public static Type<SettingChangedEventHandler> TYPE = new Type<SettingChangedEventHandler>();
	
	private JobScriptCombo jobScriptCombo;
	
	public SettingChangedEvent(JobScriptCombo jobScriptCombo) {
		super();
		this.jobScriptCombo = jobScriptCombo;
	}

	public JobScriptCombo getJobScriptCombo() {
		return jobScriptCombo;
	}

	public void setJobScriptCombo(JobScriptCombo jobScriptCombo) {
		this.jobScriptCombo = jobScriptCombo;
	}

	@Override
	protected void dispatch(SettingChangedEventHandler handler) {
		handler.onAddContact(this);
	}

	@Override
	public Type<SettingChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

}
