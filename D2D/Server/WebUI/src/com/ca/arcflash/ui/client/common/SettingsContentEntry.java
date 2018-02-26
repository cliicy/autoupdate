package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class SettingsContentEntry {
	private int id;
	private String displayName;
	private ISettingsContent contentObject;
	private int contentFlag;
	private TabItem tabItem;
	private AbstractImagePrototype tabIcon;

	public SettingsContentEntry(int id, String displayName,
			ISettingsContent object, int contentFlag,
			AbstractImagePrototype tabIcon) {
		this.id = id;
		this.displayName = displayName;
		this.contentObject = object;
		this.contentFlag = contentFlag;
		this.tabItem = null;
		this.tabIcon = tabIcon;
	}

	public AbstractImagePrototype getTabIcon() {
		return tabIcon;
	}

	public void setTabIcon(AbstractImagePrototype tabIcon) {
		this.tabIcon = tabIcon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public ISettingsContent getContentObject() {
		return contentObject;
	}

	public void setContentObject(ISettingsContent contentObject) {
		this.contentObject = contentObject;
	}

	public int getContentFlag() {
		return contentFlag;
	}

	public void setContentFlag(int contentFlag) {
		this.contentFlag = contentFlag;
	}

	public TabItem getTabItem() {
		return tabItem;
	}

	public void setTabItem(TabItem tabItem) {
		this.tabItem = tabItem;
	}
}
