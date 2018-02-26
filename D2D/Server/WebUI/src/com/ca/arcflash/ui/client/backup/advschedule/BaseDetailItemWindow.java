package com.ca.arcflash.ui.client.backup.advschedule;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;

public abstract class BaseDetailItemWindow<T> extends Window {
	public abstract void setOKButtonListener(SelectionListener<ButtonEvent> OKButtonListener);
	public abstract boolean validate();
	public abstract T getCurrentModel();
	public abstract T save();
}
