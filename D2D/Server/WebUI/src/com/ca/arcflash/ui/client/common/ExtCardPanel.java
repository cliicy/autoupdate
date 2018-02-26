package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.widget.CardPanel;

public class ExtCardPanel extends CardPanel {
	
	public ExtCardPanel() {
		    this.setDeferredRender(false);
	  }

	public void showWidget(int index) {
		this.setActiveItem(this.getItem(index));
	}
}
